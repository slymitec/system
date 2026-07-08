package indi.sly.system.services.jobs.values;

import indi.sly.system.services.jobs.lang.TaskInitializerRunMethodConsumer;

public record TaskInitializerRunRecord(TaskInitializerRunMethodConsumer method, long transaction) {
}
