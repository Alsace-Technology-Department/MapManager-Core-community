# import 命令

`/import`命令允许玩家导入地图到服务器并由MapManager进行管理。

### 命令格式

`/import n:<name> a:[alias] c:[color] g:[group] o:[owner]`

### 参数说明

- `n:<name>` **（必需）** - 要导入的世界的名称，需与文件名相符。
- `a:<alias>` - 导入世界的别名。如果未指定，将使用世界的名称作为别名。
- `c:<color>` - 导入世界名称的颜色。支持Minecraft颜色代码。如果未指定，默认为 `darkaqua`。
- `g:<group>` - 导入世界所属的权限组。如果未指定，将使用世界的名称作为权限组。
- `o:<owner>` - 导入世界的所有者。如果指定，此玩家将被赋予管理该世界的权限。注意，离线服务器需要玩家在服务器注册后才能正常添加权限。

### 使用示例

**请先将要导入的地图放在服务端主目录下，确保其中含有`level.dat`文件**

导入一个名为 `AdventureWorld` 的世界，并设置别名为 `Adventure`：

`/import n:AdventureWorld a:Adventure`

导入一个名为 `CreativeZone` 的世界，不指定别名，设置颜色为 `light_purple`：

`/import n:CreativeZone c:light_purple`

### 权限

- `mapmanager.command.import` - 允许玩家使用 `/import` 命令。

### 注意事项

- 导入的世界需要已经存在于服务器的主目录下，MapManager将会将其纳入管理而不是创建一个新的副本。
