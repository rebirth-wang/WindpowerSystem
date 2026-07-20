import i18n from '@/lang'

// 获取实例
const eTemplateString = `
console.log(e)
`
// 获取全局 echarts 实例
const echartsTemplateString = `
console.log(echarts)
`

// 获取当前组件图表集合
const componentsTemplateString = `
console.log(components)
`

// 获取 nodeModules 实例
const nodeModulesTemplateString = `
console.log(node_modules)
`

// 添加点击事件
const addClickTemplateString = `
e.el.addEventListener('click', () => {
  alert('${i18n.global.t('visualBigScreen.management-674210-753')}');
}, false)
`

// 异步引入
const importTemplateString = `
await import('https://lf3-cdn-tos.bytecdntp.com/cdn/expire-1-M/lodash.js/4.17.21/lodash.js')

console.log('isEqual', _.isEqual(['1'], ['1']))
`

// 修改图表 tooltip
const tooltipTemplateString =
  `
const chart = this.refs.vChartRef.chart

chart.setOption({
  tooltip: {
    trigger: 'axis', //item
    enterable: true, 
    formatter (params) {
      return` +
  '`' +
  `
        <div>
          <img src="https://portrait.gitee.com/uploads/avatars/user/1654/4964818_MTrun_1653229420.png!avatar30">
          <b><a href="https://gitee.com/dromara/go-view">《${i18n.global.t('visualBigScreen.management-674210-747')}》</a></b>
        <div>
        <div style='border-radius:35px;color:#666'>
        ` +
  '$' +
  `{Object.entries(params[0].value).map(kv => ` +
  '`' +
  `<div>` +
  '$' +
  `{kv[0]}:` +
  '$' +
  `{kv[1]}</div>` +
  '`' +
  `).join('')}
        </div>
      ` +
  '`;' +
  `
    },
  }
})
`

// 添加【轮播列表】样式
const addStyleString =
  `
const scoped = this.subTree.scopeId
function loadStyleString(css){
	let style = document.createElement('style')
	style.type = 'text/css'
	style.appendChild(document.createTextNode(css))
	let head = document.getElementsByTagName('head')[0]
	head.appendChild(style)
}
loadStyleString(` +
  '`' +
  `
.dv-scroll-board[` +
  '$' +
  `{scoped}] {
  position: relative;
  overflow: hidden;
}
.dv-scroll-board[` +
  '$' +
  `{scoped}]::before {
  content: '';
  display: block;
  position: absolute;
  top: -20%;
  left: -100%;
  width: 550px;
  height: 60px;
  transform: rotate(-45deg);
  background-image: linear-gradient(rgba(0, 0, 0, 0), rgba(255, 255, 255, 0.3), rgba(0, 0, 0, 0));
  animation: cross 2s infinite;
}
@keyframes cross{
  to{
    top: 80%;
    left: 100%;
    transform: rotate(-45deg);
  }
}
` +
  '`' +
  `)
`

// 修改地图原点大小
const editMapPointString = `
const chart = this.refs.vChartRef.chart
this.props.chartConfig.option.series[0].symbolSize = (val) => {
  return Math.sqrt(val[2]) / 3;
}
this.setupState.vEchartsSetOption();
let i = 0;
const len = 3;
(function showTips() {
  const action = (type, dataIndex) => {
    chart.dispatchAction({
      type,
      dataIndex,
      seriesIndex: 0,
    });
  }
  setInterval(() => {
    action("downplay", i);
    action("hideTip", i);
    if (i === len) i = 0;
    i++;
    action("highlight", i);
    action("showTip", i);
  }, 2000);
})()
`

export const templateList = [
  {
    description: i18n.global.t('visualBigScreen.management-674210-730'),
    code: eTemplateString
  },
  {
    description: i18n.global.t('visualBigScreen.management-674210-731'),
    code: echartsTemplateString
  },
  {
    description: i18n.global.t('visualBigScreen.management-674210-732'),
    code: componentsTemplateString
  },
  {
    description: i18n.global.t('visualBigScreen.management-674210-733'),
    code: nodeModulesTemplateString
  },
  {
    description: i18n.global.t('visualBigScreen.management-674210-734'),
    code: importTemplateString
  },
  {
    description: i18n.global.t('visualBigScreen.management-674210-735'),
    code: addClickTemplateString
  },
  {
    description: i18n.global.t('visualBigScreen.management-674210-736'),
    code: tooltipTemplateString
  },
  {
    description: i18n.global.t('visualBigScreen.management-674210-737'),
    code: addStyleString
  },
  {
    description: i18n.global.t('visualBigScreen.management-674210-738'),
    code: editMapPointString
  }
]
