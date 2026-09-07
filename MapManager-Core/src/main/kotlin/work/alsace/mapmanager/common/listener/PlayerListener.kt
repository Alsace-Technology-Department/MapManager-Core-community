package work.alsace.mapmanager.common.listener

import org.bukkit.Bukkit
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerChangedWorldEvent
import org.bukkit.event.player.PlayerJoinEvent
import org.bukkit.event.player.PlayerRespawnEvent
import org.bukkit.event.player.PlayerTeleportEvent
import work.alsace.mapmanager.MapManagerImpl

class PlayerListener(private val plugin: MapManagerImpl) : Listener {
    /**
     * 玩家进入游戏时，取消卸载世界任务。
     * @param event 玩家进入游戏事件。
     */
    @EventHandler
    fun onPlayerJoin(event: PlayerJoinEvent) {
        val worldName = event.player.world.name
        plugin.getDynamicWorld().cancelUnloadTask(worldName)
//        checkAccessAfterEntry(event.player)
    }
//
//    @EventHandler(priority = EventPriority.MONITOR)
//    fun onPlayerChangedWorld(event: PlayerChangedWorldEvent) {
//        checkAccessAfterEntry(event.player)
//    }
//
//    @EventHandler(priority = EventPriority.MONITOR)
//    fun onPlayerRespawn(event: PlayerRespawnEvent) {
//        checkAccessAfterEntry(event.player)
//    }

    /**
     * 检查所有玩家传送的目标世界权限，并将默认世界的目标位置改为出生点。
     */
    @EventHandler(priority = EventPriority.HIGHEST)
    fun onPlayerTeleport(event: PlayerTeleportEvent) {
        val player = event.player
        val world = event.to.world ?: return
        if (!player.hasPermission("multiverse.access.${world.name}")) {
            event.isCancelled = true
            player.sendMessage("§c你没有权限进入此地图")
            return
        }
        if (world == Bukkit.getWorlds().firstOrNull()) {
            event.to = plugin.getDynamicWorld().getDefaultSpawnLocation() ?: world.spawnLocation
        }
    }

    private fun checkAccessAfterEntry(player: Player) {
        // 等本次事件及其他插件的处理完成后，检查玩家实际所在世界。
        Bukkit.getScheduler().runTask(plugin, Runnable {
            if (!player.isOnline || player.hasPermission("multiverse.access.${player.world.name}")) {
                return@Runnable
            }

            player.sendMessage("§c你没有权限进入此地图")
            val defaultWorld = Bukkit.getWorlds().firstOrNull()
            if (defaultWorld == null || defaultWorld == player.world ||
                !player.hasPermission("multiverse.access.${defaultWorld.name}") ||
                !player.teleport(defaultWorld.spawnLocation) ||
                !player.hasPermission("multiverse.access.${player.world.name}")
            ) {
                player.kickPlayer("§c你没有权限进入此地图，且无法返回默认世界")
            }
        })
    }
}
