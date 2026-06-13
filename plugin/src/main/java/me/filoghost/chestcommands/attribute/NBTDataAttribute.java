/*
 * Copyright (C) filoghost and contributors
 *
 * SPDX-License-Identifier: GPL-3.0-or-later
 */
package me.filoghost.chestcommands.attribute;

import me.filoghost.chestcommands.icon.InternalConfigurableIcon;
import me.filoghost.chestcommands.parsing.ParseException;

public class NBTDataAttribute implements IconAttribute {

    public NBTDataAttribute(String nbtData, AttributeErrorHandler errorHandler) throws ParseException {
        throw new ParseException("NBT-DATA Minecraft 1.21 已不再支持此功能。 ; 请使用 DAMAGE, CUSTOM-MODEL-DATA, COLOR, SKULL-OWNER, ENCHANTMENTS, UNBREAKABLE, 和 ITEM-FLAGS");
    }

    @Override
    public void apply(InternalConfigurableIcon icon) {
        // Constructor always rejects this attribute.
    }

}
