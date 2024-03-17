package work.alsace.mapmanager.service

import org.bukkit.World
import work.alsace.mapmanager.pojo.WorldGroup
import work.alsace.mapmanager.pojo.WorldNode
import java.util.*
import java.util.concurrent.CompletableFuture

interface IMapAgent {
    enum class MapGroup {
        ADMIN,
        BUILDER,
        VISITOR
    }
    fun newWorld(world: String?, owner: String?, group: String?)
    fun deleteWorld(world: String?): Boolean
    fun addPlayer(world: String?, group: MapGroup?, player: String?): Boolean
    fun removePlayer(world: String?, group: Int, player: String?): Boolean
    fun publicizeWorld(world: String?): Boolean
    fun privatizeWorld(world: String?): Boolean
    fun isPublic(world: String?): Boolean
    fun getPlayers(name: String?, group: MapGroup?): CompletableFuture<MutableSet<String?>?>?
    fun setPhysical(physical: Boolean?)
    fun setPhysical(world: String?, physical: Boolean)
    fun isPhysical(world: String?): Boolean
    fun setExploded(exploded: Boolean?)
    fun setExploded(world: String?, exploded: Boolean)
    fun isExploded(world: String?): Boolean
    fun getWorldGroupName(world: String?): String?
    fun getAdminSet(world: World?): MutableSet<String?>?
    fun getBuilderSet(world: World?): MutableSet<String?>?
    fun getVisitorSet(world: World?): MutableSet<String?>?
    fun getNodeMap(): MutableMap<String?, WorldNode?>?
    fun getGroupMap(): MutableMap<String?, WorldGroup?>?
    fun containsWorld(world: String?): Boolean
    fun getWorlds(): MutableSet<String?>
    fun getUniqueID(player: String): UUID?
}
