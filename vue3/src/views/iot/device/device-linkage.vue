<template>
  <el-row :gutter="20">
    <!--机构-设备数据-->
    <el-col :span="6" :xs="24">
      <el-card style="margin: 20px 0 20px 20px">
        <div class="menu-wrap">
          <el-input
            v-model="deptName"
            :placeholder="$t('device.device-linkage.188958-0')"
            clearable
            size="small"
            :prefix-icon="Search"
            style="margin-bottom: 10px; margin-right: 10px"
          />
          <el-tree
            v-if="deptOptions && deptOptions.length > 0"
            :data="deptOptions"
            :props="defaultProps"
            :expand-on-click-node="false"
            :filter-node-method="filterNode"
            ref="treeRef"
            node-key="id"
            default-expand-all
            highlight-current
            @node-click="handleNodeClick"
          />
          <el-empty :description="$t('noData')" v-else></el-empty>
        </div>
      </el-card>
      <el-card style="margin: 20px 0 20px 20px">
        <div style="height: 445px">
          <el-radio-group v-model="queryParams.status" size="small" style="margin-bottom: 10px" @change="getDeviceList">
            <el-radio-button :value="''">{{ $t('device.device-linkage.188958-1') }}</el-radio-button>
            <el-radio-button :value="3">{{ $t('device.device-linkage.188958-2') }}</el-radio-button>
            <el-radio-button :value="4">{{ $t('device.device-linkage.188958-3') }}</el-radio-button>
          </el-radio-group>
          <el-input
            v-model="queryParams.deviceName"
            :placeholder="$t('device.device-edit.148398-2')"
            clearable
            size="small"
            :prefix-icon="Search"
            style="margin-bottom: 10px; margin-right: 10px"
            @keydown.enter="getDeviceList"
          />
          <el-menu
            class="menu-wrap"
            ref="deviceMenuRef"
            :default-active="activeDeviceId"
            :default-openeds="defaultOpeneds"
            @open="handleOpen"
            style="margin-right: 10px"
            v-if="devTotal > 0"
          >
            <el-sub-menu index="1" style="margin-left: -5px">
              <template #title>
                <el-icon><Menu /></el-icon>
                <span style="font-size: 14px; padding: 0">{{ $t('device.device-linkage.188958-5') }}</span>
              </template>
              <el-menu-item
                v-for="item in deviceList"
                :key="item.deviceId"
                :index="item.deviceId + ''"
                class="custom-menu-item"
                :label="item.deviceName"
                :value="item.deviceId"
                @click="handleTypeClick(item.deviceId)"
              >
                {{ item.deviceName }}
              </el-menu-item>
            </el-sub-menu>
          </el-menu>
          <div class="empty-wrap">
            <el-empty :description="$t('device.index.105953-41')" v-if="devTotal == 0"></el-empty>
          </div>
        </div>
      </el-card>
    </el-col>

    <el-col :span="18" :xs="24">
      <el-card style="margin: 20px 20px 20px 0">
        <div class="head-container">
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
              <el-form
                class="basic-span"
                ref="formRef"
                :model="form"
                :rules="rules"
                label-width="100px"
                v-if="devTotal > 0"
              >
                <el-row :gutter="20">
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
                    <el-form-item :label="$t('device.device-edit.148398-7')" prop="serialNumber">
                      <el-input
                        v-model="form.serialNumber"
                        :placeholder="$t('device.device-edit.148398-8')"
                        maxlength="32"
                        :disabled="form.status != 1"
                        :readonly="isGb28181Device"
                      >
                        <template #append>
                          <el-button
                            v-if="!isGb28181Device"
                            @click="generateNum"
                            :loading="genDisabled"
                            :disabled="form.status != 1"
                            v-hasPermi="['iot:device:add']"
                          >
                            {{ $t('device.device-edit.148398-9') }}
                          </el-button>
                          <el-button
                            v-else
                            @click="genSipID()"
                            :disabled="form.status != 1"
                            v-hasPermi="['iot:device:add']"
                          >
                            {{ $t('device.device-edit.148398-9') }}
                          </el-button>
                        </template>
                      </el-input>
                      <el-alert
                        v-if="openServerTip"
                        class="alert-wrap"
                        type="info"
                        show-icon
                        :description="$t('device.device-edit.148398-10')"
                      ></el-alert>
                    </el-form-item>
                    <el-form-item :label="$t('device.device-edit.148398-12')" prop="firmwareVersion">
                      <el-input
                        v-model="form.firmwareVersion"
                        :placeholder="$t('device.device-edit.148398-13')"
                        type="number"
                        step="0.1"
                        :disabled="form.status != 1 || form.deviceType === 3"
                      >
                        <template #prepend>Version</template>
                      </el-input>
                    </el-form-item>
                    <!-- 设备影子 -->
                    <el-form-item
                      v-if="form.deviceType !== 3"
                      :label="$t('device.device-edit.148398-15')"
                      prop="isShadow"
                    >
                      <el-radio-group v-model="form.isShadow">
                        <el-radio v-for="dict in device_shadow" :key="dict.value" :value="Number(dict.value)">
                          {{ dict.label }}
                        </el-radio>
                      </el-radio-group>
                    </el-form-item>
                    <el-form-item :label="$t('device.device-edit.148398-32')" prop="rssi">
                      <el-input v-model="form.rssi" :placeholder="$t('device.device-edit.148398-33')" readonly />
                    </el-form-item>
                    <el-form-item :label="$t('device.device-edit.148398-17')" prop="remark">
                      <el-input
                        v-model="form.remark"
                        type="textarea"
                        :autosize="{ minRows: 3, maxRows: 5 }"
                        :placeholder="$t('device.device-edit.148398-18')"
                      />
                    </el-form-item>
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
                      v-if="form.transport === 'MQTT'"
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
                    <el-form-item :label="$t('device.device-edit.148398-34')" prop="remark" v-if="form.deviceId != 0">
                      <dict-tag
                        :options="iot_device_status"
                        effect="plain"
                        :value="form.status"
                        style="display: inline-block; margin-right: 10px"
                      />
                      <el-button size="small" @click="handleViewMqtt()">
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
                  </el-col>
                  <el-col :xs="24" :sm="24" :md="24" :lg="12" :xl="8" v-if="form.deviceId != 0">
                    <div style="border: 1px solid #dfe4ed; border-radius: 5px; padding: 5px; margin-left: 20px">
                      <div id="map" style="height: 400px; width: 100%">{{ $t('device.device-edit.148398-37') }}</div>
                    </div>
                  </el-col>
                </el-row>
              </el-form>
              <div class="submit-btn-wrap" v-if="devTotal > 0">
                <el-button
                  type="primary"
                  @click="submitForm"
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

            <el-tab-pane name="runningStatus" v-if="form.deviceType !== 3 && devTotal > 0" lazy>
              <template #label>{{ $t('device.device-edit.148398-42') }}</template>
              <running-status ref="runningStatusRef" :device="form" @statusEvent="getDeviceStatusData($event)" />
            </el-tab-pane>
            <el-tab-pane name="variable" v-if="form.deviceType !== 3 && devTotal > 0" lazy>
              <template #label>{{ $t('device.device-edit.148398-74') }}</template>
              <device-variable ref="deviceVariableRef" :device="form" />
            </el-tab-pane>
            <el-tab-pane name="instructionParsing" v-if="form.deviceType !== 3 && devTotal > 0" lazy>
              <template #label>{{ $t('device.device-edit.148398-76') }}</template>
              <instruction-parsing ref="instructionParsingRef" :device="form" />
            </el-tab-pane>
            <el-tab-pane
              name="deviceSub"
              v-if="
                form.deviceType == 2 &&
                devTotal > 0 &&
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
            <el-tab-pane name="deviceModbusTask" v-if="form.canConfigPoll && devTotal > 0" lazy>
              <template #label>{{ $t('device.device-edit.148398-77') }}</template>
              <device-modbus-task ref="deviceModbusTaskRef" :device="form" />
            </el-tab-pane>
            <!-- 指令下发 -->
            <el-tab-pane
              name="deviceInstructionsConfig"
              v-if="
                (form.deviceType === 1 || form.deviceType === 2) && form.protocolCode == 'MODBUS-TCP' && devTotal > 0
              "
              lazy
            >
              <template #label>{{ $t('device.device-edit.148398-106') }}</template>
              <device-instruction-config ref="deviceInstructionsConfigRef" :device="form" />
            </el-tab-pane>

            <el-tab-pane name="scada" v-if="form.deviceType !== 3 && isShowScada && devTotal > 0" lazy>
              <template #label>{{ $t('device.device-edit.148398-73') }}</template>
              <device-scada ref="deviceScadaRef" :device="form" />
            </el-tab-pane>

            <el-tab-pane name="deviceMonitor" v-if="form.deviceType !== 3 && devTotal > 0">
              <template #label>{{ $t('device.device-edit.148398-51') }}</template>
              <device-monitor ref="deviceMonitorRef" :device="form" />
            </el-tab-pane>

            <el-tab-pane name="deviceStastic" v-if="form.deviceType !== 3 && devTotal > 0">
              <template #label>{{ $t('device.device-edit.148398-52') }}</template>
              <device-statistic ref="deviceStatisticRef" :device="form" />
            </el-tab-pane>

            <el-tab-pane name="sipChannel" v-if="form.deviceType === 3 && devTotal > 0" lazy>
              <template #label>{{ $t('device.device-edit.148398-44') }}</template>
              <channel ref="channelRef" :device="form" />
            </el-tab-pane>
            <el-tab-pane name="deviceTimer" v-if="form.deviceType !== 3 && devTotal > 0" lazy>
              <template #label>{{ $t('device.device-edit.148398-47') }}</template>
              <device-timer ref="deviceTimerRef" :device="form" />
            </el-tab-pane>

            <el-tab-pane name="deviceLog" v-if="form.deviceType !== 3 && devTotal > 0" lazy>
              <template #label>{{ $t('device.device-edit.148398-49') }}</template>
              <device-log ref="deviceLogRef" :device="form" />
            </el-tab-pane>

            <el-tab-pane name="deviceFuncLog" v-if="form.deviceType !== 3 && devTotal > 0" lazy>
              <template #label>{{ $t('device.device-edit.148398-50') }}</template>
              <device-func-log ref="deviceFuncLogRef" :device="form" />
            </el-tab-pane>

            <el-tab-pane
              name="alertUser"
              v-hasPermi="['iot:device:alert:user:list']"
              v-if="form.deviceType !== 3 && devTotal > 0"
            >
              <template #label>{{ $t('device.device-edit.148398-80') }}</template>
              <alert-user ref="alertUserRef" :device="form" />
            </el-tab-pane>

            <el-tab-pane name="inlineVideo" v-if="form.deviceType !== 3 && devTotal > 0" lazy>
              <template #label>{{ $t('device.device-edit.148398-75') }}</template>
              <device-inline-video ref="deviceInlineVideoRef" :sipRelationList="form.sipRelationVOList" />
            </el-tab-pane>

            <el-tab-pane name="deviceAlert" v-hasPermi="['iot:alertLog:list']" lazy v-if="devTotal > 0">
              <template #label>{{ $t('device.device-edit.148398-81') }}</template>
              <device-alert ref="deviceAlertRef" :device="form" />
            </el-tab-pane>

            <el-tab-pane name="deviceUser" lazy v-if="devTotal > 0">
              <template #label>{{ $t('device.device-edit.148398-48') }}</template>
              <device-user ref="deviceUserRef" :device="form" @userEvent="getUserData($event)" />
            </el-tab-pane>
          </el-tabs>
          <el-empty :description="$t('device.index.105953-41')" v-if="devTotal == 0"></el-empty>

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
              <el-form-item label="enrollPackage" prop="enrollPackage">
                <el-input clearable v-model="listQuery.enrollPackage" readonly style="width: 400px"></el-input>
              </el-form-item>
              <el-form-item label="port" prop="port">
                <el-input clearable v-model="listQuery.port" readonly style="width: 400px"></el-input>
              </el-form-item>
            </el-form>
            <el-form
              ref="httpFormRef"
              :model="httpForm"
              :rules="rules"
              label-width="120px"
              v-if="form.transport == 'HTTP'"
            >
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
            <el-form
              ref="GBformRef"
              :model="GBform"
              :rules="rules"
              label-width="120px"
              v-if="form.transport == 'GB28181'"
            >
              <el-form-item :label="$t('device.device-edit.148398-87')">
                <el-input clearable v-model="GBform.domainAlias" readonly style="width: 400px"></el-input>
              </el-form-item>
              <el-form-item :label="$t('device.device-edit.148398-88')">
                <el-input clearable v-model="GBform.serverSipid" readonly style="width: 400px"></el-input>
              </el-form-item>
              <el-form-item :label="$t('device.device-edit.148398-89')">
                <el-input clearable v-model="GBform.password" readonly style="width: 400px"></el-input>
              </el-form-item>
              <el-form-item :label="$t('device.device-edit.148398-90')">
                <el-input clearable v-model="GBform.port" readonly style="width: 400px"></el-input>
              </el-form-item>
            </el-form>

            <template #footer>
              <div class="dialog-footer">
                <el-button class="btns" type="primary" @click="doCopy">
                  {{ $t('device.device-edit.148398-59') }}
                </el-button>
                <el-button @click="closeSummaryDialog">{{ $t('device.device-edit.148398-57') }}</el-button>
              </div>
            </template>
          </el-dialog>
        </div>
      </el-card>
    </el-col>
  </el-row>
</template>

<script setup lang="ts">
import { JsonViewer } from 'vue3-json-viewer';
import {
  ref,
  reactive,
  computed,
  watch,
  onMounted,
  onActivated,
  onBeforeUnmount,
  nextTick,
  getCurrentInstance,
} from 'vue';
import { useRoute } from 'vue-router';
import { useI18n } from 'vue-i18n';
import { Search, Menu, Printer } from '@element-plus/icons-vue';
import { useDict } from '@/utils/dict/useDict';

import productList from './product-list.vue';
import channel from '../sip/channel.vue';
import sipid from '../sip/sipidGen.vue';
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
import deviceScada from './device-scada.vue';
import deviceVariable from './device-variable-card.vue';
import deviceInlineVideo from './device-inline-video.vue';
import deviceFuncLog from './device-functionlog.vue';
import deviceSub from './device-sub.vue';

import { loadTianDiTu } from '@/utils/map.js';
import {
  getDevice,
  updateDevice,
  generatorDeviceNum,
  getMqttConnect,
  listDeviceShort,
  getDeviceRunningStatus,
  getSipConfig,
  getHttpConfig,
} from '@/api/iot/device';
import { cacheJsonThingsModel } from '@/api/iot/model';
import { getDeviceTemp } from '@/api/iot/temp';
import { deptsTreeSelect } from '@/api/system/user';
import defaultSettings from '@/settings';
import vueQr from 'vue-qr';

const { proxy } = getCurrentInstance() as any;
const { t } = useI18n();
const route = useRoute();
const { iot_device_status, iot_location_way, device_shadow } = useDict(
  'iot_device_status',
  'iot_location_way',
  'device_shadow'
);

// Refs
const treeRef = ref();
const formRef = ref();
const productListRef = ref();
const sipidGenRef = ref();
const channelRef = ref();
const deviceMenuRef = ref();
const listQueryRef = ref();
const httpFormRef = ref();
const GBformRef = ref();
const runningStatusRef = ref();
const deviceVariableRef = ref();
const instructionParsingRef = ref();
const deviceSubRef = ref();
const deviceModbusTaskRef = ref();
const deviceInstructionsConfigRef = ref();
const deviceScadaRef = ref();
const deviceMonitorRef = ref();
const deviceStatisticRef = ref();
const deviceTimerRef = ref();
const deviceLogRef = ref();
const deviceFuncLogRef = ref();
const alertUserRef = ref();
const deviceInlineVideoRef = ref();
const deviceAlertRef = ref();
const deviceUserRef = ref();

// Data
const qrText = ref('fastbee');
const openSummary = ref(false);
const openCode = ref(false);
const deptName = ref('');
const openViewMqtt = ref(false);
const genDisabled = ref(false);
const activeName = ref('basic');
const mqttList = ref<any[]>([]);
const loading = ref(true);
const oldDeviceStatus = ref<any>(null);
const deviceId = ref<any>(0);
const channelId = ref('');
const devTotal = ref<any>('');
const form = ref<any>({
  productId: 0,
  status: 1,
  locationWay: 1,
  firmwareVersion: 1.0,
  serialNumber: '',
  deviceType: 1,
  transport: null,
  isSimulate: 0,
});
const defaultOpeneds = ref(['1']);
const defaultProps = reactive({
  children: 'children',
  label: 'label',
});
const queryParams = reactive<any>({
  pageNum: 1,
  pageSize: 999,
  deptId: undefined,
  status: '',
  deviceName: '',
  showChild: false,
});
const deviceList = ref<any[]>([]);
const deptOptions = ref<any>(undefined);
const listQuery = ref<any>({
  clientId: 0,
  username: '',
  passwd: '',
  port: '',
});
const GBform = ref<any>({
  domainAlias: '',
  serverSipid: '',
  password: '',
  port: '',
});
const httpForm = ref<any>({
  type: '',
  username: '',
  password: '',
  clientId: '',
  port: '',
});
const openTip = ref(false);
const openServerTip = ref(false);
const serverType = ref(1);
const isSubDev = ref(false);
const summary = ref<any[]>([]);
const mapInstance = ref<any>(null);
const mk = ref<any>(null);
const markerDiv = ref<any>(null);
const isShowScada = ref(defaultSettings.isShowScada);
const activeDeviceId = ref('');
let mqttMessageHandler: ((topic: string, message: any) => void) | null = null;
const rules = reactive<any>({
  deviceName: [
    { required: true, message: t('device.device-linkage.188958-60'), trigger: 'blur' },
    { min: 2, max: 32, message: t('device.device-linkage.188958-61'), trigger: 'blur' },
  ],
  firmwareVersion: [{ required: true, message: t('device.device-linkage.188958-62'), trigger: 'blur' }],
});

// Computed
const deviceStatus = computed({
  get() {
    return form.value.status == 2 ? 1 : 0;
  },
  set(val: number) {
    if (val == 1) {
      form.value.status = 2;
    } else if (val == 0) {
      form.value.status = 4;
    } else {
      form.value.status = oldDeviceStatus.value;
    }
  },
});

// Watch
watch(deptName, (val) => {
  treeRef.value?.filter(val);
});

watch(deptOptions, (val) => {
  if (val && val.length > 0) {
    nextTick(() => {
      treeRef.value?.setCurrentKey(val[0].id);
      handleNodeClick(val[0]);
    });
  }
});

// Methods
async function connectMqtt() {
  if (proxy?.$mqttTool?.client == null) {
    await proxy?.$mqttTool?.connect(proxy?.vuex_token);
  }
  mqttCallback();
}

function mqttCallback() {
  const client = proxy?.$mqttTool?.client;
  if (!client) return;

  if (mqttMessageHandler) {
    client.off?.('message', mqttMessageHandler);
  }

  mqttMessageHandler = (topic: string, message: any) => {
    const topics = topic.split('/');
    const deviceNum = topics[2];
    if (message instanceof Uint8Array) {
      const decoder = new TextDecoder('utf-8');
      message = decoder.decode(message);
    }

    try {
      message = JSON.parse(message.toString());
    } catch {
      return;
    }

    if (!message) return;

    if (topics[3] === 'status' || topics[2] === 'status') {
      if (form.value.serialNumber === deviceNum) {
        oldDeviceStatus.value = message.status;
        form.value.status = message.status;
        form.value.isShadow = message.isShadow;
        form.value.rssi = message.rssi;
      }
    }
    if (topic.endsWith('ws/service')) {
      proxy?.$busEvent?.$emit('updateData', {
        serialNumber: topics[2],
        productId: form.value.productId,
        data: message,
      });
    }
    if (topic.endsWith('service/reply')) {
      proxy?.$busEvent?.$emit('updateLog', {
        serialNumber: topics[2],
        productId: form.value.productId,
        data: message,
      });
    }
    if (topic.endsWith('message/post')) {
      proxy?.$busEvent?.$emit('updateMqttMessage', {
        serialNumber: topics[2],
        data: message,
      });
    }
  };

  client.on('message', mqttMessageHandler);
}

function mqttSubscribe(device: any) {
  const d = Array.isArray(device) ? device[0] : device;
  if (!d?.productId || !d?.serialNumber) return;
  const base = '/' + d.productId + '/' + d.serialNumber;
  const topics = [
    base + '/ws/service',
    base + '/status/post',
    base + '/function/post',
    base + '/monitor/post',
    base + '/service/reply',
  ];
  proxy?.$mqttTool?.subscribe(topics);
}

function mqttUnSubscribe(device: any) {
  if (!device?.productId || !device?.serialNumber) return;
  const base = '/' + device.productId + '/' + device.serialNumber;
  const topics = [
    base + '/ws/service',
    base + '/status/post',
    base + '/function/post',
    base + '/monitor/post',
    base + '/service/reply',
  ];
  proxy?.$mqttTool?.unsubscribe(topics);
}

function getDeviceStatusData(status: any) {
  form.value.status = status;
}

function handleOpen(index: string) {
  defaultOpeneds.value = [index];
}

function filterNode(value: string, data: any) {
  if (!value) return true;
  return data.label.indexOf(value) !== -1;
}

function getDeptTree() {
  deptsTreeSelect().then((response: any) => {
    deptOptions.value = response.data;
  });
}

function handleNodeClick(data: any) {
  queryParams.deptId = data.id;
  getDeviceList();
}

function getDeviceList() {
  loading.value = true;
  listDeviceShort(queryParams).then((response: any) => {
    deviceList.value = response.rows;
    devTotal.value = response.total;
    if (deviceList.value && deviceList.value.length > 0) {
      activeDeviceId.value = deviceList.value[0].deviceId + '';
      handleTypeClick(deviceList.value[0].deviceId);
    } else {
      activeDeviceId.value = '';
      form.value = {};
    }
    if (deviceId.value === 0 && deviceList.value.length > 0) {
      deviceId.value = deviceList.value[0].deviceId;
    }
    getDeviceDetail();
    if (deviceList.value && deviceList.value.length > 0) {
      mqttSubscribe(deviceList.value);
    }
    loading.value = false;
  });
}

function tabChange(panel: any) {
  nextTick(() => {
    if (panel.paneName === 'basic') {
      loadMap();
    }
    if (form.value.deviceType == 3 && panel.paneName !== 'deviceReturn') {
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
}

function handleTypeClick(id: any) {
  deviceId.value = id;
  activeName.value = 'basic';
  getDeviceDetail();
}

function getDeviceDetail() {
  if (!deviceId.value) return;
  getDevice(deviceId.value).then(async (response: any) => {
    await getDeviceStatusWithThingsModel(response);
  });
}

function getCacheThingsModel(productId: any) {
  return new Promise((resolve, reject) => {
    cacheJsonThingsModel(productId)
      .then((response: any) => resolve(JSON.parse(response.data)))
      .catch((error: any) => reject(error));
  });
}

function getDeviceStatusApi(data: any) {
  const params = { deviceId: data.deviceId };
  return new Promise((resolve, reject) => {
    getDeviceRunningStatus(params)
      .then((response: any) => resolve(response.data.thingsModels))
      .catch((error: any) => reject(error));
  });
}

function formatThingsModel(data: any) {
  data.chartList = [];
  data.monitorList = [];
  data.staticList = [];
  if (!data.thingsModels || !Array.isArray(data.thingsModels)) {
    data.thingsModels = [];
  }
  data.thingsModels = data.thingsModels.sort((a: any, b: any) => a.order - b.order);

  let restart = true;
  while (restart) {
    restart = false;
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
        params: [] as any[],
        arrayParams: [] as any[],
        arrayModel: [] as any[],
      },
    };
    const hasElementsToProcess = data.thingsModels.some((item: any) => item.id.includes('_'));
    if (!hasElementsToProcess) break;

    for (let i = 0; i < data.thingsModels.length; i++) {
      const thingsModelId = data.thingsModels[i].id.split('_');
      if (data.thingsModels[i].datatype.parentType === null) continue;
      if (data.thingsModels[i].datatype.parentType !== null) {
        // 对象
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
        // 数组
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
        // 数组对象
        if (data.thingsModels[i]?.datatype?.parentType === '3') {
          if (model.id === '') {
            model.id = data.thingsModels[i].datatype.parentIdentifier;
            model.name = data.thingsModels[i].datatype.parentName;
            model.datatype.type = 'array';
          }
          if (model.id === data.thingsModels[i].datatype.parentIdentifier) {
            model.datatype.arrayType = 'object';
            if (data.thingsModels[i].name === '' || data.thingsModels[i].name === null)
              data.thingsModels[i].name = model.name;
            if (model.datatype.arrayParams.length === 0) {
              model.datatype.arrayParams[0] = [data.thingsModels[i]];
            } else {
              let hasMatched = false;
              model.datatype.arrayParams.some((subArray: any) => {
                const isMatch = subArray.some((param: any) => {
                  const obj = param.id.split('_');
                  return obj[1] === thingsModelId[1];
                });
                if (isMatch) {
                  subArray[subArray.length] = data.thingsModels[i];
                  hasMatched = true;
                  return true;
                }
                return false;
              });
              if (!hasMatched) {
                model.datatype.arrayParams[model.datatype.arrayParams.length] = [data.thingsModels[i]];
              }
            }
            data.thingsModels.splice(i, 1);
            i--;
            model.datatype.arrayCount++;
          }
        }
      }
    }
    if (model.id !== '') {
      data.thingsModels.push(model);
      restart = true;
    }
  }

  // 物模型格式化
  for (let i = 0; i < data.thingsModels.length; i++) {
    // 数字类型设置默认值并转换为数值
    if (data.thingsModels[i].datatype.type == 'integer' || data.thingsModels[i].datatype.type == 'decimal') {
      if (data.thingsModels[i].shadow == '') {
        data.thingsModels[i].shadow = Number(data.thingsModels[i].datatype.min);
      } else {
        data.thingsModels[i].shadow = Number(data.thingsModels[i].shadow);
      }
    }

    // 物模型分类放置
    if (data.thingsModels[i].datatype.type == 'array') {
      if (data.thingsModels[i].datatype.arrayType == 'object' && data.thingsModels[i].datatype.arrayParams.length > 0) {
        for (let k = 0; k < data.thingsModels[i].datatype.arrayParams.length; k++) {
          for (let j = 0; j < data.thingsModels[i].datatype.arrayParams[k].length; j++) {
            // 图表、实时监测、监测统计分类放置
            if (data.thingsModels[i].datatype.arrayParams[k][j].isChart == 1) {
              data.thingsModels[i].datatype.arrayParams[k][j].fatherName =
                data.thingsModels[i].datatype.arrayParams[k][j].datatype.parentIndexName;
              data.thingsModels[i].datatype.arrayParams[k][j].datatype.arrayType = 'object';
              const thingsModelId = data.thingsModels[i].datatype.arrayParams[k][j].id;
              if (data.chartList.length === 0) {
                data.chartList[0] = [data.thingsModels[i].datatype.arrayParams[k][j]];
              } else {
                let hasMatched = false;
                data.chartList.some((subArray: any) => {
                  const isMatch = subArray.some((param: any) => {
                    const obj = param.id ? param.id.split('_') : [];
                    const id = thingsModelId.split('_');
                    if (obj[1] && id[1]) {
                      return obj[1] === id[1] && data.thingsModels[i].id === param.datatype.parentIdentifier;
                    }
                    return false;
                  });
                  if (isMatch) {
                    subArray[subArray.length] = data.thingsModels[i].datatype.arrayParams[k][j];
                    hasMatched = true;
                    return true;
                  }
                  return false;
                });
                if (!hasMatched) {
                  data.chartList[data.chartList.length] = [data.thingsModels[i].datatype.arrayParams[k][j]];
                }
              }
              if (data.thingsModels[i].datatype.arrayParams[k][j].isHistory == 1) {
                data.staticList.push(data.thingsModels[i].datatype.arrayParams[k][j]);
              }
              if (data.thingsModels[i].datatype.arrayParams[k][j].isMonitor == 1) {
                data.monitorList.push(data.thingsModels[i].datatype.arrayParams[k][j]);
              }
              data.thingsModels[i].datatype.arrayParams[k].splice(j--, 1);
            }
          }
        }
      } else if (data.thingsModels[i].datatype.arrayCount > 0) {
        let values =
          data.thingsModels[i].value && data.thingsModels[i].value != '' ? data.thingsModels[i].value.split(',') : [];
        let shadows =
          data.thingsModels[i].shadow && data.thingsModels[i].shadow != ''
            ? data.thingsModels[i].shadow.split(',')
            : [];
        for (let j = 0; j < data.thingsModels[i].datatype.arrayCount; j++) {
          if (!data.thingsModels[i].datatype.arrayModel) {
            data.thingsModels[i].datatype.arrayModel = [];
          }
          let index = j > 9 ? String(j) : '0' + j;
          let prefix = 'array_' + index + '_';
          data.thingsModels[i].datatype.arrayModel[j] = {
            ...data.thingsModels[i].datatype.arrayModel[j],
            type: data.thingsModels[i].type,
            isReadonly: data.thingsModels[i].isReadonly,
          };
        }
      }
    } else if (data.thingsModels[i].datatype.type == 'object' && data.thingsModels[i].datatype.params.length > 0) {
      for (let j = 0; j < data.thingsModels[i].datatype.params.length; j++) {
        if (data.thingsModels[i].datatype.params[j].isChart == 1) {
          data.thingsModels[i].datatype.params[j].fatherName = data.thingsModels[i].name;
          const thingsModelId = data.thingsModels[i].datatype.params[j].id;
          if (data.chartList.length === 0) {
            data.chartList[0] = [data.thingsModels[i].datatype.params[j]];
          } else {
            let hasMatched = false;
            data.chartList.some((subArray: any) => {
              const isMatch = subArray.some((param: any) => {
                return data.thingsModels[i].id === param.datatype.parentIdentifier;
              });
              if (isMatch) {
                subArray[subArray.length] = data.thingsModels[i].datatype.params[j];
                hasMatched = true;
                return true;
              }
              return false;
            });
            if (!hasMatched) {
              data.chartList[data.chartList.length] = [data.thingsModels[i].datatype.params[j]];
            }
          }
          if (data.thingsModels[i].datatype.params[j].isHistory == 1) {
            data.staticList.push(data.thingsModels[i].datatype.params[j]);
          }
          if (data.thingsModels[i].datatype.params[j].isMonitor == 1) {
            data.monitorList.push(data.thingsModels[i].datatype.params[j]);
          }
          data.thingsModels[i].datatype.params.splice(j--, 1);
        }
      }
    } else if (data.thingsModels[i].isChart == 1) {
      data.thingsModels[i].fatherName = data.thingsModels[i].name;
      if (data.chartList.length === 0) {
        data.chartList[0] = [data.thingsModels[i]];
      } else {
        data.chartList.some((subArray: any) => {
          subArray[subArray.length] = data.thingsModels[i];
        });
      }
      if (data.thingsModels[i].isHistory == 1) {
        data.staticList.push(data.thingsModels[i]);
      }
      if (data.thingsModels[i].isMonitor == 1) {
        data.monitorList.push(data.thingsModels[i]);
      }
      data.thingsModels.splice(i--, 1);
    }
  }

  // 整理数据
  for (let i = 0; i < data.thingsModels.length; i++) {
    if (data.thingsModels[i].datatype.type == 'array') {
      if (data.thingsModels[i].datatype.arrayType == 'object' && data.thingsModels[i].datatype.arrayParams.length > 0) {
        data.thingsModels[i].datatype.arrayParams = data.thingsModels[i].datatype.arrayParams.filter(
          (item: any) => item.length > 0
        );
      }
      if (
        data.thingsModels[i].datatype.arrayType == 'object' &&
        data.thingsModels[i].datatype.arrayParams.length === 0
      ) {
        data.thingsModels.splice(i--, 1);
      }
    } else if (data.thingsModels[i].datatype.type == 'object') {
      if (data.thingsModels[i].datatype.params.length === 0) {
        data.thingsModels.splice(i--, 1);
      }
    }
  }
}

function loadMap() {
  loadTianDiTu()
    .then(() => {
      getTianDiTuMap();
    })
    .catch((error: any) => {
      console.error('天地图加载失败:', error);
    });
}

function getTianDiTuMap() {
  const mapContainer = document.getElementById('map');
  if (!mapContainer) return;
  mapContainer.innerHTML = '';
  mapContainer.style.minHeight = '435px';
  mapContainer.style.position = 'relative';
  try {
    if (!(window as any).T || !(window as any).T.Map) return;
    mapInstance.value = new (window as any).T.Map('map', { minZoom: 3, maxZoom: 19, zoom: 15, enableAnimation: true });
    let centerLng = 116.404,
      centerLat = 39.915;
    if (form.value.longitude && form.value.latitude) {
      try {
        centerLng = parseFloat(form.value.longitude);
        centerLat = parseFloat(form.value.latitude);
        if (isNaN(centerLng) || isNaN(centerLat)) {
          centerLng = 116.404;
          centerLat = 39.915;
        }
      } catch (e) {
        /* use default */
      }
    }
    const centerPoint = new (window as any).T.LngLat(centerLng, centerLat);
    mapInstance.value.centerAndZoom(centerPoint, 15);
    mapInstance.value.enableScrollWheelZoom(true);
    if ((window as any).T.Control?.Zoom) {
      try {
        mapInstance.value.addControl(
          new (window as any).T.Control.Zoom({ position: (window as any).T_ANCHOR_TOP_RIGHT })
        );
      } catch (e) {
        /* ignore */
      }
    }
    if ((window as any).T.Control?.Scale) {
      try {
        mapInstance.value.addControl(new (window as any).T.Control.Scale());
      } catch (e) {
        /* ignore */
      }
    }
    addMarkerCompatible(centerPoint);
    setTimeout(() => {
      mapInstance.value?.panTo?.(centerPoint);
      mapInstance.value?.setCenter?.(centerPoint);
    }, 300);
  } catch (error) {
    console.error('天地图初始化失败:', error);
  }
}

function addMarkerCompatible(point: any) {
  if (!mapInstance.value || !point) return;
  if (mk.value) {
    try {
      mapInstance.value.removeOverlay?.(mk.value) || mapInstance.value.removeLayer?.(mk.value);
    } catch (e) {
      /* ignore */
    }
  }
  try {
    let marker = null;
    if ((window as any).T?.Marker) {
      try {
        marker = new (window as any).T.Marker(point);
      } catch (e) {
        /* ignore */
      }
    }
    if (marker) {
      if (typeof mapInstance.value.addOverlay === 'function') mapInstance.value.addOverlay(marker);
      else if (typeof mapInstance.value.addLayer === 'function') mapInstance.value.addLayer(marker);
      mk.value = marker;
    }
  } catch (error) {
    console.error('添加标记失败:', error);
  }
}

function destroyTianDiTuMap() {
  try {
    if (mapInstance.value?.destroy) {
      if (mk.value) mapInstance.value.removeOverlay?.(mk.value);
      if (markerDiv.value?.parentNode) markerDiv.value.parentNode.removeChild(markerDiv.value);
      mapInstance.value.destroy();
      const mapContainer = document.getElementById('map');
      if (mapContainer) mapContainer.innerHTML = '';
    }
  } catch (err) {
    /* ignore */
  } finally {
    mapInstance.value = null;
    mk.value = null;
    markerDiv.value = null;
  }
}

async function getDeviceStatusWithThingsModel(response: any) {
  response.data.cacheThingsModel = await getCacheThingsModel(response.data.productId);
  response.data.thingsModels = await getDeviceStatusApi(response.data);
  formatThingsModel(response.data);
  form.value = response.data;
  if (form.value.summary) summary.value = JSON.parse(form.value.summary);
  isSubDev.value = !!(form.value.subDeviceList?.length > 0);
  oldDeviceStatus.value = form.value.status;
  loadMap();
  connectMqtt();
  mqttSubscribe(form.value);
}

function selectProduct() {
  productListRef.value.open = true;
  productListRef.value.getList();
}

function genSipID() {
  if (!isGb28181Device.value) {
    return;
  }
  sipidGenRef.value.openDialog();
}

const isGb28181Device = computed(() => form.value.deviceType === 3 && form.value.transport === 'GB28181');

function getProductData(product: any) {
  form.value.productId = product.productId;
  form.value.productName = product.productName;
  form.value.deviceType = product.deviceType;
  form.value.transport = product.transport;
  getDeviceTempFn();
  form.value.tenantId = product.tenantId;
  form.value.tenantName = product.tenantName;
  if (product.transport === 'TCP') {
    openServerTip.value = true;
    serverType.value = 3;
  } else {
    openServerTip.value = false;
    serverType.value = 1;
  }
}

function getSipIDData(devsipid: string) {
  form.value.serialNumber = devsipid;
}

function getDeviceTempFn() {
  getDeviceTemp(form.value).then((response: any) => {
    openTip.value = !!(response.data && form.value.deviceType == 2);
  });
}

function getUserData(_user: any) {}

function openSummaryDialog() {
  const json = { type: 1, deviceNumber: form.value.serialNumber, productId: form.value.productId };
  qrText.value = JSON.stringify(json);
  openSummary.value = true;
}

function closeSummaryDialog() {
  openSummary.value = false;
  openViewMqtt.value = false;
}

function doCopy() {
  const input = document.createElement('input');
  if (form.value.transport === 'MQTT' || form.value.transport === 'COAP') {
    input.value = JSON.stringify({
      clientId: listQuery.value.clientId,
      username: listQuery.value.username,
      passwd: listQuery.value.passwd,
      subscribeTopic: listQuery.value.subscribeTopic,
      reportTopic: listQuery.value.reportTopic,
      port: listQuery.value.port,
    });
  } else if (form.value.transport === 'TCP') {
    input.value = JSON.stringify({ enrollPackage: listQuery.value.enrollPackage, port: listQuery.value.port });
  } else if (form.value.transport === 'HTTP') {
    input.value = JSON.stringify({
      clientId: httpForm.value.clientId,
      type: httpForm.value.type,
      username: httpForm.value.username,
      password: httpForm.value.password,
      port: httpForm.value.port,
    });
  }
  if (form.value.transport === 'GB28181') {
    input.value = JSON.stringify({
      domainAlias: GBform.value.domainAlias,
      serverSipid: GBform.value.serverSipid,
      password: GBform.value.password,
      port: GBform.value.port,
    });
  }
  document.body.appendChild(input);
  input.select();
  document.execCommand('Copy');
  document.body.removeChild(input);
  proxy.$modal.msgSuccess(t('device.device-edit.148398-71'));
}

function openCodeDialog() {
  const json = {
    type: 1,
    deviceNumber: form.value.serialNumber,
    productId: form.value.productId,
    productName: form.value.productName,
  };
  qrText.value = JSON.stringify(json);
  openCode.value = true;
}

function generateNum() {
  if (!form.value.productId || form.value.productId == 0) {
    proxy.$modal.alertError(t('device.device-linkage.188958-72'));
    return;
  }
  genDisabled.value = true;
  generatorDeviceNum({ type: serverType.value }).then((response: any) => {
    form.value.serialNumber = response.data;
    genDisabled.value = false;
  });
}

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

async function submitForm() {
  if (!form.value.serialNumber) {
    proxy.$modal.alertError(t('device.device-linkage.188958-65'));
    return;
  }
  if (!/^[0-9a-zA-Z]+$/.test(form.value.serialNumber)) {
    proxy.$modal.alertError(t('device.device-linkage.188958-66'));
    return;
  }
  if (!form.value.productId) {
    proxy.$modal.alertError(t('device.device-linkage.188958-67'));
    return;
  }
  formRef.value?.validate((valid: boolean) => {
    if (valid) {
      if (form.value.deviceId != 0) {
        updateDevice(form.value).then((response: any) => {
          if (response.data == 0) proxy.$modal.alertError(response.msg);
          else {
            proxy.$modal.alertSuccess(t('device.device-linkage.188958-68'));
            form.value = JSON.parse(JSON.stringify(form.value));
            loadMap();
          }
        });
      }
    }
  });
}

// Lifecycle
onMounted(() => {
  isSubDev.value = route.query.isSubDev == '1';
  getDeptTree();
  nextTick(() => {
    getDeviceList();
  });
});

onActivated(() => {
  const activeNameQuery = route.query.activeName as string;
  if (activeNameQuery) activeName.value = activeNameQuery;
});

onBeforeUnmount(() => {
  mqttUnSubscribe(form.value);
  const client = proxy?.$mqttTool?.client;
  if (client && mqttMessageHandler) {
    client.off?.('message', mqttMessageHandler);
    mqttMessageHandler = null;
  }
  destroyTianDiTuMap();
});
</script>

<style lang="scss" scoped>
.head-container {
  height: 852px;
  overflow-x: hidden;
  overflow-y: auto;
}

.menu-wrap {
  height: 345px;
  overflow-x: auto;
  overflow-y: auto;
  margin: 0 !important;
}

.menu-wrap :deep(.el-sub-menu__title) {
  padding-left: 0 !important;
}

.custom-menu-item {
  font-size: 12px;
  color: #000;
  margin-top: -10px;
  margin-left: -15px;
}
:deep(.el-sub-menu .el-menu-item) {
  height: 40px;
  line-height: 40px;
}
:deep(.el-menu-item.is-active) {
  background-color: #f0f3fe !important;
  color: #000 !important;
}
.tabs-container {
  display: flex;
  justify-content: space-around;
}
.custom-tabs {
  .basic-span {
    margin-top: 16px;
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
    background-color: #486ff2;
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

/* 缩小 empty 的图标大小 */
.empty-wrap {
  :deep(.el-empty__image) {
    width: 100px !important;
    height: 100px !important;
  }
  :deep(.el-empty__description) {
    font-size: 12px !important;
    line-height: 1.4 !important;
    color: #909399 !important;
  }
}

.submit-btn-wrap {
  margin-top: 70px;
  display: flex;
  justify-content: center;
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
      top: 0;
      right: 10px;
      cursor: pointer;
      font-size: 14px;
      color: #909399;
    }
  }

  :deep(.el-dialog__body) {
    padding: 2px 20px 20px;
  }
}
</style>
