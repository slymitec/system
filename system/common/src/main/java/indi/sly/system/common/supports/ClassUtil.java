package indi.sly.system.common.supports;

import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;

public abstract class ClassUtil extends org.apache.commons.lang3.ClassUtils {
	public static <T> Class<T> readExternal(ObjectInput in) throws ClassNotFoundException, IOException {
		if (ObjectUtil.isAnyNull(in)) {
			throw new NullPointerException();
		}

		@SuppressWarnings("unchecked")
		Class<T> value = (Class<T>) in.readObject();
		return value;
	}

	public static void writeExternal(ObjectOutput out, Class<?> value) throws IOException {
		if (ObjectUtil.isAnyNull(out)) {
			throw new NullPointerException();
		}

		out.writeObject(value);
	}
}
