<template>
  <div class="app-container">
    <el-form :model="queryParams" ref="queryRef" :inline="true" v-show="showSearch" label-width="68px">
      <el-form-item label="关联的模板id" prop="deviceTempId">
        <el-input
          v-model="queryParams.deviceTempId"
          placeholder="请输入关联的模板id"
          clearable
          @keyup.enter="handleQuery"
        />
      </el-form-item>
      <el-form-item label="从机编号" prop="slaveAddr">
        <el-input v-model="queryParams.slaveAddr" placeholder="请输入从机编号" clearable @keyup.enter="handleQuery" />
      </el-form-item>
      <el-form-item label="从机ip地址" prop="slaveIp">
        <el-input v-model="queryParams.slaveIp" placeholder="请输入从机ip地址" clearable @keyup.enter="handleQuery" />
      </el-form-item>
      <el-form-item label="从机名称" prop="slaveName">
        <el-input v-model="queryParams.slaveName" placeholder="请输入从机名称" clearable @keyup.enter="handleQuery" />
      </el-form-item>
      <el-form-item>
        <el-button type="primary" :icon="Search" @click="handleQuery">搜索</el-button>
        <el-button :icon="Refresh" @click="resetQuery">重置</el-button>
      </el-form-item>
    </el-form>

    <el-row :gutter="10" class="mb8">
      <el-col :span="1.5">
        <el-button type="primary" plain :icon="Plus" @click="handleAdd" v-hasPermi="['iot:salve:add']">新增</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button
          type="success"
          plain
          :icon="Edit"
          :disabled="single"
          @click="handleUpdate"
          v-hasPermi="['iot:salve:edit']"
        >
          修改
        </el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button
          type="danger"
          plain
          :icon="Delete"
          :disabled="multiple"
          @click="handleDelete"
          v-hasPermi="['iot:salve:remove']"
        >
          删除
        </el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button type="warning" plain :icon="Download" @click="handleExport" v-hasPermi="['iot:salve:export']">
          导出
        </el-button>
      </el-col>
      <right-toolbar v-model:showSearch="showSearch" @queryTable="getList"></right-toolbar>
    </el-row>

    <el-table v-loading="loading" :data="salveList" @selection-change="handleSelectionChange">
      <el-table-column type="selection" width="55" align="center" />
      <el-table-column label="主键id" align="center" prop="id" />
      <el-table-column label="关联的模板id" align="center" prop="deviceTempId" />
      <el-table-column label="从机编号" align="center" prop="slaveAddr" />
      <el-table-column label="从机ip地址" align="center" prop="slaveIp" />
      <el-table-column label="从机名称" align="center" prop="slaveName" />
      <el-table-column label="从机端口" align="center" prop="slavePort" />
      <el-table-column label="状态" align="center" prop="status" />
      <el-table-column label="备注" align="center" prop="remark" />
      <el-table-column label="操作" align="center" class-name="small-padding fixed-width">
        <template #default="scope">
          <el-button type="primary" link :icon="Edit" @click="handleUpdate(scope.row)" v-hasPermi="['iot:salve:edit']">
            修改
          </el-button>
          <el-button
            type="primary"
            link
            :icon="Delete"
            @click="handleDelete(scope.row)"
            v-hasPermi="['iot:salve:remove']"
          >
            删除
          </el-button>
        </template>
      </el-table-column>
    </el-table>

    <pagination
      v-show="total > 0"
      :total="total"
      v-model:page="queryParams.pageNum"
      v-model:limit="queryParams.pageSize"
      @pagination="getList"
    />

    <!-- 添加或修改变量模板设备从机对话框 -->
    <el-dialog :title="title" v-model="open" width="500px" append-to-body>
      <el-form ref="formRef" :model="form" :rules="rules" label-width="80px">
        <el-form-item label="关联的模板id" prop="deviceTempId">
          <el-input v-model="form.deviceTempId" placeholder="请输入关联的模板id" />
        </el-form-item>
        <el-form-item label="从机编号" prop="slaveAddr">
          <el-input v-model="form.slaveAddr" placeholder="请输入从机编号" />
        </el-form-item>
        <el-form-item label="从机ip地址" prop="slaveIp">
          <el-input v-model="form.slaveIp" placeholder="请输入从机ip地址" />
        </el-form-item>
        <el-form-item label="从机名称" prop="slaveName">
          <el-input v-model="form.slaveName" placeholder="请输入从机名称" />
        </el-form-item>
        <el-form-item label="从机端口" prop="slavePort">
          <el-input v-model="form.slavePort" placeholder="请输入从机端口" />
        </el-form-item>
        <el-form-item label="备注" prop="remark">
          <el-input v-model="form.remark" placeholder="请输入备注" />
        </el-form-item>
      </el-form>
      <template #footer>
        <div class="dialog-footer">
          <el-button type="primary" @click="submitForm">确 定</el-button>
          <el-button @click="cancel">取 消</el-button>
        </div>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, getCurrentInstance, onMounted } from 'vue';
import { Search, Refresh, Plus, Edit, Delete, Download } from '@element-plus/icons-vue';
import { listSalve, getSalve, delSalve, addSalve, updateSalve } from '@/api/iot/salve';

const { proxy } = getCurrentInstance() as any;

const loading = ref(true);
const ids = ref<any[]>([]);
const single = ref(true);
const multiple = ref(true);
const showSearch = ref(true);
const total = ref(0);
const salveList = ref<any[]>([]);
const title = ref('');
const open = ref(false);

const queryParams = reactive({
  pageNum: 1,
  pageSize: 10,
  deviceTempId: null as any,
  slaveAddr: null as any,
  slaveIp: null as any,
  slaveName: null as any,
  slavePort: null as any,
  status: null as any,
});

const form = ref<any>({});

const rules = reactive<any>({
  deviceTempId: [{ required: true, message: '关联的模板id不能为空', trigger: 'blur' }],
  slaveAddr: [{ required: true, message: '从机编号不能为空', trigger: 'blur' }],
  status: [{ required: true, message: '状态不能为空', trigger: 'blur' }],
});

function getList() {
  loading.value = true;
  listSalve(queryParams).then((response: any) => {
    salveList.value = response.rows;
    total.value = response.total;
    loading.value = false;
  });
}

function cancel() {
  open.value = false;
  reset();
}

function reset() {
  form.value = {
    id: null,
    deviceTempId: null,
    slaveAddr: null,
    slaveIndex: null,
    slaveIp: null,
    slaveName: null,
    slavePort: null,
    status: 0,
    remark: null,
  };
  proxy.resetForm('formRef');
}

function handleQuery() {
  queryParams.pageNum = 1;
  getList();
}

function resetQuery() {
  proxy.resetForm('queryRef');
  handleQuery();
}

function handleSelectionChange(selection: any[]) {
  ids.value = selection.map((item) => item.id);
  single.value = selection.length !== 1;
  multiple.value = !selection.length;
}

function handleAdd() {
  reset();
  open.value = true;
  title.value = '添加变量模板设备从机';
}

function handleUpdate(row?: any) {
  reset();
  const id = row?.id || ids.value;
  getSalve(id).then((response: any) => {
    form.value = response.data;
    open.value = true;
    title.value = '修改变量模板设备从机';
  });
}

function submitForm() {
  proxy.$refs['formRef'].validate((valid: boolean) => {
    if (valid) {
      if (form.value.id != null) {
        updateSalve(form.value).then(() => {
          proxy.$modal.msgSuccess('修改成功');
          open.value = false;
          getList();
        });
      } else {
        addSalve(form.value).then(() => {
          proxy.$modal.msgSuccess('新增成功');
          open.value = false;
          getList();
        });
      }
    }
  });
}

function handleDelete(row?: any) {
  const deleteIds = row?.id || ids.value;
  proxy.$modal
    .confirm('是否确认删除变量模板设备从机编号为"' + deleteIds + '"的数据项？')
    .then(() => {
      return delSalve(deleteIds);
    })
    .then(() => {
      getList();
      proxy.$modal.msgSuccess('删除成功');
    })
    .catch(() => {});
}

function handleExport() {
  proxy.download('iot/salve/export', { ...queryParams }, `salve_${new Date().getTime()}.xlsx`);
}

onMounted(() => {
  getList();
});
</script>
