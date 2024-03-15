package work.alsace.mapmanager.data

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

    fun getGlobal(): Global? {
        return global
    }

    fun setGlobal(global: Global?) {
        this.global = global
    }

    fun getPhysics(): Boolean? {
        return global?.getPhysical()
    }

    fun setPhysical(physics: Boolean?) {
        global?.setPhysical(physics)
    }

    fun getExplosion(): Boolean? {
        return global?.getExploded()
    }

    fun setExploded(explosion: Boolean?) {
        global?.setExploded(explosion)
    }
}
