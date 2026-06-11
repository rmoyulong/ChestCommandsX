/*
 * Copyright (C) filoghost and contributors
 *
 * SPDX-License-Identifier: GPL-3.0-or-later
 */
package me.filoghost.chestcommands.listener;

import me.filoghost.chestcommands.ChestCommands;
import me.filoghost.chestcommands.Permissions;
import me.filoghost.chestcommands.config.Settings;
import me.filoghost.chestcommands.util.Text;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

public class JoinListener implements Listener {

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();

        if (ChestCommands.getLastLoadErrors().hasErrors() && player.hasPermission(Permissions.SEE_ERRORS)) {
            Text.send(player,
                    ChestCommands.CHAT_PREFIX + ChatColor.RED + "找到的插件 " + ChestCommands.getLastLoadErrors().getErrorsCount()
                    + " 上次加载时出现错误。您可以通过以下方式查看错误信息： \"/cc reload\" 在控制台中。");
        }

        if (ChestCommands.hasNewVersion() && Settings.get().update_notifications && player.hasPermission(Permissions.UPDATE_NOTIFICATIONS)) {
            Text.send(player, ChestCommands.CHAT_PREFIX + "找到更新: " + ChestCommands.getNewVersion() + ". 下载:");
            Text.send(player, ChatColor.DARK_GREEN + ">> " + ChatColor.GREEN + "http://dev.bukkit.org/bukkit-plugins/chest-commands");
        }
    }

}
