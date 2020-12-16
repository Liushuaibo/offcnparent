package com.offcn.user.service;

import com.offcn.user.po.TMember;
import com.offcn.user.po.TMemberAddress;
import io.swagger.models.auth.In;

import java.util.List;


public interface UserService {

    public void registeUser(TMember member);

    public TMember login(String name,String pwd);

    public TMember getUserInfo(Integer id);

    public List<TMemberAddress> getUserAddrs(Integer memberId);

}
