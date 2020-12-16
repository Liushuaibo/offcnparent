package com.offcn.webui.service.impl;

import com.offcn.dycommon.response.AppResponse;
import com.offcn.webui.service.OrderService;
import com.offcn.webui.vo.OrderFormInfoSubmitVo;
import com.offcn.webui.vo.OrderInfoSubmitVo;
import org.springframework.stereotype.Component;

@Component
public class OrderFeignExcption implements OrderService {
    @Override
    public AppResponse saveOrder(OrderInfoSubmitVo vo) {
        AppResponse response = AppResponse.FAIL(null);
        response.setMessage("保存订单失败");
        return response;
    }
}
