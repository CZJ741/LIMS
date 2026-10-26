import { createRouter, createWebHistory } from 'vue-router'
import Layout from '@/layout/index.vue'

const routes = [
  {
    path: '/',
    component: Layout,
    redirect: '/entrust',
    children: [
      {
        path: 'entrust',
        name: 'Entrust',
        component: () => import('@/views/entrust/index.vue'),
        meta: { title: '委托单受理' }
      },
      {
        path: 'contract',
        name: 'Contract',
        component: { template: '<div style="padding: 20px;">合同评审模块待挂载</div>' },
        meta: { title: '合同评审' }
      },
      {
        path: 'sampling',
        name: 'Sampling',
        component: { template: '<div style="padding: 20px;">现场采样交接模块待挂载</div>' },
        meta: { title: '现场采样' }
      },
      {
        path: 'laboratory',
        name: 'Laboratory',
        component: { template: '<div style="padding: 20px;">实验检测录入模块待挂载</div>' },
        meta: { title: '实验检测' }
      },
      {
        path: 'report',
        name: 'Report',
        component: { template: '<div style="padding: 20px;">报告签发归档模块待挂载</div>' },
        meta: { title: '报告签发' }
      }
    ]
  }
]

const router = createRouter({
  history: createWebHistory(),
  routes
})

export default router
