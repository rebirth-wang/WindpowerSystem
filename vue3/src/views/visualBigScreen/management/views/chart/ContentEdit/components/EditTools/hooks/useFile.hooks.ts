import { ref, nextTick } from 'vue'
import { ElMessage } from 'element-plus'
import { UploadCustomRequestOptions } from 'naive-ui'
import i18n from '@/lang'
import { FileTypeEnum } from '@vb/enums/fileTypeEnum'
import { readFile, goDialog, JSONParse } from '@vb/utils'
import { useSync } from '@vb/views/chart/hooks/useSync.hook'

export const useFile = () => {
  const importUploadFileListRef = ref()
  const { updateComponent } = useSync()
  // 上传-前置
  //@ts-ignore
  const importBeforeUpload = ({ file }) => {
    importUploadFileListRef.value = []
    const type = file.file.type
    if (type !== FileTypeEnum.JSON && type !== FileTypeEnum.TXT) {
      ElMessage.warning(i18n.global.t('visualBigScreen.management-674210-845'))
      return false
    }
    return true
  }

  // 上传-导入
  const importCustomRequest = (options: UploadCustomRequestOptions) => {
    const { file } = options
    nextTick(() => {
      if (file.file) {
        readFile(file.file).then((fileData: any) => {
          goDialog({
            message: i18n.global.t('visualBigScreen.management-674210-1634'),
            positiveText: i18n.global.t('visualBigScreen.management-674210-1641'),
            negativeText: i18n.global.t('visualBigScreen.management-674210-1642'),
            negativeButtonProps: { type: 'info', ghost: false },
            // 新增
            onPositiveCallback: async () => {
              try {
                fileData = JSONParse(fileData)
                await updateComponent(fileData, false, true)
                ElMessage.success(i18n.global.t('visualBigScreen.management-674210-1635'))
              } catch {
                ElMessage.error(i18n.global.t('visualBigScreen.management-674210-1643'))
              }
            },
            // 覆盖
            onNegativeCallback: async () => {
              try {
                fileData = JSONParse(fileData)
                await updateComponent(fileData, true, true)
                ElMessage.success(i18n.global.t('visualBigScreen.management-674210-1635'))
              } catch {
                ElMessage.error(i18n.global.t('visualBigScreen.management-674210-1643'))
              }
            }
          })
        })
      } else {
        ElMessage.error(i18n.global.t('visualBigScreen.management-674210-1644'))
      }
    })
  }

  return {
    importUploadFileListRef,
    importBeforeUpload,
    importCustomRequest
  }
}
