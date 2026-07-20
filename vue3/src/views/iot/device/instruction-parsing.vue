<template>
  <div class="instruction-parsing">
    <el-row>
      <el-col :span="16" class="left">
        <div class="head">
          <span style="color: #909399">{{ proxy?.$t('device.instruction-parsing.830424-0') }}</span>
          <span>{{ device.serialNumber }}</span>
          <el-dropdown style="margin-left: auto">
            <span class="el-dropdown-link">
              {{ format }}
              <el-icon class="el-icon--right"><ArrowDown /></el-icon>
            </span>

            <template #dropdown>
              <el-dropdown-menu>
                <el-dropdown-item v-for="(item, index) in formatList" :key="index" :disabled="item.disabled">
                  {{ item.value }}
                </el-dropdown-item>
              </el-dropdown-menu>
            </template>
          </el-dropdown>
        </div>
        <div class="content">
          <div
            v-for="(item, index) in logList"
            :key="index"
            class="item-class"
            :class="{ 'send-class': item.type == 0, 'receive-class': item.type == 1, 'report-class': item.type == 2 }"
          >
            <div class="item-head">
              <div>
                {{
                  item.type == 0
                    ? proxy?.$t('device.instruction-parsing.830424-1')
                    : item.type == 1
                      ? proxy?.$t('device.instruction-parsing.830424-2')
                      : proxy?.$t('device.instruction-parsing.830424-3')
                }}
              </div>
              <div class="head-time">{{ item.time }}</div>
              <div
                class="analysis-btn right-btn"
                v-if="item.type != 0 && !item.analysis"
                v-loading="item.loading"
                @click.stop="analysisData(item, index)"
              >
                {{ proxy?.$t('device.instruction-parsing.830424-4') }}
              </div>
              <div class="analysised right-btn" v-if="item.type != 0 && item.analysis">
                {{ proxy?.$t('device.instruction-parsing.830424-5') }}
              </div>
            </div>
            <div class="item-content">
              <div class="content-value">{{ item.value }}</div>
            </div>
            <div class="analysis-data" v-if="item.type != 0 && item.analysis">
              <div class="data-col left-col">
                <div class="label">{{ proxy?.$t('device.instruction-parsing.830424-6') }}</div>
                <div class="value">{{ item.analysisVal.name || '--' }}</div>
              </div>
              <div class="data-col right-col">
                <div class="label">{{ proxy?.$t('device.instruction-parsing.830424-7') }}</div>
                <div class="value">{{ item.analysisVal.id || '--' }}</div>
              </div>
              <div class="data-col left-col">
                <div class="label">{{ proxy?.$t('device.instruction-parsing.830424-8') }}</div>
                <div class="value">{{ item.analysisVal.value || '--' }}</div>
              </div>
              <div class="data-col right-col" v-if="item.analysisList.length > 1">
                <el-button link size="small" @click.stop="openMore(item)">
                  {{ proxy?.$t('device.instruction-parsing.830424-9') }}
                </el-button>
              </div>
            </div>
          </div>
        </div>
        <div class="bottom">
          <el-input
            :placeholder="proxy?.$t('device.instruction-parsing.830424-10')"
            v-model="sendVal"
            class="input-with-select"
          >
            <template #prepend>
              <el-select
                v-model="dataType"
                style="width: 70px"
                :placeholder="proxy?.$t('device.instruction-parsing.830424-11')"
              >
                <el-option
                  v-for="(item, index) in formatList"
                  :key="index"
                  :label="item.label"
                  :value="item.label"
                  :disabled="item.disabled"
                ></el-option>
              </el-select>
            </template>
          </el-input>
          <el-button round type="primary" class="send-btn" size="small" @click.stop="sendMessage">
            {{ proxy?.$t('device.instruction-parsing.830424-14') }}
          </el-button>
        </div>
      </el-col>
      <el-col :span="8" class="right">
        <div class="head right-head">{{ proxy?.$t('device.instruction-parsing.830424-15') }}</div>
        <div class="content">
          <div
            v-for="(item, index) in quickParsing"
            :key="index"
            class="quick-item"
            @click.stop="quickClick(item)"
            @contextmenu.prevent="onContextmenu($event, item)"
          >
            {{ item.name }}
          </div>
        </div>
        <div class="right-bottom" @click.stop="openEditDialog">
          {{ proxy?.$t('device.instruction-parsing.830424-16') }}
        </div>
      </el-col>
    </el-row>

    <el-dialog
      :title="
        editName ? proxy?.$t('device.instruction-parsing.830424-17') : proxy?.$t('device.instruction-parsing.830424-18')
      "
      v-model="editDialog"
      :width="editName ? '550px' : '600px'"
    >
      <div v-if="dataType === 'hex'">
        <div class="dialog-content" v-show="!editName">
          <el-form :model="createForm" label-position="top">
            <el-row :gutter="40">
              <!-- 从机地址 -->
              <el-col :span="24">
                <el-form-item :label="proxy?.$t('device.instruction-parsing.830424-19')" prop="path">
                  <el-input v-model="createForm.path" type="number"></el-input>
                </el-form-item>
              </el-col>
              <!-- 指令类型 -->
              <el-col :span="12">
                <el-form-item :label="proxy?.$t('device.instruction-parsing.830424-20')" prop="start">
                  <el-select v-model="createForm.start" @change="changeNum" style="width: 100%">
                    <el-option
                      :label="start.label"
                      :value="start.value"
                      v-for="start in startList"
                      :key="start.value"
                    ></el-option>
                  </el-select>
                </el-form-item>
              </el-col>
              <!-- 功能码 -->
              <el-col :span="12">
                <el-form-item :label="proxy?.$t('device.instruction-parsing.830424-21')" prop="functionCode">
                  <el-select
                    v-model="createForm.functionCode"
                    @change="changeNum"
                    :disabled="createForm.start == '0xFFAA'"
                    style="width: 100%"
                  >
                    <el-option
                      :label="dict.label"
                      :value="dict.value"
                      v-for="dict in product_command_function_code"
                      :key="dict.value"
                    ></el-option>
                  </el-select>
                </el-form-item>
              </el-col>
              <!--起始寄存器地址-->
              <el-col :span="12">
                <el-form-item prop="startPath">
                  <template #label>
                    <div class="form-item-label">
                      <div style="margin-right: 6px">{{ proxy?.$t('device.instruction-parsing.830424-22') }}</div>
                      <el-tooltip :content="createForm.startPathSwitch" placement="top">
                        <el-switch
                          v-model="createForm.startPathSwitch"
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
                    style="width: 100%"
                    v-model="createForm.startPath"
                    type="number"
                    v-show="createForm.startPathSwitch == 'Dec'"
                    :min="0"
                    @change="
                      () => {
                        createForm.startPath16 = int2hexFn(createForm.startPath);
                      }
                    "
                    @input="
                      () => {
                        createForm.startPath16 = int2hexFn(createForm.startPath);
                      }
                    "
                  >
                    <template #append>0x{{ createForm.startPath16 }}</template>
                  </el-input>
                  <el-input
                    style="width: 100%"
                    v-model="createForm.startPath16"
                    v-show="createForm.startPathSwitch != 'Dec'"
                    @input="
                      () => {
                        createForm.startPath = hex2intFn(createForm.startPath16);
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
                  v-show="!['05', '06'].includes(createForm.functionCode)"
                >
                  <el-input-number
                    v-model="createForm.registerNum"
                    controls-position="right"
                    :min="0"
                    @change="changeNum"
                    style="width: 100%"
                  />
                </el-form-item>
                <el-form-item prop="setValue" v-show="['05', '06'].includes(createForm.functionCode)">
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
                    style="width: 100%"
                    v-model="createForm.setValue"
                    type="number"
                    v-show="createForm.setValueSwitch == 'Dec'"
                    @change="
                      () => {
                        createForm.setValue16 = int2hexFn(createForm.setValue);
                      }
                    "
                    @input="
                      () => {
                        createForm.setValue16 = int2hexFn(createForm.setValue);
                      }
                    "
                  >
                    <template #append>0x{{ createForm.setValue16 }}</template>
                  </el-input>
                  <el-input
                    style="width: 100%"
                    v-model="createForm.setValue16"
                    v-show="createForm.setValueSwitch != 'Dec'"
                    @input="
                      () => {
                        createForm.setValue = hex2intFn(createForm.setValue16);
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
                v-show="createForm.functionCode == '16'"
              >
                <el-form-item prop="registerValList">
                  <template #label>
                    <div class="form-item-label">
                      <div style="margin-right: 6px">
                        #{{ index }} {{ proxy?.$t('device.instruction-parsing.830424-23') }}
                      </div>
                      <el-tooltip :content="item.switch" placement="top">
                        <el-switch
                          v-model="item.switch"
                          size="small"
                          active-color="#13ce66"
                          @change="
                            () => {
                              refreshRegisterInpust(item, index);
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
                    style="width: 100%"
                    v-model="item.value"
                    type="number"
                    v-show="item.switch == 'Dec'"
                    :min="0"
                    @change="
                      () => {
                        item.value16 = int2hexFn(item.value);
                        refreshRegisterInpust(item, index);
                      }
                    "
                    @input="
                      () => {
                        item.value16 = int2hexFn(item.value);
                        refreshRegisterInpust(item, index);
                      }
                    "
                  >
                    <template #append>0x{{ item.value16 }}</template>
                  </el-input>
                  <el-input
                    style="width: 100%"
                    v-model="item.value16"
                    v-show="item.switch != 'Dec'"
                    @input="
                      () => {
                        item.value = hex2intFn(item.value16);
                        refreshRegisterInpust(item, index);
                      }
                    "
                  >
                    <template #append>{{ item.value }}</template>
                  </el-input>
                </el-form-item>
              </el-col>
              <!-- 批量上报值 -->
              <el-col
                :span="12"
                v-for="(item, index) in reportValList"
                :key="'report' + index"
                v-show="createForm.start == '0xFFAA'"
              >
                <el-form-item prop="reportValList">
                  <template #label>
                    <div class="form-item-label">
                      <div style="margin-right: 6px">
                        #{{ index }} {{ proxy?.$t('device.instruction-parsing.830424-24') }}
                      </div>
                      <el-tooltip :content="item.switch" placement="top">
                        <el-switch
                          v-model="item.switch"
                          size="small"
                          active-color="#13ce66"
                          @change="
                            () => {
                              refreshReportValList(item, index);
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
                    style="width: 100%"
                    v-model="item.value"
                    type="number"
                    v-show="item.switch == 'Dec'"
                    :min="0"
                    @change="
                      () => {
                        item.value16 = int2hexFn(item.value);
                        refreshReportValList(item, index);
                      }
                    "
                    @input="
                      () => {
                        item.value16 = int2hexFn(item.value);
                        refreshReportValList(item, index);
                      }
                    "
                  >
                    <template #append>0x{{ item.value16 }}</template>
                  </el-input>
                  <el-input
                    style="width: 100%"
                    v-model="item.value16"
                    v-show="item.switch != 'Dec'"
                    @input="
                      () => {
                        item.value = hex2intFn(item.value16);
                        refreshReportValList(item, index);
                      }
                    "
                  >
                    <template #append>{{ item.value }}</template>
                  </el-input>
                </el-form-item>
              </el-col>
              <!-- 批量写线圈值 -->
              <el-col
                :span="6"
                v-for="(item, index) in IOValList"
                :key="'IO' + index"
                v-show="createForm.functionCode == '15'"
              >
                <el-form-item prop="registerValList">
                  <template #label>
                    <div class="form-item-label">
                      <div style="margin-right: 6px">
                        #{{ index }} {{ proxy?.$t('device.instruction-parsing.830424-25') }}
                      </div>
                    </div>
                  </template>
                  <el-switch
                    v-model="item.value"
                    active-value="1"
                    inactive-value="0"
                    @change="
                      () => {
                        refreshIOInpust(item, index);
                      }
                    "
                  />
                </el-form-item>
              </el-col>
            </el-row>
          </el-form>
          <div v-loading="createLoading">
            <div class="create-title">
              <el-button link @click.stop="encodeCmd">
                {{ proxy?.$t('device.instruction-parsing.830424-26') }}
              </el-button>
              <div class="title-right">
                <el-button size="small" :disabled="!canSend" @click="openEditName({ command: createCode })">
                  {{ proxy?.$t('device.instruction-parsing.830424-27') }}
                </el-button>
                <el-button type="primary" :disabled="!canSend" size="small" @click="copyTextFn(createCode)">
                  {{ proxy?.$t('device.instruction-parsing.830424-28') }}
                </el-button>
              </div>
            </div>
            <div class="create-code">{{ createCode }}</div>
          </div>
        </div>
      </div>
      <div class="form_Json" v-else v-show="!editName" v-loading="modelLoading">
        <div class="list" v-if="totalCount !== 0">
          <el-descriptions
            class="margin-top"
            :title="proxy?.$t('device.device-edit.148398-94')"
            :column="1"
            border
            size="small"
          >
            <el-descriptions-item v-for="(item, index) in opationList" :key="index">
              <template #label>
                <div>{{ item.label }}</div>
              </template>
              <div class="value">
                <span v-show="item.dataTypeName !== 'bool' && item.dataTypeName !== 'enum'">
                  {{ funValue[item.key] ? funValue[item.key] : '--' }}
                </span>
                <span v-show="item.dataTypeName === 'bool' || item.dataTypeName === 'enum'">
                  {{ funShowValue[item.key] ? funShowValue[item.key] : '--' }}
                </span>
                <div>
                  <el-icon
                    style="margin-right: 20px; color: #486ff2"
                    v-show="funParam[item.key] && funParam[item.key] !== null && funParam[item.key] !== ''"
                  >
                    <SuccessFilled />
                  </el-icon>

                  <el-popover placement="bottom" width="500" :ref="(el: any) => setPopoverRef(el, index)">
                    <el-form>
                      <el-form-item :label="`${item.label}：`" :key="index" label-width="120px">
                        <el-input
                          v-model="funVal[item.key]"
                          :precision="0"
                          :controls="false"
                          @input="justNumber(item)"
                          type="number"
                          v-show="
                            item.dataTypeName == 'integer' ||
                            item.dataTypeName == 'decimal' ||
                            (item.dataTypeName == 'array' && item.arrayType == 'integer') ||
                            (item.dataTypeName == 'array' && item.arrayType == 'decimal')
                          "
                          style="width: 200px"
                        ></el-input>
                        <el-input
                          v-model="funVal[item.key]"
                          :precision="0"
                          :controls="false"
                          :placeholder="proxy?.$t('device.running-status.866086-35')"
                          type="text"
                          v-show="
                            item.dataTypeName == 'string' ||
                            (item.dataTypeName == 'array' && item.arrayType == 'string')
                          "
                          style="width: 230px"
                          @input="justNumber(item)"
                        ></el-input>
                        <el-select
                          v-show="item.dataTypeName == 'bool'"
                          v-model="funVal[item.key]"
                          @change="changeSelect()"
                          style="width: 230px"
                        >
                          <el-option
                            v-for="option in item.options"
                            :key="option.value"
                            :label="option.label"
                            :value="option.value"
                          ></el-option>
                        </el-select>
                        <div v-show="item.dataTypeName == 'enum'">
                          <div v-show="item.showWay && item.showWay == 'button'">
                            <el-button
                              style="margin: 5px"
                              size="small"
                              @click="enumButtonClick(deviceInfo, item, subItem.value)"
                              v-for="subItem in item.options"
                              :key="subItem.value"
                              :disabled="item.isReadonly == 1 || item.type == 3"
                              :class="{ 'is-active-btn': subItem.value === (item.shadow || item.value) }"
                            >
                              {{ subItem.label }}
                            </el-button>
                          </div>
                          <el-select
                            v-show="!item.showWay || item.showWay !== 'button'"
                            v-model="funVal[item.key]"
                            @change="changeSelect()"
                          >
                            <el-option
                              v-for="option in item.options"
                              :key="option.value"
                              :label="option.label"
                              :value="option.value"
                            ></el-option>
                          </el-select>
                        </div>
                        <span
                          v-if="
                            (item.dataTypeName == 'integer' ||
                              item.dataTypeName == 'decimal' ||
                              (item.dataTypeName == 'array' && item.arrayType == 'integer') ||
                              (item.dataTypeName == 'array' && item.arrayType == 'decimal')) &&
                            item.unit &&
                            item.unit != 'un' &&
                            item.unit != '/'
                          "
                        >
                          ({{ item.unit }})
                        </span>
                        <span
                          style="margin-left: 5px"
                          v-if="
                            item.dataTypeName == 'integer' ||
                            item.dataTypeName == 'decimal' ||
                            (item.dataTypeName == 'array' && item.arrayType == 'integer') ||
                            (item.dataTypeName == 'array' && item.arrayType == 'decimal')
                          "
                        >
                          （{{ item.min }} ~ {{ item.max }}）
                        </span>
                      </el-form-item>
                    </el-form>
                    <div style="text-align: right; margin: 0">
                      <el-button type="primary" size="small" @click="updateModel(index, item.key)">
                        {{ proxy?.$t('device.allot-import-dialog.060657-12') }}
                      </el-button>
                    </div>
                    <template #reference>
                      <el-button size="small" :icon="EditPen"></el-button>
                    </template>
                  </el-popover>
                  <el-button
                    size="small"
                    :icon="RefreshLeft"
                    @click="restore(item.key)"
                    style="margin-left: 10px"
                  ></el-button>
                </div>
              </div>
            </el-descriptions-item>
          </el-descriptions>
        </div>
        <el-empty v-if="totalCount === 0" description="没有可以下发指令的物模型"></el-empty>

        <pagination
          style="margin-bottom: 50px"
          v-show="totalCount > 0"
          :total="totalCount"
          v-model:page="pageNum"
          v-model:limit="pageSize"
          :pageSizes="[10, 20, 40, 80]"
          @pagination="getVariableList"
        />
        <div v-loading="createLoading">
          <div class="create-title">
            <el-button link @click.stop="encodeCmd">{{ proxy?.$t('device.instruction-parsing.830424-26') }}</el-button>
            <div class="title-right">
              <el-button size="small" :disabled="!canSend" @click="openEditName({ command: createCode })">
                {{ proxy?.$t('device.instruction-parsing.830424-27') }}
              </el-button>
              <el-button type="primary" :disabled="!canSend" size="small" @click="copyTextFn(createCode)">
                {{ proxy?.$t('device.instruction-parsing.830424-28') }}
              </el-button>
            </div>
          </div>
          <div class="create-code">{{ createCode }}</div>
        </div>
      </div>
      <div v-show="editName" class="dialog-content">
        <el-form :model="editNameForm" label-width="100px">
          <el-form-item :label="proxy?.$t('device.instruction-parsing.830424-29')">
            <el-input
              v-model="editNameForm.name"
              :placeholder="proxy?.$t('device.instruction-parsing.830424-30')"
              style="width: 350px"
            ></el-input>
          </el-form-item>
          <el-form-item :label="proxy?.$t('device.instruction-parsing.830424-31')">
            <el-input v-model="editNameForm.command" :disabled="true" style="width: 350px"></el-input>
          </el-form-item>
        </el-form>
      </div>

      <template #footer>
        <div class="dialog-btn">
          <el-button type="primary" :disabled="!canSend" @click="confrimBtn" v-loading="saveLoading">
            {{ proxy?.$t('device.instruction-parsing.830424-33') }}
          </el-button>
          <el-button @click="editDialog = false">{{ proxy?.$t('device.instruction-parsing.830424-32') }}</el-button>
        </div>
      </template>
    </el-dialog>

    <el-dialog v-model="delDialog" :title="proxy?.$t('device.instruction-parsing.830424-34')" width="420px">
      <div style="padding: 20px">
        {{ proxy?.$t('device.instruction-parsing.830424-35') }}{{ delItem.name
        }}{{ proxy?.$t('device.instruction-parsing.830424-36') }}
      </div>
      <template #footer>
        <div class="dialog-btn">
          <el-button type="primary" @click="delQuick">
            {{ proxy?.$t('device.instruction-parsing.830424-38') }}
          </el-button>
          <el-button @click="delDialog = false">{{ proxy?.$t('device.instruction-parsing.830424-37') }}</el-button>
        </div>
      </template>
    </el-dialog>

    <el-dialog :title="proxy?.$t('device.instruction-parsing.830424-39')" width="700px" v-model="moreDialog">
      <div class="dialog-content">
        <el-table :data="analysisList" height="400" :border="false">
          <el-table-column
            type="index"
            :label="proxy?.$t('device.instruction-parsing.830424-40')"
            align="center"
            min-width="100"
          />
          <el-table-column
            prop="name"
            :label="proxy?.$t('device.instruction-parsing.830424-41')"
            align="left"
            min-width="160"
          ></el-table-column>
          <el-table-column
            prop="id"
            :label="proxy?.$t('device.instruction-parsing.830424-42')"
            align="left"
            min-width="120"
          ></el-table-column>
          <el-table-column
            prop="value"
            :label="proxy?.$t('device.instruction-parsing.830424-43')"
            align="left"
            min-width="120"
          ></el-table-column>
        </el-table>
      </div>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, computed, watch, onMounted, onBeforeUnmount, getCurrentInstance } from 'vue';
import { hex2int, int2hex, copyText, formatDate, deepClone } from '@/utils/common';
import {
  encode,
  jsonEncode,
  messagePost,
  addPreferences,
  editPreferences,
  delPreferences,
  preferencesList,
} from '@/api/iot/mqttTest';
import { listThingsModel } from '@/api/iot/device';
import { getOrderControl } from '@/api/iot/control';
import { EditPen, RefreshLeft, ArrowDown, SuccessFilled } from '@element-plus/icons-vue';
import { useDict } from '@/utils/dict';
import { useUserStore } from '@/stores/modules/user';

const { proxy } = getCurrentInstance() as any;
const userStore = useUserStore();
const { product_command_function_code } = useDict('product_command_function_code');

const props = defineProps({
  device: { type: Object, default: null },
});

const format = ref('Hex');
const formatList = ref<any[]>([]);
const logList = ref<any[]>([]);
const sendVal = ref('');
const topic = ref('/function/get');
const dataType = ref('hex');
const quickParsing = ref<any[]>([]);
const editDialog = ref(false);
const createForm = ref<any>({});
const startList = ref([
  { label: proxy?.$t('device.instruction-parsing.830424-56'), value: '0xFFDD' },
  { label: proxy?.$t('device.instruction-parsing.830424-57'), value: '0xFFAA' },
]);
const createCode = ref('');
const registerValList = ref<any[]>([]);
const reportValList = ref<any[]>([]);
const IOValList = ref<any[]>([]);
const editName = ref(false);
const editNameForm = ref<any>({});
const createLoading = ref(false);
const delDialog = ref(false);
const delItem = ref<any>({});
const analysisList = ref<any[]>([]);
const moreDialog = ref(false);
const saveLoading = ref(false);
const canSend = ref(false);
const modelLoading = ref(false);
const variableList = ref<any[]>([]);
const funVal = ref<any>({});
const funValue = ref<any>({});
const funStart = ref<any>({});
const funParam = ref<any>({});
const funShow = ref<any>({});
const funShowValue = ref<any>({});
const opationList = ref<any[]>([]);
const totalCount = ref<any>(null);
const pageNum = ref(1);
const pageSize = ref(10);
const deviceInfo = ref<any>({});

// popover refs
const popoverRefs = ref<any>({});
function setPopoverRef(el: any, index: number) {
  if (el) popoverRefs.value[`popover-${index}`] = el;
}

const registerNumTitle = computed(() => {
  switch (createForm.value.functionCode) {
    case '01':
    case '02':
    case '15':
      return proxy?.$t('device.instruction-parsing.830424-44');
    case '03':
    case '04':
    case '16':
      return proxy?.$t('device.instruction-parsing.830424-45');
    case '05':
      return proxy?.$t('device.instruction-parsing.830424-46');
    case '06':
      return proxy?.$t('device.instruction-parsing.830424-47');
    default:
      return '';
  }
});

watch(
  () => props.device,
  () => {
    getPreferencesList();
  }
);

onMounted(() => {
  getPreferencesList();
  getVariableList();
  resetCreateForm();
  deviceInfo.value = props.device;

  // Vue3中事件总线需要使用mitt，暂时注释
  // proxy?.$busEvent?.$on('updateData', (params: any) => {
  //     const { serialNumber, productId, data } = params
  //     if (serialNumber == props.device.serialNumber) {
  //         logList.value.push({
  //             type: 2,
  //             time: formatDate(new Date()),
  //             value: data.sources,
  //             loading: false,
  //             analysis: false,
  //             analysisList: data.message,
  //         })
  //     }
  // })

  if (
    props.device.protocolCode === 'MODBUS-TCP' ||
    props.device.protocolCode === 'MODBUS-RTU' ||
    props.device.protocolCode === 'MODBUS-TCP-OVER-RTU'
  ) {
    formatList.value = [
      { label: 'hex', value: 'Hex', disabled: false },
      { label: 'json', value: 'JSON', disabled: true },
      { label: 'plaintext', value: 'Plaintext', disabled: true },
    ];
    format.value = 'Hex';
    dataType.value = 'hex';
  } else {
    formatList.value = [
      { label: 'hex', value: 'Hex', disabled: true },
      { label: 'json', value: 'JSON', disabled: false },
      { label: 'plaintext', value: 'Plaintext', disabled: true },
    ];
    format.value = 'JSON';
    dataType.value = 'json';
  }
});

onBeforeUnmount(() => {
  // Vue3中事件总线需要使用mitt，暂时注释
  // proxy?.$busEvent?.$off('updateMqttMessage')
});

function int2hexFn(str: any) {
  return int2hex(str);
}
function hex2intFn(str: any) {
  return hex2int(str);
}

function openEditDialog() {
  resetCreateForm();
  getVariableList();
  editName.value = false;
  editDialog.value = true;
  canSend.value = false;
}

function openMore(item: any) {
  analysisList.value = item.analysisList || [];
  moreDialog.value = true;
}

function openEditName(item: any) {
  editNameForm.value = { name: item.name || '', command: item.command };
  editName.value = true;
  editDialog.value = true;
}

function resetCreateForm() {
  createForm.value = {
    path: '01',
    functionCode: '01',
    startPath: 0,
    startPath16: '0000',
    registerNum: 1,
    startPathSwitch: 'Dec',
    setValue: 0,
    setValue16: '0000',
    setValueSwitch: 'Dec',
    start: '0xFFDD',
  };
  createCode.value = '';
}

function changeNum() {
  if (createForm.value.start == '0xFFAA') {
    for (let index = 0; index < createForm.value.registerNum; index++) {
      if (!reportValList.value[index]) {
        reportValList.value[index] = { value: 0, value16: '0000', switch: 'Dec' };
      }
    }
    if (reportValList.value.length > createForm.value.registerNum) {
      reportValList.value.splice(
        createForm.value.registerNum,
        reportValList.value.length - createForm.value.registerNum
      );
    }
    createForm.value.functionCode = '03';
  } else {
    if (createForm.value.functionCode == '16') {
      for (let index = 0; index < createForm.value.registerNum; index++) {
        if (!registerValList.value[index]) {
          registerValList.value[index] = { value: 0, value16: '0000', switch: 'Dec' };
        }
      }
      if (registerValList.value.length > createForm.value.registerNum) {
        registerValList.value.splice(
          createForm.value.registerNum,
          registerValList.value.length - createForm.value.registerNum
        );
      }
    }
    if (createForm.value.functionCode == '15') {
      for (let index = 0; index < createForm.value.registerNum; index++) {
        if (!IOValList.value[index]) {
          IOValList.value[index] = { value: '0' };
        }
      }
      if (IOValList.value.length > createForm.value.registerNum) {
        IOValList.value.splice(createForm.value.registerNum, IOValList.value.length - createForm.value.registerNum);
      }
    }
  }
}

function refreshRegisterInpust(item: any, index: number) {
  registerValList.value[index] = { ...item };
}

function refreshReportValList(item: any, index: number) {
  reportValList.value[index] = { ...item };
}

function refreshIOInpust(item: any, index: number) {
  IOValList.value[index] = { ...item };
}

async function confrimBtn() {
  if (editName.value) {
    try {
      saveLoading.value = true;
      if (editNameForm.value.id) {
        await editPreferences({
          command: editNameForm.value.command,
          name: editNameForm.value.name,
          serialNumber: props.device.serialNumber,
          id: editNameForm.value.id,
        });
      } else {
        await addPreferences({
          command: editNameForm.value.command,
          name: editNameForm.value.name,
          serialNumber: props.device.serialNumber,
        });
      }
      proxy?.$modal.msgSuccess(
        editNameForm.value.id
          ? proxy?.$t('device.instruction-parsing.830424-58')
          : proxy?.$t('device.instruction-parsing.830424-59') + proxy?.$t('device.instruction-parsing.830424-60')
      );
      getPreferencesList();
    } catch (err: any) {
      proxy?.$modal.msgError(
        err.message ||
          (editNameForm.value.id
            ? proxy?.$t('device.instruction-parsing.830424-58')
            : proxy?.$t('device.instruction-parsing.830424-59') + proxy?.$t('device.instruction-parsing.830424-61'))
      );
    }
  } else {
    sendVal.value = createCode.value;
  }
  funParam.value = {};
  funVal.value = {};
  funValue.value = {};
  funShow.value = {};
  funShowValue.value = {};
  funStart.value = {};
  saveLoading.value = false;
  editDialog.value = false;
}

async function getPreferencesList() {
  try {
    const { rows } = (await preferencesList({ serialNumber: props.device.serialNumber })) as any;
    quickParsing.value = rows;
  } catch (err) {
    console.log('getPreferencesList error:', err);
  }
}

function quickClick(item: any) {
  sendVal.value = item.command;
}

function copyTextFn(command: string) {
  const res = copyText(command);
  proxy?.$modal.msgSuccess(res.message);
}

async function encodeCmd() {
  if (
    props.device.protocolCode === 'MODBUS-TCP' ||
    props.device.protocolCode === 'MODBUS-RTU' ||
    props.device.protocolCode === 'MODBUS-TCP-OVER-RTU'
  ) {
    try {
      createLoading.value = true;
      let params: any = {
        address: parseInt(createForm.value.path),
        register: createForm.value.startPath,
        code: parseInt(createForm.value.functionCode),
        start: createForm.value.start,
        protocolCode: props.device.protocolCode,
        serialNumber: props.device.serialNumber,
      };
      if (createForm.value.start == '0xFFAA') {
        params.address = createForm.value.startPath;
        params.bitCount = createForm.value.registerNum * 2;
        params.data = reportValList.value.map((item) => item.value);
      } else {
        switch (createForm.value.functionCode) {
          case '01':
          case '02':
          case '03':
          case '04':
            params.count = createForm.value.registerNum;
            break;
          case '05':
          case '06':
            params.writeData = createForm.value.setValue;
            break;
          case '15':
            params.count = createForm.value.registerNum;
            params.bitString = IOValList.value.map((item) => item.value).join('');
            break;
          case '16':
            params.count = createForm.value.registerNum;
            params.tenWriteData = registerValList.value.map((item) => item.value);
            break;
        }
      }
      const res = await encode(params);
      createCode.value = (res as any).msg;
    } catch (err: any) {
      proxy?.$modal.msgError(err.message || proxy?.$t('device.instruction-parsing.830424-62'));
    } finally {
      createLoading.value = false;
      canSend.value = true;
    }
  } else {
    try {
      const modelParams = {
        serialNumber: props.device.serialNumber,
        params: { ...funParam.value },
        dp: { productId: props.device.productId, protocolCode: props.device.protocolCode },
      };
      const res = await jsonEncode(modelParams);
      createCode.value = (res as any).msg;
    } catch (err: any) {
      proxy?.$modal.msgError(err.message || proxy?.$t('device.instruction-parsing.830424-62'));
    } finally {
      createLoading.value = false;
      canSend.value = true;
    }
  }
}

async function sendMessage() {
  const userName = userStore.dept?.userName;
  if (userName !== props.device.createBy) {
    const params = { deviceId: props.device.deviceId, modelId: '' };
    const response = (await getOrderControl(params)) as any;
    if (response.code != 200) {
      proxy?.$modal.msgWarning(response.msg);
      return;
    }
  }
  if (props.device.status !== 3) {
    let title = '';
    if (props.device.status === 1) title = proxy?.$t('device.device-variable.930930-0');
    else if (props.device.status === 2) title = proxy?.$t('device.device-variable.930930-1');
    else title = proxy?.$t('device.device-variable.930930-2');
    proxy?.$modal.msgWarning(title);
    return;
  }
  try {
    if (sendVal.value) {
      messagePost({
        message: sendVal.value,
        serialNumber: props.device.serialNumber,
        topicName: topic.value,
        dataType: dataType.value,
      }).then(() => {
        logList.value.push({
          type: 0,
          time: formatDate(new Date()),
          value: sendVal.value,
          loading: false,
          analysis: false,
          analysisList: [],
        });
      });
    } else {
      proxy?.$modal.msgError(proxy?.$t('device.instruction-parsing.830424-63'));
    }
  } catch (err: any) {
    proxy?.$modal.msgError(err.message || proxy?.$t('device.instruction-parsing.830424-64'));
  }
}

function analysisData(item: any, index: number) {
  try {
    item.loading = true;
    item.analysis = true;
    if (item.analysisList[0]) {
      item.analysisVal = {
        name: item.analysisList[0].name,
        value: item.analysisList[0].value,
        id: item.analysisList[0].id,
      };
    } else {
      proxy?.$modal.msgError(proxy?.$t('device.instruction-parsing.830424-65'));
    }
  } catch (err: any) {
    proxy?.$modal.msgError(err.message || proxy?.$t('device.instruction-parsing.830424-66'));
  } finally {
    item.loading = false;
  }
}

function onContextmenu(event: MouseEvent, item: any) {
  // Context menu - simplified for Vue 3 (no $contextmenu plugin)
  // Use right-click to edit/delete
  const action = confirm(proxy?.$t('device.instruction-parsing.830424-67') + '?');
  if (action) {
    editNameForm.value = deepClone(item);
    editName.value = true;
    editDialog.value = true;
  }
}

async function delQuick() {
  try {
    await delPreferences(delItem.value);
    proxy?.$modal.msgSuccess(proxy?.$t('device.instruction-parsing.830424-69'));
    delDialog.value = false;
    getPreferencesList();
  } catch (err: any) {
    proxy?.$modal.msgError(err.message || proxy?.$t('device.instruction-parsing.830424-70'));
  }
}

function getVariableList() {
  modelLoading.value = true;
  opationList.value = [];
  let queryParams: any = {
    deviceId: props.device.deviceId,
    type: null,
    modelName: '',
    pageNum: pageNum.value,
    pageSize: pageSize.value,
    isMonnitor: '',
    showSubDevice: true,
    isReadonly: 0,
  };
  listThingsModel(queryParams).then((res: any) => {
    if (res.code === 200) {
      totalCount.value = res.total;
      variableList.value = res.rows.map((item: any) => ({ ...item, valueName: getValueName(item) || '-' }));
      variableList.value = variableList.value.sort((a: any, b: any) => b.order - a.order);
      variableList.value.forEach((item: any) => {
        getOpationList(item);
      });
    }
    modelLoading.value = false;
  });
}

function getValueName(item: any) {
  let res = item.value || '-';
  if (item.datatype) {
    switch (item.datatype.type) {
      case 'bool':
        if (0 == item.value) res = item.datatype.falseText;
        if (1 == item.value) res = item.datatype.trueText;
        break;
      case 'enum':
        item.datatype.enumList?.some((enumOpt: any) => {
          if (enumOpt.value == item.value) {
            res = enumOpt.text;
            return true;
          }
        });
        break;
    }
  }
  return res;
}

function getOpationList(item: any) {
  let options: any[] = [];
  const datatype = item.datatype;
  if (datatype.type == 'enum') {
    options = datatype.enumList?.map((option: any) => ({ label: option.text, value: option.value + '' })) || [];
  }
  if (datatype.type == 'bool') {
    options = [
      { label: datatype.falseText || '', value: '0' },
      { label: datatype.trueText || '', value: '1' },
    ];
  }
  opationList.value.push({
    dataTypeName: datatype.type,
    arrayType: datatype.arrayType,
    label: item.modelName,
    key: item.identifier,
    max: parseInt(datatype?.max || 100),
    min: parseInt(datatype?.min || -100),
    options: options,
    value: item.value,
  });
  opationList.value.forEach((opItem: any) => {
    let value = opItem.value;
    if (
      opItem.datatype == 'integer' ||
      opItem.datatype == 'decimal' ||
      (opItem.dataTypeName == 'array' && opItem.arrayType == 'integer') ||
      (opItem.dataTypeName == 'array' && opItem.arrayType == 'decimal')
    ) {
      value = parseInt(value);
    }
    if (funParam.value[opItem.key] && funParam.value[opItem.key] !== '' && funParam.value[opItem.key] !== null) {
      funVal.value[opItem.key] = funParam.value[opItem.key];
      funValue.value[opItem.key] = funParam.value[opItem.key];
    } else {
      funVal.value[opItem.key] = value;
      funValue.value[opItem.key] = value;
    }
    funStart.value[opItem.key] = value;
    if (opItem.dataTypeName === 'enum' || opItem.dataTypeName === 'bool') {
      let opts = opItem.options;
      funShow.value[opItem.key] = opts;
      let label = opts.find((optionItem: any) => optionItem.value === funValue.value[opItem.key]);
      funShowValue.value[opItem.key] = label?.label;
    }
  });
  modelLoading.value = false;
}

function justNumber(val: any) {
  canSend.value = true;
  opationList.value.some((item: any) => {
    if (item.max < funVal.value[item.key] || item.min > funVal.value[item.key]) {
      canSend.value = false;
      return true;
    }
  });
}

function changeSelect() {
  // trigger reactivity
  funVal.value = { ...funVal.value };
}

function enumButtonClick(device: any, item: any, value: any) {
  funVal.value = { ...funVal.value, [item.key]: value };
}

function updateModel(index: number, key: string) {
  if (funVal.value[key] !== funValue.value[key]) {
    funValue.value = { ...funValue.value, [key]: funVal.value[key] };
    funParam.value = { ...funParam.value, [key]: funVal.value[key] };
    let label = funShow.value[key]?.find((item: any) => item.value === funVal.value[key]);
    funShowValue.value = { ...funShowValue.value, [key]: label?.label };
  }
  if (funParam.value[key] && funVal.value[key] === funStart.value[key]) {
    const newParam = { ...funParam.value };
    delete newParam[key];
    funParam.value = newParam;
  }
  popoverRefs.value[`popover-${index}`]?.hide?.();
}

function restore(key: string) {
  if (funVal.value[key] !== funStart.value[key]) {
    funValue.value = { ...funValue.value, [key]: funStart.value[key] };
    funVal.value = { ...funVal.value, [key]: funStart.value[key] };
    const newParam = { ...funParam.value };
    delete newParam[key];
    funParam.value = newParam;
    let label = funShow.value[key]?.find((item: any) => item.value === funStart.value[key]);
    funShowValue.value = { ...funShowValue.value, [key]: label?.label };
  }
}
</script>

<style lang="scss" scoped>
$border-color: #dcdfe6;
$right-btn-color: #486ff2;

.instruction-parsing {
  border: 1px solid $border-color;
  border-radius: 8px;

  .left {
    border-right: 1px solid $border-color;

    .bottom {
      display: flex;
      border-top: 1px solid $border-color;
      position: relative;
      height: 64px;
      align-items: center;

      :deep(.el-select .el-input) {
        width: 80px;
      }
      :deep(.el-input-group--prepend .el-input__inner) {
        border: 0px;
        background-color: #fff;
      }
      :deep(.el-input-group__prepend) {
        border: 0;
      }

      .send-btn {
        position: absolute;
        top: 50%;
        transform: translateY(-50%);
        right: 24px;
      }
      .input-with-select {
        max-width: calc(100% - 80px);
      }
    }
  }

  .head {
    line-height: 48px;
    display: flex;
    padding: 0 16px;
    align-items: center;
    font-size: 14px;
    border-bottom: 1px solid $border-color;
    .el-dropdown-link {
      cursor: pointer;
    }
    .el-icon-arrow-down {
      font-size: 12px;
    }
  }

  .right {
    .right-head {
      font-size: 14px;
      text-align: center;
      width: 100%;
      justify-content: center;
    }
    .quick-item {
      width: 80%;
      margin: auto;
      text-align: center;
      line-height: 36px;
      margin-top: 12px;
      border: 1px solid $right-btn-color;
      color: $right-btn-color;
      cursor: pointer;
      border-radius: 4px;
      &:last-child {
        margin-bottom: 12px;
      }
    }
    .right-bottom {
      line-height: 64px;
      background-color: $right-btn-color;
      border-top: 1px solid $right-btn-color;
      font-size: 24px;
      color: #fff;
      text-align: center;
      cursor: pointer;
    }
  }

  .content {
    height: 600px;
    width: 100%;
    overflow: auto;
    padding: 20px;

    .send-class {
      border-left: 4px solid #e6a23c;
      margin-left: auto;
      color: #e6a23c;
      .head-time {
        margin-left: auto !important;
        color: #c0c4cc;
      }
    }
    .receive-class {
      border-left: 4px solid #67c23a;
      color: #67c23a;
    }
    .report-class {
      border-left: 4px solid #409eff;
      color: #409eff;
    }

    .item-class {
      width: 50%;
      box-sizing: border-box;
      border-radius: 4px;
      padding: 0 8px;
      background-color: #f2f6fc;
      margin-bottom: 16px;

      .item-head {
        display: flex;
        border-bottom: 1px solid $border-color;
        font-size: 14px;
        padding: 8px 0;
        align-items: center;
        .head-time {
          margin-left: 8px;
          color: #c0c4cc;
          font-size: 12px;
          margin-top: 3px;
        }
        .analysis-btn {
          background: #1890ff;
          color: #fff;
          cursor: pointer;
        }
        .right-btn {
          margin-left: auto;
          padding: 8px 12px;
          border-radius: 4px;
          line-height: 12px;
          font-size: 14px;
        }
        .analysised {
          background: #909399;
          color: #e4e7ed;
        }
      }
      .item-content {
        padding: 8px 0;
        color: #303133;
        font-size: 14px;
        line-height: 20px;
        display: flex;
        align-items: center;
        .content-value {
          max-width: 100%;
          word-wrap: break-word;
        }
      }
      .analysis-data {
        margin-top: 12px;
        font-size: 12px;
        line-height: 18px;
        width: 100%;
        color: #303133;
        font-weight: 600;
        display: flex;
        flex-wrap: wrap;
        .data-col {
          display: flex;
          margin-bottom: 10px;
          .label {
            text-align: right;
            margin-right: 12px;
            color: #c0c4cc;
            font-weight: 400;
          }
        }
        .left-col {
          width: 60%;
          .label {
            width: 64px;
          }
        }
        .right-col {
          margin-left: auto;
        }
      }
    }
  }

  .form_Json {
    padding: 30px;
    overflow: auto;
    .create-title {
      display: flex;
      line-height: 36px;
      margin-bottom: 16px;
      .title-right {
        margin-left: auto;
      }
    }
    .list {
      max-height: 350px;
      overflow: auto;
      .value {
        display: flex;
        justify-content: space-between;
        align-items: center;
      }
    }
    .create-code {
      font-size: 18px;
      line-height: 36px;
      font-weight: bold;
    }
    .form-item-label {
      display: flex;
      align-items: center;
      width: 100%;
      :deep(.el-form-item__label) {
        width: 100%;
      }
    }
  }

  :deep(.el-dialog__body) {
    box-sizing: border-box;
    padding: 0;
  }

  .dialog-content {
    max-height: 600px;
    width: 100%;
    box-sizing: border-box;
    padding: 30px 20px;
    overflow: auto;
    margin-top: -20px;
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
      font-weight: bold;
    }
    .form-item-label {
      display: flex;
      align-items: center;
      width: 100%;
      :deep(.el-form-item__label) {
        width: 100%;
      }
    }
  }
}
</style>
