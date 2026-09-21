import { createRouter, createWebHistory, type RouteRecordRaw } from 'vue-router'
import { useUserStore } from '@/stores/user'

const routes: RouteRecordRaw[] = [
  {
    path: '/login',
    name: 'Login',
    component: () => import('@/views/login/index.vue'),
    meta: { title: '登录', public: true }
  },
  {
    path: '/',
    component: () => import('@/layouts/DefaultLayout.vue'),
    redirect: '/home',
    children: [
      {
        path: 'home',
        name: 'Home',
        component: () => import('@/views/home/index.vue'),
        meta: { title: '工作台首页' }
      },
      {
        path: 'contract',
        name: 'ContractManagement',
        component: () => import('@/views/contract/ContractManagement.vue'),
        meta: { title: '合同管理' }
      },
      {
        path: 'entrust',
        name: 'EntrustManagement',
        component: () => import('@/views/entrust/EntrustManagement.vue'),
        meta: { title: '委托管理' }
      },
      {
        path: 'sampling',
        name: 'SamplingManagement',
        component: () => import('@/views/sampling/SamplingManagement.vue'),
        meta: { title: '采样管理' }
      },
      {
        path: 'detection',
        name: 'DetectionManagement',
        component: () => import('@/views/detection/DetectionManagement.vue'),
        meta: { title: '检测管理' }
      },
      {
        path: 'report',
        name: 'ReportManagement',
        component: () => import('@/views/report/ReportManagement.vue'),
        meta: { title: '报告管理' }
      },
      {
        path: 'finance',
        name: 'SettlementManagement',
        component: () => import('@/views/finance/SettlementManagement.vue'),
        meta: { title: '财务结算' }
      },
      {
        path: 'trace',
        name: 'TaskTraceView',
        component: () => import('@/views/task/TaskTraceView.vue'),
        meta: { title: '全链路追溯' }
      }
    ]
  },
  {
    path: '/401',
    name: '401',
    component: () => import('@/views/error/401.vue'),
    meta: { title: '401 无权限', public: true }
  },
  {
    path: '/:pathMatch(.*)*',
    name: 'NotFound',
    component: () => import('@/views/error/404.vue'),
    meta: { title: '404 未找到', public: true }
  }
]

const router = createRouter({
  history: createWebHistory(),
  routes
})

// 路由守卫
router.beforeEach((to, _from, next) => {
  const userStore = useUserStore()
  document.title = (to.meta.title ? `${to.meta.title} - ` : '') + 'LIMS 实验室信息管理系统'

  if (to.meta.public) {
    next()
  } else {
    if (!userStore.token) {
      next({ path: '/login', query: { redirect: to.fullPath } })
    } else {
      next()
    }
  }
})

export default router
