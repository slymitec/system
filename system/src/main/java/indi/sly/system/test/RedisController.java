package indi.sly.system.test;

import indi.sly.system.common.values.IdentifierRecord;
import indi.sly.system.common.values.PathRecord;
import indi.sly.system.kernel.objects.ObjectManager;
import indi.sly.system.kernel.objects.prototypes.InfoObject;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import jakarta.transaction.Transactional;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@RestController
@Transactional
public class RedisController extends ATestController {
    @RequestMapping(value = {"/Redison.action"}, method = {RequestMethod.GET})
    public Object Redison(HttpServletRequest request, HttpServletResponse response, HttpSession session) {
        this.initall(request, response, session);


        ObjectManager manager = this.coreManager.getManager(ObjectManager.class);
        InfoObject info = manager.get(new PathRecord(List.of(new IdentifierRecord("Files"))));
        UUID cache = info.cache();

        InfoObject info2 = manager.getFactory().rebuildInfo(cache);

        info2.getCache().setPath(new PathRecord(List.of(new IdentifierRecord("Sessions"))));

        return info2.toString();

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
