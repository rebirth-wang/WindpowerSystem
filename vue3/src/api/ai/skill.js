import request from '@/utils/request';

// 查询 AI 技能目录
export function listAiSkill() {
  return request({
    url: '/ai/skill/list',
    method: 'get',
  });
}
