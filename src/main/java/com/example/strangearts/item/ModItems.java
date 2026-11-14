package com.example.strangearts.item;

import com.example.strangearts.StrangeArts;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Rarity;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;

public class ModItems {
    public static final DeferredRegister.Items ITEMS = DeferredRegister.createItems(StrangeArts.MOD_ID);

    // Глаз даёт пассивное усиление в слоте Curios, плащ пока чисто декоративный
    public static final DeferredItem<Item> EYE_OF_AGAMOTTO = ITEMS.register("eye_of_agamotto",
            () -> new EyeOfAgamottoItem(new Item.Properties().stacksTo(1).rarity(Rarity.EPIC)));

    public static final DeferredItem<Item> CLOAK_OF_LEVITATION = ITEMS.registerSimpleItem(
            "cloak_of_levitation", new Item.Properties().stacksTo(1).rarity(Rarity.EPIC));

    public static final DeferredItem<Item> THUNDER_AXE = ITEMS.register("thunder_axe", ThunderAxeItem::new);

    public static final DeferredItem<Item> MYSTIC_SCROLL = ITEMS.register("mystic_scroll", MysticScrollItem::new);
}
