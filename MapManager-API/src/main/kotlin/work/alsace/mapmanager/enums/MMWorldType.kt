package work.alsace.mapmanager.enums

enum class MMWorldType {
    /**
     * 平坦世界
     */
    FLAT,

    /**
     * 常规世界
     */
    NORMAL,

    /**
     * 虚空世界
     */
    VOID,

    /**
     * 下界
     */
    NETHER,

    /**
     * 末地
     */
    END;

    private val value: String? = null

    open fun getValue(): String? {
        return value
    }

    override fun toString(): String {
        return "MMWorldType(value=$value)"
    }


}
