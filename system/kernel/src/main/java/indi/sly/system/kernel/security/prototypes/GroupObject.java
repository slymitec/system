package indi.sly.system.kernel.security.prototypes;

import indi.sly.system.kernel.core.prototypes.ACoreProcessObject;
import indi.sly.system.kernel.security.entities.GroupEntity;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;

import javax.inject.Named;
import java.util.UUID;

@Named
@Scope(value = ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class GroupObject extends ACoreProcessObject {
    private GroupEntity group;

    @Override
    protected void init() {
    }

    @Override
    protected void fresh() {
    }

    public void setGroup(GroupEntity group) {
        this.group = group;
    }

    public UUID getID() {
        return this.group.getID();
    }

    public String getName() {
        return this.group.getName();
    }
}
