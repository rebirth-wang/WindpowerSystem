<template>
  <div class="panel-wrap" v-if="datas.items">
    <el-menu v-if="datas.items[0].comList" unique-opened :default-openeds="['1', '2', '3']">
      <el-sub-menu v-for="(items, index) in datas.items" :index="String(Number(index) + 1)" :key="index">
        <template #title>
          <span>{{ items.title }}</span>
        </template>
        <div class="compon-list">
          <div
            class="double-item-wrap"
            v-for="(item, ind) in items.comList"
            :key="ind"
            :data-name="item.name"
            draggable="true"
            @dragstart="handleDragstart($event)"
            @dragend="handleDragends"
          >
            <el-icon v-if="item.icon" class="iconfont"><item.icon /></el-icon>
            <p>{{ item.text }}</p>
          </div>
        </div>
      </el-sub-menu>
    </el-menu>
    <div v-else class="compon-list">
      <div
        class="single-item-wrap"
        v-for="(item, ind) in datas.items"
        :key="ind"
        :data-name="item.name"
        draggable="true"
        @dragstart="handleDragstart($event)"
        @dragend="handleDragends()"
      >
        <img :src="getImageUrl(item.img)" height="100%" width="100%" />
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
const props = defineProps<{
  pointer: any;
  datas: any;
}>();

const dragImages = import.meta.glob('@/assets/images/drag/**/*', { eager: true, import: 'default' }) as Record<
  string,
  string
>;

function getImageUrl(img: string) {
  const key = Object.keys(dragImages).find((k) => k.endsWith(img));
  return key ? dragImages[key] : '';
}

function handleDragstart(event: DragEvent) {
  props.pointer.show = true;
  const currentTarget = event.currentTarget as HTMLElement | null;
  const componentName = currentTarget?.dataset.name || '';
  event.dataTransfer?.setData('componentName', componentName);
  event.dataTransfer?.setData('text/plain', componentName);
}

function handleDragends() {
  props.pointer.show = false;
}
</script>

<style lang="scss" scoped>
.panel-wrap {
  width: 100%;
  height: 100%;
  overflow-y: scroll;
  box-sizing: border-box;
  background: #fff;

  &::-webkit-scrollbar-thumb {
    background-color: #e1e1e1;
  }
  &::-webkit-scrollbar-thumb:hover {
    background-color: #a5a2a2;
  }
  &::-webkit-scrollbar {
    width: 3px;
    height: 3px;
    position: absolute;
  }
  &::-webkit-scrollbar-track {
    background-color: #fff;
  }

  :deep(.el-menu) {
    border-right: solid 1px transparent;
    .el-sub-menu__title {
      height: 46px;
      line-height: 46px;
      font-size: 13px;
    }
  }

  .compon-list {
    padding: 10px;
    .double-item-wrap {
      display: inline-flex;
      flex-direction: column;
      justify-content: center;
      width: 50%;
      height: 88px;
      margin-bottom: 8px;
      align-items: center;
      cursor: all-scroll;
      transition: all 0.3s;
      &:hover {
        background: #155bd4;
        border-radius: 2px;
        font-weight: 700;
        i,
        p,
        span {
          color: #fff;
        }
      }
      i {
        font-size: 32px;
        width: 32px;
        height: 32px;
        line-height: 32px;
        color: #b0a8a8;
        margin-top: 4px;
      }
      p {
        font-size: 12px;
        color: #323233;
        margin-top: 4px;
      }
      span {
        color: #7d7e80;
        margin-top: 4px;
        font-size: 10px;
      }
    }
    .single-item-wrap {
      width: 100%;
      margin-bottom: 10px;
      border: 1px solid #eee;
      border-radius: 5px;
      position: relative;
      display: inline-flex;
      flex-direction: column;
      justify-content: center;
      padding: 2px;
      cursor: all-scroll;
      transition: all 0.3s;
      img {
        -webkit-user-drag: none;
        background-size: contain;
        background-repeat: no-repeat;
      }
      &:hover {
        border: 1px solid #155bd4;
      }
    }
  }
}
</style>
