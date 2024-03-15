package work.alsace.mapmanager.data

import java.util.*
import java.util.stream.Collectors
import java.util.stream.Stream

class WorldGroup {
    private var admins: MutableSet<String?>? = null
    private var builders: MutableSet<String?>? = null
    private var worlds: MutableSet<String?>?

    constructor() {
        admins = TreeSet()
        builders = TreeSet()
        worlds = TreeSet()
    }

    constructor(world: String?, owner: String?) {
        if (owner == null) {
            admins = TreeSet()
            builders = TreeSet()
        } else {
            admins = Stream.of(owner).collect(Collectors.toSet())
            builders = Stream.of(owner).collect(Collectors.toSet())
        }
        worlds = if (world == null) TreeSet() else Stream.of(world).collect(Collectors.toSet())
    }

    constructor(world: String?) {
        admins = TreeSet()
        builders = TreeSet()
        worlds = if (world == null) TreeSet() else Stream.of(world).collect(Collectors.toSet())
    }

    fun addAdmin(admin: String?): Boolean {
        return admins?.add(admin) == true
    }

    fun addBuilder(builder: String?): Boolean {
        return builders?.add(builder) == true
    }

    fun addWorld(world: String?): Boolean {
        return worlds?.add(world) == true
    }

    fun removeAdmin(admin: String?): Boolean {
        return admins?.remove(admin) == true
    }

    fun removeBuilder(builder: String?): Boolean {
        return builders?.remove(builder) == true
    }

    fun removeWorld(world: String?): Boolean {
        return worlds?.remove(world) == true
    }

    fun getAdmins(): MutableSet<String?>? {
        return admins
    }

    fun getBuilders(): MutableSet<String?>? {
        return builders
    }

    fun getWorlds(): MutableSet<String?>? {
        return worlds
    }

    fun setAdmins(admins: MutableSet<String?>?) {
        this.admins = admins
    }

    fun setBuilders(builders: MutableSet<String?>?) {
        this.builders = builders
    }

    fun setWorlds(worlds: MutableSet<String?>?) {
        this.worlds = worlds
    }

    override fun toString(): String {
        return "{" +
                "admins=" + admins +
                ", builders=" + builders +
                ", worlds=" + worlds +
                '}'
    }
}
