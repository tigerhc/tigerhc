package com.lmrj.mes.lot.mapper;

import com.baomidou.mybatisplus.mapper.BaseMapper;
import com.lmrj.mes.lot.entity.MesLotWip;
import com.lmrj.mes.track.entity.MesLotTrack;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * All rights Reserved, Designed By www.lmrj.com
 *
 * @version V1.0
 * @package com.lmrj.mes.lot.mapper
 * @title: mes_lot_wip数据库控制层接口
 * @description: mes_lot_wip数据库控制层接口
 * @author: 张伟江
 * @date: 2020-08-05 10:32:55
 * @copyright: 2019 www.lmrj.com Inc. All rights reserved.
 */
@Mapper
public interface MesLotWipMapper extends BaseMapper<MesLotWip> {
    /*@Select("select * from mes_lot_track where  (PRODUCTION_NO, LOT_NO, start_time ) in (\n" +
         "SELECT PRODUCTION_NO, LOT_NO ,  max(start_time) FROM mes_lot_track WHERE  ( PRODUCTION_NO, LOT_NO )  NOT IN (\n" +
         "select DISTINCT PRODUCTION_NO, LOT_NO from mes_lot_track where eqp_id LIKE '%TRM%' AND END_TIME IS NOT NULL and create_date between #{startTime} and #{endTime})\n" +
         "and create_date between #{startTime} and #{endTime} \n" +
         "group by PRODUCTION_NO,LOT_NO)")*/
    List<MesLotTrack> findIncompleteLotNo(@Param("startTime") Date startTime, @Param("endTime") Date endTime);

    @Select("select * from mes_lot_wip where lot_no = #{lotNo} and production_no = #{productionNo}")
    MesLotWip finddata(@Param("lotNo") String lotNo, @Param("productionNo") String productionNo);

    @Select("select * from mes_lot_wip")
    List<MesLotWip> selectWip();

    @Select("select DISTINCT PRODUCTION_NO from mes_lot_track where eqp_id LIKE '%SIM-TRM%' AND END_TIME IS NOT NULL and lot_no = #{lotNo} and production_no = #{productionNo}")
    String selectEndData(@Param("lotNo") String lotNo, @Param("productionNo") String productionNo);

    @Delete("delete from mes_lot_wip where lot_no = #{lotNo} and production_no = #{productionNo}")
    Boolean deleteEndData(@Param("lotNo") String lotNo, @Param("productionNo") String productionNo);

    @Select("select station_id, station_code ,step_id,step_code from fab_equipment where eqp_id=#{eqpId}")
    MesLotWip findStep(@Param("eqpId") String eqpId);

    List<Map> findLotYield(@Param("officeId") String lineNo);

    @Select("select count(*) from edc_dsk_log_production where eqp_id=#{eqpId} and lot_no=#{lotNo} and production_no=#{productionNo}")
    Integer selectLotYieldEqp();

    @Select("select count(eqp_id) from edc_dsk_log_production where eqp_id = #{eqpId} and start_time between #{startTime} and #{endTime} ")
    int findDayLotYield(@Param("eqpId") String eqpId,@Param("startTime") Date startTime, @Param("endTime") Date endTime);
}
