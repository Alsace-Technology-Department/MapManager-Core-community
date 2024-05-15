package work.alsace.mapmanager.pojo

import java.util.*

class WorldNode(
    var group: String = "__nil",
    var exploded: Boolean = false,
    var physical: Boolean = true,
    var visitors: MutableSet<String> = TreeSet()
) {

    constructor(group: String) : this(group, false, true, TreeSet())

    /**
     * 添加世界访问者。
     * @param player 玩家名称。
     * @return 是否添加成功。
     */
    fun addVisitor(player: String): Boolean {
        return visitors.add(player)
    }

    /**
     * 移除世界访问者。
     * @param player 玩家名称。
     * @return 是否移除成功。
     */
    fun removeVisitor(player: String): Boolean {
        return visitors.remove(player)
    }

    /**
     * 获取字符串表示。
     * @return 字符串表示。
     */
    override fun toString(): String {
        return "WorldNode(group='$group', exploded=$exploded, physical=$physical, visitors=$visitors)"
    }
}
