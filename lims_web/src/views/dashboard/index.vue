<template>
  <div class="dashboard-container">
    <!-- 快捷菜单 / 常用入口 -->
    <el-card shadow="never" class="section-card">
      <template #header>
        <div class="card-header">
          <span class="header-title">
            <el-icon><Menu /></el-icon> 快捷菜单
          </span>
          <span class="header-desc">高频业务直达入口</span>
        </div>
      </template>

      <div class="quick-actions-grid">
        <div
          v-for="item in quickActions"
          :key="item.path"
          class="action-item"
          @click="navigateTo(item.path)"
        >
          <div class="action-icon" :style="{ backgroundColor: item.color }">
            <el-icon size="24" color="#ffffff">
              <component :is="item.icon" />
            </el-icon>
          </div>
          <div class="action-info">
            <div class="action-name">{{ item.name }}</div>
            <div class="action-desc">{{ item.desc }}</div>
          </div>
        </div>
      </div>
    </el-card>

    <!-- 下半部分：双栏布局（待提交任务 vs 待审批任务） -->
    <el-row :gutter="20" class="task-row">
      <!-- 待提交任务 -->
      <el-col :span="12">
        <el-card shadow="never" class="section-card task-card">
          <template #header>
            <div class="card-header">
              <span class="header-title">
                <el-icon><EditPen /></el-icon> 待提交任务
              </span>
              <el-tag size="small" type="info">共 0 项</el-tag>
            </div>
          </template>
          <div class="empty-box">
            <el-empty description="暂无待提交任务" :image-size="80" />
          </div>
        </el-card>
      </el-col>

      <!-- 待审批任务 -->
      <el-col :span="12">
        <el-card shadow="never" class="section-card task-card">
          <template #header>
            <div class="card-header">
              <span class="header-title">
                <el-icon><Stamp /></el-icon> 待审批任务
              </span>
              <el-tag size="small" type="warning">共 0 项</el-tag>
            </div>
          </template>
          <div class="empty-box">
            <el-empty description="暂无待审批任务" :image-size="80" />
          </div>
        </el-card>
      </el-col>
    </el-row>
  </div>
</template>

<script setup lang="ts">
import { useRouter } from 'vue-router'
import {
  Menu,
  EditPen,
  Stamp,
  DocumentChecked,
  Tickets,
  Location,
  DataAnalysis,
  Files
} from '@element-plus/icons-vue'

const router = useRouter()

const quickActions = [
  {
    name: '合同评审',
    desc: '检测方案与报价审批',
    path: '/contract',
    icon: DocumentChecked,
    color: '#409EFF'
  },
  {
    name: '委托单受理',
    desc: '新建或受理检测委托',
    path: '/entrust',
    icon: Tickets,
    color: '#67C23A'
  },
  {
    name: '现场采样',
    desc: '排班派发与点位记录',
    path: '/sampling',
    icon: Location,
    color: '#E6A23C'
  },
  {
    name: '实验检测',
    desc: '分析结果与原始记录',
    path: '/laboratory',
    icon: DataAnalysis,
    color: '#F56C6C'
  },
  {
    name: '报告签发',
    desc: '三级审核与签发归档',
    path: '/report',
    icon: Files,
    color: '#909399'
  }
]

const navigateTo = (path: string) => {
  router.push(path)
}
</script>

<style scoped>
.dashboard-container {
  display: flex;
  flex-direction: column;
  gap: 20px;
}

.section-card {
  border-radius: 6px;
  border: 1px solid #ebeef5;
}

.card-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
}

.header-title {
  font-size: 16px;
  font-weight: 600;
  color: #303133;
  display: flex;
  align-items: center;
  gap: 8px;
}

.header-desc {
  font-size: 13px;
  color: #909399;
}

.quick-actions-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(200px, 1fr));
  gap: 16px;
}

.action-item {
  display: flex;
  align-items: center;
  gap: 14px;
  padding: 14px 16px;
  border: 1px solid #f0f2f5;
  border-radius: 6px;
  cursor: pointer;
  background-color: #fafafa;
  transition: all 0.2s ease;
}

.action-item:hover {
  background-color: #ffffff;
  border-color: #dcdfe6;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.05);
  transform: translateY(-2px);
}

.action-icon {
  width: 44px;
  height: 44px;
  border-radius: 8px;
  display: flex;
  align-items: center;
  justify-content: center;
  flex-shrink: 0;
}

.action-info {
  display: flex;
  flex-direction: column;
}

.action-name {
  font-size: 15px;
  font-weight: 500;
  color: #303133;
}

.action-desc {
  font-size: 12px;
  color: #909399;
  margin-top: 4px;
}

.task-row {
  margin-top: 0;
}

.task-card {
  min-height: 320px;
}

.empty-box {
  padding: 30px 0;
}
</style>
