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
import java.util.UUID;

@JsonSerialize(using = PathRecord.PathDefinitionSerializer.class)
@JsonDeserialize(using = PathRecord.PathDefinitionDeserializer.class)
public record PathRecord(List<IdentifierRecord> identifiers) {
    public PathRecord(List<IdentifierRecord> identifiers) {
        this.identifiers = new ArrayList<>();

        if (ObjectUtil.allNotNull(identifiers)) {
            this.identifiers.addAll(identifiers);
        }
    }

    public PathRecord(PathRecord path, IdentifierRecord identifier) {
        this(new ArrayList<>());

        if (ObjectUtil.allNotNull(path)) {
            this.identifiers.addAll(path.identifiers);
        }
        if (ObjectUtil.allNotNull(identifier)) {
            this.identifiers.add(identifier);
        }
    }

    @Override
    public List<IdentifierRecord> identifiers() {
        return CollectionUtil.unmodifiable(this.identifiers);
    }

    public static class PathDefinitionSerializer extends ValueSerializer<PathRecord> {
        @Override
        public void serializeWithType(PathRecord value, JsonGenerator generator, SerializationContext ctxt, TypeSerializer typeSer) throws JacksonException {
            this.serialize(value, generator, ctxt);
        }

        @Override
        public void serialize(PathRecord value, JsonGenerator generator, SerializationContext ctxt) throws JacksonException {
            String[] texts = new String[value.identifiers.size()];

            for (int i = 0; i < value.identifiers.size(); i++) {
                IdentifierRecord identification = value.identifiers.get(i);

                if (identification.type() == String.class) {
                    texts[i] = StringUtil.readFormBytes(identification.value());
                } else if (identification.type() == UUID.class) {
                    texts[i] = "<" + UUIDUtil.toString(UUIDUtil.readFormBytes(identification.value())) + ">";
                }
            }

            generator.writeString("\\" + String.join("\\", texts));
        }
    }

    public static class PathDefinitionDeserializer extends ValueDeserializer<PathRecord> {
        @Override
        public Object deserializeWithType(JsonParser parser, DeserializationContext context, TypeDeserializer typeDeserializer) throws JacksonException {
            return this.deserialize(parser, context);
        }

        @Override
        public PathRecord deserialize(JsonParser parser, DeserializationContext context) throws JacksonException {
            String[] values = parser.getString().split("\\\\");

            List<IdentifierRecord> identifications = new ArrayList<>();

            for (String value : values) {
                IdentifierRecord identification;
                if (value.isEmpty()) {
                    continue;
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

                    identification = new IdentifierRecord(id);
                } else if (!StringUtil.isNameIllegal(value)) {
                    identification = new IdentifierRecord(value);
                } else {
                    throw new StatusUnreadableException();
                }

                identifications.add(identification);
            }

            return new PathRecord(identifications);
        }
    }
}