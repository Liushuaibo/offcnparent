package com.offcn.webui.vo;

import java.io.Serializable;
import java.math.BigDecimal;

public class ReturnPayConfirmVo implements Serializable {

    /**
     * 项目信息
     */
    private Integer projectId;   //项目ID
    private String projectName;  //项目名称
    private String projectRemark; //项目描述

    /**
     * 发起人信息
     */
    private Integer memberId;  //会员ID
    private String memberName;  //会员名称

    /**
     * 回报的信息
     */
    private Integer id; //回报ID
    private String content;

    // 支持数量，默认数量1，不能大于signalpurchase单笔限购数量
    private Integer num;

    // 支持单价
    private Integer supportmoney;

    //运费
    private Integer freight;

    //总限购数量
    private Integer purchase;

    // 单笔限购数量
    private Integer signalpurchase;

    // 所有的double和float的运算在任何情况下都会导致丢失精度，使用BigDecimal进行计算
    // 总价 totalPrice =price* num+ freight
    private BigDecimal totalPrice;

    public Integer getProjectId() {
        return projectId;
    }

    public void setProjectId(Integer projectId) {
        this.projectId = projectId;
    }

    public String getProjectName() {
        return projectName;
    }

    public void setProjectName(String projectName) {
        this.projectName = projectName;
    }

    public String getProjectRemark() {
        return projectRemark;
    }

    public void setProjectRemark(String projectRemark) {
        this.projectRemark = projectRemark;
    }

    public Integer getMemberId() {
        return memberId;
    }

    public void setMemberId(Integer memberId) {
        this.memberId = memberId;
    }

    public String getMemberName() {
        return memberName;
    }

    public void setMemberName(String memberName) {
        this.memberName = memberName;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public Integer getNum() {
        return num;
    }

    public void setNum(Integer num) {
        this.num = num;
    }

    public Integer getSupportmoney() {
        return supportmoney;
    }

    public void setSupportmoney(Integer supportmoney) {
        this.supportmoney = supportmoney;
    }

    public Integer getFreight() {
        return freight;
    }

    public void setFreight(Integer freight) {
        this.freight = freight;
    }

    public Integer getPurchase() {
        return purchase;
    }

    public void setPurchase(Integer purchase) {
        this.purchase = purchase;
    }

    public Integer getSignalpurchase() {
        return signalpurchase;
    }

    public void setSignalpurchase(Integer signalpurchase) {
        this.signalpurchase = signalpurchase;
    }

    public BigDecimal getTotalPrice() {
        return totalPrice;
    }

    public void setTotalPrice(BigDecimal totalPrice) {
        this.totalPrice = totalPrice;
    }
}
