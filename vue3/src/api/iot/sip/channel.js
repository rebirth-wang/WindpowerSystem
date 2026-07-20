import request from '@/utils/request';

// 查询监控设备通道信息列表
export function listChannel(query) {
    return request({
        url: '/iot/channel/list',
        method: 'get',
        params: query,
    });
}

// 查询监控设备通道信息详细
export function getChannel(channelId) {
    return request({
        url: '/iot/channel/' + channelId,
        method: 'get',
    });
}

// 新增监控设备通道信息
export function addChannel(createNum, data) {
    return request({
        url: '/iot/channel/sip/' + createNum,
        method: 'post',
        data: data,
    });
}

// 修改监控设备通道信息
export function updateChannel(data) {
    return request({
        url: '/iot/channel',
        method: 'put',
        data: data,
    });
}

// 删除监控设备通道信息
export function delChannel(channelId) {
    return request({
        url: '/iot/channel/' + channelId,
        method: 'delete',
    });
}

// 监控设备绑定设备或场景
export function binding(data) {
    return request({
        url: '/iot/relation/addOrUp',
        method: 'post',
        data: data,
    });
}
// 通过设备或场景查询监控设备通道信息
export function listRelDeviceOrScene(query) {
    return request({
        url: '/iot/channel/listRelDeviceOrScene',
        method: 'get',
        params: query,
    });
}
