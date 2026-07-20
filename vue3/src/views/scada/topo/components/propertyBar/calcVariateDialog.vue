<template>
  <el-dialog
    class="property-bar-calc-variate-dialog"
    :title="$t('scada.topo.components.propertyBar.calcVariateDialog.205053-0')"
    v-model="visibleModel"
    width="820px"
    :before-close="handleClose"
    @opened="handleOpened"
  >
    <div class="variate-content">
      <div class="tip-wrap">
        <p>{{ $t('scada.topo.components.propertyBar.calcVariateDialog.205053-1') }}</p>
        <p>{{ $t('scada.topo.components.propertyBar.calcVariateDialog.205053-2') }}</p>
        <p>{{ $t('scada.topo.components.propertyBar.calcVariateDialog.205053-3') }}</p>
        <p>{{ $t('scada.topo.components.propertyBar.calcVariateDialog.205053-4') }}</p>
      </div>

      <el-form class="form-wrap" ref="formRef" :model="form" :rules="rules" label-position="top">
        <el-form-item prop="formule" :label="$t('scada.topo.components.propertyBar.calcVariateDialog.205053-5')">
          <el-input
            v-model="form.formule"
            :placeholder="$t('scada.topo.components.propertyBar.calcVariateDialog.205053-6')"
            type="textarea"
            :rows="1"
            clearable
            style="width: 100%"
          />
        </el-form-item>

        <el-table
          v-if="form.variates.length !== 0"
          class="table-wrap"
          :data="form.variates"
          :border="false"
          style="width: 100%"
        >
          <el-table-column
            prop="alias"
            :label="$t('scada.topo.components.propertyBar.calcVariateDialog.205053-7')"
            width="90"
          >
            <template #default="scope">
              <div class="alias-wrap">
                {{ scope.row.alias }}
              </div>
            </template>
          </el-table-column>

          <el-table-column
            v-if="scadaType === 3"
            :label="$t('scada.topo.components.propertyBar.calcVariateDialog.205053-8')"
          >
            <template #default="scope">
              <el-form-item
                :prop="'variates.' + scope.$index + '.serialNumber'"
                :rules="{
                  required: true,
                  message: $t('scada.topo.components.propertyBar.calcVariateDialog.205053-9'),
                  trigger: 'change',
                }"
              >
                <el-select
                  v-model="scope.row.serialNumber"
                  :placeholder="$t('scada.topo.components.propertyBar.calcVariateDialog.205053-9')"
                  filterable
                  style="width: 250px"
                  @change="handleDataSourcesChange(scope, $event)"
                >
                  <el-option
                    v-for="item in deviceList"
                    :key="item.serialNumber"
                    :label="item.deviceName"
                    :value="item.serialNumber"
                  />
                </el-select>
              </el-form-item>
            </template>
          </el-table-column>

          <el-table-column
            v-if="scadaType === 2"
            :label="$t('scada.topo.components.propertyBar.calcVariateDialog.205053-10')"
          >
            <template #default="scope">
              <el-form-item
                :prop="'variates.' + scope.$index + '.sceneModelDeviceId'"
                :rules="{
                  required: true,
                  message: $t('scada.topo.components.propertyBar.calcVariateDialog.205053-11'),
                  trigger: 'change',
                }"
              >
                <el-select
                  v-model="scope.row.sceneModelDeviceId"
                  :placeholder="$t('scada.topo.components.propertyBar.calcVariateDialog.205053-11')"
                  filterable
                  style="width: 250px"
                  @change="handleDataSourcesChange(scope, $event)"
                >
                  <el-option v-for="item in deviceList" :key="item.id" :label="item.name" :value="item.id" />
                </el-select>
              </el-form-item>
            </template>
          </el-table-column>

          <el-table-column
            v-if="scadaType === 2 || scadaType === 3"
            :label="$t('scada.topo.components.propertyBar.calcVariateDialog.205053-12')"
          >
            <template #default="scope">
              <el-form-item
                :prop="'variates.' + scope.$index + '.identifier'"
                :rules="{
                  required: true,
                  message: $t('scada.topo.components.propertyBar.calcVariateDialog.205053-13'),
                  trigger: 'change',
                }"
              >
                <el-select
                  v-model="scope.row.identifier"
                  :placeholder="$t('scada.topo.components.propertyBar.calcVariateDialog.205053-13')"
                  filterable
                  style="width: 250px"
                  @change="handleVariableChange(scope, $event)"
                >
                  <el-option
                    v-for="item in numModelList[scope.$index]"
                    :key="item.identifier"
                    :label="item.modelName"
                    :value="item.identifier"
                  />
                </el-select>
              </el-form-item>
            </template>
          </el-table-column>

          <el-table-column
            v-if="scadaType === 1"
            :label="$t('scada.topo.components.propertyBar.calcVariateDialog.205053-12')"
            prop="identifier"
          >
            <template #default="scope">
              <el-form-item
                :prop="'variates.' + scope.$index + '.identifier'"
                :rules="{
                  required: true,
                  message: $t('scada.topo.components.propertyBar.calcVariateDialog.205053-13'),
                  trigger: 'change',
                }"
              >
                <el-select
                  v-model="scope.row.identifier"
                  :placeholder="$t('scada.topo.components.propertyBar.calcVariateDialog.205053-13')"
                  filterable
                  style="width: 250px"
                  @change="handleVariableChange(scope, $event)"
                >
                  <el-option
                    v-for="item in modelList"
                    :key="item.identifier"
                    :label="item.modelName"
                    :value="item.identifier"
                  />
                </el-select>
              </el-form-item>
            </template>
          </el-table-column>

          <el-table-column :label="$t('opation')" align="center" class-name="small-padding fixed-width" width="100">
            <template #default="scope">
              <el-button
                style="color: #f56c6c"
                type="text"
                icon="el-icon-delete"
                @click="handleDeleteFormula(scope.$index)"
              >
                {{ $t('del') }}
              </el-button>
            </template>
          </el-table-column>
        </el-table>

        <el-button type="text" class="add-formula-btn" :icon="CirclePlus" @click="handleAddFormula">
          {{ $t('scada.topo.components.propertyBar.calcVariateDialog.205053-14') }}
        </el-button>
      </el-form>
    </div>

    <template #footer>
      <el-button type="primary" @click="handleConfirm">{{ $t('confirm') }}</el-button>
      <el-button @click="handleClose">{{ $t('cancel') }}</el-button>
    </template>
  </el-dialog>
</template>

<script setup lang="ts">
import { computed, getCurrentInstance, nextTick, reactive, ref } from 'vue';
import { CirclePlus } from '@element-plus/icons-vue';
import { useRoute } from 'vue-router';
import { listDeviceBind, getListVariable } from '@/api/scada/topo';
import { getSceneModelDeviceList } from '@/api/scene/list.js';
import { getScadaRouteType, getRouteQueryNumber, getRouteQueryString } from '@/utils/topo/topoUtil';

const { proxy } = getCurrentInstance() as any;
const route = useRoute();

const props = defineProps<{
  visible: boolean;
}>();

const emit = defineEmits<{
  (e: 'update:visible', value: boolean): void;
  (e: 'confirm', value: any): void;
}>();

const visibleModel = computed({
  get: () => props.visible,
  set: (v: boolean) => emit('update:visible', v),
});

const scadaType = ref(3);
const formRef = ref<any>(null);

const form = reactive<any>({
  formule: '',
  variates: [],
});

const rules = reactive({
  formule: [
    {
      required: true,
      message: proxy.$t('scada.topo.components.propertyBar.calcVariateDialog.205053-6'),
      trigger: 'change',
    },
    {
      validator: validateFormule,
      trigger: 'change',
    },
  ],
});

const deviceList = ref<any[]>([]);
const numModelList = ref<any[][]>([]);
const modelList = ref<any[]>([]);
const curForm = ref<any>(null);

function handleOpened() {
  scadaType.value = getScadaRouteType(route.query);
  if (scadaType.value === 1) getBindVariables();
  if (scadaType.value === 2) getBindDataSources();
  if (scadaType.value === 3) getBindDeviceList();
  resetVariable();
}

function resetVariable() {
  let obj = {
    formule: '',
    variates: [],
  };
  if (curForm.value && (curForm.value.formule || curForm.value.variates?.length)) {
    obj = {
      formule: curForm.value.formule || '',
      variates: JSON.parse(JSON.stringify(curForm.value.variates || [])),
    };
    if (scadaType.value === 2 || scadaType.value === 3) {
      const guid = getRouteQueryString(route.query, 'guid');
      obj.variates.forEach((item: any, index: number) => {
        let params: any = {
          scadaGuid: guid,
          type: scadaType.value,
          page: 1,
          size: 9999,
          t: new Date().getTime(),
        };
        if (scadaType.value === 2) params = { ...params, sceneModelDeviceId: item.sceneModelDeviceId };
        if (scadaType.value === 3) params = { ...params, serialNumber: item.serialNumber };
        getListVariable(params).then((res: any) => {
          if (res.code === 200) {
            numModelList.value[index] = res.rows;
          }
        });
      });
    }
  }
  Object.assign(form, JSON.parse(JSON.stringify(obj)));
  nextTick(() => {
    formRef.value?.clearValidate();
  });
}

function getBindVariables() {
  const params = {
    scadaGuid: getRouteQueryString(route.query, 'guid'),
    type: scadaType.value,
    page: 1,
    size: 9999,
  };
  getListVariable(params).then((res: any) => {
    if (res.code === 200) modelList.value = res.rows;
  });
}

function getBindDeviceList() {
  const params = {
    scadaGuid: getRouteQueryString(route.query, 'guid'),
    pageNum: 1,
    pageSize: 9999,
  };
  listDeviceBind(params).then((res: any) => {
    if (res.code === 200) deviceList.value = res.rows;
  });
}

function getBindDataSources() {
  const params = {
    sceneModelId: getRouteQueryNumber(route.query, 'sceneModelId', 0),
    pageNum: 1,
    pageSize: 9999,
  };
  getSceneModelDeviceList(params).then((res: any) => {
    if (res.code === 200) deviceList.value = res.rows;
  });
}

function handleAddFormula() {
  const length = form.variates.length;
  let alias = 'A';
  if (length > 0) {
    const item = form.variates[length - 1];
    const num = item.alias.charCodeAt() + 1;
    alias = String.fromCharCode(num);
  }
  form.variates.push({
    alias,
    serialNumber: '',
    identifier: null,
  });
  if (scadaType.value === 2 || scadaType.value === 3) numModelList.value.push([]);
  form.formule = form.variates.map((item: any) => item.alias).join('');
}

function handleDataSourcesChange(scope: any, val: any) {
  form.variates[scope.$index] = { ...scope.row, identifier: null };
  let params: any = {
    scadaGuid: getRouteQueryString(route.query, 'guid'),
    type: scadaType.value,
    page: 1,
    size: 9999,
  };
  if (scadaType.value === 2) params = { ...params, sceneModelDeviceId: val };
  if (scadaType.value === 3) params = { ...params, serialNumber: val };
  getListVariable(params).then((res: any) => {
    if (res.code === 200) numModelList.value[scope.$index] = res.rows;
    else proxy.$message.error(res.msg);
  });
}

function handleVariableChange(scope: any, val: any) {
  if (scadaType.value === 1) {
    const vari = modelList.value.find((f) => f.identifier === val);
    form.variates[scope.$index] = {
      ...scope.row,
      modelName: vari?.modelName,
      serialNumber: '',
      modelValue: 0,
    };
  } else {
    const vari = numModelList.value[scope.$index]?.find((f) => f.identifier === val);
    form.variates[scope.$index] = {
      ...scope.row,
      productId: vari?.productId,
      sceneModelId: vari?.sceneModelId,
      modelName: vari?.modelName,
      serialNumber: vari?.serialNumber,
      variableType: vari?.variableType,
      modelValue: 0,
    };
  }
  formRef.value?.clearValidate('formule');
}

function handleDeleteFormula(index: number) {
  form.variates.splice(index, 1);
  if (scadaType.value === 2 || scadaType.value === 3) numModelList.value.splice(index, 1);
  form.formule = form.variates.map((item: any) => item.alias).join('');
}

function validateFormule(_: any, value: any, callback: any) {
  let tempFormule = value;
  if (form.variates.length === 0) {
    callback(new Error(proxy.$t('scada.topo.components.propertyBar.calcVariateDialog.205053-13')));
    return;
  }
  if (!value) {
    callback(new Error(proxy.$t('scada.topo.components.propertyBar.calcVariateDialog.205053-6')));
    return;
  }
  if (String(value).length >= 200) {
    callback(new Error(proxy.$t('scada.topo.components.propertyBar.calcVariateDialog.205053-16')));
    return;
  }

  let isFormule = true;
  try {
    const letters: string[] = [];
    form.variates.forEach((e: any) => {
      const t = '${' + e.identifier + '}';
      letters.push(e.alias);
      tempFormule = replaceAll(tempFormule, e.alias, t);
    });
    let evalFormule = tempFormule.replace(/\$\{.*?\}/g, '1');
    evalFormule = String(eval(evalFormule));
    const reg = /[0-9]{1}[A-Za-z]{1}|[A-Za-z]{2}|[A-Za-z]{1}[0-9]{1}/g;
    if (reg.test(form.formule)) {
      isFormule = false;
    } else {
      const beforeReg = /^(?:[+]|[-]|[*]|[/]){1}$/;
      if (evalFormule.slice(0, 1) === '-') {
        evalFormule = evalFormule.slice(1);
      }
      isFormule = !beforeReg.test(evalFormule.slice(0, 1));
    }
    const isInclude = letters.some((e) => value.includes(e));
    if (!isInclude) isFormule = false;
  } catch (e) {
    isFormule = false;
  }

  if (isFormule) callback();
  else callback(new Error(proxy.$t('scada.topo.components.propertyBar.calcVariateDialog.205053-15')));
}

function replaceAll(str: string, s1: string, s2: string) {
  return str.replace(new RegExp(s1, 'gm'), s2);
}

function handleConfirm() {
  formRef.value?.validate((valid: boolean) => {
    if (valid) emit('confirm', { ...form });
  });
}

function handleClose() {
  emit('update:visible', false);
}

defineExpose({ curForm });
</script>

<style lang="scss" scoped>
.property-bar-calc-variate-dialog {
  width: 100%;

  .variate-content {
    width: 100%;

    .tip-wrap {
      width: 100%;

      p {
        line-height: 20px;
      }
    }

    .form-wrap {
      width: 100%;
      margin-top: 10px;

      :deep(.el-form-item) {
        margin-bottom: 17px;
      }

      .table-wrap {
        :deep(.el-form-item) {
          margin-bottom: 0;
        }
      }

      .alias-wrap {
        width: 28px;
        height: 28px;
        background-image: linear-gradient(180deg, #6fb0ff, #3c78ff);
        box-shadow: 0 0 4px 0 rgba(0, 0, 0, 0.2);
        font-size: 12px;
        font-weight: 400;
        line-height: 28px;
        text-align: center;
        margin-top: 2px;
        color: #fff;
      }

      .add-formula-btn {
        color: var(--el-color-primary);

        :deep(.el-icon) {
          color: var(--el-color-primary);
        }
      }
    }
  }
}
</style>
