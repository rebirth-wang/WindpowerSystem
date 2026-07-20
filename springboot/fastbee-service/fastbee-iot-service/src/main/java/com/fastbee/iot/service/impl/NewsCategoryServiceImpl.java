package com.fastbee.iot.service.impl;

import static org.apache.commons.lang3.SystemUtils.getUserName;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import jakarta.annotation.Resource;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

import com.fastbee.common.core.domain.AjaxResult;
import com.fastbee.common.utils.DateUtils;
import com.fastbee.common.utils.MessageUtils;
import com.fastbee.common.utils.StringUtils;
import com.fastbee.iot.convert.NewsCategoryConvert;
import com.fastbee.iot.domain.NewsCategory;
import com.fastbee.iot.mapper.NewsCategoryMapper;
import com.fastbee.iot.model.IdAndName;
import com.fastbee.iot.model.vo.NewsCategoryVO;
import com.fastbee.iot.service.INewsCategoryService;

/**
 * 新闻分类Service业务层处理
 *
 * @author kerwincui
 * @date 2022-04-09
 */
@Service
public class NewsCategoryServiceImpl extends ServiceImpl<NewsCategoryMapper,NewsCategory> implements INewsCategoryService
{
    @Resource
    private NewsCategoryMapper newsCategoryMapper;

    /**
     * 查询新闻分类
     *
     * @param categoryId 新闻分类主键
     * @return 新闻分类
     */
    @Override
    public NewsCategory selectNewsCategoryByCategoryId(Long categoryId)
    {
        return newsCategoryMapper.selectById(categoryId);
    }

    /**
     * 查询新闻分类列表
     *
     * @param newsCategory 新闻分类
     * @return 新闻分类
     */
    @Override
    public List<NewsCategoryVO> selectNewsCategoryList(NewsCategory newsCategory)
    {
        LambdaQueryWrapper<NewsCategory> lqw = buildQueryWrapper(newsCategory);
        List<NewsCategory> newsCategoryList = baseMapper.selectList(lqw);
        return NewsCategoryConvert.INSTANCE.convertNewsCategoryVOList(newsCategoryList);
    }

    /**
     * 查询新闻分类分页列表
     *
     * @param newsCategory 新闻分类
     * @return 新闻分类
     */
    @Override
    public Page<NewsCategoryVO> pageNewsCategoryVO(NewsCategory newsCategory) {
        LambdaQueryWrapper<NewsCategory> lqw = buildQueryWrapper(newsCategory);
        Page<NewsCategory> newsCategoryPage = baseMapper.selectPage(new Page<>(newsCategory.getPageNum(), newsCategory.getPageSize()), lqw);
        return NewsCategoryConvert.INSTANCE.convertNewsCategoryVOPage(newsCategoryPage);
    }

    private LambdaQueryWrapper<NewsCategory> buildQueryWrapper(NewsCategory query) {
        Map<String, Object> params = query.getParams();
        LambdaQueryWrapper<NewsCategory> lqw = Wrappers.lambdaQuery();
        lqw.like(StringUtils.isNotBlank(query.getCategoryName()), NewsCategory::getCategoryName, query.getCategoryName());
        lqw.eq(query.getOrderNum() != null, NewsCategory::getOrderNum, query.getOrderNum());

        if (!Objects.isNull(params.get("beginTime")) &&
                !Objects.isNull(params.get("endTime"))) {
            lqw.between(NewsCategory::getCreateTime, params.get("beginTime"), params.get("endTime"));
        }
        return lqw;
    }

    /**
     * 查询新闻分类简短列表
     *
     * @return 新闻分类
     */
    @Override
    public List<IdAndName> selectNewsCategoryShortList()
    {
        return newsCategoryMapper.selectNewsCategoryShortList();
    }

    /**
     * 新增新闻分类
     *
     * @param newsCategory 新闻分类
     * @return 结果
     */
    @Override
    public int insertNewsCategory(NewsCategory newsCategory)
    {
        newsCategory.setCreateTime(DateUtils.getNowDate());
        newsCategory.setCreateBy(getUserName());
        return newsCategoryMapper.insert(newsCategory);
    }

    /**
     * 修改新闻分类
     *
     * @param newsCategory 新闻分类
     * @return 结果
     */
    @Override
    public int updateNewsCategory(NewsCategory newsCategory)
    {
        newsCategory.setUpdateTime(DateUtils.getNowDate());
        newsCategory.setUpdateBy(getUserName());
        return newsCategoryMapper.updateById(newsCategory);
    }

    /**
     * 批量删除新闻分类
     *
     * @param categoryIds 需要删除的新闻分类主键
     * @return 结果
     */
    @Override
    public AjaxResult deleteNewsCategoryByCategoryIds(Long[] categoryIds)
    {
        int productCount=newsCategoryMapper.newsCountInCategorys(categoryIds);
        if(productCount>0){
            return AjaxResult.error(MessageUtils.message("newsCategory.delete.fail.please.delete.category.info"));
        }
        if(newsCategoryMapper.deleteBatchIds(Arrays.asList(categoryIds))>0){
            return AjaxResult.success(MessageUtils.message("delete.success"));
        }
        return AjaxResult.error(MessageUtils.message("delete.fail"));
    }

    /**
     * 删除新闻分类信息
     *
     * @param categoryId 新闻分类主键
     * @return 结果
     */
    @Override
    public int deleteNewsCategoryByCategoryId(Long categoryId)
    {
        return newsCategoryMapper.deleteById(categoryId);
    }
}
