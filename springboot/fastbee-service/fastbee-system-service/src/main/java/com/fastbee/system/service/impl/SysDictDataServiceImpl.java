package com.fastbee.system.service.impl;

import java.util.List;
import java.util.Map;
import java.util.Objects;

import jakarta.annotation.Resource;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

import com.fastbee.common.extend.core.domin.entity.SysDictData;
import com.fastbee.common.extend.utils.DictUtils;
import com.fastbee.common.extend.utils.SecurityUtils;
import com.fastbee.common.utils.DateUtils;
import com.fastbee.common.utils.StringUtils;
import com.fastbee.system.domain.SysDictDataTranslate;
import com.fastbee.system.domain.vo.SysDictDataVO;
import com.fastbee.system.mapper.SysDictDataMapper;
import com.fastbee.system.service.ISysDictDataService;
import com.fastbee.system.service.ISysDictDataTranslateService;

/**
 * 字典 业务层处理
 *
 * @author ruoyi
 */
@Service
public class SysDictDataServiceImpl extends ServiceImpl<SysDictDataMapper, SysDictData> implements ISysDictDataService
{
    @Resource
    private SysDictDataMapper dictDataMapper;

    @Resource
    private ISysDictDataTranslateService translateService;

    /**
     * 根据条件分页查询字典数据
     *
     * @param dictData 字典数据信息
     * @return 字典数据集合信息
     */
    @Override
    public List<SysDictData> selectDictDataList(SysDictData dictData)
    {
        dictData.setLanguage(SecurityUtils.getLanguage());
        return dictDataMapper.selectDictDataList(dictData);
    }

    /**
     * 查询字典数据分页列表
     *
     * @param sysDictData 字典数据
     * @return 字典数据
     */
    @Override
    public Page<SysDictDataVO> pageSysDictDataVO(SysDictDataVO sysDictData) {
        sysDictData.setLanguage(SecurityUtils.getLanguage());
        Page<SysDictDataVO> sysDictDataVOPage = dictDataMapper.selectSysDictDataVoPage(new Page<>(sysDictData.getPageNum(), sysDictData.getPageSize()), sysDictData);
        List<SysDictDataVO> dataVOList = sysDictDataVOPage.getRecords();
        SysDictDataTranslate dictDataTranslate = new SysDictDataTranslate();
        dictDataTranslate.setId(sysDictData.getDictCode());
        dictDataTranslate.setZhCn(sysDictData.getDictLabel());
        dictDataTranslate.setEnUs(sysDictData.getDictLabel_en_US());
        List<SysDictDataTranslate> translateList = translateService.listSysDictDataTranslateVO(dictDataTranslate);
        for (SysDictDataVO sysDictDataVO : dataVOList) {
            for (SysDictDataTranslate translate : translateList) {
                if (Objects.equals(sysDictDataVO.getDictCode(), translate.getId())) {
                    sysDictDataVO.setDictLabel_en_US(translate.getEnUs());
                }
            }
        }
        return sysDictDataVOPage;
    }

    private LambdaQueryWrapper<SysDictData> buildQueryWrapper(SysDictData query) {
        Map<String, Object> params = query.getParams();
        LambdaQueryWrapper<SysDictData> lqw = Wrappers.lambdaQuery();
        lqw.eq(query.getDictCode() != null, SysDictData::getDictCode, query.getDictCode());
        lqw.eq(query.getDictSort() != null, SysDictData::getDictSort, query.getDictSort());
        lqw.like(StringUtils.isNotBlank(query.getDictLabel()), SysDictData::getDictLabel, query.getDictLabel());
        lqw.eq(StringUtils.isNotBlank(query.getDictValue()), SysDictData::getDictValue, query.getDictValue());
        lqw.eq(StringUtils.isNotBlank(query.getDictType()), SysDictData::getDictType, query.getDictType());
        lqw.eq(StringUtils.isNotBlank(query.getCssClass()), SysDictData::getCssClass, query.getCssClass());
        lqw.eq(StringUtils.isNotBlank(query.getListClass()), SysDictData::getListClass, query.getListClass());
        lqw.eq(StringUtils.isNotBlank(query.getIsDefault()), SysDictData::getIsDefault, query.getIsDefault());
        lqw.eq(query.getStatus() != null, SysDictData::getStatus, query.getStatus());
        lqw.eq(StringUtils.isNotBlank(query.getCreateBy()), SysDictData::getCreateBy, query.getCreateBy());
        lqw.eq(query.getCreateTime() != null, SysDictData::getCreateTime, query.getCreateTime());
        lqw.eq(StringUtils.isNotBlank(query.getUpdateBy()), SysDictData::getUpdateBy, query.getUpdateBy());
        lqw.eq(query.getUpdateTime() != null, SysDictData::getUpdateTime, query.getUpdateTime());
        lqw.eq(StringUtils.isNotBlank(query.getRemark()), SysDictData::getRemark, query.getRemark());

        if (!Objects.isNull(params.get("beginTime")) &&
                !Objects.isNull(params.get("endTime"))) {
            lqw.between(SysDictData::getCreateTime, params.get("beginTime"), params.get("endTime"));
        }
        return lqw;
    }

    /**
     * 根据字典类型和字典键值查询字典数据信息
     *
     * @param dictType 字典类型
     * @param dictValue 字典键值
     * @return 字典标签
     */
    @Override
    public String selectDictLabel(String dictType, String dictValue)
    {
        SysDictData dictData = new SysDictData();
        dictData.setDictType(dictType);
        dictData.setDictValue(dictValue);
        LambdaQueryWrapper<SysDictData> lqw = buildQueryWrapper(dictData);
        return dictDataMapper.selectOne(lqw).getDictLabel();
    }

    /**
     * 根据字典数据ID查询信息
     *
     * @param dictCode 字典数据ID
     * @return 字典数据
     */
    @Override
    public SysDictData selectDictDataById(Long dictCode)
    {
        SysDictData dictData = dictDataMapper.selectDictDataById(dictCode, SecurityUtils.getLanguage());
        SysDictDataTranslate dictDataTranslate = translateService.selectSysDictDataTranslateById(dictCode);
        dictData.setDictLabel_en_US(dictDataTranslate.getEnUs());
        return dictData;
    }

    /**
     * 批量删除字典数据信息
     *
     * @param dictCodes 需要删除的字典数据ID
     */
    @Override
    public void deleteDictDataByIds(Long[] dictCodes)
    {
        for (Long dictCode : dictCodes)
        {
            SysDictData data = selectDictDataById(dictCode);
            dictDataMapper.deleteById(dictCode);
            List<SysDictData> dictDatas = dictDataMapper.selectDictDataByType(data.getDictType());
            DictUtils.setDictCache(data.getDictType(), dictDatas);
        }
        translateService.deleteWithCacheByIds(dictCodes);
    }

    /**
     * 新增保存字典数据信息
     *
     * @param data 字典数据信息
     * @return 结果
     */
    @Override
    public int insertDictData(SysDictData data)
    {
        data.setCreateTime(DateUtils.getNowDate());
        int row = dictDataMapper.insert(data);
        SysDictDataTranslate translate = new SysDictDataTranslate();
        if (row > 0)
        {
            //同步翻译表
            translate.setId(data.getDictCode());
            translate.setZhCn(data.getDictLabel());
            translate.setEnUs(data.getDictLabel_en_US());
            translateService.insertWithCache(translate);
            List<SysDictData> dictDatas = dictDataMapper.selectDictDataByType(data.getDictType());
            DictUtils.setDictCache(data.getDictType(), dictDatas);
        }
        return row;
    }

    /**
     * 修改保存字典数据信息
     *
     * @param data 字典数据信息
     * @return 结果
     */
    @Override
    public int updateDictData(SysDictData data)
    {
        data.setUpdateTime(DateUtils.getNowDate());
        int row = dictDataMapper.updateById(data);
        SysDictDataTranslate translate = new SysDictDataTranslate();
        if (row > 0)
        {
            translate.setId(data.getDictCode());
            translate.setZhCn(data.getDictLabel());
            translate.setEnUs(data.getDictLabel_en_US());
            translateService.updateWithCache(translate);
            List<SysDictData> dictDatas = dictDataMapper.selectDictDataByType(data.getDictType());
            DictUtils.setDictCache(data.getDictType(), dictDatas);
        }
        return row;
    }

    @Override
    public List<SysDictData> selectDictDataListByDictTypes(List<String> dictTypeList) {
        return dictDataMapper.selectDictDataListByDictTypes(dictTypeList, SecurityUtils.getLanguage());
    }

    @Override
    public SysDictData selectByDictTypeAndDictValue(String dictType, String dictValue) {
        return dictDataMapper.selectByDictTypeAndDictValue(dictType, dictValue, SecurityUtils.getLanguage());
    }

    @Override
    public List<SysDictData> listByDictTypeAndDictValue(String dictType, List<String> dictValueList) {
        return dictDataMapper.listByDictTypeAndDictValue(dictType, dictValueList, SecurityUtils.getLanguage());
    }
}
