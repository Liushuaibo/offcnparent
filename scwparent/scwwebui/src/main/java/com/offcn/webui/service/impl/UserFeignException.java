package com.offcn.webui.service.impl;

import com.offcn.dycommon.response.AppResponse;
import com.offcn.webui.service.UserService;
import com.offcn.webui.vo.UserAddressVo;
import com.offcn.webui.vo.UserRespVo;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class UserFeignException implements UserService {
    @Override
    public AppResponse<UserRespVo> login(String name, String pwd) {
        AppResponse response = AppResponse.FAIL(null);
        response.setMessage("登录失败");
        return response;
    }

    @Override
    public AppResponse<UserRespVo> getUserInfo(Integer id) {
        AppResponse response = AppResponse.FAIL(null);
        response.setMessage("获取用户信息失败");
        return response;
    }

    @Override
    public AppResponse<List<UserAddressVo>> getUserAddrs(String token) {
        AppResponse response = AppResponse.FAIL(null);
        response.setMessage("获取地址列表失败");
        return response;
    }
}
