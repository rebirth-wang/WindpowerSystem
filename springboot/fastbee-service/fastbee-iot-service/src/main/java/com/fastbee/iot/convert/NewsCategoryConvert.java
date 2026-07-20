package com.fastbee.iot.convert;

import java.util.List;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import com.fastbee.iot.domain.NewsCategory;
import com.fastbee.iot.model.vo.NewsCategoryVO;

/**
 * 新闻分类Convert转换类
 *
 * @author zhuangpeng.li
 * @date 2024-11-18
 */
@Mapper
public interface NewsCategoryConvert
{

    NewsCategoryConvert INSTANCE = Mappers.getMapper(NewsCategoryConvert.class);

    /**
     * 实体类转换为VO类
     *
     * @param newsCategory
     * @return 新闻分类集合
     */
    NewsCategoryVO convertNewsCategoryVO(NewsCategory newsCategory);

    /**
     * VO类转换为实体类集合
     *
     * @param newsCategoryVO
     * @return 新闻分类集合
     */
    NewsCategory convertNewsCategory(NewsCategoryVO newsCategoryVO);

    /**
     * 实体类转换为VO类集合
     *
     * @param newsCategoryList
     * @return 新闻分类集合
     */
    List<NewsCategoryVO> convertNewsCategoryVOList(List<NewsCategory> newsCategoryList);

    /**
     * VO类转换为实体类
     *
     * @param newsCategoryVOList
     * @return 新闻分类集合
     */
    List<NewsCategory> convertNewsCategoryList(List<NewsCategoryVO> newsCategoryVOList);

    /**
     * 实体类转换为VO类分页
     *
     * @param newsCategoryPage
     * @return 新闻分类分页
     */
    Page<NewsCategoryVO> convertNewsCategoryVOPage(Page<NewsCategory> newsCategoryPage);

    /**
     * VO类转换为实体类
     *
     * @param newsCategoryVOPage
     * @return 新闻分类分页
     */
    Page<NewsCategory> convertNewsCategoryPage(Page<NewsCategoryVO> newsCategoryVOPage);
}
