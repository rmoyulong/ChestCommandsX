# ChestCommandsX 5.1.5

基于 CadsMC 的 ChestCommands 分支。此版本为 Paper/Folia 26.1.2 添加了更灵活的菜单命令、动态槽位和交互式物品输入支持。

## 新增

- 添加了对 `menu-settings.commands` 中子命令的支持，例如 `shop 1` 和 `shop 2`。

- 为动态菜单子命令添加了 Tab 键自动补全功能。

- 添加了 `/cc sound <sound> [pitch] [volume]` 命令，用于在游戏内测试 Minecraft 音效。

- 为 `/cc sound` 命令添加了 Tab 键自动补全功能，用于输入音效名称。

- 添加了对 `POSITION-X` 和 `POSITION-Y` 的范围支持，例如 `3-4` 和 `1-9`。

- 添加了对 `MATERIAL: AIR` 图标的支持，以便空槽位也能执行操作。

- 添加了 `DRAG: true`，允许在选定的菜单栏位中正常移动项目。

- 添加了拖拽占位符：

- `%drag_item%`

- `%drag_item_amount%`

- `{drag_item}`

- `{drag_item_amount}`

- 添加了用于动态菜单值的整数占位符：

- `%integer:{name}%`

- `%integer_plus:{name}%`

- `%integer_minus:{name}%`

- `%integer_default:{name}%`

- `%integer_reset:{name}%`

- 添加了公共和私有整数作用域：

- `%public_integer:{name}%` 在玩家之间共享。

- `%private_integer:{name}%` 存储在每个打开的菜单视图中，并在菜单关闭或更改时重置。

- 添加了带有 `name=default` 的默认整数值，例如 `%private_integer:amount=1%` 和 `%public_integer:range_x=1-2%`。

## 更改

- 菜单刷新现在会在同一物品栏中重建动态配置的图标，而无需关闭并重新打开菜单。

- 动态整数占位符可用于 `AMOUNT`、`POSITION-X` 和 `POSITION-Y`。

- `POSITION-X`、`POSITION-Y` 和 `AMOUNT` 会将动态整数值限制在有效的菜单/物品限制范围内。

- 更新了默认的 `example.yml` 文件，其中包含子命令、AIR 操作、可拖动槽位、范围位置和整数控件的示例。

## 说明

- 公共整数值保存在内存中，并在插件/服务器重启时重置。

- 私有整数值会在玩家关闭菜单或切换到其他菜单时重置。