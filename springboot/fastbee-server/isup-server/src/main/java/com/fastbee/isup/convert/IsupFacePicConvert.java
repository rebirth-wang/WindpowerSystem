package com.fastbee.isup.convert;

import java.util.List;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import com.fastbee.isup.domain.IsupFacePic;
import com.fastbee.isup.domain.vo.IsupFacePicVO;

/**
 * 人脸库图片Convert转换类
 *
 * @author fastbee
 * @date 2026-03-05
 */
@Mapper
public interface IsupFacePicConvert
{
    /** 代码生成区域 可直接覆盖**/
    IsupFacePicConvert INSTANCE = Mappers.getMapper(IsupFacePicConvert.class);

    /**
     * 实体类转换为VO类
     *
     * @param isupFacePic
     * @return 人脸库图片集合
     */
    IsupFacePicVO convertIsupFacePicVO(IsupFacePic isupFacePic);

    /**
     * VO类转换为实体类集合
     *
     * @param isupFacePicVO
     * @return 人脸库图片集合
     */
    IsupFacePic convertIsupFacePic(IsupFacePicVO isupFacePicVO);

    /**
     * 实体类转换为VO类集合
     *
     * @param isupFacePicList
     * @return 人脸库图片集合
     */
    List<IsupFacePicVO> convertIsupFacePicVOList(List<IsupFacePic> isupFacePicList);

    /**
     * VO类转换为实体类
     *
     * @param isupFacePicVOList
     * @return 人脸库图片集合
     */
    List<IsupFacePic> convertIsupFacePicList(List<IsupFacePicVO> isupFacePicVOList);

    /**
     * 实体类转换为VO类分页
     *
     * @param isupFacePicPage
     * @return 人脸库图片分页
     */
    Page<IsupFacePicVO> convertIsupFacePicVOPage(Page<IsupFacePic> isupFacePicPage);

    /**
     * VO类转换为实体类
     *
     * @param isupFacePicVOPage
     * @return 人脸库图片分页
     */
    Page<IsupFacePic> convertIsupFacePicPage(Page<IsupFacePicVO> isupFacePicVOPage);
    /** 代码生成区域 可直接覆盖END**/

    /** 自定义代码区域 **/


    /** 自定义代码区域 END**/
}
