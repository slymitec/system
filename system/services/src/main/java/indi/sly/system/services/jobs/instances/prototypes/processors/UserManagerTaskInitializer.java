package indi.sly.system.services.jobs.instances.prototypes.processors;

import indi.sly.system.common.lang.ConditionParametersException;
import indi.sly.system.common.supports.ObjectUtil;
import indi.sly.system.common.supports.StringUtil;
import indi.sly.system.common.supports.ValueUtil;
import indi.sly.system.kernel.core.enviroment.values.SpaceType;
import indi.sly.system.kernel.security.UserManager;
import indi.sly.system.kernel.security.prototypes.AccountAuthorizationObject;
import indi.sly.system.kernel.security.prototypes.AccountObject;
import indi.sly.system.kernel.security.prototypes.GroupObject;
import indi.sly.system.kernel.security.values.AccountAuthorizationTokenDefinition;
import indi.sly.system.services.core.values.TransactionType;
import indi.sly.system.services.jobs.lang.TaskRunConsumer;
import indi.sly.system.services.jobs.prototypes.TaskContentObject;
import indi.sly.system.services.jobs.values.HandledObjectDefinition;
import indi.sly.system.services.jobs.values.TaskDefinition;
import jakarta.inject.Named;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;

import java.util.UUID;

@Named
@Scope(value = ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class UserManagerTaskInitializer extends ATaskInitializer {
    public UserManagerTaskInitializer() {
        this.register("getCurrentAccount", this::getCurrentAccount, TransactionType.INDEPENDENCE);
        this.register("getAccount", this::getAccount, TransactionType.INDEPENDENCE);
        this.register("getGroup", this::getGroup, TransactionType.INDEPENDENCE);
        this.register("createAccount", this::createAccount, TransactionType.INDEPENDENCE);
        this.register("createGroup", this::createGroup, TransactionType.INDEPENDENCE);
        this.register("deleteAccount", this::deleteAccount, TransactionType.INDEPENDENCE);
        this.register("deleteGroup", this::deleteGroup, TransactionType.INDEPENDENCE);
        this.register("authorize", this::authorize, TransactionType.INDEPENDENCE);
    }

    @Override
    public void start(TaskDefinition task) {
    }

    @Override
    public void finish(TaskDefinition task) {
    }

    private void getCurrentAccount(TaskRunConsumer run, TaskContentObject content) {
        UserManager userManager = this.factoryManager.getManager(UserManager.class);

        AccountObject account = userManager.getCurrentAccount();

        UUID handle = account.cache(SpaceType.USER);

        HandledObjectDefinition handledObject = new HandledObjectDefinition();
        handledObject.setHandle(handle);
        handledObject.setType(account.getClass());

        content.setResult(handledObject);
    }

    private void getAccount(TaskRunConsumer run, TaskContentObject content) {
        UUID accountID = content.getParameterOrNull(UUID.class, "accountID");
        String accountName = content.getParameterOrNull("accountName");

        UserManager userManager = this.factoryManager.getManager(UserManager.class);

        AccountObject account;
        if (!ValueUtil.isAnyNullOrEmpty(accountID)) {
            account = userManager.getAccount(accountID);
        } else if (!StringUtil.isNameIllegal(accountName)) {
            account = userManager.getAccount(accountName);
        } else {
            throw new ConditionParametersException();
        }

        UUID handle = account.cache(SpaceType.USER);

        HandledObjectDefinition handledObject = new HandledObjectDefinition();
        handledObject.setHandle(handle);
        handledObject.setType(account.getClass());

        content.setResult(handledObject);
    }

    private void getGroup(TaskRunConsumer run, TaskContentObject content) {
        UUID groupID = content.getParameterOrNull(UUID.class, "groupID");
        String groupName = content.getParameterOrNull("groupName");

        UserManager userManager = this.factoryManager.getManager(UserManager.class);

        GroupObject group;
        if (!ValueUtil.isAnyNullOrEmpty(groupID)) {
            group = userManager.getGroup(groupID);
        } else if (!StringUtil.isNameIllegal(groupName)) {
            group = userManager.getGroup(groupName);
        } else {
            throw new ConditionParametersException();
        }

        UUID handle = group.cache(SpaceType.USER);

        HandledObjectDefinition handledObject = new HandledObjectDefinition();
        handledObject.setHandle(handle);
        handledObject.setType(group.getClass());

        content.setResult(handledObject);
    }

    private void createAccount(TaskRunConsumer run, TaskContentObject content) {
        String accountName = content.getParameterOrNull("accountName");
        String accountPassword = content.getParameterOrNull("accountPassword");

        UserManager userManager = this.factoryManager.getManager(UserManager.class);

        AccountObject account = userManager.createAccount(accountName, accountPassword);

        UUID handle = account.cache(SpaceType.USER);

        HandledObjectDefinition handledObject = new HandledObjectDefinition();
        handledObject.setHandle(handle);
        handledObject.setType(account.getClass());

        content.setResult(handledObject);
    }

    private void createGroup(TaskRunConsumer run, TaskContentObject content) {
        String groupName = content.getParameterOrNull("groupName");

        UserManager userManager = this.factoryManager.getManager(UserManager.class);

        GroupObject group = userManager.createGroup(groupName);

        UUID handle = group.cache(SpaceType.USER);

        HandledObjectDefinition handledObject = new HandledObjectDefinition();
        handledObject.setHandle(handle);
        handledObject.setType(group.getClass());

        content.setResult(handledObject);
    }

    private void deleteAccount(TaskRunConsumer run, TaskContentObject content) {
        UUID accountID = content.getParameterOrNull(UUID.class, "accountID");

        UserManager userManager = this.factoryManager.getManager(UserManager.class);

        userManager.deleteAccount(accountID);
    }

    private void deleteGroup(TaskRunConsumer run, TaskContentObject content) {
        UUID groupID = content.getParameterOrNull(UUID.class, "groupID");

        UserManager userManager = this.factoryManager.getManager(UserManager.class);

        userManager.deleteGroup(groupID);
    }

    private void authorize(TaskRunConsumer run, TaskContentObject content) {
        UUID accountID = content.getParameterOrNull(UUID.class, "accountID");
        String accountName = content.getParameterOrNull("accountName");
        String accountPassword = content.getParameterOrNull("accountPassword");
        AccountAuthorizationTokenDefinition accountAuthorizationToken =
                content.getParameterOrNull(AccountAuthorizationTokenDefinition.class, "accountAuthorizationToken");

        UserManager userManager = this.factoryManager.getManager(UserManager.class);

        AccountAuthorizationObject accountAuthorization;
        if (!ValueUtil.isAnyNullOrEmpty(accountID)) {
            accountAuthorization = userManager.authorize(accountID);
        } else if (!StringUtil.isNameIllegal(accountName)) {
            if (ObjectUtil.allNotNull(accountAuthorizationToken)) {
                accountAuthorization = userManager.authorize(accountName, accountPassword, accountAuthorizationToken);
            } else {
                accountAuthorization = userManager.authorize(accountName, accountPassword);
            }
        } else {
            throw new ConditionParametersException();
        }

        UUID handle = accountAuthorization.cache(SpaceType.USER);

        HandledObjectDefinition handledObject = new HandledObjectDefinition();
        handledObject.setHandle(handle);
        handledObject.setType(accountAuthorization.getClass());

        content.setResult(handledObject);
    }
}
