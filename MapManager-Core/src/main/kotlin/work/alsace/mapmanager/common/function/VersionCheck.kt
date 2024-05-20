package work.alsace.mapmanager.common.function

import com.onarandombox.MultiverseCore.utils.WorldNameChecker
import net.querz.nbt.io.NBTUtil
import net.querz.nbt.io.NamedTag
import net.querz.nbt.tag.CompoundTag
import work.alsace.mapmanager.MapManagerImpl
import java.io.File
import java.io.IOException


class VersionCheck(private val plugin: MapManagerImpl) {
    /**
     * 检测主目录下地图文件版本是否合法
     * @param world String 地图名
     * @return true为合法，false不合法
     */
    fun isMapVersionCorrect(world: String): Boolean {
        val worldDir = File(plugin.server.worldContainer, world)
        if (!WorldNameChecker.isValidWorldFolder(worldDir)) {
            plugin.logger.info("§c未发现" + world + "中的.dat文件")
            return false
        }
        return WorldNameChecker.isValidWorldFolder(worldDir)
                && isLevelFileVersionCorrect(worldDir)
    }

    /**
     * 检测地图文件版本是否合法
     * @param dir File 地图文件夹
     * @return true为合法，false不合法
     */
    fun isMapVersionCorrect(dir: File): Boolean {
        if (!WorldNameChecker.isValidWorldFolder(dir)) {
            plugin.logger.info("§c未发现" + dir + "中的.dat文件")
            return false
        }
        return WorldNameChecker.isValidWorldFolder(dir)
                && isLevelFileVersionCorrect(dir)
    }

    /**
     * 检测地图文件版本是否合法
     * @param file File 地图文件夹
     * @return true为合法，false不合法
     */
    private fun isLevelFileVersionCorrect(file: File): Boolean {
        try {
            val levelDatFile = File("$file/level.dat")
            val namedTag: NamedTag = NBTUtil.read(levelDatFile)
            val levelDatTag: CompoundTag = namedTag.tag as CompoundTag
            return if (levelDatTag.containsKey("Data") && levelDatTag.get("Data") is CompoundTag) {
                val dataTag: CompoundTag = levelDatTag.getCompoundTag("Data")
                if (dataTag.containsKey("version") && dataTag.get("version") != null) {
                    val version: Int = dataTag.getInt("DataVersion")
                    plugin.logger.info("level.dat version: $version")
                    version <= serverVersion
                } else {
                    plugin.logger.info("Version information not found in level.dat")
                    false
                }
            } else {
                plugin.logger.info("Invalid level.dat format")
                false
            }
        } catch (e: IOException) {
            e.printStackTrace()
        }
        return false
    }

    /**
     * 获取服务器版本
     * @return 服务器版本
     */
    @Suppress("DEPRECATION")
    private val serverVersion: Int
        get() = plugin.server.unsafe.dataVersion
}
