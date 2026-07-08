package indi.sly.system.kernel.objects.values;

import indi.sly.system.common.lang.ConditionParametersException;
import indi.sly.system.common.lang.StatusUnreadableException;
import indi.sly.system.common.supports.*;
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

import java.util.UUID;

@JsonSerialize(using = InfoWildcardRecord.InfoWildcardDefinitionSerializer.class)
@JsonDeserialize(using = InfoWildcardRecord.InfoWildcardDefinitionDeserializer.class)
public record InfoWildcardRecord(byte[] value, Class<?> type, boolean fuzzy) {
    public InfoWildcardRecord() {
        this(UUIDUtil.writeToBytes(UUIDUtil.getEmpty()), UUID.class, true);
    }

    public InfoWildcardRecord(UUID value) {
        if (ObjectUtil.isAnyNull(value)) {
            throw new ConditionParametersException();
        }

        this(UUIDUtil.writeToBytes(value), UUID.class, ValueUtil.isAnyNullOrEmpty(value));
    }

    public InfoWildcardRecord(String value) {
        if (StringUtil.isNameIllegalButWildcard(value)) {
            throw new ConditionParametersException();
        }

        this(StringUtil.writeToBytes(value), String.class, StringUtil.isNameIllegal(value));
    }

    public static class InfoWildcardDefinitionSerializer extends ValueSerializer<InfoWildcardRecord> {
        @Override
        public void serializeWithType(InfoWildcardRecord value, JsonGenerator generator, SerializationContext ctxt, TypeSerializer typeSer) throws JacksonException {
            this.serialize(value, generator, ctxt);
        }

        @Override
        public void serialize(InfoWildcardRecord value, JsonGenerator generator, SerializationContext ctxt) throws JacksonException {
            if (value.type == String.class) {
                generator.writeString(StringUtil.readFormBytes(value.value));
            } else if (value.type == UUID.class) {
                generator.writeString("<" + UUIDUtil.toString(UUIDUtil.readFormBytes(value.value)) + ">");
            }
        }
    }

    public static class InfoWildcardDefinitionDeserializer extends ValueDeserializer<InfoWildcardRecord> {
        @Override
        public Object deserializeWithType(JsonParser parser, DeserializationContext context, TypeDeserializer typeDeserializer) throws JacksonException {
            return this.deserialize(parser, context);
        }

        @Override
        public InfoWildcardRecord deserialize(JsonParser parser, DeserializationContext context) throws JacksonException {
            String value = parser.getString();

            InfoWildcardRecord infoWildcard;

            if (value.startsWith("<") && value.endsWith(">")) {
                UUID id;
                try {
                    id = UUID.fromString(value.substring(1, value.length() - 1));
                } catch (Exception e) {
                    id = null;
                }
                if (ValueUtil.isAnyNullOrEmpty(id)) {
                    throw new StatusUnreadableException();
                }

                infoWildcard = new InfoWildcardRecord(id);
            } else if (!StringUtil.isNameIllegalButWildcard(value)) {
                infoWildcard = new InfoWildcardRecord(value);
            } else {
                throw new StatusUnreadableException();
            }

            return infoWildcard;
        }
    }
}
