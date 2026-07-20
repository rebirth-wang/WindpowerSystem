## Coap


## Coap Roadmap
- Coap消息编解码 消息头 可选项 消息内容
- Coap数据路由注册
- Coap观察机制
- Coap消息块处理
- Coap认证处理 Token Messageid
- Coap content上下文
- Coap TransportService北向接口

## Coap南向接口
- .well-known/core 查询服务端支持的uri资源
- /utc-time 查询服务端utc-time
- 其他拓展（设备注册,设备上下线,设备属性上报,设备）

## Coap北向接口 （TransportService 开放给业务层的接口类）
- 设备命令下发
- 设备属性设置
- 设备升级
- 设备重启

## Coap TransportContext 上下文
- 转发器 （http转发,mqtt broker转发）
- 消息格式适配器（物模型编码,json,字节编码,裸数据）
- 调度器
- 执行线程池
- 请求消息
- 响应消息
- 会话管理

## Coap TransportHandler 处理函数
- ReqDispatcher 消息分发处理

## 2.测试工具
为了测试测试我们的代码是否是正确工作，我们需要一个CoAP的命令行工具。目前有两个不错的工具可以使用。

- CoAP-cli，一个基于NodeJS的CoAP命令行工具，其核心是基于Node-CoAP库。
- libcooap，一个用C写的CoAP命令行工具。

### 2.1　Node CoAP CLI
  安装命令如下
```
npm install coap-cli -g
```
#### 2.1.1　CoAP命令行
在coap-cli中，一共有四个方法。分别表示REST的四种不同的方式:
```
Commands:

get                    performs a GET request
put                    performs a PUT request
post                   performs a POST request
delete                 performs a DELETE request
在这里，我们用coap://vs0.inf.ethz.ch/来作一个简单的测试

coap get coap://vs0.inf.ethz.ch/
(2.05)  ************************************************************
I-D
测试一下现在的最小的物联网系统CoAP版

coap get coap://iot-coap.phodal.com/id/1
(2.05)  [{"id":1,"value":"is id 1","sensors1":19,"sensors2":20}]
```
### 2.2　libcoap

#### 2.2.3　Windows libcoap安装
使用预先编译好的 libcoap 镜像：
```
$ docker run -it --rm --name libcoap --network host heeejianbo/my-libcoap:1.0
```
通过在其中运行 coap-client 命令，我们可以查看当前使用的 CoAP 客户端版本：
```
$ coap-client
coap-client v4.3.1 -- a small CoAP implementation
Copyright (C) 2010-2022 Olaf Bergmann <bergmann@tzi.org> and others

Build: v4.3.1-4-g02b7647
TLS Library: OpenSSL - runtime 1.1.1t, libcoap built for 1.1.1t
(DTLS and TLS support; PSK, PKI, PKCS11, and no RPK support)
```
### 2.3　emqx的coap网关测试
#### 2.3.1 PubSub 模式
- 发布消息
```
使用 coap-client 向 test/topic 发布消息进行测试：
coap-client -m post -e "Hello, CoAP" "coap://127.0.0.1/ps/test/topic?qos=0"
```

- 订阅消息
```
创建一个 CoAP 订阅者，并等待 60 秒以接收 test/topic 主题上的消息：
coap-client -m get -s 60 -O 6,0x00 -o - -T "obstoken" "coap://127.0.0.1/ps/test/topic"
```

#### 2.3.2 连接模式
- 创建连接
CoAP 客户端可以通过请求 /mqtt/connection 来创建连接，例如：
```
coap-client -m post -e "" "coap://127.0.0.1/mqtt/connection?clientid=coaptest&username=admin&password=admin123"
coap-client -m post -e "" "coap://192.168.5.48/mqtt/connection?clientid=coaptest&username=admin&password=admin123"
连接成功创建后，将返回一个 Token。
```
- 断开连接
```
coap-client -m delete -e "" "coap://127.0.0.1/mqtt/connection?clientid=coaptest&token=3404490787"
```

- 心跳
```
coap-client -m put -e "" "coap://127.0.0.1/mqtt/connection?clientid=coaptest&token=3404490787"
```

- 发送消息
```
在连接模式下发送消息需要提供 Token 和 ClientId 参数，例如：
coap-client -m post -e "Hi, this is libcoap" "coap://127.0.0.1/ps/coap/test?clientid=coaptest&token=3404490787"


coap-client -m post -e "[{\"id\": \"temperature\", \"value\": 17, \"remark\": \"test\" }]" "coap://192.168.5.48/ps//41/D1ELV3A5TOJS/property/post?clientid=coaptest&token=3404490787"
coap-client -m post -e "[{\"id\": \"switch\", \"value\": 1, \"remark\": \"test\" }]" "coap://192.168.5.48/ps//41/D1ELV3A5TOJS/function/post?clientid=coaptest&token=3404490787"
coap-client -m post -e "[{\"id\": \"height_temperature\", \"value\": 50, \"remark\": \"test\" }]" "coap://192.168.5.48/ps//41/D1ELV3A5TOJS/event/post?clientid=coaptest&token=3404490787"

```

- 订阅主题
```
coap-client -m get -s 60 -O 6,0x00 -o - -T "obstoken" "coap://127.0.0.1/ps/coap/test?clientid=coaptest&token=3404490787"
coap-client -m get -s 60 -O 6,0x00 -o - -T "obstoken" "coap://192.168.5.48/ps//41/D1ELV3A5TOJS/function/post?clientid=coaptest&token=3404490787"
```

- 取消订阅
```
coap-client -m get -O 6,0x01 "coap://127.0.0.1/ps/coap/test?clientid=coaptest&token=3404490787"
```


### 2.4　fastbee coap接入测试

#### 测试命令
- 创建连接
  CoAP 客户端可以通过请求 /mqtt/connection 来创建连接，例如：
```
clientid = S-D1K031BE49X2-146-1

coap-client -m post -e "" "coap://192.168.5.48/connection?clientid=S-D1K031BE49X2-146-1&username=FastBee&password=P1N864X0420SN171"
连接成功创建后，将返回一个 Token。
```
- 断开连接
```
coap-client -m delete -e "" "coap://192.168.5.48/connection?clientid=S-D1K031BE49X2-146-1&token=CYG4UQRZKWY4SUT"
```

- 心跳
```
coap-client -m post -e "" "coap://192.168.5.48/keepalive?token=CYG4UQRZKWY4SUT"、
```

- 发送消息
```
发送消息需要提供 Token 参数，例如：

coap-client -m post -e "[{\"rssi\": -43, \"firmwareVersion\": 1.2, \"userId\": 2, \"status\": 3, \"longitude\": 0, \"latitude\": 0}]" "coap://192.168.5.48/info/post?token=CYG4UQRZKWY4SUT"
coap-client -m post -e "[{\"id\": \"temperature\", \"value\": 17, \"remark\": \"test\" }]" "coap://192.168.5.48/property/post?token=CYG4UQRZKWY4SUT"
coap-client -m post -e "[{\"id\": \"switch\", \"value\": 1, \"remark\": \"test\" }]" "coap://192.168.5.48/function/post?token=CYG4UQRZKWY4SUT"
coap-client -m post -e "[{\"id\": \"height_temperature\", \"value\": 50, \"remark\": \"test\" }]" "coap://192.168.5.48/event/post?token=CYG4UQRZKWY4SUT"
