package work.alsace.mapmanager.common.listener

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
import work.alsace.mapmanager.MapManagerImpl
import work.alsace.mapmanager.service.MapAgent

class BlockListener(plugin: MapManagerImpl) : Listener {
    private val mapAgent: MapAgent?

    init {
        mapAgent = plugin.getMapAgent()
    }

    /**
     * 检测物理效果
     */
    @EventHandler(priority = EventPriority.LOWEST)
    fun onBlockPhysics(event: BlockPhysicsEvent?) {
        if (!mapAgent?.isPhysical(event?.block?.world?.name!!)!!) event?.isCancelled = true
    }

    /**
     * 检测物理效果
     */
    @EventHandler(priority = EventPriority.LOWEST)
    fun onFallingBlock(event: EntitySpawnEvent?) {
        if (mapAgent?.isPhysical(event?.location?.world?.name!!) == true) return
        if (event?.entityType != EntityType.FALLING_BLOCK) return
        event.isCancelled = true
        event.location.block.setType((event.entity as FallingBlock).blockData.material, false)
    }

    /**
     * 检测爆炸效果
     */
    @EventHandler(priority = EventPriority.LOWEST)
    fun onBlockExplode(event: BlockExplodeEvent?) {
        if (mapAgent?.isExploded(event?.block?.world?.name!!) == true) event?.isCancelled = true
    }

    /**
     * 检测爆炸效果
     */
    @EventHandler(priority = EventPriority.LOWEST)
    fun onEntityExplode(event: EntityExplodeEvent?) {
        if (mapAgent?.isExploded(event?.entity?.world?.name!!) == true) event?.isCancelled = true
    }

    /**
     * 检测龙蛋
     */
    @EventHandler
    fun onDragonEggTeleport(event: PlayerInteractEvent?) {
        if (event?.action != Action.RIGHT_CLICK_BLOCK) return
        if (event.clickedBlock?.type == Material.DRAGON_EGG) event.isCancelled = true
    }
}
