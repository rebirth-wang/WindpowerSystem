package com.fastbee.scada.service;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import jakarta.servlet.http.HttpServletRequest;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import org.springframework.web.multipart.MultipartFile;

import com.fastbee.common.core.domain.AjaxResult;
import com.fastbee.common.core.page.TableDataInfo;
import com.fastbee.common.extend.core.domin.entity.SysUser;
import com.fastbee.iot.model.vo.AlertLogVO;
import com.fastbee.scada.domain.Scada;
import com.fastbee.scada.domain.ScadaGallery;
import com.fastbee.scada.vo.*;

/**
 * 组态中心Service接口
 *
 * @author kerwincui
 * @date 2023-11-10
 */
public interface IScadaService extends IService<Scada>
{

    /**
     * 查询组态页面列表
     *
     * @param scada 组态页面
     * @return 组态页面分页集合
     */
    Page<ScadaVO> pageScadaVO(Scada scada);

    /**
     * 查询组态中心
     *
     * @param scada 组态中心
     * @return 组态中心
     */
    public ScadaVO selectScadaById(Scada scada);

    /**
     * 查询组态页面列表
     *
     * @param scada 组态页面
     * @return 组态页面集合
     */
    List<ScadaVO> listScadaVO(Scada scada);

    /**
     * 新增组态中心
     *
     * @param scadaVO 组态中心
     * @return 结果
     */
    public AjaxResult insertScada(ScadaVO scadaVO);

    /**
     * 修改组态中心
     *
     * @param scadaVO 组态中心
     * @return 结果
     */
    public int updateScada(ScadaVO scadaVO);

    /**
     * 批量删除组态中心
     *
     * @param ids 需要删除的组态中心主键集合
     * @return 结果
     */
    public int deleteScadaByIds(Long[] ids);

    /**
     * 删除组态中心信息
     *
     * @param id 组态中心主键
     * @return 结果
     */
    public int deleteScadaById(Long id);

    /**
     * 根据guid获取组态详情
     * @param scadaDateQueryVO 组态查询参数
     * @return
     */
    ScadaVO selectScadaByGuid(ScadaDateQueryVO scadaDateQueryVO);

    /**
     * 图库收藏上传
     * @return com.fastbee.common.core.domain.AjaxResult
     */
    AjaxResult uploadGalleryFavorites(MultipartFile file, String categoryName);

    /**
     * 个人图库收藏
     * @param favoritesVO 收藏vo类
     * @return com.fastbee.common.core.domain.AjaxResult
     */
    AjaxResult saveGalleryFavorites(FavoritesVO favoritesVO);

    /**
     * 个人删除收藏图库
     * @return com.fastbee.common.core.domain.AjaxResult
     */
    AjaxResult deleteGalleryFavorites(Long[] ids);

    /**
     * 查询个人收藏图库
     * @param scadaGallery 图库类
     * @return java.util.List<com.fastbee.scada.domain.ScadaGallery>
     */
    Page<ScadaGallery> listGalleryFavorites(ScadaGallery scadaGallery);

    /**
     * 查询变量历史数据
     * @param param 查询条件
     * @return com.fastbee.common.core.domain.AjaxResult
     */
    List<ScadaHistoryModelVO> listThingsModelHistory(ThingsModelHistoryParam param);

    /**
     * 查询设备运行状态
     * @param serialNumber 设备编号
     * @return java.lang.String
     */
    Integer getStatusBySerialNumber(String serialNumber);

    /**
     * 获取系统相关统计信息
     * @return com.fastbee.common.core.domain.AjaxResult
     */
    ScadaStatisticVO selectStatistic(Long deptId);

    /**
     * 获取组态绑定设备的物模型
     * @param scadaQueryParam 查询参数
     * @return com.fastbee.common.core.page.TableDataInfo
     */
    List<ScadaVariableDataVO> listBindDeviceThingsModel(ScadaQueryParam scadaQueryParam);

    /**
     * 获取组态绑定设备
     * @param scadaDeviceBindVO
     * @return java.util.List<com.fastbee.scada.domain.ScadaDeviceBind>
     */
    Page<ScadaDeviceBindVO> listDeviceBind(ScadaDeviceBindVO scadaDeviceBindVO);

    /**
     * 查询组态变量
     * @param scadaQueryParam 查询类
     * @return com.fastbee.common.core.page.TableDataInfo
     */
    TableDataInfo listVariable(ScadaQueryParam scadaQueryParam);

    /**
     * 查询变量历史数据
     * @param param 查询条件
     * @return com.fastbee.common.core.domain.AjaxResult
     */
    Map<String, List<ScadaHistoryModelVO>> listVariableHistory(ThingsModelHistoryParam param);

    /**
     * 查询变量历史数据表格
     * @param param 查询条件
     * @return com.fastbee.common.core.domain.AjaxResult
     */
    List<ScadaVariableDataVO> listVariableHistoryTable(ThingsModelHistoryParam param);

    /**
     * 组态编辑分享
     * @param scadaShareVO  组态信息
     * @return com.fastbee.common.core.domain.AjaxResult
     */
    AjaxResult editShare(ScadaShareVO scadaShareVO, HttpServletRequest httpServletRequest);

    /**
     * 组态查看分享状态
     * @param scadaShareVO  组态信息
     * @return com.fastbee.common.core.domain.AjaxResult
     */
    ScadaShareVO getShare(ScadaShareVO scadaShareVO);

    /**
     * @description: 查询用户信息
     * @param: userId 用户id
     * @return: com.fastbee.common.core.domain.entity.SysUser
     */
    SysUser selectSysUser(Long userId);

    AjaxResult importJson(MultipartFile file, String guid) throws IOException;

    Page<AlertLogVO> pageAlertLog(ScadaAlertLogParams scadaAlertLogParams);
}
