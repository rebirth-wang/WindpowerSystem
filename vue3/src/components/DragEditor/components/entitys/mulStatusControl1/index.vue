<template>
  <div
    class="mul-status-control-1"
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
        <div class="name" :style="{ color: datas.setStyle.nameColor }">{{ item.templateName }}</div>
        <div class="tab" v-if="item.isReadonly === 1">
          <img style="width: 16px" src="@/assets/images/drag/components/other/read.png" alt="" />
        </div>
        <div
          class="grid-container"
          :style="{ gridTemplateColumns: `repeat(${datas.setStyle.rowNum}, minmax(0, 1fr))` }"
        >
          <div class="grid-col" v-for="child in item.enumList" :key="child.value">
            <div
              class="switch-state"
              :style="{
                fontSize: datas.setStyle.fontSize + 'px',
                height: datas.setStyle.figureHeight + 'px',
                borderRadius: datas.setStyle.borderRadius + 'px',
                color: item.value ? datas.setStyle.noColor : datas.setStyle.offColor,
                background: item.value ? datas.setStyle.noBackColor : datas.setStyle.offBackColor,
              }"
            >
              {{ child.text }}
            </div>
          </div>
        </div>
      </div>
    </div>
    <!-- 删除组件 -->
    <slot name="deles" />
  </div>
</template>

<script setup lang="ts">
defineOptions({ name: 'mulStatusControl1' });
defineProps<{ datas: any }>();
</script>

<style lang="scss" scoped>
.mul-status-control-1 {
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
      display: flex;
      align-items: center;
      flex-direction: row;
      padding: 15px 20px;

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

      .name {
        margin: 5px 10px 5px 3px;
        font-size: 16px;
        line-height: 20px;
        color: #333;
        min-width: 34px;
        max-width: 120px;
        overflow: hidden;
      }

      .grid-container {
        flex: 1;
        display: grid;
        grid-template-columns: repeat(4, minmax(0, 1fr));
        grid-gap: 16px;
        margin: 0;

        .grid-col {
          .switch-state {
            display: flex;
            justify-content: center;
            align-items: center;
            overflow: hidden;
            text-overflow: ellipsis;
            white-space: nowrap;
            color: rgb(51, 51, 51);
            background-color: #eee;
            height: 30px;
            font-size: 14px;
            border-radius: 15px;
          }
        }
      }
    }
  }
}
</style>
