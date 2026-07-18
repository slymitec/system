package indi.sly.subsystem.periphery.proxies.values;

import java.util.UUID;

public record ProcedureProcessRecord(UUID id, long type, String secret, String verification) {
}
