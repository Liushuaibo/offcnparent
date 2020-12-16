package com.offcn.webui.controller;

import com.offcn.dycommon.response.AppResponse;
import com.offcn.webui.service.OrderService;
import com.offcn.webui.vo.OrderFormInfoSubmitVo;
import com.offcn.webui.vo.OrderInfoSubmitVo;
import com.offcn.webui.vo.ReturnPayConfirmVo;
import com.offcn.webui.vo.UserRespVo;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpSession;

@Controller
@RequestMapping("/order")
public class OrderController {

    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    @Autowired
    private OrderService orderService;

    @PostMapping("/save")
    public String saveOrder(HttpSession session){

        OrderFormInfoSubmitVo orderVo = new OrderFormInfoSubmitVo();

        //1、获取当前登录人
        UserRespVo respVo = (UserRespVo) session.getAttribute("sessionMember");
        if(respVo == null){
            return "redirect:/login";
        }

        //2、获取订单信息
        ReturnPayConfirmVo payConfirmVo = (ReturnPayConfirmVo) session.getAttribute("returnConfirmSession");
        if(payConfirmVo == null){
            return "redirect:/login";
        }

        //3、拼接订单信息
        orderVo.setAccessToken(respVo.getAccessToken());
        orderVo.setProjectid(payConfirmVo.getProjectId());
        orderVo.setReturnid(payConfirmVo.getId());
        orderVo.setRtncount(payConfirmVo.getNum());
        orderVo.setInvoice((byte)1);
        orderVo.setInvoictitle("中公教育");

        OrderInfoSubmitVo submitVo = new OrderInfoSubmitVo();
        BeanUtils.copyProperties(orderVo,submitVo);

        AppResponse response = orderService.saveOrder(submitVo);

        return "/member/minecrowdfunding";
    }


}
