package indi.sly.system.common.utility;

import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;

public class ClassUtils extends org.apache.commons.lang3.ClassUtils {
	public static <T> Class<T> readExternal(ObjectInput in) throws ClassNotFoundException, IOException {
		if (ObjectUtils.isAnyNull(in)) {
			throw new NullPointerException();
		}

		@SuppressWarnings("unchecked")
		Class<T> value = (Class<T>) in.readObject();
		return value;
	}

	public static void writeExternal(ObjectOutput out, Class<?> value) throws IOException {
		if (ObjectUtils.isAnyNull(out)) {
			throw new NullPointerException();
		}

		out.writeObject(value);
	}
}
