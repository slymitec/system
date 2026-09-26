package indi.sly.subsystem.periphery.proxies.values;

import java.util.UUID;

public record CallContextRecord(UUID processId, long processType, String secret, String verification) {
}
