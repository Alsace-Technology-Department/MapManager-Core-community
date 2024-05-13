package work.alsace.mapmanager.common.version

import work.alsace.mapmanager.MapManagerImpl
import work.alsace.mapmanager.command.WorldCommandV116
import work.alsace.mapmanager.command.WorldCommandV120
import work.alsace.mapmanager.common.function.DynamicWorldImpl
import work.alsace.mapmanager.common.function.MapAgentImpl
import work.alsace.mapmanager.function.MainYamlV116
import work.alsace.mapmanager.function.MainYamlV120

class VersionBridge {
    /**
     * 检测服务器版本并加载对应的类
     * @param plugin 插件主类
     */
    fun serverVersionChecks(plugin: MapManagerImpl) {
        val version =
            plugin.server.javaClass.getPackage().name.replace(".", ",").split(",".toRegex())
                .dropLastWhile { it.isEmpty() }
                .toTypedArray()[3]
        plugin.logger.info("Server version: $version")
        when {
            version.startsWith("v1_16") -> {
                plugin.setMainYaml(MainYamlV116(plugin))
                plugin.setDynamicWorld(DynamicWorldImpl(plugin))
                plugin.setMapAgent(MapAgentImpl(plugin))
                plugin.registerCommand("world", WorldCommandV116(plugin))
            }

            version.startsWith("v1_17") -> {
                plugin.setMainYaml(MainYamlV116(plugin))
                plugin.setDynamicWorld(DynamicWorldImpl(plugin))
                plugin.setMapAgent(MapAgentImpl(plugin))
                plugin.registerCommand("world", WorldCommandV116(plugin))
            }

            version.startsWith("v1_18") -> {
                plugin.setMainYaml(MainYamlV116(plugin))
                plugin.setDynamicWorld(DynamicWorldImpl(plugin))
                plugin.setMapAgent(MapAgentImpl(plugin))
                plugin.registerCommand("world", WorldCommandV116(plugin))
            }

            version.startsWith("v1_19") -> {
                plugin.setMainYaml(MainYamlV116(plugin))
                plugin.setDynamicWorld(DynamicWorldImpl(plugin))
                plugin.setMapAgent(MapAgentImpl(plugin))
                plugin.registerCommand("world", WorldCommandV116(plugin))
            }

            version.startsWith("v1_20") -> {
                plugin.setMainYaml(MainYamlV120(plugin))
                plugin.setDynamicWorld(DynamicWorldImpl(plugin))
                plugin.setMapAgent(MapAgentImpl(plugin))
                plugin.registerCommand("world", WorldCommandV120(plugin))
            }

            else -> {
                throw UnsupportedOperationException("Unsupported server version: $version")
            }
        }
    }
}
