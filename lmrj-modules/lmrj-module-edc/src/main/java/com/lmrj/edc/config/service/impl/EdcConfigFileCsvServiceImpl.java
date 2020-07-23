package com.lmrj.edc.config.service.impl;

import com.lmrj.common.mybatis.mvc.service.impl.CommonServiceImpl;
import com.lmrj.edc.config.service.IEdcConfigFileCsvService;
import com.lmrj.edc.config.entity.EdcConfigFileCsv;
import com.lmrj.edc.config.mapper.EdcConfigFileCsvMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


/**
* All rights Reserved, Designed By www.lmrj.com
*
* @version V1.0
* @package com.lmrj.edc.config.service.impl
* @title: edc_config_file_csv服务实现
* @description: edc_config_file_csv服务实现
* @author: 张伟江
* @date: 2020-07-23 16:12:15
* @copyright: 2018 www.lmrj.com Inc. All rights reserved.
*/
@Transactional
@Service("edcConfigFileCsvService")
public class EdcConfigFileCsvServiceImpl  extends CommonServiceImpl<EdcConfigFileCsvMapper,EdcConfigFileCsv> implements  IEdcConfigFileCsvService {

}