package indi.sly.system.kernel.processes.instances.prototypes;

import indi.sly.system.common.lang.ConditionParametersException;
import indi.sly.system.common.lang.StatusInsufficientResourcesException;
import indi.sly.system.common.supports.ArrayUtil;
import indi.sly.system.common.supports.CollectionUtil;
import indi.sly.system.common.supports.ObjectUtil;
import indi.sly.system.kernel.core.prototypes.IByteValueSupporter;
import indi.sly.system.kernel.objects.prototypes.AInfoContentObject;
import indi.sly.system.kernel.processes.instances.values.PortDefinition;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;

import jakarta.inject.Named;

import java.util.Set;
import java.util.UUID;

@Named
@Scope(value = ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class PortContentObject extends AInfoContentObject implements IByteValueSupporter<PortDefinition> {
    public Set<UUID> getSourceProcessIDs() {
        PortDefinition port = this.init(this.read());

        return CollectionUtil.unmodifiable(port.getSourceProcessIDs());
    }

    public void setSourceProcessIDs(Set<UUID> sourceProcessIDs) {
        if (ObjectUtil.isAnyNull(sourceProcessIDs)) {
            throw new ConditionParametersException();
        }

        PortDefinition port = this.init(this.read());

        Set<UUID> portSourceProcessIDs = port.getSourceProcessIDs();
        portSourceProcessIDs.clear();
        portSourceProcessIDs.addAll(sourceProcessIDs);

        this.write(this.flush(port));
    }

    public byte[] receive() {
        PortDefinition port = this.init(this.read());

        byte[] value = port.getValue();
        port.setValue(ArrayUtil.EMPTY_BYTES);

        this.write(this.flush(port));

        return value;
    }

    public void send(byte[] value) {
        if (ObjectUtil.isAnyNull(value)) {
            throw new ConditionParametersException();
        }

        PortDefinition port = this.init(this.read());

        if (port.size() + value.length >= port.getLimit()) {
            throw new StatusInsufficientResourcesException();
        }

        port.setValue(ArrayUtil.combineBytes(port.getValue(), value));

        this.write(this.flush(port));
    }
}
