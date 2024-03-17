package work.alsace.mapmanager.listener

import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerChangedWorldEvent
import org.bukkit.event.player.PlayerJoinEvent
import org.bukkit.event.player.PlayerQuitEvent
import work.alsace.mapmanager.function.DynamicWorld

class PlayerListener(private val dynamicWorld: DynamicWorld?) : Listener {
    @EventHandler
    fun onPlayerJoin(event: PlayerJoinEvent?) {
        event?.player?.world?.name?.let { dynamicWorld?.cancelUnloadTask(it) }
    }

    @EventHandler
    fun onPlayerQuit(event: PlayerQuitEvent?) {
        val player = event?.player
        if (player?.world?.players?.size!! <= 1) dynamicWorld?.unloadWorldLater(player.world.name)
    }

    @EventHandler
    fun onPlayerChangeWorld(event: PlayerChangedWorldEvent?) {
        val world = event?.from
        if (world?.name?.let { dynamicWorld?.isExtraLoad(it) } == true && world?.players?.size!! <= 0) dynamicWorld?.unloadWorldLater(world.name)
        event?.player?.world?.name?.let { dynamicWorld?.cancelUnloadTask(it) }
    }
}
