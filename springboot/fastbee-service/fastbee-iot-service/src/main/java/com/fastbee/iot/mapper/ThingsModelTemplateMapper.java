package com.fastbee.iot.mapper;

import java.util.List;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import com.fastbee.common.annotation.DataScope;
import com.fastbee.common.mybatis.mapper.BaseMapperX;
import com.fastbee.iot.domain.ThingsModelTemplate;
import com.fastbee.iot.model.vo.ThingsModelTemplateVO;

/**
 * 通用物模型Mapper接口
 *
 * @author kerwincui
 * @date 2021-12-16
 */
@Repository
public interface ThingsModelTemplateMapper extends BaseMapperX<ThingsModelTemplate>
{
    /**
     * 查询通用物模型
     *
     * @param thingsModelTemplateVO 通用物模型
     * @param language 语言
     * @return 通用物模型
     */
    @DataScope(userAlias = "m", deptAlias = "m")
    public ThingsModelTemplate selectThingsModelTemplateByTemplateId(@Param("thingsModelTemplateVO") ThingsModelTemplateVO thingsModelTemplateVO, @Param("language") String language);

    /**
     * 根据id数组查询通用物模型集合
     * @param templateIds
     * @param language 语言
     * @return
     */
    public List<ThingsModelTemplate> selectThingsModelTemplateByTemplateIds (@Param("templateIds") Long[] templateIds, @Param("language") String language);

    /**
     * 查询通用物模型列表
     *
     * @param thingsModelTemplateVO 通用物模型
     * @return 通用物模型集合
     */
    public List<ThingsModelTemplate> selectThingsModelTemplateList(@Param("thingsModelTemplateVO") ThingsModelTemplateVO thingsModelTemplateVO);

    /**
     * 查询通用物模型列表
     *
     * @param thingsModelTemplateVO 通用物模型
     * @return 通用物模型集合
     */
    public Page<ThingsModelTemplate> selectThingsModelTemplateList(Page<ThingsModelTemplate> page, @Param("thingsModelTemplateVO") ThingsModelTemplateVO thingsModelTemplateVO);

    List<ThingsModelTemplate> selectByIdentifiers(@Param("identifiers") List<String> identifiers);

}
