package com.offcn.project.service;

import com.offcn.project.enums.ProjectStatusEnume;
import com.offcn.project.po.*;
import com.offcn.project.vo.req.ProjectBaseVoInfo;
import com.offcn.project.vo.req.ProjectRedisStorageVo;

import java.util.List;

public interface ProjectService {

    public String initCreateProject(Integer memberId);

    public void saveProjectInfo(ProjectStatusEnume enume, ProjectRedisStorageVo redisStorageVo);

    public List<TReturn> getReturnByPid(Integer pid);

    public List<TProject> getAllProject();

    public List<TProjectImages> getProjectImages(Integer pid);

    public TProject findProjectByPid(Integer pid);

    public List<TTag> getAllTags();

    public List<TType> getAllType();

    public TReturn getReturnByRId(Integer rid);

}
