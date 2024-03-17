package work.alsace.mapmanager.function

import org.bukkit.plugin.Plugin
import org.yaml.snakeyaml.DumperOptions
import org.yaml.snakeyaml.TypeDescription
import org.yaml.snakeyaml.Yaml
import org.yaml.snakeyaml.constructor.Constructor
import org.yaml.snakeyaml.constructor.CustomClassLoaderConstructor
import org.yaml.snakeyaml.nodes.Tag
import org.yaml.snakeyaml.representer.Representer
import work.alsace.mapmanager.data.MainConfig
import java.io.File
import java.io.FileReader
import java.io.FileWriter
import java.io.IOException

class MainYaml(plugin: Plugin?) {
    private val yaml: Yaml?
    private val file: File?

    init {
        val options = DumperOptions()
        options.defaultFlowStyle = DumperOptions.FlowStyle.BLOCK
        options.isPrettyFlow = true
        val constructor: Constructor = CustomClassLoaderConstructor(MainYaml::class.java.classLoader)
        val representer = Representer(options)
        val description = TypeDescription(MainConfig::class.java, Tag("!MapManagerConfig"))
        representer.addTypeDescription(description)
        constructor.addTypeDescription(description)
        yaml = Yaml(constructor, representer, options)
        file = File(plugin?.dataFolder, "config.yml")
    }

    fun load(): MainConfig? {
        if (!file?.exists()!!) {
            try {
                file.createNewFile()
            } catch (e: IOException) {
                e.printStackTrace()
                return MainConfig()
            }
            val tmp = MainConfig()
            save(tmp)
            return tmp
        }
        try {
            FileReader(file).use { reader -> return yaml?.loadAs(reader, MainConfig::class.java) }
        } catch (e: IOException) {
            e.printStackTrace()
            return MainConfig()
        }
    }

    fun save(config: MainConfig?) {
        try {
            file?.let { FileWriter(it).use { writer -> yaml?.dump(config, writer) } }
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }
}
