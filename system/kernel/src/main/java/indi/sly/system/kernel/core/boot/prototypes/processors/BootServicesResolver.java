package indi.sly.system.kernel.core.boot.prototypes.processors;

import indi.sly.system.common.lang.StatusRelationshipErrorException;
import indi.sly.system.common.supports.LogicalUtil;
import indi.sly.system.common.values.IdentifierRecord;
import indi.sly.system.common.values.PathRecord;
import indi.sly.system.kernel.core.boot.lang.BootStartConsumer;
import indi.sly.system.kernel.core.boot.prototypes.mediators.BootProcessorMediator;
import indi.sly.system.kernel.core.boot.values.StartupType;
import indi.sly.system.kernel.memory.MemoryManager;
import indi.sly.system.kernel.memory.repositories.prototypes.ServiceRepositoryObject;
import indi.sly.system.kernel.objects.ObjectManager;
import indi.sly.system.kernel.objects.prototypes.InfoObject;
import indi.sly.system.kernel.objects.values.InfoOpenAttributeType;
import indi.sly.system.kernel.objects.values.InfoSummaryDefinition;
import indi.sly.system.kernel.objects.values.InfoWildcardDefinition;
import indi.sly.system.kernel.services.ServiceManager;
import indi.sly.system.kernel.services.instances.prototypes.ServiceContentObject;
import indi.sly.system.kernel.services.instances.values.ServiceStartType;
import jakarta.inject.Named;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

@Named
@Scope(value = ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class BootServicesResolver extends ABootResolver {
    public BootServicesResolver() {
        this.start = (startup) -> {
            MemoryManager memoryManager = this.coreManager.getManager(MemoryManager.class);
            ObjectManager objectManager = this.coreManager.getManager(ObjectManager.class);
            ServiceManager serviceManager = this.coreManager.getManager(ServiceManager.class);

            if (LogicalUtil.isAnyEqual(startup, StartupType.STEP_AFTER_SERVICE)) {
                PathRecord path = new PathRecord(List.of(new IdentifierRecord("Services")));

                InfoObject services = objectManager.get(path);

                Set<InfoSummaryDefinition> infoSummaries = services.queryChild(new InfoWildcardDefinition("*"));

                Set<UUID> autoServices = new HashSet<>();
                Set<UUID> autoDelayServices = new HashSet<>();

                for (InfoSummaryDefinition infoSummary : infoSummaries) {
                    UUID id = infoSummary.getId();

                    InfoObject service = services.getChild(new IdentifierRecord(id));

                    if (service.getOpened() != 0) {
                        ServiceRepositoryObject serviceRepository = memoryManager.getServiceRepository();

                        if (serviceRepository.contain(service.getId())) {
                            continue;
                        } else {
                            throw new StatusRelationshipErrorException();
                        }
                    }

                    service.open(InfoOpenAttributeType.OPEN_EXCLUSIVE);

                    ServiceContentObject content = (ServiceContentObject) service.getContent();

                    long start = content.getStart();
                    if (LogicalUtil.isAnyEqual(start, ServiceStartType.AUTO)) {
                        autoServices.add(service.getId());
                    } else if (LogicalUtil.isAnyEqual(start, ServiceStartType.AUTO_DELAY)) {
                        autoDelayServices.add(service.getId());
                    }

                    service.close();
                }

                for (UUID autoService : autoServices) {
                    serviceManager.start(autoService);
                }
                for (UUID autoDelayService : autoDelayServices) {
                    serviceManager.start(autoDelayService);
                }
            }
        };
    }

    private final BootStartConsumer start;

    @Override
    public void resolve(BootProcessorMediator processorMediator) {
        processorMediator.getStarts().add(this.start);
    }

    @Override
    public int order() {
        return 0;
    }
}
