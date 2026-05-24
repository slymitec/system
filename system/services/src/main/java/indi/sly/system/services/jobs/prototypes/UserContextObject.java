package indi.sly.system.services.jobs.prototypes;

import indi.sly.system.common.lang.StatusRelationshipErrorException;
import indi.sly.system.common.supports.ObjectUtil;
import indi.sly.system.common.supports.ValueUtil;
import indi.sly.system.kernel.core.prototypes.ADefinitionObject;
import indi.sly.system.kernel.processes.ThreadManager;
import indi.sly.system.kernel.processes.prototypes.ThreadObject;
import indi.sly.system.services.jobs.values.*;
import jakarta.inject.Named;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;

import java.util.UUID;

@Named
@Scope(value = ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class UserContextObject extends ADefinitionObject<UserContextDefinition> {
    public UUID getThreadId() {
        ThreadManager threadManager = this.coreManager.getManager(ThreadManager.class);
        if (threadManager.size() == 0) {
            throw new StatusRelationshipErrorException();
        }
        ThreadObject thread = threadManager.getCurrent();
        if (!thread.getId().equals(this.definition.getThreadId())) {
            throw new StatusRelationshipErrorException();
        }

        return this.definition.getThreadId();
    }

    public UserContentObject getContent() {
        ThreadManager threadManager = this.coreManager.getManager(ThreadManager.class);
        if (threadManager.size() == 0) {
            throw new StatusRelationshipErrorException();
        }
        ThreadObject thread = threadManager.getCurrent();
        if (!thread.getId().equals(this.definition.getThreadId())) {
            throw new StatusRelationshipErrorException();
        }

        UserContentObject userContent = this.coreManager.create(UserContentObject.class);

        userContent.setDefinition(this.definition);

        return userContent;
    }

    public ClientResponseDefinition getResponse() {
        ThreadManager threadManager = this.coreManager.getManager(ThreadManager.class);
        if (threadManager.size() == 0) {
            throw new StatusRelationshipErrorException();
        }
        ThreadObject thread = threadManager.getCurrent();
        if (!thread.getId().equals(this.definition.getThreadId())) {
            throw new StatusRelationshipErrorException();
        }

        UserContentResponseDefinition userContentResponse = this.definition.getContent().getResponse();
        ClientResponseExceptionDefinition clientResponseException = this.definition.getException();

        ClientResponseDefinition clientResponse = new ClientResponseDefinition();

        if (ValueUtil.isAnyNullOrEmpty(clientResponseException.getId())) {
            clientResponse.setContent(userContentResponse);
        } else {
            clientResponse.setException(clientResponseException);
        }

        return clientResponse;
    }
}
