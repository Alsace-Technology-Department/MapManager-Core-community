package work.alsace.mapmanager.function

import com.onarandombox.MultiverseCore.MultiverseCore
import com.onarandombox.MultiverseCore.api.MVWorldManager
import com.onarandombox.MultiverseCore.api.MultiverseWorld
import com.onarandombox.MultiverseCore.utils.WorldNameChecker
import org.bukkit.*
import org.bukkit.scheduler.BukkitRunnable
import work.alsace.mapmanager.MapManager
import java.io.File
import java.util.*

class DynamicWorld(private val plugin: MapManager?) {
    private val mv =
        (Bukkit.getServer().pluginManager.getPlugin("Multiverse-Core") as MultiverseCore?)?.getMVWorldManager()
    private val tasks: MutableMap<String?, BukkitRunnable?> = HashMap()
    private val loaded: MutableSet<String?> = HashSet()
    fun getMVWorldManager(): MVWorldManager? {
        return mv
    }

    //for CHuNanPlugin
    fun hasLoaded(name: String?): Boolean {
        return !mv?.hasUnloadedWorld(name, false)!!
    }

    fun isExist(name: String?): Boolean {
        return mv?.hasUnloadedWorld(name, true) == true
    }

    fun loadAlready(world: String?) {
        loaded.add(world)
    }

    fun loadWorld(name: String?): Boolean {
        if (name == null) return false
        if (!mv?.loadWorld(name)!!) return false
        loaded.add(name)
        plugin?.logger?.info(name + "已加载")
        return true
    }

    fun unloadWorldLater(name: String?) {
        if (!loaded.contains(name)) return
        plugin?.logger?.info(name + "准备卸载")
        val runnable: BukkitRunnable = object : BukkitRunnable() {
            override fun run() {
                if (name?.let { Bukkit.getWorld(it)?.players?.size }!! <= 0) mv?.unloadWorld(name, true)
                loaded.remove(name)
                tasks.remove(name)
                plugin?.logger?.info(name + "已卸载")
            }
        }
        plugin?.let { runnable.runTaskLater(it, 12000) }
        tasks[name] = runnable
    }

    fun isExtraLoad(name: String?): Boolean {
        return loaded.contains(name)
    }

    fun getLoadedWorld(name: String?): MultiverseWorld? {
        val lower = name?.lowercase(Locale.getDefault())
        return mv?.mvWorlds?.stream()
            ?.filter { world: MultiverseWorld? -> world?.name?.lowercase(Locale.getDefault()) == lower }
            ?.findFirst()
            ?.orElse(null)
    }

    fun getCorrectName(name: String?): String? {
        val lower = name?.lowercase(Locale.getDefault())
        return mv?.mvWorlds?.stream()
            ?.map { obj: MultiverseWorld? -> obj?.name }
            ?.filter { world: String? -> lower?.let { world?.lowercase(Locale.getDefault())?.startsWith(it) } == true }
            ?.findFirst()
            ?.orElse(getCorrectUnloadedName(lower))
    }

    fun getCorrectUnloadedName(name: String?): String? {
        val lower = name?.lowercase(Locale.getDefault())
        return mv?.unloadedWorlds?.stream()
            ?.filter { world: String? -> world?.lowercase(Locale.getDefault()) == lower }
            ?.findFirst()
            ?.orElse(null)
    }

    fun cancelUnloadTask(name: String?) {
        if (tasks.containsKey(name)) {
            tasks[name]?.cancel()
            if (tasks.remove(name) != null) plugin?.logger?.warning(name + "已取消卸载")
        }
    }

    fun getWorlds(prefix: String?): MutableList<String?> {
        val list: MutableList<String?> = ArrayList()
        val lower = prefix?.lowercase(Locale.getDefault())
        Bukkit.getWorlds().stream()
            .map { obj: World? -> obj?.name }
            .filter { name: String? -> lower?.let { name?.lowercase(Locale.getDefault())?.startsWith(it) } == true }
            .forEach { e: String? -> list.add(e) }
        mv?.unloadedWorlds?.stream()
            ?.filter { name: String? -> lower?.let { name?.lowercase(Locale.getDefault())?.startsWith(it) } == true }
            ?.forEach { e: String? -> list.add(e) }
        return list
    }

    fun getCorrectWorld(name: String?): MultiverseWorld? {
        return mv?.mvWorlds?.stream()
            ?.filter { world: MultiverseWorld? -> world?.name.equals(name, ignoreCase = true) }
            ?.findFirst()
            ?.orElse(null)
    }

    fun removeWorld(world: String?): Boolean {
        return mv?.deleteWorld(world, true, true) == true
    }

    fun getPotentialWorlds(): MutableCollection<String?>? {
        return mv?.potentialWorlds
    }

    fun getMVWorld(world: String?): MultiverseWorld? {
        return mv?.getMVWorld(world)
    }

    fun getSpawnLocation(): Location? {
        return mv?.spawnWorld?.spawnLocation
    }

    fun importWorld(name: String?, alias: String?, color: String?): Boolean {
        val file = name?.let { File(plugin?.server?.worldContainer, it) }
        if (file != null) {
            if (!file.exists()) {
                plugin?.logger?.warning("§c未找到世界文件$name")
                return false
            }
        }
        if (!WorldNameChecker.isValidWorldFolder(file)) {
            plugin?.logger?.warning("§c未发现" + name + "中的.dat文件")
            return false
        }
        try {
            if (!mv?.addWorld(name, World.Environment.NORMAL, null, null, null, null, true)!!) {
                plugin?.logger?.warning("§c导入" + name + "时出现错误")
                return false
            }
        } catch (ignored: IllegalArgumentException) {
        }
        val world = getMVWorld(name)
        if (world == null) {
            plugin?.logger?.warning("§c获取" + name + "信息失败")
            return false
        }
        initWorld(world, alias, color)
        return true
    }

    fun createWorld(name: String?, alias: String?, color: String?, generate: String?): Boolean {
        val file = name?.let { File(plugin?.server?.worldContainer, it) }
        if (file != null) {
            if (file.exists()) {
                plugin?.logger?.warning("§c世界" + name + "已经存在")
                return false
            }
        }
        when (generate?.lowercase(Locale.getDefault())) {
            "void_gen" -> {
                if (!mv!!.addWorld(
                        name,
                        World.Environment.NORMAL,
                        null,
                        WorldType.FLAT,
                        false,
                        "VoidGen:{}",
                        true
                    )
                ) return false
            }

            "normal" -> {
                if (!mv!!.addWorld(
                        name,
                        World.Environment.NORMAL,
                        null,
                        WorldType.NORMAL,
                        false,
                        null,
                        true
                    )
                ) return false
            }

            "nether" -> {
                if (!mv!!.addWorld(
                        name,
                        World.Environment.NETHER,
                        null,
                        WorldType.NORMAL,
                        false,
                        null,
                        true
                    )
                ) return false
            }

            "the_end" -> {
                if (!mv!!.addWorld(
                        name,
                        World.Environment.THE_END,
                        null,
                        WorldType.NORMAL,
                        false,
                        null,
                        true
                    )
                ) return false
            }

            else -> {
                if (!mv!!.addWorld(
                        name,
                        World.Environment.NORMAL,
                        null,
                        WorldType.FLAT,
                        false,
                        null,
                        true
                    )
                ) return false
            }
        }
        if (!mv.addWorld(name, World.Environment.NORMAL, null, WorldType.FLAT, false, null)) return false
        val world = getMVWorld(name)
        if (world == null) {
            plugin?.logger?.warning("§c获取" + name + "信息失败")
            return false
        }
        initWorld(world, alias, color)
        return true
    }

    private fun initWorld(world: MultiverseWorld?, alias: String?, color: String?) {
        world?.alias = alias
        world?.setColor(color)
        world?.setDifficulty(Difficulty.PEACEFUL)
        world?.autoLoad = true
        world?.setKeepSpawnInMemory(false)
        world?.setGameMode(GameMode.CREATIVE)
        val w = world?.cbWorld
        w?.setGameRule(GameRule.RANDOM_TICK_SPEED, 0)
        w?.setGameRule(GameRule.DO_FIRE_TICK, false)
        w?.setGameRule(GameRule.DO_WEATHER_CYCLE, false)
        w?.setGameRule(GameRule.DO_DAYLIGHT_CYCLE, false)
        w?.setGameRule(GameRule.MOB_GRIEFING, false)
        w?.setGameRule(GameRule.DO_MOB_SPAWNING, false)
        val name = world?.name
        cancelUnloadTask(name)
        loadAlready(name)
        if (world != null) {
            if (world.cbWorld.players.size == 0) unloadWorldLater(name)
        }
    }
}
