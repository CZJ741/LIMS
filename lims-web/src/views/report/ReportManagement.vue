<template>
  <div class="report-management">
    <el-card shadow="never">
      <el-form :inline="true" :model="queryForm">
        <el-form-item label="报告编号">
          <el-input v-model="queryForm.keyword" placeholder="BG-YYYYMMDD-XXXX" clearable />
        </el-form-item>
        <el-form-item>
          <el-button type="primary" icon="Search" @click="fetchData">查询</el-button>
          <el-button icon="Refresh" @click="resetQuery">重置</el-button>
        </el-form-item>
      </el-form>

      <el-table :data="tableData" v-loading="loading" border style="width: 100%">
        <el-table-column prop="reportCode" label="报告编号" width="180" />
        <el-table-column prop="reportName" label="报告名称" min-width="200" show-overflow-tooltip />
        <el-table-column prop="templateName" label="采用模板" width="150" />
        <el-table-column prop="editorName" label="编制人" width="110" />
        <el-table-column prop="statusName" label="状态" width="110" align="center" />
        <el-table-column label="操作" width="220" fixed="right" align="center">
          <template #default="{ row }">
            <el-button link type="primary" size="small" @click="handleAudit(row)">技术签发</el-button>
            <el-button link type="danger" size="small" @click="handleReject(row)">退回编制</el-button>
          </template>
        </el-table-column>
      </el-table>
    </el-card>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import request from '@/utils/request'

const loading = ref(false)
const tableData = ref<any[]>([
  { id: 1, reportCode: 'BG-20260921-0001', reportName: '地表水109项水质全分析综合报告', templateName: '标准水质分析模板', editorName: '报告编制员', statusName: '待审核' }
])
const queryForm = reactive({ keyword: '' })

const fetchData = async () => {
  loading.value = false
}

const resetQuery = () => {
  queryForm.keyword = ''
}

const handleAudit = async (row: any) => {
  await request.post(`/api/report/task/${row.id}/complete`, { reason: '技术审核与印章核签通过' })
  ElMessage.success('报告审核已通过并流转财务')
}

const handleReject = async (row: any) => {
  const { value } = await ElMessageBox.prompt('请输入退回编制的具体修改意见（不少于10字）:', '报告退回', {
    inputPattern: /^.{10,}$/,
    inputErrorMessage: '意见不得少于10个字'
  })
  await request.post(`/api/report/task/${row.id}/reject`, { reason: value })
  ElMessage.success('报告已退回编制员修改')
}

onMounted(() => {
  fetchData()
})
</script>
<style scoped>
.report-management { padding: 10px; }
</style>
