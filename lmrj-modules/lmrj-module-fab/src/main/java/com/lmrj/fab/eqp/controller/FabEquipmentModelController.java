package com.lmrj.fab.eqp.controller;


import com.alibaba.fastjson.JSON;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.lmrj.common.http.DateResponse;
import com.lmrj.common.mvc.annotation.ViewPrefix;
import com.lmrj.common.mybatis.mvc.controller.BaseCRUDController;
import com.lmrj.common.security.shiro.authz.annotation.RequiresMethodPermissions;
import com.lmrj.common.security.shiro.authz.annotation.RequiresPathPermission;
import com.lmrj.common.utils.FastJsonUtils;
import com.lmrj.common.utils.ServletUtils;
import com.lmrj.fab.eqp.entity.FabEquipmentModel;
import com.lmrj.fab.eqp.service.IFabEquipmentModelService;
import com.lmrj.util.lang.StringUtil;
import com.lmrj.core.log.LogAspectj;
import com.lmrj.core.sys.entity.User;
import com.lmrj.cim.utils.UserUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * All rights Reserved, Designed By www.lmrj.com
 *
 * @version V1.0
 * @package com.lmrj.fab.controller
 * @title: fab_equipment_model控制器
 * @description: fab_equipment_model控制器
 * @author: kang
 * @date: 2019-06-07 22:18:19
 * @copyright: 2018 www.lmrj.com Inc. All rights reserved.
 */

@RestController
@RequestMapping("fab/fabequipmentmodel")
@ViewPrefix("eqpmodel/fabequipmentmodel")
@RequiresPathPermission("FabEquipmentModel")
@LogAspectj(title = "fab_equipment_model")
public class FabEquipmentModelController extends BaseCRUDController<FabEquipmentModel> {

    @Autowired
    private IFabEquipmentModelService fabEquipmentModelService;
    /**
     * 在返回对象之前编辑数据
     *
     * @param entity
     */
    @Override
    public void afterFind(FabEquipmentModel entity) {
        //创建人
        if(StringUtil.isNotBlank(entity.getCreateBy())){
            User creater = UserUtil.getUser(entity.getCreateBy());
            if(creater != null){
                entity.setCreateByName(creater.getUsername());
            }
        }
        //更新人
        if(StringUtil.isNotBlank(entity.getUpdateBy())){
            User updater = UserUtil.getUser(entity.getUpdateBy());
            if(updater != null){
                entity.setUpdateByName(updater.getUsername());
            }
        }
    }

    /**
     * 在返回list数据之前编辑数据
     *
     * @param pagejson
     */
    @Override
    public void afterList(DateResponse pagejson, HttpServletRequest request, HttpServletResponse response) {
        List<FabEquipmentModel> list = (List<FabEquipmentModel>) pagejson.getResults();
        for(FabEquipmentModel fabEquipmentModel: list){
            fabEquipmentModel.setModelName(fabEquipmentModel.getManufacturerName()+"-"+fabEquipmentModel.getClassCode());
        }
    }

    /**
     * 测试使用.
     * 仅查询设备类型,同事设备类型由厂家-型号拼接而成
     * 不过建议直接返回Response对象
     *
     */
    @RequestMapping(value = "lookup", method = { RequestMethod.GET, RequestMethod.POST })
    //@PageableDefaults(sort = "id=desc") 不再默认配置
    @RequiresMethodPermissions("list")
    private void lookup() throws IOException {
        List<Map> list = fabEquipmentModelService.findLookup();
        FastJsonUtils.print(list);
    }

    /**
     * 设备厂家下拉框
     * @param model
     * @param request
     * @param response
     * @return
     */
    @RequestMapping(value = "/manufacturerNameList", method = { RequestMethod.GET, RequestMethod.POST })
    public void manufacturerNameList(Model model, HttpServletRequest request,
                          HttpServletResponse response) {
        List<String> manufacturerNameList = fabEquipmentModelService.manufacturerNameList();
        List<Map> list = Lists.newArrayList();
        for (String manufacturerName : manufacturerNameList) {
            Map map = Maps.newHashMap();
            map.put("id", manufacturerName);
            list.add(map);
        }
        DateResponse listjson = new DateResponse(list);
        String content = JSON.toJSONString(listjson);
        ServletUtils.printJson(response, content);
    }

    /**
     * 设备类型下拉框
     * @param model
     * @param request
     * @param response
     * @return
     */
    @RequestMapping(value = "/classCodeList", method = { RequestMethod.GET, RequestMethod.POST })
    public void classCodeList(Model model, HttpServletRequest request,
                          HttpServletResponse response) {
        List<String> classCodeList = fabEquipmentModelService.classCodeList();
        List<Map> list = Lists.newArrayList();
        for (String classCode : classCodeList) {
            Map map = Maps.newHashMap();
            map.put("id", classCode);
            list.add(map);
        }
        DateResponse listjson = new DateResponse(list);
        String content = JSON.toJSONString(listjson);
        ServletUtils.printJson(response, content);
    }


    /**
     * 设备类型下拉框
     * @param model
     * @param request
     * @param response
     * @return
     */
    @RequestMapping(value = "/modelTemplateList", method = { RequestMethod.GET, RequestMethod.POST })
    public void modelTemplateList(Model model, HttpServletRequest request,
                              HttpServletResponse response) {
        List<FabEquipmentModel> templateList = new ArrayList<>();
        FabEquipmentModel a = new FabEquipmentModel();
        a.setParentType("1");
        a.setType("1");
        a.setClassCode("1");
        a.setModelnumber("1");
        a.setModelName("速度传感器NB");
        templateList.add(a);
        a = new FabEquipmentModel();
        a.setParentType("1");
        a.setType("2");
        a.setClassCode("1");
        a.setModelnumber("1");
        a.setModelName("温度传感器MM");
        templateList.add(a);
        a = new FabEquipmentModel();
        a.setParentType("2");
        a.setType("1");
        a.setClassCode("1");
        a.setModelnumber("1");
        a.setModelName("电表");
        templateList.add(a);

        DateResponse listjson = new DateResponse(templateList);
        String content = JSON.toJSONString(listjson);
        ServletUtils.printJson(response, content);
    }


    /**
     * 设备类型下拉框
     * @param model
     * @param request
     * @param response
     * @return
     */
    @RequestMapping(value = "/oneTemplateList", method = { RequestMethod.GET, RequestMethod.POST })
    public void oneTemplateList(Model model, HttpServletRequest request,
                                  HttpServletResponse response) {
        List<FabEquipmentModel> templateList = new ArrayList<>();
        FabEquipmentModel a = new FabEquipmentModel();
        a.setParentType("1");
        a.setType("1");
        a.setClassCode("1");
        a.setModelnumber("1");
        a.setModelName("速度传感器NB");
        templateList.add(a);

        DateResponse listjson = new DateResponse(templateList);
        String content = JSON.toJSONString(listjson);
        ServletUtils.printJson(response, content);
    }




}
