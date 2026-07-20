<script lang="ts">
import anime from 'animejs';
import { animate, getAnimate } from '@/utils/topo/anime';
import {
  getRandomInt,
  getRandomFloat,
  getRandomString,
  judgeSize,
  isInCustomRange,
  isNotInCustomRange,
} from '@/utils/topo/topoUtil';
import request from '@/utils/request';

export default {
  name: 'View',
  props: {
    editMode: {
      type: Boolean,
      default: false,
    },
    selected: {
      type: Boolean,
      default: false,
    },
    detail: {
      type: Object,
      default: {},
    },
    httpPublicSetting: {
      type: Array,
      default: () => [],
    },
  },
  computed: {
    textAlign: function () {
      if (this.detail.style.textAlign == undefined) {
        return 'center';
      } else {
        return this.detail.style.textAlign;
      }
    },
    lineHeight: function () {
      if (this.detail.style.lineHeight == undefined) {
        return this.detail.style.position.h;
      }
      return this.detail.style.lineHeight;
    },
  },
  data() {
    return {
      animateView: null,
      /** 多段动画实例（旋转/闪烁/滑动可并存） */
      animateViews: [],
    };
  },
  watch: {
    'detail.dataAction.hdAction'() {
      this.reinitAnimation();
    },
    'detail.dataAction.xzAction'() {
      this.reinitAnimation();
    },
    'detail.dataAction.ssAction'() {
      this.reinitAnimation();
    },
    'detail.dataAction.duration'() {
      this.reinitAnimation();
    },
    'detail.dataAction.rotationSpeed'() {
      this.reinitAnimation();
    },
    'detail.dataAction.translateList': {
      deep: true,
      handler() {
        this.reinitAnimation();
      },
    },
  },
  mounted() {
    if (this.detail.componentShow.indexOf('动画') > -1 && !this.editMode) {
      this.animationInit();
    }
  },
  methods: {
    /** 组件根节点（旋转/闪烁/滑动作用于此） */
    getAnimationDom() {
      const el = this.$el;
      if (el && el.nodeType === 1 && el.id === this.detail.identifier) {
        return el;
      }
      return document.getElementById(this.detail.identifier);
    },
    /** 动画条件阈值是否已配置（0 为合法值，不能用 truthy 判断） */
    isAnimationJudgeReady(action = this.detail.dataAction) {
      if (!action?.paramJudge) return false;
      if (action.paramJudge === 'between' || action.paramJudge === 'notBetween') {
        return action.paramJudgeDatarangeMin !== '' && action.paramJudgeDatarangeMax !== '';
      }
      return action.paramJudgeData !== '' && action.paramJudgeData !== undefined && action.paramJudgeData !== null;
    },
    /** MQTT 设备号/场景设备号可能一侧为字符串，统一转字符串后严格比较 */
    isSameValue(left, right) {
      if (left === undefined || left === null || right === undefined || right === null) return false;
      return String(left) === String(right);
    },
    /** 预览外层容器（显隐/明暗作用于此，避免只隐藏内部文字） */
    getAnimationTarget() {
      const el = this.getAnimationDom();
      if (!el) return null;
      return el.closest('.render-comp') || el;
    },
    getTimelineTarget() {
      return this.getAnimationTarget() || this.getAnimationDom();
    },
    getAnimationDuration(action = this.detail.dataAction || {}) {
      if (action.duration) {
        return Number(action.duration) * 1000;
      }
      if (action.rotationSpeed == '快') return 500;
      if (action.rotationSpeed == '慢') return 1500;
      return 1000;
    },
    buildTranslateKeyframes(duration) {
      const list = this.detail.dataAction?.translateList || [];
      const frames = [];
      list.forEach((item) => {
        const pos = Number(item.position);
        if (Number.isNaN(pos)) return;
        if (item.direction == '竖直') {
          frames.push({ translateY: pos, duration });
        } else {
          frames.push({ translateX: pos, duration });
        }
      });
      // 单点滑动时补回原点，便于循环可见
      if (frames.length === 1) {
        const f = frames[0];
        if (Object.prototype.hasOwnProperty.call(f, 'translateX')) {
          frames.push({ translateX: 0, duration });
        } else {
          frames.push({ translateY: 0, duration });
        }
      }
      if (frames.length === 0) {
        frames.push({ translateX: 0, duration });
      }
      return frames;
    },
    hasMeaningfulSlide(frames) {
      return frames.some((f) => Number(f.translateX) !== 0 || Number(f.translateY) !== 0);
    },
    disposeAnimateView() {
      const inner = this.getAnimationDom();
      const outer = this.getTimelineTarget();
      if (inner) getAnimate().remove(inner);
      if (outer && outer !== inner) getAnimate().remove(outer);
      (this.animateViews || []).forEach((inst) => inst?.pause());
      this.animateViews = [];
      if (this.animateView) {
        this.animateView.pause();
        this.animateView = null;
      }
    },
    reinitAnimation() {
      if (this.editMode || !this.detail.componentShow?.includes('动画')) return;
      this.disposeAnimateView();
      this.$nextTick(() => {
        this.animationInit();
        const { modelValue } = this.detail.dataAction || {};
        if (modelValue !== undefined && modelValue !== '') {
          if (typeof this.setAnimate === 'function') {
            this.setAnimate(modelValue);
          } else if (typeof this.animatePlay === 'function') {
            this.animatePlay(modelValue);
          }
        }
      });
    },
    evaluateAnimationCondition(val) {
      const action = this.detail.dataAction || {};
      if (action.paramJudge !== 'between' && action.paramJudge !== 'notBetween') {
        return judgeSize(action.paramJudge, val, action.paramJudgeData);
      }
      if (action.paramJudge === 'between') {
        return isInCustomRange(val, action.paramJudgeDatarangeMin, action.paramJudgeDatarangeMax);
      }
      if (action.paramJudge === 'notBetween') {
        return isNotInCustomRange(val, action.paramJudgeDatarangeMin, action.paramJudgeDatarangeMax);
      }
      return false;
    },
    applyXyAction(visible) {
      if (!this.detail.dataAction?.xyAction) return;

      const display = visible ? 'block' : 'none';

      // 动画显隐使用独立运行态，避免污染图层显隐配置
      this.detail.__animationVisible = visible;

      const inner = this.getAnimationDom();
      const outer = this.getAnimationTarget();

      // 与 Vue2 一致：anime.set 作用在组件根节点
      if (inner) {
        try {
          getAnimate().set(inner, { display });
        } catch (_e) {
          // ignore
        }
        inner.style.setProperty('display', display, 'important');
      }

      // 外层 render-comp 整块显隐（避免只藏文字、背景仍显示）
      if (outer) {
        outer.style.setProperty('display', display, 'important');
        const compId = this.detail.identifier;
        if (compId) {
          const byAttr = document.querySelector(`.render-comp[data-comp-id="${compId}"]`);
          if (byAttr && byAttr !== outer) {
            byAttr.style.setProperty('display', display, 'important');
          }
        }
      }
    },
    applyMaAction(dim) {
      // 明暗使用独立运行态驱动外层渲染样式，避免被 Vue 的内联 opacity 覆盖
      this.detail.__animationDim = dim;

      const inner = this.getAnimationDom();
      const outer = this.getAnimationTarget();
      const slideOnOuter = !!this.detail.dataAction?.hdAction;

      if (inner) {
        const filterVal = dim ? 'brightness(0.5)' : 'brightness(1)';
        inner.style.setProperty('filter', filterVal, 'important');
        if (dim) {
          inner.style.setProperty('opacity', '0.55', 'important');
        } else {
          inner.style.removeProperty('opacity');
        }
        try {
          getAnimate().set(inner, { filter: filterVal, opacity: dim ? 0.55 : 1 });
        } catch (_e) {
          // ignore
        }
      }

      // 未启用滑动时，同时压暗外层容器，文字组件上更明显
      if (outer && outer !== inner && !slideOnOuter) {
        if (dim) {
          outer.style.setProperty('opacity', '0.55', 'important');
        } else {
          outer.style.removeProperty('opacity');
        }
      }
    },
    runTimelineAnimation(play) {
      const list = this.animateViews?.length ? this.animateViews : this.animateView ? [this.animateView] : [];
      list.forEach((inst) => {
        if (!inst) return;
        if (play) inst.play();
        else inst.pause();
      });
    },
    /** 根据变量值应用显隐、明暗及时间轴动画（旋转/闪烁/滑动） */
    applyAnimationState(val) {
      const action = this.detail.dataAction;
      if (!action) return;
      const isGd = this.evaluateAnimationCondition(val);
      if (isGd) {
        if (action.xyAction) this.applyXyAction(false);
        this.runTimelineAnimation(true);
        // 旋转/滑动 play 后 anime 可能清掉 filter，明暗需在时间轴之后再次写入
        if (action.maAction) {
          this.applyMaAction(true);
          this.$nextTick(() => {
            requestAnimationFrame(() => this.applyMaAction(true));
          });
        }
      } else {
        this.runTimelineAnimation(false);
        if (action.xyAction) this.applyXyAction(true);
        if (action.maAction) this.applyMaAction(false);
      }
    },
    animationInit() {
      const innerDom = this.getAnimationDom();
      const outerDom = this.getTimelineTarget();
      if (!innerDom) return;

      const action = this.detail.dataAction || {};
      const { xzAction, ssAction, hdAction } = action;

      this.disposeAnimateView();

      const duration = this.getAnimationDuration(action);
      const autoplay = false;
      const loop = true;

      // 仅显隐/明暗：与 Vue2 一样注册 anime 实例，便于 anime.set 稳定生效
      if (!xzAction && !ssAction && !hdAction) {
        if (action.xyAction || action.maAction) {
          this.animateView = animate(innerDom, 'block', [0], [1], [{ translateX: 0 }], duration, autoplay, loop);
          this.animateViews = this.animateView ? [this.animateView] : [];
        }
        return;
      }

      const views = [];

      // 旋转、闪烁：作用在组件根节点
      if (xzAction || ssAction) {
        const rotate = xzAction ? [360] : [0];
        const scale = ssAction ? [0.7, 1, 1.3, 1] : [1];
        views.push(animate(innerDom, 'block', rotate, scale, [{ translateX: 0 }], duration, autoplay, loop));
      }

      // 滑动：作用在外层 render-comp，整块位移且不与 rotate 冲突
      if (hdAction) {
        const keyframes = this.buildTranslateKeyframes(duration);
        if (this.hasMeaningfulSlide(keyframes) && outerDom) {
          views.push(
            anime({
              targets: outerDom,
              keyframes,
              easing: 'linear',
              autoplay,
              loop,
            })
          );
        }
      }

      this.animateViews = views.filter(Boolean);
      this.animateView = this.animateViews[0] || null;
    },
    onResize() {},
    // 获取函数处理结果
    getFunHandlingResult(val) {
      const { filter, funValue, dataMappings } = this.detail.dataBind || {};
      // 先进行函数处理
      let processedValue = val;
      if (filter === 1) {
        const funStr = 'function (value) {\n' + funValue + '\n' + '}';
        const fun = eval('(' + funStr + ')');
        processedValue = fun(val);
      }
      // 如果有数据映射规则，则进行映射处理
      else if (dataMappings && dataMappings.length > 0) {
        for (const { operator, comparisonValue, actionValue } of dataMappings) {
          const comparisonVal = isNaN(Number(comparisonValue)) ? comparisonValue : Number(comparisonValue);
          const processedVal = isNaN(Number(processedValue)) ? processedValue : Number(processedValue);
          const ops = {
            '>': (a, b) => a > b,
            '<': (a, b) => a < b,
            '=': (a, b) => a == b,
            '>=': (a, b) => a >= b,
            '<=': (a, b) => a <= b,
            '!=': (a, b) => a != b,
          };
          //进行比较
          if (ops[operator] && ops[operator](processedVal, comparisonVal)) {
            return actionValue;
          }
        }
      }
      // 返回处理后的值
      return processedValue;
    },
    // 获取随机数据
    getRandomData() {
      const { simValue } = this.detail.dataBind || {};
      if (simValue !== undefined && simValue !== null && simValue !== '') {
        // 范围值: 最小值-最大值，例如：0-100
        if (/^-?\d+--?\d+$/.test(simValue)) {
          const arr = simValue.split(/(?<=^-?\d+)-/).map((item) => Number(item));
          if (arr[0] > arr[1]) {
            return getRandomInt(arr[1], arr[0]);
          } else {
            return getRandomInt(arr[0], arr[1]);
          }
          // 小数位：例如：0-10.1
        } else if (/^(-?\d+)-(-?\d+\.\d+)$/.test(simValue)) {
          const sp = simValue.split(/(?<=^-?\d+)-/) || [];
          const arr = sp.map((item) => parseInt(item, 10));
          const num = sp[1].split('.')[1].length;
          if (arr[0] > arr[1]) {
            return getRandomFloat(arr[1], arr[0], num);
          } else {
            return getRandomFloat(arr[1], arr[0], num);
          }
          // 随机值
        } else if (/^[^ ]+(?:,[^ ]+)*$/.test(simValue) && simValue.split(',').length > 1) {
          const val = getRandomString(simValue);
          if (/^'(?:[^'\\]|\\.)*'$/.test(val)) {
            return val.replace(/^'|'$/g, '').toString();
          } else {
            return Number(val);
          }
        } else {
          return simValue;
        }
      }
    },
    // 获取静态数据
    getStaticData() {
      const { staticValue } = this.detail.dataBind || {};
      if (staticValue) {
        return eval('(' + staticValue + ')');
      }
    },
    // 获取http数据
    getHttpData(dparams: any, dheaders: any) {
      const { dataType, httpSetting, httpFilter, httpSettingId } = this.detail.dataBind || {};
      let source: any = {};
      if (dataType === 4) {
        source = httpSetting;
      } else {
        source = this.httpPublicSetting.find((item: any) => item.id === httpSettingId);
      }
      const { url, type, params, body, headers } = source as any;
      return new Promise((resolve, reject) => {
        request({
          url: url,
          method: type,
          headers: this.getHttpheaders(body, headers, dheaders),
          params: this.getHttpParams(params, dparams),
          data: this.getHttpValues(body),
        })
          .then((res: any) => {
            if (httpFilter) {
              const resp = res;
              const funStr = 'function () {\n' + 'const res = resp;' + '\n' + httpFilter + '\n' + '}';
              let fun = eval('(' + funStr + ')');
              return resolve(fun());
            } else {
              return resolve(res);
            }
          })
          .catch((err: any) => {
            reject(err);
          });
      });
    },
    // 获取body params 值
    getHttpParams(params, dparams) {
      return { ...params, ...dparams };
    },
    // 获取body values 值
    getHttpValues(body) {
      const { type, value } = body || {};
      if (type === 3 && value) {
        return eval('(' + value + ')');
      } else {
        return value;
      }
    },
    // 获取body headers 值
    getHttpheaders(body, headers, dheaders) {
      const { type } = body || {};
      if (type === 2) {
        return { 'Content-Type': 'application/x-www-form-urlencoded', ...headers, ...dheaders };
      } else {
        return { 'Content-Type': 'application/json;charset=utf-8', ...headers, ...dheaders };
      }
    },
  },
};
</script>
