<template>
  <div>
    <svg class="svgBgClass">
      <polyline
        :points="points"
        :style="{
          fill: 'none',
          stroke: detail.style.backColor,
          strokeWidth: detail.style.lineHeight,
          strokeLinecap: detail.style.lineType,
          strokeLinejoin: detail.style.lineType,
          'fill-rule': detail.style.lineType,
        }"
      />
    </svg>
    <svg class="svgClass">
      <polyline
        :id="detail.identifier"
        :points="points"
        :stroke-dasharray="detail.style.lineWidth + ' ' + this.detail.style.lineInterval"
        :style="{
          fill: 'none',
          stroke: detail.style.foreColor,
          strokeWidth: detail.style.lineHeight,
          strokeLinecap: detail.style.lineType,
          strokeLinejoin: detail.style.lineType,
          'fill-rule': detail.style.lineType,
        }"
      />
    </svg>
    <div class="view-line-arrow" @mousemove="onMousemove($event)" @mouseup="onMouseUp($event)">
      <canvas ref="elCanvas" :width="detail.style.position.w" :height="detail.style.position.h">
        Your browser does not support the HTML5 canvas tag.
      </canvas>

      <template v-if="editMode && selected">
        <div
          class="passby"
          v-for="(pass, index) in detail.style.spotPoints"
          :key="index"
          @mousedown.stop="aroowPassDown(pass, $event, index)"
          :style="{
            left: pass.x - 5 + 'px',
            top: pass.y - 5 + 'px',
          }"
        ></div>
      </template>
    </div>
  </div>
</template>

<script lang="ts">
import { useTopoEditorStore } from '@/stores/modules/topoEditor';

import BaseView from '../View.vue';
import { judgeSize } from '@/utils/topo/topoUtil';

export default {
  name: 'view-flow-bar',
  extends: BaseView,
  setup() {
    const topoStore = useTopoEditorStore();
    return { topoStore };
  },
  computed: {
    mqttData() {
      return this.topoStore.mqttData;
    },
    animations: function () {
      return this.detail.style.animations + '-' + this.detail.style.speed;
    },
    dashoffset: function () {
      return this.detail.style.lineWidth + this.detail.style.lineInterval;
    },
  },
  data() {
    return {
      direction: '水平',
      lineWidth: 10,
      flag: false,
      passItem: {},
      points: '20,20 300,20',
      width: 0,
      height: 0,
      FACTOR_H: 0, //箭头 水平高度倍数
      FACTOR_V: 0, //箭头 垂直长度倍数
    };
  },
  watch: {
    animations(newVal, oldVal) {
      this.goggle(newVal, this.detail.style.lineWidth + this.detail.style.lineInterval, this.randomStr());
    },
    dashoffset(newVal, oldVal) {
      this.goggle('正向-中', newVal, this.randomStr());
    },
    mqttData(newVal, oldVal) {
      // console.log("newVal",newVal);
      // console.log("oldVal",oldVal);
      //流动条
      if (this.detail.dataBind.paramField && newVal.imei == this.detail.dataBind.imei) {
        let val = '';
        Object.keys(newVal).forEach((ele, index) => {
          if (ele == this.detail.dataBind.paramField) {
            if (oldVal != undefined && Object.values(newVal)[index] == Object.values(oldVal)[index]) {
              //  console.log("数据未变化无需更新流动条");
            } else {
              val = Object.values(newVal)[index] as string;
              //条件判断一
              let isGd = judgeSize(this.detail.dataAction.paramJudge, val, this.detail.dataAction.paramJudgeData);
              //条件判断二
              let isGd01 = judgeSize(this.detail.dataAction.paramJudge01, val, this.detail.dataAction.paramJudgeData01);
              if (isGd) {
                this.goggle(this.detail.dataAction.direction + '-' + this.detail.style.speed);
              } else if (isGd01) {
                this.goggle(this.detail.dataAction.direction01 + '-' + this.detail.style.speed);
              } else {
                this.goggle('停止-中');
              }
            }
          }
        });
      }
    },
  },
  mounted() {
    let horizontalPoints = [];
    this.detail.style.spotPoints.forEach((element) => {
      horizontalPoints.push(element.x + ',' + element.y);
    });
    this.points = horizontalPoints.join(' ');
    console.log(this.points);
    this.goggle(
      this.detail.style.animations + '-' + this.detail.style.animations.speed,
      this.detail.style.lineWidth + this.detail.style.lineInterval
    );
  },
  methods: {
    randomStr() {
      return (((1 + Math.random()) * 0x10000) | 0).toString(16).substring(1);
    },
    goggle(newVal, dashoffset, randomStr) {
      let element = document.getElementById(this.detail.identifier);
      let idSub = this.detail.identifier.substring(0, 8);
      let shineForward = '';
      let shineReverse = '';
      let isinit = true;
      if (!dashoffset) {
        dashoffset = this.detail.style.lineWidth + this.detail.style.lineInterval;
        isinit = false;
      }
      let val = newVal.split('-');
      // console.log("流动判断",val);
      if (val[0] == '正向') {
        if (val[1] == '快') {
          if (randomStr) {
            element.style.animation = 'shine-forward-' + randomStr + ' 0.15s infinite linear';
            shineForward =
              '@keyframes shine-forward-' +
              randomStr +
              ' {0% {stroke-dashoffset: ' +
              dashoffset +
              'px;}100% {stroke-dashoffset: 0px;}}';
          } else {
            element.style.animation = 'shine-forward-' + idSub + ' 0.15s infinite linear';
          }
        } else if (val[1] == '中') {
          if (randomStr) {
            element.style.animation = 'shine-forward-' + randomStr + ' 0.3s infinite linear';
            shineForward =
              '@keyframes shine-forward-' +
              randomStr +
              ' {0% {stroke-dashoffset: ' +
              dashoffset +
              'px;}100% {stroke-dashoffset: 0px;}}';
          } else {
            element.style.animation = 'shine-forward-' + idSub + ' 0.3s infinite linear';
          }
        } else {
          if (randomStr) {
            element.style.animation = 'shine-forward-' + randomStr + ' 0.5s infinite linear';
            shineForward =
              '@keyframes shine-forward-' +
              randomStr +
              ' {0% {stroke-dashoffset: ' +
              dashoffset +
              'px;}100% {stroke-dashoffset: 0px;}}';
          } else {
            element.style.animation = 'shine-forward-' + idSub + ' 0.5s infinite linear';
          }
        }
      } else if (val[0] == '反向') {
        if (val[1] == '快') {
          if (randomStr) {
            element.style.animation = 'shine-reverse-' + randomStr + ' 0.15s infinite linear';
            shineReverse =
              '@keyframes shine-reverse-' +
              randomStr +
              ' {0% {stroke-dashoffset: 0px;}100% {stroke-dashoffset: ' +
              dashoffset +
              'px;}}';
          } else {
            element.style.animation = 'shine-reverse-' + idSub + ' 0.15s infinite linear';
          }
        } else if (val[1] == '中') {
          if (randomStr) {
            element.style.animation = 'shine-reverse-' + randomStr + ' 0.3s infinite linear';
            shineReverse =
              '@keyframes shine-reverse-' +
              randomStr +
              ' {0% {stroke-dashoffset: 0px;}100% {stroke-dashoffset: ' +
              dashoffset +
              'px;}}';
          } else {
            element.style.animation = 'shine-reverse-' + idSub + ' 0.3s infinite linear';
          }
        } else {
          if (randomStr) {
            element.style.animation = 'shine-reverse-' + randomStr + ' 0.5s infinite linear';
            shineReverse =
              '@keyframes shine-reverse-' +
              randomStr +
              ' {0% {stroke-dashoffset: 0px;}100% {stroke-dashoffset: ' +
              dashoffset +
              'px;}}';
          } else {
            element.style.animation = 'shine-reverse-' + idSub + ' 0.5s infinite linear';
          }
        }
      } else {
        element.style.animation = null;
      }
      if (!randomStr) {
        shineForward =
          '@keyframes shine-forward-' +
          idSub +
          ' {0% {stroke-dashoffset: ' +
          dashoffset +
          'px;}100% {stroke-dashoffset: 0px;}}';
        shineReverse =
          '@keyframes shine-reverse-' +
          idSub +
          ' {0% {stroke-dashoffset: 0px;}100% {stroke-dashoffset: ' +
          dashoffset +
          'px;}}';
      }
      if (isinit) {
        this.insertRule(shineForward);
        this.insertRule(shineReverse);
      }
    },
    insertRule(keyframes) {
      for (let i = 0; i < document.styleSheets.length; i++) {
        let styleSheet = document.styleSheets[i];
        //找不到样式，样式表会报错
        try {
          for (let j = 0; j < styleSheet.cssRules.length; j++) {
            let cssRule = styleSheet.cssRules[j];
            if (cssRule.cssText && cssRule.cssText.indexOf('svgClass') > -1) {
              styleSheet.insertRule(keyframes);
              break;
            }
          }
        } catch (error) {}
      }
    },
    drawArrow(ctx, x2, y2, lineWidth, color) {
      // (x1, y1)是线段起点  (x2, y2)是线段终点
      ctx.beginPath(); // 坐标原点 => (x2, y2)
      ctx.moveTo(x2, y2);
      ctx.lineTo(x2 - lineWidth * this.FACTOR_H, y2 - lineWidth * this.FACTOR_V);
      ctx.lineTo(x2 - lineWidth * this.FACTOR_H, y2 + lineWidth * this.FACTOR_V);
      ctx.closePath();
      ctx.fillStyle = color; //设置线的颜色状态
      ctx.fill();
    },
    drawLine(ctx) {
      var lineWidth = this.lineWidth,
        color = this.getForeColor();
      ctx.beginPath();
      for (let index = 0; index < this.points.length; index++) {
        const begin = this.points[index],
          end = this.points[index + 1];
        ctx.moveTo(begin.x, begin.y);
        ctx.lineTo(end.x, end.y);
        if (index == this.points.length - 2) break;
      }
      ctx.lineWidth = lineWidth; //设置线宽状态
      ctx.strokeStyle = color; //设置线的颜色状态
      ctx.stroke(); //进行绘制
      ctx.closePath();
    },
    reDraw() {
      var w = this.detail.style.position.w;
      var h = this.detail.style.position.h;
      var el = this.$refs.elCanvas;
      var ctx = el.getContext('2d');
      ctx.clearRect(0, 0, w, h);
      this.drawLine(ctx);
      this.drawArrow(
        ctx,
        this.points[this.points.length - 1].x,
        this.points[this.points.length - 1].y,
        this.lineWidth,
        this.getForeColor()
      );
    },
    onResize() {
      this.reDraw();
    },
    aroowPassDown(pass, event, index) {
      this.flag = true;
      pass.startX = event.pageX;
      pass.startY = event.pageY;
      pass.temp = {};
      pass.temp.x = pass.x;
      pass.temp.y = pass.y;
      this.passItem = pass;
      // console.log('开始',JSON.stringify(this.startPoint));
      console.log('flag', this.flag);
    },
    onMousemove(event) {
      if (!this.flag) return;
      event.cancelBubble = true;
      var dx = event.pageX - this.passItem.startX,
        dy = event.pageY - this.passItem.startY;
      this.passItem.x = this.passItem.temp.x + dx;
      this.passItem.y = this.passItem.temp.y + dy;
      let horizontalPoints = [];
      let maxWidth = 0;
      let maxHeight = 0;
      this.detail.style.spotPoints.forEach((element) => {
        // if (element.x > maxWidth) {
        //   maxWidth = element.x;
        // }
        // if (element.y > maxHeight) {
        //   maxHeight = element.y;
        // }
        // if(element.x<0){
        //   element.x=0
        // }
        // if(element.y<0){
        //   element.y=0
        // }
        horizontalPoints.push(element.x + ',' + element.y);
      });
      this.points = horizontalPoints.join(' ');
      // this.detail.style.position.w = maxWidth + 20;
      // this.detail.style.position.h = maxHeight + 20;
      this.reDraw();
    },
    onMouseUp(event) {
      this.flag = false;
    },
    getForeColor() {
      return this.detail.style.foreColor;
    },
  },
};
</script>

<style lang="scss" scoped>
.svgClass {
  position: absolute;
  height: 100%;
  width: 100%;
}

.svgBgClass {
  position: absolute;
  height: 100%;
  width: 100%;
}

/* 正向快速流动 */
.shap-forward-quick {
  animation: shine-forward 0.15s infinite linear;
}

/* 正向中速流动 */
.shap-forward-medium {
  animation: shine-forward 0.3s infinite linear;
}

/* 正向慢速流动 */
.shap-forward-slow {
  animation: shine-forward 0.5s infinite linear;
}

/* 反向快速流动 */
.shap-reverse-quick {
  animation: shine-reverse 0.15s infinite linear;
}

/* 反向中速流动 */
.shap-reverse-medium {
  animation: shine-reverse 0.3s infinite linear;
}

/* 反向慢速流动 */
.shap-reverse-slow {
  animation: shine-reverse 0.5s infinite linear;
}

/* 正向流动*/
@keyframes shine-forward {
  0% {
    stroke-dashoffset: 21px;
  }

  100% {
    stroke-dashoffset: 0px;
  }
}

/* 反向流动*/
@keyframes shine-reverse {
  0% {
    stroke-dashoffset: 0px;
  }

  100% {
    stroke-dashoffset: 21px;
  }
}

.view-line-arrow {
  height: 100%;
  width: 100%;
  position: relative;

  .passby {
    position: absolute;
    height: 15px;
    width: 15px;
    border-radius: 50%;
    background-color: white;
    border: 1px solid rgb(34, 14, 223);
    cursor: move;
  }
}
</style>
