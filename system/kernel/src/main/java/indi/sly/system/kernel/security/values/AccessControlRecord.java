package indi.sly.system.kernel.security.values;

public record AccessControlRecord(UserIdRecord userId, long scope, long value) {
}
