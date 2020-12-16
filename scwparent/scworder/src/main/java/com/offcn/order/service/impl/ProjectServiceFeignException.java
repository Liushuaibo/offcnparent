package com.offcn.order.service.impl;

import com.offcn.dycommon.response.AppResponse;
import com.offcn.order.po.TReturn;
import com.offcn.order.service.ProjectServiceFeign;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;

@Component
public class ProjectServiceFeignException implements ProjectServiceFeign {

    @Override
    public AppResponse<List<TReturn>> getReturn(@PathVariable("pid") Integer pid) {

        AppResponse response = AppResponse.FAIL(null);
        response.setMessage("项目模块 REST 远程调用失败");

        return response;
    }
}
