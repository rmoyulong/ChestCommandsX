/*
 * Copyright (C) filoghost and contributors
 *
 * SPDX-License-Identifier: GPL-3.0-or-later
 */
package me.filoghost.chestcommands.icon.requirement;

import com.google.common.base.Preconditions;
import me.filoghost.chestcommands.config.Lang;
import me.filoghost.chestcommands.hook.VaultEconomyHook;
import me.filoghost.chestcommands.logging.Errors;
import me.filoghost.chestcommands.util.Text;
import org.bukkit.entity.Player;

public class RequiredMoney implements Requirement {

    private final double moneyAmount;

    public RequiredMoney(double moneyAmount) {
        Preconditions.checkArgument(moneyAmount > 0.0, "必须为正数。");
        this.moneyAmount = moneyAmount;
    }

    @Override
    public boolean hasCost(Player player) {
        if (!VaultEconomyHook.INSTANCE.isEnabled()) {
            Text.send(player, Errors.User.configurationError(
                    "该物品有价格，但未找到具有兼容经济插件的 Vault。"
                    + "出于安全考虑，该操作已被阻止。"));
            return false;
        }

        if (!VaultEconomyHook.hasMoney(player, moneyAmount)) {
            Text.send(player, Lang.get().no_money.replace("{money}", VaultEconomyHook.formatMoney(moneyAmount)));
            return false;
        }

        return true;
    }

    @Override
    public boolean takeCost(Player player) {
        boolean success = VaultEconomyHook.takeMoney(player, moneyAmount);

        if (!success) {
            Text.send(player, Errors.User.configurationError("一笔资金交易无法执行。"));
        }

        return success;
    }

}
