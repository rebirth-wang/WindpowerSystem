<template>
  <div class="real-time-view">
    <el-dialog v-model="datas.show" width="414px" top="8vh" @open="handleOpen" :key="dialogKey">
      <img src="@/assets/images/drag/phone.png" style="width: 375px" />
      <div class="screen">
        <div class="phone-all" ref="phoneAllRef" id="previewPhoneAll">
          <div
            class="phone-container"
            :style="{
              'background-color': dragStore.pageSetup.bgColor,
              backgroundImage: 'url(' + dragStore.pageSetup.bgImg + ')',
            }"
          >
            <div>
              <template v-for="(item, index) in dragStore.pageComponents" :key="index">
                <component
                  v-if="item"
                  :is="item.component"
                  :datas="item"
                  :data-type="item.type"
                  append-to-body
                ></component>
              </template>
            </div>
          </div>
        </div>
      </div>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted, nextTick } from 'vue';
import { useDragEditorStore } from '@/stores/modules/dragEditor';

const dragStore = useDragEditorStore();

defineProps<{ datas: any; val: any }>();

const phoneAllRef = ref<any>(null);
const dialogKey = ref(0);

function handleOpen() {
  nextTick(() => {
    dialogKey.value++;
  });
}

onMounted(() => {
  // Drag and zoom initialization deferred until dialog opens
});
</script>

<style lang="scss" scoped>
.real-time-view {
  .screen {
    background-color: #f1f3f9;
    width: 375px;
    height: 600px;
    border: 0;
    overflow-y: auto;
    margin-top: -10px;
    padding: 10px;
    &::-webkit-scrollbar {
      display: none;
    }
  }
  .phone-container {
    flex: 1;
    box-sizing: border-box;
    cursor: pointer;
    width: 100%;
    min-height: 600px;
    position: relative;
    background-repeat: no-repeat;
    background-size: cover;
    background-position: center center;
  }
}
</style>
