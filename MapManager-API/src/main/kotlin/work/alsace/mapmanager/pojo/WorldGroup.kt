package work.alsace.mapmanager.pojo

import java.util.TreeSet

class WorldGroup {

    var admins: MutableSet<String> = TreeSet()
    var builders: MutableSet<String> = TreeSet()
    var worlds: MutableSet<String> = TreeSet()
        private set

    constructor()

    constructor(world: String, owner: String) {
        admins = mutableSetOf(owner)
        builders = mutableSetOf(owner)
        worlds = mutableSetOf(world)
    }

    constructor(world: String) {
        worlds = mutableSetOf(world)
    }

    /**
     * 添加管理员。
     * @param admin 管理员。
     * @return 是否添加成功。
     */
    fun addAdmin(admin: String): Boolean {
        return admins.add(admin)
    }

    /**
     * 添加建造者。
     * @param builder 建造者。
     * @return 是否添加成功。
     */
    fun addBuilder(builder: String): Boolean {
        return builders.add(builder)
    }

    /**
     * 添加世界。
     * @param world 世界。
     * @return 是否添加成功。
     */
    fun addWorld(world: String): Boolean {
        return worlds.add(world)
    }

    /**
     * 移除管理员。
     * @param admin 管理员。
     * @return 是否移除成功。
     */
    fun removeAdmin(admin: String): Boolean {
        return admins.remove(admin)
    }

    /**
     * 移除建造者。
     * @param builder 建造者。
     * @return 是否移除成功。
     */
    fun removeBuilder(builder: String): Boolean {
        return builders.remove(builder)
    }

    /**
     * 移除世界。
     * @param world 世界。
     * @return 是否移除成功。
     */
    fun removeWorld(world: String): Boolean {
        return worlds.remove(world)
    }

    /**
     * 获取字符串表示。
     * @return 字符串表示。
     */
    override fun toString(): String {
        return "WorldGroup(admins=$admins, builders=$builders, worlds=$worlds)"
    }
}
