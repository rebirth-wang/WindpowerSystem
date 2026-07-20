import { defineComponent, h } from 'vue';

import CaptionText from '@/components/DragEditor/components/entitys/captionText/index.vue';
import TextDisplay1 from '@/components/DragEditor/components/entitys/textDisplay1/index.vue';
import NumDisplay1 from '@/components/DragEditor/components/entitys/numDisplay1/index.vue';
import NumControl1 from '@/components/DragEditor/components/entitys/numControl1/index.vue';
import MulStatusControl1 from '@/components/DragEditor/components/entitys/mulStatusControl1/index.vue';
import BtnControl1 from '@/components/DragEditor/components/entitys/btnControl1/index.vue';
import HistoricalDataGauge from '@/components/DragEditor/components/entitys/historicalDataGauge/index.vue';

import Decorate from '@/components/DragEditor/components/styles/decorate/index.vue';
import Componenmanagement from '@/components/DragEditor/components/styles/componenmanagement/index.vue';
import CaptionTextStyle from '@/components/DragEditor/components/styles/captionTextStyle/index.vue';
import TextDisplay1Style from '@/components/DragEditor/components/styles/textDisplay1Style/index.vue';
import NumDisplay1Style from '@/components/DragEditor/components/styles/numDisplay1Style/index.vue';
import NumControl1Style from '@/components/DragEditor/components/styles/numControl1Style/index.vue';
import MulStatusControl1Style from '@/components/DragEditor/components/styles/mulStatusControl1Style/index.vue';
import BtnControl1Style from '@/components/DragEditor/components/styles/btnControl1Style/index.vue';
import HistoricalDataGaugeStyle from '@/components/DragEditor/components/styles/historicalDataGaugeStyle/index.vue';

const Placementarea = defineComponent({
  name: 'placementarea',
  setup() {
    return () =>
      h('div', {
        style: {
          height: '52px',
          margin: '6px 0',
          border: '1px dashed #155bd4',
          background: 'rgba(21, 91, 212, 0.08)',
          borderRadius: '4px',
          boxSizing: 'border-box',
        },
      });
  },
});

export const dragComponentRegistry: Record<string, any> = {
  placementarea: Placementarea,
  captionText: CaptionText,
  textDisplay1: TextDisplay1,
  numDisplay1: NumDisplay1,
  numControl1: NumControl1,
  mulStatusControl1: MulStatusControl1,
  btnControl1: BtnControl1,
  historicalDataGauge: HistoricalDataGauge,
  decorate: Decorate,
  componenmanagement: Componenmanagement,
  captionTextStyle: CaptionTextStyle,
  textDisplay1Style: TextDisplay1Style,
  numDisplay1Style: NumDisplay1Style,
  numControl1Style: NumControl1Style,
  mulStatusControl1Style: MulStatusControl1Style,
  btnControl1Style: BtnControl1Style,
  historicalDataGaugeStyle: HistoricalDataGaugeStyle,
};
