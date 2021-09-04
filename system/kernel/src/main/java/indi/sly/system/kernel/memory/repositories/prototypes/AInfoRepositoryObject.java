package indi.sly.system.kernel.memory.repositories.prototypes;

import indi.sly.system.kernel.core.prototypes.AObject;
import indi.sly.system.kernel.objects.values.InfoEntity;
import indi.sly.system.kernel.objects.values.InfoRelationEntity;

import java.util.List;
import java.util.UUID;

public abstract class AInfoRepositoryObject extends AObject {
    public abstract boolean contain(UUID id);

    public abstract InfoEntity get(UUID id);

    public abstract InfoEntity add(InfoEntity info);

    public abstract void delete(InfoEntity info);

    public abstract void lock(InfoEntity info, long lock);

    public abstract List<InfoRelationEntity> listRelation(InfoEntity info);

    public abstract void addRelation(InfoRelationEntity infoRelation);

    public abstract void deleteRelation(InfoRelationEntity infoRelation);
}
