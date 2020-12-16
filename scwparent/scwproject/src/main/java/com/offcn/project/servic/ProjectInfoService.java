package com.offcn.project.servic;

import com.offcn.project.po.*;

import java.util.List;

public interface ProjectInfoService {

    /**
     * 获取项目回报列表
     * @param pid
     * @return
     */

    public List<TReturn> getReturnByPid(Integer pid);

    public List<TProject> getAllProject();

    public List<TProjectImages> getProjectImages(Integer pid);

    public TProject findProjectByPid(Integer pid);

    public List<TTag> getAllTags();

    public List<TType> getAllType();

    public TReturn getReturnByRId(Integer rid);
}
