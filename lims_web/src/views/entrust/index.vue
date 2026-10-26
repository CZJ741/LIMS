<template>
  <div class="entrust-container">
    <el-card shadow="never">
      <!-- 顶部筛选栏 -->
      <div class="filter-box">
        <el-form :inline="true" :model="queryForm">
          <el-form-item label="委托单号">
            <el-input v-model="queryForm.entrustNo" placeholder="请输入单号" clearable />
          </el-form-item>
          <el-form-item label="流转状态">
            <el-select v-model="queryForm.status" placeholder="全部状态" clearable style="width: 140px">
              <el-option label="草稿" value="DRAFT" />
              <el-option label="已提交" value="SUBMITTED" />
              <el-option label="评审中" value="AUDITING" />
              <el-option label="已受理" value="ACCEPTED" />
              <el-option label="已驳回" value="REJECTED" />
            </el-select>
          </el-form-item>
          <el-form-item>
            <el-button type="primary" :icon="Search">查询</el-button>
            <el-button :icon="Refresh">重置</el-button>
            <el-button type="success" :icon="Plus">新建委托单</el-button>
          </el-form-item>
        </el-form>
      </div>

      <!-- 委托单数据表格 (符合单文件 150-250 行规范) -->
      <el-table :data="tableData" border stripe style="width: 100%">
        <el-table-column prop="entrust_no" label="委托单编号" width="180" />
        <el-table-column prop="client_name" label="委托单位" min-width="180" />
        <el-table-column prop="test_type" label="检测类别" width="140" />
        <el-table-column prop="contact_person" label="联系人" width="100" />
        <el-table-column prop="status" label="当前状态" width="110">
          <template #default="{ row }">
            <el-tag :type="getStatusTag(row.status)">{{ getStatusLabel(row.status) }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="created_at" label="登记时间" width="170" />
        <el-table-column label="合规操作与流转" width="280" fixed="right">
          <template #default="{ row }">
            <el-button link type="primary" size="small" @click="handleAudit(row)">审核受理</el-button>
            <el-button link type="danger" size="small" @click="handleReject(row)">合规驳回</el-button>
            <el-button link type="info" size="small" @click="handleTimeline(row)">流转留痕</el-button>
          </template>
        </el-table-column>
      </el-table>
    </el-card>

    <!-- 跨业务通用组件调用：驳回弹窗 & 审批留痕时间轴 -->
    <RejectModal ref="rejectModalRef" @confirm="onRejectConfirm" />
    <WorkflowTimeline ref="timelineRef" />
  </div>
</template>

<script setup lang="ts">
import { ref, reactive } from 'vue'
import { Search, Refresh, Plus } from '@element-plus/icons-vue'
import { ElMessage } from 'element-plus'
import RejectModal from '@/components/RejectModal.vue'
import WorkflowTimeline from '@/components/WorkflowTimeline.vue'

const rejectModalRef = ref<InstanceType<typeof RejectModal>>()
const timelineRef = ref<InstanceType<typeof WorkflowTimeline>>()
const currentRecord = ref<any>(null)

const queryForm = reactive({
  entrustNo: '',
  status: ''
})

const tableData = ref([
  {
    id: 'entrust-uuid-001',
    entrust_no: 'WT-202610-001',
    client_name: '某高新技术工业园区污水处理厂',
    test_type: '地表水与工业废水',
    contact_person: '张工',
    status: 'SUBMITTED',
    created_at: '2026-10-24 09:30:00'
  },
  {
    id: 'entrust-uuid-002',
    entrust_no: 'WT-202610-002',
    client_name: '生态环境局国控断面例行监测',
    test_type: '地表水109项',
    contact_person: '李科长',
    status: 'ACCEPTED',
    created_at: '2026-10-24 10:15:00'
  }
])

const getStatusTag = (status: string) => {
  const map: Record<string, string> = {
    DRAFT: 'info',
    SUBMITTED: 'warning',
    AUDITING: '',
    ACCEPTED: 'success',
    REJECTED: 'danger'
  }
  return map[status] || 'info'
}

const getStatusLabel = (status: string) => {
  const map: Record<string, string> = {
    DRAFT: '草稿',
    SUBMITTED: '已提交',
    AUDITING: '评审中',
    ACCEPTED: '已受理',
    REJECTED: '已驳回'
  }
  return map[status] || status
}

const handleAudit = (row: any) => {
  ElMessage.success(`委托单 ${row.entrust_no} 评审通过，已流转至现场采样任务分配`)
  row.status = 'ACCEPTED'
}

const handleReject = (row: any) => {
  currentRecord.value = row
  rejectModalRef.value?.open()
}

const onRejectConfirm = (reason: string) => {
  if (currentRecord.value) {
    currentRecord.value.status = 'REJECTED'
    ElMessage.warning(`委托单 ${currentRecord.value.entrust_no} 已被驳回，理由：${reason}`)
    rejectModalRef.value?.close()
  }
}

const handleTimeline = (row: any) => {
  timelineRef.value?.open('entrust', 'entrusttask', row.id)
}
</script>

<style scoped>
.filter-box {
  margin-bottom: 16px;
}
</style>
