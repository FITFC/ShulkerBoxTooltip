package com.misterpemodder.shulkerboxtooltip.impl.config.gui.entry;

import com.misterpemodder.shulkerboxtooltip.impl.config.gui.ConfigCategoryTab;
import com.misterpemodder.shulkerboxtooltip.impl.tree.ValueConfigNode;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.network.chat.Style;
import net.minecraft.util.FormattedCharSequence;

import java.util.regex.Pattern;

@Environment(EnvType.CLIENT)
public final class IntegerValueConfigEntry<C> extends ValueConfigEntry<C, Integer, Integer> {
  private final EditBox inputField;

  private static final Pattern INTEGER_PATTERN = Pattern.compile("-?\\d*");

  public IntegerValueConfigEntry(ConfigCategoryTab<C> tab, ValueConfigNode<C, Integer, Integer> valueNode) {
    super(tab, valueNode);

    this.inputField = new EditBox(tab.getMinecraft().font, 0, 0, 158, 18, this.valueNode.getTitle());
    this.inputField.setValue(this.getValue().toString());
    this.inputField.setFilter(s -> INTEGER_PATTERN.matcher(s).matches());
    this.inputField.setResponder(this::onInputChange);
    this.children.addFirst(this.inputField);
  }

  @Override
  public void refresh() {
    if (this.valueNode.validate(this.tab.getConfig()) == null) {
      var valueStr = this.getValue().toString();
      if (!this.inputField.getValue().equals(valueStr)) {
        this.inputField.setValue(valueStr);
      }
      this.inputField.setFormatter((s, i) -> FormattedCharSequence.forward(s, Style.EMPTY));
    } else {
      this.inputField.setFormatter(
          (s, i) -> FormattedCharSequence.forward(s, Style.EMPTY.withColor(ChatFormatting.RED)));
    }
    super.refresh();
  }

  private void onInputChange(String value) {
    try {
      this.setValue(Integer.parseInt(value));
    } catch (NumberFormatException e) {
      // Ignore
    }
  }

  @Override
  public void render(GuiGraphics guiGraphics, int index, int y, int x, int entryWidth, int entryHeight, int mouseX,
      int mouseY, boolean hovered, float delta) {
    this.renderLabel(guiGraphics, x, y, entryWidth);
    this.inputField.setX(x + entryWidth - 158 - 1);
    this.inputField.setY(y + 1);

    this.resetButton.setX(x + entryWidth - this.resetButton.getWidth() - 2 - this.undoButton.getWidth());
    this.resetButton.setY(y);

    this.undoButton.setX(x + entryWidth - this.undoButton.getWidth());
    this.undoButton.setY(y);

    this.inputField.setWidth(158 - this.resetButton.getWidth() - 2 - this.undoButton.getWidth() - 2);
    if (this.tab.getMinecraft().font.isBidirectional()) {
      this.undoButton.setX(x);
      this.undoButton.setY(y);

      this.resetButton.setX(this.undoButton.getX() + this.undoButton.getWidth() + 2);
      this.resetButton.setY(y);

      this.inputField.setX(this.resetButton.getX() + this.resetButton.getWidth() + 2);
      this.inputField.setY(y + 1);
    } else {
      this.undoButton.setX(x + entryWidth - this.undoButton.getWidth());
      this.undoButton.setY(y);

      this.resetButton.setX(this.undoButton.getX() - this.resetButton.getWidth() - 2);
      this.resetButton.setY(y);

      this.inputField.setX(this.resetButton.getX() - this.inputField.getWidth() - 3);
      this.inputField.setY(y + 1);
    }

    this.inputField.render(guiGraphics, mouseX, mouseY, delta);
    this.resetButton.render(guiGraphics, mouseX, mouseY, delta);
    this.undoButton.render(guiGraphics, mouseX, mouseY, delta);
  }
}
