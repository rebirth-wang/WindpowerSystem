<template>
  <div
    class="text-display-1"
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
        <div class="tab" v-if="item.isReadonly === 1">
          <img style="width: 16px" src="@/assets/images/drag/components/other/read.png" alt="" />
        </div>
        <div class="value" :style="{ color: datas.setStyle.statusColor }">{{ item.value || '--' }}</div>
      </div>
    </div>
    <!-- 删除组件 -->
    <slot name="deles" />
  </div>
</template>

<script setup lang="ts">
import DragIcon from '@/components/DragEditor/DragIcon.vue';

defineOptions({ name: 'textDisplay1' });
defineProps<{ datas: any }>();
</script>

<style lang="scss" scoped>
.text-display-1 {
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
      padding: 0 20px;
      height: 70px;

      .tab {
        position: absolute;
        top: 0;
        right: 0;
        //background-color: #486ff2;
        //color: #ffffff;
        font-size: 10px;
        display: flex;
        align-items: center;
        justify-content: center;
        height: 20px;
        width: 20px;
        border-radius: 5px;
      }
      .img {
        width: 35px;
        height: 35px;
        border-radius: 50%;
        display: inline-flex;
        flex-direction: column;
        justify-content: center;
        align-items: center;
        background-color: rgb(7, 154, 217);
        color: #ffffff;

        :deep(.el-icon) {
          font-size: 14px;
        }
      }

      .lable {
        margin: 0 10px;
        font-size: 16px;
        line-height: 20px;
        color: #333;
        min-width: 40px;
        max-width: 120px;
        max-height: 40px;
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
      }
    }
  }
}
</style>
