package indi.sly.system.kernel.security.prototypes;

import indi.sly.system.kernel.objects.Identification;

import java.util.*;

public class SecurityDescriptorSummaryDefinition {
    public SecurityDescriptorSummaryDefinition() {
        this.identifications = new ArrayList<>();
        this.accessControl = new HashMap<>();
    }

    private final List<Identification> identifications;
    private boolean inherit;
    private final Map<UUID, Long> accessControl;

    public List<Identification> getIdentifications() {
        return this.identifications;
    }

    public boolean isInherit() {
        return this.inherit;
    }

    public void setInherit(boolean inherit) {
        this.inherit = inherit;
    }

    public Map<UUID, Long> getAccessControl() {
        return this.accessControl;
    }
}
