<template>
  <el-dialog
    v-model="visible"
    title="流程审批流转全生命周期时间轴"
    width="680px"
    destroy-on-close
  >
    <div v-loading="loading" class="timeline-container">
      <el-empty v-if="!loading && logs.length === 0" description="暂无审批流转记录" />
      <el-timeline v-else>
        <el-timeline-item
          v-for="log in logs"
          :key="log.id"
          :timestamp="log.created_at_display"
          placement="top"
          :type="getNodeType(log.action)"
        >
          <el-card shadow="hover">
            <div class="log-header">
              <span class="action-tag">
                <el-tag :type="getTagType(log.action)">{{ getActionLabel(log.action) }}</el-tag>
              </span>
              <span class="operator-info">
                <strong>{{ log.operator_name }}</strong>
                <span class="role-text" v-if="log.operator_role">（{{ log.operator_role }}）</span>
              </span>
            </div>
            <div class="status-shift">
              状态流转: <el-tag size="small" type="info">{{ log.from_status }}</el-tag>
              &rarr;
              <el-tag size="small" type="success">{{ log.to_status }}</el-tag>
            </div>
            <div class="log-comment" :class="{ 'reject-comment': log.action === 'REJECT' }">
              <strong>意见/理由：</strong>{{ log.comment }}
            </div>
          </el-card>
        </el-timeline-item>
      </el-timeline>
    </div>
  </el-dialog>
</template>

<script setup lang="ts">
import { ref } from 'vue'
import { getWorkflowLogs, type FlowActionLogItem } from '@/api/workflow'

const visible = ref(false)
const loading = ref(false)
const logs = ref<FlowActionLogItem[]>([])

const open = async (appLabel: string, model: string, objectId: string) => {
  visible.value = true
  loading.value = true
  try {
    const res: any = await getWorkflowLogs({
      app_label: appLabel,
      model,
      object_id: objectId
    })
    logs.value = res.data || []
  } catch (err) {
    logs.value = []
  } finally {
    loading.value = false
  }
}

const getNodeType = (action: string) => {
  if (action === 'REJECT') return 'danger'
  if (action === 'APPROVE') return 'success'
  return 'primary'
}

const getTagType = (action: string) => {
  if (action === 'REJECT') return 'danger'
  if (action === 'APPROVE') return 'success'
  if (action === 'SUBMIT') return 'warning'
  return 'info'
}

const getActionLabel = (action: string) => {
  const map: Record<string, string> = {
    SUBMIT: '提交审核',
    APPROVE: '审核通过',
    REJECT: '审批驳回',
    WITHDRAW: '主动撤回',
    TERMINATE: '异常终止'
  }
  return map[action] || action
}

defineExpose({ open })
</script>

<style scoped>
.timeline-container {
  max-height: 500px;
  overflow-y: auto;
  padding: 10px;
}
.log-header {
  display: flex;
  align-items: center;
  gap: 12px;
  margin-bottom: 8px;
}
.role-text {
  color: #909399;
  font-size: 13px;
}
.status-shift {
  font-size: 13px;
  color: #606266;
  margin-bottom: 8px;
}
.log-comment {
  margin-top: 6px;
  font-size: 13px;
  background-color: #f8f9fa;
  padding: 8px 12px;
  border-radius: 4px;
}
.reject-comment {
  background-color: #fef0f0;
  color: #f56c6c;
}
</style>
