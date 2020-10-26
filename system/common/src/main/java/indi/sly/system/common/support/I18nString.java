package indi.sly.system.common.support;

import java.io.Serializable;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Locale;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

public final class I18nString
		implements IDeepCloneable<I18nString>, Iterable<Entry<Locale, String>>, Map<Locale, String>, Serializable {
	private static final long serialVersionUID = 6797139230552390820L;

	protected final Map<Locale, String> text;

	public I18nString() {
		this.text = new HashMap<>();
	}

	@Override
	public void clear() {
		this.text.clear();
	}

	@Override
	public boolean containsKey(Object key) {
		return this.text.containsKey(key);
	}

	@Override
	public boolean containsValue(Object value) {
		return this.text.containsValue(value);
	}

	@Override
	public I18nString deepClone() {
		I18nString newObject = new I18nString();

		for (Entry<Locale, String> pair : this.text.entrySet()) {
			newObject.text.put(pair.getKey(), pair.getValue());
		}

		return newObject;
	}

	@Override
	public Set<Entry<Locale, String>> entrySet() {
		return this.text.entrySet();
	}

	@Override
	public boolean equals(Object anObject) {
		if (this == anObject) {
			return true;
		}
		if (!(anObject instanceof I18nString)) {
			return false;
		}
		I18nString anotherI18nString = (I18nString) anObject;
		if (anotherI18nString.text.size() != this.text.size()) {
			return false;
		}
		for (Entry<Locale, String> pair : anotherI18nString.text.entrySet()) {
			if (!this.text.containsKey(pair.getKey())) {
				return false;
			} else {
				if (!this.text.get(pair.getKey()).equals(pair.getValue())) {
					return false;
				}
			}
		}
		return true;
	}

	@Override
	public String get(Object key) {
		return this.text.getOrDefault(key, null);
	}

	@Override
	public int hashCode() {
		int hash = Integer.hashCode(this.text.size());

		for (Entry<Locale, String> pair : this.text.entrySet()) {
			hash = hash ^ pair.getKey().hashCode() ^ pair.getValue().hashCode();
		}
		return hash;
	}

	@Override
	public boolean isEmpty() {
		return this.text.isEmpty();
	}

	@Override
	public Iterator<java.util.Map.Entry<Locale, String>> iterator() {
		return this.text.entrySet().iterator();
	}

	@Override
	public Set<Locale> keySet() {
		return this.text.keySet();
	}

	@Override
	public String put(Locale key, String value) {

		if (key == null) {
			throw new IllegalArgumentException();
		} else if (value == null) {
			this.text.remove(key);
			return null;
		} else {
			return this.text.put(key, value);
		}
	}

	@Override
	public void putAll(Map<? extends Locale, ? extends String> m) {
		this.text.putAll(m);
	}

	@Override
	public String remove(Object key) {
		return this.text.remove(key);
	}

	@Override
	public int size() {
		return this.text.size();
	}

	@Override
	public Collection<String> values() {
		return this.text.values();
	}
}
