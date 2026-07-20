<template>
  <div :id="wrapperId" class="view-3d-model" :style="wrapStyle" v-loading="loading">
    <three-model
      v-if="hasModel"
      :ref="modelRefName"
      :id="modelDomId"
      :fileType="modelFileType"
      :width="modelWidth"
      :height="modelHeight"
      :enablePan="enablePan"
      :enableZoom="enableZoom"
      :enableRotate="enableRotate"
      :enableAnimate="enableAnimate"
      :enableFPS="enableFPS"
      :cameraX="cameraX"
      :cameraY="cameraY"
      :cameraZ="cameraZ"
      @load-error="handleLoadError"
    ></three-model>
    <div v-if="statusText" class="view-3d-model__placeholder">{{ statusText }}</div>
  </div>
</template>

<script lang="ts">
import { downloadModel } from '@/api/scada/model';
import ThreeModel from '@/components/ThreeModel/index.vue';
import { blobToArrayBuffer } from '@/utils/topo/index';
import BaseView from '../View.vue';

export default {
  name: 'View3DModel',
  components: { ThreeModel },
  extends: BaseView,
  props: {},
  data() {
    return {
      loading: false,
      loadError: '',
    };
  },
  computed: {
    dataBind() {
      return this.detail?.dataBind || {};
    },
    wrapperId() {
      return this.detail?.identifier || '';
    },
    modelDomId() {
      return `${this.wrapperId}-three-model`;
    },
    modelRefName() {
      return `3DModel-${this.wrapperId}`;
    },
    modelWidth() {
      return Number(this.detail?.style?.position?.w) || 0;
    },
    modelHeight() {
      return Number(this.detail?.style?.position?.h) || 0;
    },
    wrapStyle() {
      return {
        width: this.modelWidth + 'px',
        height: this.modelHeight + 'px',
        backgroundColor: this.detail?.style?.backColor,
      };
    },
    hasModel() {
      return !!this.dataBind.model3dUrl;
    },
    modelFileType() {
      const type = this.dataBind.model3dType || this.getFileType(this.dataBind.model3dUrl);
      return String(type || 'glb').toLowerCase();
    },
    orbitControls() {
      return Array.isArray(this.dataBind.orbitControls) ? this.dataBind.orbitControls : [];
    },
    enablePan() {
      return this.orbitControls.includes(1);
    },
    enableZoom() {
      return this.orbitControls.includes(2);
    },
    enableRotate() {
      return this.orbitControls.includes(3);
    },
    enableAnimate() {
      return this.orbitControls.includes(4);
    },
    enableFPS() {
      return this.orbitControls.includes(5);
    },
    cameraX() {
      return Number(this.dataBind.cameraX) || 0;
    },
    cameraY() {
      return Number(this.dataBind.cameraY) || 0;
    },
    cameraZ() {
      return Number(this.dataBind.cameraZ) || 0;
    },
    statusText() {
      if (!this.hasModel) return '请先上传三维模型';
      return this.loadError;
    },
  },
  watch: {
    'detail.dataBind.model3dUrl'() {
      this.loadModel();
    },
    'detail.dataBind.model3dType'() {
      this.loadModel();
    },
  },
  mounted() {
    this.loadModel();
  },
  methods: {
    async loadModel() {
      this.loadError = '';
      if (!this.hasModel) {
        this.loading = false;
        return;
      }
      this.loading = true;
      try {
        const blob = await downloadModel(this.dataBind.model3dUrl);
        const arrayBuffer = (await blobToArrayBuffer(blob)) as ArrayBuffer;
        await this.$nextTick();
        const modelRef = this.$refs[this.modelRefName] as any;
        if (!modelRef || typeof modelRef.loadThree !== 'function') {
          throw new Error('模型容器初始化失败');
        }
        await modelRef.loadThree(arrayBuffer, this.modelFileType);
      } catch (err) {
        this.handleLoadError(err);
      } finally {
        this.loading = false;
      }
    },
    handleLoadError(err: any) {
      console.warn('3D model load failed', err);
      this.loadError = err?.message || '模型加载失败';
    },
    getFileType(url: string) {
      if (!url) return '';
      const cleanUrl = url.split('?')[0].split('#')[0];
      return cleanUrl.includes('.') ? cleanUrl.split('.').pop() : '';
    },
  },
};
</script>

<style lang="scss" scoped>
.view-3d-model {
  position: relative;
  overflow: hidden;
}

.view-3d-model__placeholder {
  position: absolute;
  inset: 0;
  display: flex;
  align-items: center;
  justify-content: center;
  padding: 12px;
  color: #606266;
  font-size: 14px;
  text-align: center;
  pointer-events: none;
}
</style>
