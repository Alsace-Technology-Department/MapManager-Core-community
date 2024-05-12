package work.alsace.mapmanager.common.function

import com.onarandombox.MultiverseCore.utils.WorldNameChecker
import net.querz.mca.Chunk
import net.querz.mca.MCAUtil
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
                && areRegionFilesVersionCorrect(worldDir)
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
                && areRegionFilesVersionCorrect(dir)
                && isLevelFileVersionCorrect(dir)
    }

    /**
     * 检测地图文件版本是否合法
     * @param dir File 地图文件夹
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
     * 检测区块文件版本是否兼容
     * @param worldDirectory File 地图文件夹
     * @return true为兼容，false不兼容
     */
    private fun areRegionFilesVersionCorrect(worldDirectory: File): Boolean {
        val regionDir = File(worldDirectory, "region")
        if (!regionDir.exists() || !regionDir.isDirectory) {
            plugin.logger.info("§c未发现" + worldDirectory.name + "的区块文件目录")
            return false
        }
        val regionFiles = regionDir.listFiles { _: File?, name: String ->
            name.endsWith(
                ".mca"
            )
        }
            ?: return false

        plugin.logger.info("正在遍历区块文件，请稍后...")
        for (regionFile in regionFiles) {
            if (!isRegionFileVersionCompatible(regionFile)) {
                return false
            }
        }
        return true
    }

    /**
     * 检测区块文件版本是否兼容
     * @param regionFile File 区块文件
     * @return true为兼容，false不兼容
     */
    private fun isRegionFileVersionCompatible(regionFile: File): Boolean {
        try {
            var maxDataVersion = 0
            val mcaFile = MCAUtil.read(regionFile)
            for (x in 0..31) {
                for (z in 0..31) {
                    if (mcaFile.getChunk(x, z) != null) {
                        val chunk: Chunk = mcaFile.getChunk(x, z)
                        if(chunk.dataVersion > maxDataVersion) maxDataVersion = chunk.dataVersion
                    }
                }
            }
            return maxDataVersion <= serverVersion
        } catch (e: IOException) {
            plugin.logger.warning("读取区块文件" + regionFile.name + "时发生错误")
            e.printStackTrace()
            return false
        }
    }

    /**
     * 获取服务器版本
     * @return 服务器版本
     */
    private val serverVersion: Int
        get() = plugin.server.unsafe.dataVersion
}
