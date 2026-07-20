export const menuList = [
  {
    id: '1',
    type: 'group',
    name: '输入输出',
    ico: 'FolderOpened',
    open: true,
    children: [
      {
        id: '11',
        type: 'start',
        name: '开始节点',
        ico: 'VideoPlay',
        style: {}, // 自定义覆盖样式
      },
      {
        id: '12',
        type: 'end',
        name: '结束节点',
        ico: 'VideoPause',
        style: {},
      },
    ],
  },
  {
    id: '2',
    type: 'group',
    name: '通讯节点',
    ico: 'FolderOpened',
    open: true,
    children: [
      {
        id: '21',
        type: 'devTrigger',
        name: '设备变量触发',
        ico: 'Upload',
        style: {},
        data: {
          debug: 0,
        },
      },
      {
        id: '22',
        type: 'devExecute',
        name: '设备变量执行',
        ico: 'Download',
        style: {},
        data: {
          debug: 0,
        },
      },
      {
        id: '23',
        type: 'productTrigger',
        name: '产品变量触发',
        ico: 'Upload',
        style: {},
        data: {
          debug: 0,
        },
      },
      {
        id: '24',
        type: 'productExecute',
        name: '产品变量执行',
        ico: 'Download',
        style: {},
        data: {
          debug: 0,
        },
      },
      {
        id: '25',
        type: 'scheduledTrigger',
        name: '定时触发',
        ico: 'AlarmClock',
        style: {},
        data: {
          debug: 0,
        },
      },
      {
        id: '26',
        type: 'customTrigger',
        name: '自定义触发',
        ico: 'UserFilled',
        style: {},
        data: {
          debug: 0,
        },
      },
    ],
  },
  {
    id: '3',
    type: 'group',
    name: '功能节点',
    ico: 'FolderOpened',
    open: true,
    children: [
      {
        id: '31',
        type: 'condition',
        name: '条件',
        ico: 'Connection',
        style: {},
        data: {
          debug: 0,
          expressions: [],
        },
      },
      {
        id: '32',
        type: 'delay',
        name: '延迟',
        ico: 'Timer',
        style: {},
        data: {
          debug: 0,
        },
      },
      {
        id: '33',
        type: 'alarm',
        name: '告警',
        ico: 'Odometer',
        style: {},
        data: {
          debug: 0,
        },
      },
      {
        id: '34',
        type: 'comment',
        name: '注释',
        ico: 'ChatDotSquare',
        style: {},
        data: {
          debug: 0,
        },
      },
    ],
  },
  {
    id: '4',
    type: 'bridge',
    name: '桥接节点',
    ico: 'FolderOpened',
    open: true,
    children: [
      {
        id: '41',
        type: 'bridge',
        name: 'http桥接',
        ico: 'Connection',
        style: {},
        data: {
          type: 3,
          debug: 0,
        },
      },
      {
        id: '42',
        type: 'bridge',
        name: 'mqtt桥接',
        ico: 'Connection',
        style: {},
        data: {
          type: 4,
          debug: 0,
        },
      },
      {
        id: '43',
        type: 'bridge',
        name: '数据库桥接',
        ico: 'Connection',
        style: {},
        data: {
          type: 5,
          debug: 0,
        },
      },
    ],
  },
  {
    id: '5',
    type: 'script',
    name: '脚本节点',
    ico: 'FolderOpened',
    open: true,
    children: [
      {
        id: '51',
        type: 'script',
        name: '自定义脚本',
        ico: 'Tickets',
        style: {},
        data: {
          scriptId: '',
          scriptName: null,
          scriptLanguage: 'groovy',
          scriptType: 'script',
          scriptData: '',
          enable: 1,
          debug: 0,
        },
      },
    ],
  },
];