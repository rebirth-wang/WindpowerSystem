<template>
  <div class="iot-device-recycle">
    <el-card class="top-card" :body-style="{ padding: '26px 20px' }">
      <div class="title-wrap">
        <el-button class="top-button" type="info" size="small" @click="goBack()">
          <el-icon><ArrowLeft /></el-icon>
          {{ $t('product.product-edit.473153-44') }}
        </el-button>
        <span class="info-item">{{ $t('device.device-recycle.864193-0') }}</span>
      </div>
    </el-card>

    <el-card class="main-card">
      <el-form ref="queryFormRef" :model="queryParams" :inline="true">
        <el-form-item label="" prop="deptId">
          <treeselect
            v-model="queryParams.deptId"
            :options="deptOptions"
            :show-count="true"
            :clearable="false"
            :searchable="true"
            :placeholder="$t('device.device-recycle.864193-2')"
            @input="getList"
            :append-to-body="false"
            style="width: 218px"
          />
        </el-form-item>
        <el-form-item label="" prop="productName">
          <el-input
            v-model="queryParams.productName"
            :placeholder="$t('device.device-edit.148398-5')"
            clearable
            style="width: 218px"
          >
            <template #append>
              <el-button @click="selectProduct()">{{ $t('device.device-edit.148398-6') }}</el-button>
            </template>
          </el-input>
        </el-form-item>
        <el-form-item prop="deviceName">
          <el-input
            :placeholder="$t('device.device-edit.148398-2')"
            clearable
            v-model="queryParams.deviceName"
          ></el-input>
        </el-form-item>
        <div style="float: right">
          <el-button type="primary" :icon="Search" @click="handleQuery">{{ $t('search') }}</el-button>
          <el-button style="margin-left: 5px" :icon="Refresh" @click="resetQuery">
            {{ $t('reset') }}
          </el-button>
        </div>
      </el-form>
      <div class="general">
        <div class="topLeft">
          <div style="padding: 15px 10px; background-color: #f4f4f5">
            <span style="font-size: 15px; font-weight: bold">{{ $t('device.device-recycle.864193-7') }}</span>
            <span style="font-size: 15px; font-weight: bold; float: right">{{ selectedCount }}/{{ count }}</span>
          </div>
          <el-row>
            <el-table
              ref="leftTableDataRef"
              :data="menuTableData"
              style="width: 100%"
              height="400"
              @selection-change="changeCheckBoxValueLeft"
              :border="false"
            >
              <template #empty>
                <el-empty
                  style="height: 400px"
                  :image-size="100"
                  :description="$t('device.device-recycle.864193-8')"
                ></el-empty>
              </template>
              <el-table-column
                type="selection"
                width="45"
                align="center"
                :selectable="checkSelectable"
              ></el-table-column>
              <el-table-column
                prop="deptName"
                :label="$t('device.device-recycle.864193-15')"
                align="center"
                show-overflow-tooltip
              ></el-table-column>
              <el-table-column
                prop="productName"
                :label="$t('device.allot-record.155854-2')"
                align="center"
                show-overflow-tooltip
              ></el-table-column>
              <el-table-column
                prop="serialNumber"
                :label="$t('device.device-edit.148398-7')"
                align="center"
                show-overflow-tooltip
              ></el-table-column>
              <el-table-column
                prop="deviceName"
                :label="$t('device.device-edit.148398-1')"
                align="center"
                show-overflow-tooltip
              ></el-table-column>
            </el-table>
          </el-row>
          <pagination
            v-show="total > 0"
            :total="total"
            v-model:page="queryParams.pageNum"
            :pager-count="5"
            v-model:limit="queryParams.pageSize"
            :page-sizes="[10, 20, 30, 40]"
            @pagination="getList"
            layout="prev, pager, next"
          />
        </div>
        <div class="topCenter">
          <el-button type="primary" :disabled="add" @click="rightAdd">
            <el-icon><ArrowRight /></el-icon>
          </el-button>
          <el-button type="primary" :disabled="del" @click="leftDelete">
            <el-icon><ArrowLeft /></el-icon>
          </el-button>
        </div>
        <div class="topRight">
          <div style="padding: 15px 10px; background-color: #f4f4f5">
            <span style="font-size: 15px; font-weight: bold">{{ $t('device.device-recycle.864193-12') }}</span>
            <span style="font-size: 15px; font-weight: bold; float: right">{{ selectedCountRight }}/500</span>
          </div>
          <el-row>
            <el-table
              ref="rightTableDataRef"
              :data="rightTableData"
              style="width: 100%"
              height="400"
              @selection-change="changeCheckBoxValueRight"
              :border="false"
            >
              <template #empty>
                <el-empty
                  style="height: 400px"
                  :image-size="100"
                  :description="$t('device.device-recycle.864193-8')"
                ></el-empty>
              </template>
              <el-table-column type="selection" width="45" align="center"></el-table-column>
              <el-table-column
                prop="deptName"
                :label="$t('device.device-recycle.864193-15')"
                align="center"
                show-overflow-tooltip
              ></el-table-column>
              <el-table-column
                prop="productName"
                :label="$t('device.allot-record.155854-2')"
                align="center"
                show-overflow-tooltip
              ></el-table-column>
              <el-table-column
                prop="serialNumber"
                :label="$t('device.device-edit.148398-7')"
                align="center"
                show-overflow-tooltip
              ></el-table-column>
              <el-table-column
                prop="deviceName"
                :label="$t('device.device-edit.148398-1')"
                align="center"
                show-overflow-tooltip
              ></el-table-column>
            </el-table>
          </el-row>
        </div>
      </div>

      <div class="footer">
        <el-button style="width: 100px" type="primary" @click="confirmDistribution">
          {{ $t('device.device-recycle.864193-13') }}
        </el-button>
      </div>
    </el-card>
    <product-list ref="productListRef" :product-id="queryParams.productId" @productEvent="getProductData($event)" />
  </div>
</template>

<script setup>
import { ref, reactive, computed, watch, onMounted, getCurrentInstance } from 'vue';
import { ElMessage } from 'element-plus';
import { Search, Refresh, ArrowLeft, ArrowRight } from '@element-plus/icons-vue';
import Treeselect from 'vue3-treeselect';
import 'vue3-treeselect/dist/vue3-treeselect.css';
import { deptsTreeSelectSub } from '@/api/system/user';
import { listDevice, recycleDevice } from '@/api/iot/device';
import productList from './product-list.vue';

const { proxy } = getCurrentInstance();

//响应式数据
const productListRef = ref();
const queryFormRef = ref();
const leftTableDataRef = ref();
const rightTableDataRef = ref();

const productListData = ref([]);
const loading = ref(false);
const total = ref(0);
const count = ref(0);
const selectedCount = ref(0);

const queryParams = reactive({
  productId: null,
  deviceName: '',
  pageNum: 1,
  pageSize: 10,
  showChild: true,
  deptId: null,
});

const deviceIds = ref({});
const deptOptions = ref([]); // 机构树选项
const add = ref(true);
const del = ref(true);
const leftTableData = ref([]);
const rightTableData = ref([]);
const selectedListLeft = ref([]); //点击左边选中的
const selectedListRight = ref([]); //点击右边选中的
const menuTableData = ref([]);

//计算属性
const selectedCountRight = computed(() => {
  return rightTableData.value.length;
});

//监听器
watch(selectedListLeft, (val) => {
  if (val.length) {
    add.value = false;
  } else {
    add.value = true;
  }
});

watch(selectedListRight, (val) => {
  if (val.length) {
    del.value = false;
  } else {
    del.value = true;
  }
});

// 查询所有设备列表
const getList = () => {
  loading.value = true;
  listDevice(queryParams).then((response) => {
    menuTableData.value = response.rows;
    // isSelect用于判断是否可选
    menuTableData.value.map((item) => {
      leftTableData.value.push(
        Object.assign(item, {
          isSelect: 0,
        })
      );
    });
    //分页后保持已选中状态
    if (rightTableData.value.length != 0) {
      menuTableData.value.forEach((item, index) => {
        rightTableData.value.forEach((item1) => {
          if (item1.deviceId == item.deviceId) {
            item.isSelect = 1;
          }
        });
      });
    }
    total.value = response.total;
    if (count.value === 0) {
      count.value = total.value;
    }
    loading.value = false;
  });
};

/** 搜索按钮操作 */
const handleQuery = () => {
  queryParams.pageNum = 1;
  getList();
};

/** 重置按钮操作 */
const resetQuery = () => {
  queryParams.productId = null;
  rightTableData.value = [];
  proxy.resetForm('queryFormRef');
  handleQuery();
};

/**选择产品 */
const selectProduct = () => {
  productListRef.value.open = true;
  productListRef.value.getList();
};

// 获取选中的产品
const getProductData = (product) => {
  queryParams.productId = product.productId;
  queryParams.productName = product.productName;
};

/** 查询机构下拉树结构 */
const getDeptTree = () => {
  const showOwner = false;
  deptsTreeSelectSub(showOwner).then((res) => {
    if (res.code === 200) {
      deptOptions.value = res.data || [];
      if (!!deptOptions.value[0]?.id) {
        queryParams.deptId = deptOptions.value[0].id;
      }
    }
  });
};

//右增加的数据
const rightAdd = () => {
  let leftTableData = JSON.parse(JSON.stringify(menuTableData.value));
  leftTableData.forEach((item, index) => {
    selectedListLeft.value.forEach((item1) => {
      if (item.deviceId == item1.deviceId) {
        rightTableData.value = rightTableData.value.concat(item).sort((a, b) => {
          return a.deviceId - b.deviceId;
        });
        item.isSelect = 1;
      }
    });
  });
  if (selectedCountRight.value != 0) {
    count.value = count.value - selectedListLeft.value.length;
  }
  leftTableData = leftTableData.filter((val) => {
    return val;
  });
  menuTableData.value = leftTableData;
  selectedListLeft.value = [];
};

//删除的数据
const leftDelete = () => {
  let rightTableData = JSON.parse(JSON.stringify(rightTableData.value));
  rightTableData.forEach((item, index) => {
    selectedListRight.value.forEach((item1) => {
      menuTableData.value.forEach((item2) => {
        if (item2.deviceId == item1.deviceId) {
          item2.isSelect = 0;
        }
        if (item1.deviceId == item.deviceId) {
          delete rightTableData[index];
        }
      });
    });
  });
  if (selectedCountRight.value != 0) {
    count.value = count.value + selectedListRight.value.length;
    getList();
  }
  rightTableData = rightTableData.filter((val) => {
    return val;
  });
  rightTableData.value = rightTableData;
  selectedListRight.value = [];
};

const checkSelectable = (row) => {
  let flag = true;
  if (row.isSelect === 0) {
    flag = true;
  } else {
    flag = false;
  }
  return flag;
};

//左边数据
const changeCheckBoxValueLeft = (val) => {
  selectedListLeft.value = val;
  selectedCount.value = val.length;
};

//右数据
const changeCheckBoxValueRight = (val) => {
  selectedListRight.value = val;
};

//确定回收操作
const confirmDistribution = () => {
  if (selectedListRight.value.length === 0) {
    ElMessage.warning(proxy.t('device.device-recycle.864193-16'));
    return;
  }

  //【选中的数据】组装参数，不是全部数据
  const deptDeviceObj = {};
  selectedListRight.value.forEach((item) => {
    if (item.deptId && item.deviceId) {
      deptDeviceObj[item.deviceId] = item.deptId;
    }
  });

  //键对值对 {deviceId:deptId}格式
  const param = deptDeviceObj;
  recycleDevice(param).then((response) => {
    if (response.code == 200) {
      ElMessage.success(response.msg);
      resetQuery();
    } else {
      ElMessage.error(response.msg);
    }
  });
};

// 返回按钮
const goBack = () => {
  proxy.$router.go(-1);
};

// 挂载时初始化
onMounted(() => {
  getDeptTree();
});

//暴给父组件使用
defineExpose({
  getList,
  handleQuery,
  resetQuery,
  selectProduct,
  getProductData,
  getDeptTree,
  rightAdd,
  leftDelete,
  checkSelectable,
  changeCheckBoxValueLeft,
  changeCheckBoxValueRight,
  confirmDistribution,
  goBack,
});
</script>

<style lang="scss" scoped>
.iot-device-recycle {
  padding: 20px;

  .top-card {
    margin-bottom: 10px;

    .title-wrap {
      display: flex;
      flex-direction: row;
      align-items: center;

      .top-button {
        height: 22px;
        color: #909399;
        background: #f4f5f7;
        padding: 0px 8px;
        border: none;
      }

      .info-item {
        font-weight: normal;
        font-size: 14px;
        color: #333333;
        line-height: 20px;
        margin-left: 36px;
      }
    }
  }

  :deep(.vue-treeselect) {
    width: 182.5px;
    height: 34.5px;
    line-height: 34.5px;
    font-size: inherit;
    color: #606266;
  }

  :deep(.vue-treeselect__control) {
    height: 34.5px;
  }

  :deep(.vue-treeselect__placeholder),
  :deep(.vue-treeselect__single-value) {
    line-height: 34.5px;
  }

  :deep(.vue-treeselect__input) {
    width: 100%;
    height: unset;
  }

  .main-card {
    position: relative;
    padding-bottom: 100px;

    .general {
      display: flex;
      align-items: center;
      justify-content: space-between;
      padding-bottom: 20px;

      .topLeft {
        width: 44%;
        height: 450px;
      }

      .topCenter {
        width: 10%;
        display: flex;
        flex-direction: column;
        align-items: center;

        .el-button + .el-button {
          margin-left: 0px;
          margin-top: 10px;
        }
      }

      .topRight {
        width: 44%;
        height: 450px;
      }
    }

    .footer {
      position: absolute;
      bottom: 20px;
      left: 50%;
      margin-left: -49px;
    }
  }
}
</style>
