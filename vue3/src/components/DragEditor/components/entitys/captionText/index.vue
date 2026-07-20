<template>
  <div
    class="caption-text"
    :style="{
      background: datas.setStyle.backColor,
      marginTop: datas.setStyle.marginTop + 'px',
      marginBottom: datas.setStyle.marginBottom + 'px',
    }"
  >
    <div
      style="padding: 6px 0"
      :style="{
        'border-bottom': datas.setStyle.borderBott ? '1px solid #F9F9F9' : '',
      }"
    >
      <!-- 标题 -->
      <h2
        :style="{
          'font-size': datas.setStyle.wordSize + 'px',
          'font-weight': datas.setStyle.wordWeight,
          color: datas.setStyle.wordColor,
          'text-align': datas.setStyle.positions,
          height: datas.setStyle.wordHeight + 'px',
          'line-height': datas.setStyle.wordHeight + 'px',
        }"
        v-if="datas.setStyle.name"
      >
        {{ datas.setStyle.name }}
      </h2>
      <!-- 描述文字 -->
      <p
        :style="{
          'font-size': datas.setStyle.descriptionSize + 'px',
          'font-weight': datas.setStyle.descriptionWeight,
          color: datas.setStyle.descriptionColor,
          'text-align': datas.setStyle.positions,
        }"
        style="margin-top: 8px"
        v-if="datas.setStyle.description"
      >
        {{ datas.setStyle.description }}
      </p>
    </div>
    <!-- 删除组件 -->
    <slot name="deles" />
  </div>
</template>

<script setup lang="ts">
defineOptions({ name: 'CaptionText' });
defineProps<{ datas: any }>();
</script>

<style lang="scss" scoped>
.caption-text {
  box-sizing: border-box;
  width: 100%;
  padding: 0 14px;
  min-height: 20px;
  position: relative;

  h2,
  p {
    word-wrap: break-word;
    min-height: 10px;
    margin: 0;
  }

  /* 更多 */
  .more {
    font-size: 10px;
    color: #969799;
    text-align: center;
    &.lef {
      position: absolute;
      right: 15px;
      top: 12px;
    }
  }
}
</style>
