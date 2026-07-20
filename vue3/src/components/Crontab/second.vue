<template>
  <el-form size="small">
    <el-form-item>
      <el-radio v-model="radioValue" :value="1">{{ $t('components.Crontab.second.452546-0') }}</el-radio>
    </el-form-item>
    <el-form-item>
      <el-radio v-model="radioValue" :value="2">
        {{ $t('components.Crontab.second.452546-1') }}
        <el-input-number v-model="cycle01" :min="0" :max="58" />
        -
        <el-input-number v-model="cycle02" :min="cycle01 ? cycle01 + 1 : 1" :max="59" />
        {{ $t('components.Crontab.second.452546-2') }}
      </el-radio>
    </el-form-item>
    <el-form-item>
      <el-radio v-model="radioValue" :value="3">
        {{ $t('components.Crontab.second.452546-3') }}
        <el-input-number v-model="average01" :min="0" :max="58" />
        {{ $t('components.Crontab.second.452546-4') }}
        <el-input-number v-model="average02" :min="1" :max="59 - average01 || 0" />
        {{ $t('components.Crontab.second.452546-5') }}
      </el-radio>
    </el-form-item>
    <el-form-item>
      <el-radio v-model="radioValue" :value="4">
        {{ $t('components.Crontab.second.452546-6') }}
        <el-select
          clearable
          v-model="checkboxList"
          :placeholder="$t('components.Crontab.second.452546-7')"
          multiple
          style="width: 100%"
        >
          <el-option v-for="item in 60" :key="item" :value="item - 1">{{ item - 1 }}</el-option>
        </el-select>
      </el-radio>
    </el-form-item>
  </el-form>
</template>

<script setup lang="ts">
import { ref, computed, watch } from 'vue';

const props = defineProps<{ check: Function; radioParent?: number }>();
const emit = defineEmits(['update']);

const radioValue = ref(1);
const cycle01 = ref(1);
const cycle02 = ref(2);
const average01 = ref(0);
const average02 = ref(1);
const checkboxList = ref<number[]>([]);

const cycleTotal = computed(() => {
  const c1 = props.check(cycle01.value, 0, 58);
  const c2 = props.check(cycle02.value, c1 ? c1 + 1 : 1, 59);
  return c1 + '-' + c2;
});

const averageTotal = computed(() => {
  const a1 = props.check(average01.value, 0, 58);
  const a2 = props.check(average02.value, 1, 59 - a1 || 0);
  return a1 + '/' + a2;
});

const checkboxString = computed(() => {
  const str = checkboxList.value.join();
  return str === '' ? '*' : str;
});

watch(radioValue, () => {
  switch (radioValue.value) {
    case 1:
      emit('update', 'second', '*', 'second');
      break;
    case 2:
      emit('update', 'second', cycleTotal.value);
      break;
    case 3:
      emit('update', 'second', averageTotal.value);
      break;
    case 4:
      emit('update', 'second', checkboxString.value);
      break;
  }
});

watch(cycleTotal, () => {
  if (radioValue.value === 2) emit('update', 'second', cycleTotal.value);
});
watch(averageTotal, () => {
  if (radioValue.value === 3) emit('update', 'second', averageTotal.value);
});
watch(checkboxString, () => {
  if (radioValue.value === 4) emit('update', 'second', checkboxString.value);
});
watch(
  () => props.radioParent,
  (val) => {
    if (val !== undefined) radioValue.value = val;
  }
);

defineExpose({ radioValue, cycle01, cycle02, average01, average02, checkboxList });
</script>
