import mqtt from 'mqtt';
import { getToken } from '@/utils/auth';

const mqttTool: any = {
  client: null as any,
};

/** 连接Mqtt */
mqttTool.connect = function () {
  const search = new URLSearchParams(window.location.search);
  const share = search.get('share');
  const options: any = {
    username: 'FastBee',
    password: getToken() || share,
    cleanSession: true,
    keepAlive: 30,
    clientId: 'web-' + Math.random().toString(16).substr(2),
    connectTimeout: 60000,
  };
  let url = import.meta.env.VITE_APP_MQTT_SERVER_URL;
  if (url == '') {
    console.log('自动获取mqtt连接地址');
    if (window.location.protocol === 'http:') {
      url = 'ws://' + window.location.hostname + ':8083/mqtt';
    } else {
      url = 'wss://' + window.location.hostname + '/mqtt';
    }
  }
  console.log('mqtt地址：', url);
  mqttTool.client = mqtt.connect(url, options);
  mqttTool.client.on('connect', () => {
    console.log('mqtt连接成功');
  });
  mqttTool.client.on('reconnect', (error: any) => {
    console.log('正在重连:', error);
  });
  mqttTool.client.on('error', (error: any) => {
    console.log('Mqtt客户端连接失败：', error);
    mqttTool.client.end();
  });
  mqttTool.client.on('close', function () {
    console.log('已断开Mqtt连接');
  });
};

/** 断开连接 */
mqttTool.end = function () {
  return new Promise((resolve: any) => {
    if (mqttTool.client == null) {
      resolve('未连接');
      return;
    }
    mqttTool.client.end();
    mqttTool.client = null;
    console.log('Mqtt服务器已断开连接！');
    resolve('连接终止');
  });
};

/** 重新连接 */
mqttTool.reconnect = function () {
  return new Promise((resolve: any) => {
    if (mqttTool.client == null) {
      resolve('未连接');
      return;
    }
    console.log('正在重连...');
    mqttTool.client.reconnect();
  });
};

/** 消息订阅 */
mqttTool.subscribe = function (topics: string | string[]) {
  return new Promise((resolve: any) => {
    if (mqttTool.client == null) {
      resolve('未连接');
      return;
    }
    mqttTool.client.subscribe(topics, { qos: 1 }, function (err: any) {
      console.log('订阅主题：', topics);
      if (!err) {
        console.log('订阅成功');
        resolve('订阅成功');
      } else {
        console.log('订阅失败，主题可能已经订阅');
        resolve('订阅失败');
      }
    });
  });
};

/** 取消订阅 */
mqttTool.unsubscribe = function (topics: string | string[]) {
  return new Promise((resolve: any) => {
    if (mqttTool.client == null) {
      resolve('未连接');
      return;
    }
    mqttTool.client.unsubscribe(topics, function (err: any) {
      if (!err) {
        resolve('取消订阅成功');
      } else {
        resolve('取消订阅失败');
      }
    });
  });
};

/** 发布消息 */
mqttTool.publish = function (topic: string, message: string, name?: string) {
  return new Promise((resolve: any, reject: any) => {
    if (mqttTool.client == null) {
      resolve('Mqtt客户端未连接');
      return;
    }
    mqttTool.client.publish(topic, message, { qos: 1 }, function (err: any) {
      console.log('发送主题：', topic);
      console.log('发送内容：', message);
      if (!err) {
        if (topic.indexOf('offline') > 0) {
          resolve('[ ' + name + ' ] 影子指令发送成功');
        } else {
          resolve('[ ' + name + ' ] 指令发送成功');
        }
      } else {
        reject('[ ' + name + ' ] 指令发送失败');
      }
    });
  });
};

export default mqttTool;
