<template>
  <div class="view-select" :id="detail.identifier" :style="wrapStyle">
    <div v-if="editMode" class="select-editor-preview" :style="selectStyle">
      <span class="select-editor-text">{{ placeholderText }}</span>
      <span class="select-editor-arrow"></span>
    </div>
    <template v-else>
      <el-select
        :style="selectStyle"
        class="select-wrap"
        :model-value="selectModelValue"
        placeholder=""
        @update:model-value="handleValueUpdate"
        @change="handleSelectChange"
      >
        <el-option v-for="item in options" :key="item.value" :label="item.label" :value="item.value"></el-option>
      </el-select>
      <span class="select-runtime-text" :style="placeholderStyle">
        {{ runtimeDisplayText }}
      </span>
    </template>
  </div>
</template>

<script lang="ts">
import BaseView from '../View.vue';

export default {
  name: 'ViewSelect',
  extends: BaseView,
  computed: {
    wrapStyle() {
      const p = this.detail?.style?.position || {};
      return {
        width: `${Math.max(1, Number(p.w) || 200)}px`,
        height: `${Math.max(1, Number(p.h) || 36)}px`,
      };
    },
    styleBorder() {
      const { borderWidth, borderColor } = this.detail?.style || {};
      if (borderWidth && borderColor) {
        return `${borderWidth}px solid ${borderColor}`;
      } else {
        return null;
      }
    },
    placeholderText() {
      const text = this.detail?.style?.text;
      return text === undefined || text === null || String(text) === '' ? this.$t('pleaseSelect') : String(text);
    },
    selectModelValue() {
      return this.editMode ? '' : this.value;
    },
    hasSelectValue() {
      const value = this.selectModelValue;
      return value !== undefined && value !== null && String(value) !== '';
    },
    runtimeDisplayText() {
      if (!this.hasSelectValue) {
        return this.placeholderText;
      }
      const value = String(this.selectModelValue);
      const option = this.options.find((item) => String(item.value) === value);
      return option ? option.label : value;
    },
    placeholderStyle() {
      const style = this.detail?.style || {};
      const h = Math.max(1, Number(style.position?.h) || 36);
      return {
        color: style.foreColor || '#303133',
        fontSize: `${Number(style.fontSize) || 14}px`,
        fontFamily: style.fontFamily || 'Arial',
        fontWeight: style.fontWeight || 'normal',
        fontStyle: style.fontStyle || 'normal',
        lineHeight: `${h}px`,
        '--select-color': style.foreColor || '#303133',
        '--select-align': style.textAlign || 'left',
        '--select-font-size': `${Number(style.fontSize) || 14}px`,
        '--select-font-family': style.fontFamily || 'Arial',
        '--select-font-weight': style.fontWeight || 'normal',
        '--select-font-style': style.fontStyle || 'normal',
      };
    },
    selectStyle() {
      const style = this.detail?.style || {};
      const h = Math.max(1, Number(style.position?.h) || 36);
      return {
        width: '100%',
        height: '100%',
        fontSize: `${Number(style.fontSize) || 14}px`,
        fontFamily: style.fontFamily || 'Arial',
        fontWeight: style.fontWeight || 'normal',
        fontStyle: style.fontStyle || 'normal',
        color: style.foreColor || '#303133',
        lineHeight: `${h}px`,
        '--select-bg': style.backColor || '#fff',
        '--select-radius': `${Number(style.borderRadius) || 0}px`,
        '--select-color': style.foreColor || '#303133',
        '--select-align': style.textAlign || 'left',
        '--select-border': this.styleBorder || 'none',
        '--select-font-size': `${Number(style.fontSize) || 14}px`,
        '--select-font-family': style.fontFamily || 'Arial',
        '--select-font-weight': style.fontWeight || 'normal',
        '--select-font-style': style.fontStyle || 'normal',
      };
    },
  },
  data() {
    return {
      options: [],
      value: '',
    };
  },
  mounted() {
    this.refreshOptions();
    this.value = this.normalizeValue(this.detail?.dataBind?.modelValue);
  },
  watch: {
    'detail.dataBind.staticValue'() {
      this.refreshOptions();
    },
    'detail.dataBind.modelValue'(val) {
      this.value = this.normalizeValue(val);
    },
  },
  methods: {
    normalizeValue(value) {
      return value === undefined || value === null ? '' : String(value);
    },
    normalizeOptions(data) {
      const list = Array.isArray(data) ? data : [];
      return list
        .map((item) => {
          if (item && typeof item === 'object') {
            const value = item.value ?? item.label ?? item.name ?? '';
            const label = item.label ?? item.name ?? value;
            return { label: String(label), value: String(value) };
          }
          return { label: String(item), value: String(item) };
        })
        .filter((item) => item.value !== '');
    },
    refreshOptions() {
      const staticValue = this.detail?.dataBind?.staticValue;
      if (!staticValue) {
        this.options = [];
        return;
      }

      try {
        this.options = this.normalizeOptions(JSON.parse(staticValue));
      } catch (_e) {
        try {
          this.options = this.normalizeOptions(this.getStaticData());
        } catch (err) {
          console.warn('select options parse failed', err);
          this.options = [];
        }
      }
    },
    emitInteractionNotice(payload) {
      const bus = this.$busEvent;
      if (!bus) return;
      if (typeof bus.emit === 'function') {
        bus.emit('interactionNotice', payload);
      } else if (typeof bus.$emit === 'function') {
        bus.$emit('interactionNotice', payload);
      }
    },
    handleValueUpdate(val) {
      this.value = this.normalizeValue(val);
    },
    handleSelectChange(val) {
      const value = this.normalizeValue(val);
      this.value = value;
      if (this.detail?.dataBind) {
        this.detail.dataBind.modelValue = value;
      }
      const interaction = Array.isArray(this.detail?.dataEvent?.interaction) ? this.detail.dataEvent.interaction : [];
      interaction.forEach((item) => {
        const { triggerType, bindCompId } = item || {};
        const params = Array.isArray(item?.params) ? item.params : [];
        const headers = Array.isArray(item?.headers) ? item.headers : [];
        if (triggerType === 1) {
          const dparams = params.reduce((obj, item) => {
            if (item.id && item.value === 'data') {
              obj[item.id] = value;
            }
            return obj;
          }, {});
          const dheaders = headers.reduce((obj, item) => {
            if (item.id && item.value === 'data') {
              obj[item.id] = value;
            }
            return obj;
          }, {});
          this.emitInteractionNotice({
            compId: bindCompId,
            params: dparams,
            headers: dheaders,
          });
        }
      });
    },
  },
};
</script>

<style lang="scss" scoped>
.view-select {
  position: relative;
  width: 100%;
  height: 100%;

  :deep(.select-wrap),
  :deep(.el-select),
  :deep(.el-input),
  :deep(.el-input__wrapper) {
    width: 100% !important;
    height: 100% !important;
  }

  :deep(.el-select__wrapper),
  :deep(.el-input__wrapper) {
    min-height: 100%;
    box-sizing: border-box;
    background: var(--select-bg) !important;
    border: var(--select-border);
    border-radius: var(--select-radius) !important;
    box-shadow: none !important;
    font-family: var(--select-font-family);
    font-size: var(--select-font-size);
    font-style: var(--select-font-style);
    font-weight: var(--select-font-weight);
  }

  :deep(.el-select__selected-item),
  :deep(.el-select__placeholder),
  :deep(.el-input__inner) {
    color: var(--select-color) !important;
    font-family: var(--select-font-family);
    font-size: var(--select-font-size);
    font-style: var(--select-font-style);
    font-weight: var(--select-font-weight);
    text-align: var(--select-align);
  }

  :deep(.el-select__selection),
  :deep(.el-select__selected-item),
  :deep(.el-input__inner) {
    height: 100%;
    line-height: inherit;
  }

  :deep(.select-wrap .el-select__selected-item),
  :deep(.select-wrap .el-select__placeholder),
  :deep(.select-wrap .el-input__inner) {
    color: transparent !important;
  }

  :deep(.el-input__suffix) {
    display: flex;
    flex-direction: column;
    justify-content: center;
  }
}

.select-editor-preview {
  position: relative;
  display: flex;
  align-items: center;
  box-sizing: border-box;
  width: 100%;
  height: 100%;
  padding: 0 30px 0 12px;
  overflow: hidden;
  background: var(--select-bg) !important;
  border: var(--select-border);
  border-radius: var(--select-radius) !important;
  color: var(--select-color);
  font-family: var(--select-font-family);
  font-size: var(--select-font-size);
  font-style: var(--select-font-style);
  font-weight: var(--select-font-weight);
  pointer-events: none;
}

.select-editor-text {
  display: block;
  width: 100%;
  overflow: hidden;
  text-align: var(--select-align);
  text-overflow: ellipsis;
  white-space: nowrap;
}

.select-runtime-text {
  position: absolute;
  left: 12px;
  right: 30px;
  top: 0;
  bottom: 0;
  display: flex;
  align-items: center;
  overflow: hidden;
  color: var(--select-color);
  font-family: var(--select-font-family);
  font-size: var(--select-font-size);
  font-style: var(--select-font-style);
  font-weight: var(--select-font-weight);
  line-height: inherit;
  pointer-events: none;
  text-align: var(--select-align);
  text-overflow: ellipsis;
  white-space: nowrap;
}

.select-editor-arrow {
  position: absolute;
  right: 14px;
  top: 50%;
  width: 8px;
  height: 8px;
  border-bottom: 1px solid #909399;
  border-right: 1px solid #909399;
  transform: translateY(-70%) rotate(45deg);
}
</style>
