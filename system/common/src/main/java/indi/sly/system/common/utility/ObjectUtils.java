package indi.sly.system.common.utility;

import indi.sly.system.common.exceptions.StatusRelationshipErrorException;
import indi.sly.system.common.exceptions.StatusUnexpectedException;
import indi.sly.system.common.support.ISerializable;

import java.io.*;

public class ObjectUtils extends org.apache.commons.lang3.ObjectUtils {
    public static boolean isAnyNull(final Object... values) {
        return !ObjectUtils.allNotNull(values);
    }

    public static <T extends ISerializable> T readExternal(ObjectInput in) throws ClassNotFoundException, IOException {
        if (ObjectUtils.isAnyNull(in)) {
            throw new NullPointerException();
        }

        if (NumberUtils.readExternalBoolean(in)) {
            @SuppressWarnings("unchecked")
            T object = (T) in.readObject();
            return object;
        } else {
            return null;
        }
    }

    public static <T extends ISerializable> void writeExternal(ObjectOutput out, T value) throws IOException {
        if (ObjectUtils.isAnyNull(out)) {
            throw new NullPointerException();
        }

        if (value == null) {
            NumberUtils.writeExternalBoolean(out, false);
        } else {
            NumberUtils.writeExternalBoolean(out, true);
            out.writeObject(value);
        }
    }

    public static <T> boolean containObject(Class<T> requiredType) {
        String[] objectNames = SpringUtils.getApplicationContext().getBeanNamesForType(requiredType);
        if (objectNames.length == 0) {
            return false;
        } else {
            return true;
        }
    }

    public static byte[] transferToByteArray(Object object) {
        if (ObjectUtils.isAnyNull(object)) {
            return null;
        }

        byte[] stream;

        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        try {
            ObjectOutputStream objectOutputStream = new ObjectOutputStream(byteArrayOutputStream);
            objectOutputStream.writeObject(object);
            objectOutputStream.flush();
            stream = byteArrayOutputStream.toByteArray();
            objectOutputStream.close();
            objectOutputStream.close();
        } catch (IOException ex) {
            throw new StatusUnexpectedException();
        }

        return stream;
    }

    @SuppressWarnings("unchecked")
    public static <T> T transferFromByteArray(byte[] stream) {
        if (ObjectUtils.isAnyNull(stream)) {
            return null;
        }

        Object object;

        try {
            ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(stream);
            ObjectInputStream outInputStream = new ObjectInputStream(byteArrayInputStream);
            object = outInputStream.readObject();
            outInputStream.close();
            byteArrayInputStream.close();
        } catch (IOException | ClassNotFoundException ex) {
            throw new StatusUnexpectedException();
        }

        if (ObjectUtils.isAnyNull(object)) {
            throw new StatusUnexpectedException();
        }

        try {
            return (T) object;
        } catch (ClassCastException e) {
            throw new StatusRelationshipErrorException();
        }
    }
}