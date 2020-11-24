package indi.sly.system.kernel.security.prototypes;

import indi.sly.system.common.exceptions.ConditionParametersException;
import indi.sly.system.common.exceptions.ConditionPermissionsException;
import indi.sly.system.common.utility.ObjectUtils;
import indi.sly.system.kernel.core.prototypes.ACoreProcessObject;
import indi.sly.system.kernel.memory.MemoryManager;
import indi.sly.system.kernel.memory.repositories.prototypes.AccountGroupRepositoryObject;
import indi.sly.system.kernel.processes.ProcessManager;
import indi.sly.system.kernel.processes.prototypes.ProcessObject;
import indi.sly.system.kernel.processes.prototypes.ProcessTokenObject;
import indi.sly.system.kernel.security.SecurityTokenManager;
import indi.sly.system.kernel.security.entities.AccountEntity;
import indi.sly.system.kernel.security.entities.GroupEntity;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;

import javax.inject.Named;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

@Named
@Scope(value = ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class AccountObject extends ACoreProcessObject {
    private AccountEntity account;

    @Override
    protected void init() {
    }

    @Override
    protected void fresh() {
    }

    public void setAccount(AccountEntity account) {
        this.account = account;
    }

    public UUID getID() {
        return this.account.getID();
    }

    public String getName() {
        return this.account.getName();
    }

    public String getPassword() {
        return account.getPassword();
    }

    public void setPassword(String password) {
        this.account.setPassword(password);
    }

    public List<GroupObject> getGroups() {
        SecurityTokenManager securityTokenManager = this.factoryManager.getManager(SecurityTokenManager.class);

        List<GroupObject> groups = new ArrayList<>();

        for (GroupEntity group : this.account.getGroups()) {
            groups.add(securityTokenManager.getGroup(group.getID()));
        }

        return Collections.unmodifiableList(groups);
    }

    public void setGroups(List<GroupObject> groups) {
        if (ObjectUtils.isAnyNull(groups)) {
            throw new ConditionParametersException();
        }

        MemoryManager memoryManager = this.factoryManager.getManager(MemoryManager.class);
        ProcessManager processManager = this.factoryManager.getManager(ProcessManager.class);

        ProcessObject process = processManager.getCurrentProcess();
        ProcessTokenObject processToken = process.getToken();

        if (!processToken.isPrivilegeTypes(PrivilegeTypes.SECURITY_MODIFY_ACCOUNT_AND_GROUP)) {
            throw new ConditionPermissionsException();
        }

        AccountGroupRepositoryObject accountGroupRepository = memoryManager.getAccountGroupRepository();

        if (ObjectUtils.isAnyNull(this.account.getGroups())) {
            this.account.setGroups(new ArrayList<>());
        } else {
            this.account.getGroups().clear();
        }

        for (GroupObject group : groups) {
            this.account.getGroups().add(accountGroupRepository.getGroup(group.getID()));
        }
    }
}
