// 使用 import.meta.glob 替代 require.context
const svgModules = import.meta.glob('../../assets/icons/svg/*.svg', { eager: true });
const re = /\/([^/]+)\.svg$/;
const icons: string[] = Object.keys(svgModules)
  .map((path) => {
    const match = path.match(re);
    return match ? match[1] : '';
  })
  .filter(Boolean);

export default icons;
