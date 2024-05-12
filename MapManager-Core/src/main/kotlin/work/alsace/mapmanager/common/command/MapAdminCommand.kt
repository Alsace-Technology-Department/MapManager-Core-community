package work.alsace.mapmanager.common.command

import org.bukkit.command.Command
import org.bukkit.command.CommandSender
import org.bukkit.command.TabExecutor
import work.alsace.mapmanager.MapManagerImpl
import work.alsace.mapmanager.common.command.MapAdminCommand.Operation.*
import work.alsace.mapmanager.service.MapAgent
import java.util.*
import java.util.stream.Collectors

class MapAdminCommand(plugin: MapManagerImpl?) : TabExecutor {
    private val mapAgent: MapAgent? = plugin?.getMapAgent()
    private val subCommand: MutableList<String?> =
        mutableListOf("reload", "physics", "explosion", "sync", "save")
    private val emptyList: MutableList<String?> = ArrayList(0)

    enum class Operation {
        ENABLE,
        CLEAR,
        DISABLE
    }

    private fun getOperation(name: String?): Operation? {
        if (name != null) {
            return when (name.lowercase(Locale.getDefault())) {
                "on", "enable", "true", "yes" -> {
                    ENABLE
                }

                "off", "disable", "false", "no" -> {
                    DISABLE
                }

                else -> {
                    CLEAR
                }
            }
        }
        return null
    }

    override fun onTabComplete(
        sender: CommandSender,
        cmd: Command,
        label: String,
        args: Array<out String>
    ): MutableList<String?>? {
        if (!sender.hasPermission("mapmanager.command.mapadmin.md") || args.size > 2) return emptyList
        val prefix = args[args.size - 1].lowercase(Locale.getDefault())
        return if (args.size == 1) subCommand.stream()
            .filter { s: String? -> prefix.let { s!!.startsWith(it) } }
            ?.collect(Collectors.toList()) else if (args[0]
                .equals("changepassword", ignoreCase = true)
        ) mapAgent?.getWorlds()?.stream()?.filter { s: String? ->
            prefix.let {
                s!!.lowercase(Locale.getDefault()).startsWith(
                    it
                )
            }
        }
            ?.collect(Collectors.toList()) else emptyList
    }

    override fun onCommand(sender: CommandSender, cmd: Command, label: String, args: Array<String>): Boolean {
        if (!sender.hasPermission("mapmanager.command.mapadmin.md")) {
            sender.sendMessage("§c你，莫得权限")
            return true
        }
        if (args.isEmpty()) {
            sender.sendMessage("§c参数不足，请补全参数")
            return true
        }
        when (args[0].lowercase(Locale.getDefault())) {
            "reload" -> {
                mapAgent?.reload()
                sender.sendMessage("§a重载完毕")
            }

            "physics", "physical" -> {
                if (args.size < 2) {
                    sender.sendMessage("§c参数不足，请补全参数")
                    return false
                }
                when (getOperation(args[1])) {
                    ENABLE -> {
                        mapAgent?.setPhysical(true)
                        sender.sendMessage("§a已开启全局物理")
                    }

                    CLEAR -> {
                        mapAgent?.setPhysical(null)
                        sender.sendMessage("§a已清除全局物理设置")
                    }

                    DISABLE -> {
                        mapAgent?.setPhysical(false)
                        sender.sendMessage("§a已关闭全局物理")
                    }

                    null -> {}
                }
            }

            "explosion", "exploded" -> {
                if (args.size < 2) {
                    sender.sendMessage("§c参数不足，请补全参数")
                    return false
                }
                when (getOperation(args[1])) {
                    ENABLE -> {
                        mapAgent?.setExploded(true)
                        sender.sendMessage("§a已开启全局爆炸破坏")
                    }

                    CLEAR -> {
                        mapAgent?.setExploded(null)
                        sender.sendMessage("§a已清除全局爆炸破坏设置")
                    }

                    DISABLE -> {
                        mapAgent?.setExploded(false)
                        sender.sendMessage("§a已关闭全局爆炸破坏")
                    }

                    null -> {}
                }
            }

            "sync" -> {
                mapAgent?.syncWithLuckPerms(sender)
            }

            "save" -> {
                if (mapAgent?.save() == true) sender.sendMessage("§a保存成功") else sender.sendMessage("§c保存失败")
            }

            else -> {
                sender.sendMessage("§c未知操作")
            }
        }
        return true
    }
}
