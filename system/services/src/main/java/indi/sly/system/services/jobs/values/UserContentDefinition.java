package indi.sly.system.services.jobs.values;

import indi.sly.system.common.values.ADefinition;

public class UserContentDefinition extends ADefinition {
    private UserContentRequestRecord request;
    private UserContentResponseRecord response;

    public UserContentRequestRecord getRequest() {
        return request;
    }

    public void setRequest(UserContentRequestRecord request) {
        this.request = request;
    }

    public UserContentResponseRecord getResponse() {
        return response;
    }

    public void setResponse(UserContentResponseRecord response) {
        this.response = response;
    }
}
