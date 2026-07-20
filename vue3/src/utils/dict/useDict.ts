import { reactive, computed, type ComputedRef } from 'vue';
import { getDicts } from '@/api/system/dict/data';

/**
 * Vue 3 字典 composable，替代 Vue 2 的 dicts 选项 + mixin 模式
 * @param dictTypes 字典类型列表
 * @returns { dict, ...types } dict.type[dictType] 为字典数组，同时返回各类型的 ComputedRef
 *
 * 用法一（推荐）：
 * const { dict } = useDict('sys_normal_disable', 'sys_show_hide')
 * 模板: v-for="item in dict.type.sys_normal_disable"
 *
 * 用法二（兼容解构）：
 * const { sys_normal_disable } = useDict('sys_normal_disable')
 * 模板: v-for="item in sys_normal_disable"  （Vue 自动解包 ref）
 */
export function useDict(...dictTypes: string[]) {
  const dict = reactive<Record<string, any>>({
    type: {} as Record<string, any[]>,
  });

  const result: Record<string, any> = { dict };

  dictTypes.forEach((type) => {
    dict.type[type] = [];
    // 同时暴露为 computed ref，支持 const { xxx } = useDict('xxx') 解构
    result[type] = computed(() => dict.type[type]);
    getDicts(type)
      .then((res: any) => {
        const data = res.data || res.rows || res;
        if (Array.isArray(data)) {
          dict.type[type] = data.map((p: any) => ({
            label: p.dictLabel,
            value: p.dictValue,
            listClass: p.listClass ?? 'default',
            cssClass: p.cssClass ?? '',
            raw: p,
          }));
        }
      })
      .catch(() => {
        console.warn(`[useDict] failed to load dict: ${type}`);
      });
  });

  return result as { dict: typeof dict } & Record<string, ComputedRef<any[]>>;
}

export default useDict;
