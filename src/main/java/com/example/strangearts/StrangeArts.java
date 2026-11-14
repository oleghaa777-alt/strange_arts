package com.example.strangearts;

import com.example.strangearts.item.ModItems;
import com.example.strangearts.magic.StrangeAttributes;
import com.example.strangearts.magic.StrangeSchools;
import com.example.strangearts.magic.StrangeSpells;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.common.Mod;

@Mod(StrangeArts.MOD_ID)
public class StrangeArts {
    public static final String MOD_ID = "strange_arts";

    public static ResourceLocation id(String path) {
        return ResourceLocation.fromNamespaceAndPath(MOD_ID, path);
    }

    public StrangeArts(IEventBus modEventBus) {
        ModItems.ITEMS.register(modEventBus);
        StrangeAttributes.ATTRIBUTES.register(modEventBus);
        StrangeSchools.SCHOOLS.register(modEventBus);
        StrangeSpells.SPELLS.register(modEventBus);
        com.example.strangearts.item.ModTabs.TABS.register(modEventBus);
        modEventBus.addListener(StrangeAttributes::onAttach);
        net.neoforged.neoforge.common.NeoForge.EVENT_BUS.addListener(StrangeEvents::onSpellDamage);
    }
}
