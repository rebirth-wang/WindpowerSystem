<template>
  <div>
    <el-tabs type="border-card" class="tabs-wrap">
      <el-tab-pane :label="$t('components.Crontab.index.464657-0')" v-if="shouldHide('second')">
        <CrontabSecond @update="updateCrontabValue" :check="checkNumber" :cron="crontabValueObj" ref="cronsecond" />
      </el-tab-pane>
      <el-tab-pane :label="$t('components.Crontab.index.464657-1')" v-if="shouldHide('min')">
        <CrontabMin @update="updateCrontabValue" :check="checkNumber" :cron="crontabValueObj" ref="cronmin" />
      </el-tab-pane>
      <el-tab-pane :label="$t('components.Crontab.index.464657-2')" v-if="shouldHide('hour')">
        <CrontabHour @update="updateCrontabValue" :check="checkNumber" :cron="crontabValueObj" ref="cronhour" />
      </el-tab-pane>
      <el-tab-pane :label="$t('components.Crontab.index.464657-3')" v-if="shouldHide('day')">
        <CrontabDay @update="updateCrontabValue" :check="checkNumber" :cron="crontabValueObj" ref="cronday" />
      </el-tab-pane>
      <el-tab-pane :label="$t('components.Crontab.index.464657-4')" v-if="shouldHide('month')">
        <CrontabMonth @update="updateCrontabValue" :check="checkNumber" :cron="crontabValueObj" ref="cronmonth" />
      </el-tab-pane>
      <el-tab-pane :label="$t('components.Crontab.index.464657-5')" v-if="shouldHide('week')">
        <CrontabWeek @update="updateCrontabValue" :check="checkNumber" :cron="crontabValueObj" ref="cronweek" />
      </el-tab-pane>
      <el-tab-pane :label="$t('components.Crontab.index.464657-6')" v-if="shouldHide('year')">
        <CrontabYear @update="updateCrontabValue" :check="checkNumber" :cron="crontabValueObj" ref="cronyear" />
      </el-tab-pane>
    </el-tabs>

    <div class="popup-main">
      <div class="popup-result">
        <p class="title">{{ $t('components.Crontab.index.464657-7') }}</p>
        <table>
          <thead>
            <th v-for="item of tabTitles" width="40" :key="item">{{ item }}</th>
            <th>{{ $t('components.Crontab.index.464657-8') }}</th>
          </thead>
          <tbody>
            <td>
              <span>{{ crontabValueObj.second }}</span>
            </td>
            <td>
              <span>{{ crontabValueObj.min }}</span>
            </td>
            <td>
              <span>{{ crontabValueObj.hour }}</span>
            </td>
            <td>
              <span>{{ crontabValueObj.day }}</span>
            </td>
            <td>
              <span>{{ crontabValueObj.month }}</span>
            </td>
            <td>
              <span>{{ crontabValueObj.week }}</span>
            </td>
            <td>
              <span>{{ crontabValueObj.year }}</span>
            </td>
            <td>
              <span>{{ crontabValueString }}</span>
            </td>
          </tbody>
        </table>
      </div>
      <CrontabResult :ex="crontabValueString" />
      <div class="pop_btn">
        <el-button size="small" type="primary" @click="submitFill">
          {{ $t('components.Crontab.index.464657-9') }}
        </el-button>
        <el-button size="small" type="warning" @click="clearCron">
          {{ $t('components.Crontab.index.464657-10') }}
        </el-button>
        <el-button size="small" @click="hidePopup">{{ $t('components.Crontab.index.464657-11') }}</el-button>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, computed, watch, onMounted } from 'vue';
import { useI18n } from 'vue-i18n';
import CrontabSecond from './second.vue';
import CrontabMin from './min.vue';
import CrontabHour from './hour.vue';
import CrontabDay from './day.vue';
import CrontabMonth from './month.vue';
import CrontabWeek from './week.vue';
import CrontabYear from './year.vue';
import CrontabResult from './result.vue';

const { t } = useI18n();
const props = defineProps<{ expression?: string; hideComponent?: string[] }>();
const emit = defineEmits(['hide', 'fill']);

const cronsecond = ref();
const cronmin = ref();
const cronhour = ref();
const cronday = ref();
const cronmonth = ref();
const cronweek = ref();
const cronyear = ref();

const tabTitles = [
  t('components.Crontab.index.464657-0'),
  t('components.Crontab.index.464657-1'),
  t('components.Crontab.index.464657-2'),
  t('components.Crontab.index.464657-3'),
  t('components.Crontab.index.464657-4'),
  t('components.Crontab.index.464657-5'),
  t('components.Crontab.index.464657-6'),
];

const crontabValueObj = ref({
  second: '*',
  min: '*',
  hour: '*',
  day: '*',
  month: '*',
  week: '?',
  year: '',
});

const crontabValueString = computed(() => {
  const obj = crontabValueObj.value;
  return (
    obj.second +
    ' ' +
    obj.min +
    ' ' +
    obj.hour +
    ' ' +
    obj.day +
    ' ' +
    obj.month +
    ' ' +
    obj.week +
    (obj.year === '' ? '' : ' ' + obj.year)
  );
});

function shouldHide(key: string) {
  if (props.hideComponent && props.hideComponent.includes(key)) return false;
  return true;
}

function resolveExp() {
  if (props.expression) {
    const arr = props.expression.split(' ');
    if (arr.length >= 6) {
      const obj = {
        second: arr[0],
        min: arr[1],
        hour: arr[2],
        day: arr[3],
        month: arr[4],
        week: arr[5],
        year: arr[6] ? arr[6] : '',
      };
      crontabValueObj.value = { ...obj };
      for (const i in obj) {
        if ((obj as any)[i]) changeRadio(i, (obj as any)[i]);
      }
    }
  } else {
    clearCron();
  }
}

function updateCrontabValue(name: string, value: string, from?: string) {
  (crontabValueObj.value as any)[name] = value;
  if (from && from !== name) {
    changeRadio(name, value);
  }
}

function changeRadio(name: string, value: string) {
  const arr = ['second', 'min', 'hour', 'month'];
  const refName = 'cron' + name;
  const refs: Record<string, any> = { cronsecond, cronmin, cronhour, cronday, cronmonth, cronweek, cronyear };
  const refObj = refs[refName]?.value;
  if (!refObj) return;

  let insValue: number | undefined;

  if (arr.includes(name)) {
    if (value === '*') insValue = 1;
    else if (value.indexOf('-') > -1) {
      const idxArr = value.split('-');
      refObj.cycle01 = isNaN(Number(idxArr[0])) ? 0 : Number(idxArr[0]);
      refObj.cycle02 = Number(idxArr[1]);
      insValue = 2;
    } else if (value.indexOf('/') > -1) {
      const idxArr = value.split('/');
      refObj.average01 = isNaN(Number(idxArr[0])) ? 0 : Number(idxArr[0]);
      refObj.average02 = Number(idxArr[1]);
      insValue = 3;
    } else {
      insValue = 4;
      refObj.checkboxList = value.split(',');
    }
  } else if (name === 'day') {
    if (value === '*') insValue = 1;
    else if (value === '?') insValue = 2;
    else if (value.indexOf('-') > -1) {
      const idxArr = value.split('-');
      refObj.cycle01 = isNaN(Number(idxArr[0])) ? 0 : Number(idxArr[0]);
      refObj.cycle02 = Number(idxArr[1]);
      insValue = 3;
    } else if (value.indexOf('/') > -1) {
      const idxArr = value.split('/');
      refObj.average01 = isNaN(Number(idxArr[0])) ? 0 : Number(idxArr[0]);
      refObj.average02 = Number(idxArr[1]);
      insValue = 4;
    } else if (value.indexOf('W') > -1) {
      refObj.workday = isNaN(Number(value.split('W')[0])) ? 0 : Number(value.split('W')[0]);
      insValue = 5;
    } else if (value === 'L') insValue = 6;
    else {
      refObj.checkboxList = value.split(',');
      insValue = 7;
    }
  } else if (name === 'week') {
    if (value === '*') insValue = 1;
    else if (value === '?') insValue = 2;
    else if (value.indexOf('-') > -1) {
      const idxArr = value.split('-');
      refObj.cycle01 = isNaN(Number(idxArr[0])) ? 0 : Number(idxArr[0]);
      refObj.cycle02 = Number(idxArr[1]);
      insValue = 3;
    } else if (value.indexOf('#') > -1) {
      const idxArr = value.split('#');
      refObj.average01 = isNaN(Number(idxArr[0])) ? 1 : Number(idxArr[0]);
      refObj.average02 = Number(idxArr[1]);
      insValue = 4;
    } else if (value.indexOf('L') > -1) {
      refObj.weekday = isNaN(Number(value.split('L')[0])) ? 1 : Number(value.split('L')[0]);
      insValue = 5;
    } else {
      refObj.checkboxList = value.split(',');
      insValue = 6;
    }
  } else if (name === 'year') {
    if (value === '') insValue = 1;
    else if (value === '*') insValue = 2;
    else if (value.indexOf('-') > -1) insValue = 3;
    else if (value.indexOf('/') > -1) insValue = 4;
    else {
      refObj.checkboxList = value.split(',');
      insValue = 5;
    }
  }
  if (insValue !== undefined) refObj.radioValue = insValue;
}

function checkNumber(value: number, minLimit: number, maxLimit: number) {
  value = Math.floor(value);
  if (value < minLimit) value = minLimit;
  else if (value > maxLimit) value = maxLimit;
  return value;
}

function hidePopup() {
  emit('hide');
}

function submitFill() {
  emit('fill', crontabValueString.value);
  hidePopup();
}

function clearCron() {
  crontabValueObj.value = { second: '*', min: '*', hour: '*', day: '*', month: '*', week: '?', year: '' };
  for (const j in crontabValueObj.value) {
    changeRadio(j, (crontabValueObj.value as any)[j]);
  }
}

watch(() => props.expression, resolveExp);
onMounted(() => resolveExp());
</script>

<style scoped>
.pop_btn {
  text-align: center;
  margin-top: 20px;
}
.tabs-wrap {
  box-shadow: unset;
  border: 1px solid #ccc;
}
.popup-main {
  position: relative;
  margin: 10px auto;
  background: #fff;
  border-radius: 5px;
  font-size: 12px;
  overflow: hidden;
}
.popup-result {
  box-sizing: border-box;
  line-height: 24px;
  margin: 25px auto;
  padding: 15px 10px 10px;
  border: 1px solid #ccc;
  position: relative;
}
.popup-result .title {
  position: absolute;
  top: -28px;
  left: 50%;
  width: 140px;
  font-size: 14px;
  margin-left: -70px;
  text-align: center;
  line-height: 30px;
  background: #fff;
}
.popup-result table {
  text-align: center;
  width: 100%;
  margin: 0 auto;
}
.popup-result table span {
  display: block;
  width: 100%;
  font-family: arial;
  line-height: 30px;
  height: 30px;
  white-space: nowrap;
  overflow: hidden;
  border: 1px solid #e8e8e8;
}
.popup-result-scroll {
  font-size: 12px;
  line-height: 24px;
  height: 10em;
  overflow-y: auto;
}
</style>
