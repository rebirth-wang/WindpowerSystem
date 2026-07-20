package com.fastbee.iot.service.impl;

import static org.apache.commons.lang3.SystemUtils.getUserName;

import java.util.*;

import jakarta.annotation.Resource;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

import com.fastbee.common.utils.DateUtils;
import com.fastbee.common.utils.StringUtils;
import com.fastbee.iot.convert.NewsConvert;
import com.fastbee.iot.domain.News;
import com.fastbee.iot.mapper.NewsMapper;
import com.fastbee.iot.model.CategoryNews;
import com.fastbee.iot.model.vo.NewsVO;
import com.fastbee.iot.service.INewsService;

/**
 * 新闻资讯Service业务层处理
 *
 * @author kerwincui
 * @date 2022-04-09
 */
@Service
public class NewsServiceImpl extends ServiceImpl<NewsMapper,News> implements INewsService
{
    @Resource
    private NewsMapper newsMapper;

    /**
     * 查询新闻资讯
     *
     * @param newsId 新闻资讯主键
     * @return 新闻资讯
     */
    @Override
    public News selectNewsByNewsId(Long newsId)
    {
        return newsMapper.selectById(newsId);
    }

    /**
     * 查询新闻资讯列表
     *
     * @param news 新闻资讯
     * @return 新闻资讯
     */
    @Override
    public List<NewsVO> selectNewsList(News news)
    {
        LambdaQueryWrapper<News> lqw = buildQueryWrapper(news);
        List<News> newsList = baseMapper.selectList(lqw);
        return NewsConvert.INSTANCE.convertNewsVOList(newsList);
    }

    /**
     * 查询新闻资讯分页列表
     *
     * @param news 新闻资讯
     * @return 新闻资讯
     */
    @Override
    public Page<NewsVO> pageNewsVO(News news) {
        LambdaQueryWrapper<News> lqw = buildQueryWrapper(news);
        Page<News> newsPage = baseMapper.selectPage(new Page<>(news.getPageNum(), news.getPageSize()), lqw);
        return NewsConvert.INSTANCE.convertNewsVOPage(newsPage);
    }

    private LambdaQueryWrapper<News> buildQueryWrapper(News query) {
        Map<String, Object> params = query.getParams();
        LambdaQueryWrapper<News> lqw = Wrappers.lambdaQuery();
        lqw.like(StringUtils.isNotBlank(query.getTitle()), News::getTitle, query.getTitle());
        lqw.eq(StringUtils.isNotBlank(query.getContent()), News::getContent, query.getContent());
        lqw.eq(StringUtils.isNotBlank(query.getImgUrl()), News::getImgUrl, query.getImgUrl());
        lqw.eq(query.getIsTop() != null, News::getIsTop, query.getIsTop());
        lqw.eq(query.getIsBanner() != null, News::getIsBanner, query.getIsBanner());
        lqw.eq(query.getCategoryId() != null, News::getCategoryId, query.getCategoryId());
        lqw.like(StringUtils.isNotBlank(query.getCategoryName()), News::getCategoryName, query.getCategoryName());
        lqw.eq(query.getStatus() != null, News::getStatus, query.getStatus());
        lqw.eq(StringUtils.isNotBlank(query.getAuthor()), News::getAuthor, query.getAuthor());

        if (!Objects.isNull(params.get("beginTime")) &&
                !Objects.isNull(params.get("endTime"))) {
            lqw.between(News::getCreateTime, params.get("beginTime"), params.get("endTime"));
        }
        return lqw;
    }

    /**
     * 查询置顶新闻资讯列表
     *
     * @return 新闻资讯
     */
    @Override
    public List<CategoryNews> selectTopNewsList()
    {
        List<CategoryNews> categoryNewsList =new ArrayList<>();
        List<News> newsList=newsMapper.selectTopNewsList();
        for(int i=0;i<newsList.size();i++){
            boolean isAdd=false;
            for(int j=0;j<categoryNewsList.size();j++){
                if(newsList.get(i).getCategoryId().longValue()==categoryNewsList.get(j).getCategoryId().longValue()){
                    categoryNewsList.get(j).getNewsList().add(newsList.get(i));
                    isAdd=true;
                    break;
                }
            }
            if(!isAdd) {
                CategoryNews categoryNews = new CategoryNews();
                categoryNews.setCategoryId(newsList.get(i).getCategoryId());
                categoryNews.setCategoryName(newsList.get(i).getCategoryName());
                categoryNews.getNewsList().add(newsList.get(i));
                categoryNewsList.add(categoryNews);
            }
        }
        return categoryNewsList;
    }

    /**
     * 新增新闻资讯
     *
     * @param news 新闻资讯
     * @return 结果
     */
    @Override
    public int insertNews(News news)
    {
        news.setCreateTime(DateUtils.getNowDate());
        news.setCreateBy(getUserName());
        return newsMapper.insert(news);
    }

    /**
     * 修改新闻资讯
     *
     * @param news 新闻资讯
     * @return 结果
     */
    @Override
    public int updateNews(News news)
    {
        news.setUpdateTime(DateUtils.getNowDate());
        news.setUpdateBy(getUserName());
        return newsMapper.updateById(news);
    }

    /**
     * 批量删除新闻资讯
     *
     * @param newsIds 需要删除的新闻资讯主键
     * @return 结果
     */
    @Override
    public int deleteNewsByNewsIds(Long[] newsIds)
    {
        return newsMapper.deleteBatchIds(Arrays.asList(newsIds));
    }

    /**
     * 删除新闻资讯信息
     *
     * @param newsId 新闻资讯主键
     * @return 结果
     */
    @Override
    public int deleteNewsByNewsId(Long newsId)
    {
        return newsMapper.deleteById(newsId);
    }
}
