/**
 * jsPlumb 配置常量 (从 Vue 2 mixin 转换为导出常量)
 */
export const jsplumbSetting = {
  // 动态锚点、位置自适应
  Anchors: [
    'Top',
    'TopCenter',
    'TopRight',
    'TopLeft',
    'Right',
    'RightMiddle',
    'Bottom',
    'BottomCenter',
    'BottomRight',
    'BottomLeft',
    'Left',
    'LeftMiddle',
  ],
  // 容器ID
  Container: 'fContainer',
  // 连线的样式，直线或者曲线等
  Connector: ['Bezier', { curviness: 100 }],
  // 鼠标不能拖动删除线
  ConnectionsDetachable: false,
  // 删除线的时候节点不删除
  DeleteEndpointsOnDetach: false,
  // 空白端点
  Endpoint: ['Blank', { Overlays: '' }],
  // 连线的两端端点样式
  EndpointStyle: { fill: '#1879ffa1', outlineWidth: 1 },
  // 是否打开jsPlumb的内部日志记录
  LogEnabled: true,
  // 连线的样式
  PaintStyle: {
    stroke: '#E0E3E7',
    strokeWidth: 2,
    outlineStroke: 'transparent',
    outlineWidth: 10,
  },
  DragOptions: { cursor: 'pointer', zIndex: 2000 },
  // 叠加
  Overlays: [
    [
      'Arrow',
      {
        width: 10,
        length: 8,
        location: 1,
        direction: 1,
        foldback: 0.623,
      },
    ],
  ],
  // 绘制图的模式 svg、canvas
  RenderMode: 'svg',
  // 鼠标滑过线的样式
  HoverPaintStyle: { stroke: '#b0b2b5', strokeWidth: 2 },
  Scope: 'jsPlumb_DefaultScope',
};

/**
 * 连线参数
 */
export const jsplumbConnectOptions = {
  isSource: true,
  isTarget: true,
  anchor: 'Continuous',
  labelStyle: {
    cssClass: 'flowLabel',
  },
  emptyLabelStyle: {
    cssClass: 'emptyFlowLabel',
  },
};

/**
 * 源点配置参数
 */
export const jsplumbSourceOptions = {
  filter: '.flow-node-drag',
  filterExclude: false,
  anchor: 'Continuous',
  allowLoopback: true,
  dragOptions: { cursor: 'crosshair', zIndex: 2000 },
  maxConnections: -1,
  onMaxConnections: function (info, e) {
    console.log(`超过了最大值连线: ${info.maxConnections}`);
  },
};

export const jsplumbSourceOptions2 = {
  filter: '.flow-node-drag',
  filterExclude: false,
  allowLoopback: true,
  connector: ['Flowchart', { curviness: 50 }],
  connectorStyle: {
    stroke: 'red',
    strokeWidth: 1,
    outlineStroke: 'transparent',
    outlineWidth: 10,
  },
  connectorHoverStyle: { stroke: 'red', strokeWidth: 2 },
};

export const jsplumbTargetOptions = {
  filter: '.flow-node-drag',
  filterExclude: false,
  anchor: 'Continuous',
  allowLoopback: true,
  dropOptions: { hoverClass: 'f-drop-hover' },
};
