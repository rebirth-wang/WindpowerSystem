<template>
  <div class="scada-echart">
    <el-card v-show="showSearch" class="search-card">
      <el-form :model="queryParams" ref="queryForm" :inline="true" label-width="48px" class="search-form">
        <el-form-item prop="echartName">
          <el-input
            v-model="queryParams.echartName"
            :placeholder="$t('scada.center.indeieScada.373453-7')"
            clearable
            style="width: 192px"
            @keyup.enter="handleQuery"
          />
        </el-form-item>
        <el-form-item prop="echartType">
          <el-select
            v-model="queryParams.echartType"
            :placeholder="$t('scada.component.302923-8')"
            clearable
            style="width: 192px"
          >
            <el-option
              v-for="item in dict.type.scada_echart_type"
              :key="item.value"
              :label="item.label"
              :value="item.value"
              @keyup.enter="handleQuery"
            />
          </el-select>
        </el-form-item>
        <el-form-item prop="sysFlag">
          <el-select
            v-model="queryParams.sysFlag"
            :placeholder="$t('template.index.891112-124')"
            clearable
            style="width: 192px"
            @keyup.enter.native="handleQuery"
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
          <el-button type="primary" plain :icon="Plus" @click="handleAdd" v-hasPermi="['scada:echart:add']">
            {{ $t('add') }}
          </el-button>
        </el-col>
        <el-col :span="1.5">
          <el-button plain :icon="Edit" :disabled="single" @click="handleUpdate" v-hasPermi="['scada:echart:query']">
            {{ $t('update') }}
          </el-button>
        </el-col>
        <el-col :span="1.5">
          <el-button
            plain
            :icon="Delete"
            :disabled="multiple"
            @click="handleDelete"
            v-hasPermi="['scada:echart:remove']"
          >
            {{ $t('del') }}
          </el-button>
        </el-col>
        <el-col :span="1.5">
          <el-button plain :icon="Download" @click="handleExport" v-hasPermi="['scada:echart:export']">
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
            v-for="item in echartList"
            :key="item.id"
          >
            <el-checkbox-group v-model="ids" @change="checkboxChange">
              <el-card class="card-wrap" :body-style="{ padding: '0px' }">
                <div class="img-wrap">
                  <el-image
                    style="width: 100%; height: 100%"
                    :src="baseApi + item.echartImgae"
                    fit="cover"
                    @click="goToDetail(item)"
                  ></el-image>
                  <div class="tag-bar">
                    <div class="tag-wrap">
                      <span>
                        {{ item.sysFlag === 0 ? $t('scada.component.302923-2') : $t('scada.component.302923-3') }}
                      </span>
                    </div>
                    <div class="type-tag-wrap">
                      <span>{{ item.echartType }}</span>
                    </div>
                  </div>
                </div>
                <div class="title-wrap">
                  <div class="name-wrap">
                    <el-checkbox :value="item.id" :key="item.id">
                      <span v-show="false">{{ $t('scada.component.302923-4') }}</span>
                    </el-checkbox>
                    <div class="name">
                      {{ item.echartName }}
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
                    v-hasPermi="['scada:echart:query']"
                  >
                    {{ $t('update') }}
                  </el-button>
                  <span
                    style="width: 1px; margin: 0px 10px; font-size: 16px; color: #dcdfe6"
                    v-hasPermi="['scada:echart:query']"
                  >
                    |
                  </span>
                  <el-button
                    size="small"
                    link
                    :icon="SetUp"
                    @click="goToDetail(item)"
                    v-hasPermi="['scada:echart:query']"
                  >
                    {{ $t('scada.echart.209302-7') }}
                  </el-button>
                  <span
                    style="width: 1px; margin: 0px 10px; font-size: 16px; color: #dcdfe6"
                    v-hasPermi="['scada:echart:remove']"
                  >
                    |
                  </span>
                  <el-button
                    size="small"
                    link
                    :icon="Delete"
                    @click="handleDelete(item)"
                    v-hasPermi="['scada:echart:remove']"
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
        <el-table v-loading="loading" :data="echartList" :border="false" @selection-change="handleSelectionChange">
          <el-table-column type="selection" width="55" align="center" />
          <el-table-column
            :label="$t('scada.center.indeieScada.373453-0')"
            align="left"
            prop="echartName"
            min-width="230"
          />
          <el-table-column :label="$t('scada.component.302923-5')" align="center" prop="echartImgae" width="100">
            <template #default="scope">
              <image-preview :src="scope.row.echartImgae" :width="50" :height="50" />
            </template>
          </el-table-column>
          <el-table-column :label="$t('scada.component.302923-6')" align="center" prop="echartType" width="120">
            <template #default="scope">
              <el-tag>{{ scope.row.echartType }}</el-tag>
            </template>
          </el-table-column>
          <el-table-column :label="$t('template.index.891112-124')" align="left" prop="sysFlag" min-width="130">
            <template #default="scope">
              <dict-tag :options="dict.type.system_type_status" :value="scope.row.sysFlag" />
            </template>
          </el-table-column>
          <el-table-column :label="$t('template.index.891112-117')" align="center" prop="tenantName" width="150" />
          <el-table-column :label="$t('template.index.891112-118')" align="center" prop="createBy" width="150" />
          <el-table-column :label="$t('updateTime')" align="center" prop="updateTime" width="120">
            <template #default="scope">
              {{ parseTime(scope.row.updateTime, '{y}-{m}-{d}') }}
            </template>
          </el-table-column>
          <el-table-column fixed="right" :label="$t('opation')" align="center" min-width="170">
            <template #default="scope">
              <el-button
                size="small"
                link
                :icon="Edit"
                @click="handleUpdate(scope.row)"
                v-hasPermi="['scada:echart:edit']"
              >
                {{ $t('update') }}
              </el-button>
              <el-button
                size="small"
                link
                :icon="SetUp"
                @click="goToDetail(scope.row)"
                v-hasPermi="['scada:echart:query']"
              >
                {{ $t('scada.echart.209302-7') }}
              </el-button>
              <el-button
                size="small"
                link
                :icon="Delete"
                @click="handleDelete(scope.row)"
                v-hasPermi="['scada:echart:remove']"
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
        />
      </div>
    </el-card>

    <!-- 添加图表对话框 -->
    <el-dialog :title="dialog.title" v-model="dialog.open" width="500px" append-to-body>
      <el-form ref="dialogForm" :model="dialog.form" :rules="dialog.rules" label-width="70px">
        <el-form-item :label="$t('scada.component.302923-7')" prop="echartImgae">
          <imageUpload ref="imageUploadRef" :value="dialog.form.echartImgae" :limit="1" :fileSize="1" />
        </el-form-item>
        <el-form-item :label="$t('scada.center.indeieScada.373453-0')" prop="echartName">
          <el-input
            v-model="dialog.form.echartName"
            :placeholder="$t('scada.center.indeieScada.373453-7')"
            clearable
            style="width: 330px"
          />
        </el-form-item>
        <el-form-item :label="$t('scada.component.302923-6')" prop="echartType">
          <el-select
            v-model="dialog.form.echartType"
            :placeholder="$t('scada.component.302923-8')"
            clearable
            style="width: 330px"
          >
            <el-option
              v-for="item in dict.type.scada_echart_type"
              :key="item.value"
              :label="item.label"
              :value="item.value"
            ></el-option>
          </el-select>
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
import { parseTime } from '@/utils/ruoyi';
import { listEchart, getEchart, delEchart, addEchart, updateEchart } from '@/api/scada/echart';
import { useDict } from '@/utils/dict/useDict';
import imageUpload from '@/components/ImageUpload/index.vue';

const { proxy } = getCurrentInstance() as any;
const router = useRouter();
const { dict } = useDict('scada_echart_type', 'system_type_status');

const loading = ref(true);
const isListView = ref(true);
const baseApi = import.meta.env.VITE_APP_BASE_API;
const single = ref(true);
const multiple = ref(true);
const showSearch = ref(true);
const echartList = ref<any[]>([]);
const total = ref(0);
const ids = ref<any[]>([]);
const showType = ref('card');

const dialogForm = ref<any>(null);

const queryParams = reactive({
  pageNum: 1,
  pageSize: 12,
  orderByColumn: 'id',
  isAsc: 'desc',
  guid: null as any,
  deptIdStrs: null as any,
  echartType: null as any,
  echartData: null as any,
  echartName: null as any,
  sysFlag: null as any,
});

const dialog = reactive({
  open: false,
  title: '',
  form: { echartImgae: '', echartName: '', echartType: '' } as any,
  rules: {
    echartName: [{ required: true, message: proxy.$t('scada.center.indeieScada.373453-7'), trigger: 'change' }],
    echartType: [{ required: true, message: proxy.$t('scada.component.302923-8'), trigger: 'change' }],
  },
});

const uploadDisabled = computed(() => dialog.form.echartImgae !== '');

function getList() {
  loading.value = true;
  listEchart(queryParams).then((response: any) => {
    if (response.code === 200) {
      echartList.value = response.rows;
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
  dialog.form = { echartImgae: '', echartName: '', echartType: '' };
  proxy.resetForm('dialogForm');
}

function handleAdd() {
  reset();
  dialog.open = true;
  dialog.title = proxy.$t('scada.echart.209302-0');
}

function handleDialogSubmit() {
  dialogForm.value.validate((valid: boolean) => {
    if (valid) {
      if (dialog.form.id != null) {
        updateEchart(dialog.form).then((res: any) => {
          if (res.code === 200) {
            proxy.$modal.msgSuccess(proxy.$t('updateSuccess'));
            dialog.open = false;
            getList();
          }
        });
      } else {
        addEchart(dialog.form).then((res: any) => {
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
  dialog.title = proxy.$t('scada.echart.209302-1');
  const id = row.id || ids.value;
  getEchart(id).then((res: any) => {
    if (res.code === 200) {
      dialog.form = res.data;
      dialog.open = true;
    }
  });
}

function handleDelete(row: any) {
  const delIds = row.id || ids.value;
  proxy.$modal
    .confirm(proxy.$t('scada.echart.209302-2', [delIds]))
    .then(() => delEchart(delIds))
    .then(() => {
      getList();
      proxy.$modal.msgSuccess(proxy.$t('delSuccess'));
    })
    .catch(() => {});
}

function handleExport() {
  proxy.download(
    'scada/echart/export',
    { ...queryParams },
    `${proxy.$t('scada.echart.209302-12')}${new Date().getTime()}.xlsx`
  );
}

function goToDetail(row: any) {
  router.push({ path: '/scada/echart/detail', query: { id: row.id } });
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

function checkboxChange(selection: any[]) {
  single.value = selection.length != 1;
  multiple.value = !selection.length;
}

onMounted(() => {
  getList();
});
</script>

<style lang="scss" scoped>
.scada-echart {
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

    :deep(.el-button):hover,
    .el-button:focus {
      color: unset;
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
    overflow: hidden;

    .img-wrap {
      position: relative;
      height: 159px;
      width: 100%;
      overflow: hidden;
      background: #f5f7fb;
    }

    .tag-bar {
      position: absolute;
      top: 12px;
      left: 0;
      right: 0;
      z-index: 1;
      display: flex;
      align-items: center;
      justify-content: space-between;
      gap: 10px;
      pointer-events: none;
    }

    .type-tag-wrap,
    .tag-wrap {
      max-width: 46%;
      min-height: 26px;
      display: inline-flex;
      align-items: center;
      justify-content: center;
      padding: 0 10px;
      border: 1px solid rgba(72, 111, 242, 0.18);
      border-radius: 4px;
      background: rgba(255, 255, 255, 0.92);
      box-shadow: 0 4px 10px rgba(31, 45, 61, 0.08);
      color: #3157d5;
      font-size: 12px;
      font-weight: 500;
      line-height: 1;

      span {
        overflow: hidden;
        text-overflow: ellipsis;
        white-space: nowrap;
      }
    }

    .tag-wrap {
      border-color: rgba(72, 111, 242, 0.2);
      background: rgba(72, 111, 242, 0.92);
      color: #fff;
    }

    .type-tag-wrap {
      margin-left: auto;
    }

    .title-wrap {
      display: flex;
      flex-direction: row;
      align-items: center;
      padding: 15px 15px 7px;

      :deep(.el-checkbox__inner) {
        height: 16px;
        width: 16px;
      }

      :deep(.el-checkbox__inner::after) {
        height: 9px;
        left: 5px;
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

.disable {
  :deep(.el-upload--picture-card) {
    display: none !important;
  }
}
</style>
