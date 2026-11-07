package dev.andows96.dragonwings;

import java.util.function.Supplier;

import net.neoforged.api.distmarker.Dist;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.client.gui.IConfigScreenFactory;

/**
 * Port of Dragon Wings (https://github.com/Xiaoyu-2009/Dragon-Wings) to Minecraft 26.3.
 * Client-side only.
 */
@Mod(value = DragonWings.MOD_ID, dist = Dist.CLIENT)
public class DragonWings {
    public static final String MOD_ID = "dragon_wings_rebuild";

    public DragonWings(ModContainer modContainer) {
        Config.registerConfig(modContainer);

        // Standard NeoForge hook: used by the mod list's config button and by third-party
        // mod list screens (Catalogue, Configured) once they support this Minecraft version.
        Supplier<IConfigScreenFactory> configScreen = () -> (container, parent) -> new WingConfigScreen(parent);
        modContainer.registerExtensionPoint(IConfigScreenFactory.class, configScreen);
    }
}
