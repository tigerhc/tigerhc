package com.lmrj.rms.recipe.entity;

import com.baomidou.mybatisplus.annotations.TableField;
import com.baomidou.mybatisplus.annotations.TableName;
import com.google.common.collect.Lists;
import com.lmrj.core.entity.BaseDataEntity;
import com.lmrj.core.excel.annotation.ExcelField;
import lombok.Data;

import java.util.List;

/**
 * All rights Reserved, Designed By www.lmrj.com
 *
 * @version V1.0
 * @package com.lmrj.rms.recipe.entity
 * @title: rms_recipe实体
 * @description: rms_recipe实体
 * @author: zhangweijiang
 * @date: 2019-06-15 01:58:00
 * @copyright: 2019 www.lmrj.com Inc. All rights reserved.
 */

@TableName("rms_recipe")
@SuppressWarnings("serial")
@Data
public class RmsRecipe extends BaseDataEntity {

    /**配方CODE*/
    @ExcelField(title="配方CODE", type=0, align=1, sort=20)
    @TableField(value = "recipe_code")
    private String recipeCode;
    /**配方名称(备用)*/
    @TableField(value = "recipe_name")
    private String recipeName;
    /**配方描述*/
    @TableField(value = "recipe_desc")
    private String recipeDesc;
    /**生产清模润模保留，暂不用*/
    @TableField(value = "recipe_type")
    private String recipeType;
    /**设备号*/
    @TableField(value = "eqp_id")
    private String eqpId;
    /**设备类型*/
    @TableField(value = "eqp_model_id")
    private String eqpModelId;
    /**设备类型名称*/
    @TableField(value = "eqp_model_name")
    private String eqpModelName;
    /**配方版本,DRAFT,EQP,GOLD*/
    @TableField(value = "version_type")
    private String versionType;
    /**配方版本号*/
    @TableField(value = "version_no")
    private Double versionNo;
    /**状态 0、创建（不可用）Y、生效（可用）N、停用（不可用）*/
    @TableField(value = "status")
    private String status;
    /**golden是否生效标记*/
    @TableField(value = "gold_active_flag")
    private String goldActiveFlag;
    /**原始配方ID*/
    @TableField(value = "last_recipe_id")
    private String lastRecipeId;
    @TableField(value = "approve_step")
    private String approveStep;
    @TableField(value = "approve_result")
    private String approveResult ;
    @TableField(exist = false)
    private List<RmsRecipeBody> rmsRecipeBodyDtlList = Lists.newArrayList();
    @TableField(exist = false)
    private String oldId;

}
