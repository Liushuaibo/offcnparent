package com.offcn.project.service.impl;

import com.alibaba.fastjson.JSON;
import com.netflix.discovery.converters.Auto;
import com.offcn.project.constants.ProjectContent;
import com.offcn.project.enums.ProjectImageTypeEnume;
import com.offcn.project.enums.ProjectStatusEnume;
import com.offcn.project.mapper.*;
import com.offcn.project.po.*;
import com.offcn.project.service.ProjectService;
import com.offcn.project.vo.req.ProjectRedisStorageVo;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.UUID;

@Service
public class ProjectServiceImpl implements ProjectService {

    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    @Autowired
    private TProjectMapper projectMapper;

    @Autowired
    private TProjectImagesMapper projectImagesMapper;

    @Autowired
    private TProjectTagMapper projectTagMapper;

    @Autowired
    private TProjectTypeMapper projectTypeMapper;

    @Autowired
    private TReturnMapper returnMapper;

    @Autowired
    private TTagMapper tTagMapper;

    @Autowired
    private TTypeMapper tTypeMapper;

    @Override
    public String initCreateProject(Integer memberId) {

        //1、生成projectToken
        String projectToken = UUID.randomUUID().toString().replace("-","");

        //2、创建ProjectRedisStorageVo
        ProjectRedisStorageVo projectRedisStorageVo = new ProjectRedisStorageVo();
        projectRedisStorageVo.setMemberid(memberId);

        //3、存入到redis中
        stringRedisTemplate.opsForValue().set(
                ProjectContent.TEMP_PROJECT_PREFIX+projectToken,
                JSON.toJSONString(projectRedisStorageVo));

        return projectToken;
    }

    @Override
    public void saveProjectInfo(ProjectStatusEnume enume, ProjectRedisStorageVo redisStorageVo) {

        //1、添加项目表
        TProject project = new TProject();
        BeanUtils.copyProperties(redisStorageVo,project);
        Date date = new Date();
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String time = simpleDateFormat.format(date);
        project.setCreatedate(time);
        projectMapper.insert(project);

        //2、获取刚刚新增项目id  作为后面图片 和 类型 标签等的外键
        Integer pid = project.getId();
        //3、存图片
        //3.1 存头部图片
        TProjectImages images = new TProjectImages(null,pid,redisStorageVo.getHeaderImage(), ProjectImageTypeEnume.HEADER.getCode());
        projectImagesMapper.insert(images);
        //3.2 存详细图片
        List<String> detailsImages = redisStorageVo.getDetailsImage();
        for (String detailsImage : detailsImages) {
            TProjectImages detailsIMG = new TProjectImages(null,pid,detailsImage,ProjectImageTypeEnume.DETAILS.getCode());
            projectImagesMapper.insert(detailsIMG);
        }
        //4、存tag
        List<Integer> tagList = redisStorageVo.getTagids();
        for (Integer tid : tagList) {
            TProjectTag projectTag = new TProjectTag(null,pid,tid);
            projectTagMapper.insert(projectTag);
        }

        //5、存type
        List<Integer> typeList = redisStorageVo.getTypeids();
        for (Integer tyId : typeList) {
            TProjectType tt = new TProjectType(null,pid,tyId);
            projectTypeMapper.insert(tt);
        }

        //6、存return
        List<TReturn> returnList = redisStorageVo.getProjectReturns();
        for (TReturn tReturn : returnList) {
            tReturn.setProjectid(pid);
            returnMapper.insert(tReturn);
        }

        //7、移除redis对应的project
        stringRedisTemplate.delete(ProjectContent.TEMP_PROJECT_PREFIX+redisStorageVo.getProjectToken());

    }

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
        System.out.println("PID DAO : " + pid);

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
