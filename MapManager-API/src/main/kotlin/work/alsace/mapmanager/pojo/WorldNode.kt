package work.alsace.mapmanager.pojo

import java.util.*

class WorldNode {
    private var group: String?
    private var exploded: Boolean
    private var physical: Boolean
    private var visitors: MutableSet<String?>?

    constructor() {
        group = "__nil"
        exploded = false
        physical = true
        visitors = TreeSet()
    }

    constructor(group: String?) {
        this.group = group
        exploded = false
        physical = true
        visitors = TreeSet()
    }

    /**
     * 获取世界分组。
     * @return 世界分组。
     */
    fun getGroup(): String? {
        return group
    }

    /**
     * 设置世界分组。
     * @param group 世界分组。
     */
    fun setGroup(group: String?) {
        this.group = group
    }

    /**
     * 获取世界是否爆炸。
     * @return 世界是否爆炸。
     */
    fun isExploded(): Boolean {
        return exploded
    }

    /**
     * 设置世界是否爆炸。
     * @param exploded 世界是否爆炸。
     */
    fun setExploded(exploded: Boolean) {
        this.exploded = exploded
    }

    /**
     * 获取世界是否方块更新。
     * @return 世界是否方块更新。
     */
    fun isPhysical(): Boolean {
        return physical
    }

    /**
     * 设置世界是否方块更新。
     * @param physical 世界是否方块更新。
     */
    fun setPhysical(physical: Boolean) {
        this.physical = physical
    }

    /**
     * 获取世界访问者。
     * @return 世界访问者。
     */
    fun getVisitors(): MutableSet<String?>? {
        return visitors
    }

    /**
     * 设置世界访问者。
     * @param visitors 世界访问者。
     */
    fun setVisitors(visitors: MutableSet<String?>?) {
        this.visitors = visitors
    }

    /**
     * 添加世界访问者。
     * @param player 玩家名称。
     * @return 是否添加成功。
     */
    fun addVisitor(player: String?): Boolean {
        return visitors?.add(player) == true
    }

    /**
     * 移除世界访问者。
     * @param player 玩家名称。
     * @return 是否移除成功。
     */
    fun removeVisitor(player: String?): Boolean {
        return visitors?.remove(player) == true
    }

    /**
     * 获取世界访问者。
     * @return 世界访问者。
     */
    override fun toString(): String {
        return "WorldNode{" +
                "group='" + group + '\'' +
                ", exploded=" + exploded +
                ", physical=" + physical +
                ", visitors=" + visitors +
                '}'
    }
}
