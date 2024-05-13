package work.alsace.mapmanager.common.function

import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.google.gson.reflect.TypeToken
import org.bukkit.plugin.Plugin
import java.io.File
import java.io.FileReader
import java.io.FileWriter
import java.io.IOException
import java.lang.reflect.Type
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.ConcurrentMap

class FileIO<T>(plugin: Plugin, fileName: String, token: TypeToken<ConcurrentMap<String?, T?>?>?) {
    private val file: File
    private val gson: Gson
    private val type: Type

    init {
        file = File(plugin.dataFolder, "$fileName.json")
        gson = GsonBuilder().setPrettyPrinting().create()
        type = token?.type!!
    }

    fun load(): ConcurrentMap<String, T> {
        try {
            file.let {
                FileReader(it).use { reader ->
                    // 使用 Gson 从 JSON 文件中解析数据
                    return gson.fromJson(reader, type) ?: ConcurrentHashMap()
                }
            }
        } catch (e: IOException) {
            e.printStackTrace()
        }
        return ConcurrentHashMap()
    }

    fun save(nodeMap: ConcurrentMap<String, T>): Boolean {
        try {
            file.let {
                FileWriter(it).use { writer ->
                    gson.toJson(nodeMap)?.let { it1 -> writer.write(it1) }
                    writer.flush()
                }
            }
        } catch (e: IOException) {
            e.printStackTrace()
            return false
        }
        return true
    }
}
