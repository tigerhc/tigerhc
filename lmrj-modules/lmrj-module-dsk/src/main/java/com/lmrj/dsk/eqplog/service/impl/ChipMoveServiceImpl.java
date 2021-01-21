package com.lmrj.dsk.eqplog.service.impl;

import com.lmrj.common.mybatis.mvc.service.impl.CommonServiceImpl;
import com.lmrj.dsk.constant.EqpNameConstant;
import com.lmrj.dsk.eqplog.entity.ChipMove;
import com.lmrj.dsk.eqplog.entity.ChipBox;
import com.lmrj.dsk.eqplog.mapper.ChipMoveMapper;
import com.lmrj.dsk.eqplog.service.IChipMoveService;
import com.lmrj.util.lang.StringUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.MapUtils;
import org.springframework.stereotype.Service;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service("mapTrayChipMoveService")
@Slf4j
public class ChipMoveServiceImpl extends CommonServiceImpl<ChipMoveMapper, ChipMove> implements IChipMoveService {
    private SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    @Override
    public int insertData(List<Map<String, Object>> dataList) {
        List<ChipMove> mapperList = new ArrayList<>();
        try{
            for(Map<String, Object> item : dataList){
                ChipMove data = new ChipMove();
                String chipId = MapUtils.getString(item, "chipId");
                if(StringUtil.isEmpty(chipId)||"null".equals(chipId)||"NULL".equals(chipId)){
                    chipId = null;
                }
                data.setEqpId(MapUtils.getString(item, "eqpId"));
                data.setProductionNo(MapUtils.getString(item, "lotYield"));
                data.setLotNo(MapUtils.getString(item, "lotNo"));
                data.setFromTrayId(MapUtils.getString(item, "fromTrayId"));
                String fromRow = MapUtils.getString(item, "fromRow");
                if(StringUtil.isEmpty(fromRow)){
                    data.setFromX(null);
                }else{
                    data.setFromX(Integer.parseInt(fromRow));
                }
                String fromCol = MapUtils.getString(item, "fromCol");
                if(StringUtil.isEmpty(fromCol)){
                    data.setFromY(null);
                }else{
                    data.setFromY(Integer.parseInt(fromCol));
                }
                data.setToTrayId(MapUtils.getString(item, "toTrayId"));
                if(EqpNameConstant.EQP_LAST_SORT.equals(data.getEqpId())){
                    data.setToX(1);
                    data.setToY(1);
                }else{
                    data.setToX(MapUtils.getIntValue(item, "toRow"));
                    data.setToY(MapUtils.getIntValue(item, "toCol"));
                }
                data.setJudgeResult(MapUtils.getString(item, "judgeResult"));
                data.setStartTime(sdf.parse(MapUtils.getString(item, "startTime")));
                data.setChipId(chipId);
                mapperList.add(data);
            }
            if(mapperList.size()>0){
                return baseMapper.insertMoveLog(mapperList);
            }
        } catch (Exception e){
            log.error("保存追溯的前段数据有误", e);
        }
        return 0;
    }

    @Override
    public int insertChipIdData(List<Map<String, Object>> dataList) {
        List<ChipBox> mapperList = new ArrayList<>();
        List<ChipMove> moveList = new ArrayList<>();
        try {
            for(Map<String, Object> item : dataList){
                String eqpId = MapUtils.getString(item, "eqpId");
                if(EqpNameConstant.EQP_CLEAN_US.equals(eqpId)||EqpNameConstant.EQP_JET.equals(eqpId)){
                    ChipBox data = new ChipBox();
                    data.setEqpId(eqpId);
                    data.setProductionNo(MapUtils.getString(item, "productionNo"));
                    data.setLotNo(MapUtils.getString(item, "lotNo"));
                    data.setToTrayId(MapUtils.getString(item, "toTrayId"));
                    data.setJudgeResult(MapUtils.getString(item, "judgeResult"));
                    data.setStartTime(sdf.parse(MapUtils.getString(item, "startTime")));
                    mapperList.add(data);
                }else if(EqpNameConstant.EQP_TRM.equals(eqpId)){
                    //速风机需要将洗净机的日志补全
                    ChipMove data = new ChipMove();
                    String chipId = MapUtils.getString(item, "chipId");

                }else{
                    ChipMove data = new ChipMove();
                    data.setEqpId(MapUtils.getString(item, "eqpId"));
                    data.setProductionNo(MapUtils.getString(item, "productionNo"));
                    data.setLotNo(MapUtils.getString(item, "lotNo"));
                    data.setJudgeResult(MapUtils.getString(item, "judgeResult"));
                    data.setStartTime(sdf.parse(MapUtils.getString(item, "startTime")));
                    data.setChipId(MapUtils.getString(item, "chipId"));
                    String toTrayId = MapUtils.getString(item, "toTrayId");
                    if(!StringUtil.isEmpty(toTrayId)){
                        data.setToTrayId(toTrayId);
                        data.setToX(1);
                        data.setToY(1);
                    }
                    moveList.add(data);
                }
            }
            if(mapperList.size()>0){
                baseMapper.insertChipBox(mapperList);
            }
            if(moveList.size()>0){
                baseMapper.insertMoveLog(moveList);
            }
        } catch (Exception e){
            log.error("保存追溯的后半段数据有误", e);
        }
        return 0;
    }
}
