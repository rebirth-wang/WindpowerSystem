<template>
  <div class="flow-product-trigger">
    <el-form :model="curNode.data" label-width="68px" size="small">
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
      <el-form-item :label="$t('ruleengine.editor.components.form.flow-product-trigger.807357-0')">
        <el-select
          style="width: 100%"
          v-model="curNode.data.productId"
          :placeholder="$t('pleaseSelect')"
          filterable
          @change="handleProductChange"
        >
          <el-option
            v-for="item in productList"
            :key="item.productId"
            :label="item.productName"
            :value="item.productId"
          >
            <span style="float: left">{{ item.productName }}</span>
            <span style="float: right">
              <el-tag type="info" v-if="item.status == 1">
                {{ $t('ruleengine.editor.components.form.flow-product-trigger.807357-1') }}
              </el-tag>
              <el-tag type="success" v-if="item.status == 2">
                {{ $t('ruleengine.editor.components.form.flow-product-trigger.807357-2') }}
              </el-tag>
            </span>
          </el-option>
        </el-select>
      </el-form-item>
      <el-form-item :label="$t('ruleengine.editor.components.form.flow-product-trigger.807357-3')">
        <el-select
          style="width: 100%"
          v-model="curNode.data.type"
          :placeholder="$t('pleaseSelect')"
          @change="handleTypeChange"
        >
          <el-option
            v-for="dict in trigger_type"
            :key="dict.value + 'type'"
            :label="dict.label"
            :value="Number(dict.value)"
          ></el-option>
        </el-select>
      </el-form-item>
      <el-form-item
        :label="$t('ruleengine.editor.components.form.flow-product-trigger.807357-4')"
        v-if="curNode.data.type === 1"
      >
        <el-select style="width: 100%" v-model="curNode.data.modelId" :placeholder="$t('pleaseSelect')" filterable>
          <el-option
            v-for="(item, index) in thingsModel.properties"
            :key="index + 'property'"
            :label="item.name"
            :value="item.id"
          ></el-option>
        </el-select>
      </el-form-item>
      <el-form-item
        :label="$t('ruleengine.editor.components.form.flow-product-trigger.807357-4')"
        v-if="curNode.data.type === 2"
      >
        <el-select style="width: 100%" v-model="curNode.data.modelId" :placeholder="$t('pleaseSelect')" filterable>
          <el-option
            v-for="(item, index) in thingsModel.functions"
            :key="index + 'functions'"
            :label="item.name"
            :value="item.id"
          ></el-option>
        </el-select>
      </el-form-item>
      <el-form-item
        :label="$t('ruleengine.editor.components.form.flow-product-trigger.807357-4')"
        v-if="curNode.data.type === 3"
      >
        <el-select style="width: 100%" v-model="curNode.data.modelId" :placeholder="$t('pleaseSelect')" filterable>
          <el-option
            v-for="(item, index) in thingsModel.events"
            :key="index + 'events'"
            :label="item.name"
            :value="item.id"
          ></el-option>
        </el-select>
      </el-form-item>
    </el-form>
    <!-- 调试日志组件 -->
    <debug-log :log-list="curNode.data.debugLog" ref="debugLogRef"></debug-log>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted } from 'vue';
import { useI18n } from 'vue-i18n';
import { useRoute } from 'vue-router';
import { useRuleEditorStore } from '@/stores/modules/ruleEditor';
import { storeToRefs } from 'pinia';
import { useDict } from '@/utils/dict/useDict';
import { View } from '@element-plus/icons-vue';
import { listProduct } from '@/api/iot/product';
import { cacheJsonThingsModel } from '@/api/iot/model';
import debugLog from '../debug-log.vue';

const { t } = useI18n();
const route = useRoute();
const ruleEditorStore = useRuleEditorStore();
const { curNode } = storeToRefs(ruleEditorStore);
const { trigger_type } = useDict('trigger_type');

const debugLogRef = ref();
const productList = ref<any[]>([]);
const thingsModel = reactive<any>({});

onMounted(() => {
  getProductList();
});

// 获取产品列表
const getProductList = () => {
  const params = { pageNum: 1, pageSize: 999999 };
  listProduct(params).then((res: any) => {
    if (res.code === 200) {
      productList.value = res.rows;
      const { productId } = curNode.value.data || {};
      if (productId) {
        getThingsModel(productId);
      }
    }
  });
};

// 产品选择
const handleProductChange = (val: any) => {
  const product = productList.value.find((item: any) => item.productId === val) || {};
  curNode.value.data = {
    ...curNode.value.data,
    productId: product.productId,
    type: null,
  };
  getThingsModel(val);
};

// 获取物模型
const getThingsModel = (productId: any) => {
  const product = productList.value.find((item: any) => item.productId === productId) || {};
  cacheJsonThingsModel(product.productId).then((res: any) => {
    if (res.code === 200) {
      const parsed = JSON.parse(res.data);
      Object.assign(thingsModel, parsed);
    }
  });
};

// 属性类型选择
const handleTypeChange = () => {
  curNode.value.data = { ...curNode.value.data, modelId: '' };
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
.flow-product-trigger {
  width: 100%;
  padding: 0 10px;
}
</style>
