package com.offcn.project.servic;

import com.offcn.project.enums.ProjectStatusEnume;
import com.offcn.project.vo.req.ProjectRedisStorageVo;

public interface ProjectCreateService {

    public String initCreateProject(Integer memberId);

    /**
     * 保存项目信息
     * @param enume  项目状态信息
     * @param redisStorageVo  项目全部信息
     */
    public void saveProjectInfo(ProjectStatusEnume enume, ProjectRedisStorageVo redisStorageVo);
}
