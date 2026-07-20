import { i18n } from '@/lang';

const menus = [
  {
    id: 'textDisplay',
    name: i18n.global.t('dragEditor.565720-0'),
    icon: 'Postcard',
    json: 'textDisplayJson',
  },
  {
    id: 'numDisplay',
    name: i18n.global.t('dragEditor.565720-1'),
    icon: 'CreditCard',
    json: 'numDisplayJson',
  },
  {
    id: 'numControl',
    name: i18n.global.t('dragEditor.565720-2'),
    icon: 'PriceTag',
    json: 'numControlJson',
  },
  {
    id: 'mulStatusControl',
    name: i18n.global.t('dragEditor.565720-3'),
    icon: 'Operation',
    json: 'mulStatusControlJson',
  },
  {
    id: 'btnControl',
    name: i18n.global.t('dragEditor.565720-4'),
    icon: 'Open',
    json: 'btnControlJson',
  },
  {
    id: 'historicalData',
    name: i18n.global.t('dragEditor.565720-5'),
    icon: 'DataLine',
    json: 'historicalDataJson',
  },
];

export default menus;
