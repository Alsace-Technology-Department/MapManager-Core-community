package work.alsace.mapmanager.common.function

import com.onarandombox.MultiverseCore.api.MVWorldManager
import com.onarandombox.MultiverseCore.api.MultiverseWorld
import net.luckperms.api.model.user.User
import org.bukkit.*
import org.bukkit.scheduler.BukkitRunnable
import work.alsace.mapmanager.MapManagerImpl
import work.alsace.mapmanager.enums.MMWorldType
import work.alsace.mapmanager.service.DynamicWorld
import java.io.File
import java.util.*
import java.util.stream.Collectors


/**
 * 动态世界管理器，提供世界的加载、卸载和管理功能。
 */
class DynamicWorldImpl(private val plugin: MapManagerImpl) : DynamicWorld {
    private val mv = plugin.multiverseCore?.mvWorldManager
    private val tasks: MutableMap<String?, BukkitRunnable?> = HashMap()
    private val loaded: MutableSet<String?> = HashSet()

    /**
     * 获取Multiverse-Core的世界管理器。
     * @return MVWorldManager实例，如果Multiverse-Core插件不存在则为null。
     */
    override fun getMVWorldManager(): MVWorldManager? {
        return mv
    }

    /**
     * 检查指定名称的世界是否已加载。
     * @param name 世界的名称。
     * @return 如果世界已加载，返回true；否则返回false。
     */
    override fun hasLoaded(name: String): Boolean {
        return !mv?.hasUnloadedWorld(name, false)!!
    }

    /**
     * 检查指定名称的世界是否存在。
     * @param name 世界的名称。
     * @return 如果世界存在（无论是否已加载），返回true；否则返回false。
     */
    override fun isExist(name: String): Boolean {
        return mv?.hasUnloadedWorld(name, true) == true
    }

    /**
     * 将指定名称的世界标记为已加载。
     * @param world 世界的名称。
     */
    override fun loadAlready(world: String) {
        loaded.add(world)
    }

    /**
     * 尝试加载指定名称的世界。
     * @param name 世界的名称。
     * @return 如果成功加载，返回true；否则返回false。
     */
    override fun loadWorld(name: String): Boolean {
        if (!mv?.loadWorld(name)!!) return false
        loaded.add(name)
        plugin.logger.info(name + "已加载")
        return true
    }

    /**
     * 在一定时间后卸载指定名称的世界。
     * @param name 世界的名称。
     */
    override fun unloadWorldLater(name: String) {
        if (!loaded.contains(name)) return
        plugin.logger.info("$name 准备卸载")

        val runnable = object : BukkitRunnable() {
            override fun run() {
                val world = Bukkit.getWorld(name)
                if (world?.players?.isEmpty() == true) {
                    mv?.unloadWorld(name, true)
                    loaded.remove(name)
                    tasks.remove(name)
                    plugin.logger.info("$name 已卸载")
                }
            }
        }

        runnable.runTaskLater(plugin, 12000)
        tasks[name] = runnable
    }

    /**
     * 检查指定名称的世界是否为额外加载的世界。
     * @param name 世界的名称。
     * @return 如果世界是额外加载的，返回true；否则返回false。
     */
    override fun isExtraLoad(name: String): Boolean {
        return loaded.contains(name)
    }

    /**
     * 获取已加载的世界的MultiverseWorld实例。
     * @param name 世界的名称。
     * @return 对应的MultiverseWorld实例，如果未找到则返回null。
     */
    override fun getLoadedWorld(name: String): MultiverseWorld? {
        val lower = name.lowercase(Locale.getDefault())
        return mv?.mvWorlds?.stream()
            ?.filter { world: MultiverseWorld? -> world?.name?.lowercase(Locale.getDefault()) == lower }
            ?.findFirst()
            ?.orElse(null)
    }

    /**
     * 获取与给定名称匹配的正确的世界名称。
     * 如果给定名称的世界已加载，返回其准确名称；否则尝试匹配未加载的世界名称。
     * @param name 世界名称。
     * @return 匹配的世界名称，如果未找到则返回null。
     */
    override fun getCorrectName(name: String): String? {
        val lower = name.lowercase(Locale.getDefault())
        return mv?.mvWorlds?.stream()
            ?.map { obj: MultiverseWorld? -> obj?.name }
            ?.filter { world: String? -> lower.let { world?.lowercase(Locale.getDefault())?.startsWith(it) } == true }
            ?.findFirst()
            ?.orElse(getCorrectUnloadedName(lower))
    }

    /**
     * 获取未加载的世界中与给定名称匹配的正确名称。
     * @param name 世界名称。
     * @return 匹配的未加载世界名称，如果未找到则返回null。
     */
    override fun getCorrectUnloadedName(name: String): String? {
        val lower = name.lowercase(Locale.getDefault())
        return mv?.unloadedWorlds?.stream()
            ?.filter { world: String? -> world?.lowercase(Locale.getDefault()) == lower }
            ?.findFirst()
            ?.orElse(null)
    }

    /**
     * 取消指定世界的延迟卸载任务。
     * 如果指定世界有一个待执行的卸载任务，该任务将被取消。
     * @param name 世界的名称。
     */
    override fun cancelUnloadTask(name: String) {
        if (tasks.containsKey(name)) {
            tasks[name]?.cancel()
            if (tasks.remove(name) != null) plugin.logger.warning(name + "已取消卸载")
        }
    }

    /**
     * 根据前缀获取已加载和未加载的所有世界的名称列表。
     * @param prefix 世界名称的前缀。
     * @return 匹配前缀的所有世界名称列表。
     */
    override fun getWorlds(prefix: String): MutableList<String> {
        val list = mutableListOf<String>()
        val lowerPrefix = prefix.lowercase(Locale.getDefault())

        // 过滤和添加已加载的世界
        Bukkit.getWorlds()
            .mapNotNull { it.name }  // 直接获取世界名称，忽略为 null 的世界
            .filter { it.lowercase(Locale.getDefault()).startsWith(lowerPrefix) }
            .forEach { list.add(it) }

        // 过滤和添加未加载的世界
        mv?.unloadedWorlds
            ?.filter { it.lowercase(Locale.getDefault()).startsWith(lowerPrefix) }
            ?.forEach { list.add(it) }

        return list
    }

    /**
     * 获取该玩家管理的所有世界的名称列表。
     * @param player 玩家名称。
     * @return 玩家管理的所有世界名称列表。
     */
    override fun getOwnerWorlds(player: String): List<String> {
        val luckPerms = plugin.getLuckPerms()
        val playerUuid = plugin.getMapAgent().getUniqueID(player) ?: return emptyList()

        plugin.logger.info(playerUuid.toString())

        val user = luckPerms.userManager.loadUser(playerUuid).join() ?: return emptyList()

        plugin.logger.info(user.username ?: "Unknown user")

        return getWorlds("")
            .mapNotNull { world ->
                plugin.getMapAgent().getWorldGroupName(world)
            }
            .filter { group ->
                hasPermission(user, "mapmanager.admin.$group")
            }
            .flatMap { group ->
                plugin.getMapAgent().getWorldListByGroup(group)?.asSequence() ?: emptySequence()
            }
            .distinct()
            .toList()
    }

    /**
     * 获取玩家可以进入的所有世界
     * @param name 玩家名
     * @return 玩家可进入的所有世界列表
     */
    override fun getAccessWorlds(name: String): List<String> {
        val luckPerms = plugin.getLuckPerms()
        val playerUuid = plugin.getMapAgent().getUniqueID(name) ?: return emptyList()

        val user = luckPerms.userManager.loadUser(playerUuid).join() ?: return emptyList()

        return getWorlds("")
            .filter { world ->
                hasPermission(user, "multiverse.access.$world")
            }
    }

    /**
     * 获取与给定名称精确匹配的MultiverseWorld实例。
     * @param name 世界的名称。
     * @return 对应的MultiverseWorld实例，如果未找到则返回null。
     */
    override fun getCorrectWorld(name: String): MultiverseWorld? {
        return mv?.mvWorlds
            ?.find { world -> world?.name.equals(name, ignoreCase = true) }
    }

    /**
     * 从Multiverse-Core中彻底移除指定名称的世界。
     * @param world 要移除的世界名称。
     * @return 如果成功移除，返回true；否则返回false。
     */
    override fun removeWorld(world: String): Boolean {
        return mv?.deleteWorld(world, true, true) == true
    }

    /**
     * 获取可能存在的世界名称集合。
     * @return 包含所有潜在世界名称的集合。
     */
    override fun getPotentialWorlds(): MutableCollection<String?>? {
        return mv?.potentialWorlds
    }

    /**
     * 获取指定名称的MultiverseWorld实例。
     * @param world 世界的名称。
     * @return 对应的MultiverseWorld实例，如果未找到则返回null。
     */
    override fun getMVWorld(world: String): MultiverseWorld? {
        return mv?.getMVWorld(world)
    }

    /**
     * 获取服务器默认世界的出生点位置。
     * @return 服务器默认世界的出生点Location实例。
     */
    override fun getSpawnLocation(): Location? {
        return mv?.spawnWorld?.spawnLocation
    }

    /**
     * 导入指定名称的世界。
     * @param name 世界的名称。
     * @param alias 世界的别名。
     * @param color 世界名称的颜色。
     * @return 如果成功导入，返回true；否则返回false。
     */
    override fun importWorld(name: String, alias: String, color: String): Boolean {
        return importWorld(name, alias, color, MMWorldType.NORMAL)
    }

    /**
     * 导入指定名称指定类型的世界。
     * @param name 世界的名称。
     * @param alias 世界的别名。
     * @param color 世界名称的颜色。
     * @param generate 世界的生成器类型。
     * @return 如果成功导入，返回true；否则返回false。
     */
    override fun importWorld(name: String, alias: String, color: String, generate: MMWorldType): Boolean {
        val file = File(plugin.server.worldContainer, name)
        if (!file.exists()) {
            plugin.logger.warning("§c未找到世界文件$name")
            return false
        }
        val versionCheck = plugin.getVersionCheck()
        if (!versionCheck.isMapVersionCorrect(name)) {
            plugin.logger.info("地图版本过高")
            return false
        }
        var gene = World.Environment.NORMAL
        when (generate) {
            MMWorldType.FLAT -> gene = World.Environment.NORMAL
            MMWorldType.VOID -> gene = World.Environment.NORMAL
            MMWorldType.NETHER -> gene = World.Environment.NETHER
            MMWorldType.END -> gene = World.Environment.THE_END
            else -> {
            }
        }
        try {
            if (!mv?.addWorld(name, gene, null, null, null, null, true)!!) {
                plugin.logger.warning("§c导入" + name + "时出现错误")
                return false
            }
        } catch (ignored: IllegalArgumentException) {
        }
        val world = getMVWorld(name)
        if (world == null) {
            plugin.logger.warning("§c获取" + name + "信息失败")
            return false
        }
        initWorld(world, alias, color)
        return true
    }

    /**
     * 创建一个新世界。
     * @param name 世界的名称。
     * @param alias 世界的别名。
     * @param color 世界名称的颜色。
     * @param generate 世界的生成器类型。
     * @return 如果成功创建，返回true；否则返回false。
     */
    override fun createWorld(name: String, alias: String, color: String, generate: MMWorldType): Boolean {
        val file = File(plugin.server.worldContainer, name)
        if (file.exists()) {
            plugin.logger.warning("§c世界" + name + "已经存在")
            return false
        }
        plugin.logger.warning("worldtype: ${generate.name}")
        when (generate) {
            MMWorldType.VOID -> {
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

            MMWorldType.NORMAL -> {
                plugin.logger.warning("normal world")
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

            MMWorldType.NETHER -> {
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

            MMWorldType.END -> {
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
                plugin.logger.warning("flat world")
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
        val world = getMVWorld(name)
        if (world == null) {
            plugin.logger.warning("§c获取" + name + "信息失败")
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
        world?.setAllowAnimalSpawn(false)
        val w = world?.cbWorld
        w?.setGameRule(GameRule.RANDOM_TICK_SPEED, 0)
        w?.setGameRule(GameRule.DO_FIRE_TICK, false)
        w?.setGameRule(GameRule.DO_WEATHER_CYCLE, false)
        w?.setGameRule(GameRule.DO_DAYLIGHT_CYCLE, false)
        w?.setGameRule(GameRule.MOB_GRIEFING, false)
        w?.setGameRule(GameRule.DO_MOB_SPAWNING, false)
        val name = world?.name
        name?.let { cancelUnloadTask(it) }
        name?.let { loadAlready(it) }
        if (world != null) {
            if (world.cbWorld.players.size == 0) name?.let { unloadWorldLater(it) }
        }
    }

    /**
     * 判断玩家是否有权限
     * @param user 玩家LuckPerms实体
     * @param permission 权限节点
     * @return 结果
     */
    override fun hasPermission(user: User, permission: String): Boolean {
        return user.cachedData.permissionData.checkPermission(permission).asBoolean()
    }
}
