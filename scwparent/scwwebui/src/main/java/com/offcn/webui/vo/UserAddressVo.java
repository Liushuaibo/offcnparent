package com.offcn.webui.vo;

import java.io.Serializable;

public class UserAddressVo implements Serializable {

    //地址id
    private Integer id = 1;

    //会员地址
    private String address = "朝阳门外大街31号";

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }
}
