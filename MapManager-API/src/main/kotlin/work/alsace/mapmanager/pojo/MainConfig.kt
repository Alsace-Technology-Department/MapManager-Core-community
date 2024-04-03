package work.alsace.mapmanager.pojo

class MainConfig {
    class Global {
        private var physical: Boolean? = null
        private var exploded: Boolean? = null
        fun getPhysical(): Boolean? {
            return physical
        }

        fun setPhysical(physical: Boolean?) {
            this.physical = physical
        }

        fun getExploded(): Boolean? {
            return exploded
        }

        fun setExploded(exploded: Boolean?) {
            this.exploded = exploded
        }
    }

    private var global: Global?

    init {
        global = Global()
    }

    /**
     * 获取全局配置实例。
     * @return 全局配置实例。
     */
    fun getGlobal(): Global? {
        return global
    }

    /**
     * 设置全局配置实例。
     * @param global 全局配置实例。
     */
    fun setGlobal(global: Global?) {
        this.global = global
    }

    /**
     * 获取全局物理配置。
     * @return 全局物理的配置。
     */
    fun getPhysics(): Boolean? {
        return global?.getPhysical()
    }

    /**
     * 设置全局物理配置。
     * @param physics 物理配置
     */
    fun setPhysical(physics: Boolean?) {
        global?.setPhysical(physics)
    }

    /**
     * 获取全局爆炸配置。
     * @return 全局爆炸的配置。
     */
    fun getExplosion(): Boolean? {
        return global?.getExploded()
    }

    /**
     * 设置全局爆炸配置。
     * @param explosion 爆炸配置
     */
    fun setExploded(explosion: Boolean?) {
        global?.setExploded(explosion)
    }
}
