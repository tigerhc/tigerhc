package com.lmrj.dsk.eqplog.service.impl;

import com.lmrj.common.mybatis.mvc.service.impl.CommonServiceImpl;
import com.lmrj.dsk.eqplog.entity.ChipMove;
import com.lmrj.dsk.eqplog.mapper.ChipMoveMapper;
import com.lmrj.dsk.eqplog.service.IChipMoveService;
import com.lmrj.util.lang.StringUtil;
import org.apache.commons.collections.MapUtils;
import org.springframework.stereotype.Service;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service("mapTrayChipMoveService")
public class ChipMoveServiceImpl extends CommonServiceImpl<ChipMoveMapper, ChipMove> implements IChipMoveService {
    @Override
    public int insertData(List<Map<String, Object>> dataList) {
        List<ChipMove> mapperList = new ArrayList<>();
        try{
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
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
                if("APJ-HB2-SORT2".equals(data.getEqpId())){
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
            e.printStackTrace();
        }
        return 0;
    }
}
