<template>
  <table
    :id="detail.identifier"
    class="usr-table"
    :border="true"
    :style="{
      fontWeight: detail.style.fontWeight ? detail.style.fontWeight.id : 'normal',
      fontStyle: detail.style.fontStyle ? detail.style.fontStyle.id : 'normal',
      fontSize: detail.style.fontSize + 'px',
      fontFamily: detail.style.fontFamily,
      color: detail.style.foreColor,
      borderWidth: detail.style.waterBorderWidth + 'px',
      borderStyle: 'solid',
      borderColor: detail.style.waterBorderColor,
    }"
  >
    <caption v-show="false">
      {{ dataInit }} {{ listInit }}
    </caption>
    <tbody :id="`${detail.identifier}-Tbody`" @dblclick="handleTableDbClick">
      <tr
        v-for="(rItem, rIndex) in rowList"
        :key="rIndex"
        :style="{ borderWidth: detail.style.waterBorderWidth + 'px', borderStyle: 'solid' }"
      >
        <td
          v-for="(cItem, cIndex) in columnList"
          :key="cIndex"
          :style="{ borderWidth: detail.style.waterBorderWidth + 'px', borderStyle: 'solid' }"
        >
          <input
            :id="`${detail.identifier}-${rIndex}-${cIndex}`"
            :style="{ textAlign: detail.style.textAlign }"
            type="text"
            autocomplete="off"
            :value="cellValue(rIndex, cIndex)"
            @input="handleCellInput(rIndex, cIndex, $event)"
            :readOnly="isReadOnly"
          />
        </td>
      </tr>
    </tbody>
  </table>
</template>

<script lang="ts">
import { useTopoEditorStore } from '@/stores/modules/topoEditor';
import { useRoute } from 'vue-router';

import BaseView from '../View.vue';
import { judgeSize, isInCustomRange, isNotInCustomRange, getScadaRouteType, getRouteQueryString } from '@/utils/topo/topoUtil';
export default {
  name: 'UsrTable',
  extends: BaseView,
  setup() {
    const route = useRoute();
    const topoStore = useTopoEditorStore();
    return { route, topoStore };
  },
  data() {
    return {
      isReadOnly: true,
      list: [], // 表格数据
    };
  },
  computed: {
    currentCom() {
      return this.topoStore.selectedComponent;
    },
    isLayer() {
      return this.topoStore.selectedIsLayer;
    },
    mqttData() {
      return this.topoStore?.mqttData || {};
    },
    rowList() {
      const { rowNum } = this.detail.style;
      return new Array(Math.max(1, Number(rowNum) || 1));
    },
    columnList() {
      const { columnNum } = this.detail.style;
      return new Array(Math.max(1, Number(columnNum) || 1));
    },
    listInit() {
      const { rowNum, columnNum } = this.detail.style;
      const safeRowNum = Math.max(1, Number(rowNum) || 1);
      const safeColumnNum = Math.max(1, Number(columnNum) || 1);
      const listTemp = new Array(safeRowNum).fill('').map(() => new Array(safeColumnNum).fill(''));
      const sourceList = this.list?.length ? this.list : this.detail?.dataBind?.tableList || [];
      this.packedArray(listTemp, sourceList);
      return '';
    },
    dataInit() {
      if (Object.keys(this.mqttData).length !== 0) {
        const type = getScadaRouteType(this.route.query);
        let mqttNum = this.mqttData.serialNumber;
        let actionNum = this.detail.dataAction.serialNumber;
        if (type === 1) {
          actionNum = getRouteQueryString(this.route.query, 'serialNumber');
        }
        if (type === 2) {
          if (this.detail.dataAction.variableType && this.detail.dataAction.variableType !== 1) {
            mqttNum = this.mqttData.sceneModelDeviceId;
            actionNum = this.detail.dataAction.sceneModelDeviceId;
          }
        }
        // 动画初始化

        if (
          actionNum &&
          this.isSameValue(actionNum, mqttNum) &&
          this.detail.dataAction.identifier &&
          this.detail.dataAction.paramJudge &&
          (this.detail.dataAction.paramJudgeData ||
            (this.detail.dataAction.paramJudgeDatarangeMin && this.detail.dataAction.paramJudgeDatarangeMax))
        ) {
          for (let i = 0; i < this.mqttData.message.length; i++) {
            if (this.detail.dataAction.identifier == this.mqttData.message[i].id) {
              let val = this.mqttData.message[i].value;
              this.animatePlay(val);
            }
          }
        }
      }
    },
  },
  watch: {
    currentCom: {
      handler(newVal, oldVal) {
        if (newVal && newVal.identifier !== this.detail.identifier) {
          this.isReadOnly = true;
          const usrTable = document.getElementById(this.detail.identifier);
          if (usrTable) {
            usrTable.style.boxShadow = 'none';
            usrTable.style.borderColor = this.detail.style.waterBorderColor;
          }
          this.saveList();
        }
      },
      deep: true,
    },
    isLayer(val) {
      if (val) {
        this.isReadOnly = true;
        const usrTable = document.getElementById(this.detail.identifier);
        if (usrTable) {
          usrTable.style.boxShadow = 'none';
          usrTable.style.borderColor = this.detail.style.waterBorderColor;
        }
        this.saveList();
      }
    },
  },
  mounted() {
    const rowNum = Math.max(1, Number(this.detail.style.rowNum) || 1);
    const columnNum = Math.max(1, Number(this.detail.style.columnNum) || 1);
    const { tableList } = this.detail.dataBind;
    if (tableList && tableList.length !== 0) {
      this.list = tableList;
      this.getList();
    } else {
      this.list = new Array(rowNum).fill('').map((item) => new Array(columnNum).fill(''));
    }
    if (!this.editMode) {
      this.initAnimate();
    }
  },
  beforeUnmount() {
    if (this.editMode) {
      this.saveList();
    }
  },
  methods: {
    setIsDeleteCom(val?: any) { (useTopoEditorStore() as any).setIsDeleteCom?.(val); },
    cellValue(rowIndex, columnIndex) {
      return this.list?.[rowIndex]?.[columnIndex] ?? '';
    },
    ensureCell(rowIndex, columnIndex) {
      if (!Array.isArray(this.list[rowIndex])) {
        this.list[rowIndex] = [];
      }
      if (this.list[rowIndex][columnIndex] === undefined || this.list[rowIndex][columnIndex] === null) {
        this.list[rowIndex][columnIndex] = '';
      }
    },
    syncTableList() {
      if (!this.detail.dataBind) {
        this.detail.dataBind = {};
      }
      this.detail.dataBind.tableList = this.list.map((row) => (Array.isArray(row) ? [...row] : []));
    },
    handleCellInput(rowIndex, columnIndex, event) {
      this.ensureCell(rowIndex, columnIndex);
      const input = event.target as HTMLInputElement;
      this.list[rowIndex][columnIndex] = input.value;
      this.syncTableList();
    },
    // 双击表格
    handleTableDbClick(event) {
      if (this.editMode) {
        this.isReadOnly = false;
        const usrTable = document.getElementById(this.detail.identifier);
        if (usrTable) {
          usrTable.style.borderColor = '#000000';
          usrTable.style.boxShadow = '0 2px 12px 0 rgba(0, 0, 0, .1)';
        }
        event.preventDefault();
      }
    },
    // 保存列表数据
    saveList() {
      const usrTableTbody = document.getElementById(`${this.detail.identifier}-Tbody`);
      if (!usrTableTbody) return;
      const trList = Array.from(usrTableTbody.children);
      for (var i = 0; i < trList.length; i++) {
        if (!Array.isArray(this.list[i])) this.list[i] = [];
        let tdList = Array.from(trList[i].children);
        for (var j = 0; j < tdList.length; j++) {
          const inputElement = tdList[j].querySelector('input') as HTMLInputElement | null;
          this.list[i][j] = inputElement ? inputElement.value : '';
        }
      }
      this.syncTableList();
    },
    // 获取数据列表
    getList() {
      const usrTableTbody = document.getElementById(`${this.detail.identifier}-Tbody`);
      if (!usrTableTbody) return;
      const trList = Array.from(usrTableTbody.children);
      for (var i = 0; i < trList.length; i++) {
        let tdList = Array.from(trList[i].children);
        for (var j = 0; j < tdList.length; j++) {
          const inputElement = tdList[j].querySelector('input') as HTMLInputElement | null;
          if (inputElement) {
            inputElement.value = this.list[i]?.[j] ?? '';
          }
        }
      }
    },
    // 合并数组
    packedArray(newlist, oldList) {
      if (newlist && oldList) {
        for (var i = 0; i < newlist.length; i++) {
          let chilList = newlist[i];
          for (var j = 0; j < chilList.length; j++) {
            let oldRowVal = oldList[i];
            chilList[j] = oldRowVal && oldRowVal[j] !== undefined && oldRowVal[j] !== null ? oldRowVal[j] : '';
          }
        }
        this.list = [...newlist];
      }
    },
    // 动画初始化
    initAnimate() {
      let value = !this.detail.dataAction.modelValue ? 0 : this.detail.dataAction.modelValue;
      this.animatePlay(value);
    },
    // 开始动画
    animatePlay(val) {
      this.applyAnimationState(val);
    },
  },
};
</script>

<style lang="scss" scoped>
.usr-table {
  height: 100%;
  width: 100%;
  border-collapse: collapse;
  border: #b3b3b3;
  font-weight: bold;

  td {
    white-space: normal;
    word-wrap: break-word;
    padding: 0 5px;

    input {
      width: 100%;
      height: calc(100% - 5px);
      background: none;
      border: none;
      padding: 0;
      outline: none;
      font-family: inherit;
    }
  }
}
</style>
