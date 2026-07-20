// Vue 3: SVG icons are loaded via vite-plugin-svg-icons
// No need for require.context - Vite handles SVG loading
// SvgIcon component is registered globally in main.ts

// Import all SVG icons using Vite's import.meta.glob
const svgModules = import.meta.glob('./svg/*.svg', { eager: true });

export default svgModules;
