package indi.sly.system.kernel.security.prototypes;

import indi.sly.system.kernel.core.prototypes.AValueProcessObject;
import indi.sly.system.kernel.security.entities.GroupEntity;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;

import javax.inject.Named;
import java.util.UUID;

@Named
@Scope(value = ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class GroupObject extends AValueProcessObject<GroupEntity> {
    public UUID getID() {
        this.init();

        return this.value.getID();
    }

    public String getName() {
        this.init();

        return this.value.getName();
    }
}
