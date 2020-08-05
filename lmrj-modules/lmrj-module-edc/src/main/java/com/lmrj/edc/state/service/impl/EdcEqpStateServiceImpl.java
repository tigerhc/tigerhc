package com.lmrj.edc.state.service.impl;

import com.google.common.collect.Lists;
import com.lmrj.common.mybatis.mvc.service.impl.CommonServiceImpl;
import com.lmrj.common.mybatis.mvc.wrapper.EntityWrapper;
import com.lmrj.edc.quartz.MapUtil;
import com.lmrj.edc.state.entity.EdcEqpState;
import com.lmrj.edc.state.entity.RptEqpStateDay;
import com.lmrj.edc.state.mapper.EdcEqpStateMapper;
import com.lmrj.edc.state.service.IEdcEqpStateService;
import com.lmrj.edc.state.service.IRptEqpStateDayService;
import com.lmrj.fab.log.service.IFabLogService;
import com.lmrj.util.calendar.DateUtil;
import com.lmrj.util.lang.StringUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.shiro.util.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.text.ParseException;
import java.util.*;


/**
* All rights Reserved, Designed By www.lmrj.com
*
* @version V1.0
* @package com.lmrj.edc.state.service.impl
* @title: edc_eqp_state服务实现
* @description: edc_eqp_state服务实现
* @author: 张伟江
* @date: 2020-02-20 01:26:46
* @copyright: 2018 www.lmrj.com Inc. All rights reserved.
*/
@Transactional
@Service("edcEqpStateService")
@Slf4j
public class EdcEqpStateServiceImpl  extends CommonServiceImpl<EdcEqpStateMapper,EdcEqpState> implements  IEdcEqpStateService {

    @Autowired
    private IFabLogService fabLogService;
    @Autowired
    private EdcEqpStateMapper edcEqpStateMapper;
    @Autowired
    private IRptEqpStateDayService rptEqpStateDayService;

    /**
     * 更新end_time, state_times
     * @param startTime
     * @param endTime
     * @return
     */
    @Override
    public int syncEqpSate(Date startTime, Date endTime) {
        List<EdcEqpState> eqpStateList=edcEqpStateMapper.getAllByTime(startTime, endTime);
        List<EdcEqpState> neweqpStateList=new ArrayList<>();
        for (int i = 0; i < eqpStateList.size()-1; i++) {
            EdcEqpState edcEqpState=eqpStateList.get(i);
            EdcEqpState nextedcEqpState=eqpStateList.get(i+1);
            edcEqpState.setEndTime(nextedcEqpState.getStartTime());
            Double stateTime=(double)(nextedcEqpState.getStartTime().getTime()-edcEqpState.getStartTime().getTime());
            edcEqpState.setStateTimes(stateTime);
            neweqpStateList.add(edcEqpState);
        }
        if(CollectionUtils.isEmpty(eqpStateList)){
            return 0;
        }else {
            if(this.updateBatchById(neweqpStateList)){
                log.info("edc_eqp_state更新成功");
                String eventId = StringUtil.randomTimeUUID("RPT");
                fabLogService.info("",eventId,"edc_eqp_state更新","数据更新成功","","");
            }
        }
        return eqpStateList.size();
    }

    /**
     * 计算日OEE
     * @param periodDate
     * @return
     */
    @Override
    public int calEqpSateDay(String periodDate) {
        Date startTime = null;
        Date endTime = null;
        try {
            startTime = DateUtil.parseDate(periodDate+"080000", "yyyyMMddHHmmss");
            Calendar cal = Calendar.getInstance();
            cal.setTime(startTime);
            cal.add(Calendar.DAY_OF_MONTH,1);
            endTime = cal.getTime();
        } catch (ParseException e) {
            e.printStackTrace();
        }
        //计算日OEE
        List<EdcEqpState> eqpDayStateList=edcEqpStateMapper.calEqpSateDay(startTime, endTime);
        if(CollectionUtils.isEmpty(eqpDayStateList)){
            return 0;
        }
        List<RptEqpStateDay> rptEqpStateDayList= Lists.newArrayList();
        //日OEE分组
        Map<String, List<EdcEqpState>> eqpStatetMap = MapUtil.listToMapList(eqpDayStateList, e -> e.getEqpId());
        //计算日OEE
        for (List<EdcEqpState> list : eqpStatetMap.values()) {
            RptEqpStateDay rptEqpStateDay=new RptEqpStateDay();
            rptEqpStateDay.setEqpId(list.get(0).getEqpId());
            rptEqpStateDay.setPeriodDate(DateUtil.formatDate(startTime, "yyyyMMdd"));
            Double idle=0.0;
            Double run=0.0;
            Double down=0.0;
            for(EdcEqpState edcEqpState:list){
                if("run".equalsIgnoreCase(edcEqpState.getState())){
                    run=run+edcEqpState.getStateTimes();
                }
                if("down".equalsIgnoreCase(edcEqpState.getState())){
                    down=down+edcEqpState.getStateTimes();
                }
                if("idle".equalsIgnoreCase(edcEqpState.getState())){
                    idle=idle+edcEqpState.getStateTimes();
                }
            }
            rptEqpStateDay.setRunTime(run);
            rptEqpStateDay.setDownTime(down);
            rptEqpStateDay.setIdleTime(idle);
            rptEqpStateDayList.add(rptEqpStateDay);
        }
        //先删除day表 按照时间删除 在插入
        if(rptEqpStateDayService.delete(new EntityWrapper<RptEqpStateDay>().eq("period_date",periodDate))
            && rptEqpStateDayService.insertBatch(rptEqpStateDayList)){
            String eventId = StringUtil.randomTimeUUID("RPT");
            fabLogService.info("",eventId,"OEE计算更新","数据更新成功","","");
        };
        return rptEqpStateDayList.size();
    }
}
