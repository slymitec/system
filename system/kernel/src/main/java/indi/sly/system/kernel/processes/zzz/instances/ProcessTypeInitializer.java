package indi.sly.system.kernel.processes.zzz.instances;

import indi.sly.system.common.exceptions.StatusNotSupportedException;
import indi.sly.system.common.utility.ObjectUtils;
import indi.sly.system.kernel.objects.Identification;
import indi.sly.system.kernel.objects.values.InfoEntity;
import indi.sly.system.kernel.objects.values.InfoSummaryDefinition;
import indi.sly.system.kernel.objects.prototypes.AInfoContentObject;
import indi.sly.system.kernel.objects.values.InfoStatusOpenDefinition;
import indi.sly.system.kernel.objects.infotypes.prototypes.ATypeInitializer;
import indi.sly.system.kernel.objects.values.DumpDefinition;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;

import javax.inject.Named;
import java.util.Set;
import java.util.UUID;
import java.util.function.Predicate;

@Named
@Scope(value = ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class ProcessTypeInitializer extends ATypeInitializer {
    @Override
    public void install() {
    }

    @Override
    public void uninstall() {
    }

    @Override
    public UUID getPoolID(UUID id, UUID type) {
        return this.factoryManager.getKernelSpace().getConfiguration().MEMORY_REPOSITORIES_DATABASEENTITYREPOSITORYOBJECT_ID;
    }

    @Override
    public void createProcedure(InfoEntity info) {
        ProcessContentDefinition processContent = new ProcessContentDefinition();

        info.setContent(ObjectUtils.transferToByteArray(processContent));
    }

    @Override
    public void deleteProcedure(InfoEntity info) {
    }

    @Override
    public void getProcedure(InfoEntity info) {
    }

    @Override
    public void dumpProcedure(InfoEntity info, DumpDefinition dump) {
    }

    @Override
    public void openProcedure(InfoEntity info, InfoStatusOpenDefinition statusOpen, long openAttribute, Object... arguments) {
    }

    @Override
    public void closeProcedure(InfoEntity info, InfoStatusOpenDefinition statusOpen) {
    }

    @Override
    public void createChildProcedure(InfoEntity info, InfoEntity childInfo) {
        throw new StatusNotSupportedException();
    }

    @Override
    public InfoSummaryDefinition getChildProcedure(InfoEntity info, Identification identification) {
        throw new StatusNotSupportedException();
    }

    @Override
    public Set<InfoSummaryDefinition> queryChildProcedure(InfoEntity info, Predicate<InfoSummaryDefinition> wildcard) {
        throw new StatusNotSupportedException();
    }

    @Override
    public void renameChildProcedure(InfoEntity info, Identification oldIdentification, Identification newIdentification) {
        throw new StatusNotSupportedException();
    }

    @Override
    public void deleteChildProcedure(InfoEntity info, Identification identification) {
        throw new StatusNotSupportedException();
    }

    @Override
    protected Class<? extends AInfoContentObject> getContentTypeProcedure(InfoEntity info, InfoStatusOpenDefinition statusOpen) {
        return ProcessContentObject.class;
    }

    @Override
    public void refreshPropertiesProcedure(InfoEntity info, InfoStatusOpenDefinition statusOpen) {
    }
}
