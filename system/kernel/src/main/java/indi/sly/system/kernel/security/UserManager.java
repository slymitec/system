package indi.sly.system.kernel.security;

import indi.sly.system.common.lang.ConditionParametersException;
import indi.sly.system.common.lang.ConditionRefuseException;
import indi.sly.system.common.lang.StatusNotExistedException;
import indi.sly.system.common.lang.StatusRelationshipErrorException;
import indi.sly.system.common.supports.LogicalUtil;
import indi.sly.system.common.supports.ObjectUtil;
import indi.sly.system.common.supports.StringUtil;
import indi.sly.system.common.supports.ValueUtil;
import indi.sly.system.common.values.IdentificationDefinition;
import indi.sly.system.kernel.core.AManager;
import indi.sly.system.kernel.core.boot.values.StartupType;
import indi.sly.system.kernel.core.enviroment.values.KernelConfigurationDefinition;
import indi.sly.system.kernel.memory.MemoryManager;
import indi.sly.system.kernel.memory.repositories.prototypes.UserRepositoryObject;
import indi.sly.system.kernel.objects.ObjectManager;
import indi.sly.system.kernel.objects.TypeManager;
import indi.sly.system.kernel.objects.infotypes.prototypes.processors.AInfoTypeInitializer;
import indi.sly.system.kernel.objects.infotypes.values.TypeInitializerAttributeType;
import indi.sly.system.kernel.objects.prototypes.InfoObject;
import indi.sly.system.kernel.objects.values.InfoOpenAttributeType;
import indi.sly.system.kernel.objects.values.InfoWildcardDefinition;
import indi.sly.system.kernel.objects.values.InfoSummaryDefinition;
import indi.sly.system.kernel.processes.ProcessManager;
import indi.sly.system.kernel.processes.prototypes.ProcessObject;
import indi.sly.system.kernel.processes.prototypes.ProcessTokenObject;
import indi.sly.system.kernel.security.instances.prototypes.processors.AuditTypeInitializer;
import indi.sly.system.kernel.security.prototypes.*;
import indi.sly.system.kernel.security.values.*;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;

import jakarta.inject.Named;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

@Named
@Scope(value = ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class UserManager extends AManager {
    protected UserFactory factory;

    @Override
    public void startup(long startup) {
        if (LogicalUtil.isAnyEqual(startup, StartupType.STEP_INIT_SELF)) {
            this.factory = this.factoryManager.create(UserFactory.class);
            this.factory.init();
        } else if (LogicalUtil.isAnyEqual(startup, StartupType.STEP_INIT_KERNEL)) {
            TypeManager typeManager = this.factoryManager.getManager(TypeManager.class);

            KernelConfigurationDefinition kernelConfiguration = this.factoryManager.getKernelSpace().getConfiguration();

            long attribute = LogicalUtil.or(TypeInitializerAttributeType.CAN_BE_SHARED_READ,
                    TypeInitializerAttributeType.CAN_NOT_CHANGE_OWNER, TypeInitializerAttributeType.HAS_CONTENT,
                    TypeInitializerAttributeType.HAS_PERMISSION, TypeInitializerAttributeType.HAS_PROPERTIES);
            Set<UUID> childTypes = Set.of();
            AInfoTypeInitializer typeInitializer = this.factoryManager.create(AuditTypeInitializer.class);

            typeManager.create(kernelConfiguration.SECURITY_INSTANCE_AUDIT_ID, kernelConfiguration.SECURITY_INSTANCE_AUDIT_NAME, attribute, childTypes, typeInitializer);
        }
    }

    @Override
    public void shutdown() {
    }

    private AccountObject getTargetAccount(UUID accountID) {
        if (ValueUtil.isAnyNullOrEmpty(accountID)) {
            throw new ConditionParametersException();
        }

        MemoryManager memoryManager = this.factoryManager.getManager(MemoryManager.class);
        UserRepositoryObject userRepository = memoryManager.getUserRepository();

        if (!userRepository.containAccount(accountID)) {
            throw new StatusNotExistedException();
        }

        return this.factory.buildAccount(accountID);
    }

    private AccountObject getTargetAccount(String accountName) {
        if (StringUtil.isNameIllegal(accountName)) {
            throw new ConditionParametersException();
        }

        MemoryManager memoryManager = this.factoryManager.getManager(MemoryManager.class);
        UserRepositoryObject userRepository = memoryManager.getUserRepository();

        AccountEntity account = userRepository.getAccount(accountName);

        return this.factory.buildAccount(account.getID());
    }

    public AccountObject getCurrentAccount() {
        ProcessManager processManager = this.factoryManager.getManager(ProcessManager.class);

        ProcessObject process = processManager.getCurrent();
        ProcessTokenObject processToken = process.getToken();

        return this.getTargetAccount(processToken.getAccountID());
    }

    public AccountObject getAccount(UUID accountID) {
        if (ValueUtil.isAnyNullOrEmpty(accountID)) {
            throw new ConditionParametersException();
        }

        AccountObject currentAccount = this.getCurrentAccount();

        if (currentAccount.getID().equals(accountID)) {
            return currentAccount;
        } else {
            ProcessManager processManager = this.factoryManager.getManager(ProcessManager.class);

            ProcessObject process = processManager.getCurrent();
            ProcessTokenObject processToken = process.getToken();

            if (!processToken.isPrivileges(PrivilegeType.SECURITY_DO_WITH_ANY_ACCOUNT)) {
                throw new ConditionRefuseException();
            }

            return this.getTargetAccount(accountID);
        }
    }

    public AccountObject getAccount(String accountName) {
        if (StringUtil.isNameIllegal(accountName)) {
            throw new ConditionParametersException();
        }

        AccountObject currentAccount = this.getCurrentAccount();

        if (currentAccount.getName().equals(accountName)) {
            return currentAccount;
        } else {
            ProcessManager processManager = this.factoryManager.getManager(ProcessManager.class);

            ProcessObject process = processManager.getCurrent();
            ProcessTokenObject processToken = process.getToken();

            if (!processToken.isPrivileges(PrivilegeType.SECURITY_DO_WITH_ANY_ACCOUNT)) {
                throw new ConditionRefuseException();
            }

            return this.getTargetAccount(accountName);
        }
    }

    public GroupObject getGroup(UUID groupID) {
        if (ValueUtil.isAnyNullOrEmpty(groupID)) {
            throw new ConditionParametersException();
        }

        MemoryManager memoryManager = this.factoryManager.getManager(MemoryManager.class);
        UserRepositoryObject userRepository = memoryManager.getUserRepository();

        if (!userRepository.containGroup(groupID)) {
            throw new StatusNotExistedException();
        }

        return this.factory.buildGroup(groupID);
    }

    public GroupObject getGroup(String groupName) {
        if (StringUtil.isNameIllegal(groupName)) {
            throw new ConditionParametersException();
        }

        MemoryManager memoryManager = this.factoryManager.getManager(MemoryManager.class);
        UserRepositoryObject userRepository = memoryManager.getUserRepository();

        GroupEntity group = userRepository.getGroup(groupName);

        return this.factory.buildGroup(group.getID());
    }

    public AccountObject createAccount(String accountName, String accountPassword) {
        AccountBuilder accountBuilder = this.factory.createAccount();

        AccountObject account = accountBuilder.create(accountName, accountPassword);

        KernelConfigurationDefinition kernelConfiguration = this.factoryManager.getKernelSpace().getConfiguration();

        ObjectManager objectManager = this.factoryManager.getManager(ObjectManager.class);

        InfoWildcardDefinition wildcard = new InfoWildcardDefinition(account.getName());

        InfoObject parentInfo = objectManager.get(List.of(new IdentificationDefinition("Audits")));
        Set<InfoSummaryDefinition> infoSummaries = parentInfo.queryChild(wildcard);
        if (infoSummaries.isEmpty()) {
            InfoObject childInfo = parentInfo.createChildAndOpen(kernelConfiguration.OBJECTS_TYPES_INSTANCE_NAMELESSFOLDER_ID, new IdentificationDefinition(account.getName()), InfoOpenAttributeType.OPEN_EXCLUSIVE);

            SecurityDescriptorObject auditSecurityDescriptor = childInfo.getSecurityDescriptor();
            Set<AccessControlDefinition> permissions = new HashSet<>();
            AccessControlDefinition permission = new AccessControlDefinition();
            permission.getUserID().setID(account.getID());
            permission.getUserID().setType(UserType.ACCOUNT);
            permission.setScope(AccessControlScopeType.ALL);
            permission.setValue(LogicalUtil.or(PermissionType.LISTCHILD_READDATA_ALLOW, PermissionType.TRAVERSE_EXECUTE_ALLOW, PermissionType.CREATECHILD_WRITEDATA_ALLOW, PermissionType.READPROPERTIES_ALLOW, PermissionType.WRITEPROPERTIES_ALLOW, PermissionType.READPERMISSIONDESCRIPTOR_ALLOW, PermissionType.DELETECHILD_ALLOW));
            permissions.add(permission);
            auditSecurityDescriptor.setPermissions(permissions);
            auditSecurityDescriptor.setInherit(false);

            childInfo.close();
        }

        parentInfo = objectManager.get(List.of(new IdentificationDefinition("Files"), new IdentificationDefinition("Main"), new IdentificationDefinition("Home")));
        infoSummaries = parentInfo.queryChild(wildcard);
        if (infoSummaries.isEmpty()) {
            InfoObject childInfo = parentInfo.createChildAndOpen(kernelConfiguration.FILES_TYPES_INSTANCE_FOLDER_ID, new IdentificationDefinition(account.getName()), InfoOpenAttributeType.OPEN_EXCLUSIVE);

            SecurityDescriptorObject auditSecurityDescriptor = childInfo.getSecurityDescriptor();
            Set<AccessControlDefinition> permissions = new HashSet<>();
            AccessControlDefinition permission = new AccessControlDefinition();
            permission.getUserID().setID(account.getID());
            permission.getUserID().setType(UserType.ACCOUNT);
            permission.setScope(AccessControlScopeType.ALL);
            permission.setValue(LogicalUtil.or(PermissionType.LISTCHILD_READDATA_ALLOW, PermissionType.TRAVERSE_EXECUTE_ALLOW, PermissionType.CREATECHILD_WRITEDATA_ALLOW, PermissionType.READPROPERTIES_ALLOW, PermissionType.WRITEPROPERTIES_ALLOW, PermissionType.READPERMISSIONDESCRIPTOR_ALLOW, PermissionType.DELETECHILD_ALLOW));
            permissions.add(permission);
            auditSecurityDescriptor.setPermissions(permissions);
            auditSecurityDescriptor.setInherit(false);

            childInfo.close();
        }

        return account;
    }

    public GroupObject createGroup(String groupName) {
        GroupBuilder groupBuilder = this.factory.createGroup();

        return groupBuilder.create(groupName);
    }

    public void deleteAccount(UUID accountID) {
        AccountBuilder accountBuilder = this.factory.createAccount();

        AccountObject account = this.getAccount(accountID);
        AccountSessionsObject accountSessions = account.getSessions();

        if (!accountSessions.listSessions().isEmpty()) {
            throw new StatusRelationshipErrorException();
        }

        ObjectManager objectManager = this.factoryManager.getManager(ObjectManager.class);

        InfoWildcardDefinition infoQueryChildWildcard = new InfoWildcardDefinition(account.getName());

        InfoObject parentInfo = objectManager.get(List.of(new IdentificationDefinition("Audits")));
        Set<InfoSummaryDefinition> infoSummaries = parentInfo.queryChild(infoQueryChildWildcard);
        if (!infoSummaries.isEmpty()) {
            InfoObject childInfo = parentInfo.getChild(new IdentificationDefinition(account.getName()));

            infoSummaries = childInfo.queryChild(new InfoWildcardDefinition());
            for (InfoSummaryDefinition auditInfoSummary : infoSummaries) {
                childInfo.deleteChild(new IdentificationDefinition(auditInfoSummary.getID()));
            }
            parentInfo.deleteChild(new IdentificationDefinition(account.getID()));
        }

        parentInfo = objectManager.get(List.of(new IdentificationDefinition("Files"), new IdentificationDefinition("Main"), new IdentificationDefinition("Home")));
        infoSummaries = parentInfo.queryChild(infoQueryChildWildcard);
        if (!infoSummaries.isEmpty()) {
            parentInfo.renameChild(new IdentificationDefinition(account.getName()), new IdentificationDefinition(account.getName() + "_Deleted"));
        }

        accountBuilder.delete(accountID);
    }

    public void deleteGroup(UUID groupID) {
        GroupBuilder groupBuilder = this.factory.createGroup();

        groupBuilder.delete(groupID);
    }

    public AccountAuthorizationObject authorize(UUID accountID) {
        if (ValueUtil.isAnyNullOrEmpty(accountID)) {
            throw new ConditionParametersException();
        }

        ProcessManager processManager = this.factoryManager.getManager(ProcessManager.class);
        ProcessObject process = processManager.getCurrent();
        ProcessTokenObject processToken = process.getToken();

        AccountObject account = this.getTargetAccount(accountID);

        if (!processToken.isPrivileges(PrivilegeType.SECURITY_DO_WITH_ANY_ACCOUNT)) {
            throw new ConditionRefuseException();
        }

        AccountAuthorizationObject accountAuthorization = this.factoryManager.create(AccountAuthorizationObject.class);

        accountAuthorization.setAccount(() -> this.getTargetAccount(account.getID()), account.getPassword());

        return accountAuthorization;
    }

    public AccountAuthorizationObject authorize(String accountName, String accountPassword) {
        return this.authorize(accountName, accountPassword, null);
    }

    public AccountAuthorizationObject authorize(String accountName, String accountPassword, AccountAuthorizationTokenDefinition accountAuthorizationToken) {
        if (StringUtil.isNameIllegal(accountName)) {
            throw new ConditionParametersException();
        }
        if (ObjectUtil.isAnyNull(accountPassword)) {
            accountPassword = StringUtil.EMPTY;
        }

        ProcessManager processManager = this.factoryManager.getManager(ProcessManager.class);
        ProcessObject process = processManager.getCurrent();
        ProcessTokenObject processToken = process.getToken();

        AccountObject account = this.getTargetAccount(accountName);

        if (!processToken.isPrivileges(PrivilegeType.SECURITY_DO_WITH_ANY_ACCOUNT) && !ObjectUtil.equals(account.getPassword(), accountPassword)) {
            throw new ConditionRefuseException();
        }

        AccountAuthorizationObject accountAuthorization = this.factoryManager.create(AccountAuthorizationObject.class);

        accountAuthorization.setAccount(() -> this.getTargetAccount(account.getID()), account.getPassword());
        if (ObjectUtil.allNotNull(accountAuthorizationToken)) {
            accountAuthorization.setToken(processToken, accountAuthorizationToken);
        }

        return accountAuthorization;
    }
}
