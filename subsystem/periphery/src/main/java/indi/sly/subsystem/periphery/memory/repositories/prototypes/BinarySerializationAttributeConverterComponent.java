package indi.sly.subsystem.periphery.memory.repositories.prototypes;

import indi.sly.subsystem.periphery.core.prototypes.AComponent;
import indi.sly.system.common.supports.ObjectUtil;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

@Converter
public class BinarySerializationAttributeConverterComponent extends AComponent implements AttributeConverter<Object, byte[]> {
    @Override
    public byte[] convertToDatabaseColumn(Object attribute) {
        if (ObjectUtil.isNull(attribute)) {
            return null;
        } else {
            return ObjectUtil.transferToByteArray(attribute);
        }
    }

    @Override
    public Object convertToEntityAttribute(byte[] dbData) {
        if (ObjectUtil.isNull(dbData)) {
            return null;
        } else {
            return ObjectUtil.transferFromByteArray(dbData);
        }
    }
}
