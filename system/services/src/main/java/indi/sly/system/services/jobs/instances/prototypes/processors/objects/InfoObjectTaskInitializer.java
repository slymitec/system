package indi.sly.system.services.jobs.instances.prototypes.processors.objects;

import indi.sly.system.common.lang.ConditionParametersException;
import indi.sly.system.common.supports.ClassUtil;
import indi.sly.system.common.supports.ObjectUtil;
import indi.sly.system.common.values.IdentifierRecord;
import indi.sly.system.kernel.objects.ObjectManager;
import indi.sly.system.kernel.objects.prototypes.AInfoContentObject;
import indi.sly.system.kernel.objects.prototypes.DumpObject;
import indi.sly.system.kernel.objects.prototypes.InfoObject;
import indi.sly.system.kernel.objects.prototypes.SecurityDescriptorObject;
import indi.sly.system.kernel.objects.values.InfoWildcardDefinition;
import indi.sly.system.services.core.values.TransactionType;
import indi.sly.system.services.jobs.instances.prototypes.processors.ATaskInitializer;
import indi.sly.system.services.jobs.lang.TaskRunConsumer;
import indi.sly.system.services.jobs.prototypes.TaskContentObject;
import indi.sly.system.services.jobs.values.HandleContextDefinition;
import indi.sly.system.services.jobs.values.TaskDefinition;
import jakarta.inject.Named;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@Named
@Scope(value = ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class InfoObjectTaskInitializer extends ATaskInitializer {
    public InfoObjectTaskInitializer() {
        this.cacheableObjectFunction = (handle) -> this.coreManager.getManager(ObjectManager.class).getFactory().rebuildDump(handle);

        this.register("getId", this::getId, TransactionType.INDEPENDENCE);
        this.register("getType", this::getType, TransactionType.INDEPENDENCE);
        this.register("getOpened", this::getOpened, TransactionType.INDEPENDENCE);
        this.register("getName", this::getName, TransactionType.INDEPENDENCE);
        this.register("getPath", this::getPath, TransactionType.INDEPENDENCE);
        this.register("getIndex", this::getIndex, TransactionType.INDEPENDENCE);
        this.register("getParent", this::getParent, TransactionType.INDEPENDENCE);
        this.register("getDate", this::getDate, TransactionType.INDEPENDENCE);
        this.register("getSecurityDescriptor", this::getSecurityDescriptor, TransactionType.INDEPENDENCE);
        this.register("dump", this::dump, TransactionType.INDEPENDENCE);
        this.register("open", this::open, TransactionType.INDEPENDENCE);
        this.register("close", this::close, TransactionType.INDEPENDENCE);
        this.register("getOpenAttribute", this::getOpenAttribute, TransactionType.INDEPENDENCE);
        this.register("createChild", this::createChild, TransactionType.INDEPENDENCE);
        this.register("getChild", this::getChild, TransactionType.INDEPENDENCE);
        this.register("deleteChild", this::deleteChild, TransactionType.INDEPENDENCE);
        this.register("queryChild", this::queryChild, TransactionType.INDEPENDENCE);
        this.register("renameChild", this::renameChild, TransactionType.INDEPENDENCE);
        this.register("readProperties", this::readProperties, TransactionType.INDEPENDENCE);
        this.register("writeProperties", this::writeProperties, TransactionType.INDEPENDENCE);
        this.register("getContent", this::getContent, TransactionType.INDEPENDENCE);
    }

    @Override
    public void start(TaskDefinition task) {
    }

    @Override
    public void finish(TaskDefinition task) {
    }

    private void getId(TaskRunConsumer run, TaskContentObject content) {
        InfoObject info = content.getCacheableObject();

        content.setResult(info.getId());
    }

    private void getType(TaskRunConsumer run, TaskContentObject content) {
        InfoObject info = content.getCacheableObject();

        content.setResult(info.getType());
    }

    private void getOpened(TaskRunConsumer run, TaskContentObject content) {
        InfoObject info = content.getCacheableObject();

        content.setResult(info.getOpened());
    }

    private void getName(TaskRunConsumer run, TaskContentObject content) {
        InfoObject info = content.getCacheableObject();

        content.setResult(info.getName());
    }

    private void getPath(TaskRunConsumer run, TaskContentObject content) {
        InfoObject info = content.getCacheableObject();

        content.setResult(info.getPath());
    }

    private void getIndex(TaskRunConsumer run, TaskContentObject content) {
        InfoObject info = content.getCacheableObject();

        content.setResult(info.getIndex());
    }

    private void getParent(TaskRunConsumer run, TaskContentObject content) {
        InfoObject info = content.getCacheableObject();

        InfoObject parentInfo = info.getParent();

        UUID handle = parentInfo.cache();

        HandleContextDefinition handleContext = new HandleContextDefinition(ClassUtil.getSimpleName(info.getClass()), handle);

        content.setResult(handleContext);
    }

    private void getDate(TaskRunConsumer run, TaskContentObject content) {
        InfoObject info = content.getCacheableObject();

        content.setResult(info.getDate());
    }

    private void getSecurityDescriptor(TaskRunConsumer run, TaskContentObject content) {
        InfoObject info = content.getCacheableObject();

        SecurityDescriptorObject securityDescriptor = info.getSecurityDescriptor();

        UUID handle = securityDescriptor.cache();

        HandleContextDefinition handleContext = new HandleContextDefinition(ClassUtil.getSimpleName(securityDescriptor.getClass()), handle);

        content.setResult(handleContext);
    }

    private void dump(TaskRunConsumer run, TaskContentObject content) {
        InfoObject info = content.getCacheableObject();

        DumpObject dump = info.dump();

        UUID handle = dump.cache();

        HandleContextDefinition handleContext = new HandleContextDefinition(ClassUtil.getSimpleName(dump.getClass()), handle);

        content.setResult(handleContext);
    }

    private void open(TaskRunConsumer run, TaskContentObject content) {
        InfoObject info = content.getCacheableObject();

        List<String> parameters = content.getParameters();

        if (parameters.isEmpty()) {
            throw new ConditionParametersException();
        }

        long openAttribute = ObjectUtil.transferFromString(Long.class, parameters.getFirst());
        Object[] arguments = new Object[parameters.size() - 1];

        for (int i = 1; i < parameters.size(); i++) {
            arguments[i - 1] = ObjectUtil.transferFromString(Long.class, parameters.get(i));
        }

        UUID index = info.open(openAttribute, arguments);

        content.setResult(index);
    }

    private void close(TaskRunConsumer run, TaskContentObject content) {
        InfoObject info = content.getCacheableObject();

        info.close();
    }

    private void getOpenAttribute(TaskRunConsumer run, TaskContentObject content) {
        InfoObject info = content.getCacheableObject();

        content.setResult(info.getOpenAttribute());
    }

    private void createChild(TaskRunConsumer run, TaskContentObject content) {
        InfoObject info = content.getCacheableObject();

        List<String> parameters = content.getParameters();

        if (parameters.size() < 2) {
            throw new ConditionParametersException();
        }

        UUID childType = ObjectUtil.transferFromString(UUID.class, parameters.getFirst());
        IdentifierRecord identifier = ObjectUtil.transferFromString(IdentifierRecord.class, parameters.get(1));

        InfoObject childInfo = info.createChild(childType, identifier);

        UUID handle = childInfo.cache();

        HandleContextDefinition handleContext = new HandleContextDefinition(ClassUtil.getSimpleName(childInfo.getClass()), handle);

        content.setResult(handleContext);
    }

    private void getChild(TaskRunConsumer run, TaskContentObject content) {
        InfoObject info = content.getCacheableObject();

        List<String> parameters = content.getParameters();

        if (parameters.isEmpty()) {
            throw new ConditionParametersException();
        }

        IdentifierRecord identifier = ObjectUtil.transferFromString(IdentifierRecord.class, parameters.getFirst());

        InfoObject childInfo = info.getChild(identifier);

        UUID handle = childInfo.cache();

        HandleContextDefinition handleContext = new HandleContextDefinition(ClassUtil.getSimpleName(childInfo.getClass()), handle);

        content.setResult(handleContext);
    }

    private void deleteChild(TaskRunConsumer run, TaskContentObject content) {
        InfoObject info = content.getCacheableObject();

        List<String> parameters = content.getParameters();

        if (parameters.isEmpty()) {
            throw new ConditionParametersException();
        }

        IdentifierRecord identifier = ObjectUtil.transferFromString(IdentifierRecord.class, parameters.getFirst());

        info.deleteChild(identifier);
    }

    private void queryChild(TaskRunConsumer run, TaskContentObject content) {
        InfoObject info = content.getCacheableObject();

        List<String> parameters = content.getParameters();

        if (parameters.isEmpty()) {
            throw new ConditionParametersException();
        }

        InfoWildcardDefinition wildcard = ObjectUtil.transferFromString(InfoWildcardDefinition.class, parameters.getFirst());

        content.setResult(info.queryChild(wildcard));
    }

    private void renameChild(TaskRunConsumer run, TaskContentObject content) {
        InfoObject info = content.getCacheableObject();

        List<String> parameters = content.getParameters();

        if (parameters.size() < 2) {
            throw new ConditionParametersException();
        }

        IdentifierRecord oldIdentifier = ObjectUtil.transferFromString(IdentifierRecord.class, parameters.getFirst());
        IdentifierRecord newIdentifier = ObjectUtil.transferFromString(IdentifierRecord.class, parameters.get(1));

        info.renameChild(oldIdentifier, newIdentifier);
    }

    private void readProperties(TaskRunConsumer run, TaskContentObject content) {
        InfoObject info = content.getCacheableObject();

        content.setResult(info.readProperties());
    }

    private void writeProperties(TaskRunConsumer run, TaskContentObject content) {
        InfoObject info = content.getCacheableObject();

        List<String> parameters = content.getParameters();

        if (parameters.isEmpty()) {
            throw new ConditionParametersException();
        }

        Map<String, String> properties = ObjectUtil.transferMapFromString(String.class, String.class, parameters.getFirst());

        info.writeProperties(properties);
    }

    private void getContent(TaskRunConsumer run, TaskContentObject content) {
        InfoObject info = content.getCacheableObject();

        AInfoContentObject infoContent = info.getContent();

        UUID handle = infoContent.cache();

        HandleContextDefinition handleContext = new HandleContextDefinition(ClassUtil.getSimpleName(infoContent.getClass()), handle);

        content.setResult(handleContext);
    }
}
