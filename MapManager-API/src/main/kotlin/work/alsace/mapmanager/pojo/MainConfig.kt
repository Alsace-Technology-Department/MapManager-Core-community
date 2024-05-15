package work.alsace.mapmanager.pojo

class MainConfig {

    class Global(
        var physical: Boolean? = null,
        var exploded: Boolean? = null
    )

    var global: Global? = Global()

    /**
     * 获取全局物理配置。
     * @return 全局物理的配置。
     */
    fun getPhysics(): Boolean? {
        return global?.physical
    }

    /**
     * 设置全局物理配置。
     * @param physics 物理配置
     */
    fun setPhysical(physics: Boolean?) {
        global?.physical = physics
    }

    /**
     * 获取全局爆炸配置。
     * @return 全局爆炸的配置。
     */
    fun getExplosion(): Boolean? {
        return global?.exploded
    }

    /**
     * 设置全局爆炸配置。
     * @param explosion 爆炸配置
     */
    fun setExploded(explosion: Boolean?) {
        global?.exploded = explosion
    }
}
