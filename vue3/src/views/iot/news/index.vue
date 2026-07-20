<template>
  <div class="iot-news">
    <el-card v-show="showSearch" class="search-card">
      <div class="form-wrap">
        <el-form
          @submit.prevent
          v-if="isAdmin"
          :model="queryParams"
          ref="queryFormRef"
          :inline="true"
          label-width="46px"
        >
          <el-form-item prop="title">
            <el-input
              v-model="queryParams.title"
              :placeholder="$t('system.news.893410-1')"
              clearable
              @keyup.enter="handleQuery"
              style="width: 192px"
            />
          </el-form-item>
          <el-form-item prop="categoryName">
            <el-input
              v-model="queryParams.categoryName"
              :placeholder="$t('system.news.893410-3')"
              clearable
              @keyup.enter="handleQuery"
              style="width: 192px"
            />
          </el-form-item>
          <el-form-item prop="status">
            <el-select
              v-model="queryParams.status"
              :placeholder="$t('system.news.893410-9')"
              clearable
              style="width: 192px"
            >
              <el-option v-for="dict in iot_yes_no" :key="dict.value" :label="dict.label" :value="Number(dict.value)" />
            </el-select>
          </el-form-item>

          <template v-if="searchShow">
            <el-form-item prop="isBanner">
              <el-select
                v-model="queryParams.isBanner"
                :placeholder="$t('system.news.893410-7')"
                clearable
                style="width: 192px"
              >
                <el-option v-for="dict in iot_yes_no" :key="dict.value" :label="dict.label" :value="dict.value" />
              </el-select>
            </el-form-item>
          </template>

          <template v-if="searchShow">
            <el-form-item prop="isTop">
              <el-select
                v-model="queryParams.isTop"
                :placeholder="$t('system.news.893410-5')"
                clearable
                style="width: 192px"
              >
                <el-option v-for="dict in iot_yes_no" :key="dict.value" :label="dict.label" :value="dict.value" />
              </el-select>
            </el-form-item>
          </template>
        </el-form>
        <div class="search-btn-group">
          <el-button type="primary" :icon="Search" @click="handleQuery">{{ $t('search') }}</el-button>
          <el-button :icon="Refresh" @click="resetQuery">{{ $t('reset') }}</el-button>
          <el-button type="primary" link @click="searchChange">
            <span style="color: #486ff2; margin-left: 14px">
              {{ searchShow ? $t('template.index.891112-113') : $t('template.index.891112-112') }}
            </span>
            <el-icon style="color: #486ff2; margin-left: 10px">
              <ArrowDown v-if="!searchShow" />
              <ArrowUp v-else />
            </el-icon>
          </el-button>
        </div>
      </div>
    </el-card>

    <el-card>
      <el-row :gutter="10" style="margin-bottom: 16px">
        <el-col :span="1.5">
          <el-button type="primary" plain :icon="Plus" @click="handleAdd" v-hasPermi="['iot:news:add']">
            {{ $t('add') }}
          </el-button>
        </el-col>
        <el-col :span="1.5">
          <el-button plain :icon="Edit" :disabled="single" @click="handleUpdate" v-hasPermi="['iot:news:edit']">
            {{ $t('update') }}
          </el-button>
        </el-col>
        <el-col :span="1.5">
          <el-button plain :icon="Delete" :disabled="multiple" @click="handleDelete" v-hasPermi="['iot:news:remove']">
            {{ $t('del') }}
          </el-button>
        </el-col>
        <el-col :span="1.5">
          <el-button plain :icon="Download" @click="handleExport" v-hasPermi="['iot:news:export']">
            {{ $t('export') }}
          </el-button>
        </el-col>
        <right-toolbar v-model:showSearch="showSearch" @queryTable="getList"></right-toolbar>
      </el-row>

      <el-table
        v-loading="loading"
        :data="newsList"
        :border="false"
        @selection-change="handleSelectionChange"
        ref="multipleTableRef"
        :row-key="getRowKeys"
      >
        <el-table-column :reserve-selection="true" type="selection" width="55" align="center" />
        <el-table-column :label="$t('system.news.893410-10')" align="center" prop="imgUrl" width="150">
          <template #default="scope">
            <el-image
              style="border-radius: 5px; height: 80px; width: 120px; margin-bottom: -5px"
              lazy
              :preview-src-list="[baseUrl + scope.row.imgUrl]"
              :src="baseUrl + scope.row.imgUrl"
              fit="cover"
            ></el-image>
          </template>
        </el-table-column>
        <el-table-column :label="$t('system.news.893410-0')" align="left" prop="title" min-width="220" />
        <el-table-column :label="$t('system.news.893410-2')" align="center" prop="categoryName" width="100">
          <template #default="scope">
            <el-tag type="info">{{ scope.row.categoryName }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column :label="$t('system.news.893410-11')" align="center" prop="author" width="120" />
        <el-table-column :label="$t('system.news.893410-4')" align="center" prop="isTop" width="80">
          <template #default="scope">
            <dict-tag :options="iot_yes_no" :value="scope.row.isTop" />
          </template>
        </el-table-column>
        <el-table-column :label="$t('system.news.893410-6')" align="center" prop="isBanner" width="80">
          <template #default="scope">
            <dict-tag :options="iot_yes_no" :value="scope.row.isBanner" />
          </template>
        </el-table-column>
        <el-table-column :label="$t('system.news.893410-8')" align="center" prop="status" width="80">
          <template #default="scope">
            <dict-tag :options="iot_yes_no" :value="scope.row.status" />
          </template>
        </el-table-column>
        <el-table-column :label="$t('creatTime')" align="center" prop="createTime" width="120">
          <template #default="scope">
            <span>{{ parseTime(scope.row.createTime, '{y}-{m}-{d}') }}</span>
          </template>
        </el-table-column>
        <el-table-column :label="$t('remark')" align="left" prop="remark" min-width="250" />
        <el-table-column
          fixed="right"
          :label="$t('opation')"
          align="center"
          class-name="small-padding fixed-width"
          width="200"
        >
          <template #default="scope">
            <el-button size="small" link :icon="View" @click="openDetailDialog(scope.row.newsId)">
              {{ $t('look') }}
            </el-button>
            <el-button size="small" link :icon="Edit" @click="handleUpdate(scope.row)" v-hasPermi="['iot:news:edit']">
              {{ $t('update') }}
            </el-button>
            <el-button link :icon="Delete" @click="handleDelete(scope.row)" v-hasPermi="['iot:news:remove']">
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

    <!-- 添加或修改新闻资讯对话框 -->
    <el-dialog :title="title" v-model="open" width="850px" append-to-body>
      <el-form ref="formRef" :model="form" :rules="rules" label-width="75px">
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item :label="$t('system.news.893410-0')" prop="title">
              <el-input v-model="form.title" :placeholder="$t('system.news.893410-0')" style="width: 290px" />
            </el-form-item>
            <el-form-item :label="$t('system.news.893410-11')" prop="author">
              <el-input v-model="form.author" :placeholder="$t('system.news.893410-12')" style="width: 290px" />
            </el-form-item>
            <el-form-item :label="$t('system.news.893410-13')" prop="remark">
              <el-input
                v-model="form.remark"
                type="textarea"
                :placeholder="$t('plzInput')"
                :autosize="{ minRows: 3, maxRows: 5 }"
                style="width: 290px"
              />
            </el-form-item>
            <el-row>
              <el-col :span="8">
                <el-form-item :label="$t('system.news.893410-4')" prop="isTop">
                  <el-switch
                    v-model="form.isTop"
                    active-text=""
                    inactive-text=""
                    :active-value="1"
                    :inactive-value="0"
                  ></el-switch>
                </el-form-item>
              </el-col>
              <el-col :span="8">
                <el-form-item :label="$t('system.news.893410-6')" prop="isBanner">
                  <el-switch
                    v-model="form.isBanner"
                    active-text=""
                    inactive-text=""
                    :active-value="1"
                    :inactive-value="0"
                  ></el-switch>
                </el-form-item>
              </el-col>
              <el-col :span="8">
                <el-form-item :label="$t('system.news.893410-8')" prop="status">
                  <el-switch
                    v-model="form.status"
                    active-text=""
                    inactive-text=""
                    :active-value="1"
                    :inactive-value="0"
                  ></el-switch>
                </el-form-item>
              </el-col>
            </el-row>
          </el-col>
          <el-col :span="12">
            <el-form-item :label="$t('system.news.893410-2')" prop="categoryId">
              <el-select
                v-model="form.categoryId"
                placeholder="请选择分类"
                @change="selectCategory"
                style="width: 282.5px"
              >
                <el-option
                  v-for="category in categoryOptionList"
                  :key="category.id"
                  :label="category.name"
                  :value="category.id"
                ></el-option>
              </el-select>
            </el-form-item>
            <el-form-item :label="$t('system.news.893410-10')">
              <image-upload
                :model-value="form.imgUrl"
                :limit="1"
                :fileSize="1"
                @update:model-value="getImagePath"
              ></image-upload>
            </el-form-item>
          </el-col>
        </el-row>
        <el-form-item :label="$t('system.news.893410-15')">
          <editor v-model="form.content" :min-height="192" style="width: 697px" />
        </el-form-item>
      </el-form>
      <template #footer>
        <div class="dialog-footer">
          <el-button type="primary" @click="submitForm">{{ $t('confirm') }}</el-button>
          <el-button @click="cancel">{{ $t('cancel') }}</el-button>
        </div>
      </template>
    </el-dialog>

    <!--通知公告详情 -->
    <el-dialog :title="form.title" v-model="openDetail" width="800px" append-to-body>
      <div style="margin-bottom: 15px">
        <el-tag size="small" effect="dark" type="success">{{ form.categoryName }}</el-tag>
        <span style="margin-left: 15px">{{ form.createTime }}</span>
      </div>
      <div v-loading="loadingDetail" class="content">
        <div v-html="form.content"></div>
      </div>
      <template #footer>
        <div class="dialog-footer">
          <el-button type="primary" @click="closeDetail">{{ $t('close') }}</el-button>
        </div>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, getCurrentInstance, onMounted, nextTick } from 'vue';
import { Search, Refresh, Plus, Edit, Delete, Download, View, ArrowDown, ArrowUp } from '@element-plus/icons-vue';
import { listNews, getNews, delNews, addNews, updateNews } from '@/api/iot/news';
import { listShortNewsCategory } from '@/api/iot/newsCategory';
import { useUserStore } from '@/stores/modules/user';
import { parseTime } from '@/utils/ruoyi';
import { useDict } from '@/utils/dict';

const { proxy } = getCurrentInstance() as any;
const { iot_yes_no } = useDict('iot_yes_no');
const userStore = useUserStore();

const baseUrl = import.meta.env.VITE_APP_BASE_API;
const isAdmin = ref(false);
const loadingDetail = ref(false);
const openDetail = ref(false);
const loading = ref(true);
const ids = ref<any[]>([]);
const single = ref(true);
const multiple = ref(true);
const showSearch = ref(true);
const searchShow = ref(false);
const total = ref(0);
const newsList = ref<any[]>([]);
const title = ref('');
const open = ref(false);
const categoryOptionList = ref<any[]>([]);
const queryFormRef = ref<any>(null);
const formRef = ref<any>(null);
const multipleTableRef = ref<any>(null);

const queryParams = reactive({
  pageNum: 1,
  pageSize: 10,
  title: null as any,
  isTop: null as any,
  isBanner: null as any,
  categoryName: null as any,
  status: 1 as any,
});

const form = ref<any>({});

const rules = reactive({
  title: [{ required: true, message: proxy.$t('system.news.893410-16'), trigger: 'blur' }],
  content: [{ required: true, message: proxy.$t('system.news.893410-17'), trigger: 'blur' }],
  categoryId: [{ required: true, message: proxy.$t('system.news.893410-18'), trigger: 'blur' }],
  author: [{ required: true, message: proxy.$t('system.news.893410-19'), trigger: 'blur' }],
});

onMounted(() => {
  getList();
  init();
});

function init() {
  if (!userStore.roles.includes('tenant') && !userStore.roles.includes('general')) {
    isAdmin.value = true;
    listShortNewsCategory().then((response: any) => {
      categoryOptionList.value = response.data;
    });
  }
}

function getList() {
  loading.value = true;
  listNews(queryParams).then((response: any) => {
    newsList.value = response.rows;
    total.value = response.total;
    loading.value = false;
  });
}

function getImagePath(data: any) {
  form.value.imgUrl = data;
}

function cancel() {
  open.value = false;
  reset();
}

function reset() {
  form.value = {
    newsId: null,
    title: null,
    content: proxy.$t('system.news.893410-20'),
    imgUrl: '',
    isTop: null,
    isBanner: null,
    categoryId: null,
    categoryName: null,
    status: 0,
    author: null,
    delFlag: null,
    createBy: null,
    createTime: null,
    updateBy: null,
    updateTime: null,
    remark: null,
  };
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
  ids.value = selection.map((item) => item.newsId);
  single.value = selection.length !== 1;
  multiple.value = !selection.length;
}

function handleAdd() {
  reset();
  open.value = true;
  title.value = proxy.$t('system.news.893410-21');
  nextTick(() => {
    formRef.value?.clearValidate();
  });
}

function handleUpdate(row?: any) {
  reset();
  const newsId = row?.newsId || ids.value;
  getNews(newsId).then((response: any) => {
    form.value = response.data;
    open.value = true;
    title.value = proxy.$t('system.news.893410-22');
    nextTick(() => {
      formRef.value?.clearValidate();
    });
  });
}

function submitForm() {
  if (form.value.imgUrl == null || form.value.imgUrl == '') {
    proxy.$modal.msgError(proxy.$t('system.news.893410-23'));
    return;
  }
  formRef.value.validate((valid: boolean) => {
    if (valid) {
      if (form.value.newsId != null) {
        updateNews(form.value).then(() => {
          proxy.$modal.msgSuccess(proxy.$t('updateSuccess'));
          open.value = false;
          getList();
        });
      } else {
        addNews(form.value).then(() => {
          proxy.$modal.msgSuccess(proxy.$t('addSuccess'));
          open.value = false;
          getList();
        });
      }
    }
  });
}

function handleDelete(row?: any) {
  const newsIds = row?.newsId || ids.value;
  proxy.$modal
    .confirm(proxy.$t('system.news.893410-24', [newsIds]))
    .then(() => {
      return delNews(newsIds);
    })
    .then(() => {
      getList();
      proxy.$modal.msgSuccess(proxy.$t('delSuccess'));
      multipleTableRef.value.clearSelection();
    })
    .catch(() => {});
}

function handleExport() {
  proxy.download('iot/news/export', { ...queryParams }, `news_${new Date().getTime()}.xlsx`);
}

function selectCategory(val: any) {
  for (let i = 0; i < categoryOptionList.value.length; i++) {
    if (categoryOptionList.value[i].id == val) {
      form.value.categoryName = categoryOptionList.value[i].name;
      return;
    }
  }
}

function openDetailDialog(newsId: any) {
  openDetail.value = true;
  loadingDetail.value = true;
  getNews(newsId).then((response: any) => {
    form.value = response.data;
    openDetail.value = true;
    loadingDetail.value = false;
  });
}

function closeDetail() {
  openDetail.value = false;
  reset();
}

function searchChange() {
  searchShow.value = !searchShow.value;
}

function getRowKeys(row: any) {
  return row.newsId;
}
</script>

<style lang="scss" scoped>
.iot-news {
  padding: 20px;

  .search-card {
    margin-bottom: 15px;
    padding: 3px 0;

    .form-wrap {
      display: flex;
      flex-direction: row;
      justify-content: space-between;
      align-items: flex-end;

      .search-btn-group {
        display: flex;
        flex-direction: row;
        margin-bottom: 22px;
      }
    }
  }
}

.content {
  padding: 5px 15px;
  border: 1px solid #dcdfe6;
  border-radius: 4px;
}
</style>
