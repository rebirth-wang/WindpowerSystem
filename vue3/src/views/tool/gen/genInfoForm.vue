<template>
  <el-form class="gen-gen-info-form" ref="genInfoFormRef" :model="info" :rules="rules" label-width="150px">
    <el-row>
      <el-col :span="12">
        <el-form-item prop="tplCategory">
          <template #label>{{ $t('gen.genInfoForm.432422-0') }}</template>
          <el-select v-model="info.tplCategory" @change="tplSelectChange" style="width: 200px">
            <el-option :label="$t('gen.genInfoForm.432422-1')" value="crud" />
            <el-option :label="$t('gen.genInfoForm.432422-2')" value="tree" />
            <el-option :label="$t('gen.genInfoForm.432422-3')" value="sub" />
          </el-select>
        </el-form-item>
      </el-col>
      <el-col :span="12">
        <el-form-item prop="packageName">
          <template #label>
            {{ $t('gen.genInfoForm.432422-4') }}
            <el-tooltip :content="$t('gen.genInfoForm.432422-5')" placement="top">
              <el-icon><QuestionFilled /></el-icon>
            </el-tooltip>
          </template>
          <el-input v-model="info.packageName" />
        </el-form-item>
      </el-col>

      <el-col :span="12">
        <el-form-item prop="moduleName">
          <template #label>
            {{ $t('gen.genInfoForm.432422-6') }}
            <el-tooltip :content="$t('gen.genInfoForm.432422-7')" placement="top">
              <el-icon><QuestionFilled /></el-icon>
            </el-tooltip>
          </template>
          <el-input v-model="info.moduleName" />
        </el-form-item>
      </el-col>

      <el-col :span="12">
        <el-form-item prop="businessName">
          <template #label>
            {{ $t('gen.genInfoForm.432422-8') }}
            <el-tooltip :content="$t('gen.genInfoForm.432422-9')" placement="top">
              <el-icon><QuestionFilled /></el-icon>
            </el-tooltip>
          </template>
          <el-input v-model="info.businessName" />
        </el-form-item>
      </el-col>

      <el-col :span="12">
        <el-form-item prop="functionName">
          <template #label>
            {{ $t('gen.genInfoForm.432422-10') }}
            <el-tooltip :content="$t('gen.genInfoForm.432422-11')" placement="top">
              <el-icon><QuestionFilled /></el-icon>
            </el-tooltip>
          </template>
          <el-input v-model="info.functionName" />
        </el-form-item>
      </el-col>

      <el-col :span="12">
        <el-form-item>
          <template #label>
            {{ $t('gen.genInfoForm.432422-12') }}
            <el-tooltip :content="$t('gen.genInfoForm.432422-13')" placement="top">
              <el-icon><QuestionFilled /></el-icon>
            </el-tooltip>
          </template>
          <el-tree-select
            v-model="info.parentMenuId"
            :data="menus"
            :props="{ children: 'children', label: 'menuName', value: 'menuId' } as any"
            check-strictly
            :render-after-expand="false"
            :placeholder="$t('gen.genInfoForm.432422-14')"
          />
        </el-form-item>
      </el-col>

      <el-col :span="12">
        <el-form-item prop="genType">
          <template #label>
            {{ $t('gen.genInfoForm.432422-15') }}
            <el-tooltip :content="$t('gen.genInfoForm.432422-16')" placement="top">
              <el-icon><QuestionFilled /></el-icon>
            </el-tooltip>
          </template>
          <el-radio v-model="info.genType" value="0">{{ $t('gen.genInfoForm.432422-17') }}</el-radio>
          <el-radio v-model="info.genType" value="1">{{ $t('gen.genInfoForm.432422-18') }}</el-radio>
        </el-form-item>
      </el-col>

      <el-col :span="24" v-if="info.genType == '1'">
        <el-form-item prop="genPath">
          <template #label>
            {{ $t('gen.genInfoForm.432422-19') }}
            <el-tooltip :content="$t('gen.genInfoForm.432422-20')" placement="top">
              <el-icon><QuestionFilled /></el-icon>
            </el-tooltip>
          </template>
          <el-input v-model="info.genPath">
            <template #append>
              <el-dropdown>
                <el-button type="primary">
                  {{ $t('gen.genInfoForm.432422-21') }}
                  <el-icon class="el-icon--right"><ArrowDown /></el-icon>
                </el-button>
                <template #dropdown>
                  <el-dropdown-menu>
                    <el-dropdown-item @click="info.genPath = '/'">
                      {{ $t('gen.genInfoForm.432422-22') }}
                    </el-dropdown-item>
                  </el-dropdown-menu>
                </template>
              </el-dropdown>
            </template>
          </el-input>
        </el-form-item>
      </el-col>
    </el-row>

    <el-row v-show="info.tplCategory == 'tree'">
      <h4 class="form-header">{{ $t('gen.genInfoForm.432422-23') }}</h4>
      <el-col :span="12">
        <el-form-item>
          <template #label>
            {{ $t('gen.genInfoForm.432422-24') }}
            <el-tooltip :content="$t('gen.genInfoForm.432422-25')" placement="top">
              <el-icon><QuestionFilled /></el-icon>
            </el-tooltip>
          </template>
          <el-select v-model="info.treeCode" :placeholder="$t('pleaseSelect')" style="width: 200px">
            <el-option
              v-for="(column, index) in info.columns"
              :key="index"
              :label="column.columnName + '：' + column.columnComment"
              :value="column.columnName"
            ></el-option>
          </el-select>
        </el-form-item>
      </el-col>
      <el-col :span="12">
        <el-form-item>
          <template #label>
            {{ $t('gen.genInfoForm.432422-26') }}
            <el-tooltip :content="$t('gen.genInfoForm.432422-27')" placement="top">
              <el-icon><QuestionFilled /></el-icon>
            </el-tooltip>
          </template>
          <el-select v-model="info.treeParentCode" :placeholder="$t('pleaseSelect')" style="width: 200px">
            <el-option
              v-for="(column, index) in info.columns"
              :key="index"
              :label="column.columnName + '：' + column.columnComment"
              :value="column.columnName"
            ></el-option>
          </el-select>
        </el-form-item>
      </el-col>
      <el-col :span="12">
        <el-form-item>
          <template #label>
            {{ $t('gen.genInfoForm.432422-28') }}
            <el-tooltip :content="$t('gen.genInfoForm.432422-29')" placement="top">
              <el-icon><QuestionFilled /></el-icon>
            </el-tooltip>
          </template>
          <el-select v-model="info.treeName" :placeholder="$t('pleaseSelect')" style="width: 200px">
            <el-option
              v-for="(column, index) in info.columns"
              :key="index"
              :label="column.columnName + '：' + column.columnComment"
              :value="column.columnName"
            ></el-option>
          </el-select>
        </el-form-item>
      </el-col>
    </el-row>
    <el-row v-show="info.tplCategory == 'sub'">
      <h4 class="form-header">{{ $t('gen.genInfoForm.432422-30') }}</h4>
      <el-col :span="12">
        <el-form-item>
          <template #label>
            {{ $t('gen.genInfoForm.432422-31') }}
            <el-tooltip :content="$t('gen.genInfoForm.432422-32')" placement="top">
              <el-icon><QuestionFilled /></el-icon>
            </el-tooltip>
          </template>
          <el-select
            v-model="info.subTableName"
            :placeholder="$t('pleaseSelect')"
            @change="subSelectChange"
            style="width: 200px"
          >
            <el-option
              v-for="(table, index) in tables"
              :key="index"
              :label="table.tableName + '：' + table.tableComment"
              :value="table.tableName"
            ></el-option>
          </el-select>
        </el-form-item>
      </el-col>
      <el-col :span="12">
        <el-form-item>
          <template #label>
            {{ $t('gen.genInfoForm.432422-33') }}
            <el-tooltip :content="$t('gen.genInfoForm.432422-34')" placement="top">
              <el-icon><QuestionFilled /></el-icon>
            </el-tooltip>
          </template>
          <el-select v-model="info.subTableFkName" :placeholder="$t('pleaseSelect')" style="width: 200px">
            <el-option
              v-for="(column, index) in subColumns"
              :key="index"
              :label="column.columnName + '：' + column.columnComment"
              :value="column.columnName"
            ></el-option>
          </el-select>
        </el-form-item>
      </el-col>
    </el-row>
  </el-form>
</template>

<script setup lang="ts">
import { ref, reactive, watch, getCurrentInstance } from 'vue';
import { QuestionFilled, ArrowDown } from '@element-plus/icons-vue';

const { proxy } = getCurrentInstance() as any;

const props = defineProps<{
  info: any;
  tables: any[];
  menus: any[];
}>();

const genInfoFormRef = ref<any>(null);
const subColumns = ref<any[]>([]);

const rules = reactive<any>({
  tplCategory: [{ required: true, message: proxy.$t('gen.genInfoForm.432422-35'), trigger: 'blur' }],
  packageName: [{ required: true, message: proxy.$t('gen.genInfoForm.432422-36'), trigger: 'blur' }],
  moduleName: [{ required: true, message: proxy.$t('gen.genInfoForm.432422-37'), trigger: 'blur' }],
  businessName: [{ required: true, message: proxy.$t('gen.genInfoForm.432422-38'), trigger: 'blur' }],
  functionName: [{ required: true, message: proxy.$t('gen.genInfoForm.432422-39'), trigger: 'blur' }],
});

watch(
  () => props.info?.subTableName,
  (val) => {
    if (val) {
      setSubTableColumns(val);
    }
  }
);

/** 选择子表名触发 */
function subSelectChange(_value: string) {
  props.info.subTableFkName = '';
}

/** 选择生成模板触发 */
function tplSelectChange(value: string) {
  if (value !== 'sub') {
    props.info.subTableName = '';
    props.info.subTableFkName = '';
  }
}

/** 设置关联外键 */
function setSubTableColumns(value: string) {
  for (const item of props.tables) {
    if (value === item.tableName) {
      subColumns.value = item.columns;
      break;
    }
  }
}

defineExpose({ genInfoFormRef });
</script>

<style lang="scss" scoped>
.gen-gen-info-form {
  margin: 40px 60px 0 0;
}
</style>
