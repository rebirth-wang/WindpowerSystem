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
import com.fastbee.common.utils.DateUtils;
import com.fastbee.common.utils.MessageUtils;
import com.fastbee.common.utils.StringUtils;
import com.fastbee.common.utils.file.MimeTypeUtils;
import com.fastbee.scada.convert.ScadaEchartConvert;
import com.fastbee.scada.domain.ScadaEchart;
import com.fastbee.scada.mapper.ScadaEchartMapper;
import com.fastbee.scada.service.IScadaEchartService;
import com.fastbee.scada.utils.ScadaFileUploadUtils;
import com.fastbee.scada.utils.ScadaFileUtils;
import com.fastbee.scada.vo.ScadaEchartVO;

/**
 * 图表管理Service业务层处理
 *
 * @author kerwincui
 * @date 2023-11-10
 */
@Service
public class ScadaEchartServiceImpl extends ServiceImpl<ScadaEchartMapper,ScadaEchart> implements IScadaEchartService
{
    @Resource
    private ScadaEchartMapper scadaEchartMapper;

    /**
     * 查询图表管理
     *
     * @param scadaEchart 图表管理
     * @return 图表管理
     */
    @Override
    @DataScope
    public ScadaEchart selectScadaEchartById(ScadaEchart scadaEchart)
    {
        LambdaQueryWrapper<ScadaEchart> queryWrapper = this.buildQueryWrapper(scadaEchart);
        return baseMapper.selectOne(queryWrapper);
    }

    /**
     * 查询图管理分页列表
     *
     * @param scadaEchart 图管理
     * @return 图管理
     */
    @Override
    @DataScope()
    public Page<ScadaEchartVO> pageScadaEchartVO(ScadaEchart scadaEchart) {
        // 多租户版本使用
//        SysUser user = getLoginUser().getUser();
//        if (ScadaConstant.ENABLE_TENANT) {
//            if (null != user.getDeptId()) {
//                scadaEchart.setTenantId(user.getDept().getDeptUserId());
//            } else {
//                scadaEchart.setTenantId(user.getUserId());
//            }
//        } else {
//            List<SysRole> roles=user.getRoles();
//            // 租户
//            if(roles.stream().anyMatch(a->a.getRoleKey().equals("tenant"))){
//                scadaEchart.setTenantId(user.getUserId());
//            }
//        }
        LambdaQueryWrapper<ScadaEchart> lqw = buildQueryWrapper(scadaEchart);
        lqw.orderByDesc(ScadaEchart::getCreateTime);
        if (null == scadaEchart.getPageNum()) {
            scadaEchart.setPageNum(1);
        }
        if (null == scadaEchart.getPageSize()) {
            scadaEchart.setPageSize(10);
        }
        Page<ScadaEchart> scadaEchartPage = baseMapper.selectPage(new Page<>(scadaEchart.getPageNum(), scadaEchart.getPageSize()), lqw);
        return ScadaEchartConvert.INSTANCE.convertScadaEchartVOPage(scadaEchartPage);
    }

    /**
     * 查询图管理列表
     *
     * @param scadaEchart 图管理
     * @return 图管理
     */
    @Override
    public List<ScadaEchartVO> listScadaEchartVO(ScadaEchart scadaEchart) {
        LambdaQueryWrapper<ScadaEchart> lqw = buildQueryWrapper(scadaEchart);
        List<ScadaEchart> scadaEchartList = baseMapper.selectList(lqw);
        return ScadaEchartConvert.INSTANCE.convertScadaEchartVOList(scadaEchartList);
    }

    /**
     * 新增图表管理
     *
     * @param scadaEchartVO 图表管理
     * @return 结果
     */
    @Override
    public int insertScadaEchart(ScadaEchartVO scadaEchartVO) {
        scadaEchartVO.setCreateTime(DateUtils.getNowDate());
        // 多租户版本使用
        SysUser user = getLoginUser().getUser();
        if (null != user.getDeptId()) {
            scadaEchartVO.setTenantId(user.getDept().getDeptUserId());
            scadaEchartVO.setTenantName(user.getDept().getDeptName());
        } else {
            scadaEchartVO.setTenantId(user.getUserId());
            scadaEchartVO.setTenantName(user.getUserName());
        }
        if (StringUtils.isNotEmpty(scadaEchartVO.getBase64())) {
            MultipartFile multipartFile = ScadaFileUtils.base64toMultipartFile(scadaEchartVO.getBase64());
            String url;
            try {
                url = ScadaFileUploadUtils.upload(RuoYiConfig.getUploadPath(), multipartFile, MimeTypeUtils.IMAGE_EXTENSION);
            } catch (IOException | InvalidExtensionException e) {
                throw new ServiceException(StringUtils.format(MessageUtils.message("scada.base64.change.image.exception"), e.getMessage()));
            }
            scadaEchartVO.setEchartImgae(url);
        }
        ScadaEchart scadaEchart = ScadaEchartConvert.INSTANCE.convertScadaEchart(scadaEchartVO);
        scadaEchart.setCreateBy(user.getUserName());
        scadaEchart.setSysFlag(isAdmin(user.getUserId()) ? 1 : 0);
        return baseMapper.insert(scadaEchart);
    }

    /**
     * 修改图表管理
     *
     * @param scadaEchartVO 图表管理
     * @return 结果
     */
    @Override
    public int updateScadaEchart(ScadaEchartVO scadaEchartVO) {
        if (StringUtils.isNotEmpty(scadaEchartVO.getBase64())) {
            MultipartFile multipartFile = ScadaFileUtils.base64toMultipartFile(scadaEchartVO.getBase64());
            String url;
            try {
                url = ScadaFileUploadUtils.upload(RuoYiConfig.getUploadPath(), multipartFile, MimeTypeUtils.IMAGE_EXTENSION);
            } catch (IOException | InvalidExtensionException e) {
                throw new ServiceException(StringUtils.format(MessageUtils.message("scada.base64.change.image.exception"), e.getMessage()));
            }
            scadaEchartVO.setEchartImgae(url);
        }
        ScadaEchart scadaEchart = ScadaEchartConvert.INSTANCE.convertScadaEchart(scadaEchartVO);
        scadaEchart.setUpdateBy(getUsername());
        return baseMapper.updateById(scadaEchart);
    }

    /**
     * 批量删除图表管理
     *
     * @param ids 需要删除的图表管理主键
     * @return 结果
     */
    @Override
    public int deleteScadaEchartByIds(Long[] ids)
    {
        return scadaEchartMapper.deleteBatchIds(Arrays.asList(ids));
    }

    /**
     * 删除图表管理信息
     *
     * @param id 图表管理主键
     * @return 结果
     */
    @Override
    public int deleteScadaEchartById(Long id)
    {
        return scadaEchartMapper.deleteById(id);
    }

    private LambdaQueryWrapper<ScadaEchart> buildQueryWrapper(ScadaEchart query) {
        Map<String, Object> params = query.getParams();
        LambdaQueryWrapper<ScadaEchart> lqw = Wrappers.lambdaQuery();
        lqw.eq(query.getId() != null, ScadaEchart::getId, query.getId());
        lqw.eq(StringUtils.isNotBlank(query.getGuid()), ScadaEchart::getGuid, query.getGuid());
        lqw.like(StringUtils.isNotBlank(query.getEchartName()), ScadaEchart::getEchartName, query.getEchartName());
        lqw.eq(StringUtils.isNotBlank(query.getEchartType()), ScadaEchart::getEchartType, query.getEchartType());
        lqw.eq(StringUtils.isNotBlank(query.getEchartData()), ScadaEchart::getEchartData, query.getEchartData());
        lqw.eq(StringUtils.isNotBlank(query.getEchartImgae()), ScadaEchart::getEchartImgae, query.getEchartImgae());
        lqw.eq(query.getTenantId() != null, ScadaEchart::getTenantId, query.getTenantId());
        lqw.like(StringUtils.isNotBlank(query.getTenantName()), ScadaEchart::getTenantName, query.getTenantName());
        lqw.eq(StringUtils.isNotBlank(query.getCreateBy()), ScadaEchart::getCreateBy, query.getCreateBy());
        lqw.eq(query.getCreateTime() != null, ScadaEchart::getCreateTime, query.getCreateTime());
        lqw.eq(StringUtils.isNotBlank(query.getUpdateBy()), ScadaEchart::getUpdateBy, query.getUpdateBy());
        lqw.eq(query.getUpdateTime() != null, ScadaEchart::getUpdateTime, query.getUpdateTime());
        lqw.eq(query.getDelFlag() != null, ScadaEchart::getDelFlag, query.getDelFlag());
        lqw.eq(query.getSysFlag() != null, ScadaEchart::getSysFlag, query.getSysFlag());

        if (!Objects.isNull(params.get("beginTime")) &&
                !Objects.isNull(params.get("endTime"))) {
            lqw.between(ScadaEchart::getCreateTime, params.get("beginTime"), params.get("endTime"));
        }

        // 数据范围过滤
        if (ObjectUtil.isNotEmpty(query.getParams().get(DataScopeAspect.DATA_SCOPE))){
            lqw.and(wq -> wq.apply((String) query.getParams().get(DataScopeAspect.DATA_SCOPE)).or().eq(ScadaEchart::getSysFlag, 1));
        }

        return lqw;
    }
}
