<template>
  <div class="condition-wrap">
    <div class="expression-content">
      <el-form label-width="68px" size="small" label-position="left">
        <el-form-item :label="$t('ruleengine.editor.index.807357-18')">
          <el-switch v-model="curNode.data.debug" :active-value="1" :inactive-value="0"></el-switch>
          <el-button
            type="primary"
            link
            size="small"
            @click="showDebugLog"
            style="color: #486ff2; margin-left: 20px; height: 26px; font-size: 13px"
          >
            <el-icon><View /></el-icon>
            {{ $t('look') }}
          </el-button>
        </el-form-item>
        <el-form-item :label="$t('ruleengine.editor.components.form.flow-condition.807357-0')">
          <el-select :placeholder="$t('pleaseSelect')" v-model="curNode.data.type" style="width: 100%">
            <el-option v-for="item in typeList" :key="item.id" :label="item.label" :value="item.id"></el-option>
          </el-select>
        </el-form-item>
      </el-form>
      <el-form label-width="68px" size="small" label-position="top">
        <el-form-item
          :label="$t('ruleengine.editor.components.form.flow-condition.807357-1')"
          class="condition-form-item"
        >
          <div class="condition-scroll-container">
            <div v-if="curNode.data.expressions.length !== 0" class="param-list-item">
              <div class="item-left" v-for="(item, index) in curNode.data.expressions" :key="index + '_item'">
                <div class="index-no">{{ item.lineValue ? item.lineValue : index + 1 }}</div>
                <div class="right-table">
                  <div class="table-body">
                    <div class="cell">
                      <div class="list-item">
                        <el-select
                          v-if="
                            Object.keys(modelVale).length !== 0 &&
                            (modelVale.datatype.type === 'integer' ||
                              modelVale.datatype.type === 'decimal' ||
                              modelVale.datatype.type === 'string')
                          "
                          v-model="item.operator"
                          :placeholder="$t('pleaseSelect')"
                          size="small"
                          style="width: 100%; margin-bottom: 6px"
                        >
                          <el-option
                            v-for="dict in operator"
                            :key="dict.value"
                            :label="dict.label"
                            :value="dict.value"
                            :disabled="
                              (['>', '<', '>=', '<=', 'between', 'notBetween'].includes(dict.value) &&
                                !['integer', 'decimal'].includes(modelVale.datatype.type)) ||
                              (['contain', 'notContain'].includes(dict.value) && modelVale.datatype.type !== 'string')
                            "
                          ></el-option>
                        </el-select>
                        <div
                          v-if="
                            Object.keys(modelVale).length !== 0 &&
                            (modelVale.datatype.type === 'integer' || modelVale.datatype.type === 'decimal')
                          "
                          v-show="item.operator === 'between' || item.operator === 'notBetween'"
                          style="display: flex; align-items: center; gap: 6px; width: 100%"
                        >
                          <el-input
                            style="vertical-align: baseline"
                            @input="valueChange($event, item)"
                            v-model="item.valueA"
                            :placeholder="$t('scene.index.670805-21')"
                            :max="modelVale.datatype.max"
                            :min="modelVale.datatype.min"
                            type="number"
                            size="small"
                          ></el-input>
                          <span style="padding: 0 3px; color: #999">~</span>
                          <el-input
                            style="vertical-align: baseline"
                            @input="valueChange($event, item)"
                            v-model="item.valueB"
                            :placeholder="$t('scene.index.670805-21')"
                            :max="modelVale.datatype.max"
                            :min="modelVale.datatype.min"
                            type="number"
                            size="small"
                          ></el-input>
                          <el-tooltip
                            class="item unit-tooltip"
                            effect="dark"
                            :content="modelVale.datatype.unit"
                            placement="top"
                            :disabled="!modelVale.datatype.unit"
                          >
                            <span class="unit-wrap">{{ modelVale.datatype.unit || '-' }}</span>
                          </el-tooltip>
                        </div>

                        <div
                          v-if="
                            Object.keys(modelVale).length !== 0 &&
                            (modelVale.datatype.type === 'integer' || modelVale.datatype.type === 'decimal')
                          "
                          v-show="item.operator !== 'between' && item.operator !== 'notBetween'"
                        >
                          <el-input
                            style="vertical-align: baseline"
                            v-model="item.value"
                            :placeholder="$t('scene.index.670805-21')"
                            size="small"
                          >
                            <template #append v-if="modelVale.datatype.unit">{{ modelVale.datatype.unit }}</template>
                          </el-input>
                        </div>
                        <div v-else-if="Object.keys(modelVale).length !== 0 && modelVale.datatype.type === 'bool'">
                          <el-switch
                            style="vertical-align: sub"
                            v-model="item.value"
                            :active-text="modelVale.datatype.trueText"
                            :inactive-text="modelVale.datatype.falseText"
                            active-value="1"
                            inactive-value="0"
                            size="small"
                          ></el-switch>
                        </div>
                        <div v-else-if="Object.keys(modelVale).length !== 0 && modelVale.datatype.type === 'enum'">
                          <el-select
                            v-model="item.value"
                            :placeholder="$t('pleaseSelect')"
                            style="width: 100%"
                            size="small"
                          >
                            <el-option
                              v-for="(subItem, subIndex) in modelVale.datatype.enumList"
                              :key="subIndex + 'things'"
                              :label="subItem.text"
                              :value="subItem.value"
                            ></el-option>
                          </el-select>
                        </div>
                        <div v-else-if="Object.keys(modelVale).length !== 0 && modelVale.datatype.type === 'string'">
                          <el-input
                            v-model="item.value"
                            :placeholder="$t('scene.index.670805-22')"
                            size="small"
                            :max="modelVale.datatype.maxLength"
                          />
                        </div>
                      </div>
                    </div>
                  </div>
                </div>
              </div>
            </div>

            <div v-else class="param-list-item-tip">
              <el-alert
                :title="$t('ruleengine.editor.components.form.flow-condition.807357-12')"
                type="info"
                :description="$t('ruleengine.editor.components.form.flow-condition.807357-13')"
                show-icon
                :closable="false"
              ></el-alert>
            </div>
          </div>
        </el-form-item>
      </el-form>
    </div>
    <!-- 调试日志组件 -->
    <debug-log :log-list="curNode.data.debugLog" ref="debugLogRef"></debug-log>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted, getCurrentInstance } from 'vue';
import { useI18n } from 'vue-i18n';
import { useRoute } from 'vue-router';
import { useRuleEditorStore } from '@/stores/modules/ruleEditor';
import { storeToRefs } from 'pinia';
import { useDict } from '@/utils/dict/useDict';
import { View } from '@element-plus/icons-vue';
import { cloneDeep } from 'lodash';
import { cacheJsonThingsModel } from '@/api/iot/model';
import debugLog from '../debug-log.vue';

const { proxy } = getCurrentInstance() as any;
const { t } = useI18n();
const route = useRoute();
const ruleEditorStore = useRuleEditorStore();
const { data, curNode } = storeToRefs(ruleEditorStore);
const { operator } = useDict('operator');

const debugLogRef = ref();
const modelVale = ref<any>({});

const typeList = [
  { id: 1, label: t('ruleengine.editor.components.form.flow-condition.807357-14') },
  { id: 2, label: t('ruleengine.editor.components.form.flow-condition.807357-15') },
];

onMounted(() => {
  getLineList();
  getTriggerModel();
  curNode.value.data.type = 1;
});

// 获取条件组件连过去的线
const getLineList = () => {
  const { id, data: nodeData } = curNode.value;
  let { expressions } = nodeData;
  const { lines } = data.value;
  let exp = cloneDeep(expressions);
  let lin = cloneDeep(lines);

  // 新增时添加lineValue
  lin.forEach((item: any) => {
    if (item.from === id) {
      const isHave = exp.some((ex: any) => ex.from === item.from && ex.to === item.to);
      if (!isHave) {
        exp.push({
          from: item.from,
          to: item.to,
          lineValue: item.value,
        });
      }
    }
  });

  // 过滤无效表达式并补充lineValue
  exp = exp.filter((item: any) => {
    const correspondingLine = lin.find((li: any) => li.from === item.from && li.to === item.to);
    if (correspondingLine) {
      item.lineValue = correspondingLine.value;
      return true;
    }
    return false;
  });

  curNode.value.data.expressions = exp;
};

// 获取触发组件的model
const getTriggerModel = () => {
  const { nodes, lines } = data.value;
  const { id } = curNode.value;
  const line = lines.find((item: any) => item.to === id);
  if (!line) {
    proxy.$modal.msgError(t('ruleengine.editor.components.form.flow-condition.807357-16'));
    return;
  }
  const node = nodes.find((item: any) => item.id === line.from);
  getThingsModel(node.data);
};

// 获取model详情
const getThingsModel = (nodeData: any) => {
  const { productId, type, modelId } = nodeData;
  if (!productId) {
    proxy.$modal.msgError(t('ruleengine.editor.components.form.flow-condition.807357-17'));
    return;
  }
  curNode.value.data.modelId = modelId;
  curNode.value.data.triggerType = type;
  cacheJsonThingsModel(productId).then((res: any) => {
    if (res.code === 200) {
      const thingsModel = JSON.parse(res.data);
      let models: any[] = [];
      switch (type) {
        case 1:
          models = thingsModel.properties;
          break;
        case 2:
          models = thingsModel.functions;
          break;
        case 3:
          models = thingsModel.events;
          break;
        default:
          models = [];
      }
      const targetModel = models.find((m: any) => m.id === modelId);
      if (targetModel) modelVale.value = targetModel;
    }
  });
};

// 触发器中，选择between操作符，值是A值和B值用中划线分割
const valueChange = (_value: any, item: any) => {
  item.value = item.valueA + '~' + item.valueB;
};

// 查看日志
const showDebugLog = () => {
  debugLogRef.value.openLog = true;
  const id = Number(route.query.id);
  const curNodeId = curNode.value.id;
  debugLogRef.value.handleViewLog(id, curNodeId);
};
</script>

<style lang="scss" scoped>
.condition-wrap {
  width: 100%;
  padding-bottom: 5px;

  .expression-content {
    width: 100%;
    padding: 0 10px;

    .condition-form-item {
      margin-bottom: 0;
      :deep(.el-form-item__content) {
        width: 100%;
        padding-left: 0 !important;
      }

      .condition-top-title {
        background: #f5f7fa;
        border: 1px solid #ebeef5;
        border-bottom: none;
        border-radius: 4px 4px 0 0;
        padding: 10px 16px;
        font-size: 14px;
        font-weight: 500;
        color: #333;
      }

      .condition-scroll-container {
        max-height: 500px;
        overflow-y: auto;
        overflow-x: hidden;
        border: 1px solid #ebeef5;
        border-radius: 0 0 4px 4px;
        padding: 16px;
        background: #fff;

        &::-webkit-scrollbar {
          width: 6px;
          height: 6px;
        }
        &::-webkit-scrollbar-thumb {
          border-radius: 3px;
          background: rgba(0, 0, 0, 0.2);
        }
        &::-webkit-scrollbar-track {
          background: transparent;
        }
      }

      .param-list-item {
        background: transparent;
        margin-bottom: 0;
        padding: 0;

        .item-left {
          display: flex;
          align-items: flex-start;
          margin-bottom: 16px;
          padding-bottom: 16px;

          &:last-child {
            margin-bottom: 0;
            padding-bottom: 0;
            border-bottom: none;
          }

          .index-no {
            width: 28px;
            height: 26px;
            box-shadow: 0 0 4px 0 rgba(0, 0, 0, 0.1);
            font-size: 12px;
            font-weight: 400;
            line-height: 25px;
            text-align: center;
            margin-top: 3px;
            margin-right: 10px;
            color: #486ff2;
            margin-bottom: 10px;
            flex-shrink: 0;
          }

          .right-table {
            width: 100%;
            margin-bottom: 0;

            .table-body {
              display: flex;
              background: #f5f7fa;

              .cell {
                width: 100%;
                display: flex;
                align-items: flex-start;

                .list-item {
                  width: 100%;
                  margin-bottom: 0;
                  background: #fff;
                }
              }
            }
          }
        }
      }

      .param-list-item-tip {
        padding: 0;
        text-align: center;
        padding: 20px 0;
      }
    }

    :deep(.el-switch__core) {
      width: 32px !important;
      height: 16px;
    }
    :deep(.el-switch__core::after) {
      width: 14px;
      height: 14px;
      margin-top: -1px;
    }
    :deep(.el-switch.is-checked .el-switch__core::after) {
      margin-left: -15px;
    }
    :deep(.el-switch__label *) {
      font-size: 12px;
    }
    :deep(.el-switch__label--left) {
      margin-right: 7px;
    }
    :deep(.el-switch__label--right) {
      margin-left: 7px;
    }
  }

  .form-wrap {
    margin-left: 12px;
  }
  .unit-wrap {
    display: inline-block;
    background-color: rgb(245, 247, 250);
    color: rgb(144, 147, 153);
    border: 1px solid rgb(220, 223, 230);
    border-radius: 4px;
    font-size: 13px;
    text-align: center;
    width: 83px;
    height: 32px;
    white-space: nowrap;
    overflow: hidden;
    text-overflow: ellipsis;
    line-height: 32px;
    padding: 0 5px;
  }
}
</style>
