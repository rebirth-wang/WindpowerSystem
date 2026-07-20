# FastBee API HTTP测试脚本

本目录包含fastbee-open-api模块所有Controller的HTTP测试脚本,共103个文件,按照Controller目录结构进行分类管理。

## 📁 目录结构

```
script/idea/
├── device/              (10个文件) 设备管理核心模块
├── system/              (16个文件) 系统管理模块
├── deviceConfig/        (11个文件) 产品配置模块
├── media/               (11个文件) 媒体服务模块
├── ruleview/            (9个文件) 规则视图模块
├── deviceLog/           (4个文件) 设备日志模块
├── ruleEngine/          (5个文件) 规则引擎模块
├── modbus/              (6个文件) Modbus协议模块
├── sceneModel/          (4个文件) 场景模型模块
├── datacenter/          (3个文件) 数据中心模块
├── tool/                (5个文件) 工具模块
├── socialUser/          (3个文件) 社交用户模块
├── card/                (2个文件) 卡管理模块
├── firmware/            (2个文件) 固件管理模块
├── goview/              (2个文件) GoView模块
├── translate/           (2个文件) 翻译模块
├── appVersion/          (1个文件) APP版本管理
├── dashBoard/           (1个文件) 仪表盘
├── gateway/             (1个文件) 网关管理
├── netty/               (1个文件) Netty管理
├── operations/          (1个文件) 工单管理
├── protocol/            (1个文件) 协议管理
├── sip/                 (1个文件) SIP协议
├── wechat/              (1个文件) 微信模块
├── README.md            本说明文档
├── http-client.env.json 环境配置文件
└── regenerate_all.py    批量生成脚本(Python)
```

## 🎯 核心模块说明

### device/ - 设备管理核心模块 (10个文件)
**DeviceController.http** 包含最完整的28个接口测试用例:
- 设备CRUD操作(新增/查询/修改/删除)
- 设备分享、设备用户管理
- 设备任务、设备维护
- 设备状态查询、统计数据
- 设备授权码管理

### system/ - 系统管理模块 (16个文件)
- **SysUserController**: 用户管理(CRUD、导入导出、个人资料)
- **SysRoleController**: 角色权限管理
- **SysMenuController**: 菜单管理
- **SysDeptController**: 部门管理
- **SysDictData/TypeController**: 字典管理
- **SysConfigController**: 系统配置
- **SysLogin/RegisterController**: 登录注册

### deviceConfig/ - 产品配置模块 (11个文件)
- 产品管理与分类
- 物模型管理(ThingsModel)
- 设备分组管理
- 产品授权管理
- 物模型模板与翻译

### media/ - 媒体服务模块 (11个文件)
- 视频播放器管理
- PTZ云台控制
- 录像管理
- 媒体服务器配置
- SIP设备对讲

### ruleEngine/ - 规则引擎模块 (5个文件)
- 场景联动管理
- 告警配置与触发
- 网桥管理(HTTP Bridge)
- 脚本管理

### ruleview/ - 规则视图模块 (9个文件)
- 规则链管理
- 规则调试与日志
- 规则解析与执行
- EL表达式规则
- 脚本节点管理

### 其他模块
- **modbus/**: Modbus协议配置与任务管理
- **datacenter/**: 数据报表与统计
- **firmware/**: 固件管理与升级任务
- **sceneModel/**: 场景模型与标签管理
- **tool/**: 新闻、EMQX、工具接口等

## 🚀 快速开始

### 1️⃣ 环境配置

编辑 `http-client.env.json` 文件,配置您的测试环境:

```json
{
  "local": {
    "baseUrl": "http://127.0.0.1:8080",
    "token": "your_jwt_token_here",
    "tenantId": "1"
  },
  "dev": {
    "baseUrl": "http://your-dev-server.com",
    "token": "your_dev_token",
    "tenantId": "1"
  }
}
```

**配置说明**:
- `baseUrl`: API服务器地址
- `token`: JWT认证令牌(需要先登录获取)
- `tenantId`: 租户ID(默认1)

### 2️⃣ 获取Token

在测试需要认证的接口前,先调用登录接口获取token:

```http
### 登录获取Token
POST {{baseUrl}}/system/login
Content-Type: application/json

{
  "username": "admin",
  "password": "123456"
}
```

将返回的token复制到 `http-client.env.json` 中。

### 3️⃣ 运行测试

#### 使用 IntelliJ IDEA
1. 打开任意 `.http` 文件
2. 点击请求左侧的绿色 ▶️ 运行按钮
3. 选择环境(local/dev/gateway)
4. 查看响应结果

#### 使用 VS Code
1. 安装 [REST Client](https://marketplace.visualstudio.com/items?itemName=humao.rest-client) 扩展
2. 打开任意 `.http` 文件
3. 点击请求上方的 `Send Request` 链接
4. 查看响应结果

## 📝 测试脚本规范

### 基本格式

```http
### 功能描述 => 预期结果
METHOD {{baseUrl}}/api-path?param1=value1&param2=value2
Authorization: Bearer {{token}}
Content-Type: application/json

{
  "key": "value"
}
```

### 注释规范
- 使用 `###` 标记每个请求的开始
- 格式: `功能描述 => 预期结果(成功/失败)`
- 所有注释使用中文,确保UTF-8 with BOM编码

### 常见请求类型示例

#### 1. 分页查询
```http
### 查询分页列表 => 成功
GET {{baseUrl}}/iot/device/list?pageNum=1&pageSize=10
Authorization: Bearer {{token}}
```

#### 2. 条件查询
```http
### 按条件查询 => 成功
GET {{baseUrl}}/iot/device/list?deviceName=测试&status=1&pageNum=1&pageSize=10
Authorization: Bearer {{token}}
```

#### 3. 详情查询
```http
### 按ID获取详情 => 成功
GET {{baseUrl}}/iot/device/1
Authorization: Bearer {{token}}
```

#### 4. 新增数据
```http
### 新增设备 => 成功
POST {{baseUrl}}/iot/device
Authorization: Bearer {{token}}
Content-Type: application/json

{
  "productId": 1,
  "deviceName": "测试设备",
  "serialNumber": "DEVICE_TEST_001",
  "status": 1
}
```

#### 5. 修改数据
```http
### 修改设备 => 成功
PUT {{baseUrl}}/iot/device
Authorization: Bearer {{token}}
Content-Type: application/json

{
  "id": 1,
  "deviceName": "更新后的设备名称",
  "status": 0
}
```

#### 6. 删除数据
```http
### 删除设备 => 成功
DELETE {{baseUrl}}/iot/device/1,2,3
Authorization: Bearer {{token}}
```

#### 7. 导出功能
```http
### 导出数据 => 成功
POST {{baseUrl}}/iot/device/export
Authorization: Bearer {{token}}
Content-Type: application/json

{
  "deviceName": "测试"
}
```

## 🛠️ 批量生成测试脚本

### 使用Python脚本(推荐)

```bash
cd f:\project\kaiyuan\fastbee-dev\springboot\script\idea
python regenerate_all.py
```

**脚本功能**:
1. ✅ 自动扫描所有Controller Java文件
2. ✅ 提取 `@RequestMapping`, `@GetMapping` 等注解
3. ✅ 生成对应的HTTP测试文件(UTF-8 with BOM编码)
4. ✅ 自动按目录结构组织文件
5. ✅ 覆盖已存在的文件,生成新的文件

**适用场景**:
- 为新增的Controller生成测试文件
- 重新生成所有测试文件
- 修复编码问题

### 手动创建测试文件

对于需要详细测试用例的Controller,建议手动创建:

1. 复制同目录下的其他 `.http` 文件作为模板
2. 根据实际接口修改请求路径和参数
3. 添加详细的测试场景和边界测试
4. 确保UTF-8 with BOM编码

## ⚠️ 注意事项

### 编码问题
- 所有文件必须使用 **UTF-8 with BOM** 编码
- 避免使用PowerShell脚本生成(会产生中文乱码)
- 如遇到乱码,使用Python脚本重新生成

### 认证与权限
- 确保 `http-client.env.json` 中的token有效且未过期
- 某些接口需要特定权限,使用有相应权限的账号测试
- Token过期时,重新登录获取新token

### 数据依赖
- 某些接口需要前置数据(如:需要先有产品才能创建设备)
- 按顺序执行: 新增 → 查询 → 修改 → 删除
- 测试删除前,确保数据存在

### 参数配置
- 根据实际环境修改请求参数
- ID、编号等使用真实存在的值
- 分页参数: `pageNum`(从1开始), `pageSize`(建议10-50)

### 测试环境
- 建议在开发/测试环境执行,避免影响生产数据
- 使用测试账号,不要使用生产环境的管理员账号
- 定期清理测试数据

## 📊 测试覆盖范围

### 标准CRUD覆盖
每个Controller测试文件通常包含:
- ✅ 分页列表查询
- ✅ 条件查询
- ✅ 详情查询(按ID)
- ✅ 新增数据
- ✅ 修改数据
- ✅ 删除数据(单条/批量)
- ✅ 导出功能(如果支持)

### 业务特定接口
根据模块特性包含:
- 设备模块: 状态查询、统计数据、授权码、分享等
- 系统模块: 登录注册、导入导出、个人资料等
- 媒体模块: 播放控制、云台、录像等
- 规则模块: 调试、日志、链管理等

### 统计信息
- **总文件数**: 103个
- **覆盖模块**: 25个
- **总接口数**: 500+
- **编码格式**: UTF-8 with BOM
- **最后更新**: 2026-04-14

## 🔧 维护与更新

### Controller接口变更时
1. **手动更新**(推荐): 直接修改对应的 `.http` 文件
2. **重新生成**: 运行 `python regenerate_all.py` 覆盖旧文件
3. **验证测试**: 确保新增接口有对应测试用例

### 添加新Controller时
1. 在对应的子目录创建新的 `.http` 文件
2. 或使用 `regenerate_all.py` 自动生成
3. 参考同目录下的其他文件格式

### 问题排查
- **401 Unauthorized**: Token无效或过期,重新登录获取
- **403 Forbidden**: 权限不足,使用有权限的账号
- **404 Not Found**: 检查请求路径是否正确
- **500 Internal Server Error**: 检查请求参数和服务器日志
- **中文乱码**: 确保文件使用UTF-8 with BOM编码

## 💡 最佳实践

1. **环境隔离**: 为不同环境(local/dev/prod)配置不同的环境变量
2. **数据准备**: 编写测试数据准备脚本,确保测试可重复执行
3. **边界测试**: 添加异常场景测试(空参数、非法值、权限不足等)
4. **文档同步**: 接口变更时同步更新测试脚本
5. **定期清理**: 删除过时的测试用例和无用的文件

## 📚 相关资源

- [IntelliJ IDEA HTTP Client文档](https://www.jetbrains.com/help/idea/http-client-in-product-code-editor.html)
- [VS Code REST Client扩展](https://marketplace.visualstudio.com/items?itemName=humao.rest-client)
- [FastBee项目文档](../README.md)

---

**最后更新**: 2026-04-14  
**维护者**: FastBee开发团队  
**问题反馈**: 如发现测试脚本问题,请联系开发团队或提交PR
