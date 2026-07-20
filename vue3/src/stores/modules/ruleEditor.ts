import { defineStore } from 'pinia';

export const useRuleEditorStore = defineStore('ruleEditor', {
  state: () => ({
    data: {
      nodes: [] as any[],
      lines: [] as any[],
    },
    curNode: {} as any,
    curLine: {} as any,
  }),
  actions: {
    setData(data: any) {
      this.data = data;
    },
    setNodes(data: any[]) {
      this.data.nodes = data;
    },
    setLines(data: any[]) {
      this.data.lines = data;
    },
    setCurNode(data: any) {
      this.curNode = null as any;
      this.curNode = data;
    },
    setCurLine(data: any) {
      this.curLine = null as any;
      this.curLine = data;
    },
    executeNode(data: { op: string; com: any }) {
      switch (data.op) {
        case 'add':
          this.data.nodes.push(data.com);
          break;
        case 'delete':
          this.data.nodes = this.data.nodes.filter((node: any) => {
            return node.id !== data.com.nodeId;
          });
          break;
        default:
          console.log('不执行此命令');
          break;
      }
    },
    executeLine(data: { op: string; com: any }) {
      switch (data.op) {
        case 'add':
          this.data.lines.push(data.com);
          break;
        case 'update':
          this.data.lines = this.data.lines.map((line: any) => {
            if (line.from === data.com.from && line.to === data.com.to) {
              return { ...line, ...data.com };
            }
            return line;
          });
          break;
        default:
          console.log('不执行此命令');
          break;
      }
    },
  },
});
