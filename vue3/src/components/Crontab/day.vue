<template>
  <el-form size="small">
    <el-form-item>
      <el-radio v-model="radioValue" :value="1">{{ $t('components.Crontab.day.304304-0') }}</el-radio>
    </el-form-item>
    <el-form-item>
      <el-radio v-model="radioValue" :value="2">{{ $t('components.Crontab.day.304304-1') }}</el-radio>
    </el-form-item>
    <el-form-item>
      <el-radio v-model="radioValue" :value="3">
        {{ $t('components.Crontab.day.304304-2') }}
        <el-input-number v-model="cycle01" :min="1" :max="30" />
        -
        <el-input-number v-model="cycle02" :min="cycle01 ? cycle01 + 1 : 2" :max="31" />
        {{ $t('components.Crontab.day.304304-3') }}
      </el-radio>
    </el-form-item>
    <el-form-item>
      <el-radio v-model="radioValue" :value="4">
        {{ $t('components.Crontab.day.304304-4') }}
        <el-input-number v-model="average01" :min="1" :max="30" />
        {{ $t('components.Crontab.day.304304-5') }}
        <el-input-number v-model="average02" :min="1" :max="31 - average01 || 1" />
        {{ $t('components.Crontab.day.304304-6') }}
      </el-radio>
    </el-form-item>
    <el-form-item>
      <el-radio v-model="radioValue" :value="5">
        {{ $t('components.Crontab.day.304304-7') }}
        <el-input-number v-model="workday" :min="1" :max="31" />
        {{ $t('components.Crontab.day.304304-8') }}
      </el-radio>
    </el-form-item>
    <el-form-item>
      <el-radio v-model="radioValue" :value="6">{{ $t('components.Crontab.day.304304-9') }}</el-radio>
    </el-form-item>
    <el-form-item>
      <el-radio v-model="radioValue" :value="7">
        {{ $t('components.Crontab.day.304304-10') }}
        <el-select
          clearable
          v-model="checkboxList"
          :placeholder="$t('components.Crontab.day.304304-11')"
          multiple
          style="width: 100%"
        >
          <el-option v-for="item in 31" :key="item" :value="item">{{ item }}</el-option>
        </el-select>
      </el-radio>
    </el-form-item>
  </el-form>
</template>

<script setup lang="ts">
import { ref, computed, watch } from 'vue';

const props = defineProps<{ check: Function; cron: any }>();
const emit = defineEmits(['update']);

const radioValue = ref(1);
const workday = ref(1);
const cycle01 = ref(1);
const cycle02 = ref(2);
const average01 = ref(1);
const average02 = ref(1);
const checkboxList = ref<number[]>([]);

const cycleTotal = computed(() => {
  const c1 = props.check(cycle01.value, 1, 30);
  const c2 = props.check(cycle02.value, c1 ? c1 + 1 : 2, 31);
  return c1 + '-' + c2;
});

const averageTotal = computed(() => {
  const a1 = props.check(average01.value, 1, 30);
  const a2 = props.check(average02.value, 1, 31 - a1 || 0);
  return a1 + '/' + a2;
});

const workdayCheck = computed(() => props.check(workday.value, 1, 31));

const checkboxString = computed(() => {
  const str = checkboxList.value.join();
  return str === '' ? '*' : str;
});

watch(radioValue, () => {
  if (radioValue.value !== 2 && props.cron.week !== '?') {
    emit('update', 'week', '?', 'day');
  }
  switch (radioValue.value) {
    case 1:
      emit('update', 'day', '*');
      break;
    case 2:
      emit('update', 'day', '?');
      break;
    case 3:
      emit('update', 'day', cycleTotal.value);
      break;
    case 4:
      emit('update', 'day', averageTotal.value);
      break;
    case 5:
      emit('update', 'day', workday.value + 'W');
      break;
    case 6:
      emit('update', 'day', 'L');
      break;
    case 7:
      emit('update', 'day', checkboxString.value);
      break;
  }
});

watch(cycleTotal, () => {
  if (radioValue.value === 3) emit('update', 'day', cycleTotal.value);
});
watch(averageTotal, () => {
  if (radioValue.value === 4) emit('update', 'day', averageTotal.value);
});
watch(workdayCheck, () => {
  if (radioValue.value === 5) emit('update', 'day', workdayCheck.value + 'W');
});
watch(checkboxString, () => {
  if (radioValue.value === 7) emit('update', 'day', checkboxString.value);
});

defineExpose({ radioValue, cycle01, cycle02, average01, average02, workday, checkboxList });
</script>
