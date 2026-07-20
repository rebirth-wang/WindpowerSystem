import anime from 'animejs';

// 显隐、旋转、闪烁、滑动特效，display:block/none
export function animate(targets, display, rotate, scale, translates, duration, autoplay, loop) {
  const keyframes = Array.isArray(translates) ? translates : [];
  const hasSlide =
    keyframes.length > 0 && keyframes.some((k) => Number(k.translateX) !== 0 || Number(k.translateY) !== 0);
  const hasRotate = Array.isArray(rotate) && rotate.some((v) => Number(v) !== 0);
  const hasBlink = Array.isArray(scale) && scale.length > 1;

  // 仅滑动：不要与 rotate:[0]、scale:[1] 混用，否则 keyframes 不生效
  if (hasSlide && !hasRotate && !hasBlink) {
    return anime({
      targets,
      keyframes,
      easing: 'linear',
      autoplay,
      loop,
    });
  }

  return anime({
    targets,
    display,
    rotate,
    scale,
    keyframes,
    duration,
    autoplay,
    easing: 'linear',
    loop,
  });
}

//获取当前特效的实例
export function getAnimate() {
  return anime;
}
