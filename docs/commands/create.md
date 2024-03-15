# create 命令

`/create`命令允许玩家创建一个新的世界。

### 命令格式

`/create n:<name> e:[environment] a:[alias] c:[color] g:[group] o:[owner]`

### 参数说明

- `n:<name>` **（必需）** - 世界的名称。
- `e:<environment>` - 世界的环境类型。可选值包括 `normal`, `nether`, `the_end`, `flat`, `voidgen`[需要安装VoidGen](https://www.spigotmc.org/resources/voidgen.25391/)
  。如果未指定，默认为 `flat`。
- `a:<alias>` - 世界的别名。如果未指定，将使用世界的名称作为别名。
- `c:<color>` - 世界名称的颜色。支持Minecraft颜色代码。如果未指定，默认为 `darkaqua`。
- `g:<group>` - 世界所属的权限组。如果未指定，将使用世界的名称作为权限组。
- `o:<owner>` - 世界的所有者。如果指定，此玩家将被赋予管理该世界的权限。注意，离线服务器需要玩家在服务器注册后才能正常添加权限。

### 使用示例

创建一个名为 `MyWorld` 的标准环境世界：

`/create n:MyWorld`

创建一个名为 `SkyLand`，类型为 `voidgen`，带有别名 `Sky` 的世界：

`/create n:SkyLand e:voidgen a:Sky

### 权限

- `mapmanager.command.create` - 允许玩家使用 `/create` 命令。




