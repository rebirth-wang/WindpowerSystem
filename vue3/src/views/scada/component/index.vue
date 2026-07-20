<template>
  <div class="scada-component">
    <el-card v-show="showSearch" class="search-card">
      <el-form
        @submit.native.prevent
        :model="queryParams"
        ref="queryForm"
        :inline="true"
        label-width="48px"
        class="search-form"
      >
        <el-form-item prop="componentName">
          <el-input
            v-model="queryParams.componentName"
            :placeholder="$t('scada.center.indeieScada.373453-7')"
            clearable
            style="width: 192px"
            @keyup.enter="handleQuery"
          />
        </el-form-item>
        <el-form-item prop="sysFlag">
          <el-select
            v-model="queryParams.sysFlag"
            :placeholder="$t('template.index.891112-124')"
            clearable
            style="width: 192px"
            @keyup.enter="handleQuery"
          >
            <el-option
              v-for="item in dict.type.system_type_status"
              :key="item.value"
              :label="item.label"
              :value="item.value"
            />
          </el-select>
        </el-form-item>
        <div style="float: right">
          <el-button type="primary" :icon="Search" @click="handleQuery">{{ $t('search') }}</el-button>
          <el-button :icon="Refresh" @click="handleResetQuery">{{ $t('reset') }}</el-button>
        </div>
      </el-form>
    </el-card>

    <el-card>
      <el-row :gutter="10">
        <el-col :span="1.5">
          <el-button type="primary" plain :icon="Plus" @click="handleAdd" v-hasPermi="['scada:component:add']">
            {{ $t('add') }}
          </el-button>
        </el-col>
        <el-col :span="1.5">
          <el-button plain :icon="Edit" :disabled="single" @click="handleUpdate" v-hasPermi="['scada:component:query']">
            {{ $t('update') }}
          </el-button>
        </el-col>
        <el-col :span="1.5">
          <el-button
            plain
            :icon="Delete"
            :disabled="multiple"
            @click="handleDelete"
            v-hasPermi="['scada:component:remove']"
          >
            {{ $t('del') }}
          </el-button>
        </el-col>
        <el-col :span="1.5">
          <el-button plain :icon="Download" @click="handleExport" v-hasPermi="['scada:component:export']">
            {{ $t('export') }}
          </el-button>
        </el-col>
        <right-toolbar v-model:showSearch="showSearch" @queryTable="getList">
          <template #default>
            <div class="button-group">
              <el-tooltip effect="dark" :content="$t('device.index.105953-67')" placement="top">
                <el-button
                  size="small"
                  :icon="Tickets"
                  @click="handleShowTypeCard"
                  :class="['toggle-button card-button', { active: isListView }]"
                />
              </el-tooltip>
              <div class="separator"></div>
              <el-tooltip effect="dark" :content="$t('device.index.105953-68')" placement="top">
                <el-button
                  size="small"
                  :icon="Grid"
                  @click="handleShowTypeList"
                  :class="['toggle-button list-button', { active: !isListView }]"
                />
              </el-tooltip>
            </div>
          </template>
        </right-toolbar>
      </el-row>

      <div v-if="showType == 'card'" style="margin-top: 20px">
        <el-row :gutter="20" v-loading="loading">
          <el-col
            :xs="24"
            :sm="12"
            :md="12"
            :lg="8"
            :xl="6"
            style="margin-bottom: 20px"
            v-for="item in componentList"
            :key="item.id"
          >
            <el-checkbox-group v-model="ids" @change="checkboxChange">
              <el-card class="card-wrap" :body-style="{ padding: '0px' }">
                <div class="img-wrap">
                  <el-image
                    style="width: 100%; height: 100%"
                    :src="baseUrl + item.componentImage"
                    fit="cover"
                    @click="goToDetail(item)"
                  ></el-image>
                </div>
                <div class="tag-wrap">
                  <span>
                    {{ item.sysFlag === 0 ? $t('scada.component.302923-2') : $t('scada.component.302923-3') }}
                  </span>
                </div>
                <div class="title-wrap">
                  <div class="name-wrap">
                    <el-checkbox :value="item.id" :key="item.id">
                      <!-- <span v-show="false">{{ $t('scada.component.302923-4') }}</span> -->
                    </el-checkbox>
                    <div class="name">
                      {{ item.componentName }}
                    </div>
                  </div>
                  <div class="time">
                    {{ parseTime(item.updateTime, '{y}-{m}-{d}') }}
                  </div>
                </div>
                <el-button-group class="tools-wrap">
                  <el-button
                    size="small"
                    link
                    :icon="Edit"
                    @click="handleUpdate(item)"
                    v-hasPermi="['scada:component:query']"
                  >
                    {{ $t('update') }}
                  </el-button>
                  <span
                    style="width: 1px; margin: 0px 10px; font-size: 16px; color: #dcdfe6"
                    v-hasPermi="['scada:component:query']"
                  >
                    |
                  </span>
                  <el-button
                    size="small"
                    link
                    :icon="SetUp"
                    @click="goToDetail(item)"
                    v-hasPermi="['scada:component:query']"
                  >
                    {{ $t('scada.component.302923-12') }}
                  </el-button>
                  <span
                    style="width: 1px; margin: 0px 10px; font-size: 16px; color: #dcdfe6"
                    v-hasPermi="['scada:component:remove']"
                  >
                    |
                  </span>
                  <el-button
                    size="small"
                    link
                    :icon="Delete"
                    @click="handleDelete(item)"
                    v-hasPermi="['scada:component:remove']"
                  >
                    {{ $t('del') }}
                  </el-button>
                </el-button-group>
              </el-card>
            </el-checkbox-group>
          </el-col>
        </el-row>
        <el-empty :description="$t('noData')" v-if="total == 0"></el-empty>
        <pagination
          style="margin: 0 0 20px 0"
          v-show="total > 0"
          :total="total"
          :page="queryParams.pageNum"
          @update:page="queryParams.pageNum = $event"
          :limit="queryParams.pageSize"
          @update:limit="queryParams.pageSize = $event"
          @pagination="getList"
          :page-sizes="[12, 24, 36]"
        />
      </div>
      <div v-if="showType == 'list'" style="margin-top: 16px">
        <el-table v-loading="loading" :data="componentList" :border="false" @selection-change="handleSelectionChange">
          <el-table-column type="selection" width="55" align="center" />
          <el-table-column
            :label="$t('scada.center.indeieScada.373453-0')"
            align="left"
            prop="componentName"
            min-width="230"
          />
          <el-table-column :label="$t('scada.component.302923-5')" align="center" prop="componentImage" width="100">
            <template #default="scope">
              <image-preview :src="scope.row.componentImage" :width="48" :height="48" />
            </template>
          </el-table-column>
          <el-table-column :label="$t('scada.component.302923-6')" align="center" prop="sysFlag" width="120">
            <template #default="scope">
              <el-tag>
                {{ scope.row.sysFlag === 0 ? $t('scada.component.302923-2') : $t('scada.component.302923-3') }}
              </el-tag>
            </template>
          </el-table-column>
          <el-table-column :label="$t('template.index.891112-117')" align="center" prop="tenantName" width="150" />
          <el-table-column :label="$t('template.index.891112-118')" align="center" prop="createBy" width="150" />
          <el-table-column :label="$t('updateTime')" align="center" prop="updateTime" width="180" />
          <el-table-column fixed="right" :label="$t('opation')" align="center" width="285">
            <template #default="scope">
              <el-button
                size="small"
                link
                :icon="Edit"
                @click="handleUpdate(scope.row)"
                v-hasPermi="['scada:component:edit']"
              >
                {{ $t('update') }}
              </el-button>
              <el-button
                size="small"
                link
                :icon="SetUp"
                @click="goToDetail(scope.row)"
                v-hasPermi="['scada:component:query']"
              >
                {{ $t('scada.component.302923-12') }}
              </el-button>
              <el-button
                size="small"
                link
                :icon="Delete"
                @click="handleDelete(scope.row)"
                v-hasPermi="['scada:component:remove']"
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
          :page="queryParams.pageNum"
          @update:page="queryParams.pageNum = $event"
          :limit="queryParams.pageSize"
          @update:limit="queryParams.pageSize = $event"
          @pagination="getList"
          :pageSizes="[12, 24, 36, 48]"
        />
      </div>
    </el-card>

    <!-- 添加或修改组件管理对话框 -->
    <el-dialog :title="dialog.title" v-model="dialog.open" width="500px" append-to-body>
      <el-form ref="dialogForm" :model="dialog.form" :rules="dialog.rules" label-width="70px">
        <el-form-item :label="$t('scada.component.302923-7')" prop="componentImage">
          <imageUpload ref="imageUploadRef" :value="dialog.form.componentImage" :limit="1" :fileSize="1" />
        </el-form-item>
        <el-form-item :label="$t('scada.center.indeieScada.373453-0')" prop="componentName">
          <el-input
            v-model="dialog.form.componentName"
            :placeholder="$t('scada.center.indeieScada.373453-7')"
            clearable
            style="width: 330px"
          />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button type="primary" @click="handleDialogSubmit">{{ $t('confirm') }}</el-button>
        <el-button @click="handleDialogCancel">{{ $t('cancel') }}</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, computed, onMounted, getCurrentInstance } from 'vue';
import { useRouter } from 'vue-router';
import { Search, Refresh, Plus, Edit, Delete, Download, SetUp, Tickets, Grid } from '@element-plus/icons-vue';
import { listComponent, getComponent, delComponent, addComponent, updateComponent } from '@/api/scada/component';
import { useDict } from '@/utils/dict/useDict';
import { parseTime } from '@/utils/ruoyi';
import imageUpload from '@/components/ImageUpload/index.vue';

const { proxy } = getCurrentInstance() as any;
const router = useRouter();
const { dict } = useDict('system_type_status');

const loading = ref(true);
const isListView = ref(true);
const single = ref(true);
const multiple = ref(true);
const showSearch = ref(true);
const componentList = ref<any[]>([]);
const total = ref(0);
const ids = ref<any[]>([]);
const showType = ref('card');
const baseUrl = import.meta.env.VITE_APP_BASE_API;

const dialogForm = ref<any>(null);

const queryParams = reactive({
  pageNum: 1,
  pageSize: 12,
  componentName: null as any,
  sysFlag: null as any,
  componentTemplate: null as any,
  componentStyle: null as any,
  componentScript: null as any,
  componentImage: null as any,
  tenantId: null as any,
  tenantName: null as any,
});

const dialog = reactive({
  open: false,
  title: '',
  form: {
    componentImage: '',
    componentName: '',
    sysFlag: 1,
  } as any,
  rules: {
    componentName: [{ required: true, message: proxy.$t('scada.center.indeieScada.373453-7'), trigger: 'change' }],
    sysFlag: [{ required: true, message: proxy.$t('scada.component.302923-8'), trigger: 'change' }],
  },
});

const uploadDisabled = computed(() => dialog.form.componentImage !== '');

function getList() {
  loading.value = true;
  listComponent(queryParams).then((response: any) => {
    if (response.code === 200) {
      componentList.value = response.rows;
      total.value = response.total;
    }
    loading.value = false;
  });
}

function handleQuery() {
  queryParams.pageNum = 1;
  getList();
}

function handleResetQuery() {
  proxy.resetForm('queryForm');
  handleQuery();
}

function handleSelectionChange(selection: any[]) {
  ids.value = selection.map((item) => item.id);
  single.value = selection.length !== 1;
  multiple.value = !selection.length;
}

function reset() {
  dialog.form = { componentImage: '', componentName: '', sysFlag: 1 };
  proxy.resetForm('dialogForm');
}

function handleAdd() {
  reset();
  dialog.open = true;
  dialog.title = proxy.$t('scada.component.302923-9');
}

function handleDialogSubmit() {
  dialogForm.value.validate((valid: boolean) => {
    if (valid) {
      if (dialog.form.id != null) {
        updateComponent(dialog.form).then((res: any) => {
          if (res.code === 200) {
            proxy.$modal.msgSuccess(proxy.$t('updateSuccess'));
            dialog.open = false;
            getList();
          }
        });
      } else {
        addComponent(dialog.form).then((res: any) => {
          if (res.code === 200) {
            proxy.$modal.msgSuccess(proxy.$t('addSuccess'));
            dialog.open = false;
            getList();
          }
        });
      }
    }
  });
}

function handleDialogCancel() {
  dialog.open = false;
}

function handleUpdate(row: any) {
  dialog.title = proxy.$t('scada.component.302923-10');
  const id = row.id || ids.value;
  getComponent(id).then((res: any) => {
    if (res.code === 200) {
      dialog.form = res.data;
      dialog.open = true;
    }
  });
}

function handleDelete(row: any) {
  const delIds = row.id || ids.value;
  proxy.$modal
    .confirm(proxy.$t('scada.component.302923-11', [delIds]))
    .then(() => delComponent(delIds))
    .then(() => {
      getList();
      proxy.$modal.msgSuccess(proxy.$t('delSuccess'));
    })
    .catch(() => {});
}

function handleExport() {
  proxy.download(
    'scada/component/export',
    { ...queryParams },
    `${proxy.$t('scada.component.302923-20')}${new Date().getTime()}.xlsx`
  );
}

function handleShowTypeList() {
  ids.value = [];
  isListView.value = false;
  showType.value = 'list';
}

function handleShowTypeCard() {
  ids.value = [];
  isListView.value = true;
  showType.value = 'card';
}

function goToDetail(row: any) {
  router.push({ path: '/scada/component/detail', query: { id: row.id } });
}

function checkboxChange(selection: any[]) {
  single.value = selection.length != 1;
  multiple.value = !selection.length;
}

onMounted(() => {
  getList();
});
</script>

<style lang="scss" scoped>
.scada-component {
  padding: 20px;

  .search-card {
    margin-bottom: 15px;
    padding: 3px 0;
  }

  .search-form {
    margin-bottom: -22.5px;
  }

  .button-group {
    width: 65px;
    height: 32.5px;
    display: flex; /* 让按钮在同一行显示 */
    align-items: center; /* 垂直居中对齐 */
    margin-right: 12px;
    background: #ffffff;

    .toggle-button {
      width: 32.5px;
      height: 32.5px;
      display: flex; /* 使用 flexbox */
      justify-content: center; /* 水平居中 */
      align-items: center; /* 垂直居中 */
      border: 1px solid #dcdfe6;
      background: #fff;
    }

    .active {
      background-color: #e0e0e0; /* 被选中按钮的背景色 */
      border-color: #dcdfe6; /* 被选中按钮的边框色 */
    }

    .card-button {
      border-radius: 4px 0px 0px 4px !important;
    }

    .list-button {
      border-radius: 0px 4px 4px 0px !important;
    }

    .separator {
      width: 1px; /* 分割线的宽度 */
      height: 32px; /* 分割线的高度 */
      background: #dcdfe6; /* 分割线的颜色 */
    }
  }

  .card-wrap {
    position: relative;
    background: #ffffff;
    border-radius: 8px;
    border: 1px solid #dcdfe6;

    .img-wrap {
      height: 159px;
      width: 100%;
    }

    .tag-wrap {
      position: absolute;
      top: 15px;
      right: 0;
      background: #486ff2;
      opacity: 0.7;
      border-radius: 2px 0px 0px 2px;
      padding: 12px 8px;
      font-size: 12px;
      color: #fff;
    }

    .title-wrap {
      display: flex;
      flex-direction: row;
      align-items: center;
      padding: 15px 15px 7px;

      :deep(.el-button):hover,
      .el-button:focus {
        color: unset;
      }

      .name-wrap {
        flex: 1;
        display: flex;
        flex-direction: row;
        align-items: center;
        width: 100%;
        overflow: hidden;

        .name {
          flex: 1 1 auto;
          font-weight: 500;
          font-size: 16px;
          color: #303133;
          line-height: 22px;
          white-space: nowrap; /* 不换行 */
          overflow: hidden; /* 隐藏超出部分 */
          text-overflow: ellipsis; /* 使用省略号 */
        }
      }

      .time {
        font-weight: 400;
        font-size: 12px;
        color: #909399;
        line-height: 15px;
        margin-left: 10px;
      }
    }

    .tools-wrap {
      padding: 0 10px 10px;
      display: flex;
      flex-direction: row;
      justify-content: space-between;
      align-items: center;
    }
  }
}
</style>
