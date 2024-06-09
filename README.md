# MapManager-Core

MapManager-Core is a simple and easy-to-use Minecraft server plugin that offers efficient and flexible multi-world management capabilities.

## Installing MapManager

Make sure you have LuckPerms and Multiverse-core installed before installation

1. **Download the Plugin**
2. **Install the Plugin**: Place the downloaded `MapManager-Core-{version}.jar` file into the `plugins` directory of your server.
3. **Start the Server**: If the server is running, it needs to be restarted to load the plugin. If the server is not started, simply start the server.

## Map Permission Groups

Map permissions are divided into three categories: `admin`, `builder`, `visitor`.

- **admin**
  - Map administrators, who can add or remove members from private maps, manage physical (block updates), explosion protection, public status, name, and spawn point of the map.
- **builder**
  - Builders, who can build on private maps. The permissions available to builders are configured in `worldbase`.
- **visitor**
  - Visitors, who can enter private maps but do not have building permissions.

## Configuring Permission Groups

MapManager-Core automatically creates two permission groups upon first load: `worldbase` and `apply`. Here is a guide on how to configure these two permission groups:

### worldbase Permission Group

The `worldbase` permission group provides necessary permissions for the builders (builder group) in the map. These permissions are automatically inherited by the corresponding map's permission group upon creation.

We recommend adding building permissions for the map to builders (e.g., essentials.build, buildcore.protect.*), WorldEdit permissions, etc.

### apply Permission Group

The `apply` permission group provides server-wide permissions for players with private map permissions. The permission group created at the time of map creation will inherit `apply`.

You can configure permissions for the public material library or the map hosting players as members, etc., for the `apply` permission group.

This step is optional.

1. **Adding Permissions**: Similar to the commands above, you only need to add the respective permission nodes to the apply group.

## Configuration File

MapManager's configuration file `MapManagerConfig.yml` offers some global settings options:

```yaml
!MapManagerConfig #Configuration file marker, do not modify
global:
  exploded: null # Whether to globally enable explosion damage. Options: true, false, null
  physical: null # Whether to globally enable physical effects. Options: true, false, null
```


exploded: Controls whether explosion damage effects are allowed. A null value indicates adherence to each world's settings.
physical: Controls whether physical effects (such as block dropping) are allowed. A null value indicates adherence to each world's settings.

## Commands

Here are all commands which MapManager registered.

* /world admins

  List all admins in a world.

* /world admin \<add|remove\> \<id\>

  Add an admin to or remove an admin from a world.

* /world builders

  List all builders in a world.

* /world builder \<add|remove\> \<id\>

  Add a builder to or remove a builder from a world.

* /world visitors

  List all visitors in a world.

* /world visitor \<add|remove\> \<id\>

  Add a visitor to or remove a visitor from a world.

  Tip: Fill the \<id\> with "*" in order to make your world public.

* /world physics \[info|true|false\]

  * info or null - Show whether the physics turned on in the world.
  * true - Turn on the physics
  * false - Turn off the physics

* /world explosion \[info|true|false\]

  * info or null - Show whether the world has explosion.
  * true - Turn on the explosion.
  * false - Turn off the explosion.

* /world pvp \[info|true|false\]

  * info or null - Show whether the world can pvp.
  * true - Turn on the pvp.
  * false - Turn off the pvp.

* /world kick <id>

  Kick a player out of your world.

* /world setname <name>

  Set the name of the world.

* /world setspawn

  Set the spawn location.

* /world reload

  Permission "mapmanager.administrator" is required.

  Reload the config.

* /import n:\<name\> a:\[alias\] c:\[color\] p:\<permission group\> o:\<owner\>

  Permission "mapmanager.command.import" is required.

  Import and initialize a world.

* /delete \[world\]

  Permission "mapmanager.command.delete" is required.

  Delete a world.

* /write

  Permission "worldmanager.command.write" is required.

  Print details of all worlds.

## Development

### Dependencies

Maven

```xml
 <repository>
     <id>sonatype</id>
     <url>https://oss.sonatype.org/content/groups/public/</url>
 </repository>
<dependency>
    <groupId>work.alsace.mapmanager</groupId>
    <artifactId>MapManager-Core</artifactId>
    <version>3.0</version>
    <scope>provided</scope>
</dependency>
```

Gradle (kotlin)

```kts
maven(url = "https://oss.sonatype.org/content/groups/public/")
compileOnly("work.alsace.mapmanager:MapManager-Core:3.0")
```

### JavaDoc

[JavaDoc](https://www.alsace.team/MapManager/javadoc/)
### How to Integrate MapManager-Core
```java
MapManager mapManagerCore = ((MapManager) Objects
        .requireNonNull(Bukkit.getServer()
        .getPluginManager()
        .getPlugin("MapManager-Core"));
```

[Documentatiton](https://alsaceteam.feishu.cn/wiki/KFLewAQZiiHhRFkXhRQcaQmMn2c)
