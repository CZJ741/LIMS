<template>
  <div class="dept-page-container">
    <!-- 顶部检索区（严格还原截图设计） -->
    <div class="search-panel">
      <div class="panel-toggle" @click="toggleSearchExpand">
        <el-icon><ArrowUp v-if="searchExpanded" /><ArrowDown v-else /></el-icon>
      </div>
      <el-form :inline="true" :model="queryParams" class="search-form" v-show="searchExpanded">
        <el-form-item label="单位编码">
          <el-input
            v-model="queryParams.code"
            placeholder=""
            clearable
            style="width: 140px"
            @keyup.enter="handleSearch"
          />
        </el-form-item>
        <el-form-item label="单位名称">
          <el-input
            v-model="queryParams.name"
            placeholder=""
            clearable
            style="width: 150px"
            @keyup.enter="handleSearch"
          />
        </el-form-item>
        <el-form-item label="单位类型">
          <el-select
            v-model="queryParams.org_type"
            placeholder="单位类型"
            clearable
            style="width: 130px"
          >
            <el-option label="单位" value="COMPANY" />
            <el-option label="分支机构" value="BRANCH" />
            <el-option label="部门" value="DEPT" />
            <el-option label="检测实验室" value="LAB" />
            <el-option label="采样/业务组" value="TEAM" />
          </el-select>
        </el-form-item>
        <el-form-item label="业务类型">
          <el-select
            v-model="queryParams.business_type"
            placeholder="请选择"
            clearable
            style="width: 130px"
          >
            <el-option label="环境检测" value="ENV" />
            <el-option label="职业卫生" value="OCCUPATIONAL" />
            <el-option label="公共卫生" value="PUBLIC_HEALTH" />
            <el-option label="综合业务" value="COMPREHENSIVE" />
            <el-option label="其他" value="OTHER" />
          </el-select>
        </el-form-item>
        <el-form-item label="是否独立法人">
          <el-select
            v-model="queryParams.is_legal_person"
            placeholder="请选择"
            clearable
            style="width: 120px"
          >
            <el-option label="是" :value="true" />
            <el-option label="否" :value="false" />
          </el-select>
        </el-form-item>
        <el-form-item label="状态">
          <el-select
            v-model="queryParams.is_active"
            placeholder="请选择"
            clearable
            style="width: 110px"
          >
            <el-option label="有效" :value="true" />
            <el-option label="停用" :value="false" />
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" class="btn-search" @click="handleSearch">查询</el-button>
          <el-button @click="handleReset">重置</el-button>
        </el-form-item>
      </el-form>
    </div>

    <!-- 中间操作工具栏 -->
    <div class="toolbar">
      <el-button class="tool-btn" type="primary" @click="handleAddRoot">
        <el-icon class="mr-4"><Plus /></el-icon>新增
      </el-button>
      <el-button class="tool-btn btn-export" type="primary" @click="handleExport">
        <el-icon class="mr-4"><Download /></el-icon>导出数据
      </el-button>
      <el-button class="tool-btn btn-expand" @click="toggleTableExpand">
        {{ isAllExpanded ? '全部折叠' : '全部展开' }}
      </el-button>
    </div>

    <!-- 树形层级表格 -->
    <el-table
      ref="tableRef"
      v-loading="loading"
      :data="tableData"
      row-key="id"
      :default-expand-all="isAllExpanded"
      border
      stripe
      style="width: 100%"
      :tree-props="{ children: 'children', hasChildren: 'hasChildren' }"
      class="dept-table"
    >
      <el-table-column prop="code" label="单位编码" min-width="160" show-overflow-tooltip />
      <el-table-column prop="name" label="单位名称" min-width="220" show-overflow-tooltip />
      <el-table-column prop="org_type_display" label="单位类型" width="130" align="center">
        <template #default="{ row }">
          <el-tag size="small" :type="getOrgTypeTag(row.org_type)">
            {{ row.org_type_display || row.org_type }}
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column prop="company_name" label="所属单位" min-width="200" show-overflow-tooltip>
        <template #default="{ row }">
          {{ row.company_name || row.name }}
        </template>
      </el-table-column>
      <el-table-column prop="parent_name" label="上级单位" min-width="160" show-overflow-tooltip>
        <template #default="{ row }">
          {{ row.parent_name || '-' }}
        </template>
      </el-table-column>
      <el-table-column prop="is_active" label="状态" width="100" align="center">
        <template #default="{ row }">
          <span :class="row.is_active ? 'status-active' : 'status-inactive'">
            {{ row.is_active ? '有效' : '停用' }}
          </span>
        </template>
      </el-table-column>
      <el-table-column label="操作" width="170" fixed="right" align="center">
        <template #default="{ row }">
          <div class="op-group">
            <el-button link class="op-btn op-add" size="small" @click="handleAddSub(row)">新增</el-button>
            <el-button link class="op-btn op-edit" size="small" @click="handleEdit(row)">修改</el-button>
            <el-button
              link
              class="op-btn op-del"
              size="small"
              :disabled="row.code === 'ADMIN_DEPT'"
              @click="handleDelete(row)"
            >
              删除
            </el-button>
          </div>
        </template>
      </el-table-column>
    </el-table>

    <!-- 弹窗表单 -->
    <DeptFormDialog ref="formDialogRef" @success="fetchData" />
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted } from 'vue'
import { ElMessage, ElMessageBox, type TableInstance } from 'element-plus'
import { Plus, Download, ArrowUp, ArrowDown } from '@element-plus/icons-vue'
import {
  getDepartmentList,
  deleteDepartment,
  type DepartmentItem,
  type DepartmentParams
} from '@/api/department'
import DeptFormDialog from './components/DeptFormDialog.vue'

const loading = ref(false)
const searchExpanded = ref(true)
const isAllExpanded = ref(true)
const tableRef = ref<TableInstance>()
const formDialogRef = ref<InstanceType<typeof DeptFormDialog>>()

const queryParams = reactive<DepartmentParams>({
  code: '',
  name: '',
  org_type: '',
  business_type: '',
  is_legal_person: '',
  is_active: ''
})

const rawList = ref<DepartmentItem[]>([])
const tableData = ref<DepartmentItem[]>([])

onMounted(() => {
  fetchData()
})

function toggleSearchExpand() {
  searchExpanded.value = !searchExpanded.value
}

function getOrgTypeTag(type: string) {
  switch (type) {
    case 'COMPANY':
      return 'primary'
    case 'BRANCH':
      return 'warning'
    case 'LAB':
      return 'success'
    case 'TEAM':
      return 'info'
    default:
      return 'info'
  }
}

// 将扁平数组根据 parent 构建为层级树结构
function buildTree(list: DepartmentItem[]): DepartmentItem[] {
  const map: Record<string, DepartmentItem> = {}
  const roots: DepartmentItem[] = []

  list.forEach(item => {
    map[item.id] = { ...item, children: [] }
  })

  list.forEach(item => {
    const node = map[item.id]
    if (item.parent && map[item.parent]) {
      map[item.parent].children!.push(node)
    } else {
      roots.push(node)
    }
  })

  // 如果没有下级，删除空的 children 属性以避免显示展开箭头
  const prune = (nodes: DepartmentItem[]) => {
    nodes.forEach(n => {
      if (n.children && n.children.length > 0) {
        prune(n.children)
      } else {
        delete n.children
      }
    })
  }
  prune(roots)
  return roots
}

async function fetchData() {
  loading.value = true
  try {
    const res: any = await getDepartmentList(queryParams)
    rawList.value = res.data || []
    tableData.value = buildTree(rawList.value)
  } catch (err) {
    // 错误由全局拦截器捕获
  } finally {
    loading.value = false
  }
}

function handleSearch() {
  fetchData()
}

function handleReset() {
  Object.assign(queryParams, {
    code: '',
    name: '',
    org_type: '',
    business_type: '',
    is_legal_person: '',
    is_active: ''
  })
  fetchData()
}

function toggleTableExpand() {
  isAllExpanded.value = !isAllExpanded.value
  fetchData()
}

function handleAddRoot() {
  formDialogRef.value?.open(tableData.value)
}

function handleAddSub(row: DepartmentItem) {
  formDialogRef.value?.open(tableData.value, undefined, row)
}

function handleEdit(row: DepartmentItem) {
  formDialogRef.value?.open(tableData.value, row)
}

function handleDelete(row: DepartmentItem) {
  if (row.code === 'ADMIN_DEPT') {
    ElMessage.warning('系统总控核心部门受最高保护，严禁删除')
    return
  }

  ElMessageBox.confirm(
    `根据 CMA/CNAS 合规准则：删除单位【${row.name}】将校验历史业务与检测溯源。若该机构下存在人员、子部门或历史报告将自动拦截。确认继续删除吗？`,
    '组织删除确认',
    {
      confirmButtonText: '确定删除',
      cancelButtonText: '取消',
      type: 'warning',
      confirmButtonClass: 'el-button--danger'
    }
  ).then(async () => {
    try {
      await deleteDepartment(row.id)
      ElMessage.success(`单位【${row.name}】已成功删除`)
      fetchData()
    } catch (err) {
      // 错误由全局拦截器显示
    }
  })
}

function handleExport() {
  if (!rawList.value.length) {
    ElMessage.warning('暂无数据可导出')
    return
  }

  // 纯客户端 CSV 导出
  const headers = ['单位编码', '单位名称', '单位类型', '所属单位', '上级单位', '是否独立法人', '状态']
  const rows = rawList.value.map(item => [
    `"${item.code}"`,
    `"${item.name}"`,
    `"${item.org_type_display || item.org_type}"`,
    `"${item.company_name || item.name}"`,
    `"${item.parent_name || ''}"`,
    item.is_legal_person ? '是' : '否',
    item.is_active ? '有效' : '停用'
  ])

  const csvContent = '﻿' + [headers.join(','), ...rows.map(r => r.join(','))].join('\n')
  const blob = new Blob([csvContent], { type: 'text/csv;charset=utf-8;' })
  const url = URL.createObjectURL(blob)
  const link = document.createElement('a')
  link.setAttribute('href', url)
  link.setAttribute('download', `组织维护架构数据_${new Date().toLocaleDateString()}.csv`)
  document.body.appendChild(link)
  link.click()
  document.body.removeChild(link)
  ElMessage.success('组织架构数据导出成功')
}
</script>

<style scoped>
.dept-page-container {
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
  margin-right: 12px;
  margin-bottom: 8px;
}

.search-form :deep(.el-form-item__label) {
  font-size: 13px;
  padding-right: 6px;
}

.btn-search {
  background-color: #009688;
  border-color: #009688;
}

.toolbar {
  margin-bottom: 12px;
  display: flex;
  align-items: center;
  gap: 8px;
}

.tool-btn {
  background-color: #009688;
  border-color: #009688;
  color: #fff;
  border-radius: 3px;
  font-size: 13px;
  padding: 6px 14px;
}

.btn-export {
  background-color: #009688;
  border-color: #009688;
}

.btn-expand {
  background-color: #fff;
  border-color: #dcdfe6;
  color: #606266;
}

.mr-4 {
  margin-right: 4px;
}

.status-active {
  color: #67c23a;
  font-weight: 500;
}

.status-inactive {
  color: #909399;
}

.op-group {
  display: inline-flex;
  align-items: center;
  flex-wrap: nowrap;
  gap: 4px;
  white-space: nowrap;
}

.op-btn {
  padding: 2px 6px;
  font-size: 12px;
  border: 1px solid #dcdfe6;
  border-radius: 3px;
  margin: 0;
}

.op-add {
  color: #009688;
  border-color: #009688;
}

.op-edit {
  color: #409eff;
  border-color: #409eff;
}

.op-del {
  color: #f56c6c;
  border-color: #f56c6c;
}

.op-del:disabled {
  color: #c0c4cc;
  border-color: #ebeef5;
}
</style>
