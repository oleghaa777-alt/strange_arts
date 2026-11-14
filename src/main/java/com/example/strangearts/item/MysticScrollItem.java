package com.example.strangearts.item;

import io.redspace.ironsspellbooks.item.Scroll;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Rarity;

/**
 * Свиток мистического заклинания. Наследует свиток Iron's, поэтому читается, кастуется,
 * тратится и показывает название заклинания точно так же. Своя только текстура.
 * Какое заклинание внутри, записано в самом предмете (см. ModTabs).
 */
public class MysticScrollItem extends Scroll {
    public MysticScrollItem() {
        super(new Item.Properties().rarity(Rarity.RARE));
    }
}
