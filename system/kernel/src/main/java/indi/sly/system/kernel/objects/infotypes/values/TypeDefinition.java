package indi.sly.system.kernel.objects.infotypes.values;

import indi.sly.system.common.values.ADefinition;
import indi.sly.system.kernel.objects.infotypes.prototypes.wrappers.ATypeInitializer;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

public class TypeDefinition extends ADefinition<TypeDefinition> {
    private UUID id;
    private String name;
    private long attribute;
    private UUID thisType;
    private final Set<UUID> childTypes;
    private ATypeInitializer initializer;
    private final TypeCounterDefinition counter;

    public TypeDefinition() {
        this.childTypes = new HashSet<>();
        this.counter = new TypeCounterDefinition();
    }

    public UUID getId() {
        return this.id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public long getAttribute() {
        return this.attribute;
    }

    public void setAttribute(long attribute) {
        this.attribute = attribute;
    }

    public UUID getThisType() {
        return thisType;
    }

    public void setThisType(UUID thisType) {
        this.thisType = thisType;
    }

    public Set<UUID> getChildTypes() {
        return this.childTypes;
    }

    public ATypeInitializer getInitializer() {
        return this.initializer;
    }

    public void setInitializer(ATypeInitializer initializer) {
        this.initializer = initializer;
    }

    public TypeCounterDefinition getCounter() {
        return this.counter;
    }
}
