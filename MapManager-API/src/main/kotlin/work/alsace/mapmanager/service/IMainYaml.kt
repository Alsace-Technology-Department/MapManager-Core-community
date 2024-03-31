package work.alsace.mapmanager.service

import work.alsace.mapmanager.pojo.MainConfig

interface IMainYaml {
    fun load(): MainConfig?
    fun save(config: MainConfig?)
}
