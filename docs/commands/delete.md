# delete 命令

`/delete`命令用于删除管理下的世界。

### 命令格式

删除当前所在的世界（需要玩家执行）：

```
/delete [world]
```

确认删除操作：

```
/delete confirm
```

### 参数说明

在第一次使用`/delete`命令后，若未指定世界名，则默认为当前所在的世界。使用`/delete confirm`来确认并完成删除操作。

### 使用示例

若玩家当前位于一个名为`AdventureWorld`的世界中，想要删除这个世界，需要执行以下步骤：

1. `/delete` - 首次执行，将会提示确认消息。
2. `/delete confirm` - 在10秒内执行此命令，将确认并执行删除操作。

若玩家想删除一个名为`CreateWorld`的世界，需要执行以下步骤：

1. `/delete CreateWorld` - 首次执行，将会提示确认消息。
2. `/delete confirm` - 在10秒内执行此命令，将确认并执行删除操作。

### 权限

- `mapmanager.command.delete` - 允许玩家使用`/delete`命令。

### 注意事项

- 执行删除操作是不可逆的，请在确认无误后进行。
