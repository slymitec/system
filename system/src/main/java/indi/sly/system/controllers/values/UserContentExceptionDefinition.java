package indi.sly.system.controllers.values;

import indi.sly.system.common.values.ADefinition;

public class UserContentExceptionDefinition extends ADefinition<UserContentExceptionDefinition> {
    private String name;
    private String clazz;
    private String method;
    private String message;

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getClazz() {
        return this.clazz;
    }

    public void setClazz(String clazz) {
        this.clazz = clazz;
    }

    public String getMethod() {
        return this.method;
    }

    public void setMethod(String method) {
        this.method = method;
    }

    public String getMessage() {
        return this.message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
