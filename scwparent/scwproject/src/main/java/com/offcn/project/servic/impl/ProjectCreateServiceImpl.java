package com.offcn.project.servic.impl;

import com.alibaba.fastjson.JSON;
import com.fasterxml.jackson.databind.util.BeanUtil;
import com.offcn.project.contants.ProjectConstant;
import com.offcn.project.enums.ProjectImageTypeEnume;
import com.offcn.project.enums.ProjectStatusEnume;
import com.offcn.project.mapper.*;
import com.offcn.project.po.*;
import com.offcn.project.servic.ProjectCreateService;
import com.offcn.project.vo.req.ProjectRedisStorageVo;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import javax.xml.crypto.Data;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.UUID;

@Service
public class ProjectCreateServiceImpl implements ProjectCreateService {

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
    @Override
    public String initCreateProject(Integer memberId) {

        //1.生成projectToken
        String projectToken = UUID.randomUUID().toString().replace("-","");

        //2.创建ProjectRedisStorageVo
        ProjectRedisStorageVo projectRedisStorageVo = new ProjectRedisStorageVo();
        projectRedisStorageVo.setMemberid(memberId);

        //3.存入redis中
        stringRedisTemplate.opsForValue().set(ProjectConstant.TEMP_PROJECT_PREFIX+projectToken,JSON.toJSONString(projectRedisStorageVo));

        return projectToken;
    }

    @Override
    public void saveProjectInfo(ProjectStatusEnume enume, ProjectRedisStorageVo redisStorageVo) {
        //1.添加项目表
        TProject project = new TProject();
        BeanUtils.copyProperties(redisStorageVo,project);
        Date date = new Date();
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String time = simpleDateFormat.format(date);
        project.setCreatedate(time);
        projectMapper.insert(project);

        //2.获取刚刚新增项目id  作为后面图片 和 类型 标签等的外键
        Integer pid = project.getId();
        //3.存图片
        //3.1存头图片
        TProjectImages image = new TProjectImages(null,pid,redisStorageVo.getHeaderImage(), ProjectImageTypeEnume.HEADER.getCode());
        projectImagesMapper.insert(image);
        //3.2 存详细图
        List<String> detailsImages = redisStorageVo.getDetailsImage();
        for (String detailsImage : detailsImages) {
            TProjectImages detailsIMG = new TProjectImages(null,pid,detailsImage,ProjectImageTypeEnume.DETAILS.getCode());
            projectImagesMapper.insert(detailsIMG);
        }

        //4.存tag 中间关系表
        List<Integer> tagList =redisStorageVo.getTagids();
        for (Integer tid : tagList) {
            TProjectTag projectTag = new TProjectTag(null,pid,tid);
            projectTagMapper.insert(projectTag);
        }

        //5.存type 中间关系表
        List<Integer> typeList = redisStorageVo.getTypeids();
        for (Integer typeId: typeList) {
            TProjectType projectType = new TProjectType(null,pid,typeId);
            projectTypeMapper.insert(projectType);
        }

        //6.存return表
        List<TReturn> returnList = redisStorageVo.getProjectReturns();
        for (TReturn tReturn : returnList) {
            tReturn.setProjectid(pid);
            returnMapper.insert(tReturn);
        }

        //7.删除redis中对应的项目
        stringRedisTemplate.delete(ProjectConstant.TEMP_PROJECT_PREFIX+redisStorageVo.getProjectToken());
    }

}
