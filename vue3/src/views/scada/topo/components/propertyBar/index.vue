<template>
  <div class="topo-property-bar">
    <template v-if="configObject != null && isLayer == false">
      <div class="tabs-title">
        <div class="title-item" @click="handleChangeTab(0)" :class="{ 'title-item-active': tabIndex === 0 }">
          {{ $t('topo.components.propertyBar.index.038495-0') }}
        </div>
        <div class="title-item" @click="handleChangeTab(1)" :class="{ 'title-item-active': tabIndex === 1 }">
          {{ $t('topo.components.propertyBar.index.038495-1') }}
        </div>
        <div class="title-item" @click="handleChangeTab(2)" :class="{ 'title-item-active': tabIndex === 2 }">
          {{ $t('topo.components.propertyBar.index.038495-369') }}
        </div>
      </div>
      <div class="tabs-pane">
        <!-- 组件样式 -->
        <div class="pane-item" v-show="tabIndex === 0">
          <el-collapse class="collapse-wrap" v-model="styleCollapseActive">
            <el-collapse-item :title="$t('topo.components.propertyBar.index.038495-2')" name="1">
              <el-form label-width="75px">
                <el-form-item :label="$t('topo.components.propertyBar.index.038495-3')">
                  <el-input-number
                    style="width: 100%"
                    v-model="xAxis"
                    :placeholder="$t('topo.components.propertyBar.index.038495-375')"
                    controls-position="right"
                    @change="handleXAxisChange"
                  />
                </el-form-item>
                <el-form-item :label="$t('topo.components.propertyBar.index.038495-4')">
                  <el-input-number
                    style="width: 100%"
                    v-model="yAxis"
                    :placeholder="$t('topo.components.propertyBar.index.038495-376')"
                    controls-position="right"
                    @change="handleYAxisChange"
                  />
                </el-form-item>
                <el-form-item :label="$t('topo.components.propertyBar.index.038495-5')">
                  <el-input-number
                    style="width: 100%"
                    v-model="width"
                    :placeholder="$t('topo.components.propertyBar.index.038495-377')"
                    controls-position="right"
                    @change="handleWidthChange"
                  />
                </el-form-item>
                <el-form-item :label="$t('topo.components.propertyBar.index.038495-6')">
                  <el-input-number
                    style="width: 100%"
                    v-model="height"
                    :placeholder="$t('topo.components.propertyBar.index.038495-378')"
                    controls-position="right"
                    @change="handleHeightChange"
                  />
                </el-form-item>
              </el-form>
            </el-collapse-item>
            <el-collapse-item :title="$t('topo.components.propertyBar.index.038495-7')" name="2">
              <el-form label-width="75px">
                <el-form-item
                  :label="$t('topo.components.propertyBar.index.038495-8')"
                  v-if="configObject.name !== undefined"
                >
                  <el-input
                    :placeholder="$t('topo.components.propertyBar.index.038495-9')"
                    v-model="configObject.name"
                    clearable
                  />
                </el-form-item>
                <el-form-item
                  :label="$t('topo.components.propertyBar.index.038495-10')"
                  v-if="configObject.dataBind.paramMin !== undefined"
                >
                  <el-input-number
                    style="width: 100%"
                    controls-position="right"
                    :placeholder="$t('topo.components.propertyBar.index.038495-11')"
                    v-model="configObject.dataBind.paramMin"
                  />
                </el-form-item>
                <el-form-item
                  :label="$t('topo.components.propertyBar.index.038495-12')"
                  v-if="configObject.dataBind.paramMax !== undefined"
                >
                  <el-input-number
                    style="width: 100%"
                    controls-position="right"
                    :placeholder="$t('topo.components.propertyBar.index.038495-13')"
                    v-model="configObject.dataBind.paramMax"
                  />
                </el-form-item>
                <el-form-item
                  :label="$t('topo.components.propertyBar.index.038495-133')"
                  v-if="configObject.style.lineWidth !== undefined"
                >
                  <el-input-number
                    style="width: 100%"
                    controls-position="right"
                    :placeholder="$t('topo.components.propertyBar.index.038495-134')"
                    v-model="configObject.style.lineWidth"
                    :min="1"
                  />
                </el-form-item>
                <el-form-item
                  :label="$t('topo.components.propertyBar.index.038495-14')"
                  v-if="configObject.dataBind.interval !== undefined"
                >
                  <el-input-number
                    style="width: 100%"
                    controls-position="right"
                    :placeholder="$t('topo.components.propertyBar.index.038495-15')"
                    v-model="configObject.dataBind.interval"
                  />
                </el-form-item>
                <el-form-item
                  :label="$t('topo.components.propertyBar.index.038495-16')"
                  v-if="configObject.style.weatherModel !== undefined"
                >
                  <el-select
                    v-model="configObject.style.weatherModel"
                    :placeholder="$t('topo.components.propertyBar.index.038495-17')"
                    style="width: 100%"
                  >
                    <el-option :label="$t('topo.components.propertyBar.index.038495-19')" :value="1">
                      <span style="font-size: 12px">{{ $t('topo.components.propertyBar.index.038495-19') }}</span>
                    </el-option>
                    <el-option :label="$t('topo.components.propertyBar.index.038495-18')" :value="2">
                      <span style="font-size: 12px">{{ $t('topo.components.propertyBar.index.038495-18') }}</span>
                    </el-option>
                  </el-select>
                </el-form-item>
                <el-form-item
                  :label="$t('topo.components.propertyBar.index.038495-20')"
                  v-if="configObject.style.mapModel !== undefined"
                >
                  <el-select
                    v-model="configObject.style.mapModel"
                    :placeholder="$t('topo.components.propertyBar.index.038495-21')"
                    style="width: 100%"
                  >
                    <el-option :label="$t('topo.components.propertyBar.index.038495-22')" value="normal">
                      <span style="font-size: 12px">{{ $t('topo.components.propertyBar.index.038495-22') }}</span>
                    </el-option>
                    <el-option :label="$t('topo.components.propertyBar.index.038495-23')" value="light">
                      <span style="font-size: 12px">{{ $t('topo.components.propertyBar.index.038495-23') }}</span>
                    </el-option>
                    <el-option :label="$t('topo.components.propertyBar.index.038495-24')" value="dark">
                      <span style="font-size: 12px">{{ $t('topo.components.propertyBar.index.038495-24') }}</span>
                    </el-option>
                    <el-option :label="$t('topo.components.propertyBar.index.038495-25')" value="redalert">
                      <span style="font-size: 12px">{{ $t('topo.components.propertyBar.index.038495-25') }}</span>
                    </el-option>
                    <el-option :label="$t('topo.components.propertyBar.index.038495-26')" value="googlelite">
                      <span style="font-size: 12px">{{ $t('topo.components.propertyBar.index.038495-26') }}</span>
                    </el-option>
                    <el-option :label="$t('topo.components.propertyBar.index.038495-27')" value="grassgreen">
                      <span style="font-size: 12px">{{ $t('topo.components.propertyBar.index.038495-27') }}</span>
                    </el-option>
                    <el-option :label="$t('topo.components.propertyBar.index.038495-28')" value="midnight">
                      <span style="font-size: 12px">{{ $t('topo.components.propertyBar.index.038495-28') }}</span>
                    </el-option>
                    <el-option :label="$t('topo.components.propertyBar.index.038495-29')" value="pink">
                      <span style="font-size: 12px">{{ $t('topo.components.propertyBar.index.038495-29') }}</span>
                    </el-option>
                    <el-option :label="$t('topo.components.propertyBar.index.038495-30')" value="darkgreen">
                      <span style="font-size: 12px">{{ $t('topo.components.propertyBar.index.038495-30') }}</span>
                    </el-option>
                    <el-option :label="$t('topo.components.propertyBar.index.038495-31')" value="bluish">
                      <span style="font-size: 12px">{{ $t('topo.components.propertyBar.index.038495-31') }}</span>
                    </el-option>
                    <el-option :label="$t('topo.components.propertyBar.index.038495-32')" value="grayscale">
                      <span style="font-size: 12px">{{ $t('topo.components.propertyBar.index.038495-32') }}</span>
                    </el-option>
                    <el-option :label="$t('topo.components.propertyBar.index.038495-33')" value="hardedge">
                      <span style="font-size: 12px">{{ $t('topo.components.propertyBar.index.038495-33') }}</span>
                    </el-option>
                  </el-select>
                </el-form-item>
                <el-form-item :label="$t('topo.components.propertyBar.index.038495-34')">
                  <el-input-number
                    style="width: 100%"
                    controls-position="right"
                    :placeholder="$t('topo.components.propertyBar.index.038495-35')"
                    v-model="configObject.style.zIndex"
                  />
                </el-form-item>
                <el-form-item
                  :label="$t('topo.components.propertyBar.index.038495-36')"
                  v-if="isLayer === false && configObject.type !== 'map'"
                >
                  <div class="color-picker-wrap">
                    <el-color-picker
                      v-model="configObject.style.backColor"
                      show-alpha
                      :predefine="predefineColors"
                      color-format="hex"
                    />
                  </div>
                </el-form-item>
                <template
                  v-if="
                    isLayer === false &&
                    configObject.type !== 'flow-bar' &&
                    configObject.type !== 'flow-bar-dynamic' &&
                    configObject.type !== 'map'
                  "
                >
                  <el-form-item :label="$t('topo.components.propertyBar.index.038495-37')">
                    <el-input-number
                      style="width: 100%"
                      controls-position="right"
                      placeholder="0.5"
                      v-model="configObject.style.opacity"
                    />
                  </el-form-item>
                  <el-form-item :label="$t('topo.components.propertyBar.index.038495-38')">
                    <el-input-number
                      style="width: 100%"
                      controls-position="right"
                      placeholder="10"
                      v-model="configObject.style.borderRadius"
                    />
                  </el-form-item>
                  <el-form-item :label="$t('topo.components.propertyBar.index.038495-39')">
                    <el-input-number
                      style="width: 100%"
                      controls-position="right"
                      placeholder="20"
                      v-model="configObject.style.boxShadowWidth"
                    />
                  </el-form-item>
                  <el-form-item :label="$t('topo.components.propertyBar.index.038495-40')">
                    <div class="color-picker-wrap">
                      <el-color-picker
                        v-model="configObject.style.boxShadowColor"
                        show-alpha
                        :predefine="predefineColors"
                        color-format="hex"
                      />
                    </div>
                  </el-form-item>
                </template>
                <el-form-item
                  :label="$t('topo.components.propertyBar.index.038495-41')"
                  v-if="configObject.componentShow && configObject.componentShow.indexOf('设备统计') !== -1"
                >
                  <el-select
                    v-model="configObject.style.pieType"
                    :placeholder="$t('topo.components.propertyBar.index.038495-43')"
                    style="width: 100%"
                  >
                    <el-option :label="$t('topo.components.propertyBar.index.038495-44')" value="设备状态">
                      <span style="font-size: 12px">{{ $t('topo.components.propertyBar.index.038495-44') }}</span>
                    </el-option>
                    <el-option :label="$t('topo.components.propertyBar.index.038495-45')" value="报警状态">
                      <span style="font-size: 12px">{{ $t('topo.components.propertyBar.index.038495-45') }}</span>
                    </el-option>
                  </el-select>
                </el-form-item>
                <el-form-item
                  :label="$t('topo.components.propertyBar.index.038495-46')"
                  v-if="configObject.style.url !== undefined && configObject.style.url !== null"
                >
                  <el-button style="width: 100%" class="gen-btn" @click="handleGalleryClick('图片路径')">
                    {{ $t('topo.components.propertyBar.index.038495-48') }}
                  </el-button>
                </el-form-item>
                <el-form-item
                  :label="$t('topo.components.propertyBar.index.038495-49')"
                  v-if="configObject.style.text !== undefined"
                >
                  <el-input
                    :placeholder="$t('topo.components.propertyBar.index.038495-50')"
                    v-model="configObject.style.text"
                    clearable
                  />
                </el-form-item>
                <el-form-item
                  :label="$t('topo.components.propertyBar.index.038495-51')"
                  v-if="configObject.style.textAlign !== undefined"
                >
                  <el-select
                    v-model="configObject.style.textAlign"
                    :placeholder="$t('topo.components.propertyBar.index.038495-52')"
                    style="width: 100%"
                  >
                    <el-option :label="$t('topo.components.propertyBar.index.038495-53')" value="left">
                      <span style="font-size: 12px">{{ $t('topo.components.propertyBar.index.038495-53') }}</span>
                    </el-option>
                    <el-option :label="$t('topo.components.propertyBar.index.038495-54')" value="center">
                      <span style="font-size: 12px">{{ $t('topo.components.propertyBar.index.038495-54') }}</span>
                    </el-option>
                    <el-option :label="$t('topo.components.propertyBar.index.038495-55')" value="right">
                      <span style="font-size: 12px">{{ $t('topo.components.propertyBar.index.038495-55') }}</span>
                    </el-option>
                  </el-select>
                </el-form-item>
                <el-form-item
                  :label="$t('topo.components.propertyBar.index.038495-56')"
                  v-if="configObject.style.fontFamily !== undefined"
                >
                  <el-select
                    v-model="configObject.style.fontFamily"
                    :placeholder="$t('topo.components.propertyBar.index.038495-57')"
                    style="width: 100%"
                  >
                    <el-option :label="item" :value="item" v-for="item in fontFamilyOptions" :key="item">
                      <span style="font-size: 12px">{{ item }}</span>
                    </el-option>
                  </el-select>
                </el-form-item>
                <el-form-item
                  :label="$t('topo.components.propertyBar.index.038495-308')"
                  v-if="configObject.style.fontStyle !== undefined"
                >
                  <el-select
                    v-model="configObject.style.fontStyle"
                    :placeholder="$t('topo.components.propertyBar.index.038495-309')"
                    style="width: 100%"
                  >
                    <el-option :label="item.text" :value="item.id" v-for="item in fontStyleOptions" :key="item.id">
                      <span style="font-size: 12px">{{ item.text }}</span>
                    </el-option>
                  </el-select>
                </el-form-item>
                <el-form-item
                  :label="$t('topo.components.propertyBar.index.038495-310')"
                  v-if="configObject.style.fontWeight !== undefined"
                >
                  <el-select
                    v-model="configObject.style.fontWeight"
                    :placeholder="$t('topo.components.propertyBar.index.038495-311')"
                    style="width: 100%"
                  >
                    <el-option :label="item.text" :value="item.id" v-for="item in fontWeightOptions" :key="item.id">
                      <span style="font-size: 12px">{{ item.text }}</span>
                    </el-option>
                  </el-select>
                </el-form-item>
                <el-form-item
                  :label="$t('topo.components.propertyBar.index.038495-58')"
                  v-if="configObject.style.fontSize !== undefined"
                >
                  <el-input-number
                    style="width: 100%"
                    controls-position="right"
                    :placeholder="$t('topo.components.propertyBar.index.038495-59')"
                    v-model="configObject.style.fontSize"
                  />
                </el-form-item>
                <el-form-item
                  :label="$t('topo.components.propertyBar.index.038495-60')"
                  v-if="configObject.componentShow && configObject.componentShow.indexOf('滤镜渲染') !== -1"
                >
                  <el-radio-group v-model="configObject.style.isFilter" @change="handleStyleFilterChange">
                    <el-radio :label="true">{{ $t('topo.components.propertyBar.index.038495-62') }}</el-radio>
                    <el-radio :label="false">{{ $t('topo.components.propertyBar.index.038495-63') }}</el-radio>
                  </el-radio-group>
                </el-form-item>
                <el-form-item
                  :label="$t('topo.components.propertyBar.index.038495-64')"
                  v-if="configObject.componentShow && configObject.componentShow.indexOf('组件颜色') !== -1"
                >
                  <div class="color-picker-wrap">
                    <el-color-picker
                      v-model="configObject.style.foreColor"
                      show-alpha
                      :predefine="predefineColors"
                      color-format="rgb"
                    />
                  </div>
                </el-form-item>
                <el-form-item
                  :label="$t('topo.components.propertyBar.index.038495-406')"
                  v-if="configObject.componentShow && configObject.componentShow.indexOf('形状类型') !== -1"
                >
                  <el-select
                    v-model="configObject.style.shapeType"
                    :placeholder="$t('topo.components.propertyBar.index.038495-407')"
                    style="width: 100%"
                  >
                    <el-option :label="$t('topo.components.propertyBar.index.038495-408')" value="fill">
                      <span style="font-size: 12px">{{ $t('topo.components.propertyBar.index.038495-408') }}</span>
                    </el-option>
                    <el-option :label="$t('topo.components.propertyBar.index.038495-409')" value="stroke">
                      <span style="font-size: 12px">{{ $t('topo.components.propertyBar.index.038495-409') }}</span>
                    </el-option>
                  </el-select>
                </el-form-item>
                <!-- 添加边框宽度设置 -->
                <el-form-item
                  :label="$t('topo.components.propertyBar.index.038495-165')"
                  v-if="configObject.componentShow && configObject.componentShow.indexOf('形状类型') !== -1"
                >
                  <el-input-number
                    style="width: 100%"
                    controls-position="right"
                    placeholder="2"
                    v-model="configObject.style.borderWidth"
                    :min="1"
                    :max="50"
                  />
                </el-form-item>
                <!-- 添加边框颜色设置 -->
                <el-form-item
                  :label="$t('topo.components.propertyBar.index.038495-167')"
                  v-if="configObject.componentShow && configObject.componentShow.indexOf('形状类型') !== -1"
                >
                  <div class="color-picker-wrap">
                    <el-color-picker
                      v-model="configObject.style.borderColor"
                      show-alpha
                      :predefine="predefineColors"
                      color-format="hex"
                    />
                  </div>
                </el-form-item>
                <el-form-item
                  :label="$t('topo.components.propertyBar.index.038495-312')"
                  v-if="configObject.style.dataType !== undefined"
                >
                  <el-select
                    v-model="configObject.style.dataType"
                    :placeholder="$t('topo.components.propertyBar.index.038495-313')"
                    style="width: 100%"
                  >
                    <el-option :label="item.text" :value="item.value" v-for="item in dataTypeOptions" :key="item.id">
                      <span style="font-size: 12px">{{ item.text }}</span>
                    </el-option>
                  </el-select>
                </el-form-item>
                <el-form-item
                  :label="$t('topo.components.propertyBar.index.038495-314')"
                  v-if="configObject.style.decimalDigits !== undefined && configObject.style.dataType === 10"
                >
                  <el-input-number
                    style="width: 100%"
                    v-model="configObject.style.decimalDigits"
                    :placeholder="$t('topo.components.propertyBar.index.038495-315')"
                    controls-position="right"
                    :min="0"
                  />
                </el-form-item>
                <el-form-item :label="$t('topo.components.propertyBar.index.038495-65')">
                  <el-select
                    v-model="configObject.style.visible"
                    :placeholder="$t('topo.components.propertyBar.index.038495-66')"
                    style="width: 100%"
                  >
                    <el-option :label="$t('topo.components.propertyBar.index.038495-67')" :value="true">
                      <span style="font-size: 12px">{{ $t('topo.components.propertyBar.index.038495-67') }}</span>
                    </el-option>
                    <el-option :label="$t('topo.components.propertyBar.index.038495-68')" :value="false">
                      <span style="font-size: 12px">{{ $t('topo.components.propertyBar.index.038495-68') }}</span>
                    </el-option>
                  </el-select>
                </el-form-item>
                <el-form-item
                  :label="$t('topo.components.propertyBar.index.038495-69')"
                  v-if="configObject.componentShow && configObject.componentShow.indexOf('自定义echarts') !== -1"
                >
                  <el-button style="width: 100%" class="gen-btn" @click="handleEchartClick">{{ $t('edit') }}</el-button>
                </el-form-item>
                <el-form-item
                  :label="$t('topo.components.propertyBar.index.038495-71')"
                  v-if="configObject.componentShow && configObject.componentShow.indexOf('地图文件') !== -1"
                >
                  <el-select
                    v-model="configObject.dataBind.mapAddress"
                    :placeholder="$t('topo.components.propertyBar.index.038495-73')"
                    filterable
                    style="width: 100%"
                  >
                    <el-option :label="$t('topo.components.propertyBar.index.038495-74')" value="安徽">
                      <span style="font-size: 12px">{{ $t('topo.components.propertyBar.index.038495-74') }}</span>
                    </el-option>
                    <el-option :label="$t('topo.components.propertyBar.index.038495-75')" value="澳门">
                      <span style="font-size: 12px">{{ $t('topo.components.propertyBar.index.038495-75') }}</span>
                    </el-option>
                    <el-option :label="$t('topo.components.propertyBar.index.038495-76')" value="北京">
                      <span style="font-size: 12px">{{ $t('topo.components.propertyBar.index.038495-76') }}</span>
                    </el-option>
                    <el-option :label="$t('topo.components.propertyBar.index.038495-77')" value="重庆">
                      <span style="font-size: 12px">{{ $t('topo.components.propertyBar.index.038495-77') }}</span>
                    </el-option>
                    <el-option :label="$t('topo.components.propertyBar.index.038495-78')" value="福建">
                      <span style="font-size: 12px">{{ $t('topo.components.propertyBar.index.038495-78') }}</span>
                    </el-option>
                    <el-option :label="$t('topo.components.propertyBar.index.038495-79')" value="甘肃">
                      <span style="font-size: 12px">{{ $t('topo.components.propertyBar.index.038495-79') }}</span>
                    </el-option>
                    <el-option :label="$t('topo.components.propertyBar.index.038495-80')" value="广东">
                      <span style="font-size: 12px">{{ $t('topo.components.propertyBar.index.038495-80') }}</span>
                    </el-option>
                    <el-option :label="$t('topo.components.propertyBar.index.038495-81')" value="广西">
                      <span style="font-size: 12px">{{ $t('topo.components.propertyBar.index.038495-81') }}</span>
                    </el-option>
                    <el-option :label="$t('topo.components.propertyBar.index.038495-82')" value="贵州">
                      <span style="font-size: 12px">{{ $t('topo.components.propertyBar.index.038495-82') }}</span>
                    </el-option>
                    <el-option :label="$t('topo.components.propertyBar.index.038495-83')" value="海南">
                      <span style="font-size: 12px">{{ $t('topo.components.propertyBar.index.038495-83') }}</span>
                    </el-option>
                    <el-option :label="$t('topo.components.propertyBar.index.038495-84')" value="河北">
                      <span style="font-size: 12px">{{ $t('topo.components.propertyBar.index.038495-84') }}</span>
                    </el-option>
                    <el-option :label="$t('topo.components.propertyBar.index.038495-85')" value="黑龙江">
                      <span style="font-size: 12px">{{ $t('topo.components.propertyBar.index.038495-85') }}</span>
                    </el-option>
                    <el-option :label="$t('topo.components.propertyBar.index.038495-86')" value="河南">
                      <span style="font-size: 12px">{{ $t('topo.components.propertyBar.index.038495-86') }}</span>
                    </el-option>
                    <el-option :label="$t('topo.components.propertyBar.index.038495-87')" value="湖北">
                      <span style="font-size: 12px">{{ $t('topo.components.propertyBar.index.038495-87') }}</span>
                    </el-option>
                    <el-option :label="$t('topo.components.propertyBar.index.038495-88')" value="湖南">
                      <span style="font-size: 12px">{{ $t('topo.components.propertyBar.index.038495-88') }}</span>
                    </el-option>
                    <el-option :label="$t('topo.components.propertyBar.index.038495-89')" value="江苏">
                      <span style="font-size: 12px">{{ $t('topo.components.propertyBar.index.038495-89') }}</span>
                    </el-option>
                    <el-option :label="$t('topo.components.propertyBar.index.038495-90')" value="江西">
                      <span style="font-size: 12px">{{ $t('topo.components.propertyBar.index.038495-90') }}</span>
                    </el-option>
                    <el-option :label="$t('topo.components.propertyBar.index.038495-91')" value="吉林">
                      <span style="font-size: 12px">{{ $t('topo.components.propertyBar.index.038495-91') }}</span>
                    </el-option>
                    <el-option :label="$t('topo.components.propertyBar.index.038495-92')" value="辽宁">
                      <span style="font-size: 12px">{{ $t('topo.components.propertyBar.index.038495-92') }}</span>
                    </el-option>
                    <el-option :label="$t('topo.components.propertyBar.index.038495-93')" value="内蒙古">
                      <span style="font-size: 12px">{{ $t('topo.components.propertyBar.index.038495-93') }}</span>
                    </el-option>
                    <el-option :label="$t('topo.components.propertyBar.index.038495-94')" value="宁夏">
                      <span style="font-size: 12px">{{ $t('topo.components.propertyBar.index.038495-94') }}</span>
                    </el-option>
                    <el-option :label="$t('topo.components.propertyBar.index.038495-95')" value="青海">
                      <span style="font-size: 12px">{{ $t('topo.components.propertyBar.index.038495-95') }}</span>
                    </el-option>
                    <el-option :label="$t('topo.components.propertyBar.index.038495-96')" value="山东">
                      <span style="font-size: 12px">{{ $t('topo.components.propertyBar.index.038495-96') }}</span>
                    </el-option>
                    <el-option :label="$t('topo.components.propertyBar.index.038495-97')" value="上海">
                      <span style="font-size: 12px">{{ $t('topo.components.propertyBar.index.038495-97') }}</span>
                    </el-option>
                    <el-option :label="$t('topo.components.propertyBar.index.038495-98')" value="山西">
                      <span style="font-size: 12px">{{ $t('topo.components.propertyBar.index.038495-98') }}</span>
                    </el-option>
                    <el-option :label="$t('topo.components.propertyBar.index.038495-99')" value="四川">
                      <span style="font-size: 12px">{{ $t('topo.components.propertyBar.index.038495-99') }}</span>
                    </el-option>
                    <el-option :label="$t('topo.components.propertyBar.index.038495-100')" value="台湾">
                      <span style="font-size: 12px">{{ $t('topo.components.propertyBar.index.038495-100') }}</span>
                    </el-option>
                    <el-option :label="$t('topo.components.propertyBar.index.038495-101')" value="天津">
                      <span style="font-size: 12px">{{ $t('topo.components.propertyBar.index.038495-101') }}</span>
                    </el-option>
                    <el-option :label="$t('topo.components.propertyBar.index.038495-102')" value="香港">
                      <span style="font-size: 12px">{{ $t('topo.components.propertyBar.index.038495-102') }}</span>
                    </el-option>
                    <el-option :label="$t('topo.components.propertyBar.index.038495-103')" value="新疆">
                      <span style="font-size: 12px">{{ $t('topo.components.propertyBar.index.038495-103') }}</span>
                    </el-option>
                    <el-option :label="$t('topo.components.propertyBar.index.038495-104')" value="西藏">
                      <span style="font-size: 12px">{{ $t('topo.components.propertyBar.index.038495-104') }}</span>
                    </el-option>
                    <el-option :label="$t('topo.components.propertyBar.index.038495-105')" value="云南">
                      <span style="font-size: 12px">{{ $t('topo.components.propertyBar.index.038495-105') }}</span>
                    </el-option>
                    <el-option :label="$t('topo.components.propertyBar.index.038495-106')" value="浙江">
                      <span style="font-size: 12px">{{ $t('topo.components.propertyBar.index.038495-106') }}</span>
                    </el-option>
                    <el-option :label="$t('topo.components.propertyBar.index.038495-107')" value="自定义">
                      <span style="font-size: 12px">{{ $t('topo.components.propertyBar.index.038495-107') }}</span>
                    </el-option>
                  </el-select>
                </el-form-item>
                <el-form-item
                  :label="$t('topo.components.propertyBar.index.038495-108')"
                  v-if="configObject.dataBind.mapAddress == '自定义'"
                >
                  <el-input
                    :placeholder="$t('topo.components.propertyBar.index.038495-109')"
                    v-model="configObject.dataBind.mapUrl"
                  />
                </el-form-item>
                <el-form-item
                  :label="$t('topo.components.propertyBar.index.038495-111')"
                  v-if="configObject.componentShow && configObject.componentShow.indexOf('悬浮提示') !== -1"
                >
                  <el-radio-group v-model="configObject.style.tipDIs">
                    <el-radio :label="true">{{ $t('topo.components.propertyBar.index.038495-258') }}</el-radio>
                    <el-radio :label="false">{{ $t('close') }}</el-radio>
                  </el-radio-group>
                </el-form-item>
                <el-form-item
                  :label="$t('topo.components.propertyBar.index.038495-112')"
                  v-if="configObject.componentShow && configObject.componentShow.indexOf('悬浮提示') !== -1"
                >
                  <el-radio-group v-model="configObject.style.tipShow">
                    <el-radio :label="true">{{ $t('topo.components.propertyBar.index.038495-113') }}</el-radio>
                    <el-radio :label="false">{{ $t('topo.components.propertyBar.index.038495-114') }}</el-radio>
                  </el-radio-group>
                </el-form-item>
                <el-form-item
                  :label="$t('topo.components.propertyBar.index.038495-115')"
                  v-if="configObject.componentShow && configObject.componentShow.indexOf('悬浮提示') !== -1"
                >
                  <el-select
                    v-model="configObject.style.placement"
                    :placeholder="$t('topo.components.propertyBar.index.038495-116')"
                    style="width: 100%"
                  >
                    <el-option :label="$t('topo.components.propertyBar.index.038495-117')" value="top">
                      <span style="font-size: 12px">{{ $t('topo.components.propertyBar.index.038495-117') }}</span>
                    </el-option>
                    <el-option :label="$t('topo.components.propertyBar.index.038495-118')" value="top-start">
                      <span style="font-size: 12px">{{ $t('topo.components.propertyBar.index.038495-118') }}</span>
                    </el-option>
                    <el-option :label="$t('topo.components.propertyBar.index.038495-119')" value="top-end">
                      <span style="font-size: 12px">{{ $t('topo.components.propertyBar.index.038495-119') }}</span>
                    </el-option>
                    <el-option :label="$t('topo.components.propertyBar.index.038495-120')" value="bottom">
                      <span style="font-size: 12px">{{ $t('topo.components.propertyBar.index.038495-120') }}</span>
                    </el-option>
                    <el-option :label="$t('topo.components.propertyBar.index.038495-121')" value="bottom-start">
                      <span style="font-size: 12px">{{ $t('topo.components.propertyBar.index.038495-121') }}</span>
                    </el-option>
                    <el-option :label="$t('topo.components.propertyBar.index.038495-122')" value="bottom-end">
                      <span style="font-size: 12px">{{ $t('topo.components.propertyBar.index.038495-122') }}</span>
                    </el-option>
                    <el-option :label="$t('topo.components.propertyBar.index.038495-123')" value="left">
                      <span style="font-size: 12px">{{ $t('topo.components.propertyBar.index.038495-123') }}</span>
                    </el-option>
                    <el-option :label="$t('topo.components.propertyBar.index.038495-124')" value="left-start">
                      <span style="font-size: 12px">{{ $t('topo.components.propertyBar.index.038495-124') }}</span>
                    </el-option>
                    <el-option :label="$t('topo.components.propertyBar.index.038495-125')" value="left-end">
                      <span style="font-size: 12px">{{ $t('topo.components.propertyBar.index.038495-125') }}</span>
                    </el-option>
                    <el-option :label="$t('topo.components.propertyBar.index.038495-126')" value="right">
                      <span style="font-size: 12px">{{ $t('topo.components.propertyBar.index.038495-126') }}</span>
                    </el-option>
                    <el-option :label="$t('topo.components.propertyBar.index.038495-127')" value="right-start">
                      <span style="font-size: 12px">{{ $t('topo.components.propertyBar.index.038495-127') }}</span>
                    </el-option>
                    <el-option :label="$t('topo.components.propertyBar.index.038495-128')" value="right-end">
                      <span style="font-size: 12px">{{ $t('topo.components.propertyBar.index.038495-128') }}</span>
                    </el-option>
                  </el-select>
                </el-form-item>
                <el-form-item
                  :label="$t('topo.components.propertyBar.index.038495-419')"
                  v-if="configObject.componentShow && configObject.componentShow.indexOf('悬浮提示') !== -1"
                >
                  <el-select
                    v-model="configObject.style.effect"
                    :placeholder="$t('topo.components.propertyBar.index.038495-420')"
                    style="width: 100%"
                  >
                    <el-option :label="$t('topo.components.propertyBar.index.038495-129')" value="dark">
                      <span style="font-size: 12px">{{ $t('topo.components.propertyBar.index.038495-129') }}</span>
                    </el-option>
                    <el-option :label="$t('topo.components.propertyBar.index.038495-130')" value="light">
                      <span style="font-size: 12px">{{ $t('topo.components.propertyBar.index.038495-130') }}</span>
                    </el-option>
                  </el-select>
                </el-form-item>
                <el-form-item
                  :label="$t('topo.components.propertyBar.index.038495-131')"
                  v-if="configObject.componentShow && configObject.componentShow.indexOf('数统计') !== -1"
                >
                  <el-select
                    v-model="configObject.dataBind.staticType"
                    :placeholder="$t('topo.components.propertyBar.index.038495-132')"
                    style="width: 100%"
                  >
                    <el-option v-for="item in paramNameList" :key="item" :label="item" :value="item">
                      <span style="font-size: 12px">{{ item }}</span>
                    </el-option>
                  </el-select>
                </el-form-item>
                <el-form-item
                  :label="$t('topo.components.propertyBar.index.038495-133')"
                  v-if="configObject.style.lineWidth"
                >
                  <el-input-number
                    style="width: 100%"
                    controls-position="right"
                    :placeholder="$t('topo.components.propertyBar.index.038495-134')"
                    v-model="configObject.style.lineWidth"
                  />
                </el-form-item>
                <!-- 新版水流样式设置开始 -->
                <template v-if="configObject.componentShow && configObject.componentShow.indexOf('水流') !== -1">
                  <el-form-item :label="$t('topo.components.propertyBar.index.038495-135')">
                    <el-input-number
                      style="width: 100%"
                      v-model="configObject.style.lineHeight"
                      :placeholder="$t('topo.components.propertyBar.index.038495-136')"
                      controls-position="right"
                    />
                  </el-form-item>
                  <el-form-item :label="$t('topo.components.propertyBar.index.038495-137')">
                    <el-input-number
                      style="width: 100%"
                      v-model="configObject.style.lineInterval"
                      :placeholder="$t('topo.components.propertyBar.index.038495-138')"
                      controls-position="right"
                    />
                  </el-form-item>
                  <el-form-item :label="$t('topo.components.propertyBar.index.038495-139')">
                    <el-select
                      v-model="configObject.style.lineType"
                      :placeholder="$t('topo.components.propertyBar.index.038495-140')"
                      style="width: 100%"
                    >
                      <el-option :label="$t('topo.components.propertyBar.index.038495-141')" value="butt">
                        <span style="font-size: 12px">{{ $t('topo.components.propertyBar.index.038495-141') }}</span>
                      </el-option>
                      <el-option :label="$t('topo.components.propertyBar.index.038495-142')" value="round">
                        <span style="font-size: 12px">{{ $t('topo.components.propertyBar.index.038495-142') }}</span>
                      </el-option>
                    </el-select>
                  </el-form-item>
                  <el-form-item :label="$t('topo.components.propertyBar.index.038495-143')">
                    <el-select
                      v-model="configObject.style.animations"
                      :placeholder="$t('topo.components.propertyBar.index.038495-144')"
                      style="width: 100%"
                    >
                      <el-option :label="$t('topo.components.propertyBar.index.038495-145')" value="正向">
                        <span style="font-size: 12px">{{ $t('topo.components.propertyBar.index.038495-145') }}</span>
                      </el-option>
                      <el-option :label="$t('topo.components.propertyBar.index.038495-146')" value="反向">
                        <span style="font-size: 12px">{{ $t('topo.components.propertyBar.index.038495-146') }}</span>
                      </el-option>
                      <el-option :label="$t('topo.components.propertyBar.index.038495-147')" value="静止">
                        <span style="font-size: 12px">{{ $t('topo.components.propertyBar.index.038495-147') }}</span>
                      </el-option>
                    </el-select>
                  </el-form-item>
                  <el-form-item :label="$t('topo.components.propertyBar.index.038495-148')">
                    <el-select
                      v-model="configObject.style.speed"
                      :placeholder="$t('topo.components.propertyBar.index.038495-148')"
                      style="width: 100%"
                    >
                      <el-option :label="$t('topo.components.propertyBar.index.038495-149')" value="快">
                        <span style="font-size: 12px">{{ $t('topo.components.propertyBar.index.038495-149') }}</span>
                      </el-option>
                      <el-option :label="$t('topo.components.propertyBar.index.038495-150')" value="中">
                        <span style="font-size: 12px">{{ $t('topo.components.propertyBar.index.038495-150') }}</span>
                      </el-option>
                      <el-option :label="$t('topo.components.propertyBar.index.038495-151')" value="慢">
                        <span style="font-size: 12px">{{ $t('topo.components.propertyBar.index.038495-151') }}</span>
                      </el-option>
                    </el-select>
                  </el-form-item>
                  <el-form-item :label="$t('topo.components.propertyBar.index.038495-152')">
                    <el-input-number
                      style="width: 100%"
                      v-model="configObject.style.anchorPointNum"
                      :placeholder="$t('topo.components.propertyBar.index.038495-153')"
                      controls-position="right"
                    />
                  </el-form-item>
                </template>
                <!-- 水流样式设置结束 -->
                <!-- 水球图样式 -->
                <el-form-item
                  :label="$t('topo.components.propertyBar.index.038495-154')"
                  v-if="configObject.style.waterShape"
                >
                  <el-select
                    v-model="configObject.style.waterShape"
                    :placeholder="$t('topo.components.propertyBar.index.038495-155')"
                    style="width: 100%"
                  >
                    <el-option :label="$t('topo.components.propertyBar.index.038495-156')" value="container">
                      <span style="font-size: 12px">{{ $t('topo.components.propertyBar.index.038495-156') }}</span>
                    </el-option>
                    <el-option :label="$t('topo.components.propertyBar.index.038495-157')" value="circle">
                      <span style="font-size: 12px">{{ $t('topo.components.propertyBar.index.038495-157') }}</span>
                    </el-option>
                    <el-option :label="$t('topo.components.propertyBar.index.038495-158')" value="rect">
                      <span style="font-size: 12px">{{ $t('topo.components.propertyBar.index.038495-158') }}</span>
                    </el-option>
                    <el-option :label="$t('topo.components.propertyBar.index.038495-159')" value="roundRect">
                      <span style="font-size: 12px">{{ $t('topo.components.propertyBar.index.038495-159') }}</span>
                    </el-option>
                    <el-option :label="$t('topo.components.propertyBar.index.038495-160')" value="triangle">
                      <span style="font-size: 12px">{{ $t('topo.components.propertyBar.index.038495-160') }}</span>
                    </el-option>
                    <el-option :label="$t('topo.components.propertyBar.index.038495-161')" value="diamond">
                      <span style="font-size: 12px">{{ $t('topo.components.propertyBar.index.038495-161') }}</span>
                    </el-option>
                    <el-option :label="$t('topo.components.propertyBar.index.038495-162')" value="pin">
                      <span style="font-size: 12px">{{ $t('topo.components.propertyBar.index.038495-162') }}</span>
                    </el-option>
                    <el-option :label="$t('topo.components.propertyBar.index.038495-163')" value="arrow">
                      <span style="font-size: 12px">{{ $t('topo.components.propertyBar.index.038495-163') }}</span>
                    </el-option>
                    <el-option :label="$t('topo.components.propertyBar.index.038495-334')" value="convex">
                      <span style="font-size: 12px">{{ $t('topo.components.propertyBar.index.038495-334') }}</span>
                    </el-option>
                  </el-select>
                </el-form-item>
                <el-form-item
                  :label="$t('topo.components.propertyBar.index.038495-164')"
                  v-if="configObject.style.waterShape"
                >
                  <div class="color-picker-wrap">
                    <el-color-picker v-model="configObject.style.waterColor" show-alpha :predefine="predefineColors" />
                  </div>
                </el-form-item>
                <el-form-item
                  :label="$t('topo.components.propertyBar.index.038495-335')"
                  v-if="configObject.style.waterLabel !== undefined"
                >
                  <el-select
                    v-model="configObject.style.waterLabel"
                    :placeholder="$t('topo.components.propertyBar.index.038495-336')"
                    style="width: 100%"
                  >
                    <el-option :label="$t('topo.components.propertyBar.index.038495-67')" :value="true">
                      <span style="font-size: 12px">{{ $t('topo.components.propertyBar.index.038495-67') }}</span>
                    </el-option>
                    <el-option :label="$t('topo.components.propertyBar.index.038495-68')" :value="false">
                      <span style="font-size: 12px">{{ $t('topo.components.propertyBar.index.038495-68') }}</span>
                    </el-option>
                  </el-select>
                </el-form-item>
                <el-form-item
                  :label="$t('topo.components.propertyBar.index.038495-58')"
                  v-if="configObject.style.waterFontSize !== undefined"
                >
                  <el-input-number
                    style="width: 100%"
                    v-model="configObject.style.waterFontSize"
                    :placeholder="$t('topo.components.propertyBar.index.038495-59')"
                    controls-position="right"
                  />
                </el-form-item>
                <el-form-item
                  :label="$t('topo.components.propertyBar.index.038495-165')"
                  v-if="configObject.style.waterBorderWidth !== undefined"
                >
                  <el-input-number
                    style="width: 100%"
                    v-model="configObject.style.waterBorderWidth"
                    :placeholder="$t('topo.components.propertyBar.index.038495-166')"
                    controls-position="right"
                  />
                </el-form-item>
                <el-form-item
                  :label="$t('topo.components.propertyBar.index.038495-167')"
                  v-if="configObject.style.waterBorderColor !== undefined"
                >
                  <div class="color-picker-wrap">
                    <el-color-picker
                      v-model="configObject.style.waterBorderColor"
                      show-alpha
                      :predefine="predefineColors"
                      color-format="hex"
                    />
                  </div>
                </el-form-item>
                <el-form-item
                  :label="$t('topo.components.propertyBar.index.038495-168')"
                  v-if="configObject.style.titleFontSize !== undefined"
                >
                  <el-input-number
                    style="width: 100%"
                    v-model="configObject.style.titleFontSize"
                    :placeholder="$t('topo.components.propertyBar.index.038495-329')"
                    controls-position="right"
                  />
                </el-form-item>
                <el-form-item
                  :label="$t('topo.components.propertyBar.index.038495-330')"
                  v-if="configObject.style.titleVisible !== undefined"
                >
                  <el-select
                    v-model="configObject.style.titleVisible"
                    :placeholder="$t('topo.components.propertyBar.index.038495-331')"
                    style="width: 100%"
                  >
                    <el-option :label="$t('topo.components.propertyBar.index.038495-332')" :value="true">
                      <span style="font-size: 12px">{{ $t('topo.components.propertyBar.index.038495-332') }}</span>
                    </el-option>
                    <el-option :label="$t('topo.components.propertyBar.index.038495-333')" :value="false">
                      <span style="font-size: 12px">{{ $t('topo.components.propertyBar.index.038495-333') }}</span>
                    </el-option>
                  </el-select>
                </el-form-item>
                <el-form-item
                  :label="$t('topo.components.propertyBar.index.038495-170')"
                  v-if="configObject.style.rowNum !== undefined && configObject.componentShow.indexOf('轮播表') === -1"
                >
                  <el-input-number
                    style="width: 100%"
                    v-model="configObject.style.rowNum"
                    :placeholder="$t('topo.components.propertyBar.index.038495-171')"
                    controls-position="right"
                  />
                </el-form-item>
                <el-form-item
                  :label="$t('topo.components.propertyBar.index.038495-379')"
                  v-if="configObject.style.columnNum !== undefined"
                >
                  <el-input-number
                    style="width: 100%"
                    v-model="configObject.style.columnNum"
                    :placeholder="$t('topo.components.propertyBar.index.038495-380')"
                    controls-position="right"
                  />
                </el-form-item>
              </el-form>
            </el-collapse-item>
            <!-- 滚动表格开始 -->
            <el-collapse-item
              :title="$t('topo.components.propertyBar.index.038495-169')"
              name="3"
              v-if="configObject.componentShow && configObject.componentShow.indexOf('轮播表') !== -1"
            >
              <el-form label-width="78px">
                <el-form-item :label="$t('topo.components.propertyBar.index.038495-170')">
                  <el-input-number
                    style="width: 100%"
                    v-model="configObject.style.rowNum"
                    :placeholder="$t('topo.components.propertyBar.index.038495-171')"
                    controls-position="right"
                  />
                </el-form-item>
                <el-form-item :label="$t('topo.components.propertyBar.index.038495-172')">
                  <div class="color-picker-wrap">
                    <el-color-picker
                      v-model="configObject.style.foreColor"
                      show-alpha
                      :predefine="predefineColors"
                      color-format="hex"
                    />
                  </div>
                </el-form-item>
                <el-form-item :label="$t('topo.components.propertyBar.index.038495-173')">
                  <div class="color-picker-wrap">
                    <el-color-picker
                      v-model="configObject.style.headerBGC"
                      show-alpha
                      :predefine="predefineColors"
                      color-format="hex"
                    />
                  </div>
                </el-form-item>
                <el-form-item :label="$t('topo.components.propertyBar.index.038495-174')">
                  <div class="color-picker-wrap">
                    <el-color-picker
                      v-model="configObject.style.oddRowBGC"
                      show-alpha
                      :predefine="predefineColors"
                      color-format="hex"
                    />
                  </div>
                </el-form-item>
                <el-form-item :label="$t('topo.components.propertyBar.index.038495-175')">
                  <div class="color-picker-wrap">
                    <el-color-picker
                      v-model="configObject.style.evenRowBGC"
                      show-alpha
                      :predefine="predefineColors"
                      color-format="hex"
                    />
                  </div>
                </el-form-item>
                <el-form-item :label="$t('topo.components.propertyBar.index.038495-176')">
                  <el-input-number
                    style="width: 100%"
                    v-model="configObject.style.waitTime"
                    :placeholder="$t('topo.components.propertyBar.index.038495-177')"
                    controls-position="right"
                    :min="1000"
                  />
                </el-form-item>
                <el-form-item :label="$t('topo.components.propertyBar.index.038495-178')">
                  <el-input-number
                    style="width: 100%"
                    v-model="configObject.style.headerHeight"
                    :placeholder="$t('topo.components.propertyBar.index.038495-179')"
                    controls-position="right"
                  />
                </el-form-item>
                <el-form-item :label="$t('topo.components.propertyBar.index.038495-180')">
                  <el-input-number
                    style="width: 100%"
                    v-model="configObject.style.columnWidth"
                    :placeholder="$t('topo.components.propertyBar.index.038495-181')"
                    controls-position="right"
                  />
                </el-form-item>
                <el-form-item :label="$t('topo.components.propertyBar.index.038495-182')">
                  <el-radio-group v-model="configObject.style.index">
                    <el-radio :label="true">{{ $t('topo.components.propertyBar.index.038495-183') }}</el-radio>
                    <el-radio :label="false">{{ $t('topo.components.propertyBar.index.038495-184') }}</el-radio>
                  </el-radio-group>
                </el-form-item>
                <el-form-item :label="$t('topo.components.propertyBar.index.038495-185')">
                  <el-select
                    v-model="configObject.style.carousel"
                    :placeholder="$t('topo.components.propertyBar.index.038495-186')"
                    style="width: 100%"
                  >
                    <el-option :label="$t('topo.components.propertyBar.index.038495-187')" value="single">
                      <span style="font-size: 12px">{{ $t('topo.components.propertyBar.index.038495-187') }}</span>
                    </el-option>
                    <el-option :label="$t('topo.components.propertyBar.index.038495-188')" value="page">
                      <span style="font-size: 12px">{{ $t('topo.components.propertyBar.index.038495-188') }}</span>
                    </el-option>
                  </el-select>
                </el-form-item>
              </el-form>
            </el-collapse-item>
            <!-- 滚动表格结束 -->
          </el-collapse>
        </div>
        <!-- 数据绑定 -->
        <div class="pane-item" v-show="tabIndex === 1">
          <el-collapse class="collapse-wrap" v-model="dataCollapseActive">
            <!-- 参数绑定开始 -->
            <el-collapse-item
              :title="$t('topo.components.propertyBar.index.038495-189')"
              name="1"
              v-if="configObject.componentShow && configObject.componentShow.indexOf('参数绑定') !== -1"
            >
              <el-form label-width="75px">
                <el-form-item
                  :label="$t('topo.components.propertyBar.index.038495-190')"
                  v-if="configObject.componentShow && configObject.componentShow.indexOf('设备状态') !== -1"
                >
                  <el-select
                    v-model="configObject.dataBind.statusType"
                    :placeholder="$t('topo.components.propertyBar.index.038495-191')"
                    style="width: 100%"
                  >
                    <el-option :label="$t('topo.components.propertyBar.index.038495-192')" :value="1">
                      <span style="font-size: 12px">{{ $t('topo.components.propertyBar.index.038495-192') }}</span>
                    </el-option>
                    <el-option :label="$t('topo.components.propertyBar.index.038495-193')" :value="2">
                      <span style="font-size: 12px">{{ $t('topo.components.propertyBar.index.038495-193') }}</span>
                    </el-option>
                  </el-select>
                </el-form-item>

                <el-form-item
                  :label="$t('topo.components.propertyBar.index.038495-193')"
                  v-if="configObject.dataBind.statusType === 2 && scadaType !== 1"
                >
                  <el-select
                    v-model="configObject.dataBind.serialNumber"
                    :placeholder="$t('topo.components.propertyBar.index.038495-194')"
                    style="width: 100%"
                    filterable
                  >
                    <el-option
                      :label="item.deviceName"
                      :value="item.serialNumber"
                      v-for="item in deviceBindList"
                      :key="item.serialNumber"
                    >
                      <span style="font-size: 12px">{{ item.deviceName }}</span>
                    </el-option>
                  </el-select>
                </el-form-item>
                <el-form-item :label="$t('topo.components.propertyBar.index.038495-192')" v-if="showVariateItem">
                  <div class="variate-item">
                    <div class="left-wrap">
                      <span class="text">
                        {{
                          getVariateStatus()
                            ? $t('topo.components.propertyBar.index.038495-383')
                            : $t('topo.components.propertyBar.index.038495-382')
                        }}
                      </span>
                    </div>
                    <div class="right-wrap">
                      <el-icon class="icon" @click="handleVariateDrawerBind" style="color: #486ff2">
                        <Link />
                      </el-icon>
                      <el-icon
                        v-if="getVariateStatus()"
                        class="icon"
                        style="color: #8b8c90"
                        @click="handleVariateDrawerDelete"
                      >
                        <Delete />
                      </el-icon>
                    </div>
                  </div>
                </el-form-item>

                <el-form-item
                  :label="$t('topo.components.propertyBar.index.038495-316')"
                  v-if="configObject.dataBind.identifiers !== undefined"
                >
                  <el-button style="width: 100%" class="gen-btn" @click="handleVariateDialogBind('bind')">
                    {{ $t('topo.components.propertyBar.index.038495-381') }}
                  </el-button>
                </el-form-item>

                <!-- 多变量组件：参数绑定数据映射 -->
                <el-form-item
                  :label="$t('scada.topo.components.propertyBar.variateDrawer.842076-23')"
                  v-if="showMultiBindDataProcess"
                >
                  <el-button class="gen-btn" type="primary" @click="openMultiDataMappingDialog">
                    {{ $t('scada.topo.components.propertyBar.variateDrawer.842076-24') }}
                  </el-button>
                  <div
                    style="margin-left: -40px"
                    v-if="
                      configObject.dataBind.modelMaps &&
                      configObject.dataBind.modelMaps.length > 0 &&
                      configObject.dataBind.modelMaps[0].comparisonValue !== ''
                    "
                  >
                    <div
                      v-for="(item, index) in configObject.dataBind.modelMaps"
                      :key="index"
                      style="display: flex; align-items: center; gap: 10px"
                    >
                      <div class="mapping-row">
                        <div>{{ getVarName(item.identifier) }}</div>
                        <div>{{ item.operator }}</div>
                        <div class="show-item">{{ item.comparisonValue }}</div>
                        <div>→</div>
                        <div class="show-item">{{ item.actionValue }}</div>
                      </div>
                      <el-button type="text" :icon="Delete" @click="handleDeleteMultiMapping(index)"></el-button>
                    </div>
                  </div>
                </el-form-item>
              </el-form>
            </el-collapse-item>
            <!-- 参数绑定结束 -->
            <!-- 变量计算生成条件开始 -->
            <el-collapse-item
              :title="$t('topo.components.propertyBar.index.038495-386')"
              name="2"
              v-if="configObject && configObject.componentShow.indexOf('生成条件') !== -1"
            >
              <el-form label-width="75px">
                <el-form-item
                  :label="$t('topo.components.propertyBar.index.038495-316')"
                  v-if="configObject.dataBind.calcVariates !== undefined"
                >
                  <div class="variate-item">
                    <div class="left-wrap">
                      <span class="text">
                        {{
                          configObject.dataBind.calcVariates.length !== 0
                            ? $t('topo.components.propertyBar.index.038495-383')
                            : $t('topo.components.propertyBar.index.038495-382')
                        }}
                      </span>
                    </div>
                    <div class="right-wrap">
                      <el-icon class="icon" @click="handleCalcVariateDialogBind" style="color: #486ff2">
                        <Link />
                      </el-icon>
                      <el-icon
                        v-if="configObject.dataBind.calcVariates.length !== 0"
                        class="icon"
                        style="color: #8b8c90"
                        @click="handleCalcVariateDialogDelete"
                      >
                        <Delete />
                      </el-icon>
                    </div>
                  </div>
                </el-form-item>
              </el-form>
            </el-collapse-item>
            <!-- 变量计算生成条件结束 -->
            <!-- 组态界面开始 -->
            <el-collapse-item
              :title="$t('topo.components.propertyBar.index.038495-196')"
              name="3"
              v-if="configObject && configObject.componentShow.indexOf('组态界面') !== -1"
            >
              <el-form label-width="70px">
                <el-form-item :label="$t('topo.components.propertyBar.index.038495-197')">
                  <el-select
                    v-model="configObject.dataBind.scadaGuid"
                    placement="bottom-start"
                    :popper-options="{
                      modifiers: [
                        {
                          name: 'flip',
                          options: {
                            fallbackPlacements: ['bottom-start'],
                          },
                        },
                      ],
                    }"
                    :placeholder="$t('topo.components.propertyBar.index.038495-198')"
                  >
                    <el-option
                      v-for="item in ztOption"
                      :key="item.id"
                      :label="item.pageName"
                      :value="item.id + '&' + item.guid + '&' + (item.type ?? scadaType)"
                    >
                      <span style="font-size: 12px">{{ item.pageName }}</span>
                    </el-option>
                  </el-select>
                </el-form-item>
              </el-form>
            </el-collapse-item>
            <!-- 组态界面结束 -->
            <!-- 萤石云开始 -->
            <el-collapse-item
              :title="$t('topo.components.propertyBar.index.038495-199')"
              name="4"
              v-if="configObject.componentShow && configObject.componentShow.indexOf('萤石云') !== -1"
            >
              <el-form label-width="62px">
                <el-form-item :label="$t('topo.components.propertyBar.index.038495-200')">
                  <el-input
                    :placeholder="$t('topo.components.propertyBar.index.038495-201')"
                    v-model="configObject.dataBind.serialNumber"
                    clearable
                  />
                </el-form-item>
                <el-form-item :label="$t('topo.components.propertyBar.index.038495-202')">
                  <el-input
                    :placeholder="$t('topo.components.propertyBar.index.038495-202')"
                    v-model="configObject.dataBind.ezvizChannel"
                    clearable
                  />
                </el-form-item>
                <el-form-item label="Token">
                  <el-input
                    :placeholder="$t('topo.components.propertyBar.index.038495-201')"
                    v-model="configObject.dataBind.ezvizToken"
                    clearable
                  />
                </el-form-item>
              </el-form>
            </el-collapse-item>
            <!-- 萤石云结束 -->
            <!-- 直播视频开始 -->
            <el-collapse-item
              :title="$t('topo.components.propertyBar.index.038495-203')"
              name="5"
              v-if="configObject.componentShow && configObject.componentShow.indexOf('直播视频') !== -1"
            >
              <el-form label-width="84px">
                <el-form-item :label="$t('topo.components.propertyBar.index.038495-204')">
                  <el-input
                    :placeholder="$t('topo.components.propertyBar.index.038495-205')"
                    v-model="configObject.dataBind.videoUrl"
                    clearable
                  />
                </el-form-item>
              </el-form>
            </el-collapse-item>
            <!-- 直播视频结束 -->
            <!-- 通用视频开始 -->
            <el-collapse-item
              :title="$t('topo.components.propertyBar.index.038495-206')"
              name="6"
              v-if="configObject.componentShow && configObject.componentShow.indexOf('通用视频') !== -1"
            >
              <el-form label-width="75px">
                <el-form-item :label="$t('topo.components.propertyBar.index.038495-207')">
                  <el-input
                    :placeholder="$t('topo.components.propertyBar.index.038495-208')"
                    v-model="configObject.dataBind.videoUrl"
                    clearable
                  />
                </el-form-item>
                <el-form-item :label="$t('topo.components.propertyBar.index.038495-209')">
                  <el-input
                    :placeholder="$t('topo.components.propertyBar.index.038495-210')"
                    v-model="configObject.dataBind.imageUrl"
                    clearable
                  />
                </el-form-item>
              </el-form>
            </el-collapse-item>
            <!-- 通用视频结束 -->
            <!-- 三维模型开始 -->
            <el-collapse-item
              :title="$t('topo.components.propertyBar.index.038495-211')"
              name="7"
              v-if="configObject.componentShow && configObject.componentShow.indexOf('三维模型') !== -1"
            >
              <el-form label-width="75px" label-position="top">
                <el-form-item :label="$t('topo.components.propertyBar.index.038495-212')">
                  <label class="model-upload-label gen-btn" :class="{ 'is-loading': modelUploading }">
                    <input
                      ref="modelFileInputRef"
                      type="file"
                      class="model-file-input"
                      :accept="model3dAccept"
                      :disabled="modelUploading"
                      @change="handleModelFileChange"
                    />
                    <span>{{
                      modelUploading
                        ? $t('components.FileUpload.index.232435-9')
                        : $t('topo.components.propertyBar.index.038495-213')
                    }}</span>
                  </label>
                </el-form-item>
                <el-form-item :label="$t('topo.components.propertyBar.index.038495-395')">
                  <el-checkbox-group style="margin-left: 14px" v-model="configObject.dataBind.orbitControls">
                    <el-checkbox :value="1">{{ $t('topo.components.propertyBar.index.038495-396') }}</el-checkbox>
                    <el-checkbox :value="2">{{ $t('topo.components.propertyBar.index.038495-397') }}</el-checkbox>
                    <el-checkbox :value="3">{{ $t('topo.components.propertyBar.index.038495-398') }}</el-checkbox>
                    <el-checkbox :value="4">{{ $t('topo.components.propertyBar.index.038495-399') }}</el-checkbox>
                    <el-checkbox :value="5">{{ $t('topo.components.propertyBar.index.038495-400') }}</el-checkbox>
                  </el-checkbox-group>
                </el-form-item>
                <el-form-item :label="$t('topo.components.propertyBar.index.038495-401')" class="camera-form-item">
                  <div class="camera-fields">
                    <div class="form-item-wrap">
                      <span class="name">{{ $t('topo.components.propertyBar.index.038495-3') }}</span>
                      <div class="content">
                        <el-input-number
                          v-model="configObject.dataBind.cameraX"
                          controls-position="right"
                          :min="-99999"
                          :max="99999"
                        />
                      </div>
                    </div>
                    <div class="form-item-wrap">
                      <span class="name">{{ $t('topo.components.propertyBar.index.038495-4') }}</span>
                      <div class="content">
                        <el-input-number
                          v-model="configObject.dataBind.cameraY"
                          controls-position="right"
                          :min="-99999"
                          :max="99999"
                        />
                      </div>
                    </div>
                    <div class="form-item-wrap">
                      <span class="name">{{ $t('topo.components.propertyBar.index.038495-402') }}</span>
                      <div class="content">
                        <el-input-number
                          v-model="configObject.dataBind.cameraZ"
                          controls-position="right"
                          :min="-99999"
                          :max="99999"
                        />
                      </div>
                    </div>
                  </div>
                </el-form-item>
              </el-form>
            </el-collapse-item>
            <!-- 三维模型结束 -->
            <!-- 事件点击开始 -->
            <el-collapse-item
              :title="$t('topo.components.propertyBar.index.038495-220')"
              name="9"
              v-if="configObject.componentShow && configObject.componentShow.indexOf('单击') !== -1"
            >
              <div class="click-event-item">
                <el-checkbox v-model="configObject.dataEvent.djAction">
                  {{ $t('topo.components.propertyBar.index.038495-417') }}
                </el-checkbox>
                <el-button
                  :class="{ btn: configObject.dataEvent.djAction }"
                  type="text"
                  :icon="Edit"
                  :disabled="!configObject.dataEvent.djAction"
                  @click="handleClickEventConfig"
                ></el-button>
              </div>
            </el-collapse-item>
            <!-- 事件点击结束 -->
            <!-- 事件交互开始 -->
            <el-collapse-item
              :title="$t('topo.components.propertyBar.index.038495-220')"
              name="10"
              v-if="configObject.componentShow && configObject.componentShow.indexOf('交互') !== -1"
            >
              <div class="click-event-item">
                <el-checkbox v-model="configObject.dataEvent.jhAction">
                  {{ $t('topo.components.propertyBar.index.038495-418') }}
                </el-checkbox>
                <el-button
                  :class="{ btn: configObject.dataEvent.jhAction }"
                  type="text"
                  :icon="Edit"
                  :disabled="!configObject.dataEvent.jhAction"
                  @click="handleInterEventConfig"
                ></el-button>
              </div>
            </el-collapse-item>
            <!-- 事件交互结束 -->
            <!-- 新版线条流动判断开始 -->
            <el-collapse-item
              :title="$t('topo.components.propertyBar.index.038495-223')"
              name="12"
              v-if="configObject.componentShow && configObject.componentShow.indexOf('水流') !== -1"
            >
              <el-form label-width="75px">
                <el-form-item :label="$t('topo.components.propertyBar.index.038495-224')">
                  <el-input
                    class="input-with-select"
                    type="number"
                    v-model="configObject.dataAction.paramJudgeData"
                    :placeholder="$t('topo.components.propertyBar.index.038495-225')"
                    controls-position="right"
                  >
                    <template #prepend>
                      <el-select v-model="configObject.dataAction.paramJudge" :placeholder="$t('pleaseSelect')">
                        <el-option label=">" value=">">
                          <span style="font-size: 12px">&gt;</span>
                        </el-option>
                        <el-option label=">=" value=">=">
                          <span style="font-size: 12px">&gt;=</span>
                        </el-option>
                        <el-option label="=" value="=">
                          <span style="font-size: 12px">=</span>
                        </el-option>
                        <el-option label="<=" value="<=">
                          <span style="font-size: 12px">&lt;=</span>
                        </el-option>
                        <el-option label="<" value="<">
                          <span style="font-size: 12px">&lt;</span>
                        </el-option>
                        <el-option label="!=" value="!=">
                          <span style="font-size: 12px">!=</span>
                        </el-option>
                      </el-select>
                    </template>
                  </el-input>
                </el-form-item>
                <el-form-item :label="$t('topo.components.propertyBar.index.038495-226')">
                  <el-radio-group v-model="configObject.dataAction.direction">
                    <el-radio label="正向">{{ $t('topo.components.propertyBar.index.038495-227') }}</el-radio>
                    <el-radio label="反向">{{ $t('topo.components.propertyBar.index.038495-228') }}</el-radio>
                  </el-radio-group>
                </el-form-item>
                <el-form-item :label="$t('topo.components.propertyBar.index.038495-224')">
                  <el-input
                    type="number"
                    class="input-with-select"
                    v-model="configObject.dataAction.paramJudgeData01"
                    :placeholder="$t('topo.components.propertyBar.index.038495-225')"
                    controls-position="right"
                  >
                    <template #prepend>
                      <el-select v-model="configObject.dataAction.paramJudge01" :placeholder="$t('pleaseSelect')">
                        <el-option label=">" value=">">
                          <span style="font-size: 12px">&gt;</span>
                        </el-option>
                        <el-option label=">=" value=">=">
                          <span style="font-size: 12px">&gt;=</span>
                        </el-option>
                        <el-option label="=" value="=">
                          <span style="font-size: 12px">=</span>
                        </el-option>
                        <el-option label="<=" value="<=">
                          <span style="font-size: 12px">&lt;=</span>
                        </el-option>
                        <el-option label="<" value="<">
                          <span style="font-size: 12px">&lt;</span>
                        </el-option>
                        <el-option label="!=" value="!=">
                          <span style="font-size: 12px">!=</span>
                        </el-option>
                      </el-select>
                    </template>
                  </el-input>
                </el-form-item>
                <el-form-item :label="$t('topo.components.propertyBar.index.038495-226')">
                  <el-radio-group v-model="configObject.dataAction.direction01">
                    <el-radio :label="$t('topo.components.propertyBar.index.038495-227')">
                      {{ $t('topo.components.propertyBar.index.038495-227') }}
                    </el-radio>
                    <el-radio :label="$t('topo.components.propertyBar.index.038495-228')">
                      {{ $t('topo.components.propertyBar.index.038495-228') }}
                    </el-radio>
                  </el-radio-group>
                </el-form-item>
              </el-form>
            </el-collapse-item>
            <!-- 线条流动判断结束 -->
            <!-- 闪烁、旋转、显隐动画 -->
            <el-collapse-item
              :title="$t('topo.components.propertyBar.index.038495-229')"
              name="13"
              v-if="configObject.componentShow && configObject.componentShow.indexOf('动画') !== -1"
            >
              <div class="click-event-item">
                <el-checkbox v-model="configObject.dataAction.xyAction">
                  {{ $t('topo.components.propertyBar.index.038495-230') }}
                </el-checkbox>
                <el-button
                  :class="{ btn: configObject.dataAction.xyAction }"
                  type="text"
                  :icon="Edit"
                  :disabled="!configObject.dataAction.xyAction"
                  @click="handleEventConfig('xy')"
                ></el-button>
              </div>
              <div class="click-event-item">
                <el-checkbox v-model="configObject.dataAction.xzAction">
                  {{ $t('topo.components.propertyBar.index.038495-231') }}
                </el-checkbox>
                <el-button
                  :class="{ btn: configObject.dataAction.xzAction }"
                  type="text"
                  :icon="Edit"
                  :disabled="!configObject.dataAction.xzAction"
                  @click="handleEventConfig('xz')"
                ></el-button>
              </div>
              <div class="click-event-item">
                <el-checkbox v-model="configObject.dataAction.ssAction">
                  {{ $t('topo.components.propertyBar.index.038495-232') }}
                </el-checkbox>
                <el-button
                  :class="{ btn: configObject.dataAction.ssAction }"
                  type="text"
                  :icon="Edit"
                  :disabled="!configObject.dataAction.ssAction"
                  @click="handleEventConfig('ss')"
                ></el-button>
              </div>
              <div class="click-event-item">
                <el-checkbox v-model="configObject.dataAction.hdAction">
                  {{ $t('topo.components.propertyBar.index.038495-233') }}
                </el-checkbox>
                <el-button
                  :class="{ btn: configObject.dataAction.hdAction }"
                  type="text"
                  :icon="Edit"
                  :disabled="!configObject.dataAction.hdAction"
                  @click="handleEventConfig('hd')"
                ></el-button>
              </div>
              <div class="click-event-item">
                <el-checkbox v-model="configObject.dataAction.maAction">
                  {{ $t('topo.components.propertyBar.index.038495-305') }}
                </el-checkbox>
                <el-button
                  :class="{ btn: configObject.dataAction.maAction }"
                  type="text"
                  :icon="Edit"
                  :disabled="!configObject.dataAction.maAction"
                  @click="handleEventConfig('ma')"
                ></el-button>
              </div>
            </el-collapse-item>
            <!-- 状态开关开始 -->
            <el-collapse-item
              :title="$t('topo.components.propertyBar.index.038495-234')"
              name="14"
              v-if="configObject.componentShow && configObject.componentShow.indexOf('状态开关') !== -1"
            >
              <template v-if="configObject.dataBind.statusType !== 2">
                <div class="btn-tools">
                  <span class="name">{{ $t('topo.components.propertyBar.index.038495-235') }}</span>
                  <div>
                    <el-button type="text" :icon="Plus" @click="addSwitch"></el-button>
                    <el-button type="text" :icon="Delete" style="margin-left: 10px" @click="deleteSwitch"></el-button>
                  </div>
                </div>
                <div
                  class="btn-tools-content"
                  v-for="(item, index) in configObject.dataBind.stateList"
                  :key="index"
                  :class="{ 'no-align': !configObject.componentShow.includes('形状类型') }"
                >
                  <div class="input-wrap">
                    <el-input
                      type="number"
                      controls-position="right"
                      class="input-with-select"
                      :placeholder="$t('topo.components.propertyBar.index.038495-225')"
                      v-model="item.paramData"
                    >
                      <template #prepend>
                        <el-select v-model="item.paramCondition" :placeholder="$t('pleaseSelect')">
                          <el-option label=">" value=">">
                            <span style="font-size: 12px">&gt;</span>
                          </el-option>
                          <el-option label=">=" value=">=">
                            <span style="font-size: 12px">&gt;=</span>
                          </el-option>
                          <el-option label="=" value="=">
                            <span style="font-size: 12px">=</span>
                          </el-option>
                          <el-option label="<=" value="<=">
                            <span style="font-size: 12px">&lt;=</span>
                          </el-option>
                          <el-option label="<" value="<">
                            <span style="font-size: 12px">&lt;</span>
                          </el-option>
                          <el-option label="!=" value="!=">
                            <span style="font-size: 12px">!=</span>
                          </el-option>
                        </el-select>
                      </template>
                    </el-input>
                  </div>
                  <div class="img-wrap">
                    <el-image
                      style="width: 30px; height: 30px; cursor: pointer"
                      fit="fill"
                      v-if="item.imageUrl"
                      :src="baseApi + item.imageUrl"
                      @click="handleGalleryClick('状态开关-' + item.id)"
                    />
                    <el-icon v-else class="state-switch-plus" @click="handleGalleryClick('状态开关-' + item.id)">
                      <Plus />
                    </el-icon>
                  </div>
                  <div class="color-picker-wrap">
                    <el-color-picker
                      v-model="item.foreColor"
                      show-alpha
                      :predefine="predefineColors"
                      color-format="hex"
                    />
                  </div>
                </div>
              </template>
              <!-- 设备状态判断在线、离线、报警 -->
              <template v-if="configObject.dataBind.statusType === 2">
                <div class="form-item-wrap">
                  <span class="name">{{ $t('topo.components.propertyBar.index.038495-236') }}</span>
                  <div class="content">
                    <img
                      v-if="configObject?.dataBind?.shutImageUrl"
                      style="width: 30px; height: 30px"
                      :src="baseApi + configObject.dataBind.shutImageUrl"
                      @click="handleGalleryClick('开关状态(关)')"
                    />
                    <el-button
                      v-else
                      type="text"
                      :icon="Plus"
                      size="small"
                      @click="handleGalleryClick('开关状态(关)')"
                    ></el-button>
                  </div>
                </div>
                <div class="form-item-wrap">
                  <span class="name">{{ $t('topo.components.propertyBar.index.038495-237') }}</span>
                  <div class="content">
                    <img
                      v-if="configObject?.dataBind?.openImageUrl"
                      style="width: 30px; height: 30px"
                      :src="baseApi + configObject.dataBind.openImageUrl"
                      @click="handleGalleryClick('开关状态(开)')"
                    />
                    <el-button
                      v-else
                      type="text"
                      :icon="Plus"
                      size="small"
                      @click="handleGalleryClick('开关状态(开)')"
                    ></el-button>
                  </div>
                </div>
                <div class="form-item-wrap">
                  <span class="name">{{ $t('topo.components.propertyBar.index.038495-238') }}</span>
                  <div class="content">
                    <img
                      v-if="configObject?.dataBind?.warnImageUrl"
                      style="width: 30px; height: 30px"
                      :src="baseApi + configObject.dataBind.warnImageUrl"
                      @click="handleGalleryClick('开关状态(报警)')"
                    />
                    <el-button
                      v-else
                      type="text"
                      :icon="Plus"
                      size="small"
                      @click="handleGalleryClick('开关状态(报警)')"
                    ></el-button>
                  </div>
                </div>
              </template>
            </el-collapse-item>
            <!-- 状态开关结束 -->
            <!-- 填充颜色开始 -->
            <el-collapse-item
              :title="$t('topo.components.propertyBar.index.038495-239')"
              name="15"
              v-if="configObject.componentShow && configObject.componentShow.indexOf('组件填充') !== -1"
            >
              <div class="btn-tools">
                <span class="name">{{ $t('topo.components.propertyBar.index.038495-235') }}</span>
                <div>
                  <el-button type="text" :icon="Plus" @click="addSwitch"></el-button>
                  <el-button type="text" :icon="Delete" style="margin-left: 10px" @click="deleteSwitch"></el-button>
                </div>
              </div>
              <div
                class="btn-tools-content"
                v-for="(item, index) in configObject.dataBind.stateList"
                :key="index"
                :class="{ 'no-align': !configObject.componentShow.includes('形状类型') }"
              >
                <!-- 条件输入 -->
                <div style="margin-bottom: 10px" class="input-wrap">
                  <el-input
                    type="number"
                    controls-position="right"
                    class="input-with-select"
                    :placeholder="$t('topo.components.propertyBar.index.038495-225')"
                    v-model="item.paramData"
                  >
                    <template #prepend>
                      <el-select v-model="item.paramCondition" :placeholder="$t('pleaseSelect')">
                        <el-option v-for="op in ['>', '>=', '=', '<=', '<', '!=']" :key="op" :label="op" :value="op">
                          <span style="font-size: 12px">{{ op }}</span>
                        </el-option>
                      </el-select>
                    </template>
                  </el-input>
                </div>
                <!-- 包含形状类型 -->
                <div v-if="configObject.componentShow.includes('形状类型')" class="color-picker-row">
                  <!-- 形状类型 = stroke -->
                  <div v-if="configObject.style.shapeType === 'stroke'" class="color-picker-wrap">
                    <div class="color-picker-box">
                      <span class="label">{{ $t('topo.components.propertyBar.index.038495-167') }}</span>
                      <el-color-picker
                        v-model="item.borderColor"
                        show-alpha
                        :predefine="predefineColors"
                        color-format="hex"
                      />
                    </div>
                  </div>
                  <!-- 形状类型 = fill-->
                  <template v-else-if="configObject.style.shapeType === 'fill'">
                    <div class="color-picker-wrap">
                      <div class="color-picker-box">
                        <span class="label">{{ $t('topo.components.propertyBar.index.038495-64') }}</span>
                        <el-color-picker
                          v-model="item.foreColor"
                          show-alpha
                          :predefine="predefineColors"
                          color-format="hex"
                        />
                      </div>
                    </div>
                    <div class="color-picker-wrap">
                      <div class="color-picker-box">
                        <span class="label">{{ $t('topo.components.propertyBar.index.038495-167') }}</span>
                        <el-color-picker
                          v-model="item.borderColor"
                          show-alpha
                          :predefine="predefineColors"
                          color-format="hex"
                        />
                      </div>
                    </div>
                  </template>
                </div>
                <!-- 不包含形状类型，就只显示填充色 -->
                <div v-else class="color-picker-wrap">
                  <el-color-picker
                    v-model="item.foreColor"
                    show-alpha
                    :predefine="predefineColors"
                    color-format="hex"
                  />
                </div>
              </div>
            </el-collapse-item>
            <!-- 填充颜色结束 -->
            <!-- 天气组件开始 -->
            <el-collapse-item
              :title="$t('topo.components.propertyBar.index.038495-240')"
              name="16"
              v-if="configObject.componentShow && configObject.componentShow.indexOf('天气') !== -1"
            >
              <el-form label-width="35px">
                <el-form-item :label="$t('topo.components.propertyBar.index.038495-241')">
                  <el-select
                    v-model="configObject.dataBind.provinceCode"
                    :placeholder="$t('topo.components.propertyBar.index.038495-242')"
                    style="width: 100%"
                    filterable
                    @change="handleProvinceChange"
                  >
                    <el-option v-for="pro in provinceList" :key="pro.code" :label="pro.name" :value="pro.code">
                      <span style="font-size: 12px">{{ pro.name }}</span>
                    </el-option>
                  </el-select>
                </el-form-item>
                <el-form-item :label="$t('topo.components.propertyBar.index.038495-243')">
                  <el-select
                    v-model="configObject.dataBind.cityCode"
                    :placeholder="$t('topo.components.propertyBar.index.038495-244')"
                    style="width: 100%"
                    filterable
                    @change="handleCityChange"
                  >
                    <el-option v-for="city in cityList" :key="city.code" :label="city.name" :value="city.code">
                      <span style="font-size: 12px">{{ city.name }}</span>
                    </el-option>
                  </el-select>
                </el-form-item>
                <el-form-item :label="$t('topo.components.propertyBar.index.038495-245')">
                  <el-select
                    v-model="configObject.dataBind.districtCode"
                    :placeholder="$t('topo.components.propertyBar.index.038495-246')"
                    style="width: 100%"
                    filterable
                    @change="handleDistrictChange"
                  >
                    <el-option v-for="dis in districtList" :key="dis.code" :label="dis.name" :value="dis.code">
                      <span style="font-size: 12px">{{ dis.name }}</span>
                    </el-option>
                  </el-select>
                </el-form-item>
              </el-form>
              <el-alert
                style="margin-top: 15px"
                :title="$t('topo.components.propertyBar.index.038495-246')"
                type="warning"
                :description="$t('topo.components.propertyBar.index.038495-248')"
              ></el-alert>
            </el-collapse-item>
            <!-- 天气组件结束 -->
            <!-- 卡片组件开始 -->
            <el-collapse-item
              :title="$t('topo.components.propertyBar.index.038495-317')"
              name="17"
              v-if="configObject.componentShow && configObject.componentShow.indexOf('卡片') !== -1"
            >
              <el-form label-width="75px">
                <el-form-item :label="$t('topo.components.propertyBar.index.038495-318')">
                  <el-input
                    :placeholder="$t('topo.components.propertyBar.index.038495-319')"
                    v-model="configObject.dataBind.tableTitle"
                    clearable
                  />
                </el-form-item>
                <el-form-item :label="$t('topo.components.propertyBar.index.038495-320')">
                  <el-button style="width: 100%" class="gen-btn" @click="handleCustomTableSetting(1)">
                    {{ $t('topo.components.propertyBar.index.038495-321') }}
                  </el-button>
                </el-form-item>
              </el-form>
            </el-collapse-item>
            <!-- 卡片组件结束 -->
            <!-- 内联视频开始 -->
            <el-collapse-item
              :title="$t('topo.components.propertyBar.index.038495-189')"
              name="18"
              v-if="configObject.componentShow && configObject.componentShow.indexOf('内联视频') !== -1"
            >
              <el-form v-if="scadaType === 3" label-width="48px">
                <el-form-item :label="$t('topo.components.propertyBar.index.038495-307')">
                  <el-select
                    v-model="configObject.dataBind.serialNumber"
                    :placeholder="$t('topo.components.propertyBar.index.038495-194')"
                    style="width: 100%"
                    filterable
                  >
                    <el-option
                      :label="item.deviceName"
                      :value="item.serialNumber"
                      v-for="item in deviceBindList"
                      :key="item.serialNumber"
                      :disabled="item.deviceType === 3"
                    >
                      <span style="font-size: 12px">{{ item.deviceName }}</span>
                    </el-option>
                  </el-select>
                </el-form-item>
              </el-form>
              <el-alert
                v-else
                :title="$t('topo.components.propertyBar.index.038495-247')"
                type="warning"
                :description="$t('topo.components.propertyBar.index.038495-306')"
                :closable="false"
              ></el-alert>
            </el-collapse-item>
            <!-- 内联视频结束 -->
            <!-- 数据表格开始 -->
            <el-collapse-item
              :title="$t('topo.components.propertyBar.index.038495-338')"
              name="19"
              v-if="configObject.componentShow && configObject.componentShow.indexOf('数据表格') !== -1"
            >
              <el-form label-width="75px">
                <el-form-item :label="$t('topo.components.propertyBar.index.038495-339')">
                  <el-radio-group v-model="configObject.dataBind.tableType">
                    <el-radio style="margin-right: 16px" :label="1">
                      {{ $t('topo.components.propertyBar.index.038495-340') }}
                    </el-radio>
                    <el-radio :label="2">{{ $t('topo.components.propertyBar.index.038495-341') }}</el-radio>
                  </el-radio-group>
                </el-form-item>
                <el-form-item :label="$t('topo.components.propertyBar.index.038495-342')">
                  <el-button style="width: 100%" class="gen-btn" @click="handleCustomTableSetting(2)">
                    {{ $t('topo.components.propertyBar.index.038495-343') }}
                  </el-button>
                </el-form-item>
              </el-form>
            </el-collapse-item>
            <!-- 数据表格结束 -->
            <!-- 报警记录开始 -->
            <el-collapse-item
              :title="$t('topo.components.propertyBar.index.038495-189')"
              name="20"
              v-if="configObject.componentShow && configObject.componentShow.indexOf('报警记录') !== -1"
            >
              <el-form label-width="75px" size="small">
                <el-form-item :label="$t('topo.components.propertyBar.index.038495-312')">
                  <el-radio-group v-model="configObject.dataBind.warnLogType">
                    <el-radio style="margin-right: 16px" :label="1">
                      {{ $t('topo.components.propertyBar.index.038495-410') }}
                    </el-radio>
                    <el-radio :label="2">{{ $t('topo.components.propertyBar.index.038495-411') }}</el-radio>
                  </el-radio-group>
                </el-form-item>
                <div v-if="scadaType === 1" class="tip">
                  <p>{{ $t('topo.components.propertyBar.index.038495-412') }}</p>
                  <p>{{ $t('topo.components.propertyBar.index.038495-413') }}</p>
                </div>
              </el-form>
            </el-collapse-item>
            <!-- 报警记录结束 -->
            <!-- 无需绑定数据开始 -->
            <el-collapse-item
              :title="$t('topo.components.propertyBar.index.038495-189')"
              name="21"
              v-if="configObject.componentShow && configObject.componentShow.indexOf('暂无绑定') !== -1"
            >
              <el-alert
                :title="$t('topo.components.propertyBar.index.038495-247')"
                type="warning"
                :description="$t('topo.components.propertyBar.index.038495-337')"
                :closable="false"
              ></el-alert>
            </el-collapse-item>
            <!-- 无需绑定数据结束 -->
          </el-collapse>
        </div>
        <!-- 图层 -->
        <div class="pane-item" v-show="tabIndex === 2">
          <div class="layer-box">
            <draggable
              :list="topoData[selected][pageIndex].components"
              :force-fallback="true"
              item-key="identifier"
              ghost-class="item-ghost"
              chosen-class="layer-item-chosen"
              animation="300"
              @end="handleLayerItemDraggableEnd"
            >
              <template #item="{ element: item, index }">
                <div
                  class="layers-list-item"
                  :class="{ 'is-active': layerActiveIndex === normalizeTopoIndex(index) }"
                  @click="handleLayerItemSelect(index, item)"
                  @contextmenu.prevent="(e) => handleLayerListContextMenu(e as MouseEvent, index, item)"
                >
                  <template v-if="item.img">
                    <img
                      v-if="item.img.type === 'static'"
                      class="img"
                      src="@/assets/images/scada/unitIcon/base/select.png"
                    />
                    <svg-icon v-else-if="item.img.type === 'svg'" class="img" :icon-class="item.img.icon" />
                    <img v-else class="img" :src="baseApi + item.img.icon" />
                  </template>
                  <span class="name">{{ item.name }}</span>
                  <div class="tools">
                    <svg-icon
                      v-if="item.isLock"
                      icon-class="password_open"
                      @pointerdown.stop
                      @click.stop="handleLayerItemLock(item)"
                    />
                    <svg-icon v-else icon-class="password" @pointerdown.stop @click.stop="handleLayerItemLock(item)" />
                    <svg-icon
                      v-if="item.style.visible"
                      icon-class="eye"
                      @pointerdown.stop
                      @click.stop="handleLayerItemHide(item)"
                    />
                    <svg-icon v-else icon-class="eye_close" @pointerdown.stop @click.stop="handleLayerItemHide(item)" />
                    <svg-icon
                      icon-class="edit"
                      @pointerdown.stop
                      @click.stop="handleLayerItemNameEdit(item)"
                    ></svg-icon>
                  </div>
                </div>
              </template>
            </draggable>
          </div>
        </div>
      </div>
    </template>
    <!-- 界面背景设置 -->
    <template v-if="isLayer">
      <el-form class="bg-set-wrap" label-width="70px">
        <el-form-item :label="$t('topo.components.propertyBar.index.038495-249')">
          <el-input
            :placeholder="$t('topo.components.propertyBar.index.038495-250')"
            v-model="topoData.name"
          ></el-input>
        </el-form-item>
        <el-form-item :label="$t('scada.center.indeieScada.373453-6')">
          <el-select
            v-if="selected === 'pcConfig'"
            v-model="resolution"
            :placeholder="$t('pleaseSelect')"
            style="width: 100%"
          >
            <el-option v-for="dict in pc_page_size" :key="dict.value" :label="dict.label" :value="dict.value">
              <span style="font-size: 12px">{{ dict.label }}</span>
            </el-option>
            <el-option key="custom" :label="$t('topo.components.chart.073848-8')" value="custom">
              <span style="font-size: 12px">{{ $t('topo.components.chart.073848-8') }}</span>
            </el-option>
          </el-select>
          <el-select
            v-if="selected === 'mdConfig'"
            v-model="resolution"
            :placeholder="$t('pleaseSelect')"
            style="width: 100%"
          >
            <el-option v-for="dict in mobile_page_size" :key="dict.value" :label="dict.label" :value="dict.value">
              <span style="font-size: 12px">{{ dict.label }}</span>
            </el-option>
            <el-option key="custom" :label="$t('topo.components.chart.073848-8')" value="custom">
              <span style="font-size: 12px">{{ $t('topo.components.chart.073848-8') }}</span>
            </el-option>
          </el-select>
        </el-form-item>
        <el-form-item :label="$t('topo.components.propertyBar.index.038495-251')" v-if="resolution === 'custom'">
          <el-input-number
            style="width: 100%"
            controls-position="right"
            :placeholder="$t('topo.components.propertyBar.index.038495-252')"
            v-model="topoData[selected][pageIndex].layer.width"
            :min="100"
          />
        </el-form-item>
        <el-form-item :label="$t('topo.components.propertyBar.index.038495-253')" v-if="resolution === 'custom'">
          <el-input-number
            style="width: 100%"
            controls-position="right"
            :placeholder="$t('topo.components.propertyBar.index.038495-254')"
            v-model="topoData[selected][pageIndex].layer.height"
            :min="100"
          />
        </el-form-item>
        <el-form-item :label="$t('topo.components.propertyBar.index.038495-255')">
          <div class="color-picker-wrap">
            <el-color-picker
              v-model="topoData[selected][pageIndex].layer.backColor"
              show-alpha
              :predefine="predefineColors"
              color-format="hex"
            />
          </div>
        </el-form-item>
        <el-form-item :label="$t('topo.components.propertyBar.index.038495-256')">
          <div class="bg-img-wrap" @click="handleGalleryClick('背景图片')">
            <img
              class="img-wrap"
              v-if="topoData[selected][pageIndex].layer.backgroundImage"
              :src="baseApi + topoData[selected][pageIndex].layer.backgroundImage"
            />
            <el-icon v-else class="ico-wrap"><Plus /></el-icon>
            <div class="img-tools" v-if="topoData[selected][pageIndex].layer.backgroundImage" @click.stop>
              <el-icon @click.stop="handleGalleryClick('背景图片')"><Plus /></el-icon>
              <el-icon @click.stop="topoData[selected][pageIndex].layer.backgroundImage = ''"><Delete /></el-icon>
            </div>
          </div>
        </el-form-item>
        <el-form-item :label="$t('topo.components.propertyBar.index.038495-257')">
          <el-checkbox v-model="topoData[selected][pageIndex].layer.dragZoom">
            {{ $t('topo.components.propertyBar.index.038495-258') }}
          </el-checkbox>
        </el-form-item>
      </el-form>
    </template>
    <!-- 图库对话框 -->
    <el-dialog
      :title="$t('topo.components.propertyBar.index.038495-259')"
      v-model="isGalleryDialog"
      width="1110px"
      :close-on-click-modal="false"
      append-to-body
    >
      <topo-select-image ref="topoSelectImageRef" />
      <template #footer>
        <el-button type="primary" @click="handleSelectImageConfirm">{{ $t('confirm') }}</el-button>
        <el-button @click="isGalleryDialog = false">{{ $t('cancel') }}</el-button>
      </template>
    </el-dialog>
    <!--图片动画设置  -->
    <el-dialog
      v-if="configObject && configObject.componentShow.indexOf('动画') !== -1"
      :title="$t('topo.components.propertyBar.index.038495-260')"
      v-model="isActionDialog"
      :close-on-click-modal="false"
      width="600px"
      v-dialogDrag
    >
      <el-form label-width="100px">
        <el-form-item :label="$t('topo.components.propertyBar.index.038495-261')">
          <el-input
            :placeholder="$t('topo.components.propertyBar.index.038495-195')"
            v-model="configObject.dataAction.modelName"
            readonly
            style="width: 400px"
          >
            <template #append>
              <el-button @click="handleVariateDialogBind('action')">{{ $t('select') }}</el-button>
            </template>
          </el-input>
        </el-form-item>
        <el-form-item :label="$t('topo.components.propertyBar.index.038495-262')" prop="paramJudge">
          <el-select
            v-model="configObject.dataAction.paramJudge"
            :placeholder="$t('pleaseSelect')"
            style="width: 400px"
          >
            <el-option :label="$t('topo.components.propertyBar.index.038495-389')" value=">">
              <span style="font-size: 12px">{{ $t('topo.components.propertyBar.index.038495-389') }}</span>
            </el-option>
            <el-option :label="$t('topo.components.propertyBar.index.038495-390')" value=">=">
              <span style="font-size: 12px">{{ $t('topo.components.propertyBar.index.038495-390') }}</span>
            </el-option>
            <el-option :label="$t('topo.components.propertyBar.index.038495-391')" value="=">
              <span style="font-size: 12px">{{ $t('topo.components.propertyBar.index.038495-391') }}</span>
            </el-option>
            <el-option :label="$t('topo.components.propertyBar.index.038495-392')" value="<=">
              <span style="font-size: 12px">{{ $t('topo.components.propertyBar.index.038495-392') }}</span>
            </el-option>
            <el-option :label="$t('topo.components.propertyBar.index.038495-393')" value="<">
              <span style="font-size: 12px">{{ $t('topo.components.propertyBar.index.038495-393') }}</span>
            </el-option>
            <el-option :label="$t('topo.components.propertyBar.index.038495-394')" value="!=">
              <span style="font-size: 12px">{{ $t('topo.components.propertyBar.index.038495-394') }}</span>
            </el-option>
            <el-option :label="$t('topo.components.propertyBar.index.038495-384')" value="between">
              <span style="font-size: 12px">{{ $t('topo.components.propertyBar.index.038495-384') }}</span>
            </el-option>
            <el-option :label="$t('topo.components.propertyBar.index.038495-385')" value="notBetween">
              <span style="font-size: 12px">{{ $t('topo.components.propertyBar.index.038495-385') }}</span>
            </el-option>
          </el-select>
          <el-input
            v-if="
              configObject.dataAction.paramJudge &&
              configObject.dataAction.paramJudge !== 'between' &&
              configObject.dataAction.paramJudge !== 'notBetween'
            "
            type="number"
            controls-position="right"
            class="input-with-select"
            :placeholder="$t('topo.components.propertyBar.index.038495-225')"
            v-model="configObject.dataAction.paramJudgeData"
            style="width: 400px; margin-top: 10px"
          ></el-input>
          <div
            v-if="
              configObject.dataAction.paramJudge &&
              (configObject.dataAction.paramJudge === 'between' || configObject.dataAction.paramJudge === 'notBetween')
            "
            style="width: 400px; display: flex; justify-content: space-between; align-items: center; margin-top: 10px"
          >
            <el-input
              type="number"
              controls-position="right"
              class="input-with-select"
              :placeholder="$t('topo.components.propertyBar.index.038495-11')"
              v-model="configObject.dataAction.paramJudgeDatarangeMin"
              style="width: 190px"
            ></el-input>
            <span>--</span>
            <el-input
              type="number"
              controls-position="right"
              class="input-with-select"
              :placeholder="$t('topo.components.propertyBar.index.038495-13')"
              v-model="configObject.dataAction.paramJudgeDatarangeMax"
              style="width: 190px"
            ></el-input>
          </div>
        </el-form-item>
        <el-form-item
          v-if="actionType === 'xz'"
          :label="$t('topo.components.propertyBar.index.038495-264')"
          prop="rotationSpeed"
        >
          <el-radio-group v-model="configObject.dataAction.rotationSpeed">
            <el-radio :label="$t('topo.components.propertyBar.index.038495-149')">
              {{ $t('topo.components.propertyBar.index.038495-149') }}
            </el-radio>
            <el-radio :label="$t('topo.components.propertyBar.index.038495-150')">
              {{ $t('topo.components.propertyBar.index.038495-150') }}
            </el-radio>
            <el-radio :label="$t('topo.components.propertyBar.index.038495-151')">
              {{ $t('topo.components.propertyBar.index.038495-151') }}
            </el-radio>
          </el-radio-group>
        </el-form-item>
        <div v-if="actionType === 'hd'">
          <el-form-item :label="$t('topo.components.propertyBar.index.038495-267')">
            <el-input-number
              style="width: 400px"
              controls-position="right"
              v-model="configObject.dataAction.duration"
              clearable
              :placeholder="$t('topo.components.propertyBar.index.038495-268')"
            ></el-input-number>
          </el-form-item>
          <el-form-item :label="$t('topo.components.propertyBar.index.038495-266')">
            <el-button type="primary" size="small" :icon="Plus" plain circle @click="addTranslate"></el-button>
            <el-button type="danger" size="small" :icon="Delete" plain circle @click="deleteTranslate"></el-button>
          </el-form-item>
          <el-form-item
            :label="$t('topo.components.propertyBar.index.038495-269')"
            v-for="(item, index) in configObject.dataAction.translateList"
            :key="index"
          >
            <el-input
              class="input-with-select"
              type="number"
              controls-position="right"
              :placeholder="$t('topo.components.propertyBar.index.038495-269')"
              v-model="item.position"
              style="width: 400px"
            >
              <template #prepend>
                <el-select v-model="item.direction" :placeholder="$t('pleaseSelect')">
                  <el-option :label="$t('topo.components.propertyBar.index.038495-271')" value="水平">
                    <span style="font-size: 12px">{{ $t('topo.components.propertyBar.index.038495-271') }}</span>
                  </el-option>
                  <el-option :label="$t('topo.components.propertyBar.index.038495-272')" value="竖直">
                    <span style="font-size: 12px">{{ $t('topo.components.propertyBar.index.038495-272') }}</span>
                  </el-option>
                </el-select>
              </template>
            </el-input>
          </el-form-item>
        </div>
      </el-form>
      <template #footer>
        <el-button type="primary" @click="handleAnimationSubmit">{{ $t('confirm') }}</el-button>
        <el-button @click="isActionDialog = false">{{ $t('cancel') }}</el-button>
      </template>
    </el-dialog>
    <!-- 代码视图对话框 -->
    <el-dialog
      class="code-view-dialog"
      v-if="configObject"
      :title="$t('topo.components.propertyBar.index.038495-69')"
      v-model="isCodeViewDialog"
      :close-on-click-modal="false"
      width="50%"
      append-to-body
      v-dialogDrag
    >
      <div class="title-wrap">
        <a style="color: #1890ff" href="https://www.isqqw.com/" target="_blank">
          {{ $t('topo.components.propertyBar.index.038495-297') }}
        </a>
        <span>{{ $t('topo.components.propertyBar.index.038495-368') }}</span>
        <el-icon
          class="echart-info-icon"
          @click="isEchartExplainDialog = true"
          style="color: #e6a23c; margin-left: 5px"
        >
          <InfoFilled />
        </el-icon>
      </div>
      <monaco-editor
        ref="monacoRef"
        height="65vh"
        :value="configObject.dataBind.echartOption"
        @change="handleMonacoChange"
      ></monaco-editor>
      <template #footer>
        <el-button type="primary" @click="handleEchartRunClick">
          {{ $t('topo.components.propertyBar.index.038495-299') }}
        </el-button>
        <el-button @click="isCodeViewDialog = false">{{ $t('close') }}</el-button>
      </template>
    </el-dialog>
    <!-- 数据引擎示例对话框 -->
    <el-dialog
      class="engine-example-dialog"
      :title="$t('topo.components.propertyBar.index.038495-300')"
      v-model="isEchartExplainDialog"
      width="600px"
      append-to-body
      :close-on-click-modal="false"
    >
      <div class="box-message" v-html="getDataEngineHtml()"></div>
      <template #footer>
        <el-button @click="isEchartExplainDialog = false">{{ $t('close') }}</el-button>
      </template>
    </el-dialog>

    <!-- 组件密码 -->
    <el-dialog
      :title="$t('topo.components.propertyBar.index.038495-301')"
      v-model="isSetPasswordDialog"
      width="550px"
      append-to-body
      :close-on-click-modal="false"
    >
      <el-form label-width="100px">
        <el-form-item :label="$t('topo.components.propertyBar.index.038495-302')">
          <el-input
            v-model="compPassword"
            :placeholder="$t('topo.topoRender.038944-8')"
            clearable
            type="password"
            show-password
            style="width: 350px"
          />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button type="primary" @click="handleSetPasswordSubmit">{{ $t('confirm') }}</el-button>
        <el-button @click="isSetPasswordDialog = false">{{ $t('close') }}</el-button>
      </template>
    </el-dialog>
    <!-- 自定义展示列 -->
    <el-dialog
      :title="$t('topo.components.propertyBar.index.038495-322')"
      v-model="isCustomTableDialog"
      width="600px"
      append-to-body
      :close-on-click-modal="false"
    >
      <el-table ref="customTableRef" :data="customTableData" :border="false" row-key="id">
        <el-table-column
          prop="title"
          :label="$t('topo.components.propertyBar.index.038495-323')"
          align="left"
          min-width="150"
        >
          <template #default="scope">
            <div v-if="customTableEditAllowed" class="editable-cell">
              <el-input
                v-if="scope.row._editing"
                v-model="scope.row.title"
                size="small"
                @keyup.enter="saveTitleEdit(scope.row)"
                ref="titleInput"
              ></el-input>
              <div v-else class="editable-cell-text">
                <span>{{ scope.row.title }}</span>
                <el-button
                  :icon="Edit"
                  type="text"
                  size="small"
                  @click="startTitleEdit(scope.row)"
                  style="margin-top: 3px; margin-left: 10px"
                ></el-button>
              </div>
            </div>
            <span v-else>{{ scope.row.title }}</span>
          </template>
        </el-table-column>
        <el-table-column
          prop="visible"
          :label="$t('topo.components.propertyBar.index.038495-324')"
          align="center"
          width="120"
        >
          <template #default="scope">
            <el-checkbox v-model="scope.row.visible"></el-checkbox>
          </template>
        </el-table-column>
        <el-table-column
          prop="color"
          :label="$t('topo.components.propertyBar.index.038495-325')"
          align="center"
          width="120"
        >
          <template #default="scope">
            <div v-if="scope.row.color">
              <el-color-picker
                v-model="scope.row.color"
                show-alpha
                :predefine="predefineColors"
                color-format="hex"
                size="small"
              />
            </div>
            <span v-else>{{ $t('topo.components.propertyBar.index.038495-326') }}</span>
          </template>
        </el-table-column>
      </el-table>
      <template #footer>
        <el-button type="primary" @click="handleCustomTableSubmit">
          {{ $t('topo.components.propertyBar.index.038495-327') }}
        </el-button>
        <el-button @click="isCustomTableDialog = false">
          {{ $t('topo.components.propertyBar.index.038495-328') }}
        </el-button>
      </template>
    </el-dialog>
    <!-- 变量状态抽屉 -->
    <variate-drawer
      :visible="isVariateDrawer"
      @update:visible="isVariateDrawer = $event"
      :data="variateData"
      @confirm="handleVariateDrawerConfirm"
    ></variate-drawer>
    <!-- 变量对话框 -->
    <variate-dialog
      ref="variateDialogRef"
      :visible="isVariateDialog"
      @update:visible="isVariateDialog = $event"
      :multiple="isMulVariate"
      @confirm="handleVariateDialogConfirm"
    ></variate-dialog>
    <!-- 单击事件对话框 -->
    <click-event-dialog
      ref="clickEventDialog"
      :visible="isClickEventDialog"
      @update:visible="isClickEventDialog = $event"
      :data="clickEventData"
      @variateBind="handleClickEventVariateBind"
      @confirm="handleClickEventDialogConfirm"
    ></click-event-dialog>
    <!-- 计算变量对话框 -->
    <calc-variate-dialog
      ref="calcVariateDialogRef"
      :visible="isCalcVariateDialog"
      @update:visible="isCalcVariateDialog = $event"
      @confirm="handleCalcVariateDialogConfirm"
    ></calc-variate-dialog>
    <!-- 交互事件对话框 -->
    <interaction-event-dialog
      ref="interactionEventDialog"
      :visible="isInteractionEventDialog"
      @update:visible="isInteractionEventDialog = $event"
      :data="interactionEventData"
      @confirm="handleInteractionEventDialogConfirm"
    ></interaction-event-dialog>
    <!-- 多变量数据映射对话框（数显面板 / 数据表格） -->
    <data-mapping-dialog
      :visible="multiDataMappingVisible"
      @update:visible="multiDataMappingVisible = $event"
      :modelName="configObject && configObject.dataBind ? configObject.dataBind.modelName : ''"
      :valueType="configObject && configObject.dataBind ? configObject.dataBind.valueType : ''"
      :modelMaps="configObject && configObject.dataBind ? configObject.dataBind.modelMaps : []"
      :variables="mappingVariables"
      @save="saveMultiDataMappings"
    />
  </div>
</template>
<script setup lang="ts">
import {
  ref,
  reactive,
  computed,
  watch,
  onMounted,
  nextTick,
  getCurrentInstance,
  h,
  type ComponentInternalInstance,
} from 'vue';
import type { Component } from 'vue';
import { useRoute } from 'vue-router';
import { useTopoEditorStore } from '@/stores/modules/topoEditor';
import { useDict } from '@/utils/dict';
import { Delete, Plus, Edit, Link, InfoFilled } from '@element-plus/icons-vue';
import { ElIcon } from 'element-plus';
import ContextMenu from '@imengyu/vue3-context-menu';
import { JsonViewer } from 'vue3-json-viewer';
import Draggable from 'vuedraggable';
import Sortable from 'sortablejs';

import TopoSelectImage from '../topoSelectImage.vue';
import VariateDrawer from './variateDrawer.vue';
import VariateDialog from './variateDialog.vue';
import ClickEventDialog from './clickEventDialog.vue';
import CalcVariateDialog from './calcVariateDialog.vue';
import InteractionEventDialog from './interactionEventDialog.vue';
import DataMappingDialog from './dataMappingDialog.vue';
import { listCenter } from '@/api/scada/center';
import { listDeviceBind } from '@/api/scada/topo';
import {
  applyComponentRotation,
  getEchartExplain,
  getScadaRouteType,
  getRouteQueryString,
  normalizeTopoIndex,
  reorderComponentsByStackEdge,
  syncZIndexFromComponentsOrder,
  toggleComponentMirror,
} from '@/utils/topo/topoUtil';
import { getToken } from '@/utils/auth';
import request from '@/utils/request';
import cityDatas from '@/assets/data/json/scada/citylist.json';

const { proxy } = getCurrentInstance() as ComponentInternalInstance;
const route = useRoute();
const topoEditorStore = useTopoEditorStore();
const { sys_page_size, mobile_page_size, pc_page_size } = useDict('sys_page_size', 'mobile_page_size', 'pc_page_size');

// Props：当前选中的图层层级下标（可为 null）
const props = withDefaults(
  defineProps<{
    layerActiveIndex?: number | null;
  }>(),
  { layerActiveIndex: null }
);

// ==================== 响应式数据 ====================
const baseApi = import.meta.env.VITE_APP_BASE_API;
const scadaType = ref(3);
const authorization = 'Bearer ' + getToken();
const tabIndex = ref(0);
const resolutionTemp = ref('');
const mappingVariables = ref<any[]>([]);
const styleCollapseActive = ref(['1', '2', '3']);
const dataCollapseActive = ref([
  '1',
  '2',
  '3',
  '4',
  '5',
  '6',
  '7',
  '8',
  '9',
  '10',
  '11',
  '12',
  '13',
  '14',
  '15',
  '16',
  '17',
  '18',
  '19',
  '20',
  '21',
]);
const predefineColors = [
  '#ff4500',
  '#ff8c00',
  '#ffd700',
  '#90ee90',
  '#00ced1',
  '#1e90ff',
  '#c71585',
  'rgba(255, 69, 0, 0.68)',
  'rgb(255, 120, 0)',
  'hsv(51, 100, 98)',
  'hsva(120, 40, 94, 0.5)',
  'hsl(181, 100%, 37%)',
  'hsla(209, 100%, 56%, 0.73)',
  '#c7158577',
  '#ffffff',
  '#000',
];
const deviceBindList = ref<any[]>([]);
const ztOption = ref<any[]>([]);
const fontFamilyOptions = [
  'Arial',
  'Helvetica',
  'sans-serif',
  '宋体',
  '新宋体',
  '黑体',
  '微软雅黑',
  '楷体',
  '隶书',
  '幼圆',
  '华文琥珀',
  '华文行楷',
  '华文新魏',
];
const fontStyleOptions = [
  { id: 'normal', text: '正常' },
  { id: 'italic', text: '倾斜' },
  { id: 'oblique', text: '倾斜(字体不支持使用)' },
];
const fontWeightOptions = [
  { id: 'normal', text: '正常' },
  { id: 'lighter', text: '细体' },
  { id: 'bold', text: '粗体' },
];
const dataTypeOptions = [
  { id: 'binary', text: '二进制', value: 2 },
  { id: 'octonary', text: '八进制', value: 8 },
  { id: 'decimal', text: '十进制', value: 10 },
  { id: 'hexadecimal', text: '十六进制', value: 16 },
];
const textAlignOptions = ['left', 'right', 'center', 'justify'];
const borderStyleOptions = ['solid', 'dashed', 'dotted'];
const isCustomTableDialog = ref(false);
const customTableEditAllowed = ref(true);
const customTableData = ref([
  { id: 'index', title: '序号', visible: true, color: '#606266' },
  { id: 'deviceName', title: '设备', visible: true, color: '#606266' },
  { id: 'modelName', title: '变量名', visible: true, color: '#606266' },
  { id: 'value', title: '当前值', visible: true, color: '#606266' },
  { id: 'unit', title: '单位', visible: true, color: '#606266' },
  { id: 'ts', title: '更新时间', visible: true, color: '#606266' },
]);
const colorTable = [
  { id: 'index', title: '序号', visible: true, color: '#606266' },
  { id: 'deviceName', title: '设备', visible: true, color: '#606266' },
  { id: 'modelName', title: '变量名', visible: true, color: '#606266' },
  { id: 'value', title: '当前值', visible: true, color: '#606266' },
  { id: 'unit', title: '单位', visible: true, color: '#606266' },
  { id: 'ts', title: '更新时间', visible: true, color: '#606266' },
];
const sortable = ref<any>(null);
const provinceList = cityDatas;
const cityList = ref<any[]>([]);
const districtList = ref<any[]>([]);
const isCodeViewDialog = ref(false);
const isEchartExplainDialog = ref(false);
const isGalleryDialog = ref(false);
const imgProperty = ref('');
const isSetPasswordDialog = ref(false);
const compPassword = ref('');
const paramNameList = [
  '设备总数',
  '设备在线数',
  '设备离线数',
  '设备报警数',
  '在线比率',
  '日报警总数',
  '日报警已处理',
  '日报警未处理',
  '日报警处理比率',
  '周报警总数',
  '周报警已处理',
  '周报警未处理',
  '周报警处理比率',
  '月报警总数',
  '月报警已处理',
  '月报警未处理',
  '月报警处理比率',
  '年报警已处理',
  '年报警未处理',
  '年报警处理比率',
  '日工单总数',
  '日工单已处理',
  '日工单未处理',
  '日工单处理比率',
  '周工单总数',
  '周工单已处理',
  '周工单未处理',
  '周工单处理比率',
  '月工单总数',
  '月工单已处理',
  '月工单未处理',
  '月工单处理比率',
  '年工单总数',
  '年工单已处理',
  '年工单未处理',
  '年工单处理比率',
];
const angleStyle = reactive({ opacity: 0.1 });
const debouncedWatch = ref<any>(null);
const xAxis = ref<number | null>(null);
const yAxis = ref<number | null>(null);
const width = ref<number | null>(null);
const height = ref<number | null>(null);
const isVariateDrawer = ref(false);
const variateType = ref<string | null>(null);
const variateData = ref<any>({});
const isVariateDialog = ref(false);
const isMulVariate = ref(false);
const isActionDialog = ref(false);
const actionType = ref('');
const isCalcVariateDialog = ref(false);
const isClickEventDialog = ref(false);
const clickEventData = ref<any>({});
const isInteractionEventDialog = ref(false);
const interactionEventData = ref<any>({});
const requestType = ref('dynamic');
const multiDataMappingVisible = ref(false);

// ==================== 计算属性 ====================
const topoData = computed(() => topoEditorStore.topoData);
const selectedComponents = computed(() => topoEditorStore.selectedComponents);
const selectedComponentMap = computed(() => topoEditorStore.selectedComponentMap);
const isLayer = computed(() => topoEditorStore.selectedIsLayer);
const configObject = computed(() => topoEditorStore.selectedComponent);
const historyData = computed(() => topoEditorStore.historyData);
const selected = computed(() => topoEditorStore.selected);
const pageIndex = computed(() => topoEditorStore.pageIndex);
const animations = computed(() => {
  if (configObject.value?.direction === 'vertical') {
    return [
      { label: '向上', value: 'up' },
      { label: '向下', value: 'down' },
    ];
  }
  return [
    { label: '向右', value: 'right' },
    { label: '向左', value: 'left' },
  ];
});
const resolution = computed({
  get: () => {
    if (resolutionTemp.value === 'custom') return 'custom';
    const data = topoData.value[selected.value];
    if (!data || !data[pageIndex.value]) return '';
    return `${data[pageIndex.value].layer.width}x${data[pageIndex.value].layer.height}`;
  },
  set: (val: string) => {
    resolutionTemp.value = val;
    if (val !== 'custom') {
      const [w, h] = val.split('x');
      topoData.value[selected.value][pageIndex.value].layer.width = parseInt(w);
      topoData.value[selected.value][pageIndex.value].layer.height = parseInt(h);
    }
  },
});
const showVariateItem = computed(() => {
  if (configObject.value?.type === 'time-table' || configObject.value?.type === 'num-panel') return false;
  const bind = configObject.value?.dataBind;
  if (!bind) return false;
  return (bind.statusType !== 2 && bind.modelName !== undefined) || bind.dataType === 4 || bind.dataType === 5;
});
const showMultiBindDataProcess = computed(() => {
  return !!(
    configObject.value &&
    configObject.value.dataBind &&
    configObject.value.dataBind.identifiers !== undefined &&
    (configObject.value.type === 'num-panel' || configObject.value.type === 'time-table')
  );
});

// ==================== 侦听器 ====================
watch(
  () => selectedComponentMap.value,
  (val) => {
    if (!val) return;
    const keys = Object.keys(val);
    if (keys.length === 1) {
      const comp = val[keys[0]];
      xAxis.value = comp?.style?.position?.x ?? null;
      yAxis.value = comp?.style?.position?.y ?? null;
      width.value = comp?.style?.position?.w ?? null;
      height.value = comp?.style?.position?.h ?? null;
    } else {
      xAxis.value = null;
      yAxis.value = null;
      width.value = null;
      height.value = null;
    }
  },
  { deep: true, immediate: true }
);

watch(selected, () => resetSelected());
watch(pageIndex, () => resetSelected());

watch(
  () => configObject.value?.dataBind,
  (val) => {
    const vars: any[] = [];
    if (val) {
      const ids = val.identifiers || [];
      const names = val.modelNames || [];
      const rows = val.boundRows || [];
      if (rows?.length) {
        rows.forEach((r: any) => {
          vars.push({ identifier: r.identifier, modelName: r.modelName, valueType: r.valueType });
        });
      } else {
        ids.forEach((id: string, idx: number) => {
          vars.push({ identifier: id, modelName: names[idx] || id });
        });
      }
    }
    mappingVariables.value = vars;
  },
  { deep: true, immediate: true }
);

watch(
  configObject,
  (newVal) => {
    if (!newVal?.dataBind) return;
    if (newVal.type !== 'num-panel' && newVal.type !== 'time-table') return;
    if (newVal.dataBind.filter === undefined) newVal.dataBind.filter = 0;
    if (!Array.isArray(newVal.dataBind.modelMaps)) newVal.dataBind.modelMaps = [];
    if (!Array.isArray(newVal.dataBind.identifiers)) newVal.dataBind.identifiers = [];
    if (!Array.isArray(newVal.dataBind.modelNames)) newVal.dataBind.modelNames = [];
    if (!Array.isArray(newVal.dataBind.boundRows)) newVal.dataBind.boundRows = [];
  },
  { immediate: true }
);

// ==================== 方法 ====================
const execute = (args?: any) => (topoEditorStore.execute as any)(args);
const clearSelectedComponent = (args?: any) => (topoEditorStore.clearSelectedComponent as any)(args);
const setLayerSelected = (args?: any) => (topoEditorStore.setLayerSelected as any)(args);
const resetSelected = (args?: any) => (topoEditorStore.resetSelected as any)(args);

const handleChangeTab = (index: number) => {
  tabIndex.value = index;
};

/** 面板嵌套组态：仅列出与当前编辑器相同 type 的其它组态（与 Vue2 一致） */
const getZtPage = () => {
  const guid = getRouteQueryString(route.query, 'guid');
  const type = getScadaRouteType(route.query);
  listCenter({
    type,
    pageNum: 1,
    pageSize: 9999,
  })
    .then((res: any) => {
      if (res.code === 200) {
        const rows = res.rows || [];
        ztOption.value = rows.filter((item: any) => item.guid && item.guid !== guid);
      } else {
        ztOption.value = [];
      }
    })
    .catch(() => {
      ztOption.value = [];
    });
};

const getBindDeviceList = () => {
  listDeviceBind({
    scadaGuid: getRouteQueryString(route.query, 'guid'),
    scadaType: getScadaRouteType(route.query),
    pageNum: 1,
    pageSize: 9999,
  }).then((res: any) => {
    if (res.code === 200) deviceBindList.value = res.rows || [];
  });
};

// ==================== 生命周期 ====================
onMounted(() => {
  scadaType.value = getScadaRouteType(route.query);
  getZtPage();
  getBindDeviceList();
});

watch(
  () => [getRouteQueryString(route.query, 'guid'), getScadaRouteType(route.query)] as const,
  () => {
    scadaType.value = getScadaRouteType(route.query);
    getZtPage();
  }
);

watch(
  () => configObject.value?.type,
  (type) => {
    if (type === 'panel') {
      getZtPage();
    }
  }
);
const openMultiDataMappingDialog = () => {
  const vars: any[] = [];
  if (configObject.value?.dataBind) {
    const ids = configObject.value.dataBind.identifiers || [];
    const names = configObject.value.dataBind.modelNames || [];
    const rows = configObject.value.dataBind.boundRows || [];
    if (rows?.length) {
      rows.forEach((r: any) => {
        vars.push({ identifier: r.identifier, modelName: r.modelName, valueType: r.valueType });
      });
    } else {
      ids.forEach((id: string, idx: number) => {
        vars.push({ identifier: id, modelName: names[idx] || id });
      });
    }
  }
  mappingVariables.value = vars;
  multiDataMappingVisible.value = true;
};

const saveMultiDataMappings = (newMappings: any[]) => {
  if (!configObject.value?.dataBind) return;
  configObject.value.dataBind.modelMaps = JSON.parse(JSON.stringify(newMappings || []));
};
const handleDeleteMultiMapping = (index: number | string) => {
  if (!configObject.value?.dataBind || !Array.isArray(configObject.value.dataBind.modelMaps)) return;
  configObject.value.dataBind.modelMaps.splice(normalizeTopoIndex(index), 1);
};

const getVarName = (identifier: string) => {
  const v = mappingVariables.value.find((x: any) => x.identifier === identifier);
  if (v) return v.modelName || identifier;
  if (configObject.value?.dataBind) {
    const { identifiers = [], modelNames = [], boundRows = [] } = configObject.value.dataBind;
    if (boundRows?.length) {
      const r = boundRows.find((r: any) => r.identifier === identifier);
      if (r) return r.modelName || identifier;
    }
    const idx = identifiers.indexOf(identifier);
    if (idx !== -1) return modelNames[idx] || identifier;
  }
  return identifier;
};

// 自定义表格
const handleCustomTableSetting = (index: number) => {
  customTableEditAllowed.value = index === 2;

  const source = configObject.value?.dataBind?.customTableDragData;
  const baseData = Array.isArray(source) && source.length ? source : colorTable;
  const customTableDataCopy = baseData.map((item: any) => ({ ...item }));
  const tableColumns = Array.isArray(configObject.value?.dataBind?.tableColumns)
    ? configObject.value.dataBind.tableColumns
    : [];
  const map = new Map();
  colorTable.forEach((item) => map.set(item.title, item.color));

  if (tableColumns.length !== 0) {
    const list = customTableDataCopy.map((item: any) => {
      const obj = tableColumns.find((chil: any) => chil.title === item.title);
      if (index === 2) {
        return { ...item, visible: !!obj, color: obj ? obj.color : item.color, _editing: false };
      }
      if (index === 1) {
        const color = map.get(item.title);
        return { ...item, visible: !!obj, color: obj?.color ? obj.color : color, _editing: false };
      }
      return { ...item, _editing: false };
    });
    customTableData.value = [...list];
  } else {
    customTableData.value = customTableDataCopy.map((item: any) => ({ ...item, _editing: false }));
  }

  isCustomTableDialog.value = true;
  nextTick(() => {
    initSortable();
  });
};

const handleCustomTableSubmit = () => {
  const list = customTableData.value.filter((item: any) => item.visible);
  configObject.value.dataBind.tableColumns = [...list];
  configObject.value.dataBind.customTableDragData = [...customTableData.value];
  isCustomTableDialog.value = false;
};

const startTitleEdit = (row: any) => {
  if (!customTableEditAllowed.value) return;
  customTableData.value.forEach((item: any) => {
    if (item._editing && item !== row) item._editing = false;
  });
  row._editing = true;
};

const saveTitleEdit = (row: any) => {
  row._editing = false;
};

const customTableRef = ref<any>(null);
const initSortable = () => {
  nextTick(() => {
    if (!customTableRef.value) return;
    const tableEl = customTableRef.value.$el;
    if (!tableEl) return;
    const el = tableEl.querySelector('.el-table__body-wrapper tbody');
    if (el) {
      if (sortable.value) sortable.value.destroy();
      sortable.value = new Sortable(el, {
        animation: 150,
        ghostClass: 'sortable-ghost',
        chosenClass: 'sortable-chosen',
        onEnd: handleSortEnd,
      });
    }
  });
};

const handleSortEnd = (evt: any) => {
  const { oldIndex, newIndex } = evt;
  const movedItem = customTableData.value.splice(oldIndex, 1)[0];
  customTableData.value.splice(newIndex, 0, movedItem);
};

const handleProvinceChange = (e: string) => {
  const province = provinceList.find((item: any) => item.code === e);
  cityList.value = province?.children || [];
  configObject.value.dataBind.provinceName = province?.name || '';
  configObject.value.dataBind.cityCode = '';
  configObject.value.dataBind.cityName = '';
  configObject.value.dataBind.districtCode = '';
  configObject.value.dataBind.districtName = '';
  configObject.value.dataBind.latitude = '';
  configObject.value.dataBind.longitude = '';
  districtList.value = [];
};

const handleCityChange = (e: string) => {
  const city = cityList.value.find((item: any) => item.code === e);
  districtList.value = city?.children || [];
  configObject.value.dataBind.cityName = city?.name || '';
  configObject.value.dataBind.districtCode = '';
  configObject.value.dataBind.districtName = '';
  configObject.value.dataBind.latitude = '';
  configObject.value.dataBind.longitude = '';
};

const handleDistrictChange = (e: string) => {
  const { lat, lon, name } = districtList.value.find((item: any) => item.code === e) || ({} as any);
  configObject.value.dataBind.districtName = name || '';
  configObject.value.dataBind.latitude = lat;
  configObject.value.dataBind.longitude = lon;
};

const httpSetting = ref<any>({});
const isHttpSetting = ref(false);
const handleHttpSetting = () => {
  const { url, type, time, unit, params, body, headers } = configObject.value?.dataBind?.httpSetting || ({} as any);
  httpSetting.value = {
    ...httpSetting.value,
    url: url || '',
    type: type || 'get',
    time: time || 60,
    unit: unit || 's',
    paramsData: (params &&
      Object.keys(params).length &&
      Object.keys(params).map((key) => ({ key, value: params[key] }))) || [{}],
    bodyType: body && Object.keys(body).length ? 2 : 1,
    bodyData: (body && Object.keys(body).length && Object.keys(body).map((key) => ({ key, value: body[key] }))) || [{}],
    headerData: (headers &&
      Object.keys(headers).length &&
      Object.keys(headers).map((key) => ({ key, value: headers[key] }))) || [{}],
  };
  isHttpSetting.value = true;
};

const monacoRef = ref<any>(null);
const handleEchartClick = () => {
  isCodeViewDialog.value = true;
  nextTick(() => {
    monacoRef.value?.setValue(configObject.value?.dataBind?.echartOption);
  });
};

const handleMonacoChange = (data: string) => {
  configObject.value.dataBind.echartOption = data;
};

const handleEchartRunClick = () => {
  configObject.value.dataBind.echartRun = Date.now();
  isCodeViewDialog.value = false;
};

const getDataEngineHtml = () => getEchartExplain();
// 变化日志

const changeLog = (newVal: any) => {
  historyData.value.push(newVal);
};

const addTranslate = () => {
  const id = configObject.value?.dataAction?.translateList?.length || 0;
  configObject.value?.dataAction.translateList.push({ id: id + 1, direction: '水平', position: 0 });
};

const deleteTranslate = () => {
  const id = configObject.value?.dataAction?.translateList?.length || 0;
  configObject.value?.dataAction?.translateList?.some((item: any, i: number) => {
    if (item.id === id) {
      configObject.value?.dataAction.translateList.splice(i, 1);
      return true;
    }
  });
};

const addSwitch = () => {
  const id = configObject.value?.dataBind?.stateList?.length || 0;
  configObject.value?.dataBind.stateList.push({ id: id + 1, paramCondition: '', paramData: '', imageUrl: '' });
};

const deleteSwitch = () => {
  const id = configObject.value?.dataBind?.stateList?.length || 0;
  configObject.value?.dataBind?.stateList?.some((item: any, i: number) => {
    if (item.id === id) {
      configObject.value?.dataBind.stateList.splice(i, 1);
      return true;
    }
  });
};

const stack = (val: string) => {
  if (!configObject.value) return;
  if (val === '置顶' || val === '置底') {
    reorderComponentsByStackEdge(
      topoData.value[selected.value][pageIndex.value].components,
      selectedComponentMap.value,
      val,
      topoData.value
    );
    if (selectedComponents.value.length === 1) {
      const arr = topoData.value[selected.value][pageIndex.value].components;
      const id = selectedComponents.value[0];
      const newIdx = arr.findIndex((c: any) => c.identifier === id);
      if (newIdx >= 0) emit('layerItemChange', { index: newIdx, item: arr[newIdx] });
    }
    return;
  }
  let zIndex = 0;
  if (val === '上一层') {
    zIndex = (configObject.value.style.zIndex || 0) + 1;
  } else if (val === '下一层') {
    zIndex = (configObject.value.style.zIndex || 0) - 1;
  } else {
    return;
  }
  for (const key in selectedComponentMap.value) {
    selectedComponentMap.value[key].style.zIndex = zIndex;
  }
};

const createCode = () => {
  let code = '';
  for (let i = 0; i < 4; i++) code += Math.floor(Math.random() * 9);
  return code;
};

const handleAnimationSubmit = () => {
  isActionDialog.value = false;
  // dataAction 已写入当前组件，子组件 watch 会触发 reinitAnimation
};

const handleGalleryClick = (label: string) => {
  imgProperty.value = label;
  isGalleryDialog.value = true;
};

const topoSelectImageRef = ref<any>(null);
const handleSelectImageConfirm = () => {
  const imgs = topoSelectImageRef.value?.handleChoice() || [];
  if (!imgs.length) {
    (proxy as any).$message.warning(proxy?.$t('scada.topo.editor.023345-36'));
    return;
  }
  const selectImage = imgs[imgs.length - 1];
  if (imgProperty.value === '背景图片') {
    topoData.value[selected.value][pageIndex.value].layer.backgroundImage = selectImage.resourceUrl;
  } else if (imgProperty.value === '图片路径') {
    configObject.value.style.url = selectImage.resourceUrl;
  } else if (imgProperty.value === '开关状态(关)') {
    configObject.value.dataBind.shutImageUrl = selectImage.resourceUrl;
  } else if (imgProperty.value === '开关状态(开)') {
    configObject.value.dataBind.openImageUrl = selectImage.resourceUrl;
  } else if (imgProperty.value === '开关状态(报警)') {
    configObject.value.dataBind.warnImageUrl = selectImage.resourceUrl;
  } else if (imgProperty.value.includes('状态开关')) {
    const id = imgProperty.value.split('-')[1];
    configObject.value?.dataBind?.stateList?.forEach((el: any) => {
      if (el.id == id) el.imageUrl = selectImage.resourceUrl;
    });
  }
  isGalleryDialog.value = false;
  topoSelectImageRef.value?.clearChoice();
};

const transform = (val: string, value?: any) => {
  if (!configObject.value) return;
  for (const key in selectedComponentMap.value) {
    const style = selectedComponentMap.value[key].style;
    if (!style) continue;
    if (val === '顺时针旋转') {
      applyComponentRotation(style, (style.transform || 0) + 90);
    } else if (val === '逆时针旋转') {
      applyComponentRotation(style, (style.transform || 0) - 90);
    } else if (val === '水平镜像') {
      toggleComponentMirror(style, 'x');
    } else if (val === '垂直镜像') {
      toggleComponentMirror(style, 'y');
    } else if (val === '自定义角度' && value) {
      applyComponentRotation(style, (style.transform || 0) + parseInt(value, 10));
    } else if (val === '自定义旋转角度' && value) {
      applyComponentRotation(style, parseInt(value, 10));
    }
  }
};

const handleSetPassword = () => {
  compPassword.value = configObject.value?.dataBind?.password || '';
  isSetPasswordDialog.value = true;
};

const handleSetPasswordSubmit = () => {
  configObject.value.dataBind.password = compPassword.value;
  isSetPasswordDialog.value = false;
};

const emit = defineEmits(['layerItemChange', 'lockStatusChange']);
/** 点击行选中画布组件；index 统一为 number 再 emit（避免 string 与 Sortable 干扰 click） */
const handleLayerItemSelect = (index: number | string, item: any) => {
  const idx = normalizeTopoIndex(index);
  if (!Number.isFinite(idx)) return;
  emit('layerItemChange', { index: idx, item });
};

const handleLayerItemDraggableChoose = (event: any) => {
  const { oldIndex } = event;
  const item = topoData.value[selected.value][pageIndex.value].components[oldIndex];
  emit('layerItemChange', { index: oldIndex, item });
};

const handleLayerItemDraggableEnd = (event: any) => {
  const { newIndex } = event;
  const arr = topoData.value[selected.value][pageIndex.value].components;
  syncZIndexFromComponentsOrder(arr, topoData.value);
  const item = arr[newIndex];
  emit('layerItemChange', { index: newIndex, item });
};

const handleLayerItemLock = (item: any) => {
  item.isLock = !item.isLock;
  if (selectedComponents.value.length === 1 && selectedComponents.value[0] === item.identifier) {
    emit('lockStatusChange', item.isLock);
  }
};

const handleLayerItemHide = (item: any) => {
  item.style.visible = !item.style.visible;
};

const handleLayerItemNameEdit = (item: any) => {
  (proxy as any).$modal
    .prompt(
      proxy?.$t('topo.components.propertyBar.index.038495-370'),
      proxy?.$t('topo.components.propertyBar.index.038495-371'),
      {
        cancelButtonText: proxy?.$t('topo.components.propertyBar.index.038495-373'),
        confirmButtonText: proxy?.$t('topo.components.propertyBar.index.038495-372'),
        inputValue: item.name,
        inputPattern: /^(?=.*\S).+$/,
        inputErrorMessage: proxy?.$t('topo.components.propertyBar.index.038495-374'),
      }
    )
    .then(({ value }: { value: string }) => {
      item.name = value;
    })
    .catch(() => {});
};

const handleLayerListContextMenu = (event: MouseEvent, _index: number | string, _item: any) => {
  event.preventDefault();
  ContextMenu.closeContextMenu();
  ContextMenu.showContextMenu({
    items: [{ label: proxy?.$t('del'), icon: ctxMenuIcon(Delete), onClick: () => deleteTopoItemComp() }],
    x: event.clientX,
    y: event.clientY,
    customClass: 'custom-class',
    zIndex: 9999,
    minWidth: 150,
  });
};

function ctxMenuIcon(comp: Component) {
  return h(ElIcon, { size: 16 }, { default: () => h(comp) });
}

const deleteTopoItemComp = () => {
  execute({ op: 'del' });
  clearSelectedComponent();
  setLayerSelected(true);
};

const handleXAxisChange = () => {
  if (!selectedComponentMap.value) return;
  Object.keys(selectedComponentMap.value).forEach((key) => {
    selectedComponentMap.value[key].style.position.x = xAxis.value;
  });
};

const handleYAxisChange = () => {
  if (!selectedComponentMap.value) return;
  Object.keys(selectedComponentMap.value).forEach((key) => {
    selectedComponentMap.value[key].style.position.y = yAxis.value;
  });
};

const handleWidthChange = () => {
  if (!selectedComponentMap.value) return;
  Object.keys(selectedComponentMap.value).forEach((key) => {
    selectedComponentMap.value[key].style.position.w = width.value;
  });
};

const handleHeightChange = () => {
  if (!selectedComponentMap.value) return;
  Object.keys(selectedComponentMap.value).forEach((key) => {
    selectedComponentMap.value[key].style.position.h = height.value;
  });
};

const getVariateStatus = () => {
  const { dataType, identifier, simValue, httpSetting, httpSettingId } = configObject.value.dataBind || ({} as any);
  if (dataType === 1) return !!identifier;
  if (dataType === 2) return !!simValue;
  if (dataType === 3) return true;
  if (dataType === 4) return !!(Object.keys(httpSetting || {}).length && httpSetting.url);
  if (dataType === 5) return !!httpSettingId;
  return false;
};

const handleVariateDrawerBind = () => {
  const { type, dataBind } = configObject.value!;
  variateData.value = { ...dataBind, type, modelMaps: Array.isArray(dataBind.modelMaps) ? dataBind.modelMaps : [] };
  isVariateDrawer.value = true;
};

const handleVariateDrawerConfirm = (data: any) => {
  const bind = configObject.value.dataBind;
  Object.assign(bind, data);
  isVariateDrawer.value = false;
};

const handleVariateDrawerDelete = () => {
  const bind = configObject.value.dataBind;
  bind.dataType = configObject.value.type === 'chart-wrapper' ? 4 : 1;
  bind.filter = 0;
  bind.funValue = '// 数据处理逻辑编辑\r\n// 以返回值作为绑定结果\r\nconst result = value;\r\nreturn result;';
  bind.simValue = '';
  bind.simInterval = 1;
  bind.modelName = '';
  bind.identifier = '';
  bind.sceneModelId = '';
  bind.sceneModelDeviceId = '';
  bind.variableType = '';
  bind.productId = '';
  bind.serialNumber = '';
  bind.staticValue =
    '[\n{"label":"选项1","value":"选项1"},\n{"label":"选项2","value":"选项2"},\n{"label":"选项3","value":"选项3"}\n]';
  bind.httpSetting = {};
  bind.httpFilter = '';
  bind.httpSettingId = '';
  bind.modelMaps = [];
  bind.valueType = '';
};

const variateDialogRef = ref<any>(null);
const handleVariateDialogBind = (type: string) => {
  if (!variateDialogRef.value) return;
  if (type === 'bind') {
    isMulVariate.value = true;
    variateDialogRef.value.setCurMultIdentifier(configObject.value.dataBind.identifiers);
  } else if (type === 'action') {
    isMulVariate.value = false;
    variateDialogRef.value.setCurSinIdentifier(configObject.value.dataAction.identifier);
  }
  isVariateDialog.value = true;
  variateType.value = type;
  nextTick(() => {
    variateDialogRef.value?.open();
  });
};

const handleVariateDialogConfirm = (rows: any) => {
  if (variateType.value === 'bind') {
    const bind = configObject.value.dataBind;
    bind.identifiers = rows.map((item: any) => item.identifier);
    bind.modelNames = rows.map((item: any) => item.modelName);
    bind.modelName = bind.modelNames.join(',');
    bind.boundRows = JSON.parse(JSON.stringify(rows));
    const types = [...new Set(rows.map((i: any) => i.valueType))];
    bind.valueType = types.length === 1 ? types[0] : '';
    bind.sceneModelId = rows[0].sceneModelId;
    bind.sceneModelDeviceIds = rows.map((item: any) => item.sceneModelDeviceId);
    bind.productId = rows[0].productId;
    bind.serialNumber = rows[0].serialNumber;
    mappingVariables.value = rows.map((r: any) => ({
      identifier: r.identifier,
      modelName: r.modelName,
      valueType: r.valueType,
    }));
  } else if (variateType.value === 'event') {
    clickEventData.value = { ...clickEventData.value, ...rows };
  } else if (variateType.value === 'action') {
    const action = configObject.value.dataAction;
    Object.assign(action, rows);
  }
  isVariateDialog.value = false;
};

const handleEventConfig = (type: string) => {
  isActionDialog.value = true;
  actionType.value = type;
};

const handleStyleFilterChange = (val: boolean) => {
  if (val) configObject.value.style.foreColor = '';
};

const calcVariateDialogRef = ref<any>(null);
const handleCalcVariateDialogBind = () => {
  const { calcFormule, calcVariates } = configObject.value.dataBind;
  if (!calcVariateDialogRef.value) return;
  calcVariateDialogRef.value.curForm = {
    formule: calcFormule || '',
    variates: JSON.parse(JSON.stringify(calcVariates || [])),
  };
  isCalcVariateDialog.value = true;
};

const handleCalcVariateDialogDelete = () => {
  configObject.value.dataBind.calcFormule = '';
  configObject.value.dataBind.calcVariates = [];
};

const handleCalcVariateDialogConfirm = (data: any) => {
  configObject.value.dataBind.calcFormule = data.formule;
  configObject.value.dataBind.calcVariates = JSON.parse(JSON.stringify(data.variates || []));
  isCalcVariateDialog.value = false;
};

const handleClickEventConfig = () => {
  const { dataEvent, type } = configObject.value!;
  clickEventData.value = { ...dataEvent, comType: type, djType: dataEvent.djType || 1 };
  isClickEventDialog.value = true;
};

const handleClickEventVariateBind = () => {
  if (!variateDialogRef.value) return;
  isMulVariate.value = false;
  variateDialogRef.value.curSinIdentifier = configObject.value.dataEvent.identifier;
  variateDialogRef.value.open();
  isVariateDialog.value = true;
  variateType.value = 'event';
};

const handleClickEventDialogConfirm = (data: any) => {
  configObject.value.dataEvent = { ...configObject.value.dataEvent, ...data };
  isClickEventDialog.value = false;
};

const modelFileInputRef = ref<HTMLInputElement | null>(null);
const modelUploading = ref(false);
const model3dAccept = '.glb,.gltf,.obj,.fbx,.stl,.dae,.3ds';

const handleModelFileChange = async (event: Event) => {
  const input = event.target as HTMLInputElement;
  const file = input.files?.[0];
  input.value = '';
  if (!file || !configObject.value?.dataBind) return;

  const formData = new FormData();
  formData.append('file', file);

  const modal = (proxy as any)?.$modal;
  modelUploading.value = true;
  try {
    const res: any = await request({
      url: '/iot/tool/upload',
      method: 'post',
      data: formData,
      headers: {
        'Content-Type': 'multipart/form-data',
        repeatSubmit: false,
      },
    });
    if (res?.code === 200 && res.url) {
      configObject.value.dataBind.model3dType = file.name.split('.').pop() || '';
      configObject.value.dataBind.model3dUrl = res.url;
      configObject.value.dataBind.model3dName = file.name;
      modal?.msgSuccess?.(proxy.$t('uploadSuccess'));
    } else {
      modal?.msgError?.(res?.msg || proxy.$t('uploadError'));
    }
  } catch {
    modal?.msgError?.(proxy.$t('uploadError'));
  } finally {
    modelUploading.value = false;
  }
};

const handleInterEventConfig = () => {
  const { dataBind, dataEvent } = configObject.value;
  interactionEventData.value = { interInfo: dataBind.interaction, interList: dataEvent.interaction };
  isInteractionEventDialog.value = true;
};

const handleInteractionEventDialogConfirm = (data: any) => {
  configObject.value.dataEvent = { ...configObject.value.dataEvent, interaction: data.interList };
  isInteractionEventDialog.value = false;
};
</script>

<style lang="scss" scoped>
.topo-property-bar {
  width: 100%;
  height: 100%;
  border-left: 1px solid #dcdfe6;

  .editable-cell {
    .editable-cell-text {
      display: flex;
      justify-content: space-between;
      align-items: center;
    }
  }

  .tabs-title {
    height: 44px;
    display: flex;
    border-bottom: 1px solid #dcdfe6;

    .title-item {
      flex: 1;
      height: 44px;
      text-align: center;
      line-height: 44px;
      color: #323232;
      font-size: 14px;
      font-weight: 500;
    }

    .title-item:hover {
      cursor: pointer;
    }

    .title-item-active {
      color: #486ff2;
      border-bottom: #486ff2 solid 2px;
    }
  }

  .tabs-pane {
    overflow-x: hidden;
    overflow-y: auto;
    height: calc(100% - 44px);
    padding: 0 10px;

    .pane-item {
      padding: 0;

      .collapse-wrap {
        border-top: unset;
        border-bottom: unset;

        .gen-btn {
          width: 100%;
          border: 1px solid #6d8cf58f !important;
          background-color: #ffffff !important;
          color: #6d8cf5 !important;
        }

        .gen-btn:hover {
          border: 1px solid #486ff2 !important;
          background-color: #edf1fe !important;
          color: #606266 !important;
        }

        .variate-item {
          display: flex;
          align-items: center; /* 垂直居中（关键） */
          justify-content: space-between; /* 文字左、图标右（关键） */
          width: 100%; /* 占满父容器宽度 */

          .left-wrap {
            display: flex;
            align-items: center;
          }

          .right-wrap {
            display: flex;
            align-items: center;
            gap: 10px;
            margin-right: 10px;
          }

          .text {
            font-size: 13px;
            color: #486ff2;
            white-space: nowrap; /* 防止文字换行 */
          }

          .icon {
            font-size: 15px;
            cursor: pointer;
            display: inline-flex;
            align-items: center;
            justify-content: center;
          }
        }

        .click-event-item {
          display: flex;
          flex-direction: row;
          justify-content: space-between;
          align-items: center;
          padding: 0 5px 0 8px;
          font-size: 13px;

          .btn {
            :deep(i) {
              color: #486ff2;
            }
          }
        }

        .btn-tools {
          display: flex;
          flex-direction: row;
          align-items: center;
          justify-content: space-between;
          padding: 0 5px 0 8px;

          .name {
            font-size: 13px;
            color: #606266;
          }
        }

        .btn-tools-content {
          display: flex;
          flex-direction: row;
          align-items: center;
          margin-top: 10px;
          padding: 0 8px;
          flex-wrap: wrap;

          .img-wrap {
            width: 39px;
            height: 32px;
            display: flex;
            flex-direction: row;
            justify-content: center;
            padding: 0 5px;
          }

          .state-switch-plus {
            display: inline-flex;
            align-items: center;
            justify-content: center;
            width: 39px;
            height: 32px;
            cursor: pointer;
            color: #909399;
          }
        }
        .btn-tools-content.no-align {
          align-items: unset;
          flex-wrap: unset;
        }

        .form-item-wrap {
          display: flex;
          flex-direction: row;
          align-items: center;
          padding-left: 8px;

          .name {
            font-size: 12px;
            color: #606266;
            min-width: 47px;
          }

          .content {
            margin-left: 13px;
            height: 32px;
            line-height: 32px;
          }
        }

        .camera-form-item {
          :deep(.el-form-item__content) {
            width: 100%;
          }
        }

        .camera-fields {
          width: 100%;

          .form-item-wrap {
            width: 100%;
            padding-left: 0;
            margin-bottom: 10px;

            &:last-child {
              margin-bottom: 0;
            }

            .content {
              flex: 1;
              min-width: 0;
              margin-left: 8px;

              :deep(.el-input-number) {
                width: 100%;
              }
            }
          }
        }

        .upload-btn {
          width: 100%;

          :deep(.el-upload) {
            width: 100%;
            display: block;
          }
        }

        .model-upload-label {
          display: flex;
          align-items: center;
          justify-content: center;
          width: 100%;
          height: 32px;
          margin: 0;
          cursor: pointer;
          box-sizing: border-box;

          &.is-loading {
            opacity: 0.7;
            pointer-events: none;
          }
        }

        .model-file-input {
          display: none;
        }

        .tip {
          margin-left: 11px;

          p {
            font-size: 13px;
            line-height: 23px;
            margin-top: 10px;
          }
        }
      }

      .layer-box {
        padding: 10px 0;

        .layers-list-item {
          display: flex;
          flex-direction: row;
          align-items: center;
          padding: 10px 1px;
          border-radius: 4px;
          cursor: pointer;
          user-select: none;

          &:hover {
            background-color: #edf1fe;
            box-shadow:
              0 0 8px 0 rgba(232, 237, 250, 0.6),
              0 2px 4px 0 rgba(232, 237, 250, 0.5);
          }

          .img {
            width: 30px;
            height: 28px;
            border-radius: 4px;
            margin-left: 4px;
          }

          .name {
            flex: 1;
            display: inline-block;
            padding: 0 10px;
            font-size: 13px;
            line-height: normal;
            white-space: nowrap;
            overflow: hidden;
            text-overflow: ellipsis;
          }

          .tools {
            width: 58px;
            display: flex;
            flex-direction: row;
            justify-content: space-between;
            font-size: 14px;
            margin-right: 4px;
          }
        }

        .is-active {
          color: #486ff2;
          background-color: #edf1fe;
          box-shadow:
            0 0 8px 0 rgba(232, 237, 250, 0.6),
            0 2px 4px 0 rgba(232, 237, 250, 0.5);
        }

        .layer-item-chosen {
          opacity: 0.95;
        }

        .item-ghost {
          background-color: #edf1fe;
          box-shadow:
            0 0 8px 0 rgba(232, 237, 250, 0.6),
            0 2px 4px 0 rgba(232, 237, 250, 0.5);
        }
      }
    }
  }

  .bg-set-wrap {
    padding: 35px 10px;

    .bg-img-wrap {
      background-color: #fbfdff;
      border: 1px dashed #c0ccda;
      border-radius: 6px;
      width: 100%;
      height: 128px;
      cursor: pointer;
      display: flex;
      flex-direction: row;
      justify-content: center;
      align-items: center;

      &:hover {
        border-color: #409eff;

        .img-tools {
          opacity: 1;
        }
      }

      .ico-wrap {
        font-size: 26px;
        color: #8c939d;
      }

      .img-wrap {
        width: 100%;
        height: 100%;
        padding: 10px;
        background-size: cover;
      }

      .img-tools {
        position: absolute;
        width: 100%;
        height: 100%;
        left: 0;
        top: 0;
        cursor: default;
        text-align: center;
        color: #fff;
        font-size: 36px;
        background-color: rgba(0, 0, 0, 0.5);
        transition: opacity 0.3s;
        cursor: pointer;
        opacity: 0;
        display: flex;
        flex-direction: row;
        justify-content: center;
        align-items: center;

        i {
          padding: 0 8px;
        }
      }
    }
  }

  .color-picker-row {
    display: flex;
    gap: 20px;
  }
  .color-picker-wrap {
    height: 32.5px;
    margin-left: 6px;
  }
  .color-picker-box {
    display: flex;
    align-items: center;
    gap: 10px;
    .label {
      font-size: 12px;
      color: #606266;
    }
  }

  .json-view-wrap {
    height: 125px;
    overflow: auto;
    border-radius: 4px;
    border: 1px solid #dcdfe6;

    :deep(.jv-button) {
      font-size: 12px;
      padding: 0;
    }
  }

  .code-view-dialog {
    width: 100%;

    .title-wrap {
      font-size: 16px;
      margin-bottom: 20px;

      .echart-info-icon {
        cursor: pointer;
        vertical-align: middle;
      }
    }
  }

  .engine-example-dialog {
    width: 100%;

    .box-message {
      padding: 20px;
      border: 1px solid #f4dfb6;
      background: #fffbf4;
      text-align: left;
      height: 400px;
      overflow-y: auto;
    }
  }

  :deep(.el-form-item:last-child) {
    margin-bottom: 0;
  }

  :deep(.el-form--label-top .el-form-item__label) {
    padding: 0;
  }

  :deep(.el-input) {
    font-size: 12px;
  }

  :deep(.el-checkbox__label) {
    font-size: 12px;
  }

  :deep(.el-collapse-item__wrap) {
    background-color: transparent;
    border-bottom: unset;
  }

  :deep(.el-radio__label) {
    font-size: 12px;
  }

  :deep(.el-input-group--prepend .el-input__inner) {
    padding: 0 10px;
  }

  .input-with-select :deep(.el-input-group__prepend) {
    background-color: #fff;
    padding: 0 10px;
    min-width: 75px;

    .el-select {
      margin: -10px;
    }
  }

  :deep(.sortable-ghost) {
    background-color: #edf1fe !important;
    box-shadow:
      0 0 8px 0 rgba(232, 237, 250, 0.6),
      0 2px 4px 0 rgba(232, 237, 250, 0.5);
  }

  :deep(.sortable-chosen) {
    background-color: #edf1fe !important;
  }

  .multi-fun-input {
    width: 100%;
    margin-bottom: 10px;

    .title {
      height: 20px;
      line-height: 16px;
      border: 1px solid rgba(0, 0, 0, 0.298);
      font-size: 13px;
      padding: 2px 6px;
      box-sizing: border-box;
      margin-bottom: 6px;
    }
  }

  .mapping-row {
    font-size: 12px;
    display: flex;
    gap: 5px;
    max-width: 200px;
  }

  .show-item {
    max-width: 100px;
    overflow-x: auto;
    white-space: nowrap;
  }

  .setup-angle-wrap {
    position: fixed;
    top: 100px;
    right: 350px;
    height: 100px;
    width: 100px;
    z-index: 1000;
    background-color: transparent;
  }
}
</style>
