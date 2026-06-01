package indi.sly.system.services.jobs.instances.prototypes.processors.security;

import indi.sly.system.common.lang.ConditionParametersException;
import indi.sly.system.common.supports.ClassUtil;
import indi.sly.system.common.supports.ObjectUtil;
import indi.sly.system.common.values.IdentifierDefinition;
import indi.sly.system.kernel.objects.ObjectManager;
import indi.sly.system.kernel.security.UserManager;
import indi.sly.system.kernel.security.prototypes.*;
import indi.sly.system.services.core.values.TransactionType;
import indi.sly.system.services.jobs.instances.prototypes.processors.ATaskInitializer;
import indi.sly.system.services.jobs.lang.TaskRunConsumer;
import indi.sly.system.services.jobs.prototypes.TaskContentObject;
import indi.sly.system.services.jobs.values.HandleContextDefinition;
import indi.sly.system.services.jobs.values.TaskDefinition;
import jakarta.inject.Named;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

@Named
@Scope(value = ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class AccountObjectTaskInitializer extends ATaskInitializer {
    public AccountObjectTaskInitializer() {
        this.cacheableObjectFunction = (handle) -> this.coreManager.getManager(ObjectManager.class).getFactory().rebuildInfo(handle);

        this.register("getId", this::getId, TransactionType.INDEPENDENCE);
        this.register("getName", this::getName, TransactionType.INDEPENDENCE);
        this.register("getToken", this::getToken, TransactionType.INDEPENDENCE);
        this.register("getPassword", this::getPassword, TransactionType.INDEPENDENCE);
        this.register("setPassword", this::setPassword, TransactionType.INDEPENDENCE);
        this.register("getGroups", this::getGroups, TransactionType.INDEPENDENCE);
        this.register("setGroups", this::setGroups, TransactionType.INDEPENDENCE);
        this.register("getToken", this::getToken, TransactionType.INDEPENDENCE);
        this.register("getSessions", this::getSessions, TransactionType.INDEPENDENCE);
    }

    @Override
    public void start(TaskDefinition task) {
    }

    @Override
    public void finish(TaskDefinition task) {
    }

    private void getId(TaskRunConsumer run, TaskContentObject content) {
        AccountObject account = content.getCacheableObject();

        content.setResult(account.getId());
    }

    private void getName(TaskRunConsumer run, TaskContentObject content) {
        AccountObject account = content.getCacheableObject();

        content.setResult(account.getName());
    }

    private void getPassword(TaskRunConsumer run, TaskContentObject content) {
        AccountObject account = content.getCacheableObject();

        content.setResult(account.getPassword());
    }

    private void setPassword(TaskRunConsumer run, TaskContentObject content) {
        AccountObject account = content.getCacheableObject();

        List<String> parameters = content.getParameters();

        if (parameters.isEmpty()) {
            throw new ConditionParametersException();
        }

        String password = ObjectUtil.transferFromString(String.class, parameters.getFirst());

        account.setPassword(password);
    }

    private void getGroups(TaskRunConsumer run, TaskContentObject content) {
        AccountObject account = content.getCacheableObject();

        Set<GroupObject> groups = account.getGroups();

        Set<HandleContextDefinition> handleContexts = new HashSet<>();

        for (GroupObject group : groups) {
            UUID handle = group.cache();

            HandleContextDefinition handleContext = new HandleContextDefinition(ClassUtil.getSimpleName(group.getClass()), handle);

            handleContexts.add(handleContext);
        }

        content.setResult(handleContexts);
    }

    private void setGroups(TaskRunConsumer run, TaskContentObject content) {
        AccountObject account = content.getCacheableObject();

        UserManager userManager = this.coreManager.getManager(UserManager.class);
        UserFactory userFactory = userManager.getFactory();

        List<String> parameters = content.getParameters();

        if (parameters.isEmpty()) {
            throw new ConditionParametersException();
        }

        Set<UUID> handles = ObjectUtil.transferSetFromString(UUID.class, parameters.getFirst());

        Set<GroupObject> groups = new HashSet<>();
        for (UUID handle : handles) {
            GroupObject group = userFactory.rebuildGroup(handle);

            groups.add(group);
        }

        account.setGroups(groups);
    }

    private void getToken(TaskRunConsumer run, TaskContentObject content) {
        AccountObject account = content.getCacheableObject();

        AccountTokenObject accountToken = account.getToken();

        UUID handle = accountToken.cache();

        HandleContextDefinition handleContext = new HandleContextDefinition(ClassUtil.getSimpleName(accountToken.getClass()), handle);

        content.setResult(handleContext);
    }

    private void getSessions(TaskRunConsumer run, TaskContentObject content) {
        AccountObject account = content.getCacheableObject();

        AccountSessionsObject accountSession = account.getSessions();

        UUID handle = accountSession.cache();

        HandleContextDefinition handleContext = new HandleContextDefinition(ClassUtil.getSimpleName(accountSession.getClass()), handle);

        content.setResult(handleContext);
    }
}