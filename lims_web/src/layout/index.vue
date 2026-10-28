<template>
  <el-container class="layout-container">
    <el-aside width="240px" class="aside">
      <div class="logo-box">
        <h3>环境检测 LIMS</h3>
      </div>
      <el-menu
        :default-active="activeRoute"
        class="el-menu-vertical"
        router
        unique-opened
        background-color="#304156"
        text-color="#bfcbd9"
        active-text-color="#409EFF"
      >
        <!-- 工作台首页 -->
        <el-menu-item v-if="hasMenuPermission('/dashboard')" index="/dashboard">
          <el-icon><Odometer /></el-icon>
          <span>工作台首页</span>
        </el-menu-item>

        <!-- 客商管理 -->
        <el-sub-menu v-if="hasMenuPermission('/merchant')" index="/merchant">
          <template #title>
            <el-icon><OfficeBuilding /></el-icon>
            <span>客商管理</span>
          </template>
          <el-menu-item v-if="hasMenuPermission('/merchant/customer')" index="/merchant/customer">
            <el-icon><User /></el-icon>
            <span>客户基本信息</span>
          </el-menu-item>
        </el-sub-menu>

        <!-- 合同/委托协议管理 -->
        <el-sub-menu v-if="hasMenuPermission('/contract')" index="/contract">
          <template #title>
            <el-icon><DocumentCopy /></el-icon>
            <span>合同/委托协议管理</span>
          </template>
          <el-menu-item v-if="hasMenuPermission('/contract/contract-reg')" index="/contract/contract-reg">
            <el-icon><List /></el-icon>
            <span>合同登记</span>
          </el-menu-item>
          <el-menu-item v-if="hasMenuPermission('/contract/entrust-reg')" index="/contract/entrust-reg">
            <el-icon><Document /></el-icon>
            <span>委托协议登记</span>
          </el-menu-item>
          <el-menu-item v-if="hasMenuPermission('/contract/my-history')" index="/contract/my-history">
            <el-icon><Tickets /></el-icon>
            <span>我的历史合同/委托协议</span>
          </el-menu-item>
          <el-menu-item v-if="hasMenuPermission('/contract/summary')" index="/contract/summary">
            <el-icon><Collection /></el-icon>
            <span>合同/委托协议书汇总</span>
          </el-menu-item>
          <el-menu-item v-if="hasMenuPermission('/contract/approval')" index="/contract/approval">
            <el-icon><Checked /></el-icon>
            <span>合同/委托协议审批</span>
          </el-menu-item>
          <el-menu-item v-if="hasMenuPermission('/contract/package-maintain')" index="/contract/package-maintain">
            <el-icon><Goods /></el-icon>
            <span>检测包维护</span>
          </el-menu-item>
        </el-sub-menu>

        <!-- 委托单管理 -->
        <el-sub-menu v-if="hasMenuPermission('/entrust')" index="/entrust">
          <template #title>
            <el-icon><DocumentCopy /></el-icon>
            <span>委托单管理</span>
          </template>
          <el-menu-item v-if="hasMenuPermission('/entrust/no-quote-reg')" index="/entrust/no-quote-reg">
            <el-icon><Tickets /></el-icon>
            <span>无报价任务登记</span>
          </el-menu-item>
          <el-menu-item v-if="hasMenuPermission('/entrust/quote-reg')" index="/entrust/quote-reg">
            <el-icon><EditPen /></el-icon>
            <span>有报价任务登记</span>
          </el-menu-item>
          <el-menu-item v-if="hasMenuPermission('/entrust/order')" index="/entrust/order">
            <el-icon><EditPen /></el-icon>
            <span>委托下单</span>
          </el-menu-item>
          <el-menu-item v-if="hasMenuPermission('/entrust/my-info')" index="/entrust/my-info">
            <el-icon><EditPen /></el-icon>
            <span>我的委托单信息</span>
          </el-menu-item>
          <el-menu-item v-if="hasMenuPermission('/entrust/summary')" index="/entrust/summary">
            <el-icon><EditPen /></el-icon>
            <span>委托单汇总</span>
          </el-menu-item>
        </el-sub-menu>

        <!-- 系统管理 -->
        <el-sub-menu v-if="hasMenuPermission('/system')" index="/system">
          <template #title>
            <el-icon><Setting /></el-icon>
            <span>系统管理</span>
          </template>

          <!-- 权限管理 -->
          <el-sub-menu v-if="hasMenuPermission('/system/permission')" index="/system/permission">
            <template #title>
              <el-icon><Lock /></el-icon>
              <span>权限管理</span>
            </template>

            <!-- 组织管理 (包含: 组织维护、人员维护、角色管理、职位维护) -->
            <el-sub-menu v-if="hasMenuPermission('/system/permission/org')" index="/system/permission/org">
              <template #title>
                <el-icon><OfficeBuilding /></el-icon>
                <span>组织管理</span>
              </template>
              <el-menu-item v-if="hasMenuPermission('/system/permission/org/dept')" index="/system/permission/org/dept">
                <el-icon><Folder /></el-icon>
                <span>组织维护</span>
              </el-menu-item>
              <el-menu-item v-if="hasMenuPermission('/system/permission/org/user-org')" index="/system/permission/org/user-org">
                <el-icon><UserFilled /></el-icon>
                <span>人员维护</span>
              </el-menu-item>
              <el-menu-item v-if="hasMenuPermission('/system/permission/org/role')" index="/system/permission/org/role">
                <el-icon><Stamp /></el-icon>
                <span>角色管理</span>
              </el-menu-item>
            </el-sub-menu>

            <!-- 用户管理 -->
            <el-menu-item v-if="hasMenuPermission('/system/permission/user')" index="/system/permission/user">
              <el-icon><User /></el-icon>
              <span>用户管理</span>
            </el-menu-item>
          </el-sub-menu>
        </el-sub-menu>
      </el-menu>
    </el-aside>
    <el-container>
      <el-header class="header">
        <div class="header-left">
          <span>检验检测机构资质认定 (CMA/CNAS) 管理平台</span>
        </div>
        <div class="header-right">
          <!-- 职位切换控件 -->
          <div class="role-switcher-wrap">
            <template v-if="userRoles.length > 1">
              <el-dropdown trigger="click" @command="handleRoleSwitch">
                <span class="role-badge active-cursor">
                  <span class="role-label">当前执业身份：</span>
                  <span class="role-name">{{ currentActiveRoleName }}</span>
                  <el-icon class="el-icon--right"><ArrowDown /></el-icon>
                </span>
                <template #dropdown>
                  <el-dropdown-menu class="role-dropdown-menu">
                    <div class="role-menu-title">切换工作身份（影响菜单与权限）</div>
                    <el-dropdown-item
                      v-for="r in userRoles"
                      :key="r.id"
                      :command="r.id"
                      :class="{ 'is-active-role': r.id === currentActiveRoleId }"
                    >
                      <div class="role-menu-item">
                        <span>{{ r.name }} ({{ r.code }})</span>
                        <span v-if="r.id === currentActiveRoleId" class="current-check">✓ 当前</span>
                      </div>
                    </el-dropdown-item>
                  </el-dropdown-menu>
                </template>
              </el-dropdown>
            </template>
            <template v-else>
              <span class="role-badge">
                <span class="role-label">当前身份：</span>
                <span class="role-name">{{ currentActiveRoleName }}</span>
              </span>
            </template>
          </div>

          <!-- 用户信息与退出登录 -->
          <el-dropdown @command="handleCommand">
            <span class="user-dropdown">
              <el-icon><User /></el-icon>
              <span>{{ realName }}</span>
              <el-icon><ArrowDown /></el-icon>
            </span>
            <template #dropdown>
              <el-dropdown-menu>
                <el-dropdown-item command="profile">个人中心与签名</el-dropdown-item>
                <el-dropdown-item command="logout" divided>退出登录</el-dropdown-item>
              </el-dropdown-menu>
            </template>
          </el-dropdown>
        </div>
      </el-header>
      <el-main class="main">
        <router-view />
      </el-main>
    </el-container>
  </el-container>
</template>

<script setup lang="ts">
import { computed } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { ElMessageBox, ElMessage } from 'element-plus'
import {
  Odometer,
  Setting,
  Lock,
  OfficeBuilding,
  Folder,
  UserFilled,
  Stamp,
  User,
  ArrowDown,
  DocumentCopy,
  List,
  Document,
  Tickets,
  Collection,
  Checked,
  Goods,
  EditPen
} from '@element-plus/icons-vue'
import { useUserStore } from '@/stores/user'

const route = useRoute()
const router = useRouter()
const userStore = useUserStore()

const activeRoute = computed(() => route.path)

const isSuperAdmin = computed(() => {
  const info = userStore.userInfo
  if (!info) return false
  if (info.username === 'admin') return true
  if (info.activeRoleCode === 'ADMIN') return true
  return false
})

const userRoles = computed(() => {
  return userStore.userInfo?.roles || []
})

const currentActiveRoleId = computed(() => {
  return userStore.userInfo?.activeRoleId
})

const currentActiveRoleName = computed(() => {
  return userStore.userInfo?.activeRoleName || (isSuperAdmin.value ? '超级管理员' : '暂无职位')
})

const realName = computed(() => {
  return userStore.userInfo?.realName || userStore.userInfo?.username || '用户'
})

const userPermissions = computed(() => {
  return userStore.userInfo?.permissions || []
})

const hasMenuPermission = (menuPath: string) => {
  if (isSuperAdmin.value) return true
  const perms = userPermissions.value
  // 如果拥有该菜单的具体权限或包含其子路由权限
  return perms.some((p) => p === menuPath || p.startsWith(menuPath + '/'))
}

const handleRoleSwitch = (roleId: string) => {
  if (roleId === currentActiveRoleId.value) return
  const success = userStore.switchRole(roleId)
  if (success) {
    ElMessage.success(`已切换工作身份为【${currentActiveRoleName.value}】`)
    // 切换身份后，检查当前路由是否仍拥有访问权限；若无权限则平滑导航回工作台首页
    if (!hasMenuPermission(route.path)) {
      router.push('/dashboard')
    }
  }
}

const handleCommand = (command: string) => {
  if (command === 'logout') {
    ElMessageBox.confirm('确定要退出系统吗？', '提示', {
      confirmButtonText: '确定退出',
      cancelButtonText: '取消',
      type: 'warning'
    }).then(() => {
      userStore.logout()
      ElMessage.success('已安全退出登录')
      router.push('/login')
    }).catch(() => {})
  }
}
</script>

<style scoped>
.layout-container {
  height: 100vh;
}
.aside {
  background-color: #304156;
  color: #fff;
  transition: width 0.3s;
}
.logo-box {
  height: 60px;
  line-height: 60px;
  text-align: center;
  background-color: #2b3643;
}
.logo-box h3 {
  margin: 0;
  font-size: 16px;
  color: #fff;
  font-weight: 600;
}
.header {
  height: 60px;
  border-bottom: 1px solid #e6e6e6;
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 0 20px;
  background-color: #ffffff;
}
.header-left {
  font-size: 14px;
  color: #606266;
}
.header-right {
  display: flex;
  align-items: center;
  gap: 16px;
}
.role-switcher-wrap {
  display: flex;
  align-items: center;
}
.role-badge {
  display: inline-flex;
  align-items: center;
  background: #f0f7f5;
  border: 1px solid #b2dfdb;
  color: #00796b;
  padding: 4px 10px;
  border-radius: 4px;
  font-size: 13px;
}
.role-badge.active-cursor {
  cursor: pointer;
  transition: all 0.2s;
}
.role-badge.active-cursor:hover {
  background: #e0f2f1;
  border-color: #009688;
}
.role-label {
  color: #606266;
  margin-right: 2px;
}
.role-name {
  font-weight: 600;
  color: #009688;
}
.role-menu-title {
  padding: 6px 16px;
  font-size: 12px;
  color: #909399;
  border-bottom: 1px solid #f0f2f5;
  margin-bottom: 4px;
}
.role-menu-item {
  display: flex;
  justify-content: space-between;
  align-items: center;
  width: 100%;
  min-width: 170px;
  gap: 12px;
}
.current-check {
  color: #009688;
  font-size: 12px;
  font-weight: bold;
}
.is-active-role {
  color: #009688 !important;
  background-color: #f0f9f8;
  font-weight: 500;
}
.user-dropdown {
  cursor: pointer;
  display: flex;
  align-items: center;
  gap: 4px;
}
.main {
  background-color: #f0f2f5;
  padding: 20px;
}
</style>
