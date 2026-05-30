package indi.sly.system.services.jobs.instances.prototypes.processors.objects;

import indi.sly.system.common.lang.ConditionParametersException;
import indi.sly.system.common.supports.ClassUtil;
import indi.sly.system.common.supports.ObjectUtil;
import indi.sly.system.common.values.IdentifierDefinition;
import indi.sly.system.common.values.PathDefinition;
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
        this.cacheableObjectFunction = (handle) -> this.coreManager.getManager(ObjectManager.class).getFactory().rebuildInfo(handle);

        this.register("getId", this::getId, TransactionType.WHATEVER);
        this.register("getType", this::getType, TransactionType.WHATEVER);
        this.register("getOpened", this::getOpened, TransactionType.WHATEVER);
        this.register("getName", this::getName, TransactionType.WHATEVER);
        this.register("getPath", this::getPath, TransactionType.WHATEVER);
        this.register("getIndex", this::getIndex, TransactionType.WHATEVER);
        this.register("getParent", this::getParent, TransactionType.WHATEVER);
        this.register("getDate", this::getDate, TransactionType.WHATEVER);
        this.register("getSecurityDescriptor", this::getSecurityDescriptor, TransactionType.WHATEVER);
        this.register("dump", this::dump, TransactionType.WHATEVER);
        this.register("open", this::open, TransactionType.WHATEVER);
        this.register("close", this::close, TransactionType.WHATEVER);
        this.register("getOpenAttribute", this::getOpenAttribute, TransactionType.WHATEVER);
        this.register("createChild", this::createChild, TransactionType.WHATEVER);
        this.register("getChild", this::getChild, TransactionType.WHATEVER);
        this.register("deleteChild", this::deleteChild, TransactionType.WHATEVER);
        this.register("queryChild", this::queryChild, TransactionType.WHATEVER);
        this.register("renameChild", this::renameChild, TransactionType.WHATEVER);
        this.register("readProperties", this::readProperties, TransactionType.WHATEVER);
        this.register("writeProperties", this::writeProperties, TransactionType.WHATEVER);
        this.register("getContent", this::getContent, TransactionType.WHATEVER);
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

        HandleContextDefinition handleContext = new HandleContextDefinition(ClassUtil.getSimpleName(info.getClass()), handle);

        content.setResult(handleContext);
    }

    private void dump(TaskRunConsumer run, TaskContentObject content) {
        InfoObject info = content.getCacheableObject();

        DumpObject dump = info.dump();

        UUID handle = dump.cache();

        HandleContextDefinition handleContext = new HandleContextDefinition(ClassUtil.getSimpleName(info.getClass()), handle);

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
        IdentifierDefinition identifier = ObjectUtil.transferFromString(IdentifierDefinition.class, parameters.get(1));

        InfoObject childInfo = info.createChild(childType, identifier);

        UUID handle = childInfo.cache();

        HandleContextDefinition handleContext = new HandleContextDefinition(ClassUtil.getSimpleName(info.getClass()), handle);

        content.setResult(handleContext);
    }

    private void getChild(TaskRunConsumer run, TaskContentObject content) {
        InfoObject info = content.getCacheableObject();

        List<String> parameters = content.getParameters();

        if (parameters.isEmpty()) {
            throw new ConditionParametersException();
        }

        IdentifierDefinition identifier = ObjectUtil.transferFromString(IdentifierDefinition.class, parameters.getFirst());

        InfoObject childInfo = info.getChild(identifier);

        UUID handle = childInfo.cache();

        HandleContextDefinition handleContext = new HandleContextDefinition(ClassUtil.getSimpleName(info.getClass()), handle);

        content.setResult(handleContext);
    }

    private void deleteChild(TaskRunConsumer run, TaskContentObject content) {
        InfoObject info = content.getCacheableObject();

        List<String> parameters = content.getParameters();

        if (parameters.isEmpty()) {
            throw new ConditionParametersException();
        }

        IdentifierDefinition identifier = ObjectUtil.transferFromString(IdentifierDefinition.class, parameters.getFirst());

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

        IdentifierDefinition oldIdentifier = ObjectUtil.transferFromString(IdentifierDefinition.class, parameters.getFirst());
        IdentifierDefinition newIdentifier = ObjectUtil.transferFromString(IdentifierDefinition.class, parameters.get(1));

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

        HandleContextDefinition handleContext = new HandleContextDefinition(ClassUtil.getSimpleName(info.getClass()), handle);

        content.setResult(handleContext);
    }
}
