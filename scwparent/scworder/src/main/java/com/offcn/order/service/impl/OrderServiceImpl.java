package com.offcn.order.service.impl;

import com.alibaba.druid.util.StringUtils;
import com.offcn.dycommon.enums.OrderStatusEnumes;
import com.offcn.dycommon.response.AppResponse;
import com.offcn.order.exception.OrderException;
import com.offcn.order.mapper.TOrderMapper;
import com.offcn.order.po.TOrder;
import com.offcn.order.po.TReturn;
import com.offcn.order.service.OrderService;
import com.offcn.order.service.ProjectServiceFeign;
import com.offcn.order.vo.req.OrderInfoSubmitVo;
import com.offcn.util.AppDateUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.UUID;

@Service
public class OrderServiceImpl implements OrderService {

    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    @Autowired
    private ProjectServiceFeign projectServiceFeign;

    @Autowired
    private TOrderMapper orderMapper;

    @Override
    public TOrder saveOrder(OrderInfoSubmitVo orderVo) {

        TOrder order = new TOrder();
        //1、获取当前登录人Id
        String userToken = stringRedisTemplate.opsForValue().get(orderVo.getAccessToken());
        if(StringUtils.isEmpty(userToken)){
            throw new OrderException("用户未登陆，没有订单模块访问权限");
        }
        Integer memberId = Integer.parseInt(userToken);
        order.setMemberid(memberId);

        order.setProjectid(orderVo.getProjectid());
        order.setReturnid(orderVo.getReturnid());
        //2、生成订单Id
        order.setOrdernum(UUID.randomUUID().toString().replace("-",""));
        //3、创建时间
        order.setCreatedate(AppDateUtils.dateToStr(new Date()));
        //4、远程访问
        AppResponse<List<TReturn>> response = projectServiceFeign.getReturn(orderVo.getProjectid());
        List<TReturn> returnList = response.getData();
        //5、获取回报列表中的第一个对象 计算消费者 在该平台 消费的总金额
        TReturn tReturn = returnList.get(0);
        Integer totalPrice = tReturn.getSupportmoney()*tReturn.getCount()+tReturn.getFreight();
        order.setMoney(totalPrice);
        //6、其他属性
        //回报数量
        order.setRtncount(orderVo.getRtncount());
        //支付状态  未支付
        order.setStatus(OrderStatusEnumes.UNPAY.getCode()+"");
        //收货地址
        order.setAddress(orderVo.getAddress());
        //是否开发票
        order.setInvoice(orderVo.getInvoice().toString());
        //发票名头
        order.setInvoictitle(orderVo.getInvoictitle());
        //备注
        order.setRemark(orderVo.getRemark());
        orderMapper.insert(order);
        return order;
    }

}
