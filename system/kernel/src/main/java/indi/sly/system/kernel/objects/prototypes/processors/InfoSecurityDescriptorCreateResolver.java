package indi.sly.system.kernel.objects.prototypes.processors;

import indi.sly.system.common.supports.ObjectUtil;
import indi.sly.system.kernel.objects.TypeManager;
import indi.sly.system.kernel.objects.infotypes.prototypes.TypeObject;
import indi.sly.system.kernel.objects.infotypes.values.TypeInitializerAttributeType;
import indi.sly.system.kernel.objects.lang.InfoProcessorCreateChildFunction;
import indi.sly.system.kernel.objects.prototypes.wrappers.InfoProcessorMediator;
import indi.sly.system.kernel.objects.values.InfoEntity;
import indi.sly.system.kernel.processes.ProcessManager;
import indi.sly.system.kernel.processes.prototypes.ProcessObject;
import indi.sly.system.kernel.security.values.SecurityDescriptorDefinition;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;

import jakarta.inject.Named;

@Named
@Scope(value = ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class InfoSecurityDescriptorCreateResolver extends AInfoResolver {
    public InfoSecurityDescriptorCreateResolver() {
        this.createChild = (childInfo, info, type, status, childTypeID, identification) -> {
            TypeManager typeManager = this.factoryManager.getManager(TypeManager.class);
            TypeObject childType = typeManager.get(childTypeID);

            if (childType.isTypeInitializerAttributesExist(TypeInitializerAttributeType.HAS_AUDIT)
                    || childType.isTypeInitializerAttributesExist(TypeInitializerAttributeType.HAS_PERMISSION)) {
                SecurityDescriptorDefinition securityDescriptor = new SecurityDescriptorDefinition();

                if (childType.isTypeInitializerAttributesExist(TypeInitializerAttributeType.HAS_PERMISSION)) {
                    ProcessManager processManager = this.factoryManager.getManager(ProcessManager.class);
                    ProcessObject process = processManager.getCurrent();

                    securityDescriptor.getOwners().add(process.getToken().getAccountID());
                    securityDescriptor.setInherit(true);
                    securityDescriptor.setHasChild(childType.isTypeInitializerAttributesExist(TypeInitializerAttributeType.HAS_CHILD));
                }

                childInfo.setSecurityDescriptor(ObjectUtil.transferToByteArray(securityDescriptor));
            } else {
                childInfo.setSecurityDescriptor(null);
            }

            return childInfo;
        };
    }

    private final InfoProcessorCreateChildFunction createChild;

    @Override
    public void resolve(InfoEntity info, InfoProcessorMediator processorMediator) {
        if (ObjectUtil.allNotNull(info)) {
            TypeManager typeManager = this.factoryManager.getManager(TypeManager.class);
            TypeObject type = typeManager.get(info.getType());

            if (!type.isTypeInitializerAttributesExist(TypeInitializerAttributeType.HAS_AUDIT) && !type.isTypeInitializerAttributesExist(TypeInitializerAttributeType.HAS_PERMISSION)) {
                return;
            }
        }

        processorMediator.getCreateChilds().add(this.createChild);
    }

    @Override
    public int order() {
        return 3;
    }
}
