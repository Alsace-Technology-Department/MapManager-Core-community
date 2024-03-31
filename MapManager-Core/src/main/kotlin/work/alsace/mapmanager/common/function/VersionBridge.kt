package work.alsace.mapmanager.common.function

import work.alsace.mapmanager.command.WorldCommandV116
import work.alsace.mapmanager.command.WorldCommandV120
import work.alsace.mapmanager.function.MainYamlV116
import work.alsace.mapmanager.function.MainYamlV120
import work.alsace.mapmanager.MapManager

class VersionBridge {
    /**
     * 检测服务器版本并加载对应的类
     * @param plugin 插件主类
     */
    fun serverVersionChecks(plugin: MapManager) {
        val version =
            plugin.server.javaClass.getPackage().name.replace(".", ",").split(",".toRegex())
                .dropLastWhile { it.isEmpty() }
                .toTypedArray()[3]
        plugin.logger.info("Server version: $version")
        when (version) {
            "v1_16_R1" -> {
                plugin.registerCommand("world", WorldCommandV116(plugin))
                plugin.setMainYaml(MainYamlV116(plugin))
            }

            "v1_17_R1" -> {
                plugin.registerCommand("world", WorldCommandV116(plugin))
                plugin.setMainYaml(MainYamlV116(plugin))
            }

            "v1_18_R1" -> {
                plugin.registerCommand("world", WorldCommandV116(plugin))
                plugin.setMainYaml(MainYamlV116(plugin))
            }

            "v1_19_R1" -> {
                plugin.registerCommand("world", WorldCommandV116(plugin))
                plugin.setMainYaml(MainYamlV116(plugin))
            }

            "v1_20_R1" -> {
                plugin.registerCommand("world", WorldCommandV120(plugin))
                plugin.setMainYaml(MainYamlV120(plugin))
            }

            else -> {
                throw UnsupportedOperationException("Unsupported server version: $version");
            }
        }
    }
}
