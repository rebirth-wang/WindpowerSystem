<template>
  <div style="padding: 6px">
    <el-card v-show="showSearch" style="margin-bottom: 6px">
      <el-form :model="queryParams" ref="queryFormRef" :inline="true" label-width="68px" style="margin-bottom: -20px">
        <el-form-item label="日志名称" prop="logName">
          <el-input v-model="queryParams.logName" placeholder="请输入日志名称" clearable @keyup.enter="handleQuery" />
        </el-form-item>
        <el-form-item label="类型" prop="logType">
          <el-select v-model="queryParams.logType" placeholder="请选择类型" clearable>
            <el-option v-for="dict in iot_things_type" :key="dict.value" :label="dict.label" :value="dict.value" />
          </el-select>
        </el-form-item>
        <el-form-item label="日志级别" prop="logLevel">
          <el-input v-model="queryParams.logLevel" placeholder="请输入日志级别" clearable @keyup.enter="handleQuery" />
        </el-form-item>
        <el-form-item label="设备ID" prop="deviceId">
          <el-input v-model="queryParams.deviceId" placeholder="请输入设备ID" clearable @keyup.enter="handleQuery" />
        </el-form-item>
        <el-form-item label="设备名称" prop="deviceName">
          <el-input
            v-model="queryParams.deviceName"
            placeholder="请输入设备名称"
            clearable
            @keyup.enter="handleQuery"
          />
        </el-form-item>
        <el-form-item label="用户昵称" prop="userName">
          <el-input v-model="queryParams.userName" placeholder="请输入用户昵称" clearable @keyup.enter="handleQuery" />
        </el-form-item>
        <el-form-item label="租户名称" prop="tenantName">
          <el-input
            v-model="queryParams.tenantName"
            placeholder="请输入租户名称"
            clearable
            @keyup.enter="handleQuery"
          />
        </el-form-item>
        <el-form-item label="生成告警" prop="isAlert">
          <el-input
            v-model="queryParams.isAlert"
            placeholder="请输入是否生成告警"
            clearable
            @keyup.enter="handleQuery"
          />
        </el-form-item>
        <el-form-item label="告警处理" prop="status">
          <el-select v-model="queryParams.status" placeholder="请选择告警处理" clearable>
            <el-option v-for="dict in iot_yes_no" :key="dict.value" :label="dict.label" :value="dict.value" />
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" :icon="Search" @click="handleQuery">搜索</el-button>
          <el-button :icon="Refresh" @click="resetQuery">重置</el-button>
        </el-form-item>
      </el-form>
    </el-card>

    <el-card style="padding-bottom: 100px">
      <el-row :gutter="10" class="mb8">
        <el-col :span="1.5">
          <el-button type="primary" plain :icon="Plus" size="small" @click="handleAdd" v-hasPermi="['iot:log:add']">
            新增
          </el-button>
        </el-col>
        <el-col :span="1.5">
          <el-button
            type="success"
            plain
            :icon="Edit"
            size="small"
            :disabled="single"
            @click="handleUpdate"
            v-hasPermi="['iot:log:edit']"
          >
            修改
          </el-button>
        </el-col>
        <el-col :span="1.5">
          <el-button
            type="danger"
            plain
            :icon="Delete"
            size="small"
            :disabled="multiple"
            @click="handleDelete"
            v-hasPermi="['iot:log:remove']"
          >
            删除
          </el-button>
        </el-col>
        <el-col :span="1.5">
          <el-button
            type="warning"
            plain
            :icon="Download"
            size="small"
            @click="handleExport"
            v-hasPermi="['iot:log:export']"
          >
            导出
          </el-button>
        </el-col>
        <right-toolbar v-model:showSearch="showSearch" @queryTable="getList"></right-toolbar>
      </el-row>

      <el-table v-loading="loading" :data="logList" @selection-change="handleSelectionChange" border>
        <el-table-column type="selection" width="55" align="center" />
        <el-table-column label="日志名称" align="center" prop="logName" />
        <el-table-column label="值" align="center" prop="logValue" />
        <el-table-column label="类型" align="center" prop="logType">
          <template #default="scope">
            <dict-tag :options="iot_things_type" :value="scope.row.logType" />
          </template>
        </el-table-column>
        <el-table-column label="日志级别" align="center" prop="logLevel">
          <template #default="scope">
            <dict-tag :options="iot_yes_no" :value="scope.row.logLevel" />
          </template>
        </el-table-column>
        <el-table-column label="设备ID" align="center" prop="deviceId" />
        <el-table-column label="设备名称" align="center" prop="deviceName" />
        <el-table-column label="用户ID" align="center" prop="userId" />
        <el-table-column label="用户昵称" align="center" prop="userName" />
        <el-table-column label="租户ID" align="center" prop="tenantId" />
        <el-table-column label="租户名称" align="center" prop="tenantName" />
        <el-table-column label="触发源" align="center" prop="triggerSource">
          <template #default="scope">
            <dict-tag :options="iot_yes_no" :value="scope.row.triggerSource" />
          </template>
        </el-table-column>
        <el-table-column label="生成告警" align="center" prop="isAlert">
          <template #default="scope">
            <dict-tag :options="iot_yes_no" :value="scope.row.isAlert" />
          </template>
        </el-table-column>
        <el-table-column label="告警处理" align="center" prop="status">
          <template #default="scope">
            <dict-tag :options="iot_yes_no" :value="scope.row.status" />
          </template>
        </el-table-column>
        <el-table-column label="创建时间" align="center" prop="createTime" width="180">
          <template #default="scope">
            <span>{{ parseTime(scope.row.createTime, '{y}-{m}-{d}') }}</span>
          </template>
        </el-table-column>
        <el-table-column label="备注" align="center" prop="remark" />
        <el-table-column label="操作" align="center" class-name="small-padding fixed-width" width="150">
          <template #default="scope">
            <el-button
              size="small"
              type="primary"
              style="padding: 5px"
              :icon="Edit"
              @click="handleUpdate(scope.row)"
              v-hasPermi="['iot:log:edit']"
            >
              处理
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

      <!-- 添加或修改设备日志对话框 -->
      <el-dialog :title="title" v-model="open" width="500px" append-to-body>
        <el-form ref="formRef" :model="form" :rules="rules" label-width="80px">
          <el-form-item label="日志名称" prop="logName">
            <el-input v-model="form.logName" placeholder="请输入日志名称" />
          </el-form-item>
          <el-form-item label="类型" prop="logType">
            <el-select v-model="form.logType" placeholder="请选择类型">
              <el-option
                v-for="dict in iot_things_type"
                :key="dict.value"
                :label="dict.label"
                :value="parseInt(dict.value)"
              ></el-option>
            </el-select>
          </el-form-item>
          <el-form-item label="日志级别" prop="logLevel">
            <el-input v-model="form.logLevel" placeholder="请输入日志级别" />
          </el-form-item>
          <el-form-item label="设备ID" prop="deviceId">
            <el-input v-model="form.deviceId" placeholder="请输入设备ID" />
          </el-form-item>
          <el-form-item label="设备名称" prop="deviceName">
            <el-input v-model="form.deviceName" placeholder="请输入设备名称" />
          </el-form-item>
          <el-form-item label="用户ID" prop="userId">
            <el-input v-model="form.userId" placeholder="请输入用户ID" />
          </el-form-item>
          <el-form-item label="用户昵称" prop="userName">
            <el-input v-model="form.userName" placeholder="请输入用户昵称" />
          </el-form-item>
          <el-form-item label="租户ID" prop="tenantId">
            <el-input v-model="form.tenantId" placeholder="请输入租户ID" />
          </el-form-item>
          <el-form-item label="租户名称" prop="tenantName">
            <el-input v-model="form.tenantName" placeholder="请输入租户名称" />
          </el-form-item>
          <el-form-item label="触发源" prop="triggerSource">
            <el-input v-model="form.triggerSource" placeholder="请输入触发源" />
          </el-form-item>
          <el-form-item label="是否生成告警" prop="isAlert">
            <el-input v-model="form.isAlert" placeholder="请输入是否生成告警" />
          </el-form-item>
          <el-form-item label="告警处理">
            <el-radio-group v-model="form.status">
              <el-radio v-for="dict in iot_yes_no" :key="dict.value" :value="parseInt(dict.value)">
                {{ dict.label }}
              </el-radio>
            </el-radio-group>
          </el-form-item>
          <el-form-item label="备注" prop="remark">
            <el-input v-model="form.remark" placeholder="请输入备注" />
          </el-form-item>
          <el-form-item label="日志收到的值" prop="logValue">
            <el-input v-model="form.logValue" placeholder="请输入日志收到的值" />
          </el-form-item>
          <el-form-item label="是否置顶" prop="istop">
            <el-input v-model="form.istop" placeholder="请输入是否置顶" />
          </el-form-item>
          <el-form-item label="是否监测" prop="ismonitor">
            <el-input v-model="form.ismonitor" placeholder="请输入是否监测" />
          </el-form-item>
        </el-form>
        <template #footer>
          <div class="dialog-footer">
            <el-button type="primary" @click="submitForm">确 定</el-button>
            <el-button @click="cancel">取 消</el-button>
          </div>
        </template>
      </el-dialog>
    </el-card>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, getCurrentInstance, onMounted } from 'vue';
import { Search, Refresh, Plus, Edit, Delete, Download } from '@element-plus/icons-vue';
import { listLog, getLog, delLog, addLog, updateLog } from '@/api/iot/log';
import { parseTime } from '@/utils/ruoyi';
import { useDict } from '@/utils/dict';

const { proxy } = getCurrentInstance() as any;
const { iot_things_type, iot_yes_no } = useDict('iot_things_type', 'iot_yes_no');

const loading = ref(true);
const ids = ref<any[]>([]);
const single = ref(true);
const multiple = ref(true);
const showSearch = ref(true);
const total = ref(0);
const logList = ref<any[]>([]);
const title = ref('');
const open = ref(false);
const queryFormRef = ref<any>(null);
const formRef = ref<any>(null);

const queryParams = reactive({
  pageNum: 1,
  pageSize: 10,
  logName: null as any,
  logType: null as any,
  logLevel: null as any,
  deviceId: null as any,
  deviceName: null as any,
  userName: null as any,
  tenantName: null as any,
  triggerSource: null as any,
  isAlert: null as any,
  status: null as any,
  istop: null as any,
  ismonitor: null as any,
});

const form = ref<any>({});

const rules = reactive({
  logName: [{ required: true, message: '日志名称不能为空', trigger: 'blur' }],
  logType: [{ required: true, message: '类型不能为空', trigger: 'change' }],
  logLevel: [{ required: true, message: '日志级别不能为空', trigger: 'blur' }],
  deviceId: [{ required: true, message: '设备ID不能为空', trigger: 'blur' }],
  deviceName: [{ required: true, message: '设备名称不能为空', trigger: 'blur' }],
  userId: [{ required: true, message: '用户ID不能为空', trigger: 'blur' }],
  userName: [{ required: true, message: '用户昵称不能为空', trigger: 'blur' }],
  tenantId: [{ required: true, message: '租户ID不能为空', trigger: 'blur' }],
  tenantName: [{ required: true, message: '租户名称不能为空', trigger: 'blur' }],
});

onMounted(() => {
  getList();
});

function getList() {
  loading.value = true;
  listLog(queryParams).then((response: any) => {
    logList.value = response.rows;
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
    deviceLogId: null,
    logName: null,
    logType: null,
    logLevel: null,
    deviceId: null,
    deviceName: null,
    userId: null,
    userName: null,
    tenantId: null,
    tenantName: null,
    triggerSource: null,
    isAlert: null,
    status: 0,
    createBy: null,
    createTime: null,
    remark: null,
    logValue: null,
    istop: null,
    ismonitor: null,
  };
  proxy.resetForm('formRef');
}

function handleQuery() {
  queryParams.pageNum = 1;
  getList();
}

function resetQuery() {
  proxy.resetForm('queryFormRef');
  handleQuery();
}

function handleSelectionChange(selection: any[]) {
  ids.value = selection.map((item) => item.deviceLogId);
  single.value = selection.length !== 1;
  multiple.value = !selection.length;
}

function handleAdd() {
  reset();
  open.value = true;
  title.value = '添加设备日志';
}

function handleUpdate(row?: any) {
  reset();
  const deviceLogId = row?.deviceLogId || ids.value;
  getLog(deviceLogId).then((response: any) => {
    form.value = response.data;
    open.value = true;
    title.value = '修改设备日志';
  });
}

function submitForm() {
  formRef.value.validate((valid: boolean) => {
    if (valid) {
      if (form.value.deviceLogId != null) {
        updateLog(form.value).then(() => {
          proxy.$modal.msgSuccess('修改成功');
          open.value = false;
          getList();
        });
      } else {
        addLog(form.value).then(() => {
          proxy.$modal.msgSuccess('新增成功');
          open.value = false;
          getList();
        });
      }
    }
  });
}

function handleDelete(row?: any) {
  const deviceLogIds = row?.deviceLogId || ids.value;
  proxy.$modal
    .confirm('是否确认删除设备日志编号为"' + deviceLogIds + '"的数据项？')
    .then(() => {
      return delLog(deviceLogIds);
    })
    .then(() => {
      getList();
      proxy.$modal.msgSuccess('删除成功');
    })
    .catch(() => {});
}

function handleExport() {
  proxy.download('iot/log/export', { ...queryParams }, `log_${new Date().getTime()}.xlsx`);
}
</script>
