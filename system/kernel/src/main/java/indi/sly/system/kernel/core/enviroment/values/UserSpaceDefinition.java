package indi.sly.system.kernel.core.enviroment.values;

import java.util.Map;
import java.util.Stack;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import indi.sly.system.common.values.ADefinition;
import indi.sly.system.kernel.memory.caches.values.InfoCacheDefinition;
import indi.sly.system.kernel.objects.prototypes.InfoObject;
import indi.sly.system.kernel.processes.prototypes.ThreadObject;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;

import javax.inject.Named;

@Named
@Scope(value = ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class UserSpaceDefinition extends ADefinition<UserSpaceDefinition> {
    public UserSpaceDefinition() {
        this.infoLock = new ReentrantReadWriteLock();
        this.infoCaches = new ConcurrentHashMap<>();
        this.infos = new ConcurrentHashMap<>();
        this.threads = new Stack<>();
    }

    private final ReadWriteLock infoLock;
    private final Map<UUID, InfoCacheDefinition> infoCaches;
    private final Map<UUID, InfoObject> infos;

    public ReadWriteLock getInfoLock() {
        return this.infoLock;
    }

    public Map<UUID, InfoCacheDefinition> getInfoCaches() {
        return this.infoCaches;
    }

    public Map<UUID, InfoObject> getInfos() {
        return this.infos;
    }

    private final Stack<ThreadObject> threads;

    public Stack<ThreadObject> getThreads() {
        return this.threads;
    }
}
