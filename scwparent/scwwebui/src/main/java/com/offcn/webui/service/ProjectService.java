package com.offcn.webui.service;

import com.offcn.dycommon.response.AppResponse;
import com.offcn.webui.config.FeignConfig;
import com.offcn.webui.service.impl.ProjectFeignException;
import com.offcn.webui.vo.ProjectInfoVo;
import com.offcn.webui.vo.ProjectVo;
import com.offcn.webui.vo.ReturnPayConfirmVo;
import com.offcn.webui.vo.TReturn;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;

@FeignClient(value = "SCW-PROJECT",configuration = FeignConfig.class,fallback = ProjectFeignException.class)
public interface ProjectService {

    @GetMapping("/project/getAllProject")
    public AppResponse<List<ProjectVo>> getAllProject();

    @GetMapping("/project/findProjectByPid/{pid}")
    public AppResponse<ProjectInfoVo> findProjectByPid(@PathVariable("pid") Integer pid);

    @GetMapping("/project/getReturnByRid/{rid}")
    public AppResponse<ReturnPayConfirmVo> getReturnByRId(@PathVariable("rid") Integer rid);


}
