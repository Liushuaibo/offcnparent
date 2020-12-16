package com.offcn.webui.controller;

import com.offcn.dycommon.response.AppResponse;
import com.offcn.webui.service.ProjectService;
import com.offcn.webui.service.UserService;
import com.offcn.webui.vo.*;
import org.apache.catalina.User;
import org.hibernate.validator.constraints.pl.REGON;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpSession;
import java.math.BigDecimal;
import java.util.List;

@Controller
@RequestMapping("/project")
public class ProjectController {

    @Autowired
    private ProjectService projectService;

    @Autowired
    private UserService userService;

    @RequestMapping("/confirm/order/{num}")
    public String confirmOrder(@PathVariable("num") Integer num,Model model,HttpSession session){

//      1、  获取当前登录人信息
        UserRespVo userRespVo = (UserRespVo) session.getAttribute("sessionMember");
        if(userRespVo == null){
            session.setAttribute("preUrl","project/confirm/order/"+num);
            return "redirect:/login";
        }
        //2、通过用户token获取id  查找对应的地址列表
        AppResponse<List<UserAddressVo>> response = userService.getUserAddrs(userRespVo.getAccessToken());
        List<UserAddressVo> addressList = response.getData();
        //3、装入model 供页面选择
        model.addAttribute("addresses",addressList);
        //4、从redis中获取订单信息
        ReturnPayConfirmVo vo = (ReturnPayConfirmVo)session.getAttribute("returnConfirm");
        vo.setNum(num);
        vo.setTotalPrice(new BigDecimal(num*vo.getSupportmoney()+vo.getFreight()));
        //5、将存有 支持回报数量的 对象 存入session
        session.setAttribute("returnConfirmSession",vo);

        return "/project/pay-step-2.html";
    }

    @RequestMapping("/returns/{projectId}/{returnId}")
    public String returnInfo(Model model, HttpSession session, @PathVariable("projectId") Integer projectId,@PathVariable("returnId") Integer returnId){

        //1、从redis中取出 用户所选择的项目对象
        ProjectInfoVo projectInfoVo = (ProjectInfoVo) session.getAttribute("DetailVo");
        //2、查询回报详情
        AppResponse<ReturnPayConfirmVo> response = projectService.getReturnByRId(returnId);
//        TReturn tReturn = response.getData();
//        ReturnPayConfirmVo vo = new ReturnPayConfirmVo();
//        BeanUtils.copyProperties(tReturn,vo);
        ReturnPayConfirmVo vo = response.getData();

        //3、存基本信息
        vo.setProjectId(projectId);
        vo.setProjectName(projectInfoVo.getName());

        //4、查用户模块
        AppResponse<UserRespVo> response1 = userService.getUserInfo(projectInfoVo.getMemberid());
        UserRespVo uvo = response1.getData();

        //5、存用户的基本信息
        vo.setMemberId(projectInfoVo.getMemberid());
        vo.setMemberName(uvo.getUsername());

        //6、存入session
        session.setAttribute("returnConfirm",vo);
        model.addAttribute("returnConfirm",vo);
        return "project/pay-step-1.html";
    }


    @RequestMapping("/projectInfo")
    public String projectInfo(Model model, Integer id, HttpSession session){
        AppResponse<ProjectInfoVo> response = projectService.findProjectByPid(id);
        ProjectInfoVo projectInfoVo = response.getData();
        model.addAttribute("DetailVo",projectInfoVo);
        session.setAttribute("DetailVo",projectInfoVo);
        return "/project/project.html";
    }

}
