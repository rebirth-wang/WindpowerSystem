<template>
  <div class="select-allot-wrap">
    <el-card class="top-card" :body-style="{ padding: '26px 20px' }">
      <div class="title-wrap">
        <el-button class="top-button" type="info" size="small" @click="goBack()">
          <el-icon><ArrowLeft /></el-icon>
          {{ $t('product.product-edit.473153-44') }}
        </el-button>
        <span class="info-item">{{ $t('device.device-select-allot.903153-0') }}</span>
      </div>
    </el-card>

    <el-card class="main-card">
      <el-row>
        <el-form ref="queryFormRef" :model="queryParams" :inline="true">
          <el-form-item label="" prop="productName">
            <el-input
              readonly
              v-model="queryParams.productName"
              :placeholder="$t('device.allot-import-dialog.060657-1')"
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
          <el-form-item prop="serialNumber">
            <el-input
              :placeholder="$t('device.device-edit.148398-8')"
              clearable
              v-model="queryParams.serialNumber"
            ></el-input>
          </el-form-item>

          <div style="float: right">
            <el-button type="primary" @click="handleQuery">
              {{ $t('device.device-select-allot.903153-4') }}
            </el-button>
            <el-button @click="resetQuery" style="margin-left: 5px">
              {{ $t('device.device-select-allot.903153-5') }}
            </el-button>
          </div>
        </el-form>
      </el-row>
      <div class="general">
        <div class="topLeft">
          <div style="padding: 15px 10px; background-color: #f4f4f5">
            <span style="font-size: 15px; font-weight: bold">{{ $t('device.device-select-allot.903153-6') }}</span>
            <span style="font-size: 15px; font-weight: bold; float: right">{{ selectedCount }}/{{ count }}</span>
          </div>
          <el-row>
            <el-table
              ref="leftTableDataRef"
              :data="menuTableData"
              style="width: 100%"
              max-height="400"
              @selection-change="changeCheckBoxValueLeft"
              :border="false"
            >
              <template #empty>
                <div class="table-empty-wrap">
                  <el-empty :image-size="100" :description="$t('device.device-select-allot.903153-7')"></el-empty>
                </div>
              </template>
              <el-table-column
                type="selection"
                align="center"
                width="50"
                :selectable="checkSelectable"
              ></el-table-column>
              <el-table-column
                prop="productName"
                :label="$t('device.allot-record.155854-2')"
                show-overflow-tooltip
              ></el-table-column>
              <el-table-column
                prop="serialNumber"
                :label="$t('device.device-edit.148398-7')"
                show-overflow-tooltip
              ></el-table-column>
              <el-table-column
                prop="deviceName"
                :label="$t('device.device-edit.148398-1')"
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
          <el-button class="transfer-btn" type="primary" :disabled="add" @click="rightAdd">></el-button>
          <el-button class="transfer-btn" type="primary" :disabled="del" @click="leftDelete"><</el-button>
        </div>
        <div class="topRight">
          <div style="padding: 15px 10px; background-color: #f4f4f5">
            <span style="font-size: 15px; font-weight: bold">{{ $t('device.device-select-allot.903153-11') }}</span>
            <span style="font-size: 15px; font-weight: bold; float: right">{{ selectedCountRight }}/500</span>
          </div>
          <el-row>
            <el-table
              ref="rightTableDataRef"
              :data="rightTableData"
              style="width: 100%"
              max-height="400"
              @selection-change="changeCheckBoxValueRight"
              :border="false"
            >
              <template #empty>
                <div class="table-empty-wrap">
                  <el-empty :image-size="100" :description="$t('device.device-select-allot.903153-7')"></el-empty>
                </div>
              </template>
              <el-table-column type="selection" align="center" width="50"></el-table-column>
              <el-table-column
                prop="productName"
                :label="$t('device.allot-record.155854-2')"
                show-overflow-tooltip
              ></el-table-column>
              <el-table-column
                prop="serialNumber"
                :label="$t('device.device-edit.148398-7')"
                show-overflow-tooltip
              ></el-table-column>
              <el-table-column
                prop="deviceName"
                :label="$t('device.device-edit.148398-1')"
                show-overflow-tooltip
              ></el-table-column>
            </el-table>
          </el-row>
        </div>
      </div>

      <div style="width: 100%">
        <el-form label-position="top" :model="allotForm" ref="allotFormRef" :rules="allotRules">
          <div style="width: 45%; margin: 60px 0">
            <el-form-item :label="$t('device.user-list.041943-0')" prop="userId">
              <el-select
                v-model="allotForm.userId"
                :placeholder="$t('device.user-list.041943-0')"
                clearable
                style="width: 100%"
                filterable
              >
                <el-option
                  v-for="item in userList"
                  :key="item.value"
                  :label="item.label"
                  :value="item.value"
                ></el-option>
              </el-select>
            </el-form-item>
          </div>
        </el-form>
      </div>
      <div class="footer">
        <el-button type="primary" @click="confirmDistribution">
          {{ $t('device.device-select-allot.903153-14') }}
        </el-button>
      </div>
    </el-card>

    <product-list ref="productListRef" :product-id="queryParams.productId" @productEvent="getProductData($event)" />
  </div>
</template>

<script setup>
import { ref, reactive, computed, watch, onMounted, getCurrentInstance } from 'vue';
import { ElMessage } from 'element-plus';
import { ArrowLeft } from '@element-plus/icons-vue';
import { listUser } from '@/api/system/user';
import { listDevice, allotUserDevice } from '@/api/iot/device';
import productList from './product-list.vue';

const { proxy } = getCurrentInstance();

//响应式数据
const productListRef = ref();
const queryFormRef = ref();
const leftTableDataRef = ref();
const rightTableDataRef = ref();
const allotFormRef = ref();

const total = ref(0);
const loading = ref(false);
const productListData = ref([]);
const queryParams = reactive({
  productId: null,
  deviceName: '',
  pageNum: 1,
  pageSize: 10,
  productName: null,
  serialNumber: null,
  showChild: false,
});
const count = ref(0);
//导入表单
const allotForm = reactive({
  productId: 0,
  userId: null,
});
const deviceIds = ref({});
const selectedCount = ref(0);
// 用户列表
const userList = ref([]);
const selectedRow = ref(null);
const add = ref(true);
const del = ref(true);
const leftTableData = ref([]);
const rightTableData = ref([]);
const selectedListLeft = ref([]); //点击左边选中的设备
const selectedListRight = ref([]); //点击右边选中的设备
//设备列表数据
const menuTableData = ref([]);
//导入分配表单校验
const allotRules = {
  userId: [{ required: true, message: proxy?.$t('order.index.045965-26'), trigger: 'change' }],
};

//计算属性
//已选设备数量
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

//查询所有设备列表
const getList = () => {
  loading.value = true;
  listDevice(queryParams).then((response) => {
    menuTableData.value = response.rows;
    //isSelect用于判断是否可选
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
  rightTableData.value = [];
  queryParams.productId = null;
  proxy.resetForm('queryFormRef');
  handleQuery();
};

/**选择产品 */
const selectProduct = () => {
  productListRef.value.open = true;
  productListRef.value.getList();
};

/**获取选中的产品 */
const getProductData = (product) => {
  queryParams.productId = product.productId;
  queryParams.productName = product.productName;
};

/** 查询用户列表 */
const getUserList = () => {
  const deptId = proxy?.$store?.state?.user?.dept?.deptId;
  const params = {
    pageNum: 1,
    pageSize: 999,
    deptId,
  };
  listUser(params).then((res) => {
    if (res.code === 200) {
      userList.value = res.rows.map((item) => {
        return {
          label: item.userName,
          value: item.userId,
          userId: item.userId,
        };
      });
    }
  });
};

// 返回按钮
const goBack = () => {
  proxy.$router.go(-1);
};

//右边增加的数据
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

//右边
const changeCheckBoxValueRight = (val) => {
  selectedListRight.value = val;
};

//确定分配操作
const confirmDistribution = () => {
  allotFormRef.value.validate((valid) => {
    if (valid) {
      if (selectedListRight.value.length === 0) {
        ElMessage.warning(proxy.t('device.device-select-allot.903153-16'));
        return;
      }
      deviceIds.value = rightTableData.value.map((item) => item.deviceId);
      const deviceIdsStr = deviceIds.value.join(',');
      const userId = allotForm.userId;
      allotUserDevice(userId, deviceIdsStr).then((response) => {
        if (response.code == 200) {
          ElMessage.success(response.msg);
          resetQuery();
        } else {
          ElMessage.error(response.msg);
        }
      });
    }
  });
};

//挂时初始化
onMounted(() => {
  getList();
  getUserList();
});

//暴露给父组件使用
defineExpose({
  getList,
  handleQuery,
  resetQuery,
  selectProduct,
  getProductData,
  getUserList,
  goBack,
  rightAdd,
  leftDelete,
  checkSelectable,
  changeCheckBoxValueLeft,
  changeCheckBoxValueRight,
  confirmDistribution,
});
</script>

<style lang="scss" scoped>
.select-allot-wrap {
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

  .main-card {
    position: relative;
    padding-bottom: 60px;

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

        .transfer-btn {
          width: 36px;
          height: 28px;
          min-width: 36px;
          padding: 0;
          font-size: 16px;
          line-height: 1;
          display: inline-flex;
          align-items: center;
          justify-content: center;
        }

        .transfer-btn + .transfer-btn {
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

:deep(.table-empty-wrap) {
  height: 400px;
  display: flex;
  align-items: center;
  justify-content: center;
}
</style>
