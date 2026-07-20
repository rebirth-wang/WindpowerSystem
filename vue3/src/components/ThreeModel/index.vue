<template>
  <div
    :id="id"
    :style="{
      width: width + 'px',
      height: height + 'px',
    }"
  ></div>
</template>

<script lang="ts">
import { markRaw } from 'vue';
import * as THREE from 'three';
import Stats from 'three/examples/jsm/libs/stats.module.js';
import { GLTFLoader } from 'three/examples/jsm/loaders/GLTFLoader.js';
import { FBXLoader } from 'three/examples/jsm/loaders/FBXLoader.js';
import { OBJLoader } from 'three/examples/jsm/loaders/OBJLoader.js';
import { STLLoader } from 'three/examples/jsm/loaders/STLLoader.js';
import { ColladaLoader } from 'three/examples/jsm/loaders/ColladaLoader.js';
import { TDSLoader } from 'three/examples/jsm/loaders/TDSLoader.js';
import { OrbitControls } from 'three/examples/jsm/controls/OrbitControls.js';

export default {
  name: 'ThreeModel',
  emits: ['load', 'load-error'],
  props: {
    id: { type: String, default: 'three-model' },
    fileType: { type: String, default: 'glb' },
    width: { type: Number, default: 400 },
    height: { type: Number, default: 400 },
    enablePan: { type: Boolean, default: true },
    enableZoom: { type: Boolean, default: true },
    enableRotate: { type: Boolean, default: true },
    enableAnimate: { type: Boolean, default: true },
    enableFPS: { type: Boolean, default: true },
    cameraX: { type: Number, default: 0 },
    cameraY: { type: Number, default: 0 },
    cameraZ: { type: Number, default: 0 },
  },
  data() {
    return {
      scene: null as any,
      camera: null as any,
      renderer: null as any,
      animate: null as any,
      controls: null as any,
      stats: null as any,
      object3D: null as any,
      fileLoaderMap: {
        glb: new GLTFLoader(),
        gltf: new GLTFLoader(),
        fbx: new FBXLoader(),
        obj: new OBJLoader(),
        stl: new STLLoader(),
        dae: new ColladaLoader(),
        '3ds': new TDSLoader(),
      } as Record<string, any>,
    };
  },
  watch: {
    enablePan(newVal: boolean, oldVal: boolean) {
      if (newVal !== oldVal && this.controls) {
        this.controls.enablePan = newVal;
        this.controls.update();
        this.renderFrame();
      }
    },
    enableZoom(newVal: boolean, oldVal: boolean) {
      if (newVal !== oldVal && this.controls) {
        this.controls.enableZoom = newVal;
        this.controls.update();
        this.renderFrame();
      }
    },
    enableRotate(newVal: boolean, oldVal: boolean) {
      if (newVal !== oldVal && this.controls) {
        this.controls.enableRotate = newVal;
        this.controls.update();
        this.renderFrame();
      }
    },
    enableAnimate(newVal: boolean, oldVal: boolean) {
      if (newVal !== oldVal && newVal && !this.animate) {
        this.rotate();
      }
    },
    enableFPS(newVal: boolean, oldVal: boolean) {
      if (newVal !== oldVal && this.stats) {
        this.stats.dom.style.visibility = newVal ? 'visible' : 'hidden';
      }
    },
    cameraX(newVal: number, oldVal: number) {
      if (newVal !== oldVal) this.updateCameraPosition();
    },
    cameraY(newVal: number, oldVal: number) {
      if (newVal !== oldVal) this.updateCameraPosition();
    },
    cameraZ(newVal: number, oldVal: number) {
      if (newVal !== oldVal) this.updateCameraPosition();
    },
    width() {
      this.$nextTick(() => this.resizeRenderer());
    },
    height() {
      this.$nextTick(() => this.resizeRenderer());
    },
  },
  methods: {
    async loadThree(arrayBuffer: ArrayBuffer, fileType?: string) {
      this.resetThree();
      const container = document.getElementById(this.id);
      if (!container) {
        const error = new Error('模型容器初始化失败');
        this.$emit('load-error', error);
        throw error;
      }

      this.clearContainer(container);
      const viewSize = this.getViewSize(container);

      this.scene = markRaw(new THREE.Scene());
      this.camera = markRaw(new THREE.PerspectiveCamera(45, viewSize.width / viewSize.height, 0.01, 10000));
      this.renderer = markRaw(new THREE.WebGLRenderer({ alpha: true, antialias: true }));
      this.renderer.setPixelRatio(window.devicePixelRatio || 1);
      this.renderer.setSize(viewSize.width, viewSize.height);
      this.renderer.setClearColor(0x000000, 0);
      container.appendChild(this.renderer.domElement);

      const ambientLight = new THREE.AmbientLight(0xffffff, 1);
      this.scene.add(ambientLight);

      const pointLight = new THREE.PointLight(0xffffff, 1);
      pointLight.position.set(10, 10, 10);
      this.scene.add(pointLight);

      this.stats = markRaw(new (Stats as any)());
      this.stats.setMode(0);
      this.stats.dom.style.position = 'absolute';
      this.stats.dom.style.left = '0px';
      this.stats.dom.style.top = '0px';
      this.stats.dom.style.visibility = this.enableFPS ? 'visible' : 'hidden';
      container.appendChild(this.stats.dom);

      this.controls = new OrbitControls(this.camera, this.renderer.domElement);
      this.controls.enableDamping = true;
      this.controls.dampingFactor = 0.25;
      this.controls.enableRotate = this.enableRotate;
      this.controls.enableZoom = this.enableZoom;
      this.controls.enablePan = this.enablePan;

      try {
        const type = String(fileType || this.fileType || 'glb').toLowerCase();
        const model = await this.parseModel(arrayBuffer, type);
        if (!model) throw new Error('模型解析失败');

        this.mountModel(model);
        this.rotate();
        this.$emit('load');
      } catch (error: any) {
        console.error('模型加载错误:', error);
        this.$emit('load-error', error);
        throw error;
      }
    },
    parseModel(arrayBuffer: ArrayBuffer, type: string) {
      const loader = this.fileLoaderMap[type];
      if (!loader) return Promise.reject(new Error(`不支持的模型类型: ${type}`));

      if (type === 'glb' || type === 'gltf') {
        return new Promise((resolve, reject) => {
          loader.parse(
            arrayBuffer,
            '',
            (result: any) => resolve(result?.scene),
            (error: any) => reject(error)
          );
        });
      }
      if (type === 'fbx') return Promise.resolve(loader.parse(arrayBuffer, ''));
      if (type === 'obj') return Promise.resolve(loader.parse(this.arrayBufferToText(arrayBuffer)));
      if (type === 'stl') {
        const geometry = loader.parse(arrayBuffer);
        const material = new THREE.MeshStandardMaterial({ color: 0x9aa4b2, metalness: 0.1, roughness: 0.75 });
        return Promise.resolve(new THREE.Mesh(geometry, material));
      }
      if (type === 'dae') {
        const result = loader.parse(this.arrayBufferToText(arrayBuffer), '');
        return Promise.resolve(result?.scene);
      }
      if (type === '3ds') return Promise.resolve(loader.parse(arrayBuffer, ''));

      return Promise.reject(new Error(`不支持的模型类型: ${type}`));
    },
    mountModel(model: any) {
      const box = new THREE.Box3().setFromObject(model);
      const center = box.getCenter(new THREE.Vector3());
      const size = box.getSize(new THREE.Vector3());
      const maxDim = Math.max(size.x, size.y, size.z) || 1;
      const scale = 4 / maxDim;

      model.scale.multiplyScalar(scale);
      model.position.addScaledVector(center, -scale);

      this.object3D = markRaw(new THREE.Object3D());
      this.object3D.add(model);
      this.scene.add(this.object3D);

      this.updateCameraPosition();
      this.controls.target.set(this.object3D.position.x, this.object3D.position.y, this.object3D.position.z);
      this.controls.update();
      this.renderFrame();
    },
    updateCameraPosition() {
      if (!this.camera || !this.object3D) return;
      const cameraZ = this.cameraZ === 0 ? 8 : this.cameraZ;
      this.camera.position.set(this.cameraX, this.cameraY, cameraZ);
      this.camera.lookAt(this.object3D.position);
      this.camera.near = 0.01;
      this.camera.far = Math.max(Math.abs(cameraZ) * 10, 1000);
      this.camera.updateProjectionMatrix();
      this.renderFrame();
    },
    rotate() {
      if (!this.object3D || !this.renderer) return;
      this.animate = requestAnimationFrame(this.rotate);
      if (this.enableAnimate) {
        this.object3D.rotation.y += 0.001;
      }
      this.renderFrame();
    },
    renderFrame() {
      if (!this.renderer || !this.scene || !this.camera) return;
      this.controls?.update();
      this.renderer.render(this.scene, this.camera);
      this.stats?.update();
    },
    resizeRenderer() {
      if (!this.renderer || !this.camera) return;
      const container = document.getElementById(this.id);
      if (!container) return;
      const viewSize = this.getViewSize(container);
      this.renderer.setSize(viewSize.width, viewSize.height);
      this.camera.aspect = viewSize.width / viewSize.height;
      this.camera.updateProjectionMatrix();
      this.renderFrame();
    },
    getViewSize(container: HTMLElement) {
      return {
        width: Math.max(container.offsetWidth || this.width || 1, 1),
        height: Math.max(container.offsetHeight || this.height || 1, 1),
      };
    },
    arrayBufferToText(arrayBuffer: ArrayBuffer) {
      return new TextDecoder('utf-8').decode(new Uint8Array(arrayBuffer));
    },
    resetThree() {
      if (this.animate) {
        cancelAnimationFrame(this.animate);
        this.animate = null;
      }
      if (this.object3D) {
        this.disposeObject(this.object3D);
      }
      if (this.controls) {
        this.controls.dispose();
        this.controls = null;
      }
      if (this.renderer) {
        this.renderer.dispose();
        this.renderer.domElement.remove();
        this.renderer = null;
      }
      if (this.stats?.dom?.parentNode) {
        this.stats.dom.parentNode.removeChild(this.stats.dom);
      }
      if (this.scene) {
        this.scene.clear();
        this.scene = null;
      }
      this.camera = null;
      this.object3D = null;
      this.stats = null;

      const container = document.getElementById(this.id);
      if (container) this.clearContainer(container);
    },
    clearContainer(container: HTMLElement) {
      while (container.firstChild) {
        container.removeChild(container.firstChild);
      }
    },
    disposeObject(object: any) {
      object.traverse?.((child: any) => {
        child.geometry?.dispose?.();
        const materials = Array.isArray(child.material) ? child.material : [child.material];
        materials.forEach((material: any) => {
          if (!material) return;
          Object.keys(material).forEach((key) => {
            const value = material[key];
            if (value && typeof value.dispose === 'function') value.dispose();
          });
          material.dispose?.();
        });
      });
    },
  },
  beforeUnmount() {
    this.resetThree();
  },
};
</script>
