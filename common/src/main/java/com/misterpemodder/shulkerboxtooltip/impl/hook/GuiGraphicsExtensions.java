package com.misterpemodder.shulkerboxtooltip.impl.hook;

/**
 * Provides access to the {@link net.minecraft.client.gui.GuiGraphics} methods added by the mod.
 */
public interface GuiGraphicsExtensions {
  void setTooltipTopYPosition(int topY);

  void setTooltipBottomYPosition(int bottomY);

  int getTooltipTopYPosition();

  int getTooltipBottomYPosition();

  void setMouseX(int mouseX);

  int getMouseX();

  void setMouseY(int mouseY);

  int getMouseY();
}