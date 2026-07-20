<template>
  <div style="padding: 6px">
    <el-card v-show="showSearch" style="margin-bottom: 6px">
      <el-form
        :model="queryParams"
        ref="queryFormRef"
        :inline="true"
        v-show="showSearch"
        label-width="68px"
        style="margin-bottom: -20px"
      >
        <el-form-item label="客户端ID" prop="clientId">
          <el-input v-model="queryParams.clientId" placeholder="请输入客户端ID" clearable @keyup.enter="handleQuery" />
        </el-form-item>
        <el-form-item label="授权平台" prop="type">
          <el-select v-model="queryParams.type" placeholder="请选择平台" clearable>
            <el-option v-for="dict in oauth_platform" :key="dict.value" :label="dict.label" :value="dict.value" />
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" :icon="Search" @click="handleQuery">搜索</el-button>
          <el-button :icon="Refresh" @click="resetQuery">重置</el-button>
          <el-tag type="danger" style="margin-left: 15px">该功能暂不可用,后面版本发布</el-tag>
        </el-form-item>
        <el-form-item style="float: right">
          <el-button
            type="primary"
            plain
            :icon="Plus"
            size="small"
            @click="handleAdd"
            v-hasPermi="['iot:clientDetails:add']"
          >
            新增
          </el-button>
        </el-form-item>
      </el-form>
    </el-card>

    <el-card style="padding-bottom: 100px">
      <el-table v-loading="loading" :data="clientDetailsList" @selection-change="handleSelectionChange">
        <el-table-column label="客户端ID" align="center" prop="clientId" />
        <el-table-column label="资源" align="center" prop="resourceIds" />
        <el-table-column label="权限范围" align="center" prop="scope" />
        <el-table-column label="授权平台" align="center" prop="type">
          <template #default="scope">
            <dict-tag :options="oauth_platform" :value="scope.row.type" />
          </template>
        </el-table-column>
        <el-table-column label="自动授权" align="center" prop="autoapprove">
          <template #default="scope">
            <span v-if="scope.row.autoapprove == 'true'">自动授权</span>
            <span v-if="scope.row.autoapprove == 'false'">用户验证</span>
          </template>
        </el-table-column>
        <el-table-column label="授权模式" align="center" prop="authorizedGrantTypes">
          <template #default="scope">
            <div v-html="formatGrantTypesDisplay(scope.row.authorizedGrantTypes)"></div>
          </template>
        </el-table-column>
        <el-table-column label="回调地址" align="center" prop="webServerRedirectUri" min-width="130" />
        <el-table-column label="权限" align="center" prop="authorities" />
        <el-table-column label="Token有效期" align="center" prop="accessTokenValidity" />
        <el-table-column label="Token刷新时间" align="center" prop="refreshTokenValidity" />
        <el-table-column label="操作" align="center" class-name="small-padding fixed-width">
          <template #default="scope">
            <el-button
              size="small"
              type="primary"
              link
              :icon="Edit"
              @click="handleUpdate(scope.row)"
              v-hasPermi="['iot:clientDetails:edit']"
            >
              修改
            </el-button>
            <el-button
              size="small"
              type="primary"
              link
              :icon="Delete"
              @click="handleDelete(scope.row)"
              v-hasPermi="['iot:clientDetails:remove']"
              disabled
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

      <!-- 添加或修改云云对接对话框 -->
      <el-dialog :title="title" v-model="open" width="600px" append-to-body>
        <el-form ref="formRef" :model="form" :rules="rules" label-width="120px">
          <el-form-item label="授权平台" prop="type">
            <el-select v-model="form.type" placeholder="请选择授权平台">
              <el-option
                v-for="dict in oauth_platform"
                :key="dict.value"
                :label="dict.label"
                :value="parseInt(dict.value)"
              ></el-option>
            </el-select>
          </el-form-item>
          <el-form-item label="客户端ID" prop="clientId">
            <el-input v-model="form.clientId" placeholder="请输入客户端ID" />
          </el-form-item>
          <el-form-item label="资源集合" prop="resourceIds">
            <el-input v-model="form.resourceIds" placeholder="请输入资源" />
          </el-form-item>
          <el-form-item label="授权模式" prop="authorizedGrantTypes">
            <el-input v-model="form.authorizedGrantTypes" type="textarea" placeholder="请输入授权模式" />
          </el-form-item>
          <el-form-item label="秘钥" prop="clientSecret">
            <el-input v-model="form.clientSecret" placeholder="请输入客户端秘钥" />
          </el-form-item>
          <el-form-item label="回调地址" prop="webServerRedirectUri">
            <el-input v-model="form.webServerRedirectUri" type="textarea" placeholder="请输入回调地址" />
          </el-form-item>
          <el-form-item label="权限" prop="authorities">
            <el-input v-model="form.authorities" placeholder="请输入权限" />
          </el-form-item>
          <el-form-item label="Token有效期" prop="accessTokenValidity">
            <el-input v-model="form.accessTokenValidity" placeholder="请输入Token有效时间" />
          </el-form-item>
          <el-form-item label="Token刷新时间" prop="refreshTokenValidity">
            <el-input v-model="form.refreshTokenValidity" placeholder="请输入Token刷新有效时间" />
          </el-form-item>
          <el-form-item label="预留信息" prop="additionalInformation">
            <el-input v-model="form.additionalInformation" type="textarea" placeholder="请输入内容" />
          </el-form-item>
        </el-form>
        <template #footer>
          <div class="dialog-footer">
            <el-button type="primary" @click="submitForm" disabled>确 定</el-button>
            <el-button @click="cancel">取 消</el-button>
          </div>
        </template>
      </el-dialog>
    </el-card>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, getCurrentInstance, onMounted } from 'vue';
import { Search, Refresh, Plus, Edit, Delete } from '@element-plus/icons-vue';
import {
  listClientDetails,
  getClientDetails,
  delClientDetails,
  addClientDetails,
  updateClientDetails,
} from '@/api/iot/clientDetails';
import { useDict } from '@/utils/dict';

const { proxy } = getCurrentInstance() as any;
const { oauth_platform } = useDict('oauth_platform');

const loading = ref(true);
const ids = ref<any[]>([]);
const single = ref(true);
const multiple = ref(true);
const showSearch = ref(true);
const total = ref(0);
const clientDetailsList = ref<any[]>([]);
const title = ref('');
const open = ref(false);
const formRef = ref<any>(null);
const form = ref<any>({});
const rules = reactive({});

const queryParams = reactive({
  pageNum: 1,
  pageSize: 10,
  clientId: null as any,
  authorizedGrantTypes: null as any,
  autoapprove: null as any,
  type: null as any,
});

onMounted(() => {
  getList();
});

function getList() {
  loading.value = true;
  listClientDetails(queryParams).then((response: any) => {
    clientDetailsList.value = response.rows;
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
    clientId: null,
    resourceIds: null,
    clientSecret: null,
    scope: null,
    authorizedGrantTypes: null,
    webServerRedirectUri: null,
    authorities: null,
    accessTokenValidity: null,
    refreshTokenValidity: null,
    additionalInformation: null,
    autoapprove: null,
    type: null,
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
  ids.value = selection.map((item) => item.clientId);
  single.value = selection.length !== 1;
  multiple.value = !selection.length;
}

function handleAdd() {
  reset();
  open.value = true;
  title.value = '添加云云对接';
}

function handleUpdate(row: any) {
  reset();
  getClientDetails(row.clientId || ids.value).then((response: any) => {
    form.value = response.data;
    open.value = true;
    title.value = '修改云云对接';
  });
}

function submitForm() {
  formRef.value.validate((valid: boolean) => {
    if (valid) {
      if (form.value.clientId != null) {
        updateClientDetails(form.value).then(() => {
          proxy.$modal.msgSuccess('修改成功');
          open.value = false;
          getList();
        });
      } else {
        addClientDetails(form.value).then(() => {
          proxy.$modal.msgSuccess('新增成功');
          open.value = false;
          getList();
        });
      }
    }
  });
}

function handleDelete(row: any) {
  const clientIds = row.clientId || ids.value;
  proxy.$modal
    .confirm('是否确认删除云云对接编号为"' + clientIds + '"的数据项？')
    .then(() => delClientDetails(clientIds))
    .then(() => {
      getList();
      proxy.$modal.msgSuccess('删除成功');
    })
    .catch(() => {});
}

function formatGrantTypesDisplay(data: string) {
  if (!data) return '';
  return data
    .split(',')
    .map(
      (t: string) =>
        `<div style='background-color:#eee;margin:0 auto;margin-bottom:5px;width:86px;border-radius:5px;padding:3px;'>${convertGrantType(t)}</div>`
    )
    .join('');
}

function convertGrantType(type: string) {
  const map: Record<string, string> = {
    client_credentials: '客户端模式',
    password: '密码模式',
    authorization_code: '授权码模式',
    implicit: '简化模式',
    refresh_token: '刷新Token',
  };
  return map[type] || '';
}

/** 导出按钮操作 */
function handleExport() {
  proxy?.download(
    'iot/clientDetails/export',
    { ...queryParams },
    `clientDetails_${new Date().getTime()}.xlsx`
  );
}
</script>
