<template>
  <div class="popup-result">
    <p class="title">{{ $t('components.result.893023-0') }}</p>
    <ul class="popup-result-scroll">
      <template v-if="isShow">
        <li v-for="item in resultList" :key="item">{{ item }}</li>
      </template>
      <li v-else>{{ $t('components.result.893023-1') }}</li>
    </ul>
  </div>
</template>

<script setup lang="ts">
import { ref, watch, onMounted } from 'vue';
import { useI18n } from 'vue-i18n';

const { t } = useI18n();
const props = defineProps<{ ex: string }>();

const dayRule = ref('');
const dayRuleSup = ref<any>('');
const dateArr = ref<any[]>([]);
const resultList = ref<string[]>([]);
const isShow = ref(false);

function getOrderArr(min: number, max: number) {
  const arr: number[] = [];
  for (let i = min; i <= max; i++) arr.push(i);
  return arr;
}

function getAssignArr(rule: string) {
  return rule
    .split(',')
    .map(Number)
    .sort((a, b) => a - b);
}

function getAverageArr(rule: string, limit: number) {
  const arr: number[] = [];
  const agArr = rule.split('/');
  let min = Number(agArr[0]);
  const step = Number(agArr[1]);
  while (min <= limit) {
    arr.push(min);
    min += step;
  }
  return arr;
}

function getCycleArr(rule: string, limit: number, status: boolean) {
  const arr: number[] = [];
  const cycleArr = rule.split('-');
  let min = Number(cycleArr[0]);
  let max = Number(cycleArr[1]);
  if (min > max) max += limit;
  for (let i = min; i <= max; i++) {
    let add = 0;
    if (!status && i % limit === 0) add = limit;
    arr.push(Math.round((i % limit) + add));
  }
  return arr.sort((a, b) => a - b);
}

function compare(v1: number, v2: number) {
  return v1 - v2 > 0 ? 1 : -1;
}

function formatDate(value: any, type?: string) {
  const time = typeof value === 'number' ? new Date(value) : value;
  const Y = time.getFullYear();
  const M = time.getMonth() + 1;
  const D = time.getDate();
  const h = time.getHours();
  const m = time.getMinutes();
  const s = time.getSeconds();
  const week = time.getDay();
  if (type === undefined) {
    return (
      Y +
      '-' +
      (M < 10 ? '0' + M : M) +
      '-' +
      (D < 10 ? '0' + D : D) +
      ' ' +
      (h < 10 ? '0' + h : h) +
      ':' +
      (m < 10 ? '0' + m : m) +
      ':' +
      (s < 10 ? '0' + s : s)
    );
  } else if (type === 'week') {
    return week + 1;
  }
}

function checkDate(value: string) {
  const time = new Date(value);
  return value === formatDate(time);
}

function getIndex(arr: number[], value: number) {
  if (value <= arr[0] || value > arr[arr.length - 1]) return 0;
  for (let i = 0; i < arr.length - 1; i++) {
    if (value > arr[i] && value <= arr[i + 1]) return i + 1;
  }
  return 0;
}

function getSecondArr(rule: string) {
  dateArr.value[0] = getOrderArr(0, 59);
  if (rule.indexOf('-') >= 0) dateArr.value[0] = getCycleArr(rule, 60, true);
  else if (rule.indexOf('/') >= 0) dateArr.value[0] = getAverageArr(rule, 59);
  else if (rule !== '*') dateArr.value[0] = getAssignArr(rule);
}
function getMinArr(rule: string) {
  dateArr.value[1] = getOrderArr(0, 59);
  if (rule.indexOf('-') >= 0) dateArr.value[1] = getCycleArr(rule, 60, true);
  else if (rule.indexOf('/') >= 0) dateArr.value[1] = getAverageArr(rule, 59);
  else if (rule !== '*') dateArr.value[1] = getAssignArr(rule);
}
function getHourArr(rule: string) {
  dateArr.value[2] = getOrderArr(0, 23);
  if (rule.indexOf('-') >= 0) dateArr.value[2] = getCycleArr(rule, 24, true);
  else if (rule.indexOf('/') >= 0) dateArr.value[2] = getAverageArr(rule, 23);
  else if (rule !== '*') dateArr.value[2] = getAssignArr(rule);
}
function getDayArr(rule: string) {
  dateArr.value[3] = getOrderArr(1, 31);
  dayRule.value = '';
  dayRuleSup.value = '';
  if (rule.indexOf('-') >= 0) {
    dateArr.value[3] = getCycleArr(rule, 31, false);
    dayRuleSup.value = 'null';
  } else if (rule.indexOf('/') >= 0) {
    dateArr.value[3] = getAverageArr(rule, 31);
    dayRuleSup.value = 'null';
  } else if (rule.indexOf('W') >= 0) {
    dayRule.value = 'workDay';
    dayRuleSup.value = Number(rule.match(/[0-9]{1,2}/g)?.[0]);
    dateArr.value[3] = [dayRuleSup.value];
  } else if (rule.indexOf('L') >= 0) {
    dayRule.value = 'lastDay';
    dayRuleSup.value = 'null';
    dateArr.value[3] = [31];
  } else if (rule !== '*' && rule !== '?') {
    dateArr.value[3] = getAssignArr(rule);
    dayRuleSup.value = 'null';
  } else if (rule === '*') {
    dayRuleSup.value = 'null';
  }
}
function getMonthArr(rule: string) {
  dateArr.value[4] = getOrderArr(1, 12);
  if (rule.indexOf('-') >= 0) dateArr.value[4] = getCycleArr(rule, 12, false);
  else if (rule.indexOf('/') >= 0) dateArr.value[4] = getAverageArr(rule, 12);
  else if (rule !== '*') dateArr.value[4] = getAssignArr(rule);
}
function getWeekArr(rule: string) {
  if (dayRule.value === '' && dayRuleSup.value === '') {
    if (rule.indexOf('-') >= 0) {
      dayRule.value = 'weekDay';
      dayRuleSup.value = getCycleArr(rule, 7, false);
    } else if (rule.indexOf('#') >= 0) {
      dayRule.value = 'assWeek';
      const matchRule = rule.match(/[0-9]{1}/g) || [];
      dayRuleSup.value = [Number(matchRule[1]), Number(matchRule[0])];
      dateArr.value[3] = [1];
      if (dayRuleSup.value[1] === 7) dayRuleSup.value[1] = 0;
    } else if (rule.indexOf('L') >= 0) {
      dayRule.value = 'lastWeek';
      dayRuleSup.value = Number((rule.match(/[0-9]{1,2}/g) || [])[0]);
      dateArr.value[3] = [31];
      if (dayRuleSup.value === 7) dayRuleSup.value = 0;
    } else if (rule !== '*' && rule !== '?') {
      dayRule.value = 'weekDay';
      dayRuleSup.value = getAssignArr(rule);
    }
  }
}
function getYearArr(rule: string | undefined, year: number) {
  dateArr.value[5] = getOrderArr(year, year + 100);
  if (rule !== undefined) {
    if (rule.indexOf('-') >= 0) dateArr.value[5] = getCycleArr(rule, year + 100, false);
    else if (rule.indexOf('/') >= 0) dateArr.value[5] = getAverageArr(rule, year + 100);
    else if (rule !== '*') dateArr.value[5] = getAssignArr(rule);
  }
}

function expressionChange() {
  isShow.value = false;
  const ruleArr = props.ex.split(' ');
  let nums = 0;
  const resultArr: string[] = [];
  const nTime = new Date();
  let nYear = nTime.getFullYear();
  let nMonth = nTime.getMonth() + 1;
  let nDay = nTime.getDate();
  let nHour = nTime.getHours();
  let nMin = nTime.getMinutes();
  let nSecond = nTime.getSeconds();

  getSecondArr(ruleArr[0]);
  getMinArr(ruleArr[1]);
  getHourArr(ruleArr[2]);
  getDayArr(ruleArr[3]);
  getMonthArr(ruleArr[4]);
  getWeekArr(ruleArr[5]);
  getYearArr(ruleArr[6], nYear);

  const sDate = dateArr.value[0],
    mDate = dateArr.value[1],
    hDate = dateArr.value[2];
  const DDate = dateArr.value[3],
    MDate = dateArr.value[4],
    YDate = dateArr.value[5];

  let sIdx = getIndex(sDate, nSecond),
    mIdx = getIndex(mDate, nMin);
  let hIdx = getIndex(hDate, nHour),
    DIdx = getIndex(DDate, nDay);
  let MIdx = getIndex(MDate, nMonth),
    YIdx = getIndex(YDate, nYear);

  const resetSecond = () => {
    sIdx = 0;
    nSecond = sDate[sIdx];
  };
  const resetMin = () => {
    mIdx = 0;
    nMin = mDate[mIdx];
    resetSecond();
  };
  const resetHour = () => {
    hIdx = 0;
    nHour = hDate[hIdx];
    resetMin();
  };
  const resetDay = () => {
    DIdx = 0;
    nDay = DDate[DIdx];
    resetHour();
  };
  const resetMonth = () => {
    MIdx = 0;
    nMonth = MDate[MIdx];
    resetDay();
  };

  if (nYear !== YDate[YIdx]) resetMonth();
  if (nMonth !== MDate[MIdx]) resetDay();
  if (nDay !== DDate[DIdx]) resetHour();
  if (nHour !== hDate[hIdx]) resetMin();
  if (nMin !== mDate[mIdx]) resetSecond();

  // Simplified calculation - show up to 5 next execution times
  let count = 0;
  const maxIterations = 100000;
  let iterations = 0;

  for (let Yi = YIdx; Yi < YDate.length && count < 5 && iterations < maxIterations; Yi++) {
    const YY = YDate[Yi];
    for (let Mi = Yi === YIdx ? MIdx : 0; Mi < MDate.length && count < 5 && iterations < maxIterations; Mi++) {
      let MM: any = MDate[Mi];
      MM = MM < 10 ? '0' + MM : MM;
      for (
        let Di = Mi === (Yi === YIdx ? MIdx : 0) && Yi === YIdx ? DIdx : 0;
        Di < DDate.length && count < 5 && iterations < maxIterations;
        Di++
      ) {
        let DD: any = DDate[Di];
        const thisDD = DD < 10 ? '0' + DD : DD;
        if (
          checkDate(YY + '-' + MM + '-' + thisDD + ' 00:00:00') !== true &&
          dayRule.value !== 'workDay' &&
          dayRule.value !== 'lastWeek' &&
          dayRule.value !== 'lastDay'
        )
          continue;
        for (
          let hi = Di === DIdx && Mi === MIdx && Yi === YIdx ? hIdx : 0;
          hi < hDate.length && count < 5 && iterations < maxIterations;
          hi++
        ) {
          const hh = hDate[hi] < 10 ? '0' + hDate[hi] : hDate[hi];
          for (
            let mi = hi === hIdx && Di === DIdx && Mi === MIdx && Yi === YIdx ? mIdx : 0;
            mi < mDate.length && count < 5 && iterations < maxIterations;
            mi++
          ) {
            const mm = mDate[mi] < 10 ? '0' + mDate[mi] : mDate[mi];
            for (
              let si = mi === mIdx && hi === hIdx && Di === DIdx && Mi === MIdx && Yi === YIdx ? sIdx : 0;
              si < sDate.length && count < 5 && iterations < maxIterations;
              si++
            ) {
              iterations++;
              const ss = sDate[si] < 10 ? '0' + sDate[si] : sDate[si];
              if (MM !== '00' && thisDD !== '00') {
                resultArr.push(YY + '-' + MM + '-' + thisDD + ' ' + hh + ':' + mm + ':' + ss);
                count++;
              }
              if (count >= 5) break;
            }
          }
        }
      }
    }
  }

  if (resultArr.length === 0) {
    resultList.value = [t('components.result.893023-2')];
  } else {
    resultList.value = resultArr;
    if (resultArr.length !== 5) {
      resultList.value.push(t('components.result.893023-3') + resultArr.length + t('components.result.893023-4'));
    }
  }
  isShow.value = true;
}

watch(() => props.ex, expressionChange);
onMounted(() => expressionChange());
</script>
