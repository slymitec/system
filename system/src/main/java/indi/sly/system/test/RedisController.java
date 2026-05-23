package indi.sly.system.test;

import indi.sly.system.common.supports.ObjectUtil;
import indi.sly.system.common.supports.UUIDUtil;
import indi.sly.system.common.values.IdentifierDefinition;
import indi.sly.system.common.values.PathDefinition;
import indi.sly.system.kernel.processes.values.SignalDefinition;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.redisson.api.RBucket;
import org.redisson.api.RedissonClient;
import org.redisson.codec.TypedJsonJackson3Codec;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import tools.jackson.databind.json.JsonMapper;

import java.util.List;

@RestController
public class RedisController {
    @Resource
    private RedissonClient redissonClient;

    @RequestMapping(value = {"/Redison.action"}, method = {RequestMethod.GET})
    public Object Redison(HttpServletRequest request, HttpServletResponse response, HttpSession session) {
//
//        TypedJsonJackson3Codec codec = new TypedJsonJackson3Codec(SignalDefinition.class);
//
//        RBucket<PathDefinition> processCommunicationSignalBucket = redissonClient.getBucket("A", codec);
//
        PathDefinition path = new PathDefinition(List.of(new IdentifierDefinition(UUIDUtil.getFormLongs(1, 2)), new IdentifierDefinition("Hello")));

        String s = ObjectUtil.transferToString(path);

        System.out.println(s);

        PathDefinition pathDefinition = JsonMapper.builder().build().readValue(s, PathDefinition.class);

        return pathDefinition.get();


//        processCommunicationSignalBucket.set(path);


        //return "ok";
    }
}
