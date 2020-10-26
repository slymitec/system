package indi.sly.system.kernel.security;

import indi.sly.system.common.exceptions.StatusInsufficientResourcesException;
import indi.sly.system.common.functions.Consumer;
import indi.sly.system.common.functions.Provider;
import indi.sly.system.common.utility.ObjectUtils;
import indi.sly.system.kernel.objects.Identification;
import indi.sly.system.kernel.objects.entities.InfoEntity;
import indi.sly.system.kernel.objects.entities.InfoSummaryDefinition;

import java.util.ArrayList;
import java.util.List;

public class SecurityDescriptorAnalysisDefinition {
    public SecurityDescriptorAnalysisDefinition() {
        this.identifications = new ArrayList<>();
    }

    private InfoSummaryDefinition infoSummary;
    private final List<Identification> identifications;

    private Provider<SecurityDescriptorDefinition> funcRead;
    private Consumer<SecurityDescriptorDefinition> funcWrite;

    public void setKernelEntity(InfoEntity info) {
        infoSummary = new InfoSummaryDefinition();
        infoSummary.setID(info.getID());
        infoSummary.setType(info.getType());
        infoSummary.setName(info.getName());

        this.funcRead = () -> {
            if (ObjectUtils.isAnyNull(info.getSecurityDescriptor())) {
                return null;
            } else {
                return ObjectUtils.transferFromByteArray(info.getSecurityDescriptor());
            }
        };
        this.funcWrite = (securityDescriptor) -> {
            if (ObjectUtils.isAnyNull(securityDescriptor)) {
                info.setSecurityDescriptor(null);
            } else {
                byte[] SecurityDescriptorSource = ObjectUtils.transferToByteArray(securityDescriptor);
                if (SecurityDescriptorSource.length > 1024) {
                    throw new StatusInsufficientResourcesException();
                }
                info.setSecurityDescriptor(SecurityDescriptorSource);
            }
        };
    }

    public SecurityDescriptorDefinition getSecurityDescriptor() {
        return this.funcRead.acquire();
    }

    public void setSecurityDescriptor(SecurityDescriptorDefinition securityDescriptor) {
        this.funcWrite.accept(securityDescriptor);
    }

    public InfoSummaryDefinition getInfoSummary() {
        return this.infoSummary;
    }

    public List<Identification> getIdentifications() {
        return this.identifications;
    }
}
