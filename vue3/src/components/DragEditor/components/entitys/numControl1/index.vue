<template>
  <div
    class="num-control-1"
    :style="{
      background: datas.setStyle.backColor,
      marginTop: datas.setStyle.marginTop + 'px',
      marginBottom: datas.setStyle.marginBottom + 'px',
    }"
  >
    <div class="no-data" v-if="datas.setConfig.attributes.length === 0">
      <span>{{ $t('dragEditor.565720-29') }}</span>
    </div>
    <div class="content" v-else>
      <div class="panel" v-for="item in datas.setConfig.attributes" :key="item.templateId">
        <div class="img" :style="{ backgroundColor: datas.setStyle.iconBackColor }" v-if="datas.setStyle.showIcon">
          <drag-icon :name="item.icon" :color="datas.setStyle.iconColor" />
        </div>
        <div class="lable" :style="{ color: datas.setStyle.nameColor }">{{ item.templateName }}</div>
        <div class="value" :style="{ color: datas.setStyle.numColor }">
          <span class="num">{{ item.value || '--' }}</span>
          <span class="unit">{{ item.unit }}</span>
        </div>
      </div>
    </div>
    <!-- 删除组件 -->
    <slot name="deles" />
    <!-- 服务下发 -->
    <el-dialog v-model="isShow" title="下发指令" append-to-body width="480px">
      <el-form>
        <el-form-item :label="currItem.templateName" label-width="120px">
          <el-input
            v-model="currItem.value"
            :precision="0"
            :controls="false"
            type="number"
            style="width: 200px"
          ></el-input>
          <span v-if="currItem.min && currItem.max" style="margin-left: 5px">
            （{{ currItem.min }} ~ {{ currItem.max }}）
          </span>
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="isShow = false">取消</el-button>
        <el-button type="primary">确认</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref } from 'vue';
import DragIcon from '@/components/DragEditor/DragIcon.vue';

defineOptions({ name: 'numControl1' });
defineProps<{ datas: any }>();

const isShow = ref(false);
const currItem = ref<any>({});

function handleItemClick(item: any) {
  currItem.value = item;
  isShow.value = true;
}
</script>

<style lang="scss" scoped>
.num-control-1 {
  position: relative;

  .no-data {
    padding: 20px;
    margin: 12px 20px;
    border: 2px dotted #ddd;
    border-radius: 5px;
    text-align: center;
    font-size: 14px;
    line-height: 20px;
    color: #999;
  }

  .content {
    .panel {
      position: relative;
      display: flex;
      flex-direction: row;
      justify-content: space-between;
      align-items: center;
      padding: 0 20px 0 20px;
      height: 70px;

      .img {
        width: 35px;
        height: 35px;
        border-radius: 50%;
        display: inline-flex;
        flex-direction: column;
        justify-content: center;
        align-items: center;
        color: #fff;
        background-color: rgb(255, 153, 0);
      }

      .lable {
        margin: 0 10px;
        font-size: 16px;
        line-height: 20px;
        color: #333;
        max-height: 40px;
        min-width: 40px;
        max-width: 120px;
        overflow: hidden;
      }

      .value {
        flex: 1;
        font-size: 20px;
        line-height: 20px;
        color: #444;
        text-align: right;
        min-width: 100px;
        min-height: 20px;
        max-height: 40px;
        overflow: hidden;

        .unit {
          margin: 0 0 0 4px;
          font-size: 14px;
          line-height: 40px;
        }
      }
    }
  }
}
</style>
