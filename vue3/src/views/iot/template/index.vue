<template>
  <div class="iot-template">
    <el-card v-show="showSearch" style="margin-bottom: 15px; border-radius: 8px; width: 100%">
      <el-form
        @submit.prevent
        :model="queryParams"
        ref="queryFormRef"
        :inline="true"
        label-width="46px"
        style="margin-bottom: -18px; padding: 3px 0 0 0"
      >
        <el-form-item prop="templateName">
          <el-input
            v-model="queryParams.templateName"
            :placeholder="$t('template.index.891112-1')"
            clearable
            style="width: 192px"
            @keyup.enter="handleQuery"
          />
        </el-form-item>
        <el-form-item prop="type">
          <el-select
            style="width: 192px"
            v-model="queryParams.type"
            :placeholder="$t('template.index.891112-3')"
            clearable
          >
            <el-option v-for="dict in iot_things_type" :key="dict.value" :label="dict.label" :value="dict.value" />
          </el-select>
        </el-form-item>
        <el-form-item prop="isSys">
          <el-select
            style="width: 192px"
            v-model="queryParams.isSys"
            :placeholder="$t('template.index.891112-124')"
            clearable
          >
            <el-option v-for="dict in system_type_status" :key="dict.value" :label="dict.label" :value="dict.value" />
          </el-select>
        </el-form-item>
        <div style="float: right">
          <el-button type="primary" :icon="Search" @click="handleQuery">{{ $t('search') }}</el-button>
          <el-button :icon="Refresh" @click="resetQuery">{{ $t('reset') }}</el-button>
        </div>
      </el-form>
    </el-card>

    <el-card style="border-radius: 8px">
      <el-row :gutter="10" style="margin-bottom: 16px">
        <el-col :span="1.5">
          <el-button type="primary" plain :icon="Plus" @click="handleAdd" v-hasPermi="['iot:template:add']">
            {{ $t('add') }}
          </el-button>
        </el-col>
        <el-col :span="1.5">
          <el-button plain :icon="Upload" @click="handleImport" v-hasPermi="['iot:model:import']">
            {{ $t('product.product-things-model.142341-126') }}
          </el-button>
        </el-col>
        <el-col :span="1.5">
          <el-button
            plain
            :icon="Delete"
            :disabled="multiple"
            @click="handleDelete"
            v-hasPermi="['iot:template:remove']"
          >
            {{ $t('del') }}
          </el-button>
        </el-col>
        <right-toolbar v-model:showSearch="showSearch" @queryTable="getList" />
      </el-row>

      <el-table
        v-loading="loading"
        :data="templateList"
        :border="false"
        @selection-change="handleSelectionChange"
        :row-key="getRowKeys"
        ref="multipleTableRef"
      >
        <el-table-column :reserve-selection="true" type="selection" width="55" align="center" />
        <el-table-column
          :label="$t('template.index.891112-0')"
          align="left"
          prop="templateName"
          min-width="170"
          show-overflow-tooltip
        />
        <el-table-column :label="$t('template.index.891112-7')" align="left" prop="identifier" min-width="140" />
        <el-table-column :label="$t('template.index.891112-8')" align="center" prop="isMonitor" width="85">
          <template #default="scope">
            <dict-tag :options="iot_yes_no" :value="scope.row.isChart" />
          </template>
        </el-table-column>
        <el-table-column :label="$t('template.index.891112-11')" align="center" prop="" width="85">
          <template #default="scope">
            <dict-tag :options="iot_yes_no" :value="scope.row.isHistory" />
          </template>
        </el-table-column>
        <el-table-column :label="$t('template.index.891112-13')" align="center" prop="type" width="100">
          <template #default="scope">
            <dict-tag :options="iot_things_type" :value="scope.row.type" />
          </template>
        </el-table-column>
        <el-table-column :label="$t('template.index.891112-14')" align="center" prop="datatype" width="80">
          <template #default="scope">
            <dict-tag :options="iot_data_type" :value="scope.row.datatype" />
          </template>
        </el-table-column>
        <el-table-column :label="$t('template.index.891112-124')" align="center" prop="isSys" min-width="80">
          <template #default="scope">
            <dict-tag :options="system_type_status" :value="scope.row.isSys" />
          </template>
        </el-table-column>
        <el-table-column
          :label="$t('template.index.891112-15')"
          align="left"
          prop="specs"
          min-width="270"
          show-overflow-tooltip
        >
          <template #default="scope">
            <el-space :size="5" alignment="center">
              <component v-for="(item, index) in getSpecsDisplay(scope.row)" :key="index" :is="ElTag" v-bind="item">
                {{ item.text }}
              </component>
            </el-space>
          </template>
        </el-table-column>
        <el-table-column :label="$t('template.index.891112-117')" align="left" prop="tenantName" width="150" />
        <el-table-column :label="$t('template.index.891112-118')" align="left" prop="createBy" width="150" />
        <el-table-column :label="$t('template.index.891112-17')" align="center" prop="createTime" width="160" />
        <el-table-column
          fixed="right"
          :label="$t('opation')"
          align="center"
          class-name="small-padding fixed-width"
          width="150"
        >
          <template #default="scope">
            <el-button
              size="small"
              :icon="View"
              link
              @click="handleUpdate(scope.row)"
              v-hasPermi="['iot:template:query']"
              v-if="scope.row.isSys === '0' ? true : !isTenant"
            >
              {{ $t('look') }}
            </el-button>
            <el-button
              size="small"
              link
              :icon="Delete"
              @click="handleDelete(scope.row)"
              v-hasPermi="['iot:template:remove']"
              v-if="scope.row.isSys === '0' ? true : !isTenant"
            >
              {{ $t('del') }}
            </el-button>
            <span style="font-size: 12px; color: #999; margin-left: 10px" v-if="scope.row.isSys == '1' && isTenant">
              {{ $t('template.index.891112-21') }}
            </span>
          </template>
        </el-table-column>
      </el-table>

      <pagination
        style="margin-bottom: 20px"
        v-show="total > 0"
        :total="total"
        v-model:page="queryParams.pageNum"
        v-model:limit="queryParams.pageSize"
        @pagination="getList"
      />
    </el-card>

    <!-- 添加或修改通用物模型对话框 -->
    <el-dialog :title="title" v-model="open" width="600px" append-to-body>
      <el-form ref="formRef" :model="form" :rules="rules" label-width="100px">
        <el-form-item :label="$t('template.index.891112-22')" prop="templateName">
          <el-input v-model="form.templateName" :placeholder="$t('template.index.891112-23')" style="width: 400px" />
        </el-form-item>
        <el-form-item :label="$t('template.index.891112-121')" prop="templateName_en_US">
          <el-input
            v-model="form.templateName_en_US"
            :placeholder="$t('template.index.891112-122')"
            style="width: 400px"
          />
        </el-form-item>
        <el-form-item :label="$t('template.index.891112-24')" prop="identifier">
          <el-input v-model="form.identifier" :placeholder="$t('template.index.891112-25')" style="width: 400px" />
        </el-form-item>
        <el-form-item :label="$t('template.index.891112-26')" prop="modelOrder">
          <el-input-number
            controls-position="right"
            v-model="form.modelOrder"
            :placeholder="$t('template.index.891112-27')"
            style="width: 400px"
          />
        </el-form-item>
        <el-form-item :label="$t('template.index.891112-28')" prop="type">
          <el-radio-group v-model="form.type" @change="typeChange(form.type)">
            <el-radio-button v-for="dict in iot_things_type" :key="dict.value" :value="Number(dict.value)">
              {{ dict.label }}
            </el-radio-button>
          </el-radio-group>
        </el-form-item>
        <el-form-item :label="$t('template.index.891112-32')" prop="property">
          <el-row>
            <el-col :span="6" v-if="form.type == 1">
              <el-tooltip effect="dark" :content="$t('template.index.891112-33')" placement="top">
                <el-checkbox
                  name="isChart"
                  :label="$t('template.index.891112-8')"
                  @change="isChartChange"
                  v-model="form.isChart"
                  :true-value="1"
                  :false-value="0"
                ></el-checkbox>
              </el-tooltip>
            </el-col>
            <el-col :span="6" v-if="form.type == 1">
              <el-tooltip effect="dark" :content="$t('template.index.891112-34')" placement="top">
                <el-checkbox
                  name="isMonitor"
                  :label="$t('template.index.891112-9')"
                  @change="isMonitorChange"
                  v-model="form.isMonitor"
                  :true-value="1"
                  :false-value="0"
                ></el-checkbox>
              </el-tooltip>
            </el-col>
            <el-col :span="6">
              <el-tooltip effect="dark" :content="$t('template.index.891112-35')" placement="top">
                <el-checkbox
                  name="isReadonly"
                  :label="$t('template.index.891112-36')"
                  @change="isReadonlyChange"
                  :disabled="form.type == 3"
                  v-model="form.isReadonly"
                  :true-value="1"
                  :false-value="0"
                  style="margin-right: 100px"
                ></el-checkbox>
              </el-tooltip>
            </el-col>
            <el-col :span="6">
              <el-tooltip effect="dark" :content="$t('template.index.891112-37')" placement="top">
                <el-checkbox
                  name="isHistory"
                  :label="$t('template.index.891112-11')"
                  v-model="form.isHistory"
                  :true-value="1"
                  :false-value="0"
                  style="margin-right: 100px"
                ></el-checkbox>
              </el-tooltip>
            </el-col>
            <el-col :span="6">
              <el-tooltip effect="dark" :content="$t('template.index.891112-38')" placement="top">
                <el-checkbox
                  name="isSharePerm"
                  :label="$t('template.index.891112-39')"
                  v-model="form.isSharePerm"
                  :true-value="1"
                  :false-value="0"
                ></el-checkbox>
              </el-tooltip>
            </el-col>
          </el-row>
        </el-form-item>

        <el-divider></el-divider>
        <el-form-item :label="$t('template.index.891112-14')" prop="datatype">
          <el-select v-model="form.datatype" :placeholder="$t('template.index.891112-40')" style="width: 183px">
            <el-option
              v-for="dict in iot_data_type"
              :key="dict.value"
              :label="dict.label"
              :value="dict.value"
              :disabled="
                form.isChart == 1 &&
                (dict.value == 'bool' ||
                  dict.value == 'enum' ||
                  dict.value == 'string' ||
                  dict.value == 'array' ||
                  dict.value == 'object')
              "
            ></el-option>
          </el-select>
        </el-form-item>
        <div v-if="form.datatype == 'integer' || form.datatype == 'decimal'">
          <el-form-item label="">
            <template #label>
              <span>
                <el-tooltip effect="dark" :content="$t('template.index.891112-119')" placement="top">
                  <el-icon><QuestionFilled /></el-icon>
                </el-tooltip>

                {{ $t('template.index.891112-48') }}
              </span>
            </template>
            <el-row style="width: 400px">
              <el-col :span="11">
                <el-input v-model="form.specs.min" :placeholder="$t('template.index.891112-49')" type="number" />
              </el-col>
              <el-col :span="2" class="text-center">{{ $t('template.index.891112-50') }}</el-col>
              <el-col :span="11">
                <el-input v-model="form.specs.max" :placeholder="$t('template.index.891112-51')" type="number" />
              </el-col>
            </el-row>
          </el-form-item>
          <el-form-item :label="$t('template.index.891112-52')">
            <el-input v-model="form.specs.unit" :placeholder="$t('template.index.891112-53')" style="width: 400px" />
          </el-form-item>
          <el-form-item :label="$t('template.index.891112-54')">
            <el-input-number
              controls-position="right"
              v-model="form.specs.step"
              :placeholder="$t('template.index.891112-55')"
              style="width: 400px"
            />
          </el-form-item>
        </div>
        <div v-if="form.datatype == 'bool'">
          <el-form-item :label="$t('template.index.891112-56')" prop="">
            <el-row style="margin-bottom: 10px">
              <el-col :span="9">
                <el-input v-model="form.specs.falseText" :placeholder="$t('template.index.891112-57')" />
              </el-col>
              <el-col :span="10" :offset="1">{{ $t('template.index.891112-58') }}</el-col>
            </el-row>
            <el-row>
              <el-col :span="9">
                <el-input v-model="form.specs.trueText" :placeholder="$t('template.index.891112-59')" />
              </el-col>
              <el-col :span="10" :offset="1">{{ $t('template.index.891112-60') }}</el-col>
            </el-row>
          </el-form-item>
        </div>
        <div v-if="form.datatype == 'enum'">
          <el-form-item :label="$t('template.index.891112-61')">
            <el-select v-model="form.specs.showWay" :placeholder="$t('template.index.891112-62')" style="width: 175px">
              <el-option key="select" :label="$t('template.index.891112-63')" value="select"></el-option>
              <el-option key="button" :label="$t('template.index.891112-64')" value="button"></el-option>
            </el-select>
          </el-form-item>
          <el-form-item :label="$t('template.index.891112-65')" prop="">
            <el-row v-for="(item, index) in form.specs.enumList" :key="'enum' + index" style="margin-bottom: 10px">
              <el-col :span="9">
                <el-input v-model="item.value" :placeholder="$t('template.index.891112-66')" />
              </el-col>
              <el-col :span="11" :offset="1">
                <el-input v-model="item.text" :placeholder="$t('template.index.891112-67')" />
              </el-col>
              <el-col :span="2" :offset="1" v-if="index != 0">
                <a style="color: #f56c6c" @click="removeEnumItem(index)">{{ $t('del') }}</a>
              </el-col>
            </el-row>
            <div>
              +
              <a style="color: #409eff" @click="addEnumItem()">{{ $t('template.index.891112-68') }}</a>
            </div>
          </el-form-item>
        </div>
        <div v-if="form.datatype == 'string'">
          <el-form-item :label="$t('template.index.891112-69')" prop="">
            <el-row>
              <el-col :span="9">
                <el-input v-model="form.specs.maxLength" :placeholder="$t('template.index.891112-70')" type="number" />
              </el-col>
              <el-col :span="14" :offset="1">{{ $t('template.index.891112-71') }}</el-col>
            </el-row>
          </el-form-item>
        </div>
        <div v-if="form.datatype == 'array'">
          <el-form-item :label="$t('template.index.891112-72')" prop="">
            <el-row>
              <el-col :span="9">
                <el-input
                  v-model="form.specs.arrayCount"
                  :placeholder="$t('template.index.891112-73')"
                  type="number"
                  @input="handleChangeCount"
                />
              </el-col>
            </el-row>
          </el-form-item>
          <el-form-item
            :label="$t('template.index.891112-115')"
            prop=""
            v-if="form.specs.arrayCount > 0 && (form.specs.arrayIndex || form.templateId == null)"
          >
            <template #label>
              <span>
                <el-tooltip style="cursor: pointer" effect="light" placement="top">
                  <template #content>{{ $t('template.index.891112-116') }}</template>
                  <el-icon><QuestionFilled /></el-icon>
                </el-tooltip>
              </span>
              {{ $t('template.index.891112-115') }}
            </template>
            <div v-for="(tag, index) in arrayModelList" :key="index" style="display: inline-block">
              <el-input
                v-model="arrayModelList[index]"
                @keyup.enter="editTag(index)"
                @blur="editTag(index)"
                size="small"
                style="width: 80px; margin-right: 10px; display: inline-block"
                type="number"
                oninput="
                  if (value > 10000) value = 10000;
                  if (value < 0) value = 0;
                "
                class="custom-input"
              ></el-input>
            </div>
          </el-form-item>
          <el-form-item :label="$t('template.index.891112-74')" prop="">
            <el-radio-group v-model="form.specs.arrayType">
              <el-radio v-for="dict in model_array_type" :key="dict.value" :value="dict.value">
                {{ dict.label }}
              </el-radio>
            </el-radio-group>
          </el-form-item>
          <el-form-item :label="$t('product.product-things-model.142341-82')" v-if="form.specs.arrayType == 'object'">
            <div style="background-color: #f8f8f8; border-radius: 5px">
              <el-row style="padding: 0 10px 5px" v-for="(item, index) in form.specs.params" :key="index">
                <div style="margin-top: 5px" v-if="index == 0"></div>
                <el-col :span="18">
                  <el-input
                    readonly
                    v-model="item.name"
                    size="small"
                    :placeholder="$t('product.product-things-model.142341-83')"
                    style="margin-top: 3px"
                  >
                    <template #prepend>
                      <el-tag size="small" effect="dark" style="margin-left: -21px; height: 26px; line-height: 26px">
                        {{ item.order }}
                      </el-tag>
                      {{ form.identifier + '_' + item.id }}
                    </template>
                    <template #append>
                      <el-button @click="editParameter(item, index)" size="small">{{ $t('edit') }}</el-button>
                    </template>
                  </el-input>
                </el-col>
                <el-col :span="2" :offset="2">
                  <el-button
                    size="small"
                    plain
                    type="danger"
                    style="padding: 5px"
                    :icon="Delete"
                    @click="removeParameter(index)"
                  >
                    {{ $t('del') }}
                  </el-button>
                </el-col>
              </el-row>
            </div>
            <div>
              +
              <a style="color: #486ff2" @click="addParameter()">{{ $t('product.product-things-model.142341-85') }}</a>
            </div>
          </el-form-item>
        </div>
        <div v-if="form.datatype == 'object'">
          <el-form-item :label="$t('template.index.891112-75')" prop="">
            <div style="background-color: #f8f8f8; border-radius: 5px">
              <el-row style="padding: 0 10px 5px" v-for="(item, index) in form.specs.params" :key="index">
                <div style="margin-top: 5px" v-if="index == 0"></div>
                <el-col :span="18">
                  <el-input
                    readonly
                    v-model="item.name"
                    size="small"
                    :placeholder="$t('template.index.891112-76')"
                    style="margin-top: 3px"
                  >
                    <template #prepend>
                      <el-tag size="small" effect="dark" style="margin-left: -21px; height: 26px; line-height: 26px">
                        {{ item.order }}
                      </el-tag>
                      {{ form.identifier + '_' + item.id }}
                    </template>
                    <template #append>
                      <el-button @click="editParameter(item, index)">{{ $t('edit') }}</el-button>
                    </template>
                  </el-input>
                </el-col>
                <el-col :span="2" :offset="2">
                  <el-button
                    size="small"
                    plain
                    type="danger"
                    style="padding: 5px"
                    :icon="Delete"
                    @click="removeParameter(index)"
                  >
                    {{ $t('del') }}
                  </el-button>
                </el-col>
              </el-row>
            </div>
            <div>
              +
              <a style="color: #486ff2" @click="addParameter()">{{ $t('template.index.891112-78') }}</a>
            </div>
          </el-form-item>
        </div>
      </el-form>

      <template #footer>
        <div class="dialog-footer">
          <el-button
            type="primary"
            @click="handleSubmitForm"
            v-hasPermi="['iot:template:edit']"
            v-show="form.templateId"
          >
            {{ $t('update') }}
          </el-button>
          <el-button
            type="primary"
            @click="handleSubmitForm"
            v-hasPermi="['iot:template:add']"
            v-show="!form.templateId"
          >
            {{ $t('add') }}
          </el-button>
          <el-button @click="handleCancel">{{ $t('cancel') }}</el-button>
        </div>
      </template>
    </el-dialog>

    <!--物模型参数类型-->
    <things-parameter :data="paramData" @dataEvent="getParamData($event)" />
    <!-- 批量导入 -->
    <ImportBatch ref="importBatchRef" />
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted, getCurrentInstance, type ComponentInternalInstance } from 'vue';
import { Search, Refresh, Plus, Delete, View, QuestionFilled, Upload } from '@element-plus/icons-vue';
import { ElTag, ElMessage, ElMessageBox } from 'element-plus';
import { useI18n } from 'vue-i18n';
import { listTemplate, getTemplate, delTemplate, addTemplate, updateTemplate } from '@/api/iot/template';
import { mergeObjects } from '@/utils/index';
import { getUserId } from '@/utils/auth';
import thingsParameter from './parameter.vue';
import ImportBatch from '../product/components/batchImportThingsModel.vue';
import { useDict } from '@/utils/dict';
import { useUserStore } from '@/stores/modules/user';

const { iot_things_type, iot_data_type, iot_yes_no, system_type_status, model_array_type } = useDict(
  'iot_things_type',
  'iot_data_type',
  'iot_yes_no',
  'system_type_status',
  'model_array_type'
);
const { proxy } = getCurrentInstance() as any;
const userStore = useUserStore();

const isTenant = ref(false);
const loading = ref(true);
const ids = ref<any[]>([]);
const single = ref(true);
const multiple = ref(true);
const showSearch = ref(true);
const total = ref(0);
const templateList = ref<any[]>([]);
const title = ref('');
const open = ref(false);
const queryFormRef = ref<any>(null);
const formRef = ref<any>(null);
const multipleTableRef = ref<any>(null);
const sysUserId = ref<any>(0);
const importBatchRef = ref();
const { t } = useI18n();

const queryParams = reactive({
  pageNum: 1,
  pageSize: 10,
  templateName: null as string | null,
  type: null,
  isSys: null,
});

const formParams = {
  templateName: '',
  templateName_en_US: '',
  identifier: '',
  modelOrder: 0,
  type: 1,
  isChart: 1,
  isHistory: 1,
  isMonitor: 1,
  isReadonly: 1,
  isSharePerm: 1,
  datatype: 'integer',
  specs: {
    arrayType: 'integer',
    enumList: [] as any[],
    showWay: 'select',
    arrayCount: 5,
    params: [] as any[],
  },
};
const form = ref<any>({});
const arrayModelList = ref<any[]>([]);
let modelCount = 5;
const tags = ref<any[]>([]);
const isEditing = ref<any[]>([]);
const newTag = ref<any[]>([]);
const paramData = ref<any>({ index: -1, parameter: {} });

const rules = reactive<any>({
  templateName: [{ required: true, message: t('template.index.891112-84'), trigger: 'blur' }],
  identifier: [
    { required: true, message: t('template.index.891112-85'), trigger: 'blur' },
    { validator: validateInput, trigger: 'blur' },
  ],
  modelOrder: [{ required: true, message: t('template.index.891112-86'), trigger: 'blur' }],
  type: [{ required: true, message: t('template.index.891112-87'), trigger: 'change' }],
  datatype: [{ required: true, message: t('template.index.891112-88'), trigger: 'change' }],
  templateName_en_US: [{ required: true, message: t('template.index.891112-123'), trigger: 'blur' }],
});

onMounted(() => {
  getList();
  init();
  if (modelCount) {
    arrayModelList.value = Array.from({ length: modelCount }, (_, i) => i);
  }
});

function init() {
  if (userStore.roles.indexOf('tenant') !== -1) {
    isTenant.value = true;
  }
  sysUserId.value = getUserId();
}

function getList() {
  loading.value = true;
  listTemplate(queryParams).then((response: any) => {
    templateList.value = response.rows;
    total.value = response.total;
    loading.value = false;
  });
}

function validateInput(rule: any, value: string, callback: Function) {
  if (!value || !value.trim() || /\s/.test(value)) {
    callback(new Error(t('template.index.891112-114')));
  } else {
    callback();
  }
}

function toNumberOrNull(value: any) {
  if (value === null || value === undefined || value === '') return null;
  const numberValue = Number(value);
  return Number.isNaN(numberValue) ? null : numberValue;
}

function normalizeNumberFields(data: any) {
  data.modelOrder = toNumberOrNull(data.modelOrder);
  if (data.specs) {
    data.specs.step = toNumberOrNull(data.specs.step);
    data.specs.arrayCount = toNumberOrNull(data.specs.arrayCount);
  }
  return data;
}

function handleCancel() {
  open.value = false;
  reset();
}

function handleImport() {
  importBatchRef.value?.upload && (importBatchRef.value.upload.importDeviceDialog = true);
}

function handleChangeCount() {
  modelCount = form.value.specs.arrayCount;
  if (form.value.specs.arrayCount) {
    arrayModelList.value = Array.from({ length: form.value.specs.arrayCount }, (_, i) => i);
  }
}

function editTag(idx: number) {
  isEditing.value[idx] = true;
  newTag.value[idx] = arrayModelList.value[idx];
}

function reset() {
  form.value = normalizeNumberFields(JSON.parse(JSON.stringify(formParams)));
  formRef.value?.resetFields();
}

function handleQuery() {
  queryParams.pageNum = 1;
  getList();
}

function resetQuery() {
  queryFormRef.value?.resetFields();
  handleQuery();
}

function handleSelectionChange(selection: any[]) {
  ids.value = selection.map((item) => item.templateId);
  single.value = selection.length !== 1;
  multiple.value = !selection.length;
}

function handleAdd() {
  reset();
  modelCount = 5;
  arrayModelList.value = Array.from({ length: modelCount }, (_, i) => i);
  open.value = true;
  title.value = t('template.index.891112-89');
}

function handleUpdate(row: any) {
  reset();
  const templateId = row.templateId || ids.value;
  getTemplate(templateId).then((res: any) => {
    const { code, data } = res;
    if (code === 200) {
      open.value = true;
      title.value = t('template.index.891112-90');
      let tempData = { ...data };
      tempData.specs = JSON.parse(tempData.specs);
      if (!tempData.specs.enumList) {
        tempData.specs.showWay = 'select';
        tempData.specs.enumList = [{ value: '', text: '' }];
      }
      if (!tempData.specs.arrayType) {
        tempData.specs.arrayType = 'integer';
      }
      if (!tempData.specs.arrayCount) {
        tempData.specs.arrayCount = 5;
      }
      if (!tempData.specs.params) {
        tempData.specs.params = [];
      }
      if (
        (tempData.specs.type === 'array' && tempData.specs.arrayType === 'object') ||
        tempData.specs.type === 'object'
      ) {
        for (let i = 0; i < tempData.specs.params.length; i++) {
          tempData.specs.params[i].id = String(tempData.specs.params[i].id).substring(
            String(tempData.identifier).length + 1
          );
        }
      }
      form.value = normalizeNumberFields({ ...tempData });
    }
  });
}

function handleSubmitForm() {
  formRef.value?.validate((valid: boolean) => {
    if (valid) {
      normalizeNumberFields(form.value);
      const { templateId, ...fo } = form.value;
      const { specs, ...fop } = formParams;
      if (fo.datatype === 'integer' || fo.datatype === 'decimal') {
        const { max, min, step, unit } = fo.specs;
        fo.specs = { type: fo.datatype, max: max || 100, min: min || 0, step: step || 1, unit: unit || '' };
      } else if (fo.datatype === 'string') {
        fo.specs = { type: fo.datatype, maxLength: fo.specs.maxLength || 1024 };
      } else if (fo.datatype === 'bool') {
        fo.specs = {
          type: fo.datatype,
          falseText: fo.specs.falseText || '关闭',
          trueText: fo.specs.trueText || '打开',
        };
      } else if (fo.datatype === 'enum') {
        fo.specs = { type: fo.datatype, showWay: fo.specs.showWay, enumList: fo.specs.enumList };
      } else if (fo.datatype === 'array') {
        fo.specs = {
          type: fo.datatype,
          arrayIndex: arrayModelList.value.map((item: any) => Number(item)),
          arrayType: fo.specs.arrayType,
          arrayCount: fo.specs.arrayCount,
        };
        if (fo.specs.arrayType === 'object') {
          let list = JSON.parse(JSON.stringify(fo.specs.params || form.value.specs?.params || []));
          for (let i = 0; i < list.length; i++) {
            list[i].id = fo.identifier + '_' + list[i].id;
          }
          fo.specs.params = list;
        }
      } else if (fo.datatype === 'object') {
        let list = JSON.parse(JSON.stringify(fo.specs.params));
        for (let i = 0; i < list.length; i++) {
          list[i].id = fo.identifier + '_' + list[i].id;
        }
        fo.specs = { type: fo.datatype, params: list };
      }
      let tempData = mergeObjects(fop, fo);
      tempData.specs = fo.specs;
      if (tempData.datatype == 'object' || (tempData.datatype == 'array' && tempData.specs.arrayType == 'object')) {
        if (!tempData.specs.params || tempData.specs.params.length === 0) {
          ElMessage.error(t('template.index.891112-91'));
          return;
        }
        if (/_/.test(tempData.identifier)) {
          ElMessage.error(t('template.index.891112-92'));
          return;
        }
      }
      if (tempData.specs.params && tempData.specs.params.length > 0) {
        let arr = tempData.specs.params.map((item: any) => item.id).sort();
        for (let i = 0; i < arr.length; i++) {
          if (arr[i] == arr[i + 1]) {
            ElMessage.error(t('template.index.891112-93') + ' ' + arr[i] + ' ' + t('template.index.891112-120'));
            return;
          }
        }
      }
      if (
        tempData.isChart == 1 &&
        tempData.datatype != 'integer' &&
        tempData.isChart == 1 &&
        tempData.datatype != 'decimal'
      ) {
        ElMessage.error(t('template.index.891112-95'));
      }
      if (templateId === undefined || templateId === null || templateId === '') {
        let tempForm = JSON.parse(JSON.stringify(tempData));
        tempForm.specs = JSON.stringify(tempForm.specs);
        tempForm.createBy = userStore.name;
        addTemplate(tempForm).then((res: any) => {
          if (res.code === 200) {
            ElMessage({
              message: t('addSuccess'),
              type: 'success',
            });
            getList();
          }
          open.value = false;
        });
      } else {
        let tempForm = JSON.parse(JSON.stringify(tempData));
        tempForm.specs = JSON.stringify(tempForm.specs);
        tempForm.templateId = templateId;
        tempForm.updateBy = userStore.name;
        const { isSys, tenantId, createBy } = form.value;
        tempForm.isSys = isSys;
        tempForm.tenantId = tenantId;
        tempForm.createBy = createBy;
        updateTemplate(tempForm).then((res: any) => {
          if (res.code === 200) {
            ElMessage({
              message: t('updateSuccess'),
              type: 'success',
            });
            getList();
          }
          open.value = false;
        });
      }
    }
  });
}

function handleDelete(row?: any) {
  const templateIds = row?.templateId || ids.value;
  ElMessageBox.confirm(t('template.index.891112-98', [templateIds]), {
    type: 'warning',
  })
    .then(() => {
      return delTemplate(templateIds);
    })
    .then(() => {
      getList();
      ElMessage({
        message: t('delSuccess'),
        type: 'success',
      });
      multipleTableRef.value?.clearSelection();
    })
    .catch(() => {});
}

function typeChange(type: number) {
  if (type == 1) {
    Object.assign(form.value, {
      isChart: 1,
      isHistory: 1,
      isMonitor: 1,
      isReadonly: 1,
      isSharePerm: 1,
      datatype: 'integer',
    });
  } else if (type == 2) {
    Object.assign(form.value, { isChart: 0, isHistory: 1, isSharePerm: 1, isMonitor: 0, isReadonly: 0 });
  } else if (type == 3) {
    Object.assign(form.value, { isChart: 0, isHistory: 1, isMonitor: 0, isReadonly: 1, isSharePerm: 0 });
  }
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

function addEnumItem() {
  form.value.specs.enumList.push({ value: '', text: '' });
}

function removeEnumItem(idx: number | string) {
  form.value.specs.enumList.splice(idx, 1);
}

function getSpecsDisplay(row: any) {
  const { specs } = row;
  if (!specs) {
    return [];
  }
  const specsobj = JSON.parse(specs);
  if (specsobj.type === 'integer' || specsobj.type === 'decimal') {
    return [
      { type: 'danger', text: `${t('template.index.891112-105')}${specsobj.max || '-'}` },
      { type: 'primary', text: `${t('template.index.891112-106')}${specsobj.min || '-'}` },
      { type: 'info', text: `${t('template.index.891112-107')}${specsobj.step || '-'}` },
      { type: 'success', text: `${t('template.index.891112-108')}${specsobj.unit || '-'}` },
    ];
  } else if (specsobj.type === 'string') {
    return [{ type: 'info', text: `${t('template.index.891112-109')}${specsobj.maxLength || '-'}` }];
  } else if (specsobj.type === 'array') {
    return [
      { type: 'danger', text: `${t('template.index.891112-110')}${specsobj.arrayType || '-'}` },
      { type: 'primary', text: `${t('template.index.891112-111')}${specsobj.arrayCount || '-'}` },
    ];
  } else if (specsobj.type === 'enum' && specsobj.enumList) {
    const items = [];
    specsobj.enumList.forEach((item: any, index: number) => {
      items.push({
        type: index % 2 === 0 ? 'primary' : 'success',
        text: `${item.value}：${item.text}`,
      });
    });
    return items;
  } else if (specsobj.type === 'bool') {
    return [
      { type: 'danger', text: `0：${specsobj.falseText || '-'}` },
      { type: 'primary', text: `1：${specsobj.trueText || '-'}` },
    ];
  } else if (specsobj.type === 'object' && specsobj.params) {
    const items = [];
    specsobj.params.forEach((param: any, index: number) => {
      items.push({
        type: index % 2 === 0 ? 'info' : 'warning',
        text: `${param.name}：${param.datatype?.type || '-'}`,
      });
    });
    return items;
  }
}

function addParameter() {
  paramData.value = { index: -1, parameter: {} };
}

function editParameter(data: any, idx: number | string) {
  paramData.value = null;
  paramData.value = { index: idx, parameter: data };
}

function removeParameter(idx: number | string) {
  form.value.specs.params.splice(idx, 1);
}

function getParamData(data: any) {
  if (data.index == -1) {
    form.value.specs.params.push(data.parameter);
  } else {
    form.value.specs.params[data.index] = data.parameter;
    form.value.specs.params = [...form.value.specs.params];
  }
}

function getRowKeys(row: any) {
  return row.templateId;
}
</script>

<style lang="scss" scoped>
.iot-template {
  padding: 20px;

  .text-center {
    text-align: center;
  }
}
</style>
