<template>
  <div style="padding: 10px">
    <el-form :model="queryParams" ref="queryFormRef" :inline="true" v-show="showSearch" label-width="68px">
      <el-form-item prop="modelName" label-width="45px">
        <el-input
          v-model="queryParams.modelName"
          :placeholder="$t('product.product-things-model.142341-139')"
          clearable
          @keyup.enter="handleQuery"
          style="width: 200px"
        />
      </el-form-item>
      <el-form-item prop="identifier">
        <el-input
          v-model="queryParams.identifier"
          :placeholder="$t('product.product-things-model.142341-27')"
          clearable
          @keyup.enter="handleQuery"
          style="width: 200px"
        />
      </el-form-item>
      <el-form-item prop="type">
        <el-select
          v-model="queryParams.type"
          :placeholder="$t('product.product-things-model.142341-130')"
          clearable
          style="width: 200px"
        >
          <el-option
            v-for="dict in iot_things_type"
            :key="dict.value"
            :label="dict.label"
            :value="Number(dict.value)"
          />
        </el-select>
      </el-form-item>
      <el-form-item prop="isHistory">
        <el-select
          v-model="queryParams.isHistory"
          :placeholder="$t('product.product-things-model.142341-132')"
          clearable
          style="width: 200px"
        >
          <el-option v-for="dict in iot_yes_no" :key="dict.value" :label="dict.label" :value="dict.value" />
        </el-select>
      </el-form-item>
      <el-form-item>
        <el-button type="primary" :icon="Search" @click="handleQuery">
          {{ $t('product.product-things-model.142341-133') }}
        </el-button>
        <el-button :icon="Refresh" @click="resetQuery">{{ $t('product.product-things-model.142341-134') }}</el-button>
      </el-form-item>
    </el-form>

    <el-row class="action-toolbar" style="margin-bottom: 8px">
      <div class="action-toolbar-left">
        <div
          class="action-item"
          v-if="productInfo.status == 1 && productInfo.isOwner != 0"
          v-hasPermi="['iot:model:add']"
        >
          <el-button type="primary" :icon="Plus" plain @click="handleAdd">
            {{ $t('product.product-things-model.142341-0') }}
          </el-button>
        </div>
        <div
          class="action-item"
          v-if="productInfo.status == 1 && productInfo.isOwner != 0"
          v-hasPermi="['iot:model:import']"
        >
          <el-button plain :icon="Upload" @click="handleImport">
            {{ $t('product.product-things-model.142341-126') }}
          </el-button>
        </div>
        <div
          class="action-item"
          v-if="productInfo.status == 1 && productInfo.isOwner != 0"
          v-hasPermi="['iot:model:import']"
        >
          <el-button plain :icon="Upload" @click="handleSelect">
            {{ $t('product.product-things-model.142341-1') }}
          </el-button>
        </div>
        <div class="action-item" v-if="productInfo.isOwner != 0" v-hasPermi="['iot:model:export']">
          <el-button plain :icon="Download" @click="handleExport">
            {{ $t('export') }}
          </el-button>
        </div>
        <div class="action-item">
          <el-button plain :icon="View" @click="handleOpenThingsModel">
            {{ $t('product.product-things-model.142341-3') }}
          </el-button>
        </div>
        <div
          class="action-item"
          v-if="productInfo.status == 1 && productInfo.isOwner != 0"
          v-hasPermi="['iot:model:refresh']"
        >
          <el-tooltip
            class="item"
            effect="dark"
            :content="$t('product.product-things-model.142341-137')"
            placement="top-start"
          >
            <el-button plain :icon="Refresh" @click="handleSyncThingsModel">
              {{ $t('product.product-things-model.142341-136') }}
            </el-button>
          </el-tooltip>
        </div>
        <div
          class="action-item"
          v-if="productInfo.status == 1 && productInfo.isOwner != 0"
          v-hasPermi="['iot:model:remove']"
        >
          <el-button plain :icon="Delete" :disabled="multiple" @click="handleDelete">
            {{ $t('product.product-authorize.314975-9') }}
          </el-button>
        </div>
        <div class="action-tip">
          <el-link type="danger" underline="never">
            {{ $t('product.product-things-model.142341-4') }}
          </el-link>
        </div>
      </div>
      <right-toolbar class="action-toolbar-right" v-model:showSearch="showSearch" @queryTable="getList">
        <div style="margin-right: 10px" v-if="productInfo.status == 1 && productInfo.isOwner != 0">
          <el-tooltip class="item" effect="dark" :content="$t('template.index.891112-16')" placement="top">
            <el-button style="width: 32.5px; height: 32.5px" :icon="Sort" @click="toggleSortMode" />
          </el-tooltip>
        </div>
      </right-toolbar>
    </el-row>

    <el-table
      class="things-table"
      v-loading="loading"
      :data="modelList"
      @selection-change="handleSelectionChange"
      :border="false"
      :header-cell-style="{ background: '#ffffff', color: '#303133' }"
    >
      <el-table-column type="selection" width="55" align="center" />
      <el-table-column
        :label="$t('product.product-things-model.142341-8')"
        align="left"
        prop="modelName"
        min-width="150"
      />
      <el-table-column
        :label="$t('product.product-things-model.142341-9')"
        align="left"
        prop="identifier"
        min-width="130"
      />
      <el-table-column
        :label="$t('product.product-things-model.142341-20')"
        align="center"
        prop="modelOrder"
        width="88"
      >
        <template #header>
          <div class="sort-header">
            <span>{{ $t('product.product-things-model.142341-20') }}</span>
            <span class="sort-icons">
              <el-icon
                class="sort-icon sort-up"
                :class="{ active: sortOrderAsc }"
                @click.stop="setModelOrderSort(true)"
              >
                <CaretTop />
              </el-icon>
              <el-icon
                class="sort-icon sort-down"
                :class="{ active: !sortOrderAsc }"
                @click.stop="setModelOrderSort(false)"
              >
                <CaretBottom />
              </el-icon>
            </span>
          </div>
        </template>
      </el-table-column>
      <el-table-column :label="$t('product.product-things-model.142341-12')" align="center" width="80">
        <template #default="scope">
          <dict-tag :options="iot_yes_no" :value="scope.row.isChart" />
        </template>
      </el-table-column>
      <el-table-column :label="$t('product.product-select-template.318012-9')" align="center" width="85">
        <template #default="scope">
          <dict-tag :options="iot_yes_no" :value="scope.row.isMonitor" />
        </template>
      </el-table-column>
      <el-table-column :label="$t('product.product-things-model.142341-16')" align="center" prop="type" width="100">
        <template #default="scope">
          <dict-tag :options="iot_things_type" :value="scope.row.type" />
        </template>
      </el-table-column>
      <el-table-column :label="$t('product.product-things-model.142341-17')" align="center" prop="datatype" width="80">
        <template #default="scope">
          <dict-tag :options="iot_data_type" :value="scope.row.datatype" />
        </template>
      </el-table-column>
      <el-table-column :label="$t('product.product-things-model.142341-15')" align="center" min-width="80">
        <template #default="scope">
          <dict-tag :options="iot_yes_no" :value="scope.row.isHistory" />
        </template>
      </el-table-column>
      <el-table-column :label="$t('product.product-things-model.142341-18')" align="left" prop="specs" min-width="270">
        <template #default="scope">
          <div v-html="formatSpecsDisplay(scope.row.specs)"></div>
        </template>
      </el-table-column>
      <el-table-column
        v-if="productInfo.status != 2 && productInfo.isOwner != 0"
        :label="$t('product.product-things-model.142341-21')"
        align="center"
        width="300"
      >
        <template #default="scope">
          <template v-if="!isSortMode">
            <el-button link :icon="View" size="small" @click="handleUpdate(scope.row)" v-hasPermi="['iot:model:query']">
              {{ $t('product.product-things-model.142341-22') }}
            </el-button>
            <el-button
              link
              :icon="Delete"
              size="small"
              @click="handleDelete(scope.row)"
              v-hasPermi="['iot:model:remove']"
            >
              {{ $t('product.product-things-model.142341-23') }}
            </el-button>
            <span style="font-size: 10px; color: #999; margin-left: 10px" v-if="scope.row.isSys == '1' && isTenant">
              {{ $t('template.index.891112-21') }}
            </span>
          </template>
          <template v-else>
            <el-button
              link
              size="small"
              @click="moveTop(scope.row, scope.$index)"
              v-hasPermi="['iot:model:edit']"
              :disabled="scope.$index === 0"
            >
              {{ $t('product.product-things-model.142341-143') }}
            </el-button>
            <el-button
              link
              size="small"
              @click="moveUp(scope.row, scope.$index)"
              v-hasPermi="['iot:model:edit']"
              :disabled="scope.$index === 0"
            >
              {{ $t('product.product-things-model.142341-144') }}
            </el-button>
            <el-button
              link
              size="small"
              @click="moveDown(scope.row, scope.$index)"
              v-hasPermi="['iot:model:edit']"
              :disabled="scope.$index === total - 1"
            >
              {{ $t('product.product-things-model.142341-145') }}
            </el-button>
          </template>
        </template>
      </el-table-column>
    </el-table>

    <pagination
      v-show="total > 0"
      :total="total"
      v-model:page="queryParams.pageNum"
      v-model:limit="queryParams.pageSize"
      @pagination="getList"
    />

    <!-- 添加或修改物模型对话框 -->
    <el-dialog :title="title" v-model="open" width="600px" append-to-body>
      <el-form ref="formRef" :model="form" :rules="rules" label-width="100px">
        <el-form-item :label="$t('product.product-things-model.142341-24')" prop="modelName">
          <el-input
            v-model="form.modelName"
            :placeholder="$t('product.product-things-model.142341-25')"
            style="width: 385px"
          />
        </el-form-item>
        <el-form-item :label="$t('template.index.891112-121')" prop="modelName_en_US">
          <el-input
            v-model="form.modelName_en_US"
            :placeholder="$t('template.index.891112-122')"
            style="width: 385px"
          />
        </el-form-item>
        <el-form-item :label="$t('product.product-things-model.142341-26')" prop="identifier">
          <el-input
            v-model="form.identifier"
            :placeholder="$t('product.product-things-model.142341-27')"
            style="width: 385px"
          />
        </el-form-item>
        <el-form-item :label="$t('product.product-things-model.142341-30')" prop="type">
          <el-radio-group v-model="form.type" @change="typeChange(form.type)">
            <el-radio-button v-for="dict in iot_things_type" :key="dict.value" :value="Number(dict.value)">
              {{ dict.label }}
            </el-radio-button>
          </el-radio-group>
        </el-form-item>
        <el-form-item :label="$t('product.product-things-model.142341-34')" prop="property">
          <el-checkbox
            name="isChart"
            :label="$t('product.product-things-model.142341-12')"
            @change="isChartChange"
            v-show="form.type == 1"
            v-model="form.isChart"
            :true-value="1"
            :false-value="0"
          ></el-checkbox>
          <el-checkbox
            name="isMonitor"
            :label="$t('product.product-select-template.318012-9')"
            @change="isMonitorChange"
            v-show="form.type == 1"
            v-model="form.isMonitor"
            :true-value="1"
            :false-value="0"
          ></el-checkbox>
          <el-checkbox
            name="isReadonly"
            :label="$t('product.product-things-model.142341-35')"
            @change="isReadonlyChange"
            :disabled="form.type == 3"
            v-model="form.isReadonly"
            :true-value="1"
            :false-value="0"
          ></el-checkbox>
          <el-checkbox
            name="isHistory"
            :label="$t('product.product-things-model.142341-15')"
            v-model="form.isHistory"
            :true-value="1"
            :false-value="0"
          ></el-checkbox>
          <el-checkbox
            name="isSharePerm"
            :label="$t('product.product-things-model.142341-36')"
            v-model="form.isSharePerm"
            :true-value="1"
            :false-value="0"
          ></el-checkbox>
        </el-form-item>
        <el-divider></el-divider>
        <el-form-item :label="$t('product.product-app.045891-5')" prop="datatype">
          <el-select
            v-model="form.datatype"
            :placeholder="$t('product.product-things-model.142341-37')"
            @change="dataTypeChange"
            style="width: 175px"
          >
            <el-option
              v-for="dict in iot_data_type"
              :key="dict.value"
              :label="dict.label"
              :value="dict.value"
              :disabled="form.isChart == 1 && ['bool', 'enum', 'string', 'array', 'object'].includes(dict.value)"
            ></el-option>
          </el-select>
        </el-form-item>

        <div v-if="form.datatype == 'integer' || form.datatype == 'decimal'">
          <el-form-item label="">
            <template #label>
              <span>
                <el-tooltip effect="dark" :content="$t('template.index.891112-119')" placement="top">
                  <el-icon><QuestionFilled /></el-icon>
                </el-tooltip>
              </span>
              {{ $t('product.product-things-model.142341-45') }}
            </template>
            <el-row>
              <el-col :span="9">
                <el-input
                  v-model="form.specs.min"
                  :placeholder="$t('product.product-things-model.142341-46')"
                  type="number"
                />
              </el-col>
              <el-col :span="2" align="center">{{ $t('product.product-things-model.142341-47') }}</el-col>
              <el-col :span="9">
                <el-input
                  v-model="form.specs.max"
                  :placeholder="$t('product.product-things-model.142341-48')"
                  type="number"
                />
              </el-col>
            </el-row>
          </el-form-item>
          <el-form-item :label="$t('product.product-things-model.142341-49')">
            <el-input
              v-model="form.specs.unit"
              :placeholder="$t('product.product-things-model.142341-50')"
              style="width: 385px"
            />
          </el-form-item>
          <el-form-item :label="$t('product.product-things-model.142341-51')">
            <el-input
              v-model="form.specs.step"
              :placeholder="$t('product.product-things-model.142341-52')"
              type="number"
              style="width: 385px"
            />
          </el-form-item>
          <el-form-item :label="$t('product.product-things-model.142341-19')" prop="formula">
            <template #label>
              <span>
                {{ $t('product.product-things-model.142341-19') }}
                <el-tooltip style="cursor: pointer" effect="light" placement="top">
                  <template #content>
                    {{ $t('product.product-things-model.142341-53') }}
                    <br />
                    {{ $t('product.product-things-model.142341-54') }}
                    <br />
                    {{ $t('product.product-things-model.142341-55') }}
                    <br />
                    {{ $t('product.product-things-model.142341-56') }}
                    <br />
                    {{ $t('product.product-things-model.142341-57') }}
                    <br />
                    {{ $t('product.product-things-model.142341-58') }}
                    <br />
                    {{ $t('product.product-things-model.142341-59') }}
                    <br />
                    {{ $t('product.product-things-model.142341-60') }}
                  </template>
                  <el-icon><QuestionFilled /></el-icon>
                </el-tooltip>
              </span>
            </template>
            <el-input v-model="form.formula" style="width: 385px" />
          </el-form-item>
        </div>

        <div v-if="form.datatype == 'bool'">
          <el-form-item :label="$t('product.product-things-model.142341-63')">
            <el-row style="margin-bottom: 10px">
              <el-col :span="9">
                <el-input v-model="form.specs.falseText" :placeholder="$t('product.product-things-model.142341-64')" />
              </el-col>
              <el-col :span="10" :offset="1">{{ $t('product.product-things-model.142341-65') }}</el-col>
            </el-row>
            <el-row>
              <el-col :span="9">
                <el-input v-model="form.specs.trueText" :placeholder="$t('product.product-things-model.142341-66')" />
              </el-col>
              <el-col :span="10" :offset="1">{{ $t('product.product-things-model.142341-67') }}</el-col>
            </el-row>
          </el-form-item>
        </div>

        <div v-if="form.datatype == 'enum'">
          <el-form-item :label="$t('product.product-things-model.142341-68')">
            <el-select
              v-model="form.specs.showWay"
              :placeholder="$t('product.product-things-model.142341-69')"
              style="width: 175px"
            >
              <el-option key="select" :label="$t('product.product-things-model.142341-70')" value="select"></el-option>
              <el-option key="button" :label="$t('product.product-things-model.142341-71')" value="button"></el-option>
            </el-select>
          </el-form-item>
          <el-form-item :label="$t('product.product-things-model.142341-72')">
            <el-row v-for="(item, index) in form.specs.enumList" :key="'enum' + index" style="margin-bottom: 10px">
              <el-col :span="9">
                <el-input v-model="item.value" :placeholder="$t('product.product-things-model.142341-73')" />
              </el-col>
              <el-col :span="11" :offset="1">
                <el-input v-model="item.text" :placeholder="$t('product.product-things-model.142341-74')" />
              </el-col>
              <el-col :span="2" :offset="1" v-if="index != 0">
                <a style="color: #f56c6c" @click="removeEnumItem(index)">{{ $t('del') }}</a>
              </el-col>
            </el-row>
            <div>
              +
              <a style="color: #409eff" @click="addEnumItem()">{{ $t('product.product-things-model.142341-75') }}</a>
            </div>
          </el-form-item>
        </div>

        <div v-if="form.datatype == 'string'">
          <el-form-item :label="$t('product.product-things-model.142341-76')">
            <el-row>
              <el-col :span="9">
                <el-input
                  v-model="form.specs.maxLength"
                  :placeholder="$t('product.product-things-model.142341-77')"
                  type="number"
                />
              </el-col>
              <el-col :span="14" :offset="1">{{ $t('product.product-things-model.142341-78') }}</el-col>
            </el-row>
          </el-form-item>
        </div>

        <div v-if="form.datatype == 'array'">
          <el-form-item :label="$t('product.product-things-model.142341-79')">
            <el-row>
              <el-col :span="9">
                <el-input
                  @input="checkInput"
                  v-model="form.specs.arrayCount"
                  :placeholder="$t('product.product-things-model.142341-80')"
                  type="number"
                  @input.native="handleChangeCount"
                />
              </el-col>
            </el-row>
          </el-form-item>
          <el-form-item
            :label="$t('template.index.891112-115')"
            v-if="form.specs.arrayCount > 0 && (form.specs.arrayIndex || form.modelId == null)"
          >
            <template #label>
              <span>{{ $t('template.index.891112-115') }}</span>
              <el-tooltip style="cursor: pointer" effect="light" placement="top">
                <template #content>{{ $t('template.index.891112-116') }}</template>
                <el-icon><QuestionFilled /></el-icon>
              </el-tooltip>
            </template>
            <div v-for="(tag, index) in arrayModelList" :key="index" style="display: inline-block">
              <el-input
                v-model="arrayModelList[index]"
                @keyup.enter="editTag(index)"
                @blur="editTag(index)"
                style="width: 80px; margin-right: 10px; display: inline-block"
                type="number"
                oninput="
                  if (value > 10000) value = 10000;
                  if (value < 0) value = 0;
                "
                class="custom-input"
              ></el-input>
            </div>
          </el-form-item>
          <el-form-item :label="$t('product.product-things-model.142341-81')">
            <el-radio-group v-model="form.specs.arrayType">
              <el-radio value="integer">{{ $t('product.product-things-model.142341-38') }}</el-radio>
              <el-radio value="decimal">{{ $t('product.product-things-model.142341-39') }}</el-radio>
              <el-radio value="string">{{ $t('product.product-things-model.142341-42') }}</el-radio>
              <el-radio value="object">{{ $t('product.product-things-model.142341-44') }}</el-radio>
            </el-radio-group>
          </el-form-item>
          <el-form-item :label="$t('product.product-things-model.142341-82')" v-if="form.specs.arrayType == 'object'">
            <div style="background-color: #f8f8f8; border-radius: 5px">
              <el-row style="padding: 0 10px 5px" v-for="(item, index) in form.specs.params" :key="index">
                <div style="margin-top: 5px" v-if="index == 0"></div>
                <el-col :span="18">
                  <el-input
                    readonly
                    v-model="item.name"
                    :placeholder="$t('product.product-things-model.142341-83')"
                    style="margin-top: 3px"
                  >
                    <template #prepend>
                      <el-tag effect="dark" style="margin-left: -21px; height: 26px; line-height: 26px">
                        {{ item.order }}
                      </el-tag>
                      {{ form.identifier + '_' + item.id }}
                    </template>
                    <template #append>
                      <el-button @click="editParameter(item, index)">{{ $t('edit') }}</el-button>
                    </template>
                  </el-input>
                </el-col>
                <el-col :span="2" :offset="2">
                  <el-button plain type="danger" style="padding: 5px" :icon="Delete" @click="removeParameter(index)">
                    {{ $t('del') }}
                  </el-button>
                </el-col>
              </el-row>
            </div>
            <div>
              +
              <a style="color: #409eff" @click="addParameter()">{{ $t('product.product-things-model.142341-85') }}</a>
            </div>
          </el-form-item>
        </div>
        <div v-if="form.datatype == 'object'">
          <el-form-item :label="$t('product.product-things-model.142341-82')">
            <div style="background-color: #f8f8f8; border-radius: 5px">
              <el-row style="padding: 0 10px 5px" v-for="(item, index) in form.specs.params" :key="index">
                <div style="margin-top: 5px" v-if="index == 0"></div>
                <el-col :span="18">
                  <el-input
                    readonly
                    v-model="item.name"
                    :placeholder="$t('product.product-things-model.142341-83')"
                    style="margin-top: 3px"
                  >
                    <template #prepend>
                      <el-tag effect="dark" style="margin-left: -21px; height: 26px; line-height: 26px">
                        {{ item.order }}
                      </el-tag>
                      {{ form.identifier + '_' + item.id }}
                    </template>
                    <template #append>
                      <el-button @click="editParameter(item, index)">{{ $t('edit') }}</el-button>
                    </template>
                  </el-input>
                </el-col>
                <el-col :span="2" :offset="2">
                  <el-button plain type="danger" style="padding: 5px" :icon="Delete" @click="removeParameter(index)">
                    {{ $t('del') }}
                  </el-button>
                </el-col>
              </el-row>
            </div>
            <div>
              +
              <a style="color: #409eff" @click="addParameter()">{{ $t('product.product-things-model.142341-85') }}</a>
            </div>
          </el-form-item>
        </div>
      </el-form>

      <template #footer>
        <div class="dialog-footer">
          <el-button type="primary" @click="submitForm" v-hasPermi="['iot:model:edit']" v-show="form.modelId">
            {{ $t('update') }}
          </el-button>
          <el-button type="primary" @click="submitForm" v-hasPermi="['iot:model:add']" v-show="!form.modelId">
            {{ $t('add') }}
          </el-button>
          <el-button @click="cancel">{{ $t('cancel') }}</el-button>
        </div>
      </template>
    </el-dialog>

    <!--物模型参数类型-->
    <things-parameter :data="paramData" @dataEvent="getParamData($event)" />

    <!-- 导入通用物模型对话框 -->
    <el-dialog :title="title" v-model="openSelect" width="1000px" append-to-body>
      <product-select-template ref="productSelectTemplateRef" @idsToParentEvent="getChildData($event)" />
      <template #footer>
        <div class="dialog-footer">
          <el-button type="primary" @click="importSelect">{{ $t('import') }}</el-button>
          <el-button @click="cancelSelect">{{ $t('cancel') }}</el-button>
        </div>
      </template>
    </el-dialog>

    <!-- 查看物模型JSON -->
    <el-dialog :title="title" v-model="openThingsModel" width="600px" append-to-body>
      <div style="border: 1px solid #dcdfe6; border-radius: 8px; margin-top: -15px; height: 600px; overflow: scroll">
        <json-viewer :value="thingsModel" :expand-depth="10" copyable>
          <template v-slot:copy>{{ $t('product.product-things-model.142341-92') }}</template>
        </json-viewer>
      </div>
      <template #footer>
        <div class="dialog-footer">
          <el-button type="info" @click="handleCloseThingsModel">{{ $t('close') }}</el-button>
        </div>
      </template>
    </el-dialog>

    <!-- 批量导入 -->
    <ImportBatch ref="importBatchRef" :productId="productId" />
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, watch, onMounted, computed, getCurrentInstance } from 'vue';
import {
  Search,
  Refresh,
  Plus,
  Upload,
  Download,
  View,
  Delete,
  Sort,
  QuestionFilled,
  CaretTop,
  CaretBottom,
} from '@element-plus/icons-vue';
import { ElMessage } from 'element-plus';
import { JsonViewer } from 'vue3-json-viewer';
import 'vue3-json-viewer/dist/vue3-json-viewer.css';
import thingsParameter from '../template/parameter.vue';
import productSelectTemplate from './product-select-template.vue';
import ImportBatch from './components/batchImportThingsModel.vue';
import { listModel, getModel, delModel, addModel, updateModel, importModel, syncModel } from '@/api/iot/model';
import { checkPermi } from '@/utils/permission';
import { useDict } from '@/utils/dict';

const { iot_things_type, iot_data_type, iot_yes_no } = useDict('iot_things_type', 'iot_data_type', 'iot_yes_no');
const { proxy } = getCurrentInstance() as any;

const emit = defineEmits(['updateModel']);

const props = defineProps({
  product: {
    type: Object,
    default: null,
  },
});

const queryFormRef = ref();
const formRef = ref();
const productSelectTemplateRef = ref();
const importBatchRef = ref();

const thingsModel = ref<any>({});
const productInfo = ref<any>({});
const templateIds = ref<any[]>([]);
const loading = ref(false);
const ids = ref<any[]>([]);
const multiple = ref(true);
const showSearch = ref(true);
const total = ref(0);
const isSortMode = ref(false);
const sortOrderAsc = ref(true);
const isTenant = ref(false);
const modelList = ref<any[]>([]);
const modelCount = ref(5);
const arrayModelList = ref<any[]>([]);
const title = ref('');
const open = ref(false);
const openSelect = ref(false);
const openThingsModel = ref(false);
const productId = ref(0);
const form = ref<any>({});
const paramData = ref<any>({ index: -1, parameter: {} });
const isDisabled = ref(false);

const queryParams = reactive<any>({
  productId: 0,
  pageNum: 1,
  pageSize: 10,
});

const rules = reactive<any>({
  modelName: [{ required: true, message: proxy?.$t('product.product-things-model.142341-94'), trigger: 'blur' }],
  identifier: [
    { required: true, message: proxy?.$t('product.product-things-model.142341-95'), trigger: 'blur' },
    { validator: validateInput, trigger: 'blur' },
  ],
  modelOrder: [{ required: true, message: proxy?.$t('product.product-things-model.142341-96'), trigger: 'blur' }],
  type: [{ required: true, message: proxy?.$t('product.product-things-model.142341-97'), trigger: 'change' }],
  datatype: [{ required: true, message: proxy?.$t('product.product-things-model.142341-98'), trigger: 'change' }],
  modelName_en_US: [{ required: true, message: proxy?.$t('template.index.891112-123'), trigger: 'blur' }],
});

watch(
  () => props.product?.productId,
  (newProductId) => {
    productInfo.value = props.product || {};
    const pid = Number(newProductId || 0);
    if (pid > 0) {
      queryParams.productId = pid;
      productId.value = pid;
      getList();
    }
  },
  { immediate: true }
);

onMounted(() => {
  if (modelCount.value) {
    arrayModelList.value = Array.from({ length: modelCount.value }, (_, index) => index);
  }
  let hasPermission = checkPermi(['iot:model:edit']);
  if (!hasPermission) {
    isDisabled.value = true;
  }
});

function validateInput(rule: any, value: any, callback: any) {
  if (!value || !value.trim() || /\s/.test(value)) {
    callback(new Error(proxy?.$t('template.index.891112-114')));
  } else {
    callback();
  }
}

function handleChangeCount() {
  modelCount.value = form.value.specs.arrayCount;
  if (form.value.specs.arrayCount) {
    arrayModelList.value = Array.from({ length: form.value.specs.arrayCount }, (_, index) => index);
  }
}

function editTag(index: number) {
  // placeholder for tag edit
}

// 自动刷新排序
function handleInputChange(row: any) {
  updateModel(row).then(() => {
    proxy?.$modal.msgSuccess(proxy?.$t('updateSuccess'));
    getList();
    emit('updateModel');
  });
}

function getList() {
  loading.value = true;
  listModel(queryParams).then((response: any) => {
    modelList.value = response.rows;
    total.value = response.total;
    loading.value = false;
  });
}

function handleOrderSort(direction: 'asc' | 'desc') {
  queryParams.pageNum = 1;
  queryParams.orderBy = 'model_order';
  queryParams.orderType = direction;
  getList();
}

function toggleModelOrderSort() {
  setModelOrderSort(!sortOrderAsc.value);
}

function setModelOrderSort(isAsc: boolean) {
  sortOrderAsc.value = isAsc;
  modelList.value = [...modelList.value].sort((a, b) => {
    const left = Number(a.modelOrder || 0);
    const right = Number(b.modelOrder || 0);
    return sortOrderAsc.value ? left - right : right - left;
  });
}

function cancel() {
  open.value = false;
  reset();
}

function reset() {
  form.value = {
    templateId: null,
    templateName: null,
    userId: null,
    userName: null,
    tenantId: null,
    tenantName: null,
    identifier: null,
    modelOrder: 0,
    type: 1,
    datatype: 'integer',
    isSys: null,
    isChart: 1,
    isHistory: 1,
    isSharePerm: 1,
    isMonitor: 1,
    isReadonly: 1,
    delFlag: null,
    createBy: null,
    createTime: null,
    updateBy: null,
    updateTime: null,
    remark: null,
    modelCount: 5,
    modelName_en_US: null,
    specs: { enumList: [{ value: '', text: '' }], arrayType: 'integer', arrayCount: 5, showWay: 'select', params: [] },
  };
  proxy?.resetForm(formRef.value);
}

function handleQuery() {
  queryParams.pageNum = 1;
  queryParams.action = '';
  queryParams.modelId = '';
  queryParams.upModelId = '';
  queryParams.downModelId = '';
  getList();
}

function resetQuery() {
  proxy?.resetForm(queryFormRef.value);
  queryParams.action = '';
  queryParams.modelId = '';
  queryParams.upModelId = '';
  queryParams.downModelId = '';
  handleQuery();
}

function handleAdd() {
  reset();
  modelCount.value = 5;
  arrayModelList.value = Array.from({ length: modelCount.value }, (_, index) => index);
  open.value = true;
  title.value = proxy?.$t('product.product-things-model.142341-99');
}

function handleUpdate(row: any) {
  reset();
  const modelId = row.modelId;
  getModel(modelId).then((response: any) => {
    let tempForm = response.data;
    open.value = true;
    title.value = proxy?.$t('product.product-things-model.142341-100');
    tempForm.specs = JSON.parse(tempForm.specs);
    if (!tempForm.specs.enumList) {
      tempForm.specs.showWay = 'select';
      tempForm.specs.enumList = [{ value: '', text: '' }];
    }
    if (!tempForm.specs.arrayType) tempForm.specs.arrayType = 'integer';
    if (!tempForm.specs.arrayCount) tempForm.specs.arrayCount = 5;
    if (!tempForm.specs.params) tempForm.specs.params = [];
    if ((tempForm.specs.type == 'array' && tempForm.specs.arrayType == 'object') || tempForm.specs.type == 'object') {
      for (let i = 0; i < tempForm.specs.params.length; i++) {
        tempForm.specs.params[i].id = String(tempForm.specs.params[i].id).substring(
          String(tempForm.identifier).length + 1
        );
      }
    }
    form.value = tempForm;
  });
}

function handleSelectionChange(selection: any[]) {
  ids.value = selection.map((item) => item.modelId);
  multiple.value = !selection.length;
}

function handleOpenThingsModel() {
  title.value = proxy?.$t('product.product-things-model.142341-101');
  const tm: any = { properties: [], functions: [], events: [] };
  for (let i = 0; i < modelList.value.length; i++) {
    let thingsItem: any = {};
    thingsItem.id = modelList.value[i].identifier;
    thingsItem.name = modelList.value[i].modelName;
    if (modelList.value[i].type == 1) {
      thingsItem.isChart = modelList.value[i].isChart;
      thingsItem.isMonitor = modelList.value[i].isMonitor;
      thingsItem.isHistory = modelList.value[i].isHistory;
      thingsItem.isSharePerm = modelList.value[i].isSharePerm;
      thingsItem.isReadonly = modelList.value[i].isReadonly;
      thingsItem.datatype = JSON.parse(modelList.value[i].specs);
      tm.properties.push(thingsItem);
    } else if (modelList.value[i].type == 2) {
      thingsItem.isHistory = modelList.value[i].isHistory;
      thingsItem.isSharePerm = modelList.value[i].isSharePerm;
      thingsItem.isReadonly = modelList.value[i].isReadonly;
      thingsItem.datatype = JSON.parse(modelList.value[i].specs);
      tm.functions.push(thingsItem);
    } else if (modelList.value[i].type == 3) {
      thingsItem.isHistory = modelList.value[i].isHistory;
      thingsItem.isSharePerm = modelList.value[i].isSharePerm;
      thingsItem.isReadonly = modelList.value[i].isReadonly;
      thingsItem.datatype = JSON.parse(modelList.value[i].specs);
      tm.events.push(thingsItem);
    }
  }
  thingsModel.value = tm;
  openThingsModel.value = true;
}

function handleCloseThingsModel() {
  openThingsModel.value = false;
}

function handleSelect() {
  openSelect.value = true;
  title.value = proxy?.$t('product.product-things-model.142341-1');
  form.value.type = 1;
  form.value.datatype = 'integer';
  form.value.specs = { enumList: [] };
}

function cancelSelect() {
  openSelect.value = false;
  productSelectTemplateRef.value?.$refs?.selectTemplateTable?.clearSelection();
}

function getChildData(data: any[]) {
  templateIds.value = data;
}

function importSelect() {
  if (templateIds.value != null && templateIds.value.length > 0) {
    const importData = {
      productId: productInfo.value.productId,
      productName: productInfo.value.productName,
      templateIds: templateIds.value,
    };
    importModel(importData).then((response: any) => {
      const msg = response.msg || '';
      const isFail = msg.includes('未') || msg.includes('失败');
      if (isFail) {
        proxy?.$modal.msgError(msg);
      } else {
        proxy?.$modal.msgSuccess(msg);
      }
      openSelect.value = false;
      productSelectTemplateRef.value?.$refs?.selectTemplateTable?.clearSelection();
      getList();
      emit('updateModel');
    });
  }
}

function submitForm() {
  formRef.value?.validate((valid: boolean) => {
    if (valid) {
      if (
        form.value.datatype == 'object' ||
        (form.value.datatype == 'array' && form.value.specs.arrayType == 'object')
      ) {
        if (!form.value.specs.params || form.value.specs.params.length == 0) {
          proxy?.$modal.msgError(proxy?.$t('product.product-things-model.142341-102'));
          return;
        }
      }
      if (form.value.specs.params && form.value.specs.params.length > 0) {
        let arr = form.value.specs.params.map((item: any) => item.id).sort();
        for (let i = 0; i < arr.length; i++) {
          if (arr[i] == arr[i + 1]) {
            proxy?.$modal.msgError('参数标识 ' + arr[i] + ' 重复');
            return;
          }
        }
      }
      if (form.value.isChart == 1 && form.value.datatype != 'integer' && form.value.datatype != 'decimal') {
        proxy?.$modal.msgError(proxy?.$t('product.product-things-model.142341-106'));
        return;
      }
      if (form.value.modelId != null) {
        let tempForm = JSON.parse(JSON.stringify(form.value));
        tempForm.specs = formatThingsSpecs();
        if (form.value.type == 2) {
          tempForm.isMonitor = 0;
          tempForm.isChart = 0;
        } else if (form.value.type == 3) {
          tempForm.isMonitor = 0;
          tempForm.isChart = 0;
        }
        updateModel(tempForm).then(() => {
          proxy?.$modal.msgSuccess(proxy?.$t('product.product-things-model.142341-107'));
          open.value = false;
          getList();
          emit('updateModel');
        });
      } else {
        let tempForm = JSON.parse(JSON.stringify(form.value));
        tempForm.specs = formatThingsSpecs();
        tempForm.productId = productInfo.value.productId;
        tempForm.productName = productInfo.value.productName;
        if (form.value.type == 2) tempForm.isMonitor = 0;
        else if (form.value.type == 3) {
          tempForm.isMonitor = 0;
          tempForm.isChart = 0;
        }
        addModel(tempForm).then(() => {
          proxy?.$modal.msgSuccess(proxy?.$t('product.product-things-model.142341-108'));
          open.value = false;
          getList();
          emit('updateModel');
        });
      }
    }
  });
}

function handleDelete(row?: any) {
  const modelIds = row?.modelId || ids.value;
  proxy?.$modal
    .confirm(proxy?.$t('product.product-things-model.142341-109', [modelIds]))
    .then(() => delModel(modelIds))
    .then(() => {
      getList();
      proxy?.$modal.msgSuccess(proxy?.$t('product.product-things-model.142341-111'));
    })
    .catch(() => {});
}

function handleExport() {
  proxy?.download('iot/model/export', { ...queryParams }, `model_${new Date().getTime()}.xlsx`);
}

function typeChange(type: number) {
  if (type == 1) {
    form.value.isChart = 1;
    form.value.isHistory = 1;
    form.value.isSharePerm = 1;
    form.value.isMonitor = 1;
    form.value.isReadonly = 1;
    form.value.datatype = 'integer';
  } else if (type == 2) {
    form.value.isChart = 0;
    form.value.isHistory = 1;
    form.value.isSharePerm = 1;
    form.value.isMonitor = 0;
    form.value.isReadonly = 0;
  } else if (type == 3) {
    form.value.isChart = 0;
    form.value.isHistory = 1;
    form.value.isSharePerm = 0;
    form.value.isMonitor = 0;
    form.value.isReadonly = 1;
  }
}

function isChartChange() {
  if (form.value.isChart == 1) form.value.isReadonly = 1;
  else form.value.isMonitor = 0;
}

function isMonitorChange() {
  if (form.value.isMonitor == 1) {
    form.value.isReadonly = 1;
    form.value.isChart = 1;
  }
}

function isReadonlyChange() {
  if (form.value.isReadonly == 0) {
    form.value.isMonitor = 0;
    form.value.isChart = 0;
  }
}

function formatThingsSpecs() {
  const data: any = {};
  data.type = form.value.datatype;
  if (form.value.datatype == 'integer' || form.value.datatype == 'decimal') {
    data.min = Number(form.value.specs.min || 0);
    data.max = Number(form.value.specs.max || 100);
    data.unit = form.value.specs.unit || '';
    data.step = Number(form.value.specs.step || 1);
  } else if (form.value.datatype == 'string') {
    data.maxLength = Number(form.value.specs.maxLength || 1024);
  } else if (form.value.datatype == 'bool') {
    data.falseText = form.value.specs.falseText || proxy?.$t('close');
    data.trueText = form.value.specs.trueText || proxy?.$t('open');
  } else if (form.value.datatype == 'enum') {
    data.showWay = form.value.specs.showWay;
    if (form.value.specs.enumList && form.value.specs.enumList[0].text != '') {
      data.enumList = form.value.specs.enumList;
    } else {
      data.showWay = 'select';
      data.enumList = [
        { value: '0', text: proxy?.$t('product.product-things-model.142341-115') },
        { value: '1', text: proxy?.$t('product.product-things-model.142341-116') },
      ];
    }
  } else if (form.value.datatype == 'array') {
    data.arrayIndex = arrayModelList.value.map((item) => Number(item));
    data.arrayType = form.value.specs.arrayType;
    data.arrayCount = form.value.specs.arrayCount || 5;
    if (data.arrayType == 'object') {
      data.params = form.value.specs.params;
      for (let i = 0; i < data.params.length; i++) {
        data.params[i].id = form.value.identifier + '_' + data.params[i].id;
      }
    }
  } else if (form.value.datatype == 'object') {
    data.params = form.value.specs.params;
    for (let i = 0; i < data.params.length; i++) {
      data.params[i].id = form.value.identifier + '_' + data.params[i].id;
    }
  }
  const { parentName, parentType, parentldentifier, parentIdentifier } = form.value.specs;
  if (parentName) data.parentName = parentName;
  if (parentType) data.parentType = parentType;
  if (parentldentifier) data.parentldentifier = parentldentifier;
  if (parentIdentifier) data.parentIdentifier = parentIdentifier;
  return JSON.stringify(data);
}

function dataTypeChange(val: any) {}

function addEnumItem() {
  form.value.specs.enumList.push({ value: '', text: '' });
}

function removeEnumItem(index: number | string) {
  form.value.specs.enumList.splice(index, 1);
}

function formatSpecsDisplay(json: string) {
  if (json == null || json == undefined) return;
  let specs = JSON.parse(json);
  if (specs.type === 'integer' || specs.type === 'decimal') {
    return (
      `<div style='display:inline-block;'>${proxy?.$t('template.index.891112-105')}<span style="color:#F56C6C">${specs.max}</span></div>，` +
      `<div style='display:inline-block;'>${proxy?.$t('template.index.891112-106')}<span style="color:#F56C6C">${specs.min}</span></div><br />` +
      `<div style='display:inline-block;'>${proxy?.$t('template.index.891112-107')}<span style="color:#F56C6C">${specs.step}</span></div>，` +
      `<div style='display:inline-block;'>${proxy?.$t('template.index.891112-108')}<span style="color:#F56C6C">${specs.unit || '无'}</span></div>`
    );
  } else if (specs.type === 'string') {
    return '最大长度：<span style="color:#F56C6C">' + specs.maxLength + '</span>';
  } else if (specs.type === 'array') {
    return (
      `<div style='display:inline-block;'>数组类型：<span style="color:#F56C6C">${specs.arrayType}</span></div>，` +
      `<div style='display:inline-block;'>元素个数：<span style="color:#F56C6C">${specs.arrayCount}</span></div>`
    );
  } else if (specs.type === 'enum') {
    let items = '';
    for (let i = 0; i < specs.enumList.length; i++) {
      items +=
        "<div style='display:inline-block;'>" +
        specs.enumList[i].value +
        "：<span style='color:#F56C6C'>" +
        specs.enumList[i].text +
        '</span></div>';
      if (i !== specs.enumList.length - 1) items += '，';
      if (i > 0 && i % 2 != 0) items += '<br />';
    }
    return items;
  } else if (specs.type === 'bool') {
    return (
      `<div style='display:inline-block;'>0：<span style="color:#F56C6C">${specs.falseText}</span></div>，` +
      `<div style='display:inline-block;'>1：<span style="color:#F56C6C">${specs.trueText}</span></div>`
    );
  } else if (specs.type === 'object') {
    let items = '';
    for (let i = 0; i < specs.params.length; i++) {
      items +=
        "<div style='display:inline-block;'>" +
        specs.params[i].name +
        "：<span style='color:#F56C6C'>" +
        specs.params[i].datatype.type +
        '</span></div>';
      if (i !== specs.params.length - 1) items += '，';
      if (i > 0 && i % 2 !== 0) items += '<br />';
    }
    return items;
  }
}

function addParameter() {
  paramData.value = { index: -1, parameter: {} };
}

function editParameter(data: any, index: number | string) {
  const idx = Number(index);
  paramData.value = null;
  paramData.value = { index: idx, parameter: data };
}

function removeParameter(index: number | string) {
  form.value.specs.params.splice(Number(index), 1);
}

function getParamData(data: any) {
  if (data.index == -1) {
    form.value.specs.params.push(data.parameter);
  } else {
    form.value.specs.params[data.index] = data.parameter;
    form.value.specs.params = [...form.value.specs.params];
  }
}

function handleImport() {
  importBatchRef.value?.upload && (importBatchRef.value.upload.importDeviceDialog = true);
}

function handleSyncThingsModel() {
  const pId = productInfo.value.productId;
  syncModel(pId).then((response: any) => {
    if (response.code == 200) {
      proxy?.$modal.msgSuccess(response.msg);
      getList();
    } else {
      proxy?.$modal.msgError(response.msg);
    }
  });
}

function checkInput() {
  if (form.value.specs.arrayCount > 1000) form.value.specs.arrayCount = 1000;
  else if (form.value.specs.arrayCount < 0) form.value.specs.arrayCount = 0;
}

function toggleSortMode() {
  isSortMode.value = !isSortMode.value;
}

function moveTop(row: any, index: number) {
  if (index > 0) {
    const item = modelList.value.splice(index, 1)[0];
    modelList.value.unshift(item);
    queryParams.action = 0;
    queryParams.modelId = row.modelId;
    queryParams.downModelId = '';
    queryParams.upModelId = '';
    getList();
  } else {
    ElMessage.warning(proxy?.$t('product.product-things-model.142341-140'));
  }
}

function moveUp(row: any, index: number) {
  if (index > 0) {
    const upModelId = modelList.value[index - 1].modelId;
    const temp = modelList.value[index];
    modelList.value[index] = modelList.value[index - 1];
    modelList.value[index - 1] = temp;
    queryParams.upModelId = upModelId;
    queryParams.action = 1;
    queryParams.modelId = row.modelId;
    queryParams.downModelId = '';
    getList();
  } else {
    ElMessage.warning(proxy?.$t('product.product-things-model.142341-141'));
  }
}

function moveDown(row: any, index: number) {
  if (index < modelList.value.length - 1) {
    const downModelId = modelList.value[index + 1].modelId;
    const temp = modelList.value[index];
    modelList.value[index] = modelList.value[index + 1];
    modelList.value[index + 1] = temp;
    queryParams.downModelId = downModelId;
    queryParams.action = 2;
    queryParams.modelId = row.modelId;
    queryParams.upModelId = '';
    getList();
  } else {
    ElMessage.warning(proxy?.$t('product.product-things-model.142341-142'));
  }
}

defineExpose({
  getList,
});
</script>

<style lang="scss" scoped>
.el-tag + .el-tag {
  margin-left: 10px;
}
.button-new-tag {
  margin-left: 10px;
  height: 32px;
  line-height: 30px;
  padding-top: 0;
  padding-bottom: 0;
}
.input-new-tag {
  width: 90px;
  margin-left: 10px;
  vertical-align: bottom;
}
:deep(.custom-input .el-input__inner) {
  border: 1px solid #dcdfe6;
  border-radius: 4px;
  padding: 5px;
}

.action-toolbar {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 0;

  .action-toolbar-left {
    display: flex;
    align-items: center;
    flex-wrap: wrap;
    gap: 8px;
  }

  .action-item {
    display: inline-flex;
    align-items: center;
  }

  .action-tip {
    margin-left: 6px;
    display: inline-flex;
    align-items: center;
  }

  .action-toolbar-right {
    margin-left: 10px;
  }
}

.sort-header {
  display: inline-flex;
  align-items: center;
  justify-content: center;
  gap: 1px;
  cursor: pointer;
  white-space: nowrap;
}

.sort-icons {
  display: inline-flex;
  flex-direction: column;
  line-height: 1;
  gap: 0;
  margin-left: 0;
}

.sort-icon {
  display: block;
  height: 9px;
  line-height: 8px;
  font-size: 10px;
  color: #c0c4cc;
  margin: 0;
  padding: 0;
  cursor: pointer;

  &.sort-down {
    margin-top: -1px;
  }

  &.active {
    color: #486ff2;
  }
}

.things-table :deep(.el-table__header th.el-table__cell) {
  background: #fff;
}

.things-table :deep(.el-table__fixed-right) {
  box-shadow: none;
}
</style>
