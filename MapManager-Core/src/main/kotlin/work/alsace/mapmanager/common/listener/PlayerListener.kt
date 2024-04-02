package work.alsace.mapmanager.common.listener

import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerChangedWorldEvent
import org.bukkit.event.player.PlayerJoinEvent
import org.bukkit.event.player.PlayerQuitEvent
import work.alsace.mapmanager.MapManager

class PlayerListener(private val plugin: MapManager) : Listener {
    @EventHandler
    fun onPlayerJoin(event: PlayerJoinEvent?) {
        event?.player?.world?.name?.let { plugin.getDynamicWorld().cancelUnloadTask(it) }
    }

    @EventHandler
    fun onPlayerQuit(event: PlayerQuitEvent?) {
        val player = event?.player
        if (player?.world?.players?.size!! <= 1) plugin.getDynamicWorld().unloadWorldLater(player.world.name)
    }

    @EventHandler
    fun onPlayerChangeWorld(event: PlayerChangedWorldEvent?) {
        val world = event?.from
        world?.let {
            plugin.getDynamicWorld().unloadWorldLater(
                it.name
            )
        }
        event?.player?.world?.name?.let { plugin.getDynamicWorld().cancelUnloadTask(it) }
    }
}
