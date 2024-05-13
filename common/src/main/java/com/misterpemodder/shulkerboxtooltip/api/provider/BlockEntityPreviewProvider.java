package com.misterpemodder.shulkerboxtooltip.api.provider;

import com.misterpemodder.shulkerboxtooltip.ShulkerBoxTooltip;
import com.misterpemodder.shulkerboxtooltip.api.PreviewContext;
import com.misterpemodder.shulkerboxtooltip.api.PreviewType;
import com.misterpemodder.shulkerboxtooltip.api.ShulkerBoxTooltipApi;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.ContainerLootComponent;
import net.minecraft.inventory.Inventories;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.text.MutableText;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.collection.DefaultedList;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * <p>
 * A PreviewProvider that works on items that carries block entity data.
 * </p>
 * <p>
 * Use/extend this when the target item(s) has the {@code Inventory} inside {@code BlockEntityData}
 * as created by {@link Inventories#writeNbt(NbtCompound, DefaultedList, RegistryWrapper.WrapperLookup)}.
 * </p>
 *
 * @since 1.3.0
 */
public class BlockEntityPreviewProvider implements PreviewProvider {
  /**
   * The maximum preview inventory size of the item (maybe lower than the actual inventory size).
   *
   * @deprecated Use {@link #getInventoryMaxSize(PreviewContext)} instead.
   */
  @Deprecated(since = "4.0.8", forRemoval = true)
  protected final int maxInvSize;
  /**
   * If true, previews will not be shown when the {@code LootTable} tag inside {@code BlockEntityData} is present.
   *
   * @deprecated Use {@link #canUseLootTables()} instead.
   */
  @Deprecated(since = "4.0.8", forRemoval = true)
  protected final boolean canUseLootTables;
  /**
   * The maximum number of item stacks to be displayed in a row.
   *
   * @deprecated Use {@link #getMaxRowSize(PreviewContext)} instead.
   */
  @Deprecated(since = "4.0.8", forRemoval = true)
  protected final int maxRowSize;

  /**
   * Creates a BlockEntityPreviewProvider instance.
   *
   * @param maxInvSize       The maximum preview inventory size of the item
   *                         (maybe lower than the actual inventory size).
   *                         If the inventory size isn't constant,
   *                         override {@link #getInventoryMaxSize(PreviewContext)}
   *                         and use {@code maxInvSize} as a default value.
   * @param canUseLootTables If true, previews will not be shown when the {@code LootTable}
   *                         tag inside {@code BlockEntityData} is present.
   * @since 1.3.0
   */
  public BlockEntityPreviewProvider(int maxInvSize, boolean canUseLootTables) {
    this.maxInvSize = maxInvSize;
    this.canUseLootTables = canUseLootTables;
    this.maxRowSize = 9;
  }

  /**
   * Creates a BlockEntityPreviewProvider instance.
   *
   * @param maxInvSize       The maximum preview inventory size of the item
   *                         (maybe lower than the actual inventory size).
   *                         If the inventory size isn't constant,
   *                         override {@link #getInventoryMaxSize(PreviewContext)}
   *                         and use {@code maxInvSize} as a default value.
   * @param canUseLootTables If true, previews will not be shown when the {@code LootTable}
   *                         tag inside {@code BlockEntityData} is present.
   * @param maxRowSize       The maximum number of item stacks to be displayed in a row.
   *                         If less or equal to zero, defaults to 9.
   * @since 2.0.0
   */
  public BlockEntityPreviewProvider(int maxInvSize, boolean canUseLootTables, int maxRowSize) {
    this.maxInvSize = maxInvSize;
    this.canUseLootTables = canUseLootTables;
    this.maxRowSize = maxRowSize <= 0 ? 9 : maxRowSize;
  }

  @Override
  public boolean shouldDisplay(PreviewContext context) {
    if (this.canUseLootTables() && context.stack().contains(DataComponentTypes.CONTAINER_LOOT))
      return false;
    return getItemCount(this.getInventory(context)) > 0;
  }

  @Override
  public boolean showTooltipHints(PreviewContext context) {
    return context.stack().contains(DataComponentTypes.CONTAINER);
  }

  @Override
  public List<ItemStack> getInventory(PreviewContext context) {
    var registries = context.registryLookup();
    var container = context.stack().get(DataComponentTypes.CONTAINER);
    var invMaxSize = this.getInventoryMaxSize(context);
    var inv = DefaultedList.ofSize(invMaxSize, ItemStack.EMPTY);

    if (registries != null && container != null)
      container.copyTo(inv);

    return inv;
  }

  @Override
  public int getInventoryMaxSize(PreviewContext context) {
    return this.maxInvSize;
  }

  @Override
  public List<Text> addTooltip(PreviewContext context) {
    ItemStack stack = context.stack();
    ContainerLootComponent lootComponent = stack.get(DataComponentTypes.CONTAINER_LOOT);
    Style style = Style.EMPTY.withColor(Formatting.GRAY);

    if (this.canUseLootTables() && lootComponent != null) {
      return switch (ShulkerBoxTooltip.config.tooltip.lootTableInfoType) {
        case HIDE -> Collections.emptyList();
        case SIMPLE -> Collections.singletonList(Text.translatable("shulkerboxtooltip.hint.lootTable").setStyle(style));
        default -> Arrays.asList(
            Text.translatable("shulkerboxtooltip.hint.lootTable.advanced").append(Text.literal(": ")),
            Text.literal(" " + lootComponent.lootTable().getValue()).setStyle(style));
      };
    }
    if (ShulkerBoxTooltipApi.getCurrentPreviewType(this.isFullPreviewAvailable(context)) == PreviewType.FULL)
      return Collections.emptyList();
    return getItemListTooltip(new ArrayList<>(), this.getInventory(context), style);
  }

  /**
   * Adds the number of items to the passed tooltip, adds 'empty' if there is no items to count.
   *
   * @param tooltip The tooltip in which to add the item count.
   * @param items   The list of items to display, may be null or empty.
   * @return The passed tooltip, to allow chaining.
   * @since 2.0.0
   */
  public static List<Text> getItemCountTooltip(List<Text> tooltip, @Nullable List<ItemStack> items) {
    return getItemListTooltip(tooltip, items, Style.EMPTY.withColor(Formatting.GRAY));
  }

  /**
   * Adds the number of items to the passed tooltip, adds 'empty' if there is no items to count.
   *
   * @param tooltip The tooltip in which to add the item count.
   * @param items   The list of items to display, may be null or empty.
   * @param style   The formatting style of the tooltip.
   * @return The passed tooltip, to allow chaining.
   * @since 2.0.0
   */
  public static List<Text> getItemListTooltip(List<Text> tooltip, @Nullable List<ItemStack> items, Style style) {
    int itemCount = getItemCount(items);
    MutableText text;

    if (itemCount > 0)
      text = Text.translatable("container.shulkerbox.contains", itemCount);
    else
      text = Text.translatable("container.shulkerbox.empty");
    tooltip.add(text.setStyle(style));
    return tooltip;
  }

  @Override
  public int getMaxRowSize(PreviewContext context) {
    return this.maxRowSize;
  }

  /**
   * If true, previews will not be shown when the {@code LootTable} tag inside {@code BlockEntityData} is present.
   *
   * @since 4.0.8
   */
  public boolean canUseLootTables() {
    return this.canUseLootTables;
  }

  private static int getItemCount(@Nullable List<ItemStack> items) {
    int itemCount = 0;

    if (items != null)
      for (ItemStack stack : items)
        if (stack.getItem() != Items.AIR)
          ++itemCount;
    return itemCount;
  }
}
