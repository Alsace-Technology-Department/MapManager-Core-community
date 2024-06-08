package work.alsace.mapmanager.service

import work.alsace.mapmanager.pojo.MainConfig

interface MainYaml {
    /**
     * 加载插件配置
     */
    fun load(): MainConfig

    /**
     * 保存插件配置
     */
    fun save(config: MainConfig)
}
