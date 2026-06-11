/*
 * Copyright (C) filoghost and contributors
 *
 * SPDX-License-Identifier: GPL-3.0-or-later
 */
package me.filoghost.chestcommands.hook;

import me.filoghost.fcommons.Preconditions;

public interface PluginHook {

    
    void setup();
    
    boolean isEnabled();
    
    default void checkEnabledState() {
        Preconditions.checkState(isEnabled(), "插件关联 " + getClass().getSimpleName() + " 未启用");
    }

}
