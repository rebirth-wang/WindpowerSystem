package com.fastbee.iot.convert;

import java.util.List;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import com.fastbee.iot.domain.AppVersion;
import com.fastbee.iot.model.vo.AppVersionVO;

/**
 * APP版本Convert转换类
 *
 * @author fastbee
 * @date 2025-08-11
 */
@Mapper
public interface AppVersionConvert
{
    /** 代码生成区域 可直接覆盖**/
    AppVersionConvert INSTANCE = Mappers.getMapper(AppVersionConvert.class);

    /**
     * 实体类转换为VO类
     *
     * @param appVersion
     * @return APP版本集合
     */
    AppVersionVO convertAppVersionVO(AppVersion appVersion);

    /**
     * VO类转换为实体类集合
     *
     * @param appVersionVO
     * @return APP版本集合
     */
    AppVersion convertAppVersion(AppVersionVO appVersionVO);

    /**
     * 实体类转换为VO类集合
     *
     * @param appVersionList
     * @return APP版本集合
     */
    List<AppVersionVO> convertAppVersionVOList(List<AppVersion> appVersionList);

    /**
     * VO类转换为实体类
     *
     * @param appVersionVOList
     * @return APP版本集合
     */
    List<AppVersion> convertAppVersionList(List<AppVersionVO> appVersionVOList);

    /**
     * 实体类转换为VO类分页
     *
     * @param appVersionPage
     * @return APP版本分页
     */
    Page<AppVersionVO> convertAppVersionVOPage(Page<AppVersion> appVersionPage);

    /**
     * VO类转换为实体类
     *
     * @param appVersionVOPage
     * @return APP版本分页
     */
    Page<AppVersion> convertAppVersionPage(Page<AppVersionVO> appVersionVOPage);
    /** 代码生成区域 可直接覆盖END**/

    /** 自定义代码区域 **/


    /** 自定义代码区域 END**/
}
