package indi.sly.system.services.job.instances.prototypes.processors;

import indi.sly.system.common.lang.ConditionParametersException;
import indi.sly.system.common.supports.StringUtil;
import indi.sly.system.common.values.IdentificationDefinition;
import indi.sly.system.kernel.core.date.prototypes.DateTimeObject;
import indi.sly.system.kernel.core.enviroment.values.SpaceType;
import indi.sly.system.kernel.core.prototypes.SystemVersionObject;
import indi.sly.system.kernel.objects.ObjectManager;
import indi.sly.system.kernel.objects.prototypes.InfoObject;
import indi.sly.system.services.job.lang.JobRunConsumer;
import indi.sly.system.services.job.prototypes.JobContentObject;
import indi.sly.system.services.job.prototypes.processors.AJobInitializer;
import indi.sly.system.services.job.values.JobDefinition;
import indi.sly.system.services.job.values.JobTransactionType;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;

import javax.inject.Named;
import java.util.List;
import java.util.UUID;

@Named
@Scope(value = ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class PointerJobInitializer extends AJobInitializer {
    public PointerJobInitializer() {
        this.register("coreGetDate", this::coreGetDate, JobTransactionType.INDEPENDENCE);
        this.register("coreGetVersion", this::coreGetVersion, JobTransactionType.INDEPENDENCE);

        this.register("objectGet", this::objectGet, JobTransactionType.INDEPENDENCE);

        this.register("processGetCurrent", this::NULL, JobTransactionType.INDEPENDENCE);
        this.register("processGet", this::NULL, JobTransactionType.INDEPENDENCE);
        this.register("processCreate", this::NULL, JobTransactionType.INDEPENDENCE);
        this.register("processEndCurrent", this::NULL, JobTransactionType.INDEPENDENCE);
        this.register("processEnd", this::NULL, JobTransactionType.INDEPENDENCE);

        this.register("sessionGetAndOpen", this::NULL, JobTransactionType.INDEPENDENCE);
        this.register("sessionEnd", this::NULL, JobTransactionType.INDEPENDENCE);

        this.register("userGetCurrentAccount", this::NULL, JobTransactionType.INDEPENDENCE);
        this.register("userGetAccount", this::NULL, JobTransactionType.INDEPENDENCE);
        this.register("userGetGroup", this::NULL, JobTransactionType.INDEPENDENCE);
        this.register("userCreateAccount", this::NULL, JobTransactionType.INDEPENDENCE);
        this.register("userCreateGroup", this::NULL, JobTransactionType.INDEPENDENCE);
        this.register("userDeleteAccount", this::NULL, JobTransactionType.INDEPENDENCE);
        this.register("userDeleteGroup", this::NULL, JobTransactionType.INDEPENDENCE);
        this.register("userAuthorize", this::NULL, JobTransactionType.INDEPENDENCE);
    }

    @Override
    public void start(JobDefinition job) {
    }

    @Override
    public void finish(JobDefinition job) {
    }

    private void coreGetDate(JobRunConsumer run, JobContentObject content) {
        String parameter_ResultName = content.getParameterOrDefault(String.class, "pointerName", null);
        if (StringUtil.isNameIllegal(parameter_ResultName)) {
            throw new ConditionParametersException();
        }

        DateTimeObject dateTime = this.factoryManager.getCorePrototypeRepository().get(SpaceType.KERNEL, DateTimeObject.class);

        UUID id = content.setCache(parameter_ResultName, dateTime);
        content.setResult("pointerID", id);
    }

    private void coreGetVersion(JobRunConsumer run, JobContentObject content) {
        String parameter_ResultName = content.getParameterOrDefault(String.class, "pointerName", null);
        if (StringUtil.isNameIllegal(parameter_ResultName)) {
            throw new ConditionParametersException();
        }

        SystemVersionObject systemVersion = this.factoryManager.getCorePrototypeRepository().get(SpaceType.KERNEL,
                SystemVersionObject.class);

        UUID id = content.setCache(parameter_ResultName, systemVersion);
        content.setResult("pointerID", id);
    }

    private void objectGet(JobRunConsumer run, JobContentObject content) {
        String parameter_ResultName = content.getParameterOrDefault(String.class, "pointerName", null);
        if (StringUtil.isNameIllegal(parameter_ResultName)) {
            throw new ConditionParametersException();
        }
        String parameter_Identifications = content.getParameterOrDefault(String.class, "identifications", null);
        List<IdentificationDefinition> identifications = StringUtil.parseIdentifications(parameter_Identifications);

        ObjectManager objectManager = this.factoryManager.getManager(ObjectManager.class);

        InfoObject info = objectManager.get(identifications);

        UUID id = content.setCache(parameter_ResultName, info);
        content.setResult("pointerID", id);
    }

    private void NULL(JobRunConsumer run, JobContentObject content) {
    }
}
