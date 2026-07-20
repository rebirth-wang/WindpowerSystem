<template>
  <div
    class="view-time-table"
    :id="detail.identifier"
    :style="{
      border: `${detail.style.waterBorderWidth}px solid !important`,
      borderRadius: `${detail.style.borderRadius}px !important`,
      borderColor: detail.style.waterBorderColor,
    }"
  >
    <el-form
      v-if="detail.dataBind.tableType === 2"
      class="form-wrap"
      :model="queryParams"
      ref="queryForm"
      :inline="true"
      label-width="68px"
    >
      <el-form-item prop="dayDaterange">
        <el-date-picker
          :style="{ width: '340px', color: detail.style.foreColor }"
          v-model="queryParams.dayDaterange"
          value-format="YYYY-MM-DD HH:mm:ss"
          type="datetimerange"
          range-separator="-"
          :start-placeholder="$t('scada.topo.unit.viewTimeTable.764059-4')"
          :end-placeholder="$t('scada.topo.unit.viewTimeTable.764059-5')"
          :shortcuts="dateShortcuts"
          :clearable="false"
        ></el-date-picker>
      </el-form-item>
      <el-form-item>
        <el-button type="primary" @click="handleQuery">
          <el-icon><Search /></el-icon>
          <span>{{ $t('search') }}</span>
        </el-button>
        <el-button @click="handleResetQuery">
          <el-icon><Refresh /></el-icon>
          <span>{{ $t('reset') }}</span>
        </el-button>
      </el-form-item>
    </el-form>

    <el-table
      v-if="hasTableConfig"
      :data="computedTableList"
      style="width: 100%"
      :cell-style="{ fontSize: detail.style.fontSize + 'px', color: detail.style.foreColor }"
      :header-cell-style="{ fontSize: detail.style.fontSize + 'px', color: detail.style.foreColor }"
      :border="false"
    >
      <el-table-column
        v-for="(item, index) in tableColumns"
        :key="item.id + index"
        :type="item.id === 'index' ? 'index' : undefined"
        :prop="item.id"
        :label="item.title"
        align="center"
        min-width="60"
      ></el-table-column>
    </el-table>
    <el-empty v-else class="table-empty" description="请先绑定变量并配置表格列" />

    <div class="pag-wrap" v-show="total > 0">
      <pagination
        :total="total"
        v-model:page="queryParams.pageNum"
        v-model:limit="queryParams.pageSize"
        @pagination="getList"
      />
    </div>
  </div>
</template>

<script lang="ts">
import { useTopoEditorStore } from '@/stores/modules/topoEditor';
import { useRoute } from 'vue-router';
import BaseView from '../View.vue';
import { getListVariable, listVariableHistoryTable } from '@/api/scada/topo';
import { getScadaRouteType, getRouteQueryString } from '@/utils/topo/topoUtil';
import { Search, Refresh } from '@element-plus/icons-vue';

function formatDateTime(value) {
  const date = value ? new Date(value) : new Date();
  const pad = (n) => String(n).padStart(2, '0');
  return `${date.getFullYear()}-${pad(date.getMonth() + 1)}-${pad(date.getDate())} ${pad(date.getHours())}:${pad(date.getMinutes())}:${pad(date.getSeconds())}`;
}

export default {
  name: 'ViewTimeTable',
  extends: BaseView,
  components: { Search, Refresh },
  setup() {
    const route = useRoute();
    const topoStore = useTopoEditorStore();
    return { route, topoStore };
  },
  data() {
    return {
      queryParams: {
        dayDaterange: [new Date(Date.now() - 3600 * 1000 * 2), new Date()], // 默认最近2小时
        pageNum: 1,
        pageSize: 10,
      },
      total: 0,
      variableRealList: [], // 实时数据全量缓存
      variableHistoryList: [], // 历史数据全量缓存
      thingsModels: [], // 物模型定义列表
      tableList: [], // 当前页显示的数据（非编辑模式使用）
      initDataTimer: null, // 用于防抖的定时器
    };
  },
  computed: {
    dateShortcuts() {
      const ranges = [
        { text: this.$t('scada.topo.unit.viewTimeTable.764059-0'), offset: 3600 * 1000 * 2 },
        { text: this.$t('scada.topo.unit.viewTimeTable.764059-1'), offset: 3600 * 1000 * 24 },
        { text: this.$t('scada.topo.unit.viewTimeTable.764059-2'), offset: 3600 * 1000 * 24 * 7 },
        { text: this.$t('scada.topo.unit.viewTimeTable.764059-3'), offset: 3600 * 1000 * 24 * 30 },
      ];
      return ranges.map((item) => ({
        text: item.text,
        value: () => {
          const end = new Date();
          const start = new Date(end.getTime() - item.offset);
          return [start, end];
        },
      }));
    },
    mqttData() {
      return this.topoStore.mqttData;
    },
    // 判断是否为实时数据模式
    isRealTimeMode() {
      return this.detail.dataBind.tableType === 1;
    },
    identifiers() {
      return Array.isArray(this.detail?.dataBind?.identifiers) ? this.detail.dataBind.identifiers : [];
    },
    tableColumns() {
      const columns = Array.isArray(this.detail?.dataBind?.tableColumns) ? this.detail.dataBind.tableColumns : [];
      return columns.filter((item) => item && item.visible !== false);
    },
    hasTableConfig() {
      return this.identifiers.length > 0 && this.tableColumns.length > 0;
    },
    // 处理编辑模式的模拟数据和非编辑模式的tableList
    computedTableList() {
      if (!this.hasTableConfig) return [];
      if (this.editMode) {
        return this.identifiers.map(() => ({
          serialNum: 'XXX',
          deviceName: 'XXX',
          slaveName: 'XXX',
          modelName: 'XXX',
          value: 'XXX',
          unit: 'XXX',
          ts: 'XXXX',
        }));
      } else {
        return this.tableList;
      }
    },
  },
  watch: {
    // 监听identifiers
    'detail.dataBind.identifiers': {
      handler(newVal, oldVal) {
        if (JSON.stringify(newVal) !== JSON.stringify(oldVal)) {
          if (this.editMode) {
            this.total = Array.isArray(newVal) ? newVal.length : 0;
          } else {
            this.debouncedInitData();
          }
        }
      },
      deep: true,
      immediate: true,
    },
    // 监听tableType
    'detail.dataBind.tableType': {
      handler(newVal, oldVal) {
        if (newVal !== oldVal) {
          if (!this.editMode) {
            this.debouncedInitData();
          }
        }
      },
      immediate: true,
    },
    // 监听物模型列表加载完成
    thingsModels: {
      handler(newVal) {
        if (!this.editMode && newVal.length > 0) {
          this.refreshTableData();
        }
      },
    },
    // 监听 MQTT 数据推送
    mqttData: {
      handler(newVal) {
        if (!this.isRealTimeMode || this.editMode || !newVal) return;
        this.handleMqttUpdate(newVal);
      },
      deep: true,
    },
  },
  mounted() {
    if (!this.editMode) {
      this.getVariableList(); // 初始化获取物模型字典
    }
  },
  beforeUnmount() {
    if (this.initDataTimer) {
      clearTimeout(this.initDataTimer);
      this.initDataTimer = null;
    }
  },
  methods: {
    // 防抖函数
    debouncedInitData() {
      if (this.initDataTimer) {
        clearTimeout(this.initDataTimer);
      }
      this.initDataTimer = setTimeout(() => {
        this.initData();
      }, 300);
    },

    /**
     * 初始化数据入口
     */
    async initData() {
      if (!this.hasTableConfig) {
        this.total = 0;
        this.tableList = [];
        return;
      }
      if (this.isRealTimeMode) {
        await this.initRealTimeList();
      } else {
        await this.getHistoryList();
      }
      this.updatePageList();
    },

    /**
     * 获取设备的所有变量定义（物模型）
     */
    getVariableList() {
      const type = getScadaRouteType(this.route.query);
      const params = {
        scadaGuid: getRouteQueryString(this.route.query, 'guid'),
        type: type,
        page: 1,
        size: 9999,
      };
      getListVariable(params).then((res) => {
        if (res.code == 200) {
          this.thingsModels = res.rows;
        }
      });
    },

    /**
     * 初始化实时数据列表结构
     */
    initRealTimeList() {
      const type = getScadaRouteType(this.route.query);
      const { sceneModelDeviceIds, modelValues, modelTimes } = this.detail.dataBind;
      const identifiers = this.identifiers;
      let list = [];
      if (type === 2) {
        // 场景模式
        const modelMap = this.thingsModels.map((item) => ({
          ...item,
          uniqueId: `${item.sceneModelDeviceId}-${item.identifier}`,
        }));
        list = identifiers.map((identifier, index) => {
          const targetId = `${sceneModelDeviceIds[index]}-${identifier}`;
          const found = modelMap.find((m) => m.uniqueId === targetId);
          return found ? { ...found } : { identifier, value: 0 };
        });
      } else {
        // 单设备模式
        list = identifiers.map((identifier) => {
          const found = this.thingsModels.find((m) => m.identifier === identifier);
          return found ? { ...found } : { identifier, value: 0 };
        });
      }

      this.variableRealList = list.map((item, index) => ({
        ...item,
        value: this.getFunHandlingResult(modelValues && modelValues[index] ? modelValues[index] : 0, item.identifier),
        ts: modelTimes && modelTimes[index] ? modelTimes[index] : '---',
        id: item.identifier,
      }));

      this.total = this.variableRealList.length;
    },

    /**
     * 处理 MQTT 消息推送
     */
    handleMqttUpdate(mqttData) {
      if (Object.keys(mqttData).length === 0) return;

      const routeType = getScadaRouteType(this.route.query);
      const bindNum = this.detail.dataBind.serialNumber || getRouteQueryString(this.route.query, 'serialNumber');
      const mqttNum = mqttData.serialNumber;
      const mqttDeviceId = mqttData.sceneModelDeviceId;

      // 检查序列号是否匹配
      const isSerialNumberMatched = routeType === 2 ? true : bindNum && this.isSameValue(bindNum, mqttNum);

      if (!isSerialNumberMatched) return;

      // 更新数据
      const messages = mqttData.message || [];

      this.variableRealList.forEach((item) => {
        const updateMsg = messages.find((msg) => {
          if (routeType === 2 && mqttDeviceId) {
            // 场景模式
            return this.isSameValue(item.sceneModelDeviceId, mqttDeviceId) && item.identifier === msg.id;
          } else {
            // 单机模式
            return item.identifier === msg.id;
          }
        });

        if (updateMsg) {
          item.value = this.getFunHandlingResult(updateMsg.value, item.identifier);
          item.ts = formatDateTime(new Date());
        }
      });
      // 刷新当前页显示
      this.updatePageList();
    },

    /**
     * 获取历史数据
     */
    getHistoryList() {
      if (!this.hasTableConfig || this.thingsModels.length === 0) {
        this.variableHistoryList = [];
        this.total = 0;
        return;
      }

      let { serialNumber, sceneModelId } = this.detail.dataBind;
      const identifiers = this.identifiers;
      const type = getScadaRouteType(this.route.query);

      if (type === 1) serialNumber = getRouteQueryString(this.route.query, 'serialNumber');
      if (type === 2) serialNumber = '';

      // 构造请求参数
      const thingsModelList = identifiers.map((item) => {
        const data = this.thingsModels.find((chil) => chil.identifier === item);
        return {
          identifier: data?.identifier || item,
          type: data?.type,
          slaveld: data?.slaveld,
          slaveName: data?.slaveName,
          sceneModelDeviceId: data?.sceneModelDeviceId ? Number(data.sceneModelDeviceId) : null,
          unit: data?.unit,
        };
      });

      const params = {
        scadaType: type,
        sceneModelId,
        serialNumber,
        beginTime: formatDateTime(this.queryParams.dayDaterange?.[0]),
        endTime: formatDateTime(this.queryParams.dayDaterange?.[1]),
        thingsModelList,
      };

      return listVariableHistoryTable(params).then((res) => {
        if (res.code == 200) {
          this.variableHistoryList = (res.data || []).map((row) => ({
            ...row,
            value: this.getFunHandlingResult(row.value, row.identifier),
          }));
          this.total = this.variableHistoryList.length;
        }
      });
    },

    /**
     * 更新当前表格页显示数据 (前端分页)
     */
    updatePageList() {
      const start = (this.queryParams.pageNum - 1) * this.queryParams.pageSize;
      const end = start + this.queryParams.pageSize;

      if (this.isRealTimeMode) {
        this.tableList = this.variableRealList.slice(start, end);
      } else {
        this.tableList = this.variableHistoryList.slice(start, end);
      }
    },

    /**
     * 数据/配置变化时刷新表格
     */
    async refreshTableData() {
      if (this.isRealTimeMode) {
        await this.initRealTimeList();
      } else {
        await this.getHistoryList();
      }
      this.updatePageList();
    },

    // 分页事件
    getList(e) {
      this.queryParams.pageNum = e.page;
      this.queryParams.pageSize = e.limit;
      if (!this.editMode) {
        this.updatePageList();
      }
    },

    // 查询操作
    async handleQuery() {
      this.queryParams.pageNum = 1;
      await this.getHistoryList();
      this.updatePageList();
    },

    // 重置操作
    handleResetQuery() {
      this.resetForm('queryForm');
      // 重置时间范围为最近2小时
      this.queryParams.dayDaterange = [new Date(Date.now() - 3600 * 1000 * 2), new Date()];
      this.handleQuery();
    },
  },
};
</script>

<style lang="scss" scoped>
.view-time-table {
  height: 100%;
  width: 100%;
  overflow-y: auto;

  .form-wrap {
    padding: 10px 10px 5px;

    :deep(.el-form-item) {
      margin-bottom: 0;
    }

    :deep(.el-date-editor) {
      background-color: transparent;

      .el-range-input {
        color: unset;
        background-color: transparent;
      }
    }
  }

  :deep(.el-table) {
    background-color: transparent;

    .el-table__header-wrapper {
      background-color: transparent;

      tr {
        background-color: transparent;
        th {
          background-color: transparent;
        }
      }
    }

    .el-table__body-wrapper {
      background-color: transparent;

      tr {
        background-color: transparent;

        &:hover > td {
          background-color: transparent;
        }
      }
    }
  }

  .table-empty {
    height: calc(100% - 20px);
    min-height: 120px;

    :deep(.el-empty__description p) {
      color: inherit;
    }
  }

  .pag-wrap {
    margin: 0 10px 40px;
  }
}
</style>
