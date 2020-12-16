package com.offcn.webui.controller;

import com.alibaba.druid.util.StringUtils;
import com.netflix.discovery.converters.Auto;
import com.offcn.dycommon.response.AppResponse;
import com.offcn.webui.service.ProjectService;
import com.offcn.webui.service.UserService;
import com.offcn.webui.vo.ProjectVo;
import com.offcn.webui.vo.UserRespVo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpSession;
import java.util.List;
import java.util.concurrent.TimeUnit;

@Controller
public class DispathcherController {

    @Autowired
    private UserService userService;

    @Autowired
    private RedisTemplate redisTemplate;

    @Autowired
    private ProjectService projectService;


    @RequestMapping("/")
    public String toIndex(Model model){

        //1、去redis中寻找项目列表
        List<ProjectVo> projectVoList = (List<ProjectVo>)redisTemplate.opsForValue().get("projectStr");
        //2、redis找不到 则去mysql中取
        if(projectVoList==null){
            AppResponse<List<ProjectVo>> response = projectService.getAllProject();
            projectVoList = response.getData();
            //3、取回来 放入redis下次取值可以直接应用
            redisTemplate.opsForValue().set("projectStr",projectVoList,10000, TimeUnit.MINUTES);
        }

        model.addAttribute("pvoList",projectVoList);

        return "index";
    }

    Logger log = LoggerFactory.getLogger(getClass());

    @RequestMapping("/login")
    public String loginUrl(){
        return "login";
    }

    @RequestMapping("/doLogin")
    public String doLogin(String loginacct, String password, HttpSession session){

        //1、执行登录
        AppResponse<UserRespVo> response = userService.login(loginacct,password);
        UserRespVo respVo = response.getData();

        //2、记录日志
        log.debug("用户名和密码："+loginacct+"\t"+password);
        log.debug("返回对象："+respVo);

        //3、判断用户是否登录成功
        if(respVo == null){
            return "redirect:/login";
        }
        //4、将登录人存入到session中
        session.setAttribute("sessionMember",respVo);

        //5、假若登录成功判断是否存在要跳转的地址
        String url = (String)session.getAttribute("preUrl");

        if(!StringUtils.isEmpty(url)){
            return "redirect:/"+url;
        }

        return "redirect:/";

    }

}
