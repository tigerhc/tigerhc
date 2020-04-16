package com.lmrj.edc.state.controller;

import com.google.common.collect.Lists;
import com.lmrj.common.http.Response;
import com.lmrj.common.mvc.annotation.ViewPrefix;
import com.lmrj.common.mybatis.mvc.controller.BaseCRUDController;
import com.lmrj.common.security.shiro.authz.annotation.RequiresPathPermission;
import com.lmrj.edc.state.entity.RptEqpStateDay;
import com.lmrj.edc.state.service.IRptEqpStateDayService;
import com.lmrj.util.lang.ArrayUtil;
import com.lmrj.core.log.LogAspectj;
import org.apache.shiro.util.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;


/**
 * All rights Reserved, Designed By www.lmrj.com
 *
 * @version V1.0
 * @package com.lmrj.edc.state.controller
 * @title: rpt_eqp_state_day控制器
 * @description: rpt_eqp_state_day控制器
 * @author: 张伟江
 * @date: 2020-02-20 01:26:27
 * @copyright: 2019 www.lmrj.com Inc. All rights reserved.
 */

@RestController
@RequestMapping("edc/rpteqpstateday")
@ViewPrefix("edc/rpteqpstateday")
@RequiresPathPermission("edc:rpteqpstateday")
@LogAspectj(title = "rpt_eqp_state_day")
public class RptEqpStateDayController extends BaseCRUDController<RptEqpStateDay> {

    @Autowired
    private IRptEqpStateDayService rptEqpStateDayService;

    @GetMapping("eqp")
    public Response day(String beginTime,String endTime,String eqpId){
        Response response=new Response();
        beginTime=beginTime.replaceAll("-","");
        endTime=endTime.replaceAll("-","");
        List eqpList = Lists.newArrayList();
        if(eqpId.contains(",")){
            eqpList = ArrayUtil.split(eqpId, ",");
        }else{
            eqpList = ArrayUtil.split(eqpId, "\n");
        }
        List<Map> list = rptEqpStateDayService.findEqpOee(beginTime, endTime, eqpList);
        if(!CollectionUtils.isEmpty(list)) {
            list.forEach(map -> {
                map.put("periodDate", getTimes((String) map.get("periodDate")));
            });
        }

        List<Map> list2 = rptEqpStateDayService.findEqpsOee(beginTime, endTime, eqpList);
        if(!CollectionUtils.isEmpty(list2)) {
            list2.forEach(map -> {
                map.put("periodDate", getTimes((String) map.get("periodDate")));
            });
        }
        response.put("count",list.size());
        response.put("eqpOee",list);
        response.put("eqpsOee",list2);
        return response;
    }
    public static Double getHour(Double second){
        return (double) Math.round(second/36)/100;
    }



    private String getTimes(String str){

        String time=str.substring(0,4)+"-"+str.substring(4,6)+"-"+str.substring(6);

        return time;
    }
}
