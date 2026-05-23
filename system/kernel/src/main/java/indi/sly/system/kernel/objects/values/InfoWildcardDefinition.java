package indi.sly.system.kernel.objects.values;

import indi.sly.system.common.lang.ConditionParametersException;
import indi.sly.system.common.lang.StatusUnreadableException;
import indi.sly.system.common.supports.*;
import indi.sly.system.common.values.ADefinition;
import tools.jackson.core.JacksonException;
import tools.jackson.core.JsonGenerator;
import tools.jackson.core.JsonParser;
import tools.jackson.databind.DeserializationContext;
import tools.jackson.databind.SerializationContext;
import tools.jackson.databind.ValueDeserializer;
import tools.jackson.databind.ValueSerializer;
import tools.jackson.databind.annotation.JsonDeserialize;
import tools.jackson.databind.annotation.JsonSerialize;

import java.util.UUID;

@JsonSerialize(using = InfoWildcardDefinition.InfoWildcardDefinitionSerializer.class)
@JsonDeserialize(using = InfoWildcardDefinition.InfoWildcardDefinitionDeserializer.class)
public class InfoWildcardDefinition extends ADefinition {
    private byte[] value;
    private Class<?> type;
    private boolean fuzzy;

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
        public void serialize(InfoWildcardDefinition value, JsonGenerator generator, SerializationContext ctxt) throws JacksonException {
            if (value.type == String.class) {
                generator.writeString(StringUtil.readFormBytes(value.value));
            } else if (value.type == UUID.class) {
                generator.writeString("<" + UUIDUtil.readFormBytes(value.value) + ">");
            }
        }
    }

    public static class InfoWildcardDefinitionDeserializer extends ValueDeserializer<InfoWildcardDefinition> {
        @Override
        public InfoWildcardDefinition deserialize(JsonParser parser, DeserializationContext context) throws JacksonException {
            String value;
            value = parser.getString();

            InfoWildcardDefinition infoWildcard;

            if (value.startsWith("<") && value.endsWith(">")) {
                if (value.length() == 38) {
                    UUID id = ObjectUtil.transferFromString(UUID.class, "\"" + value.substring(1, value.length() - 1) + "\"");
                    if (ValueUtil.isAnyNullOrEmpty(id)) {
                        throw new StatusUnreadableException();
                    }
                    infoWildcard = new InfoWildcardDefinition(id);
                } else {
                    throw new StatusUnreadableException();
                }
            } else if (!StringUtil.isNameIllegalButWildcard(value)) {
                infoWildcard = new InfoWildcardDefinition(value);
            } else {
                throw new StatusUnreadableException();
            }

            return infoWildcard;
        }
    }
}
