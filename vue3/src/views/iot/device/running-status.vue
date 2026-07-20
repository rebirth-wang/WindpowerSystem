<template>
  <div class="running-status">
    <el-row :gutter="40">
      <el-col :xs="24" :sm="24" :md="24" :lg="12" :xl="10" class="status-col">
        <DescriptionsWithStyles :column="1" border :content-style="width" :label-style="statusColor">
          <!-- 设备模式-->
          <el-descriptions-item>
            <template #label>
              <el-icon><Menu /></el-icon>
              {{ $t('device.running-status.866086-0') }}
            </template>
            <span class="title">{{ title }}</span>
          </el-descriptions-item>
          <!-- 设备升级-->
          <el-descriptions-item>
            <template #label>
              <svg-icon icon-class="ota" />
              {{ $t('device.running-status.866086-1') }}
            </template>
            <el-button type="primary" size="small" :plain="true" @click="viewVersion()">
              {{ $t('device.running-status.866086-44') }}
            </el-button>
          </el-descriptions-item>

          <el-descriptions-item v-for="(item, index) in deviceInfo.thingsModels" :key="index">
            <template #label>
              <el-icon><Open /></el-icon>
              {{ item.name }}
            </template>
            <div v-if="item.datatype.type == 'bool'">
              <el-switch
                v-model="item.shadow"
                @change="mqttPublish(deviceInfo, item)"
                active-text=""
                inactive-text=""
                :active-value="1"
                :inactive-value="0"
                style="min-width: 100px"
                :disabled="shadowUnEnable || item.isReadonly == 1"
              />
            </div>
            <div v-if="item.datatype.type == 'enum'" class="emum-wrap">
              <div v-if="item.datatype.showWay && item.datatype.showWay == 'button'">
                <el-button
                  class="btn"
                  size="small"
                  @click="enumButtonClick(deviceInfo, item, subItem.value)"
                  v-for="subItem in item.datatype.enumList"
                  :key="subItem.value"
                  :disabled="shadowUnEnable || item.isReadonly == 1"
                  :class="{ 'is-active-btn': subItem.value === item.shadow }"
                >
                  {{ subItem.text }}
                </el-button>
              </div>
              <el-select
                v-else
                v-model="item.shadow"
                :placeholder="$t('device.running-status.866086-3')"
                @change="mqttPublish(deviceInfo, item)"
                :disabled="shadowUnEnable || item.isReadonly == 1"
              >
                <el-option
                  v-for="subItem in item.datatype.enumList"
                  :key="subItem.value"
                  :label="subItem.text"
                  :value="subItem.value"
                />
              </el-select>
            </div>
            <div v-if="item.datatype.type == 'string'">
              <el-input
                v-model="item.shadow"
                :placeholder="
                  item.datatype.unit
                    ? t('device.running-status.866086-5', [item.datatype.unit])
                    : t('device.running-status.866086-4')
                "
                :disabled="shadowUnEnable || item.isReadonly == 1"
              >
                <template #append>
                  <el-button
                    @click="mqttPublish(deviceInfo, item)"
                    :title="$t('device.running-status.866086-6')"
                    v-if="!shadowUnEnable && item.isReadonly == 0"
                  >
                    <el-icon><Promotion /></el-icon>
                  </el-button>
                </template>
              </el-input>
            </div>
            <div v-if="item.datatype.type == 'decimal'">
              <div
                style="
                  display: flex;
                  flex-direction: row;
                  justify-content: space-between;
                  align-items: center;
                  padding: 0 10px;
                  gap: 16px;
                "
              >
                <el-slider
                  v-model="item.shadow"
                  :min="item.datatype.min"
                  :max="item.datatype.max"
                  :step="item.datatype.step"
                  :format-tooltip="(x) => x + ' ' + item.datatype.unit"
                  :disabled="shadowUnEnable || item.isReadonly == 1"
                ></el-slider>
                <el-button
                  type="info"
                  size="small"
                  @click="mqttPublish(deviceInfo, item)"
                  :title="$t('device.running-status.866086-6')"
                  v-if="!shadowUnEnable && item.isReadonly == 0"
                >
                  <el-icon><Promotion /></el-icon>
                </el-button>
              </div>
            </div>
            <div v-if="item.datatype.type == 'integer'">
              <div
                style="
                  display: flex;
                  flex-direction: row;
                  justify-content: space-between;
                  align-items: center;
                  padding: 0 10px;
                  gap: 16px;
                "
              >
                <el-slider
                  v-model="item.shadow"
                  :min="item.datatype.min"
                  :max="item.datatype.max"
                  :step="item.datatype.step"
                  :format-tooltip="(x) => x + ' ' + item.datatype.unit"
                  :disabled="shadowUnEnable || item.isReadonly == 1"
                ></el-slider>
                <el-button
                  type="info"
                  size="small"
                  @click="mqttPublish(deviceInfo, item)"
                  :title="$t('device.running-status.866086-6')"
                  v-if="!shadowUnEnable && item.isReadonly == 0"
                >
                  <el-icon><Promotion /></el-icon>
                </el-button>
              </div>
            </div>
            <div v-if="item.datatype.type == 'object'">
              <el-descriptions :column="1" size="small" border>
                <el-descriptions-item
                  v-for="(param, index) in item.datatype.params"
                  :key="index"
                  :label="getParamLabel(item.name, param.name)"
                >
                  <div v-if="param.datatype.type == 'bool'">
                    <el-switch
                      v-model="param.shadow"
                      @change="mqttPublish(deviceInfo, param)"
                      active-text=""
                      inactive-text=""
                      :active-value="1"
                      :inactive-value="0"
                      style="min-width: 100px"
                      :disabled="shadowUnEnable || param.isReadonly == 1"
                    />
                  </div>
                  <div v-if="param.datatype.type == 'enum'">
                    <div v-if="param.datatype.showWay && param.datatype.showWay == 'button'">
                      <el-button
                        style="margin: 5px"
                        size="small"
                        @click="enumButtonClick(deviceInfo, param, subItem.value)"
                        v-for="subItem in param.datatype.enumList"
                        :key="subItem.value"
                        :disabled="shadowUnEnable || param.isReadonly == 1"
                        :class="{ 'is-active-btn': subItem.value === param.shadow }"
                      >
                        {{ subItem.text }}
                      </el-button>
                    </div>
                    <el-select
                      size="small"
                      v-else
                      v-model="param.shadow"
                      :placeholder="$t('device.running-status.866086-3')"
                      @change="mqttPublish(deviceInfo, param)"
                      :disabled="shadowUnEnable || param.isReadonly == 1"
                    >
                      <el-option
                        v-for="subItem in param.datatype.enumList"
                        :key="subItem.value"
                        :label="subItem.text"
                        :value="subItem.value"
                      />
                    </el-select>
                  </div>
                  <div v-if="param.datatype.type == 'string'">
                    <el-input
                      v-model="param.shadow"
                      :placeholder="$t('device.running-status.866086-4')"
                      :disabled="shadowUnEnable || param.isReadonly == 1"
                    >
                      <template #append>
                        <el-button
                          @click="mqttPublish(deviceInfo, param)"
                          :title="$t('device.running-status.866086-6')"
                          v-if="!shadowUnEnable && param.isReadonly == 0"
                        >
                          <el-icon><Promotion /></el-icon>
                        </el-button>
                      </template>
                    </el-input>
                  </div>
                  <div v-if="param.datatype.type == 'decimal'">
                    <el-input
                      v-model="param.shadow"
                      type="number"
                      :placeholder="$t('device.running-status.866086-7')"
                      :disabled="shadowUnEnable || param.isReadonly == 1"
                    >
                      <template #append>
                        <el-button
                          @click="mqttPublish(deviceInfo, param)"
                          :title="$t('device.running-status.866086-6')"
                          v-if="!shadowUnEnable && param.isReadonly == 0"
                        >
                          <el-icon><Promotion /></el-icon>
                        </el-button>
                      </template>
                    </el-input>
                  </div>
                  <div v-if="param.datatype.type == 'integer'">
                    <el-input
                      v-model="param.shadow"
                      type="integer"
                      :placeholder="$t('device.running-status.866086-8')"
                      :disabled="shadowUnEnable || param.isReadonly == 1"
                    >
                      <template #append>
                        <el-button
                          @click="mqttPublish(deviceInfo, param)"
                          :title="$t('device.running-status.866086-6')"
                          v-if="!shadowUnEnable && param.isReadonly == 0"
                        >
                          <el-icon><Promotion /></el-icon>
                        </el-button>
                      </template>
                    </el-input>
                  </div>
                </el-descriptions-item>
              </el-descriptions>
            </div>
            <div v-if="item.datatype.type == 'array' && item.datatype.arrayType != 'object'">
              <div v-if="item.datatype.arrayType == 'integer'">
                <el-button
                  type="primary"
                  plain
                  size="small"
                  @click="batchPublish(deviceInfo, item)"
                  style="margin-bottom: 10px"
                >
                  批量下发
                </el-button>
              </div>
              <el-descriptions :column="1" size="small" border v-if="item.datatype.arrayType != 'object'">
                <el-descriptions-item
                  v-for="(model, index) in item.datatype.arrayModel"
                  :key="index"
                  :label="model.name"
                >
                  <div v-if="item.datatype.arrayType == 'string'">
                    <el-input
                      :placeholder="$t('device.running-status.866086-4')"
                      size="small"
                      v-model="model.shadow"
                      :disabled="shadowUnEnable || item.isReadonly == 1"
                      @input="arrayItemChange($event, item)"
                    >
                      <template #append>
                        <el-button
                          @click="mqttPublish(deviceInfo, model)"
                          :title="$t('device.running-status.866086-6')"
                          v-if="!shadowUnEnable || item.isReadonly == 0"
                        >
                          <el-icon><Promotion /></el-icon>
                        </el-button>
                      </template>
                    </el-input>
                  </div>
                  <div v-if="item.datatype.arrayType == 'decimal'">
                    <el-input
                      type="number"
                      :placeholder="$t('device.running-status.866086-7')"
                      size="small"
                      v-model="model.shadow"
                      :disabled="shadowUnEnable || item.isReadonly == 1"
                      @input="arrayItemChange($event, item)"
                    >
                      <template #append>
                        <el-button
                          @click="mqttPublish(deviceInfo, model)"
                          :title="$t('device.running-status.866086-6')"
                          v-if="!shadowUnEnable || item.isReadonly == 0"
                        >
                          <el-icon><Promotion /></el-icon>
                        </el-button>
                      </template>
                    </el-input>
                  </div>
                  <div v-if="item.datatype.arrayType == 'integer'">
                    <el-input
                      type="integer"
                      :placeholder="$t('device.running-status.866086-8')"
                      size="small"
                      v-model="model.shadow"
                      :disabled="shadowUnEnable || item.isReadonly == 1"
                      @input="arrayItemChange($event, item)"
                    >
                      <template #append>
                        <el-button
                          @click="mqttPublish(deviceInfo, model)"
                          :title="$t('device.running-status.866086-6')"
                          v-if="!shadowUnEnable && item.isReadonly == 0"
                        >
                          <el-icon><Promotion /></el-icon>
                        </el-button>
                      </template>
                    </el-input>
                  </div>
                </el-descriptions-item>
              </el-descriptions>
            </div>
            <el-collapse v-if="item.datatype.arrayType == 'object'">
              <el-collapse-item v-for="(arrayParam, index) in item.datatype.arrayParams" :key="index">
                <template #title>
                  <span style="color: #666">
                    <el-icon><Tickets /></el-icon>
                    {{ arrayParam[0].datatype.parentIndexName }}
                  </span>
                </template>
                <el-descriptions :column="1" size="small" border>
                  <el-descriptions-item
                    v-for="(param, index) in arrayParam"
                    :key="index"
                    :label="getParamLabel(item.name, param.name)"
                  >
                    <div v-if="param.datatype.type == 'bool'">
                      <el-switch
                        v-model="param.shadow"
                        @change="mqttPublish(deviceInfo, param)"
                        active-text=""
                        inactive-text=""
                        :active-value="1"
                        :inactive-value="0"
                        style="min-width: 100px"
                        :disabled="shadowUnEnable || param.isReadonly == 1"
                      />
                    </div>
                    <div v-if="param.datatype.type == 'enum'">
                      <div v-if="param.datatype.showWay && param.datatype.showWay == 'button'">
                        <el-button
                          style="margin: 5px"
                          size="small"
                          @click="enumButtonClick(deviceInfo, param, subItem.value)"
                          v-for="subItem in param.datatype.enumList"
                          :key="subItem.value"
                          :disabled="shadowUnEnable || param.isReadonly == 1"
                          :class="{ 'is-active-btn': subItem.value === param.shadow }"
                        >
                          {{ subItem.text }}
                        </el-button>
                      </div>
                      <el-select
                        v-else
                        v-model="param.shadow"
                        :placeholder="$t('device.running-status.866086-3')"
                        size="small"
                        @change="mqttPublish(deviceInfo, param)"
                        :disabled="shadowUnEnable || param.isReadonly == 1"
                      >
                        <el-option
                          v-for="subItem in param.datatype.enumList"
                          :key="subItem.value"
                          :label="subItem.text"
                          :value="subItem.value"
                        />
                      </el-select>
                    </div>
                    <div v-if="param.datatype.type == 'string'">
                      <el-input
                        v-model="param.shadow"
                        :placeholder="$t('device.running-status.866086-4')"
                        :disabled="shadowUnEnable || param.isReadonly == 1"
                      >
                        <template #append>
                          <el-button
                            @click="mqttPublish(deviceInfo, param)"
                            :title="$t('device.running-status.866086-6')"
                            v-if="!shadowUnEnable && param.isReadonly == 0"
                          >
                            <el-icon><Promotion /></el-icon>
                          </el-button>
                        </template>
                      </el-input>
                    </div>
                    <div v-if="param.datatype.type == 'decimal'">
                      <el-input
                        v-model="param.shadow"
                        type="number"
                        :placeholder="$t('device.running-status.866086-7')"
                        :disabled="shadowUnEnable || param.isReadonly == 1"
                      >
                        <template #append>
                          <el-button
                            @click="mqttPublish(deviceInfo, param)"
                            :title="$t('device.running-status.866086-6')"
                            v-if="!shadowUnEnable && param.isReadonly == 0"
                          >
                            <el-icon><Promotion /></el-icon>
                          </el-button>
                        </template>
                      </el-input>
                    </div>
                    <div v-if="param.datatype.type == 'integer'">
                      <el-input
                        v-model="param.shadow"
                        type="integer"
                        :placeholder="$t('device.running-status.866086-8')"
                        :disabled="shadowUnEnable || param.isReadonly == 1"
                      >
                        <template #append>
                          <el-button
                            @click="mqttPublish(deviceInfo, param)"
                            :title="$t('device.running-status.866086-6')"
                            v-if="!shadowUnEnable && param.isReadonly == 0"
                          >
                            <el-icon><Promotion /></el-icon>
                          </el-button>
                        </template>
                      </el-input>
                    </div>
                  </el-descriptions-item>
                </el-descriptions>
              </el-collapse-item>
            </el-collapse>
          </el-descriptions-item>
        </DescriptionsWithStyles>

        <!-- 设备状态(离线模式，value值不会更新) -->
        <DescriptionsWithStyles
          style="margin-top: 30px"
          :column="1"
          border
          v-if="deviceInfo.isShadow == 1 && deviceInfo.status != 3"
          :content-style="width"
          :label-style="{ minWidth: '100px' }"
        >
          <template #title v-if="deviceInfo.thingsModels.length > 0">
            <span style="font-size: 14px; color: #606266">{{ $t('device.running-status.866086-9') }}</span>
          </template>
          <!-- 设备物模型-->

          <el-descriptions-item v-for="(item, index) in deviceInfo.thingsModels" :key="index">
            <template #label>
              <el-icon><Open /></el-icon>
              {{ item.name }}
            </template>
            <div v-if="item.datatype.type == 'bool'">
              <el-switch
                v-model="item.value"
                @change="mqttPublish(deviceInfo, item)"
                active-text=""
                inactive-text=""
                :active-value="1"
                :inactive-value="0"
                style="min-width: 100px"
                disabled
              />
            </div>
            <div v-if="item.datatype.type == 'enum'">
              <div v-if="item.datatype.showWay && item.datatype.showWay == 'button'">
                <el-button
                  style="margin: 5px"
                  size="small"
                  disabled
                  v-for="subItem in item.datatype.enumList"
                  :key="subItem.value"
                >
                  {{ subItem.text }}
                </el-button>
              </div>
              <el-select
                v-else
                v-model="item.value"
                :placeholder="$t('device.running-status.866086-3')"
                @change="mqttPublish(deviceInfo, item)"
                disabled
              >
                <el-option
                  v-for="subItem in item.datatype.enumList"
                  :key="subItem.value"
                  :label="subItem.text"
                  :value="subItem.value"
                />
              </el-select>
            </div>
            <div v-if="item.datatype.type == 'string'">
              <el-input v-model="item.value" :placeholder="$t('device.running-status.866086-4')" disabled></el-input>
            </div>
            <div v-if="item.datatype.type == 'decimal'">
              <el-input
                v-model="item.value"
                type="number"
                :placeholder="$t('device.running-status.866086-7')"
                disabled
              ></el-input>
            </div>
            <div v-if="item.datatype.type == 'integer'">
              <el-input
                v-model="item.value"
                type="integer"
                :placeholder="$t('device.running-status.866086-8')"
                disabled
              ></el-input>
            </div>
            <div v-if="item.datatype.type == 'object'">
              <el-descriptions :column="1" size="small" border>
                <el-descriptions-item
                  v-for="(param, index) in item.datatype.params"
                  :key="index"
                  :label="getParamLabel(item.name, param.name)"
                >
                  <div v-if="param.datatype.type == 'bool'">
                    <el-switch
                      v-model="param.value"
                      size="small"
                      @change="mqttPublish(deviceInfo, param)"
                      active-text=""
                      inactive-text=""
                      :active-value="1"
                      :inactive-value="0"
                      style="min-width: 100px"
                      disabled
                    />
                  </div>
                  <div v-if="param.datatype.type == 'enum'">
                    <el-select
                      v-model="param.value"
                      :placeholder="$t('device.running-status.866086-3')"
                      @change="mqttPublish(deviceInfo, param)"
                      disabled
                      size="small"
                    >
                      <el-option
                        v-for="subItem in param.datatype.enumList"
                        :key="subItem.value"
                        :label="subItem.text"
                        :value="subItem.value"
                      />
                    </el-select>
                  </div>
                  <div v-if="param.datatype.type == 'string'">
                    <el-input
                      v-model="param.value"
                      :placeholder="$t('device.running-status.866086-4')"
                      disabled
                      size="small"
                    ></el-input>
                  </div>
                  <div v-if="param.datatype.type == 'decimal'">
                    <el-input
                      v-model="param.value"
                      type="number"
                      :placeholder="$t('device.running-status.866086-7')"
                      disabled
                      size="small"
                    ></el-input>
                  </div>
                  <div v-if="param.datatype.type == 'integer'">
                    <el-input
                      v-model="param.value"
                      type="integer"
                      :placeholder="$t('device.running-status.866086-8')"
                      disabled
                      size="small"
                    ></el-input>
                  </div>
                </el-descriptions-item>
              </el-descriptions>
            </div>
            <div v-if="item.datatype.type == 'array'">
              <template v-if="item.datatype.arrayType != 'object'">
                <el-descriptions :column="1" size="small" border>
                  <el-descriptions-item
                    v-for="(model, index) in item.datatype.arrayModel"
                    :key="index"
                    :label="model.name"
                  >
                    <div v-if="item.datatype.arrayType == 'string'">
                      <el-input
                        v-model="model.value"
                        :placeholder="$t('device.running-status.866086-4')"
                        size="small"
                        disabled
                      ></el-input>
                    </div>
                    <div v-if="item.datatype.arrayType == 'decimal'">
                      <el-input
                        v-model="model.value"
                        type="number"
                        :placeholder="$t('device.running-status.866086-7')"
                        size="small"
                        disabled
                      ></el-input>
                    </div>
                    <div v-if="item.datatype.arrayType == 'integer'">
                      <el-input
                        v-model="model.value"
                        type="integer"
                        :placeholder="$t('device.running-status.866086-8')"
                        size="small"
                        disabled
                      ></el-input>
                    </div>
                  </el-descriptions-item>
                </el-descriptions>
              </template>
              <template v-else>
                <el-collapse>
                  <el-collapse-item v-for="(arrayParam, index) in item.datatype.arrayParams" :key="index">
                    <template #title>
                      <span style="color: #666">
                        <el-icon><Tickets /></el-icon>
                        {{ arrayParam[0].datatype.parentIndexName }}
                      </span>
                    </template>
                    <el-descriptions :column="1" size="small" border>
                      <el-descriptions-item
                        v-for="(param, index) in arrayParam"
                        :key="index"
                        :label="getParamLabel(item.name, param.name)"
                      >
                        <div v-if="param.datatype.type == 'bool'">
                          <el-switch
                            v-model="param.value"
                            @change="mqttPublish(deviceInfo, param)"
                            active-text=""
                            inactive-text=""
                            :active-value="1"
                            :inactive-value="0"
                            style="min-width: 100px"
                            disabled
                          />
                        </div>
                        <div v-if="param.datatype.type == 'enum'">
                          <el-select
                            v-model="param.value"
                            :placeholder="$t('device.running-status.866086-3')"
                            @change="mqttPublish(deviceInfo, param)"
                            disabled
                            size="small"
                          >
                            <el-option
                              v-for="subItem in param.datatype.enumList"
                              :key="subItem.value"
                              :label="subItem.text"
                              :value="subItem.value"
                            />
                          </el-select>
                        </div>
                        <div v-if="param.datatype.type == 'string'">
                          <el-input
                            v-model="param.value"
                            :placeholder="$t('device.running-status.866086-4')"
                            disabled
                            size="small"
                          ></el-input>
                        </div>
                        <div v-if="param.datatype.type == 'decimal'">
                          <el-input
                            v-model="param.value"
                            type="number"
                            :placeholder="$t('device.running-status.866086-7')"
                            disabled
                            size="small"
                          ></el-input>
                        </div>
                        <div v-if="param.datatype.type == 'integer'">
                          <el-input
                            v-model="param.value"
                            type="integer"
                            :placeholder="$t('device.running-status.866086-8')"
                            disabled
                            size="small"
                          ></el-input>
                        </div>
                      </el-descriptions-item>
                    </el-descriptions>
                  </el-collapse-item>
                </el-collapse>
              </template>
            </div>
          </el-descriptions-item>
        </DescriptionsWithStyles>
      </el-col>

      <el-col :xs="24" :sm="24" :md="24" :lg="12" :xl="14">
        <!-- 设备监测图表-->
        <div v-for="(item, groupIndex) in deviceInfo.chartList" :key="groupIndex">
          <div class="device_title" v-if="item && item[0] && item[0].fatherName">{{ item[0].fatherName }}</div>
          <el-row :gutter="20" v-if="item && item.length > 0">
            <div v-for="(param, paramIndex) in item" :key="paramIndex">
              <el-col :xs="24" :sm="12" :md="12" :lg="12" :xl="8">
                <el-card
                  shadow="hover"
                  style="border-radius: 8px; margin-bottom: 20px; width: 270px; height: 282px; padding: 0"
                >
                  <div :ref="`chartRef-${groupIndex}-${paramIndex}`" style="height: 100%; width: 100%"></div>
                </el-card>
              </el-col>
            </div>
          </el-row>
        </div>
      </el-col>
    </el-row>

    <!-- 固件版本查看对话框 -->
    <el-dialog v-model="openVersion" :title="$t('device.running-status.866086-10')" width="550px" append-to-body>
      <el-form ref="firmwareForm" label-width="100px" :model="firmwareParams" :inline="true" :rules="rules">
        <el-form-item :label="$t('device.running-status.866086-38')">
          <el-select
            v-model="deviceInfo.firmwareType"
            :placeholder="$t('firmware.index.222541-51')"
            style="width: 350px"
            disabled
          >
            <el-option
              v-for="item in firmwareTypeList"
              :key="item.value"
              :label="item.label"
              :value="item.value"
            ></el-option>
          </el-select>
        </el-form-item>
        <el-form-item :label="$t('device.running-status.866086-39')">
          <el-input
            :placeholder="$t('device.running-status.866086-40')"
            v-model="deviceInfo.firmwareVersion"
            style="width: 350px"
            disabled
          >
            <template #prepend>Version</template>
          </el-input>
        </el-form-item>
      </el-form>

      <template #footer>
        <el-tooltip
          effect="dark"
          :content="$t('device.running-status.866086-41')"
          placement="top-start"
          :disabled="device.status === 3"
        >
          <el-button type="primary" @click="fetchLatestFirmware" :disabled="device.status !== 3">
            {{ $t('device.running-status.866086-42') }}
          </el-button>
        </el-tooltip>
        <el-button @click="cancel1">{{ $t('cancel') }}</el-button>
      </template>
    </el-dialog>

    <!-- 添加或修改产品固件对话框 -->
    <el-dialog v-model="openFirmware" :title="$t('device.running-status.866086-10')" width="600px" append-to-body>
      <div v-if="firmware == null" style="text-align: center; font-size: 16px">
        <el-icon><SuccessFilled /></el-icon>
        {{ $t('device.running-status.866086-11') }}
      </div>
      <div
        v-if="firmware != null && deviceInfo.firmwareVersion > firmware.version"
        style="text-align: center; font-size: 16px"
      >
        <el-icon><Warning /></el-icon>
        {{ $t('device.running-status.866086-47') }}
      </div>
      <DescriptionsWithStyles
        :column="1"
        border
        size="large"
        v-if="firmware != null && deviceInfo.firmwareVersion < firmware.version"
        :label-style="{ width: '150px', fontWeight: 'bold' }"
      >
        <template #title>
          <el-link type="success" underline="never">
            <el-icon><SuccessFilled /></el-icon>
            {{ $t('device.running-status.866086-12') }}
          </el-link>
        </template>
        <el-descriptions-item :label="$t('device.running-status.866086-13')">
          {{ firmware.firmwareName }}
        </el-descriptions-item>
        <el-descriptions-item :label="$t('device.device-edit.148398-4')">
          {{ firmware.productName }}
        </el-descriptions-item>
        <el-descriptions-item :label="$t('device.device-edit.148398-12')">
          Version {{ firmware.version }}
        </el-descriptions-item>
        <el-descriptions-item :label="$t('device.running-status.866086-16')">
          <el-link :href="getDownloadUrl(firmware.filePath)" underline="never" type="primary">
            {{ getDownloadUrl(firmware.filePath) }}
          </el-link>
        </el-descriptions-item>
        <el-descriptions-item :label="$t('device.running-status.866086-17')">
          {{ firmware.remark }}
        </el-descriptions-item>
      </DescriptionsWithStyles>
      <template #footer class="dialog-footer">
        <el-button
          type="success"
          @click="otaUpgrade"
          v-if="firmware != null && deviceInfo.firmwareVersion < firmware.version"
        >
          {{ $t('device.running-status.866086-18') }}
        </el-button>
        <el-button @click="cancel">{{ $t('cancel') }}</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, nextTick, onMounted, onBeforeUnmount, getCurrentInstance, watch } from 'vue';
import { ElMessage, ElMessageBox } from 'element-plus';
import { Promotion, Tickets, Open, Menu, SuccessFilled, Warning } from '@element-plus/icons-vue';
import { getLatestFirmware } from '@/api/iot/firmware';
import { serviceInvoke, serviceInvokeReply } from '@/api/iot/runstatus';
import busEvent from '@/utils/busEvent';
import { getOrderControl } from '@/api/iot/control';
import { messagePost } from '@/api/iot/mqttTest';
import { useI18n } from 'vue-i18n';
import { useUserStore } from '@/stores/modules/user';
import DescriptionsWithStyles from '@/components/DescriptionsWithStyles/index.vue';

const { proxy } = getCurrentInstance();
const { t } = useI18n();
const userStore = useUserStore();

// 响应式数据
const title = ref(t('device.running-status.866086-48'));
const shadowUnEnable = ref(false);
const width = reactive({
  width: '70%',
});
const statusColor = reactive({
  background: '#67C23A',
  color: '#fff',
  width: '100px',
});
const firmware = ref({});
const openFirmware = ref(false);
const loading = ref(true);
const deviceInfo = reactive({
  boolList: [],
  enumList: [],
  stringList: [],
  integerList: [],
  decimalList: [],
  arrayList: [],
  thingsModels: [],
  chartList: [],
});
const firmwareParams = reactive({
  firmwareType: '',
  versionInput: '',
});
const monitorChart = ref([
  {
    chart: {},
    data: {
      id: '',
      name: '',
      value: '',
    },
  },
]);
const openVersion = ref(false);
const firmwareTypeList = ref([
  {
    label: t('firmware.index.222541-52'),
    value: 1,
  },
  {
    label: 'HTTP',
    value: 2,
  },
]);
const sendVal = ref('');
let offUpdateData = null;
let offUpdateStatus = null;

// 表单校验规则
const rules = {
  firmwareType: [
    {
      required: true,
      message: t('device.running-status.866086-43'),
      trigger: 'blur',
    },
  ],
};

/** 更新设备状态 */
const updateDeviceStatus = (device) => {
  if (device.status == 3) {
    statusColor.background = '#12d09f';
    title.value = t('device.running-status.866086-26');
    shadowUnEnable.value = false;
  } else {
    if (device.isShadow == 1) {
      statusColor.background = '#486FF2';
      title.value = t('device.running-status.866086-27');
      shadowUnEnable.value = false;
    } else {
      statusColor.background = '#909399';
      title.value = t('device.running-status.866086-28');
      shadowUnEnable.value = true;
    }
  }
  // 发出状态事件
  proxy.$emit('statusEvent', deviceInfo.status);
};

/**图表展示*/
const MonitorChart = () => {
  // 检查数据有效性
  if (!deviceInfo.chartList || deviceInfo.chartList.length === 0) {
    console.warn('chartList 为空，无法初始化图表');
    return;
  }

  // 检查 echarts 是否可用
  if (!proxy.$echarts) {
    console.error('echarts 未正确初始化');
    return;
  }

  let k = 0;
  for (let i = 0; i < deviceInfo.chartList.length; i++) {
    const item = deviceInfo.chartList[i];
    if (!item || item.length === 0) continue;

    for (let j = 0; j < item.length; j++) {
      const refKey = `chartRef-${i}-${j}`;
      const chartRef = proxy.$refs[refKey];

      // 检查 ref 是否存在
      if (!chartRef) {
        console.warn(`图表 ref ${refKey} 不存在`);
        continue;
      }

      // 获取 DOM 元素（ref 可能是数组）
      const chartDom = Array.isArray(chartRef) ? chartRef[0] : chartRef;
      if (!chartDom) {
        console.warn(`无法获取图表 DOM 元素: ${refKey}`);
        continue;
      }

      try {
        // 初始化 echarts 实例
        const existingChart = proxy.$echarts.getInstanceByDom(chartDom);
        if (existingChart) {
          existingChart.dispose();
        }
        const chart = proxy.$echarts.init(chartDom);
        const chartItem = deviceInfo.chartList[i][j];
        const value = Number(chartItem.shadow) ?? chartItem.datatype.min ?? 0;
        const option = {
          tooltip: {
            formatter: '{b} <br/> {c}' + (chartItem.datatype.unit || ''),
          },
          series: [
            {
              name: chartItem.datatype.type,
              type: 'gauge',
              min: chartItem.datatype.min ?? 0,
              max: chartItem.datatype.max ?? 100,
              colorBy: 'data',
              splitNumber: 10,
              radius: '75%',
              splitLine: {
                distance: 4,
              },
              axisLabel: {
                fontSize: 9,
                distance: 9,
              },
              axisTick: {
                distance: 4,
              },
              axisLine: {
                lineStyle: {
                  width: 7,
                  color: [
                    [0.2, '#409EFF'],
                    [0.8, '#12d09f'],
                    [1, '#F56C6C'],
                  ],
                  opacity: 0.3,
                },
              },
              pointer: {
                icon: 'triangle',
                length: '60%',
                width: 6,
              },
              progress: {
                show: true,
                width: 8,
              },
              detail: {
                valueAnimation: true,
                formatter: '{value}' + ' ' + (chartItem.datatype.unit || ''),
                offsetCenter: [0, '80%'],
                fontSize: 16,
              },
              data: [
                {
                  value: value,
                  name: chartItem.name,
                },
              ],
              title: {
                offsetCenter: [0, '115%'],
                fontSize: 14,
              },
            },
          ],
        };

        chart.setOption(option);

        // 保存图表实例供后续使用
        monitorChart.value[k] = {
          chart: chart,
          data: {
            id: chartItem.id,
            name: chartItem.name,
            value: value,
          },
        };
        k++;
      } catch (error) {
        console.error(`初始化图表 ${refKey} 失败:`, error);
      }
    }
  }
};

// 监听设备变化
const props = defineProps({
  device: {
    type: Object,
    default: null,
  },
});

const normalizeSwitchValue = (val) => {
  if (val === 1 || val === '1' || val === true || val === 'true') return 1;
  return 0;
};

const normalizeBoolModelForSwitch = (model) => {
  if (!model || model?.datatype?.type !== 'bool') return;
  if ('shadow' in model) model.shadow = normalizeSwitchValue(model.shadow);
  if ('value' in model) model.value = normalizeSwitchValue(model.value);
};

const normalizeBoolModelsForSwitch = (thingsModels) => {
  if (!Array.isArray(thingsModels)) return;
  for (const item of thingsModels) {
    if (!item?.datatype) continue;
    if (item.datatype.type === 'bool') {
      normalizeBoolModelForSwitch(item);
      continue;
    }
    if (item.datatype.type === 'object' && Array.isArray(item.datatype.params)) {
      for (const param of item.datatype.params) {
        normalizeBoolModelForSwitch(param);
      }
      continue;
    }
    if (item.datatype.type === 'array') {
      if (Array.isArray(item.datatype.arrayParams)) {
        for (const row of item.datatype.arrayParams) {
          if (!Array.isArray(row)) continue;
          for (const param of row) {
            normalizeBoolModelForSwitch(param);
          }
        }
      }
      if (item.datatype.arrayType === 'bool' && Array.isArray(item.datatype.arrayModel)) {
        for (const model of item.datatype.arrayModel) {
          if ('shadow' in model) model.shadow = normalizeSwitchValue(model.shadow);
          if ('value' in model) model.value = normalizeSwitchValue(model.value);
        }
      }
    }
  }
};

// 监听设备变化
const handleDeviceChange = (device) => {
  if (device && device.deviceId) {
    Object.assign(deviceInfo, device);

    updateDeviceStatus(deviceInfo);

    // 物模型排序
    if (deviceInfo.thingsModels && deviceInfo.thingsModels.length > 0) {
      normalizeBoolModelsForSwitch(deviceInfo.thingsModels);
      deviceInfo.thingsModels.sort((a, b) => b.order - a.order);
    }

    // 图表列表排序
    if (deviceInfo.chartList && deviceInfo.chartList.length > 0) {
      deviceInfo.chartList.sort((a, b) => b.order - a.order);

      // 图表初始化需要等待 DOM 更新完成
      nextTick(() => {
        setTimeout(() => {
          MonitorChart();
        }, 100);
      });
    }
  }
};

// watch监听props.device变化
watch(
  () => props.device,
  (newDevice) => {
    if (newDevice && newDevice.deviceId) {
      handleDeviceChange(newDevice);
      initDataStatus();
      initData();
    }
  },
  { deep: true, immediate: true }
);

// 初始化数据监听（与 Vue2 一致：监听 busEvent.updateData）
function initData() {
  if (offUpdateData) return;
  const handler = (params) => {
    updateParam(params);
  };
  busEvent.on('updateData', handler);
  offUpdateData = () => {
    busEvent.off('updateData', handler);
    offUpdateData = null;
  };
}

// 初始化状态监听（与 Vue2 一致：监听 busEvent.updateStatus）
function initDataStatus() {
  if (offUpdateStatus) return;
  const handler = (status) => {
    updateStatus(status);
  };
  busEvent.on('updateStatus', handler);
  offUpdateStatus = () => {
    busEvent.off('updateStatus', handler);
    offUpdateStatus = null;
  };
}

// 更新状态
const updateStatus = (status) => {
  let { serialNumber, productId, data } = status;
  if (data) {
    if (deviceInfo.serialNumber == serialNumber) {
      deviceInfo.status = data.status;
      deviceInfo.isShadow = data.isShadow;
      deviceInfo.rssi = data.rssi;
      updateDeviceStatus(deviceInfo);
    }
  }
};

// 更新参数
function getParamLabel(parentName, paramName) {
  const parent = String(parentName || '').trim();
  const child = String(paramName || '').trim();
  if (!parent) return child;
  if (!child) return parent;
  return child.startsWith(parent) ? child.slice(parent.length).replace(/^[_\-\s]+/, '') : child;
}

const updateParam = (params) => {
  let { data } = params;
  if (typeof data === 'string') {
    try {
      data = JSON.parse(data);
    } catch (e) {
      data = [];
    }
  }
  if (data && data.message) {
    data = data.message;
  }
  if (typeof data === 'string') {
    try {
      data = JSON.parse(data);
    } catch (e) {
      data = [];
    }
  }
  if (data && data.length !== 0) {
    for (let j = 0; j < data.length; j++) {
      for (let k = 0; k < deviceInfo.thingsModels.length; k++) {
        if (deviceInfo.thingsModels[k].id == data[j].id) {
          const variable = deviceInfo.thingsModels[k];
          // 普通类型(小数/整数/字符串/布尔/枚举)
          if (
            deviceInfo.thingsModels[k].datatype.type == 'decimal' ||
            deviceInfo.thingsModels[k].datatype.type == 'integer' ||
            deviceInfo.thingsModels[k].datatype.type == 'bool'
          ) {
            variable.shadow = Number(data[j].value);
          } else {
            variable.shadow = data[j].value;
          }
        }
        if (deviceInfo.thingsModels[k].datatype.type == 'object') {
          // 对象类型
          for (let n = 0; n < deviceInfo.thingsModels[k].datatype.params.length; n++) {
            if (deviceInfo.thingsModels[k].datatype.params[n].id == data[j].id) {
              const paramModel = deviceInfo.thingsModels[k].datatype.params[n];
              paramModel.shadow =
                paramModel.datatype.type == 'bool' ? normalizeSwitchValue(data[j].value) : data[j].value;
            }
          }
        } else if (deviceInfo.thingsModels[k].datatype.type == 'array') {
          // 数组类型
          if (deviceInfo.thingsModels[k].datatype.arrayType == 'object') {
            // 1.对象类型数组,id为数组中一个元素,例如：array_01_gateway_temperature
            if (String(data[j].id).indexOf('array_') == 0) {
              for (let n = 0; n < deviceInfo.thingsModels[k].datatype.arrayParams.length; n++) {
                for (let m = 0; m < deviceInfo.thingsModels[k].datatype.arrayParams[n].length; m++) {
                  if (deviceInfo.thingsModels[k].datatype.arrayParams[n][m].id == data[j].id) {
                    const paramModel = deviceInfo.thingsModels[k].datatype.arrayParams[n][m];
                    paramModel.shadow =
                      paramModel.datatype.type == 'bool' ? normalizeSwitchValue(data[j].value) : data[j].value;
                  }
                }
              }
            } else {
              // 2.对象类型数组，例如：gateway_temperature,消息ID添加前缀后匹配
              for (let n = 0; n < deviceInfo.thingsModels[k].datatype.arrayParams.length; n++) {
                for (let m = 0; m < deviceInfo.thingsModels[k].datatype.arrayParams[n].length; m++) {
                  let index = n > 9 ? String(n) : '0' + k;
                  let prefix = 'array_' + index + '_';
                  if (deviceInfo.thingsModels[k].datatype.arrayParams[n][m].id == prefix + data[j].id) {
                    const paramModel = deviceInfo.thingsModels[k].datatype.arrayParams[n][m];
                    paramModel.shadow =
                      paramModel.datatype.type == 'bool' ? normalizeSwitchValue(data[j].value) : data[j].value;
                  }
                }
              }
            }
          } else if (
            deviceInfo.thingsModels[k].datatype.arrayModel &&
            deviceInfo.thingsModels[k].datatype.arrayModel.length > 0
          ) {
            // 整数、小数和字符串类型数组
            for (let n = 0; n < deviceInfo.thingsModels[k].datatype.arrayModel.length; n++) {
              if (deviceInfo.thingsModels[k].datatype.arrayModel[n].id == data[j].id) {
                const itemModel = deviceInfo.thingsModels[k].datatype.arrayModel[n];
                itemModel.shadow =
                  deviceInfo.thingsModels[k].datatype.arrayType == 'bool'
                    ? normalizeSwitchValue(data[j].value)
                    : data[j].value;
                break;
              }
            }
          }
        }
      }
      // 图表数据
      const chartList = deviceInfo.chartList;
      for (let x = 0; x < chartList.length; x++) {
        const chartData = chartList[x];
        for (let k = 0; k < chartData.length; k++) {
          // console.log(data[j]);
          // console.log('图表数据', chartData[k]);
          if (chartData[k].id) {
            // 数组类型匹配,例如：array_00_gateway_temperature
            if (chartData[k].id == data[j].id) {
              console.log('匹配成功');
              chartData[k].shadow = data[j].value;
              // 更新图表
              for (let m = 0; m < monitorChart.value.length; m++) {
                if (data[j].id == monitorChart.value[m].data.id) {
                  let data = [
                    {
                      value: chartData[k].shadow,
                      name: monitorChart.value[m].data.name,
                    },
                  ];
                  monitorChart.value[m].chart.setOption({
                    series: [
                      {
                        data: data,
                      },
                    ],
                  });
                  break;
                }
              }
            }
          } else {
            // 普通类型匹配
            if (chartData[k].id == data[j].id) {
              chartData[k].shadow = data[j].value;
              // 更新图表
              for (let m = 0; m < monitorChart.value.length; m++) {
                if (data[j].id == monitorChart.value[m].data.id) {
                  let data = [
                    {
                      value: chartData[k].shadow,
                      name: monitorChart.value[m].data.name,
                    },
                  ];
                  monitorChart.value[m].chart.setOption({
                    series: [
                      {
                        data: data,
                      },
                    ],
                  });
                  break;
                }
              }
            }
          }
        }
      }
    }
  }
};

// 发送指令
const mqttPublish = async (device, model) => {
  const command = {};
  command[model.id] = model.shadow;
  const userName = userStore.dept?.userName;
  if (userName !== device.createBy) {
    //判断是否有权限
    const params = {
      deviceId: device.deviceId,
      modelId: model.modelId,
    };
    const response = await getOrderControl(params);
    if (response.code != 200) {
      ElMessage({
        type: 'warning',
        message: response.msg,
      });
      return;
    }
  }
  const data = {
    serialNumber: device.serialNumber,
    productId: device.productId,
    remoteCommand: command,
    identifier: model.id,
    modelName: model.name,
    isShadow: device.status != 3,
    type: model.type,
  };
  let title = '';
  //设备在线状态判断
  if (device.status !== 3 && device.isShadow !== 1) {
    if (device.status === 1) {
      title = t('device.device-variable.930930-0');
    } else if (device.status === 2) {
      title = t('device.device-variable.930930-1');
    } else {
      title = t('device.device-variable.930930-2');
    }
    ElMessage({
      type: 'warning',
      message: title,
    });
    return;
  }
  if (
    (deviceInfo.protocolCode === 'MODBUS-TCP-OVER-RTU' ||
      deviceInfo.protocolCode === 'MODBUS-RTU' ||
      deviceInfo.protocolCode === 'MODBUS-TCP') &&
    device.status === 3
  ) {
    await serviceInvokeReply(data).then((response) => {
      if (response.code === 200) {
        ElMessage({
          type: 'success',
          message: t('device.running-status.866086-25'),
        });
      } else {
        ElMessage.error(response.msg);
      }
    });
  } else {
    await serviceInvoke(data).then((response) => {
      if (response.code === 200) {
        ElMessage({
          type: 'success',
          message: t('device.running-status.866086-25'),
        });
      } else {
        ElMessage.error(response.msg);
      }
    });
  }
};

/** 枚举类型按钮单击 */
const enumButtonClick = (device, model, value) => {
  model.shadow = value;
  mqttPublish(device, model);
};

const viewVersion = () => {
  openVersion.value = true;
};

// 取消按钮
const cancel1 = () => {
  openVersion.value = false;
};

/** 物模型数组元素值改变事件 */
const arrayItemChange = (value, thingsModel) => {
  let shadow = '';
  for (let i = 0; i < thingsModel.datatype.arrayCount; i++) {
    shadow += thingsModel.datatype.arrayModel[i].shadow + ',';
  }
  shadow = shadow.substring(0, shadow.length - 1);
  thingsModel.shadow = shadow;
};

/** 物模型中数组值改变事件 */
const arrayInputChange = (value, thingsModel) => {
  let arrayModels = value.split(',');
  if (arrayModels.length != thingsModel.datatype.arrayCount) {
    proxy.$modal.alertWarning(
      t('device.running-status.866086-29') + thingsModel.datatype.arrayCount + t('device.running-status.866086-30')
    );
  } else {
    for (let i = 0; i < thingsModel.datatype.arrayCount; i++) {
      thingsModel.datatype.arrayModel[i].shadow = arrayModels[i];
    }
  }
};

/** 设备升级 */
const otaUpgrade = async () => {
  const topic = '/' + deviceInfo.productId + '/' + deviceInfo.serialNumber + '/ota/get';
  const message = JSON.stringify({
    version: firmware.value.version,
    downloadUrl: getDownloadUrl(firmware.value.filePath),
  });

  try {
    if (proxy?.$mqttTool?.client == null) {
      await proxy?.$mqttTool?.connect(proxy?.vuex_token);
    }
    const res = await proxy?.$mqttTool?.publish(topic, message, t('device.running-status.866086-31'));
    proxy?.$modal?.notifySuccess(res);
    openFirmware.value = false;
  } catch (err) {
    proxy?.$modal?.notifyError(err);
  }
};

/** 获取最新固件 */
const fetchLatestFirmware = () => {
  const { deviceId, firmwareType } = deviceInfo;
  getLatestFirmware(deviceId, firmwareType).then((response) => {
    if (response.code === 200) {
      firmware.value = response.data;
      openFirmware.value = true;
    }
  });
};

// 取消按钮
const cancel = () => {
  openFirmware.value = false;
};

// 获取下载路径前缀
const getDownloadUrl = (path) => {
  return window.location.origin + import.meta.env.VITE_APP_BASE_API + path;
};

//批量下发
const batchPublish = async (device, model) => {
  const userName = userStore.dept?.userName;
  if (userName !== device.createBy) {
    //判断是否有权限
    const params = {
      deviceId: device.deviceId,
      modelId: model.modelId,
    };
    const response = await getOrderControl(params);
    if (response.code != 200) {
      ElMessage({
        type: 'warning',
        message: response.msg,
      });
      return;
    }
  }
  let title = '';
  //设备在线状态判断
  if (device.status !== 3 && device.isShadow !== 1) {
    if (device.status === 1) {
      title = t('device.device-variable.930930-0');
    } else if (device.status === 2) {
      title = t('device.device-variable.930930-1');
    } else {
      title = t('device.device-variable.930930-2');
    }
    ElMessage({
      type: 'warning',
      message: title,
    });
    return;
  }
  setTimeout(() => {
    ElMessageBox.prompt(t('device.running-status.866086-49'), {
      confirmButtonText: t('confirm'),
      cancelButtonText: t('cancel'),
      inputPattern: /^-?\d+$/, // 正则表达式，只允许整数（包括负数）
      inputValidator: (value) => {
        if (value === null || value === '') {
          return t('plzInput');
        }
        if (!/^-?\d+$/.test(value)) {
          return t('device.running-status.866086-50');
        }
        return true;
      },
      inputErrorMessage: t('device.running-status.866086-51'),
    })
      .then(({ value }) => {
        sendVal.value = model.datatype.arrayModel.map((item) => {
          item.shadow = value;
          return { id: item.id, value: value, remark: '' };
        });
      })
      .then(() => {
        sendMessagePost(device, model);
      });
  });
};

//数组类型的批量下发
const sendMessagePost = async (device, model) => {
  messagePost({
    message: JSON.stringify(sendVal.value),
    serialNumber: device.serialNumber,
    dataType: 'json',
    topicName: '/function/get',
  }).then((response) => {
    if (response.code === 200) {
      ElMessage({
        type: 'success',
        message: t('device.running-status.866086-25'),
      });
    } else {
      ElMessage.error(response.msg);
    }
  });
};

// 挂载生命周期
onMounted(() => {
  // 处理设备变更
  if (proxy.device) {
    Object.assign(deviceInfo, proxy.device);
    handleDeviceChange(proxy.device);

    if (deviceInfo.deviceId) {
      initDataStatus();
      initData();
    }
  }
});

onBeforeUnmount(() => {
  if (offUpdateStatus) offUpdateStatus();
  if (offUpdateData) offUpdateData();
});

// 为了在模板中使用这些函数，需要将它们暴露出去
defineExpose({
  handleDeviceChange,
  updateStatus,
  updateParam,
  mqttPublish,
  enumButtonClick,
  viewVersion,
  cancel1,
  updateDeviceStatus,
  arrayItemChange,
  arrayInputChange,
  otaUpgrade,
  fetchLatestFirmware,
  cancel,
  getDownloadUrl,
  MonitorChart,
  batchPublish,
  sendMessagePost,
});
</script>

<style lang="scss" scoped>
.running-status {
  padding-bottom: 20px;

  .status-col {
    .title {
      line-height: 28px;
      font-size: 16px;
    }

    .emum-wrap {
      .btn {
        margin: 5px 10px 5px 0;
      }
    }

    :deep(.el-slider__bar) {
      height: 18px;
    }

    :deep(.el-slider__runway) {
      height: 18px;
      margin: 5px 0;
    }

    :deep(.el-slider__button) {
      height: 18px;
      width: 18px;
      border-radius: 10%;
    }

    :deep(.el-slider__button-wrapper) {
      top: -9px;
    }

    .is-active-btn {
      color: #1890ff;
      border-color: #badeff;
      background-color: #e8f4ff;
    }
  }

  .device_title {
    font-size: 14px;
    font-weight: bold;
    margin-bottom: 5px;
  }
}
</style>
