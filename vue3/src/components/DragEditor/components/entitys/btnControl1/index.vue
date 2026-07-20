<template>
  <div
    class="btn-control-1"
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
        <div class="lable" :style="{ color: datas.setStyle.nameColor }">{{ item.templateName }}</div>
        <div class="tab" v-if="item.isReadonly === 1">
          <img style="width: 16px" src="@/assets/images/drag/components/other/read.png" alt="" />
        </div>
        <div class="value">
          <el-switch
            v-if="datas.setStyle.model === 1"
            v-model="value1"
            :active-color="datas.setStyle.btnColor"
          ></el-switch>
          <div
            v-else
            class="btn"
            :style="{
              color: datas.setStyle.btnColor,
              borderColor: datas.setStyle.btnColor,
              background: getBtnBackgroundColor(datas.setStyle.btnColor),
            }"
          >
            按钮
          </div>
        </div>
      </div>
    </div>
    <!-- 删除组件 -->
    <slot name="deles" />
  </div>
</template>

<script setup lang="ts">
import { ref } from 'vue';
import utils from '@/utils/index';
defineOptions({ name: 'btnControl1' });
defineProps<{ datas: any }>();

const value1 = ref(false);

function getBtnBackgroundColor(str: string) {
  const obj = utils.getRGBAValues(str);
  return `rgba(${obj.r}, ${obj.g}, ${obj.b}, 0.1)`;
}
</script>

<style lang="scss" scoped>
.btn-control-1 {
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
      height: 70px;
      display: flex;
      flex-direction: row;
      justify-content: space-between;
      align-items: center;
      padding: 0 20px 0 20px;
      .tab {
        position: absolute;
        top: 0;
        right: 0;
        //color: #ffffff;
        font-size: 10px;
        display: flex;
        align-items: center;
        justify-content: center;
        height: 15px;
        width: 20px;
        padding: 2px;
        border-radius: 5px;
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
        font-size: 16px;
        line-height: 20px;
        color: #444;
        text-align: right;
        min-width: 100px;
        min-height: 20px;
        max-height: 40px;
        overflow: hidden;
      }

      .btn {
        display: inline-block;
        line-height: 1;
        cursor: pointer;
        color: rgb(96, 98, 102);
        appearance: none;
        text-align: center;
        box-sizing: border-box;
        font-weight: 500;
        user-select: none;
        font-size: 14px;
        white-space: nowrap;
        background: rgb(255, 255, 255);
        border-width: 1px;
        border-style: solid;
        border-color: rgb(96, 98, 10);
        border-image: initial;
        outline: none;
        margin: 0px;
        transition: 0.1s;
        padding: 7px 20px;
        border-radius: 4px;
      }
    }
  }
}
</style>
