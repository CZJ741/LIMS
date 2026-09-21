<template>
  <div class="entrust-management">
    <el-card shadow="never">
      <el-form :inline="true" :model="queryForm">
        <el-form-item label="关键字">
          <el-input v-model="queryForm.keyword" placeholder="委托单号/委托单位/联系人" clearable />
        </el-form-item>
        <el-form-item>
          <el-button type="primary" icon="Search" @click="fetchData">查询</el-button>
          <el-button icon="Refresh" @click="resetQuery">重置</el-button>
          <el-button type="success" icon="Plus" @click="openCreateDialog">开立委托单</el-button>
        </el-form-item>
      </el-form>

      <el-table :data="tableData" v-loading="loading" border style="width: 100%">
        <el-table-column prop="entrustCode" label="委托单号" width="180" />
        <el-table-column prop="clientCompany" label="委托单位" min-width="180" show-overflow-tooltip />
        <el-table-column prop="clientContact" label="联系人" width="120" />
        <el-table-column prop="clientPhone" label="联系电话" width="130" />
        <el-table-column prop="sampleSourceName" label="样品来源" width="100" align="center" />
        <el-table-column prop="urgencyLevel" label="加急度" width="90" align="center">
          <template #default="{ row }">
            <el-tag :type="row.urgencyLevel === 'URGENT' ? 'danger' : 'info'">{{ row.urgencyLevel }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="statusName" label="状态" width="100" align="center" />
        <el-table-column label="操作" width="200" fixed="right" align="center">
          <template #default="{ row }">
            <el-button link type="primary" size="small" @click="viewDetail(row)">详情</el-button>
            <el-button link type="success" size="small" @click="handleConfirm(row)">确认下单</el-button>
            <el-button link type="danger" size="small" @click="handleCancel(row)">取消</el-button>
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

    <el-dialog v-model="formVisible" title="开立检测委托单" width="600px">
      <el-form :model="formData" :rules="formRules" ref="formRef" label-width="110px">
        <el-form-item label="委托单位" prop="clientCompany">
          <el-input v-model="formData.clientCompany" placeholder="请输入委托单位客户名称" />
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
            <el-form-item label="样品来源" prop="sampleSource">
              <el-select v-model="formData.sampleSource" style="width: 100%">
                <el-option label="现场采样" value="SAMPLING" />
                <el-option label="客户送样" value="DELIVERY" />
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="加急级别" prop="urgencyLevel">
              <el-select v-model="formData.urgencyLevel" style="width: 100%">
                <el-option label="普通" value="NORMAL" />
                <el-option label="加急" value="URGENT" />
              </el-select>
            </el-form-item>
          </el-col>
        </el-row>
        <el-form-item label="特殊要求">
          <el-input type="textarea" :rows="3" v-model="formData.remark" placeholder="分析指标及检测要求说明" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="formVisible = false">取消</el-button>
        <el-button type="primary" @click="submitForm">确定下单</el-button>
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
  clientCompany: '',
  clientContact: '',
  clientPhone: '',
  sampleSource: 'SAMPLING',
  urgencyLevel: 'NORMAL',
  remark: ''
})

const formRules = {
  clientCompany: [{ required: true, message: '请输入委托单位', trigger: 'blur' }],
  clientContact: [{ required: true, message: '请输入联系人', trigger: 'blur' }],
  clientPhone: [{ required: true, message: '请输入联系电话', trigger: 'blur' }]
}

const fetchData = async () => {
  loading.value = true
  try {
    const res: any = await request.get('/api/entrust/page', { params: { ...pageReq, ...queryForm } })
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
  formData.clientCompany = ''
  formData.clientContact = ''
  formData.clientPhone = ''
  formData.sampleSource = 'SAMPLING'
  formData.urgencyLevel = 'NORMAL'
  formData.remark = ''
  formVisible.value = true
}

const submitForm = async () => {
  await request.post('/api/entrust', formData)
  ElMessage.success('开立委托单成功')
  formVisible.value = false
  fetchData()
}

const handleConfirm = async (row: any) => {
  await request.post(`/api/entrust/task/${row.id}/complete`)
  ElMessage.success('委托已确认下单')
  fetchData()
}

const handleCancel = async (row: any) => {
  const { value } = await ElMessageBox.prompt('请输入取消委托的原因（不少于10字）:', '取消委托', {
    inputPattern: /^.{10,}$/,
    inputErrorMessage: '原因不得少于10个字'
  })
  await request.post(`/api/entrust/task/${row.id}/cancel`, { reason: value })
  ElMessage.success('委托已取消')
  fetchData()
}

const viewDetail = (row: any) => {
  ElMessage.info(`查看委托单: ${row.entrustCode}`)
}

onMounted(() => {
  fetchData()
})
</script>
<style scoped>
.entrust-management { padding: 10px; }
</style>
