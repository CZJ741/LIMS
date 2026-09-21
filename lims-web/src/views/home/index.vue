<template>
  <div class="home-container">
    <el-card shadow="never" class="box-card">
      <template #header>
        <div class="card-header">
          <span>实验室运营概览（全流程六大阶段）</span>
        </div>
      </template>
      <el-row :gutter="20">
        <el-col :span="4" v-for="item in stages" :key="item.name">
          <div class="stage-stat-box">
            <div class="stage-title">{{ item.name }}</div>
            <div class="stage-count">{{ item.count }}</div>
            <div class="stage-desc">{{ item.desc }}</div>
          </div>
        </el-col>
      </el-row>
    </el-card>
  </div>
</template>

<script setup lang="ts">
import { ref } from 'vue'

const stages = ref([
  { name: '合同管理', count: 12, desc: '待评审合同' },
  { name: '委托下单', count: 28, desc: '待受理委托' },
  { name: '采样管理', count: 9, desc: '现场采样中' },
  { name: '检测分析', count: 45, desc: '实验检测中' },
  { name: '报告管理', count: 16, desc: '待审核报告' },
  { name: '财务结算', count: 8, desc: '待对账结算' }
])
</script>

<style scoped>
.home-container {
  display: flex;
  flex-direction: column;
  gap: 20px;
}

.stage-stat-box {
  background: #f8fafc;
  border-radius: 6px;
  padding: 20px 15px;
  text-align: center;
  border: 1px solid #e2e8f0;
}

.stage-title {
  font-size: 14px;
  color: #64748b;
}

.stage-count {
  font-size: 28px;
  font-weight: bold;
  color: #1e293b;
  margin: 10px 0;
}

.stage-desc {
  font-size: 12px;
  color: #94a3b8;
}
</style>
