package com.lmrj.dsk.eqplog.service.impl;

import com.lmrj.common.mybatis.mvc.service.impl.CommonServiceImpl;
import com.lmrj.dsk.eqplog.entity.EdcDskLogRecipe;
import com.lmrj.dsk.eqplog.entity.EdcDskLogRecipeBody;
import com.lmrj.dsk.eqplog.mapper.EdcDskLogRecipeMapper;
import com.lmrj.dsk.eqplog.service.IEdcDskLogRecipeBodyService;
import com.lmrj.dsk.eqplog.service.IEdcDskLogRecipeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.Serializable;
import java.util.List;


/**
* All rights Reserved, Designed By www.lmrj.com
*
* @version V1.0
* @package com.lmrj.dsk.eqplog.service.impl
* @title: edc_dsk_log_recipe服务实现
* @description: edc_dsk_log_recipe服务实现
* @author: 张伟江
* @date: 2020-04-17 17:21:17
* @copyright: 2018 www.lmrj.com Inc. All rights reserved.
*/
@Transactional
@Service("edcDskLogRecipeService")
public class EdcDskLogRecipeServiceImpl  extends CommonServiceImpl<EdcDskLogRecipeMapper,EdcDskLogRecipe> implements  IEdcDskLogRecipeService {

    @Autowired
    private IEdcDskLogRecipeBodyService edcDskLogRecipeBodyService;

    @Override
    public EdcDskLogRecipe selectById(Serializable id){
        EdcDskLogRecipe edcDskLogRecipe = super.selectById(id);
        String oldRecipeId=baseMapper.findOldRecipeId(edcDskLogRecipe.getEqpId(),edcDskLogRecipe.getStartTime());
        List<EdcDskLogRecipeBody> rmsRecipeBodyList = edcDskLogRecipeBodyService.selectParamList(edcDskLogRecipe.getId());
        List<EdcDskLogRecipeBody> oldRmsRecipeBodyList = edcDskLogRecipeBodyService.selectParamList(oldRecipeId);
        if(oldRmsRecipeBodyList.size()==rmsRecipeBodyList.size()){
            for (int i = 0; i < rmsRecipeBodyList.size(); i++) {
                if(oldRmsRecipeBodyList.get(i).getSortNo()==rmsRecipeBodyList.get(i).getSortNo()){
                    rmsRecipeBodyList.get(i).setPreValue(oldRmsRecipeBodyList.get(i).getSetValue());
                }
            }
        }
        edcDskLogRecipe.setEdcDskLogRecipeBodyList(rmsRecipeBodyList);
        edcDskLogRecipe.setOldId(oldRecipeId);
        return edcDskLogRecipe;
    }

    @Override
    public boolean insert(EdcDskLogRecipe edcDskLogRecipe) {
        // 保存主表
        super.insert(edcDskLogRecipe);
        // 保存细表
        List<EdcDskLogRecipeBody> edcDskLogRecipeBodyList = edcDskLogRecipe.getEdcDskLogRecipeBodyList();
        for (EdcDskLogRecipeBody edcDskLogRecipeBody : edcDskLogRecipeBodyList) {
            edcDskLogRecipeBody.setRecipeLogId(edcDskLogRecipe.getId());
        }
        edcDskLogRecipeBodyService.insertBatch(edcDskLogRecipeBodyList);
        return true;
    }
}
