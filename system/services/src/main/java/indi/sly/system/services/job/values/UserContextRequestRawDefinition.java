package indi.sly.system.services.job.values;

import indi.sly.system.common.values.ADefinition;

import java.util.HashMap;
import java.util.Map;

public class UserContextRequestRawDefinition extends ADefinition<UserContextRequestRawDefinition> {
    public UserContextRequestRawDefinition() {
        this.value = new HashMap<>();
    }

    private final Map<String, String> value;

    public Map<String, String> getValue() {
        return this.value;
    }
}
