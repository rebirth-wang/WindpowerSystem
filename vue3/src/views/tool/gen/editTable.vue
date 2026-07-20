<template>
  <div class="tool-gen-edit-table">
    <el-card>
      <el-tabs v-model="activeName">
        <el-tab-pane :label="$t('gen.editTable.650980-0')" name="basic">
          <basic-info-form ref="basicInfoRef" :info="info" />
        </el-tab-pane>
        <el-tab-pane :label="$t('gen.editTable.650980-1')" name="columnInfo">
          <el-table ref="dragTableRef" :data="columns" row-key="columnId" :max-height="tableHeight" :border="false">
            <el-table-column :label="$t('gen.editTable.650980-2')" type="index" min-width="55" class-name="allowDrag" />
            <el-table-column
              :label="$t('gen.editTable.650980-3')"
              prop="columnName"
              align="left"
              min-width="140"
              :show-overflow-tooltip="true"
            />
            <el-table-column :label="$t('gen.editTable.650980-4')" align="left" min-width="140">
              <template #default="scope">
                <el-input v-model="scope.row.columnComment"></el-input>
              </template>
            </el-table-column>
            <el-table-column
              :label="$t('gen.editTable.650980-5')"
              prop="columnType"
              align="center"
              min-width="110"
              :show-overflow-tooltip="true"
            />
            <el-table-column :label="$t('gen.editTable.650980-6')" align="center" min-width="120">
              <template #default="scope">
                <el-select v-model="scope.row.javaType" style="width: 200px">
                  <el-option label="Long" value="Long" />
                  <el-option label="String" value="String" />
                  <el-option label="Integer" value="Integer" />
                  <el-option label="Double" value="Double" />
                  <el-option label="BigDecimal" value="BigDecimal" />
                  <el-option label="Date" value="Date" />
                  <el-option label="Boolean" value="Boolean" />
                </el-select>
              </template>
            </el-table-column>
            <el-table-column :label="$t('gen.editTable.650980-7')" align="left" min-width="120">
              <template #default="scope">
                <el-input v-model="scope.row.javaField"></el-input>
              </template>
            </el-table-column>
            <el-table-column :label="$t('gen.editTable.650980-8')" align="center" min-width="60">
              <template #default="scope">
                <el-checkbox true-value="1" false-value="0" v-model="scope.row.isInsert"></el-checkbox>
              </template>
            </el-table-column>
            <el-table-column :label="$t('gen.editTable.650980-9')" align="center" min-width="60">
              <template #default="scope">
                <el-checkbox true-value="1" false-value="0" v-model="scope.row.isEdit"></el-checkbox>
              </template>
            </el-table-column>
            <el-table-column :label="$t('gen.editTable.650980-10')" align="center" min-width="60">
              <template #default="scope">
                <el-checkbox true-value="1" false-value="0" v-model="scope.row.isList"></el-checkbox>
              </template>
            </el-table-column>
            <el-table-column :label="$t('gen.editTable.650980-11')" align="center" min-width="60">
              <template #default="scope">
                <el-checkbox true-value="1" false-value="0" v-model="scope.row.isQuery"></el-checkbox>
              </template>
            </el-table-column>
            <el-table-column :label="$t('gen.editTable.650980-12')" align="center" min-width="120">
              <template #default="scope">
                <el-select v-model="scope.row.queryType" style="width: 200px">
                  <el-option label="=" value="EQ" />
                  <el-option label="!=" value="NE" />
                  <el-option label=">" value="GT" />
                  <el-option label=">=" value="GE" />
                  <el-option label="<" value="LT" />
                  <el-option label="<=" value="LE" />
                  <el-option label="LIKE" value="LIKE" />
                  <el-option label="BETWEEN" value="BETWEEN" />
                </el-select>
              </template>
            </el-table-column>
            <el-table-column :label="$t('gen.editTable.650980-13')" align="center" min-width="60">
              <template #default="scope">
                <el-checkbox true-value="1" false-value="0" v-model="scope.row.isRequired"></el-checkbox>
              </template>
            </el-table-column>
            <el-table-column :label="$t('gen.editTable.650980-14')" align="center" min-width="130">
              <template #default="scope">
                <el-select v-model="scope.row.htmlType" style="width: 200px">
                  <el-option :label="$t('gen.editTable.650980-15')" value="input" />
                  <el-option :label="$t('gen.editTable.650980-16')" value="textarea" />
                  <el-option :label="$t('gen.editTable.650980-17')" value="select" />
                  <el-option :label="$t('gen.editTable.650980-18')" value="radio" />
                  <el-option :label="$t('gen.editTable.650980-19')" value="checkbox" />
                  <el-option :label="$t('gen.editTable.650980-20')" value="datetime" />
                  <el-option :label="$t('gen.editTable.650980-21')" value="imageUpload" />
                  <el-option :label="$t('gen.editTable.650980-22')" value="fileUpload" />
                  <el-option :label="$t('gen.editTable.650980-23')" value="editor" />
                </el-select>
              </template>
            </el-table-column>
            <el-table-column :label="$t('gen.editTable.650980-24')" align="center" min-width="130">
              <template #default="scope">
                <el-select
                  v-model="scope.row.dictType"
                  clearable
                  filterable
                  :placeholder="$t('pleaseSelect')"
                  style="width: 200px"
                >
                  <el-option
                    v-for="dict in dictOptions"
                    :key="dict.dictType"
                    :label="dict.dictName"
                    :value="dict.dictType"
                  >
                    <span style="float: left">{{ dict.dictName }}</span>
                    <span style="float: right; color: #8492a6; font-size: 13px">{{ dict.dictType }}</span>
                  </el-option>
                </el-select>
              </template>
            </el-table-column>
          </el-table>
        </el-tab-pane>
        <el-tab-pane :label="$t('gen.editTable.650980-25')" name="genInfo">
          <gen-info-form ref="genInfoRef" :info="info" :tables="tables" :menus="menus" />
        </el-tab-pane>
      </el-tabs>
      <el-form label-width="100px">
        <el-form-item style="text-align: center; margin-left: -100px; margin-top: 50px">
          <el-button type="primary" @click="submitForm()">{{ $t('submit') }}</el-button>
          <el-button @click="close()">{{ $t('gen.editTable.650980-27') }}</el-button>
        </el-form-item>
      </el-form>
    </el-card>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted, nextTick, getCurrentInstance } from 'vue';
import { useRoute } from 'vue-router';
import { getGenTable, updateGenTable } from '@/api/tool/gen';
import { optionselect as getDictOptionselect } from '@/api/system/dict/type';
import { listMenu as getMenuTreeselect } from '@/api/system/menu';
import basicInfoForm from './basicInfoForm.vue';
import genInfoForm from './genInfoForm.vue';
import Sortable from 'sortablejs';

const { proxy } = getCurrentInstance() as any;
const route = useRoute();

const activeName = ref('columnInfo');
const tableHeight = ref(document.documentElement.scrollHeight - 245 + 'px');
const tables = ref<any[]>([]);
const columns = ref<any[]>([]);
const dictOptions = ref<any[]>([]);
const menus = ref<any[]>([]);
const info = ref<any>({});
const basicInfoRef = ref<any>(null);
const genInfoRef = ref<any>(null);
const dragTableRef = ref<any>(null);

/** 初始化数据 */
function initData() {
  const tableId = route.params?.tableId;
  if (tableId) {
    getGenTable(tableId).then((res: any) => {
      columns.value = res.data.rows;
      info.value = res.data.info;
      tables.value = res.data.tables;
    });
    getDictOptionselect().then((response: any) => {
      dictOptions.value = response.data;
    });
    getMenuTreeselect().then((response: any) => {
      menus.value = proxy.handleTree(response.data, 'menuId');
    });
  }
}

/** 提交按钮 */
function submitForm() {
  const basicForm = basicInfoRef.value?.basicInfoFormRef;
  const genForm = genInfoRef.value?.genInfoFormRef;
  Promise.all([basicForm, genForm].map(getFormPromise)).then((res: any[]) => {
    const validateResult = res.every((item) => !!item);
    if (validateResult) {
      const genTable = Object.assign({}, basicForm.model, genForm.model);
      genTable.columns = columns.value;
      genTable.params = {
        treeCode: genTable.treeCode,
        treeName: genTable.treeName,
        treeParentCode: genTable.treeParentCode,
        parentMenuId: genTable.parentMenuId,
      };
      updateGenTable(genTable).then((res: any) => {
        proxy.$modal.msgSuccess(res.msg);
        if (res.code === 200) {
          close();
        }
      });
    } else {
      proxy.$modal.msgError(proxy.$t('gen.editTable.650980-26'));
    }
  });
}

function getFormPromise(form: any) {
  return new Promise((resolve) => {
    form.validate((res: any) => {
      resolve(res);
    });
  });
}

/** 关闭按钮 */
function close() {
  const obj = { path: '/tool/gen', query: { t: Date.now(), pageNum: route.query.pageNum } };
  (proxy as any).$tab.closeOpenPage(obj);
}

onMounted(() => {
  initData();
  nextTick(() => {
    if (dragTableRef.value?.$el) {
      const el = dragTableRef.value.$el.querySelectorAll('.el-table__body-wrapper > table > tbody')[0];
      if (el) {
        Sortable.create(el, {
          handle: '.allowDrag',
          onEnd: (evt: any) => {
            const targetRow = columns.value.splice(evt.oldIndex, 1)[0];
            columns.value.splice(evt.newIndex, 0, targetRow);
            for (let index in columns.value) {
              columns.value[index].sort = parseInt(index) + 1;
            }
          },
        });
      }
    }
  });
});
</script>

<style lang="scss" scoped>
.tool-gen-edit-table {
  padding: 20px;
}
</style>
