package com.offcn.user.service.impl;

import com.offcn.user.enums.UserExceptionEnums;
import com.offcn.user.exception.UserException;
import com.offcn.user.mapper.TMemberAddressMapper;
import com.offcn.user.mapper.TMemberMapper;
import com.offcn.user.po.TMember;
import com.offcn.user.po.TMemberAddress;
import com.offcn.user.po.TMemberAddressExample;
import com.offcn.user.po.TMemberExample;
import com.offcn.user.service.UserService;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;


@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private TMemberMapper memberMapper;

    @Autowired
    private TMemberAddressMapper memberAddressMapper;

    @Override
    public void registeUser(TMember member){

        //1、判断当前登录账号是否已经注册 若是则抛异常 若不是添加数据库
        TMemberExample example = new TMemberExample();
        TMemberExample.Criteria criteria = example.createCriteria();
        criteria.andLoginacctEqualTo(member.getLoginacct());
        long num = memberMapper.countByExample(example);
        if(num > 0){
            throw new UserException(UserExceptionEnums.LOGINACCT_EXIST);
        }

        //2、密码加密
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        String password = encoder.encode(member.getUserpswd());
        member.setUserpswd(password);
        //3、普通数据
        member.setUsername(member.getLoginacct());
        member.setEmail(member.getEmail());
        //实名认证状态 0 - 未实名认证， 1 - 实名认证申请中， 2 - 已实名认证
        member.setAuthstatus("0");
        //用户类型: 0 - 个人， 1 - 企业
        member.setUsertype("0");
        //账户类型: 0 - 企业， 1 - 个体， 2 - 个人， 3 - 政府
        member.setAccttype("2");
        System.out.println("插入数据    :"+member.getLoginacct());
        //4、插入数据库
        memberMapper.insert(member);
    }

    @Override
    public TMember login(String name, String pwd) {

        //1、创建加密对象
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        //2、查询对象
        TMemberExample example = new TMemberExample();
        TMemberExample.Criteria criteria = example.createCriteria();
        criteria.andLoginacctEqualTo(name);
        List<TMember> memberList =  memberMapper.selectByExample(example);
        if(memberList!=null){
            TMember member = memberList.get(0);
            //3、加密对象 比对 密码是否相同
            return encoder.matches(pwd,member.getUserpswd())?member:null;
        }
        return null;
    }

    @Override
    public TMember getUserInfo(Integer id) {
        return memberMapper.selectByPrimaryKey(id);
    }

    @Override
    public List<TMemberAddress> getUserAddrs(Integer memberId) {
        TMemberAddressExample example = new TMemberAddressExample();
        TMemberAddressExample.Criteria criteria = example.createCriteria();
        criteria.andMemberidEqualTo(memberId);
        return memberAddressMapper.selectByExample(example);
    }
}
