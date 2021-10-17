package indi.sly.system.common.values;

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

import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.util.Arrays;
import java.util.Objects;
import java.util.UUID;

@JsonSerialize(using = IdentificationDefinition.IdentificationDefinitionSerializer.class)
@JsonDeserialize(using = IdentificationDefinition.IdentificationDefinitionDeserializer.class)
public final class IdentificationDefinition extends ADefinition<IdentificationDefinition> {
    private byte[] id;
    private Class<?> type;

    public byte[] getID() {
        return this.id;
    }

    public Class<?> getType() {
        return this.type;
    }

    public IdentificationDefinition() {
        this.id = UUIDUtil.writeToBytes(UUIDUtil.getEmpty());
        this.type = UUID.class;
    }

    public IdentificationDefinition(UUID id) {
        if (ObjectUtil.isAnyNull(id)) {
            throw new ConditionParametersException();
        }

        this.id = UUIDUtil.writeToBytes(id);
        this.type = UUID.class;
    }

    public IdentificationDefinition(String id) {
        if (StringUtil.isNameIllegal(id)) {
            throw new ConditionParametersException();
        }

        this.id = StringUtil.writeToBytes(id);
        this.type = String.class;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        IdentificationDefinition that = (IdentificationDefinition) o;
        return Arrays.equals(id, that.id) && type.equals(that.type);
    }

    @Override
    public int hashCode() {
        int result = Objects.hash(type);
        result = 31 * result + Arrays.hashCode(id);
        return result;
    }

    @Override
    public String toString() {
        if (this.type == UUID.class) {
            return ObjectUtil.transferToString(UUIDUtil.readFormBytes(this.id));
        } else if (this.type == String.class) {
            return StringUtil.readFormBytes(this.id);
        } else {
            return null;
        }
    }

    @Override
    public IdentificationDefinition deepClone() {
        IdentificationDefinition definition = new IdentificationDefinition();

        definition.id = ArrayUtil.copyBytes(this.id);
        definition.type = this.type;

        return definition;
    }

    @Override
    public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
        super.readExternal(in);

        this.id = NumberUtil.readExternalBytes(in);
        this.type = ClassUtil.readExternal(in);
    }

    @Override
    public void writeExternal(ObjectOutput out) throws IOException {
        super.writeExternal(out);

        NumberUtil.writeExternalBytes(out, this.id);
        ClassUtil.writeExternal(out, this.type);
    }

    public static class IdentificationDefinitionSerializer extends JsonSerializer<IdentificationDefinition> {
        @Override
        public void serialize(IdentificationDefinition value, JsonGenerator generator, SerializerProvider serializer) throws IOException {
            if (value.type == String.class) {
                generator.writeString(StringUtil.readFormBytes(value.id));
            } else if (value.type == UUID.class) {
                generator.writeObject("<" + UUIDUtil.readFormBytes(value.id) + ">");
            }
        }
    }

    public static class IdentificationDefinitionDeserializer extends JsonDeserializer<IdentificationDefinition> {
        @Override
        public IdentificationDefinition deserialize(JsonParser parser, DeserializationContext context) {
            String value;
            try {
                value = parser.getText();
            } catch (IOException ignored) {
                throw new StatusUnreadableException();
            }

            IdentificationDefinition identification;

            if (value.startsWith("<") && value.endsWith(">")) {
                if (value.length() == 38) {
                    UUID id = ObjectUtil.transferFromString(UUID.class, "\"" + value.substring(1, value.length() - 1) + "\"");
                    if (ValueUtil.isAnyNullOrEmpty(id)) {
                        throw new StatusUnreadableException();
                    }
                    identification = new IdentificationDefinition(id);
                } else {
                    throw new StatusUnreadableException();
                }
            } else if (!StringUtil.isNameIllegal(value)) {
                identification = new IdentificationDefinition(value);
            } else {
                throw new StatusUnreadableException();
            }

            return identification;
        }
    }
}
