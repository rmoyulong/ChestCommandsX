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
        sender.sendMessage(ChestCommands.CHAT_PREFIX);
        sender.sendMessage(ChatColor.GREEN + "版本: " + ChatColor.GRAY + ChestCommands.getInstance().getDescription().getVersion());
        sender.sendMessage(ChatColor.GREEN + "开发者: " + ChatColor.GRAY + "filoghost");
        sender.sendMessage(ChatColor.GREEN + "命令: " + ChatColor.GRAY + "/" + context.getRootLabel() + " help");
    }
	
    @Override
    protected void sendUnknownSubCommandMessage(SubCommandContext context) {
        context.getSender().sendMessage(ChatColor.RED + "未知子命令 \"" + context.getSubLabel() + "\". "
                + "使用 \"/" + context.getRootLabel() + " help\" 查看可用命令。");
    }	

    @Name("help")
    @Permission(Permissions.COMMAND_PREFIX + "help")
    public void help(CommandSender sender, SubCommandContext context) {
        sender.sendMessage(ChestCommands.CHAT_PREFIX + "命令:");
        for (AnnotatedSubCommand subCommand : getSubCommands()) {
            if (subCommand == context.getSubCommand()) {
                continue;
            }
            String usageText = getUsageText(context, subCommand);
            sender.sendMessage(ChatColor.WHITE + usageText + ChatColor.GRAY + " - " + subCommand.getDescription());
        }
    }

    @Name("reload")
    @Description("插件已重新加载.")
    @Permission(Permissions.COMMAND_PREFIX + "reload")
    @DisplayPriority(100)
    public void reload(CommandSender sender) {
        MenuManager.closeAllOpenMenuViews();

        ErrorCollector errorCollector = ChestCommands.load();

        if (!errorCollector.hasErrors()) {
            sender.sendMessage(ChestCommands.CHAT_PREFIX + "配置文件加载成功.");
        } else {
            errorCollector.logToConsole();
            sender.sendMessage(ChestCommands.CHAT_PREFIX + ChatColor.RED + "插件已重新加载：" + errorCollector.getErrorsCount() + " 错误。");
            if (!(sender instanceof ConsoleCommandSender)) {
                sender.sendMessage(ChestCommands.CHAT_PREFIX + ChatColor.RED + "请检查控制台。");
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
            sender.sendMessage(ChestCommands.CHAT_PREFIX + ChatColor.RED + "上次插件加载时间，"
                    + errorCollector.getErrorsCount() + " 发现错误。");
            if (!(sender instanceof ConsoleCommandSender)) {
                sender.sendMessage(ChestCommands.CHAT_PREFIX + ChatColor.RED + "控制台上列出了错误信息。");
            }
        } else {
            sender.sendMessage(ChestCommands.CHAT_PREFIX + ChatColor.GREEN + "上次插件加载成功，未记录任何错误。");
        }
    }

    @Name("list")
    @Description("列出已加载的菜单。")
    @Permission(Permissions.COMMAND_PREFIX + "list")
    @DisplayPriority(2)
    public void list(CommandSender sender) {
        sender.sendMessage(ChestCommands.CHAT_PREFIX + "已加载菜单:");
        for (CaseInsensitiveString name : MenuManager.getMenuFileNames()) {
            sender.sendMessage(ChatColor.GRAY + "- " + ChatColor.WHITE + name);
        }
    }

    @Name("open")
    @Description("打开玩家菜单。")
    @Permission(Permissions.COMMAND_PREFIX + "open")
    @MinArgs(1)
    @UsageArgs("<menu> [player]")
    @DisplayPriority(1)
    @SuppressWarnings("deprecation")
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
            Text.send(sender, ChatColor.GREEN + "Opening the menu " + menuName + ".");
        } else {
            Text.send(sender, ChatColor.GREEN + "Opening the menu " + menuName + " to " + target.getName() + ".");
        }

        FoliaScheduler.runAtPlayer(target, () -> menu.open(target));
    }

    @Name("sound")
    @Description("播放声音进行测试。")
    @Permission(Permissions.COMMAND_PREFIX + "sound")
    @MinArgs(1)
    @UsageArgs("<sound> [pitch] [volume]")
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
        /* Text.send(sender, ChatColor.GREEN + "播放声音 " + ChatColor.WHITE + args[0] + ChatColor.GREEN + "."); */
		sender.sendMessage(ChatColor.GREEN + "播放声音 " + ChatColor.WHITE + args[0] + ChatColor.GREEN + ".");
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
