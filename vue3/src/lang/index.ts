import { createI18n } from 'vue-i18n';
import defaultSettings from '@/settings';

// Element Plus内语言包
import zhLocale from 'element-plus/es/locale/lang/zh-cn';
import enLocale from 'element-plus/es/locale/lang/en';

//动态导入语言文件
const modulesZh = import.meta.glob('./zh-CN/**/*.json', { eager: true, import: 'default' });
const modulesEn = import.meta.glob('./en-US/**/*.json', { eager: true, import: 'default' });

const zh: Record<string, any> = {};
const en: Record<string, any> = {};

const validPlaceholderRE = /^(?:\d+|[A-Za-z_$][\w$]*(?:\.[A-Za-z_$][\w$]*)*)$/;
const literalPlaceholderRE = /^\s*'.*'\s*$/;

const escapeLiteralBraces = (value: string) => {
  return value.replace(/\{([^{}]+)\}/g, (match, content: string) => {
    const placeholder = content.trim();
    if (validPlaceholderRE.test(placeholder) || literalPlaceholderRE.test(placeholder)) {
      return match;
    }
    return `{'${match.replace(/\\/g, '\\\\').replace(/'/g, "\\'")}'}`;
  });
};

const escapeInvalidPlaceholders = (messages: Record<string, any>) => {
  Object.keys(messages).forEach((key) => {
    const value = messages[key];
    if (typeof value === 'string') {
      messages[key] = escapeLiteralBraces(value);
    } else if (value && typeof value === 'object' && !Array.isArray(value)) {
      escapeInvalidPlaceholders(value);
    }
  });
};

//合所有中文模块
Object.keys(modulesZh).forEach((key) => {
  const mod = modulesZh[key] as any;
  Object.assign(zh, mod);
});

//合所有英文模块
Object.keys(modulesEn).forEach((key) => {
  const mod = modulesEn[key] as any;
  Object.assign(en, mod);
});

escapeInvalidPlaceholders(zh);
escapeInvalidPlaceholders(en);

const langs = {
  'zh-CN': { ...zhLocale, ...zh, language: '简体中文' },
  'en-US': { ...enLocale, ...en, language: 'English' },
};

const i18n = createI18n({
  legacy: false, // Vue 3 Composition API
  locale: defaultSettings.language,
  fallbackLocale: 'zh-CN',
  messages: langs,
});

// 加载自定义语言（延迟加载以避免循环依赖）
setTimeout(async () => {
  try {
    const { listLanguage } = await import('@/api/system/language');
    const res: any = await listLanguage({ pageNum: 1, pageSize: 1000 });
    res.rows?.forEach((row: any) => {
      if (langs[row.language]) {
        langs[row.language].language = row.langName;
      }
    });
  } catch (e) {
    // 忽略加载失败
  }
}, 0);

export { i18n, langs };
export default i18n;
