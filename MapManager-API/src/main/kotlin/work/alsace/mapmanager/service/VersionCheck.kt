package work.alsace.mapmanager.service

import java.io.File

interface VersionCheck {
    /**
     * 获取服务器版本
     * @return 服务器版本
     */
    @Suppress("DEPRECATION")
    val serverVersion: Int

    /**
     * 检测主目录下地图文件版本是否合法
     * @param world String 地图名
     * @return true为合法，false不合法
     */
    fun isMapVersionCorrect(world: String): Boolean

    /**
     * 检测地图文件版本是否合法
     * @param dir File 地图文件夹
     * @return true为合法，false不合法
     */
    fun isMapVersionCorrect(dir: File): Boolean

}
