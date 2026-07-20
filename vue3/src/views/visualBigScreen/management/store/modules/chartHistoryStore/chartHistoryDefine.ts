import i18n from '@/lang'
import { HistoryTargetTypeEnum, HistoryActionTypeEnum } from './chartHistoryStore.d'

export const historyActionTypeName = {
  [HistoryActionTypeEnum.ADD]: i18n.global.t('visualBigScreen.management-674210-413'),
  [HistoryActionTypeEnum.DELETE]: i18n.global.t('visualBigScreen.management-674210-105'),
  [HistoryActionTypeEnum.UPDATE]: i18n.global.t('visualBigScreen.management-674210-700'),
  [HistoryActionTypeEnum.MOVE]: i18n.global.t('visualBigScreen.management-674210-701'),
  [HistoryActionTypeEnum.PASTE]: i18n.global.t('visualBigScreen.management-674210-92'),
  [HistoryActionTypeEnum.COPY]: i18n.global.t('visualBigScreen.management-674210-90'),
  [HistoryActionTypeEnum.CUT]: i18n.global.t('visualBigScreen.management-674210-91'),
  [HistoryActionTypeEnum.TOP]: i18n.global.t('visualBigScreen.management-674210-702'),
  [HistoryActionTypeEnum.BOTTOM]: i18n.global.t('visualBigScreen.management-674210-703'),
  [HistoryActionTypeEnum.UP]: i18n.global.t('visualBigScreen.management-674210-704'),
  [HistoryActionTypeEnum.DOWN]: i18n.global.t('visualBigScreen.management-674210-705'),
  [HistoryActionTypeEnum.GROUP]: i18n.global.t('visualBigScreen.management-674210-706'),
  [HistoryActionTypeEnum.UN_GROUP]: i18n.global.t('visualBigScreen.management-674210-707'),
  [HistoryActionTypeEnum.LOCK]: i18n.global.t('visualBigScreen.management-674210-73'),
  [HistoryActionTypeEnum.UNLOCK]: i18n.global.t('visualBigScreen.management-674210-72'),
  [HistoryActionTypeEnum.HIDE]: i18n.global.t('visualBigScreen.management-674210-89'),
  [HistoryActionTypeEnum.SHOW]: i18n.global.t('visualBigScreen.management-674210-323'),

  [HistoryTargetTypeEnum.CANVAS]: i18n.global.t('visualBigScreen.management-674210-708')
}
