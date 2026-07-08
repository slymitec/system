package indi.sly.system.services.jobs.prototypes;

import indi.sly.system.common.lang.StatusRelationshipErrorException;
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

    public ClientResponseRecord getResponse() {
        ThreadManager threadManager = this.coreManager.getManager(ThreadManager.class);
        if (threadManager.size() == 0) {
            throw new StatusRelationshipErrorException();
        }
        ThreadObject thread = threadManager.getCurrent();
        if (!thread.getId().equals(this.definition.getThreadId())) {
            throw new StatusRelationshipErrorException();
        }

        UserContentResponseRecord userContentResponse = this.definition.getContent().getResponse();
        ClientResponseExceptionRecord clientResponseException = this.definition.getException();

        ClientResponseRecord clientResponse;

        if (ValueUtil.isAnyNullOrEmpty(clientResponseException.id())) {
            clientResponse = new ClientResponseRecord(userContentResponse);
        } else {
            clientResponse = new ClientResponseRecord(clientResponseException);
        }

        return clientResponse;
    }
}
