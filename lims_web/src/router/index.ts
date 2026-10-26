import { createRouter, createWebHistory } from 'vue-router'
import Layout from '@/layout/index.vue'

const routes = [
  {
    path: '/login',
    name: 'Login',
    component: () => import('@/views/login/index.vue'),
    meta: { title: '登录 - 环境检测 LIMS' }
  },
  {
    path: '/',
    component: Layout,
    redirect: '/dashboard',
    children: [
      {
        path: 'dashboard',
        name: 'Dashboard',
        component: () => import('@/views/dashboard/index.vue'),
        meta: { title: '工作台首页', requiresAuth: true }
      },
      {
        path: 'contract',
        name: 'Contract',
        component: { template: '<div style="padding: 20px;">合同评审模块待挂载</div>' },
        meta: { title: '合同评审', requiresAuth: true }
      },
      {
        path: 'entrust',
        name: 'Entrust',
        component: () => import('@/views/entrust/index.vue'),
        meta: { title: '委托单受理', requiresAuth: true }
      },
      {
        path: 'sampling',
        name: 'Sampling',
        component: { template: '<div style="padding: 20px;">现场采样交接模块待挂载</div>' },
        meta: { title: '现场采样', requiresAuth: true }
      },
      {
        path: 'laboratory',
        name: 'Laboratory',
        component: { template: '<div style="padding: 20px;">实验检测录入模块待挂载</div>' },
        meta: { title: '实验检测', requiresAuth: true }
      },
      {
        path: 'report',
        name: 'Report',
        component: { template: '<div style="padding: 20px;">报告签发归档模块待挂载</div>' },
        meta: { title: '报告签发', requiresAuth: true }
      }
    ]
  }
]

const router = createRouter({
  history: createWebHistory(),
  routes
})

// 路由守卫：未登录跳转到 /login
router.beforeEach((to, _from, next) => {
  const token = localStorage.getItem('lims_token')
  if (to.meta.requiresAuth && !token) {
    next('/login')
  } else if (to.path === '/login' && token) {
    next('/dashboard')
  } else {
    next()
  }
})

export default router
