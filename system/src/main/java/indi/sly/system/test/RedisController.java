package indi.sly.system.test;

import indi.sly.system.common.lang.ConditionParametersException;
import indi.sly.system.common.supports.ObjectUtil;
import indi.sly.system.common.supports.UUIDUtil;
import indi.sly.system.common.supports.ValueUtil;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.time.Duration;
import java.time.LocalTime;
import java.util.UUID;

@RestController
public class RedisController {
    @Resource
    private RedisTemplate<String, byte[]> redisTemplate;

    @RequestMapping(value = {"/Redis.action"}, method = {RequestMethod.GET})
    public Object Redis(HttpServletRequest request, HttpServletResponse response, HttpSession session) {
        UUID id = UUIDUtil.getFormLongs(7156, 7156);

        this.add(id, new byte[]{1, 2, 3, 4, 5, 6, 7, 8}, null);
        return "success";
    }

    private String convertKey(UUID id) {
        return "Memory:CacheRepository:" + id.toString();
    }

    public void add(UUID id, byte[] value, Duration duration) {
        if (ValueUtil.isAnyNullOrEmpty(id, value)) {
            throw new ConditionParametersException();
        }

        if (ObjectUtil.isAnyNull(duration)) {
            this.redisTemplate.opsForValue().set(this.convertKey(id), value);
        } else {
            this.redisTemplate.opsForValue().set(this.convertKey(id), value, duration);
        }
    }
}
