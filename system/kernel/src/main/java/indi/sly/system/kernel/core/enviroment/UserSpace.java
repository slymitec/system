package indi.sly.system.kernel.core.enviroment;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import javax.inject.Named;

import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;

import indi.sly.system.kernel.memory.caches.InfoObjectCacheDefinition;
import indi.sly.system.kernel.objects.prototypes.InfoObject;
import indi.sly.system.kernel.processes.threads.ThreadObject;

@Named
@Scope(scopeName = ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class UserSpace {
    public UserSpace() {
        this.kernelObjectLock = new ReentrantReadWriteLock();
        this.cachedKernelObjectDefinitions = new ConcurrentHashMap<>();
        this.kernelObjects = new ConcurrentHashMap<>();
    }

    private final ReadWriteLock kernelObjectLock;
    private final Map<UUID, InfoObjectCacheDefinition> cachedKernelObjectDefinitions;
    private final Map<UUID, InfoObject> kernelObjects;
    private ThreadObject currentThread;

    public ReadWriteLock getKernelObjectLock() {
        return this.kernelObjectLock;
    }

    public Map<UUID, InfoObjectCacheDefinition> getCachedKernelObjectDefinitions() {
        return cachedKernelObjectDefinitions;
    }

    public Map<UUID, InfoObject> getKernelObjects() {
        return kernelObjects;
    }

    public ThreadObject getCurrentThread() {
        return currentThread;
    }

    public void setCurrentThread(ThreadObject currentThread) {
        this.currentThread = currentThread;
    }
}
