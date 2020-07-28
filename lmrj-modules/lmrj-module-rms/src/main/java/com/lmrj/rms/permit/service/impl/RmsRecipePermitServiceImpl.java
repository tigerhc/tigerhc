package com.lmrj.rms.permit.service.impl;

import com.lmrj.common.mybatis.mvc.service.impl.CommonServiceImpl;
import com.lmrj.common.mybatis.mvc.wrapper.EntityWrapper;
import com.lmrj.rms.log.service.IRmsRecipeLogService;
import com.lmrj.rms.permit.entity.RmsRecipePermitConfig;
import com.lmrj.rms.permit.service.IRmsRecipePermitConfigService;
import com.lmrj.rms.permit.service.IRmsRecipePermitService;
import com.lmrj.rms.permit.entity.RmsRecipePermit;
import com.lmrj.rms.permit.mapper.RmsRecipePermitMapper;
import com.lmrj.rms.recipe.entity.RmsRecipe;
import com.lmrj.rms.recipe.service.IRmsRecipeService;
import com.lmrj.util.file.FtpUtil;
import org.apache.commons.net.ftp.FTPFile;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.annotation.AccessType;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;


/**
* All rights Reserved, Designed By www.lmrj.com
*
* @version V1.0
* @package com.lmrj.rms.permit.service.impl
* @title: rms_recipe_permit服务实现
* @description: rms_recipe_permit服务实现
* @author: 张伟江
* @date: 2020-07-15 23:08:38
* @copyright: 2018 www.lmrj.com Inc. All rights reserved.
*/
@Transactional
@Service("rmsRecipePermitService")
public class RmsRecipePermitServiceImpl  extends CommonServiceImpl<RmsRecipePermitMapper,RmsRecipePermit> implements  IRmsRecipePermitService {

    @Autowired
    private IRmsRecipeService recipeService;
    @Autowired
    private IRmsRecipePermitConfigService recipePermitConfigService;
    @Autowired
    private IRmsRecipeLogService rmsRecipeLogService;

    public static String[] FTP94 = new String[]{"106.12.76.94", "21", "cim", "Pp123!@#"};

    //老版遗弃
    @Override
    public void recipePermit(String approveStep, String roleName, String recipeId, String submitResult, String submitDesc) throws Exception{
        RmsRecipePermit recipePermit = new RmsRecipePermit();
        List<RmsRecipePermitConfig> permitConfigs = recipePermitConfigService.selectList(new EntityWrapper<RmsRecipePermitConfig>().eq("submitter_role_name", roleName));
        RmsRecipePermitConfig recipePermitConfig = new RmsRecipePermitConfig();
        if (permitConfigs.size() > 0){
            recipePermitConfig = permitConfigs.get(0);
        }
        RmsRecipe recipe = recipeService.selectById(recipeId);
        recipePermit.setRecipeId(recipeId);
        recipePermit.setRecipeCode(recipe.getRecipeCode());
        recipePermit.setWiRecipeId(recipeId);
        recipePermit.setSubmitterId(recipePermitConfig.getSubmitterRoleId());
        recipePermit.setSubmitterRole(roleName);
        recipePermit.setSubmitResult(submitResult);
        recipePermit.setSubmitDesc(submitDesc);
        recipePermit.setSubmitDate(new Date());
        baseMapper.insert(recipePermit);
        if ("1".equals(submitResult)){
            //审批通过，修改配方审批结果，并将审批状态加1,如果审批状态为3则将审批状态改为0
            if ("EQP".equals(recipe.getVersionType())){
                if ("2".equals(approveStep)){
                    recipe.setApproveStep("0");
                } else {
                    int i = Integer.parseInt(approveStep);
                    i++;
                    recipe.setApproveStep(i + "");
                }
            }
            if ("GOLD".equals(recipe.getVersionType())){
                if ("3".equals(approveStep)){
                    recipe.setApproveStep("0");
                } else {
                    int i = Integer.parseInt(approveStep);
                    i++;
                    recipe.setApproveStep(i + "");
                }
            }
            recipe.setApproveResult(submitResult);
            recipeService.updateById(recipe);
        }else{
            //审批不通过，修改配方审批结果
            recipe.setApproveResult(submitResult);
            recipeService.updateById(recipe);
        }
    }

    //新版再用
    @Override
    public void permit(String roleName, String recipeId, String submitResult, String submitDesc) {
        List<RmsRecipePermit> rmsRecipePermits = baseMapper.selectList(new EntityWrapper<RmsRecipePermit>().eq("recipe_id", recipeId).isNull("submit_result"));
        RmsRecipePermit recipePermit = baseMapper.selectList(new EntityWrapper<RmsRecipePermit>().eq("recipe_id", recipeId).isNull("submit_result").eq("submitter_role", roleName)).get(0);
        RmsRecipe recipe = recipeService.selectById(recipeId);
        rmsRecipeLogService.addLog(recipe,"permit",recipe.getEqpId());
        recipe.setApproveResult(submitResult);
        int approveStep = Integer.parseInt(recipe.getApproveStep());
        approveStep++;
        if ("1".equals(submitResult)){
            //审批通过,修改审批信息，修改配方审批等级
            recipePermit.setSubmitResult(submitResult);
            recipePermit.setSubmitDesc(submitDesc);
            recipePermit.setSubmitDate(new Date());
            baseMapper.updateById(recipePermit);
            if (rmsRecipePermits.size() > 1) {
                //大于1说明不是最后一级审批
                recipe.setApproveStep(approveStep + "");
                recipeService.updateById(recipe);
            }else if(rmsRecipePermits.size() == 1){
                //修改文件路径,复制文件
                String filePath = null;
                if ("EQP".equals(recipe.getVersionType())){
                    filePath = "/recipe/shanghai/mold/" + recipe.getEqpModelName() + "/EQP/" + recipe.getEqpId() + "/" + recipe.getRecipeCode();
                } else if ("GOLD".equals(recipe.getVersionType())){
                    filePath = "/recipe/shanghai/mold/" + recipe.getEqpModelName() + "/GOLD/" + recipe.getRecipeCode();
                }
                //复制recipe到his文件夹
                FtpUtil.copyFile(FTP94,recipe.getRecipeFilePath(),recipe.getRecipeCode() + "_V" + recipe.getVersionNo(),filePath + "/HIS",recipe.getRecipeCode() + "_V" + recipe.getVersionNo());
                //删除原来不带版本号的recipe
                FtpUtil.deleteFile(FTP94,filePath + "/" + recipe.getRecipeCode());
                //复制为最新版本
                FtpUtil.copyFile(FTP94,recipe.getRecipeFilePath(),recipe.getRecipeCode() + "_V" + recipe.getVersionNo(),filePath,recipe.getRecipeCode());
                //删除草稿版
                FtpUtil.deleteFile(FTP94,"/recipe/shanghai/mold/" + recipe.getEqpModelName() + "/DRAFT/" + recipe.getEqpId() + "/" + recipe.getRecipeCode() + "/" + recipe.getRecipeCode() + "_V" + recipe.getVersionNo());
                recipe.setApproveStep("0");
                recipe.setStatus("Y");
                recipe.setRecipeFilePath(filePath + "/HIS");
                recipeService.updateById(recipe);
            }
        }else{
            //审批不通过，修改审批记录，修改配方状态，删除后续审批记录
            recipePermit.setSubmitResult(submitResult);
            recipePermit.setSubmitDesc(submitDesc);
            recipePermit.setSubmitDate(new Date());
            baseMapper.updateById(recipePermit);
            recipe.setApproveStep("1");
            recipe.setStatus("2");
            recipeService.updateById(recipe);
            List<String> ids = new ArrayList<>();
            for (RmsRecipePermit rmsRecipePermit:rmsRecipePermits) {
                if (!rmsRecipePermit.getId().equals(recipePermit.getId())){
                    ids.add(rmsRecipePermit.getId());
                }
            }
            if (ids.size() > 0){
                baseMapper.deleteBatchIds(ids);
            }
        }
    }


    @Override
    public void addPermitList(String recipeId, String versionType) {
        RmsRecipe recipe = recipeService.selectById(recipeId);
        Integer approveStep = Integer.parseInt(recipe.getApproveStep());
        if ("EQP".equals(versionType)){
            for (int i = approveStep; i<=2; i++){
                RmsRecipePermit recipePermit = new RmsRecipePermit();
                RmsRecipePermitConfig recipePermitConfig = recipePermitConfigService.selectList(new EntityWrapper<RmsRecipePermitConfig>().eq("submit_level", i + "")).get(0);
                recipePermit.setRecipeId(recipeId);
                recipePermit.setRecipeCode(recipe.getRecipeCode());
                recipePermit.setWiRecipeId(recipeId);
                recipePermit.setSubmitterId(recipePermitConfig.getSubmitterRoleId());
                recipePermit.setSubmitterRole(recipePermitConfig.getSubmitterRoleName());
                baseMapper.insert(recipePermit);
            }
        }
        if ("GOLD".equals(versionType)){
            for (int i = approveStep; i<=3; i++){
                RmsRecipePermit recipePermit = new RmsRecipePermit();
                RmsRecipePermitConfig recipePermitConfig = recipePermitConfigService.selectList(new EntityWrapper<RmsRecipePermitConfig>().eq("submit_level", i + "")).get(0);
                recipePermit.setRecipeId(recipeId);
                recipePermit.setRecipeCode(recipe.getRecipeCode());
                recipePermit.setWiRecipeId(recipeId);
                recipePermit.setSubmitterId(recipePermitConfig.getSubmitterRoleId());
                recipePermit.setSubmitterRole(recipePermitConfig.getSubmitterRoleName());
                baseMapper.insert(recipePermit);
            }
        }
    }
}
