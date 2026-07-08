package indi.sly.system.services.jobs.values;

import java.util.UUID;

public record ClientRequestProcessIdRecord(UUID id, long type, String secret, String verification) {
}
