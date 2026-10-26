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
      // 系统管理 -> 权限管理
      {
        path: 'system/permission/org/dept',
        name: 'SystemOrgDept',
        component: () => import('@/views/system/permission/org/dept.vue'),
        meta: { title: '组织维护', requiresAuth: true }
      },
      {
        path: 'system/permission/org/user-org',
        name: 'SystemOrgUser',
        component: () => import('@/views/system/permission/org/user-org.vue'),
        meta: { title: '人员维护', requiresAuth: true }
      },
      {
        path: 'system/permission/org/role',
        name: 'SystemOrgRole',
        component: () => import('@/views/system/permission/org/role.vue'),
        meta: { title: '角色管理', requiresAuth: true }
      },
      {
        path: 'system/permission/org/position',
        name: 'SystemOrgPosition',
        component: () => import('@/views/system/permission/org/position.vue'),
        meta: { title: '职位维护', requiresAuth: true }
      },
      {
        path: 'system/permission/user',
        name: 'SystemUser',
        component: () => import('@/views/system/permission/user/index.vue'),
        meta: { title: '用户管理', requiresAuth: true }
      }
    ]
  }
]

const router = createRouter({
  history: createWebHistory(),
  routes
})

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
