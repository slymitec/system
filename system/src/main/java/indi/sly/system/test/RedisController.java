package indi.sly.system.test;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalTime;

@RestController
public class RedisController {
    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    @RequestMapping(value = {"/Redis.action"}, method = {RequestMethod.GET})
    public Object Redis(HttpServletRequest request, HttpServletResponse response, HttpSession session) {
        stringRedisTemplate.opsForValue().set("A", LocalTime.now().toString());
        return "success";
    }
}
