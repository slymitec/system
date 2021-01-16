package indi.sly.system.kernel.core.prototypes;

import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;

import javax.inject.Named;
import java.io.File;
import java.io.IOException;
import java.net.JarURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.security.CodeSource;
import java.util.jar.Attributes;
import java.util.jar.JarFile;

@Named
@Scope(value = ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class SystemVersionObject extends APrototype {
    public SystemVersionObject() {
        Class<SystemVersionObject> clazz = SystemVersionObject.class;

        String implementationVersion = clazz.getPackage().getImplementationVersion();
        if (implementationVersion != null) {
            this.version = implementationVersion;
        } else {
            CodeSource codeSource = clazz.getProtectionDomain().getCodeSource();
            if (codeSource == null) {
                return;
            }
            URL codeSourceLocation = codeSource.getLocation();
            try {
                URLConnection connection = codeSourceLocation.openConnection();
                if (connection instanceof JarURLConnection) {
                    this.version = this.getImplementationVersion(((JarURLConnection) connection).getJarFile());
                } else {
                    try (JarFile jarFile = new JarFile(new File(codeSourceLocation.toURI()))) {
                        this.version = this.getImplementationVersion(jarFile);
                    }
                }
            } catch (Exception ex) {
                this.version = "SNAPSHOT";
            }
        }
    }

    private String version;

    public String getVersion() {
        return this.version;
    }

    private String getImplementationVersion(JarFile jarFile) throws IOException {
        return jarFile.getManifest().getMainAttributes().getValue(Attributes.Name.IMPLEMENTATION_VERSION);
    }
}
