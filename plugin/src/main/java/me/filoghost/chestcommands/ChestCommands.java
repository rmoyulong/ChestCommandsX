/*
 * Copyright (C) filoghost and contributors
 *
 * SPDX-License-Identifier: GPL-3.0-or-later
 */
package me.filoghost.chestcommands;

import me.filoghost.chestcommands.api.internal.BackendAPI;
import me.filoghost.chestcommands.command.CommandHandler;
import me.filoghost.chestcommands.config.ConfigManager;
import me.filoghost.chestcommands.config.CustomPlaceholders;
import me.filoghost.chestcommands.config.Settings;
import me.filoghost.chestcommands.hook.BungeeCordHook;
import me.filoghost.chestcommands.hook.PlaceholderAPIHook;
import me.filoghost.chestcommands.hook.VaultEconomyHook;
import me.filoghost.chestcommands.legacy.UpgradeExecutorException;
import me.filoghost.chestcommands.legacy.UpgradesExecutor;
import me.filoghost.chestcommands.listener.CommandListener;
import me.filoghost.chestcommands.listener.InventoryListener;
import me.filoghost.chestcommands.listener.JoinListener;
import me.filoghost.chestcommands.listener.SignListener;
import me.filoghost.chestcommands.logging.Errors;
import me.filoghost.chestcommands.logging.PrintableErrorCollector;
import me.filoghost.chestcommands.menu.MenuManager;
import me.filoghost.chestcommands.parsing.menu.LoadedMenu;
import me.filoghost.chestcommands.placeholder.PlaceholderManager;
import me.filoghost.chestcommands.task.TickingTask;
import me.filoghost.chestcommands.util.FoliaScheduler;
import me.filoghost.chestcommands.util.Text;
import me.filoghost.fcommons.EnhancedJavaPlugin;
import me.filoghost.fcommons.FCommons;
import me.filoghost.fcommons.config.ConfigLoader;
import me.filoghost.fcommons.logging.ErrorCollector;
import me.filoghost.fcommons.logging.Log;
import me.filoghost.updatechecker.UpdateChecker;
import org.bstats.bukkit.Metrics;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.plugin.Plugin;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.List;

public class ChestCommands extends EnhancedJavaPlugin {


    public static final String CHAT_PREFIX = ChatColor.DARK_GREEN + "[" + ChatColor.GREEN + "ChestCommandsX" + ChatColor.DARK_GREEN + "] " + ChatColor.GREEN;

    private static Plugin pluginInstance;
    private static Path dataFolderPath;

    private static ConfigManager configManager;
    private static CustomPlaceholders placeholders;

    private static ErrorCollector lastLoadErrors;
    private static String newVersion;

    @Override
    public void onEnable() {
        FCommons.setPluginInstance(this);

        try {
            onCheckedEnable();
        } catch (PluginEnableException e) {
            printCriticalError(e.getMessageLines(), e.getCause());
            setEnabled(false);
        } catch (Throwable t) {
            printCriticalError(null, t);
            setEnabled(false);
        }
    }

    protected void onCheckedEnable() throws PluginEnableException {
        if (pluginInstance != null || System.getProperty("ChestCommandsLoaded") != null) {
            throw new PluginEnableException("不支持外部插件重新加载:"
                    + " 避免使用 /重新加载或插件重新加载器，并使用以下命令 \"/cx reload\" 代替."
                    + " 完全重启服务器以重新启用 ChestCommandsX。");
        }

        System.setProperty("ChestCommandsLoaded", "true");

        pluginInstance = this;
        dataFolderPath = getDataFolder().toPath();
        configManager = new ConfigManager(getDataFolderPath());
        placeholders = new CustomPlaceholders();

        BackendAPI.setImplementation(new DefaultBackendAPI());

        VaultEconomyHook.INSTANCE.setup();
        PlaceholderAPIHook.INSTANCE.setup();
        BungeeCordHook.INSTANCE.setup();

        if (VaultEconomyHook.INSTANCE.isEnabled()) {
            Log.info("已关联 Vault");
        } else {
            Log.warning("找不到 Vault 和兼容的经济插件！与金钱相关的功能将无法使用。");
        }

        if (PlaceholderAPIHook.INSTANCE.isEnabled()) {
            Log.info("已关联 PlaceholderAPI");
        }

        Bukkit.getPluginManager().registerEvents(new CommandListener(), this);
        Bukkit.getPluginManager().registerEvents(new InventoryListener(), this);
        Bukkit.getPluginManager().registerEvents(new JoinListener(), this);
        Bukkit.getPluginManager().registerEvents(new SignListener(), this);

        CommandHandler commandHandler = new CommandHandler("chestcommands");
        commandHandler.register(this);
        getCommand("chestcommands").setTabCompleter(commandHandler);

        ErrorCollector errorCollector = load();

        if (errorCollector.hasErrors()) {
            errorCollector.logToConsole();
            FoliaScheduler.runGlobalLater(() -> {
                Text.send(Bukkit.getConsoleSender(),
                        ChestCommands.CHAT_PREFIX + ChatColor.RED + "Encountered " + errorCollector.getErrorsCount() + " error(s) on load. "
                        + "查看之前的控制台日志或运行 \"/chestcommandsx errors\" 希望能再次发现它们。");
            }, 10L);
        }

        if (Settings.get().update_notifications && !FoliaScheduler.isFolia()) {
            UpdateChecker.run(this, 56919, (String newVersion) -> {
                ChestCommands.newVersion = newVersion;

                Log.info("找到新版本: " + newVersion + " (你的: v" + getDescription().getVersion() + ")");
                Log.info("从 Bukkit Dev 下载更新:");
                Log.info("https://dev.bukkit.org/projects/chest-commands");
            });
        } else if (Settings.get().update_notifications) {
            Log.info("Folia 上的更新检查功能已禁用。");
        }

        if (!FoliaScheduler.isFolia()) {
            // Start bStats metrics
            int pluginID = 3658;
            new Metrics(this, pluginID);
        }

        FoliaScheduler.runGlobalTimer(new TickingTask(), 1L, 1L);
    }

    @Override
    public void onDisable() {
        MenuManager.closeAllOpenMenuViews();
        FoliaScheduler.cancelTasks();
    }

    public static ErrorCollector load() {
        ErrorCollector errorCollector = new PrintableErrorCollector();
        MenuManager.reset();
        boolean isFreshInstall = !Files.isDirectory(configManager.getRootDataFolder());
        try {
            Files.createDirectories(configManager.getRootDataFolder());
        } catch (IOException e) {
            errorCollector.add(e, Errors.Config.createDataFolderIOException);
            return errorCollector;
        }
        
        try {
            UpgradesExecutor upgradeExecutor = new UpgradesExecutor(configManager);
            boolean allUpgradesSuccessful = upgradeExecutor.run(isFreshInstall, errorCollector);
            if (!allUpgradesSuccessful) {
                errorCollector.add(Errors.Upgrade.failedSomeUpgrades);
            }
        } catch (UpgradeExecutorException e) {
            errorCollector.add(e, Errors.Upgrade.genericExecutorError);
            errorCollector.add(Errors.Upgrade.failedSomeUpgrades);
        }

        configManager.tryLoadSettings(errorCollector);
        configManager.tryLoadLang(errorCollector);
        placeholders = configManager.tryLoadCustomPlaceholders(errorCollector);
        PlaceholderManager.setStaticPlaceholders(placeholders.getPlaceholders());

        // Create the menu folder with the example menu
        if (!Files.isDirectory(configManager.getMenusFolder())) {
            ConfigLoader exampleMenuLoader = configManager.getConfigLoader(configManager.getMenusFolder().resolve("example.yml"));
            configManager.tryCreateDefault(errorCollector, exampleMenuLoader);
        }

        List<LoadedMenu> loadedMenus = configManager.tryLoadMenus(errorCollector);
        for (LoadedMenu loadedMenu : loadedMenus) {
            MenuManager.registerMenu(loadedMenu, errorCollector);
        }

        ChestCommands.lastLoadErrors = errorCollector;
        return errorCollector;
    }


    public static Plugin getInstance() {
        return pluginInstance;
    }

    public static Path getDataFolderPath() {
        return dataFolderPath;
    }

    public static boolean hasNewVersion() {
        return newVersion != null;
    }

    public static String getNewVersion() {
        return newVersion;
    }

    public static ErrorCollector getLastLoadErrors() {
        return lastLoadErrors;
    }

    private void printCriticalError(List<String> messageLines, Throwable throwable) {
        if (messageLines != null && !messageLines.isEmpty()) {
            getLogger().severe("启用插件时发生致命错误:");
            for (String line : messageLines) {
                getLogger().severe(line);
            }
        } else {
            getLogger().severe("启用插件时发生致命意外错误:");
        }

        if (throwable != null) {
            getLogger().log(java.util.logging.Level.SEVERE, "插件启用失败。", throwable);
        }

        getLogger().severe("ChestCommandsX 已被禁用。");
    }

    protected static class PluginEnableException extends Exception {

        private final List<String> messageLines;

        public PluginEnableException(String... messageLines) {
            this.messageLines = Arrays.asList(messageLines);
        }

        public List<String> getMessageLines() {
            return messageLines;
        }
    }

}
