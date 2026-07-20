import { ElMessage, ElMessageBox, ElNotification, ElLoading } from 'element-plus';
import type { ElMessageBoxOptions } from 'element-plus';
import { i18n } from '@/lang';

let loadingInstance: any;

export default {
  // 消息提示
  msg(content: string) {
    ElMessage.info(content);
  },
  // 错误消息
  msgError(content: string) {
    ElMessage.error(content);
  },
  // 成功消息
  msgSuccess(content: string) {
    ElMessage.success(content);
  },
  // 警告消息
  msgWarning(content: string) {
    ElMessage.warning(content);
  },
  // 弹出提示
  alert(content: string, title?: string, options?: ElMessageBoxOptions) {
    return ElMessageBox.alert(content, title || i18n.global.t('systemPrompt'), options);
  },
  // 错误提示
  alertError(content: string, title?: string) {
    return ElMessageBox.alert(content, title || i18n.global.t('systemPrompt'), { type: 'error' });
  },
  // 成功提示
  alertSuccess(content: string, title?: string) {
    return ElMessageBox.alert(content, title || i18n.global.t('systemPrompt'), { type: 'success' });
  },
  // 警告提示
  alertWarning(content: string, title?: string) {
    return ElMessageBox.alert(content, title || i18n.global.t('systemPrompt'), { type: 'warning' });
  },
  // 通知提示
  notify(content: string) {
    ElNotification.info({ message: content });
  },
  // 错误通知
  notifyError(content: string) {
    ElNotification.error({ message: content });
  },
  // 成功通知
  notifySuccess(content: string) {
    ElNotification.success({ message: content });
  },
  // 警告通知
  notifyWarning(content: string) {
    ElNotification.warning({ message: content });
  },
  // 确认窗体
  confirm(content: string, title?: string, options?: ElMessageBoxOptions) {
    const defaultOptions: ElMessageBoxOptions = {
      confirmButtonText: i18n.global.t('confirm'),
      cancelButtonText: i18n.global.t('cancel'),
      type: 'warning',
    };
    return ElMessageBox.confirm(content, title || i18n.global.t('systemPrompt'), {
      ...defaultOptions,
      ...options,
    });
  },
  // 提交内容
  prompt(content: string, title?: string, options?: ElMessageBoxOptions) {
    const defaultOptions: ElMessageBoxOptions = {
      confirmButtonText: i18n.global.t('confirm'),
      cancelButtonText: i18n.global.t('cancel'),
      type: 'warning',
    };
    return ElMessageBox.prompt(content, title || i18n.global.t('systemPrompt'), {
      ...defaultOptions,
      ...options,
    });
  },
  // 打开遮罩层
  loading(content: string) {
    loadingInstance = ElLoading.service({
      lock: true,
      text: content,
      background: 'rgba(0, 0, 0, 0.7)',
    });
  },
  // 关闭遮罩层
  closeLoading() {
    loadingInstance.close();
  },
};
