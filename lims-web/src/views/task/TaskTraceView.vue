<template>
  <div class="task-trace-view">
    <el-card shadow="never">
      <el-form :inline="true">
        <el-form-item label="业务单号">
          <el-input v-model="businessKey" placeholder="输入合同号/委托号如 HT-20260921-0001" style="width: 280px" />
        </el-form-item>
        <el-form-item>
          <el-button type="primary" icon="Search" @click="fetchTrace">查询全链路留痕</el-button>
        </el-form-item>
      </el-form>

      <div v-if="traceData" style="margin-top: 20px;">
        <h3>业务主单：{{ traceData.businessKey }}</h3>
        <el-divider />

        <div v-for="(stage, idx) in traceData.stages" :key="idx" style="margin-bottom: 30px;">
          <h4>【阶段】{{ stage.stage }} - 流程ID: {{ stage.processInstanceId }} (状态: {{ stage.status }})</h4>
          <el-timeline style="margin-top: 15px;">
            <el-timeline-item
              v-for="(node, nIdx) in stage.nodes"
              :key="nIdx"
              :timestamp="node.operateTime"
              :type="node.actionType === 'REJECT' ? 'danger' : 'primary'"
            >
              <p><b>节点：{{ node.nodeName }}</b></p>
              <p>操作人：{{ node.operatorName || node.operator }} ({{ node.operatorPosition || '职位' }})</p>
              <p>动作：<el-tag :type="node.actionType === 'REJECT' ? 'danger' : 'success'">{{ node.actionType }}</el-tag></p>
              <p v-if="node.commentOrReason">批注/原因：<span style="color: #666;">{{ node.commentOrReason }}</span></p>
            </el-timeline-item>
          </el-timeline>
        </div>
      </div>
    </el-card>
  </div>
</template>

<script setup lang="ts">
import { ref } from 'vue'
import { ElMessage } from 'element-plus'
import request from '@/utils/request'

const businessKey = ref('HT-20260921-0001')
const traceData = ref<any>(null)

const fetchTrace = async () => {
  if (!businessKey.value) {
    ElMessage.warning('请输入业务单据号')
    return
  }
  const res: any = await request.get(`/api/trace/${businessKey.value}`)
  traceData.value = res.data
}
</script>
<style scoped>
.task-trace-view { padding: 10px; }
</style>
