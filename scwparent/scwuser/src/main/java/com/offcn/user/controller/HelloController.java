package com.offcn.user.controller;

import com.offcn.user.bean.User;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Api(tags = "helloController swagger 测试")
@RestController
@RequestMapping("/hello")
public class HelloController {


    @ApiOperation(value = "获取用户名")
    @ApiImplicitParams(value = {
            @ApiImplicitParam(name = "name",value = "姓名",required = true,dataType = "String"),
            @ApiImplicitParam(name="age",value = "年龄",dataType = "Integer")
    })
    @GetMapping(value = "getName")
    public String getName(String name,Integer age){
        return "hello , " + name;
    }

    @ApiOperation(value = "保存用户")
    @ApiImplicitParams(value = {
            @ApiImplicitParam(name="name",value = "姓名"),
            @ApiImplicitParam(name="email",value = "邮箱")
    })
    @PostMapping("/save")
    public User save(String name,String email){
        User user = new User();
        user.setName(name);
        user.setEmail(email);
        return user;
    }

}
