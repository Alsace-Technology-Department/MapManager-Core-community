package work.alsace.mapmanager.enums

enum class MMWorldType(s: String) {
    FLAT("flat"),
    NORMAL("normal"),
    VOID("void_gen"),
    NETHER("nether"),
    END("the_end");

    private val value: String? = null

    open fun getValue(): String? {
        return value
    }

    override fun toString(): String {
        return "MMWorldType(value=$value)"
    }


}
