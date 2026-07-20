import { ref, nextTick } from 'vue'
import { ElMessage } from 'element-plus'
import i18n from '@/lang'
import { UploadCustomRequestOptions } from 'naive-ui'
import { FileTypeEnum } from '@vb/enums/fileTypeEnum'
import { readFile, downloadTextFile, JSONStringify, JSONParse } from '@vb/utils'

export const useFile = (targetData: any) => {
  const uploadFileListRef = ref()
  const t = i18n.global.t

  //@ts-ignore
  const beforeUpload = ({ file }) => {
    uploadFileListRef.value = []
    const type = file.file.type
    if (type !== FileTypeEnum.JSON && type !== FileTypeEnum.TXT) {
      ElMessage.warning(t('visualBigScreen.management-674210-845'))
      return false
    }
    return true
  }

  // 自定义上传操作
  const customRequest = (options: UploadCustomRequestOptions) => {
    const { file } = options
    nextTick(() => {
      if (file.file) {
        readFile(file.file).then((fileData: any) => {
          targetData.value.option.dataset = JSONParse(fileData)
        })
      } else {
        ElMessage.error(t('visualBigScreen.management-674210-846'))
      }
    })
  }

  // 下载文件
  const download = () => {
    try {
      ElMessage.success(t('visualBigScreen.management-674210-847'))
      downloadTextFile(JSONStringify(targetData.value.option.dataset), undefined, 'json')
    } catch (error) {
      ElMessage.error(t('visualBigScreen.management-674210-848'))
    }
  }
  return {
    uploadFileListRef,
    beforeUpload,
    customRequest,
    download
  }
}
