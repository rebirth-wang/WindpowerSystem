package com.fastbee.scada.service.impl;

import static com.fastbee.common.extend.utils.SecurityUtils.getLoginUser;
import static com.fastbee.common.extend.utils.SecurityUtils.isAdmin;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import javax.annotation.Resource;

import cn.hutool.core.util.ObjectUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

import com.fastbee.common.annotation.DataScope;
import com.fastbee.common.extend.aspectj.DataScopeAspect;
import com.fastbee.common.extend.core.domin.entity.SysUser;
import com.fastbee.common.utils.DateUtils;
import com.fastbee.common.utils.StringUtils;
import com.fastbee.scada.convert.ScadaModelConvert;
import com.fastbee.scada.domain.ScadaModel;
import com.fastbee.scada.mapper.ScadaModelMapper;
import com.fastbee.scada.service.IScadaModelService;
import com.fastbee.scada.vo.ScadaModelVO;

/**
 * 模型管理Service业务层处理
 *
 * @author kerwincui
 * @date 2023-11-10
 */
@Service
public class ScadaModelServiceImpl extends ServiceImpl<ScadaModelMapper,ScadaModel> implements IScadaModelService
{
    @Resource
    private ScadaModelMapper scadaModelMapper;

    /**
     * 查询模型管理
     *
     * @param scadaModel 模型管理
     * @return 模型管理
     */
    @Override
    @DataScope
    public ScadaModel selectScadaModelById(ScadaModel scadaModel)
    {
        LambdaQueryWrapper<ScadaModel> queryWrapper = this.buildQueryWrapper(scadaModel);
        return baseMapper.selectOne(queryWrapper);
    }

    /**
     * 查询三维配置分页列表
     *
     * @param scadaModel 三维配置
     * @return 三维配置
     */
    @Override
    @DataScope()
    public Page<ScadaModelVO> pageScadaModelVO(ScadaModel scadaModel) {
        // 多租户版本使用
//        SysUser user = getLoginUser().getUser();
//        if (ScadaConstant.ENABLE_TENANT) {
//            if (null != user.getDeptId()) {
//                scadaModel.setTenantId(user.getDept().getDeptUserId());
//            } else {
//                scadaModel.setTenantId(user.getUserId());
//            }
//        } else {
//            List<SysRole> roles=user.getRoles();
//            // 租户
//            if(roles.stream().anyMatch(a->a.getRoleKey().equals("tenant"))){
//                scadaModel.setTenantId(user.getUserId());
//            }
//        }
        LambdaQueryWrapper<ScadaModel> lqw = buildQueryWrapper(scadaModel);
        lqw.orderByDesc(ScadaModel::getCreateTime);
        if (null == scadaModel.getPageNum()) {
            scadaModel.setPageNum(1);
        }
        if (null == scadaModel.getPageSize()) {
            scadaModel.setPageSize(10);
        }
        Page<ScadaModel> scadaModelPage = baseMapper.selectPage(new Page<>(scadaModel.getPageNum(), scadaModel.getPageSize()), lqw);
        return ScadaModelConvert.INSTANCE.convertScadaModelVOPage(scadaModelPage);
    }

    /**
     * 查询三维配置列表
     *
     * @param scadaModel 三维配置
     * @return 三维配置
     */
    @Override
    public List<ScadaModelVO> listScadaModelVO(ScadaModel scadaModel) {
        LambdaQueryWrapper<ScadaModel> lqw = buildQueryWrapper(scadaModel);
        List<ScadaModel> scadaModelList = baseMapper.selectList(lqw);
        return ScadaModelConvert.INSTANCE.convertScadaModelVOList(scadaModelList);
    }

    /**
     * 新增模型管理
     *
     * @param scadaModel 模型管理
     * @return 结果
     */
    @Override
    public int insertScadaModel(ScadaModel scadaModel)
    {
        scadaModel.setCreateTime(DateUtils.getNowDate());
        // 多租户版本使用
        SysUser user = getLoginUser().getUser();
        if (null != user.getDeptId()) {
            scadaModel.setTenantId(user.getDept().getDeptUserId());
            scadaModel.setTenantName(user.getDept().getDeptName());
        } else {
            scadaModel.setTenantId(user.getUserId());
            scadaModel.setTenantName(user.getUserName());
        }
        scadaModel.setCreateBy(user.getUserName());
        scadaModel.setSysFlag(isAdmin(user.getUserId()) ? 1 : 0);
        return baseMapper.insert(scadaModel);
    }

    /**
     * 修改模型管理
     *
     * @param scadaModel 模型管理
     * @return 结果
     */
    @Override
    public int updateScadaModel(ScadaModel scadaModel)
    {
        return baseMapper.updateById(scadaModel);
    }

    /**
     * 批量删除模型管理
     *
     * @param ids 需要删除的模型管理主键
     * @return 结果
     */
    @Override
    public int deleteScadaModelByIds(Long[] ids)
    {
        return baseMapper.deleteBatchIds(Arrays.asList(ids));
    }

    /**
     * 删除模型管理信息
     *
     * @param id 模型管理主键
     * @return 结果
     */
    @Override
    public int deleteScadaModelById(Long id)
    {
        return baseMapper.deleteById(id);
    }

    private LambdaQueryWrapper<ScadaModel> buildQueryWrapper(ScadaModel query) {
        Map<String, Object> params = query.getParams();
        LambdaQueryWrapper<ScadaModel> lqw = Wrappers.lambdaQuery();
        lqw.eq(query.getId() != null, ScadaModel::getId, query.getId());
        lqw.like(StringUtils.isNotBlank(query.getModelName()), ScadaModel::getModelName, query.getModelName());
        lqw.eq(StringUtils.isNotBlank(query.getModelUrl()), ScadaModel::getModelUrl, query.getModelUrl());
        lqw.eq(query.getStatus() != null, ScadaModel::getStatus, query.getStatus());
        lqw.eq(StringUtils.isNotBlank(query.getImageUrl()), ScadaModel::getImageUrl, query.getImageUrl());
        lqw.eq(query.getTenantId() != null, ScadaModel::getTenantId, query.getTenantId());
        lqw.like(StringUtils.isNotBlank(query.getTenantName()), ScadaModel::getTenantName, query.getTenantName());
        lqw.eq(StringUtils.isNotBlank(query.getCreateBy()), ScadaModel::getCreateBy, query.getCreateBy());
        lqw.eq(query.getCreateTime() != null, ScadaModel::getCreateTime, query.getCreateTime());
        lqw.eq(StringUtils.isNotBlank(query.getUpdateBy()), ScadaModel::getUpdateBy, query.getUpdateBy());
        lqw.eq(query.getUpdateTime() != null, ScadaModel::getUpdateTime, query.getUpdateTime());
        lqw.eq(query.getDelFlag() != null, ScadaModel::getDelFlag, query.getDelFlag());
        lqw.eq(query.getSysFlag() != null, ScadaModel::getSysFlag, query.getSysFlag());

        if (!Objects.isNull(params.get("beginTime")) &&
                !Objects.isNull(params.get("endTime"))) {
            lqw.between(ScadaModel::getCreateTime, params.get("beginTime"), params.get("endTime"));
        }

        // 数据范围过滤
        if (ObjectUtil.isNotEmpty(query.getParams().get(DataScopeAspect.DATA_SCOPE))){
            lqw.and(wq -> wq.apply((String) query.getParams().get(DataScopeAspect.DATA_SCOPE)).or().eq(ScadaModel::getSysFlag, 1));
        }

        return lqw;
    }
}
