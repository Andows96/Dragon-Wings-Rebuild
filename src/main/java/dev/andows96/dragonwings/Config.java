package dev.andows96.dragonwings;

import net.neoforged.fml.ModContainer;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.neoforge.common.ModConfigSpec;

/**
 * Unchanged from upstream apart from the package: same keys, same defaults, same ranges,
 * so an existing {@code config/dragon_wings-client.toml} maps over key-for-key.
 */
public class Config {
    public static ModConfigSpec configSpec;

    private static final WingType[] WING_TYPES = WingType.values();

    public static ModConfigSpec.BooleanValue[] WINGS_ENABLED;
    public static ModConfigSpec.IntValue[] WINGS_SCALE;
    public static ModConfigSpec.BooleanValue[] WINGS_FLYING_EXPAND;
    public static ModConfigSpec.BooleanValue[] SHOW_OTHER_PLAYERS_WINGS;

    static {
        ModConfigSpec.Builder builder = new ModConfigSpec.Builder();

        WINGS_ENABLED = new ModConfigSpec.BooleanValue[WING_TYPES.length];
        WINGS_SCALE = new ModConfigSpec.IntValue[WING_TYPES.length];
        WINGS_FLYING_EXPAND = new ModConfigSpec.BooleanValue[WING_TYPES.length];
        SHOW_OTHER_PLAYERS_WINGS = new ModConfigSpec.BooleanValue[WING_TYPES.length];

        for (int i = 0; i < WING_TYPES.length; i++) {
            WingType wingType = WING_TYPES[i];
            builder.push(wingType.getDisplayName() + " Settings");

            WINGS_ENABLED[i] = builder
                .comment("Enable or disable the " + wingType.getDisplayName().toLowerCase() + " rendering")
                .define(wingType.getEnabledConfigKey(), i == 0);

            WINGS_SCALE[i] = builder
                .comment("Scale of the " + wingType.getDisplayName().toLowerCase())
                .defineInRange(wingType.getScaleConfigKey(), 100, 1, Integer.MAX_VALUE);

            WINGS_FLYING_EXPAND[i] = builder
                .comment("Whether to expand wings when flying for " + wingType.getDisplayName().toLowerCase())
                .define(wingType.getFlyingExpandConfigKey(), false);

            SHOW_OTHER_PLAYERS_WINGS[i] = builder
                .comment("Whether to show " + wingType.getDisplayName().toLowerCase() + " on other players")
                .define(wingType.getDisplayName().toLowerCase().replace(" ", "_") + "_show_other_players_wings", false);

            builder.pop();
        }

        configSpec = builder.build();
    }

    public static void registerConfig(ModContainer modContainer) {
        modContainer.registerConfig(ModConfig.Type.CLIENT, configSpec);
    }

    public static boolean isWingsEnabled(WingType wingType) {
        int index = wingType.ordinal();
        if (index >= 0 && index < WINGS_ENABLED.length) {
            return WINGS_ENABLED[index].get();
        }
        return true;
    }

    public static int getWingsScale(WingType wingType) {
        int index = wingType.ordinal();
        if (index >= 0 && index < WINGS_SCALE.length) {
            return WINGS_SCALE[index].get();
        }
        return 100;
    }

    public static boolean isWingsFlyingExpand(WingType wingType) {
        int index = wingType.ordinal();
        if (index >= 0 && index < WINGS_FLYING_EXPAND.length) {
            return WINGS_FLYING_EXPAND[index].get();
        }
        return true;
    }

    public static boolean showOtherPlayersWings(WingType wingType) {
        int index = wingType.ordinal();
        if (index >= 0 && index < SHOW_OTHER_PLAYERS_WINGS.length) {
            return SHOW_OTHER_PLAYERS_WINGS[index].get();
        }
        return true;
    }
}
