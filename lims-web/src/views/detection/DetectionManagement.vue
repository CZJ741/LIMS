<template>
  <div class="detection-management">
    <el-card shadow="never">
      <el-form :inline="true" :model="queryForm">
        <el-form-item label="任务编号">
          <el-input v-model="queryForm.keyword" placeholder="JC-YYYYMMDD-XXXX" clearable />
        </el-form-item>
        <el-form-item>
          <el-button type="primary" icon="Search" @click="fetchData">查询</el-button>
          <el-button icon="Refresh" @click="resetQuery">重置</el-button>
          <el-button type="success" icon="Plus" @click="openCreateDialog">下发检测任务</el-button>
        </el-form-item>
      </el-form>

      <el-table :data="tableData" v-loading="loading" border style="width: 100%">
        <el-table-column prop="taskCode" label="检测任务编号" width="180" />
        <el-table-column prop="labHeadName" label="下发人" width="130" />
        <el-table-column prop="assignTime" label="派发时间" width="170" />
        <el-table-column prop="deadline" label="截止完成时间" width="170" />
        <el-table-column prop="statusName" label="状态" width="110" align="center" />
        <el-table-column label="操作" width="220" fixed="right" align="center">
          <template #default="{ row }">
            <el-button link type="primary" size="small" @click="handlePass(row)">复审合格</el-button>
            <el-button link type="danger" size="small" @click="handleReject(row)">质控驳回</el-button>
          </template>
        </el-table-column>
      </el-table>

      <div style="margin-top: 15px; display: flex; justify-content: flex-end;">
        <el-pagination
          v-model:current-page="pageReq.current"
          v-model:page-size="pageReq.size"
          :total="total"
          layout="total, sizes, prev, pager, next, jumper"
          @size-change="fetchData"
          @current-change="fetchData"
        />
      </div>
    </el-card>

    <el-dialog v-model="formVisible" title="下发检测任务" width="500px">
      <el-form :model="formData" label-width="100px">
        <el-form-item label="关联委托ID">
          <el-input-number v-model="formData.entrustId" :min="1" style="width: 100%" />
        </el-form-item>
        <el-form-item label="完成截止期">
          <el-date-picker
            v-model="formData.deadline"
            type="datetime"
            placeholder="选择完成截止时间"
            value-format="YYYY-MM-DDTHH:mm:ss"
            style="width: 100%"
          />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="formVisible = false">取消</el-button>
        <el-button type="primary" @click="submitForm">确认下发</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import request from '@/utils/request'

const loading = ref(false)
const formVisible = ref(false)
const tableData = ref([])
const total = ref(0)
const queryForm = reactive({ keyword: '' })
const pageReq = reactive({ current: 1, size: 10 })

const formData = reactive({
  entrustId: 1,
  deadline: '2026-09-25T18:00:00'
})

const fetchData = async () => {
  loading.value = true
  try {
    const res: any = await request.get('/api/detection/page', { params: { ...pageReq, ...queryForm } })
    tableData.value = res.data?.records || []
    total.value = res.data?.total || 0
  } finally {
    loading.value = false
  }
}

const resetQuery = () => {
  queryForm.keyword = ''
  pageReq.current = 1
  fetchData()
}

const openCreateDialog = () => {
  formVisible.value = true
}

const submitForm = async () => {
  await request.post('/api/detection', formData)
  ElMessage.success('检测任务下发成功')
  formVisible.value = false
  fetchData()
}

const handlePass = async (row: any) => {
  await request.post(`/api/detection/task/${row.id}/complete`)
  ElMessage.success('数据复审合格')
  fetchData()
}

const handleReject = async (row: any) => {
  const { value } = await ElMessageBox.prompt('请输入质控驳回原因（不少于10字）:', '数据复审驳回', {
    inputPattern: /^.{10,}$/,
    inputErrorMessage: '原因不得少于10个字'
  })
  await request.post(`/api/detection/task/${row.id}/reject`, { reason: value })
  ElMessage.success('已驳回分析员重测')
  fetchData()
}

onMounted(() => {
  fetchData()
})
</script>
<style scoped>
.detection-management { padding: 10px; }
</style>
