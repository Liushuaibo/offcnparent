package com.offcn.webui.service.impl;

import com.offcn.dycommon.response.AppResponse;
import com.offcn.webui.service.ProjectService;
import com.offcn.webui.vo.ProjectInfoVo;
import com.offcn.webui.vo.ProjectVo;
import com.offcn.webui.vo.ReturnPayConfirmVo;
import com.offcn.webui.vo.TReturn;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class ProjectFeignException implements ProjectService {
    @Override
    public AppResponse<List<ProjectVo>> getAllProject() {
        AppResponse response = AppResponse.FAIL(null);
        response.setMessage("项目列表获取失败");
        return response;
    }

    @Override
    public AppResponse<ProjectInfoVo> findProjectByPid(Integer pid) {
        AppResponse response = AppResponse.FAIL(null);
        response.setMessage("项目详情获取失败");
        return response;
    }

    @Override
    public AppResponse<ReturnPayConfirmVo> getReturnByRId(Integer rid) {
        AppResponse response = AppResponse.FAIL(null);
        response.setMessage("项目回报列表获取失败");
        return response;
    }
}
