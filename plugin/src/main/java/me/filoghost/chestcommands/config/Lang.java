/*
 * Copyright (C) filoghost and contributors
 *
 * SPDX-License-Identifier: GPL-3.0-or-later
 */
package me.filoghost.chestcommands.config;

import me.filoghost.chestcommands.logging.Errors;
import me.filoghost.fcommons.config.mapped.MappedConfig;
import me.filoghost.fcommons.config.mapped.modifier.ChatColors;

@ChatColors
public class Lang implements MappedConfig {

    public String no_open_permission = "&c您没有找个权限： &e{permission} &c 不能执行这个菜单.";
    public String default_no_icon_permission = "&c您没有使用此图标的权限.";
    public String no_required_item = "&c您身上包裹里必须有数量为 &e{amount} 的 {material} 并且 &c(耐久性为: {durability}) 的物品.";
    public String no_money = "&c您需要有 {money} 金币才行.";
    public String no_exp = "&c您需要有 {levels} XP 等级才行.";
    public String menu_not_found = "&c未找到菜单！请告知小森!";
    public String any = "任意";
    
    private static Lang instance;
    
    static void setInstance(Lang instance) {
        Lang.instance = instance;
    }

    public static Lang get() {
        return instance;
    }
    
}
