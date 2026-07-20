<template>
  <div class="scene-list-edit">
    <el-row>
      <el-col :span="24" class="l-card-box">
        <el-card>
          <template #header>
            <span>{{ $t('scene.edit.202832-0') }}</span>
          </template>
          <div class="el-table el-table--enable-row-hover el-table--medium">
            <el-form ref="formRef" :model="form" :rules="rules" label-width="120px" style="width: 100%">
              <el-row>
                <el-col :span="12">
                  <el-form-item :label="$t('scene.edit.202832-1')" prop="sceneModelName">
                    <el-input
                      style="width: 310px"
                      v-model="form.sceneModelName"
                      :placeholder="$t('scene.edit.202832-5')"
                      clearable
                    />
                  </el-form-item>
                </el-col>
                <el-col :span="12">
                  <el-form-item :label="$t('scene.edit.202832-2')" prop="deptId">
                    <el-tree-select
                      style="width: 310px"
                      v-model="form.deptId"
                      :data="deptOptions"
                      node-key="id"
                      :props="{ children: 'children', label: 'label' }"
                      :render-after-expand="false"
                      check-strictly
                      filterable
                      clearable
                      :placeholder="$t('scene.edit.202832-6')"
                      @change="handleTreeselectInput"
                    />
                  </el-form-item>
                </el-col>
              </el-row>
              <el-row>
                <el-col :span="12">
                  <el-form-item :label="$t('scene.edit.202832-3')">
                    <image-upload v-model="form.imgUrl" :multiple="false" :class="{ disable: uploadDisabled }" />
                  </el-form-item>
                </el-col>
                <el-col :span="12">
                  <el-form-item :label="$t('scene.edit.202832-4')">
                    <el-input
                      style="width: 310px"
                      v-model="form.sceneDesc"
                      type="textarea"
                      :autosize="{ minRows: 3, maxRows: 5 }"
                      :placeholder="$t('scene.edit.202832-7')"
                      clearable
                    />
                  </el-form-item>
                </el-col>
              </el-row>
            </el-form>
          </div>
        </el-card>
      </el-col>
      <!-- 场景配置 -->
      <el-col :span="24" class="l-card-box">
        <el-card>
          <template #header>
            <span>{{ $t('scene.edit.202832-8') }}</span>
          </template>
          <div class="el-table--enable-row-hover el-table--medium scene-setting">
            <el-radio-group v-model="congigType" style="margin-top: 5px">
              <el-radio-button :value="1">{{ $t('scene.edit.202832-9') }}</el-radio-button>
              <el-radio-button :value="2">{{ $t('scene.edit.202832-10') }}</el-radio-button>
              <el-radio-button :value="3">{{ $t('scene.edit.202832-11') }}</el-radio-button>
            </el-radio-group>
            <div v-show="congigType === 1" class="device-setting">
              <el-col :span="8" style="padding-right: 7.5px">
                <el-card shadow="never">
                  <template #header>
                    <span>{{ $t('scene.edit.202832-12') }}</span>
                  </template>
                  <div class="el-table--enable-row-hover el-table--medium device-select" style="height: 581px">
                    <el-table
                      ref="deviceTableRef"
                      :data="selectDeviceList"
                      style="width: 100%"
                      :border="false"
                      max-height="529"
                      highlight-current-row
                      @current-change="handleCurrentDeviceItemChange"
                    >
                      <el-table-column type="index" :label="$t('scene.edit.202832-13')" width="60" />
                      <el-table-column prop="cusDeviceId" :label="$t('scene.edit.202832-14')" width="190">
                        <template #default="scope">
                          <el-select
                            style="width: 180px"
                            v-model="scope.row.cusDeviceId"
                            size="small"
                            :placeholder="$t('scene.edit.202832-15')"
                            filterable
                            @change="handleUpdateDeviceItem(scope.row, $event)"
                          >
                            <el-option
                              v-for="(item, index) in deviceList"
                              :key="index"
                              :label="item.deviceName"
                              :value="item.deviceId"
                              :disabled="!isSelectDevice"
                            />
                          </el-select>
                        </template>
                      </el-table-column>
                      <el-table-column
                        :label="$t('opation')"
                        align="center"
                        class-name="small-padding fixed-width"
                        width="80"
                      >
                        <template #default="scope">
                          <el-button
                            style="color: #f56c6c"
                            size="small"
                            type="danger"
                            link
                            @click="handleDeleteDeviceItem(scope.row)"
                            v-hasPermi="['scene:modelDevice:remove']"
                          >
                            {{ $t('del') }}
                          </el-button>
                        </template>
                      </el-table-column>
                    </el-table>
                    <div class="tools-wrap">
                      <el-button
                        style="width: 100px"
                        type="primary"
                        @click="handleAddDeviceItem"
                        v-hasPermi="['scene:modelDevice:add']"
                      >
                        {{ $t('add') }}
                      </el-button>
                    </div>
                  </div>
                </el-card>
              </el-col>
              <el-col :span="16" style="padding-left: 7.5px">
                <el-card shadow="never">
                  <template #header>
                    <span>{{ $t('scene.edit.202832-16') }}</span>
                  </template>
                  <div class="el-table--enable-row-hover el-table--medium">
                    <div class="variable-list">
                      <el-form
                        :model="devConfigQueryParams"
                        @submit.prevent
                        ref="devConfigQueryFormRef"
                        :inline="true"
                        label-width="68px"
                      >
                        <el-form-item :label="$t('scene.overview.324354-11')" prop="sourceName">
                          <el-input
                            v-model="devConfigQueryParams.sourceName"
                            :placeholder="$t('scene.overview.324354-12')"
                            clearable
                            @keyup.enter="handleDevConfigQuery"
                          />
                        </el-form-item>
                        <el-form-item>
                          <el-button type="primary" :icon="Search" @click="handleDevConfigQuery">
                            {{ $t('search') }}
                          </el-button>
                        </el-form-item>
                      </el-form>
                      <div class="switch-wrap">
                        {{ $t('scene.edit.202832-17') }}
                        <el-switch
                          v-model="devConfigEnable"
                          :active-value="1"
                          :inactive-value="0"
                          @change="devConfigEnableChange"
                          v-hasPermi="['scene:modelData:editEnable']"
                        />
                      </div>
                    </div>
                    <el-table
                      v-loading="devConfigLoading"
                      :data="devConfigList"
                      style="width: 100%; margin-top: 15px"
                      :border="false"
                      height="483"
                    >
                      <el-table-column type="index" :label="$t('scene.edit.202832-13')" width="80" />
                      <el-table-column prop="slaveName" :label="$t('scene.overview.324354-9')" />
                      <el-table-column prop="sourceName" :label="$t('scene.overview.324354-11')" />
                      <el-table-column prop="name" :label="$t('scene.edit.202832-18')" width="100">
                        <template #default="scope">
                          <el-switch
                            v-model="scope.row.enable"
                            :active-value="1"
                            :inactive-value="0"
                            @change="devConfigItemEnableChange(scope.row)"
                            :disabled="!isEnableSwitch"
                          />
                        </template>
                      </el-table-column>
                    </el-table>
                    <div
                      v-show="devConfigTotal === 0"
                      style="
                        position: relative;
                        height: 25px;
                        margin-bottom: 10px;
                        margin-top: 15px;
                        padding: 10px 20px !important;
                      "
                    />
                    <pagination
                      v-show="devConfigTotal > 0"
                      :total="devConfigTotal"
                      v-model:page="devConfigQueryParams.pageNum"
                      v-model:limit="devConfigQueryParams.pageSize"
                      @pagination="getDevConfigVariableList"
                    />
                  </div>
                </el-card>
              </el-col>
            </div>

            <div v-show="congigType === 2" class="input-variable">
              <el-col :span="24">
                <el-card shadow="never">
                  <template #header>
                    <span>{{ $t('scene.edit.202832-16') }}</span>
                  </template>
                  <div class="el-table--enable-row-hover el-table--medium">
                    <div class="variable-list">
                      <el-form
                        :model="inputVariableQueryParams"
                        ref="inputVariableQueryFormRef"
                        size="small"
                        :inline="true"
                        label-width="68px"
                      >
                        <el-form-item :label="$t('scene.overview.324354-11')" prop="name">
                          <el-input
                            v-model="inputVariableQueryParams.name"
                            :placeholder="$t('scene.overview.324354-12')"
                            clearable
                          />
                        </el-form-item>
                        <el-form-item>
                          <el-button type="primary" :icon="Search" size="small" @click="handleInputVariableQuery">
                            {{ $t('search') }}
                          </el-button>
                        </el-form-item>
                      </el-form>
                      <div class="switch-wrap">
                        <el-button
                          size="small"
                          :icon="Plus"
                          type="primary"
                          :plain="true"
                          style="margin-right: 10px"
                          @click="handleAddInputVariable"
                          v-hasPermi="['scene:modelTag:add']"
                        >
                          {{ $t('add') }}
                        </el-button>
                        {{ $t('scene.edit.202832-17') }}
                        <el-switch
                          v-model="inputVariableEnable"
                          :active-value="1"
                          :inactive-value="0"
                          @change="inputVariableEnableChange"
                          v-hasPermi="['scene:modelData:editEnable']"
                        />
                      </div>
                    </div>
                    <el-table
                      v-loading="inputVariableLoading"
                      :data="inputVariableList"
                      style="width: 100%; margin-top: 15px"
                      :border="false"
                      max-height="539"
                    >
                      <el-table-column type="index" :label="$t('scene.edit.202832-13')" width="60" />
                      <el-table-column prop="name" :label="$t('scene.overview.324354-11')" />
                      <el-table-column prop="unit" :label="$t('scene.edit.202832-19')" width="100" />
                      <el-table-column prop="dataType" :label="$t('scene.edit.202832-20')" width="100">
                        <template #default="scope">
                          <span v-if="scope.row.dataType === '0'">{{ $t('scene.edit.202832-21') }}</span>
                          <span v-if="scope.row.dataType === '1'">{{ $t('scene.edit.202832-22') }}</span>
                        </template>
                      </el-table-column>
                      <el-table-column prop="defaultValue" :label="$t('scene.edit.202832-32')" width="120" />
                      <el-table-column prop="enable" :label="$t('scene.edit.202832-18')" width="100">
                        <template #default="scope">
                          <el-switch
                            v-model="scope.row.enable"
                            :active-value="1"
                            :inactive-value="0"
                            :disabled="!isEnableSwitch"
                            @change="inputVariableItemEnableChange(scope.row)"
                          />
                        </template>
                      </el-table-column>
                      <el-table-column prop="isSceneAttribute" width="200" :label="$t('scene.edit.202832-86')">
                        <template #default="scope">
                          <el-tooltip :content="$t('scene.edit.202832-87')" placement="top" effect="dark">
                            <el-checkbox
                              v-model="scope.row.isSceneAttribute"
                              :true-value="1"
                              :false-value="0"
                              @change="updateInputVariable(scope.row)"
                            />
                          </el-tooltip>
                        </template>
                      </el-table-column>
                      <el-table-column
                        :label="$t('opation')"
                        align="center"
                        class-name="small-padding fixed-width"
                        width="150"
                      >
                        <template #default="scope">
                          <el-button
                            size="small"
                            type="primary"
                            link
                            :icon="EditPen"
                            @click="handleEditInputVariable(scope.row)"
                            v-hasPermi="['scene:modelTag:query']"
                          >
                            {{ $t('look') }}
                          </el-button>
                          <el-button
                            style="color: #f56c6c"
                            size="small"
                            type="danger"
                            link
                            v-hasPermi="['scene:modelTag:remove']"
                            :icon="Delete"
                            @click="handleDeleteInputVariable(scope.row)"
                          >
                            {{ $t('del') }}
                          </el-button>
                        </template>
                      </el-table-column>
                    </el-table>
                    <pagination
                      v-show="inputVariableTotal > 0"
                      :total="inputVariableTotal"
                      v-model:page="inputVariableQueryParams.pageNum"
                      v-model:limit="inputVariableQueryParams.pageSize"
                      @pagination="getInputVariableList"
                    />
                  </div>
                </el-card>
              </el-col>
            </div>

            <div v-show="congigType === 3" class="operation-variable">
              <el-col :span="24">
                <el-card shadow="never">
                  <template #header>
                    <span>{{ $t('scene.edit.202832-16') }}</span>
                  </template>
                  <div class="el-table--enable-row-hover el-table--medium">
                    <div class="variable-list">
                      <el-form
                        :model="operationVariableQueryParams"
                        ref="operationVariableQueryFormRef"
                        size="small"
                        :inline="true"
                        label-width="68px"
                      >
                        <el-form-item :label="$t('scene.overview.324354-11')" prop="modelName">
                          <el-input
                            v-model="operationVariableQueryParams.name"
                            :placeholder="$t('scene.overview.324354-12')"
                            clearable
                          />
                        </el-form-item>
                        <el-form-item>
                          <el-button type="primary" :icon="Search" size="small" @click="handleOperationVariableQuery">
                            {{ $t('search') }}
                          </el-button>
                        </el-form-item>
                      </el-form>
                      <div class="switch-wrap">
                        <el-button
                          size="small"
                          :icon="Plus"
                          type="primary"
                          :plain="true"
                          style="margin-right: 10px"
                          @click="handleAddOperationVariable"
                          v-hasPermi="['scene:modelTag:add']"
                        >
                          {{ $t('add') }}
                        </el-button>
                        {{ $t('scene.edit.202832-17') }}
                        <el-switch
                          v-model="operationVariableEnable"
                          :active-value="1"
                          v-hasPermi="['scene:modelData:editEnable']"
                          :inactive-value="0"
                          @change="operationVariableEnableChange"
                        />
                      </div>
                    </div>
                    <el-table
                      v-loading="inputVariableLoading"
                      :data="operationVariableList"
                      style="width: 100%; margin-top: 15px"
                      :border="false"
                      max-height="539"
                    >
                      <el-table-column type="index" :label="$t('scene.edit.202832-13')" width="60" />
                      <el-table-column prop="name" :label="$t('scene.overview.324354-11')" />
                      <el-table-column prop="unit" :label="$t('scene.edit.202832-19')" width="100" />
                      <el-table-column prop="storage" :label="$t('scene.edit.202832-23')" width="100">
                        <template #default="scope">
                          <span v-if="scope.row.storage === 0">{{ $t('scene.edit.202832-24') }}</span>
                          <span v-if="scope.row.storage === 1">{{ $t('scene.edit.202832-25') }}</span>
                        </template>
                      </el-table-column>
                      <el-table-column prop="isReadonly" :label="$t('scene.edit.202832-26')" width="120">
                        <template #default="scope">
                          <span v-if="scope.row.isReadonly === 0">{{ $t('scene.edit.202832-27') }}</span>
                          <span v-if="scope.row.isReadonly === 1">{{ $t('scene.edit.202832-28') }}</span>
                        </template>
                      </el-table-column>
                      <el-table-column prop="enable" :label="$t('scene.edit.202832-18')" width="100">
                        <template #default="scope">
                          <el-switch
                            v-model="scope.row.enable"
                            :active-value="1"
                            :inactive-value="0"
                            @change="operationVariableItemEnableChange(scope.row)"
                            :disabled="!isEnableSwitch"
                          />
                        </template>
                      </el-table-column>
                      <el-table-column
                        :label="$t('opation')"
                        align="center"
                        class-name="small-padding fixed-width"
                        width="150"
                      >
                        <template #default="scope">
                          <el-button
                            size="small"
                            type="primary"
                            link
                            :icon="EditPen"
                            @click="handleEditOperationVariable(scope.row)"
                            v-hasPermi="['scene:modelTag:query']"
                          >
                            {{ $t('look') }}
                          </el-button>
                          <el-button
                            style="color: #f56c6c"
                            size="small"
                            type="danger"
                            link
                            :icon="Delete"
                            @click="handleDeleteOperationVariable(scope.row)"
                            v-hasPermi="['scene:modelTag:remove']"
                          >
                            {{ $t('del') }}
                          </el-button>
                        </template>
                      </el-table-column>
                    </el-table>
                    <pagination
                      v-show="operationVariableTotal > 0"
                      :total="operationVariableTotal"
                      v-model:page="operationVariableQueryParams.pageNum"
                      v-model:limit="operationVariableQueryParams.pageSize"
                      @pagination="getOperationVariableList"
                    />
                  </div>
                </el-card>
              </el-col>
            </div>
          </div>
        </el-card>
      </el-col>

      <el-col :span="24" class="l-card-box save-wrap">
        <el-button style="width: 200px" type="primary" @click="handleSave" v-hasPermi="['scene:model:edit']">
          {{ $t('save') }}
        </el-button>
      </el-col>
    </el-row>

    <!-- 添加或修改录入型变量对话框 -->
    <el-dialog :title="inputVariableTitle" v-model="isInputVariable" width="600px" append-to-body>
      <div class="el-divider el-divider--horizontal" style="margin-top: -25px" />
      <el-form ref="inputVariableFormRef" :model="inputVariableForm" :rules="inputVariableRules" label-width="120px">
        <el-form-item :label="$t('scene.overview.324354-11')" prop="name">
          <el-input
            style="width: 400px"
            v-model="inputVariableForm.name"
            :placeholder="$t('scene.overview.324354-12')"
            clearable
          />
        </el-form-item>
        <el-form-item :label="$t('scene.edit.202832-19')">
          <el-input
            style="width: 400px"
            v-model="inputVariableForm.unit"
            :placeholder="$t('scene.edit.202832-29')"
            clearable
          />
        </el-form-item>
        <el-form-item :label="$t('scene.edit.202832-30')" prop="dataType">
          <el-select
            style="width: 400px"
            v-model="inputVariableForm.dataType"
            :placeholder="$t('scene.edit.202832-31')"
            clearable
          >
            <el-option :label="$t('scene.edit.202832-21')" value="0" />
            <el-option :label="$t('scene.edit.202832-22')" value="1" />
          </el-select>
        </el-form-item>
        <el-form-item :label="$t('scene.edit.202832-32')" prop="defaultValue">
          <el-input
            style="width: 400px"
            v-model="inputVariableForm.defaultValue"
            :placeholder="$t('scene.edit.202832-33')"
            clearable
          />
        </el-form-item>
      </el-form>
      <template #footer>
        <div class="dialog-footer">
          <el-button
            type="primary"
            @click="handleInputVariableSubmit"
            v-hasPermi="['scene:modelTag:edit']"
            v-show="inputVariableForm.id"
          >
            {{ $t('update') }}
          </el-button>
          <el-button
            type="primary"
            @click="handleInputVariableSubmit"
            v-hasPermi="['scene:modelTag:add']"
            v-show="!inputVariableForm.id"
          >
            {{ $t('add') }}
          </el-button>
          <el-button @click="isInputVariable = false">{{ $t('cancel') }}</el-button>
        </div>
      </template>
    </el-dialog>

    <!-- 添加或修改运算型变量对话框 -->
    <el-dialog
      class="operation-variable-dialog"
      :title="operationVariableTitle"
      v-model="isOperationVariable"
      width="948px"
      append-to-body
    >
      <div class="el-divider el-divider--horizontal" style="margin-top: -10px" />
      <el-form
        ref="operationVariableFormRef"
        :model="operationVariableForm"
        :rules="operationVariableRules"
        label-width="150px"
      >
        <el-form-item :label="$t('scene.overview.324354-11')" prop="name">
          <el-input
            style="width: 763px"
            v-model="operationVariableForm.name"
            :placeholder="$t('scene.overview.324354-12')"
            clearable
          />
        </el-form-item>
        <el-form-item :label="$t('scene.edit.202832-19')">
          <el-input
            style="width: 763px"
            v-model="operationVariableForm.unit"
            :placeholder="$t('scene.edit.202832-29')"
            clearable
          />
        </el-form-item>
        <el-form-item :label="$t('scene.edit.202832-34')" prop="cycleType">
          <div class="timer-wrap">
            <el-radio-group v-model="operationVariableForm.cycleType" @change="handleCycleTypeInput">
              <el-radio :value="1" style="display: block">
                {{ $t('scene.edit.202832-35') }}
                <el-tooltip placement="right">
                  <template #content>
                    {{ $t('scene.edit.202832-36') }}
                    <br />
                    {{ $t('scene.edit.202832-37') }}
                  </template>
                  <el-icon style="color: #909399"><QuestionFilled /></el-icon>
                </el-tooltip>
                <div class="timer-period">
                  <span>{{ $t('scene.edit.202832-38') }}</span>
                  <el-select
                    style="width: 100px; margin-left: 10px"
                    v-model="cycles1[0].interval"
                    size="small"
                    :disabled="operationVariableForm.cycleType === 2"
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
                    :disabled="operationVariableForm.cycleType === 2"
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
                    :disabled="operationVariableForm.cycleType === 2"
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
                      cycles1[0].interval === 'day' || cycles1[0].interval === 'week' || cycles1[0].interval === 'month'
                    "
                    style="width: 100px; margin-left: 5px"
                    v-model="cycles1[0].time"
                    size="small"
                    :disabled="operationVariableForm.cycleType === 2"
                  >
                    <el-option
                      v-for="dict in variable_operation_time"
                      :key="dict.value"
                      :label="dict.label"
                      :value="dict.value"
                    />
                  </el-select>
                  <span style="margin-left: 10px">{{ $t('scene.edit.202832-39') }}</span>
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
                  :disabled="operationVariableForm.cycleType === 1"
                  @click="handleCustomIntervalAdd"
                >
                  {{ $t('scene.edit.202832-52') }}
                </el-button>
              </div>
              <div class="timer-custom" v-for="(item, index) in cycles2" :key="index">
                <span>{{ $t('scene.edit.202832-38') }}</span>
                <el-select
                  style="width: 100px; margin-left: 10px"
                  v-model="cycles2[index].type"
                  size="small"
                  :disabled="operationVariableForm.cycleType === 1"
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
                  :disabled="operationVariableForm.cycleType === 1"
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
                  :disabled="operationVariableForm.cycleType === 1"
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
                  :disabled="operationVariableForm.cycleType === 1"
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
                  :disabled="operationVariableForm.cycleType === 1"
                >
                  <el-option :label="$t('scene.edit.202832-47')" value="1" />
                  <el-option :label="$t('scene.edit.202832-48')" value="2" />
                </el-select>
                <el-select
                  v-if="cycles2[index].type === 'week'"
                  style="width: 100px; margin-left: 5px"
                  v-model="cycles2[index].toType"
                  size="small"
                  :disabled="operationVariableForm.cycleType === 1"
                >
                  <el-option :label="$t('scene.edit.202832-49')" value="3" />
                </el-select>
                <el-select
                  v-if="cycles2[index].type === 'month'"
                  style="width: 100px; margin-left: 5px"
                  v-model="cycles2[index].toType"
                  size="small"
                  :disabled="operationVariableForm.cycleType === 1"
                >
                  <el-option :label="$t('scene.edit.202832-50')" value="4" />
                </el-select>
                <el-select
                  v-if="cycles2[index].type === 'week'"
                  style="width: 100px; margin-left: 5px"
                  v-model="cycles2[index].toWeek"
                  size="small"
                  :disabled="operationVariableForm.cycleType === 1"
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
                  :disabled="operationVariableForm.cycleType === 1"
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
                  :disabled="operationVariableForm.cycleType === 1"
                  @click="handleCustomIntervalDelete(index)"
                >
                  {{ $t('del') }}
                </el-button>
              </div>
            </el-radio-group>
          </div>
        </el-form-item>
        <el-form-item
          class="comp-add-edit"
          :label="$t('scene.edit.202832-53')"
          prop="aliasFormule"
          :show-message="false"
        >
          <template #label>
            {{ $t('scene.edit.202832-53') }}
            <el-tooltip placement="right">
              <template #content>
                {{ $t('scene.edit.202832-54') }}
                <br />
                {{ $t('scene.edit.202832-55') }}
                <br />
                {{ $t('scene.edit.202832-56') }}
                <br />
                {{ $t('scene.edit.202832-57') }}
                <br />
                {{ $t('scene.edit.202832-58') }}
                <br />
                {{ $t('scene.edit.202832-59') }}
                <br />
                {{ $t('scene.edit.202832-60') }}
                <br />
                {{ $t('scene.edit.202832-61') }}
                <br />
                {{ $t('scene.edit.202832-62') }}
                <br />
                {{ $t('scene.edit.202832-63') }}
                <br />
                {{ $t('scene.edit.202832-64') }}
              </template>
              <el-icon style="color: #909399"><QuestionFilled /></el-icon>
            </el-tooltip>
          </template>
          <div class="comput-formula-box">
            <div class="title">{{ $t('scene.edit.202832-65') }}</div>
            <div class="content">
              <el-form-item prop="aliasFormule">
                <el-input
                  style="width: 733px !important; max-width: 100%"
                  v-model="operationVariableForm.aliasFormule"
                  :placeholder="$t('scene.edit.202832-76')"
                  type="textarea"
                  :rows="1"
                  clearable
                  :autosize="{ minRows: 1, maxRows: 4 }"
                />
              </el-form-item>
              <el-form-item>
                <el-table
                  v-if="operationVariableForm.tagPointsVOList.length > 0"
                  :data="operationVariableForm.tagPointsVOList"
                  style="width: 100%; margin-top: 15px; table-layout: fixed"
                  :border="false"
                >
                  <el-table-column prop="alias" :label="$t('scene.edit.202832-13')" width="80">
                    <template #default="scope">
                      <div class="alias-wrap">{{ scope.row.alias }}</div>
                    </template>
                  </el-table-column>
                  <el-table-column prop="sceneModelDeviceId" :label="$t('scene.overview.324354-7')">
                    <template #default="scope">
                      <el-select
                        v-model="scope.row.sceneModelDeviceId"
                        :placeholder="$t('scene.overview.324354-8')"
                        size="small"
                        @change="handleFormulaDataSourcesChange(scope, $event)"
                      >
                        <el-option
                          v-for="item in formulaDataSourcesList"
                          :key="item.id"
                          :label="item.name"
                          :value="item.id"
                        />
                      </el-select>
                    </template>
                  </el-table-column>
                  <el-table-column prop="sceneModelDataId" :label="$t('scene.edit.202832-66')">
                    <template #default="scope">
                      <el-select
                        v-model="scope.row.sceneModelDataId"
                        :placeholder="$t('scene.edit.202832-67')"
                        size="small"
                        @change="handleFormulaVariableChange(scope, $event)"
                      >
                        <el-option
                          v-for="item in formulaVariableLists[scope.$index]"
                          :key="item.id"
                          :label="item.sourceName"
                          :value="item.id"
                          :disabled="item.variableDataType === '1'"
                        />
                      </el-select>
                    </template>
                  </el-table-column>
                  <el-table-column prop="operation" :label="$t('scene.edit.202832-68')">
                    <template #default="scope">
                      <el-select v-model="scope.row.operation" :placeholder="$t('scene.edit.202832-69')" size="small">
                        <el-option
                          v-for="dict in variable_operation_type"
                          :key="dict.value"
                          :label="dict.label"
                          :value="parseInt(dict.value)"
                        />
                      </el-select>
                    </template>
                  </el-table-column>
                  <el-table-column
                    :label="$t('opation')"
                    align="center"
                    class-name="small-padding fixed-width"
                    width="100"
                  >
                    <template #default="scope">
                      <el-button
                        style="color: #f56c6c"
                        size="small"
                        type="danger"
                        link
                        :icon="Delete"
                        @click="handleDeleteFormula(scope.$index)"
                      >
                        {{ $t('del') }}
                      </el-button>
                    </template>
                  </el-table-column>
                </el-table>
                <el-button
                  style="margin-top: 20px"
                  size="small"
                  type="primary"
                  link
                  :icon="CirclePlus"
                  @click="handleAddFormula"
                >
                  {{ $t('scene.edit.202832-70') }}
                </el-button>
              </el-form-item>
            </div>
          </div>
        </el-form-item>
        <el-form-item :label="$t('scene.edit.202832-71')" prop="storage">
          <el-radio-group v-model="operationVariableForm.storage">
            <el-radio :value="1">{{ $t('scene.edit.202832-72') }}</el-radio>
            <el-radio :value="0">{{ $t('scene.edit.202832-73') }}</el-radio>
          </el-radio-group>
        </el-form-item>
      </el-form>
      <template #footer>
        <div class="dialog-footer">
          <el-button
            type="primary"
            @click="handleOperationVariableSubmit"
            v-hasPermi="['scene:modelTag:edit']"
            v-show="operationVariableForm.id"
          >
            {{ $t('update') }}
          </el-button>
          <el-button
            type="primary"
            @click="handleOperationVariableSubmit"
            v-hasPermi="['scene:modelTag:add']"
            v-show="!operationVariableForm.id"
          >
            {{ $t('add') }}
          </el-button>
          <el-button @click="isOperationVariable = false">{{ $t('cancel') }}</el-button>
        </div>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, computed, onMounted, nextTick, getCurrentInstance } from 'vue';
import { useRoute, useRouter } from 'vue-router';
import { useI18n } from 'vue-i18n';
import { Search, Plus, Delete, EditPen, CirclePlus, QuestionFilled } from '@element-plus/icons-vue';
import { deptsTreeSelect } from '@/api/system/user';
import { listDeviceShort } from '@/api/iot/device';
import { checkPermi } from '@/utils/permission';
import {
  getSceneModelDetail,
  getSceneModelDeviceList,
  addModelDevice,
  deleteModelDevice,
  updateModelDevice,
  getSceneModelDataListByType,
  enableModelDevice,
  enableModelData,
  getSceneModelTagList,
  addSceneModelTag,
  updateSceneModelTag,
  deleteSceneModelTag,
  getSceneModelTag,
  getSceneModelDataList,
  updateSceneModel,
} from '@/api/scene/list.js';
import { useDict } from '@/utils/dict/useDict';

const { proxy } = getCurrentInstance() as any;
const { t } = useI18n();
const route = useRoute();
const router = useRouter();

const {
  variable_operation_interval,
  variable_operation_time,
  variable_operation_week,
  variable_operation_day,
  variable_operation_type,
} = useDict(
  'variable_operation_interval',
  'variable_operation_time',
  'variable_operation_week',
  'variable_operation_day',
  'variable_operation_type'
);

const formRef = ref();
const deviceTableRef = ref();
const inputVariableFormRef = ref();
const operationVariableFormRef = ref();

const deptOptions = ref<any[]>([]);
const form = ref<any>({ sceneModelName: '', deptId: '', imgUrl: '', desc: '' });
const isSelectDevice = ref(false);
const isEnableSwitch = ref(false);
const rules = reactive<any>({
  sceneModelName: [{ required: true, message: t('scene.edit.202832-5'), trigger: 'blur' }],
  deptId: [{ required: true, message: t('scene.edit.202832-6'), trigger: 'change' }],
});
const congigType = ref(1);
const selectDeviceList = ref<any[]>([]);
const deviceList = ref<any[]>([]);
const devConfigLoading = ref(false);
const devConfigQueryParams = reactive({
  sceneModelId: null as any,
  variableType: 1,
  sceneModelDeviceId: null as any,
  sourceName: '',
  pageNum: 1,
  pageSize: 10,
});
const devConfigEnable = ref(0);
const devConfigTotal = ref(0);
const devConfigList = ref<any[]>([]);
const inputVariableLoading = ref(true);
const inputVariableQueryParams = reactive({
  sceneModelId: null as any,
  variableType: 2,
  name: '',
  pageNum: 1,
  pageSize: 10,
});
const inputVariableEnable = ref(0);
const inputVariableTotal = ref(0);
const inputVariableList = ref<any[]>([]);
const isInputVariable = ref(false);
const inputVariableTitle = ref(t('scene.edit.202832-74'));
const inputVariableForm = ref<any>({ name: '', unit: '', dataType: null, defaultValue: '' });
const inputVariableRules = reactive<any>({
  name: [{ required: true, message: t('scene.overview.324354-12'), trigger: 'blur' }],
  dataType: [{ required: true, message: t('scene.edit.202832-31'), trigger: 'change' }],
});
const operationVariableQueryParams = reactive({
  sceneModelId: null as any,
  variableType: 3,
  name: '',
  pageNum: 1,
  pageSize: 10,
});
const operationVariableEnable = ref(0);
const operationVariableTotal = ref(0);
const operationVariableList = ref<any[]>([]);
const isOperationVariable = ref(false);
const operationVariableTitle = ref(t('scene.edit.202832-74'));
const operationVariableForm = ref<any>({
  name: '',
  unit: '',
  cycleType: 1,
  cycle: '',
  aliasFormule: '',
  formule: '',
  tagPointsVOList: [],
  storage: 0,
  isReadonly: 1,
});
const operationVariableRules = reactive<any>({
  name: [{ required: true, message: t('scene.overview.324354-12'), trigger: 'blur' }],
  cycleType: [{ required: true, message: t('scene.edit.202832-75'), trigger: 'change' }],
  aliasFormule: [
    { required: true, message: t('scene.edit.202832-76'), trigger: 'change' },
    { validator: validateAliasFormule, trigger: 'change' },
  ],
  storage: [{ required: true, message: t('scene.edit.202832-77'), trigger: 'change' }],
});
const cycles1 = reactive<any[]>([{ interval: 'hour', time: '', week: '', day: '' }]);
const cycles2 = reactive<any[]>([
  { type: 'day', time: '00', week: '', day: '', toType: '1', toTime: '02', toWeek: '', toDay: '' },
]);
const formulaDataSourcesList = ref<any[]>([]);
const formulaVariableLists = reactive<any[]>([]);
const uploadDisabled = computed(() => form.value.imgUrl !== '');

// 判断权限
if (checkPermi(['scene:modelData:editEnable'])) isEnableSwitch.value = true;
if (checkPermi(['scene:modelDevice:edit'])) isSelectDevice.value = true;

onMounted(() => {
  getDeptTree();
  getSceneDetail();
  getDeviceList();
  getSelectDeviceList();
  getInputVariableList();
  getOperationVariableList();
});

function getDeptTree() {
  deptsTreeSelect().then((res: any) => {
    if (res.code === 200) deptOptions.value = res.data;
    else proxy.$modal.msgError(res.msg);
  });
}

function handleTreeselectInput(val: any) {
  if (val) formRef.value?.clearValidate('deptId');
}

function getSceneDetail() {
  proxy.$modal.loading(t('scene.detail.index.209809-3'));
  const id = route.query.id;
  getSceneModelDetail(id).then((res: any) => {
    if (res.code === 200) form.value = res.data;
    else proxy.$modal.msgError(res.msg);
    proxy.$modal.closeLoading();
  });
}

function getDeviceList() {
  listDeviceShort({ showChild: false, pageNum: 1, pageSize: 9999 }).then((res: any) => {
    if (res.code === 200) deviceList.value = res.rows;
    else proxy.$modal.msgError(res.msg);
  });
}

function getSelectDeviceList() {
  const { id } = route.query;
  const params = { sceneModelId: Number(id), variableType: congigType.value, pageNum: 1, pageSize: 9999 };
  getSceneModelDeviceList(params).then((res: any) => {
    if (res.code === 200) {
      selectDeviceList.value = res.rows;
      deviceTableRef.value?.setCurrentRow(res.rows[0]);
    } else proxy.$modal.msgError(res.msg);
    proxy.$modal.closeLoading();
  });
}

function handleAddDeviceItem() {
  selectDeviceList.value.push({ cusDeviceId: '' });
  nextTick(() => {
    const tableWrapper = document.querySelector('.el-table__body-wrapper');
    if (tableWrapper) tableWrapper.scrollTop = tableWrapper.scrollHeight;
  });
}

function handleDeleteDeviceItem(row: any) {
  if (!row.id) {
    const index = selectDeviceList.value.findIndex((item: any) => item.id === row.id);
    if (index !== -1) {
      selectDeviceList.value.splice(index, 1);
      getSelectDeviceList();
    }
  } else {
    deleteModelDevice(row.id).then((res: any) => {
      if (res.code === 200) getSelectDeviceList();
      else proxy.$modal.msgError(res.msg);
    });
  }
}

function handleUpdateDeviceItem(item: any, val: any) {
  const device = deviceList.value.find((f: any) => f.deviceId === val);
  const { id } = route.query;
  if (!item.id) {
    addModelDevice({ sceneModelId: Number(id), name: device.deviceName, cusDeviceId: device.deviceId }).then(
      (res: any) => {
        if (res.code === 200) getSelectDeviceList();
        else proxy.$modal.msgError(res.msg);
      }
    );
  } else {
    updateModelDevice({
      sceneModelId: Number(id),
      id: item.id,
      name: device.deviceName,
      cusDeviceId: device.deviceId,
    }).then((res: any) => {
      if (res.code === 200) getSelectDeviceList();
      else proxy.$modal.msgError(res.msg);
    });
  }
}

function handleCurrentDeviceItemChange(row: any) {
  devConfigQueryParams.sceneModelDeviceId = row.id || 0;
  getDevConfigVariableList();
}

function getDevConfigVariableList() {
  devConfigLoading.value = true;
  const { id } = route.query;
  devConfigQueryParams.sceneModelId = Number(id);
  getSceneModelDataListByType(devConfigQueryParams).then((res: any) => {
    if (res.code === 200) {
      devConfigList.value = res.rows;
      devConfigEnable.value = res.allEnable;
      devConfigTotal.value = res.total;
    } else proxy.$modal.msgError(res.msg);
    devConfigLoading.value = false;
  });
}

function handleDevConfigQuery() {
  getDevConfigVariableList();
}

function devConfigEnableChange() {
  const { id } = route.query;
  enableModelDevice({
    id: devConfigQueryParams.sceneModelDeviceId,
    sceneModelId: Number(id),
    variableType: congigType.value,
    allEnable: devConfigEnable.value,
  }).then((res: any) => {
    if (res.code === 200) getDevConfigVariableList();
    else proxy.$modal.msgError(res.msg);
  });
}

function devConfigItemEnableChange(item: any) {
  enableModelData({
    id: item.id,
    variableType: 1,
    sceneModelDeviceId: item.sceneModelDeviceId,
    enable: item.enable,
  }).then((res: any) => {
    if (res.code === 200) getDevConfigVariableList();
    else proxy.$modal.msgError(res.msg);
  });
}

function getInputVariableList() {
  inputVariableLoading.value = true;
  const { id } = route.query;
  inputVariableQueryParams.sceneModelId = Number(id);
  getSceneModelTagList(inputVariableQueryParams).then((res: any) => {
    if (res.code === 200) {
      inputVariableList.value = res.rows;
      inputVariableEnable.value = res.allEnable;
      inputVariableTotal.value = res.total;
    } else proxy.$modal.msgError(res.msg);
    inputVariableLoading.value = false;
  });
}

function handleInputVariableQuery() {
  getInputVariableList();
}

function inputVariableEnableChange() {
  const { id } = route.query;
  enableModelDevice({
    sceneModelId: Number(id),
    variableType: congigType.value,
    allEnable: inputVariableEnable.value,
  }).then((res: any) => {
    if (res.code === 200) getInputVariableList();
    else proxy.$modal.msgError(res.msg);
  });
}

function inputVariableItemEnableChange(item: any) {
  enableModelData({
    id: item.id,
    variableType: 2,
    sceneModelDeviceId: item.sceneModelDeviceId,
    enable: item.enable,
  }).then((res: any) => {
    if (res.code === 200) getInputVariableList();
    else proxy.$modal.msgError(res.msg);
  });
}

function handleAddInputVariable() {
  resetInputVariable();
  inputVariableTitle.value = t('scene.edit.202832-78');
  isInputVariable.value = true;
}

function resetInputVariable() {
  inputVariableForm.value = { name: '', unit: '', dataType: null, defaultValue: '' };
  inputVariableFormRef.value?.resetFields();
}

function handleEditInputVariable(item: any) {
  inputVariableTitle.value = t('scene.edit.202832-79');
  getSceneModelTag(item.id).then((res: any) => {
    if (res.code === 200) {
      inputVariableForm.value = res.data;
      isInputVariable.value = true;
    } else proxy.$modal.msgError(res.msg);
  });
}

function handleInputVariableSubmit() {
  inputVariableFormRef.value?.validate((valid: boolean) => {
    if (valid) {
      if (inputVariableForm.value.id != null) {
        updateSceneModelTag(inputVariableForm.value).then((res: any) => {
          if (res.code === 200) {
            proxy.$modal.msgSuccess(t('updateSuccess'));
            isInputVariable.value = false;
            getInputVariableList();
          } else proxy.$modal.msgError(res.msg);
        });
      } else {
        const { id } = route.query;
        inputVariableForm.value = {
          sceneModelId: Number(id),
          variableType: congigType.value,
          ...inputVariableForm.value,
        };
        addSceneModelTag(inputVariableForm.value).then((res: any) => {
          if (res.code === 200) {
            proxy.$modal.msgSuccess(t('addSuccess'));
            isInputVariable.value = false;
            getInputVariableList();
          } else proxy.$modal.msgError(res.msg);
        });
      }
    }
  });
}

function updateInputVariable(row: any) {
  updateSceneModelTag(row).then((res: any) => {
    if (res.code === 200) getInputVariableList();
    else proxy.$modal.msgError(res.msg);
  });
}

function handleDeleteInputVariable(item: any) {
  deleteSceneModelTag(item.id).then((res: any) => {
    if (res.code === 200) getInputVariableList();
    else proxy.$modal.msgError(res.msg);
  });
}

function getOperationVariableList() {
  inputVariableLoading.value = true;
  const { id } = route.query;
  operationVariableQueryParams.sceneModelId = Number(id);
  getSceneModelTagList(operationVariableQueryParams).then((res: any) => {
    if (res.code === 200) {
      operationVariableList.value = res.rows;
      operationVariableEnable.value = res.allEnable;
      operationVariableTotal.value = res.total;
    } else proxy.$modal.msgError(res.msg);
    inputVariableLoading.value = false;
  });
}

function handleOperationVariableQuery() {
  getOperationVariableList();
}

function operationVariableEnableChange() {
  const { id } = route.query;
  enableModelDevice({
    sceneModelId: Number(id),
    variableType: congigType.value,
    allEnable: operationVariableEnable.value,
  }).then((res: any) => {
    if (res.code === 200) getOperationVariableList();
    else proxy.$modal.msgError(res.msg);
  });
}

function operationVariableItemEnableChange(item: any) {
  enableModelData({
    id: item.id,
    variableType: 3,
    sceneModelDeviceId: item.sceneModelDeviceId,
    enable: item.enable,
  }).then((res: any) => {
    if (res.code === 200) getOperationVariableList();
    else proxy.$modal.msgError(res.msg);
  });
}

function handleAddOperationVariable() {
  resetOperationVariable();
  getFormulaDataSources();
  operationVariableTitle.value = t('scene.edit.202832-78');
  isOperationVariable.value = true;
}

function resetOperationVariable() {
  operationVariableForm.value = {
    name: '',
    unit: '',
    cycleType: 1,
    cycle: '',
    aliasFormule: '',
    formule: '',
    tagPointsVOList: [],
    storage: 0,
    isReadonly: 1,
  };
  operationVariableFormRef.value?.resetFields();
}

function handleCycleTypeInput(val: any) {
  if (val === 1)
    cycles2.splice(0, cycles2.length, {
      type: 'day',
      time: '00',
      week: '',
      day: '',
      toType: '1',
      toTime: '02',
      toWeek: '',
      toDay: '',
    });
  else cycles1.splice(0, cycles1.length, { interval: 'hour', time: '', week: '', day: '' });
}

function handleCycleInterval(val: any) {
  if (val === 'hour') cycles1.splice(0, 1, { interval: val, time: '', week: '', day: '' });
  else if (val === 'day') cycles1.splice(0, 1, { interval: val, time: '01', week: '', day: '' });
  else if (val === 'week') cycles1.splice(0, 1, { interval: val, time: '01', week: '1', day: '' });
  else if (val === 'month') cycles1.splice(0, 1, { interval: val, time: '01', week: '', day: '1' });
  else cycles1.splice(0, 1, { interval: val, time: '', week: '', day: '' });
}

function handleCustomInterval(index: number, val: any) {
  if (val === 'day')
    cycles2.splice(index, 1, {
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
    cycles2.splice(index, 1, {
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
    cycles2.splice(index, 1, {
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
  cycles2.push({ type: 'day', time: '00', week: '', day: '', toType: '1', toTime: '02', toWeek: '', toDay: '' });
}

function handleCustomIntervalDelete(index: number) {
  cycles2.splice(index, 1);
}

function getFormulaDataSources() {
  const { id } = route.query;
  getSceneModelDeviceList({ sceneModelId: Number(id), pageNum: 1, pageSize: 9999 }).then((res: any) => {
    if (res.code === 200) formulaDataSourcesList.value = res.rows;
    else proxy.$modal.msgError(res.msg);
  });
}

function handleFormulaDataSourcesChange(scope: any, val: any) {
  operationVariableForm.value.tagPointsVOList[scope.$index] = { ...scope.row, sceneModelDataId: null };
  getSceneModelDataList({ sceneModelId: route.query.id, sceneModelDeviceId: val, pageNum: 1, pageSize: 9999 }).then(
    (res: any) => {
      if (res.code === 200) formulaVariableLists[scope.$index] = res.rows;
      else proxy.$modal.msgError(res.msg);
    }
  );
}

function handleFormulaVariableChange(scope: any, val: any) {
  const vari = formulaVariableLists[scope.$index].find((f: any) => f.id === val);
  operationVariableForm.value.tagPointsVOList[scope.$index] = {
    ...scope.row,
    name: vari.sourceName,
    variableType: vari.variableType,
  };
  operationVariableFormRef.value?.clearValidate('aliasFormule');
}

function handleAddFormula() {
  const length = operationVariableForm.value.tagPointsVOList.length;
  let alias = 'A';
  if (length > 0) {
    const item = operationVariableForm.value.tagPointsVOList[length - 1];
    alias = String.fromCharCode(item.alias.charCodeAt(0) + 1);
  }
  operationVariableForm.value.tagPointsVOList.push({
    alias,
    sceneModelDeviceId: '',
    sceneModelDataId: null,
    name: '',
    variableType: '',
    operation: 1,
  });
  formulaVariableLists.push([]);
  operationVariableForm.value.aliasFormule = operationVariableForm.value.tagPointsVOList
    .map((item: any) => item.alias)
    .join('');
}

function handleDeleteFormula(index: number) {
  operationVariableForm.value.tagPointsVOList.splice(index, 1);
  formulaVariableLists.splice(index, 1);
  operationVariableForm.value.aliasFormule = operationVariableForm.value.tagPointsVOList
    .map((item: any) => item.alias)
    .join('');
}

function handleOperationVariableSubmit() {
  operationVariableFormRef.value?.validate((valid: boolean) => {
    if (valid) {
      const { id, cycleType, ...pres } = operationVariableForm.value;
      let cycle = '';
      if (cycleType === 1) {
        const c = cycles1.map((item: any) => {
          if (item.interval === 'hour') return { type: 'hour' };
          else if (item.interval === 'day') return { type: 'day', time: item.time };
          else if (item.interval === 'week') return { type: 'week', week: item.week, time: item.time };
          else if (item.interval === 'month') return { type: 'month', day: item.day, time: item.time };
          else return { interval: item.interval };
        });
        cycle = JSON.stringify(c);
      } else {
        const c = cycles2.map((item: any) => {
          if (item.type === 'day') return { type: 'day', time: item.time, toType: item.toType, toTime: item.toTime };
          else if (item.type === 'week')
            return {
              type: 'week',
              week: item.week,
              time: item.time,
              toType: item.toType,
              toWeek: item.toWeek,
              toTime: item.toTime,
            };
          else if (item.type === 'month')
            return {
              type: 'month',
              day: item.day,
              time: item.time,
              toType: item.toType,
              toDay: item.toDay,
              toTime: item.toTime,
            };
        });
        cycle = JSON.stringify(c);
      }
      let params: any = { ...pres, id, cycleType, cycle };
      if (id != null) {
        updateSceneModelTag(params).then((res: any) => {
          if (res.code === 200) {
            proxy.$modal.msgSuccess(t('updateSuccess'));
            isOperationVariable.value = false;
            getOperationVariableList();
          } else proxy.$modal.msgError(res.msg);
        });
      } else {
        const routeId = route.query.id;
        params = { sceneModelId: Number(routeId), variableType: congigType.value, ...params };
        addSceneModelTag(params).then((res: any) => {
          if (res.code === 200) {
            proxy.$modal.msgSuccess(t('addSuccess'));
            isOperationVariable.value = false;
            getOperationVariableList();
          } else proxy.$modal.msgError(res.msg);
        });
      }
    }
  });
}

function handleEditOperationVariable(item: any) {
  operationVariableTitle.value = t('scene.edit.202832-79');
  getFormulaDataSources();
  getSceneModelTag(item.id).then((res: any) => {
    if (res.code === 200) {
      const { data } = res;
      if (data.cycleType === 1) {
        const c = JSON.parse(data.cycle).map((item: any) => {
          if (item.type) {
            if (item.type === 'hour') return { interval: item.type, time: '', week: '', day: '' };
            else if (item.type === 'day') return { interval: item.type, time: item.time, week: '', day: '' };
            else if (item.type === 'week') return { interval: item.type, time: item.time, week: item.week, day: '' };
            else if (item.type === 'month') return { interval: item.type, time: item.time, week: '', day: item.day };
          } else return { interval: item.interval, time: '', week: '', day: '' };
        });
        cycles1.splice(0, cycles1.length, ...c);
      } else {
        const c = JSON.parse(data.cycle).map((item: any) => {
          if (item.type === 'day')
            return {
              type: 'day',
              time: item.time,
              week: '',
              day: '',
              toType: item.toType,
              toTime: item.toTime,
              toWeek: '',
              toDay: '',
            };
          else if (item.type === 'week')
            return {
              type: 'week',
              time: item.time,
              week: item.week,
              day: '',
              toType: item.toType,
              toTime: item.toTime,
              toWeek: item.toWeek,
              toDay: '',
            };
          else if (item.type === 'month')
            return {
              type: 'month',
              time: item.time,
              week: '',
              day: item.day,
              toType: item.toType,
              toTime: item.toTime,
              toWeek: item.toWeek,
              toDay: item.toDay,
            };
        });
        cycles2.splice(0, cycles2.length, ...c);
      }
      data.tagPointsVOList.forEach((item: any, index: number) => {
        getSceneModelDataList({
          sceneModelId: route.query.id,
          sceneModelDeviceId: item.sceneModelDeviceId,
          pageNum: 1,
          pageSize: 9999,
          t: new Date().getTime(),
        }).then((res: any) => {
          if (res.code === 200) formulaVariableLists[index] = res.rows;
          else proxy.$modal.msgError(res.msg);
        });
      });
      setTimeout(() => {
        operationVariableForm.value = data;
      }, 500);
      isOperationVariable.value = true;
    } else proxy.$modal.msgError(res.msg);
  });
}

function handleDeleteOperationVariable(item: any) {
  deleteSceneModelTag(item.id).then((res: any) => {
    if (res.code === 200) getOperationVariableList();
    else proxy.$modal.msgError(res.msg);
  });
}

function handleSave() {
  formRef.value?.validate((valid: boolean) => {
    if (valid) {
      if (form.value.sceneModelId != null) {
        updateSceneModel(form.value).then((res: any) => {
          if (res.code === 200) {
            proxy.$modal.msgSuccess(t('updateSuccess'));
            router.push({ path: '/scene/list/index' });
          } else proxy.$modal.msgError(res.msg);
        });
      }
    }
  });
}

function validateAliasFormule(_rule: any, value: any, callback: any) {
  operationVariableForm.value.formule = value;
  if (0 === operationVariableForm.value.tagPointsVOList.length) {
    callback(new Error(t('scene.edit.202832-67')));
  } else if (value) {
    if (String(value).length < 200) {
      let isFormule = true;
      try {
        const letters: string[] = [];
        operationVariableForm.value.tagPointsVOList.forEach(function (e: any) {
          const tt = '${' + e.sceneModelDataId + '}';
          letters.push(e.alias);
          operationVariableForm.value.formule = replaceAll(operationVariableForm.value.formule, e.alias, tt);
        });
        let evalFormule = operationVariableForm.value.formule.replace(/\$\{[0-9]*\}/g, '1');
        const calculate = new Function(`return ${evalFormule}`);
        evalFormule = String(calculate());
        const reg = /[0-9]{1}[A-Za-z]{1}|[A-Za-z]{2}|[A-Za-z]{1}[0-9]{1}/g;
        if (reg.test(operationVariableForm.value.aliasFormule)) {
          isFormule = false;
        } else {
          const beforeReg = /^(?:[+]|[-]|[*]|[/]){1}$/;
          if (evalFormule.slice(0, 1) === '-') evalFormule = evalFormule.slice(1);
          isFormule = !beforeReg.test(evalFormule.slice(0, 1));
        }
        const isInclude = letters.some((e: string) => value.includes(e));
        if (!isInclude) isFormule = false;
      } catch (e) {
        isFormule = false;
      }
      if (isFormule) callback();
      else callback(new Error(t('scene.edit.202832-81')));
    } else callback(new Error(t('scene.edit.202832-82')));
  } else callback(new Error(t('scene.edit.202832-83')));
}

function replaceAll(str: string, s1: string, s2: string) {
  return str.replace(new RegExp(s1, 'gm'), s2);
}
</script>

<style lang="scss" scoped>
.scene-list-edit {
  width: 100%;
  .l-card-box {
    padding: 0 15px;
    margin: 15px 0 0;
  }
  :deep(label) {
    font-weight: normal;
  }
  .scene-setting {
    overflow: hidden;
    font-size: 14px;
    color: #606266;
    .device-setting {
      margin-top: 20px;
      display: flex;
      .device-select {
        .tools-wrap {
          text-align: center;
          margin-top: 20px;
        }
      }
    }
    .device-setting,
    .operation-variable,
    .input-variable {
      margin-top: 20px;
      .variable-list {
        display: flex;
        flex-direction: row;
        justify-content: space-between;
        align-items: center;
        :deep(.el-form-item) {
          margin-bottom: 0px;
        }
        .switch-wrap {
          font-size: 14px;
        }
      }
    }
  }
  .save-wrap {
    text-align: center;
    margin-bottom: 15px;
  }
}
.operation-variable-dialog {
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
    .comput-formula-box {
      padding: 20px 10px;
      border: 1px solid #e7e9f1;
      margin-left: 50px;
      display: flex;
      max-width: 100%;
      overflow: hidden;
      .title {
        text-align: right;
        width: 96px;
        padding-right: 16px;
        flex-shrink: 0;
      }
      .content {
        font-size: 12px;
        line-height: 32px;
        flex: 1;
        min-width: 0;
        overflow-x: auto;
        .alias-wrap {
          width: 28px;
          height: 28px;
          background-image: linear-gradient(180deg, #6fb0ff, #3c78ff);
          box-shadow: 0 0 4px 0 rgba(0, 0, 0, 0.2);
          font-size: 12px;
          font-weight: 400;
          line-height: 28px;
          text-align: center;
          margin-top: 2px;
          color: #fff;
        }
      }
    }
  }
}
.disable {
  :deep(.el-upload--picture-card) {
    display: none !important;
  }
}
</style>
