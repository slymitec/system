package indi.sly.system.services.jobs.instances.prototypes.processors.security;

import indi.sly.system.common.lang.ConditionParametersException;
import indi.sly.system.common.supports.ClassUtil;
import indi.sly.system.common.supports.ObjectUtil;
import indi.sly.system.kernel.security.UserManager;
import indi.sly.system.kernel.security.prototypes.AccountAuthorizationObject;
import indi.sly.system.kernel.security.prototypes.AccountObject;
import indi.sly.system.kernel.security.prototypes.GroupObject;
import indi.sly.system.kernel.security.values.AccountAuthorizationTokenDefinition;
import indi.sly.system.services.core.values.TransactionType;
import indi.sly.system.services.jobs.instances.prototypes.processors.ATaskInitializer;
import indi.sly.system.services.jobs.lang.TaskRunConsumer;
import indi.sly.system.services.jobs.prototypes.TaskContentObject;
import indi.sly.system.services.jobs.values.HandleContextDefinition;
import indi.sly.system.services.jobs.values.TaskDefinition;
import jakarta.inject.Named;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;

import java.util.List;
import java.util.UUID;

@Named
@Scope(value = ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class UserManagerTaskInitializer extends ATaskInitializer {
    public UserManagerTaskInitializer() {
        this.cacheableObjectFunction = (_) -> this.coreManager.getManager(UserManager.class);

        this.register("getCurrentAccount", this::getCurrentAccount, TransactionType.INDEPENDENCE);
        this.register("getAccountById", this::getAccountById, TransactionType.INDEPENDENCE);
        this.register("getAccountByName", this::getAccountByName, TransactionType.INDEPENDENCE);
        this.register("getGroupById", this::getGroupById, TransactionType.INDEPENDENCE);
        this.register("getGroupByName", this::getGroupByName, TransactionType.INDEPENDENCE);
        this.register("createAccount", this::createAccount, TransactionType.INDEPENDENCE);
        this.register("createGroup", this::createGroup, TransactionType.INDEPENDENCE);
        this.register("deleteAccount", this::deleteAccount, TransactionType.INDEPENDENCE);
        this.register("deleteGroup", this::deleteGroup, TransactionType.INDEPENDENCE);
        this.register("authorizeById", this::authorizeById, TransactionType.INDEPENDENCE);
        this.register("authorizeByName", this::authorizeByName, TransactionType.INDEPENDENCE);
        this.register("authorizeByNameWithToken", this::authorizeByNameWithToken, TransactionType.INDEPENDENCE);
    }

    @Override
    public void start(TaskDefinition task) {
    }

    @Override
    public void finish(TaskDefinition task) {
    }

    private void getCurrentAccount(TaskRunConsumer run, TaskContentObject content) {
        UserManager userManager = this.coreManager.getManager(UserManager.class);

        AccountObject account = userManager.getCurrentAccount();

        UUID handle = account.cache();

        HandleContextDefinition handleContext = new HandleContextDefinition(ClassUtil.getSimpleName(account.getClass()), handle);

        content.setResult(handleContext);
    }

    private void getAccountById(TaskRunConsumer run, TaskContentObject content) {
        List<String> parameters = content.getParameters();

        UserManager userManager = this.coreManager.getManager(UserManager.class);

        if (parameters.isEmpty()) {
            throw new ConditionParametersException();
        }

        UUID accountId = ObjectUtil.transferFromString(UUID.class, parameters.getFirst());

        AccountObject account = userManager.getAccountById(accountId);

        UUID handle = account.cache();

        HandleContextDefinition handleContext = new HandleContextDefinition(ClassUtil.getSimpleName(account.getClass()), handle);

        content.setResult(handleContext);
    }

    private void getAccountByName(TaskRunConsumer run, TaskContentObject content) {
        List<String> parameters = content.getParameters();

        UserManager userManager = this.coreManager.getManager(UserManager.class);

        if (parameters.isEmpty()) {
            throw new ConditionParametersException();
        }

        String accountName = ObjectUtil.transferFromString(String.class, parameters.getFirst());

        AccountObject account = userManager.getAccountByName(accountName);

        UUID handle = account.cache();

        HandleContextDefinition handleContext = new HandleContextDefinition(ClassUtil.getSimpleName(account.getClass()), handle);

        content.setResult(handleContext);
    }

    private void getGroupById(TaskRunConsumer run, TaskContentObject content) {
        List<String> parameters = content.getParameters();

        UserManager userManager = this.coreManager.getManager(UserManager.class);

        if (parameters.isEmpty()) {
            throw new ConditionParametersException();
        }

        UUID groupId = ObjectUtil.transferFromString(UUID.class, parameters.getFirst());

        GroupObject group = userManager.getGroupById(groupId);

        UUID handle = group.cache();

        HandleContextDefinition handleContext = new HandleContextDefinition(ClassUtil.getSimpleName(group.getClass()), handle);

        content.setResult(handleContext);
    }

    private void getGroupByName(TaskRunConsumer run, TaskContentObject content) {
        List<String> parameters = content.getParameters();

        UserManager userManager = this.coreManager.getManager(UserManager.class);

        if (parameters.isEmpty()) {
            throw new ConditionParametersException();
        }

        String groupName = ObjectUtil.transferFromString(String.class, parameters.getFirst());

        GroupObject group = userManager.getGroupByName(groupName);

        UUID handle = group.cache();

        HandleContextDefinition handleContext = new HandleContextDefinition(ClassUtil.getSimpleName(group.getClass()), handle);

        content.setResult(handleContext);
    }

    private void createAccount(TaskRunConsumer run, TaskContentObject content) {
        List<String> parameters = content.getParameters();

        UserManager userManager = this.coreManager.getManager(UserManager.class);

        if (parameters.size() < 2) {
            throw new ConditionParametersException();
        }

        String accountName = ObjectUtil.transferFromString(String.class, parameters.getFirst());
        String accountPassword = ObjectUtil.transferFromString(String.class, parameters.get(1));

        AccountObject account = userManager.createAccount(accountName, accountPassword);

        UUID handle = account.cache();

        HandleContextDefinition handleContext = new HandleContextDefinition(ClassUtil.getSimpleName(account.getClass()), handle);

        content.setResult(handleContext);
    }

    private void createGroup(TaskRunConsumer run, TaskContentObject content) {
        List<String> parameters = content.getParameters();

        UserManager userManager = this.coreManager.getManager(UserManager.class);

        if (parameters.size() < 2) {
            throw new ConditionParametersException();
        }

        String groupName = ObjectUtil.transferFromString(String.class, parameters.getFirst());

        GroupObject group = userManager.createGroup(groupName);

        UUID handle = group.cache();

        HandleContextDefinition handleContext = new HandleContextDefinition(ClassUtil.getSimpleName(group.getClass()), handle);

        content.setResult(handleContext);
    }

    private void deleteAccount(TaskRunConsumer run, TaskContentObject content) {
        List<String> parameters = content.getParameters();

        UserManager userManager = this.coreManager.getManager(UserManager.class);

        if (parameters.isEmpty()) {
            throw new ConditionParametersException();
        }

        UUID accountId = ObjectUtil.transferFromString(UUID.class, parameters.getFirst());

        userManager.deleteAccount(accountId);
    }

    private void deleteGroup(TaskRunConsumer run, TaskContentObject content) {
        List<String> parameters = content.getParameters();

        UserManager userManager = this.coreManager.getManager(UserManager.class);

        if (parameters.isEmpty()) {
            throw new ConditionParametersException();
        }

        UUID groupId = ObjectUtil.transferFromString(UUID.class, parameters.getFirst());

        userManager.deleteGroup(groupId);
    }

    private void authorizeById(TaskRunConsumer run, TaskContentObject content) {
        List<String> parameters = content.getParameters();

        UserManager userManager = this.coreManager.getManager(UserManager.class);

        if (parameters.isEmpty()) {
            throw new ConditionParametersException();
        }

        UUID accountId = ObjectUtil.transferFromString(UUID.class, parameters.getFirst());

        AccountAuthorizationObject accountAuthorization = userManager.authorizeById(accountId);

        UUID handle = accountAuthorization.cache();

        HandleContextDefinition handleContext = new HandleContextDefinition(ClassUtil.getSimpleName(accountAuthorization.getClass()), handle);

        content.setResult(handleContext);
    }

    private void authorizeByName(TaskRunConsumer run, TaskContentObject content) {
        List<String> parameters = content.getParameters();

        UserManager userManager = this.coreManager.getManager(UserManager.class);

        if (parameters.size() < 2) {
            throw new ConditionParametersException();
        }

        String accountName = ObjectUtil.transferFromString(String.class, parameters.getFirst());
        String accountPassword = ObjectUtil.transferFromString(String.class, parameters.get(1));

        AccountAuthorizationObject accountAuthorization = userManager.authorizeByName(accountName, accountPassword);

        UUID handle = accountAuthorization.cache();

        HandleContextDefinition handleContext = new HandleContextDefinition(ClassUtil.getSimpleName(accountAuthorization.getClass()), handle);

        content.setResult(handleContext);
    }

    private void authorizeByNameWithToken(TaskRunConsumer run, TaskContentObject content) {
        List<String> parameters = content.getParameters();

        UserManager userManager = this.coreManager.getManager(UserManager.class);

        if (parameters.size() < 3) {
            throw new ConditionParametersException();
        }

        String accountName = ObjectUtil.transferFromString(String.class, parameters.getFirst());
        String accountPassword = ObjectUtil.transferFromString(String.class, parameters.get(1));
        AccountAuthorizationTokenDefinition accountAuthorizationToken = ObjectUtil.transferFromString(AccountAuthorizationTokenDefinition.class, parameters.get(2));

        AccountAuthorizationObject accountAuthorization = userManager.authorizeByNameWithToken(accountName, accountPassword, accountAuthorizationToken);

        UUID handle = accountAuthorization.cache();

        HandleContextDefinition handleContext = new HandleContextDefinition(ClassUtil.getSimpleName(accountAuthorization.getClass()), handle);

        content.setResult(handleContext);
    }
}
