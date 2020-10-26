package indi.sly.system.kernel.memory.repositories;

import java.util.List;
import java.util.UUID;

import indi.sly.system.kernel.core.ACoreObject;
import indi.sly.system.kernel.objects.entities.InfoEntity;
import indi.sly.system.kernel.objects.entities.InfoRelationEntity;

public abstract class AEntityRepositoryObject extends ACoreObject {
    public abstract boolean contain(UUID id);

    public abstract InfoEntity get(UUID id);

    public abstract void add(InfoEntity info);

    public abstract void delete(InfoEntity info);

    public abstract void lock(InfoEntity info, long lockMode);

    public abstract List<InfoRelationEntity> listRelation(InfoEntity info);

    public abstract void addRelation(InfoRelationEntity infoRelation);

    public abstract void deleteRelation(InfoRelationEntity infoRelation);
}
