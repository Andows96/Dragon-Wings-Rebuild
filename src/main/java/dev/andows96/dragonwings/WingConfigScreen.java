package dev.andows96.dragonwings;

import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.AbstractSliderButton;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.Checkbox;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;

/**
 * In-game configuration screen for the ender dragon wings. Every change applies immediately (the
 * renderer reads the config every frame); {@code Done} writes the values to the client config file
 * and {@code Cancel} restores the values the screen was opened with.
 *
 * <p>Only the first wing type is exposed here. The second type stays available for players who
 * edit {@code config/dragon_wings_rebuild-client.toml} by hand.</p>
 */
public class WingConfigScreen extends Screen {
    private static final WingType WING_TYPE = WingType.ENDER_DRAGON;
    private static final int MIN_SCALE = 1;
    private static final int MAX_SCALE = 500;
    private static final int FIRST_ROW = 56;
    private static final int ROW_GAP = 26;
    private static final int COLUMN_WIDTH = 220;

    private static final String KEY_TITLE = "dragon_wings_rebuild.config.title";
    private static final String KEY_ENABLED = "dragon_wings_rebuild.config.enabled";
    private static final String KEY_SCALE = "dragon_wings_rebuild.config.scale";
    private static final String KEY_FLYING_EXPAND = "dragon_wings_rebuild.config.flying_expand";
    private static final String KEY_SHOW_OTHERS = "dragon_wings_rebuild.config.show_others";
    private static final String KEY_DONE = "dragon_wings_rebuild.config.done";
    private static final String KEY_CANCEL = "dragon_wings_rebuild.config.cancel";

    private final Screen parent;
    private final int index = WING_TYPE.ordinal();

    private final boolean initialEnabled;
    private final boolean initialFlyingExpand;
    private final boolean initialShowOthers;
    private final int initialScale;

    public WingConfigScreen(Screen parent) {
        super(Component.translatable(KEY_TITLE));
        this.parent = parent;
        this.initialEnabled = Config.WINGS_ENABLED[this.index].get();
        this.initialFlyingExpand = Config.WINGS_FLYING_EXPAND[this.index].get();
        this.initialShowOthers = Config.SHOW_OTHER_PLAYERS_WINGS[this.index].get();
        this.initialScale = Config.WINGS_SCALE[this.index].get();
    }

    @Override
    protected void init() {
        int x = (this.width - COLUMN_WIDTH) / 2;

        this.addRenderableWidget(Checkbox.builder(Component.translatable(KEY_ENABLED), this.font)
            .pos(x, FIRST_ROW)
            .maxWidth(COLUMN_WIDTH)
            .selected(Config.WINGS_ENABLED[this.index].get())
            .onValueChange((checkbox, value) -> Config.WINGS_ENABLED[this.index].set(value))
            .build());

        this.addRenderableWidget(new ScaleSlider(x, FIRST_ROW + ROW_GAP, COLUMN_WIDTH, 20));

        this.addRenderableWidget(Checkbox.builder(Component.translatable(KEY_FLYING_EXPAND), this.font)
            .pos(x, FIRST_ROW + ROW_GAP * 2)
            .maxWidth(COLUMN_WIDTH)
            .selected(Config.WINGS_FLYING_EXPAND[this.index].get())
            .onValueChange((checkbox, value) -> Config.WINGS_FLYING_EXPAND[this.index].set(value))
            .build());

        this.addRenderableWidget(Checkbox.builder(Component.translatable(KEY_SHOW_OTHERS), this.font)
            .pos(x, FIRST_ROW + ROW_GAP * 3)
            .maxWidth(COLUMN_WIDTH)
            .selected(Config.SHOW_OTHER_PLAYERS_WINGS[this.index].get())
            .onValueChange((checkbox, value) -> Config.SHOW_OTHER_PLAYERS_WINGS[this.index].set(value))
            .build());

        this.addRenderableWidget(Button.builder(Component.translatable(KEY_DONE), button -> this.saveAndClose())
            .bounds(this.width / 2 - 104, this.height - 28, 100, 20)
            .build());

        this.addRenderableWidget(Button.builder(Component.translatable(KEY_CANCEL), button -> this.cancel())
            .bounds(this.width / 2 + 4, this.height - 28, 100, 20)
            .build());
    }

    @Override
    public void extractRenderState(GuiGraphicsExtractor graphics, int mouseX, int mouseY, float partialTick) {
        int x = (this.width - COLUMN_WIDTH) / 2;
        graphics.fill(x - 10, 40, x + COLUMN_WIDTH + 10, FIRST_ROW + ROW_GAP * 3 + 22, 0x50101010);

        super.extractRenderState(graphics, mouseX, mouseY, partialTick);

        graphics.centeredText(this.font, this.title, this.width / 2, 20, 0xFFFFFF);
    }

    private void saveAndClose() {
        if (Config.configSpec.isLoaded()) {
            Config.configSpec.save();
        }
        this.closeToParent();
    }

    private void cancel() {
        Config.WINGS_ENABLED[this.index].set(this.initialEnabled);
        Config.WINGS_FLYING_EXPAND[this.index].set(this.initialFlyingExpand);
        Config.SHOW_OTHER_PLAYERS_WINGS[this.index].set(this.initialShowOthers);
        Config.WINGS_SCALE[this.index].set(this.initialScale);
        this.closeToParent();
    }

    private void closeToParent() {
        this.minecraft.setScreenAndShow(this.parent);
    }

    @Override
    public void onClose() {
        this.cancel();
    }

    private final class ScaleSlider extends AbstractSliderButton {
        private ScaleSlider(int x, int y, int width, int height) {
            super(x, y, width, height, Component.empty(), fractionOf(Config.WINGS_SCALE[index].get()));
            this.updateMessage();
        }

        private static double fractionOf(int scale) {
            int clamped = Math.max(MIN_SCALE, Math.min(MAX_SCALE, scale));
            return (double) (clamped - MIN_SCALE) / (MAX_SCALE - MIN_SCALE);
        }

        private int currentScale() {
            return (int) Math.round(MIN_SCALE + this.value * (MAX_SCALE - MIN_SCALE));
        }

        @Override
        protected void updateMessage() {
            this.setMessage(Component.translatable(KEY_SCALE, this.currentScale()));
        }

        @Override
        protected void applyValue() {
            Config.WINGS_SCALE[index].set(this.currentScale());
        }
    }
}
