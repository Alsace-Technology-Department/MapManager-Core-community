package work.alsace.mapmanager

import net.luckperms.api.LuckPerms
import work.alsace.mapmanager.service.IDynamicWorld
import work.alsace.mapmanager.service.IMainYaml
import work.alsace.mapmanager.service.IMapAgent

interface IMapManager {
    /**
     * 获取DynamicWorld实例
     */
    fun getDynamicWorld(): IDynamicWorld

    /**
     * 设置DynamicWorld实例
     * @param dynamicWorld DynamicWorld实例
     */
    fun setDynamicWorld(dynamicWorld: IDynamicWorld)

    /**
     * 获取MapAgent实例
     * @return MapAgent实例
     */
    fun getMapAgent(): IMapAgent

    /**
     * 设置MapAgent实例
     * @param mapAgent MapAgent实例
     */
    fun setMapAgent(mapAgent: IMapAgent)

    /**
     * 获取LuckPerms实例
     * @return LuckPerms实例
     */
    fun getLuckPerms(): LuckPerms

    /**
     * 获取MainYaml实例
     * @return MainYaml实例
     */
    fun getMainYaml(): IMainYaml

    /**
     * 设置MainYaml实例
     * @param yaml MainYaml实例
     */
    fun setMainYaml(yaml: IMainYaml)


}
