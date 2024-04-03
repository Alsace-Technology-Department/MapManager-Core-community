package work.alsace.mapmanager.common.function

import com.google.gson.reflect.TypeToken
import net.luckperms.api.LuckPerms
import net.luckperms.api.context.DefaultContextKeys
import net.luckperms.api.model.group.Group
import net.luckperms.api.model.user.User
import net.luckperms.api.node.Node
import net.luckperms.api.node.matcher.NodeMatcher
import net.luckperms.api.node.types.InheritanceNode
import net.luckperms.api.node.types.PermissionNode
import net.luckperms.api.node.types.WeightNode
import org.bukkit.Bukkit
import org.bukkit.ChatColor
import org.bukkit.OfflinePlayer
import org.bukkit.World
import org.bukkit.command.CommandSender
import work.alsace.mapmanager.MapManager
import work.alsace.mapmanager.pojo.MainConfig
import work.alsace.mapmanager.pojo.WorldGroup
import work.alsace.mapmanager.pojo.WorldNode
import work.alsace.mapmanager.service.IMainYaml
import work.alsace.mapmanager.service.IMapAgent
import java.util.*
import java.util.concurrent.CompletableFuture
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.ConcurrentMap
import java.util.concurrent.ExecutionException
import java.util.function.Consumer
import java.util.stream.Collectors

/**
 * 地图管理代理，负责处理与LuckPerms权限插件的交互、管理世界及其权限组等功能。
 */
class MapAgent(private val plugin: MapManager) : IMapAgent {
    private val nodeIO: FileIO<WorldNode?>?
    private val groupIO: FileIO<WorldGroup?>?
    private val yaml: IMainYaml
    private var luckPerms: LuckPerms = plugin.getLuckPerms()
    private val dynamicWorld: DynamicWorld = plugin.getDynamicWorld()
    private var nodeMap //world name -> world node
            : ConcurrentMap<String?, WorldNode?>?
    private var groupMap //group name -> group node
            : ConcurrentMap<String?, WorldGroup?>?
    private val config: MainConfig
    private val nullWorldNode: WorldNode = WorldNode()
    private val nullWorldGroup: WorldGroup = WorldGroup()

    init {
        nodeIO = FileIO(plugin, "worlds", object : TypeToken<ConcurrentMap<String?, WorldNode?>?>() {})
        nodeMap = nodeIO.load()
        groupIO = FileIO(plugin, "groups", object : TypeToken<ConcurrentMap<String?, WorldGroup?>?>() {})
        groupMap = groupIO.load()
        yaml = plugin.getMainYaml()
        config = yaml.load()
        val global = config.getGlobal()
        if (global != null) {
            physical = global.getPhysical()
        }
        if (global != null) {
            exploded = global.getExploded()
        }
    }

    fun reload() {
        nodeMap?.clear()
        nodeMap = nodeIO?.load()
        if (nodeMap == null) nodeMap = ConcurrentHashMap()
        groupMap?.clear()
        groupMap = groupIO?.load()
        if (groupMap == null) groupMap = ConcurrentHashMap()
    }


    override fun save(): Boolean {
        return nodeIO!!.save(nodeMap) && groupIO!!.save(groupMap)
    }

    fun setLuckPerms(luckPerms: LuckPerms) {
        this.luckPerms = luckPerms
    }

    private fun getWorldNode(world: String?): WorldNode? {
        return nodeMap?.getOrDefault(world, nullWorldNode)
    }

    private fun getWorldGroup(world: String?): WorldGroup? {
        return groupMap?.getOrDefault(getWorldGroupName(world), nullWorldGroup)
    }

    private fun getWorldGroupByName(group: String?): WorldGroup? {
        return groupMap?.getOrDefault(group, nullWorldGroup)
    }

    /**
     * 获取玩家的UUID
     * @param player 玩家名称
     * @return 返回玩家的UUID
     */
    override fun getUniqueID(player: String): UUID? {
        var offline = player
        val online = plugin.server.getPlayer(player)
        if (online == null) {
            offline = offline.lowercase(Locale.getDefault())
            for (off in plugin.server.offlinePlayers) {
                val name = off.name
                if (name != null && name.lowercase(Locale.getDefault()) == offline) return off.uniqueId
            }
        } else {
            return online.uniqueId
        }
        return null
    }

    private fun getProcess(owner: String?): CompletableFuture<User?>? {
        val uuid = owner?.let { getUniqueID(it) }
        return if (uuid == null) CompletableFuture.supplyAsync { null } else luckPerms.userManager.loadUser(uuid)
    }

    /**
     * 创建一个新的世界，并将其注册到权限管理中。
     *
     * @param world 世界名称。
     * @param owner 世界拥有者的玩家名。
     * @param group 权限组名称。
     */
    override fun newWorld(world: String?, owner: String?, group: String?) {
        val w = world?.lowercase(Locale.getDefault())
        val g = group?.lowercase(Locale.getDefault())
        val manager = luckPerms.groupManager
        g?.let {
            manager.createAndLoadGroup(it).thenApplyAsync<Group?> { lp: Group? ->
                val data = lp?.data()
                data?.add(PermissionNode.builder("multiverse.access.$w").build())
                data?.add(InheritanceNode.builder("default").build())
                data?.add(
                    InheritanceNode.builder("worldbase").withContext(DefaultContextKeys.WORLD_KEY, w.toString()).build()
                )
                data?.add(InheritanceNode.builder("apply").build())
                data?.add(WeightNode.builder(1).build())
                if (lp != null) {
                    manager.saveGroup(lp)
                    plugin.logger.info("权限组" + lp.name + "已创建并初始化完毕")
                }
                lp
            }?.thenAcceptBoth<User?>(getProcess(owner)) { lp: Group?, user: User? ->
                if (user == null) {
                    plugin.logger.warning("未找到对应玩家")
                    return@thenAcceptBoth
                }
                user.data().add(PermissionNode.builder("mapmanager.admin.$g").build())
                user.data().add(InheritanceNode.builder(lp!!).build())
                luckPerms.userManager.saveUser(user)
                plugin.logger.info("已将" + user.username + "添加至" + g + "权限组")
            }?.thenRun {
                nodeMap?.set(world, WorldNode(g))
                if (groupMap?.containsKey(g) == true) getWorldGroupByName(g)?.addWorld(world) else {
                    if (owner == null) groupMap?.set(g, WorldGroup(world)) else groupMap?.set(
                        g,
                        WorldGroup(world, owner)
                    )
                }
                save()
            }
        }
    }

    private fun checkGroup(group: Group?): Boolean {
        val nodes: MutableCollection<Node?> = group!!.distinctNodes
        nodes.forEach(Consumer { node: Node? -> plugin.logger.info(node!!.key) })
        if (nodes.size > 3) {
            luckPerms.groupManager.saveGroup(group)
            return false
        }
        for (node in nodes) {
            val key = node?.key
            plugin.logger.info(key)
            if (key != null) {
                if (!key.startsWith("weight.") && key != "group.apply" && key != "group.default") return false
            }
        }
        return true
    }

    /**
     * 删除一个世界及其相关的权限信息。
     *
     * @param world 要删除的世界名称。
     * @return 如果成功删除，返回true；否则返回false。
     */
    override fun deleteWorld(world: String?): Boolean {
        val worldNode = getWorldNode(world)
        val groupNode = getWorldGroup(world)
        val gm = luckPerms.groupManager
        val um = luckPerms.userManager
        val map = world?.let { Bukkit.getWorld(it) }
        if (map != null) {
            val loc = dynamicWorld.getSpawnLocation()
            for (player in map.players) {
                loc?.let { player.teleport(it) }
                player.sendMessage("§7世界" + world + "正在被删除，您已被传送至出生点")
            }
        }
        dynamicWorld.getMVWorldManager()?.unloadWorld(world, true)
        val enter = PermissionNode.builder("multiverse.access." + (world!!.lowercase(Locale.getDefault()))).build()
        um.searchAll(NodeMatcher.key(enter))
            .thenAcceptAsync { result: MutableMap<UUID?, MutableCollection<PermissionNode?>?>? ->
                result?.keys?.forEach(Consumer { uuid: UUID? ->
                    uuid?.let {
                        um.loadUser(it).thenAccept { user: User? ->
                            user?.data()?.remove(enter)
                            user?.let { it1 -> um.saveUser(it1) }
                        }
                    }
                })
            }
        nodeMap?.remove(world)
        groupNode?.removeWorld(world)
        val group = worldNode?.getGroup()?.let { gm.getGroup(it) }
        if (group == null || group.name == "__nil") {
            if (worldNode != null) {
                plugin.logger.info("§c权限组" + worldNode.getGroup() + "未找到")
            }
            nodeIO?.save(nodeMap)
            groupIO?.save(groupMap)
            dynamicWorld.cancelUnloadTask(world)
            return dynamicWorld.removeWorld(world)
        }
        group.data().remove(enter)
        world.let {
            InheritanceNode.builder("worldbase")
                .withContext(DefaultContextKeys.WORLD_KEY, it.lowercase(Locale.getDefault())).build()
        }.let {
            group.data().remove(
                it
            )
        }
        gm.saveGroup(group)
        if (checkGroup(group)) {
            val node = InheritanceNode.builder(group).build()
            um.searchAll(NodeMatcher.key(node))
                .thenAcceptAsync { result: MutableMap<UUID?, MutableCollection<InheritanceNode?>?>? ->
                    result?.keys?.forEach(Consumer { uuid: UUID? ->
                        uuid?.let {
                            um.loadUser(it).thenAccept { user: User? ->
                                user?.data()?.remove(node)
                                user?.let { it1 -> um.saveUser(it1) }
                            }
                        }
                    })
                }?.thenRun {
                    gm.deleteGroup(group)
                }
            groupMap?.remove(worldNode.getGroup())
        }
        nodeIO?.save(nodeMap)
        groupIO?.save(groupMap)
        dynamicWorld.cancelUnloadTask(world)
        return dynamicWorld.removeWorld(world)
    }

    /**
     * 为指定世界的指定权限组添加一个玩家。
     *
     * @param world 世界名称。
     * @param group 权限组（管理员、建筑师、访客之一）。
     * @param player 玩家名称。
     * @return 操作成功返回true，否则返回false。
     */
    override fun addPlayer(world: String?, group: IMapAgent.MapGroup?, player: String?): Boolean {
        val worldGroup = getWorldGroupName(world)
        if (world == "__nil") {
            plugin.logger.warning("§c无法找到" + world + "对应的权限组")
            return false
        }
        var uuid: UUID? = null
        var name = ""
        val online = player?.let { plugin.server.getPlayer(it) }
        if (online == null) {
            for (off in plugin.server.offlinePlayers!!) {
                if (Objects.requireNonNull<String?>(off.name).equals(player, ignoreCase = true)) {
                    uuid = off.uniqueId
                    name = off.name.toString()
                    break
                }
            }
            if (uuid == null) {
                plugin.logger.info("§c玩家" + player + "不存在")
                return false
            }
        } else {
            uuid = online.uniqueId
            name = online.name
        }
        val user: User? = try {
            luckPerms.userManager.loadUser(uuid).get()
        } catch (e: ExecutionException) {
            e.printStackTrace()
            return false
        } catch (e: InterruptedException) {
            e.printStackTrace()
            return false
        }
        when (group) {
            IMapAgent.MapGroup.ADMIN -> {
                run {
                    user?.data()?.add(PermissionNode.builder("mapmanager.admin.$worldGroup").build())
                    addAdmin(world, name)
                }
                run {
                    worldGroup?.let { InheritanceNode.builder(it).build() }?.let { user?.data()?.add(it) }
                    addBuilder(world, name)
                }
            }

            IMapAgent.MapGroup.BUILDER -> {
                worldGroup?.let { InheritanceNode.builder(it).build() }?.let { user?.data()?.add(it) }
                addBuilder(world, name)
            }

            IMapAgent.MapGroup.VISITOR -> {
                if (world != null) {
                    user?.data()?.add(
                        PermissionNode.builder("multiverse.access." + world.lowercase(Locale.getDefault())).build()
                    )
                }
                addVisitor(world, name)
            }

            null -> {}
        }
        user?.let { luckPerms.userManager.saveUser(it) }
        groupIO?.save(groupMap)
        return true
    }

    /**
     * 从指定世界的指定权限组中移除一个玩家。
     *
     * @param world 世界名称。
     * @param group 权限组索引（0为管理员，1为建筑师，2为访客）。
     * @param player 玩家名称。
     * @return 操作成功返回true，否则返回false。
     */
    override fun removePlayer(world: String?, group: Int, player: String?): Boolean {
        val worldGroup = getWorldGroupName(world)
        if (world == "__nil") {
            plugin.logger.warning("§c无法找到" + world + "对应的权限组")
            return false
        }
        var uuid: UUID? = null
        var name = ""
        val online = player?.let { plugin.server.getPlayer(it) }
        if (online == null) {
            for (off in plugin.server.offlinePlayers!!) {
                if (Objects.requireNonNull<String?>(off.name).equals(player, ignoreCase = true)) {
                    uuid = off.uniqueId
                    name = off.name.toString()
                    break
                }
            }
            if (uuid == null) {
                plugin.logger.warning("§c玩家" + player + "不存在")
                return false
            }
        } else {
            uuid = online.uniqueId
            name = online.name
        }
        val user = luckPerms.userManager.loadUser(uuid).join()
        when (group) {
            0 -> {
                run {
                    user?.data()?.remove(PermissionNode.builder("mapmanager.admin.$worldGroup").build())
                    removeAdmin(world, name)
                }
                run {
                    worldGroup?.let { InheritanceNode.builder(it).build() }?.let { user?.data()?.remove(it) }
                    removeBuilder(world, name)
                    groupIO?.save(groupMap)
                }
            }

            1 -> {
                worldGroup?.let { InheritanceNode.builder(it).build() }?.let { user?.data()?.remove(it) }
                removeBuilder(world, name)
                groupIO?.save(groupMap)
            }

            2 -> {
                if (world != null) {
                    user?.data()
                        ?.remove(
                            PermissionNode.builder("multiverse.access." + world.lowercase(Locale.getDefault())).build()
                        )
                }
                removeVisitor(world, name)
                nodeIO?.save(nodeMap)
            }
        }
        user?.let { luckPerms.userManager.saveUser(it) }
        return true
    }

    /**
     * 将指定世界设置为公开状态。
     *
     * @param world 世界名称。
     * @return 操作成功返回true，否则返回false。
     */
    override fun publicizeWorld(world: String?): Boolean {
        val lp = luckPerms.groupManager.getGroup("apply")
        if (lp == null) {
            plugin.logger.warning("§c未找到apply权限组")
            return false
        }
        if (world != null) {
            lp.data().add(PermissionNode.builder("multiverse.access." + world.lowercase(Locale.getDefault())).build())
        }
        luckPerms.groupManager.saveGroup(lp)
        world?.let { dynamicWorld.getMVWorld(it)?.setColor("darkgreen") }
        return true
    }

    /**
     * 将指定世界设置为私有状态。
     *
     * @param world 世界名称。
     * @return 操作成功返回true，否则返回false。
     */
    override fun privatizeWorld(world: String?): Boolean {
        val lp = luckPerms.groupManager.getGroup("apply")
        if (lp == null) {
            plugin.logger.warning("§c未找到apply权限组")
            return false
        }
        if (world != null) {
            lp.data()
                .remove(PermissionNode.builder("multiverse.access." + world.lowercase(Locale.getDefault())).build())
        }
        luckPerms.groupManager.saveGroup(lp)
        world?.let { dynamicWorld.getMVWorld(it)?.setColor("darkaqua") }
        return true
    }

    /**
     * 判断指定的世界是否为公共世界。
     *
     * @param world 要检查的世界名称。
     * @return 如果世界为公共世界，返回true，否则返回false。
     */
    override fun isPublic(world: String?): Boolean {
        val color = world?.let { dynamicWorld.getMVWorld(it)?.color }
        return color == ChatColor.GOLD || color == ChatColor.DARK_GREEN
    }

    //name -> group name | world name
    /**
     * 获取指定名称的玩家集合，基于指定的权限组筛选。
     *
     * @param name 世界名称或权限组名称。
     * @param group 权限组枚举（管理员、建筑师、访客）。
     * @return 包含玩家名称的CompletableFuture实例。
     */
    override fun getPlayers(name: String?, group: IMapAgent.MapGroup?): CompletableFuture<MutableSet<String?>?>? {
        val matcher = when (group) {
            IMapAgent.MapGroup.ADMIN -> NodeMatcher.key<Node?>(
                PermissionNode.builder("mapmanager.admin." + (name?.lowercase(Locale.getDefault()))).build()
            )

            IMapAgent.MapGroup.BUILDER -> name?.let {
                InheritanceNode.builder(it.lowercase(Locale.getDefault())).build()
            }?.let {
                NodeMatcher.key<Node?>(
                    it
                )
            }

            IMapAgent.MapGroup.VISITOR -> NodeMatcher.key<Node?>(
                PermissionNode.builder(
                    "multiverse.access." + (name?.lowercase(
                        Locale.getDefault()
                    ))
                ).build()
            )

            null -> return CompletableFuture.completedFuture(mutableSetOf())
        }
        return matcher?.let { it ->
            luckPerms.userManager.searchAll(it)
                .thenApplyAsync { results: MutableMap<UUID?, MutableCollection<Node?>?>? ->
                    results?.keys?.stream()
                        ?.map { id: UUID? -> id?.let { Bukkit.getOfflinePlayer(it) } }
                        ?.map { obj: OfflinePlayer? -> obj?.name }
                        ?.filter { obj: String? -> Objects.nonNull(obj) }
                        ?.collect(Collectors.toSet())
                }
        }


    }

    private fun putWorldGroup(group: String?, world: String?) {
        if (groupMap?.containsKey(group) == true) getWorldGroupByName(group)?.addWorld(world) else groupMap?.set(
            group,
            WorldGroup(world)
        )
    }

    /**
     * 获取玩家可以进入的所有世界
     * @param name 玩家名
     * @return 玩家可进入的所有世界
     */
    override fun getAccessWorlds(name: String?): List<String?>? {
        return dynamicWorld.getWorlds("").stream()
            .filter { world ->
                Objects.requireNonNull(
                    Bukkit.getServer().getPlayer(
                        name!!
                    )
                )
                    ?.hasPermission("multiverse.access.$world") == true
            }
            .collect(Collectors.toList())
    }

    /**
     * 与LuckPerms插件同步数据，更新本地存储的玩家权限信息。
     *
     * @param sender 命令发送者，用于回显操作结果。
     */
    fun syncWithLuckPerms(sender: CommandSender?) {
        val nodeMapBackup = ConcurrentHashMap(nodeMap)
        val groupMapBackup = ConcurrentHashMap(groupMap)

        plugin.logger.info("§e正在与LuckPerms同步数据...")
        sender?.sendMessage("§e正在与LuckPerms同步数据...")
        groupMap?.clear()

        val tasks = mutableListOf<CompletableFuture<*>>()

        plugin.logger.info("§e开始同步参观人员数据")
        nodeMap?.keys?.forEach { worldName ->
            val visitorTask = getPlayers(worldName, IMapAgent.MapGroup.VISITOR)?.thenAccept { players ->
                nodeMap?.get(worldName)?.setVisitors(players)
            }
            visitorTask?.let { tasks.add(it) }
        }

        plugin.logger.info("§e开始同步管理员和建筑人员数据")
        groupMap?.keys?.forEach { worldName ->
            val adminTask = getPlayers(worldName, IMapAgent.MapGroup.ADMIN)?.thenAccept { players ->
                groupMap?.get(worldName)?.setAdmins(players)
            }
            adminTask?.let { tasks.add(it) }

            val builderTask = getPlayers(worldName, IMapAgent.MapGroup.BUILDER)?.thenAccept { players ->
                groupMap!![worldName]?.setBuilders(players)
            }
            builderTask?.let { tasks.add(it) }
        }

        CompletableFuture.allOf(*tasks.toTypedArray()).whenComplete { _, error ->
            if (error != null) {
                plugin.logger.warning("§c数据同步时出现错误，同步中止")
                sender?.sendMessage("§c数据同步时出现错误，同步中止")
                error.printStackTrace()

                // Restore from backup in case of error
                nodeMap = nodeMapBackup
                groupMap = groupMapBackup
            } else {
                plugin.logger.info("§a所有数据均已同步完成")
                plugin.logger.info("§e数据保存中...")
                save()
                plugin.logger.info("§a数据保存完成")
                sender?.sendMessage("§a数据同步完成")
            }
        }.exceptionally { e ->
            nodeMap = nodeMapBackup
            groupMap = groupMapBackup
            plugin.logger.warning("§c数据同步异常中止")
            sender?.sendMessage("§c数据同步异常中止")
            e.printStackTrace()
            null
        }
    }


    //Getters and setters
    /**
     * 设置全局物理规则状态。
     *
     * @param physical 新的物理规则状态。
     */
    override fun setPhysical(physical: Boolean?) {
        Companion.physical = physical
        config.setPhysical(physical)
        yaml.save(config)
    }

    /**
     * 设置指定世界的物理规则状态。
     *
     * @param world 世界名称。
     * @param physical 新的物理规则状态。
     */
    override fun setPhysical(world: String?, physical: Boolean) {
        getWorldNode(world)?.setPhysical(physical)
    }

    /**
     * 获取指定世界的物理规则状态。
     *
     * @param world 世界名称。
     * @return 指定世界的物理规则状态。
     */
    override fun isPhysical(world: String?): Boolean {
        return physical ?: (getWorldNode(world)?.isPhysical() == true)
    }

    /**
     * 设置全局爆炸破坏状态。
     *
     * @param exploded 新的爆炸破坏状态。
     */
    override fun setExploded(exploded: Boolean?) {
        Companion.exploded = exploded
        config.setExploded(exploded)
        yaml.save(config)
    }

    /**
     * 设置指定世界的爆炸破坏状态。
     *
     * @param world 世界名称。
     * @param exploded 新的爆炸破坏状态。
     */
    override fun setExploded(world: String?, exploded: Boolean) {
        getWorldNode(world)?.setExploded(exploded)
    }

    /**
     * 获取指定世界的爆炸破坏状态。
     *
     * @param world 世界名称。
     * @return 指定世界的爆炸破坏状态。
     */
    override fun isExploded(world: String?): Boolean {
        return exploded ?: (getWorldNode(world)?.isExploded() == true)
    }

    /**
     * 获取指定世界的权限组名称。
     *
     * @param world 世界名称。
     * @return 权限组名称，如果世界未指定权限组，则返回null。
     */
    override fun getWorldGroupName(world: String?): String? {
        return nodeMap?.getOrDefault(world, nullWorldNode)?.getGroup()
    }

    /**
     * 获取权限组所包含的世界。
     *
     * @param group 权限组名称。
     * @return 世界列表，若权限组下无世界，则返回null。
     */
    override fun getWorldListByGroup(group: String?): List<String?>? {
        return groupMap!!.getOrDefault(group, nullWorldGroup)!!.getWorlds()!!.stream().toList()
    }

    /**
     * 获取指定世界的管理员集合。
     *
     * @param world 指定的世界对象。
     * @return 该世界的管理员用户名集合。
     */
    override fun getAdminSet(world: World?): MutableSet<String?>? {
        return getWorldGroup(world?.name)?.getAdmins()
    }

    /**
     * 获取指定世界的建筑师集合。
     *
     * @param world 指定的世界对象。
     * @return 该世界的建筑师用户名集合。
     */
    override fun getBuilderSet(world: World?): MutableSet<String?>? {
        return getWorldGroup(world?.name)?.getBuilders()
    }

    /**
     * 获取指定世界的访客集合。
     *
     * @param world 指定的世界对象。
     * @return 该世界的访客用户名集合。
     */
    override fun getVisitorSet(world: World?): MutableSet<String?>? {
        return getWorldNode(world?.name)?.getVisitors()
    }

    private fun addAdmin(world: String?, player: String?): Boolean {
        return getWorldGroup(world)?.addAdmin(player) == true
    }

    private fun addBuilder(world: String?, player: String?): Boolean {
        return getWorldGroup(world)?.addBuilder(player) == true
    }

    private fun addVisitor(world: String?, player: String?): Boolean {
        return getWorldNode(world)?.addVisitor(player) == true
    }

    private fun removeAdmin(world: String?, player: String?): Boolean {
        return getWorldGroup(world)?.removeAdmin(player) == true
    }

    private fun removeBuilder(world: String?, player: String?): Boolean {
        return getWorldGroup(world)?.removeBuilder(player) == true
    }

    private fun removeVisitor(world: String?, player: String?): Boolean {
        return getWorldNode(world)?.removeVisitor(player) == true
    }

    /**
     * 获取当前所有MapManager所管理的世界的节点映射。
     *
     * @return 包含所有MapManager所管理的世界及其对应节点信息的映射表。世界名称作为键，对应的[WorldNode]作为值。
     */
    override fun getNodeMap(): MutableMap<String?, WorldNode?>? {
        return nodeMap
    }

    /**
     * 获取当前所有权限组的映射。
     *
     * @return 包含所有权限组及其对应信息的映射表。权限组名称作为键，对应的[WorldGroup]作为值。
     */
    override fun getGroupMap(): MutableMap<String?, WorldGroup?>? {
        return groupMap
    }

    /**
     * 检查指定的世界是否被MapManager所管理。
     *
     * @param world 要检查的世界名称。
     * @return 如果指定的世界被MapManager所管理，返回true；否则返回false。
     */
    override fun containsWorld(world: String?): Boolean {
        return nodeMap?.containsKey(world) == true
    }

    /**
     * 获取当前所有MapManager所管理的世界的名称。
     *
     * @return 一个包含所有MapManager所管理的世界名称的集合。
     */
    override fun getWorlds(): MutableSet<String?> {
        return nodeMap?.keys!!
    }

    companion object {
        // 全局物理效果设置。当设置为true时，启用全局物理效果；否则禁用。
        private var physical: Boolean? = null

        // 全局爆炸效果设置。当设置为true时，启用全局爆炸效果；否则禁用。
        private var exploded: Boolean? = null
    }
}
