package indi.sly.system.common.values;

import indi.sly.system.common.lang.ConditionParametersException;
import indi.sly.system.common.lang.StatusUnexpectedException;
import indi.sly.system.common.lang.StatusUnreadableException;
import indi.sly.system.common.supports.*;
import jakarta.annotation.Nonnull;
import tools.jackson.core.JacksonException;
import tools.jackson.core.JsonGenerator;
import tools.jackson.core.JsonParser;
import tools.jackson.databind.DeserializationContext;
import tools.jackson.databind.SerializationContext;
import tools.jackson.databind.ValueDeserializer;
import tools.jackson.databind.ValueSerializer;
import tools.jackson.databind.annotation.JsonDeserialize;
import tools.jackson.databind.annotation.JsonSerialize;
import tools.jackson.databind.jsontype.TypeDeserializer;
import tools.jackson.databind.jsontype.TypeSerializer;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@JsonSerialize(using = IdentifierRecord.IdentifierSerializer.class)
@JsonDeserialize(using = IdentifierRecord.IdentifierDeserializer.class)
public record IdentifierRecord(byte[] value, Class<?> type) {
    public IdentifierRecord() {
        this(UUIDUtil.writeToBytes(UUIDUtil.getEmpty()), UUID.class);
    }

    public IdentifierRecord(UUID value) {
        if (ObjectUtil.isAnyNull(value)) {
            throw new ConditionParametersException();
        }

        this(UUIDUtil.writeToBytes(value), UUID.class);
    }

    public IdentifierRecord(String value) {
        if (StringUtil.isNameIllegal(value)) {
            throw new ConditionParametersException();
        }

        this(StringUtil.writeToBytes(value), String.class);
    }

    @Override
    @Nonnull
    public String toString() {
        if (this.type == UUID.class) {
            return "<" + UUIDUtil.toString(UUIDUtil.readFormBytes(this.value())) + ">";
        } else if (this.type == String.class) {
            return StringUtil.readFormBytes(this.value);
        } else {
            throw new StatusUnexpectedException();
        }
    }

    public static class IdentifierSerializer extends ValueSerializer<IdentifierRecord> {
        @Override
        public void serializeWithType(IdentifierRecord value, JsonGenerator generator, SerializationContext ctxt, TypeSerializer typeSer) throws JacksonException {
            this.serialize(value, generator, ctxt);
        }

        @Override
        public void serialize(IdentifierRecord value, JsonGenerator generator, SerializationContext ctxt) throws JacksonException {
            if (value.type() == String.class) {
                generator.writeString(StringUtil.readFormBytes(value.value()));
            } else if (value.type() == UUID.class) {
                generator.writeString("<" + UUIDUtil.toString(UUIDUtil.readFormBytes(value.value())) + ">");
            } else {
                throw new StatusUnexpectedException();
            }
        }
    }

    public static class IdentifierDeserializer extends ValueDeserializer<IdentifierRecord> {
        @Override
        public Object deserializeWithType(JsonParser parser, DeserializationContext context, TypeDeserializer typeDeserializer) throws JacksonException {
            return this.deserialize(parser, context);
        }

        @Override
        public IdentifierRecord deserialize(JsonParser parser, DeserializationContext context) throws JacksonException {
            String value = parser.getString();

            if (ValueUtil.isAnyNullOrEmpty(value)) {
                return new IdentifierRecord();
            } else if (value.startsWith("<") && value.endsWith(">")) {
                UUID id;
                try {
                    id = UUID.fromString(value.substring(1, value.length() - 1));
                } catch (Exception e) {
                    id = null;
                }
                if (ValueUtil.isAnyNullOrEmpty(id)) {
                    throw new StatusUnreadableException();
                }

                return new IdentifierRecord(id);
            } else if (!StringUtil.isNameIllegal(value)) {
                return new IdentifierRecord(value);
            } else {
                throw new StatusUnreadableException();
            }
        }
    }
}
