# Keep the main class
-keep public class work.alsace.mapmanager.MapManager {
    public void onEnable();
	public void onDisable();
}

# Keep the public API
-keep interface work.alsace.mapmanager.IMapManager { *; }
-keep interface work.alsace.mapmanager.service.IDynamicWorld { *; }
-keep interface work.alsace.mapmanager.service.IMainYaml { *; }
-keep interface work.alsace.mapmanager.IMapAgent { *; }
-keep class work.alsace.mapmanager.pojo.** { *; }
-keepclassmembers class work.alsace.mapmanager.pojo.**

# Keep event handlers
-keep,allowobfuscation class * extends org.bukkit.event.Listener {
    @org.bukkit.event.EventHandler <methods>;
}

# If your goal is obfuscating and making things harder to read, repackage your classes with this rule
-repackageclasses 'work.alsace.mapmanager'

# Some attributes that you'll need to keep (to be honest I'm not sure which ones really need to be kept here, but this is what works for me)
-keepattributes !LocalVariableTable,!LocalVariableTypeTable,Exceptions,InnerClasses,Signature,Deprecated,LineNumberTable,*Annotation*,EnclosingMethod

-keep class java.** { *; }
-keep class javax.** { *; }
-keep class net.kyori.adventure.** { *; }
-keep class org.apache.logging.log4j.** { *; }
-keep class com.fasterxml.jackson.** { *; }
-keep class com.github.Querz.nbt.** { *; }
-keep class com.onarandombox.MultiverseCore.** { *; }
-keep class net.luckperms.api.** { *; }
-keep class com.destroystokyo.paper.** { *; }
-keep class io.papermc.paper.** { *; }
-keep class org.yaml.snakeyaml.** { *; }
-keep class com.google.common.** { *; }
-keep class org.bukkit.** { *; }
-keep class com.google.gson.** { *; }
-keep class com.lmax.** { *; }
