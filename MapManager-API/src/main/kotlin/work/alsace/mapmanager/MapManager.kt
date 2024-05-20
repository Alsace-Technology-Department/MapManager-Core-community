package work.alsace.mapmanager

import net.luckperms.api.LuckPerms
import work.alsace.mapmanager.service.DynamicWorld
import work.alsace.mapmanager.service.MainYaml
import work.alsace.mapmanager.service.MapAgent

interface MapManager {
    /**
     * 获取DynamicWorld实例
     */
    fun getDynamicWorld(): DynamicWorld

    /**
     * 设置DynamicWorld实例
     * @param dynamicWorld DynamicWorld实例
     */
    fun setDynamicWorld(dynamicWorld: DynamicWorld)

    /**
     * 获取MapAgent实例
     * @return MapAgent实例
     */
    fun getMapAgent(): MapAgent

    /**
     * 设置MapAgent实例
     * @param mapAgent MapAgent实例
     */
    fun setMapAgent(mapAgent: MapAgent)

    /**
     * 获取LuckPerms实例
     * @return LuckPerms实例
     */
    fun getLuckPerms(): LuckPerms

    /**
     * 获取MainYaml实例
     * @return MainYaml实例
     */
    fun getMainYaml(): MainYaml

    /**
     * 设置MainYaml实例
     * @param yaml MainYaml实例
     */
    fun setMainYaml(yaml: MainYaml)

    /**
     * 获取MapManager实例
     * @return MapManager实例
     */
    fun getInstance(): MapManager?
}
