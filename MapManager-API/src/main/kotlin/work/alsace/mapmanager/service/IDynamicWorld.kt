package work.alsace.mapmanager.service

import com.onarandombox.MultiverseCore.api.MVWorldManager
import org.bukkit.Location
import com.onarandombox.MultiverseCore.api.MultiverseWorld

interface IDynamicWorld {
    fun getMVWorldManager(): MVWorldManager?
    fun hasLoaded(name: String): Boolean
    fun isExist(name: String): Boolean
    fun loadWorld(name: String): Boolean
    fun unloadWorldLater(name: String)
    fun isExtraLoad(name: String): Boolean
    fun getLoadedWorld(name: String): MultiverseWorld?
    fun getCorrectName(name: String): String?
    fun getCorrectUnloadedName(name: String): String?
    fun cancelUnloadTask(name: String)
    fun getWorlds(prefix: String): MutableList<String>
    fun getOwnerWorlds(player: String): List<String>
    fun getCorrectWorld(name: String): MultiverseWorld?
    fun removeWorld(world: String): Boolean
    fun getPotentialWorlds(): MutableCollection<String?>?
    fun getMVWorld(world: String): MultiverseWorld?
    fun getSpawnLocation(): Location?
    fun importWorld(name: String, alias: String, color: String): Boolean
    fun createWorld(name: String, alias: String, color: String, generate: String): Boolean
    fun loadAlready(world: String)
}
