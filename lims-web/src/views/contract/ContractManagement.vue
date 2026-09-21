<template>
  <div class="contract-management">
    <el-card shadow="never">
      <!-- 搜索栏 -->
      <el-form :inline="true" :model="queryForm">
        <el-form-item label="关键字">
          <el-input v-model="queryForm.keyword" placeholder="合同编号/名称/委托单位" clearable />
        </el-form-item>
        <el-form-item label="状态">
          <el-select v-model="queryForm.status" placeholder="全部状态" clearable style="width: 130px">
            <el-option label="草稿" :value="10" />
            <el-option label="待审核" :value="20" />
            <el-option label="已审核" :value="30" />
            <el-option label="已驳回" :value="40" />
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" icon="Search" @click="fetchData">查询</el-button>
          <el-button icon="Refresh" @click="resetQuery">重置</el-button>
          <el-button type="success" icon="Plus" @click="openCreateDialog">登记合同</el-button>
        </el-form-item>
      </el-form>

      <!-- 数据表格 -->
      <el-table :data="tableData" v-loading="loading" border style="width: 100%">
        <el-table-column prop="contractCode" label="合同编号" width="170" />
        <el-table-column prop="contractName" label="合同名称" min-width="180" show-overflow-tooltip />
        <el-table-column prop="clientCompany" label="委托单位" min-width="160" show-overflow-tooltip />
        <el-table-column prop="totalAmount" label="金额(元)" width="120" align="right">
          <template #default="{ row }">¥{{ Number(row.totalAmount).toLocaleString() }}</template>
        </el-table-column>
        <el-table-column prop="statusName" label="状态" width="100" align="center">
          <template #default="{ row }">
            <el-tag :type="getStatusTag(row.status)">{{ row.statusName }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="startDate" label="生效日期" width="110" />
        <el-table-column prop="endDate" label="截止日期" width="110" />
        <el-table-column label="操作" width="260" fixed="right" align="center">
          <template #default="{ row }">
            <el-button link type="primary" size="small" @click="viewDetail(row)">详情</el-button>
            <el-button v-if="row.status === 'DRAFT' || row.status === 'REJECTED'" link type="warning" size="small" @click="openEditDialog(row)">编辑</el-button>
            <el-button v-if="row.status === 'DRAFT' || row.status === 'REJECTED'" link type="success" size="small" @click="submitAudit(row)">提交审核</el-button>
            <el-button v-if="row.status === 'PENDING'" link type="primary" size="small" @click="openAuditDialog(row)">审批</el-button>
            <el-button v-if="row.status === 'DRAFT'" link type="danger" size="small" @click="handleDelete(row)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>

      <!-- 分页 -->
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

    <!-- 新增/编辑弹窗 -->
    <el-dialog v-model="formVisible" :title="isEdit ? '编辑合同' : '合同登记'" width="650px">
      <el-form :model="formData" :rules="formRules" ref="formRef" label-width="110px">
        <el-form-item label="合同名称" prop="contractName">
          <el-input v-model="formData.contractName" placeholder="例如：2026年XX经开区地表水常规监测合同" />
        </el-form-item>
        <el-form-item label="委托单位" prop="clientCompany">
          <el-input v-model="formData.clientCompany" placeholder="请输入委托单位客户全称" />
        </el-form-item>
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="联系人" prop="clientContact">
              <el-input v-model="formData.clientContact" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="联系电话" prop="clientPhone">
              <el-input v-model="formData.clientPhone" />
            </el-form-item>
          </el-col>
        </el-row>
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="合同总金额" prop="totalAmount">
              <el-input-number v-model="formData.totalAmount" :min="0.01" :precision="2" style="width: 100%" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="有效起止期" prop="dateRange">
              <el-date-picker
                v-model="formData.dateRange"
                type="daterange"
                range-separator="至"
                start-placeholder="开始"
                end-placeholder="截止"
                value-format="YYYY-MM-DD"
                style="width: 100%"
              />
            </el-form-item>
          </el-col>
        </el-row>
        <el-form-item label="备注条款">
          <el-input type="textarea" :rows="3" v-model="formData.remark" placeholder="补充约定条款" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="formVisible = false">取消</el-button>
        <el-button type="primary" :loading="submitLoading" @click="submitForm">保存</el-button>
      </template>
    </el-dialog>

    <!-- 审核/驳回弹窗 -->
    <el-dialog v-model="auditVisible" title="合同财务审核" width="480px">
      <el-form :model="auditForm" label-width="90px">
        <el-form-item label="审核动作">
          <el-radio-group v-model="auditForm.action">
            <el-radio label="PASS">审核通过</el-radio>
            <el-radio label="REJECT">驳回重改</el-radio>
          </el-radio-group>
        </el-form-item>
        <el-form-item :label="auditForm.action === 'PASS' ? '审批批注' : '驳回原因'">
          <el-input
            type="textarea"
            :rows="3"
            v-model="auditForm.reason"
            :placeholder="auditForm.action === 'PASS' ? '通过意见（可选）' : '必填，不得少于10个字'"
          />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="auditVisible = false">取消</el-button>
        <el-button type="primary" @click="handleAuditSubmit">确定</el-button>
      </template>
    </el-dialog>

    <!-- 详情抽屉 -->
    <el-drawer v-model="detailVisible" title="合同详情与执行档案" size="550px">
      <div v-if="currentDetail">
        <el-descriptions :column="1" border>
          <el-descriptions-item label="合同编号">{{ currentDetail.contractCode }}</el-descriptions-item>
          <el-descriptions-item label="合同名称">{{ currentDetail.contractName }}</el-descriptions-item>
          <el-descriptions-item label="委托方单位">{{ currentDetail.clientCompany }}</el-descriptions-item>
          <el-descriptions-item label="联系人">{{ currentDetail.clientContact }} ({{ currentDetail.clientPhone }})</el-descriptions-item>
          <el-descriptions-item label="合同金额">¥{{ currentDetail.totalAmount }}</el-descriptions-item>
          <el-descriptions-item label="有效期">{{ currentDetail.startDate }} ~ {{ currentDetail.endDate }}</el-descriptions-item>
          <el-descriptions-item label="当前状态">
            <el-tag :type="getStatusTag(currentDetail.status)">{{ currentDetail.statusName }}</el-tag>
          </el-descriptions-item>
          <el-descriptions-item label="创建时间">{{ currentDetail.createTime }}</el-descriptions-item>
        </el-descriptions>
      </div>
    </el-drawer>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import request from '@/utils/request'

const loading = ref(false)
const submitLoading = ref(false)
const formVisible = ref(false)
const auditVisible = ref(false)
const detailVisible = ref(false)
const isEdit = ref(false)
const currentId = ref<number | null>(null)
const currentDetail = ref<any>(null)

const tableData = ref([])
const total = ref(0)

const queryForm = reactive({
  keyword: '',
  status: undefined
})

const pageReq = reactive({
  current: 1,
  size: 10
})

const formData = reactive({
  contractName: '',
  clientCompany: '',
  clientContact: '',
  clientPhone: '',
  totalAmount: 10000,
  dateRange: [] as string[],
  remark: ''
})

const auditForm = reactive({
  taskId: '',
  action: 'PASS',
  reason: ''
})

const formRules = {
  contractName: [{ required: true, message: '请输入合同名称', trigger: 'blur' }],
  clientCompany: [{ required: true, message: '请输入委托单位', trigger: 'blur' }],
  clientContact: [{ required: true, message: '请输入联系人', trigger: 'blur' }],
  clientPhone: [{ required: true, message: '请输入联系电话', trigger: 'blur' }]
}

const getStatusTag = (status: string) => {
  switch (status) {
    case 'APPROVED': return 'success'
    case 'PENDING': return 'warning'
    case 'REJECTED': return 'danger'
    default: return 'info'
  }
}

const fetchData = async () => {
  loading.value = true
  try {
    const res: any = await request.get('/api/contract/page', {
      params: { ...pageReq, ...queryForm }
    })
    tableData.value = res.data?.records || []
    total.value = res.data?.total || 0
  } finally {
    loading.value = false
  }
}

const resetQuery = () => {
  queryForm.keyword = ''
  queryForm.status = undefined
  pageReq.current = 1
  fetchData()
}

const openCreateDialog = () => {
  isEdit.value = false
  currentId.value = null
  formData.contractName = ''
  formData.clientCompany = ''
  formData.clientContact = ''
  formData.clientPhone = ''
  formData.totalAmount = 10000
  formData.dateRange = []
  formData.remark = ''
  formVisible.value = true
}

const openEditDialog = (row: any) => {
  isEdit.value = true
  currentId.value = row.id
  formData.contractName = row.contractName
  formData.clientCompany = row.clientCompany
  formData.clientContact = row.clientContact
  formData.clientPhone = row.clientPhone
  formData.totalAmount = row.totalAmount
  formData.dateRange = [row.startDate, row.endDate]
  formData.remark = row.remark || ''
  formVisible.value = true
}

const submitForm = async () => {
  submitLoading.value = true
  try {
    const payload = {
      ...formData,
      startDate: formData.dateRange?.[0] || '2026-09-01',
      endDate: formData.dateRange?.[1] || '2027-08-31'
    }
    if (isEdit.value && currentId.value) {
      await request.put(`/api/contract/${currentId.value}`, payload)
      ElMessage.success('合同修改成功')
    } else {
      await request.post('/api/contract', payload)
      ElMessage.success('合同登记成功')
    }
    formVisible.value = false
    fetchData()
  } finally {
    submitLoading.value = false
  }
}

const viewDetail = async (row: any) => {
  const res: any = await request.get(`/api/contract/${row.id}`)
  currentDetail.value = res.data
  detailVisible.value = true
}

const submitAudit = async (row: any) => {
  await ElMessageBox.confirm(`确定提报合同 [${row.contractCode}] 进入财务审批流程？`, '提交审核', { type: 'info' })
  await request.post(`/api/contract/${row.id}/submit-audit`)
  ElMessage.success('已提报审核')
  fetchData()
}

const openAuditDialog = (row: any) => {
  auditForm.taskId = String(row.id)
  auditForm.action = 'PASS'
  auditForm.reason = ''
  auditVisible.value = true
}

const handleAuditSubmit = async () => {
  if (auditForm.action === 'REJECT' && auditForm.reason.trim().length < 10) {
    ElMessage.warning('驳回原因不得少于10个字！')
    return
  }
  const endpoint = auditForm.action === 'PASS'
    ? `/api/contract/audit/${auditForm.taskId}/approve`
    : `/api/contract/audit/${auditForm.taskId}/reject`
  await request.post(endpoint, { reason: auditForm.reason })
  ElMessage.success('审核处理完成')
  auditVisible.value = false
  fetchData()
}

const handleDelete = async (row: any) => {
  await ElMessageBox.confirm(`确认删除合同 [${row.contractCode}] 吗？`, '删除警告', { type: 'warning' })
  await request.delete(`/api/contract/${row.id}`)
  ElMessage.success('合同已删除')
  fetchData()
}

onMounted(() => {
  fetchData()
})
</script>

<style scoped>
.contract-management {
  padding: 10px;
}
</style>
