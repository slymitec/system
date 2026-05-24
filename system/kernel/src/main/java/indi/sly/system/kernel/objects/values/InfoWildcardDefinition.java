package indi.sly.system.kernel.objects.values;

import indi.sly.system.common.lang.ConditionParametersException;
import indi.sly.system.common.lang.StatusUnreadableException;
import indi.sly.system.common.supports.*;
import indi.sly.system.common.values.ADefinition;
import indi.sly.system.common.values.PathDefinition;
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

@JsonSerialize(using = InfoWildcardDefinition.InfoWildcardDefinitionSerializer.class)
@JsonDeserialize(using = InfoWildcardDefinition.InfoWildcardDefinitionDeserializer.class)
public class InfoWildcardDefinition extends ADefinition {
    private final byte[] value;
    private final Class<?> type;
    private final boolean fuzzy;

    public byte[] getValue() {
        return value;
    }

    public Class<?> getType() {
        return type;
    }

    public boolean isFuzzy() {
        return fuzzy;
    }

    public InfoWildcardDefinition() {
        this.value = UUIDUtil.writeToBytes(UUIDUtil.getEmpty());
        this.type = UUID.class;
        this.fuzzy = true;
    }

    public InfoWildcardDefinition(UUID value) {
        if (ObjectUtil.isAnyNull(value)) {
            throw new ConditionParametersException();
        }

        this.value = UUIDUtil.writeToBytes(value);
        this.type = UUID.class;
        this.fuzzy = ValueUtil.isAnyNullOrEmpty(value);
    }

    public InfoWildcardDefinition(String value) {
        if (StringUtil.isNameIllegalButWildcard(value)) {
            throw new ConditionParametersException();
        }

        this.value = StringUtil.writeToBytes(value);
        this.type = String.class;
        this.fuzzy = StringUtil.isNameIllegal(value);
    }

    public static class InfoWildcardDefinitionSerializer extends ValueSerializer<InfoWildcardDefinition> {
        @Override
        public void serializeWithType(InfoWildcardDefinition value, JsonGenerator generator, SerializationContext ctxt, TypeSerializer typeSer) throws JacksonException {
            this.serialize(value, generator, ctxt);
        }

        @Override
        public void serialize(InfoWildcardDefinition value, JsonGenerator generator, SerializationContext ctxt) throws JacksonException {
            if (value.type == String.class) {
                generator.writeString(StringUtil.readFormBytes(value.value));
            } else if (value.type == UUID.class) {
                generator.writeString("<" + UUIDUtil.toString(UUIDUtil.readFormBytes(value.value)) + ">");
            }
        }
    }

    public static class InfoWildcardDefinitionDeserializer extends ValueDeserializer<InfoWildcardDefinition> {
        @Override
        public Object deserializeWithType(JsonParser parser, DeserializationContext context, TypeDeserializer typeDeserializer) throws JacksonException {
            return this.deserialize(parser, context);
        }

        @Override
        public InfoWildcardDefinition deserialize(JsonParser parser, DeserializationContext context) throws JacksonException {
            String value = parser.getString();

            InfoWildcardDefinition infoWildcard;

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

                infoWildcard = new InfoWildcardDefinition(id);
            } else if (!StringUtil.isNameIllegalButWildcard(value)) {
                infoWildcard = new InfoWildcardDefinition(value);
            } else {
                throw new StatusUnreadableException();
            }

            return infoWildcard;
        }
    }
}
