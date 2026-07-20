<template>
  <el-dialog
    :title="$t('scada.topo.components.propertyBar.variateDrawer.842076-41')"
    v-model="visible"
    width="900px"
    append-to-body
    :close-on-click-modal="false"
  >
    <el-form label-width="100px">
      <el-form-item :label="$t('topo.components.propertyBar.index.038495-351')">
        <el-row>
          <el-col :span="16">
            <el-input :placeholder="$t('topo.components.propertyBar.index.038495-352')" v-model="form.url">
              <template #prepend>
                <el-select style="width: 100px" v-model="form.type">
                  <el-option label="get" value="get" />
                  <el-option label="post" value="post" />
                  <el-option label="put" value="put" />
                </el-select>
              </template>
            </el-input>
          </el-col>
          <el-col :span="8">
            <el-input
              style="margin-left: 10px; width: 170px"
              type="number"
              :placeholder="$t('topo.components.propertyBar.index.038495-353')"
              v-model="form.time"
            >
              <template #append>
                <el-select style="width: 70px" v-model="form.unit">
                  <el-option :label="$t('topo.components.propertyBar.index.038495-354')" value="s" />
                  <el-option :label="$t('topo.components.propertyBar.index.038495-355')" value="m" />
                  <el-option :label="$t('topo.components.propertyBar.index.038495-356')" value="h" />
                </el-select>
              </template>
            </el-input>
          </el-col>
        </el-row>
      </el-form-item>
      <el-form-item :label="$t('topo.components.propertyBar.index.038495-357')">
        <div>{{ $t('topo.components.propertyBar.index.038495-358') }}</div>
        <el-tabs style="width: 80%" v-model="form.tabActive">
          <el-tab-pane style="max-height: 300px; overflow-y: auto" label="Params" name="params">
            <el-table :data="form.paramsData" style="width: 100%" :border="false" size="small">
              <el-table-column type="index" width="50" />
              <el-table-column prop="key" label="Key">
                <template #default="scope">
                  <el-input
                    :placeholder="$t('topo.components.propertyBar.index.038495-359')"
                    v-model="scope.row.key"
                    size="small"
                    clearable
                  />
                </template>
              </el-table-column>
              <el-table-column prop="value" label="Value">
                <template #default="scope">
                  <el-input
                    :placeholder="$t('topo.components.propertyBar.index.038495-359')"
                    v-model="scope.row.value"
                    size="small"
                    clearable
                  />
                </template>
              </el-table-column>
              <el-table-column prop="address" :label="$t('topo.components.propertyBar.index.038495-360')" width="120">
                <template #default="scope">
                  <el-button type="primary" plain size="small" @click="handleAddHttpParams">+</el-button>
                  <el-button type="danger" plain size="small" @click="handleDeleteHttpParams(scope.$index)">
                    -
                  </el-button>
                </template>
              </el-table-column>
            </el-table>
          </el-tab-pane>
          <el-tab-pane style="max-height: 300px" label="Body" name="body">
            <el-radio-group v-model="form.bodyType" @input="handleHttpBodyRadioInput">
              <el-radio :label="1">none</el-radio>
              <el-radio :label="2">form-data</el-radio>
            </el-radio-group>
            <el-empty v-if="form.bodyType === 1" :description="$t('topo.components.propertyBar.index.038495-361')" />
            <el-table
              v-if="form.bodyType === 2"
              :data="form.bodyData"
              style="width: 100%; height: 264px; overflow-y: auto"
              :border="false"
              size="small"
            >
              <el-table-column type="index" width="50" />
              <el-table-column prop="key" label="Key">
                <template #default="scope">
                  <el-input
                    :placeholder="$t('topo.components.propertyBar.index.038495-359')"
                    v-model="scope.row.key"
                    size="small"
                    clearable
                  />
                </template>
              </el-table-column>
              <el-table-column prop="value" label="Value">
                <template #default="scope">
                  <el-input
                    :placeholder="$t('topo.components.propertyBar.index.038495-359')"
                    v-model="scope.row.value"
                    size="small"
                    clearable
                  />
                </template>
              </el-table-column>
              <el-table-column prop="address" :label="$t('topo.components.propertyBar.index.038495-360')" width="120">
                <template #default="scope">
                  <el-button type="primary" plain size="small" @click="handleAddHttpBody">+</el-button>
                  <el-button type="danger" plain size="small" @click="handleDeleteHttpBody(scope.$index)">-</el-button>
                </template>
              </el-table-column>
            </el-table>
          </el-tab-pane>
          <el-tab-pane style="max-height: 300px; overflow-y: auto" label="Headers" name="headers">
            <el-table :data="form.headerData" style="width: 100%" :border="false" size="small">
              <el-table-column type="index" width="50" />
              <el-table-column prop="key" label="Key">
                <template #default="scope">
                  <el-input
                    :placeholder="$t('topo.components.propertyBar.index.038495-359')"
                    v-model="scope.row.key"
                    size="small"
                    clearable
                  />
                </template>
              </el-table-column>
              <el-table-column prop="value" label="Value">
                <template #default="scope">
                  <el-input
                    :placeholder="$t('topo.components.propertyBar.index.038495-359')"
                    v-model="scope.row.value"
                    size="small"
                    clearable
                  />
                </template>
              </el-table-column>
              <el-table-column prop="address" :label="$t('topo.components.propertyBar.index.038495-360')" width="120">
                <template #default="scope">
                  <el-button type="primary" plain size="small" @click="handleAddHttpHeader">+</el-button>
                  <el-button type="danger" plain size="small" @click="handleDeleteHttpHeader(scope.$index)">
                    -
                  </el-button>
                </template>
              </el-table-column>
            </el-table>
          </el-tab-pane>
        </el-tabs>
      </el-form-item>
    </el-form>

    <template #footer>
      <el-button type="primary" @click="save">{{ $t('topo.components.propertyBar.index.038495-362') }}</el-button>
      <el-button @click="visible = false">{{ $t('topo.components.propertyBar.index.038495-363') }}</el-button>
    </template>
  </el-dialog>
</template>

<script setup lang="ts">
import { getCurrentInstance, reactive, ref, watch } from 'vue';

const { proxy } = getCurrentInstance() as any;

const props = defineProps<{
  value: Record<string, any>;
  hasAdd?: boolean;
}>();

const emit = defineEmits<{
  (e: 'save', value: any): void;
}>();

const visible = ref(false);
const form = reactive<any>({});

watch(
  () => props.value,
  (newVal) => {
    Object.keys(form).forEach((k) => delete form[k]);
    Object.assign(form, JSON.parse(JSON.stringify(newVal || {})));
  },
  { immediate: true, deep: true }
);

function open(val?: boolean) {
  if (val) {
    Object.assign(form, {
      url: '',
      type: 'get',
      time: 60,
      unit: 's',
      tabActive: 'params',
      paramsData: [{}],
      bodyType: 1,
      bodyData: [{}],
      headerData: [{}],
    });
  }
  visible.value = true;
}

function save() {
  if (form.url) {
    let convertedTime = Number(form.time);
    switch (form.unit) {
      case 'm':
        convertedTime = convertedTime * 60;
        break;
      case 'h':
        convertedTime = convertedTime * 3600;
        break;
      default:
        break;
    }
    form.time = convertedTime;
    emit('save', JSON.parse(JSON.stringify(form)));
    Object.keys(form).forEach((k) => delete form[k]);
    visible.value = false;
  } else {
    proxy.$message.warning(proxy.$t('scada.topo.components.propertyBar.variateDrawer.842076-42'));
  }
}

function handleAddHttpParams() {
  form.paramsData.push({});
}
function handleDeleteHttpParams(index: number) {
  if (form.paramsData.length !== 1) form.paramsData.splice(index, 1);
}
function handleAddHttpBody() {
  form.bodyData.push({});
}
function handleDeleteHttpBody(index: number) {
  if (form.bodyData.length !== 1) form.bodyData.splice(index, 1);
}
function handleHttpBodyRadioInput(val: number) {
  if (form.bodyType === 1) form.bodyData = [{}];
  form.bodyType = val;
}
function handleAddHttpHeader() {
  form.headerData.push({});
}
function handleDeleteHttpHeader(index: number) {
  if (form.headerData.length !== 1) form.headerData.splice(index, 1);
}

defineExpose({ open });
</script>
