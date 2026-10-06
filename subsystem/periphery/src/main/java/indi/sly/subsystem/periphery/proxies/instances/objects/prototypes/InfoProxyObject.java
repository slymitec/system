package indi.sly.subsystem.periphery.proxies.instances.objects.prototypes;

import indi.sly.subsystem.periphery.proxies.instances.objects.values.InfoSummaryRecord;
import indi.sly.subsystem.periphery.proxies.instances.objects.values.InfoWildcardRecord;
import indi.sly.subsystem.periphery.proxies.prototypes.AProxyObject;
import indi.sly.subsystem.periphery.proxies.prototypes.RemoteObject;
import indi.sly.system.common.lang.ConditionParametersException;
import indi.sly.system.common.supports.ObjectUtil;
import indi.sly.system.common.values.IdentifierRecord;
import indi.sly.system.common.values.PathRecord;
import jakarta.inject.Named;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;

import java.util.*;

@Named
@Scope(value = ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class InfoProxyObject extends AProxyObject {
    public UUID getId() {
        RemoteObject remote = this.remote.invoke("getId");

        return this.factory.getValue(UUID.class, remote);
    }

    public UUID getType() {
        RemoteObject remote = this.remote.invoke("getType");

        return this.factory.getValue(UUID.class, remote);
    }

    public long getOpened() {
        RemoteObject remote = this.remote.invoke("getOpened");

        return this.factory.getValue(Long.class, remote);
    }

    public String getName() {
        RemoteObject remote = this.remote.invoke("getName");

        return this.factory.getValue(String.class, remote);
    }

    public PathRecord getPath() {
        RemoteObject remote = this.remote.invoke("getPath");

        return this.factory.getValue(PathRecord.class, remote);
    }

    public UUID getIndex() {
        RemoteObject remote = this.remote.invoke("getIndex");

        return this.factory.getValue(UUID.class, remote);
    }

    public InfoProxyObject getParent() {
        RemoteObject remote = this.remote.invoke("getParent");

        return this.factory.buildProxy(InfoProxyObject.class, remote);
    }

    public Map<Long, Long> getDate() {
        RemoteObject remote = this.remote.invoke("getDate");

        return this.factory.getMapValue(Long.class, Long.class, remote);
    }

    public SecurityDescriptorProxyObject getSecurityDescriptor() {
        RemoteObject remote = this.remote.invoke("getSecurityDescriptor");

        return this.factory.buildProxy(SecurityDescriptorProxyObject.class, remote);
    }

    public DumpProxyObject dump() {
        RemoteObject remote = this.remote.invoke("dump");

        return this.factory.buildProxy(DumpProxyObject.class, remote);
    }

    public UUID open(long openAttribute, Object... arguments) {
        RemoteObject remote = this.remote.invoke("open", openAttribute, arguments);

        return this.factory.getValue(UUID.class, remote);
    }

    public void close() {
        this.remote.invoke("close");
    }

    public long getOpenAttribute() {
        RemoteObject remote = this.remote.invoke("getOpenAttribute");

        return this.factory.getValue(Long.class, remote);
    }

    public InfoProxyObject createChild(UUID childType, IdentifierRecord identifier) {
        RemoteObject remote = this.remote.invoke("createChild", childType, identifier);

        return this.factory.buildProxy(InfoProxyObject.class, remote);
    }

    public InfoProxyObject getChild(IdentifierRecord identifier) {
        RemoteObject remote = this.remote.invoke("getChild", identifier);

        return this.factory.buildProxy(InfoProxyObject.class, remote);
    }

    public void deleteChild(IdentifierRecord identifier) {
        this.remote.invoke("deleteChild", identifier);
    }

    public Set<InfoSummaryRecord> queryChild(InfoWildcardRecord wildcard) {
        RemoteObject remote = this.remote.invoke("queryChild", wildcard);

        return this.factory.getSetValue(InfoSummaryRecord.class, remote);
    }

    public void renameChild(IdentifierRecord oldIdentifier, IdentifierRecord newIdentifier) {
        this.remote.invoke("renameChild", oldIdentifier, newIdentifier);
    }

    public Map<String, String> readProperties() {
        RemoteObject remote = this.remote.invoke("readProperties");

        return this.factory.getMapValue(String.class, String.class, remote);
    }

    public void writeProperties(Map<String, String> properties) {
        this.remote.invoke("writeProperties", properties);
    }

    public <T extends AInfoContentProxyObject> T getContent(Class<T> contentClazz) {
        if (ObjectUtil.isAnyNull(contentClazz)) {
            throw new ConditionParametersException();
        }

        RemoteObject remote = this.remote.invoke("getContent", contentClazz);

        return this.factory.buildProxy(contentClazz, remote);
    }
}
