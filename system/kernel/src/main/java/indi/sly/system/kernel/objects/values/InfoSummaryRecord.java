package indi.sly.system.kernel.objects.values;

import org.jspecify.annotations.Nullable;

import java.util.UUID;

public record InfoSummaryRecord(UUID id, UUID type, @Nullable String name) {
}
