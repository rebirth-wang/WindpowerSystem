import { h } from 'vue';
import {
  ElButton,
  ElCascader,
  ElCheckbox,
  ElCheckboxButton,
  ElCheckboxGroup,
  ElColorPicker,
  ElDatePicker,
  ElInput,
  ElInputNumber,
  ElOption,
  ElRadio,
  ElRadioButton,
  ElRadioGroup,
  ElRate,
  ElSelect,
  ElSlider,
  ElSwitch,
  ElTimePicker,
  ElUpload,
} from 'element-plus';
import { makeMap } from '@/utils/index';

// 参考https://github.com/vuejs/vue/blob/v2.6.10/src/platforms/web/server/util.js
const isAttr = makeMap(
  'accept,accept-charset,accesskey,action,align,alt,async,autocomplete,' +
    'autofocus,autoplay,autosave,bgcolor,border,buffered,challenge,charset,' +
    'checked,cite,class,code,codebase,color,cols,colspan,content,http-equiv,' +
    'name,contenteditable,contextmenu,controls,coords,data,datetime,default,' +
    'defer,dir,dirname,disabled,download,draggable,dropzone,enctype,method,for,' +
    'form,formaction,headers,height,hidden,high,href,hreflang,http-equiv,' +
    'icon,id,ismap,itemprop,keytype,kind,label,lang,language,list,loop,low,' +
    'manifest,max,maxlength,media,method,GET,POST,min,multiple,email,file,' +
    'muted,name,novalidate,open,optimum,pattern,ping,placeholder,poster,' +
    'preload,radiogroup,readonly,rel,required,reversed,rows,rowspan,sandbox,' +
    'scope,scoped,seamless,selected,shape,size,type,text,password,sizes,span,' +
    'spellcheck,src,srcdoc,srclang,srcset,start,step,style,summary,tabindex,' +
    'target,title,type,usemap,value,width,wrap'
);

const elementPlusComponentMap = {
  'el-button': ElButton,
  'el-cascader': ElCascader,
  'el-checkbox': ElCheckbox,
  'el-checkbox-button': ElCheckboxButton,
  'el-checkbox-group': ElCheckboxGroup,
  'el-color-picker': ElColorPicker,
  'el-date-picker': ElDatePicker,
  'el-input': ElInput,
  'el-input-number': ElInputNumber,
  'el-option': ElOption,
  'el-radio': ElRadio,
  'el-radio-button': ElRadioButton,
  'el-radio-group': ElRadioGroup,
  'el-rate': ElRate,
  'el-select': ElSelect,
  'el-slider': ElSlider,
  'el-switch': ElSwitch,
  'el-time-picker': ElTimePicker,
  'el-upload': ElUpload,
};

function resolveGeneratorComponent(tag) {
  return elementPlusComponentMap[tag] || tag;
}

function normalizeElementSize(size) {
  if (size === 'medium') return 'default';
  if (size === 'mini') return 'small';
  return size;
}

const componentChild = {
  'el-button': {
    default(conf, key) {
      return conf[key];
    },
  },
  'el-input': {
    prepend(conf, key) {
      return { prepend: () => conf[key] };
    },
    append(conf, key) {
      return { append: () => conf[key] };
    },
  },
  'el-select': {
    options(conf, key) {
      const list = [];
      conf.options.forEach((item) => {
        list.push(h(resolveGeneratorComponent('el-option'), { label: item.label, value: item.value, disabled: item.disabled }));
      });
      return list;
    },
  },
  'el-radio-group': {
    options(conf, key) {
      const list = [];
      conf.options.forEach((item) => {
        if (conf.optionType === 'button') {
          list.push(h(resolveGeneratorComponent('el-radio-button'), { label: item.value }, () => item.label));
        } else {
          list.push(h(resolveGeneratorComponent('el-radio'), { label: item.value, border: conf.border }, () => item.label));
        }
      });
      return list;
    },
  },
  'el-checkbox-group': {
    options(conf, key) {
      const list = [];
      conf.options.forEach((item) => {
        if (conf.optionType === 'button') {
          list.push(h(resolveGeneratorComponent('el-checkbox-button'), { label: item.value }, () => item.label));
        } else {
          list.push(h(resolveGeneratorComponent('el-checkbox'), { label: item.value, border: conf.border }, () => item.label));
        }
      });
      return list;
    },
  },
  'el-upload': {
    'list-type': (conf, key) => {
      const list = [];
      if (conf['list-type'] === 'picture-card') {
        list.push(h('i', { class: 'el-icon-plus' }));
      } else {
        list.push(
          h(
            resolveGeneratorComponent('el-button'),
            { size: 'small', type: 'primary', icon: 'el-icon-upload' },
            () => conf.buttonText
          )
        );
      }
      if (conf.showTip) {
        list.push(
          h('div', { class: 'el-upload__tip' }, `只能上传不超过 ${conf.fileSize}${conf.sizeUnit} 的${conf.accept}文件`)
        );
      }
      return list;
    },
  },
};

export default {
  props: ['conf'],
  emits: ['input'],
  render() {
    const dataObject = {};
    const confClone = JSON.parse(JSON.stringify(this.conf));
    const children = [];
    let slots = {};

    const childObjs = componentChild[confClone.tag];
    if (childObjs) {
      Object.keys(childObjs).forEach((key) => {
        const childFunc = childObjs[key];
        if (confClone[key]) {
          const result = childFunc(confClone, key);
          // If result is an object with slot functions (like prepend/append)
          if (
            result &&
            typeof result === 'object' &&
            !Array.isArray(result) &&
            !(result.__v_isVNode || result._isVue)
          ) {
            Object.assign(slots, result);
          } else {
            if (Array.isArray(result)) {
              children.push(...result);
            } else {
              children.push(result);
            }
          }
        }
      });
    }

    Object.keys(confClone).forEach((key) => {
      const val = confClone[key];
      if (key === 'vModel') {
        dataObject.modelValue = confClone.defaultValue;
        dataObject['onUpdate:modelValue'] = (val) => {
          this.$emit('input', val);
        };
      } else if (key === 'on') {
        // skip
      } else if (key === 'style') {
        dataObject.style = val;
      } else if (key === 'size') {
        dataObject.size = normalizeElementSize(val);
      } else {
        dataObject[key] = val;
      }
    });

    // Build the children/slots
    const childContent = children.length > 0 ? children : undefined;
    if (Object.keys(slots).length > 0) {
      // Merge default slot with named slots
      if (childContent) {
        slots.default = () => childContent;
      }
      return h(resolveGeneratorComponent(this.conf.tag), dataObject, slots);
    }
    return h(resolveGeneratorComponent(this.conf.tag), dataObject, childContent);
  },
};
