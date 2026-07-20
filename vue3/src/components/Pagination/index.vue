<template>
  <div :class="{ 'pagination-container': true, hidden: hidden }">
    <el-config-provider :locale="currLocale">
      <el-pagination
        v-model:current-page="currentPage"
        v-model:page-size="pageSize"
        :page-sizes="pageSizes"
        :pager-count="pagerCount"
        :total="total"
        :layout="layout"
        :background="background"
        @size-change="handleSizeChange"
        @current-change="handleCurrentChange"
      />
    </el-config-provider>
  </div>
</template>

<script setup lang="ts">
import { computed } from 'vue';
import { ElConfigProvider } from 'element-plus';
import { useI18n } from 'vue-i18n';
import zhCn from 'element-plus/es/locale/lang/zh-cn';
import en from 'element-plus/es/locale/lang/en';

const props = defineProps({
  total: {
    required: true,
    type: Number,
  },
  page: {
    type: Number,
    default: 1,
  },
  limit: {
    type: Number,
    default: 20,
  },
  pageSizes: {
    type: Array as () => number[],
    default: () => [10, 20, 30, 50],
  },
  pagerCount: {
    type: Number,
    default: document.body.clientWidth < 992 ? 5 : 7,
  },
  layout: {
    type: String,
    default: 'total, sizes, prev, pager, next, jumper',
  },
  background: {
    type: Boolean,
    default: true,
  },
  autoScroll: {
    type: Boolean,
    default: true,
  },
  hidden: {
    type: Boolean,
    default: false,
  },
});

const { locale } = useI18n();

const currLocale = computed(() => {
  return locale.value === 'zh-cn' || locale.value === 'zh-CN' ? zhCn : en;
});

const emit = defineEmits(['pagination', 'update:page', 'update:limit']);

const currentPage = computed({
  get() {
    return props.page;
  },
  set(val: number) {
    emit('update:page', val);
  },
});

const pageSize = computed({
  get() {
    return props.limit;
  },
  set(val: number) {
    emit('update:limit', val);
  },
});

function handleSizeChange(val: number) {
  emit('update:limit', val);
  emit('pagination', { page: currentPage.value, limit: val });
  if (props.autoScroll) {
    scrollTo(0, 800);
  }
}

function handleCurrentChange(val: number) {
  emit('update:page', val);
  emit('pagination', { page: val, limit: pageSize.value });
  if (props.autoScroll) {
    scrollTo(0, 800);
  }
}

function handleJumperChange(event: Event) {
  const target = event.target as HTMLInputElement;
  let val = parseInt(target.value);
  const maxPage = Math.ceil(props.total / pageSize.value) || 1;
  if (isNaN(val) || val < 1) {
    val = 1;
  } else if (val > maxPage) {
    val = maxPage;
  }
  emit('update:page', val);
  emit('pagination', { page: val, limit: pageSize.value });
}

function scrollTo(top: number, duration: number) {
  const el = document.documentElement;
  if (duration <= 0) {
    el.scrollTop = top;
    return;
  }
  const difference = top - el.scrollTop;
  const perTick = (difference / duration) * 10;
  setTimeout(() => {
    el.scrollTop = el.scrollTop + perTick;
    if (el.scrollTop === top) return;
    scrollTo(top, duration - 10);
  }, 10);
}
</script>

<style lang="scss" scoped>
.pagination-container {
  display: flex;
  flex-direction: row;
  justify-content: end;
  padding: 16px 16px 0px 16px;

  .hidden {
    display: none;
  }
}
</style>
