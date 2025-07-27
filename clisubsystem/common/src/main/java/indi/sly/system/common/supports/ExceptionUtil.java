package indi.sly.system.common.supports;

import indi.sly.system.common.lang.AKernelException;

import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;

public abstract class ExceptionUtil {
    public static AKernelException readExternal(ObjectInput in) throws ClassNotFoundException, IOException {
        if (ObjectUtil.isAnyNull(in)) {
            throw new NullPointerException();
        }

        if (NumberUtil.readExternalBoolean(in)) {
            AKernelException value = (AKernelException) in.readObject();
            return value;
        } else {
            return null;
        }
    }

    public static void writeExternal(ObjectOutput out, AKernelException value) throws IOException {
        if (ObjectUtil.isAnyNull(out)) {
            throw new NullPointerException();
        }

        if (value == null) {
            NumberUtil.writeExternalBoolean(out, false);
        } else {
            NumberUtil.writeExternalBoolean(out, true);
            out.writeObject(value);
        }
    }
}
