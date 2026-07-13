package indi.sly.system.kernel.core.prototypes;

import indi.sly.system.kernel.core.date.prototypes.DateTimeObject;
import indi.sly.system.kernel.core.environment.values.CacheDurationType;
import indi.sly.system.kernel.core.systemversion.prototypes.SystemVersionObject;
import indi.sly.system.kernel.core.systemversion.values.SystemVersionCacheEntity;
import indi.sly.system.kernel.core.values.NoneCacheEntity;
import indi.sly.system.kernel.memory.MemoryManager;
import indi.sly.system.kernel.memory.repositories.prototypes.CacheRepositoryObject;
import jakarta.inject.Named;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;

import java.io.File;
import java.io.IOException;
import java.net.JarURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.security.CodeSource;
import java.util.UUID;
import java.util.jar.Attributes;
import java.util.jar.JarFile;

@Named
@Scope(value = ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class CoreFactory extends AFactory {
    @Override
    public void init() {
    }

    private SystemVersionObject createSystemVersion(SystemVersionCacheEntity cache) {
        SystemVersionObject systemVersionObject = this.coreManager.create(SystemVersionObject.class);

        systemVersionObject.setCache(cache);

        return systemVersionObject;
    }

    private String getImplementationVersion(JarFile jarFile) throws IOException {
        return jarFile.getManifest().getMainAttributes().getValue(Attributes.Name.IMPLEMENTATION_VERSION);
    }

    public SystemVersionObject buildSystemVersion() {
        SystemVersionCacheEntity cache = new SystemVersionCacheEntity();

        String version;
        Class<SystemVersionObject> clazz = SystemVersionObject.class;
        String implementationVersion = clazz.getPackage().getImplementationVersion();
        if (implementationVersion != null) {
            version = implementationVersion;
        } else {
            CodeSource codeSource = clazz.getProtectionDomain().getCodeSource();
            if (codeSource == null) {
                version = "SNAPSHOT";
            } else {
                URL codeSourceLocation = codeSource.getLocation();
                try {
                    URLConnection connection = codeSourceLocation.openConnection();
                    if (connection instanceof JarURLConnection) {
                        version = this.getImplementationVersion(((JarURLConnection) connection).getJarFile());
                    } else {
                        try (JarFile jarFile = new JarFile(new File(codeSourceLocation.toURI()))) {
                            version = this.getImplementationVersion(jarFile);
                        }
                    }
                } catch (Exception ex) {
                    version = "SNAPSHOT";
                }
            }
        }

        cache.setSystemVersion(version);
        cache.setDuration(CacheDurationType.NORMAL);

        return this.createSystemVersion(cache);
    }

    public SystemVersionObject rebuildSystemVersion(UUID handle) {
        MemoryManager memoryManager = this.coreManager.getManager(MemoryManager.class);

        CacheRepositoryObject cacheRepository = memoryManager.getCacheRepository();
        SystemVersionCacheEntity cache = cacheRepository.get(SystemVersionCacheEntity.class, handle);

        return this.rebuildSystemVersion(cache);
    }

    public SystemVersionObject rebuildSystemVersion(SystemVersionCacheEntity cache) {
        MemoryManager memoryManager = this.coreManager.getManager(MemoryManager.class);

        CacheRepositoryObject cacheRepository = memoryManager.getCacheRepository();
        cacheRepository.refresh(SystemVersionCacheEntity.class, cache);

        return this.createSystemVersion(cache);
    }

    private DateTimeObject createDateTime(NoneCacheEntity cache) {
        DateTimeObject dateTime = this.coreManager.create(DateTimeObject.class);

        dateTime.setCache(cache);

        return dateTime;
    }

    public DateTimeObject buildDateTime() {
        NoneCacheEntity cache = new NoneCacheEntity();

        cache.setDuration(CacheDurationType.NORMAL);

        return this.createDateTime(cache);
    }

    public DateTimeObject rebuildDateTime(UUID handle) {
        MemoryManager memoryManager = this.coreManager.getManager(MemoryManager.class);

        CacheRepositoryObject cacheRepository = memoryManager.getCacheRepository();
        NoneCacheEntity cache = cacheRepository.get(NoneCacheEntity.class,handle);

        return this.rebuildDateTime(cache);
    }

    public DateTimeObject rebuildDateTime(NoneCacheEntity cache) {
        MemoryManager memoryManager = this.coreManager.getManager(MemoryManager.class);

        CacheRepositoryObject cacheRepository = memoryManager.getCacheRepository();
        cacheRepository.refresh(NoneCacheEntity.class,cache);

        return this.createDateTime(cache);
    }
}
