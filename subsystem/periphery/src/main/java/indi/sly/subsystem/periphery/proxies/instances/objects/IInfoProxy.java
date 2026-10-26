package indi.sly.subsystem.periphery.proxies.instances.objects;

import indi.sly.subsystem.periphery.proxies.instances.IProxy;
import indi.sly.subsystem.periphery.proxies.instances.objects.values.InfoSummaryRecord;
import indi.sly.subsystem.periphery.proxies.instances.objects.values.InfoWildcardRecord;
import indi.sly.system.common.values.IdentifierRecord;
import indi.sly.system.common.values.PathRecord;

import java.util.Map;
import java.util.Set;
import java.util.UUID;

public interface IInfoProxy extends IProxy {
    UUID getId();

    UUID getType();

    long getOpened();

    String getName();

    PathRecord getPath();

    UUID getIndex();

    IInfoProxy getParent();

    Map<Long, Long> getDate();

    ISecurityDescriptorProxy getSecurityDescriptor();

    IDumpProxy dump();

    UUID open(long openAttribute, Object... arguments);

    void close();

    long getOpenAttribute();

    IInfoProxy createChild(UUID childType, IdentifierRecord identifier);

    IInfoProxy getChild(IdentifierRecord identifier);

    void deleteChild(IdentifierRecord identifier);

    Set<InfoSummaryRecord> queryChild(InfoWildcardRecord wildcard);

    void renameChild(IdentifierRecord oldIdentifier, IdentifierRecord newIdentifier);

    Map<String, String> readProperties();

    void writeProperties(Map<String, String> properties);

    <T extends IInfoContentProxy> T getContent(Class<T> contentClazz);
}