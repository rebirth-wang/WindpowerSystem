<template>
  <div class="go-chart-configurations-data-static">
    <chart-data-matching-and-show :show="false" :ajax="false"></chart-data-matching-and-show>
  </div>
</template>

<script setup lang="ts">
import { ChartDataMatchingAndShow } from '../ChartDataMatchingAndShow'
</script>

<style lang="scss" scoped>
@include go('chart-configurations-data-static') {
}
</style>
