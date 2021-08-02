package indi.sly.system.kernel.processes.instances.prototypes.processors;

import indi.sly.system.common.lang.StatusNotSupportedException;
import indi.sly.system.common.supports.ObjectUtil;
import indi.sly.system.common.values.IdentificationDefinition;
import indi.sly.system.kernel.objects.values.DumpDefinition;
import indi.sly.system.kernel.objects.values.InfoOpenDefinition;
import indi.sly.system.kernel.objects.values.InfoEntity;
import indi.sly.system.kernel.objects.values.InfoSummaryDefinition;
import indi.sly.system.kernel.objects.infotypes.prototypes.processors.AInfoTypeInitializer;
import indi.sly.system.kernel.objects.prototypes.AInfoContentObject;
import indi.sly.system.kernel.processes.instances.prototypes.SessionContentObject;
import indi.sly.system.kernel.processes.instances.values.SessionDefinition;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;

import javax.inject.Named;
import java.util.Set;
import java.util.UUID;
import java.util.function.Predicate;

@Named
@Scope(value = ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class SessionTypeInitializer extends AInfoTypeInitializer {
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
        info.setContent(ObjectUtil.transferToByteArray(new SessionDefinition()));
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
    public void openProcedure(InfoEntity info, InfoOpenDefinition infoOpen, long openAttribute,
                              Object... arguments) {
    }

    @Override
    public void closeProcedure(InfoEntity info, InfoOpenDefinition infoOpen) {
    }

    @Override
    public void createChildProcedure(InfoEntity info, InfoEntity childInfo) {
        throw new StatusNotSupportedException();
    }

    @Override
    public InfoSummaryDefinition getChildProcedure(InfoEntity info, IdentificationDefinition identification) {
        throw new StatusNotSupportedException();
    }

    @Override
    public Set<InfoSummaryDefinition> queryChildProcedure(InfoEntity info, Predicate<InfoSummaryDefinition> wildcard) {
        throw new StatusNotSupportedException();
    }

    @Override
    public void renameChildProcedure(InfoEntity info, IdentificationDefinition oldIdentification,
                                     IdentificationDefinition newIdentification) {
        throw new StatusNotSupportedException();
    }

    @Override
    public void deleteChildProcedure(InfoEntity info, IdentificationDefinition identification) {
        throw new StatusNotSupportedException();
    }

    @Override
    public Class<? extends AInfoContentObject> getContentTypeProcedure(InfoEntity info, InfoOpenDefinition infoOpen) {
        return SessionContentObject.class;
    }

    @Override
    public void refreshPropertiesProcedure(InfoEntity info, InfoOpenDefinition infoOpen) {
    }
}
