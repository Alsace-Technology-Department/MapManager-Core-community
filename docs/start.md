# MapManager-Core 插件安装与配置指南

MapManager-Core是一款简单易用的Minecraft服务器插件，提供高效、灵活的多世界管理功能。

## 安装MapManager

在安装前确保你已经安装了LuckPerms和Multiverse-core

1. **下载插件**: 此插件为付费订阅制，订阅失效后仍然可以使用，但无法享受插件更新。
   1. [阿尔萨斯工业爱发电](https://afdian.net/a/alsace)
   2. [阿尔萨斯工业Patreon](https://www.patreon.com/alsaceteam) （暂时只有中文版）
2. **安装插件**: 将下载的`MapManager-Core-{version}.jar`文件放置到您服务器的`plugins`目录下。
3. **启动服务器**: 如果服务器正在运行，需要重启服务器以加载插件。如果服务器未启动，则直接启动服务器。

## 地图权限组

地图权限分为三类：`admin`, `builder`, `visitor`

- admin
  - 地图管理员，可以为私人地图添加删除地图的成员，对地图的物理（方块更新）、爆炸保护、是否公开、名称、出生点进行管理
- builder
  - 建筑师，可以在私人地图进行建筑，拥有的权限在worldbase中配置
- visitor
  - 参观者，可以进入私人地图，但没有建筑权限

## 配置权限组

MapManager-Core在首次加载时会自动创建两个权限组：`worldbase`和`apply`。以下是如何配置这两个权限组的指南：

### worldbase 权限组

`worldbase`权限组为地图中的建筑师（builder）组提供必要的权限。这些权限在创建地图时会自动继承至对应地图的权限组。

我们推荐您为建筑师添加地图的建筑权限（例如essentials.build、buildcore.protect.*）、WorldEdit权限等

### apply 权限组

`apply`权限组为拥有私人地图权限的玩家提供全服的权限。创建地图时的权限组将继承`apply`。

您可以为`apply`权限组配置公共素材库的权限或是地图托管玩家作为会员的权限等。

此项可以不进行配置

1. **添加权限**: 类似于上方的命令，您只需要为apply组添加相应权限节点即可


## 配置文件

MapManager的配置文件`MapManagerConfig.yml`提供了一些全局设置选项：

```yaml
!MapManagerConfig #配置文件标记，请勿修改
global:
  exploded: null # 是否全局开启爆炸破坏。可选值：true, false, null
  physical: null # 是否全局开启物理效果。可选值：true, false, null
```

- `exploded`: 控制是否允许爆炸破坏效果。`null`值表示遵循各个世界的设置。
- `physical`: 控制是否允许物理效果（如方块掉落）。`null`值表示遵循各个世界的设置。

## 开发

### 依赖

Maven

```xml
<dependency>
    <groupId>work.alsace.mapmanager</groupId>
    <artifactId>MapManager-Core</artifactId>
    <version>3.0</version>
    <scope>provided</scope>
</dependency>
```

Gradle (kotlin)

```kts
compileOnly("work.alsace.mapmanager:MapManager-Core:3.0")
```

### JavaDoc

[JavaDoc](https://www.alsace.team/MapManager/javadoc/)

### 如何引入MapManager-Core

```java
MapManager mapManagerCore = ((MapManager) Objects
        .requireNonNull(Bukkit.getServer()
        .getPluginManager()
        .getPlugin("MapManager-Core"));
```
