package com.offcn.user.controller;

import com.alibaba.druid.util.StringUtils;
import com.offcn.dycommon.response.AppResponse;
import com.offcn.user.po.TMember;
import com.offcn.user.po.TMemberAddress;
import com.offcn.user.service.UserService;
import com.offcn.user.vo.resp.UserAddressVo;
import com.offcn.user.vo.resp.UserRespVo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;

@Api(tags = "用户信息")
@RestController
@RequestMapping("/user")
public class UserInfoController {

    @Autowired
    private UserService userService;

    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    @ApiOperation("获取用户收货人地址")
    @ApiImplicitParams(
            value = {
                    @ApiImplicitParam(name="token",value = "用户秘钥")
            }
    )
    @GetMapping("/getUserAddress/{token}")
    public AppResponse<List<UserAddressVo>> getUserAddrs(@PathVariable("token") String token){
        //1、从redis中获取用户id
        String mid = stringRedisTemplate.opsForValue().get(token);
        if(StringUtils.isEmpty(mid)){
            AppResponse response = AppResponse.FAIL(null);
            response.setMessage("用户未登录，没有访问权限");
            return response;
        }
        Integer id = Integer.parseInt(mid);

        //2、获取该用户对应地址信息列表
        List<TMemberAddress> addressList = userService.getUserAddrs(id);

        //3、由于开发习惯限制 制定地址返回值对象 UserAddressVo
        //制作userAddressVo的集合
        List<UserAddressVo> addressVoList = new ArrayList<UserAddressVo>();

        //将查回来的集合装入到 addressVO 对象中
        for (TMemberAddress tMemberAddress : addressList) {

            UserAddressVo uvo = new UserAddressVo();
            uvo.setAddressId(tMemberAddress.getId());
            uvo.setAddress(tMemberAddress.getAddress());
            addressVoList.add(uvo);
        }
        return AppResponse.OK(addressVoList);
    }

    @GetMapping("/getUserInfo/{id}")
    public AppResponse<UserRespVo> getUserInfo(@PathVariable("id") Integer id){
        TMember member = userService.getUserInfo(id);
        UserRespVo respVo = new UserRespVo();
        BeanUtils.copyProperties(member,respVo);
        return AppResponse.OK(respVo);
    }


}
