package indi.sly.subsystem.periphery.proxies.values;

import java.util.UUID;

public record CallContextRecord(UUID sessionId, CallContextProcessRecord process) {
}