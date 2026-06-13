ChestCommandsX
===================

这是 CadsMC 的 ChestCommands 的一个分支，已针对 Paper/Folia 26.1.2 和 Java 25 进行了现代化改造。

面向开发者的 API Javadoc: https://ci.codemc.io/job/filoghost/job/ChestCommands/javadoc/index.html?me/filoghost/chestcommands/api/ChestCommandsAPI.html

## 要求
- Paper、Folia 或基于 Minecraft 26.1.2 或更高版本的兼容 Bukkit 实现。

- Java 25 或更高版本。

- Vault 为可选组件，仅用于经济功能。

- PlaceholderAPI 为可选组件，仅用于外部占位符。

## Build
```powershell
gradle build
```

The plugin jar is created at `plugin/build/libs/chestcommands-plugin-5.1.6.jar`.

## Gradle
```groovy
repositories {
    maven { url = uri("https://repo.codemc.io/repository/maven-public/") }
}
```

```groovy
dependencies {
    compileOnly "me.filoghost.chestcommands:chestcommands-api:5.1.6"
}
```

## 配置说明

- 请使用现代材质名称，例如 `DIAMOND_SWORD`、`WHITE_WOOL` 和 `REDSTONE_LAMP`。

- `menu-settings.commands` 下的命令是动态注册的。请使用 `menu` 而不是 `/menu`，并避免使用已被其他插件使用的名称。支持子命令，例如 `shop 1`。

- 使用 `/cc sound <sound> [pitch] [volume]` 测试游戏中可用的 Minecraft 音效。sound 参数支持 Tab 键自动补全。

- 已声明支持 Folia。玩家物品栏/菜单操作由玩家实体调度器调度，而全局任务则使用 Folia 的全局调度器。Folia 已禁用旧版更新检查器和 bStats 调度器钩子。

- 使用 `DAMAGE` 表示物品损坏。不支持旧版材质数据值，例如 `WOOL:14`。

- 不再支持 `NBT-DATA`。使用受支持的元数据键，例如 `COLOR`、`SKULL-OWNER`、`ENCHANTMENTS`、`CUSTOM-MODEL-DATA`、`UNBREAKABLE` 和 `ITEM-FLAGS`。

- `boss-bar:` 使用 Bukkit 原生的 Boss 栏 API。旧的 `dragon-bar:` 前缀仍然可以作为别名使用。

- `POSITION-X` 和 `POSITION-Y` 支持诸如 `3-4` 之类的范围以及动态整数占位符。

- `MATERIAL: AIR` 可用于不可见的可点击物品栏，而 `DRAG: true` 允许物品栏接受正常的物品移动。

## 许可

Chest Commands 是自由软件/开源软件，并根据 [GPL 3.0 许可](https://opensource.org/licenses/GPL-3.0) 发布。它包含第三方代码，有关第三方代码的许可信息，请参阅随附的 THIRD-PARTY.txt 文件。