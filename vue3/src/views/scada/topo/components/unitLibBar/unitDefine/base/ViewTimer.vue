<template>
  <div
    :id="detail.identifier"
    :style="{
      fontSize: detail.style.fontSize + 'px',
      fontFamily: detail.style.fontFamily,
      color: detail.style.foreColor,
      textAlign: textAlign,
      lineHeight: lineHeight + 'px',
    }"
  >
    {{ dateYear }} {{ dateWeek }} {{ dateDay }}
    <div></div>
  </div>
</template>

<script lang="ts">
import BaseView from '../View.vue';
import { formatTime1 } from '@/utils/index';

export default {
  name: 'ViewTimer',
  extends: BaseView,
  data() {
    return {
      timing: null,
      dateDay: null,
      dateYear: null,
      dateWeek: null,
      weekday: ['周日', '周一', '周二', '周三', '周四', '周五', '周六'],
      animation: null,
    };
  },
  mounted() {
    this.getTimer();
    this.timeFn();
  },
  methods: {
    getTimer() {
      this.dateDay = formatTime1(new Date(), 'HH:mm:ss');
      this.dateYear = formatTime1(new Date(), 'yyyy-MM-dd');
      this.dateWeek = this.weekday[new Date().getDay()];
    },
    timeFn() {
      this.timing = setInterval(() => {
        this.dateDay = formatTime1(new Date(), 'HH:mm:ss');
        this.dateYear = formatTime1(new Date(), 'yyyy-MM-dd');
        this.dateWeek = this.weekday[new Date().getDay()];
      }, 1000);
    },
  },
};
</script>
