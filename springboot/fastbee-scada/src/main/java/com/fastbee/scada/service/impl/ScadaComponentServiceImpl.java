package com.fastbee.scada.service.impl;

import static com.fastbee.common.extend.utils.SecurityUtils.getLoginUser;
import static com.fastbee.common.extend.utils.SecurityUtils.getUsername;
import static com.fastbee.common.extend.utils.SecurityUtils.isAdmin;

import java.io.IOException;
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
import org.springframework.web.multipart.MultipartFile;

import com.fastbee.common.annotation.DataScope;
import com.fastbee.common.config.RuoYiConfig;
import com.fastbee.common.exception.ServiceException;
import com.fastbee.common.exception.file.InvalidExtensionException;
import com.fastbee.common.extend.aspectj.DataScopeAspect;
import com.fastbee.common.extend.core.domin.entity.SysUser;
import com.fastbee.common.extend.core.domin.model.LoginUser;
import com.fastbee.common.utils.MessageUtils;
import com.fastbee.common.utils.StringUtils;
import com.fastbee.common.utils.file.MimeTypeUtils;
import com.fastbee.scada.convert.ScadaComponentConvert;
import com.fastbee.scada.domain.ScadaComponent;
import com.fastbee.scada.mapper.ScadaComponentMapper;
import com.fastbee.scada.service.IScadaComponentService;
import com.fastbee.scada.utils.ScadaConstant;
import com.fastbee.scada.utils.ScadaFileUploadUtils;
import com.fastbee.scada.utils.ScadaFileUtils;
import com.fastbee.scada.vo.ScadaComponentVO;

/**
 * 组件管理Service业务层处理
 *
 * @author kerwincui
 * @date 2023-11-10
 */
@Service
public class ScadaComponentServiceImpl extends ServiceImpl<ScadaComponentMapper,ScadaComponent> implements IScadaComponentService
{
    @Resource
    private ScadaComponentMapper scadaComponentMapper;

    /**
     * 查询组件管理
     *
     * @param scadaComponent 组件管理
     * @return 组件管理
     */
    @Override
    @DataScope
    public ScadaComponent selectScadaComponentById(ScadaComponent scadaComponent)
    {
        LambdaQueryWrapper<ScadaComponent> queryWrapper = this.buildQueryWrapper(scadaComponent);
        return baseMapper.selectOne(queryWrapper);
    }

    /**
     * 查询组态组件分页列表
     *
     * @param scadaComponent 组态组件
     * @return 组态组件
     */
    @Override
    @DataScope()
    public Page<ScadaComponentVO> pageScadaComponentVO(ScadaComponent scadaComponent) {
        // 多租户版本使用
//        SysUser user = getLoginUser().getUser();
//        if (ScadaConstant.ENABLE_TENANT) {
//            if (null != user.getDeptId()) {
//                scadaComponent.setTenantId(user.getDept().getDeptUserId());
//            } else {
//                scadaComponent.setTenantId(user.getUserId());
//            }
//        } else {
//            List<SysRole> roles=user.getRoles();
//            // 租户
//            if(roles.stream().anyMatch(a->a.getRoleKey().equals("tenant"))){
//                scadaComponent.setTenantId(user.getUserId());
//            }
//        }
        LambdaQueryWrapper<ScadaComponent> lqw = buildQueryWrapper(scadaComponent);
        lqw.orderByDesc(ScadaComponent::getCreateTime);
        if (null == scadaComponent.getPageNum()) {
            scadaComponent.setPageNum(1);
        }
        if (null == scadaComponent.getPageSize()) {
            scadaComponent.setPageSize(10);
        }
        Page<ScadaComponent> scadaComponentPage = baseMapper.selectPage(new Page<>(scadaComponent.getPageNum(), scadaComponent.getPageSize()), lqw);
        return ScadaComponentConvert.INSTANCE.convertScadaComponentVOPage(scadaComponentPage);
    }

    /**
     * 查询组态组件列表
     *
     * @param scadaComponent 组态组件
     * @return 组态组件
     */
    @Override
    public List<ScadaComponentVO> listScadaComponentVO(ScadaComponent scadaComponent) {
        LambdaQueryWrapper<ScadaComponent> lqw = buildQueryWrapper(scadaComponent);
        List<ScadaComponent> scadaComponentList = baseMapper.selectList(lqw);
        return ScadaComponentConvert.INSTANCE.convertScadaComponentVOList(scadaComponentList);
    }

    /**
     * 新增组件管理
     *
     * @param scadaComponent 组件管理
     * @return 结果
     */
    @Override
    public int insertScadaComponent(ScadaComponent scadaComponent)
    {
        LoginUser loginUser = getLoginUser();
        scadaComponent.setUserId(loginUser.getUserId());
        // 多租户版本使用
        SysUser user = getLoginUser().getUser();
        if (null != user.getDeptId()) {
            scadaComponent.setTenantId(user.getDept().getDeptUserId());
            scadaComponent.setTenantName(user.getDept().getDeptName());
        } else {
            scadaComponent.setTenantId(user.getUserId());
            scadaComponent.setTenantName(user.getUserName());
        }
        if (StringUtils.isEmpty(scadaComponent.getComponentTemplate())) {
            scadaComponent.setComponentTemplate(ScadaConstant.COMPONENT_TEMPLATE_DEFAULT);
        }
        if (StringUtils.isEmpty(scadaComponent.getComponentStyle())) {
            scadaComponent.setComponentStyle(ScadaConstant.COMPONENT_STYLE_DEFAULT);
        }
        if (StringUtils.isEmpty(scadaComponent.getComponentScript())) {
            scadaComponent.setComponentScript(ScadaConstant.COMPONENT_SCRIPT_DEFAULT);
        }
        scadaComponent.setCreateBy(user.getUserName());
        scadaComponent.setSysFlag(isAdmin(user.getUserId()) ? 1 : 0);
        return baseMapper.insert(scadaComponent);
    }

    /**
     * 修改组件管理
     *
     * @param scadaComponentVO 组件管理
     * @return 结果
     */
    @Override
    public int updateScadaComponent(ScadaComponentVO scadaComponentVO)
    {
        if (StringUtils.isNotEmpty(scadaComponentVO.getBase64())) {
            MultipartFile multipartFile = ScadaFileUtils.base64toMultipartFile(scadaComponentVO.getBase64());
            String url;
            try {
                url = ScadaFileUploadUtils.upload(RuoYiConfig.getUploadPath(), multipartFile, MimeTypeUtils.IMAGE_EXTENSION);
            } catch (IOException | InvalidExtensionException e) {
                throw new ServiceException(StringUtils.format(MessageUtils.message("scada.base64.change.image.exception"), e.getMessage()));
            }
            scadaComponentVO.setComponentImage(url);
        }
        ScadaComponent scadaComponent = ScadaComponentConvert.INSTANCE.convertScadaComponent(scadaComponentVO);
        scadaComponent.setUpdateBy(getUsername());
        return baseMapper.updateById(scadaComponent);
    }

    /**
     * 批量删除组件管理
     *
     * @param ids 需要删除的组件管理主键
     * @return 结果
     */
    @Override
    public int deleteScadaComponentByIds(Long[] ids)
    {
        return scadaComponentMapper.deleteBatchIds(Arrays.asList(ids));
    }

    /**
     * 删除组件管理信息
     *
     * @param id 组件管理主键
     * @return 结果
     */
    @Override
    public int deleteScadaComponentById(Long id)
    {
        return scadaComponentMapper.deleteById(id);
    }

    private LambdaQueryWrapper<ScadaComponent> buildQueryWrapper(ScadaComponent query) {
        Map<String, Object> params = query.getParams();
        LambdaQueryWrapper<ScadaComponent> lqw = Wrappers.lambdaQuery();
        lqw.eq(query.getId() != null, ScadaComponent::getId, query.getId());
        lqw.like(StringUtils.isNotBlank(query.getComponentName()), ScadaComponent::getComponentName, query.getComponentName());
        lqw.eq(StringUtils.isNotBlank(query.getComponentTemplate()), ScadaComponent::getComponentTemplate, query.getComponentTemplate());
        lqw.eq(StringUtils.isNotBlank(query.getComponentStyle()), ScadaComponent::getComponentStyle, query.getComponentStyle());
        lqw.eq(StringUtils.isNotBlank(query.getComponentScript()), ScadaComponent::getComponentScript, query.getComponentScript());
        lqw.eq(StringUtils.isNotBlank(query.getComponentImage()), ScadaComponent::getComponentImage, query.getComponentImage());
        lqw.eq(query.getTenantId() != null, ScadaComponent::getTenantId, query.getTenantId());
        lqw.like(StringUtils.isNotBlank(query.getTenantName()), ScadaComponent::getTenantName, query.getTenantName());
        lqw.eq(StringUtils.isNotBlank(query.getCreateBy()), ScadaComponent::getCreateBy, query.getCreateBy());
        lqw.eq(query.getCreateTime() != null, ScadaComponent::getCreateTime, query.getCreateTime());
        lqw.eq(StringUtils.isNotBlank(query.getUpdateBy()), ScadaComponent::getUpdateBy, query.getUpdateBy());
        lqw.eq(query.getUpdateTime() != null, ScadaComponent::getUpdateTime, query.getUpdateTime());
        lqw.eq(query.getDelFlag() != null, ScadaComponent::getDelFlag, query.getDelFlag());
        lqw.eq(query.getSysFlag() != null, ScadaComponent::getSysFlag, query.getSysFlag());
        lqw.eq(query.getUserId() != null, ScadaComponent::getUserId, query.getUserId());
        lqw.eq(query.getSysFlag() != null, ScadaComponent::getSysFlag, query.getSysFlag());

        if (!Objects.isNull(params.get("beginTime")) &&
                !Objects.isNull(params.get("endTime"))) {
            lqw.between(ScadaComponent::getCreateTime, params.get("beginTime"), params.get("endTime"));
        }

        // 数据范围过滤
        if (ObjectUtil.isNotEmpty(query.getParams().get(DataScopeAspect.DATA_SCOPE))){
            lqw.and(wq -> wq.apply((String) query.getParams().get(DataScopeAspect.DATA_SCOPE)).or().eq(ScadaComponent::getSysFlag, 1));
        }

        return lqw;
    }
}
