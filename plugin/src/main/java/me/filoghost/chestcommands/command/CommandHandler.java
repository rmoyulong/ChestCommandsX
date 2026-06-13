/*
 * Copyright (C) filoghost and contributors
 *
 * SPDX-License-Identifier: GPL-3.0-or-later
 */
package me.filoghost.chestcommands.command;

import me.filoghost.chestcommands.ChestCommands;
import me.filoghost.chestcommands.Permissions;
import me.filoghost.chestcommands.action.PlaySoundAction;
import me.filoghost.chestcommands.menu.InternalMenu;
import me.filoghost.chestcommands.menu.MenuManager;
import me.filoghost.chestcommands.parsing.ParseException;
import me.filoghost.chestcommands.util.FoliaScheduler;
import me.filoghost.chestcommands.util.Text;
import me.filoghost.chestcommands.util.Utils;
import me.filoghost.fcommons.collection.CaseInsensitiveString;
import me.filoghost.fcommons.command.CommandContext;
import me.filoghost.fcommons.command.sub.SubCommandContext;
import me.filoghost.fcommons.command.sub.annotated.AnnotatedSubCommand;
import me.filoghost.fcommons.command.sub.annotated.AnnotatedSubCommandManager;
import me.filoghost.fcommons.command.sub.annotated.Description;
import me.filoghost.fcommons.command.sub.annotated.DisplayPriority;
import me.filoghost.fcommons.command.sub.annotated.MinArgs;
import me.filoghost.fcommons.command.sub.annotated.Name;
import me.filoghost.fcommons.command.sub.annotated.Permission;
import me.filoghost.fcommons.command.sub.annotated.UsageArgs;
import me.filoghost.fcommons.command.validation.CommandException;
import me.filoghost.fcommons.command.validation.CommandValidate;
import me.filoghost.fcommons.logging.ErrorCollector;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collections;
import java.util.List;

public class CommandHandler extends AnnotatedSubCommandManager implements TabCompleter {

    public CommandHandler(String label) {
        setName(label);
    }

    @Override
    protected String getDefaultSubCommandPermission(AnnotatedSubCommand subCommand) {
        return Permissions.COMMAND_PREFIX + "." + subCommand.getName();
    }

    @Override
    protected void sendNoArgsMessage(CommandContext context) {
        CommandSender sender = context.getSender();
        Text.send(sender, ChestCommands.CHAT_PREFIX);
        Text.send(sender, ChatColor.GREEN + "版本: " + ChatColor.GRAY + ChestCommands.getInstance().getDescription().getVersion());
        Text.send(sender, ChatColor.GREEN + "开发者: " + ChatColor.GRAY + "filoghost");
        Text.send(sender, ChatColor.GREEN + "命令: " + ChatColor.GRAY + "/" + context.getRootLabel() + " help");
    }
    
    @Override
    protected void sendUnknownSubCommandMessage(SubCommandContext context) {
        Text.send(context.getSender(), ChatColor.RED + "未知的子命令 \"" + context.getSubLabel() + "\". "
                + "使用 \"/" + context.getRootLabel() + " help\" 查看可用命令。");
    }

    @Name("help")
	@Description("帮助")
    @Permission(Permissions.COMMAND_PREFIX + "help")
    public void help(CommandSender sender, SubCommandContext context) {
        Text.send(sender, ChestCommands.CHAT_PREFIX + "命令:");
        for (AnnotatedSubCommand subCommand : getSubCommands()) {
            if (subCommand == context.getSubCommand()) {
                continue;
            }
            String usageText = getUsageText(context, subCommand);
            Text.send(sender, ChatColor.WHITE + usageText + ChatColor.GRAY + " - " + subCommand.getDescription());
        }
    }

    @Name("reload")
    @Description("重新加载插件。")
    @Permission(Permissions.COMMAND_PREFIX + "reload")
    @DisplayPriority(4)
    public void reload(CommandSender sender) {
        MenuManager.closeAllOpenMenuViews();

        ErrorCollector errorCollector = ChestCommands.load();

        if (!errorCollector.hasErrors()) {
            Text.send(sender, ChestCommands.CHAT_PREFIX + "插件已重新加载。");
        } else {
            errorCollector.logToConsole();
            Text.send(sender, ChestCommands.CHAT_PREFIX + ChatColor.RED + "插件已重新加载 " + errorCollector.getErrorsCount() + " 错误。");
            if (!(sender instanceof ConsoleCommandSender)) {
                Text.send(sender, ChestCommands.CHAT_PREFIX + ChatColor.RED + "请检查控制台。");
            }
        }
    }

    @Name("errors")
    @Description("在控制台上显示最近的加载错误。")
    @Permission(Permissions.COMMAND_PREFIX + "errors")
    @DisplayPriority(3)
    public void errors(CommandSender sender) {
        ErrorCollector errorCollector = ChestCommands.getLastLoadErrors();

        if (errorCollector.hasErrors()) {
            errorCollector.logToConsole();
            Text.send(sender, ChestCommands.CHAT_PREFIX + ChatColor.RED + "上次插件加载时间, 发生："
                    + errorCollector.getErrorsCount() + " 个错误。");
            if (!(sender instanceof ConsoleCommandSender)) {
                Text.send(sender, ChestCommands.CHAT_PREFIX + ChatColor.RED + "控制台上显示了错误信息。");
            }
        } else {
            Text.send(sender, ChestCommands.CHAT_PREFIX + ChatColor.GREEN + "上次插件加载成功，未记录任何错误。");
        }
    }

    @Name("list")
    @Description("列出已加载的菜单。")
    @Permission(Permissions.COMMAND_PREFIX + "list")
    @DisplayPriority(2)
    public void list(CommandSender sender) {
        Text.send(sender, ChestCommands.CHAT_PREFIX + "已加载菜单:");
        for (CaseInsensitiveString name : MenuManager.getMenuFileNames()) {
            Text.send(sender, ChatColor.GRAY + "- " + ChatColor.WHITE + name);
        }
    }

    @Name("open")
    @Description("打开玩家菜单。")
    @Permission(Permissions.COMMAND_PREFIX + "open")
    @MinArgs(1)
    @UsageArgs("<菜单名> [玩家ID]")
    @DisplayPriority(1)
    @SuppressWarnings("弃用")
    public void open(CommandSender sender, String[] args) throws CommandException {
        Player target;

        if (sender instanceof Player) {
            if (args.length > 1) {
                CommandValidate.check(sender.hasPermission(Permissions.COMMAND_PREFIX + "open.others"),
                        "您没有权限为其他玩家打开菜单。");
                target = Bukkit.getPlayerExact(args[1]);
            } else {
                target = (Player) sender;
            }
        } else {
            CommandValidate.minLength(args, 2, "您必须通过控制台指定玩家。");
            target = Bukkit.getPlayerExact(args[1]);
        }

        CommandValidate.notNull(target, "该玩家未在线。");

        String menuName = Utils.addYamlExtension(args[0]);
        InternalMenu menu = MenuManager.getMenuByFileName(menuName);
        CommandValidate.notNull(menu, "菜单 \"" + menuName + "\" 未找到。");

        if (!sender.hasPermission(menu.getOpenPermission())) {
            menu.sendNoOpenPermissionMessage(sender);
            return;
        }

        if (sender.getName().equalsIgnoreCase(target.getName())) {
            Text.send(sender, ChatColor.GREEN + "打开菜单 " + menuName + ".");
        } else {
            Text.send(sender, ChatColor.GREEN + "打开菜单 " + menuName + " 给玩家： " + target.getName() + ".");
        }

        FoliaScheduler.runAtPlayer(target, () -> menu.open(target));
    }

    @Name("sound")
    @Description("播放声音进行测试。")
    @Permission(Permissions.COMMAND_PREFIX + "sound")
    @MinArgs(1)
    @UsageArgs("<sound> [音调] [音量]")
    @DisplayPriority(0)
    public void sound(CommandSender sender, String[] args) throws CommandException {
        Player player = CommandValidate.getPlayerSender(sender);

        String serializedSound = args[0];
        if (args.length > 1) {
            serializedSound += ", " + args[1];
        }
        if (args.length > 2) {
            serializedSound += ", " + args[2];
        }

        PlaySoundAction action;
        try {
            action = new PlaySoundAction(serializedSound);
        } catch (ParseException e) {
            throw new CommandException(e.getMessage());
        }

        FoliaScheduler.runAtPlayer(player, () -> action.execute(player));
        Text.send(sender, ChatColor.GREEN + "播放声音 " + ChatColor.WHITE + args[0] + ChatColor.GREEN + ".");
    }

    @Override
    public @Nullable List<String> onTabComplete(
            @NotNull CommandSender sender,
            @NotNull Command command,
            @NotNull String alias,
            @NotNull String[] args) {

        if (args.length == 2 && args[0].equalsIgnoreCase("sound") && sender.hasPermission(Permissions.COMMAND_PREFIX + "sound")) {
            return PlaySoundAction.getSoundNamesStartingWith(args[1]);
        }

        if (args.length == 3 && args[0].equalsIgnoreCase("sound") && sender.hasPermission(Permissions.COMMAND_PREFIX + "sound")) {
            return List.of("1.0");
        }

        if (args.length == 4 && args[0].equalsIgnoreCase("sound") && sender.hasPermission(Permissions.COMMAND_PREFIX + "sound")) {
            return List.of("1.0");
        }

        return Collections.emptyList();
    }

}