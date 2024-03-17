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

    fun getGroup(): String? {
        return group
    }

    fun setGroup(group: String?) {
        this.group = group
    }

    fun isExploded(): Boolean {
        return exploded
    }

    fun setExploded(exploded: Boolean) {
        this.exploded = exploded
    }

    fun isPhysical(): Boolean {
        return physical
    }

    fun setPhysical(physical: Boolean) {
        this.physical = physical
    }

    fun getVisitors(): MutableSet<String?>? {
        return visitors
    }

    fun setVisitors(visitors: MutableSet<String?>?) {
        this.visitors = visitors
    }

    fun addVisitor(player: String?): Boolean {
        return visitors?.add(player) == true
    }

    fun removeVisitor(player: String?): Boolean {
        return visitors?.remove(player) == true
    }

    override fun toString(): String {
        return "WorldNode{" +
                "group='" + group + '\'' +
                ", exploded=" + exploded +
                ", physical=" + physical +
                ", visitors=" + visitors +
                '}'
    }
}
