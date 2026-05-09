package indi.sly.system.common.values;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import indi.sly.system.common.lang.StatusUnreadableException;
import indi.sly.system.common.supports.*;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
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

    public static class PathDefinitionSerializer extends JsonSerializer<PathDefinition> {
        @Override
        public void serialize(PathDefinition value, JsonGenerator generator, SerializerProvider serializer) throws IOException {
            String[] texts = new String[value.identifiers.size()];

            for (int i = 0; i < value.identifiers.size(); i++) {
                IdentifierDefinition identification = value.identifiers.get(i);

                if (identification.getType() == String.class) {
                    texts[i] = ObjectUtil.transferToString(StringUtil.readFormBytes(identification.getValue()));
                } else if (identification.getType() == UUID.class) {
                    texts[i] = "<" + ObjectUtil.transferToString(UUIDUtil.readFormBytes(identification.getValue())) + ">";
                }
            }

            generator.writeString("\\" + String.join("\\", texts));
        }
    }

    public static class PathDefinitionDeserializer extends JsonDeserializer<PathDefinition> {
        @Override
        public PathDefinition deserialize(JsonParser parser, DeserializationContext context) {
            String value;
            try {
                value = parser.getText();
            } catch (IOException ignored) {
                throw new StatusUnreadableException();
            }

            String[] texts = value.split("\\\\");

            List<IdentifierDefinition> identifications = new ArrayList<>();

            for (String text : texts) {
                IdentifierDefinition identification;
                if (text.startsWith("<") && text.endsWith(">")) {
                    UUID id = ObjectUtil.transferFromString(UUID.class, text.substring(1, text.length() - 1));
                    if (ValueUtil.isAnyNullOrEmpty(id)) {
                        throw new StatusUnreadableException();
                    }

                    identification = new IdentifierDefinition(id);
                } else {
                    String name = ObjectUtil.transferFromString(String.class, text);

                    if (!StringUtil.isNameIllegal(name)) {
                        throw new StatusUnreadableException();
                    }

                    identification = new IdentifierDefinition(name);
                }
                identifications.add(identification);
            }

            return new PathDefinition(identifications);
        }
    }
}