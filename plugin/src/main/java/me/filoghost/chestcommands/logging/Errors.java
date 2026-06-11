/*
 * Copyright (C) filoghost and contributors
 *
 * SPDX-License-Identifier: GPL-3.0-or-later
 */
package me.filoghost.chestcommands.logging;

import me.filoghost.chestcommands.ChestCommands;
import me.filoghost.chestcommands.parsing.icon.AttributeType;
import me.filoghost.chestcommands.parsing.icon.IconSettings;
import me.filoghost.fcommons.config.ConfigErrors;
import me.filoghost.fcommons.config.ConfigPath;
import org.bukkit.ChatColor;

import java.nio.file.Path;

public class Errors {

    public static class Config {

        public static final String createDataFolderIOException = "plugin failed to load, couldn't create data folder";

        public static String menuListIOException(Path menuFolder) {
            return "无法获取文件夹中的菜单文件 \"" + menuFolder + "\"";
        }

        public static String initException(Path file) {
            return "初始化配置文件时出错 \"" + formatPath(file) + "\"";
        }

        public static String emptyPlaceholder(Path configFile) {
            return "错误 \"" + configFile + "\": 占位符不能为空（不能跳过）。";
        }

        public static String tooLongPlaceholder(Path configFile, String placeholder) {
            return "错误 \"" + configFile + "\": 占位符长度不能超过 100 个字符 (" + placeholder + ").";
        }

    }


    public static class Upgrade {

        public static final String genericExecutorError = "自动配置升级过程中出错";
        public static final String menuListIOException = "无法获取菜单文件列表";
        public static final String failedSomeUpgrades =
                "注意：可能未应用一项或多项自动升级。 "
                + "配置文件或菜单可能需要手动更改";
        public static final String failedToPrepareUpgradeTasks = "尝试准备自动配置升级时出错";

        public static String metadataReadError(Path metadataFile) {
            return "无法读取升级元数据文件 \"" + formatPath(metadataFile) + "\"";
        }

        public static String metadataSaveError(Path metadataFile) {
            return "无法保存升级元数据文件 \"" + formatPath(metadataFile) + "\"";
        }

        public static String failedSingleUpgrade(Path file) {
            return "自动升级时出错 \"" + formatPath(file) + "\"";
        }

        public static String loadError(Path file) {
            return "无法加载升级文件 \"" + formatPath(file) + "\"";
        }

        public static String backupError(Path file) {
            return "无法创建文件备份 \"" + formatPath(file) + "\"";
        }

        public static String saveError(Path file) {
            return "无法保存升级后的文件 \"" + formatPath(file) + "\"";
        }

    }


    public static class Parsing {

        public static final String invalidDecimal = "该值不是有效的十进制数。";
        public static final String invalidShort = "该值不是有效的短整型";
        public static final String invalidInteger = "值不是有效的整数";

        public static final String strictlyPositive = "值必须大于零";
        public static final String zeroOrPositive = "该值必须大于或等于零。";

        public static final String invalidColorFormat = "值必须符合格式 \"red, green, blue\"";
        public static final String invalidPatternFormat = "值必须符合格式 \"pattern:color\"";

        public static final String unknownAttribute = "未知属性";
        public static final String materialCannotBeAir = "材料不能是空气";

        public static String unknownMaterial(String materialString) {
            return "未知物质 \"" + materialString + "\"";
        }

        public static String unknownPatternType(String patternTypeString) {
            return "未知模式类型 \"" + patternTypeString + "\"";
        }

        public static String unknownDyeColor(String dyeColorString) {
            return "未知染料颜色 \"" + dyeColorString + "\"";
        }

        public static String unknownEnchantmentType(String typeString) {
            return "未知附魔类型 \"" + typeString + "\"";
        }

        public static String invalidEnchantmentLevel(String levelString) {
            return "无效的附魔等级 \"" + levelString + "\",";
        }

        public static String invalidDurability(String durabilityString) {
            return "无效耐久性 \"" + durabilityString + "\"";
        }

        public static String invalidAmount(String amountString) {
            return "无效金额 \"" + amountString + "\"";
        }

        public static String invalidColorNumber(String numberString, String colorName) {
            return "无效 " + colorName + " 颜色 \"" + numberString + "\"";
        }

        public static String invalidColorRange(String valueString, String colorName) {
            return "无效 " + colorName + " 颜色 \"" + valueString + "\", value must be between 0 and 255";
        }

        public static String invalidBossBarTime(String timeString) {
            return "无效的末影龙状态条时间 \"" + timeString + "\"";
        }

        public static String invalidSoundPitch(String pitchString) {
            return "无效音调 \"" + pitchString + "\"";
        }

        public static String invalidSoundVolume(String volumeString) {
            return "无效音量 \"" + volumeString + "\"";
        }

        public static String unknownSound(String soundString) {
            return "未知的声音 \"" + soundString + "\"";
        }

    }

    public static class Menu {

        public static String invalidSetting(Path menuFile, ConfigPath invalidSetting) {
            return menuError(menuFile, "菜单设置无效 \"" + invalidSetting + "\"");
        }

        public static String missingSetting(Path menuFile, ConfigPath missingSetting) {
            return menuError(menuFile, "缺少菜单设置 \"" + missingSetting + "\"");
        }

        public static String missingSettingsSection(Path menuFile) {
            return menuError(menuFile, "缺少菜单设置部分");
        }

        public static String invalidSettingListElement(Path menuFile, ConfigPath invalidSetting, String listElement) {
            return menuError(menuFile,
                    "包含无效的列表元素 (\"" + listElement + "\") "
                            + "在菜单设置中 \"" + invalidSetting + "\"");
        }

        private static String menuError(Path menuFile, String errorMessage) {
            return "菜单 \"" + formatPath(menuFile) + "\" " + errorMessage;
        }

        public static String invalidAttribute(IconSettings iconSettings, AttributeType attributeType) {
            return invalidAttribute(iconSettings, attributeType.getConfigKey());
        }

        public static String invalidAttribute(IconSettings iconSettings, ConfigPath attributeConfigKey) {
            return iconError(iconSettings, "具有无效属性 \"" + attributeConfigKey + "\"");
        }

        public static String missingAttribute(IconSettings iconSettings, AttributeType attributeType) {
            return iconError(iconSettings, "缺少该属性 \"" + attributeType.getConfigKey() + "\"");
        }

        public static String invalidAttributeListElement(IconSettings iconSettings, ConfigPath attributeConfigKey, String listElement) {
            return iconError(iconSettings,
                    "包含无效的列表元素 (\"" + listElement + "\") "
                    + "在属性中 \"" + attributeConfigKey + "\"");
        }

        public static String iconOverridesAnother(IconSettings iconSettings) {
            return iconError(iconSettings, "正在覆盖位置相同的另一个图标");
        }

        private static String iconError(IconSettings iconSettings, String errorMessage) {
            return "the icon \"" + iconSettings.getConfigPath() + "\" in the menu \""
                    + formatPath(iconSettings.getMenuFile()) + "\" " + errorMessage;
        }

        public static String duplicateMenuName(Path menuFile1, Path menuFile2) {
            return "两个菜单 (\"" + menuFile1 + "\" 和 \"" + menuFile2 + "\") "
                    + "它们的文件名相同。只有其中一个可以通过文件名引用。";
        }

        public static String duplicateMenuCommand(Path menuFile1, Path menuFile2, String command) {
            return "两个菜单 (\"" + menuFile1 + "\" 和 \"" + menuFile2 + "\") "
                    + "具有相同的命令 \"" + command + "\". 执行该命令时，只会打开一个窗口。";
        }
    }

    public static class User {

        public static final String notifyStaffRequest = "请通知工作人员。";

        public static String configurationError(String errorMessage) {
            return ChatColor.RED + "错误: " + errorMessage + ". " + Errors.User.notifyStaffRequest;
        }

    }

    public static String formatPath(Path path) {
        return ConfigErrors.formatPath(ChestCommands.getDataFolderPath(), path);
    }

}
