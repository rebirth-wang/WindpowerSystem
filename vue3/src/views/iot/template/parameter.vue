<template>
  <el-dialog
    class="template-parameter-dialog"
    :title="$t('template.paramter.038405-0')"
    v-model="openEdit"
    width="900px"
    append-to-body
  >
    <el-row>
      <el-col :span="11" class="model-card">
        <el-form class="search-form" :model="queryParams" :inline="true" label-width="48px">
          <el-form-item label="" prop="templateName">
            <el-input
              v-model="queryParams.templateName"
              :placeholder="$t('template.paramter.038405-1')"
              style="width: 200px"
              clearable
              @keyup.enter="handleQuery"
            >
              <template #append>
                <el-button :icon="Search" @click="handleQuery"></el-button>
              </template>
            </el-input>
          </el-form-item>
        </el-form>

        <div class="tip-wrap">
          <el-icon><Warning /></el-icon>
          {{ $t('template.paramter.038405-3') }}
        </div>

        <el-table
          v-loading="loading"
          :data="templateList"
          size="small"
          @row-click="rowClick"
          highlight-current-row
          :border="false"
          :show-header="false"
          :row-style="{ backgroundColor: '#eee' }"
        >
          <el-table-column :label="$t('template.paramter.038405-4')" width="30" align="center">
            <template #default="scope">
              <input
                type="radio"
                :checked="scope.row.isSelect"
                :disabled="scope.row.datatype == 'array' || scope.row.datatype == 'object'"
                name="template"
              />
            </template>
          </el-table-column>
          <el-table-column :label="$t('template.paramter.038405-5')" align="left" prop="templateName" />
          <el-table-column :label="$t('template.paramter.038405-6')" align="left" prop="identifier" />
          <el-table-column :label="$t('template.paramter.038405-7')" align="center" prop="datatype" width="60">
            <template #default="scope">
              <dict-tag :options="iot_data_type" :value="scope.row.datatype" />
            </template>
          </el-table-column>
        </el-table>

        <div class="pager-wrap" v-show="total > 0">
          <pagination
            small
            layout="prev, pager, next"
            :total="total"
            v-model:page="queryParams.pageNum"
            v-model:limit="queryParams.pageSize"
            @pagination="getList"
          />
        </div>
      </el-col>

      <el-col :span="11" :offset="1">
        <el-form ref="formRef" :model="form" :rules="rules" label-width="80px">
          <el-form-item :label="$t('template.paramter.038405-8')" prop="name">
            <el-input v-model="form.name" :placeholder="$t('template.paramter.038405-9')" style="width: 290px" />
          </el-form-item>
          <el-form-item :label="$t('template.paramter.038405-10')" prop="id">
            <el-input
              v-model="form.id"
              :placeholder="$t('template.paramter.038405-11')"
              style="width: 290px"
            ></el-input>
          </el-form-item>
          <el-form-item :label="$t('template.paramter.038405-12')" prop="order">
            <el-input-number
              controls-position="right"
              v-model="form.order"
              :placeholder="$t('template.paramter.038405-13')"
              style="width: 290px"
            />
          </el-form-item>

          <el-form-item :label="$t('template.paramter.038405-14')" prop="property">
            <el-checkbox
              name="isChart"
              :label="$t('template.paramter.038405-15')"
              @change="isChartChange"
              v-model="form.isChart"
              :true-value="1"
              :false-value="0"
            ></el-checkbox>
            <el-checkbox
              name="isMonitor"
              :label="$t('template.paramter.038405-16')"
              @change="isMonitorChange"
              v-model="form.isMonitor"
              :true-value="1"
              :false-value="0"
            ></el-checkbox>
            <el-checkbox
              name="isReadonly"
              :label="$t('template.paramter.038405-17')"
              @change="isReadonlyChange"
              v-model="form.isReadonly"
              :true-value="1"
              :false-value="0"
            ></el-checkbox>
            <el-checkbox
              name="isHistory"
              :label="$t('template.paramter.038405-18')"
              v-model="form.isHistory"
              :true-value="1"
              :false-value="0"
            ></el-checkbox>
            <el-checkbox
              name="isSharePerm"
              :label="$t('template.paramter.038405-19')"
              v-model="form.isSharePerm"
              :true-value="1"
              :false-value="0"
            ></el-checkbox>
          </el-form-item>

          <div style="margin-bottom: 20px; background-color: #ddd; height: 1px"></div>
          <el-form-item :label="$t('template.paramter.038405-20')" prop="datatype">
            <el-select v-model="form.datatype" :placeholder="$t('template.paramter.038405-21')" style="width: 132.9px">
              <el-option
                v-for="dict in iot_data_type"
                :key="dict.value"
                :value="dict.value"
                :label="dict.label"
                :disabled="
                  (form.isChart == 1 && ['bool', 'enum', 'string'].includes(dict.value)) ||
                  ['array', 'object'].includes(dict.value)
                "
              ></el-option>
            </el-select>
          </el-form-item>
          <div v-if="form.datatype == 'integer' || form.datatype == 'decimal'">
            <el-form-item :label="$t('template.paramter.038405-27')">
              <el-row style="width: 290px">
                <el-col :span="11">
                  <el-input v-model="form.specs.min" :placeholder="$t('template.paramter.038405-28')" type="number" />
                </el-col>
                <el-col :span="2" align="center">{{ $t('template.paramter.038405-29') }}</el-col>
                <el-col :span="11">
                  <el-input v-model="form.specs.max" :placeholder="$t('template.paramter.038405-30')" type="number" />
                </el-col>
              </el-row>
            </el-form-item>
            <el-form-item :label="$t('template.paramter.038405-31')">
              <el-input
                v-model="form.specs.unit"
                :placeholder="$t('template.paramter.038405-32')"
                style="width: 290px"
              />
            </el-form-item>
            <el-form-item :label="$t('template.paramter.038405-33')">
              <el-input-number
                controls-position="right"
                v-model="form.specs.step"
                :placeholder="$t('template.paramter.038405-34')"
                style="width: 290px"
              />
            </el-form-item>
          </div>
          <div v-if="form.datatype == 'bool'">
            <el-form-item :label="$t('template.paramter.038405-35')" prop="">
              <el-row style="margin-bottom: 10px">
                <el-col :span="10">
                  <el-input
                    v-model="form.specs.falseText"
                    :placeholder="$t('template.paramter.038405-36')"
                    style="width: 290px"
                  />
                </el-col>
                <el-col :span="10" :offset="1">{{ $t('template.paramter.038405-37') }}</el-col>
              </el-row>
              <el-row>
                <el-col :span="10">
                  <el-input
                    v-model="form.specs.trueText"
                    :placeholder="$t('template.paramter.038405-38')"
                    style="width: 290px"
                  />
                </el-col>
                <el-col :span="10" :offset="1">{{ $t('template.paramter.038405-39') }}</el-col>
              </el-row>
            </el-form-item>
          </div>
          <div v-if="form.datatype == 'enum'">
            <el-form-item :label="$t('template.paramter.038405-40')">
              <el-select
                v-model="form.specs.showWay"
                :placeholder="$t('template.paramter.038405-41')"
                style="width: 132.9px"
              >
                <el-option key="select" :label="$t('template.paramter.038405-42')" value="select"></el-option>
                <el-option key="button" :label="$t('template.paramter.038405-43')" value="button"></el-option>
              </el-select>
            </el-form-item>
            <el-form-item :label="$t('template.paramter.038405-44')" prop="">
              <el-row
                v-for="(item, index) in form.specs.enumList"
                :key="'enum' + index"
                style="width: 290px; margin-bottom: 10px"
              >
                <el-col :span="8">
                  <el-input v-model="item.value" :placeholder="$t('template.paramter.038405-45')" />
                </el-col>
                <el-col :span="11" :offset="1">
                  <el-input v-model="item.text" :placeholder="$t('template.paramter.038405-46')" />
                </el-col>
                <el-col :span="3" :offset="1" v-if="index != 0">
                  <a style="color: #f56c6c" @click="removeEnumItem(index)">{{ $t('template.paramter.038405-47') }}</a>
                </el-col>
              </el-row>
              <div>
                +
                <a style="color: #486ff2" @click="addEnumItem()">{{ $t('template.paramter.038405-48') }}</a>
              </div>
            </el-form-item>
          </div>
          <div v-if="form.datatype == 'string'">
            <el-form-item :label="$t('template.paramter.038405-49')" prop="">
              <el-row>
                <el-col :span="10">
                  <el-input
                    v-model="form.specs.maxLength"
                    :placeholder="$t('template.paramter.038405-50')"
                    type="number"
                    style="width: 290px"
                  />
                </el-col>
              </el-row>
            </el-form-item>
          </div>
        </el-form>
      </el-col>
    </el-row>

    <template #footer>
      <div class="dialog-footer">
        <el-button type="primary" @click="submitForm">{{ $t('template.paramter.038405-51') }}</el-button>
        <el-button @click="cancel">{{ $t('template.paramter.038405-52') }}</el-button>
      </div>
    </template>
  </el-dialog>
</template>

<script setup lang="ts">
import { ref, reactive, watch, getCurrentInstance, type ComponentInternalInstance } from 'vue';
import { Search, Warning } from '@element-plus/icons-vue';
import { listTemplate } from '@/api/iot/template';
import { useDict } from '@/utils/dict';

const { iot_things_type, iot_data_type, iot_yes_no } = useDict(
  'iot_things_type',
  'iot_data_type',
  'iot_yes_no',
  'iot_data_type'
);
const { proxy } = getCurrentInstance() as any;

const props = defineProps<{
  data: any;
}>();
const emit = defineEmits(['dataEvent']);

const loading = ref(true);
const total = ref(0);
const templateList = ref<any[]>([]);
const openEdit = ref(false);
const formRef = ref<any>(null);
const queryParams = reactive({
  pageNum: 1,
  pageSize: 10,
  templateName: null as string | null,
  type: null,
});
let index = -1;
const formDefault = {
  name: null,
  id: null,
  order: 0,
  datatype: 'integer',
  isChart: 0,
  isHistory: 1,
  isSharePerm: 0,
  isMonitor: 0,
  isReadonly: 0,
  specs: {
    enumList: [{ value: '', text: '' }],
    showWay: 'select',
  },
};
const form = ref<any>(JSON.parse(JSON.stringify(formDefault)));

const rules = reactive<any>({
  name: [{ required: true, message: proxy?.$t('template.paramter.038405-53'), trigger: 'blur' }],
  id: [{ required: true, message: proxy?.$t('template.paramter.038405-54'), trigger: 'blur' }],
  order: [{ required: true, message: proxy?.$t('template.paramter.038405-55'), trigger: 'blur' }],
  datatype: [{ required: true, message: proxy?.$t('template.paramter.038405-56'), trigger: 'change' }],
});

function toNumberOrNull(value: any) {
  if (value === null || value === undefined || value === '') return null;
  const numberValue = Number(value);
  return Number.isNaN(numberValue) ? null : numberValue;
}

function normalizeNumberFields() {
  form.value.order = toNumberOrNull(form.value.order);
  if (form.value.specs) {
    form.value.specs.step = toNumberOrNull(form.value.specs.step);
  }
}

watch(
  () => props.data,
  (newVal) => {
    if (!newVal) return;
    index = newVal.index;
    if (newVal.parameter?.name && newVal.parameter.name != '') {
      form.value.name = newVal.parameter.name;
      form.value.id = newVal.parameter.id;
      form.value.order = toNumberOrNull(newVal.parameter.order);
      form.value.isChart = newVal.parameter.isChart || 0;
      form.value.isHistory = newVal.parameter.isHistory || 1;
      form.value.isSharePerm = newVal.parameter.isSharePerm || 0;
      form.value.isMonitor = newVal.parameter.isMonitor || 0;
      form.value.isReadonly = newVal.parameter.isReadonly || 0;
      form.value.specs = newVal.parameter.datatype;
      form.value.datatype = form.value.specs.type;
      if (!form.value.specs.enumList) {
        form.value.specs.enumList = [{ value: '', text: '' }];
      }
      if (!form.value.specs.arrayType) {
        form.value.specs.arrayType = 'integer';
      }
      normalizeNumberFields();
    }
    openEdit.value = true;
    getList();
  }
);

getList();
reset();

/** 查询通用物模型列表 */
function getList() {
  loading.value = true;
  listTemplate(queryParams).then((response: any) => {
    for (let i = 0; i < response.rows.length; i++) {
      response.rows[i].isSelect = false;
    }
    templateList.value = response.rows;
    total.value = response.total;
    loading.value = false;
  });
}

/** 单选数据 */
function rowClick(item: any) {
  if (item != null && item.datatype != 'array' && item.datatype != 'object') {
    form.value.name = item.templateName;
    form.value.id = item.identifier;
    form.value.order = toNumberOrNull(item.modelOrder);
    form.value.isChart = item.isChart || 0;
    form.value.isHistory = item.isHistory || 1;
    form.value.isSharePerm = item.isSharePerm || 0;
    form.value.isReadonly = item.isReadonly || 0;
    form.value.isMonitor = item.isMonitor || 0;
    form.value.datatype = item.datatype;
    form.value.specs = JSON.parse(item.specs);
    if (!form.value.specs.enumList) {
      form.value.specs.enumList = [{ value: '', text: '' }];
    }
    if (!form.value.specs.arrayType) {
      form.value.specs.arrayType = 'integer';
    }
    normalizeNumberFields();
    setRadioSelected(item.templateId);
  }
}

function setRadioSelected(templateId: any) {
  for (let i = 0; i < templateList.value.length; i++) {
    templateList.value[i].isSelect = templateList.value[i].templateId == templateId;
  }
}

function cancel() {
  openEdit.value = false;
  reset();
}

function reset() {
  index = -1;
  form.value = JSON.parse(JSON.stringify(formDefault));
  normalizeNumberFields();
  formRef.value?.resetFields();
}

function handleQuery() {
  queryParams.pageNum = 1;
  getList();
}

function submitForm() {
  formRef.value?.validate((valid: boolean) => {
    if (valid) {
      normalizeNumberFields();
      form.value.datatype = formatThingsSpecs();
      delete form.value.specs;
      openEdit.value = false;
      emit('dataEvent', { parameter: JSON.parse(JSON.stringify(form.value)), index });
      reset();
    }
  });
}

function isChartChange() {
  if (form.value.isChart == 1) {
    form.value.isReadonly = 1;
  } else {
    form.value.isMonitor = 0;
  }
}

function isMonitorChange() {
  if (form.value.isMonitor == 1) {
    form.value.isReadonly = 1;
    form.value.isChart = 1;
  }
}

function isReadonlyChange() {
  if (form.value.isReadonly == 0) {
    form.value.isMonitor = 0;
    form.value.isChart = 0;
  }
}

function formatThingsSpecs() {
  const data: any = { type: form.value.datatype };
  if (form.value.datatype == 'integer' || form.value.datatype == 'decimal') {
    data.min = Number(form.value.specs.min || 0);
    data.max = Number(form.value.specs.max || 100);
    data.unit = form.value.specs.unit || '';
    data.step = Number(form.value.specs.step || 1);
  } else if (form.value.datatype == 'string') {
    data.maxLength = Number(form.value.specs.maxLength || 1024);
  } else if (form.value.datatype == 'bool') {
    data.falseText = form.value.specs.falseText || proxy?.$t('template.paramter.038405-57');
    data.trueText = form.value.specs.trueText || proxy?.$t('template.paramter.038405-58');
  } else if (form.value.datatype == 'array') {
    data.arrayType = form.value.specs.arrayType;
  } else if (form.value.datatype == 'enum') {
    data.showWay = form.value.specs.showWay;
    if (form.value.specs.enumList && form.value.specs.enumList[0].text != '') {
      data.enumList = form.value.specs.enumList;
    } else {
      data.showWay = 'select';
      data.enumList = [
        { value: '0', text: proxy?.$t('template.paramter.038405-59') },
        { value: '1', text: proxy?.$t('template.paramter.038405-60') },
      ];
    }
  }
  return data;
}

function addEnumItem() {
  form.value.specs.enumList.push({ value: '', text: '' });
}

function removeEnumItem(idx: number | string) {
  form.value.specs.enumList.splice(Number(idx), 1);
}
</script>

<style lang="scss" scoped>
.template-parameter-dialog {
  padding: 6px;

  .model-card {
    border: 1px solid #dcdfe6;
    border-radius: 4px;
    padding: 15px 10px;
    background-color: rgb(238, 238, 238);

    .search-form {
      margin-left: 10px;
    }

    .tip-wrap {
      margin: -8px 5px 8px 11px;
      color: #ffb032;
      font-size: 13px;
    }

    .pager-wrap {
      margin: 6px 0 0;
      background-color: #eee;
      display: flex;
      justify-content: flex-end;
      overflow: hidden;
      padding-right: 6px;
    }

    .pager-wrap :deep(.pagination-container) {
      margin: 0 !important;
      padding: 0 !important;
      background: transparent !important;
      min-height: auto;
      justify-content: flex-end;
    }

    .pager-wrap :deep(.el-pagination) {
      margin: 0;
      padding: 0;
      --el-pagination-button-height: 24px;
      --el-pagination-button-width: 24px;
      --el-pagination-font-size: 12px;
    }

    .pager-wrap :deep(.el-pager li),
    .pager-wrap :deep(.btn-prev),
    .pager-wrap :deep(.btn-next) {
      min-width: 24px;
      height: 24px;
      line-height: 24px;
    }
  }
}
</style>
