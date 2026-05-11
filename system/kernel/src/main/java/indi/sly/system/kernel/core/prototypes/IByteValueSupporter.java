package indi.sly.system.kernel.core.prototypes;

import indi.sly.system.common.supports.ObjectUtil;

public interface IByteValueSupporter<T> {
    default T init(byte[] source) {
        if (ObjectUtil.isAnyNull(source)) {
            return null;
        } else {
            return ObjectUtil.transferFromByteArray(source);
        }
    }

    default byte[] flush(T value) {
        if (ObjectUtil.isAnyNull(value)) {
            return null;
        } else {
            return ObjectUtil.transferToByteArray(value);
        }
    }
}
