package com.offcn.user.controller;

import com.offcn.dycommon.enums.ResponseCodeEnume;
import com.offcn.dycommon.response.AppResponse;
import com.offcn.user.component.SmsTemplate;
import com.offcn.user.po.TMember;
import com.offcn.user.service.UserService;
import com.offcn.user.vo.req.UserRegistVo;
import com.offcn.user.vo.resp.UserRespVo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpSession;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

@Api(tags = "短信测试")
@RestController
@RequestMapping("/login")
public class UserLoginController {

    @Autowired
    private SmsTemplate smsTemplate;

    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    @Autowired
    private UserService userService;

    Logger log = LoggerFactory.getLogger(getClass());

    @ApiOperation("用户登录")
    @ApiImplicitParams(
            value = {
                    @ApiImplicitParam(name="name",value = "用户名"),
                    @ApiImplicitParam(name="pwd",value = "密码")
            }
    )
    @GetMapping("/login")
    public AppResponse<UserRespVo> login(String name, String pwd, HttpSession session){
        //1、查用户
        TMember member = userService.login(name,pwd);
        if(member == null){
           AppResponse response = AppResponse.FAIL(null);
           response.setMessage("用户不存在");
           return response;
        }

        //2、生成token
        String token = UUID.randomUUID().toString().replace("-","");
        UserRespVo respVo = new UserRespVo();
        //2.1 将查回来的数据 导入到 respVo对象中
        BeanUtils.copyProperties(member,respVo);
        //2.2 存入token
        respVo.setAccessToken(token);
        //3、将token存入redis 以用户id为value
        stringRedisTemplate.opsForValue().set(token,member.getId()+"",2,TimeUnit.HOURS);

        //4、登录后将用户信息存入session
        session.setAttribute("sessionMember",respVo);

        return AppResponse.OK(respVo);
    }

    @ApiOperation("用户注册")
    @PostMapping("/regist")
    public AppResponse regist(UserRegistVo reqVo){
        System.out.println("用户名："+reqVo.getLoginacct());
        //1、获取验证码
        String sysCode = stringRedisTemplate.opsForValue().get(reqVo.getLoginacct());
        System.out.println("sysCode : " + sysCode);
        if(!StringUtils.isEmpty(sysCode)){
            if(sysCode.equalsIgnoreCase(reqVo.getCode())){

                //2、验证码合格 进行插入对象
                TMember member = new TMember();
                //2.1、利用实体对象工具类 将重名属性 加入到 目标对象中
                BeanUtils.copyProperties(reqVo,member);

                //3、插入数据
                try {
                    userService.registeUser(member);
                    //记录日志
                    log.debug("注册成功");
                    //清除redis验证码
                    stringRedisTemplate.delete(member.getLoginacct());
                    return AppResponse.OK(ResponseCodeEnume.SUCCESS);
                } catch (Exception e) {
                    e.printStackTrace();
                    log.debug("注册成功");
                    return AppResponse.FAIL("操作失败");
                }
            }else{
                return AppResponse.FAIL("验证码错误");
            }
        }else{
            return AppResponse.FAIL("验证码过期");
        }



    }








    @ApiOperation("发送短信")
    @ApiImplicitParams(value = {
            @ApiImplicitParam(name = "mobile",value = "手机号",required = true)
    })
    @GetMapping("/sendCode")
    public AppResponse sendCode(String mobile){

        //1、生成验证码
        String code = UUID.randomUUID().toString().substring(0,4);
        //2、将生成的验证码存入到redis 以便后面登录验证
        stringRedisTemplate.opsForValue().set(mobile,code,10, TimeUnit.MINUTES);
        //3、封装短信模版对象的参数
        Map<String, String> querys = new HashMap<String, String>();
        querys.put("mobile", mobile);
        querys.put("param", "code:" + code);
        querys.put("tpl_id", "TP1711063");//短信模板
        //4、短信模版 发送短信
        String smsResponse = smsTemplate.sendCode(querys);

        if("".equals(smsResponse) || "fail".equals(smsResponse)){
            return AppResponse.FAIL("短信发送失败");
        }

        return AppResponse.OK(smsResponse);
    }

}
