package com.offcn.webui.service;

import com.offcn.dycommon.response.AppResponse;
import com.offcn.webui.config.FeignConfig;
import com.offcn.webui.service.impl.OrderFeignExcption;
import com.offcn.webui.vo.OrderFormInfoSubmitVo;
import com.offcn.webui.vo.OrderInfoSubmitVo;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(value = "SCW-ORDER",configuration = FeignConfig.class,fallback = OrderFeignExcption.class)
public interface OrderService {

    @PostMapping("/order/saveOrder")
    public AppResponse saveOrder(@RequestBody OrderInfoSubmitVo vo);


}
