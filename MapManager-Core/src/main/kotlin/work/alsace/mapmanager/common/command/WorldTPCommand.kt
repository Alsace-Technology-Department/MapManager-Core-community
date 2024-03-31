package work.alsace.mapmanager.common.command

import org.bukkit.Bukkit
import org.bukkit.command.Command
import org.bukkit.command.CommandSender
import org.bukkit.command.TabExecutor
import work.alsace.mapmanager.MapManager
import work.alsace.mapmanager.common.function.DynamicWorld
import java.util.stream.Collectors

class WorldTPCommand(plugin: MapManager) : TabExecutor {
    private var dynamicWorld: DynamicWorld? = null

    init {
        this.dynamicWorld = plugin.getDynamicWorld()
    }

    override fun onTabComplete(
        sender: CommandSender,
        cmd: Command,
        label: String,
        args: Array<String>
    ): MutableList<String?>? {
        return if (args.size != 1) ArrayList(0) else dynamicWorld?.getWorlds(args[0])?.stream()
            ?.filter { world: String? -> sender.hasPermission("multiverse.access.$world") }
            ?.collect(Collectors.toList())
    }

    override fun onCommand(sender: CommandSender, cmd: Command, label: String, args: Array<String>): Boolean {
        if (!sender.hasPermission("mapmanager.world")) {
            sender.sendMessage("§c你没有权限使用此命令")
            return true
        }
        if (args.isEmpty()) {
            sender.sendMessage("§c参数不足，请补全参数")
            return true
        }
        Bukkit.dispatchCommand(sender, "world tp " + args[0])
        return true
    }
}
