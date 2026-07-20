<template>
  <el-form size="small">
    <el-form-item>
      <el-radio v-model="radioValue" :value="1">{{ $t('components.Crontab.week.903494-0') }}</el-radio>
    </el-form-item>
    <el-form-item>
      <el-radio v-model="radioValue" :value="2">{{ $t('components.Crontab.week.903494-1') }}</el-radio>
    </el-form-item>
    <el-form-item>
      <el-radio v-model="radioValue" :value="3">
        {{ $t('components.Crontab.week.903494-2') }}
        <el-select clearable v-model="cycle01">
          <el-option
            v-for="(item, index) of weekList"
            :key="index"
            :label="item.value"
            :value="item.key"
            :disabled="item.key === 1"
          />
        </el-select>
        -
        <el-select clearable v-model="cycle02">
          <el-option
            v-for="(item, index) of weekList"
            :key="index"
            :label="item.value"
            :value="item.key"
            :disabled="item.key < cycle01 && item.key !== 1"
          />
        </el-select>
      </el-radio>
    </el-form-item>
    <el-form-item>
      <el-radio v-model="radioValue" :value="4">
        {{ $t('components.Crontab.week.903494-3') }}
        <el-input-number v-model="average01" :min="1" :max="4" />
        {{ $t('components.Crontab.week.903494-4') }}
        <el-select clearable v-model="average02">
          <el-option v-for="(item, index) of weekList" :key="index" :label="item.value" :value="item.key" />
        </el-select>
      </el-radio>
    </el-form-item>
    <el-form-item>
      <el-radio v-model="radioValue" :value="5">
        {{ $t('components.Crontab.week.903494-5') }}
        <el-select clearable v-model="weekday">
          <el-option v-for="(item, index) of weekList" :key="index" :label="item.value" :value="item.key" />
        </el-select>
      </el-radio>
    </el-form-item>
    <el-form-item>
      <el-radio v-model="radioValue" :value="6">
        {{ $t('components.Crontab.week.903494-6') }}
        <el-select
          clearable
          v-model="checkboxList"
          :placeholder="$t('components.Crontab.second.452546-7')"
          multiple
          style="width: 100%"
        >
          <el-option v-for="(item, index) of weekList" :key="index" :label="item.value" :value="String(item.key)" />
        </el-select>
      </el-radio>
    </el-form-item>
  </el-form>
</template>

<script setup lang="ts">
import { ref, computed, watch } from 'vue';
import { useI18n } from 'vue-i18n';

const { t } = useI18n();
const props = defineProps<{ check: Function; cron: any }>();
const emit = defineEmits(['update']);

const radioValue = ref(2);
const weekday = ref(2);
const cycle01 = ref(2);
const cycle02 = ref(3);
const average01 = ref(1);
const average02 = ref(2);
const checkboxList = ref<string[]>([]);

const weekList = [
  { key: 2, value: t('components.Crontab.week.903494-7') },
  { key: 3, value: t('components.Crontab.week.903494-8') },
  { key: 4, value: t('components.Crontab.week.903494-9') },
  { key: 5, value: t('components.Crontab.week.903494-10') },
  { key: 6, value: t('components.Crontab.week.903494-11') },
  { key: 7, value: t('components.Crontab.week.903494-12') },
  { key: 1, value: t('components.Crontab.week.903494-13') },
];

const cycleTotal = computed(() => {
  const c1 = props.check(cycle01.value, 1, 7);
  const c2 = props.check(cycle02.value, 1, 7);
  return c1 + '-' + c2;
});

const averageTotal = computed(() => {
  const a1 = props.check(average01.value, 1, 4);
  const a2 = props.check(average02.value, 1, 7);
  return a2 + '#' + a1;
});

const weekdayCheck = computed(() => props.check(weekday.value, 1, 7));

const checkboxString = computed(() => {
  const str = checkboxList.value.join();
  return str === '' ? '*' : str;
});

watch(radioValue, () => {
  if (radioValue.value !== 2 && props.cron.day !== '?') {
    emit('update', 'day', '?', 'week');
  }
  switch (radioValue.value) {
    case 1:
      emit('update', 'week', '*');
      break;
    case 2:
      emit('update', 'week', '?');
      break;
    case 3:
      emit('update', 'week', cycleTotal.value);
      break;
    case 4:
      emit('update', 'week', averageTotal.value);
      break;
    case 5:
      emit('update', 'week', weekdayCheck.value + 'L');
      break;
    case 6:
      emit('update', 'week', checkboxString.value);
      break;
  }
});

watch(cycleTotal, () => {
  if (radioValue.value === 3) emit('update', 'week', cycleTotal.value);
});
watch(averageTotal, () => {
  if (radioValue.value === 4) emit('update', 'week', averageTotal.value);
});
watch(weekdayCheck, () => {
  if (radioValue.value === 5) emit('update', 'week', weekday.value + 'L');
});
watch(checkboxString, () => {
  if (radioValue.value === 6) emit('update', 'week', checkboxString.value);
});

defineExpose({ radioValue, cycle01, cycle02, average01, average02, weekday, checkboxList });
</script>
