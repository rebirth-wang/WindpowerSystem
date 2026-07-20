package com.fastbee.controller.card;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;

import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletResponse;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import com.fastbee.common.core.domain.AjaxResult;
import com.fastbee.common.core.page.TableDataInfo;
import com.fastbee.common.extend.core.controller.BaseController;
import com.fastbee.common.extend.core.domin.entity.SysUser;
import com.fastbee.common.extend.utils.SecurityUtils;
import com.fastbee.common.extend.utils.poi.ExcelUtil;
import com.fastbee.iot.domain.Card;
import com.fastbee.iot.model.vo.CardVO;
import com.fastbee.iot.service.ICardService;

/**
 * 物联网卡Controller
 *
 * @author fastbee
 * @date 2025-11-12
 */
@RestController
@RequestMapping("/iot/card")
@Api(tags = "物联网卡")
public class CardController extends BaseController {

    @Resource
    private ICardService cardService;


    /**
     * 查询物联网卡列表
     *
     * @param card 卡
     * @return {@link TableDataInfo }
     */
    @PreAuthorize("@ss.hasPermi('iot:card:list')")
    @GetMapping("/list")
    @ApiOperation("查询物联网卡列表")
    public TableDataInfo list(Card card) {
        Page<CardVO> voPage = cardService.pageCardVO(card);
        return getDataTable(voPage.getRecords(), voPage.getTotal());
    }


    /**
     * 导出物联网卡列表
     *
     * @param response 响应
     * @param card     卡
     */
    @ApiOperation("导出物联网卡列表")
    @PreAuthorize("@ss.hasPermi('iot:card:export')")
    @PostMapping("/export")
    public void export(HttpServletResponse response, Card card) {
        Page<CardVO> voPage = cardService.pageCardVO(card);
        ExcelUtil<CardVO> util = new ExcelUtil<CardVO>(CardVO.class);
        util.exportExcel(response, voPage.getRecords(), "物联网卡数据");
    }


    /**
     * 获取物联网卡详细信息
     *
     * @param id ID
     * @return {@link AjaxResult }
     */
    @PreAuthorize("@ss.hasPermi('iot:card:query')")
    @GetMapping(value = "/{id}")
    @ApiOperation("获取物联网卡详细信息")
    public AjaxResult getInfo(@PathVariable("id") Long id) {
        Card card = new Card();
        card.setId(id);
        return success(cardService.queryByIdWithCache(card));
    }


    /**
     * 新增物联网卡
     *
     * @param card 卡
     * @return {@link AjaxResult }
     */
    @PreAuthorize("@ss.hasPermi('iot:card:add')")
    @PostMapping
    @ApiOperation("新增物联网卡")
    public AjaxResult add(@RequestBody Card card) {
        return toAjax(cardService.insertWithCache(card));
    }


    /**
     * 修改物联网卡
     *
     * @param card 卡
     * @return {@link AjaxResult }
     */
    @PreAuthorize("@ss.hasPermi('iot:card:edit')")
    @PutMapping
    @ApiOperation("修改物联网卡")
    public AjaxResult edit(@RequestBody Card card) {
        SecurityUtils.checkUserOperatePermission(card.getTenantId(), card.getCreateBy());
        return toAjax(cardService.updateWithCache(card));
    }


    /**
     * 删除物联网卡
     *
     * @param ids IDs
     * @return {@link AjaxResult }
     */
    @PreAuthorize("@ss.hasPermi('iot:card:remove')")
    @DeleteMapping("/{ids}")
    @ApiOperation("删除物联网卡")
    public AjaxResult remove(@PathVariable Long[] ids) {
        List<Card> cardList = cardService.listByIds(Arrays.asList(ids));
        for (Card card : cardList) {
            SecurityUtils.checkUserOperatePermission(card.getTenantId(), card.getCreateBy());
        }
        return toAjax(cardService.deleteWithCacheByIds(ids, true));
    }

    /**
     * 同步卡信息
     *
     * @param cardVO
     * @return {@link AjaxResult }
     */
    @ApiOperation("同步卡信息")
    @PreAuthorize("@ss.hasPermi('iot:card:edit')")
    @PostMapping("/syncInfo")
    public AjaxResult syncInfo(@RequestBody CardVO cardVO) {
        try {
            SysUser user = getLoginUser().getUser();
            cardVO.setTenantId(user.getDept().getDeptUserId());
            cardVO.setTenantName(user.getDept().getDeptName());
            cardVO.setCreateBy(user.getUserName());
            Card card = cardService.syncCardInfo(cardVO);
            return AjaxResult.success("同步成功", card);
        } catch (Exception e) {
            return AjaxResult.error("同步失败: " + e.getMessage());
        }
    }

    /**
     * 同步流量信息
     *
     * @param cardVO
     * @return {@link AjaxResult }
     */
    @ApiOperation("同步流量信息")
    @PreAuthorize("@ss.hasPermi('iot:card:edit')")
    @PostMapping("/syncTrafficInfo")
    public AjaxResult syncTrafficInfo(@RequestBody CardVO cardVO) {
        try {
            Card card = cardService.syncTrafficInfo(cardVO.getIccid(), cardVO.getCardPlatformId());
            if (Objects.isNull(card)) {
                return AjaxResult.error("查询失败");
            }
            return AjaxResult.success(card);
        } catch (Exception e) {
            return AjaxResult.error("查询失败: " + e.getMessage());
        }
    }

    /**
     * 同步状态信息
     *
     * @param cardVO
     * @return {@link AjaxResult }
     */
    @ApiOperation("同步状态信息")
    @PreAuthorize("@ss.hasPermi('iot:card:edit')")
    @PostMapping("/syncStatus")
    public AjaxResult syncStatus(@RequestBody CardVO cardVO) {
        try {
            CardVO resultVo = cardService.syncStatus(cardVO.getIccid(), cardVO.getCardPlatformId());
            if (Objects.isNull(resultVo)) {
                return AjaxResult.error("查询失败");
            }
            return AjaxResult.success(resultVo);
        } catch (Exception e) {
            return AjaxResult.error("查询失败: " + e.getMessage());
        }
    }

    /**
     * 激活
     *
     * @param iccid
     * @return {@link AjaxResult }
     */
    @ApiOperation("激活卡片")
    @PreAuthorize("@ss.hasPermi('iot:card:edit')")
    @PostMapping("/activate/{iccid}")
    public AjaxResult activate(@PathVariable String iccid) {
        try {
            boolean result = cardService.activate(iccid);
            return result ? AjaxResult.success("激活成功") : AjaxResult.error("激活失败");
        } catch (Exception e) {
            return AjaxResult.error("激活失败: " + e.getMessage());
        }
    }

    /**
     * 暂停
     *
     * @param iccid
     * @return {@link AjaxResult }
     */
    @ApiOperation("停用卡片")
    @PreAuthorize("@ss.hasPermi('iot:card:edit')")
    @PostMapping("/suspend/{iccid}")
    public AjaxResult suspend(@PathVariable String iccid) {
        try {
            boolean result = cardService.suspend(iccid);
            return result ? AjaxResult.success("停用成功") : AjaxResult.error("停用失败");
        } catch (Exception e) {
            return AjaxResult.error("停用失败: " + e.getMessage());
        }
    }

    /**
     * 物联网卡统计信息
     *
     * @return {@link AjaxResult }
     */
    @PreAuthorize("@ss.hasPermi('iot:card:overview')")
    @GetMapping(value = "/statistics")
    @ApiOperation("获取物联网卡详细信息")
    public AjaxResult statistics() {
        return success(cardService.statistics());
    }
}
