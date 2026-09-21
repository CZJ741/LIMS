<template>
  <div class="audit-log-container">
    <el-card shadow="never">
      <el-form :inline="true" :model="queryForm">
        <el-form-item label="操作模块">
          <el-input v-model="queryForm.moduleName" placeholder="如: 合同管理" clearable />
        </el-form-item>
        <el-form-item label="操作人">
          <el-input v-model="queryForm.username" placeholder="请输入操作人账号" clearable />
        </el-form-item>
        <el-form-item>
          <el-button type="primary" icon="Search" @click="fetchLogs">查询日志</el-button>
        </el-form-item>
      </el-form>

      <el-table :data="logList" border style="width: 100%">
        <el-table-column prop="id" label="ID" width="80" align="center" />
        <el-table-column prop="moduleName" label="系统模块" width="130" />
        <el-table-column prop="operationType" label="操作类型" width="110" align="center">
          <template #default="{ row }">
            <el-tag :type="row.operationType === 'DELETE' ? 'danger' : 'primary'">{{ row.operationType }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="realName" label="操作人" width="120" />
        <el-table-column prop="requestIp" label="客户端IP" width="130" />
        <el-table-column prop="executionTime" label="耗时(ms)" width="100" align="center" />
        <el-table-column prop="status" label="状态" width="90" align="center">
          <template #default="{ row }">
            <el-tag :type="row.status === 1 ? 'success' : 'danger'">{{ row.status === 1 ? '成功' : '失败' }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="createTime" label="操作时间" min-width="170" />
        <el-table-column label="审计快照" width="120" align="center" fixed="right">
          <template #default="{ row }">
            <el-button link type="primary" size="small" @click="openDetail(row)">查看变更</el-button>
          </template>
        </el-table-column>
      </el-table>
    </el-card>

    <!-- 变更快照对话框 -->
    <el-dialog v-model="detailDialogVisible" title="操作审计前后快照详情" width="700px">
      <el-descriptions border :column="1">
        <el-descriptions-item label="执行方法">{{ currentLog?.methodName }}</el-descriptions-item>
        <el-descriptions-item label="请求地址">{{ currentLog?.requestUri }}</el-descriptions-item>
        <el-descriptions-item label="变更前参数 (Before)">
          <pre class="json-code">{{ currentLog?.paramBefore }}</pre>
        </el-descriptions-item>
        <el-descriptions-item label="变更后响应 (After)">
          <pre class="json-code">{{ currentLog?.paramAfter }}</pre>
        </el-descriptions-item>
      </el-descriptions>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted } from 'vue'

const queryForm = reactive({
  moduleName: '',
  username: ''
})

const logList = ref<any[]>([])
const detailDialogVisible = ref(false)
const currentLog = ref<any>(null)

function fetchLogs() {
  logList.value = [
    {
      id: 1,
      moduleName: '合同管理',
      operationType: 'AUDIT',
      realName: '系统超级管理员',
      requestIp: '127.0.0.1',
      executionTime: 42,
      status: 1,
      createTime: '2024-05-20 14:30:15',
      methodName: 'com.lims.contract.controller.ContractDemoController.auditContract',
      requestUri: '/api/contract/demo/1/audit',
      paramBefore: '[1, "PASSED", "资质齐全，准予通过"]',
      paramAfter: '{"code":200,"message":"操作成功","data":"合同 [ID=1] 审核完成"}'
    }
  ]
}

function openDetail(row: any) {
  currentLog.value = row
  detailDialogVisible.value = true
}

onMounted(() => {
  fetchLogs()
})
</script>

<style scoped>
.json-code {
  background: #f8fafc;
  padding: 8px;
  border-radius: 4px;
  max-height: 200px;
  overflow-y: auto;
  margin: 0;
  font-family: monospace;
}
</style>
