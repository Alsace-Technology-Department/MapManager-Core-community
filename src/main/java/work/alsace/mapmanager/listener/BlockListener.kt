package work.alsace.mapmanager.listener

import org.bukkit.Material
import org.bukkit.entity.EntityType
import org.bukkit.entity.FallingBlock
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.Listener
import org.bukkit.event.block.Action
import org.bukkit.event.block.BlockExplodeEvent
import org.bukkit.event.block.BlockPhysicsEvent
import org.bukkit.event.entity.EntityExplodeEvent
import org.bukkit.event.entity.EntitySpawnEvent
import org.bukkit.event.player.PlayerInteractEvent
import work.alsace.mapmanager.MapManager
import work.alsace.mapmanager.function.MapAgent

class BlockListener(plugin: MapManager?) : Listener {
    private val mapAgent: MapAgent?

    init {
        mapAgent = plugin?.getMapAgent()
    }

    @EventHandler(priority = EventPriority.LOWEST)
    fun onBlockPhysics(event: BlockPhysicsEvent?) {
        if (!mapAgent?.isPhysical(event?.block?.world?.name)!!) event?.isCancelled = true
    }

    @EventHandler(priority = EventPriority.LOWEST)
    fun onFallingBlock(event: EntitySpawnEvent?) {
        if (mapAgent?.isPhysical(event?.location?.world?.name) == true) return
        if (event?.entityType != EntityType.FALLING_BLOCK) return
        event.isCancelled = true
        event.location.block.setType((event.entity as FallingBlock).blockData.material, false)
    }

    @EventHandler(priority = EventPriority.LOWEST)
    fun onBlockExplode(event: BlockExplodeEvent?) {
        if (!mapAgent?.isExploded(event?.block?.world?.name)!!) event?.isCancelled = true
    }

    @EventHandler(priority = EventPriority.LOWEST)
    fun onEntityExplode(event: EntityExplodeEvent?) {
        if (!mapAgent?.isExploded(event?.entity?.world?.name)!!) event?.isCancelled = true
    }

    @EventHandler
    fun onDragonEggTeleport(event: PlayerInteractEvent?) {
        if (event?.action != Action.RIGHT_CLICK_BLOCK) return
        if (event.clickedBlock?.type == Material.DRAGON_EGG) event.isCancelled = true
    }
}
