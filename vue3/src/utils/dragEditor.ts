const map = new Map<string, any>();

// -------- 文字展示 --------
// 标题文字
map.set('captionText', {
  component: 'captionText',
  text: 'dragEditor.565720-22', // 国际化
  type: 'caption-text',
  active: true,
  style: 'captionTextStyle',
  setStyle: {
    text: '标题文字',
    name: '标题文字', // 标题内容
    description: '', // 描述内容
    wordSize: 16, // 标题大小
    descriptionSize: 12, // 描述大小
    wordWeight: 400, // 标题粗细
    positions: 'left', // 显示位置  可选left/center
    descriptionWeight: 200, // 描述粗细
    wordColor: 'rgba(50, 50, 51, 10)', // 标题颜色
    descriptionColor: 'rgba(150, 151, 153, 10)', // 描述颜色
    backColor: 'rgba(255, 255, 255, 0)', // 背景颜色
    wordHeight: 24, // 标题高度
    marginTop: 0,
    marginBottom: 0,
  },
});
// 文字1
map.set('textDisplay1', {
  component: 'textDisplay1',
  text: 'dragEditor.565720-23',
  type: 'text-display-1',
  active: true,
  style: 'textDisplay1Style',
  setStyle: {
    marginTop: 0,
    marginBottom: 0,
    backColor: 'rgba(255, 255, 255, 0)', // 背景颜色
    nameColor: 'rgba(51, 51, 51, 1)',
    statusColor: 'rgba(51, 51, 51, 1)',
    showIcon: true,
    iconColor: 'rgba(255, 255, 255, 1)',
    iconBackColor: 'rgba(7, 154, 217, 1)',
  },
  setConfig: {
    attributes: [], // 属性
  },
});

// -------- 数值展示 --------
// 数值1
map.set('numDisplay1', {
  component: 'numDisplay1',
  text: 'dragEditor.565720-24',
  type: 'num-display-1',
  active: true,
  style: 'numDisplay1Style',
  setStyle: {
    marginTop: 0,
    marginBottom: 0,
    backColor: 'rgba(255, 255, 255, 0)', // 背景颜色
    nameColor: 'rgba(51, 51, 51, 1)',
    numColor: 'rgba(51, 51, 51, 1)',
    showIcon: true,
    iconColor: 'rgba(66, 134, 222, 1)',
    iconBackColor: 'rgba(235, 244, 255, 1)',
  },
  setConfig: {
    attributes: [], // 属性
  },
});

// -------- 数值控制 --------
// 数值1
map.set('numControl1', {
  component: 'numControl1',
  text: 'dragEditor.565720-25',
  type: 'num-control-1',
  active: true,
  style: 'numControl1Style',
  setStyle: {
    marginTop: 0,
    marginBottom: 0,
    backColor: 'rgba(255, 255, 255, 0)', // 背景颜色
    nameColor: 'rgba(51, 51, 51, 1)',
    numColor: 'rgba(51, 51, 51, 1)',
    showIcon: true,
    iconColor: 'rgba(255, 255, 255, 1)',
    iconBackColor: 'rgba(255, 153, 0, 1)',
  },
  setConfig: {
    attributes: [], // 属性
  },
});

// -------- 多状态控制 --------
// 开关1
map.set('mulStatusControl1', {
  component: 'mulStatusControl1',
  text: 'dragEditor.565720-26',
  type: 'mul-status-control-1',
  active: true,
  style: 'mulStatusControl1Style',
  setStyle: {
    marginTop: 0,
    marginBottom: 0,
    rowNum: 4, // 每行个数
    fontSize: 14,
    figureHeight: 30,
    borderRadius: 15,
    backColor: 'rgba(255, 255, 255, 0)', // 背景颜色
    nameColor: 'rgba(51, 51, 51, 1)',
    noColor: 'rgba(255, 255, 255, 1)',
    offColor: 'rgba(51, 51, 51, 1)',
    noBackColor: 'rgba(233, 30, 99, 1)',
    offBackColor: 'rgba(238, 238, 238, 1)',
  },
  setConfig: {
    attributes: [], // 属性
  },
});

// -------- 开关控制 --------
// 开关1
map.set('btnControl1', {
  component: 'btnControl1',
  text: 'dragEditor.565720-27',
  type: 'btn-control-1',
  active: true,
  style: 'btnControl1Style',
  setStyle: {
    marginTop: 0,
    marginBottom: 0,
    model: 1, // 开关模式
    backColor: 'rgba(255, 255, 255, 0)', // 背景颜色
    nameColor: 'rgba(51, 51, 51, 1)',
    btnColor: 'rgba(66, 134, 222, 1)',
  },
  setConfig: {
    attributes: [], // 属性
  },
});

// -------- 历史数据 --------
// 仪表
map.set('historicalDataGauge', {
  component: 'historicalDataGauge',
  text: 'dragEditor.565720-28',
  type: 'hstorical-data-gauge',
  active: true,
  style: 'historicalDataGaugeStyle',
  setStyle: {
    marginTop: 0,
    marginBottom: 0,
    rowNum: 1, // 每行个数
    width: 230, // 仪表宽度
    backColor: 'rgba(255, 255, 255, 0)', // 背景颜色
    nameColor: 'rgba(51, 51, 51, 1)',
    loopColor: 'rgba(83, 111, 196, 1)',
    numColor: 'rgba(51, 51, 51, 1)',
  },
  setConfig: {
    attributes: [], // 属性
  },
});

// -------- 其他 --------
map.set('notice', {
  component: 'notice',
  text: '公告',
  type: '1-7',
  active: true,
  style: 'noticestyle',
  setStyle: {
    text: '公告',
    noticeText: '请填写内容，如果过长，将会在手机上滚动显示', // 内容
    backColor: 'rgb(255, 248, 233)', // 背景颜色
    textColor: 'rgba(100, 101, 102)', // 文字颜色
  },
});

export default map;
