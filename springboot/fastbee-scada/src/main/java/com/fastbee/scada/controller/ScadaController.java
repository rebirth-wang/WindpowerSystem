package com.fastbee.scada.controller;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import javax.annotation.Resource;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import com.fastbee.common.annotation.Log;
import com.fastbee.common.config.RuoYiConfig;
import com.fastbee.common.core.domain.AjaxResult;
import com.fastbee.common.core.page.TableDataInfo;
import com.fastbee.common.enums.BusinessType;
import com.fastbee.common.exception.ServiceException;
import com.fastbee.common.exception.file.InvalidExtensionException;
import com.fastbee.common.extend.core.controller.BaseController;
import com.fastbee.common.extend.core.domin.entity.SysUser;
import com.fastbee.common.extend.utils.SecurityUtils;
import com.fastbee.common.extend.utils.poi.ExcelUtil;
import com.fastbee.common.utils.MessageUtils;
import com.fastbee.common.utils.StringUtils;
import com.fastbee.common.utils.file.MimeTypeUtils;
import com.fastbee.iot.model.vo.AlertLogVO;
import com.fastbee.scada.domain.Scada;
import com.fastbee.scada.domain.ScadaDeviceBind;
import com.fastbee.scada.domain.ScadaGallery;
import com.fastbee.scada.enums.ScadaTypeEnum;
import com.fastbee.scada.service.IScadaDeviceBindService;
import com.fastbee.scada.service.IScadaService;
import com.fastbee.scada.utils.ScadaFileUploadUtils;
import com.fastbee.scada.utils.ScadaFileUtils;
import com.fastbee.scada.vo.*;

/**
 * 组态中心Controller
 *
 * @author kerwincui
 * @date 2023-11-10
 */
@Api(tags = "组态中心")
@RestController
@RequestMapping("/scada/center")
public class ScadaController extends BaseController
{
    @Resource
    private IScadaService scadaService;

    @Resource
    private IScadaDeviceBindService scadaDeviceBindService;

    /**
     * 查询组态中心列表
     */
    @ApiOperation("查询组态中心列表")
    @PreAuthorize("@ss.hasPermi('scada:center:list')")
    @GetMapping("/list")
    public TableDataInfo list(Scada scada)
    {
        Page<ScadaVO> voPage = scadaService.pageScadaVO(scada);
        return getDataTable(voPage.getRecords(), voPage.getTotal());
    }

    /**
     * 导出组态中心列表
     */
    @ApiOperation("导出组态中心列表")
    @PreAuthorize("@ss.hasPermi('scada:center:export')")
    @Log(title = "组态中心", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(HttpServletResponse response, Scada scada)
    {
        Page<ScadaVO> voPage = scadaService.pageScadaVO(scada);
        ExcelUtil<ScadaVO> util = new ExcelUtil<ScadaVO>(ScadaVO.class);
        util.exportExcel(response, voPage.getRecords(), "组态页面数据");
    }

    /**
     * 获取组态中心详细信息
     */
    @ApiOperation("查询组态详细信息")
    @PreAuthorize("@ss.hasPermi('scada:center:query')")
    @GetMapping(value = "/{id}")
    public AjaxResult getInfo(@PathVariable("id") Long id)
    {
        Scada scada = new Scada();
        scada.setId(id);
        return success(scadaService.selectScadaById(scada));
    }

    /**
     * 新增组态中心
     */
    @ApiOperation("新增组态中心")
    @PreAuthorize("@ss.hasPermi('scada:center:add')")
    @Log(title = "组态中心", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody ScadaVO scadaVO)
    {
        if (null == scadaVO.getType()) {
            scadaVO.setType(ScadaTypeEnum.PUBLIC.getType());
        }
        assert !ScadaTypeEnum.PRODUCT_TEMPLATE.getType().equals(scadaVO.getType()) || !Objects.isNull(scadaVO.getProductId()) : MessageUtils.message("scada.product.id.is.null");
        assert !ScadaTypeEnum.SCENE_MODEL.getType().equals(scadaVO.getType()) || !Objects.isNull(scadaVO.getSceneModelId()) : MessageUtils.message("scada.scene.id.is.null");
        return scadaService.insertScada(scadaVO);
    }

    /**
     * 修改组态中心
     */
    @ApiOperation("修改组态中心")
    @PreAuthorize("@ss.hasPermi('scada:center:edit')")
    @Log(title = "组态中心", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody ScadaVO scadaVO)
    {
        SecurityUtils.checkUserOperatePermission(scadaVO.getTenantId(), scadaVO.getCreateBy());
        return toAjax(scadaService.updateScada(scadaVO));
    }

    /**
     * 删除组态中心
     */
    @ApiOperation("批量删除组态中心")
    @PreAuthorize("@ss.hasPermi('scada:center:remove')")
    @Log(title = "组态中心", businessType = BusinessType.DELETE)
    @DeleteMapping("/{ids}")
    public AjaxResult remove(@PathVariable Long[] ids)
    {
        LambdaQueryWrapper<Scada> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.select(Scada::getId, Scada::getTenantId, Scada::getCreateBy);
        queryWrapper.in(Scada::getId, Arrays.asList(ids));
        List<Scada> scadaList = scadaService.list(queryWrapper);
        for (Scada scada : scadaList) {
            SecurityUtils.checkUserOperatePermission(scada.getTenantId(), scada.getCreateBy());
        }
        return toAjax(scadaService.deleteScadaByIds(ids));
    }

    /**
     * 根据guid获取组态详情
     * @param scadaDateQueryVO 组态查询参数
     * @return
     */
    @ApiOperation("根据guid获取组态详情")
    @PreAuthorize("@ss.hasPermi('scada:center:query')")
    @GetMapping(value = "/getByGuid")
    public AjaxResult getByGuid(ScadaDateQueryVO scadaDateQueryVO) {
        ScadaVO scadaVO = scadaService.selectScadaByGuid(scadaDateQueryVO);
        return AjaxResult.success(scadaVO);
    }

    /**
     * 保存组态信息
     */
    @ApiOperation("保存组态信息")
    @Log(title = "组态中心", businessType = BusinessType.INSERT)
    @PreAuthorize("@ss.hasPermi('scada:center:edit')")
    @PostMapping("/save")
    public AjaxResult save(@RequestBody ScadaVO scadaVO)
    {
        if (StringUtils.isEmpty(scadaVO.getGuid())) {
            return AjaxResult.error(MessageUtils.message("scada.guid.cannot.empty"));
        }
        if (null == scadaVO.getType()) {
            scadaVO.setType(ScadaTypeEnum.PUBLIC.getType());
        }
        Scada scadaQuery = new Scada();
        scadaQuery.setGuid(scadaVO.getGuid());
        List<ScadaVO> scadaVOList = scadaService.listScadaVO(scadaQuery);
        if (StringUtils.isNotEmpty(scadaVO.getBase64())) {
            MultipartFile multipartFile = ScadaFileUtils.base64toMultipartFile(scadaVO.getBase64());
            String url;
            try {
                url = ScadaFileUploadUtils.upload(RuoYiConfig.getUploadPath(), multipartFile, MimeTypeUtils.IMAGE_EXTENSION);
            } catch (IOException | InvalidExtensionException e) {
                throw new ServiceException(StringUtils.format(MessageUtils.message("scada.base64.change.image.exception"), e.getMessage()));
            }
            scadaVO.setPageImage(url);
        }
        if (CollectionUtils.isNotEmpty(scadaVOList)) {
            ScadaVO updateScadaVO = scadaVOList.get(0);
            SecurityUtils.checkUserOperatePermission(updateScadaVO.getTenantId(), updateScadaVO.getCreateBy());
            updateScadaVO.setScadaData(scadaVO.getScadaData());
            updateScadaVO.setPageImage(scadaVO.getPageImage());
            scadaService.updateScada(updateScadaVO);
        } else {
            // 多租户版本使用
            SysUser user = getLoginUser().getUser();
            if (null != user.getDeptId()) {
                scadaVO.setTenantId(user.getDept().getDeptUserId());
                scadaVO.setTenantName(user.getDept().getDeptUserName());
            } else {
                scadaVO.setTenantId(user.getUserId());
                scadaVO.setTenantName(user.getUserName());
            }
            scadaService.insertScada(scadaVO);
        }
        return AjaxResult.success();
    }

    /**
     * 获取组态绑定的设备数，一个组态可以绑定多个设备，用于多个设备的参数绑定
     * @param scadaDeviceBindVO 组态guid
     * @return
     */
    @ApiOperation("获取组态绑定的设备列表")
    @PreAuthorize("@ss.hasPermi('scada:center:query')")
    @GetMapping(value = "/listDeviceBind")
    public TableDataInfo listDeviceBind(ScadaDeviceBindVO scadaDeviceBindVO) {
        if (StringUtils.isNotEmpty(scadaDeviceBindVO.getScadaGuid())) {
            LambdaQueryWrapper<Scada> queryWrapper = new LambdaQueryWrapper<>();
            queryWrapper.select(Scada::getId, Scada::getTenantId, Scada::getCreateBy);
            queryWrapper.eq(Scada::getGuid, scadaDeviceBindVO.getScadaGuid());
            Scada scada = scadaService.getOne(queryWrapper);
            SecurityUtils.checkUserOperatePermission(scada.getTenantId(), scada.getCreateBy());
        }
        Page<ScadaDeviceBindVO> voPage = scadaService.listDeviceBind(scadaDeviceBindVO);
        return getDataTable(voPage.getRecords(), voPage.getTotal());
    }

    /**
     * 保存组态关联设备
     * @param scadaDeviceBindDTO 组态关联设备
     * @return
     */
    @ApiOperation("保存组态关联设备")
    @PreAuthorize("@ss.hasPermi('scada:center:edit')")
    @PostMapping("/saveDeviceBind")
    public AjaxResult saveDeviceBind(@RequestBody ScadaDeviceBindDTO scadaDeviceBindDTO)
    {
        if (StringUtils.isEmpty(scadaDeviceBindDTO.getScadaGuid()) || StringUtils.isEmpty(scadaDeviceBindDTO.getSerialNumbers())) {
            return error(MessageUtils.message("scada.please.select.device"));
        }
        List<String> addSerialNumberList = StringUtils.str2List(scadaDeviceBindDTO.getSerialNumbers(), ",", true, true);
        List<ScadaDeviceBind> scadaDeviceBindList = scadaDeviceBindService.listByGuidAndSerialNumber(scadaDeviceBindDTO.getScadaGuid(), addSerialNumberList);
        List<String> oldSerialNumberList = scadaDeviceBindList.stream().map(ScadaDeviceBind::getSerialNumber).collect(Collectors.toList());
        for (String serialNumber : addSerialNumberList) {
            if (oldSerialNumberList.contains(serialNumber)) {
                continue;
            }
            ScadaDeviceBind scadaDeviceBind = new ScadaDeviceBind();
            scadaDeviceBind.setScadaGuid(scadaDeviceBindDTO.getScadaGuid());
            scadaDeviceBind.setSerialNumber(serialNumber);
            scadaDeviceBindService.insertScadaDeviceBind(scadaDeviceBind);
        }
        return success();
    }

    /**
     * 删除组态设备关联
     */
    @ApiOperation("删除组态设备关联")
    @PreAuthorize("@ss.hasPermi('scada:center:edit')")
    @DeleteMapping("/removeDeviceBind/{ids}")
    public AjaxResult removeDeviceBind(@PathVariable Long[] ids)
    {
        return toAjax(scadaDeviceBindService.deleteScadaDeviceBindByIds(ids));
    }

    /**
     * 获取组态变量数据，用于绑定变量
     * @param scadaQueryParam 查询参数
     * @return
     */
    @ApiOperation("获取组态变量数据")
    @PreAuthorize("@ss.hasPermi('scada:center:query')")
    @GetMapping("/listVariable")
    public TableDataInfo listVariable(ScadaQueryParam scadaQueryParam)
    {
        if(StringUtils.isEmpty(scadaQueryParam.getScadaGuid())){
            return getDataTable(new ArrayList<>());
        }
        return scadaService.listVariable(scadaQueryParam);
    }

    /**
     * 导入json文件
     * @param file 文件
     * @param guid guid
     * @return
     */
    @ApiOperation("组态页面导入json文件")
    @PreAuthorize("@ss.hasPermi('scada:center:add')")
    @PostMapping("/importJson")
    public AjaxResult importJson(MultipartFile file, String guid) throws IOException {
        return scadaService.importJson(file, guid);
    }

    /**
     * 收藏图库
     * @param favoritesVO 图库收藏传参类
     * @return
     */
    @ApiOperation("个人收藏图库")
    @PostMapping("/saveGalleryFavorites")
    public AjaxResult saveGalleryFavorites(@RequestBody FavoritesVO favoritesVO) {
        return scadaService.saveGalleryFavorites(favoritesVO);
    }

    /**
     * 查询收藏图库列表
     * @param scadaGallery 图库类
     * @return
     */
    @ApiOperation("查询个人收藏图库列表")
    @GetMapping("/listGalleryFavorites")
    public TableDataInfo listGalleryFavorites(ScadaGallery scadaGallery)
    {
        Page<ScadaGallery> page = scadaService.listGalleryFavorites(scadaGallery);
        return getDataTable(page.getRecords(), page.getTotal());
    }

    /**
     * 删除收藏图库
     * @return
     */
    @ApiOperation("删除个人收藏图库")
    @DeleteMapping("/deleteGalleryFavorites/{ids}")
    public AjaxResult deleteGalleryFavorites(@PathVariable Long[] ids) {
        return scadaService.deleteGalleryFavorites(ids);
    }

    /**
     * 收藏上传图库
     * @return
     */
    @ApiOperation("个人收藏上传图库")
    @PostMapping("/uploadGalleryFavorites")
    public AjaxResult uploadGalleryFavorites(MultipartFile file, String categoryName) {
        return scadaService.uploadGalleryFavorites(file, categoryName);
    }

    /**
     * 查询变量历史数据
     * @param param 查询条件
     * @return com.fastbee.common.core.domain.AjaxResult
     */
    @ApiOperation("查询变量历史数据")
    @PostMapping("/listVariableHistory")
    public AjaxResult listVariableHistory(@RequestBody ThingsModelHistoryParam param) {
        List<ThingsModelHistoryParam.ThingsModelSim> thingsModelList = param.getThingsModelList();
        if (CollectionUtils.isEmpty(thingsModelList)) {
            return success();
        }
        return success(scadaService.listVariableHistory(param));
    }

    /**
     * 获取设备运行状态
     * @param serialNumber 设备编号
     * @return com.fastbee.common.core.domain.AjaxResult
     */
    @ApiOperation("获取设备运行状态")
    @GetMapping("/getDeviceStatus")
    public AjaxResult getDeviceStatus(String serialNumber) {
        return AjaxResult.success(scadaService.getStatusBySerialNumber(serialNumber));
    }

    /**
     * 获取系统相关统计信息
     * @return com.fastbee.common.core.domain.AjaxResult
     */
    @GetMapping(value = "/statistic")
    @ApiOperation("获取系统相关统计信息")
    public AjaxResult getDeviceStatistic(Long deptId)
    {
        return AjaxResult.success(scadaService.selectStatistic(deptId));
    }

    /**
     * @description: 校验用户密码
     * @param: password 输入密码
     * @return: com.fastbee.common.core.domain.AjaxResult
     * @author admin
     * @date: 2024-07-15 16:07
     */
    @GetMapping("/verifyUserPassword")
    public AjaxResult verifyUserPassword(String password, Long userId) {
        if (StringUtils.isEmpty(password)) {
            return error(MessageUtils.message("scada.please.enter.password"));
        }
        SysUser user;
        if (userId != null) {
            user = scadaService.selectSysUser(userId);
        } else {
            user = getLoginUser().getUser();
        }
        if (null == user) {
            return error(MessageUtils.message("scada.please.login"));
        }
        String encodedPassword = user.getPassword();
        boolean matchResult = SecurityUtils.matchesPassword(password, encodedPassword);
        if (!matchResult) {
            return AjaxResult.error(MessageUtils.message("scada.password.fail.please.reload.enter"));
        }
        return success();
    }

    /**
     * 查询变量历史数据表格
     * @param param 查询条件
     * @return com.fastbee.common.core.domain.AjaxResult
     */
    @ApiOperation("查询变量历史数据表格")
    @PostMapping("/listVariableHistoryTable")
    public AjaxResult listVariableHistoryTable(@RequestBody ThingsModelHistoryParam param) {
        return success(scadaService.listVariableHistoryTable(param));
    }

    /**
     * 组态查看分享信息
     * @param scadaShareVO  组态信息
     * @return com.fastbee.common.core.domain.AjaxResult
     */
    @ApiOperation("组态查看分享信息")
    @GetMapping("/getShare")
    public AjaxResult getShare(ScadaShareVO scadaShareVO) {
        return AjaxResult.success(scadaService.getShare(scadaShareVO));
    }

    /**
     * 组态编辑分享
     * @param scadaShareVO  组态信息
     * @return com.fastbee.common.core.domain.AjaxResult
     */
    @ApiOperation("组态编辑分享")
    @PreAuthorize("@ss.hasPermi('scada:center:share')")
    @PostMapping("/editShare")
    public AjaxResult editShare(@RequestBody ScadaShareVO scadaShareVO,
                                HttpServletRequest httpServletRequest) {
        SecurityUtils.checkUserOperatePermission(scadaShareVO.getTenantId(), scadaShareVO.getCreateBy());
        return scadaService.editShare(scadaShareVO, httpServletRequest);
    }

    /**
     * 查询组态相关告警列表
     */
    @ApiOperation("查询组态相关告警列表")
    @GetMapping("/pageAlertLog")
    public TableDataInfo pageAlertLog(ScadaAlertLogParams scadaAlertLogParams)
    {
        Page<AlertLogVO> voPage = scadaService.pageAlertLog(scadaAlertLogParams);
        return getDataTable(voPage.getRecords(), voPage.getTotal());
    }

}
