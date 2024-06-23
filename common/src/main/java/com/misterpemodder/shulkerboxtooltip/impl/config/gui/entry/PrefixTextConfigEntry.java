package com.misterpemodder.shulkerboxtooltip.impl.config.gui.entry;

import com.misterpemodder.shulkerboxtooltip.impl.config.gui.ConfigCategoryTab;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.MultiLineTextWidget;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.narration.NarratableEntry;
import net.minecraft.network.chat.Component;
import org.jetbrains.annotations.NotNull;

import java.util.List;

@Environment(EnvType.CLIENT)
public final class PrefixTextConfigEntry extends ConfigEntry {
  private final MultiLineTextWidget textWidget;
  private final List<MultiLineTextWidget> textWidgetAsList;

  public PrefixTextConfigEntry(ConfigCategoryTab<?> tab, Component text) {
    super();
    this.textWidget = new MultiLineTextWidget(text, tab.getMinecraft().font);
    this.textWidgetAsList = List.of(this.textWidget);
  }

  @NotNull
  @Override
  public List<? extends NarratableEntry> narratables() {
    return this.textWidgetAsList;
  }

  @Override
  public void render(GuiGraphics guiGraphics, int index, int y, int x, int entryWidth, int entryHeight, int mouseX,
      int mouseY, boolean hovered, float delta) {
    this.textWidget.setX(x);
    this.textWidget.setY(y);
    this.textWidget.setMaxWidth(entryWidth);
    this.textWidget.renderWidget(guiGraphics, mouseX, mouseY, delta);
  }

  @NotNull
  @Override
  public List<? extends GuiEventListener> children() {
    return this.textWidgetAsList;
  }
}
