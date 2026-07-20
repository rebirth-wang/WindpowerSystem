<template>
  <div class="device-edit">
    <el-card class="top-card" :body-style="{ padding: '26px 20px' }">
      <div class="title-wrap">
        <el-button class="top-button" type="info" size="small" @click="goBack()">
          <el-icon><ArrowLeft /></el-icon>
          {{ $t('product.product-edit.473153-44') }}
        </el-button>
        <span class="info-item">{{ $t('device.device-edit.148398-1') }}：{{ form.deviceName }}</span>
        <span class="info-item">{{ $t('device.device-edit.148398-7') }}：{{ form.serialNumber }}</span>
        <span class="info-item" v-if="!!form.status">
          {{ $t('device.device-edit.148398-83') }}：
          <span class="status" :style="{ color: statusColor }">
            <span class="dot" :style="{ backgroundColor: statusColor }"></span>
            <span v-if="form.status == 1">{{ $t('home.notActive') }}</span>
            <span v-if="form.status == 2">{{ $t('home.disabled') }}</span>
            <span v-if="form.status == 3">{{ $t('home.onLine') }}</span>
            <span v-if="form.status == 4">{{ $t('home.offline') }}</span>
          </span>
        </span>
        <span class="info-item">{{ $t('device.device-edit.148398-4') }}：{{ form.productName }}</span>
        <el-button
          v-if="isGb28181Device && form.deviceId != 0"
          class="diagnostics-button"
          type="warning"
          size="small"
          plain
          :icon="Warning"
          :loading="accessDiagnosticsLoading"
          @click="openAccessDiagnostics"
        >
          {{ $t('device.device-edit.148398-113') }}
        </el-button>
      </div>
    </el-card>

    <el-card style="padding-bottom: 100px">
      <el-tabs
        id="deviceDetailTab"
        class="custom-tabs"
        v-model="activeName"
        tab-position="top"
        @tab-click="tabChange"
        style="min-height: 400px"
        lazy
      >
        <el-tab-pane name="basic">
          <template #label>{{ $t('device.device-edit.148398-0') }}</template>
          <el-form class="basic-span" ref="formRef" :model="form" :rules="rules" label-width="100px">
            <el-row :gutter="100">
              <el-col :xs="24" :sm="24" :md="24" :lg="12" :xl="8">
                <el-form-item :label="$t('device.device-edit.148398-1')" prop="deviceName">
                  <el-input v-model="form.deviceName" :placeholder="$t('device.device-edit.148398-2')">
                    <template #append v-if="form.deviceId != 0">
                      <el-button @click="openSummaryDialog">{{ $t('device.device-edit.148398-3') }}</el-button>
                    </template>
                  </el-input>
                </el-form-item>
                <el-form-item :label="$t('device.device-edit.148398-4')" prop="productName">
                  <el-input
                    readonly
                    v-model="form.productName"
                    :placeholder="$t('device.device-edit.148398-5')"
                    :disabled="form.status != 1"
                  >
                    <template #append>
                      <el-button @click="selectProduct()" :disabled="form.status != 1">
                        {{ $t('device.device-edit.148398-6') }}
                      </el-button>
                    </template>
                  </el-input>
                </el-form-item>
                <!-- 设备编号 -->
                <el-form-item :label="$t('device.device-edit.148398-7')" prop="serialNumber">
                  <el-input
                    v-model="form.serialNumber"
                    :placeholder="$t('device.device-edit.148398-8')"
                    maxlength="32"
                    :disabled="form.status != 1"
                    :readonly="isGb28181Device"
                  >
                    <template #append v-if="!isGb28181Device">
                      <el-button
                        @click="generateNum"
                        :loading="genDisabled"
                        :disabled="form.status != 1"
                        v-hasPermi="['iot:device:add']"
                      >
                        {{ $t('device.device-edit.148398-9') }}
                      </el-button>
                    </template>
                    <template #append v-if="isGb28181Device">
                      <el-button @click="genSipID()" :disabled="form.status != 1" v-hasPermi="['iot:device:add']">
                        {{ $t('device.device-edit.148398-9') }}
                      </el-button>
                    </template>
                  </el-input>
                </el-form-item>
                <!-- modbus主机 -->
                <el-form-item
                  :label="$t('device.device-edit.148398-103')"
                  prop="deviceIp"
                  v-if="form.protocolCode === 'MODBUS-TCP' && form.deviceType !== 4"
                >
                  <el-input v-model="form.deviceIp" placeholder="127.0.0.1" :disabled="form.status != 1"></el-input>
                </el-form-item>
                <el-form-item
                  label=""
                  prop="devicePort"
                  v-if="form.protocolCode === 'MODBUS-TCP' && (form.deviceType === 1 || form.deviceType === 2)"
                >
                  <template #label>{{ $t('device.device-edit.148398-104') }}</template>
                  <el-input
                    v-model="form.devicePort"
                    placeholder="502"
                    maxlength="32"
                    :readonly="form.deviceType === 3"
                    :disabled="form.status != 1"
                  ></el-input>
                </el-form-item>
                <el-form-item :label="$t('device.device-edit.148398-12')" prop="firmwareVersion">
                  <el-input
                    v-model="form.firmwareVersion"
                    :placeholder="$t('device.device-edit.148398-13')"
                    :readonly="form.status != 1 || form.deviceType === 3"
                  >
                    <template #prepend>Version</template>
                    <template #append>
                      {{ form.firmwareType === 1 ? $t('firmware.index.222541-52') : $t('firmware.index.222541-53') }}
                    </template>
                  </el-input>
                </el-form-item>
                <!-- 设备影子 -->
                <el-form-item v-if="form.deviceType !== 3" :label="$t('device.device-edit.148398-15')" prop="isShadow">
                  <el-radio-group v-model="form.isShadow">
                    <el-radio v-for="dict in device_shadow" :key="dict.value" :value="Number(dict.value)">
                      {{ dict.label }}
                    </el-radio>
                  </el-radio-group>
                </el-form-item>
                <el-form-item :label="$t('device.device-edit.148398-32')" prop="rssi">
                  <el-input v-model="form.rssi" :placeholder="$t('device.device-edit.148398-33')" readonly />
                </el-form-item>
                <el-form-item v-if="form.iccid" :label="$t('device.device-edit.148398-110')" prop="iccid">
                  <el-input v-model="form.iccid" :placeholder="$t('device.device-edit.148398-110')" readonly />
                </el-form-item>
                <el-form-item :label="$t('device.device-edit.148398-17')" prop="remark">
                  <el-input
                    v-model="form.remark"
                    type="textarea"
                    :autosize="{ minRows: 3, maxRows: 5 }"
                    :placeholder="$t('device.device-edit.148398-18')"
                  />
                </el-form-item>
                <!-- 扩展信息 -->
                <template v-if="form.extParamList?.length">
                  <!-- <el-form-item :label="$t('device.device-edit.148398-111')"></el-form-item> -->

                  <el-form-item
                    v-for="(item, index) in form.extParamList.filter((_, i) => i % 2 === 0)"
                    :key="item.paramId || index"
                    :label="item.paramName"
                  >
                    <el-input
                      v-if="item.paramType === 'text'"
                      v-model="item.paramValue"
                      :placeholder="$t('device.device-edit.148398-112')"
                    />
                    <el-input-number
                      v-else-if="item.paramType === 'number'"
                      v-model="item.paramValue"
                      :precision="getExtPrecision(item)"
                      :placeholder="$t('device.device-edit.148398-112')"
                      style="width: 100%"
                    />
                    <el-switch
                      v-else-if="item.paramType === 'switch'"
                      v-model="item.paramValue"
                      active-value="1"
                      inactive-value="0"
                    />
                    <el-select
                      v-else-if="item.paramType === 'select'"
                      v-model="item.paramValue"
                      :placeholder="$t('device.device-edit.148398-112')"
                      style="width: 100%"
                    >
                      <el-option v-for="(op, i) in getExtOptions(item)" :key="i" :label="op.label" :value="op.value" />
                    </el-select>
                    <el-date-picker
                      v-else-if="item.paramType === 'date'"
                      v-model="item.paramValue"
                      type="date"
                      value-format="YYYY-MM-DD"
                      :placeholder="$t('device.device-edit.148398-112')"
                      style="width: 100%"
                    />
                    <el-time-picker
                      v-else-if="item.paramType === 'time'"
                      v-model="item.paramValue"
                      value-format="HH:mm:ss"
                      :placeholder="$t('device.device-edit.148398-112')"
                      style="width: 100%"
                    />
                  </el-form-item>
                </template>
              </el-col>
              <el-col :xs="24" :sm="24" :md="24" :lg="12" :xl="8">
                <el-form-item :label="$t('device.device-edit.148398-19')" prop="locationWay">
                  <el-select
                    v-model="form.locationWay"
                    :placeholder="$t('device.device-edit.148398-20')"
                    clearable
                    style="width: 100%"
                  >
                    <el-option
                      v-for="dict in iot_location_way"
                      :key="dict.value"
                      :label="dict.label"
                      :value="Number(dict.value)"
                    />
                  </el-select>
                </el-form-item>
                <el-form-item :label="$t('device.device-edit.148398-21')" prop="longitude">
                  <el-input
                    v-model="form.longitude"
                    :placeholder="$t('device.device-edit.148398-22')"
                    type="number"
                    :disabled="form.locationWay !== 3"
                  >
                    <template #append>
                      <el-link
                        underline="never"
                        href="https://api.map.baidu.com/lbsapi/getpoint/index.html"
                        target="_blank"
                        :disabled="form.locationWay != 3"
                      >
                        {{ $t('device.device-edit.148398-23') }}
                      </el-link>
                    </template>
                  </el-input>
                </el-form-item>
                <el-form-item :label="$t('device.device-edit.148398-24')" prop="latitude">
                  <el-input
                    v-model="form.latitude"
                    :placeholder="$t('device.device-edit.148398-25')"
                    type="number"
                    :disabled="form.locationWay !== 3"
                  >
                    <template #append>
                      <el-link
                        underline="never"
                        href="https://api.map.baidu.com/lbsapi/getpoint/index.html"
                        target="_blank"
                        :disabled="form.locationWay != 3"
                      >
                        {{ $t('device.device-edit.148398-23') }}
                      </el-link>
                    </template>
                  </el-input>
                </el-form-item>
                <el-form-item :label="$t('device.device-edit.148398-26')" prop="networkAddress">
                  <el-input
                    v-model="form.networkAddress"
                    :placeholder="$t('device.device-edit.148398-27')"
                    :disabled="form.locationWay !== 3"
                  />
                </el-form-item>
                <el-form-item :label="$t('device.device-edit.148398-28')" prop="networkIp">
                  <el-input v-model="form.networkIp" :placeholder="$t('device.device-edit.148398-29')" readonly />
                </el-form-item>
                <el-form-item :label="$t('device.device-edit.148398-30')" prop="activeTime">
                  <el-date-picker
                    v-model="form.activeTime"
                    type="date"
                    value-format="YYYY-MM-DD"
                    :placeholder="$t('device.device-edit.148398-31')"
                    readonly
                    style="width: 100%"
                  ></el-date-picker>
                </el-form-item>
                <el-form-item
                  v-if="form.transport === 'MQTT' && form.status !== 1"
                  :label="$t('device.device-edit.148398-16')"
                  prop="deviceStatus"
                >
                  <el-switch
                    v-model="deviceStatus"
                    active-text=""
                    inactive-text=""
                    :active-value="1"
                    :inactive-value="0"
                  ></el-switch>
                </el-form-item>
                <el-form-item :label="$t('device.device-edit.148398-34')" v-if="form.deviceId != 0">
                  <el-button
                    size="small"
                    @click="handleViewMqtt()"
                    :disabled="
                      form.transport !== 'MQTT' &&
                      form.transport !== 'TCP' &&
                      form.transport !== 'HTTP' &&
                      form.transport !== 'COAP' &&
                      form.transport !== 'GB28181'
                    "
                  >
                    {{ $t('device.device-edit.148398-35') }}
                  </el-button>
                  <el-tooltip :content="$t('device.index.105953-62')" placement="top" v-if="form.isOwner !== 1">
                    <el-button size="small" :disabled="form.isOwner !== 1" @click="openCodeDialog()">
                      {{ $t('device.device-edit.148398-36') }}
                    </el-button>
                  </el-tooltip>
                  <el-button size="small" :disabled="form.isOwner !== 1" @click="openCodeDialog()" v-else>
                    {{ $t('device.device-edit.148398-36') }}
                  </el-button>
                </el-form-item>
                <!-- 扩展参数 -->
                <template v-if="form.extParamList?.length > 1">
                  <el-form-item
                    v-for="(item, index) in form.extParamList.filter((_, i) => i % 2 === 1)"
                    :key="(item.paramId || index) + '-r'"
                    :label="item.paramName"
                  >
                    <el-input
                      v-if="item.paramType === 'text'"
                      v-model="item.paramValue"
                      :placeholder="$t('device.device-edit.148398-112')"
                    />
                    <el-input-number
                      v-else-if="item.paramType === 'number'"
                      v-model="item.paramValue"
                      :precision="getExtPrecision(item)"
                      :placeholder="$t('device.device-edit.148398-112')"
                      style="width: 100%"
                    />
                    <el-switch
                      v-else-if="item.paramType === 'switch'"
                      v-model="item.paramValue"
                      active-value="1"
                      inactive-value="0"
                    />
                    <el-select
                      v-else-if="item.paramType === 'select'"
                      v-model="item.paramValue"
                      :placeholder="$t('pleaseSelect')"
                      style="width: 100%"
                    >
                      <el-option v-for="(op, i) in getExtOptions(item)" :key="i" :label="op.label" :value="op.value" />
                    </el-select>
                    <el-date-picker
                      v-else-if="item.paramType === 'date'"
                      v-model="item.paramValue"
                      type="date"
                      value-format="YYYY-MM-DD"
                      :placeholder="$t('device.device-edit.148398-112')"
                      style="width: 100%"
                    />
                    <el-time-picker
                      v-else-if="item.paramType === 'time'"
                      v-model="item.paramValue"
                      value-format="HH:mm:ss"
                      :placeholder="$t('device.device-edit.148398-112')"
                      style="width: 100%"
                    />
                  </el-form-item>
                </template>
              </el-col>

              <el-col :xs="24" :sm="24" :md="24" :lg="12" :xl="8">
                <div style="border: 1px solid #dfe4ed; border-radius: 5px; padding: 5px; margin-left: 20px">
                  <div id="map" style="height: 435px; width: 100%">{{ $t('device.device-edit.148398-37') }}</div>
                </div>
              </el-col>
            </el-row>
          </el-form>

          <div class="submit-btn-wrap">
            <el-button
              type="primary"
              @click="handleSubmitForm"
              v-hasPermi="['iot:device:edit']"
              v-show="form.deviceId != 0"
            >
              {{ $t('update') }}
            </el-button>
          </div>
          <!-- 选择产品 -->
          <product-list ref="productListRef" :productId="form.productId" @productEvent="getProductData($event)" />
          <sipid ref="sipidGenRef" :product="form" @addGenEvent="getSipIDData($event)" />
        </el-tab-pane>

        <el-tab-pane name="runningStatus" v-if="form.deviceType !== 3" lazy>
          <template #label>{{ $t('device.device-edit.148398-42') }}</template>
          <!-- <real-time-status ref="realTimeStatus" :device="form" @statusEvent="getDeviceStatusData($event)" /> -->
          <running-status ref="runningStatusRef" :device="form" @statusEvent="getDeviceStatusData($event)" />
        </el-tab-pane>
        <el-tab-pane name="variable" :disabled="form.deviceId == 0" v-if="form.deviceType !== 3" lazy>
          <template #label>{{ $t('device.device-edit.148398-74') }}</template>
          <device-variable ref="deviceVariableRef" :device="form" />
        </el-tab-pane>
        <el-tab-pane
          name="instructionParsing"
          :disabled="form.deviceId == 0"
          v-if="form.deviceType !== 3 && isPasDataDebug"
          lazy
        >
          <template #label>{{ $t('device.device-edit.148398-76') }}</template>
          <instruction-parsing ref="instructionParsingRef" :device="form" />
        </el-tab-pane>
        <el-tab-pane
          name="deviceSub"
          :disabled="form.deviceId == 0"
          v-if="
            form.deviceType == 2 &&
            [
              'MODBUS-RTU',
              'MODBUS-TCP',
              'MODBUS-TCP-OVER-RTU',
              'MODBUS-JSON-HP',
              'MODBUS-JSON-ZQWL',
              'JSON-GATEWAY',
            ].includes(form.protocolCode)
          "
          lazy
        >
          <template #label>{{ $t('device.device-edit.148398-43') }}</template>
          <device-sub ref="deviceSubRef" :device="form" />
        </el-tab-pane>

        <!-- 轮询任务 -->
        <el-tab-pane name="deviceModbusTask" :disabled="form.deviceId == 0" v-if="form.canConfigPoll" lazy>
          <template #label>{{ $t('device.device-edit.148398-77') }}</template>
          <device-modbus-task ref="deviceModbusTaskRef" :device="form" />
        </el-tab-pane>
        <!-- 指令下发 -->
        <el-tab-pane
          name="deviceInstructionsConfig"
          v-if="(form.deviceType === 1 || form.deviceType === 2) && form.protocolCode == 'MODBUS-TCP'"
          lazy
        >
          <template #label>{{ $t('device.device-edit.148398-106') }}</template>
          <device-instruction-config ref="deviceInstructionsConfigRef" :device="form" />
        </el-tab-pane>

        <!-- 组态应用 -->
        <el-tab-pane
          name="scada"
          :disabled="form.deviceId == 0"
          v-if="form.deviceType !== 3 && isPasShowScada && isShowScada"
          lazy
        >
          <template #label>{{ $t('device.device-edit.148398-73') }}</template>
          <device-scada ref="deviceScadaRef" :device="form" />
        </el-tab-pane>

        <el-tab-pane name="deviceMonitor" :disabled="form.deviceId == 0" v-if="form.deviceType !== 3 && isPasMonitor">
          <template #label>{{ $t('device.device-edit.148398-51') }}</template>
          <device-monitor ref="deviceMonitorRef" :device="form" />
        </el-tab-pane>

        <el-tab-pane
          name="deviceStastic"
          :disabled="form.deviceId == 0"
          v-if="form.deviceType !== 3 && isPasMonitorCount"
        >
          <template #label>{{ $t('device.device-edit.148398-52') }}</template>
          <device-statistic ref="deviceStatisticRef" :device="form" />
        </el-tab-pane>

        <el-tab-pane name="sipChannel" :disabled="form.deviceId == 0" v-if="form.deviceType === 3" lazy>
          <template #label>{{ $t('device.device-edit.148398-44') }}</template>
          <channel ref="channelRef" :device="form" />
        </el-tab-pane>
        <el-tab-pane name="deviceTimer" :disabled="form.deviceId == 0" v-if="form.deviceType !== 3 && isPasTiming" lazy>
          <template #label>{{ $t('device.device-edit.148398-47') }}</template>
          <device-timer ref="deviceTimerRef" :device="form" />
        </el-tab-pane>

        <el-tab-pane name="deviceLog" :disabled="form.deviceId == 0" v-if="form.deviceType !== 3 && isPasEventLog" lazy>
          <template #label>{{ $t('device.device-edit.148398-49') }}</template>
          <device-log ref="deviceLogRef" :device="form" />
        </el-tab-pane>

        <el-tab-pane
          name="deviceFuncLog"
          :disabled="form.deviceId == 0"
          v-if="form.deviceType !== 3 && isPasFunctionLog"
          lazy
        >
          <template #label>{{ $t('device.device-edit.148398-50') }}</template>
          <device-func-log ref="deviceFuncLogRef" :device="form" />
        </el-tab-pane>

        <el-tab-pane name="alertUser" :disabled="form.deviceId == 0" v-if="form.deviceType !== 3 && isPasDeviceUser">
          <template #label>{{ $t('device.device-edit.148398-80') }}</template>
          <alert-user ref="alertUserRef" :device="form" />
        </el-tab-pane>

        <el-tab-pane
          name="inlineVideo"
          :disabled="form.deviceId == 0"
          v-if="form.deviceType !== 3 && isPasVideoMonitor"
          lazy
        >
          <template #label>{{ $t('device.device-edit.148398-75') }}</template>
          <device-inline-video ref="deviceInlineVideoRef" :sipRelationList="form.sipRelationVOList" />
        </el-tab-pane>

        <el-tab-pane name="deviceAlert" v-if="isPasDeviceAlert" :disabled="form.deviceId == 0" lazy>
          <template #label>{{ $t('device.device-edit.148398-81') }}</template>
          <device-alert ref="deviceAlertRef" :device="form" />
        </el-tab-pane>

        <el-tab-pane name="deviceUser" :disabled="form.deviceId == 0" v-if="isPasDeviceShare" lazy>
          <template #label>{{ $t('device.device-edit.148398-48') }}</template>
          <device-user ref="deviceUserRef" :device="form" @userEvent="getUserData($event)" />
        </el-tab-pane>

        <!-- <el-tab-pane name="device04" v-if="form.deviceType !== 3" disabled>
                    <template #label>
                        <el-tooltip class="item" effect="dark" content="用于查看发送的指令，设备是否已经响应" placement="right-start">
                            <el-button type="warning" size="mini" @click="deviceSynchronizationFunc()" :disabled="form.deviceId == 0">数据同步</el-button>
                        </el-tooltip>
                    </template>
                </el-tab-pane> -->
      </el-tabs>

      <!-- 设备配置JSON -->
      <el-dialog :title="$t('device.device-edit.148398-54')" v-model="openSummary" width="700px" append-to-body>
        <el-row :gutter="20">
          <el-col :span="14">
            <div style="border: 1px solid #ccc; height: 234px; width: 360px; overflow: scroll">
              <json-viewer :value="summary" :expand-depth="10" copyable style="margin-top: 5px; cursor: pointer">
                <template #copy>{{ $t('device.device-edit.148398-55') }}</template>
              </json-viewer>
            </div>
          </el-col>
          <el-col :span="10" v-if="form.isOwner == 1">
            <div style="border: 1px solid #ccc; width: 220px; text-align: center; margin-left: 20px">
              <vue-qr :text="qrText" :size="200"></vue-qr>
              <div style="padding-bottom: 10px">{{ $t('device.device-edit.148398-56') }}</div>
            </div>
          </el-col>
        </el-row>
        <template #footer>
          <div class="dialog-footer">
            <el-button @click="closeSummaryDialog">{{ $t('device.device-edit.148398-57') }}</el-button>
          </div>
        </template>
      </el-dialog>

      <!-- 二维码 -->
      <el-dialog class="qr-dialog" v-model="openCode" width="300px" append-to-body>
        <template #header>
          <div class="title-wrap">
            <div class="name">{{ $t('device.index.105953-42') }}</div>
            <el-icon class="btn" v-print="{ id: 'qr', popTitle: '' }"><Printer /></el-icon>
          </div>
        </template>
        <div id="qr" style="text-align: center; margin: 0 auto">
          <vue-qr :text="qrText" :size="200"></vue-qr>
        </div>
      </el-dialog>

      <el-dialog
        :title="$t('device.device-edit.148398-58')"
        v-model="openViewMqtt"
        width="600px"
        :show-close="true"
        append-to-body
        :close-on-click-modal="false"
      >
        <el-form
          ref="listQueryRef"
          :model="listQuery"
          :rules="rules"
          label-width="120px"
          v-if="form.transport == 'MQTT' || form.transport == 'COAP'"
        >
          <el-form-item label="clientId" prop="clientId">
            <el-input v-model="listQuery.clientId" readonly style="width: 400px" />
          </el-form-item>
          <el-form-item label="username" prop="username">
            <el-input v-model="listQuery.username" readonly style="width: 400px" />
          </el-form-item>
          <el-form-item label="passwd" prop="passwd">
            <el-input clearable v-model="listQuery.passwd" readonly style="width: 400px"></el-input>
          </el-form-item>
          <el-form-item label="subscribeTopic" prop="subscribeTopic">
            <el-input clearable v-model="listQuery.subscribeTopic" readonly style="width: 400px"></el-input>
          </el-form-item>
          <el-form-item label="reportTopic" prop="reportTopic">
            <el-input clearable v-model="listQuery.reportTopic" readonly style="width: 400px"></el-input>
          </el-form-item>
          <el-form-item label="port" prop="port">
            <el-input clearable v-model="listQuery.port" readonly style="width: 400px"></el-input>
          </el-form-item>
        </el-form>
        <el-form
          ref="listQueryRef"
          :model="listQuery"
          :rules="rules"
          label-width="120px"
          v-if="form.transport == 'TCP'"
        >
          <!-- 注册包 -->
          <el-form-item label="enrollPackage" prop="enrollPackage">
            <el-input clearable v-model="listQuery.enrollPackage" readonly style="width: 400px"></el-input>
          </el-form-item>
          <el-form-item label="port" prop="port">
            <el-input clearable v-model="listQuery.port" readonly style="width: 400px"></el-input>
          </el-form-item>
        </el-form>
        <el-form ref="httpFormRef" :model="httpForm" :rules="rules" label-width="120px" v-if="form.transport == 'HTTP'">
          <!-- 认证类型 -->
          <el-form-item label="clientId" prop="clientId">
            <el-input v-model="httpForm.clientId" readonly style="width: 400px" />
          </el-form-item>
          <el-form-item :label="$t('device.device-edit.148398-91')">
            <el-input clearable v-model="httpForm.type" readonly style="width: 400px"></el-input>
          </el-form-item>
          <el-form-item :label="$t('product.product-edit.473153-93')">
            <el-input clearable v-model="httpForm.username" readonly style="width: 400px"></el-input>
          </el-form-item>
          <el-form-item :label="$t('product.product-edit.473153-94')">
            <el-input clearable v-model="httpForm.password" readonly style="width: 400px"></el-input>
          </el-form-item>
          <el-form-item label="port">
            <el-input clearable v-model="httpForm.port" readonly style="width: 400px"></el-input>
          </el-form-item>
        </el-form>
        <el-form ref="GBformRef" :model="GBform" :rules="rules" label-width="120px" v-if="form.transport == 'GB28181'">
          <!-- 服务器域 -->
          <el-form-item :label="$t('device.device-edit.148398-87')">
            <el-input clearable v-model="GBform.domainAlias" readonly style="width: 400px"></el-input>
          </el-form-item>
          <!-- 服务器sipid" -->
          <el-form-item :label="$t('device.device-edit.148398-88')">
            <el-input clearable v-model="GBform.serverSipid" readonly style="width: 400px"></el-input>
          </el-form-item>
          <!--  认证密码-->
          <el-form-item :label="$t('device.device-edit.148398-89')">
            <el-input clearable v-model="GBform.password" readonly style="width: 400px"></el-input>
          </el-form-item>
          <!--  接入端口号  -->
          <el-form-item :label="$t('device.device-edit.148398-90')">
            <el-input clearable v-model="GBform.port" readonly style="width: 400px"></el-input>
          </el-form-item>
        </el-form>

        <template #footer>
          <div class="dialog-footer">
            <el-button class="btns" type="primary" @click="doCopy">{{ $t('device.device-edit.148398-59') }}</el-button>
            <el-button @click="closeSummaryDialog">{{ $t('device.device-edit.148398-57') }}</el-button>
          </div>
        </template>
      </el-dialog>

      <el-drawer
        v-model="accessDiagnosticsOpen"
        class="access-diagnostics-drawer"
        size="760px"
        append-to-body
        :title="$t('device.device-edit.148398-113')"
      >
        <template #header>
          <div class="drawer-header">
            <span>{{ $t('device.device-edit.148398-113') }}</span>
            <el-button
              type="primary"
              size="small"
              plain
              :icon="Refresh"
              :loading="accessDiagnosticsLoading"
              @click="loadAccessDiagnostics"
            >
              {{ $t('refresh') }}
            </el-button>
          </div>
        </template>
        <el-skeleton v-if="accessDiagnosticsLoading && !accessDiagnosticsData" :rows="8" animated />
        <template v-else>
          <el-empty v-if="!accessDiagnosticsData" :description="$t('device.device-edit.148398-136')" />
          <div v-else class="diagnostics-content">
            <el-row :gutter="12" class="status-grid">
              <el-col :xs="24" :sm="8">
                <div class="status-panel">
                  <div class="label">{{ $t('device.device-edit.148398-114') }}</div>
                  <el-tag :type="statusTagType(accessLatest.register)">
                    {{ statusText(accessLatest.register) }}
                  </el-tag>
                  <div class="time">{{ recordTime(accessLatest.register) }}</div>
                </div>
              </el-col>
              <el-col :xs="24" :sm="8">
                <div class="status-panel">
                  <div class="label">{{ $t('device.device-edit.148398-115') }}</div>
                  <el-tag :type="statusTagType(accessLatest.keepalive)">
                    {{ statusText(accessLatest.keepalive) }}
                  </el-tag>
                  <div class="time">{{ recordTime(accessLatest.keepalive) }}</div>
                </div>
              </el-col>
              <el-col :xs="24" :sm="8">
                <div class="status-panel">
                  <div class="label">{{ $t('device.device-edit.148398-116') }}</div>
                  <el-tag :type="statusTagType(accessLatest.catalog)">
                    {{ catalogStatusText(accessLatest.catalog) }}
                  </el-tag>
                  <div class="time">{{ recordTime(accessLatest.catalog) }}</div>
                </div>
              </el-col>
            </el-row>

            <div class="section-title">{{ $t('device.device-edit.148398-117') }}</div>
            <el-empty
              v-if="!accessDiagnosisItems.length"
              :description="$t('device.device-edit.148398-118')"
              :image-size="80"
            />
            <div v-else class="diagnosis-list">
              <div
                v-for="item in accessDiagnosisItems"
                :key="`${item.level}-${item.title}-${item.evidence}`"
                class="diagnosis-item"
              >
                <div class="diagnosis-title">
                  <el-tag :type="diagnosisTagType(item.level)" size="small">
                    {{ diagnosisLevelText(item.level) }}
                  </el-tag>
                  <span>{{ item.title }}</span>
                </div>
                <div class="diagnosis-suggestion">{{ item.suggestion }}</div>
                <div class="diagnosis-evidence">{{ item.evidence }}</div>
              </div>
            </div>

            <el-descriptions :column="1" border class="redis-keys">
              <el-descriptions-item label="latest">{{ accessRedisKeys.latest || '-' }}</el-descriptions-item>
              <el-descriptions-item label="history">{{ accessRedisKeys.history || '-' }}</el-descriptions-item>
            </el-descriptions>

            <el-tabs class="diagnostics-tabs" model-value="register">
              <el-tab-pane :label="$t('device.device-edit.148398-119')" name="register">
                <el-table :data="diagnosticsRowsByEvent('REGISTER')" size="small" max-height="280">
                  <el-table-column prop="recordTime" :label="$t('device.device-edit.148398-123')" width="160" />
                  <el-table-column prop="status" :label="$t('device.device-edit.148398-124')" width="140" />
                  <el-table-column
                    prop="message"
                    :label="$t('device.device-edit.148398-125')"
                    min-width="180"
                    show-overflow-tooltip
                  />
                  <el-table-column
                    prop="hostAddress"
                    :label="$t('device.device-edit.148398-126')"
                    width="160"
                    show-overflow-tooltip
                  />
                </el-table>
              </el-tab-pane>
              <el-tab-pane :label="$t('device.device-edit.148398-120')" name="keepalive">
                <el-table :data="diagnosticsRowsByEvent('KEEPALIVE')" size="small" max-height="280">
                  <el-table-column prop="recordTime" :label="$t('device.device-edit.148398-123')" width="160" />
                  <el-table-column prop="status" :label="$t('device.device-edit.148398-124')" width="140" />
                  <el-table-column
                    prop="message"
                    :label="$t('device.device-edit.148398-125')"
                    min-width="180"
                    show-overflow-tooltip
                  />
                  <el-table-column
                    prop="hostAddress"
                    :label="$t('device.device-edit.148398-126')"
                    width="160"
                    show-overflow-tooltip
                  />
                </el-table>
              </el-tab-pane>
              <el-tab-pane :label="$t('device.device-edit.148398-121')" name="catalog">
                <el-table :data="diagnosticsRowsByEvent('CATALOG')" size="small" max-height="280">
                  <el-table-column prop="recordTime" :label="$t('device.device-edit.148398-123')" width="160" />
                  <el-table-column prop="status" :label="$t('device.device-edit.148398-124')" width="150" />
                  <el-table-column prop="total" :label="$t('device.device-edit.148398-127')" width="90" />
                  <el-table-column prop="online" :label="$t('device.device-edit.148398-128')" width="90" />
                  <el-table-column prop="offline" :label="$t('device.device-edit.148398-129')" width="90" />
                  <el-table-column
                    prop="message"
                    :label="$t('device.device-edit.148398-125')"
                    min-width="180"
                    show-overflow-tooltip
                  />
                </el-table>
              </el-tab-pane>
              <el-tab-pane :label="$t('device.device-edit.148398-122')" name="raw">
                <div class="raw-json">
                  <json-viewer :value="accessDiagnosticsData" :expand-depth="4" copyable>
                    <template #copy>{{ $t('device.device-edit.148398-55') }}</template>
                  </json-viewer>
                </div>
              </el-tab-pane>
            </el-tabs>
          </div>
        </template>
      </el-drawer>
    </el-card>
  </div>
</template>

<script setup lang="ts">
import { JsonViewer } from 'vue3-json-viewer';
import vueQr from 'vue-qr';
import {
  ref,
  reactive,
  computed,
  onMounted,
  onActivated,
  onBeforeUnmount,
  nextTick,
  watch,
  getCurrentInstance,
} from 'vue';
import busEvent from '@/utils/busEvent';
import { useRouter, useRoute } from 'vue-router';
import { ElMessage } from 'element-plus';
import { ArrowLeft, Printer, Warning, Refresh } from '@element-plus/icons-vue';
import productList from './product-list.vue';
import deviceLog from './device-log.vue';
import deviceAlert from './device-alert.vue';
import alertUser from './alert-user.vue';
import deviceUser from './device-user.vue';
import runningStatus from './running-status.vue';
import deviceMonitor from './device-monitor.vue';
import deviceStatistic from './device-statistic.vue';
import instructionParsing from './instruction-parsing.vue';
import deviceModbusTask from './device-modbus-task.vue';
import deviceInstructionConfig from './device-instruction-config.vue';
import deviceTimer from './device-timer.vue';
import channel from '../sip/channel.vue';
import sipid from '../sip/sipidGen.vue';
import deviceScada from './device-scada.vue';
import deviceVariable from './device-variable-card.vue';
import deviceInlineVideo from './device-inline-video.vue';
import deviceFuncLog from './device-functionlog.vue';
import deviceSub from './device-sub.vue';
import { loadTianDiTu } from '@/utils/map.js';
import {
  getDeviceRunningStatus,
  deviceSynchronization,
  getDevice,
  updateDevice,
  generatorDeviceNum,
  getMqttConnect,
  getSipConfig,
  getHttpConfig,
  getDeviceAccessDiagnostics,
} from '@/api/iot/device';
import { cacheJsonThingsModel } from '@/api/iot/model';
import defaultSettings from '@/settings';
import { checkPermi } from '@/utils/permission';
import { mergeObjects } from '@/utils/index';
import { useDict } from '@/utils/dict';

const { iot_device_status, iot_location_way, device_shadow } = useDict(
  'iot_device_status',
  'iot_location_way',
  'device_shadow'
);

const { proxy } = getCurrentInstance() as any;
const router = useRoute();

// 响应式数据
const qrText = ref('fastbee'); // 二维码内容
const openSummary = ref(false); // 打开设备配置对话框
const openCode = ref(false); // 二维码
const openViewMqtt = ref(false);
const accessDiagnosticsOpen = ref(false);
const accessDiagnosticsLoading = ref(false);
const accessDiagnosticsData = ref<any>(null);
const genDisabled = ref(false); // 生成设备编码是否禁用
const activeName = ref('basic'); // 选中选项卡
const mqttList = ref<any[]>([]); // 查看mqtt参数
const loading = ref(true); // 遮罩层
const oldDeviceStatus = ref(null); // 设备开始状态
const deviceId = ref('');
const channelId = ref('');
const optionList = ref<any[]>([]);
const openServerTip = ref(false);
const serverType = ref(1);
const summary = ref<any[]>([]); // 设备摘要
const baseUrl = ref(import.meta.env.VITE_APP_BASE_API); // 地址
const gwSerialNumber = ref('');
const extLoading = ref(false);
const props = defineProps({ device: { type: Object, default: () => ({}) } });
// 编辑状态控制
const isEditing = ref(false);
// 备份原始数据，用于取消恢复
let extBackup = [];

// 天地图相关
const map = ref<any>(null);
const mk = ref<any>(null);
const markerDiv = ref<HTMLElement | null>(null);

// 天地图：初始化
const initTianDiTuMap = () => {
  const mapContainer = document.getElementById('map');
  if (!mapContainer) return;
  destroyMap();
  mapContainer.innerHTML = '';
  mapContainer.style.minHeight = '435px';
  mapContainer.style.position = 'relative';
  try {
    const T = (window as any).T;
    if (!T || !T.Map) {
      console.error('天地图API未正确加载');
      return;
    }
    map.value = new T.Map('map', { minZoom: 3, maxZoom: 19, zoom: 15, enableAnimation: true });
    let centerLng = 116.404,
      centerLat = 39.915;
    if (form.value.longitude && form.value.latitude) {
      const lng = parseFloat(String(form.value.longitude));
      const lat = parseFloat(String(form.value.latitude));
      if (!isNaN(lng) && !isNaN(lat)) {
        centerLng = lng;
        centerLat = lat;
      }
    }
    const centerPoint = new T.LngLat(centerLng, centerLat);
    map.value.centerAndZoom(centerPoint, 15);
    map.value.enableScrollWheelZoom(true);
    if (T.Control?.Zoom) {
      try {
        map.value.addControl(new T.Control.Zoom({ position: (window as any).T_ANCHOR_TOP_RIGHT }));
      } catch (e) {}
    }
    if (T.Control?.Scale) {
      try {
        map.value.addControl(new T.Control.Scale());
      } catch (e) {}
    }
    // 添加标记点
    if (mk.value) {
      try {
        if (typeof map.value.removeOverlay === 'function') map.value.removeOverlay(mk.value);
      } catch (e) {}
    }
    try {
      let marker: any = null;
      if (T.Marker) {
        try {
          marker = new T.Marker(centerPoint);
        } catch (e) {}
      }
      if (marker) {
        if (typeof map.value.addOverlay === 'function') map.value.addOverlay(marker);
        mk.value = marker;
      }
    } catch (e) {
      console.error('添加标记失败:', e);
    }
    setTimeout(() => {
      if (map.value?.panTo) map.value.panTo(centerPoint);
    }, 300);
  } catch (error) {
    console.error('天地图初始化失败:', error);
  }
};

// 天地图：加载
const loadMapWithCoord = () => {
  loadTianDiTu()
    .then(() => {
      nextTick(() => {
        initTianDiTuMap();
      });
    })
    .catch((e: any) => {
      console.error('天地图加载失败:', e);
    });
};

// 天地图：销毁
const destroyMap = () => {
  try {
    if (map.value) {
      if (mk.value && typeof map.value.removeOverlay === 'function') map.value.removeOverlay(mk.value);
      if (markerDiv.value?.parentNode) markerDiv.value.parentNode.removeChild(markerDiv.value);
      if (typeof map.value.destroy === 'function') map.value.destroy();
      const c = document.getElementById('map');
      if (c) c.innerHTML = '';
    }
  } catch (err) {
    console.warn('地图销毁异常:', err);
  } finally {
    map.value = null;
    mk.value = null;
    markerDiv.value = null;
  }
};
// 权限标识
const isPasDataDebug = ref(false);
const isPasShowScada = ref(false);
const isPasMonitor = ref(false);
const isPasMonitorCount = ref(false);
const isPasTiming = ref(false);
const isPasEventLog = ref(false);
const isPasFunctionLog = ref(false);
const isPasDeviceUser = ref(false);
const isPasVideoMonitor = ref(false);
const isPasDeviceAlert = ref(false);
const isPasDeviceShare = ref(false);

const initPermissions = () => {
  const permList = [
    { ref: isPasDataDebug, perm: 'iot:device:dataDebug' },
    { ref: isPasShowScada, perm: 'iot:device:scadaApply' },
    { ref: isPasMonitor, perm: 'iot:device:monitor' },
    { ref: isPasMonitorCount, perm: 'iot:device:monitorCount' },
    { ref: isPasTiming, perm: 'iot:device:timing' },
    { ref: isPasEventLog, perm: 'iot:device:eventLog' },
    { ref: isPasFunctionLog, perm: 'iot:device:functionLog' },
    { ref: isPasDeviceUser, perm: 'iot:device:alertUser' },
    { ref: isPasVideoMonitor, perm: 'iot:device:videoMonitor' },
    { ref: isPasDeviceAlert, perm: 'iot:device:deviceAlert' },
    { ref: isPasDeviceShare, perm: 'iot:device:deviceShare' },
  ];
  permList.forEach(({ ref: r, perm }) => {
    r.value = checkPermi([perm]);
  });
};
// 组态相关按钮是否显示，true显示，false不显示
const isShowScada = ref(defaultSettings.isShowScada);

// 表单参数
const formParams = reactive({
  deviceName: '',
  productId: null as number | null,
  productName: '',
  serialNumber: '',
  firmwareType: 1,
  firmwareVersion: 1,
  rssi: 0,
  iccid: '',
  remark: '',
  locationWay: 1,
  longitude: null as number | null,
  latitude: null as number | null,
  networkAddress: '',
  networkIp: '',
  activeTime: '',
  isShadow: 0,
  status: null as number | null,
  extParamList: [],
});

const form = ref<any>({});
const listQuery = ref<any>({
  clientId: 0,
  username: '',
  passwd: '',
  port: '',
});

const GBform = ref({
  domainAlias: '',
  serverSipid: '',
  password: '',
  port: '',
});

const httpForm = ref({
  type: '',
  username: '',
  password: '',
  clientId: '',
  port: '',
});

// 表单校验
const rules = reactive<any>({
  deviceName: [
    {
      required: true,
      message: proxy?.$t('device.device-edit.148398-60'),
      trigger: 'blur',
    },
    {
      min: 2,
      max: 32,
      message: proxy?.$t('device.device-edit.148398-61'),
      trigger: 'blur',
    },
  ],
  productName: [
    {
      required: true,
      message: proxy?.$t('device.device-edit.148398-67'),
      trigger: 'blur',
    },
  ],
  serialNumber: [
    {
      required: true,
      message: proxy?.$t('device.device-edit.148398-65'),
      trigger: 'blur',
    },
  ],
  firmwareVersion: [
    {
      required: true,
      message: proxy?.$t('device.device-edit.148398-62'),
      trigger: 'blur',
    },
    {
      validator: (rule: any, value: any, callback: any) => {
        const regex = /^[a-zA-Z0-9][a-zA-Z0-9]*(?:\.[a-zA-Z0-9]+)*$/;
        if (!regex.test(value)) {
          callback(new Error(proxy?.$t('device.device-edit.148398-108')));
        } else {
          callback();
        }
      },
      trigger: 'blur',
    },
  ],
  devicePort: [
    {
      required: true,
      message: proxy?.$t('device.device-edit.148398-97'),
      trigger: 'blur',
    },
  ],
  deviceIp: [
    {
      required: true,
      message: proxy?.$t('device.device-edit.148398-102'),
      trigger: 'blur',
    },
  ],
});

// 计算属性
const deviceStatus = computed({
  set(val: number) {
    if (val == 1) {
      // 1-未激活，2-禁用，3-在线，4-离线
      form.value.status = 2;
    } else if (val == 0) {
      form.value.status = 4;
    } else {
      form.value.status = oldDeviceStatus.value;
    }
  },
  get() {
    if (form.value.status == 2) {
      return 1;
    }
    return 0;
  },
});

const statusColor = computed(() => {
  switch (form.value.status) {
    case 1:
      return '#ffba00';
    case 2:
      return '#f56c6c';
    case 3:
      return '#67c23a';
    case 4:
      return '#909399';
    default:
      return '#f56c6c';
  }
});

const isGb28181Device = computed(() => form.value.deviceType === 3 && form.value.transport === 'GB28181');
const accessLatest = computed(() => accessDiagnosticsData.value?.latest || {});
const accessDiagnosisItems = computed(() => accessDiagnosticsData.value?.diagnosisItems || []);
const accessRedisKeys = computed(() => accessDiagnosticsData.value?.redisKeys || {});

function toNumberOrNull(value: any) {
  if (value === '' || value === null || value === undefined) return null;
  const numberValue = Number(value);
  return Number.isNaN(numberValue) ? null : numberValue;
}

function normalizeExtParamList(list: any[] = []) {
  return list.map((item: any) => {
    if (item?.paramType !== 'number') {
      return item;
    }
    return {
      ...item,
      paramValue: toNumberOrNull(item.paramValue ?? item.defaultValue),
    };
  });
}

// Refs
const formRef = ref();
const productListRef = ref();
const sipidGenRef = ref();
const runningStatusRef = ref();
const deviceVariableRef = ref();
const instructionParsingRef = ref();
const deviceSubRef = ref();
const deviceModbusTaskRef = ref();
const deviceInstructionsConfigRef = ref();
const deviceTimerRef = ref();
const deviceFuncLogRef = ref();
const deviceLogRef = ref();
const deviceAlertRef = ref();
const deviceUserRef = ref();
const alertUserRef = ref();
const deviceMonitorRef = ref();
const deviceStatisticRef = ref();
const channelRef = ref();
const deviceScadaRef = ref();
const deviceInlineVideoRef = ref();
const listQueryRef = ref();
const httpFormRef = ref();
const GBformRef = ref();
// 过滤掉非 app.vue 的监听器
const removeNonAppListeners = () => {
  const mqttClient = proxy?.$mqttTool?.client;
  if (!mqttClient) return;
  const listeners = mqttClient._events?.message;
  if (!listeners) return;
  const listenersArray = Array.isArray(listeners) ? listeners : [listeners];
  listenersArray.forEach((listener: any) => {
    if (!listener.isAppListener) {
      mqttClient.off('message', listener);
    }
  });
};

// 方法
const connectMqtt = async () => {
  if (proxy?.$mqttTool.client == null) {
    await proxy?.$mqttTool.connect(proxy?.vuex_token);
  }
  // 只移除非 app 监听器，保留 app.vue 的全局监听
  removeNonAppListeners();
  mqttCallback();
};

// Mqtt 回调处理
const mqttCallback = () => {
  proxy?.$mqttTool.client.on('message', (topic: string, message: any, buffer: any) => {
    let topics = topic.split('/');
    let productId = topics[1];
    let deviceNum = topics[2];
    if (message instanceof Uint8Array) {
      // 创建 TextDecoder 对象来转换 Uint8Array 到字符串
      const decoder = new TextDecoder('utf-8');
      const str = decoder.decode(message);
      message = str; // 转换后的字符串
    }
    message = JSON.parse(message.toString());

    if (!message) {
      return;
    }
    if (topics[3] == 'status' || topics[2] == 'status') {
      busEvent.emit('updateStatus', {
        serialNumber: topics[2],
        productId: form.value.productId,
        data: message,
      });
      console.log('接收到【设备状态-详情】主题:', topic);
      console.log('接收到【设备状态-详情】内容：', message);
      // 更新列表中设备的状态
      if (form.value.serialNumber == deviceNum) {
        oldDeviceStatus.value = message.status;
        form.value.status = message.status;
        form.value.isShadow = message.isShadow;
        form.value.rssi = message.rssi;
      }
    }
    if (topic.endsWith('ws/service')) {
      busEvent.emit('updateData', {
        serialNumber: topics[2],
        productId: form.value.productId,
        data: message,
      });
    }
    if (topic.endsWith('service/reply')) {
      busEvent.emit('updateLog', {
        serialNumber: topics[2],
        productId: form.value.productId,
        data: message,
      });
    }
    /** mqtt 测试 */
    if (topic.endsWith('message/post')) {
      busEvent.emit('updateMqttMessage', {
        serialNumber: topics[2],
        data: message,
      });
    }
  });
};

/** Mqtt topics array */
const getMqttTopics = (device: any) => {
  // 订阅当前设备状态和实时监测
  const topicService = '/ws/service';
  const topicStatus = '/status/post';
  const topicFunction = '/function/post';
  const topicMonitor = '/monitor/post';
  const topicReply = '/service/reply';
  //订阅mqtt测试
  let messagePost = '/message/post';
  const topics = [topicService, topicStatus, topicFunction, topicMonitor, topicReply, messagePost];

  return topics.map((topic) => {
    return `/${device.productId}/${device.serialNumber}${topic}`;
  });
};

// 获取子组件订阅的设备状态
const getDeviceStatusData = (status: any) => {
  form.value.status = status;
};

/** Mqtt 取消订阅主题 */
const mqttUnSubscribe = (device: any) => {
  const topics = getMqttTopics(device);
  if (proxy?.$mqttTool) {
    proxy?.$mqttTool.unsubscribe(topics);
  }
};

/** Mqtt 订阅主题 */
const mqttSubscribe = (device: any) => {
  const topics = getMqttTopics(device);
  if (proxy?.$mqttTool) {
    proxy?.$mqttTool.subscribe(topics);
  }
};

/** 选项卡改变事件*/
const tabChange = (panel: any) => {
  nextTick(() => {
    if (panel.paneName == 'basic') {
      loadMapWithCoord();
    }
    if (form.value.deviceType == 3 && panel.paneName != 'deviceReturn') {
      if (panel.paneName === 'sipChannel') {
        nextTick(() => {
          channelRef.value?.getList();
        });
      }
    }
  });
  nextTick(() => {
    // 获取监测统计数据
    if (panel.paneName === 'deviceStastic') {
      deviceStatisticRef.value?.getListHistory();
    } else if (panel.paneName === 'deviceTimer') {
      deviceTimerRef.value?.getList();
    } else if (panel.paneName === 'deviceSub') {
      if (form.value.serialNumber) {
        deviceSubRef.value?.getList();
      }
    }
  });
  if (form.value.deviceType !== 3) {
    // 用于关闭视频推流（页面切换时候需要关闭推流）
    if (panel.paneName !== 'inlineVideo') {
      deviceInlineVideoRef.value && deviceInlineVideoRef.value.handleClose();
    }
    if (panel.paneName !== 'scada') {
      const scadaRef = deviceScadaRef.value || {};
      if (scadaRef && scadaRef.$refs && scadaRef.$refs.deviceScada) {
        const copmRef = scadaRef.$refs.deviceScada;
        if (copmRef.$refs && copmRef.$refs.spirit) {
          copmRef.$refs.spirit.forEach((item: any) => {
            if (typeof item.handleCloseJessibuca === 'function') {
              item.handleCloseJessibuca();
            }
          });
        }
      }
    }
  }
};

/** 数据同步*/
const deviceSynchronizationFunc = () => {
  deviceSynchronization(form.value.serialNumber).then(async (response: any) => {
    // 获取缓存物模型
    response.data.cacheThingsModel = await getCacheThingsModdel(response.data.productId);
    // 获取设备运行状态
    response.data.thingsModels = await getDeviceStatus(form.value);
    // 格式化物模型，拆分出监测值,数组添加前缀
    formatThingsModel(response.data);
    response.data.extParamList = normalizeExtParamList(response.data.extParamList);
    form.value = response.data;
    // 选项卡切换
    activeName.value = 'runningStatus';
    oldDeviceStatus.value = form.value.status;
    loadMapWithCoord();
  });
};

/**获取设备详情*/
const getDeviceInfo = (deviceId: string) => {
  getDevice(deviceId).then(async (response: any) => {
    form.value.protocolCode = response.data.protocolCode;
    form.value.extParamList = normalizeExtParamList(response.data.extParamList);
    // 获取设备状态和物模型
    getDeviceStatusWitchThingsModel(response);
  });
};

/** 获取缓存物模型*/
const getCacheThingsModdel = (productId: any) => {
  return new Promise((resolve, reject) => {
    cacheJsonThingsModel(productId)
      .then((response: any) => {
        resolve(JSON.parse(response.data));
      })
      .catch((error) => {
        reject(error);
      });
  });
};

/**获取设备运行状态*/
const getDeviceStatus = (data: any) => {
  const params = {
    deviceId: data.deviceId,
  };
  return new Promise((resolve, reject) => {
    getDeviceRunningStatus(params)
      .then((response: any) => {
        // console.log(response);
        resolve(response.data.thingsModels);
      })
      .catch((error) => {
        reject(error);
      });
  });
};

// 物模型格式化
const formatThingsModel = (data: any) => {
  data.chartList = [];
  data.monitorList = [];
  data.staticList = [];
  data.thingsModels = data.thingsModels.sort((a: any, b: any) => b.order - a.order);
  let restart = true;
  while (restart) {
    restart = false;
    const hasElementsToProcess = data.thingsModels.some((item: any) => item.id.includes('_'));
    if (!hasElementsToProcess) break;
    let model: any = {
      id: '',
      name: '',
      isMonitor: 0,
      isHistory: 0,
      isChart: 0,
      isReadonly: 0,
      isApp: 0,
      order: 8,
      type: 1,
      datatype: {
        type: '',
        falseText: '',
        trueText: '',
        min: 0,
        max: 100,
        step: 1,
        unit: '',
        arrayType: '',
        arrayCount: 0,
        showWay: null,
        maxLength: 1024,
        enumList: null,
        params: [],
        arrayParams: [],
        arrayModel: [],
      },
    };
    for (let i = 0; i < data.thingsModels.length; i++) {
      const thingsModelId = data.thingsModels[i].id.split('_');
      if (data.thingsModels[i].datatype.parentType === null) continue;
      if (data.thingsModels[i].datatype.parentType === '1') {
        if (model.id === '') {
          model.id = data.thingsModels[i].datatype.parentIdentifier;
          model.name = data.thingsModels[i].datatype.parentName;
          model.datatype.type = 'object';
        }
        if (model.id === data.thingsModels[i].datatype.parentIdentifier) {
          model.datatype.params.push(data.thingsModels[i]);
          data.thingsModels.splice(i, 1);
          i--;
        }
      }
      if (data.thingsModels[i]?.datatype?.parentType === '2') {
        if (model.id === '') {
          model.id = data.thingsModels[i].datatype.parentIdentifier;
          model.name = data.thingsModels[i].datatype.parentName;
          model.datatype.type = 'array';
        }
        if (model.id === data.thingsModels[i].datatype.parentIdentifier) {
          model.isReadonly = data.thingsModels[i].isReadonly;
          model.datatype.arrayType = data.thingsModels[i].datatype.type;
          model.datatype.arrayModel.push({
            id: data.thingsModels[i].id,
            name: data.thingsModels[i].name,
            value: data.thingsModels[i].value,
            shadow: data.thingsModels[i].shadow,
          });
          data.thingsModels.splice(i, 1);
          i--;
          model.datatype.arrayCount++;
        }
      }
      if (data.thingsModels[i]?.datatype?.parentType === '3') {
        if (model.id === '') {
          model.id = data.thingsModels[i].datatype.parentIdentifier;
          model.name = data.thingsModels[i].datatype.parentName;
          model.datatype.type = 'array';
        }
        if (model.id === data.thingsModels[i].datatype.parentIdentifier) {
          model.datatype.arrayType = 'object';
          if (!data.thingsModels[i].name) data.thingsModels[i].name = model.name;
          if (model.datatype.arrayParams.length === 0) {
            model.datatype.arrayParams[0] = [data.thingsModels[i]];
          } else {
            let hasMatched = false;
            model.datatype.arrayParams.some((subArray: any[]) => {
              const isMatch = subArray.some((param: any) => {
                const obj = param.id.split('_');
                return obj[1] === thingsModelId[1];
              });
              if (isMatch) {
                subArray.push(data.thingsModels[i]);
                hasMatched = true;
                return true;
              }
              return false;
            });
            if (!hasMatched) model.datatype.arrayParams.push([data.thingsModels[i]]);
          }
          data.thingsModels.splice(i, 1);
          i--;
          model.datatype.arrayCount++;
        }
      }
    }
    if (model.id !== '') {
      data.thingsModels.push(model);
      restart = true;
    }
  }
  for (let i = 0; i < data.thingsModels.length; i++) {
    if (data.thingsModels[i].datatype.type === 'integer' || data.thingsModels[i].datatype.type === 'decimal') {
      data.thingsModels[i].shadow =
        data.thingsModels[i].shadow === ''
          ? Number(data.thingsModels[i].datatype.min)
          : Number(data.thingsModels[i].shadow);
    }
    if (data.thingsModels[i].datatype.type === 'array') {
      if (
        data.thingsModels[i].datatype.arrayType === 'object' &&
        data.thingsModels[i].datatype.arrayParams.length > 0
      ) {
        for (let k = 0; k < data.thingsModels[i].datatype.arrayParams.length; k++) {
          for (let j = 0; j < data.thingsModels[i].datatype.arrayParams[k].length; j++) {
            if (data.thingsModels[i].datatype.arrayParams[k][j].isChart === 1) {
              data.thingsModels[i].datatype.arrayParams[k][j].fatherName =
                data.thingsModels[i].datatype.arrayParams[k][j].datatype.parentIndexName;
              data.thingsModels[i].datatype.arrayParams[k][j].datatype.arrayType = 'object';
              const tmId = data.thingsModels[i].datatype.arrayParams[k][j].id;
              if (data.chartList.length === 0) {
                data.chartList[0] = [data.thingsModels[i].datatype.arrayParams[k][j]];
              } else {
                let hm = false;
                data.chartList.some((sa: any[]) => {
                  const isM = sa.some((p: any) => {
                    const o = p.id ? p.id.split('_') : [];
                    const id = tmId.split('_');
                    return o[1] && id[1] && o[1] === id[1] && data.thingsModels[i].id === p.datatype.parentIdentifier;
                  });
                  if (isM) {
                    sa.push(data.thingsModels[i].datatype.arrayParams[k][j]);
                    hm = true;
                    return true;
                  }
                  return false;
                });
                if (!hm) data.chartList.push([data.thingsModels[i].datatype.arrayParams[k][j]]);
              }
              if (data.thingsModels[i].datatype.arrayParams[k][j].isHistory === 1)
                data.staticList.push(data.thingsModels[i].datatype.arrayParams[k][j]);
              if (data.thingsModels[i].datatype.arrayParams[k][j].isMonitor === 1)
                data.monitorList.push(data.thingsModels[i].datatype.arrayParams[k][j]);
              data.thingsModels[i].datatype.arrayParams[k].splice(j--, 1);
            }
          }
        }
      } else if (data.thingsModels[i].datatype.arrayCount > 0) {
        for (let j = 0; j < data.thingsModels[i].datatype.arrayCount; j++) {
          if (!data.thingsModels[i].datatype.arrayModel) data.thingsModels[i].datatype.arrayModel = [];
          data.thingsModels[i].datatype.arrayModel[j] = {
            ...data.thingsModels[i].datatype.arrayModel[j],
            type: data.thingsModels[i].type,
            isReadonly: data.thingsModels[i].isReadonly,
          };
        }
      }
    } else if (data.thingsModels[i].datatype.type === 'object' && data.thingsModels[i].datatype.params.length > 0) {
      for (let j = 0; j < data.thingsModels[i].datatype.params.length; j++) {
        if (data.thingsModels[i].datatype.params[j].isChart === 1) {
          data.thingsModels[i].datatype.params[j].fatherName = data.thingsModels[i].name;
          if (data.chartList.length === 0) {
            data.chartList[0] = [data.thingsModels[i].datatype.params[j]];
          } else {
            let hm = false;
            data.chartList.some((sa: any[]) => {
              const isM = sa.some((p: any) => data.thingsModels[i].id === p.datatype.parentIdentifier);
              if (isM) {
                sa.push(data.thingsModels[i].datatype.params[j]);
                hm = true;
                return true;
              }
              return false;
            });
            if (!hm) data.chartList.push([data.thingsModels[i].datatype.params[j]]);
          }
          if (data.thingsModels[i].datatype.params[j].isHistory === 1)
            data.staticList.push(data.thingsModels[i].datatype.params[j]);
          if (data.thingsModels[i].datatype.params[j].isMonitor === 1)
            data.monitorList.push(data.thingsModels[i].datatype.params[j]);
          data.thingsModels[i].datatype.params.splice(j--, 1);
        }
      }
    } else if (data.thingsModels[i].isChart === 1) {
      if (data.chartList.length === 0) {
        data.chartList[0] = [data.thingsModels[i]];
      } else {
        data.chartList.some((sa: any[]) => {
          sa.push(data.thingsModels[i]);
        });
      }
      if (data.thingsModels[i].isHistory === 1) data.staticList.push(data.thingsModels[i]);
      if (data.thingsModels[i].isMonitor === 1) data.monitorList.push(data.thingsModels[i]);
      data.thingsModels.splice(i--, 1);
    }
  }
  for (let i = 0; i < data.thingsModels.length; i++) {
    if (data.thingsModels[i].datatype.type === 'array') {
      if (
        data.thingsModels[i].datatype.arrayType === 'object' &&
        data.thingsModels[i].datatype.arrayParams.length > 0
      ) {
        data.thingsModels[i].datatype.arrayParams = data.thingsModels[i].datatype.arrayParams.filter(
          (item: any[]) => item.length > 0
        );
      }
      if (
        data.thingsModels[i].datatype.arrayType === 'object' &&
        data.thingsModels[i].datatype.arrayParams.length === 0
      )
        data.thingsModels.splice(i--, 1);
    } else if (data.thingsModels[i].datatype.type === 'object') {
      if (data.thingsModels[i].datatype.params.length === 0) data.thingsModels.splice(i--, 1);
    }
  }
};

// 返回按钮
const goBack = () => {
  const obj = {
    path: '/iot/device/list',
    query: {
      t: Date.now(),
      pageNum: router.query.pageNum,
    },
  };
  proxy?.$tab.closeOpenPage(obj);
  reset();
  channelRef.value?.closeDestroy(false);
};

// 表单重置
const reset = () => {
  form.value = JSON.parse(JSON.stringify(formParams));
  deviceStatus.value = 0;
  proxy?.resetForm('formRef');
};

// 提交按钮
const handleSubmitForm = () => {
  if (form.value.serialNumber == null || form.value.serialNumber == 0) {
    proxy?.$modal.alertError(proxy?.$t('device.device-edit.148398-65'));
    return;
  }
  if (form.value.protocolCode !== 'MODBUS-TCP') {
    let reg = /^[0-9a-zA-Z]+$/;
    if (!reg.test(form.value.serialNumber)) {
      proxy?.$modal.alertError(proxy?.$t('device.device-edit.148398-66'));
      return;
    }
  } else {
    let ipReg =
      /^(25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\.(25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\.(25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\.(25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)$/;
    if (!ipReg.test(form.value.deviceIp)) {
      proxy?.$modal.alertError(proxy?.$t('device.device-edit.148398-98'));
      return;
    }
  }
  if (form.value.productId == null || form.value.productId == 0) {
    proxy?.$modal.alertError(proxy?.$t('device.device-edit.148398-67'));
    return;
  }
  formRef.value?.validate((valid: boolean) => {
    if (valid) {
      const { deviceId, ...fo } = form.value;
      let data = mergeObjects(formParams, fo);
      const { latitude, longitude, ...da } = data;
      const { tenantId, createBy } = form.value;

      const deviceExtParamValueList = (form.value.extParamList || []).map((item: any) => {
        const rawValue = item?.paramValue ?? item?.defaultValue ?? null;
        const value =
          item?.paramType === 'number'
            ? rawValue === '' || rawValue === null || rawValue === undefined
              ? null
              : Number(rawValue)
            : rawValue;

        return {
          id: item.id,
          paramId: item.paramId,
          paramValue: value,
        };
      });

      const newData = {
        ...da,
        deviceId,
        latitude: latitude ? latitude : 0,
        longitude: longitude ? longitude : 0,
        tenantId: tenantId,
        createBy: createBy,
        deviceExtParamValueList,
      };
      updateDevice(newData).then((res: any) => {
        if (res.code === 200) {
          proxy?.$modal.alertSuccess(proxy?.$t('device.device-edit.148398-68'));
        } else {
          proxy?.$modal.alertError(res.msg);
        }
      });
    }
  });
};

/** 获取设备状态和物模型 **/
const getDeviceStatusWitchThingsModel = async (res: any) => {
  // 获取缓存物模型
  res.data.cacheThingsModel = await getCacheThingsModdel(res.data.productId);
  // 获取设备运行状态
  res.data.thingsModels = await getDeviceStatus(res.data);
  // 格式化物模型，拆分出监测值,数组添加前缀
  formatThingsModel(res.data);
  form.value = {
    ...res.data,
    extParamList: normalizeExtParamList(res.data.extParamList),
    latitude: res.data.latitude === 0 ? null : res.data.latitude,
    longitude: res.data.longitude === 0 ? null : res.data.longitude,
  };
  // 解析设备摘要
  if (form.value.summary != null && form.value.summary != '') {
    summary.value = JSON.parse(form.value.summary);
  }
  oldDeviceStatus.value = form.value.status;
  loadMapWithCoord();
  //Mqtt订阅
  connectMqtt();
  mqttSubscribe(form.value);
};

/**选择产品 */
const selectProduct = () => {
  productListRef.value.open = true;
  form.value.deviceIp = '';
  productListRef.value.getList();
};

const genSipID = () => {
  if (!isGb28181Device.value) {
    return;
  }
  sipidGenRef.value.openDialog();
};

/**获取选中的产品 */
const getProductData = (product: any) => {
  form.value.productId = product.productId;
  form.value.productName = product.productName;
  form.value.deviceType = product.deviceType;
  form.value.protocolCode = product.protocolCode;
  form.value.transport = product.transport;
  form.value.tenantId = product.tenantId;
  form.value.tenantName = product.tenantName;
  form.value.slaveId = product.slaveId;
  if (product.transport === 'TCP') {
    openServerTip.value = true;
    serverType.value = 3;
  } else {
    openServerTip.value = false;
    serverType.value = 1;
  }
};

// 获取监控产品设备编号
const getSipIDData = (devsipid: any) => {
  form.value.serialNumber = devsipid;
};

// 获取选中的用户
const getUserData = (user: any) => {};

/**关闭物模型 */
const openSummaryDialog = () => {
  let json = {
    type: 1, // 1=扫码关联设备
    deviceNumber: form.value.serialNumber,
    productId: form.value.productId,
    productName: form.value.productName,
  };
  qrText.value = JSON.stringify(json);
  openSummary.value = true;
};

const openAccessDiagnostics = () => {
  accessDiagnosticsOpen.value = true;
  loadAccessDiagnostics();
};

const loadAccessDiagnostics = () => {
  if (!form.value.deviceId) return;
  accessDiagnosticsLoading.value = true;
  getDeviceAccessDiagnostics(form.value.deviceId)
    .then((response: any) => {
      accessDiagnosticsData.value = response.data;
    })
    .finally(() => {
      accessDiagnosticsLoading.value = false;
    });
};

const diagnosticsRowsByEvent = (event: string) => {
  const history = accessDiagnosticsData.value?.history || [];
  return history.filter((item: any) => item.event === event);
};

const statusText = (record: any) => {
  return record?.status || proxy?.$t('device.device-edit.148398-130');
};

const catalogStatusText = (record: any) => {
  if (!record) return proxy?.$t('device.device-edit.148398-130');
  return `${record.status || '-'} / ${proxy?.$t('device.device-edit.148398-127')}:${record.total ?? 0}`;
};

const recordTime = (record: any) => {
  return record?.recordTime || proxy?.$t('device.device-edit.148398-131');
};

const statusTagType = (record: any) => {
  if (!record) return 'info';
  const status = record.status;
  if (['ONLINE', 'CATALOG_REPORTED', 'OK'].includes(status)) return 'success';
  if (['UNAUTHORIZED', 'FORBIDDEN', 'DEVICE_NOT_FOUND', 'SIP_CONFIG_NOT_FOUND', 'UNKNOWN_DEVICE'].includes(status)) {
    return 'danger';
  }
  return 'warning';
};

const diagnosisTagType = (level: string) => {
  const typeMap: Record<string, string> = {
    danger: 'danger',
    warning: 'warning',
    success: 'success',
    info: 'info',
  };
  return typeMap[level] || 'info';
};

const diagnosisLevelText = (level: string) => {
  const textMap: Record<string, string> = {
    danger: proxy?.$t('device.device-edit.148398-132'),
    warning: proxy?.$t('device.device-edit.148398-133'),
    success: proxy?.$t('device.device-edit.148398-134'),
    info: proxy?.$t('device.device-edit.148398-135'),
  };
  return textMap[level] || textMap.info;
};

/**关闭物模型 */
const closeSummaryDialog = () => {
  openSummary.value = false;
  openViewMqtt.value = false;
};

const doCopy = () => {
  const input = document.createElement('input');
  if (form.value.transport == 'MQTT' || form.value.transport == 'COAP') {
    input.value =
      '{"clientId":"' +
      listQuery.value.clientId +
      '","username":"' +
      listQuery.value.username +
      '","passwd":"' +
      listQuery.value.passwd +
      '","subscribeTopic":"' +
      listQuery.value.subscribeTopic +
      '","reportTopic":"' +
      listQuery.value.reportTopic +
      '","port":"' +
      listQuery.value.port +
      '"}';
  } else if (form.value.transport == 'TCP') {
    input.value = '{enrollPackage:' + listQuery.value.enrollPackage + ',port:' + listQuery.value.port + '}';
  } else if (form.value.transport == 'HTTP') {
    input.value =
      '{clientId:' +
      httpForm.value.clientId +
      ',type:' +
      httpForm.value.type +
      ',username:' +
      httpForm.value.username +
      ',password:' +
      httpForm.value.password +
      '","port":"' +
      httpForm.value.port +
      '"}';
  }
  if (form.value.transport == 'GB28181') {
    input.value =
      '{domainAlias:' +
      GBform.value.domainAlias +
      ',serverSipid:' +
      GBform.value.serverSipid +
      ',password:' +
      GBform.value.password +
      ',port:' +
      GBform.value.port +
      '}';
  }
  document.body.appendChild(input);
  input.select(); //选中输入框
  document.execCommand('Copy'); //复制当前选中文本到剪切板
  document.body.removeChild(input);
  ElMessage.success(proxy?.$t('device.device-edit.148398-71'));
};

const openCodeDialog = () => {
  let json = {
    type: 1, // 1=扫码关联设备
    deviceNumber: form.value.serialNumber,
    productId: form.value.productId,
    productName: form.value.productName,
  };
  qrText.value = JSON.stringify(json);
  openCode.value = true;
};

// 地图定位
const getmap = () => {
  const BMap = (window as any).BMap;
  if (!BMap) return;
  map.value = new BMap.Map('map');
  let point = null;
  if (
    form.value.longitude != null &&
    form.value.longitude != '' &&
    form.value.latitude != null &&
    form.value.latitude != ''
  ) {
    point = new BMap.Point(form.value.longitude, form.value.latitude);
  } else {
    point = new BMap.Point(116.404, 39.915);
  }
  map.value.centerAndZoom(point, 19);
  map.value.enableScrollWheelZoom(true); // 开启鼠标滚轮缩放
  map.value.addControl(new BMap.NavigationControl());

  // 标注设备位置
  mk.value = new BMap.Marker(point);
  map.value.addOverlay(mk.value);
  map.value.panTo(point);
};

// 生成随机字母和数字
const generateNum = () => {
  if (!form.value.productId || form.value.productId == 0) {
    proxy?.$modal.alertError(proxy?.$t('device.device-edit.148398-72'));
    return;
  }
  genDisabled.value = true;
  const params = { type: serverType.value };
  generatorDeviceNum(params).then((response: any) => {
    form.value.serialNumber = response.data;
    genDisabled.value = false;
  });
};

//mqtt参数查看
const handleViewMqtt = () => {
  openViewMqtt.value = true;
  loading.value = true;
  if (form.value.transport === 'MQTT' || form.value.transport === 'TCP' || form.value.transport === 'COAP') {
    const params = {
      deviceId: form.value.deviceId,
    };
    getMqttConnect(params).then((response: any) => {
      if (response.code == 200) {
        listQuery.value = response.data;
        loading.value = false;
      }
    });
  } else if (form.value.transport === 'GB28181') {
    const deviceSipId = form.value.deviceId;
    getSipConfig(deviceSipId).then((response: any) => {
      if (response.code == 200) {
        GBform.value = response.data;
        loading.value = false;
      }
    });
  } else if (form.value.transport === 'HTTP') {
    const params = {
      deviceId: form.value.deviceId,
    };
    getHttpConfig(params).then((response: any) => {
      if (response.code == 200) {
        httpForm.value = response.data;
        loading.value = false;
      }
    });
  }
};

// 初始化
const init = async () => {
  let activeNameParam = router.query.activeName;
  if (activeNameParam != null && activeNameParam != '') {
    activeName.value = activeNameParam as string;
  }
  // 获取设备信息
  form.value.deviceId = router.query && router.query.deviceId;
  if (form.value.deviceId != 0) {
    // this.connectMqtt();
    getDeviceInfo(form.value.deviceId);
    // 确保DOM渲染完成后再加载地图
    nextTick(() => {
      if (form.value.deviceId !== 0 && document.getElementById('map')) {
        setTimeout(() => {
          loadMapWithCoord();
        }, 300);
      }
    });
  }
  // 初始化权限标识（通过 composable 统一管理）
  initPermissions();
};

// 监听 deviceId 变化，自动销毁并重建地图
watch(
  () => form.value.deviceId,
  (newVal) => {
    if (newVal && newVal !== 0) {
      nextTick(() => {
        setTimeout(() => {
          destroyMap();
          loadMapWithCoord();
        }, 500);
      });
    } else {
      destroyMap();
    }
  }
);

// 在组件挂载时初始化
onMounted(() => {
  init();
  // 确保DOM渲染完成后再加载地图
  nextTick(() => {
    if (form.value.deviceId !== 0 && document.getElementById('map')) {
      setTimeout(() => {
        loadMapWithCoord();
      }, 300);
    }
  });
});

onActivated(() => {
  // 跳转选项卡
  let activeNameParam = router.query.activeName;
  if (activeNameParam != null && activeNameParam != '') {
    activeName.value = activeNameParam as string;
  }
});

onBeforeUnmount(() => {
  // 取消订阅主题
  mqttUnSubscribe(form.value);
  destroyMap();
  if (channelRef.value && channelRef.value.closeDestroy) {
    channelRef.value.closeDestroy(false);
  }
});

function parseExtSpec(item: any) {
  try {
    return item?.spec ? JSON.parse(item.spec) : {};
  } catch {
    return {};
  }
}

function getExtOptions(item: any) {
  const spec = parseExtSpec(item);
  return Array.isArray(spec.option) ? spec.option : Array.isArray(spec.options) ? spec.options : [];
}

function getExtPrecision(item: any) {
  return parseExtSpec(item).mode === 'decimal' ? 2 : 0;
}

defineExpose({
  form,
  activeName,
  goBack,
  reset,
  handleSubmitForm,
});
</script>

<style lang="scss" scoped>
.device-edit {
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

        .status {
          position: relative;
          margin-left: 4px;

          .dot {
            position: absolute;
            top: 3px;
            left: -8px;
            width: 4px;
            height: 4px;
            border-radius: 50%;
          }
        }
      }

      .diagnostics-button {
        margin-left: 18px;
      }
    }
  }

  .custom-tabs {
    .submit-btn-wrap {
      width: 100%;
      display: flex;
      justify-content: center;
      align-items: center;
      margin-top: 80px;
      gap: 12px;
    }

    .basic-span {
      margin-top: 16px;
    }

    .ext-section {
      .ext-section-title {
        display: inline-block;
        margin: 0 10px 16px;
        font-size: 18px;
        font-weight: 500;
      }

      .ext-inline-form {
        margin: 0;
      }
    }

    .map-xl-only {
      display: none;
    }

    .map-below-on-non-xl {
      display: block;
    }

    @media (min-width: 1920px) {
      .map-xl-only {
        display: block;
      }

      .map-below-on-non-xl {
        display: none;
      }
    }

    .map-section {
      .map-wrap {
        border: 1px solid #dfe4ed;
        border-radius: 5px;
        padding: 5px;
      }

      .map-canvas {
        width: 100%;
        height: 435px;
      }
    }

    :deep(.el-card__body) {
      padding: 0 20px;
    }

    :deep(.el-tabs__active-bar) {
      background-color: transparent;
    }

    :deep(.el-tabs__nav) {
      margin-bottom: 12px;
    }

    :deep(.el-tabs__item) {
      padding: 0px 25px !important;
      box-sizing: border-box;
      display: inline-block;
      list-style: none;
      font-size: 14px;
      font-weight: 500;
      color: #303133;
      position: relative;
    }

    :deep(.el-tabs__item.is-active) {
      color: #fff;
      background-color: var(--el-color-primary);
      border-radius: 4px;
      height: 32px;
      line-height: 34px;
    }

    .alert-wrap {
      height: 35px;
      margin-top: 10px;

      :deep(.el-alert__icon) {
        font-size: 16px;
        width: 16px;
      }

      :deep(.el-alert__description) {
        font-size: 12px;
        margin: 0;
      }
    }
  }
}

.access-diagnostics-drawer {
  .drawer-header {
    width: 100%;
    display: flex;
    align-items: center;
    justify-content: space-between;
    gap: 12px;
  }

  .diagnostics-content {
    display: flex;
    flex-direction: column;
    gap: 16px;
  }

  .status-grid {
    width: 100%;
  }

  .status-panel {
    min-height: 92px;
    border: 1px solid var(--el-border-color-light);
    border-radius: 6px;
    padding: 12px;
    background: var(--el-fill-color-lighter);

    .label {
      margin-bottom: 10px;
      color: var(--el-text-color-secondary);
      font-size: 13px;
    }

    .time {
      margin-top: 10px;
      color: var(--el-text-color-secondary);
      font-size: 12px;
    }
  }

  .section-title {
    font-weight: 600;
    color: var(--el-text-color-primary);
  }

  .diagnosis-list {
    display: flex;
    flex-direction: column;
    gap: 10px;
  }

  .diagnosis-item {
    border: 1px solid var(--el-border-color-light);
    border-radius: 6px;
    padding: 12px;
    background: var(--el-bg-color);
  }

  .diagnosis-title {
    display: flex;
    align-items: center;
    gap: 8px;
    font-weight: 600;
  }

  .diagnosis-suggestion {
    margin-top: 8px;
    color: var(--el-text-color-regular);
    line-height: 1.6;
  }

  .diagnosis-evidence {
    margin-top: 6px;
    color: var(--el-text-color-secondary);
    font-size: 12px;
  }

  .redis-keys {
    :deep(.el-descriptions__label) {
      width: 90px;
    }
  }

  .diagnostics-tabs {
    :deep(.el-tabs__content) {
      min-height: 300px;
    }
  }

  .raw-json {
    max-height: 360px;
    overflow: auto;
    border: 1px solid var(--el-border-color-light);
    border-radius: 6px;
    padding: 8px;
  }
}

/* 简单标记样式 */
.simple-marker {
  position: absolute;
  width: 20px;
  height: 20px;
  background-color: #1890ff;
  border: 2px solid #fff;
  border-radius: 50%;
  box-shadow: 0 2px 6px rgba(0, 0, 0, 0.3);
  cursor: pointer;
  z-index: 1000;
  transform: translate(-50%, -50%);
}
.qr-dialog {
  width: 100%;

  .title-wrap {
    width: 100%;
    position: relative;

    .name {
      width: 100%;
    }

    .btn {
      position: absolute;
      cursor: pointer;
      top: 0;
      right: 10px;
      font-size: 14px;
      color: #909399;
    }
  }

  :deep(.el-dialog__body) {
    padding: 2px 20px 20px;
  }
}
</style>
