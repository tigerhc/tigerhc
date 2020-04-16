package com.lmrj.rms.recipe.service.impl;

import com.lmrj.rms.RmsBootApplication;
import com.lmrj.rms.recipe.entity.RmsRecipe;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

/**
 * Created by zwj on 2019/10/15.
 */
@SpringBootTest(classes = RmsBootApplication.class)
@RunWith(SpringRunner.class)
public class RmsRecipeServiceImplTest {

    @Autowired
    RmsRecipeServiceImpl rmsRecipeServiceImpl;

    @Test
    public void findLastByName() throws Exception {
        RmsRecipe rmsRecipe = rmsRecipeServiceImpl.selectById("be247412263c43cf9414288488be6122");
        RmsRecipe rmsRecipeLast = rmsRecipeServiceImpl.findLastByRecipeCode(rmsRecipe);
    }

    @Test
    public void uploadRecipe() throws Exception {

    }

}