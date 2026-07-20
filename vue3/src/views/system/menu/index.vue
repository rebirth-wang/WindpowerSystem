<template>
  <div class="system-menu">
    <el-card v-show="showSearch" class="search-card">
      <el-form
        @submit.prevent
        :model="queryParams"
        ref="queryFormRef"
        :inline="true"
        label-width="68px"
        class="search-form"
      >
        <el-form-item prop="menuName">
          <el-input
            v-model="queryParams.menuName"
            :placeholder="$t('system.menu.034890-1')"
            clearable
            style="width: 192px"
            @keyup.enter="handleQuery"
          />
        </el-form-item>
        <el-form-item prop="status">
          <el-select
            v-model="queryParams.status"
            :placeholder="$t('system.menu.034890-2')"
            clearable
            style="width: 192px"
          >
            <el-option
              v-for="item in dict.type.sys_normal_disable"
              :key="item.value"
              :label="item.label"
              :value="item.value"
            />
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
          <el-button type="primary" plain :icon="Plus" @click="handleAdd()" v-hasPermi="['system:menu:add']">
            {{ $t('add') }}
          </el-button>
        </el-col>
        <el-col :span="1.5">
          <el-button plain :icon="Sort" @click="toggleExpandAll">
            {{ $t('role.index.094567-18') }}
          </el-button>
        </el-col>
        <right-toolbar v-model:showSearch="showSearch" @queryTable="getList" />
      </el-row>

      <el-table
        v-if="refreshTable"
        v-loading="loading"
        :data="menuList"
        :border="false"
        row-key="menuId"
        :default-expand-all="isExpandAll"
        :tree-props="{ children: 'children', hasChildren: 'hasChildren' }"
      >
        <el-table-column prop="menuName" :label="$t('system.menu.034890-0')" show-overflow-tooltip min-width="200" />
        <el-table-column prop="icon" :label="$t('system.menu.034890-3')" align="center" width="80">
          <template #default="scope">
            <svg-icon :icon-class="scope.row.icon" />
          </template>
        </el-table-column>
        <el-table-column prop="orderNum" :label="$t('system.menu.034890-4')" width="55" />
        <el-table-column prop="perms" :label="$t('system.menu.034890-5')" show-overflow-tooltip width="200" />
        <el-table-column prop="component" :label="$t('system.menu.034890-6')" show-overflow-tooltip width="180" />
        <el-table-column prop="status" :label="$t('status')" align="center" width="80">
          <template #default="scope">
            <dict-tag :options="dict.type.sys_normal_disable" :value="scope.row.status" />
          </template>
        </el-table-column>
        <el-table-column :label="$t('creatTime')" align="center" prop="createTime" width="180">
          <template #default="scope">
            <span>{{ parseTime(scope.row.createTime) }}</span>
          </template>
        </el-table-column>
        <el-table-column
          fixed="right"
          :label="$t('opation')"
          align="center"
          class-name="small-padding fixed-width"
          width="200"
        >
          <template #default="scope">
            <el-button
              size="small"
              link
              :icon="Edit"
              @click="handleUpdate(scope.row)"
              v-hasPermi="['system:menu:edit']"
            >
              {{ $t('update') }}
            </el-button>
            <el-button size="small" link :icon="Plus" @click="handleAdd(scope.row)" v-hasPermi="['system:menu:add']">
              {{ $t('add') }}
            </el-button>
            <el-button
              size="small"
              link
              :icon="Delete"
              @click="handleDelete(scope.row)"
              v-hasPermi="['system:menu:remove']"
            >
              {{ $t('del') }}
            </el-button>
          </template>
        </el-table-column>
      </el-table>

      <!-- 添加或修改菜单对话框 -->
      <el-dialog :title="title" v-model="open" width="760px" append-to-body>
        <el-form ref="formRef" :model="form" :rules="rules" label-width="120px">
          <el-row>
            <el-col :span="24">
              <el-form-item :label="$t('system.menu.034890-7')" prop="parentId">
                <treeselect
                  v-model="form.parentId"
                  :options="menuOptions"
                  :normalizer="normalizer"
                  :show-count="true"
                  :placeholder="$t('system.menu.034890-8')"
                  style="width: 560px"
                />
              </el-form-item>
            </el-col>
            <el-col :span="24">
              <el-form-item :label="$t('system.menu.034890-9')" prop="menuType">
                <el-radio-group v-model="form.menuType">
                  <el-radio v-for="item in dict.type.menu_type" :key="item.value" :value="item.value">
                    {{ item.label }}
                  </el-radio>
                </el-radio-group>
              </el-form-item>
            </el-col>
            <el-col :span="24" v-if="form.menuType != 'F'">
              <el-form-item :label="$t('system.menu.034890-13')" prop="icon">
                <el-popover placement="bottom-start" :width="560" trigger="click" @show="iconSelectRef?.reset()">
                  <template #reference>
                    <el-input
                      v-model="form.icon"
                      :placeholder="$t('system.menu.034890-14')"
                      readonly
                      style="width: 560px"
                    >
                      <template #prefix>
                        <svg-icon v-if="form.icon" :icon-class="form.icon" style="height: 32px; width: 16px" />
                        <el-icon v-else><Search /></el-icon>
                      </template>
                    </el-input>
                  </template>
                  <IconSelect ref="iconSelectRef" @selected="selected" />
                </el-popover>
              </el-form-item>
            </el-col>
            <el-col :span="12">
              <el-form-item :label="$t('system.menu.034890-0')" prop="menuName">
                <el-input v-model="form.menuName" :placeholder="$t('system.menu.034890-1')" style="width: 200px" />
              </el-form-item>
            </el-col>
            <el-col :span="12">
              <el-form-item :label="$t('system.menu.034890-46')" prop="menuName_en_US">
                <el-input
                  v-model="form.menuName_en_US"
                  :placeholder="$t('system.menu.034890-47')"
                  style="width: 200px"
                />
              </el-form-item>
            </el-col>
            <el-col :span="12">
              <el-form-item :label="$t('system.menu.034890-15')" prop="orderNum">
                <el-input-number v-model="form.orderNum" controls-position="right" :min="0" style="width: 200px" />
              </el-form-item>
            </el-col>
            <el-col :span="12" v-if="form.menuType != 'F'">
              <el-form-item prop="isFrame">
                <template #label>
                  <span style="display: inline-flex; align-items: center">
                    <el-tooltip :content="$t('system.menu.034890-16')" placement="top">
                      <el-icon style="margin-right: 4px"><QuestionFilled /></el-icon>
                    </el-tooltip>
                    {{ $t('system.menu.034890-17') }}
                  </span>
                </template>
                <el-radio-group v-model="form.isFrame">
                  <el-radio :value="0">{{ $t('scene.index.670805-1') }}</el-radio>
                  <el-radio :value="1">{{ $t('scene.index.670805-2') }}</el-radio>
                </el-radio-group>
              </el-form-item>
            </el-col>
            <el-col :span="12" v-if="form.menuType != 'F'">
              <el-form-item prop="path">
                <template #label>
                  <span style="display: inline-flex; align-items: center">
                    <el-tooltip :content="$t('system.menu.034890-18')" placement="top">
                      <el-icon style="margin-right: 4px"><QuestionFilled /></el-icon>
                    </el-tooltip>
                    {{ $t('system.menu.034890-19') }}
                  </span>
                </template>
                <el-input v-model="form.path" :placeholder="$t('system.menu.034890-20')" style="width: 200px" />
              </el-form-item>
            </el-col>
            <el-col :span="12" v-if="form.menuType == 'C'">
              <el-form-item prop="component">
                <template #label>
                  <span style="display: inline-flex; align-items: center">
                    <el-tooltip :content="$t('system.menu.034890-21')" placement="top">
                      <el-icon style="margin-right: 4px"><QuestionFilled /></el-icon>
                    </el-tooltip>
                    {{ $t('system.menu.034890-22') }}
                  </span>
                </template>
                <el-input v-model="form.component" :placeholder="$t('system.menu.034890-23')" style="width: 200px" />
              </el-form-item>
            </el-col>
            <el-col :span="12" v-if="form.menuType != 'M'">
              <el-form-item prop="perms">
                <template #label>
                  <span style="display: inline-flex; align-items: center">
                    <el-tooltip :content="$t('system.menu.034890-25')" placement="top">
                      <el-icon style="margin-right: 4px"><QuestionFilled /></el-icon>
                    </el-tooltip>
                    {{ $t('system.menu.034890-26') }}
                  </span>
                </template>
                <el-input
                  v-model="form.perms"
                  :placeholder="$t('system.menu.034890-24')"
                  maxlength="100"
                  style="width: 200px"
                />
              </el-form-item>
            </el-col>
            <el-col :span="12" v-if="form.menuType == 'C'">
              <el-form-item prop="queryParam">
                <template #label>
                  <span style="display: inline-flex; align-items: center">
                    <el-tooltip :content="$t('system.menu.034890-28')" placement="top">
                      <el-icon style="margin-right: 4px"><QuestionFilled /></el-icon>
                    </el-tooltip>
                    {{ $t('system.menu.034890-29') }}
                  </span>
                </template>
                <el-input
                  v-model="form.queryParam"
                  :placeholder="$t('system.menu.034890-27')"
                  maxlength="255"
                  style="width: 200px"
                />
              </el-form-item>
            </el-col>
            <el-col :span="12" v-if="form.menuType == 'C'">
              <el-form-item prop="isCache">
                <template #label>
                  <span style="display: inline-flex; align-items: center">
                    <el-tooltip :content="$t('system.menu.034890-30')" placement="top">
                      <el-icon style="margin-right: 4px"><QuestionFilled /></el-icon>
                    </el-tooltip>
                    {{ $t('system.menu.034890-31') }}
                  </span>
                </template>
                <el-radio-group v-model="form.isCache">
                  <el-radio :value="0">{{ $t('system.menu.034890-32') }}</el-radio>
                  <el-radio :value="1">{{ $t('system.menu.034890-33') }}</el-radio>
                </el-radio-group>
              </el-form-item>
            </el-col>
            <el-col :span="12" v-if="form.menuType === 'C' && form.isFrame === 0">
              <el-form-item prop="target">
                <template #label>
                  <span style="display: inline-flex; align-items: center">
                    <el-tooltip :content="$t('system.menu.034890-44')" placement="top">
                      <el-icon style="margin-right: 4px"><QuestionFilled /></el-icon>
                    </el-tooltip>
                    {{ $t('system.menu.034890-45') }}
                  </span>
                </template>
                <el-radio-group v-model="form.target">
                  <el-radio v-for="item in dict.type.sys_menu_target" :key="item.value" :value="Number(item.value)">
                    {{ item.label }}
                  </el-radio>
                </el-radio-group>
              </el-form-item>
            </el-col>
            <el-col :span="12" v-if="form.menuType != 'F'">
              <el-form-item prop="visible">
                <template #label>
                  <span style="display: inline-flex; align-items: center">
                    <el-tooltip :content="$t('system.menu.034890-34')" placement="top">
                      <el-icon style="margin-right: 4px"><QuestionFilled /></el-icon>
                    </el-tooltip>
                    {{ $t('system.menu.034890-35') }}
                  </span>
                </template>
                <el-radio-group v-model="form.visible">
                  <el-radio v-for="item in dict.type.sys_show_hide" :key="item.value" :value="item.value">
                    {{ item.label }}
                  </el-radio>
                </el-radio-group>
              </el-form-item>
            </el-col>
            <el-col :span="12" v-if="form.menuType != 'F'">
              <el-form-item prop="status">
                <template #label>
                  <span style="display: inline-flex; align-items: center">
                    <el-tooltip :content="$t('system.menu.034890-36')" placement="top">
                      <el-icon style="margin-right: 4px"><QuestionFilled /></el-icon>
                    </el-tooltip>
                    {{ $t('system.menu.034890-2') }}
                  </span>
                </template>
                <el-radio-group v-model="form.status">
                  <el-radio v-for="item in dict.type.sys_normal_disable" :key="item.value" :value="Number(item.value)">
                    {{ item.label }}
                  </el-radio>
                </el-radio-group>
              </el-form-item>
            </el-col>
          </el-row>
        </el-form>
        <template #footer>
          <div class="dialog-footer">
            <el-button type="primary" @click="submitForm">{{ $t('confirm') }}</el-button>
            <el-button @click="cancel">{{ $t('cancel') }}</el-button>
          </div>
        </template>
      </el-dialog>
    </el-card>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, nextTick, onMounted, getCurrentInstance } from 'vue';
import { useI18n } from 'vue-i18n';
import { Search, Refresh, Plus, Delete, Edit, Sort, QuestionFilled } from '@element-plus/icons-vue';
import Treeselect from 'vue3-treeselect';
import 'vue3-treeselect/dist/vue3-treeselect.css';
import IconSelect from '@/components/IconSelect/index.vue';
import { listMenu, getMenu, delMenu, addMenu, updateMenu } from '@/api/system/menu';
import { parseTime, handleTree } from '@/utils/ruoyi';
import { useDict } from '@/utils/dict/useDict';

const { t } = useI18n();
const { proxy } = getCurrentInstance()!;
const { dict } = useDict('sys_show_hide', 'sys_normal_disable', 'sys_menu_target', 'menu_type');

const loading = ref(true);
const showSearch = ref(true);
const menuList = ref<any[]>([]);
const menuOptions = ref<any[]>([]);
const title = ref('');
const open = ref(false);
const isExpandAll = ref(false);
const refreshTable = ref(true);
const form = ref<any>({});

const queryFormRef = ref();
const formRef = ref();
const iconSelectRef = ref();

const queryParams = reactive({
  menuName: '',
  visible: '',
  status: undefined as string | undefined,
});

const rules = reactive({
  menuName: [{ required: true, message: t('system.menu.034890-37'), trigger: 'blur' }],
  orderNum: [{ required: true, message: t('system.menu.034890-38'), trigger: 'blur' }],
  path: [{ required: true, message: t('system.menu.034890-39'), trigger: 'blur' }],
  menuName_en_US: [{ required: true, message: t('system.menu.034890-47'), trigger: 'blur' }],
});

onMounted(() => {
  getList();
});

function selected(name: string) {
  form.value.icon = name;
}

function getList() {
  loading.value = true;
  listMenu(queryParams).then((res: any) => {
    menuList.value = handleTree(res.data, 'menuId');
    loading.value = false;
  });
}

function normalizer(node: any) {
  if (node.children && !node.children.length) {
    delete node.children;
  }
  return { id: node.menuId, label: node.menuName, children: node.children };
}

function getTreeselect() {
  listMenu().then((res: any) => {
    menuOptions.value = [];
    const menu = { menuId: 0, menuName: t('system.menu.034890-40'), children: [] as any[] };
    menu.children = handleTree(res.data, 'menuId');
    menuOptions.value.push(menu);
  });
}

function cancel() {
  open.value = false;
  reset();
}

function reset() {
  form.value = {
    menuId: undefined,
    parentId: 0,
    menuName: undefined,
    icon: undefined,
    menuType: 'M',
    orderNum: undefined,
    isFrame: 1,
    isCache: 0,
    visible: '0',
    status: 0,
    target: 0,
  };
  formRef.value?.resetFields();
}

function handleQuery() {
  getList();
}

function resetQuery() {
  queryFormRef.value?.resetFields();
  handleQuery();
}

function handleAdd(row?: any) {
  reset();
  getTreeselect();
  if (row != null && row.menuId) {
    form.value.parentId = row.menuId;
  } else {
    form.value.parentId = 0;
  }
  open.value = true;
  title.value = t('system.menu.034890-41');
}

function toggleExpandAll() {
  refreshTable.value = false;
  isExpandAll.value = !isExpandAll.value;
  nextTick(() => {
    refreshTable.value = true;
  });
}

function handleUpdate(row: any) {
  reset();
  getTreeselect();
  getMenu(row.menuId).then((res: any) => {
    form.value = res.data;
    open.value = true;
    title.value = t('system.menu.034890-42');
  });
}

function submitForm() {
  formRef.value?.validate((valid: boolean) => {
    if (valid) {
      if (form.value.menuId != undefined) {
        updateMenu(form.value).then(() => {
          (proxy as any).$modal.msgSuccess(t('updateSuccess'));
          open.value = false;
          getList();
        });
      } else {
        addMenu(form.value).then(() => {
          (proxy as any).$modal.msgSuccess(t('addSuccess'));
          open.value = false;
          getList();
        });
      }
    }
  });
}

function handleDelete(row: any) {
  (proxy as any).$modal
    .confirm(t('system.menu.034890-43', [row.menuName]))
    .then(() => delMenu(row.menuId))
    .then(() => {
      getList();
      (proxy as any).$modal.msgSuccess(t('delSuccess'));
    })
    .catch(() => {});
}
</script>

<style lang="scss" scoped>
.system-menu {
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
