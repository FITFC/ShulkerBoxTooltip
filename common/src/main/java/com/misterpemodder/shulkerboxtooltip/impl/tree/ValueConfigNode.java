package com.misterpemodder.shulkerboxtooltip.impl.tree;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;

public final class ValueConfigNode<C, T, V> implements ConfigNode<C> {
  private String name;
  private Component title;
  private Component tooltip;
  private Component prefix;
  private Class<? extends T> type;
  private Class<? extends V> valueType;
  private boolean requiresRestart;
  private ValueReader<C, V> valueReader;
  private ValueWriter<C, V> valueWriter;
  @Nullable
  private ValueReader<CompoundTag, V> nbtReader;
  @Nullable
  private ValueWriter<CompoundTag, V> nbtWriter;
  @Nullable
  private ValueValidator<V> validator;
  private V defaultValue;
  private V editingValue;
  private CategoryConfigNode<C> category;

  private ValueConfigNode() {
  }

  public static <C, T, V> Builder<C, T, V> builder() {
    return new Builder<>();
  }

  public Class<? extends T> getType() {
    return this.type;
  }

  @NotNull
  public Class<? extends V> getValueType() {
    return this.valueType;
  }

  public V getDefaultValue() {
    return this.defaultValue;
  }

  public V getActiveValue(C config) {
    return this.valueReader.read(config);
  }

  public void setActiveValue(C config, V value) {
    this.valueWriter.write(config, value);
  }

  public V getEditingValue(C config) {
    if (this.editingValue == null) {
      this.setEditingValue(this.getActiveValue(config));
    }
    return this.editingValue;
  }

  public void setEditingValue(V value) {
    this.editingValue = value;
  }

  @Override
  public void resetToDefault() {
    this.setEditingValue(this.getDefaultValue());
  }

  @Override
  public void resetToActive(C config) {
    this.setEditingValue(this.getActiveValue(config));
  }

  @Override
  public boolean isDefaultValue(C config) {
    return Objects.equals(this.getDefaultValue(), this.getEditingValue(config));
  }

  @Override
  public boolean isActiveValue(C config) {
    return Objects.equals(this.getActiveValue(config), this.getEditingValue(config));
  }

  @Override
  public Component validate(C config) {
    return this.validator == null ? null : this.validator.validate(this.getEditingValue(config));
  }

  @NotNull
  @Override
  public String getName() {
    return this.name;
  }

  @NotNull
  @Override
  public Component getTitle() {
    return this.title;
  }

  @Nullable
  @Override
  public Component getTooltip() {
    return this.tooltip;
  }

  @Nullable
  @Override
  public Component getPrefix() {
    return this.prefix;
  }

  @Override
  public boolean restartRequired(C config) {
    return this.requiresRestart && !this.isActiveValue(config);
  }

  @Override
  public void writeToNbt(C config, CompoundTag compound) {
    if (this.nbtWriter == null)
      return;
    this.nbtWriter.write(compound, this.getActiveValue(config));
  }

  @Override
  public void readFromNbt(C config, CompoundTag compound) {
    if (this.nbtReader == null)
      return;
    this.setActiveValue(config, this.nbtReader.read(compound));
  }

  @Override
  public void copy(C from, C to) {
    this.setActiveValue(to, this.getActiveValue(from));
  }

  @Override
  public void writeEditingToConfig(C config) {
    this.setActiveValue(config, this.getEditingValue(config));
  }

  @FunctionalInterface
  public interface ValueReader<S, V> {
    V read(S source);
  }


  @FunctionalInterface
  public interface ValueWriter<S, V> {
    void write(S source, V value);
  }

  public interface ValueValidator<V> {
    @Nullable
    Component validate(V value);
  }

  public static class Builder<C, T, V> {
    private ValueConfigNode<C, T, V> node;

    private Builder() {
      this.node = new ValueConfigNode<>();
    }

    public Builder<C, T, V> type(Class<? extends T> type) {
      this.node.type = type;
      return this;
    }

    public Builder<C, T, V> valueType(Class<? extends V> valueType) {
      this.node.valueType = valueType;
      return this;
    }

    public Builder<C, T, V> name(String name) {
      this.node.name = name;
      return this;
    }

    public Builder<C, T, V> title(Component title) {
      this.node.title = title;
      return this;
    }

    public Builder<C, T, V> tooltip(Component tooltip) {
      this.node.tooltip = tooltip;
      return this;
    }

    public Builder<C, T, V> prefix(Component prefix) {
      this.node.prefix = prefix;
      return this;
    }

    public Builder<C, T, V> defaultValue(V defaultValue) {
      this.node.defaultValue = defaultValue;
      return this;
    }

    public Builder<C, T, V> requiresRestart(boolean requiresRestart) {
      this.node.requiresRestart = requiresRestart;
      return this;
    }

    public Builder<C, T, V> valueReader(ValueReader<C, V> valueReader) {
      this.node.valueReader = valueReader;
      return this;
    }

    public Builder<C, T, V> valueWriter(ValueWriter<C, V> valueWriter) {
      this.node.valueWriter = valueWriter;
      return this;
    }

    public Builder<C, T, V> validator(ValueValidator<V> validator) {
      this.node.validator = validator;
      return this;
    }

    public Builder<C, T, V> nbtReader(ValueReader<CompoundTag, V> nbtReader) {
      this.node.nbtReader = nbtReader;
      return this;
    }

    public Builder<C, T, V> nbtWriter(ValueWriter<CompoundTag, V> nbtWriter) {
      this.node.nbtWriter = nbtWriter;
      return this;
    }

    public Builder<C, T, V> category(CategoryConfigNode<C> category) {
      this.node.category = category;
      return this;
    }

    public ValueConfigNode<C, T, V> build() {
      var n = this.node;

      Objects.requireNonNull(n.name);
      Objects.requireNonNull(n.type);
      Objects.requireNonNull(n.valueType);
      Objects.requireNonNull(n.title);
      Objects.requireNonNull(n.valueReader);
      Objects.requireNonNull(n.valueWriter);
      Objects.requireNonNull(n.category);
      this.node = null;
      return n;
    }
  }
}
