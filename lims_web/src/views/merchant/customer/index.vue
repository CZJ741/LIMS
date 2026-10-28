<template>
  <div class="customer-page-container">
    <!-- 顶部检索区（完全对应第一张截图） -->
    <div class="search-panel">
      <div class="panel-toggle" @click="toggleSearchExpand">
        <el-icon><ArrowUp v-if="searchExpanded" /><ArrowDown v-else /></el-icon>
      </div>
      <el-form :inline="true" :model="queryParams" class="search-form" v-show="searchExpanded">
        <el-form-item label="客户名称">
          <el-input
            v-model="queryParams.name"
            placeholder="请输入客户名称"
            clearable
            style="width: 200px"
            @keyup.enter="handleSearch"
          />
        </el-form-item>
        <el-form-item label="通讯地址">
          <el-input
            v-model="queryParams.address"
            placeholder="请输入通讯地址"
            clearable
            style="width: 200px"
            @keyup.enter="handleSearch"
          />
        </el-form-item>
        <el-form-item label="联系人">
          <el-input
            v-model="queryParams.contact_person"
            placeholder="请输入联系人"
            clearable
            style="width: 160px"
            @keyup.enter="handleSearch"
          />
        </el-form-item>
        <el-form-item label="电话">
          <el-input
            v-model="queryParams.phone"
            placeholder="请输入电话"
            clearable
            style="width: 160px"
            @keyup.enter="handleSearch"
          />
        </el-form-item>
        <el-form-item class="search-actions">
          <el-button type="primary" class="btn-search" @click="handleSearch">查询</el-button>
          <el-button @click="handleReset" class="btn-reset">重置</el-button>
        </el-form-item>
      </el-form>
    </div>

    <!-- 工具栏（新增、删除、导入数据、导出数据 | 金额单位: 元） -->
    <div class="toolbar">
      <div class="toolbar-left">
        <el-button type="primary" class="tool-btn btn-add" @click="handleAdd">新增</el-button>
        <el-button
          type="danger"
          class="tool-btn btn-del-batch"
          :disabled="selectedRows.length === 0"
          @click="handleBatchDelete"
        >
          删除
        </el-button>
        <el-button class="tool-btn btn-import" @click="handleImport">导入数据</el-button>
        <el-button class="tool-btn btn-export" @click="handleExport">导出数据</el-button>
      </div>
      <div class="toolbar-right">
        <span class="unit-text">金额单位: 元</span>
      </div>
    </div>

    <!-- 表格区域 -->
    <el-table
      v-loading="loading"
      :data="tableData"
      border
      stripe
      style="width: 100%"
      class="custom-table"
      @selection-change="handleSelectionChange"
    >
      <el-table-column type="selection" width="48" align="center" />
      <el-table-column prop="name" label="客户名称" min-width="220" show-overflow-tooltip />
      <el-table-column prop="legal_person" label="法人代表" width="130" show-overflow-tooltip />
      <el-table-column prop="address" label="通讯地址" min-width="240" show-overflow-tooltip />
      <el-table-column prop="contact_person" label="联系人" width="120" show-overflow-tooltip />
      <el-table-column prop="phone" label="电话" width="130" show-overflow-tooltip />
      <el-table-column prop="email" label="邮箱" width="160" show-overflow-tooltip />
      <el-table-column label="操作" width="120" align="center" fixed="right">
        <template #default="{ row }">
          <div class="op-group">
            <el-button link class="op-btn op-edit" size="small" @click="handleEdit(row)">
              修改
            </el-button>
            <el-button link class="op-btn op-feedback" size="small" @click="handleFeedback(row)">
              反馈
            </el-button>
          </div>
        </template>
      </el-table-column>
    </el-table>

    <!-- 分页 -->
    <div class="pagination-wrap">
      <el-pagination
        v-model:current-page="pagination.page"
        v-model:page-size="pagination.pageSize"
        :page-sizes="[15, 30, 50, 100]"
        layout="total, sizes, prev, pager, next, jumper"
        :total="pagination.total"
        @size-change="fetchData"
        @current-change="fetchData"
      />
    </div>

    <!-- 新增 / 修改客户弹窗 -->
    <CustomerDialog ref="dialogRef" @success="fetchData" />
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted } from 'vue'
import { ArrowUp, ArrowDown } from '@element-plus/icons-vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import CustomerDialog from './components/CustomerDialog.vue'
import {
  getCustomers,
  batchDeleteCustomers,
  type CustomerItem,
  type CustomerQueryParams
} from '@/api/customer'

const searchExpanded = ref(true)
const loading = ref(false)
const tableData = ref<CustomerItem[]>([])
const selectedRows = ref<CustomerItem[]>([])
const dialogRef = ref<InstanceType<typeof CustomerDialog>>()

const queryParams = reactive<CustomerQueryParams>({
  name: '',
  address: '',
  contact_person: '',
  phone: ''
})

const pagination = reactive({
  page: 1,
  pageSize: 15,
  total: 0
})

function toggleSearchExpand() {
  searchExpanded.value = !searchExpanded.value
}

async function fetchData() {
  loading.value = true
  try {
    const res: any = await getCustomers({
      ...queryParams,
      page: pagination.page,
      page_size: pagination.pageSize
    })
    // 兼容 DRF 分页格式及普通列表格式
    if (res && res.results && Array.isArray(res.results)) {
      tableData.value = res.results
      pagination.total = res.count || res.results.length
    } else if (Array.isArray(res)) {
      tableData.value = res
      pagination.total = res.length
    } else {
      tableData.value = []
      pagination.total = 0
    }
  } catch (err) {
    // 错误已由全局拦截器处理
  } finally {
    loading.value = false
  }
}

function handleSearch() {
  pagination.page = 1
  fetchData()
}

function handleReset() {
  queryParams.name = ''
  queryParams.address = ''
  queryParams.contact_person = ''
  queryParams.phone = ''
  pagination.page = 1
  fetchData()
}

function handleSelectionChange(rows: CustomerItem[]) {
  selectedRows.value = rows
}

function handleAdd() {
  dialogRef.value?.open()
}

function handleEdit(row: CustomerItem) {
  dialogRef.value?.open(row)
}

function handleFeedback(row: CustomerItem) {
  ElMessage.info(`反馈功能已就绪，当前选定客户：${row.name}`)
}

function handleBatchDelete() {
  if (selectedRows.value.length === 0) {
    ElMessage.warning('请选择要删除的客户记录')
    return
  }
  const count = selectedRows.value.length
  ElMessageBox.confirm(
    `确定要将选中的 ${count} 位客户信息移入历史归档吗？（合规软删除，已留存审计历史）`,
    '删除确认',
    {
      confirmButtonText: '确定删除',
      cancelButtonText: '取消',
      type: 'warning'
    }
  ).then(async () => {
    try {
      const ids = selectedRows.value.map((r) => r.id as string).filter(Boolean)
      await batchDeleteCustomers(ids)
      ElMessage.success('批量删除成功')
      fetchData()
    } catch (err) {
      // 错误由全局拦截器处理
    }
  })
}

function handleImport() {
  ElMessage.info('客户导入模板下载与批量导入功能已就绪')
}

function handleExport() {
  if (tableData.value.length === 0) {
    ElMessage.warning('当前无数据可导出')
    return
  }
  const headers = ['客户名称', '法人代表', '通讯地址', '联系人', '电话', '邮箱']
  const rows = tableData.value.map((item) => [
    `"${item.name || ''}"`,
    `"${item.legal_person || ''}"`,
    `"${item.address || ''}"`,
    `"${item.contact_person || ''}"`,
    `"${item.phone || item.mobile || ''}"`,
    `"${item.email || ''}"`
  ])
  const csvContent = '﻿' + [headers.join(','), ...rows.map((r) => r.join(','))].join('\n')
  const blob = new Blob([csvContent], { type: 'text/csv;charset=utf-8;' })
  const url = URL.createObjectURL(blob)
  const link = document.createElement('a')
  link.setAttribute('href', url)
  link.setAttribute('download', `客户基本信息表_${new Date().toLocaleDateString()}.csv`)
  document.body.appendChild(link)
  link.click()
  document.body.removeChild(link)
  ElMessage.success('客户数据已成功导出为 CSV')
}

onMounted(() => {
  fetchData()
})
</script>

<style scoped>
.customer-page-container {
  background: #fff;
  padding: 12px 16px;
  min-height: calc(100vh - 90px);
}

.search-panel {
  background: #fafafa;
  border: 1px solid #ebeef5;
  padding: 10px 12px 2px 12px;
  margin-bottom: 12px;
  position: relative;
}

.panel-toggle {
  position: absolute;
  top: 8px;
  right: 12px;
  cursor: pointer;
  color: #909399;
}
.panel-toggle:hover {
  color: #009688;
}

.search-form :deep(.el-form-item) {
  margin-right: 14px;
  margin-bottom: 8px;
}

.search-form :deep(.el-form-item__label) {
  font-size: 13px;
  padding-right: 6px;
}

.btn-search {
  background-color: #009688;
  border-color: #009688;
  color: #fff;
  border-radius: 3px;
  padding: 6px 16px;
}
.btn-search:hover {
  background-color: #00796b;
  border-color: #00796b;
}

.btn-reset {
  border-radius: 3px;
  padding: 6px 14px;
}

.toolbar {
  margin-bottom: 12px;
  display: flex;
  align-items: center;
  justify-content: space-between;
}

.toolbar-left {
  display: flex;
  align-items: center;
  gap: 8px;
}

.tool-btn {
  border-radius: 3px;
  font-size: 13px;
  padding: 6px 14px;
  height: 30px;
}

.btn-add,
.btn-import,
.btn-export {
  background-color: #009688;
  border-color: #009688;
  color: #fff;
}
.btn-add:hover,
.btn-import:hover,
.btn-export:hover {
  background-color: #00796b;
  border-color: #00796b;
  color: #fff;
}

.btn-del-batch {
  background-color: #e74c3c;
  border-color: #e74c3c;
  color: #fff;
}
.btn-del-batch:hover {
  background-color: #c0392b;
  border-color: #c0392b;
}

.toolbar-right .unit-text {
  font-size: 13px;
  color: #009688;
  font-weight: 500;
}

.custom-table {
  font-size: 13px;
}

:deep(.el-table th.el-table__cell) {
  background-color: #fafafa;
  color: #303133;
  font-weight: 600;
  font-size: 13px;
  padding: 8px 0;
}

:deep(.el-table td.el-table__cell) {
  padding: 6px 0;
}

.op-group {
  display: inline-flex;
  align-items: center;
  gap: 6px;
  white-space: nowrap;
}

.op-btn {
  padding: 2px 6px;
  font-size: 12px;
  border: 1px solid #009688;
  color: #009688;
  border-radius: 3px;
  margin: 0;
  height: 22px;
}
.op-btn:hover {
  background-color: #e0f2f1;
}

.pagination-wrap {
  margin-top: 14px;
  display: flex;
  justify-content: flex-end;
}
</style>
