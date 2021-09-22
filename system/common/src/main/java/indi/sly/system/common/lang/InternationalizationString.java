package indi.sly.system.common.lang;

import indi.sly.system.common.supports.NumberUtil;
import indi.sly.system.common.supports.ObjectUtil;
import indi.sly.system.common.supports.StringUtil;

import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.util.Collection;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.Function;

public final class InternationalizationString implements ISerializeCapable<InternationalizationString>, Map<Locale, String> {
    public InternationalizationString() {
        this.text = new HashMap<>();
    }

    private final Map<Locale, String> text;

    public int size() {
        return this.text.size();
    }

    public boolean isEmpty() {
        return this.text.isEmpty();
    }

    public boolean containsKey(Object key) {
        return this.text.containsKey(key);
    }

    public boolean containsValue(Object value) {
        return this.text.containsValue(value);
    }

    public String get(Object key) {
        return this.text.get(key);
    }

    public String put(Locale key, String value) {
        return this.text.put(key, value);
    }

    public String remove(Object key) {
        return this.text.remove(key);
    }

    public void putAll(Map<? extends Locale, ? extends String> m) {
        this.text.putAll(m);
    }

    public void clear() {
        this.text.clear();
    }

    public Set<Locale> keySet() {
        return this.text.keySet();
    }

    public Collection<String> values() {
        return this.text.values();
    }

    public Set<Entry<Locale, String>> entrySet() {
        return this.text.entrySet();
    }

    @Override
    public boolean equals(Object o) {
        return this.text.equals(o);
    }

    @Override
    public int hashCode() {
        return this.text.hashCode();
    }

    public String getOrDefault(Object key, String defaultValue) {
        return this.text.getOrDefault(key, defaultValue);
    }

    public void forEach(BiConsumer<? super Locale, ? super String> action) {
        this.text.forEach(action);
    }

    public void replaceAll(BiFunction<? super Locale, ? super String, ? extends String> function) {
        this.text.replaceAll(function);
    }

    public String putIfAbsent(Locale key, String value) {
        return this.text.putIfAbsent(key, value);
    }

    public boolean remove(Object key, Object value) {
        return this.text.remove(key, value);
    }

    public boolean replace(Locale key, String oldValue, String newValue) {
        return this.text.replace(key, oldValue, newValue);
    }

    public String replace(Locale key, String value) {
        return this.text.replace(key, value);
    }

    public String computeIfAbsent(Locale key, Function<? super Locale, ? extends String> mappingFunction) {
        return this.text.computeIfAbsent(key, mappingFunction);
    }

    public String computeIfPresent(Locale key,
                                   BiFunction<? super Locale, ? super String, ? extends String> remappingFunction) {
        return this.text.computeIfPresent(key, remappingFunction);
    }

    public String compute(Locale key, BiFunction<? super Locale, ? super String, ? extends String> remappingFunction) {
        return this.text.compute(key, remappingFunction);
    }

    public String merge(Locale key, String value,
                        BiFunction<? super String, ? super String, ? extends String> remappingFunction) {
        return this.text.merge(key, value, remappingFunction);
    }

    @Override
    public InternationalizationString deepClone() {
        InternationalizationString definition = new InternationalizationString();

        for (Entry<Locale, String> pair : this.text.entrySet()) {
            definition.text.put(pair.getKey(), pair.getValue());
        }

        return definition;
    }

    @Override
    public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
        int valueInteger;

        valueInteger = NumberUtil.readExternalInteger(in);
        for (int i = 0; i < valueInteger; i++) {
            this.text.put(NumberUtil.readExternalBoolean(in) ? (Locale) in.readObject() : null,
                    StringUtil.readExternal(in));
        }
    }

    @Override
    public void writeExternal(ObjectOutput out) throws IOException {
        NumberUtil.writeExternalInteger(out, this.text.size());
        for (Entry<Locale, String> pair : this.text.entrySet()) {
            if (ObjectUtil.allNotNull(pair.getKey())) {
                NumberUtil.writeExternalBoolean(out, true);
                out.writeObject(pair.getKey());
            } else {
                NumberUtil.writeExternalBoolean(out, false);
            }
            StringUtil.writeExternal(out, pair.getValue());
        }
    }
}
