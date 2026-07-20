<template>
  <div class="monitor-online">
    <el-card v-show="showSearch" class="search-card">
      <el-form
        @submit.prevent
        :model="queryParams"
        ref="queryFormRef"
        :inline="true"
        label-width="68px"
        class="search-form"
      >
        <el-form-item prop="ipaddr">
          <el-input
            v-model="queryParams.ipaddr"
            :placeholder="$t('online.093480-1')"
            clearable
            style="width: 192px"
            @keyup.enter="handleQuery"
          />
        </el-form-item>
        <el-form-item prop="userName">
          <el-input
            v-model="queryParams.userName"
            :placeholder="$t('online.093480-2')"
            clearable
            style="width: 192px"
            @keyup.enter="handleQuery"
          />
        </el-form-item>
        <div style="float: right">
          <el-button type="primary" :icon="Search" @click="handleQuery">{{ $t('search') }}</el-button>
          <el-button :icon="Refresh" @click="resetQuery">{{ $t('reset') }}</el-button>
        </div>
      </el-form>
    </el-card>

    <el-card>
      <el-row :gutter="10" style="margin-bottom: 15px">
        <right-toolbar v-model:showSearch="showSearch" @queryTable="getList" />
      </el-row>

      <el-table
        v-loading="loading"
        :data="list.slice((pageNum - 1) * pageSize, pageNum * pageSize)"
        :border="false"
        style="width: 100%"
      >
        <el-table-column :label="$t('online.093480-3')" type="index" align="center" width="60">
          <template #default="scope">
            <span>{{ (pageNum - 1) * pageSize + scope.$index + 1 }}</span>
          </template>
        </el-table-column>
        <el-table-column
          :label="$t('online.093480-4')"
          align="center"
          prop="tokenId"
          :show-overflow-tooltip="true"
          width="310"
        />
        <el-table-column
          :label="$t('online.093480-5')"
          align="left"
          prop="userName"
          :show-overflow-tooltip="true"
          min-width="200"
        />
        <el-table-column :label="$t('online.093480-6')" align="left" prop="deptName" min-width="180" />
        <el-table-column
          :label="$t('online.093480-7')"
          align="center"
          prop="ipaddr"
          :show-overflow-tooltip="true"
          min-width="150"
        />
        <el-table-column
          :label="$t('online.093480-8')"
          align="center"
          prop="loginLocation"
          :show-overflow-tooltip="true"
          min-width="180"
        />
        <el-table-column :label="$t('online.093480-9')" align="center" prop="browser" min-width="120" />
        <el-table-column :label="$t('online.093480-10')" align="center" prop="os" min-width="150" />
        <el-table-column :label="$t('online.093480-11')" align="center" prop="loginTime" min-width="180">
          <template #default="scope">
            <span>{{ parseTime(scope.row.loginTime) }}</span>
          </template>
        </el-table-column>
        <el-table-column fixed="right" :label="$t('opation')" align="center" width="80">
          <template #default="scope">
            <el-button
              link
              style="color: #ed2525"
              size="small"
              @click="handleForceLogout(scope.row)"
              v-hasPermi="['monitor:online:forceLogout']"
            >
              <svg-icon icon-class="logout" />
              <span style="margin-left: 5px">{{ $t('online.093480-12') }}</span>
            </el-button>
          </template>
        </el-table-column>
      </el-table>

      <pagination
        style="margin-bottom: 20px"
        v-show="total > 0"
        :total="total"
        v-model:page="pageNum"
        v-model:limit="pageSize"
      />
    </el-card>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, getCurrentInstance, onMounted } from 'vue';
import { useI18n } from 'vue-i18n';
import { Search, Refresh } from '@element-plus/icons-vue';
import { list as listOnline, forceLogout } from '@/api/monitor/online';
import { parseTime } from '@/utils/ruoyi';

const { t } = useI18n();
const { proxy } = getCurrentInstance()!;

const loading = ref(true);
const showSearch = ref(true);
const total = ref(0);
const list = ref<any[]>([]);
const pageNum = ref(1);
const pageSize = ref(10);
const queryFormRef = ref();

const queryParams = reactive({
  ipaddr: undefined as string | undefined,
  userName: undefined as string | undefined,
});

function getList() {
  loading.value = true;
  listOnline(queryParams).then((response: any) => {
    list.value = response.rows;
    total.value = response.total;
    loading.value = false;
  });
}

function handleQuery() {
  pageNum.value = 1;
  getList();
}

function resetQuery() {
  queryFormRef.value?.resetFields();
  handleQuery();
}

function handleForceLogout(row: any) {
  (proxy as any).$modal
    .confirm(t('online.093480-13', [row.userName]))
    .then(() => {
      return forceLogout(row.tokenId);
    })
    .then(() => {
      getList();
      (proxy as any).$modal.msgSuccess(t('online.093480-14'));
    })
    .catch(() => {});
}

onMounted(() => {
  getList();
});
</script>

<style lang="scss" scoped>
.monitor-online {
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
