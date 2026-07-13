package indi.sly.subsystem.periphery.calls.values;

import java.util.UUID;

public record ClientRequestProcessIdRecord(UUID id, long type, String secret, String verification) {
}
