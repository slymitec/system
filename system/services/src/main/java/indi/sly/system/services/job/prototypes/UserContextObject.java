package indi.sly.system.services.job.prototypes;

import indi.sly.system.common.lang.StatusRelationshipErrorException;
import indi.sly.system.kernel.core.prototypes.AIndependentValueProcessObject;
import indi.sly.system.kernel.processes.ThreadManager;
import indi.sly.system.kernel.processes.prototypes.ThreadObject;
import indi.sly.system.services.job.values.UserContentDefinition;
import indi.sly.system.services.job.values.UserContentResponseDefinition;
import indi.sly.system.services.job.values.UserContextDefinition;
import jakarta.inject.Named;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;

import java.util.UUID;

@Named
@Scope(value = ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class UserContextObject extends AIndependentValueProcessObject<UserContextDefinition> {
    public UUID getThreadID() {
        this.init();

        ThreadManager threadManager = this.factoryManager.getManager(ThreadManager.class);
        if (threadManager.size() == 0) {
            throw new StatusRelationshipErrorException();
        }
        ThreadObject thread = threadManager.getCurrent();
        if (!thread.getID().equals(this.value.getThreadID())) {
            throw new StatusRelationshipErrorException();
        }

        return this.value.getThreadID();
    }

    public UserContentObject getContent() {
        this.init();

        ThreadManager threadManager = this.factoryManager.getManager(ThreadManager.class);
        if (threadManager.size() == 0) {
            throw new StatusRelationshipErrorException();
        }
        ThreadObject thread = threadManager.getCurrent();
        if (!thread.getID().equals(this.value.getThreadID())) {
            throw new StatusRelationshipErrorException();
        }

        UserContentObject userContent = this.factoryManager.create(UserContentObject.class);

        userContent.setParent(this);
        userContent.setSource(() -> this.value, (source) -> {
        });

        return userContent;
    }

    public UserContentResponseDefinition getResponse() {
        this.init();

        ThreadManager threadManager = this.factoryManager.getManager(ThreadManager.class);
        if (threadManager.size() == 0) {
            throw new StatusRelationshipErrorException();
        }
        ThreadObject thread = threadManager.getCurrent();
        if (!thread.getID().equals(this.value.getThreadID())) {
            throw new StatusRelationshipErrorException();
        }

        UserContentDefinition userContent = this.value.getContent();

        return userContent.getResponse();
    }
}
