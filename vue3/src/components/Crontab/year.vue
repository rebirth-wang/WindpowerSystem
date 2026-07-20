<template>
  <el-form size="small">
    <el-form-item>
      <el-radio :value="1" v-model="radioValue">
        {{ $t('components.Crontab.year.999034-0') }}
      </el-radio>
    </el-form-item>

    <el-form-item>
      <el-radio :value="2" v-model="radioValue">
        {{ $t('components.Crontab.year.999034-1') }}
      </el-radio>
    </el-form-item>

    <el-form-item>
      <el-radio :value="3" v-model="radioValue">
        {{ $t('components.Crontab.year.999034-2') }}
        <el-input-number v-model="cycle01" :min="fullYear" :max="2098" />
        -
        <el-input-number v-model="cycle02" :min="cycle01 ? cycle01 + 1 : fullYear + 1" :max="2099" />
      </el-radio>
    </el-form-item>

    <el-form-item>
      <el-radio :value="4" v-model="radioValue">
        {{ $t('components.Crontab.year.999034-3') }}
        <el-input-number v-model="average01" :min="fullYear" :max="2098" />
        {{ $t('components.Crontab.year.999034-4') }}
        <el-input-number v-model="average02" :min="1" :max="2099 - average01 || fullYear" />
        {{ $t('components.Crontab.year.999034-5') }}
      </el-radio>
    </el-form-item>

    <el-form-item>
      <el-radio :value="5" v-model="radioValue">
        {{ $t('components.Crontab.year.999034-6') }}
        <el-select clearable v-model="checkboxList" :placeholder="$t('components.Crontab.year.999034-7')" multiple>
          <el-option v-for="item in 9" :key="item" :value="item - 1 + fullYear" :label="item - 1 + fullYear" />
        </el-select>
      </el-radio>
    </el-form-item>
  </el-form>
</template>

<script setup lang="ts">
import { ref, computed, watch, onMounted } from 'vue';

const props = defineProps<{ check: Function }>();
const emit = defineEmits(['update']);

const fullYear = ref(0);
const radioValue = ref(1);
const cycle01 = ref(0);
const cycle02 = ref(0);
const average01 = ref(0);
const average02 = ref(1);
const checkboxList = ref<number[]>([]);

const cycleTotal = computed(() => {
  const c1 = props.check(cycle01.value, fullYear.value, 2098);
  const c2 = props.check(cycle02.value, c1 ? c1 + 1 : fullYear.value + 1, 2099);
  return c1 + '-' + c2;
});

const averageTotal = computed(() => {
  const a1 = props.check(average01.value, fullYear.value, 2098);
  const a2 = props.check(average02.value, 1, 2099 - a1 || fullYear.value);
  return a1 + '/' + a2;
});

const checkboxString = computed(() => {
  return checkboxList.value.join();
});

watch(radioValue, () => {
  switch (radioValue.value) {
    case 1:
      emit('update', 'year', '');
      break;
    case 2:
      emit('update', 'year', '*');
      break;
    case 3:
      emit('update', 'year', cycleTotal.value);
      break;
    case 4:
      emit('update', 'year', averageTotal.value);
      break;
    case 5:
      emit('update', 'year', checkboxString.value);
      break;
  }
});
watch(cycleTotal, () => {
  if (radioValue.value === 3) emit('update', 'year', cycleTotal.value);
});
watch(averageTotal, () => {
  if (radioValue.value === 4) emit('update', 'year', averageTotal.value);
});
watch(checkboxString, () => {
  if (radioValue.value === 5) emit('update', 'year', checkboxString.value);
});

onMounted(() => {
  fullYear.value = Number(new Date().getFullYear());
  cycle01.value = fullYear.value;
  average01.value = fullYear.value;
});

defineExpose({ radioValue, cycle01, cycle02, average01, average02, checkboxList });
</script>
