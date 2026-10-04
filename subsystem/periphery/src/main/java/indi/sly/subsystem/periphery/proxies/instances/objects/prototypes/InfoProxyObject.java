package indi.sly.subsystem.periphery.proxies.instances.objects.prototypes;

import indi.sly.subsystem.periphery.proxies.instances.objects.values.InfoSummaryRecord;
import indi.sly.subsystem.periphery.proxies.instances.objects.values.InfoWildcardRecord;
import indi.sly.subsystem.periphery.proxies.prototypes.AProxyObject;
import indi.sly.subsystem.periphery.proxies.prototypes.RemoteObject;
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
        RemoteObject remote = this.remote.invoke("getId", UUID.class);

        return this.factory.getValue(UUID.class, remote);
    }

    public UUID getType() {
        RemoteObject remote = this.remote.invoke("getType", UUID.class);

        return this.factory.getValue(UUID.class, remote);
    }

    public long getOpened() {
        RemoteObject remote = this.remote.invoke("getOpened", Long.class);

        return this.factory.getValue(Long.class, remote);
    }

    public String getName() {
        RemoteObject remote = this.remote.invoke("getName", String.class);

        return this.factory.getValue(String.class, remote);
    }

    public PathRecord getPath() {
        RemoteObject remote = this.remote.invoke("getPath", PathRecord.class);

        return this.factory.getValue(PathRecord.class, remote);
    }

    public UUID getIndex() {
        RemoteObject remote = this.remote.invoke("getIndex", UUID.class);

        return this.factory.getValue(UUID.class, remote);
    }

    public InfoProxyObject getParent() {
        RemoteObject remote = this.remote.invoke("getParent", InfoProxyObject.class);

        return this.factory.buildProxy(InfoProxyObject.class, remote);
    }

    public Map<Long, Long> getDate() {
        RemoteObject remote = this.remote.invoke("getDate", Map.class);

        return this.factory.getMapValue(Long.class, Long.class, remote);
    }

    public SecurityDescriptorProxyObject getSecurityDescriptor() {
        RemoteObject remote = this.remote.invoke("getSecurityDescriptor", SecurityDescriptorProxyObject.class);

        return this.factory.buildProxy(SecurityDescriptorProxyObject.class, remote);
    }

    public DumpProxyObject dump() {
        RemoteObject remote = this.remote.invoke("dump", DumpProxyObject.class);

        return this.factory.buildProxy(DumpProxyObject.class, remote);
    }

    public UUID open(long openAttribute, Object... arguments) {
        RemoteObject remote = this.remote.invoke("open", UUID.class, openAttribute, arguments);

        return this.factory.getValue(UUID.class, remote);
    }

    public void close() {
        RemoteObject remote = this.remote.invoke("close", Void.class);
    }

    public long getOpenAttribute() {
        RemoteObject remote = this.remote.invoke("getOpenAttribute", Long.class);

        return this.factory.getValue(Long.class, remote);
    }

    public InfoProxyObject createChild(UUID childType, IdentifierRecord identifier) {
        RemoteObject remote = this.remote.invoke("createChild", UUID.class, childType, identifier);

        return this.factory.buildProxy(InfoProxyObject.class, remote);
    }

    public InfoProxyObject getChild(IdentifierRecord identifier) {
        RemoteObject remote = this.remote.invoke("getChild", InfoProxyObject.class, identifier);

        return this.factory.buildProxy(InfoProxyObject.class, remote);
    }

    public void deleteChild(IdentifierRecord identifier) {
        this.remote.invoke("deleteChild", Void.class, identifier);
    }

    public Set<InfoSummaryRecord> queryChild(InfoWildcardRecord wildcard) {
        RemoteObject remote = this.remote.invoke("queryChild", Set.class, wildcard);

        return this.factory.getSetValue(InfoSummaryRecord.class, remote);
    }

    public void renameChild(IdentifierRecord oldIdentifier, IdentifierRecord newIdentifier) {
        this.remote.invoke("renameChild", Void.class, oldIdentifier, newIdentifier);
    }

    public Map<String, String> readProperties() {
        RemoteObject remote = this.remote.invoke("readProperties", Map.class);

        return this.factory.getMapValue(String.class, String.class, remote);
    }

    public void writeProperties(Map<String, String> properties) {
        this.remote.invoke("writeProperties", Void.class, properties);
    }
}
