<template>
  <div class="iot-scene">
    <el-card v-show="showSearch" class="search-card">
      <el-form @submit.prevent :model="queryParams" ref="queryFormRef" :inline="true" class="search-form">
        <el-form-item prop="sceneName">
          <el-input
            v-model="queryParams.sceneName"
            :placeholder="$t('alert.scene-list.591934-2')"
            clearable
            style="width: 192px"
            @keyup.enter="handleQuery"
          />
        </el-form-item>
        <div style="float: right">
          <el-button type="primary" :icon="Search" @click="handleQuery">{{ $t('search') }}</el-button>
          <el-button :icon="Refresh" @click="handleResetQuery">{{ $t('reset') }}</el-button>
        </div>
      </el-form>
    </el-card>

    <el-card>
      <el-row :gutter="10" style="margin-bottom: 15px">
        <el-col :span="1.5">
          <el-button type="primary" plain :icon="Plus" @click="handleAdd" v-hasPermi="['iot:scene:add']">
            {{ $t('add') }}
          </el-button>
        </el-col>
        <el-col :span="1.5">
          <el-button plain :icon="Delete" :disabled="multiple" @click="handleDelete" v-hasPermi="['iot:scene:remove']">
            {{ $t('del') }}
          </el-button>
        </el-col>
        <right-toolbar v-model:showSearch="showSearch" @queryTable="getList" />
      </el-row>

      <el-table
        v-loading="loading"
        :data="sceneList"
        :border="false"
        @selection-change="handleSelectionChange"
        ref="multipleTableRef"
        :row-key="getRowKeys"
      >
        <el-table-column :reserve-selection="true" type="selection" width="55" align="center" />
        <el-table-column :label="$t('alert.scene-list.591934-1')" align="left" prop="sceneName" min-width="200" />
        <el-table-column :label="$t('alert.index.236501-44')" align="center" prop="enable" width="80">
          <template #default="scope">
            <el-switch
              v-model="scope.row.enable"
              :active-value="1"
              :inactive-value="2"
              @change="handleEnableChange(scope.row)"
              :disabled="isDisabled"
            />
          </template>
        </el-table-column>
        <el-table-column :label="$t('scene.index.670805-0')" align="center" prop="hasAlert" width="80">
          <template #default="scope">
            <dict-tag :options="is_alert" :value="scope.row.hasAlert" />
          </template>
        </el-table-column>
        <el-table-column :label="$t('alert.index.236501-19')" align="center" prop="cond" width="80">
          <template #default="scope">
            <dict-tag :options="trigger_condition" :value="scope.row.cond" />
          </template>
        </el-table-column>
        <el-table-column :label="$t('alert.index.236501-23')" align="center" prop="executeMode" width="80">
          <template #default="scope">
            <dict-tag :options="execution_method" :value="scope.row.executeMode" />
          </template>
        </el-table-column>
        <el-table-column :label="$t('scene.index.670805-3')" align="center" prop="silentPeriod" width="80">
          <template #default="scope">
            <span>{{ scope.row.silentPeriod }} {{ $t('scene.index.670805-4') }}</span>
          </template>
        </el-table-column>
        <el-table-column :label="$t('scene.index.670805-6')" align="center" prop="executeDelay" width="80">
          <template #default="scope">
            <span>{{ scope.row.executeDelay }} {{ $t('scene.index.670805-5') }}</span>
          </template>
        </el-table-column>
        <el-table-column :label="$t('template.index.891112-117')" align="center" prop="userName" width="150" />
        <el-table-column :label="$t('template.index.891112-118')" align="center" prop="createBy" width="150" />
        <el-table-column :label="$t('creatTime')" align="center" prop="createTime" width="120">
          <template #default="scope">
            <span>{{ parseTime(scope.row.createTime, '{y}-{m}-{d}') }}</span>
          </template>
        </el-table-column>
        <el-table-column fixed="right" :label="$t('opation')" align="center" width="290">
          <template #default="scope">
            <el-button link :icon="View" size="small" @click="handleUpdate(scope.row)" v-hasPermi="['iot:scene:query']">
              {{ $t('look') }}
            </el-button>
            <el-button
              link
              :icon="Delete"
              size="small"
              @click="handleDelete(scope.row)"
              v-hasPermi="['iot:scene:remove']"
            >
              {{ $t('del') }}
            </el-button>
            <el-button
              link
              :icon="CaretRight"
              size="small"
              @click="handleRun(scope.row)"
              v-hasPermi="['iot:scene:run']"
            >
              {{ $t('scene.index.670805-7') }}
            </el-button>
            <el-button
              link
              :icon="View"
              size="small"
              @click="handleLog(scope.row.chainName)"
              v-hasPermi="['iot:scene:run']"
            >
              {{ $t('script.349087-36') }}
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

    <!-- 添加或修改场景联动对话框 -->
    <el-dialog
      class="scene-config-dialog"
      :title="title"
      v-model="open"
      width="900px"
      append-to-body
      :close-on-click-modal="false"
      :close-on-press-escape="false"
    >
      <el-form ref="formRef" :model="form" :rules="rules" label-width="90px">
        <el-row :gutter="50">
          <el-col :span="12">
            <el-form-item :label="$t('alert.index.236501-18')" prop="sceneName">
              <el-input v-model="form.sceneName" :placeholder="$t('alert.scene-list.591934-2')" />
            </el-form-item>
            <el-form-item :label="$t('scene.index.670805-8')">
              <el-switch v-model="form.enable" :active-value="1" :inactive-value="2" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item :label="$t('alert.index.236501-11')" prop="remark">
              <el-input v-model="form.remark" type="textarea" :rows="4" :placeholder="$t('plzInput')" />
            </el-form-item>
          </el-col>
        </el-row>

        <div style="height: 1px; background-color: #ddd; margin: 0 0 20px 0"></div>
        <div class="condition-wrap">
          <el-form-item :label="$t('scene.index.670805-9')" prop="triggers">
            <template #label>
              <span>
                <el-tooltip class="item" effect="dark" :content="$t('scene.index.670805-13')" placement="top">
                  <el-icon><QuestionFilled /></el-icon>
                </el-tooltip>
                {{ $t('scene.index.670805-9') }}
              </span>
            </template>
            <div class="item-wrap" style="background-color: #eef3f7">
              <el-row :gutter="16">
                <el-col :span="8">
                  <span>{{ $t('scene.index.670805-10') }}</span>
                  <el-select v-model="form.cond" :placeholder="$t('scene.index.670805-11')" style="width: 160px">
                    <el-option
                      v-for="dict in trigger_condition"
                      :key="dict.value"
                      :label="dict.label"
                      :value="Number(dict.value)"
                      :disabled="formJson.triggers.length > 1 && dict.value == 3"
                    />
                  </el-select>
                </el-col>
                <el-col :span="8">
                  <span>
                    <el-tooltip class="item" effect="dark" :content="$t('scene.index.670805-80')" placement="top">
                      <el-icon><QuestionFilled /></el-icon>
                    </el-tooltip>
                    {{ $t('scene.index.670805-81') }}
                  </span>
                  <el-input
                    v-model="form.checkDelay"
                    :placeholder="$t('scene.index.670805-5')"
                    :max="90"
                    :min="0"
                    oninput="
                      if (value > 600) value = 600;
                      if (value < 0) value = 0;
                    "
                    type="number"
                    style="width: 140px"
                  >
                    <template #append>{{ $t('scene.index.670805-5') }}</template>
                  </el-input>
                </el-col>
              </el-row>
            </div>
            <!-- 触发器列表 -->
            <div class="item-wrap" v-for="(item, index) in formJson.triggers" :key="index">
              <el-row :gutter="16" style="margin-bottom: 0">
                <el-col :span="5">
                  <el-select
                    v-model="item.source"
                    :placeholder="$t('pleaseSelect')"
                    @change="handleTriggerSource($event, index)"
                  >
                    <el-option
                      v-for="dict in scene_trigger_source"
                      :key="dict.value"
                      :label="dict.label"
                      :value="Number(dict.value)"
                    />
                  </el-select>
                </el-col>
                <el-col :span="10" v-if="item.source == 1">
                  <el-input
                    readonly
                    v-model="item.deviceCount"
                    :placeholder="$t('product.product-things-model.142341-83')"
                    style="margin-top: 3px"
                  >
                    <template #prepend>{{ $t('scene.index.670805-14') }}</template>
                    <template #append>
                      <el-button @click="handleSelectDevice('trigger', item, index)">
                        {{ $t('firmware.index.222541-31') }}
                      </el-button>
                    </template>
                  </el-input>
                </el-col>
                <el-col :span="10" v-if="item.source == 3">
                  <el-input
                    readonly
                    v-model="item.productName"
                    :placeholder="$t('device.allot-import-dialog.060657-1')"
                    style="margin-top: 3px"
                  >
                    <template #append>
                      <el-button @click="handleSelectProduct('trigger', item, index)">
                        {{ $t('sip.product-list.998536-0') }}
                      </el-button>
                    </template>
                  </el-input>
                </el-col>
                <div class="delete-wrap" v-if="index != 0">
                  <el-button
                    plain
                    type="danger"
                    style="padding: 5px"
                    :icon="Delete"
                    @click="handleRemoveTrigger(index)"
                  >
                    {{ $t('del') }}
                  </el-button>
                </div>
              </el-row>
              <el-row :gutter="16" style="margin-top: 10px">
                <el-col :span="10" v-if="item.source == 4">
                  <el-input v-model="item.id" :placeholder="$t('scene.index.670805-77')" style="margin-top: 3px">
                    <template #prepend>{{ $t('scene.index.670805-78') }}</template>
                  </el-input>
                  <MonacoEditor v-model="item.value" :language="'json'" style="margin-top: 10px; height: 300px" />
                </el-col>
              </el-row>
              <!--定时-->
              <el-row :gutter="16" v-if="item.source == 2" style="margin-top: 10px">
                <el-col :span="5">
                  <el-time-picker
                    style="width: 100%"
                    v-model="item.timerTimeValue"
                    value-format="HH:mm"
                    format="HH:mm"
                    :placeholder="$t('device.device-timer.433369-19')"
                    @change="timeChange($event, index)"
                    :disabled="item.isAdvance == 1"
                  />
                </el-col>
                <el-col :span="19">
                  <el-select
                    v-model="item.timerWeekValue"
                    :placeholder="$t('pleaseSelect')"
                    multiple
                    style="width: 100%"
                    @change="weekChange($event, index)"
                    :disabled="item.isAdvance == 1"
                  >
                    <el-option
                      v-for="dict in variable_operation_week"
                      :key="dict.value"
                      :label="dict.label"
                      :value="Number(dict.value)"
                    />
                  </el-select>
                </el-col>
                <el-col :span="5" style="margin-top: 5px">
                  <el-checkbox
                    v-model="item.isAdvance"
                    :true-value="1"
                    :false-value="0"
                    @change="customerCronChange($event, index)"
                    border
                    style="width: 100%"
                  >
                    {{ $t('scene.index.670805-15') }}
                  </el-checkbox>
                </el-col>
                <el-col :span="19" style="margin-top: 10px">
                  <el-input
                    v-model="item.cronExpression"
                    :placeholder="$t('scene.index.670805-16')"
                    :disabled="item.isAdvance == 0"
                  >
                    <template #append>
                      <el-button type="primary" @click="handleShowCron(item, index)" :disabled="item.isAdvance == 0">
                        {{ $t('scene.index.670805-17') }}
                      </el-button>
                    </template>
                  </el-input>
                </el-col>
              </el-row>
              <!-- 物模型选择（触发器） -->
              <div v-if="item.thingsModel">
                <el-row :gutter="16" style="margin-top: 10px">
                  <el-col :span="5">
                    <el-select
                      v-model="item.type"
                      :placeholder="$t('scene.index.670805-18')"
                      @change="handleTriggerTypeChange($event, index)"
                    >
                      <el-option
                        v-for="dict in trigger_type"
                        :key="dict.value + 'type'"
                        :label="dict.label"
                        :value="Number(dict.value)"
                      />
                    </el-select>
                  </el-col>
                  <el-col :span="10">
                    <el-select
                      style="width: 100%"
                      v-model="item.parentId"
                      :placeholder="$t('scene.index.670805-19')"
                      v-if="item.type == 1"
                      @change="handleTriggerParentModelChange($event, index)"
                    >
                      <el-option
                        v-for="(subItem, subIndex) in item.thingsModel.properties"
                        :key="subIndex + 'triggerProperty'"
                        :label="subItem.name"
                        :value="subItem.id"
                      />
                    </el-select>
                    <el-select
                      style="width: 100%"
                      v-model="item.parentId"
                      :placeholder="$t('scene.index.670805-19')"
                      v-else-if="item.type == 2"
                      @change="handleTriggerParentModelChange($event, index)"
                    >
                      <el-option
                        v-for="(subItem, subIndex) in item.thingsModel.functions"
                        :key="subIndex + 'triggerFunc'"
                        :label="subItem.name"
                        :value="subItem.id"
                      />
                    </el-select>
                    <el-select
                      style="width: 100%"
                      v-model="item.parentId"
                      :placeholder="$t('scene.index.670805-19')"
                      v-else-if="item.type == 3"
                      @change="handleTriggerParentModelChange($event, index)"
                    >
                      <el-option
                        v-for="(subItem, subIndex) in item.thingsModel.events"
                        :key="subIndex + 'triggerEvents'"
                        :label="subItem.name"
                        :value="subItem.id"
                      />
                    </el-select>
                  </el-col>
                </el-row>
                <el-row :gutter="16" style="margin-top: 10px">
                  <el-col :span="5" v-if="item.parentModel && item.parentModel.datatype.type === 'array'">
                    <el-select
                      v-model="item.arrayIndex"
                      :placeholder="$t('pleaseSelect')"
                      @change="handleTriggerIndexChange($event, index)"
                    >
                      <el-option
                        v-for="subItem in item.parentModel.datatype.arrayModel"
                        :key="subItem.id"
                        :label="subItem.name"
                        :value="subItem.id"
                      />
                    </el-select>
                  </el-col>
                  <el-col
                    :span="5"
                    v-if="
                      item.parentModel &&
                      item.parentModel.datatype.type === 'array' &&
                      item.parentModel.datatype.arrayType === 'object'
                    "
                  >
                    <el-select
                      v-model="item.id"
                      :placeholder="$t('pleaseSelect')"
                      @change="handleTriggerModelChange($event, index)"
                    >
                      <el-option
                        v-for="(subItem, subIndex) in item.parentModel.datatype.params"
                        :key="subIndex"
                        :label="subItem.name"
                        :value="subItem.id"
                      />
                    </el-select>
                  </el-col>
                  <el-col :span="5" v-if="item.parentModel && item.parentModel.datatype.type === 'object'">
                    <el-select
                      v-model="item.id"
                      :placeholder="$t('pleaseSelect')"
                      @change="handleTriggerModelChange($event, index)"
                    >
                      <el-option
                        v-for="(subItem, subIndex) in item.parentModel.datatype.params"
                        :key="subIndex"
                        :label="subItem.name"
                        :value="subItem.id"
                      />
                    </el-select>
                  </el-col>
                  <!-- operator 选择器（仅数字/字符串类型显示） -->
                  <el-col
                    :span="5"
                    v-if="
                      item.model &&
                      (item.model.datatype.type === 'integer' ||
                        item.model.datatype.type === 'decimal' ||
                        item.model.datatype.type === 'string')
                    "
                  >
                    <el-select v-model="item.operator" :placeholder="$t('scene.index.670805-20')">
                      <el-option
                        v-for="dict in operator"
                        :key="dict.value"
                        :label="dict.label"
                        :value="dict.value"
                        :disabled="
                          (['>', '<', '>=', '<=', 'between', 'notBetween'].includes(dict.value) &&
                            !['integer', 'decimal'].includes(item.model.datatype.type)) ||
                          (['contain', 'notContain'].includes(dict.value) && item.model.datatype.type !== 'string')
                        "
                      />
                    </el-select>
                  </el-col>
                  <!-- 值输入器（封装组件） -->
                  <el-col :span="10" v-if="item.model">
                    <things-model-input
                      v-model="item.value"
                      v-model:value-a="item.valueA"
                      v-model:value-b="item.valueB"
                      :model="item.model"
                      :operator="item.operator"
                      @range-change="valueChange($event, item)"
                    />
                  </el-col>
                </el-row>
              </div>
            </div>
            <div v-if="!(form.cond == 3 && formJson.triggers.length > 0)">
              +
              <a style="color: #486ff2" @click="handleAddTrigger()">{{ $t('scene.index.670805-23') }}</a>
            </div>
          </el-form-item>
        </div>

        <el-divider />
        <div class="action-wrap">
          <el-form-item :label="$t('scene.index.670805-24')">
            <div class="item-wrap" style="background-color: #eef3f7">
              <el-row :gutter="16">
                <el-col :span="8">
                  <span>
                    <el-tooltip class="item" effect="dark" :content="$t('scene.index.670805-25')" placement="top">
                      <el-icon><QuestionFilled /></el-icon>
                    </el-tooltip>
                    {{ $t('scene.index.670805-26') }}
                  </span>
                  <el-input
                    v-model="form.silentPeriod"
                    :placeholder="$t('scene.index.670805-4')"
                    type="number"
                    style="width: 140px"
                  >
                    <template #append>{{ $t('scene.index.670805-4') }}</template>
                  </el-input>
                </el-col>
                <el-col :span="8">
                  <span>{{ $t('scene.index.670805-27') }}</span>
                  <el-select v-model="form.executeMode" :placeholder="$t('scene.index.670805-28')" style="width: 160px">
                    <el-option key="1" :label="$t('scene.index.670805-29')" :value="1" />
                    <el-option key="2" :label="$t('scene.index.670805-30')" :value="2" />
                  </el-select>
                </el-col>
                <el-col :span="8">
                  <span>
                    <el-tooltip class="item" effect="dark" :content="$t('scene.index.670805-31')" placement="top">
                      <el-icon><QuestionFilled /></el-icon>
                    </el-tooltip>
                    {{ $t('scene.index.670805-32') }}
                  </span>
                  <el-input
                    v-model="form.executeDelay"
                    :placeholder="$t('scene.index.670805-5')"
                    :max="90"
                    :min="0"
                    oninput="
                      if (value > 90) value = 90;
                      if (value < 0) value = 0;
                    "
                    type="number"
                    style="width: 140px"
                  >
                    <template #append>{{ $t('scene.index.670805-5') }}</template>
                  </el-input>
                </el-col>
              </el-row>
            </div>
            <!-- 执行动作列表 -->
            <div class="item-wrap" v-for="(item, index) in formJson.actions" :key="index">
              <el-row :gutter="16" style="margin-bottom: 0">
                <el-col :span="5">
                  <el-select
                    v-model="item.source"
                    :placeholder="$t('pleaseSelect')"
                    @change="handleActionSourceChange($event, index)"
                  >
                    <el-option
                      v-for="dict in scene_operator"
                      :key="dict.value"
                      :label="dict.label"
                      :value="Number(dict.value)"
                    />
                  </el-select>
                </el-col>
                <el-col :span="10" v-if="item.source == 1">
                  <el-input
                    readonly
                    v-model="item.deviceCount"
                    :placeholder="$t('scene.index.670805-33')"
                    style="margin-top: 3px"
                  >
                    <template #prepend>{{ $t('scene.index.670805-14') }}</template>
                    <template #append>
                      <el-button @click="handleSelectDevice('action', item, index)">
                        {{ $t('scene.index.670805-34') }}
                      </el-button>
                    </template>
                  </el-input>
                </el-col>
                <el-col :span="10" v-if="item.source == 3">
                  <el-input
                    readonly
                    v-model="item.productName"
                    :placeholder="$t('scene.index.670805-35')"
                    style="margin-top: 3px"
                  >
                    <template #append>
                      <el-button @click="handleSelectProduct('action', item, index)">
                        {{ $t('scene.index.670805-36') }}
                      </el-button>
                    </template>
                  </el-input>
                </el-col>
                <el-col :span="10" v-if="item.source == 4" style="display: flex; align-items: center">
                  <el-input v-model="item.notifyCount" :placeholder="$t('scene.edit.202832-84')" type="number" />
                </el-col>
                <el-col :span="10" v-if="item.source == 5">
                  <el-input
                    readonly
                    v-model="item.name"
                    :placeholder="$t('scene.index.670805-75')"
                    style="margin-top: 3px"
                  >
                    <template #append>
                      <el-button @click="handleSelectRecoverScenes('action', item, index)">
                        {{ $t('scene.index.670805-76') }}
                      </el-button>
                    </template>
                  </el-input>
                </el-col>
                <div class="delete-wrap">
                  <el-icon v-if="item.source == 4" class="link-style" @click="openAlertList"><Connection /></el-icon>
                  <el-button
                    v-if="index != 0"
                    plain
                    type="danger"
                    style="padding: 5px"
                    :icon="Delete"
                    @click="handleRemoveAction(index)"
                  >
                    {{ $t('del') }}
                  </el-button>
                </div>
              </el-row>
              <!-- 物模型选择（动作） -->
              <div v-if="item.thingsModel">
                <el-row :gutter="16" style="margin-top: 10px">
                  <el-col :span="5">
                    <el-select
                      v-model="item.type"
                      :placeholder="$t('scene.index.670805-1')"
                      @change="handleActionTypeChange($event, index)"
                    >
                      <el-option
                        v-for="dict in trigger_type"
                        :key="dict.value + 'type'"
                        :label="dict.label"
                        :value="Number(dict.value)"
                        :disabled="['3', '5', '6'].includes(dict.value)"
                      />
                    </el-select>
                  </el-col>
                  <el-col :span="10">
                    <el-select
                      style="width: 100%"
                      v-model="item.parentId"
                      :placeholder="$t('scene.index.670805-19')"
                      v-if="item.type == 1"
                      @change="handleActionParentModelChange($event, index)"
                    >
                      <el-option
                        v-for="(subItem, subIndex) in item.thingsModel.properties"
                        :key="subIndex + 'actionProperty'"
                        :label="subItem.name"
                        :value="subItem.id"
                      />
                    </el-select>
                    <el-select
                      style="width: 100%"
                      v-model="item.parentId"
                      :placeholder="$t('scene.index.670805-19')"
                      v-else-if="item.type == 2"
                      @change="handleActionParentModelChange($event, index)"
                    >
                      <el-option
                        v-for="(subItem, subIndex) in item.thingsModel.functions"
                        :key="subIndex + 'actionFunc'"
                        :label="subItem.name"
                        :value="subItem.id"
                      />
                    </el-select>
                  </el-col>
                </el-row>
                <el-row :gutter="16" style="margin-top: 10px">
                  <el-col :span="5" v-if="item.parentModel && item.parentModel.datatype.type === 'array'">
                    <el-select
                      v-model="item.arrayIndex"
                      :placeholder="$t('pleaseSelect')"
                      @change="handleActionIndexChange($event, index)"
                    >
                      <el-option
                        v-for="subItem in item.parentModel.datatype.arrayModel"
                        :key="subItem.id"
                        :label="subItem.name"
                        :value="subItem.id"
                      />
                    </el-select>
                  </el-col>
                  <el-col
                    :span="5"
                    v-if="
                      item.parentModel &&
                      item.parentModel.datatype.type === 'array' &&
                      item.parentModel.datatype.arrayType === 'object'
                    "
                  >
                    <el-select
                      v-model="item.id"
                      :placeholder="$t('pleaseSelect')"
                      @change="handleActionModelChange($event, index)"
                    >
                      <el-option
                        v-for="(subItem, subIndex) in item.parentModel.datatype.params"
                        :key="subIndex"
                        :label="subItem.name"
                        :value="subItem.id"
                      />
                    </el-select>
                  </el-col>
                  <el-col :span="5" v-if="item.parentModel && item.parentModel.datatype.type === 'object'">
                    <el-select
                      v-model="item.id"
                      :placeholder="$t('pleaseSelect')"
                      @change="handleActionModelChange($event, index)"
                    >
                      <el-option
                        v-for="(subItem, subIndex) in item.parentModel.datatype.params"
                        :key="subIndex"
                        :label="subItem.name"
                        :value="subItem.id"
                      />
                    </el-select>
                  </el-col>
                  <!-- 值输入器（封装组件，action 不需要区间操作符） -->
                  <el-col :span="10" v-if="item.model">
                    <things-model-input v-model="item.value" :model="item.model" />
                  </el-col>
                </el-row>
              </div>
            </div>
            <div>
              +
              <a style="color: #486ff2" @click="handleAddAction()">{{ $t('scene.index.670805-37') }}</a>
            </div>
          </el-form-item>
        </div>
      </el-form>
      <template #footer>
        <div class="dialog-footer">
          <el-button
            type="primary"
            @click="handleSubmitForm"
            :disabled="updateBtnDisabled"
            :loading="confirmLoading"
            v-hasPermi="['iot:scene:edit']"
            v-show="form.sceneId"
          >
            {{ $t('update') }}
          </el-button>
          <el-button
            type="primary"
            @click="handleSubmitForm"
            :disabled="updateBtnDisabled"
            :loading="confirmLoading"
            v-hasPermi="['iot:scene:add']"
            v-show="!form.sceneId"
          >
            {{ $t('add') }}
          </el-button>
          <el-button @click="cancel">{{ $t('cancel') }}</el-button>
        </div>
      </template>
    </el-dialog>

    <!-- 日志对话框 -->
    <el-dialog
      :title="title"
      v-model="openLog"
      width="800px"
      append-to-body
      :close-on-click-modal="false"
      :close-on-press-escape="false"
    >
      <div
        v-if="logs"
        ref="logContainerRef"
        v-loading="logLoading"
        :element-loading-text="$t('script.349087-39')"
        style="
          border: 1px solid #ccc;
          border-radius: 4px;
          height: 480px;
          background-color: #181818;
          color: #fff;
          padding: 10px;
          line-height: 20px;
          overflow: auto;
        "
      >
        <pre>{{ logs }}</pre>
      </div>
      <el-empty v-if="!logs" :description="$t('ruleengine.editor.components.debugLog.807357-0')" />
      <template #footer>
        <div class="dialog-footer">
          <el-button @click="cancelLog">{{ $t('script.349087-38') }}</el-button>
        </div>
      </template>
    </el-dialog>

    <!-- 子组件 -->
    <deviceList ref="deviceListRef" @deviceEvent="getSelectProductDevice($event, 1)" />
    <productList ref="productListRef" @productEvent="getSelectProductDevice($event, 2)" />
    <sceneListComp ref="sceneListRef" @sceneEvent="getSceneData($event)" />
    <alertList ref="alertListRef" @bindEvent="getBindData" />
    <alertConfigList ref="alertConfigListRef" @bindConfigEvent="getBindConfigData" />

    <!-- CRON表达式生成器 -->
    <el-dialog
      :title="$t('scene.index.670805-38')"
      v-model="openCron"
      append-to-body
      destroy-on-close
      class="scrollbar"
    >
      <crontab @hide="openCron = false" @fill="crontabFill" :expression="expression" style="padding-bottom: 10px" />
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, nextTick, onMounted, getCurrentInstance } from 'vue';
import { Search, Refresh, Plus, Delete, View, QuestionFilled, Connection, CaretRight } from '@element-plus/icons-vue';
import { listScene, getScene, getSceneLog, delScene, addScene, updateScene, runScene } from '@/api/iot/scene';
import { cacheJsonThingsModel } from '@/api/iot/model';
import { checkPermi } from '@/utils/permission';
import { parseTime } from '@/utils/ruoyi';
import { useDict } from '@/utils/dict';
import MonacoEditor from '@/components/MonacoEditor/index.vue';
import ThingsModelInput from '@/components/ThingsModelInput/index.vue';
import deviceList from './device-list.vue';
import productList from './product-list.vue';
import sceneListComp from './scene-list.vue';
import alertList from './alert-list.vue';
import alertConfigList from './alert-config-list.vue';

const { proxy } = getCurrentInstance() as any;
const {
  is_alert,
  trigger_condition,
  execution_method,
  trigger_type,
  variable_operation_week,
  scene_trigger_source,
  scene_operator,
  operator,
} = useDict(
  'is_alert',
  'trigger_condition',
  'execution_method',
  'trigger_type',
  'variable_operation_week',
  'scene_trigger_source',
  'scene_operator',
  'operator'
);

const confirmLoading = ref(false);
const updateBtnDisabled = ref(false);
const currentType = ref<string | null>(null);
const currentIndex = ref<number | null>(null);
const loading = ref(true);
const ids = ref<number[]>([]);
const single = ref(true);
const multiple = ref(true);
const showSearch = ref(true);
const total = ref(0);
const sceneList = ref<any[]>([]);
const title = ref('');
const open = ref(false);
const logs = ref('');
const openCron = ref(false);
const openLog = ref(false);
const logLoading = ref(false);
const expression = ref('');
const triggerIndex = ref(0);
const isDisabled = ref(false);
const tempSceneId = ref<any>(null);

const queryFormRef = ref();
const formRef = ref();
const multipleTableRef = ref();
const logContainerRef = ref();
const deviceListRef = ref();
const productListRef = ref();
const sceneListRef = ref();
const alertListRef = ref();
const alertConfigListRef = ref();

const queryParams = reactive<any>({ pageNum: 1, pageSize: 10, sceneName: null, userId: null, userName: null });

const defaultTrigger = () => ({
  productId: 0,
  productName: '',
  groupId: '',
  groupName: '',
  deviceCount: 0,
  deviceNums: [],
  source: 1,
  type: 1,
  parentId: '',
  parentName: '',
  parentModel: null,
  model: null,
  operator: '',
  id: '',
  name: '',
  value: '',
  valueA: '',
  valueB: '',
  arrayIndex: '',
  arrayIndexName: '',
  isAdvance: 0,
  cronExpression: '',
  timerTimeValue: '',
  timerWeekValue: [1, 2, 3, 4, 5, 6, 7],
  scriptPurpose: 2,
});

const defaultAction = () => ({
  productId: 0,
  productName: '',
  notifyCount: 1,
  groupId: '',
  groupName: '',
  deviceCount: 0,
  deviceNums: [],
  source: 1,
  type: 2,
  parentId: '',
  parentName: '',
  parentModel: null,
  model: null,
  id: '',
  name: '',
  value: '',
  arrayIndex: '',
  arrayIndexName: '',
  scriptPurpose: 3,
});

const formJson = reactive<any>({ triggers: [defaultTrigger()], actions: [defaultAction()] });
const form = ref<any>({});
const rules = reactive<any>({
  sceneName: [{ required: true, message: proxy.$t('scene.index.670805-59'), trigger: 'blur' }],
});

onMounted(() => {
  getList();
  if (!checkPermi(['iot:scene:edit'])) isDisabled.value = true;
});

function getList() {
  loading.value = true;
  listScene(queryParams).then((response: any) => {
    sceneList.value = response.rows;
    total.value = response.total;
    loading.value = false;
  });
}

function cancel() {
  open.value = false;
  reset();
}

function reset() {
  form.value = {
    sceneId: null,
    recoverId: null,
    recoverSceneName: null,
    sceneName: null,
    userId: null,
    userName: null,
    remark: null,
    enable: 1,
    cond: 1,
    silentPeriod: 0,
    executeMode: 1,
    executeDelay: 0,
    checkDelay: 0,
    hasAlert: 2,
    applicationName: 'fastbee',
  };
  formJson.triggers = [defaultTrigger()];
  formJson.actions = [defaultAction()];
  confirmLoading.value = false;
  updateBtnDisabled.value = false;
  tempSceneId.value = null;
  proxy.resetForm(formRef.value);
}

function valueChange(_value: any, item: any) {
  item.value = item.valueA + '~' + item.valueB;
}

function handleQuery() {
  queryParams.pageNum = 1;
  getList();
}

function handleResetQuery() {
  proxy.resetForm(queryFormRef.value);
  handleQuery();
}

function handleEnableChange(row: any) {
  isDisabled.value = true;
  setTimeout(() => {
    isDisabled.value = false;
  }, 1000);
  reset();
  getScene(row.sceneId).then((res: any) => {
    if (res.code === 200) {
      form.value = res.data;
      form.value.enable = form.value.enable == 1 ? 2 : 1;
      updateScene(form.value).then((chilRes: any) => {
        if (chilRes.code === 200) {
          getList();
          proxy.$modal.msgSuccess(proxy.$t('updateSuccess'));
        }
        open.value = false;
      });
    }
  });
}

function handleSelectionChange(selection: any[]) {
  ids.value = selection.map((item) => item.sceneId);
  single.value = selection.length !== 1;
  multiple.value = !selection.length;
}

function handleAdd() {
  reset();
  open.value = true;
  title.value = proxy.$t('scene.index.670805-62');
}

function handleUpdate(row: any) {
  reset();
  const sceneId = row.sceneId || ids.value;
  tempSceneId.value = sceneId;
  getScene(sceneId).then((response: any) => {
    form.value = response.data;
    formJson.triggers = form.value.triggers;
    for (let i = 0; i < formJson.triggers.length; i++) {
      if (formJson.triggers[i].source == 2) {
        if (formJson.triggers[i].isAdvance == 0) {
          let arrayValue = formJson.triggers[i].cronExpression
            .substring(12)
            .split(',')
            .map((s: string) => Number(s));
          formJson.triggers[i].timerWeekValue = arrayValue.map((num: number) => (num === 1 ? 7 : num - 1));
          formJson.triggers[i].timerTimeValue =
            formJson.triggers[i].cronExpression.substring(5, 7) +
            ':' +
            formJson.triggers[i].cronExpression.substring(2, 4);
        }
      } else if (formJson.triggers[i].source == 4) {
        // custom trigger - no extra processing
      } else if (formJson.triggers[i].source !== 10) {
        formatSceneScript(formJson.triggers[i], i);
      }
    }
    formJson.actions = form.value.actions;
    for (let i = 0; i < formJson.actions.length; i++) {
      formatSceneScript(formJson.actions[i], i);
    }
    setTimeout(() => {
      updateBtnDisabled.value = false;
    }, 2000);
    open.value = true;
    title.value = proxy.$t('scene.index.670805-63');
  });
}

function formatSceneScript(sceneScript: any, index: number) {
  if (sceneScript.scriptPurpose == 2) {
    cacheJsonThingsModel(sceneScript.productId).then((response: any) => {
      let data = JSON.parse(response.data);
      sceneScript.thingsModel = formatArrayIndex(data);
      const triggerValue = sceneScript.value == null ? '' : String(sceneScript.value);
      if (triggerValue.indexOf('~') != -1) {
        let values = triggerValue.split('~');
        sceneScript.valueA = values[0];
        sceneScript.valueB = values[1];
      }
      let sceneScripts: any[] = [];
      if (sceneScript.type == 1) sceneScripts = sceneScript.thingsModel.properties;
      else if (sceneScript.type == 2) sceneScripts = sceneScript.thingsModel.functions;
      else if (sceneScript.type == 3) sceneScripts = sceneScript.thingsModel.events;
      setParentAndModelData(sceneScript, sceneScripts);
      formJson.triggers[index] = { ...formJson.triggers[index] };
    });
  } else if (sceneScript.scriptPurpose == 3) {
    if (sceneScript.source == 4 || sceneScript.source == 5) return;
    cacheJsonThingsModel(sceneScript.productId).then((response: any) => {
      let data = JSON.parse(response.data);
      sceneScript.thingsModel = formatArrayIndex(data);
      if (sceneScript.thingsModel.properties) {
        sceneScript.thingsModel.properties = sceneScript.thingsModel.properties.filter(
          (item: any) => item.isMonitor == 0 && item.isReadonly == 0
        );
        for (let i = 0; i < sceneScript.thingsModel.properties.length; i++) {
          if (sceneScript.thingsModel.properties[i].datatype.params) {
            sceneScript.thingsModel.properties[i].datatype.params = sceneScript.thingsModel.properties[
              i
            ].datatype.params.filter((item: any) => item.isReadonly == 0);
          }
        }
      }
      if (sceneScript.thingsModel.functions) {
        sceneScript.thingsModel.functions = sceneScript.thingsModel.functions.filter(
          (item: any) => item.isReadonly == 0
        );
        for (let i = 0; i < sceneScript.thingsModel.functions.length; i++) {
          if (sceneScript.thingsModel.functions[i].datatype.params) {
            sceneScript.thingsModel.functions[i].datatype.params = sceneScript.thingsModel.functions[
              i
            ].datatype.params.filter((item: any) => item.isReadonly == 0);
          }
        }
      }
      let sceneScripts: any[] = [];
      if (sceneScript.type == 1) sceneScripts = sceneScript.thingsModel.properties;
      else if (sceneScript.type == 2) sceneScripts = sceneScript.thingsModel.functions;
      setParentAndModelData(sceneScript, sceneScripts);
      formJson.actions[index] = { ...formJson.actions[index] };
    });
  }
}

function setParentAndModelData(sceneScript: any, sceneScripts: any[]) {
  for (let i = 0; i < sceneScripts.length; i++) {
    if (sceneScript.parentId == sceneScripts[i].id) {
      sceneScript.parentModel = sceneScripts[i];
      if (sceneScript.parentModel.datatype.type === 'object') {
        for (let j = 0; j < sceneScript.parentModel.datatype.params.length; j++) {
          if (sceneScript.id == sceneScript.parentModel.datatype.params[j].id)
            sceneScript.model = sceneScript.parentModel.datatype.params[j];
        }
      } else if (
        sceneScript.parentModel.datatype.arrayType === 'object' &&
        sceneScript.parentModel.datatype.type === 'array'
      ) {
        if (sceneScript.id.indexOf('array_') != -1) sceneScript.id = sceneScript.id.substring(9);
        for (let j = 0; j < sceneScript.parentModel.datatype.params.length; j++) {
          if (sceneScript.id == sceneScript.parentModel.datatype.params[j].id)
            sceneScript.model = sceneScript.parentModel.datatype.params[j];
        }
      } else if (
        sceneScript.parentModel.datatype.arrayType !== 'object' &&
        sceneScript.parentModel.datatype.type === 'array'
      ) {
        if (sceneScript.id.indexOf('array_') != -1) sceneScript.id = sceneScript.id.substring(9);
        sceneScript.model = {
          datatype: {
            type: sceneScript.parentModel.datatype.arrayType,
            maxLength: -1,
            min: -1,
            max: -1,
            unit: proxy.$t('scene.index.670805-64'),
          },
        };
      } else {
        sceneScript.model = sceneScript.parentModel;
      }
      break;
    }
  }
}

function handleDelete(row: any) {
  const sceneIds = row.sceneId || ids.value;
  proxy.$modal
    .confirm(proxy.$t('scene.index.670805-65', [sceneIds]))
    .then(() => delScene(sceneIds))
    .then(() => {
      getList();
      proxy.$modal.msgSuccess(proxy.$t('scene.index.670805-66'));
      multipleTableRef.value?.clearSelection();
    })
    .catch(() => {});
}

function handleRun(data: any) {
  runScene({ sceneId: data.sceneId }).then((res: any) => {
    proxy.$modal.msgSuccess(res.msg);
  });
}

function handleLog(chainName: string) {
  logLoading.value = true;
  getSceneLog(chainName).then((response: any) => {
    logs.value = response.msg;
    form.value.chainName = chainName;
    openLog.value = true;
    title.value = proxy.$t('script.349087-40');
    logLoading.value = false;
    nextTick(() => {
      logContainerRef.value?.scroll({ top: logContainerRef.value.scrollHeight });
    });
  });
}

function cancelLog() {
  logs.value = '';
  openLog.value = false;
}

function handleTriggerSource(source: number, index: number | string) {
  const idx = Number(index);
  Object.assign(formJson.triggers[idx], {
    deviceCount: 0,
    productId: '',
    productName: '',
    groupId: '',
    groupName: '',
    thingsModel: null,
    id: '',
    name: '',
    value: '',
    valueA: '',
    valueB: '',
    parentId: '',
    parentName: '',
    model: null,
    parentModel: null,
    operator: '',
    deviceNums: [],
    timerTimeValue: '',
    timerWeekValue: [1, 2, 3, 4, 5, 6, 7],
  });
}

function handleSelectDevice(type: string, item: any, index: number | string) {
  const _idxNum = Number(index);
  currentType.value = type;
  currentIndex.value = _idxNum;
  deviceListRef.value.queryParams.pageNum = 1;
  deviceListRef.value.openDeviceList = true;
  deviceListRef.value.selectDeviceNums = item.deviceNums;
  deviceListRef.value.productId = item.productId;
  deviceListRef.value.productName = item.productName;
  deviceListRef.value.queryParams.groupId = item.groupId || '';
  deviceListRef.value.queryParams.productId = item.productId;
  deviceListRef.value.getList();
}

function handleSelectProduct(type: string, item: any, index: number | string) {
  const _idxNum2 = Number(index);
  currentType.value = type;
  currentIndex.value = _idxNum2;
  productListRef.value.queryParams.pageNum = 1;
  productListRef.value.open = true;
  productListRef.value.selectProductId = item.productId;
  productListRef.value.getList();
}

function handleSelectRecoverScenes(type: string, item: any, index: number | string) {
  const _idxNum3 = Number(index);
  currentType.value = type;
  currentIndex.value = _idxNum3;
  sceneListRef.value.queryParams.pageNum = 1;
  sceneListRef.value.sceneId = form.value.sceneId;
  sceneListRef.value.openScene = true;
  sceneListRef.value.selectSceneId = item.id;
  sceneListRef.value.getList();
}

function getSelectProductDevice(data: any, type: number) {
  if (currentType.value == null || currentIndex.value == null) return;
  const idx = currentIndex.value;
  if (currentType.value == 'trigger') {
    if (type == 1) {
      Object.assign(formJson.triggers[idx], {
        deviceNums: data.deviceNums,
        deviceCount: data.deviceNums.length,
        productId: data.productId,
        productName: data.productName,
        groupId: data.groupId,
      });
    } else if (type == 2) {
      Object.assign(formJson.triggers[idx], {
        deviceNums: [],
        deviceCount: 0,
        productId: data.productId,
        productName: data.productName,
      });
    }
    cacheJsonThingsModel(data.productId).then((response: any) => {
      let d = JSON.parse(response.data);
      formJson.triggers[idx].thingsModel = formatArrayIndex(d);
      Object.assign(formJson.triggers[idx], {
        type: 1,
        parentId: '',
        parentName: '',
        parentModel: null,
        model: null,
        operator: '',
        id: '',
        name: '',
        value: '',
        arrayIndex: '',
        arrayIndexName: '',
      });
      formJson.triggers[idx] = { ...formJson.triggers[idx] };
    });
  } else if (currentType.value == 'action') {
    if (type == 1) {
      Object.assign(formJson.actions[idx], {
        deviceNums: data.deviceNums,
        deviceCount: data.deviceNums.length,
        productId: data.productId,
        productName: data.productName,
        groupId: data.groupId,
      });
    } else if (type == 2) {
      Object.assign(formJson.actions[idx], {
        deviceNums: [],
        deviceCount: 0,
        productId: data.productId,
        productName: data.productName,
      });
    }
    cacheJsonThingsModel(data.productId).then((response: any) => {
      let d = JSON.parse(response.data);
      formJson.actions[idx].thingsModel = formatArrayIndex(d);
      Object.assign(formJson.actions[idx], {
        type: 1,
        parentId: '',
        parentModel: null,
        parentName: '',
        model: null,
        operator: '',
        id: '',
        name: '',
        value: '',
        arrayIndex: '',
        arrayIndexName: '',
      });
      // Filter readonly for actions
      if (formJson.actions[idx].thingsModel.properties) {
        formJson.actions[idx].thingsModel.properties = formJson.actions[idx].thingsModel.properties.filter(
          (item: any) => item.isMonitor == 0 && item.isReadonly == 0
        );
        for (let i = 0; i < formJson.actions[idx].thingsModel.properties.length; i++) {
          if (formJson.actions[idx].thingsModel.properties[i].datatype.params) {
            formJson.actions[idx].thingsModel.properties[i].datatype.params = formJson.actions[
              idx
            ].thingsModel.properties[i].datatype.params.filter((it: any) => it.isMonitor == 0 && it.isReadonly == 0);
          }
        }
      }
      if (formJson.actions[idx].thingsModel.functions) {
        formJson.actions[idx].thingsModel.functions = formJson.actions[idx].thingsModel.functions.filter(
          (item: any) => item.isReadonly == 0
        );
        for (let i = 0; i < formJson.actions[idx].thingsModel.functions.length; i++) {
          if (formJson.actions[idx].thingsModel.functions[i].datatype.params) {
            formJson.actions[idx].thingsModel.functions[i].datatype.params = formJson.actions[
              idx
            ].thingsModel.functions[i].datatype.params.filter((it: any) => it.isMonitor == 0 && it.isReadonly == 0);
          }
        }
      }
      formJson.actions[idx] = { ...formJson.actions[idx] };
    });
  }
}

function getSceneData(data: any) {
  if (currentIndex.value == null) return;
  formJson.actions[currentIndex.value].id = data.sceneId;
  formJson.actions[currentIndex.value].name = data.sceneName;
}

function formatArrayIndex(data: any) {
  let obj = { ...data };
  for (let o in obj) {
    obj[o] = obj[o].map((item: any) => {
      if (item.datatype.type === 'array') {
        let arrayModel: any[] = [];
        for (let k = 0; k < item.datatype.arrayCount; k++) {
          let index = k > 9 ? String(k) : '0' + k;
          arrayModel.push({ id: index, name: item.name + ' ' + (k + 1) });
        }
        item.datatype.arrayModel = arrayModel;
      }
      return item;
    });
  }
  return obj;
}

function handleTriggerTypeChange(_source: any, index: number | string) {
  const _ttIdx = Number(index);
  Object.assign(formJson.triggers[_ttIdx], {
    id: '',
    name: '',
    model: null,
    operator: '',
    value: '',
    valueA: '',
    valueB: '',
    parentId: '',
    parentName: '',
    parentModel: null,
    arrayIndex: '',
    arrayIndexName: '',
  });
}

function handleTriggerParentModelChange(identifier: any, index: number | string) {
  index = Number(index);
  Object.assign(formJson.triggers[index], {
    operator: '',
    value: '',
    valueA: '',
    valueB: '',
    arrayIndex: '',
    arrayIndexName: '',
    model: null,
  });
  let sceneScripts: any[] = [];
  if (formJson.triggers[index].type == 1) sceneScripts = formJson.triggers[index].thingsModel.properties;
  else if (formJson.triggers[index].type == 2) sceneScripts = formJson.triggers[index].thingsModel.functions;
  else if (formJson.triggers[index].type == 3) sceneScripts = formJson.triggers[index].thingsModel.events;
  for (let i = 0; i < sceneScripts.length; i++) {
    if (sceneScripts[i].id == identifier) {
      formJson.triggers[index].parentName = sceneScripts[i].name;
      formJson.triggers[index].parentModel = sceneScripts[i];
      if (sceneScripts[i].datatype.type === 'object') {
        formJson.triggers[index].id = '';
        formJson.triggers[index].name = '';
      } else if (sceneScripts[i].datatype.type === 'array' && sceneScripts[i].datatype.arrayType === 'object') {
        formJson.triggers[index].id = '';
        formJson.triggers[index].name = '';
      } else if (sceneScripts[i].datatype.type === 'array' && sceneScripts[i].datatype.arrayType !== 'object') {
        formJson.triggers[index].id = sceneScripts[i].id;
        formJson.triggers[index].name = sceneScripts[i].name;
        formJson.triggers[index].model = {
          datatype: {
            type: formJson.triggers[index].parentModel.datatype.arrayType,
            maxLength: -1,
            min: -1,
            max: -1,
            unit: proxy.$t('scene.index.670805-64'),
          },
        };
      } else {
        formJson.triggers[index].id = sceneScripts[i].id;
        formJson.triggers[index].name = sceneScripts[i].name;
        formJson.triggers[index].model = sceneScripts[i];
      }
      break;
    }
  }
}

function handleTriggerIndexChange(id: any, index: number | string) {
  index = Number(index);
  formJson.triggers[index].arrayIndexName = formJson.triggers[index].parentModel.datatype.arrayModel.find(
    (x: any) => x.id == id
  ).name;
  Object.assign(formJson.triggers[index], { value: '', valueA: '', valueB: '', operator: '' });
  if (formJson.triggers[index].parentModel.datatype.arrayType === 'object') {
    formJson.triggers[index].id = '';
    formJson.triggers[index].name = '';
  }
}

function handleTriggerModelChange(id: any, index: number | string) {
  index = Number(index);
  Object.assign(formJson.triggers[index], { operator: '', value: '', valueA: '', valueB: '' });
  if (
    formJson.triggers[index].parentModel.datatype.type === 'array' ||
    formJson.triggers[index].parentModel.datatype.type === 'object'
  ) {
    let model = formJson.triggers[index].parentModel.datatype.params.find((item: any) => item.id == id);
    formJson.triggers[index].name = model.name;
    formJson.triggers[index].model = model;
  }
}

function handleAddTrigger() {
  formJson.triggers.push({ ...defaultTrigger(), scriptPurpose: 2 });
}
function handleAddAction() {
  formJson.actions.push({ ...defaultAction(), scriptPurpose: 3 });
}
function handleRemoveTrigger(index: number | string) {
  formJson.triggers.splice(Number(index), 1);
}
function handleRemoveAction(index: number | string) {
  formJson.actions.splice(Number(index), 1);
}

function handleShowCron(item: any, index: number | string) {
  const _cronIdx = Number(index);
  expression.value = item.cronExpression;
  triggerIndex.value = _cronIdx;
  openCron.value = true;
}
function crontabFill(value: string) {
  formJson.triggers[triggerIndex.value].cronExpression = value;
}
function weekChange(_data: any, index: number | string) {
  gentCronExpression(index);
}
function timeChange(_data: any, index: number | string) {
  gentCronExpression(index);
}
function customerCronChange(_data: any, _index: number | string) {}

function gentCronExpression(index: number | string) {
  const _gIdx = Number(index);
  index = _gIdx;
  let hour = '00',
    minute = '00';
  if (formJson.triggers[index].timerTimeValue) {
    hour = formJson.triggers[index].timerTimeValue.substring(0, 2);
    minute = formJson.triggers[index].timerTimeValue.substring(3);
  }
  let week: any = '*';
  if (formJson.triggers[index].timerWeekValue.length > 0) {
    let order: number[] = formJson.triggers[index].timerWeekValue.slice().sort((a: number, b: number) => a - b);
    for (let i = 0; i < order.length; i++) {
      order[i] = order[i] != 7 ? order[i] + 1 : 1;
    }
    week = order;
  }
  formJson.triggers[index].cronExpression = '0 ' + minute + ' ' + hour + ' ? * ' + week;
}

function handleActionSourceChange(_source: any, index: number | string) {
  index = Number(index);
  Object.assign(formJson.actions[index], {
    deviceCount: 0,
    productId: '',
    productName: '',
    thingsModel: null,
    id: '',
    name: '',
    value: '',
    valueA: '',
    valueB: '',
    parentId: '',
    parentName: '',
    model: null,
    parentModel: null,
    operator: '',
    deviceNums: [],
    timerTimeValue: '',
    timerWeekValue: [1, 2, 3, 4, 5, 6, 7],
  });
}

function handleActionTypeChange(_data: any, index: number | string) {
  index = Number(index);
  Object.assign(formJson.actions[index], {
    id: '',
    name: '',
    value: '',
    model: null,
    parentId: '',
    parentName: '',
    arrayIndex: '',
    arrayIndexName: '',
    parentModel: null,
  });
}

function handleActionParentModelChange(identifier: any, index: number | string) {
  index = Number(index);
  Object.assign(formJson.actions[index], { model: null, value: '', arrayIndex: '', arrayIndexName: '' });
  let sceneScripts: any[] = [];
  if (formJson.actions[index].type == 1) sceneScripts = formJson.actions[index].thingsModel.properties;
  else if (formJson.actions[index].type == 2) sceneScripts = formJson.actions[index].thingsModel.functions;
  for (let i = 0; i < sceneScripts.length; i++) {
    if (sceneScripts[i].id == identifier) {
      formJson.actions[index].parentName = sceneScripts[i].name;
      formJson.actions[index].parentModel = sceneScripts[i];
      if (sceneScripts[i].datatype.type === 'object') {
        formJson.actions[index].id = '';
        formJson.actions[index].name = '';
      } else if (sceneScripts[i].datatype.type === 'array' && sceneScripts[i].datatype.arrayType === 'object') {
        formJson.actions[index].id = '';
        formJson.actions[index].name = '';
      } else if (sceneScripts[i].datatype.type === 'array' && sceneScripts[i].datatype.arrayType !== 'object') {
        formJson.actions[index].id = sceneScripts[i].id;
        formJson.actions[index].name = sceneScripts[i].name;
        formJson.actions[index].model = {
          datatype: {
            type: formJson.actions[index].parentModel.datatype.arrayType,
            maxLength: -1,
            min: -1,
            max: -1,
            unit: proxy.$t('scene.index.670805-64'),
          },
        };
      } else {
        formJson.actions[index].id = sceneScripts[i].id;
        formJson.actions[index].name = sceneScripts[i].name;
        formJson.actions[index].model = sceneScripts[i];
      }
      break;
    }
  }
}

function handleActionIndexChange(id: any, index: number | string) {
  index = Number(index);
  formJson.actions[index].arrayIndexName = formJson.actions[index].parentModel.datatype.arrayModel.find(
    (x: any) => x.id == id
  ).name;
  Object.assign(formJson.actions[index], { value: '', valueA: '', valueB: '', operator: '' });
  if (formJson.actions[index].parentModel.datatype.arrayType === 'object') {
    formJson.actions[index].id = '';
    formJson.actions[index].name = '';
  }
}

function handleActionModelChange(id: any, index: number | string) {
  index = Number(index);
  Object.assign(formJson.actions[index], { operator: '', value: '' });
  if (
    formJson.actions[index].parentModel.datatype.type === 'array' ||
    formJson.actions[index].parentModel.datatype.type === 'object'
  ) {
    let model = formJson.actions[index].parentModel.datatype.params.find((item: any) => item.id == id);
    formJson.actions[index].name = model.name;
    formJson.actions[index].model = model;
  }
}

function handleSubmitForm() {
  formRef.value.validate((valid: boolean) => {
    if (!valid) return;
    let triggers: any[] = [],
      actions: any[] = [];
    for (let i = 0; i < formJson.triggers.length; i++) {
      if (formJson.triggers[i].type == 1 || formJson.triggers[i].type == 2 || formJson.triggers[i].type == 3) {
        if (formJson.triggers[i].source == 1 && formJson.triggers[i].value == '') {
          proxy.$modal.alertError(proxy.$t('scene.index.670805-67'));
          return;
        }
        const triggerValue = formJson.triggers[i].value == null ? '' : String(formJson.triggers[i].value);
        if (formJson.triggers[i].source == 1 && triggerValue.indexOf('~') != -1) {
          if (formJson.triggers[i].valueA == '' || formJson.triggers[i].valueB == '') {
            proxy.$modal.alertError(proxy.$t('scene.index.670805-68'));
            return;
          }
        }
        if (formJson.triggers[i].source == 2) {
          if (formJson.triggers[i].isAdvance == 0) {
            if (!formJson.triggers[i].timerTimeValue) {
              proxy.$modal.alertError(proxy.$t('scene.index.670805-69'));
              return;
            }
            if (!formJson.triggers[i].timerWeekValue || formJson.triggers[i].timerWeekValue == '') {
              proxy.$modal.alertError(proxy.$t('scene.index.670805-70'));
              return;
            }
          } else if (formJson.triggers[i].isAdvance == 1 && formJson.triggers[i].cronExpression == '') {
            proxy.$modal.alertError(proxy.$t('scene.index.670805-71'));
            return;
          }
        }
      }
      let item = formJson.triggers[i];
      let id = item.id;
      if (item.arrayIndex != '') id = 'array_' + item.arrayIndex + '_' + item.id;
      triggers[i] = {
        productId: item.productId,
        productName: item.productName,
        groupId: item.groupId,
        deviceNums: item.deviceNums,
        deviceCount: item.deviceCount,
        source: item.source,
        type: item.type,
        id: id,
        name: item.name,
        operator: item.operator,
        value: item.value,
        isAdvance: item.isAdvance,
        cronExpression: item.cronExpression,
        parentId: item.parentId,
        parentName: item.parentName,
        arrayIndex: item.arrayIndex,
        arrayIndexName: item.arrayIndexName,
        scriptPurpose: 2,
      };
    }
    for (let i = 0; i < formJson.actions.length; i++) {
      if (formJson.actions[i].value === '' && formJson.actions[i].source !== 4 && formJson.actions[i].source !== 5) {
        proxy.$modal.alertError(proxy.$t('scene.index.670805-72'));
        return;
      }
      if (
        formJson.actions[i].source == 4 &&
        (!formJson.actions[i].notifyCount || formJson.actions[i].notifyCount == 0)
      ) {
        proxy.$modal.alertError(proxy.$t('scene.edit.202832-84'));
        return;
      }
      let item = formJson.actions[i];
      let id = item.id;
      if (item.arrayIndex != '') id = 'array_' + item.arrayIndex + '_' + item.id;
      actions[i] = {
        productId: item.productId,
        productName: item.productName,
        groupId: item.groupId,
        deviceCount: item.deviceCount,
        source: item.source,
        deviceNums: item.deviceNums,
        type: item.type,
        id: id,
        name: item.name,
        value: item.value,
        parentId: item.parentId,
        parentName: item.parentName,
        arrayIndex: item.arrayIndex,
        arrayIndexName: item.arrayIndexName,
        scriptPurpose: 3,
        notifyCount: item.notifyCount,
      };
    }
    if (actions.filter((x) => x.source === 4).length > 1) {
      proxy.$modal.alertError(proxy.$t('scene.edit.202832-85'));
      return;
    }
    form.value.hasAlert = actions.filter((x) => x.source === 4).length > 0 ? 1 : 2;
    form.value.triggers = triggers;
    form.value.actions = actions;
    confirmLoading.value = true;
    if (form.value.sceneId != null) {
      updateScene(form.value).then((res: any) => {
        if (res.code === 200) {
          proxy.$modal.msgSuccess(proxy.$t('scene.index.670805-73'));
          getList();
        }
        open.value = false;
        confirmLoading.value = false;
      });
    } else {
      addScene(form.value).then((res: any) => {
        if (res.code === 200) {
          proxy.$modal.msgSuccess(proxy.$t('scene.index.670805-74'));
          getList();
        }
        open.value = false;
        confirmLoading.value = false;
      });
    }
  });
}

function getRowKeys(row: any) {
  return row.sceneId;
}

function openAlertList() {
  if (tempSceneId.value) {
    nextTick(() => {
      alertListRef.value.openList = true;
      alertListRef.value.queryParams.sceneId = tempSceneId.value;
      alertListRef.value.getList();
    });
  } else {
    nextTick(() => {
      alertConfigListRef.value.openConfigList = true;
      alertConfigListRef.value.getList();
    });
  }
}

function getBindData(data: any) {
  form.value.alertIds = data;
}
function getBindConfigData(data: any) {
  form.value.alertIds = data;
}
</script>

<style lang="scss" scoped>
.iot-scene {
  padding: 20px;
  .search-card {
    margin-bottom: 15px;
    padding: 3px 0;
  }
  .search-form {
    margin-bottom: -22.5px;
  }
}
.scene-config-dialog {
  .condition-wrap,
  .action-wrap {
    position: relative;
    .trigger-type-wrap {
      margin-bottom: 10px;
      :deep(.el-input__inner) {
        box-shadow: none;
      }
    }
    .item-wrap {
      margin-bottom: 15px;
      padding: 10px;
      background-color: #d9e5f6;
      border-radius: 5px;
      width: 780px;
      .el-row + .el-row {
        margin-top: 10px;
      }
      .delete-wrap {
        position: absolute;
        right: 10px;
        top: 0;
      }
      .unit-wrap {
        display: inline-block;
        background-color: rgb(245, 247, 250);
        color: rgb(144, 147, 153);
        border: 1px solid rgb(220, 223, 230);
        border-radius: 4px;
        font-size: 13px;
        text-align: center;
        width: 83px;
        height: 32px;
        white-space: nowrap;
        overflow: hidden;
        text-overflow: ellipsis;
        line-height: 32px;
        padding: 0 5px;
      }
      .link-style {
        font-size: 20px;
        cursor: pointer;
        position: relative;
        top: 3px;
        right: 15px;
      }
    }
  }
}
</style>
