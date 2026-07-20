<template>
  <div id="DeviceTree" style="width: 100%; height: calc(100vh - 50px); background-color: #ffffff; overflow: hidden">
    <el-input
      style="margin-bottom: 10px"
      v-model="filterText"
      :placeholder="$t('device.index.105953-1')"
      clearable
    ></el-input>
    <div class="tree-wrapper" style="height: calc(100% - 40px); overflow: auto; min-height: 200px">
      <el-tree
        ref="treeRef"
        :props="defaultProps"
        :current-node-key="selectchannelId"
        :default-expanded-keys="expandIds"
        :highlight-current="true"
        @node-click="handleNodeClick"
        :load="loadNode"
        lazy
        node-key="id"
        :filter-node-method="filterNode"
        style="width: 100%; height: 100%"
      >
        <template #default="{ node }">
          <span class="custom-tree-node" style="width: 100%">
            <span
              v-if="node.data.type === 0 && node.data.online"
              title="在线设备"
              class="device-online iconfont icon-jiedianleizhukongzhongxin2"
            ></span>
            <span
              v-if="node.data.type === 0 && !node.data.online"
              title="离线设备"
              class="device-offline iconfont icon-jiedianleizhukongzhongxin2"
            ></span>
            <span
              v-if="node.data.type === 3 && node.data.online"
              title="在线通道"
              class="device-online iconfont icon-shebeileijiankongdian"
            ></span>
            <span
              v-if="node.data.type === 3 && !node.data.online"
              title="离线通道"
              class="device-offline iconfont icon-shebeileijiankongdian"
            ></span>
            <span
              v-if="node.data.type === 4 && node.data.online"
              title="在线通道-球机"
              class="device-online iconfont icon-shebeileiqiuji"
            ></span>
            <span
              v-if="node.data.type === 4 && !node.data.online"
              title="离线通道-球机"
              class="device-offline iconfont icon-shebeileiqiuji"
            ></span>
            <span
              v-if="node.data.type === 5 && node.data.online"
              title="在线通道-半球"
              class="device-online iconfont icon-shebeileibanqiu"
            ></span>
            <span
              v-if="node.data.type === 5 && !node.data.online"
              title="离线通道-半球"
              class="device-offline iconfont icon-shebeileibanqiu"
            ></span>
            <span
              v-if="node.data.type === 6 && node.data.online"
              title="在线通道-枪机"
              class="device-online iconfont icon-shebeileiqiangjitongdao"
            ></span>
            <span
              v-if="node.data.type === 6 && !node.data.online"
              title="离线通道-枪机"
              class="device-offline iconfont icon-shebeileiqiangjitongdao"
            ></span>
            <span v-if="node.data.online" style="padding-left: 1px" class="device-online">{{ node.label }}</span>
            <span v-if="!node.data.online" style="padding-left: 1px" class="device-offline">{{ node.label }}</span>
          </span>
        </template>
      </el-tree>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, watch, onMounted } from 'vue';
import { listSipDeviceChannel } from '@/api/iot/sipdevice';
import { listDeviceShort } from '@/api/iot/device';

const props = defineProps<{
  onlyCatalog?: boolean;
  clickEvent?: Function;
}>();

const emit = defineEmits(['play']);

const treeRef = ref<any>(null);
const filterText = ref('');
const expandIds = ref<string[]>([]);
const selectData = ref<any>({});
const selectchannelId = ref('');

const defaultProps = {
  children: 'children',
  label: 'name',
  isLeaf: 'isLeaf',
};

const queryParams = ref({
  pageNum: 1,
  pageSize: 100,
  status: 3,
  deviceType: 3,
  showChild: true,
  deviceName: '',
});

watch(filterText, (val) => {
  treeRef.value?.filter(val);
});

onMounted(() => {
  selectchannelId.value = '';
  expandIds.value = ['0'];
});

function handleNodeClick(data: any, node: any) {
  selectData.value = node.data;
  selectchannelId.value = node.data.value;
  if (node.level !== 0) {
    const deviceNode = treeRef.value?.getNode(data.userData?.channelId);
    if (typeof props.clickEvent === 'function' && node.level > 1) {
      props.clickEvent(deviceNode?.data?.userData);
    }
  }
}

function loadNode(node: any, resolve: Function) {
  if (node.level === 0) {
    listDeviceShort(queryParams.value).then((response: any) => {
      const data = response.rows;
      if (data && data.length > 0) {
        const nodeList = data.map((item: any) => ({
          name: item.deviceName,
          isLeaf: false,
          id: item.serialNumber,
          type: 0,
          online: item.status === 3,
          userData: item,
        }));
        resolve(nodeList);
      } else {
        resolve([]);
      }
    });
  } else {
    listSipDeviceChannel(node.data.userData?.serialNumber).then((res: any) => {
      if (res.data != null) {
        channelDataHandler(res.data, resolve);
      } else {
        resolve([]);
      }
    });
  }
}

function channelDataHandler(data: any[], resolve: Function) {
  if (data.length > 0) {
    const nodeList: any[] = [];
    for (let i = 0; i < data.length; i++) {
      const item = data[i];
      const channelType = item.id.substring(10, 13);
      let type = 3;
      if (item.id.length <= 10) {
        type = 2;
      } else {
        if (item.id.length > 14) {
          const ct = item.id.substring(10, 13);
          if (ct !== '111' && ct !== '112' && ct !== '118' && ct !== '131' && ct !== '132') {
            type = -1;
          } else if (item.basicData?.ptztype === 1) {
            type = 4;
          } else if (item.basicData?.ptztype === 2) {
            type = 5;
          } else if (item.basicData?.ptztype === 3 || item.basicData?.ptztype === 4) {
            type = 6;
          }
        } else {
          if (item.basicData?.subCount > 0 || item.basicData?.parental === 1) {
            type = 2;
          }
        }
      }
      const nodeItem = {
        name: item.name || item.id,
        isLeaf: true,
        id: item.id,
        deviceId: item.deviceId,
        type: type,
        onlineStatus: item.status === 3,
        userData: item.basicData,
      };
      if (
        channelType === '111' ||
        channelType === '112' ||
        channelType === '118' ||
        channelType === '131' ||
        channelType === '132'
      ) {
        nodeList.push(nodeItem);
      }
    }
    resolve(nodeList);
  } else {
    resolve([]);
  }
}

function filterNode(value: string, data: any) {
  if (!value) return true;
  return data.name?.indexOf(value) !== -1;
}

defineExpose({
  treeRef,
});
</script>

<style>
.device-tree-main-box {
  text-align: left;
}

.device-online {
  color: #252525;
}

.device-offline {
  color: #727272;
}
</style>
