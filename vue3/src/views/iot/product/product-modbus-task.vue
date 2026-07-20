<template>
  <div class="modbus-task">
    <el-form
      :model="queryParams"
      ref="queryFormRef"
      :inline="true"
      v-show="showSearch"
      label-width="70px"
      v-if="product.protocolCode !== 'MODBUS-TCP'"
    >
      <el-form-item prop="jobName">
        <el-input
          v-model="queryParams.jobName"
          :placeholder="$t('product.product-modbus-task.894593-1')"
          clearable
          @keyup.enter="handleQuery"
        />
      </el-form-item>
      <el-form-item prop="status">
        <el-select
          style="width: 192px"
          v-model="queryParams.status"
          :placeholder="$t('product.product-modbus-task.894593-3')"
          clearable
        >
          <el-option v-for="dict in sys_job_status" :key="dict.value" :label="dict.label" :value="dict.value" />
        </el-select>
      </el-form-item>
      <el-form-item>
        <el-button type="primary" :icon="Search" @click="handleQuery">
          {{ $t('product.product-modbus-task.894593-4') }}
        </el-button>
        <el-button :icon="Refresh" @click="resetQuery">{{ $t('product.product-modbus-task.894593-5') }}</el-button>
      </el-form-item>
    </el-form>

    <el-row :gutter="10" style="margin-bottom: 8px">
      <el-col :span="1.5" v-if="product.protocolCode !== 'MODBUS-TCP'">
        <el-button type="primary" plain :icon="Plus" @click="openEditDialog">
          {{ $t('device.device-modbus.433390-1') }}
        </el-button>
      </el-col>
      <div class="btn-group" v-else>
        <el-button
          type="primary"
          plain
          :icon="Edit"
          size="small"
          @click="setSlave"
          v-if="!enableSetSlave && product.status == 1"
          v-hasPermi="['productModbus:job:edit']"
        >
          {{ $t('product.product-modbus.562372-2') }}
        </el-button>
        <el-button type="primary" plain :icon="Close" size="small" @click="saveSlave" v-if="enableSetSlave">
          {{ $t('product.product-modbus.562372-3') }}
        </el-button>
        <el-button type="info" plain :icon="Edit" size="small" @click="cancelSlave" v-if="enableSetSlave">
          {{ $t('product.product-modbus.562372-4') }}
        </el-button>
      </div>
      <right-toolbar v-model:showSearch="showSearch" @queryTable="getList"></right-toolbar>
    </el-row>

    <el-table
      v-loading="loading"
      :data="commandList"
      :border="false"
      v-if="(product.deviceType === 1 || product.deviceType === 2) && product.protocolCode === 'MODBUS-TCP'"
    >
      <el-table-column :label="$t('product.product-modbus-task.894593-71')" align="center" prop="address" width="200px">
        <template #default="scope">
          <el-input-number
            style="width: 100%"
            :disabled="!enableSetSlave"
            v-model="scope.row.address"
            :min="0"
            :max="400000"
          ></el-input-number>
        </template>
      </el-table-column>
      <el-table-column
        :label="$t('product.product-modbus-task.894593-72')"
        align="center"
        prop="register"
        width="200px"
      >
        <template #default="scope">
          <el-input-number
            style="width: 100%"
            :disabled="!enableSetSlave"
            v-model="scope.row.register"
            :min="0"
            :max="400000"
          ></el-input-number>
        </template>
      </el-table-column>
      <el-table-column :label="$t('product.product-modbus-task.894593-73')" align="center" prop="code" width="260px">
        <template #default="scope">
          <el-select v-model="scope.row.code" style="width: 100%" :disabled="!enableSetSlave">
            <el-option
              v-for="dict in product_command_function_code"
              :key="dict.value"
              :label="dict.label"
              :value="dict.value"
              :disabled="
                ['05', '06', '15', '16'].includes(dict.value) &&
                product.protocolCode !== 'MODBUS-TCP-OVER-RTU' &&
                product.protocolCode !== 'MODBUS-RTU'
              "
            />
          </el-select>
        </template>
      </el-table-column>
      <el-table-column
        :label="$t('product.product-modbus-task.894593-74')"
        align="center"
        prop="quantity"
        width="200px"
      >
        <template #default="scope">
          <el-input-number
            style="width: 100%"
            :disabled="!enableSetSlave"
            v-model="scope.row.quantity"
            :min="0"
          ></el-input-number>
        </template>
      </el-table-column>
      <el-table-column
        :label="$t('product.product-modbus.562372-34')"
        align="center"
        class-name="small-padding fixed-width"
        fixed="right"
      >
        <template #default="scope">
          <el-button
            size="small"
            type="primary"
            link
            :icon="Delete"
            v-if="enableSetSlave"
            @click="handleDelete(scope.row, scope.$index, commandList)"
            v-hasPermi="['productModbus:job:remove']"
          >
            {{ $t('product.product-modbus.562372-35') }}
          </el-button>
        </template>
      </el-table-column>
    </el-table>
    <!-- 非直连设备和网关设备 -->
    <el-table v-loading="loading" :data="jobList" :border="false" v-else>
      <el-table-column
        :label="$t('product.product-modbus-task.894593-56')"
        align="left"
        prop="taskId"
        min-width="100"
      />
      <el-table-column
        :label="$t('product.product-modbus-task.894593-7')"
        align="left"
        prop="jobName"
        min-width="180"
      />
      <el-table-column
        :label="$t('product.product-modbus-task.894593-57')"
        align="left"
        prop="command"
        min-width="160"
      />
      <el-table-column
        :label="$t('product.product-modbus-task.894593-58')"
        align="center"
        prop="status"
        min-width="100"
      >
        <template #default="scope">
          <el-switch
            v-model="scope.row.status"
            :active-value="0"
            :inactive-value="1"
            @change="handleStatusChange(scope.row)"
          ></el-switch>
        </template>
      </el-table-column>
      <el-table-column
        :label="$t('product.product-modbus-task.894593-59')"
        align="center"
        prop="remarkStr"
        min-width="110"
      ></el-table-column>
      <el-table-column :label="$t('product.product-modbus-task.894593-60')" align="center" fixed="right" width="160">
        <template #default="scope">
          <el-button
            type="primary"
            link
            :icon="Edit"
            @click="handleEditRow(scope.row)"
            v-hasPermi="['productModbus:job:edit']"
          >
            {{ $t('edit') }}
          </el-button>
          <el-button
            type="primary"
            link
            :icon="Delete"
            @click="handleDelete(scope.row)"
            v-hasPermi="['productModbus:job:remove']"
          >
            {{ $t('product.product-modbus-task.894593-61') }}
          </el-button>
        </template>
      </el-table-column>
    </el-table>

    <pagination
      v-if="product.protocolCode !== 'MODBUS-TCP'"
      v-show="total > 0"
      :total="total"
      v-model:page="queryParams.pageNum"
      v-model:limit="queryParams.pageSize"
      @pagination="getList"
    />
    <el-row :gutter="10" class="mb8" style="margin-top: 20px">
      <el-col :span="1.5">
        <el-button type="primary" plain :icon="Plus" size="small" @click="handleAddData" v-if="enableSetSlave">
          {{ $t('add') }}
        </el-button>
      </el-col>
    </el-row>

    <el-dialog
      :title="editName ? $t('edit') : $t('product.product-modbus-task.894593-13')"
      v-model="editDialog"
      :width="editName ? '800' : '900'"
    >
      <div class="dialog-content">
        <el-form :model="createForm" label-position="top">
          <el-form-item :label="$t('product.product-modbus-task.894593-0')" prop="jobName">
            <el-input
              v-model="createForm.jobName"
              :placeholder="$t('product.product-modbus-task.894593-1')"
              class="input-item"
            />
          </el-form-item>
          <el-row :gutter="40">
            <!-- 从机地址 -->
            <el-col :span="12">
              <el-form-item :label="$t('device.device-modbus-task.384302-14')" prop="address" v-if="!selectPath">
                <el-input v-model="createForm.address" class="input-item" type="number"></el-input>
              </el-form-item>
              <el-form-item :label="$t('device.device-modbus-task.384302-14')" prop="address" v-else>
                <el-select v-model="createForm.address" class="input-item">
                  <el-option
                    v-for="address in addressList"
                    :key="address"
                    :label="address"
                    :value="address"
                  ></el-option>
                </el-select>
              </el-form-item>
            </el-col>
            <!-- 功能码 -->
            <el-col :span="12">
              <el-form-item :label="$t('product.product-modbus-task.894593-15')" prop="code">
                <el-select v-model="createForm.code" @change="changeNum" class="input-item">
                  <el-option
                    v-for="dict in product_command_function_code"
                    :key="dict.value"
                    :label="dict.label"
                    :value="dict.value"
                    :disabled="
                      ['05', '06', '15', '16'].includes(dict.value) &&
                      product.protocolCode !== 'MODBUS-TCP-OVER-RTU' &&
                      product.protocolCode !== 'MODBUS-RTU'
                    "
                  />
                </el-select>
              </el-form-item>
            </el-col>
            <!-- 起始寄存器地址 -->
            <el-col :span="12">
              <el-form-item prop="startPath">
                <template #label>
                  <div class="form-item-label">
                    <div style="margin-right: auto">{{ $t('product.product-modbus-task.894593-16') }}</div>
                    <el-tooltip :content="createForm.startPathSwitch" placement="top">
                      <el-switch
                        style="margin-left: 10px"
                        v-model="createForm.startPathSwitch"
                        size="small"
                        active-value="Dec"
                        inactive-value="Hex"
                      />
                    </el-tooltip>
                  </div>
                </template>
                <el-input
                  v-model="createForm.startPath"
                  type="number"
                  class="input-item"
                  v-show="createForm.startPathSwitch == 'Dec'"
                  :min="0"
                  @change="
                    () => {
                      createForm.startPath16 = int2hex(createForm.startPath);
                    }
                  "
                  @input="
                    () => {
                      createForm.startPath16 = int2hex(createForm.startPath);
                    }
                  "
                >
                  <template #append>0x{{ createForm.startPath16 }}</template>
                </el-input>
                <el-input
                  class="input-item"
                  v-model="createForm.startPath16"
                  v-show="createForm.startPathSwitch != 'Dec'"
                  @input="
                    () => {
                      createForm.startPath = hex2int(createForm.startPath16);
                    }
                  "
                >
                  <template #append>{{ createForm.startPath }}</template>
                </el-input>
              </el-form-item>
            </el-col>
            <!-- 个数或写值 -->
            <el-col :span="12">
              <el-form-item
                :label="registerNumTitle"
                prop="registerNum"
                v-show="!['05', '06'].includes(createForm.code)"
              >
                <el-input-number
                  v-model="createForm.registerNum"
                  controls-position="right"
                  :min="0"
                  class="input-item"
                  @change="changeNum"
                />
              </el-form-item>
              <el-form-item prop="setValue" v-show="['05', '06'].includes(createForm.code)">
                <template #label>
                  <div class="form-item-label">
                    <div style="margin-right: auto">{{ registerNumTitle }}</div>
                    <el-tooltip :content="createForm.setValueSwitch" placement="top">
                      <el-switch
                        v-model="createForm.setValueSwitch"
                        size="small"
                        active-color="#13ce66"
                        inactive-color="#ff4949"
                        active-value="Dec"
                        inactive-value="Hex"
                      />
                    </el-tooltip>
                  </div>
                </template>
                <el-input
                  v-model="createForm.setValue"
                  type="number"
                  v-show="createForm.setValueSwitch == 'Dec'"
                  @change="
                    () => {
                      createForm.setValue16 = int2hex(createForm.setValue);
                    }
                  "
                  @input="
                    () => {
                      createForm.setValue16 = int2hex(createForm.setValue);
                    }
                  "
                >
                  <template #append>0x{{ createForm.setValue16 }}</template>
                </el-input>
                <el-input
                  v-model="createForm.setValue16"
                  v-show="createForm.setValueSwitch != 'Dec'"
                  @input="
                    () => {
                      createForm.setValue = hex2int(createForm.setValue16);
                    }
                  "
                >
                  <template #append>{{ createForm.setValue }}</template>
                </el-input>
              </el-form-item>
            </el-col>
            <!-- 批量写寄存器值 -->
            <el-col
              :span="12"
              v-for="(item, index) in registerValList"
              :key="'register' + index"
              v-show="createForm.code == '16'"
            >
              <el-form-item prop="registerValList">
                <template #label>
                  <div class="form-item-label">
                    <div style="margin-right: auto">#{{ index }} {{ $t('product.product-modbus-task.894593-17') }}</div>
                    <el-tooltip :content="item.switch" placement="top">
                      <el-switch
                        v-model="item.switch"
                        size="small"
                        active-color="#13ce66"
                        @change="
                          () => {
                            refreshRegisterInputs(item, index);
                          }
                        "
                        inactive-color="#ff4949"
                        active-value="Dec"
                        inactive-value="Hex"
                      />
                    </el-tooltip>
                  </div>
                </template>
                <el-input
                  v-model="item.value"
                  type="number"
                  v-show="item.switch == 'Dec'"
                  :min="0"
                  @change="
                    () => {
                      item.value16 = int2hex(item.value);
                      refreshRegisterInputs(item, index);
                    }
                  "
                  @input="
                    () => {
                      item.value16 = int2hex(item.value);
                      refreshRegisterInputs(item, index);
                    }
                  "
                >
                  <template #append>0x{{ item.value16 }}</template>
                </el-input>
                <el-input
                  v-model="item.value16"
                  v-show="item.switch != 'Dec'"
                  @input="
                    () => {
                      item.value = hex2int(item.value16);
                      refreshRegisterInputs(item, index);
                    }
                  "
                >
                  <template #append>{{ item.value }}</template>
                </el-input>
              </el-form-item>
            </el-col>
            <!-- 批量写线圈值 -->
            <el-col :span="6" v-for="(item, index) in IOValList" :key="'IO' + index" v-show="createForm.code == '15'">
              <el-form-item prop="registerValList">
                <template #label>
                  <div class="form-item-label">
                    <div style="margin-right: auto">#{{ index }} {{ $t('product.product-modbus-task.894593-18') }}</div>
                  </div>
                </template>
                <el-switch
                  v-model="item.value"
                  active-value="1"
                  inactive-value="0"
                  @change="
                    () => {
                      refreshIOInputs(item, index);
                    }
                  "
                />
              </el-form-item>
            </el-col>
          </el-row>
          <!-- 任务状态 -->
          <el-form-item :label="$t('device.device-timer.433369-2')" prop="status">
            <el-radio-group v-model="createForm.status">
              <el-radio v-for="dict in sys_job_status" :key="dict.value" :value="Number(dict.value)">
                {{ dict.label }}
              </el-radio>
            </el-radio-group>
          </el-form-item>
          <!-- 时间周期 -->
          <el-form-item :label="$t('product.product-modbus-task.894593-19')" prop="cycleType">
            <div class="timer-wrap">
              <el-radio-group v-model="createForm.cycleType" @change="handleCycleTypeInput">
                <el-radio :value="1" style="display: block">
                  {{ $t('product.product-modbus-task.894593-20') }}
                  <el-tooltip placement="right">
                    <template #content>
                      {{ $t('product.product-modbus-task.894593-21') }}
                      <br />
                      {{ $t('product.product-modbus-task.894593-22') }}
                    </template>
                    <el-icon style="color: #909399"><QuestionFilled /></el-icon>
                  </el-tooltip>
                  <div class="timer-period">
                    <span>{{ $t('product.product-modbus-task.894593-23') }}</span>
                    <el-select
                      style="width: 100px; margin-left: 10px"
                      v-model="cycles1[0].interval"
                      size="small"
                      :disabled="createForm.cycleType === 2"
                      @change="handleCycleInterval"
                    >
                      <el-option
                        v-for="dict in variable_operation_interval"
                        :key="dict.value"
                        :label="dict.label"
                        :value="dict.value"
                      />
                    </el-select>
                    <el-select
                      v-if="cycles1[0].interval === 'week'"
                      style="width: 100px; margin-left: 5px"
                      v-model="cycles1[0].week"
                      size="small"
                      :disabled="createForm.cycleType === 2"
                    >
                      <el-option
                        v-for="dict in variable_operation_week"
                        :key="dict.value"
                        :label="dict.label"
                        :value="dict.value"
                      />
                    </el-select>
                    <el-select
                      v-if="cycles1[0].interval === 'month'"
                      style="width: 100px; margin-left: 5px"
                      v-model="cycles1[0].day"
                      size="small"
                      :disabled="createForm.cycleType === 2"
                    >
                      <el-option
                        v-for="dict in variable_operation_day"
                        :key="dict.value"
                        :label="dict.label"
                        :value="dict.value"
                      />
                    </el-select>
                    <el-select
                      v-if="['day', 'week', 'month'].includes(cycles1[0].interval)"
                      style="width: 100px; margin-left: 5px"
                      v-model="cycles1[0].time"
                      size="small"
                      :disabled="createForm.cycleType === 2"
                    >
                      <el-option
                        v-for="dict in variable_operation_time"
                        :key="dict.value"
                        :label="dict.label"
                        :value="dict.value"
                      />
                    </el-select>
                    <span style="margin-left: 10px">{{ $t('product.product-modbus-task.894593-24') }}</span>
                  </div>
                </el-radio>
                <el-radio :value="2" style="display: block">
                  {{ $t('scene.edit.202832-40') }}
                  <el-tooltip placement="right">
                    <template #content>
                      {{ $t('scene.edit.202832-41') }}
                      <br />
                      {{ $t('scene.edit.202832-42') }}
                    </template>
                    <el-icon style="color: #909399"><QuestionFilled /></el-icon>
                  </el-tooltip>
                </el-radio>
                <div>
                  <el-button
                    size="small"
                    type="primary"
                    link
                    :icon="CirclePlus"
                    :disabled="createForm.cycleType === 1"
                    @click="handleCustomIntervalAdd"
                  >
                    {{ $t('scene.edit.202832-52') }}
                  </el-button>
                </div>
                <div class="timer-custom" v-for="(item, index) in cycles2" :key="'c2-' + index">
                  <span>{{ $t('product.product-modbus-task.894593-23') }}</span>
                  <el-select
                    style="width: 100px; margin-left: 10px"
                    v-model="cycles2[index].type"
                    size="small"
                    :disabled="createForm.cycleType === 1"
                    @change="handleCustomInterval(index, $event)"
                  >
                    <el-option :label="$t('scene.edit.202832-43')" value="day" />
                    <el-option :label="$t('scene.edit.202832-44')" value="week" />
                    <el-option :label="$t('scene.edit.202832-45')" value="month" />
                  </el-select>
                  <el-select
                    v-if="cycles2[index].type === 'week'"
                    style="width: 100px; margin-left: 5px"
                    v-model="cycles2[index].week"
                    size="small"
                    :disabled="createForm.cycleType === 1"
                  >
                    <el-option
                      v-for="dict in variable_operation_week"
                      :key="dict.value"
                      :label="dict.label"
                      :value="dict.value"
                    />
                  </el-select>
                  <el-select
                    v-if="cycles2[index].type === 'month'"
                    style="width: 100px; margin-left: 5px"
                    v-model="cycles2[index].day"
                    size="small"
                    :disabled="createForm.cycleType === 1"
                  >
                    <el-option
                      v-for="dict in variable_operation_day"
                      :key="dict.value"
                      :label="dict.label"
                      :value="dict.value"
                    />
                  </el-select>
                  <el-select
                    v-if="
                      cycles2[index].type === 'day' || cycles2[index].type === 'week' || cycles2[index].type === 'month'
                    "
                    style="width: 100px; margin-left: 5px"
                    v-model="cycles2[index].time"
                    size="small"
                    :disabled="createForm.cycleType === 1"
                  >
                    <el-option
                      v-for="dict in variable_operation_time"
                      :key="dict.value"
                      :label="dict.label"
                      :value="dict.value"
                    />
                  </el-select>
                  <span style="margin-left: 5px">{{ $t('scene.edit.202832-46') }}</span>
                  <el-select
                    v-if="cycles2[index].type === 'day'"
                    style="width: 100px; margin-left: 5px"
                    v-model="cycles2[index].toType"
                    size="small"
                    :disabled="createForm.cycleType === 1"
                  >
                    <el-option :label="$t('scene.edit.202832-47')" value="1" />
                    <el-option :label="$t('scene.edit.202832-48')" value="2" />
                  </el-select>
                  <el-select
                    v-if="cycles2[index].type === 'week'"
                    style="width: 100px; margin-left: 5px"
                    v-model="cycles2[index].toType"
                    size="small"
                    :disabled="createForm.cycleType === 1"
                  >
                    <el-option :label="$t('scene.edit.202832-49')" value="3" />
                  </el-select>
                  <el-select
                    v-if="cycles2[index].type === 'month'"
                    style="width: 100px; margin-left: 5px"
                    v-model="cycles2[index].toType"
                    size="small"
                    :disabled="createForm.cycleType === 1"
                  >
                    <el-option :label="$t('scene.edit.202832-50')" value="4" />
                  </el-select>
                  <el-select
                    v-if="cycles2[index].type === 'week'"
                    style="width: 100px; margin-left: 5px"
                    v-model="cycles2[index].toWeek"
                    size="small"
                    :disabled="createForm.cycleType === 1"
                  >
                    <el-option
                      v-for="dict in variable_operation_week"
                      :key="dict.value"
                      :label="dict.label"
                      :value="dict.value"
                    />
                  </el-select>
                  <el-select
                    v-if="cycles2[index].type === 'month'"
                    style="width: 100px; margin-left: 5px"
                    v-model="cycles2[index].toDay"
                    size="small"
                    :disabled="createForm.cycleType === 1"
                  >
                    <el-option
                      v-for="dict in variable_operation_day"
                      :key="dict.value"
                      :label="dict.label"
                      :value="dict.value"
                    />
                  </el-select>
                  <el-select
                    v-if="
                      cycles2[index].type === 'day' || cycles2[index].type === 'week' || cycles2[index].type === 'month'
                    "
                    style="width: 100px; margin-left: 5px"
                    v-model="cycles2[index].toTime"
                    size="small"
                    :disabled="createForm.cycleType === 1"
                  >
                    <el-option
                      v-for="dict in variable_operation_time"
                      :key="dict.value"
                      :label="dict.label"
                      :value="dict.value"
                    />
                  </el-select>
                  <span style="margin-left: 10px">{{ $t('scene.edit.202832-51') }}</span>
                  <el-button
                    style="color: #f56c6c; margin-left: 15px"
                    size="small"
                    type="danger"
                    link
                    :disabled="createForm.cycleType === 1"
                    @click="handleCustomIntervalDelete(index)"
                  >
                    {{ $t('del') }}
                  </el-button>
                </div>
              </el-radio-group>
            </div>
          </el-form-item>
        </el-form>
        <div v-loading="createLoading">
          <div class="create-title">
            <el-button type="primary" link @click.stop="encodeCmd">
              {{ $t('product.product-modbus-task.894593-25') }}
            </el-button>
            <div class="title-right">
              <el-button type="primary" plain size="small" @click="copyTextFn(createCode)">
                {{ $t('product.product-modbus-task.894593-26') }}
              </el-button>
            </div>
          </div>
          <div class="create-code">{{ createCode }}</div>
        </div>
      </div>
      <template #footer>
        <div class="dialog-btn">
          <el-button @click="editDialog = false">{{ $t('product.product-modbus-task.894593-27') }}</el-button>
          <el-button type="primary" @click="handleAdd" v-hasPermi="['productModbus:job:add']">
            {{ $t('confirm') }}
          </el-button>
        </div>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, watch, onMounted, computed, getCurrentInstance } from 'vue';
import { Search, Refresh, Plus, Edit, Close, Delete, QuestionFilled, CirclePlus } from '@element-plus/icons-vue';
import { hex2int as hex2intFn, int2hex as int2hexFn, copyText } from '@/utils/common';
import { encode } from '@/api/iot/mqttTest';
import {
  listProductJob,
  delProductJob,
  addProductJob,
  updateProductJob,
  getProductJob,
  getAddress as getAddressApi,
} from '@/api/iot/modbusJob';
import { useDict } from '@/utils/dict';

const {
  sys_job_status,
  variable_operation_interval,
  variable_operation_time,
  variable_operation_week,
  variable_operation_day,
  product_command_function_code,
} = useDict(
  'sys_job_status',
  'variable_operation_interval',
  'variable_operation_time',
  'variable_operation_week',
  'variable_operation_day',
  'product_command_function_code'
);

const { proxy } = getCurrentInstance() as any;

const props = defineProps({
  product: { type: Object, default: null },
});

const queryFormRef = ref();
const loading = ref(false);
const editDialog = ref(false);
const addressList = ref<any[]>([]);
const total = ref(0);
const productInfo = ref<any>({});
const enableSetSlave = ref(false);
const jobList = ref<any[]>([]);
const commandList = ref<any[]>([]);
const showSearch = ref(true);
const createCode = ref('');
const registerValList = ref<any[]>([]);
const IOValList = ref<any[]>([]);
const editName = ref(false);
const createLoading = ref(false);
const delDataIds = ref<any[]>([]);
const selectPath = ref(false);
const cycles1 = ref([{ interval: '300', time: '', week: '', day: '' }]);
const cycles2 = ref([{ type: 'day', time: '00', week: '', day: '', toType: '1', toTime: '02', toWeek: '', toDay: '' }]);
const slaveTaskId = ref<any>(null);

const queryParams = reactive<any>({
  pageNum: 1,
  pageSize: 10,
  productId: null,
  command: null,
  taskId: null,
  status: null,
  commandType: 1,
});
const createForm = reactive<any>({
  cycleType: 1,
  status: 0,
  register: 0,
  code: '03',
  address: 1,
  quantity: 1,
  commandType: 1,
  startPath: 0,
  startPath16: '0000',
  registerNum: 1,
  startPathSwitch: 'Dec',
  setValue: 0,
  setValue16: '0000',
  setValueSwitch: 'Dec',
  jobName: '',
  jobId: undefined as any,
  taskId: null as any,
  productId: null,
  command: '',
  remark: '',
});

const registerNumTitle = computed(() => {
  switch (createForm.code) {
    case '01':
    case '02':
    case '15':
      return proxy?.$t('product.product-modbus-task.894593-29');
    case '03':
    case '04':
    case '16':
      return proxy?.$t('product.product-modbus-task.894593-30');
    case '05':
      return proxy?.$t('product.product-modbus-task.894593-31');
    case '06':
      return proxy?.$t('product.product-modbus-task.894593-32');
    default:
      return '';
  }
});

watch(
  () => props.product,
  (newVal, oldVal) => {
    if (newVal?.productId && newVal.productId !== oldVal?.productId) {
      queryParams.productId = newVal.productId;
      queryParams.commandType = 1;
      productInfo.value = newVal;
      getList();
    }
  }
);

onMounted(() => {
  const { productId } = props.product || {};
  productInfo.value = props.product;
  if (productId) {
    queryParams.productId = productId;
    queryParams.commandType = 1;
    getList();
  }
  resetCreateForm();
});

function getList() {
  loading.value = true;
  queryParams.commandType = 1;
  listProductJob(queryParams).then((response: any) => {
    jobList.value = response.rows;
    if (response.rows.length > 0) {
      const first = response.rows[0];
      const cmd = first.command && String(first.command);
      if (cmd && cmd.includes('{"register":')) {
        try {
          commandList.value = JSON.parse(first.command);
        } catch {
          commandList.value = [];
        }
      } else {
        commandList.value = [];
      }
      slaveTaskId.value = first.taskId ?? null;
    } else {
      slaveTaskId.value = null;
      commandList.value = [];
    }
    total.value = response.total;
    loading.value = false;
  });
}

function getAddress(isEdit = false) {
  getAddressApi(props.product.productId, '', props.product.deviceType).then((response: any) => {
    if (!response.data || response.data.length === 0) {
      selectPath.value = false;
      if (!isEdit) {
        createForm.address = '1';
      }
    } else {
      selectPath.value = true;
      addressList.value = response.data;
    }
    editDialog.value = true;
  });
}

function taskIdPresent() {
  const t = createForm.taskId;
  if (t === null || t === undefined) return false;
  if (typeof t === 'string') return t !== '';
  return true;
}

function submitForm() {
  createForm.commandType = 1;
  createForm.productId = props.product.productId;
  const isUpdate = editName.value && taskIdPresent();
  const req = isUpdate ? updateProductJob(createForm) : addProductJob(createForm);
  req.then(() => {
    editName.value = false;
    proxy?.$modal.msgSuccess(
      isUpdate ? proxy?.$t('product.product-modbus-task.894593-62') : proxy?.$t('product.product-modbus-task.894593-63')
    );
    getList();
  });
}

function setSlave() {
  enableSetSlave.value = !enableSetSlave.value;
}

function saveSlave() {
  const data: any = {
    taskId: slaveTaskId.value,
    productId: props.product.productId,
    command: JSON.stringify(commandList.value),
    status: 0,
    commandType: 1,
  };
  const hasSlaveTask = slaveTaskId.value != null && slaveTaskId.value !== '';
  const req = hasSlaveTask ? updateProductJob(data) : addProductJob(data);
  req.then(() => {
    proxy?.$modal.msgSuccess(
      hasSlaveTask
        ? proxy?.$t('product.product-modbus-task.894593-62')
        : proxy?.$t('product.product-modbus-task.894593-63')
    );
    setSlave();
    getList();
  });
}

function cancelSlave() {
  enableSetSlave.value = !enableSetSlave.value;
}

function handleAddData() {
  commandList.value.push({ register: 0, address: 1, code: '03', quantity: 1 });
}

function handleDelete(row: any, index?: number, list?: any[]) {
  if (props.product.protocolCode !== 'MODBUS-TCP') {
    const taskIds = row.taskId;
    proxy?.$modal
      .confirm(proxy?.$t('product.product-modbus-task.894593-64', [taskIds]))
      .then(() => delProductJob(taskIds))
      .then(() => {
        getList();
        proxy?.$modal.msgSuccess(proxy?.$t('product.product-modbus-task.894593-65'));
      })
      .catch(() => {});
  } else if (list && index !== undefined) {
    list.splice(index, 1);
    delDataIds.value.push(row.id);
  }
}

function handleAdd() {
  const params: any = {
    address: parseInt(createForm.address),
    register: createForm.startPath,
    code: parseInt(createForm.code),
    protocolCode: props.product.protocolCode,
    serialNumber: props.product.serialNumber,
  };
  switch (createForm.code) {
    case '01':
    case '02':
    case '03':
    case '04':
      params.count = createForm.registerNum;
      break;
    case '05':
    case '06':
      params.writeData = createForm.setValue;
      break;
    case '15':
      params.count = createForm.registerNum;
      params.bitString = IOValList.value.map((i: any) => i.value).join('');
      break;
    case '16':
      params.count = createForm.registerNum;
      params.tenWriteData = registerValList.value.map((i: any) => i.value);
      break;
  }
  encode(params).then((response: any) => {
    createCode.value = response.msg;
    handlePush();
  });
}

function buildRemarkFromCycles() {
  if (createForm.cycleType === 1) {
    const c = cycles1.value.map((item: any) => {
      if (item.interval === 'hour') return { type: 'hour' };
      if (item.interval === 'day') return { type: 'day', time: item.time };
      if (item.interval === 'week') return { type: 'week', week: item.week, time: item.time };
      if (item.interval === 'month') return { type: 'month', day: item.day, time: item.time };
      return { interval: item.interval };
    });
    return JSON.stringify(c);
  }
  const c = cycles2.value.map((item: any) => {
    if (item.type === 'day') return { type: 'day', time: item.time, toType: item.toType, toTime: item.toTime };
    if (item.type === 'week')
      return {
        type: 'week',
        week: item.week,
        time: item.time,
        toType: item.toType,
        toWeek: item.toWeek,
        toTime: item.toTime,
      };
    if (item.type === 'month')
      return {
        type: 'month',
        day: item.day,
        time: item.time,
        toType: item.toType,
        toDay: item.toDay,
        toTime: item.toTime,
      };
    return { type: 'day', time: item.time, toType: item.toType, toTime: item.toTime };
  });
  return JSON.stringify(c);
}

function applyRemarkToCycles(remarkStr: string) {
  cycles1.value = [{ interval: '300', time: '', week: '', day: '' }];
  cycles2.value = [{ type: 'day', time: '00', week: '', day: '', toType: '1', toTime: '02', toWeek: '', toDay: '' }];
  if (!remarkStr) {
    createForm.cycleType = 1;
    return;
  }
  try {
    const arr = JSON.parse(remarkStr);
    if (!Array.isArray(arr) || arr.length === 0) {
      createForm.cycleType = 1;
      return;
    }
    const isCustom = arr.some((x: any) => x && (x.toType != null || x.toWeek != null || x.toDay != null));
    if (isCustom) {
      createForm.cycleType = 2;
      cycles2.value = arr.map((item: any) => {
        if (item.type === 'week')
          return {
            type: 'week',
            time: item.time ?? '00',
            week: item.week ?? '1',
            day: '',
            toType: item.toType ?? '3',
            toTime: item.toTime ?? '02',
            toWeek: item.toWeek ?? '2',
            toDay: '',
          };
        if (item.type === 'month')
          return {
            type: 'month',
            time: item.time ?? '00',
            week: '',
            day: item.day ?? '1',
            toType: item.toType ?? '4',
            toTime: item.toTime ?? '02',
            toWeek: '',
            toDay: item.toDay ?? '2',
          };
        return {
          type: 'day',
          time: item.time ?? '00',
          week: '',
          day: '',
          toType: item.toType ?? '1',
          toTime: item.toTime ?? '02',
          toWeek: '',
          toDay: '',
        };
      });
    } else {
      createForm.cycleType = 1;
      cycles1.value = arr.map((item: any) => {
        if (item.type === 'hour') return { interval: 'hour', time: '', week: '', day: '' };
        if (item.type === 'day') return { interval: 'day', time: item.time || '01', week: '', day: '' };
        if (item.type === 'week') return { interval: 'week', time: item.time || '01', week: item.week || '1', day: '' };
        if (item.type === 'month')
          return { interval: 'month', time: item.time || '01', week: '', day: item.day || '1' };
        if (item.interval != null)
          return {
            interval: String(item.interval),
            time: item.time || '',
            week: item.week || '',
            day: item.day || '',
          };
        return { interval: '300', time: '', week: '', day: '' };
      });
    }
  } catch {
    createForm.cycleType = 1;
  }
}

function handlePush() {
  createForm.productId = props.product.productId;
  createForm.command = createCode.value;
  createForm.remark = buildRemarkFromCycles();
  submitForm();
  editDialog.value = false;
}

function openEditDialog() {
  resetCreateForm();
  editName.value = false;
  getAddress();
}

function padCode(c: string | number | undefined) {
  if (c === undefined || c === null || c === '') return '01';
  const s = String(c);
  return s.length >= 2 ? s : s.padStart(2, '0');
}

async function handleEditRow(row: any) {
  resetCreateForm();
  editName.value = true;
  try {
    const res: any = await getProductJob(row.taskId);
    const d = res.data ?? res;
    Object.assign(createForm, d);
    createForm.taskId = d.taskId ?? row.taskId;
    createForm.jobId = d.jobId ?? row.jobId;
    createForm.jobName = d.jobName ?? '';
    createForm.status = d.status ?? 0;
    createForm.cycleType = d.cycleType ?? 1;
    if (d.code != null) createForm.code = padCode(d.code);
    if (d.register != null) {
      createForm.startPath = Number(d.register);
      createForm.startPath16 = int2hex(d.register);
    }
    if (d.address != null) createForm.address = String(d.address);
    if (d.count != null) createForm.registerNum = Number(d.count);
    if (d.writeData != null) {
      createForm.setValue = Number(d.writeData);
      createForm.setValue16 = int2hex(d.writeData);
    }
    createCode.value = d.command || '';
    applyRemarkToCycles(d.remark || '');
    changeNum();
    getAddress(true);
  } catch (e: any) {
    proxy?.$modal.msgError(e?.message || proxy?.$t('fail'));
    editName.value = false;
  }
}

function resetCreateForm() {
  Object.assign(createForm, {
    address: '1',
    code: '01',
    startPath: 0,
    startPath16: '0000',
    registerNum: 1,
    startPathSwitch: 'Dec',
    setValue: 0,
    setValue16: '0000',
    setValueSwitch: 'Dec',
    status: 0,
    cycleType: 1,
    jobName: '',
    taskId: null,
    jobId: undefined,
    command: '',
    remark: '',
  });
  createCode.value = '';
  registerValList.value = [];
  IOValList.value = [];
  cycles1.value = [{ interval: '300', time: '', week: '', day: '' }];
  cycles2.value = [{ type: 'day', time: '00', week: '', day: '', toType: '1', toTime: '02', toWeek: '', toDay: '' }];
}

function int2hex(str: any) {
  return int2hexFn(str);
}
function hex2int(str: any) {
  return hex2intFn(str);
}

function changeNum() {
  if (createForm.code == '16') {
    for (let i = 0; i < createForm.registerNum; i++) {
      if (!registerValList.value[i]) registerValList.value[i] = { value: 0, value16: '0000', switch: 'Dec' };
    }
    if (registerValList.value.length > createForm.registerNum) registerValList.value.splice(createForm.registerNum);
  }
  if (createForm.code == '15') {
    for (let i = 0; i < createForm.registerNum; i++) {
      if (!IOValList.value[i]) IOValList.value[i] = { value: '0' };
    }
    if (IOValList.value.length > createForm.registerNum) IOValList.value.splice(createForm.registerNum);
  }
}

function refreshRegisterInputs(item: any, index: number) {
  registerValList.value[index] = { ...item };
}
function refreshIOInputs(item: any, index: number) {
  IOValList.value[index] = { ...item };
}

function copyTextFn(code: string) {
  const res = copyText(code);
  if (res.type === 'success') proxy?.$modal.msgSuccess(res.message);
  else proxy?.$modal.msgError(res.message);
}

async function encodeCmd() {
  try {
    createLoading.value = true;
    const params: any = {
      address: parseInt(createForm.address),
      register: createForm.startPath,
      code: parseInt(createForm.code),
      protocolCode: props.product.protocolCode,
      serialNumber: props.product.serialNumber,
    };
    switch (createForm.code) {
      case '01':
      case '02':
      case '03':
      case '04':
        params.count = createForm.registerNum;
        break;
      case '05':
      case '06':
        params.writeData = createForm.setValue;
        break;
      case '15':
        params.count = createForm.registerNum;
        params.bitString = IOValList.value.map((i: any) => i.value).join('');
        break;
      case '16':
        params.count = createForm.registerNum;
        params.tenWriteData = registerValList.value.map((i: any) => i.value);
        break;
    }
    const res = await encode(params);
    createCode.value = res.msg;
  } catch (err: any) {
    proxy?.$modal.msgError(err.message || proxy?.$t('product.product-modbus-task.894593-41'));
  } finally {
    createLoading.value = false;
  }
}

function handleQuery() {
  queryParams.pageNum = 1;
  getList();
}
function resetQuery() {
  proxy?.resetForm(queryFormRef.value);
  handleQuery();
}

function handleStatusChange(row: any) {
  const text =
    row.status === 0
      ? proxy?.$t('product.product-modbus-task.894593-42')
      : proxy?.$t('product.product-modbus-task.894593-43');
  proxy?.$modal
    .confirm(proxy?.$t('product.product-modbus-task.894593-44', [text + '""' + row.jobName]))
    .then(() => updateProductJob({ taskId: row.taskId, status: row.status }))
    .then(() => {
      proxy?.$modal.msgSuccess(text + proxy?.$t('product.product-modbus-task.894593-45'));
    })
    .catch(() => {
      row.status = row.status === 0 ? 1 : 0;
    });
}

function handleCycleTypeInput(val: number | string) {
  const n = Number(val);
  if (n === 1) {
    cycles2.value = [{ type: 'day', time: '00', week: '', day: '', toType: '1', toTime: '02', toWeek: '', toDay: '' }];
  } else {
    cycles1.value = [{ interval: 'hour', time: '', week: '', day: '' }];
  }
}

function handleCustomInterval(index: number, val: string) {
  if (val === 'day')
    cycles2.value.splice(index, 1, {
      type: val,
      time: '00',
      week: '',
      day: '',
      toType: '1',
      toTime: '02',
      toWeek: '',
      toDay: '',
    });
  else if (val === 'week')
    cycles2.value.splice(index, 1, {
      type: val,
      time: '00',
      week: '1',
      day: '',
      toType: '3',
      toTime: '02',
      toWeek: '2',
      toDay: '',
    });
  else if (val === 'month')
    cycles2.value.splice(index, 1, {
      type: val,
      time: '00',
      week: '',
      day: '1',
      toType: '4',
      toTime: '02',
      toWeek: '',
      toDay: '2',
    });
}

function handleCustomIntervalAdd() {
  cycles2.value.push({
    type: 'day',
    time: '00',
    week: '',
    day: '',
    toType: '1',
    toTime: '02',
    toWeek: '',
    toDay: '',
  });
}

function handleCustomIntervalDelete(index: number) {
  cycles2.value.splice(index, 1);
}

function handleCycleInterval(val: string) {
  if (val === 'hour') cycles1.value[0] = { interval: val, time: '', week: '', day: '' };
  else if (val === 'day') cycles1.value[0] = { interval: val, time: '01', week: '', day: '' };
  else if (val === 'week') cycles1.value[0] = { interval: val, time: '01', week: '1', day: '' };
  else if (val === 'month') cycles1.value[0] = { interval: val, time: '01', week: '', day: '1' };
  else cycles1.value[0] = { interval: val, time: '', week: '', day: '' };
}
</script>

<style lang="scss" scoped>
$border-color: #dcdfe6;
$right-btn-color: #1890ff;

.modbus-task {
  width: 100%;
  padding: 10px;
}

.dialog-content {
  width: 100%;
  box-sizing: border-box;
  padding: 0px 20px;
  overflow: auto;
  .input-item {
    width: 250px;
  }
  .create-title {
    display: flex;
    line-height: 36px;
    margin-bottom: 16px;
    .title-right {
      margin-left: auto;
    }
  }
  .create-code {
    font-size: 18px;
    line-height: 36px;
    font-weight: 800;
  }
  .form-item-label {
    display: flex;
    align-items: center;
    width: 100%;
    :deep(.el-form-item__label) {
      width: 100%;
    }
  }
  .timer-wrap {
    .timer-period {
      display: inline-block;
      margin-left: 30px;
      color: #000000;
      font-size: 12px;
      font-weight: normal;
    }
    .timer-custom {
      display: block;
      margin-top: 12px;
      color: #000000;
      font-size: 12px;
      font-weight: normal;
    }
  }
  .comp-add-edit {
    display: flex;
    flex-direction: column;
    :deep(.el-form-item__content) {
      margin-left: 0 !important;
    }
  }
}
</style>
