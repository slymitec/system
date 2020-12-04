package indi.sly.system.kernel.memory.repositories.prototypes;

import java.util.List;
import java.util.UUID;

import indi.sly.system.kernel.core.prototypes.ACorePrototype;
import indi.sly.system.kernel.objects.values.InfoEntity;
import indi.sly.system.kernel.objects.values.InfoRelationEntity;

public abstract class AInfoRepositoryObject extends ACorePrototype {
    public abstract boolean contain(UUID id);

    public abstract InfoEntity get(UUID id);

    public abstract void add(InfoEntity info);

    public abstract void delete(InfoEntity info);

    public abstract void lock(InfoEntity info, long lockType);

    public abstract List<InfoRelationEntity> listRelation(InfoEntity info);

    public abstract void addRelation(InfoRelationEntity infoRelation);

    public abstract void deleteRelation(InfoRelationEntity infoRelation);
}
