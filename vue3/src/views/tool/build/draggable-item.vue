<script>
import { h, resolveComponent } from 'vue';
import { ElCol, ElRow, ElFormItem, ElIcon } from 'element-plus';
import { CopyDocument, Delete } from '@element-plus/icons-vue';
import draggable from 'vuedraggable';
import render from '@/utils/generator/render';

const components = {
  itemBtns(currentCtx, element, index, parent) {
    return [
      h(
        'span',
        {
          class: 'drawing-item-copy',
          title: '复制',
          onClick: (event) => {
            currentCtx.$emit('copyItem', element, parent);
            event.stopPropagation();
          },
        },
        [h(ElIcon, null, { default: () => h(CopyDocument) })]
      ),
      h(
        'span',
        {
          class: 'drawing-item-delete',
          title: '删除',
          onClick: (event) => {
            currentCtx.$emit('deleteItem', index, parent);
            event.stopPropagation();
          },
        },
        [h(ElIcon, null, { default: () => h(Delete) })]
      ),
    ];
  },
};

const layouts = {
  colFormItem(currentCtx, element, index, parent) {
    let className = currentCtx.activeId === element.formId ? 'drawing-item active-from-item' : 'drawing-item';
    if (currentCtx.formConf.unFocusedComponentBorder) className += ' unfocus-bordered';
    return h(
      ElCol,
      {
        span: element.span,
        class: className,
        onClick: (event) => {
          currentCtx.$emit('activeItem', element);
          event.stopPropagation();
        },
      },
      () => [
        h(
          ElFormItem,
          {
            labelWidth: element.labelWidth ? `${element.labelWidth}px` : null,
            label: element.label,
            required: element.required,
          },
          () => [
            h(render, {
              key: element.renderKey,
              conf: element,
              onInput: (event) => {
                element.defaultValue = event;
              },
            }),
          ]
        ),
        ...components.itemBtns(currentCtx, element, index, parent),
      ]
    );
  },
  rowFormItem(currentCtx, element, index, parent) {
    const className = currentCtx.activeId === element.formId ? 'drawing-row-item active-from-item' : 'drawing-row-item';
    let child = renderChildren(currentCtx, element, index, parent);
    if (element.type === 'flex') {
      child = [
        h(
          ElRow,
          { type: element.type, justify: element.justify, align: element.align },
          () => child
        ),
      ];
    }
    return h(ElCol, { span: element.span }, () => [
      h(
        ElRow,
        {
          gutter: element.gutter,
          class: className,
          onClick: (event) => {
            currentCtx.$emit('activeItem', element);
            event.stopPropagation();
          },
        },
        () => [
          h('span', { class: 'component-name' }, element.componentName),
          h(
            draggable,
            {
              list: element.children,
              animation: 340,
              group: 'componentsGroup',
              class: 'drag-wrapper',
              itemKey: 'renderKey',
            },
            {
              item: ({ element: el, index: i }) => {
                const layout = layouts[el.layout];
                if (layout) {
                  return layout(currentCtx, el, i, element.children);
                }
                return null;
              },
            }
          ),
          ...components.itemBtns(currentCtx, element, index, parent),
        ]
      ),
    ]);
  },
};

function renderChildren(currentCtx, element, index, parent) {
  if (!Array.isArray(element.children)) return null;
  return element.children.map((el, i) => {
    const layout = layouts[el.layout];
    if (layout) {
      return layout(currentCtx, el, i, element.children);
    }
    return null;
  });
}

export default {
  components: {
    render,
    draggable,
  },
  props: ['element', 'index', 'drawingList', 'activeId', 'formConf'],
  emits: ['activeItem', 'copyItem', 'deleteItem'],
  render() {
    const layout = layouts[this.element.layout];
    if (layout) {
      return layout(this, this.element, this.index, this.drawingList);
    }
    return null;
  },
};
</script>
<style lang="scss" scoped>
$selectedColor: #fff;
$lighterBlue: #486ff2;
.active-from-item {
  & > .el-form-item {
    background: $selectedColor;
    border-radius: 6px;
  }

  & > .drawing-item-copy,
  & > .drawing-item-delete {
    display: initial;
  }

  & > .component-name {
    color: $lighterBlue;
  }
}
.drawing-item,
.drawing-row-item {
  &:hover {
    & > .el-form-item {
      background: $selectedColor;
      border-radius: 6px;
    }

    & > .drawing-item-copy,
    & > .drawing-item-delete {
      display: initial;
    }
  }

  & > .drawing-item-copy,
  & > .drawing-item-delete {
    display: none;
    position: absolute;
    top: -8px;
    width: 18px;
    height: 18px;
    line-height: 18px;
    text-align: center;
    border-radius: 2px;
    font-size: 11px;
    border: 1px solid;
    cursor: pointer;
    z-index: 2;
    box-sizing: border-box;
  }

  & > .drawing-item-copy {
    right: 38px;
    border-color: #b5c7ff;
    color: #486ff2;
    background: #eef3ff;

    &:hover {
      border-color: #486ff2;
      background: #486ff2;
      color: #fff;
    }
  }

  & > .drawing-item-delete {
    right: 16px;
    border-color: #ffd3d1;
    color: #f56c6c;
    background: #fff1f0;

    &:hover {
      border-color: #f56c6c;
      background: #f56c6c;
      color: #fff;
    }
  }
}
.drawing-item {
  position: relative;
  cursor: move;
  :deep(.el-form-item) {
    margin-bottom: 15px;
  }

  &.unfocus-bordered:not(.activeFromItem) > div:first-child {
    border: 1px dashed #ccc;
  }

  .el-form-item {
    padding: 12px 10px;
  }
}
.drawing-row-item {
  position: relative;
  cursor: move;
  box-sizing: border-box;
  border: 1px dashed #ccc;
  border-radius: 3px;
  padding: 10px 2px 10px 2px;
  margin-bottom: 15px;

  .drawing-row-item {
    margin-bottom: 2px;
  }
  .el-col {
    margin-top: 15px;
  }
  .el-form-item {
    margin-bottom: 0;
  }
  .drag-wrapper {
    min-height: 80px;
  }
  &.active-from-item {
    border: 1px dashed $lighterBlue;
  }

  .component-name {
    position: absolute;
    top: 0;
    left: 0;
    font-size: 12px;
    color: #bbb;
    display: inline-block;
    padding: 0 6px;
  }
}
</style>
