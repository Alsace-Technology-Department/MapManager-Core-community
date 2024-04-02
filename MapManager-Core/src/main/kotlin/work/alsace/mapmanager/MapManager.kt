package work.alsace.mapmanager

import net.luckperms.api.LuckPerms
import net.luckperms.api.model.group.Group
import net.luckperms.api.node.types.InheritanceNode
import org.apache.logging.log4j.LogManager
import org.apache.logging.log4j.core.Logger
import org.bukkit.Bukkit
import org.bukkit.command.CommandExecutor
import org.bukkit.command.TabExecutor
import org.bukkit.plugin.java.JavaPlugin
import work.alsace.mapmanager.common.command.*
import work.alsace.mapmanager.common.function.DynamicWorld
import work.alsace.mapmanager.service.IMainYaml
import work.alsace.mapmanager.common.function.MapAgent
import work.alsace.mapmanager.common.function.VersionBridge
import work.alsace.mapmanager.common.listener.BlockListener
import work.alsace.mapmanager.common.listener.PlayerListener
import work.alsace.mapmanager.common.log.Log4JFilter
import work.alsace.mapmanager.service.IDynamicWorld
import work.alsace.mapmanager.service.IMapAgent
import java.io.File
import java.io.IOException
import java.util.*

class MapManager : JavaPlugin(), IMapManager {
    private var dynamicWorld: IDynamicWorld? = null
    private var mapAgent: IMapAgent? = null
    private var luckPerms: LuckPerms? = null
    private var yaml: IMainYaml? = null

    override fun onEnable() {
        server.consoleSender.sendMessage("[§6MapManager§7] §f启动中...")

        logger.info("正在加载配置...")
        try {
            loadConfig()
        } catch (e: IOException) {
            throw RuntimeException(e)
        }

        logger.info("正在获取LuckPerms API...")
        (LogManager.getRootLogger() as Logger).addFilter(Log4JFilter())
        this.luckPerms = Bukkit.getServicesManager().getRegistration(LuckPerms::class.java)?.provider
        initPermission()

        VersionBridge().serverVersionChecks(this)

        logger.info("正在注册指令和事件...")
        registerCommand("import", ImportCommand(this))
        registerCommand("delete", DeleteCommand(this))
        registerCommand("mapadmin", MapAdminCommand(this))
        registerCommand("write", WriteCommand(this))
        registerCommand("worldtp", WorldTPCommand(this))
        registerCommand("create", CreateCommand(this))
        server.pluginManager.registerEvents(BlockListener(this), this)
        server.pluginManager.registerEvents(PlayerListener(this), this)
        server.consoleSender.sendMessage("[§6MapManager§7] §f加载成功！")
    }

    override fun onDisable() {
        mapAgent?.save()
        server.consoleSender.sendMessage("[§6MapManager§7] §f已卸载")
    }

    private fun registerCommand(cmd: String, executor: CommandExecutor) {
        cmd.let { getCommand(it)?.setExecutor(executor) }
    }

    fun registerCommand(cmd: String, executor: TabExecutor) {
        Objects.requireNonNull(getCommand(cmd))?.setExecutor(executor)
        Objects.requireNonNull(getCommand(cmd))?.tabCompleter = executor
    }

    override fun getDynamicWorld(): DynamicWorld {
        return (dynamicWorld as DynamicWorld?)!!
    }

    override fun setDynamicWorld(dynamicWorld: IDynamicWorld) {
        this.dynamicWorld = dynamicWorld
    }

    override fun getMapAgent(): MapAgent {
        return (mapAgent as MapAgent?)!!
    }

    override fun setMapAgent(mapAgent: IMapAgent) {
        this.mapAgent = mapAgent
    }

    override fun getLuckPerms(): LuckPerms {
        return luckPerms!!
    }

    override fun getMainYaml(): IMainYaml {
        return yaml!!
    }

    override fun setMainYaml(yaml: IMainYaml) {
        this.yaml = yaml
    }

    @Throws(IOException::class)
    private fun loadConfig() {
        val configFile = File(dataFolder, "config.yml")
        if (!configFile.exists()) {
            saveResource("config.yml", false)
        }
        val worldsFile = File(dataFolder, "worlds.json")
        val groupsFile = File(dataFolder, "groups.json")
        if (!worldsFile.exists()) {
            if (!worldsFile.createNewFile()) {
                logger.warning("无法创建 worlds.json 文件")
            }
        }
        if (!groupsFile.exists()) {
            if (!groupsFile.createNewFile()) {
                logger.warning("无法创建 groups.json 文件")
            }
        }
        logger.info("运行所需文件创建完成")
    }

    private fun initPermission() {
        val manager = luckPerms?.groupManager
        manager?.loadGroup("apply")?.thenAcceptAsync {
            if (it == null) {
                manager.createAndLoadGroup("apply").thenAcceptAsync { lp: Group? ->
                    val data = lp?.data()
                    data?.add(InheritanceNode.builder("default").build())
                    if (lp != null) {
                        manager.saveGroup(lp)
                        logger.info("权限组" + lp.name + "已创建并初始化完毕")
                    }
                }
            }
        }
        manager?.loadGroup("public")?.thenAcceptAsync {
            if (it == null) {
                manager.createAndLoadGroup("public").thenAcceptAsync { lp: Group? ->
                    if (lp != null) {
                        manager.saveGroup(lp)
                        logger.info("权限组" + lp.name + "已创建并初始化完毕")
                    }
                }
            }
        }
        manager?.loadGroup("worldbase")?.thenAcceptAsync {
            if (it == null) {
                manager.createAndLoadGroup("worldbase").thenAcceptAsync { lp: Group? ->
                    val data = lp?.data()
                    data?.add(InheritanceNode.builder("default").build())
                    if (lp != null) {
                        manager.saveGroup(lp)
                        logger.info("权限组" + lp.name + "已创建并初始化完毕")
                    }
                }
            }
        }
    }
}
