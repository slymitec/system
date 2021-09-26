package indi.sly.system.controllers.values;

import indi.sly.system.common.values.ADefinition;

public class UserContentDefinition extends ADefinition<UserContentDefinition> {
    public UserContentDefinition() {
        this.request = new UserContentRequestDefinition();
        this.response = new UserContentResponseDefinition();
    }

    private final UserContentRequestDefinition request;
    private final UserContentResponseDefinition response;

    public UserContentRequestDefinition getRequest() {
        return this.request;
    }

    public UserContentResponseDefinition getResponse() {
        return this.response;
    }
}
