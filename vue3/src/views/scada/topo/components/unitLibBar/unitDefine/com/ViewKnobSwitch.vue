<template>
  <div class="view-knob-switch" :class="{ 'view-knob-switch--edit': editMode }" :style="wrapStyle" @dragstart.prevent>
    <div
      class="view-knob-switch__control"
      :style="knobStyle"
      :tabindex="editMode ? -1 : 0"
      role="slider"
      aria-valuemin="-2"
      aria-valuemax="2"
      :aria-valuenow="currentValue"
      :aria-valuetext="displayText"
      @pointerdown="handlePointerDown"
      @pointermove="handlePointerMove"
      @pointerup="handlePointerEnd"
      @pointercancel="handlePointerEnd"
      @keydown="handleKeydown"
    >
      <svg class="view-knob-switch__svg" viewBox="0 0 100 100" aria-hidden="true">
        <circle class="view-knob-switch__outer" cx="50" cy="50" r="42" />
        <circle class="view-knob-switch__track" cx="50" cy="50" r="34" />
        <g v-for="value in knobValues" :key="value" :transform="`rotate(${getAngle(value)} 50 50)`">
          <line class="view-knob-switch__tick" x1="50" y1="11" x2="50" y2="17" />
        </g>
        <g class="view-knob-switch__pointer" :transform="`rotate(${knobAngle} 50 50)`">
          <line x1="50" y1="50" x2="50" y2="20" />
          <circle cx="50" cy="20" r="4" />
        </g>
        <circle class="view-knob-switch__center" cx="50" cy="50" r="23" />
        <text class="view-knob-switch__value" x="50" y="47">{{ currentValue }}</text>
        <text class="view-knob-switch__label" x="50" y="61">{{ displayText }}</text>
      </svg>
    </div>
    <div v-show="false">{{ dataInit }}</div>
  </div>
</template>

<script lang="ts">
import { useRoute } from 'vue-router';
import { useTopoEditorStore } from '@/stores/modules/topoEditor';
import { getScadaRouteType, getRouteQueryString } from '@/utils/topo/topoUtil';

import BaseView from '../View.vue';

const KNOB_MIN = -2;
const KNOB_MAX = 2;
const KNOB_MIN_ANGLE = -135;
const KNOB_MAX_ANGLE = 135;

export default {
  name: 'view-knob-switch',
  extends: BaseView,
  setup() {
    const route = useRoute();
    const topoStore = useTopoEditorStore();
    return { route, topoStore };
  },
  data() {
    return {
      currentValue: 0,
      dragging: false,
      knobValues: [-2, -1, 0, 1, 2],
    };
  },
  computed: {
    mqttData() {
      return this.topoStore?.mqttData || {};
    },
    wrapStyle() {
      const position = this.detail?.style?.position || {};
      return {
        width: `${Math.max(1, Number(position.w) || 50)}px`,
        height: `${Math.max(1, Number(position.h) || 50)}px`,
        background: this.getValidColor(this.detail?.style?.backColor, 'transparent'),
      };
    },
    knobSize() {
      const position = this.detail?.style?.position || {};
      const width = Math.max(1, Number(position.w) || 50);
      const height = Math.max(1, Number(position.h) || 50);
      return Math.max(24, Math.min(width, height));
    },
    knobStyle() {
      return {
        width: `${this.knobSize}px`,
        height: `${this.knobSize}px`,
        '--knob-primary': this.getValidColor(this.detail?.style?.foreColor, '#e844c3'),
        '--knob-secondary': this.getSecondaryColor(),
        '--knob-text-size': `${this.clamp(Math.round(this.knobSize / 7.5), 7, 18)}px`,
        '--knob-value-size': `${this.clamp(Math.round(this.knobSize / 4.5), 10, 28)}px`,
        '--knob-stroke': `${this.clamp(Math.round(this.knobSize / 14), 3, 8)}px`,
      };
    },
    knobAngle() {
      return this.getAngle(this.currentValue);
    },
    displayText() {
      const map = {
        '-2': '倒二档',
        '-1': '倒一档',
        0: '空挡',
        1: '一档',
        2: '二档',
      };
      return map[this.currentValue] || '空挡';
    },
    dataInit() {
      this.applyMqttValue();
      return this.currentValue;
    },
  },
  watch: {
    'detail.dataBind.modelValue'(value) {
      this.setValue(value, false);
    },
  },
  mounted() {
    this.ensureDataBind();
    this.setValue(this.detail?.dataBind?.modelValue, false);
  },
  beforeUnmount() {
    this.dragging = false;
  },
  methods: {
    ensureDataBind() {
      if (!this.detail.dataBind) {
        this.detail.dataBind = {};
      }
      if (this.detail.dataBind.modelValue === undefined || this.detail.dataBind.modelValue === null) {
        this.detail.dataBind.modelValue = this.currentValue;
      }
    },
    normalizeValue(value) {
      const labelMap = {
        倒二档: -2,
        倒一档: -1,
        空挡: 0,
        一档: 1,
        二档: 2,
      };
      const mappedValue = labelMap[value] !== undefined ? labelMap[value] : value;
      const number = Number(mappedValue);
      if (!Number.isFinite(number)) return 0;
      return this.clamp(Math.round(number), KNOB_MIN, KNOB_MAX);
    },
    setValue(value, syncDataBind = true) {
      const nextValue = this.normalizeValue(value);
      if (this.currentValue === nextValue) {
        if (syncDataBind) {
          this.ensureDataBind();
          if (this.detail.dataBind.modelValue !== nextValue) {
            this.detail.dataBind.modelValue = nextValue;
          }
        }
        return;
      }
      this.currentValue = nextValue;
      if (syncDataBind) {
        this.ensureDataBind();
        this.detail.dataBind.modelValue = nextValue;
      }
    },
    applyMqttValue() {
      const mqttData = this.mqttData || {};
      if (Object.keys(mqttData).length === 0) return;
      const dataBind = this.detail?.dataBind || {};
      const type = getScadaRouteType(this.route.query);
      let bindNum = dataBind.serialNumber;
      let mqttNum = mqttData.serialNumber;
      if (type === 1) {
        bindNum = getRouteQueryString(this.route.query, 'serialNumber');
      }
      if (type === 2 && dataBind.variableType && dataBind.variableType !== 1) {
        bindNum = dataBind.sceneModelDeviceId;
        mqttNum = mqttData.sceneModelDeviceId;
      }
      const messages = Array.isArray(mqttData.message) ? mqttData.message : [];
      if (!bindNum || !this.isSameValue(bindNum, mqttNum) || !dataBind.identifier) return;
      const message = messages.find((item) => dataBind.identifier == item.id);
      if (!message) return;
      const value = this.getFunHandlingResult(message.value);
      this.setValue(value);
    },
    getAngle(value) {
      const percent = (this.normalizeValue(value) - KNOB_MIN) / (KNOB_MAX - KNOB_MIN);
      return KNOB_MIN_ANGLE + percent * (KNOB_MAX_ANGLE - KNOB_MIN_ANGLE);
    },
    getValueByPointer(event) {
      const rect = event.currentTarget.getBoundingClientRect();
      const centerX = rect.left + rect.width / 2;
      const centerY = rect.top + rect.height / 2;
      const angle = Math.atan2(event.clientY - centerY, event.clientX - centerX) * (180 / Math.PI) + 90;
      const normalizedAngle = angle > 180 ? angle - 360 : angle;
      const clampedAngle = this.clamp(normalizedAngle, KNOB_MIN_ANGLE, KNOB_MAX_ANGLE);
      const percent = (clampedAngle - KNOB_MIN_ANGLE) / (KNOB_MAX_ANGLE - KNOB_MIN_ANGLE);
      return KNOB_MIN + percent * (KNOB_MAX - KNOB_MIN);
    },
    updateValueByPointer(event) {
      this.setValue(this.getValueByPointer(event));
    },
    handlePointerDown(event) {
      if (this.editMode) return;
      event.preventDefault();
      this.dragging = true;
      event.currentTarget.setPointerCapture?.(event.pointerId);
      this.updateValueByPointer(event);
    },
    handlePointerMove(event) {
      if (!this.dragging || this.editMode) return;
      event.preventDefault();
      this.updateValueByPointer(event);
    },
    handlePointerEnd(event) {
      if (!this.dragging) return;
      this.dragging = false;
      event.currentTarget.releasePointerCapture?.(event.pointerId);
    },
    handleKeydown(event) {
      if (this.editMode) return;
      if (event.key === 'ArrowRight' || event.key === 'ArrowUp') {
        event.preventDefault();
        this.setValue(this.currentValue + 1);
      }
      if (event.key === 'ArrowLeft' || event.key === 'ArrowDown') {
        event.preventDefault();
        this.setValue(this.currentValue - 1);
      }
    },
    getValidColor(color, fallback) {
      if (!color || color === 'transparent') return fallback;
      if (/rgba\([^)]*,\s*0(?:\.0+)?\s*\)$/i.test(color)) return fallback;
      return color;
    },
    getSecondaryColor() {
      const color = this.getValidColor(this.detail?.style?.foreColor, '');
      return color ? color : 'rgba(232, 68, 195, 0.22)';
    },
    clamp(value, min, max) {
      return Math.min(Math.max(value, min), max);
    },
  },
};
</script>

<style lang="scss" scoped>
.view-knob-switch {
  box-sizing: border-box;
  display: flex;
  align-items: center;
  justify-content: center;
  overflow: hidden;
  user-select: none;
  touch-action: none;
}

.view-knob-switch__control {
  display: flex;
  align-items: center;
  justify-content: center;
  cursor: pointer;
  outline: none;
  touch-action: none;
}

.view-knob-switch--edit .view-knob-switch__control {
  cursor: default;
  pointer-events: none;
}

.view-knob-switch__svg {
  width: 100%;
  height: 100%;
  display: block;
}

.view-knob-switch__outer {
  fill: rgba(255, 255, 255, 0.92);
  stroke: var(--knob-secondary);
  stroke-width: var(--knob-stroke);
}

.view-knob-switch__track {
  fill: rgba(255, 255, 255, 0.72);
  stroke: rgba(0, 0, 0, 0.08);
  stroke-width: 1;
}

.view-knob-switch__tick {
  stroke: var(--knob-primary);
  stroke-width: 3;
  stroke-linecap: round;
  opacity: 0.5;
}

.view-knob-switch__pointer {
  fill: var(--knob-primary);
  stroke: var(--knob-primary);
  stroke-linecap: round;
  stroke-width: 5;
  transition: transform 0.12s ease;
}

.view-knob-switch__center {
  fill: #fff;
  stroke: var(--knob-primary);
  stroke-width: 1.5;
}

.view-knob-switch__value,
.view-knob-switch__label {
  fill: var(--knob-primary);
  dominant-baseline: middle;
  font-family: Arial, 'Microsoft YaHei', sans-serif;
  font-weight: 600;
  pointer-events: none;
  text-anchor: middle;
}

.view-knob-switch__value {
  font-size: var(--knob-value-size);
}

.view-knob-switch__label {
  font-size: var(--knob-text-size);
}
</style>
