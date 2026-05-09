package indi.sly.system.kernel.objects.values;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import indi.sly.system.common.lang.ConditionParametersException;
import indi.sly.system.common.lang.StatusUnreadableException;
import indi.sly.system.common.supports.*;
import indi.sly.system.common.values.ADefinition;

import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.util.Arrays;
import java.util.Objects;
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

    public static class InfoWildcardDefinitionSerializer extends JsonSerializer<InfoWildcardDefinition> {
        @Override
        public void serialize(InfoWildcardDefinition value, JsonGenerator generator, SerializerProvider serializer) throws IOException {
            if (value.type == String.class) {
                generator.writeString(StringUtil.readFormBytes(value.value));
            } else if (value.type == UUID.class) {
                generator.writeObject("<" + UUIDUtil.readFormBytes(value.value) + ">");
            }
        }
    }

    public static class InfoWildcardDefinitionDeserializer extends JsonDeserializer<InfoWildcardDefinition> {
        @Override
        public InfoWildcardDefinition deserialize(JsonParser parser, DeserializationContext context) {
            String value;
            try {
                value = parser.getText();
            } catch (IOException ignored) {
                throw new StatusUnreadableException();
            }

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
