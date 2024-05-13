package work.alsace.mapmanager.pojo

import java.util.*
import java.util.stream.Collectors
import java.util.stream.Stream

class WorldGroup {
    private var admins: MutableSet<String>
    private var builders: MutableSet<String>
    private var worlds: MutableSet<String>

    constructor() {
        admins = TreeSet()
        builders = TreeSet()
        worlds = TreeSet()
    }

    constructor(world: String, owner: String) {
        admins = Stream.of(owner).collect(Collectors.toSet())
        builders = Stream.of(owner).collect(Collectors.toSet())
        worlds = Stream.of(world).collect(Collectors.toSet())
    }

    constructor(world: String) {
        admins = TreeSet()
        builders = TreeSet()
        worlds = Stream.of(world).collect(Collectors.toSet())
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
     * 获取管理员。
     * @return 管理员。
     */
    fun getAdmins(): MutableSet<String> {
        return admins
    }

    /**
     * 获取建造者。
     * @return 建造者。
     */
    fun getBuilders(): MutableSet<String> {
        return builders
    }

    /**
     * 获取世界。
     * @return 世界。
     */
    fun getWorlds(): MutableSet<String> {
        return worlds
    }

    /**
     * 设置管理员。
     * @param admins 管理员。
     */
    fun setAdmins(admins: MutableSet<String>) {
        this.admins = admins
    }

    /**
     * 设置建造者。
     * @param builders 建造者。
     */
    fun setBuilders(builders: MutableSet<String>) {
        this.builders = builders
    }

    /**
     * 设置世界。
     * @param worlds 世界。
     */
    fun setWorlds(worlds: MutableSet<String>) {
        this.worlds = worlds
    }

    /**
     * 获取字符串表示。
     * @return 字符串表示。
     */
    override fun toString(): String {
        return "{" +
                "admins=" + admins +
                ", builders=" + builders +
                ", worlds=" + worlds +
                '}'
    }
}
