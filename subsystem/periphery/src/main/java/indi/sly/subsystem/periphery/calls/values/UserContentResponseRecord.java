package indi.sly.subsystem.periphery.calls.values;

import java.util.UUID;

public record UserContentResponseRecord(UUID id, String clazz, String value) {
}
