<template>
  <div
    :id="detail.identifier"
    class="view-num-panel"
    :style="{
      borderRadius: detail.style.borderRadius + 'px',
      borderWidth: detail.style.waterBorderWidth + 'px',
      borderStyle: 'solid',
      borderColor: detail.style.waterBorderColor,
      overflow: 'hidden',
      transition: '0.3s',
    }"
  >
    <div
      :style="{
        width: '100%',
        height: '100%',
        borderImageSource: detail.style.url ? 'url(' + baseApi + detail.style.url + ')' : null,
        borderImageSlice: '0 0 0 0 fill',
        borderImageRepeat: 'stretch',
      }"
    >
      <div
        class="panel-header"
        :style="{
          borderBottomWidth: detail.style.waterBorderWidth + 'px',
          borderBottomStyle: 'solid',
          borderBottomColor: detail.style.waterBorderColor,
          fontSize: detail.style.titleFontSize + 'px',
          textAlign: detail.style.textAlign,
          display: detail.style.titleVisible ? 'block' : 'none',
          color: detail.style.foreColor,
        }"
      >
        {{ detail.dataBind.tableTitle }}
      </div>
      <div class="panel-body">
        <table class="table-wrap" cellspacing="0" cellpadding="0" border="0">
          <tbody>
            <tr
              class="item-row"
              :style="{
                fontSize: detail.style.fontSize + 'px',
                textAlign: detail.style.textAlign,
              }"
              v-for="(item, index) in variableList"
              :key="index"
            >
              <template v-for="(param, paramIndex) in detail.dataBind.tableColumns">
                <td
                  class="item-cell"
                  v-if="param.visible && param.id === 'index'"
                  :key="paramIndex"
                  :style="{ color: param.color }"
                  rowspan="1"
                  colspan="1"
                >
                  {{ index + 1 }}
                </td>
                <td
                  class="item-cell"
                  v-if="param.visible && param.id !== 'index'"
                  :key="paramIndex"
                  :style="{ color: param.color }"
                  rowspan="1"
                  colspan="1"
                >
                  {{
                    item[param.id] !== undefined && item[param.id] !== '' && item[param.id] !== null
                      ? item[param.id]
                      : '----'
                  }}
                </td>
              </template>
            </tr>
          </tbody>
        </table>
      </div>
    </div>
    <div v-show="false">{{ tableMockInit }}{{ dataInit }}</div>
  </div>
</template>

<script lang="ts">
import { useTopoEditorStore } from '@/stores/modules/topoEditor';
import { useRoute } from 'vue-router';

import BaseView from '../View.vue';
import { getListVariable } from '@/api/scada/topo';
import { getScadaRouteType, getRouteQueryString } from '@/utils/topo/topoUtil';

export default {
  name: 'ViewNumPanel',
  extends: BaseView,
  setup() {
    const route = useRoute();
    const topoStore = useTopoEditorStore();
    return { route, topoStore };
  },
  data() {
    return {
      baseApi: import.meta.env.VITE_APP_BASE_API,
      variableList: [],
    };
  },
  computed: {
    mqttData() {
      return this.topoStore.mqttData;
    },
    tableMockInit() {
      const { identifiers } = this.detail.dataBind;
      this.variableList = identifiers.map((item) => ({
        deviceName: 'XXX',
        modelName: 'XXX',
        value: '--',
        unit: 'XXX',
        ts: 'XXX',
      }));
      return identifiers;
    },
    dataInit() {
      if (Object.keys(this.mqttData).length !== 0) {
        const type = getScadaRouteType(this.route.query);
        let bindNum = this.detail.dataBind.serialNumber;
        let mqttNum = this.mqttData.serialNumber;
        let mqttDeviceId = this.mqttData.sceneModelDeviceId;
        if (type === 1) {
          bindNum = getRouteQueryString(this.route.query, 'serialNumber');
        }
        if (type === 2) {
          if (bindNum && this.isSameValue(bindNum, mqttNum)) {
            for (let i = 0; i < this.mqttData.message.length; i++) {
              this.variableList.forEach((item) => {
                if (item.identifier === this.mqttData.message[i].id) {
                  item.value = this.mqttData.message[i].value;
                  const specs = item.specs ? JSON.parse(item.specs) : null;
                  if (specs.type === 'enum') {
                    const enumValue = specs.enumList.find((item) => item.value === this.mqttData.message[i].value);
                    item.value = enumValue?.text ? enumValue.text : item.value;
                  }
                  if (specs.type === 'bool') {
                    // item.value = !!this.mqttData.message[i].value && this.mqttData.message[i].value === '1' ? specs.trueText : specs.falseText;
                    if (!!this.mqttData.message[i].value && this.mqttData.message[i].value === '1') {
                      item.value = specs.trueText;
                    }
                    if (!this.mqttData.message[i].value && this.mqttData.message[i].value === '0') {
                      item.value = specs.falseText;
                    }
                  }
                  item.value = this.getFunHandlingResult(item.value, item.identifier);
                }
              });
            }
            this.variableList = [...this.variableList];
          }
          if (mqttDeviceId) {
            for (let i = 0; i < this.mqttData.message.length; i++) {
              this.variableList.forEach((item) => {
                if (item.id === `${mqttDeviceId}-${this.mqttData.message[i].id}`) {
                  item.value = this.mqttData.message[i].value;
                  const specs = item.specs ? JSON.parse(item.specs) : null;
                  if (specs.type === 'enum') {
                    const enumValue = specs.enumList.find((item) => item.value === this.mqttData.message[i].value);
                    item.value = enumValue?.text ? enumValue.text : item.value;
                  }
                  if (specs.type === 'bool') {
                    // item.value = !!this.mqttData.message[i].value && this.mqttData.message[i].value === '1' ? specs.trueText : specs.falseText;
                    if (!!this.mqttData.message[i].value && this.mqttData.message[i].value === '1') {
                      item.value = specs.trueText;
                    }
                    if (this.mqttData.message[i].value === '0') {
                      item.value = specs.falseText;
                    }
                  }
                  item.value = this.getFunHandlingResult(item.value, item.identifier);
                }
              });
              this.variableList = [...this.variableList];
            }
          }
        } else {
          if (bindNum && this.isSameValue(bindNum, mqttNum)) {
            for (let i = 0; i < this.mqttData.message.length; i++) {
              this.variableList.forEach((item) => {
                if (item.id === this.mqttData.message[i].id) {
                  item.value = this.mqttData.message[i].value;
                  const specs = item.specs ? JSON.parse(item.specs) : null;
                  if (specs.type === 'enum') {
                    const enumValue = specs.enumList.find((item) => item.value === this.mqttData.message[i].value);
                    item.value = enumValue?.text ? enumValue.text : item.value;
                  }
                  if (specs.type === 'bool') {
                    // item.value = !!this.mqttData.message[i].value && this.mqttData.message[i].value === '1' ? specs.trueText : specs.falseText;
                    if (!!this.mqttData.message[i].value && this.mqttData.message[i].value === '1') {
                      item.value = specs.trueText;
                    }
                    if (this.mqttData.message[i].value === '0') {
                      item.value = specs.falseText;
                    }
                  }
                  item.value = this.getFunHandlingResult(item.value, item.identifier);
                }
              });
              this.variableList = [...this.variableList];
            }
          }
        }
      }
    },
  },
  mounted() {
    this.$nextTick(async () => {
      if (!this.editMode) {
        await this.getVariableList();
        this.initValue();
      }
    });
  },
  methods: {
    // 查询设备实时数据列表
    getVariableList() {
      return new Promise<void>((resolve) => {
        const guid = getRouteQueryString(this.route.query, 'guid');
        const type = getScadaRouteType(this.route.query);
        const params = {
          scadaGuid: guid,
          type: type,
          page: 1,
          size: 9999,
        };
        getListVariable(params).then((res) => {
          if (res.code == 200) {
            const { identifiers, sceneModelDeviceIds } = this.detail.dataBind;
            if (type === 2) {
              const list = res.rows.map((item) => ({ ...item, id: item.sceneModelDeviceId + '-' + item.identifier }));
              if (identifiers.length !== 0) {
                this.variableList = identifiers.map((item, index) => {
                  const data = list.find((chil) => chil.id === `${sceneModelDeviceIds[index]}-${item}`);
                  return { ...data, value: '----' };
                });
              }
            } else {
              const list = res.rows.map((item) => ({ ...item, id: item.identifier }));
              if (identifiers.length !== 0) {
                this.variableList = identifiers.map((item) => {
                  const data = list.find((chil) => chil.id === item);
                  return { ...data, value: '----' };
                });
              }
            }
            resolve();
          }
        });
      });
    },
    // 获取td显隐
    getTdVisible(name) {
      const { tableColumns } = this.detail.dataBind;
      const obj = tableColumns.find((item) => item.title === name);
      return obj ? true : false;
    },
    // 获取td颜色
    getTdColor(name) {
      const { tableColumns } = this.detail.dataBind;
      const obj = tableColumns.find((item) => item.title === name);
      return obj ? obj.color : null;
    },
    // 设置初始值
    initValue() {
      const { modelValues, modelTimes } = this.detail.dataBind;
      this.variableList.forEach((item, index) => {
        item.value = modelValues[index];
        item.ts = modelTimes[index];
        const specs = item.specs ? JSON.parse(item.specs) : null;
        if (specs?.type && specs.type === 'enum') {
          const enumValue = specs.enumList.find((param) => item.value === param.value);
          item.value = enumValue?.text ? enumValue.text : item.value;
        }
        if (specs?.type && specs.type === 'bool') {
          if (!!item.value && item.value === '1') {
            item.value = specs.trueText;
          }
          if (item.value === '0') {
            item.value = specs.falseText;
          }
        }
        item.value = this.getFunHandlingResult(item.value, item.identifier);
      });
    },
  },
};
</script>

<style lang="scss" scoped>
.view-num-panel {
  width: 100%;
  height: 100%;

  .panel-header {
    padding: 12px 14px;
    box-sizing: border-box;
  }

  .panel-body {
    padding: 14px;

    .table-wrap {
      width: 100%;

      .item-row {
        .item-cell {
          padding: 6px 0;
          min-width: 0;
          box-sizing: border-box;
          vertical-align: middle;
          position: relative;
          overflow: hidden;
          text-overflow: ellipsis;
          white-space: normal;
          word-break: break-all;
          line-height: 23px;
          padding-left: 10px;
          padding-right: 10px;
        }
      }
    }
  }
}
</style>
