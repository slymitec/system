package indi.sly.system.common.values;

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

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

@JsonSerialize(using = PathDefinition.PathDefinitionSerializer.class)
@JsonDeserialize(using = PathDefinition.PathDefinitionDeserializer.class)
public class PathDefinition extends ADefinition {
    public PathDefinition(List<IdentifierDefinition> identifiers) {
        this.identifiers = new ArrayList<>();

        if (ObjectUtil.allNotNull(identifiers)) {
            this.identifiers.addAll(identifiers);
        }
    }

    public PathDefinition(PathDefinition path, IdentifierDefinition identifier) {
        this.identifiers = new ArrayList<>();

        this.identifiers.addAll(path.identifiers);

        if (ObjectUtil.allNotNull(identifier)) {
            this.identifiers.add(identifier);
        }
    }

    private final List<IdentifierDefinition> identifiers;

    public List<IdentifierDefinition> get() {
        return CollectionUtil.unmodifiable(this.identifiers);
    }

    public static class PathDefinitionSerializer extends ValueSerializer<PathDefinition> {
        @Override
        public void serializeWithType(PathDefinition value, JsonGenerator generator, SerializationContext ctxt, TypeSerializer typeSer) throws JacksonException {
            this.serialize(value, generator, ctxt);
        }

        @Override
        public void serialize(PathDefinition value, JsonGenerator generator, SerializationContext ctxt) throws JacksonException {
            String[] texts = new String[value.identifiers.size()];

            for (int i = 0; i < value.identifiers.size(); i++) {
                IdentifierDefinition identification = value.identifiers.get(i);

                if (identification.getType() == String.class) {
                    texts[i] = StringUtil.readFormBytes(identification.getValue());
                } else if (identification.getType() == UUID.class) {
                    texts[i] = "<" + UUIDUtil.toString(UUIDUtil.readFormBytes(identification.getValue())) + ">";
                }
            }

            generator.writeString("\\" + String.join("\\", texts));
        }
    }

    public static class PathDefinitionDeserializer extends ValueDeserializer<PathDefinition> {
        @Override
        public Object deserializeWithType(JsonParser parser, DeserializationContext context, TypeDeserializer typeDeserializer) throws JacksonException {
            return this.deserialize(parser, context);
        }

        @Override
        public PathDefinition deserialize(JsonParser parser, DeserializationContext context) throws JacksonException {
            String value;
            value = parser.getString();

            String[] texts = value.split("\\\\");

            List<IdentifierDefinition> identifications = new ArrayList<>();

            for (String text : texts) {
                IdentifierDefinition identification;
                if (text.isEmpty()) {
                    continue;
                } else if (text.startsWith("<") && text.endsWith(">")) {
                    UUID id = UUID.fromString(text.substring(1, text.length() - 1));
                    if (ValueUtil.isAnyNullOrEmpty(id)) {
                        throw new StatusUnreadableException();
                    }

                    identification = new IdentifierDefinition(id);
                } else {
                    if (StringUtil.isNameIllegal(text)) {
                        throw new StatusUnreadableException();
                    }

                    identification = new IdentifierDefinition(text);
                }
                identifications.add(identification);
            }

            return new PathDefinition(identifications);
        }
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        PathDefinition that = (PathDefinition) o;
        return Objects.equals(identifiers, that.identifiers);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(identifiers);
    }
}