<template>
  <div class="ruleengine-list">
    <el-card v-show="showSearch" class="search-card">
      <el-form :model="queryParams" ref="queryFormRef" :inline="true" class="search-form">
        <el-form-item prop="elName">
          <el-input
            v-model="queryParams.elName"
            :placeholder="$t('ruleengine.list.index.807357-0')"
            clearable
            @keyup.enter="handleQuery"
            style="width: 192px"
          />
        </el-form-item>
        <el-form-item prop="enable">
          <el-select
            v-model="queryParams.enable"
            :placeholder="$t('ruleengine.list.index.807357-1')"
            clearable
            style="width: 192px"
          >
            <el-option
              v-for="option in rule_engine_status"
              :key="option.value"
              :label="option.label"
              :value="option.value"
            ></el-option>
          </el-select>
        </el-form-item>
        <div style="float: right">
          <el-button type="primary" :icon="Search" @click="handleQuery">{{ $t('search') }}</el-button>
          <el-button :icon="Refresh" @click="resetQuery">{{ $t('reset') }}</el-button>
        </div>
      </el-form>
    </el-card>

    <el-card>
      <el-row :gutter="10" style="margin-bottom: 15px">
        <el-col :span="1.5">
          <el-button type="primary" plain :icon="Plus" @click="handleAdd" v-hasPermi="['rule:el:add']">
            {{ $t('add') }}
          </el-button>
        </el-col>
        <el-col :span="1.5">
          <el-button plain :icon="Delete" :disabled="multiple" @click="handleDelete" v-hasPermi="['rule:el:remove']">
            {{ $t('del') }}
          </el-button>
        </el-col>
        <right-toolbar v-model:showSearch="showSearch" @queryTable="getList"></right-toolbar>
      </el-row>

      <el-table
        v-loading="loading"
        :data="list"
        :border="false"
        @selection-change="handleSelectionChange"
        ref="multipleTableRef"
        :row-key="(row: any) => row.id"
      >
        <el-table-column :reserve-selection="true" type="selection" width="55" align="center" />
        <el-table-column :label="$t('ruleengine.list.index.807357-2')" align="left" prop="elName" min-width="200" />
        <el-table-column :label="$t('ruleengine.list.index.807357-3')" align="center" prop="productName" width="100">
          <template #default="scope">
            {{ getNodeNum(scope.row.sourceJson) }}
          </template>
        </el-table-column>
        <el-table-column :label="$t('ruleengine.list.index.807357-4')" align="center" prop="enable" width="120">
          <template #default="scope">
            <dict-tag :options="rule_engine_status" :value="scope.row.enable" />
          </template>
        </el-table-column>
        <el-table-column :label="$t('ruleengine.list.index.807357-5')" align="left" prop="remark" min-width="190" />
        <el-table-column :label="$t('ruleengine.list.index.807357-6')" align="center" prop="updateTime" width="160" />
        <el-table-column
          fixed="right"
          :label="$t('opation')"
          align="center"
          class-name="small-padding fixed-width"
          width="280"
        >
          <template #default="scope">
            <el-button
              size="small"
              link
              :icon="CaretRight"
              @click="handleRun(scope.row)"
              v-hasPermi="['iot:scene:run']"
            >
              {{ $t('scene.index.670805-7') }}
            </el-button>
            <el-button
              size="small"
              link
              :icon="ScaleToOriginal"
              @click="handleDesign(scope.row)"
              v-hasPermi="['rule:el:query']"
            >
              {{ $t('design') }}
            </el-button>
            <el-button size="small" link :icon="Edit" @click="handleEdit(scope.row)" v-hasPermi="['rule:el:edit']">
              {{ $t('edit') }}
            </el-button>
            <el-button
              size="small"
              link
              :icon="Delete"
              @click="handleDelete(scope.row)"
              v-hasPermi="['rule:el:remove']"
            >
              {{ $t('del') }}
            </el-button>
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

    <!-- 添加或修改规则引擎对话框 -->
    <el-dialog
      :title="ruleDialogForm.id ? $t('ruleengine.list.index.807357-9') : $t('ruleengine.list.index.807357-8')"
      v-model="isAddRule"
      width="620px"
      append-to-body
      :close-on-click-modal="false"
      :close-on-press-escape="false"
    >
      <el-form ref="ruleDialogFormRef" :model="ruleDialogForm" :rules="ruleDialogRules" label-width="110px">
        <el-form-item :label="$t('ruleengine.list.index.807357-2')" prop="elName">
          <el-input
            v-model="ruleDialogForm.elName"
            :placeholder="$t('ruleengine.list.index.807357-0')"
            style="width: 400px"
          />
        </el-form-item>
        <el-form-item :label="$t('ruleengine.list.index.807357-5')" prop="remark">
          <el-input
            v-model="ruleDialogForm.remark"
            type="textarea"
            :placeholder="$t('product.category.142342-3')"
            :autosize="{ minRows: 3, maxRows: 5 }"
            style="width: 400px"
          />
        </el-form-item>
        <el-form-item :label="$t('ruleengine.list.index.807357-4')" prop="enable">
          <el-switch v-model="ruleDialogForm.enable" :active-value="1" :inactive-value="2" />
        </el-form-item>
      </el-form>

      <template #footer>
        <div class="dialog-footer">
          <el-button
            type="primary"
            @click="handleSubmitRuleForm"
            v-hasPermi="['rule:el:edit']"
            v-show="ruleDialogForm.id"
          >
            {{ $t('update') }}
          </el-button>
          <el-button
            type="primary"
            @click="handleSubmitRuleForm"
            v-hasPermi="['rule:el:add']"
            v-show="!ruleDialogForm.id"
          >
            {{ $t('add') }}
          </el-button>
          <el-button @click="isAddRule = false" type="info">{{ $t('device.product-list.058448-15') }}</el-button>
        </div>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted, getCurrentInstance, nextTick } from 'vue';
import { useRouter } from 'vue-router';
import { Search, Refresh, Plus, Delete, Edit, CaretRight, ScaleToOriginal } from '@element-plus/icons-vue';
import { getRuleElList, addRuleEl, updateRuleEl, deleteRuleEl, getRuleElDetail, executeRuleEl } from '@/api/rule/el.js';
import { useDict } from '@/utils/dict';

const { rule_engine_status } = useDict('rule_engine_status');
const { proxy } = getCurrentInstance() as any;
const router = useRouter();

const queryFormRef = ref();
const ruleDialogFormRef = ref();
const multipleTableRef = ref();

const showSearch = ref(true);
const loading = ref(true);
const single = ref(true);
const multiple = ref(true);
const ids = ref<any[]>([]);
const total = ref(0);
const list = ref<any[]>([]);
const isAddRule = ref(false);

const queryParams = reactive<any>({
  elName: null,
  enable: null,
  pageNum: 1,
  pageSize: 10,
});

const ruleDialogForm = ref<any>({
  elName: '',
  remark: '',
  enable: 2,
});

const ruleDialogRules = reactive<any>({
  elName: [{ required: true, message: proxy?.$t('ruleengine.list.index.807357-0'), trigger: 'blur' }],
});

/** 查询规则引擎列表 */
function getList() {
  loading.value = true;
  getRuleElList(queryParams).then((res: any) => {
    if (res.code === 200) {
      list.value = res.rows;
      total.value = res.total;
    }
    loading.value = false;
  });
}

/** 获取节点个数 */
function getNodeNum(sourceJson: any) {
  if (sourceJson) {
    const source = JSON.parse(sourceJson);
    return source.nodes.length;
  }
  return 0;
}

/** 新增按钮操作 */
function handleAdd() {
  resetRuleDialogForm();
  isAddRule.value = true;
  nextTick(() => {
    ruleDialogFormRef.value?.clearValidate();
  });
}

/** 执行一次 */
function handleRun(data: any) {
  executeRuleEl(data).then((res: any) => {
    if (res.code === 200) {
      proxy?.$modal.msgSuccess(res.msg);
    }
  });
}

/** 表单重置 */
function resetRuleDialogForm() {
  ruleDialogForm.value = { elName: '', remark: '', enable: 2 };
  ruleDialogFormRef.value?.resetFields();
}

/** 搜索按钮操作 */
function handleQuery() {
  queryParams.pageNum = 1;
  getList();
}

/** 重置按钮操作 */
function resetQuery() {
  queryFormRef.value?.resetFields();
  handleQuery();
}

/** 新增或修改表单 */
function handleSubmitRuleForm() {
  ruleDialogFormRef.value?.validate((valid: boolean) => {
    if (valid) {
      if (ruleDialogForm.value.id) {
        updateRuleEl(ruleDialogForm.value).then((res: any) => {
          if (res.code === 200) {
            proxy?.$modal.msgSuccess(proxy?.$t('updateSuccess'));
            getList();
          }
          isAddRule.value = false;
        });
      } else {
        addRuleEl(ruleDialogForm.value).then((res: any) => {
          if (res.code === 200) {
            proxy?.$modal.msgSuccess(proxy?.$t('addSuccess'));
            getList();
          }
          isAddRule.value = false;
        });
      }
    }
  });
}

/** 修改按钮操作 */
function handleEdit(row: any) {
  resetRuleDialogForm();
  getRuleElDetail(row.id).then((res: any) => {
    if (res.code === 200) {
      ruleDialogForm.value = res.data;
      isAddRule.value = true;
      nextTick(() => {
        ruleDialogFormRef.value?.clearValidate();
      });
    }
  });
}

/** 删除按钮操作 */
function handleDelete(row?: any) {
  const delIds = row?.id || ids.value;
  proxy?.$modal
    .confirm(proxy?.$t('ruleengine.list.index.807357-7', [delIds]))
    .then(() => {
      return deleteRuleEl(delIds);
    })
    .then(() => {
      getList();
      proxy?.$modal.msgSuccess(proxy?.$t('delSuccess'));
      multipleTableRef.value?.clearSelection();
    })
    .catch(() => {});
}

/** 多选框选中数据 */
function handleSelectionChange(selection: any[]) {
  ids.value = selection.map((item) => item.id);
  single.value = selection.length !== 1;
  multiple.value = !selection.length;
}

/** 设计 */
function handleDesign(row: any) {
  const routeUrl = router.resolve({ path: '/ruleengine/editor', query: { id: row.id } });
  window.open(routeUrl.href, '_blank');
}

onMounted(() => {
  getList();
});
</script>

<style lang="scss" scoped>
.ruleengine-list {
  padding: 20px;

  .search-card {
    margin-bottom: 15px;
    padding: 3px 0;
  }

  .search-form {
    margin-bottom: -22.5px;
  }
}
</style>
