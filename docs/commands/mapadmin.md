# mapadmin 命令

`/mapadmin`命令提供了一个用于地图管理和配置的高级工具集。此命令允许执行操作如重载配置、同步权限、全局设置等。仅拥有相应权限的管理员可以使用此命令。

### 命令格式与子命令

命令格式遵循 `/mapadmin <子命令> [参数]` 的结构，其中 `<子命令>` 可以是以下几种：

- `reload` - 重载MapManager插件的配置。
- `physics` - 全局开启或关闭物理效果。
- `explosion` - 全局开启或关闭爆炸破坏。
- `sync` - 与LuckPerms插件同步权限。
- `save` - 保存当前的设置和配置到文件。

### 参数说明

- `[参数]` 取决于所使用的子命令。例如，对于 `physics on` 或 `explosion off`，`on/off` 是必需的参数，用于开启或关闭相应的全局设置。

### 使用示例

- 重载插件配置：`/mapadmin reload`
- 开启全局物理效果：`/mapadmin physics on`
- 关闭全局爆炸破坏：`/mapadmin explosion off`
- 同步LuckPerms权限：`/mapadmin sync`
- 保存当前配置：`/mapadmin save`

### 权限

- `mapmanager.command.mapadmin` - 允许玩家使用`/mapadmin`命令。

### 注意事项

- 执行全局设置改变（如物理和爆炸破坏）可能会影响服务器上所有地图的游戏体验，不建议全局修改
