package com.misterpemodder.shulkerboxtooltip.impl.config.gui.entry;

import com.misterpemodder.shulkerboxtooltip.impl.config.gui.ConfigCategoryTab;
import com.misterpemodder.shulkerboxtooltip.impl.tree.ValueConfigNode;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.CycleButton;
import net.minecraft.network.chat.CommonComponents;

import java.util.Objects;

@Environment(EnvType.CLIENT)
public final class BooleanValueConfigEntry<C> extends ValueConfigEntry<C, Boolean, Boolean> {
  private final CycleButton<Boolean> valueButton;

  public BooleanValueConfigEntry(ConfigCategoryTab<C> tab, ValueConfigNode<C, Boolean, Boolean> valueNode) {
    super(tab, valueNode);

    this.valueButton = CycleButton.booleanBuilder(CommonComponents.GUI_YES.copy().withStyle(ChatFormatting.GREEN),
            CommonComponents.GUI_NO.copy().withStyle(ChatFormatting.RED))
        .withInitialValue(this.getValue())
        .displayOnlyValue()
        .create(0, 0, 160, 20, valueNode.getTitle(), (b, value) -> this.setValue(value));
    this.children.addFirst(this.valueButton);
  }

  @Override
  public void refresh() {
    super.refresh();
    var value = this.getValue();
    if (!Objects.equals(this.valueButton.getValue(), value)) {
      this.valueButton.setValue(value);
    }
  }

  @Override
  public void render(GuiGraphics guiGraphics, int index, int y, int x, int entryWidth, int entryHeight, int mouseX,
      int mouseY, boolean hovered, float delta) {
    this.renderLabel(guiGraphics, x, y, entryWidth);

    this.valueButton.setWidth(160 - this.resetButton.getWidth() - 2 - this.undoButton.getWidth() - 2);
    if (this.tab.getMinecraft().font.isBidirectional()) {
      this.undoButton.setX(x);
      this.undoButton.setY(y);

      this.resetButton.setX(x + undoButton.getWidth() + 2);
      this.resetButton.setY(y);

      this.valueButton.setX(x + undoButton.getWidth() + 2 + resetButton.getWidth() + 2);
      this.valueButton.setY(y);
    } else {
      this.undoButton.setX(x + entryWidth - this.undoButton.getWidth());
      this.undoButton.setY(y);

      this.resetButton.setX(this.undoButton.getX() - this.resetButton.getWidth() - 2);
      this.resetButton.setY(y);

      this.valueButton.setX(this.resetButton.getX() - this.valueButton.getWidth() - 2);
      this.valueButton.setY(y);
    }

    this.valueButton.render(guiGraphics, mouseX, mouseY, delta);
    this.resetButton.render(guiGraphics, mouseX, mouseY, delta);
    this.undoButton.render(guiGraphics, mouseX, mouseY, delta);
  }
}
