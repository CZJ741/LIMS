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
        <el-menu-item index="/dashboard">
          <el-icon><Odometer /></el-icon>
          <span>工作台首页</span>
        </el-menu-item>

        <!-- 系统管理 -->
        <el-sub-menu index="/system">
          <template #title>
            <el-icon><Setting /></el-icon>
            <span>系统管理</span>
          </template>

          <!-- 权限管理 -->
          <el-sub-menu index="/system/permission">
            <template #title>
              <el-icon><Lock /></el-icon>
              <span>权限管理</span>
            </template>

            <!-- 组织管理 (包含: 组织维护、人员维护、角色管理、职位维护) -->
            <el-sub-menu index="/system/permission/org">
              <template #title>
                <el-icon><OfficeBuilding /></el-icon>
                <span>组织管理</span>
              </template>
              <el-menu-item index="/system/permission/org/dept">
                <el-icon><Folder /></el-icon>
                <span>组织维护</span>
              </el-menu-item>
              <el-menu-item index="/system/permission/org/user-org">
                <el-icon><UserFilled /></el-icon>
                <span>人员维护</span>
              </el-menu-item>
              <el-menu-item index="/system/permission/org/role">
                <el-icon><Stamp /></el-icon>
                <span>角色管理</span>
              </el-menu-item>
              <el-menu-item index="/system/permission/org/position">
                <el-icon><Postcard /></el-icon>
                <span>职位维护</span>
              </el-menu-item>
            </el-sub-menu>

            <!-- 用户管理 -->
            <el-menu-item index="/system/permission/user">
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
          <el-dropdown @command="handleCommand">
            <span class="user-dropdown">
              {{ displayName }} <el-icon><ArrowDown /></el-icon>
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
  Postcard,
  User,
  ArrowDown
} from '@element-plus/icons-vue'
import { useUserStore } from '@/stores/user'

const route = useRoute()
const router = useRouter()
const userStore = useUserStore()

const activeRoute = computed(() => route.path)

const displayName = computed(() => {
  return userStore.userInfo?.realName
    ? `${userStore.userInfo.realName} (${userStore.userInfo.role})`
    : '系统管理员 (ADMIN)'
})

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
