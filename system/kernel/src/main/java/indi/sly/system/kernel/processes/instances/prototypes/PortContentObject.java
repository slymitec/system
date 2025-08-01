package indi.sly.system.kernel.processes.instances.prototypes;

import indi.sly.system.common.lang.ConditionParametersException;
import indi.sly.system.common.lang.MethodScope;
import indi.sly.system.common.lang.StatusInsufficientResourcesException;
import indi.sly.system.common.supports.ArrayUtil;
import indi.sly.system.common.supports.CollectionUtil;
import indi.sly.system.common.supports.ObjectUtil;
import indi.sly.system.common.values.LockType;
import indi.sly.system.common.values.MethodScopeType;
import indi.sly.system.kernel.objects.prototypes.AInfoContentObject;
import indi.sly.system.kernel.processes.instances.values.PortDefinition;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;

import jakarta.inject.Named;

import java.util.Set;
import java.util.UUID;

@Named
@Scope(value = ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class PortContentObject extends AInfoContentObject {
    public PortContentObject() {
        this.funcCustomRead = () -> this.port = ObjectUtil.transferFromByteArray(this.value);
        this.funcCustomWrite = () -> this.value = ObjectUtil.transferToByteArray(this.port);
    }

    private PortDefinition port;

    @MethodScope(value = MethodScopeType.ONLY_KERNEL)
    public Set<UUID> getSourceProcessIDs() {
        try {
            this.lock(LockType.READ);
            this.init();

            return CollectionUtil.unmodifiable(this.port.getSourceProcessIDs());
        } finally {
            this.lock(LockType.NONE);
        }
    }

    @MethodScope(value = MethodScopeType.ONLY_KERNEL)
    public void setSourceProcessIDs(Set<UUID> sourceProcessIDs) {
        if (ObjectUtil.isAnyNull(sourceProcessIDs)) {
            throw new ConditionParametersException();
        }

        try {
            this.lock(LockType.WRITE);
            this.init();

            Set<UUID> portSourceProcessIDs = this.port.getSourceProcessIDs();
            portSourceProcessIDs.clear();
            portSourceProcessIDs.addAll(sourceProcessIDs);

            this.fresh();
        } finally {
            this.lock(LockType.NONE);
        }
    }

    @MethodScope(value = MethodScopeType.ONLY_KERNEL)
    public byte[] receive() {
        byte[] value;

        try {
            this.lock(LockType.WRITE);
            this.init();

            value = this.port.getValue();
            this.port.setValue(ArrayUtil.EMPTY_BYTES);

            this.fresh();
        } finally {
            this.lock(LockType.NONE);
        }

        return value;
    }

    @MethodScope(value = MethodScopeType.ONLY_KERNEL)
    public void send(byte[] value) {
        if (ObjectUtil.isAnyNull(value)) {
            throw new ConditionParametersException();
        }

        try {
            this.lock(LockType.WRITE);
            this.init();

            if (this.port.size() + value.length >= this.port.getLimit()) {
                throw new StatusInsufficientResourcesException();
            }

            this.port.setValue(ArrayUtil.combineBytes(this.port.getValue(), value));

            this.fresh();
        } finally {
            this.lock(LockType.NONE);
        }
    }
}
