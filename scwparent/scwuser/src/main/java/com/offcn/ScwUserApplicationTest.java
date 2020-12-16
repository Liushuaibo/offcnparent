package com.offcn;


import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.test.context.junit4.SpringRunner;


@RunWith(SpringRunner.class)
@SpringBootTest(classes = {UserStartApplication.class})
public class ScwUserApplicationTest {

    Logger logger = LoggerFactory.getLogger(getClass());

    @Autowired
    private RedisTemplate redisTemplate;

    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    @Test
    public void setValue(){
          redisTemplate.opsForValue().set("name","李四");
//
//        String name = (String)redisTemplate.opsForValue().get("name");
//
//        System.out.println("name : " + name);

//        stringRedisTemplate.opsForValue().set("name","张三");
        String name = stringRedisTemplate.opsForValue().get("name");
        System.out.println("name1 : " + name);
    }

}
