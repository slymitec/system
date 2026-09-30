package indi.sly.system.kernel.core.prototypes;

import indi.sly.system.common.supports.ObjectUtil;

public interface IByteValueSupporter<T> {
    default T init(Class<T> clazz, byte[] source) {
        if (ObjectUtil.isAnyNull(clazz, source)) {
            return null;
        } else {
            return ObjectUtil.transferFromJsonByteArray(clazz, source);
        }
    }

    default byte[] flush(T value) {
        if (ObjectUtil.isAnyNull(value)) {
            return null;
        } else {
            return ObjectUtil.transferToJsonByteArray(value);
        }
    }
}
