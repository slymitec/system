package indi.sly.system.services.jobs.values;

import indi.sly.system.common.values.ADefinition;

public class UserContentResponseExceptionDefinition extends ADefinition<UserContentResponseExceptionDefinition> {
    private Class<?> clazz;
    private Class<?> owner;
    private String method;
    private String message;

    public Class<?> getClazz() {
        return this.clazz;
    }

    public void setClazz(Class<?> clazz) {
        this.clazz = clazz;
    }

    public Class<?> getOwner() {
        return owner;
    }

    public void setOwner(Class<?> owner) {
        this.owner = owner;
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
