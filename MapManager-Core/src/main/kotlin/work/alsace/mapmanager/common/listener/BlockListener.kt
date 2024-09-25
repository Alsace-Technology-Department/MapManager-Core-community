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
    private val mapAgent: MapAgent = plugin.getMapAgent()

    /**
     * 处理方块物理事件
     *
     * 如果地图不允许物理效果，则取消事件。
     *
     * @param event BlockPhysicsEvent 方块物理事件
     */
    @EventHandler(priority = EventPriority.LOWEST)
    fun onBlockPhysics(event: BlockPhysicsEvent) {
        if (!mapAgent.isPhysical(event.block.world.name)) {
            event.isCancelled = true
        }
    }

    /**
     * 处理掉落方块生成事件
     *
     * 如果地图不允许物理效果，并且生成的实体为掉落方块，则阻止该掉落方块的生成。
     *
     * @param event EntitySpawnEvent 实体生成事件
     */
    @EventHandler(priority = EventPriority.LOWEST)
    fun onFallingBlock(event: EntitySpawnEvent) {
        if (!mapAgent.isPhysical(event.location.world.name) && event.entityType == EntityType.FALLING_BLOCK) {
            event.isCancelled = true
            val fallingBlock = event.entity as FallingBlock
            event.location.block.setType(fallingBlock.blockData.material, false)
        }
    }

    /**
     * 处理方块爆炸事件
     *
     * 如果地图不允许爆炸效果，则取消事件。
     *
     * @param event BlockExplodeEvent 方块爆炸事件
     */
    @EventHandler(priority = EventPriority.LOWEST)
    fun onBlockExplode(event: BlockExplodeEvent) {
        if (!mapAgent.isExploded(event.block.world.name)) {
            event.isCancelled = true
        }
    }

    /**
     * 处理实体爆炸事件
     *
     * 如果地图不允许爆炸效果，则取消事件。
     *
     * @param event EntityExplodeEvent 实体爆炸事件
     */
    @EventHandler(priority = EventPriority.LOWEST)
    fun onEntityExplode(event: EntityExplodeEvent) {
        if (!mapAgent.isExploded(event.entity.world.name)) {
            event.isCancelled = true
        }
    }

    /**
     * 处理玩家右键龙蛋事件
     *
     * 如果玩家右键点击龙蛋，则取消事件，防止龙蛋传送。
     *
     * @param event PlayerInteractEvent 玩家交互事件
     */
    @EventHandler
    fun onDragonEggTeleport(event: PlayerInteractEvent) {
        if (event.action == Action.RIGHT_CLICK_BLOCK && event.clickedBlock?.type == Material.DRAGON_EGG) {
            event.isCancelled = true
        }
    }
}
