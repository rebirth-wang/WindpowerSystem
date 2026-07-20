<template>
  <div class="app-content">
    <el-card v-show="showSearch" class="search-card">
      <el-form class="search-form" :model="queryParams" ref="queryFormRef" :inline="true" label-width="68px">
        <el-form-item prop="name">
          <el-input
            v-model="queryParams.name"
            :placeholder="$t('card.platform.please_enter_name')"
            clearable
            @keyup.enter="handleQuery"
          />
        </el-form-item>
        <el-form-item prop="platform">
          <el-select
            v-model="queryParams.platform"
            :placeholder="$t('card.platform.please_select_platform')"
            clearable
            style="width: 205px"
          >
            <el-option v-for="dict in iot_card_platform" :key="dict.value" :label="dict.label" :value="dict.value" />
          </el-select>
        </el-form-item>
        <div style="float: right">
          <el-button type="primary" :icon="Search" @click="handleQuery">{{ $t('card.platform.search') }}</el-button>
          <el-button :icon="Refresh" @click="resetQuery">{{ $t('card.platform.reset') }}</el-button>
        </div>
      </el-form>
    </el-card>

    <el-card>
      <el-row :gutter="10" style="margin-bottom: 16px">
        <el-col :span="1.5">
          <el-button type="primary" plain :icon="Plus" @click="handleAdd" v-hasPermi="['iot:cardPlatform:add']">
            {{ $t('card.platform.add') }}
          </el-button>
        </el-col>
        <el-col :span="1.5">
          <el-button plain :icon="Edit" :disabled="single" @click="handleUpdate" v-hasPermi="['iot:cardPlatform:edit']">
            {{ $t('card.platform.edit') }}
          </el-button>
        </el-col>
        <el-col :span="1.5">
          <el-button
            plain
            :icon="Delete"
            :disabled="multiple"
            @click="handleDelete"
            v-hasPermi="['iot:cardPlatform:remove']"
          >
            {{ $t('card.platform.delete') }}
          </el-button>
        </el-col>
        <el-col :span="1.5">
          <el-button plain :icon="Download" @click="handleExport" v-hasPermi="['iot:cardPlatform:export']">
            {{ $t('card.platform.export') }}
          </el-button>
        </el-col>
        <right-toolbar v-model:showSearch="showSearch" @queryTable="getList"></right-toolbar>
      </el-row>

      <el-table v-loading="loading" :data="cardPlatformList" :border="false" @selection-change="handleSelectionChange">
        <el-table-column type="selection" width="55" align="center" />
        <el-table-column :label="$t('card.platform.id')" align="center" prop="id" min-width="60" />
        <el-table-column :label="$t('card.platform.name')" align="left" prop="name" min-width="220">
          <template #default="scope">
            <span>{{ scope.row.name || '--' }}</span>
          </template>
        </el-table-column>
        <el-table-column :label="$t('card.platform.platform_label')" align="left" prop="platform" min-width="150">
          <template #default="scope">
            <dict-tag :options="iot_card_platform" :value="scope.row.platform" />
          </template>
        </el-table-column>
        <el-table-column :label="$t('card.platform.tenant_name')" align="left" prop="tenantName" min-width="170">
          <template #default="scope">
            <span>{{ scope.row.tenantName || '--' }}</span>
          </template>
        </el-table-column>
        <el-table-column :label="$t('card.platform.create_by')" align="center" prop="createBy" min-width="170">
          <template #default="scope">
            <span>{{ scope.row.createBy || '--' }}</span>
          </template>
        </el-table-column>
        <el-table-column :label="$t('card.platform.create_time')" align="center" prop="createTime" min-width="180">
          <template #default="scope">
            <span>{{ scope.row.createTime ? scope.row.createTime : '--' }}</span>
          </template>
        </el-table-column>
        <el-table-column
          fixed="right"
          :label="$t('card.platform.operation')"
          align="center"
          class-name="small-padding fixed-width"
          min-width="120px"
        >
          <template #default="scope">
            <el-button
              size="small"
              link
              :icon="Edit"
              @click="handleUpdate(scope.row)"
              v-hasPermi="['iot:cardPlatform:edit']"
            >
              {{ $t('card.platform.edit') }}
            </el-button>
            <el-button
              size="small"
              link
              :icon="Delete"
              @click="handleDelete(scope.row)"
              v-hasPermi="['iot:cardPlatform:remove']"
            >
              {{ $t('card.platform.delete') }}
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
    </el-card>

    <!-- 添加或修改物联卡平台对话框 -->
    <el-dialog :title="title" v-model="open" width="600px" append-to-body>
      <el-form ref="formRef" :model="form" :rules="rules" label-width="100px">
        <el-form-item :label="$t('card.platform.name')" prop="name">
          <el-input v-model="form.name" :placeholder="$t('card.platform.please_enter_name')" style="width: 400px" />
        </el-form-item>
        <el-form-item :label="$t('card.platform.platform_label')" prop="platform">
          <el-select
            v-model="form.platform"
            :placeholder="$t('card.platform.please_select_platform')"
            style="width: 400px"
            @change="handlePlatformChange"
          >
            <el-option
              v-for="dict in iot_card_platform"
              :key="dict.value"
              :label="dict.label"
              :value="dict.value"
            ></el-option>
          </el-select>
        </el-form-item>
        <!-- 动态生成表单项 -->
        <div v-if="currentPlatformFields.length > 0">
          <el-form-item v-for="field in currentPlatformFields" :key="field.key" :label="field.label" :prop="field.key">
            <el-input
              v-model="form[field.key]"
              :placeholder="$t('card.platform.please_enter_name') + field.label"
              style="width: 400px"
            />
          </el-form-item>
        </div>
      </el-form>
      <template #footer>
        <el-button type="primary" @click="handleSubmit">{{ $t('card.platform.ok') }}</el-button>
        <el-button @click="handleCancel">{{ $t('card.platform.cancel') }}</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, computed, nextTick, getCurrentInstance, type ComponentInternalInstance } from 'vue';
import { Search, Refresh, Plus, Edit, Delete, Download } from '@element-plus/icons-vue';
import {
  listCardPlatform,
  getCardPlatform,
  delCardPlatform,
  addCardPlatform,
  updateCardPlatform,
} from '@/api/iot/cardPlatform';
import { useDict } from '@/utils/dict';

const { proxy } = getCurrentInstance() as any;
const { iot_card_platform } = useDict('iot_card_platform');

const loading = ref(true);
const ids = ref<any[]>([]);
const single = ref(true);
const multiple = ref(true);
const showSearch = ref(true);
const total = ref(0);
const cardPlatformList = ref<any[]>([]);
const title = ref('');
const open = ref(false);

const queryFormRef = ref<any>(null);
const formRef = ref<any>(null);

const queryParams = reactive({
  pageNum: 1,
  pageSize: 10,
  name: null as any,
  platform: null as any,
});

const form = ref<any>({});

const rules = reactive<any>({
  id: [{ required: true, message: proxy?.$t('card.platform.please_enter_id'), trigger: 'blur' }],
  name: [{ required: true, message: proxy?.$t('card.platform.please_enter_name'), trigger: 'blur' }],
  platform: [{ required: true, message: proxy?.$t('card.platform.please_select_platform'), trigger: 'change' }],
});

/** 获取当前平台需要显示的字段 */
const currentPlatformFields = computed(() => {
  if (!form.value.platform) return [];
  const selectedPlatform = iot_card_platform.value?.find((item: any) => item.value === form.value.platform);
  if (selectedPlatform && selectedPlatform.raw?.remark) {
    try {
      const remarkObj = JSON.parse(selectedPlatform.raw.remark);
      return Object.keys(remarkObj).map((key: string) => ({
        key,
        label: getFieldLabel(key),
        defaultValue: remarkObj[key],
      }));
    } catch (e) {
      console.error(proxy?.$t('card.platform.parse_remark_field_failed'), e);
    }
  }
  return [];
});

/** 查询列表 */
const getList = () => {
  loading.value = true;
  listCardPlatform(queryParams)
    .then((res: any) => {
      if (res.code === 200) {
        cardPlatformList.value = res.rows;
        total.value = res.total;
      }
      loading.value = false;
    })
    .catch(() => {
      loading.value = false;
    });
};

/** 重置 */
const reset = () => {
  form.value = { name: null, platform: null };
  proxy?.resetForm('formRef');
};

const handleQuery = () => {
  queryParams.pageNum = 1;
  getList();
};
const resetQuery = () => {
  queryFormRef.value?.resetFields();
  handleQuery();
};

const handleSelectionChange = (selection: any[]) => {
  ids.value = selection.map((item: any) => item.id);
  single.value = selection.length !== 1;
  multiple.value = !selection.length;
};

const handleAdd = () => {
  reset();
  open.value = true;
  title.value = proxy?.$t('card.platform.add_title');
};

const handleUpdate = (row: any) => {
  reset();
  const id = row.id || ids.value;
  getCardPlatform(id).then((res: any) => {
    if (res.code === 200) {
      form.value = res.data;
      if (form.value.configContent) {
        try {
          const config = JSON.parse(form.value.configContent);
          Object.keys(config).forEach((key: string) => {
            form.value[key] = config[key];
          });
        } catch (e) {
          console.error(proxy?.$t('card.platform.parse_config_content_failed'), e);
        }
      }
      open.value = true;
      title.value = proxy?.$t('card.platform.edit_title');
    }
  });
};

const handlePlatformChange = (value: any) => {
  if (currentPlatformFields.value.length > 0) {
    currentPlatformFields.value.forEach((field: any) => {
      form.value[field.key] = '';
    });
  }
  nextTick(() => {
    const selectedPlatform = iot_card_platform.value?.find((item: any) => item.value === value);
    if (selectedPlatform && selectedPlatform.raw?.remark) {
      try {
        const remarkObj = JSON.parse(selectedPlatform.raw.remark);
        Object.keys(remarkObj).forEach((key: string) => {
          if (!form.value.hasOwnProperty(key)) {
            form.value[key] = remarkObj[key] || '';
          }
        });
      } catch (e) {
        console.error(proxy?.$t('card.platform.parse_remark_field_failed'), e);
      }
    }
  });
};

const getFieldLabel = (key: string) => {
  const platformDict = iot_card_platform.value || [];
  for (const dictItem of platformDict) {
    if ((dictItem as any).value === form.value.platform && (dictItem as any).raw?.remark) {
      try {
        const remarkObj = JSON.parse((dictItem as any).raw.remark);
        if (remarkObj.labels && remarkObj.labels[key]) return remarkObj.labels[key];
      } catch (e) {
        console.error(proxy?.$t('card.platform.parse_remark_field_failed'), e);
      }
    }
  }
  return key;
};

const handleSubmit = () => {
  formRef.value?.validate((valid: boolean) => {
    if (valid) {
      const submitForm = { ...form.value };
      const configContentObj: any = {};
      if (currentPlatformFields.value.length > 0) {
        currentPlatformFields.value.forEach((field: any) => {
          configContentObj[field.key] = form.value[field.key];
        });
      }
      submitForm.configContent = JSON.stringify(configContentObj);
      Object.keys(configContentObj).forEach((key: string) => {
        delete submitForm[key];
      });
      const { id } = form.value;
      const api = id === undefined || id === null || id === '' ? addCardPlatform : updateCardPlatform;
      api(submitForm)
        .then((res: any) => {
          if (res.code === 200) {
            proxy?.$modal.msgSuccess(
              id === undefined || id === null || id === ''
                ? proxy?.$t('card.platform.add_success')
                : proxy?.$t('card.platform.modify_success')
            );
            open.value = false;
            getList();
          }
        })
        .catch((err: any) => {
          console.error(err);
        });
    }
  });
};

const handleCancel = () => {
  open.value = false;
  reset();
};

const handleDelete = (row: any) => {
  const delIds = row.id || ids.value;
  proxy?.$modal
    .confirm(proxy?.$t('card.platform.confirm_delete', { ids: delIds }))
    .then(() => {
      return delCardPlatform(delIds);
    })
    .then(() => {
      getList();
      proxy?.$modal.msgSuccess(proxy?.$t('card.platform.delete_success'));
    })
    .catch(() => {});
};

const handleExport = () => {
  proxy?.download('iot/cardPlatform/export', { ...queryParams }, `cardPlatform_${new Date().getTime()}.xlsx`);
};

getList();
</script>

<style lang="scss" scoped>
.app-content {
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
