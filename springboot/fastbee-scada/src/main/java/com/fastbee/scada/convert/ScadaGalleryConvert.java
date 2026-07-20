package com.fastbee.scada.convert;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.fastbee.scada.domain.ScadaGallery;
import com.fastbee.scada.vo.ScadaGalleryVO;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import java.util.List;

/**
 * 组态图库Convert转换类
 *
 * @author zhuangpeng.li
 * @date 2024-12-04
 */
@Mapper
public interface ScadaGalleryConvert
{

    ScadaGalleryConvert INSTANCE = Mappers.getMapper(ScadaGalleryConvert.class);

    /**
     * 实体类转换为VO类
     *
     * @param scadaGallery
     * @return 组态图库集合
     */
    ScadaGalleryVO convertScadaGalleryVO(ScadaGallery scadaGallery);

    /**
     * VO类转换为实体类集合
     *
     * @param scadaGalleryVO
     * @return 组态图库集合
     */
    ScadaGallery convertScadaGallery(ScadaGalleryVO scadaGalleryVO);

    /**
     * 实体类转换为VO类集合
     *
     * @param scadaGalleryList
     * @return 组态图库集合
     */
    List<ScadaGalleryVO> convertScadaGalleryVOList(List<ScadaGallery> scadaGalleryList);

    /**
     * VO类转换为实体类
     *
     * @param scadaGalleryVOList
     * @return 组态图库集合
     */
    List<ScadaGallery> convertScadaGalleryList(List<ScadaGalleryVO> scadaGalleryVOList);

    /**
     * 实体类转换为VO类分页
     *
     * @param scadaGalleryPage
     * @return 组态图库分页
     */
    Page<ScadaGalleryVO> convertScadaGalleryVOPage(Page<ScadaGallery> scadaGalleryPage);

    /**
     * VO类转换为实体类
     *
     * @param scadaGalleryVOPage
     * @return 组态图库分页
     */
    Page<ScadaGallery> convertScadaGalleryPage(Page<ScadaGalleryVO> scadaGalleryVOPage);
}
