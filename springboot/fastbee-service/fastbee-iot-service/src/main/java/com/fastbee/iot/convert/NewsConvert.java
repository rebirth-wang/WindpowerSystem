package com.fastbee.iot.convert;

import java.util.List;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import com.fastbee.iot.domain.News;
import com.fastbee.iot.model.vo.NewsVO;

/**
 * 新闻资讯Convert转换类
 *
 * @author zhuangpeng.li
 * @date 2024-11-18
 */
@Mapper
public interface NewsConvert
{

    NewsConvert INSTANCE = Mappers.getMapper(NewsConvert.class);

    /**
     * 实体类转换为VO类
     *
     * @param news
     * @return 新闻资讯集合
     */
    NewsVO convertNewsVO(News news);

    /**
     * VO类转换为实体类集合
     *
     * @param newsVO
     * @return 新闻资讯集合
     */
    News convertNews(NewsVO newsVO);

    /**
     * 实体类转换为VO类集合
     *
     * @param newsList
     * @return 新闻资讯集合
     */
    List<NewsVO> convertNewsVOList(List<News> newsList);

    /**
     * VO类转换为实体类
     *
     * @param newsVOList
     * @return 新闻资讯集合
     */
    List<News> convertNewsList(List<NewsVO> newsVOList);

    /**
     * 实体类转换为VO类分页
     *
     * @param newsPage
     * @return 新闻资讯分页
     */
    Page<NewsVO> convertNewsVOPage(Page<News> newsPage);

    /**
     * VO类转换为实体类
     *
     * @param newsVOPage
     * @return 新闻资讯分页
     */
    Page<News> convertNewsPage(Page<NewsVO> newsVOPage);
}
