<template>
  <div class="sampling-management">
    <el-card shadow="never">
      <el-form :inline="true" :model="queryForm">
        <el-form-item label="关键字">
          <el-input v-model="queryForm.keyword" placeholder="任务编号/采样地址" clearable />
        </el-form-item>
        <el-form-item>
          <el-button type="primary" icon="Search" @click="fetchData">查询</el-button>
          <el-button icon="Refresh" @click="resetQuery">重置</el-button>
          <el-button type="success" icon="Plus" @click="openCreateDialog">创建采样任务</el-button>
        </el-form-item>
      </el-form>

      <el-table :data="tableData" v-loading="loading" border style="width: 100%">
        <el-table-column prop="taskCode" label="采样编号" width="180" />
        <el-table-column prop="samplingSite" label="现场点位地址" min-width="200" show-overflow-tooltip />
        <el-table-column prop="planStartTime" label="计划开始时间" width="170" />
        <el-table-column prop="planEndTime" label="计划截止时间" width="170" />
        <el-table-column prop="statusName" label="状态" width="110" align="center" />
        <el-table-column label="操作" width="220" fixed="right" align="center">
          <template #default="{ row }">
            <el-button link type="primary" size="small" @click="handleComplete(row)">审核合格</el-button>
            <el-button link type="danger" size="small" @click="handleReject(row)">驳回重采</el-button>
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

    <el-dialog v-model="formVisible" title="创建采样任务" width="550px">
      <el-form :model="formData" label-width="110px">
        <el-form-item label="关联委托ID">
          <el-input-number v-model="formData.entrustId" :min="1" style="width: 100%" />
        </el-form-item>
        <el-form-item label="采样现场点位">
          <el-input v-model="formData.samplingSite" placeholder="请输入采样地址或点位说明" />
        </el-form-item>
        <el-form-item label="计划时间段">
          <el-date-picker
            v-model="formData.timeRange"
            type="datetimerange"
            range-separator="至"
            start-placeholder="开始时间"
            end-placeholder="截止时间"
            value-format="YYYY-MM-DDTHH:mm:ss"
            style="width: 100%"
          />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="formVisible = false">取消</el-button>
        <el-button type="primary" @click="submitForm">确定创建</el-button>
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
  samplingSite: '',
  timeRange: [] as string[]
})

const fetchData = async () => {
  loading.value = true
  try {
    const res: any = await request.get('/api/sampling/page', { params: { ...pageReq, ...queryForm } })
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
  formData.samplingSite = ''
  formData.timeRange = []
  formVisible.value = true
}

const submitForm = async () => {
  await request.post('/api/sampling', {
    entrustId: formData.entrustId,
    samplingSite: formData.samplingSite,
    planStartTime: formData.timeRange?.[0] || '2026-09-22T09:00:00',
    planEndTime: formData.timeRange?.[1] || '2026-09-22T17:00:00'
  })
  ElMessage.success('采样任务已创建')
  formVisible.value = false
  fetchData()
}

const handleComplete = async (row: any) => {
  await request.post(`/api/sampling/task/${row.id}/complete`)
  ElMessage.success('采样记录审核合格入库')
  fetchData()
}

const handleReject = async (row: any) => {
  const { value } = await ElMessageBox.prompt('请输入驳回现场重采的具体原因（不少于10字）:', '采样审核驳回', {
    inputPattern: /^.{10,}$/,
    inputErrorMessage: '原因不得少于10个字'
  })
  await request.post(`/api/sampling/task/${row.id}/reject`, { reason: value })
  ElMessage.success('已驳回现场重采')
  fetchData()
}

onMounted(() => {
  fetchData()
})
</script>
<style scoped>
.sampling-management { padding: 10px; }
</style>
