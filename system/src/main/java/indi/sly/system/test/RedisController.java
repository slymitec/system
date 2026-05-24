package indi.sly.system.test;

import indi.sly.system.common.lang.StatusNotReadyException;
import indi.sly.system.common.supports.ObjectUtil;
import indi.sly.system.common.supports.UUIDUtil;
import indi.sly.system.common.values.IdentifierDefinition;
import indi.sly.system.common.values.PathDefinition;
import indi.sly.system.kernel.core.enviroment.values.UserSpaceDefinition;
import indi.sly.system.kernel.memory.MemoryManager;
import indi.sly.system.kernel.memory.repositories.prototypes.CacheRepositoryObject;
import indi.sly.system.kernel.objects.ObjectManager;
import indi.sly.system.kernel.objects.prototypes.InfoObject;
import indi.sly.system.services.faces.AController;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import jakarta.transaction.Transactional;
import org.redisson.api.RLiveObjectService;
import org.redisson.api.RedissonClient;
import org.redisson.api.annotation.REntity;
import org.redisson.api.annotation.RId;
import org.redisson.codec.TypedJsonJackson3Codec;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Objects;
import java.util.UUID;

@RestController
@Transactional
public class RedisController extends ATestController {
    @Resource
    private RedissonClient redissonClient;

    @RequestMapping(value = {"/Redison.action"}, method = {RequestMethod.GET})
    public Object Redison(HttpServletRequest request, HttpServletResponse response, HttpSession session) {
        this.initall(request, response, session);


        ObjectManager manager = this.coreManager.getManager(ObjectManager.class);
        InfoObject info = manager.get(new PathDefinition(List.of(new IdentifierDefinition("Files"))));
        UUID cache = info.cache();

        return cache.toString();

//        RLiveObjectService liveObjectService = this.redissonClient.getLiveObjectService();
//
//        TestEntity testEntity = new TestEntity();
//        testEntity.setId(UUIDUtil.createRandom());
//        testEntity.setN(12L);
//
//        testEntity = liveObjectService.persist(testEntity);
//
//
//        return testEntity.getN();
    }

}

//        PathDefinition path = new PathDefinition(List.of(new IdentifierDefinition(UUIDUtil.getFormLongs(1, 2)), new IdentifierDefinition("Hello")));
//
/// /        String s = ObjectUtil.transferToString(path);
/// /
/// /        System.out.println(s);
/// /
/// /        PathDefinition pathDefinition = JsonMapper.builder().build().readValue(s, PathDefinition.class);
//
//        StringBuilder stringBuilder = new StringBuilder();
//        for (IdentifierDefinition identifierDefinition : path.get()) {
//            stringBuilder.append(identifierDefinition.toString());
//        }
//
//        return stringBuilder.toString();
//
//
////        processCommunicationSignalBucket.set(path);
//
//
//        //return "ok";
