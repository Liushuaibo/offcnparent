package com.offcn.project.controller;

import com.offcn.dycommon.response.AppResponse;
import com.offcn.project.enums.ProjectImageTypeEnume;
import com.offcn.project.po.*;
import com.offcn.project.service.ProjectService;
import com.offcn.project.vo.resp.ProjectInfoVo;
import com.offcn.project.vo.resp.ProjectVo;
import com.offcn.util.OssTemplate;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/project")
@Api(
        tags = "项目模块"
)
public class ProjectInfoController {

    @Autowired
    private OssTemplate ossTemplate;

    Logger logger = LoggerFactory.getLogger(getClass());

    @Autowired
    private ProjectService projectService;

    @ApiOperation("获取指定回报详情")
    @GetMapping("/getReturnByRid/{rid}")
    public AppResponse<TReturn> getReturnByRId(@PathVariable("rid") Integer rid){
        TReturn tReturn = projectService.getReturnByRId(rid);
        return AppResponse.OK(tReturn);
    }

    @ApiOperation("获取所有类型")
    @GetMapping("/getAllType")
    public AppResponse<List<TType>> getAllType(){
        List<TType> tTypeList = projectService.getAllType();
        return AppResponse.OK(tTypeList);
    }

    @ApiOperation("获取所有标签")
    @GetMapping("/getAllTags")
    public AppResponse<List<TTag>> getAllTags(){
        List<TTag> tTagList = projectService.getAllTags();
        return AppResponse.OK(tTagList);
    }

    @ApiOperation("获取项目详情信息")
    @GetMapping("/findProjectByPid/{pid}")
    public AppResponse<ProjectInfoVo> findProjectByPid(@PathVariable("pid") Integer pid){
        //1、查询项目详情 来自于mysql
        TProject project = projectService.findProjectByPid(pid);

        //2、制作响应给前端的对象
        ProjectInfoVo infoVo = new ProjectInfoVo();
        BeanUtils.copyProperties(project,infoVo);

        //3、将查询回来的详情对象 复制 到响应对象
        //3.1 处理图片
        List<TProjectImages> imagesList = projectService.getProjectImages(project.getId());
        // 如果项目详细图片不存在 创建新的 如果存在 则追加图片
        List<String> detailsImages = new ArrayList<>();

        for (TProjectImages images : imagesList) {
            if(images.getImgtype() == ProjectImageTypeEnume.HEADER.getCode()){
                infoVo.setHeaderImage(images.getImgurl());
            }else{
                //存入详细图片
                detailsImages.add(images.getImgurl());
            }
        }
        infoVo.setDetailsImage(detailsImages);
        //3.2 处理回报
        List<TReturn> returnList = projectService.getReturnByPid(project.getId());
        infoVo.setProjectReturns(returnList);

        return AppResponse.OK(infoVo);

    }

    @ApiOperation("获取所有项目")
    @GetMapping("/getAllProject")
    public AppResponse<List<ProjectVo>> getAllProject(){
        //作为返回值
        List<ProjectVo> projectVoList = new ArrayList<ProjectVo>();

        //1、获取所有的项目
        List<TProject> projectList = projectService.getAllProject();
        //2、遍历获取项目id
        for (TProject project : projectList) {
            //2.1 获取每一个对象的id
            Integer pid = project.getId();

            //2.2 封装导入对象
            ProjectVo vo = new ProjectVo();
            BeanUtils.copyProperties(project,vo);
            System.out.println("pid : " + pid);
            //2.3 根据项目id 查询对应图片列表
            List<TProjectImages> imagesList = projectService.getProjectImages(pid);
            for (TProjectImages images : imagesList) {
                //2.4 只存头部图片
                if(images.getImgtype() == ProjectImageTypeEnume.HEADER.getCode()){
                    vo.setHeaderImage(images.getImgurl());
                }
            }

            //2.4 将vo装入集合
            projectVoList.add(vo);
        }
        return AppResponse.OK(projectVoList);
    }


    @ApiOperation(value = "获取项目回报列表")
    @GetMapping("/getReturn/{pid}")
    public AppResponse getReturn(@PathVariable("pid") Integer pid){
        List<TReturn> returnList = projectService.getReturnByPid(pid);
        return AppResponse.OK(returnList);
    }


    @ApiOperation(value = "上传")
    @PostMapping("/upload")
    public AppResponse<Object> upload(@RequestParam("file") MultipartFile[] files)throws Exception{


        //1、创建集合容器 存文件的存储地址
        List<String> urlList = new ArrayList<String>();
        //2、上传
        if(files!=null){
            for (MultipartFile file : files) {
                if(!file.isEmpty()){
                    String url = ossTemplate.upload(file.getInputStream(),file.getOriginalFilename());
                    urlList.add(url);
                }
            }
        }
        //3、创建map对象返回上传后的文件地址列表
        Map<String,Object> map = new HashMap<String,Object>();
        map.put("urls",urlList);

        //4、记录日志
        logger.debug("ossTemplate信息：{},文件上传成功访问路径{}",ossTemplate,urlList);

        return AppResponse.OK(map);
    }


}
