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
        path: 'system/permission/user',
        name: 'SystemUser',
        component: () => import('@/views/system/permission/user/index.vue'),
        meta: { title: '用户管理', requiresAuth: true }
      },
      // 客商管理
      {
        path: 'merchant/customer',
        name: 'MerchantCustomer',
        component: () => import('@/views/merchant/customer/index.vue'),
        meta: { title: '客户基本信息', requiresAuth: true }
      },
      // 合同/委托协议管理
      {
        path: 'contract/contract-reg',
        name: 'ContractReg',
        component: () => import('@/views/contract/pages/contract-reg.vue'),
        meta: { title: '合同登记', requiresAuth: true }
      },
      {
        path: 'contract/entrust-reg',
        name: 'EntrustReg',
        component: () => import('@/views/contract/pages/entrust-reg.vue'),
        meta: { title: '委托协议登记', requiresAuth: true }
      },
      {
        path: 'contract/my-history',
        name: 'ContractMyHistory',
        component: () => import('@/views/contract/pages/my-history.vue'),
        meta: { title: '我的历史合同/委托协议', requiresAuth: true }
      },
      {
        path: 'contract/summary',
        name: 'ContractSummary',
        component: () => import('@/views/contract/pages/summary.vue'),
        meta: { title: '合同/委托协议书汇总', requiresAuth: true }
      },
      {
        path: 'contract/approval',
        name: 'ContractApproval',
        component: () => import('@/views/contract/pages/approval.vue'),
        meta: { title: '合同/委托协议审批', requiresAuth: true }
      },
      {
        path: 'contract/package-maintain',
        name: 'ContractPackageMaintain',
        component: () => import('@/views/contract/pages/package-maintain.vue'),
        meta: { title: '检测包维护', requiresAuth: true }
      },
      // 委托单管理
      {
        path: 'entrust/no-quote-reg',
        name: 'EntrustNoQuoteReg',
        component: () => import('@/views/entrust/pages/no-quote-reg.vue'),
        meta: { title: '无报价任务登记', requiresAuth: true }
      },
      {
        path: 'entrust/quote-reg',
        name: 'EntrustQuoteReg',
        component: () => import('@/views/entrust/pages/quote-reg.vue'),
        meta: { title: '有报价任务登记', requiresAuth: true }
      },
      {
        path: 'entrust/order',
        name: 'EntrustOrder',
        component: () => import('@/views/entrust/pages/order.vue'),
        meta: { title: '委托下单', requiresAuth: true }
      },
      {
        path: 'entrust/my-info',
        name: 'EntrustMyInfo',
        component: () => import('@/views/entrust/pages/my-info.vue'),
        meta: { title: '我的委托单信息', requiresAuth: true }
      },
      {
        path: 'entrust/summary',
        name: 'EntrustSummary',
        component: () => import('@/views/entrust/pages/summary.vue'),
        meta: { title: '委托单汇总', requiresAuth: true }
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
