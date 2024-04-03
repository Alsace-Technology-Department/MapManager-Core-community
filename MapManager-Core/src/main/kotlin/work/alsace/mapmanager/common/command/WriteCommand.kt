package work.alsace.mapmanager.common.command

import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender
import work.alsace.mapmanager.MapManager

class WriteCommand(private val plugin: MapManager?) : CommandExecutor {
    override fun onCommand(sender: CommandSender, cmd: Command, label: String, args: Array<String>): Boolean {
        if (!sender.hasPermission("mapmanager.command.write")) {
            sender.sendMessage("§c你没有权限使用此命令")
            return true
        }
        sender.sendMessage("Node Map:" + plugin?.getMapAgent()!!.getNodeMap())
        sender.sendMessage("")
        sender.sendMessage("")
        sender.sendMessage("Group Map:" + plugin.getMapAgent().getGroupMap())
        return true
    }
}
