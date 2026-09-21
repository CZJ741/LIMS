<template>
  <div class="settlement-management">
    <el-card shadow="never">
      <el-form :inline="true" :model="queryForm">
        <el-form-item label="结算单号">
          <el-input v-model="queryForm.keyword" placeholder="JS-YYYYMMDD-XXXX" clearable />
        </el-form-item>
        <el-form-item>
          <el-button type="primary" icon="Search" @click="fetchData">查询</el-button>
          <el-button icon="Refresh" @click="resetQuery">重置</el-button>
        </el-form-item>
      </el-form>

      <el-table :data="tableData" v-loading="loading" border style="width: 100%">
        <el-table-column prop="settleCode" label="结算单号" width="180" />
        <el-table-column prop="contractCode" label="关联合同号" width="180" />
        <el-table-column prop="amount" label="应收金额(元)" width="130" align="right">
          <template #default="{ row }">¥{{ Number(row.amount).toLocaleString() }}</template>
        </el-table-column>
        <el-table-column prop="statusName" label="结算状态" width="120" align="center" />
        <el-table-column label="操作" width="220" fixed="right" align="center">
          <template #default="{ row }">
            <el-button link type="primary" size="small" @click="handleConfirm(row)">核销到账</el-button>
            <el-button link type="danger" size="small" @click="handleReject(row)">异议退回</el-button>
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
  { id: 1, settleCode: 'JS-20260921-0001', contractCode: 'HT-20260921-0001', amount: 120000.00, statusName: '结算中' }
])
const queryForm = reactive({ keyword: '' })

const fetchData = async () => {
  loading.value = false
}

const resetQuery = () => {
  queryForm.keyword = ''
}

const handleConfirm = async (row: any) => {
  await request.post(`/api/finance/task/${row.id}/complete`, { reason: '款项全额到账核销完毕' })
  ElMessage.success('财务核销结算完毕')
}

const handleReject = async (row: any) => {
  const { value } = await ElMessageBox.prompt('请输入退回重新核算原因（不少于10字）:', '财务退回', {
    inputPattern: /^.{10,}$/,
    inputErrorMessage: '原因不得少于10个字'
  })
  await request.post(`/api/finance/task/${row.id}/reject`, { reason: value })
  ElMessage.success('结算单已退回')
}

onMounted(() => {
  fetchData()
})
</script>
<style scoped>
.settlement-management { padding: 10px; }
</style>
