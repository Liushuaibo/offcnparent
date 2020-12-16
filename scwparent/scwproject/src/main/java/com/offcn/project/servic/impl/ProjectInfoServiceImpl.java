package com.offcn.project.servic.impl;

import com.netflix.discovery.converters.Auto;
import com.offcn.project.mapper.*;
import com.offcn.project.po.*;
import com.offcn.project.servic.ProjectInfoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
@Service
public class ProjectInfoServiceImpl implements ProjectInfoService {

    @Autowired
    private TReturnMapper returnMapper;

    @Autowired
    private TProjectMapper projectMapper;

    @Autowired
    private TProjectImagesMapper projectImagesMapper;


    @Autowired
    private TTagMapper tTagMapper;

    @Autowired
    private TTypeMapper tTypeMapper;
    /**
     * 查讯项目回报列表
     * @param projectId
     * @return
     */
    @Override
    public List<TReturn> getReturnByPid(Integer pid) {

        TReturnExample example = new TReturnExample();
        TReturnExample.Criteria criteria = example.createCriteria();
        criteria.andProjectidEqualTo(pid);
        return returnMapper.selectByExample(example);

    }

    @Override
    public List<TProject> getAllProject() {
        return projectMapper.selectByExample(null);
    }

    @Override
    public List<TProjectImages> getProjectImages(Integer pid) {
        TProjectImagesExample example = new TProjectImagesExample();
        TProjectImagesExample.Criteria criteria = example.createCriteria();
        criteria.andProjectidEqualTo(pid);
        return projectImagesMapper.selectByExample(example);
    }

    @Override
    public TProject findProjectByPid(Integer pid) {
        return projectMapper.selectByPrimaryKey(pid);
    }

    @Override
    public List<TTag> getAllTags() {
        return tTagMapper.selectByExample(null);
    }

    @Override
    public List<TType> getAllType() {
        return tTypeMapper.selectByExample(null);
    }

    @Override
    public TReturn getReturnByRId(Integer rid) {
        return returnMapper.selectByPrimaryKey(rid);
    }
}
