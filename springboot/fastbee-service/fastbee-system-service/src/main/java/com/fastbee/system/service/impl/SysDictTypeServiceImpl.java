package com.fastbee.system.service.impl;

import static com.fastbee.common.constant.Constants.EN_US;
import static com.fastbee.common.constant.Constants.ZH_CN;

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

import jakarta.annotation.PostConstruct;
import jakarta.annotation.Resource;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.fastbee.common.constant.UserConstants;
import com.fastbee.common.exception.ServiceException;
import com.fastbee.common.extend.core.domin.entity.SysDictData;
import com.fastbee.common.extend.utils.DictUtils;
import com.fastbee.common.extend.utils.SecurityUtils;
import com.fastbee.common.utils.DateUtils;
import com.fastbee.common.utils.StringUtils;
import com.fastbee.system.domain.SysDictType;
import com.fastbee.system.domain.SysDictTypeTranslate;
import com.fastbee.system.domain.vo.SysDictTypeTranslateVO;
import com.fastbee.system.domain.vo.SysDictTypeVO;
import com.fastbee.system.mapper.SysDictDataMapper;
import com.fastbee.system.mapper.SysDictTypeMapper;
import com.fastbee.system.service.ISysDictTypeService;
import com.fastbee.system.service.ISysDictTypeTranslateService;

/**
 * 字典 业务层处理
 *
 * @author ruoyi
 */
@Service
public class SysDictTypeServiceImpl extends ServiceImpl<SysDictTypeMapper, SysDictType> implements ISysDictTypeService
{
    @Resource
    private SysDictTypeMapper dictTypeMapper;

    @Resource
    private ISysDictTypeTranslateService dictTypeTranslateService;

    @Resource
    private SysDictDataMapper dictDataMapper;

    /**
     * 项目启动时，初始化字典到缓存
     */
    @PostConstruct
    public void init()
    {
        loadingDictCache();
    }

    /**
     * 根据条件分页查询字典类型
     *
     * @param dictType 字典类型信息
     * @return 字典类型集合信息
     */
    @Override
    public List<SysDictType> selectDictTypeList(SysDictType dictType)
    {
        dictType.setLanguage(SecurityUtils.getLanguage());
        LambdaQueryWrapper<SysDictType> lqw = buildQueryWrapper(dictType);
        List<SysDictType> sysDictTypeList = baseMapper.selectList(lqw);
        return sysDictTypeList;
    }

    /**
     * 查询字典类型分页列表
     *
     * @param sysDictType 字典类型
     * @return 字典类型
     */
    @Override
    public Page<SysDictTypeVO> pageSysDictTypeVO(SysDictTypeVO sysDictType) {
        sysDictType.setLanguage(SecurityUtils.getLanguage());
        Page<SysDictTypeVO> sysDictTypeVOPage = dictTypeMapper.selectSysDictTypeVOPage(new Page<>(sysDictType.getPageNum(), sysDictType.getPageSize()), sysDictType);
        List<SysDictTypeVO> sysDictTypeList = sysDictTypeVOPage.getRecords();
        SysDictTypeTranslate typeTranslate = new SysDictTypeTranslate();
        typeTranslate.setId(sysDictType.getDictId());
        typeTranslate.setZhCn(sysDictType.getDictName());
        typeTranslate.setEnUs(sysDictType.getDictName_en_US());
        List<SysDictTypeTranslateVO> translateVOList = dictTypeTranslateService.listSysDictTypeTranslateVO(typeTranslate);
        for (SysDictTypeVO sysDictTypeVO : sysDictTypeList) {
            for (SysDictTypeTranslateVO sysDictTypeTranslateVO : translateVOList) {
                if (sysDictTypeVO.getDictId() == sysDictTypeTranslateVO.getId()) {
                    sysDictTypeVO.setDictName_en_US(sysDictTypeTranslateVO.getEnUs());
                }
            }
        }
        return sysDictTypeVOPage;
    }

    private LambdaQueryWrapper<SysDictType> buildQueryWrapper(SysDictType query) {
        Map<String, Object> params = query.getParams();
        LambdaQueryWrapper<SysDictType> lqw = Wrappers.lambdaQuery();
        lqw.eq(query.getDictId() != null, SysDictType::getDictId, query.getDictId());
        lqw.like(StringUtils.isNotBlank(query.getDictName()), SysDictType::getDictName, query.getDictName());
        lqw.like(StringUtils.isNotBlank(query.getDictType()), SysDictType::getDictType, query.getDictType());
        lqw.eq(query.getStatus() != null, SysDictType::getStatus, query.getStatus());
        lqw.eq(StringUtils.isNotBlank(query.getCreateBy()), SysDictType::getCreateBy, query.getCreateBy());
        lqw.eq(query.getCreateTime() != null, SysDictType::getCreateTime, query.getCreateTime());
        lqw.eq(StringUtils.isNotBlank(query.getUpdateBy()), SysDictType::getUpdateBy, query.getUpdateBy());
        lqw.eq(query.getUpdateTime() != null, SysDictType::getUpdateTime, query.getUpdateTime());
        lqw.eq(StringUtils.isNotBlank(query.getRemark()), SysDictType::getRemark, query.getRemark());

        if (!Objects.isNull(params.get("beginTime")) &&
                !Objects.isNull(params.get("endTime"))) {
            lqw.between(SysDictType::getCreateTime, params.get("beginTime"), params.get("endTime"));
        }
        return lqw;
    }

    /**
     * 根据所有字典类型
     *
     * @return 字典类型集合信息
     */
    @Override
    public List<SysDictType> selectDictTypeAll()
    {
        return dictTypeMapper.selectDictTypeAll(SecurityUtils.getLanguage());
    }

    /**
     * 根据字典类型查询字典数据
     *
     * @param dictType 字典类型
     * @return 字典数据集合信息
     */
    @Override
    public List<SysDictData> selectDictDataByType(String dictType)
    {
        List<SysDictData> dictDatas = DictUtils.getDictCache(dictType);
        if (StringUtils.isNotEmpty(dictDatas))
        {
            convertDictLabel(dictDatas, SecurityUtils.getLanguage());
            return dictDatas;
        }
        dictDatas = dictDataMapper.selectDictDataByType(dictType);
        if (StringUtils.isNotEmpty(dictDatas))
        {
            DictUtils.setDictCache(dictType, dictDatas);
            convertDictLabel(dictDatas, SecurityUtils.getLanguage());
            return dictDatas;
        }
        return null;
    }

    /**
     * 根据字典类型ID查询信息
     *
     * @param dictId 字典类型ID
     * @return 字典类型
     */
    @Override
    public SysDictType selectDictTypeById(Long dictId)
    {
        SysDictType sysDictType = dictTypeMapper.selectDictTypeById(dictId, SecurityUtils.getLanguage());
        SysDictTypeTranslate typeTranslate = dictTypeTranslateService.selectSysDictTypeTranslateById(sysDictType.getDictId());
        sysDictType.setDictName_en_US(typeTranslate.getEnUs());
        return sysDictType;
    }

    /**
     * 根据字典类型查询信息
     *
     * @param dictType 字典类型
     * @return 字典类型
     */
    @Override
    public SysDictType selectDictTypeByType(String dictType)
    {
        return dictTypeMapper.selectDictTypeByType(dictType, SecurityUtils.getLanguage());
    }

    /**
     * 批量删除字典类型信息
     *
     * @param dictIds 需要删除的字典ID
     */
    @Override
    public void deleteDictTypeByIds(Long[] dictIds)
    {
        for (Long dictId : dictIds)
        {
            SysDictType dictType = selectDictTypeById(dictId);
            if (dictDataMapper.countDictDataByType(dictType.getDictType()) > 0)
            {
                throw new ServiceException(String.format("%1$s已分配,不能删除", dictType.getDictName()));
            }
            dictTypeMapper.deleteById(dictId);
            DictUtils.removeDictCache(dictType.getDictType());
        }
        dictTypeTranslateService.deleteWithCacheByIds(dictIds, true);
    }

    /**
     * 加载字典缓存数据
     *
     */
    @Override
    public void loadingDictCache()
    {
        SysDictData dictData = new SysDictData();
        dictData.setStatus(0);
        Map<String, List<SysDictData>> dictDataMap = dictDataMapper.selectDictDataListAll(dictData).stream().collect(Collectors.groupingBy(SysDictData::getDictType));
        for (Map.Entry<String, List<SysDictData>> entry : dictDataMap.entrySet())
        {
            DictUtils.setDictCache(entry.getKey(), entry.getValue().stream().sorted(Comparator.comparing(SysDictData::getDictSort)).collect(Collectors.toList()));
        }
    }

    /**
     * 清空字典缓存数据
     */
    @Override
    public void clearDictCache()
    {
        DictUtils.clearDictCache();
    }

    /**
     * 重置字典缓存数据
     *
     */
    @Override
    public void resetDictCache()
    {
        clearDictCache();
        loadingDictCache();
    }

    /**
     * 新增保存字典类型信息
     *
     * @param dict 字典类型信息
     * @return 结果
     */
    @Override
    public int insertDictType(SysDictType dict)
    {
        dict.setCreateTime(DateUtils.getNowDate());
        int row = dictTypeMapper.insert(dict);
        if (row > 0)
        {
            SysDictTypeTranslate typeTranslate = new SysDictTypeTranslate();
            typeTranslate.setId(dict.getDictId());
            typeTranslate.setZhCn(dict.getDictName());
            typeTranslate.setEnUs(dict.getDictName_en_US());
            dictTypeTranslateService.insertWithCache(typeTranslate);
            DictUtils.setDictCache(dict.getDictType(), null);
        }
        return row;
    }

    /**
     * 修改保存字典类型信息
     *
     * @param dict 字典类型信息
     * @return 结果
     */
    @Override
    @Transactional
    public int updateDictType(SysDictType dict)
    {
        SysDictType oldDict = dictTypeMapper.selectDictTypeById(dict.getDictId(), SecurityUtils.getLanguage());
        dictDataMapper.updateDictDataType(oldDict.getDictType(), dict.getDictType());
        dict.setUpdateTime(DateUtils.getNowDate());
        int row = dictTypeMapper.updateById(dict);
        if (row > 0)
        {
            SysDictTypeTranslate typeTranslate = new SysDictTypeTranslate();
            typeTranslate.setId(dict.getDictId());
            typeTranslate.setZhCn(dict.getDictName());
            typeTranslate.setEnUs(dict.getDictName_en_US());
            dictTypeTranslateService.updateWithCache(typeTranslate);
            List<SysDictData> dictDatas = dictDataMapper.selectDictDataByType(dict.getDictType());
            DictUtils.setDictCache(dict.getDictType(), dictDatas);
        }
        return row;
    }

    /**
     * 校验字典类型称是否唯一
     *
     * @param dict 字典类型
     * @return 结果
     */
    @Override
    public String checkDictTypeUnique(SysDictType dict)
    {
        Long dictId = StringUtils.isNull(dict.getDictId()) ? -1L : dict.getDictId();
        List<SysDictType> dictType = dictTypeMapper.checkDictTypeUnique(dict.getDictType(), SecurityUtils.getLanguage());
        if (!dictType.isEmpty() && dictType.get(0).getDictId().longValue() != dictId.longValue())
        {
            return UserConstants.NOT_UNIQUE;
        }
        return UserConstants.UNIQUE;
    }

    /**
     * 多语言转换
     * @param list
     * @param language
     * @return
     */
    public static void convertDictLabel(List<SysDictData> list, String language) {
        switch (language) {
            case EN_US:
                for (SysDictData data : list) {
                    data.setDictLabel(data.getDictLabel_en_US());
                }
                break;
            case ZH_CN:
                for (SysDictData data : list) {
                    data.setDictLabel(data.getDictLabel_zh_CN());
                }
                break;
        }
    }
}
