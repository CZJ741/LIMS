<template>
  <el-container class="layout-container">
    <el-aside width="220px" class="aside-menu">
      <div class="logo-box">
        <el-icon :size="24" color="#409EFF"><Platform /></el-icon>
        <span class="logo-title">LIMS 检验系统</span>
      </div>
      <el-menu
        default-active="/home"
        router
        background-color="#304156"
        text-color="#bfcbd9"
        active-text-color="#409EFF"
      >
        <el-menu-item index="/home">
          <el-icon><DataBoard /></el-icon>
          <span>首页工作台</span>
        </el-menu-item>
        <el-menu-item index="/contract">
          <el-icon><Document /></el-icon>
          <span>合同管理</span>
        </el-menu-item>
        <el-menu-item index="/entrust">
          <el-icon><Tickets /></el-icon>
          <span>委托管理</span>
        </el-menu-item>
        <el-menu-item index="/sampling">
          <el-icon><LocationInformation /></el-icon>
          <span>采样管理</span>
        </el-menu-item>
        <el-menu-item index="/detection">
          <el-icon><Aim /></el-icon>
          <span>检测管理</span>
        </el-menu-item>
        <el-menu-item index="/report">
          <el-icon><Files /></el-icon>
          <span>报告管理</span>
        </el-menu-item>
        <el-menu-item index="/finance">
          <el-icon><Money /></el-icon>
          <span>财务结算</span>
        </el-menu-item>
        <el-menu-item index="/trace">
          <el-icon><Connection /></el-icon>
          <span>全链路留痕追溯</span>
        </el-menu-item>
      </el-menu>
    </el-aside>
    <el-container>
      <el-header class="header-box">
        <div class="breadcrumb-container">
          <el-breadcrumb separator="/">
            <el-breadcrumb-item :to="{ path: '/home' }">首页</el-breadcrumb-item>
            <el-breadcrumb-item>{{ currentRouteName }}</el-breadcrumb-item>
          </el-breadcrumb>
        </div>
        <div class="user-action">
          <el-dropdown @command="handleCommand">
            <span class="user-dropdown-link">
              <el-avatar :size="32" icon="UserFilled" />
              <span class="username">{{ userStore.userInfo?.realName || '管理员' }}</span>
              <el-icon><ArrowDown /></el-icon>
            </span>
            <template #dropdown>
              <el-dropdown-menu>
                <el-dropdown-item command="profile">个人中心</el-dropdown-item>
                <el-dropdown-item divided command="logout">退出登录</el-dropdown-item>
              </el-dropdown-menu>
            </template>
          </el-dropdown>
        </div>
      </el-header>
      <el-main class="main-content">
        <router-view />
      </el-main>
    </el-container>
  </el-container>
</template>

<script setup lang="ts">
import { computed } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { useUserStore } from '@/stores/user'

const route = useRoute()
const router = useRouter()
const userStore = useUserStore()

const currentRouteName = computed(() => {
  return (route.meta.title as string) || '工作台'
})

function handleCommand(cmd: string) {
  if (cmd === 'logout') {
    userStore.logout()
    router.push('/login')
  }
}
</script>

<style scoped>
.layout-container {
  height: 100vh;
}

.aside-menu {
  background-color: #304156;
  overflow-x: hidden;
}

.logo-box {
  height: 60px;
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 10px;
  background-color: #2b3643;
}

.logo-title {
  color: #ffffff;
  font-weight: bold;
  font-size: 16px;
}

.header-box {
  height: 60px;
  border-bottom: 1px solid #e6e6e6;
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 0 20px;
}

.user-dropdown-link {
  cursor: pointer;
  display: flex;
  align-items: center;
  gap: 8px;
}

.username {
  font-size: 14px;
  color: #333333;
}

.main-content {
  background-color: #f0f2f5;
  padding: 20px;
  box-sizing: border-box;
}
</style>
