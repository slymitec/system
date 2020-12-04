package indi.sly.system.kernel.core.enviroment;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import indi.sly.system.kernel.memory.caches.values.InfoCacheDefinition;
import indi.sly.system.kernel.objects.prototypes.InfoObject;
import indi.sly.system.kernel.processes.prototypes.ThreadObject;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;

import javax.inject.Named;

@Named
@Scope(value = ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class UserSpace {
    public UserSpace() {
        this.infoObjectLock = new ReentrantReadWriteLock();
        this.cachedInfoObjectDefinitions = new ConcurrentHashMap<>();
        this.infoObjects = new ConcurrentHashMap<>();
    }

    private final ReadWriteLock infoObjectLock;
    private final Map<UUID, InfoCacheDefinition> cachedInfoObjectDefinitions;
    private final Map<UUID, InfoObject> infoObjects;

    public ReadWriteLock getInfoObjectLock() {
        return this.infoObjectLock;
    }

    public Map<UUID, InfoCacheDefinition> getCachedInfoObjectDefinitions() {
        return cachedInfoObjectDefinitions;
    }

    public Map<UUID, InfoObject> getInfoObjects() {
        return infoObjects;
    }

    private ThreadObject thread;

    public ThreadObject getThread() {
        return this.thread;
    }

    public void setThread(ThreadObject thread) {
        this.thread = thread;
    }
}
