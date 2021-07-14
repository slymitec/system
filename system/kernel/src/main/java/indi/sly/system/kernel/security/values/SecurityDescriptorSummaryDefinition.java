package indi.sly.system.kernel.security.values;

import indi.sly.system.common.values.ADefinition;
import indi.sly.system.common.values.IdentificationDefinition;

import java.util.*;

public class SecurityDescriptorSummaryDefinition extends ADefinition<SecurityDescriptorSummaryDefinition> {
    public SecurityDescriptorSummaryDefinition() {
        this.identifications = new ArrayList<>();
        this.accessControls = new HashSet<>();
    }

    private final List<IdentificationDefinition> identifications;
    private boolean inherit;
    private final Set<AccessControlDefinition> accessControls;

    public List<IdentificationDefinition> getIdentifications() {
        return this.identifications;
    }

    public boolean isInherit() {
        return this.inherit;
    }

    public void setInherit(boolean inherit) {
        this.inherit = inherit;
    }

    public Set<AccessControlDefinition> getAccessControls() {
        return this.accessControls;
    }
}
