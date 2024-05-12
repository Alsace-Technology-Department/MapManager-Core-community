package work.alsace.mapmanager.service

import work.alsace.mapmanager.pojo.MainConfig

interface MainYaml {
    fun load(): MainConfig
    fun save(config: MainConfig)
}
