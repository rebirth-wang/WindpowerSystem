package com.fastbee.iot.convert;

import java.util.List;

import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import com.fastbee.iot.domain.Script;
import com.fastbee.iot.model.vo.ScriptVO;

/**
 * 规则引擎脚本Convert转换类
 *
 * @author zhuangpeng.li
 * @date 2024-11-14
 */
@Mapper
public interface ScriptConvert
{

    ScriptConvert INSTANCE = Mappers.getMapper(ScriptConvert.class);

    /**
     * 实体类转换为VO类
     *
     * @param script
     * @return 规则引擎脚本集合
     */
    ScriptVO convertScriptVO(Script script);

    /**
     * VO类转换为实体类集合
     *
     * @param scriptVO
     * @return 规则引擎脚本集合
     */
    Script convertScript(ScriptVO scriptVO);

    /**
     * 实体类转换为VO类集合
     *
     * @param scriptList
     * @return 规则引擎脚本集合
     */
    List<ScriptVO> convertScriptVOList(List<Script> scriptList);

    /**
     * VO类转换为实体类
     *
     * @param scriptVOList
     * @return 规则引擎脚本集合
     */
    List<Script> convertScriptList(List<ScriptVO> scriptVOList);
}
