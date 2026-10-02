package indi.sly.system.services.jobs.values;

import java.util.UUID;

public record CallContextProcessRecord(UUID processId, long processType, String secret, String verification) {
}
