package com.lmrj.mes.track.service;

import com.lmrj.common.mybatis.mvc.service.ICommonService;
import com.lmrj.core.entity.MesResult;
import com.lmrj.mes.track.entity.MesLotTrack;

import java.util.Date;
import java.util.List;

/**
 * All rights Reserved, Designed By www.lmrj.com
 *
 * @version V1.0
 * @package com.lmrj.mes.track.service
 * @title: mes_lot_track服务接口
 * @description: mes_lot_track服务接口
 * @author: 张伟江
 * @date: 2020-04-28 14:03:17
 * @copyright: 2019 www.lmrj.com Inc. All rights reserved.
 */
public interface IMesLotTrackService extends ICommonService<MesLotTrack> {
    MesResult trackin(String eqpId, String productionNo, String productionName, String orderNo, String lotNo, String recipeCode, String opId);

    MesResult trackout(String eqpId, String productionNo, String productionName, String orderNo, String lotNo, String yield, String recipeCode, String opId);

    MesResult findRecipeName(String eqpId, String opId);

    MesResult findTemp(String eqpId, String opId);

    MesResult findParam(String eqpId, String param, String opId, String lotNo, String productionName);

    MesLotTrack findLotNo(String startTime,String eqpId);

    MesLotTrack findNextStartTime(String endTime,String eqpId);

    List<MesLotTrack> findIncompleteLotNo(Date startTime,Date endTime);

    MesLotTrack finddata(String eqpId,String productionNo);

    Boolean updateWip(Integer lotYield,Integer lotYieldEqp,String eqpId,String productionNo);

    Boolean insterWip(String id, String eqpId, String lotNo, String productionName, String productionNo, String orderNo, Integer lotYield, Integer lotYieldEqp, Date startTime, Date endTime, String remarks, String createBy, Date create_date);

    Boolean deleteWip(Date startTime,Date endTime);

}
