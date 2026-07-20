// 使用 Vite 的 import.meta.glob 替代 require.context
const svgModules = import.meta.glob('../../../assets/icons/svg/*.svg', { eager: false });
const re = /([^/]+)\.svg$/;
const svgIcons = Object.keys(svgModules).map((path) => {
  const match = path.match(re);
  return match ? match[1] : '';
}).filter(Boolean);

export default svgIcons;
