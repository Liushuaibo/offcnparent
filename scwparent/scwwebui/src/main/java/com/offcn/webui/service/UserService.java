package com.offcn.webui.service;

import com.offcn.dycommon.response.AppResponse;
import com.offcn.webui.config.FeignConfig;
import com.offcn.webui.service.impl.UserFeignException;
import com.offcn.webui.vo.UserAddressVo;
import com.offcn.webui.vo.UserRespVo;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@FeignClient(value = "SCW-USER",configuration = FeignConfig.class,fallback = UserFeignException.class)
public interface UserService {

    @GetMapping("/login/login")
    public AppResponse<UserRespVo> login(@RequestParam("name") String name,@RequestParam("pwd") String pwd);

    @GetMapping("/user/getUserInfo/{id}")
    public AppResponse<UserRespVo> getUserInfo(@RequestParam("id") Integer id);

    @GetMapping("/user/getUserAddress/{token}")
    public AppResponse<List<UserAddressVo>> getUserAddrs(@RequestParam("token") String token);

}
