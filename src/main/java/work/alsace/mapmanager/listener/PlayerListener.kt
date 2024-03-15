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
        dynamicWorld?.cancelUnloadTask(event?.player?.world?.name)
    }

    @EventHandler
    fun onPlayerQuit(event: PlayerQuitEvent?) {
        val player = event?.player
        if (player?.world?.players?.size!! <= 1) dynamicWorld?.unloadWorldLater(player.world.name)
    }

    @EventHandler
    fun onPlayerChangeWorld(event: PlayerChangedWorldEvent?) {
        val world = event?.from
        if (dynamicWorld?.isExtraLoad(world?.name) == true && world?.players?.size!! <= 0) dynamicWorld.unloadWorldLater(world.name)
        dynamicWorld?.cancelUnloadTask(event?.player?.world?.name)
    }
}
