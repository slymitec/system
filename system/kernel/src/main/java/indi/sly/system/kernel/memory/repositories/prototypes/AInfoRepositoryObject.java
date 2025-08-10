package indi.sly.system.kernel.memory.repositories.prototypes;

import indi.sly.system.kernel.core.prototypes.AObject;
import indi.sly.system.kernel.objects.values.InfoEntity;
import indi.sly.system.kernel.objects.values.InfoWildcardDefinition;
import indi.sly.system.kernel.objects.values.InfoRelationEntity;

import java.util.List;
import java.util.UUID;

public abstract class AInfoRepositoryObject extends AObject {
    public abstract boolean contain(UUID id);

    public abstract InfoEntity get(UUID id);

    public abstract InfoEntity add(InfoEntity info);

    public abstract void delete(InfoEntity info);

    public abstract void lock(InfoEntity info, long lock);

    public abstract void unlock(InfoEntity info, long lock);

    public abstract InfoRelationEntity getRelation(InfoEntity info, UUID id);

    public abstract InfoRelationEntity getRelation(InfoEntity info, String name);

    public abstract List<InfoRelationEntity> listRelation(InfoEntity info, InfoWildcardDefinition wildcard);

    public abstract int countRelation(InfoEntity info, InfoWildcardDefinition wildcard);

    public abstract void addRelation(InfoRelationEntity infoRelation);

    public abstract void deleteRelation(InfoRelationEntity infoRelation);
}
