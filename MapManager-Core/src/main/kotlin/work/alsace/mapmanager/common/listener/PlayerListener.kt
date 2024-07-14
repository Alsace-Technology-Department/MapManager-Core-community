package work.alsace.mapmanager.common.listener

import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.entity.EntityTeleportEvent
import org.bukkit.event.player.PlayerChangedWorldEvent
import org.bukkit.event.player.PlayerJoinEvent
import org.bukkit.event.player.PlayerQuitEvent
import work.alsace.mapmanager.MapManagerImpl

class PlayerListener(private val plugin: MapManagerImpl) : Listener {
    /**
     * 玩家进入游戏时，取消卸载世界任务。
     * @param event 玩家进入游戏事件。
     */
    @EventHandler
    fun onPlayerJoin(event: PlayerJoinEvent) {
        event.player.world.name.let { plugin.getDynamicWorld().cancelUnloadTask(it) }
    }

    /**
     * 玩家离开游戏时，判断是否需要卸载世界。
     * @param event 玩家离开游戏事件。
     */
    @EventHandler
    fun onPlayerQuit(event: PlayerQuitEvent) {
        val player = event.player
        if (player.world.players.size <= 1) plugin.getDynamicWorld().unloadWorldLater(player.world.name)
    }

    /**
     * 玩家切换世界时，取消卸载世界任务。
     * @param event 玩家切换世界事件。
     */
    @EventHandler
    fun onPlayerChangeWorld(event: PlayerChangedWorldEvent) {
        val world = event.from
        world.let {
            plugin.getDynamicWorld().unloadWorldLater(
                it.name
            )
        }
        event.player.world.name.let { plugin.getDynamicWorld().cancelUnloadTask(it) }
    }

    /**
     * 玩家切换世界时，检测是否有权限
     */
    @EventHandler
    fun onPlayerTeleport(event: EntityTeleportEvent) {
        if (event.entity is Player) {
            val player = event.entity as Player
            val world = event.to?.world
            if (world != null) {
                if (!player.hasPermission("multiverse.access.${world.name}")) {
                    event.isCancelled = true
                    player.sendMessage("§c你没有权限进入此地图")
                }
            }
        }
    }
}
