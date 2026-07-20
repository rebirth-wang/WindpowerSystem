<template>
  <div class="monitor-cache">
    <el-row :gutter="20">
      <el-col :span="24" style="margin-bottom: 19px">
        <el-card>
          <template #header>
            <span>{{ $t('system.cache.232015-0') }}</span>
          </template>
          <div class="el-table el-table--enable-row-hover el-table--medium">
            <table cellspacing="0" style="width: 100%">
              <tbody>
                <tr>
                  <td class="el-table__cell is-leaf">
                    <div class="cell">{{ $t('system.cache.232015-1') }}</div>
                  </td>
                  <td class="el-table__cell is-leaf">
                    <div class="cell" v-if="cache.info">{{ cache.info.redis_version }}</div>
                  </td>
                  <td class="el-table__cell is-leaf">
                    <div class="cell">{{ $t('system.cache.232015-2') }}</div>
                  </td>
                  <td class="el-table__cell is-leaf">
                    <div class="cell" v-if="cache.info">
                      {{ cache.info.redis_mode == 'standalone' ? '单机' : '集群' }}
                    </div>
                  </td>
                  <td class="el-table__cell is-leaf">
                    <div class="cell">{{ $t('system.cache.232015-3') }}</div>
                  </td>
                  <td class="el-table__cell is-leaf">
                    <div class="cell" v-if="cache.info">{{ cache.info.tcp_port }}</div>
                  </td>
                  <td class="el-table__cell is-leaf">
                    <div class="cell">{{ $t('system.cache.232015-4') }}</div>
                  </td>
                  <td class="el-table__cell is-leaf">
                    <div class="cell" v-if="cache.info">{{ cache.info.connected_clients }}</div>
                  </td>
                </tr>
                <tr>
                  <td class="el-table__cell is-leaf">
                    <div class="cell">{{ $t('system.cache.232015-5') }}</div>
                  </td>
                  <td class="el-table__cell is-leaf">
                    <div class="cell" v-if="cache.info">{{ cache.info.uptime_in_days }}</div>
                  </td>
                  <td class="el-table__cell is-leaf">
                    <div class="cell">{{ $t('system.cache.232015-6') }}</div>
                  </td>
                  <td class="el-table__cell is-leaf">
                    <div class="cell" v-if="cache.info">{{ cache.info.used_memory_human }}</div>
                  </td>
                  <td class="el-table__cell is-leaf">
                    <div class="cell">{{ $t('system.cache.232015-7') }}</div>
                  </td>
                  <td class="el-table__cell is-leaf">
                    <div class="cell" v-if="cache.info">
                      {{ parseFloat(cache.info.used_cpu_user_children).toFixed(2) }}
                    </div>
                  </td>
                  <td class="el-table__cell is-leaf">
                    <div class="cell">{{ $t('system.cache.232015-8') }}</div>
                  </td>
                  <td class="el-table__cell is-leaf">
                    <div class="cell" v-if="cache.info">{{ cache.info.maxmemory_human }}</div>
                  </td>
                </tr>
                <tr>
                  <td class="el-table__cell is-leaf">
                    <div class="cell">{{ $t('system.cache.232015-9') }}</div>
                  </td>
                  <td class="el-table__cell is-leaf">
                    <div class="cell" v-if="cache.info">{{ cache.info.aof_enabled == '0' ? '否' : '是' }}</div>
                  </td>
                  <td class="el-table__cell is-leaf">
                    <div class="cell">{{ $t('system.cache.232015-10') }}</div>
                  </td>
                  <td class="el-table__cell is-leaf">
                    <div class="cell" v-if="cache.info">{{ cache.info.rdb_last_bgsave_status }}</div>
                  </td>
                  <td class="el-table__cell is-leaf">
                    <div class="cell">{{ $t('system.cache.232015-11') }}</div>
                  </td>
                  <td class="el-table__cell is-leaf">
                    <div class="cell" v-if="cache.dbSize">{{ cache.dbSize }}</div>
                  </td>
                  <td class="el-table__cell is-leaf">
                    <div class="cell">{{ $t('system.cache.232015-12') }}</div>
                  </td>
                  <td class="el-table__cell is-leaf">
                    <div class="cell" v-if="cache.info">
                      {{ cache.info.instantaneous_input_kbps }}kps/{{ cache.info.instantaneous_output_kbps }}kps
                    </div>
                  </td>
                </tr>
              </tbody>
            </table>
          </div>
        </el-card>
      </el-col>

      <el-col :span="12">
        <el-card>
          <template #header>
            <span>{{ $t('system.cache.232015-13') }}</span>
          </template>
          <div ref="commandstatsRef" style="height: 420px" />
        </el-card>
      </el-col>

      <el-col :span="12">
        <el-card>
          <template #header>
            <span>{{ $t('system.cache.232015-14') }}</span>
          </template>
          <div ref="usedmemoryRef" style="height: 420px" />
        </el-card>
      </el-col>
    </el-row>
  </div>
</template>

<script setup lang="ts">
import { ref, getCurrentInstance, onMounted } from 'vue';
import { useI18n } from 'vue-i18n';
import { getCache } from '@/api/monitor/cache';
import * as echarts from 'echarts';

const { t } = useI18n();
const { proxy } = getCurrentInstance()!;

const cache = ref<any>({});
const commandstatsRef = ref<HTMLElement>();
const usedmemoryRef = ref<HTMLElement>();

function getList() {
  getCache().then((response: any) => {
    cache.value = response.data;
    (proxy as any).$modal.closeLoading();

    const commandstatsChart = echarts.init(commandstatsRef.value!, 'macarons');
    commandstatsChart.setOption({
      tooltip: {
        trigger: 'item',
        formatter: '{a} <br/>{b} : {c} ({d}%)',
      },
      series: [
        {
          name: t('system.cache.232015-15'),
          type: 'pie',
          roseType: 'radius',
          radius: [15, 95],
          center: ['50%', '38%'],
          data: response.data.commandStats,
          animationEasing: 'cubicInOut',
          animationDuration: 1000,
        },
      ],
    });

    const usedmemoryChart = echarts.init(usedmemoryRef.value!, 'macarons');
    usedmemoryChart.setOption({
      tooltip: {
        formatter: '{b} <br/>{a} : ' + cache.value.info.used_memory_human,
      },
      series: [
        {
          name: t('system.cache.232015-16'),
          type: 'gauge',
          min: 0,
          max: 1000,
          detail: {
            formatter: cache.value.info.used_memory_human,
          },
          data: [
            {
              value: parseFloat(cache.value.info.used_memory_human),
              name: t('system.cache.232015-17'),
            },
          ],
        },
      ],
    });
  });
}

function openLoading() {
  (proxy as any).$modal.loading(t('system.server.890786-32'));
}

onMounted(() => {
  openLoading();
  getList();
});
</script>

<style lang="scss" scoped>
.monitor-cache {
  padding: 20px;
}
</style>
