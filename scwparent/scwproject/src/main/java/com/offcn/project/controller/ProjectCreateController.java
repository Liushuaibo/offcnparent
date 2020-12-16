package com.offcn.project.controller;

import com.alibaba.druid.util.StringUtils;
import com.alibaba.fastjson.JSON;
import com.offcn.dycommon.enums.ResponseCodeEnume;
import com.offcn.dycommon.response.AppResponse;
import com.offcn.project.constants.ProjectContent;
import com.offcn.project.enums.ProjectStatusEnume;
import com.offcn.project.po.TReturn;
import com.offcn.project.service.ProjectService;
import com.offcn.project.vo.req.ProjectBaseVoInfo;
import com.offcn.project.vo.req.ProjectRedisStorageVo;
import com.offcn.project.vo.req.ProjectReturnVo;
import com.offcn.vo.BaseVo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import io.swagger.models.auth.In;
import org.apiguardian.api.API;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@Api(tags = "项目创建模块")
@RestController
@RequestMapping("/project")
public class ProjectCreateController {

    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    @Autowired
    private ProjectService projectService;

    @ApiOperation("项目发起第1步-阅读同意协议")
    @PostMapping("/init")
    public AppResponse init(BaseVo vo){

        //1、获取accessToken 通过token 获取用户id 判断用户是否是登录状态
        String memberId = stringRedisTemplate.opsForValue().get(vo.getAccessToken());
        if(StringUtils.isEmpty(memberId)){
            return AppResponse.FAIL("没有访问权限，请先登录。");
        }

        //2、创建项目模版 存储到 redis中
        Integer id = Integer.parseInt(memberId);
        String projectToken = projectService.initCreateProject(id);

        return AppResponse.OK(projectToken);
    }

    @ApiOperation("项目发起第2步-封装项目信息")
    @PostMapping("/save")
    public AppResponse saveBaseInfo(ProjectBaseVoInfo baseVoInfo){
        System.out.println("baseVoInfo : " + baseVoInfo.getProjectToken());
        //1、获取redis中存储对象
        String storageObj = stringRedisTemplate.opsForValue().get(ProjectContent.TEMP_PROJECT_PREFIX+baseVoInfo.getProjectToken());
        System.out.println("storageObj : " + storageObj);
        if(storageObj!=null){
            //2、转换为对象格式
            ProjectRedisStorageVo redisStorageVo = JSON.parseObject(storageObj,ProjectRedisStorageVo.class);
            //3、将页面中填写的项目信息 存入到redis对象中
            BeanUtils.copyProperties(baseVoInfo,redisStorageVo);
            //4、再将封装好的对象数据装入redis中
            String redisStorageObj = JSON.toJSONString(redisStorageVo);
            stringRedisTemplate.opsForValue().set(ProjectContent.TEMP_PROJECT_PREFIX+baseVoInfo.getProjectToken(),redisStorageObj);
            return AppResponse.OK("项目详细信息状态完毕");
        }
        return AppResponse.FAIL("项目不存在");
    }

    @ApiOperation("项目发起第3步-封装回报列表")
    @PostMapping("/saveReturn")
    public AppResponse saveReturn(@RequestBody List<ProjectReturnVo> returnVoList){
        //1、取出returnvo对应 projectToken
        String projectToken = returnVoList.get(0).getProjectToken();
        //2、取出redis中 projectToken 对应的项目对象
        String redisObj = stringRedisTemplate.opsForValue().get(ProjectContent.TEMP_PROJECT_PREFIX+projectToken);
        ProjectRedisStorageVo redisStorageVo = JSON.parseObject(redisObj,ProjectRedisStorageVo.class);
        //3、将returnVo集合对象 中的值 导入到 TReturn对象的集合
        List<TReturn> returnList = new ArrayList<TReturn>();

        for (ProjectReturnVo projectReturnVo : returnVoList) {
            TReturn tReturn = new TReturn();
            BeanUtils.copyProperties(projectReturnVo,tReturn);
            returnList.add(tReturn);
        }
        //4、将倒入好的returnList 加入到redis存储对象中
        redisStorageVo.setProjectReturns(returnList);
        //5、将封装好的 redisStorageVo 再存入到redis中
        stringRedisTemplate.opsForValue().set(ProjectContent.TEMP_PROJECT_PREFIX+projectToken,JSON.toJSONString(redisStorageVo));
        return AppResponse.OK("回报列表添加完毕");
    }

    @ApiOperation("项目发起第4步-提交项目")
    @ApiImplicitParams(
            value = {
                    @ApiImplicitParam(name="accessToken",value="用户token"),
                    @ApiImplicitParam(name="projectToken",value = "项目token"),
                    @ApiImplicitParam(name="ops",value = "审核状态")
            }
    )
    @GetMapping("/submit")
    public AppResponse submit(String accessToken,String projectToken,String ops){
        //1、通过用户token 获取 用户id
        String memberId = stringRedisTemplate.opsForValue().get(accessToken);
        if(StringUtils.isEmpty(memberId)){
            return AppResponse.FAIL(ResponseCodeEnume.NOT_AUTHED);
        }
        //2、通过项目token 获取redis存储对象
        String project = stringRedisTemplate.opsForValue().get(ProjectContent.TEMP_PROJECT_PREFIX+projectToken);
        ProjectRedisStorageVo redisStorageVo = JSON.parseObject(project,ProjectRedisStorageVo.class);
        if(redisStorageVo!=null && !StringUtils.isEmpty(ops)){
            if("1".equals(ops)){
                projectService.saveProjectInfo(ProjectStatusEnume.SUBMIT_AUTH,redisStorageVo);
                return AppResponse.OK("提交成功，项目处于审核中");

            }else{
                projectService.saveProjectInfo(ProjectStatusEnume.DRAFT,redisStorageVo);
                return AppResponse.OK("提交成功，项目存于草稿中");
            }
        }
        return AppResponse.FAIL("提交失败");
    }

}
