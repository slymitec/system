package indi.sly.subsystem.periphery.proxies.instances.security.values;

public record AccessControlRecord(UserIdRecord userId, long scope, long value) {
}
