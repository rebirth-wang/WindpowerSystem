package com.fastbee.controller.operations;

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
import com.fastbee.common.extend.core.domin.notify.WorkOrderNotifyParams;
import com.fastbee.common.extend.enums.ObjectOperationTypeEnum;
import com.fastbee.common.extend.enums.WorkOrderStatusEnum;
import com.fastbee.common.extend.enums.WorkOrderTypeEnum;
import com.fastbee.common.extend.utils.SecurityUtils;
import com.fastbee.common.extend.utils.poi.ExcelUtil;
import com.fastbee.common.utils.MessageUtils;
import com.fastbee.common.utils.StringUtils;
import com.fastbee.iot.domain.ObjectOperationLog;
import com.fastbee.iot.domain.WorkOrder;
import com.fastbee.iot.model.vo.WorkOrderVO;
import com.fastbee.iot.service.IObjectOperationLogService;
import com.fastbee.iot.service.IWorkOrderService;
import com.fastbee.notify.core.service.NotifySendService;

/**
 * 工单管理Controller
 *
 * @author fastbee
 * @date 2025-08-18
 */
@RestController
@RequestMapping("/iot/workOrder")
@Api(tags = "工单管理")
public class WorkOrderController extends BaseController {

    @Resource
    private IWorkOrderService workOrderService;
    @Resource
    private IObjectOperationLogService objectOperationLogService;
    @Resource
    private NotifySendService notifySendService;

    /**
     * 查询工单管理列表
     */
    @PreAuthorize("@ss.hasPermi('iot:workOrder:list')")
    @GetMapping("/list")
    @ApiOperation("查询工单管理列表")
    public TableDataInfo list(WorkOrderVO workOrderVO) {
        Page<WorkOrderVO> voPage = workOrderService.pageWorkOrderVO(workOrderVO);
        return getDataTable(voPage.getRecords(), voPage.getTotal());
    }

    /**
     * 查询自己的工单列表
     */
    @PreAuthorize("@ss.hasPermi('iot:workOrder:listMyself')")
    @GetMapping("/listMyself")
    @ApiOperation("查询工单管理列表")
    public TableDataInfo listMyself(WorkOrderVO workOrderVO) {
        Page<WorkOrderVO> voPage = workOrderService.pageWorkOrderVO(workOrderVO);
        return getDataTable(voPage.getRecords(), voPage.getTotal());
    }

    /**
     * 导出工单管理列表
     */
    @ApiOperation("导出工单管理列表")
    @PreAuthorize("@ss.hasPermi('iot:workOrder:export')")
    @PostMapping("/export")
    public void export(HttpServletResponse response, WorkOrderVO workOrderVO) {
        Page<WorkOrderVO> voPage = workOrderService.pageWorkOrderVO(workOrderVO);
        ExcelUtil<WorkOrderVO> util = new ExcelUtil<WorkOrderVO>(WorkOrderVO.class);
        util.exportExcel(response, voPage.getRecords(), "工单管理数据");
    }

    /**
     * 获取工单管理详细信息
     */
    @PreAuthorize("@ss.hasPermi('iot:workOrder:query')")
    @GetMapping(value = "/{id}")
    @ApiOperation("获取工单管理详细信息")
    public AjaxResult getInfo(@PathVariable("id") Long id) {
        WorkOrder workOrder = new WorkOrder();
        workOrder.setId(id);
        return success(workOrderService.queryByIdWithCache(workOrder));
    }

    /**
     * 新增工单管理
     */
    @PreAuthorize("@ss.hasPermi('iot:workOrder:add')")
    @PostMapping
    @ApiOperation("新增工单管理")
    public AjaxResult add(@RequestBody WorkOrder workOrder) {
        WorkOrderVO orderVO = workOrderService.insertWithCache(workOrder);
        if (Objects.isNull(orderVO)) {
            return error();
        }
        this.notifySend(orderVO);
        return success();
    }

    /**
     * 修改工单管理
     */
    @PreAuthorize("@ss.hasPermi('iot:workOrder:edit')")
    @PutMapping
    @ApiOperation("修改工单管理")
    public AjaxResult edit(@RequestBody WorkOrderVO workOrderVO) {
        SecurityUtils.checkUserOperatePermission(workOrderVO.getTenantId(), workOrderVO.getCreateBy());
        WorkOrderVO orderVO = workOrderService.updateWithCache(workOrderVO);
        if (Objects.isNull(orderVO)) {
            return error();
        }
        this.notifySend(orderVO);
        return success();
    }

    /**
     * 删除工单管理
     */
    @PreAuthorize("@ss.hasPermi('iot:workOrder:remove')")
    @DeleteMapping("/{ids}")
    @ApiOperation("删除工单管理")
    public AjaxResult remove(@PathVariable Long[] ids) {
        List<WorkOrder> workOrders = workOrderService.listByIds(Arrays.asList(ids));
        for (WorkOrder workOrder : workOrders) {
            SecurityUtils.checkUserOperatePermission(workOrder.getTenantId(), workOrder.getCreateBy());
        }
        return toAjax(workOrderService.deleteWithCacheByIds(ids, true));
    }

    /**
     * 变更工单状态
     */
//    @PreAuthorize("@ss.hasPermi('iot:workOrder:edit')")
    @PostMapping("/changeStatus")
    @ApiOperation("变更工单状态")
    public AjaxResult changeStatus(@RequestBody WorkOrderVO workOrderVO) {
        if (WorkOrderStatusEnum.ORDER_DISPATCHED.getStatus().equals(workOrderVO.getStatus()) && Objects.isNull(workOrderVO.getUserId())) {
            return AjaxResult.error(MessageUtils.message("workOrder.please.select.user"));
        }
        if (WorkOrderStatusEnum.CLOSED_ORDER.getStatus().equals(workOrderVO.getStatus()) && StringUtils.isEmpty(workOrderVO.getResult())) {
            return AjaxResult.error(MessageUtils.message("workOrder.please.fill.result.info"));
        }
        WorkOrderVO resultVo = workOrderService.changeStatus(workOrderVO);
        if (Objects.isNull(resultVo)) {
            return error();
        }
        this.notifySend(resultVo);
        return success();
    }

    private void notifySend(WorkOrderVO resultVo) {
        if (Boolean.FALSE.equals(resultVo.getNotifyFlag())) {
            return;
        }
        // 发通知
        WorkOrderNotifyParams workOrderNotifyParams = new WorkOrderNotifyParams();
        workOrderNotifyParams.setWorkOrderType(Objects.requireNonNull(WorkOrderTypeEnum.getEnumByType(resultVo.getType())).getDescription());
        workOrderNotifyParams.setCreateBy(resultVo.getCreateBy());
        workOrderNotifyParams.setDeviceName(resultVo.getDeviceName());
        workOrderNotifyParams.setSerialNumber(resultVo.getSerialNumber());
        workOrderNotifyParams.setName(resultVo.getName());
        workOrderNotifyParams.setSendEmail(resultVo.getUserEmail());
        workOrderNotifyParams.setNumber(resultVo.getNumber());
        workOrderNotifyParams.setTenantId(resultVo.getTenantId());
        notifySendService.workOrderSend(workOrderNotifyParams);
    }


    /**
     * 查询工单操作记录列表
     */
    @PreAuthorize("@ss.hasPermi('iot:workOrderLog:log')")
    @GetMapping("/listLog")
    @ApiOperation("查询工单操作记录列表")
    public TableDataInfo listLog(Long workOrderId) {
        ObjectOperationLog objectOperationLog = new ObjectOperationLog();
        objectOperationLog.setObjectId(workOrderId);
        objectOperationLog.setType(ObjectOperationTypeEnum.WORK_ORDER.getType());
        Page<ObjectOperationLog> voPage = objectOperationLogService.pageObjectOperationLog(objectOperationLog);
        return getDataTable(voPage.getRecords(), voPage.getTotal());
    }

    /**
     * 终端用户新增工单
     */
    @PreAuthorize("@ss.hasPermi('iot:workOrder:endUserAdd')")
    @PostMapping("/endUserAdd")
    @ApiOperation("终端用户新增工单")
    public AjaxResult endUserAdd(@RequestBody WorkOrderVO workOrderVO) {
        return workOrderService.endUserAdd(workOrderVO);
    }

    /**
     * 终端用户获取工单处理结果
     */
    @PreAuthorize("@ss.hasPermi('iot:workOrder:endUserQuery')")
    @GetMapping(value = "/endUserQuery")
    @ApiOperation("终端用户获取工单处理结果")
    public AjaxResult endUserQuery(Long id) {
        return success(workOrderService.endUserQuery(id));
    }

}
