<template>
  <div class="role-page">
    <!-- 顶部检索区 -->
    <div class="search-panel">
      <div class="panel-toggle">
        <el-icon><ArrowUp /></el-icon>
      </div>
      <el-form :inline="true" :model="queryForm" class="search-form">
        <el-form-item label="角色名称">
          <el-input
            v-model="queryForm.name"
            placeholder=""
            clearable
            style="width: 140px"
          />
        </el-form-item>
        <el-form-item label="角色编码">
          <el-input
            v-model="queryForm.code"
            placeholder=""
            clearable
            style="width: 140px"
          />
        </el-form-item>
        <el-form-item label="备注说明">
          <el-input
            v-model="queryForm.description"
            placeholder=""
            clearable
            style="width: 160px"
          />
        </el-form-item>
        <el-form-item class="actions">
          <el-button type="primary" class="btn-search" @click="handleSearch">
            查询
          </el-button>
          <el-button class="btn-reset" @click="handleReset">重置</el-button>
        </el-form-item>
      </el-form>
    </div>

    <!-- 快捷操作工具栏 -->
    <div class="toolbar">
      <el-button type="primary" class="tool-btn" :icon="Plus" @click="handleCreate">
        新建角色
      </el-button>
      <el-button class="tool-btn-plain" :icon="Refresh" @click="fetchData">
        刷新
      </el-button>
    </div>

    <!-- 数据表格 -->
    <div class="table-container">
      <el-table
        v-loading="loading"
        :data="filteredList"
        border
        stripe
        size="small"
        style="width: 100%"
        :header-cell-style="{ background: '#f5f7fa', color: '#606266', fontWeight: 'bold' }"
      >
        <el-table-column type="index" label="序号" width="55" align="center" />
        <el-table-column prop="name" label="角色名称" min-width="160">
          <template #default="{ row }">
            <span class="role-name">{{ row.name }}</span>
            <el-tag
              v-if="row.code === 'ADMIN'"
              size="small"
              type="danger"
              effect="plain"
              style="margin-left: 6px"
            >
              系统核心
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="code" label="角色编码" min-width="140">
          <template #default="{ row }">
            <span class="code-badge">{{ row.code }}</span>
          </template>
        </el-table-column>
        <el-table-column prop="description" label="备注说明" min-width="240" show-overflow-tooltip>
          <template #default="{ row }">
            <span>{{ row.description || '-' }}</span>
          </template>
        </el-table-column>
        <el-table-column prop="is_active" label="状态" width="80" align="center">
          <template #default="{ row }">
            <el-tag :type="row.is_active !== false ? 'success' : 'info'" size="small">
              {{ row.is_active !== false ? '启用' : '停用' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="created_at" label="创建时间" min-width="150" align="center">
          <template #default="{ row }">
            <span>{{ formatTime(row.created_at) }}</span>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="240" fixed="right" align="center">
          <template #default="{ row }">
            <div class="op-group">
              <el-button
                class="op-btn op-view"
                link
                @click="handleEdit(row)"
              >
                修改
              </el-button>
              <el-button
                class="op-btn op-auth"
                link
                @click="handlePermission(row)"
              >
                菜单权限
              </el-button>

              <!-- 非超级管理员核心角色才展示 停用/启用 与 删除 按钮 -->
              <template v-if="row.code !== 'ADMIN'">
                <el-button
                  link
                  :class="['op-btn', row.is_active !== false ? 'op-status-disable' : 'op-status-enable']"
                  @click="handleToggleStatus(row)"
                >
                  {{ row.is_active !== false ? '停用' : '启用' }}
                </el-button>

                <el-button
                  class="op-btn op-del"
                  link
                  @click="handleDelete(row)"
                >
                  删除
                </el-button>
              </template>
            </div>
          </template>
        </el-table-column>
      </el-table>
    </div>

    <!-- 弹窗与抽屉组件 -->
    <RoleFormDialog ref="formDialogRef" @success="fetchData" />
    <RolePermissionDrawer ref="permissionDrawerRef" @success="fetchData" />
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, computed, onMounted } from 'vue'
import { Plus, Refresh, ArrowUp } from '@element-plus/icons-vue'
import { ElMessageBox, ElMessage } from 'element-plus'
import { getRoleList, updateRole, deleteRole, type RoleItem } from '@/api/role'
import RoleFormDialog from './components/RoleFormDialog.vue'
import RolePermissionDrawer from './components/RolePermissionDrawer.vue'

const loading = ref(false)
const roleList = ref<RoleItem[]>([])
const formDialogRef = ref<InstanceType<typeof RoleFormDialog>>()
const permissionDrawerRef = ref<InstanceType<typeof RolePermissionDrawer>>()

const queryForm = reactive({
  name: '',
  code: '',
  description: ''
})

const activeFilter = reactive({
  name: '',
  code: '',
  description: ''
})

const filteredList = computed(() => {
  return roleList.value.filter((item) => {
    const matchName = !activeFilter.name || item.name.toLowerCase().includes(activeFilter.name.toLowerCase())
    const matchCode = !activeFilter.code || item.code.toLowerCase().includes(activeFilter.code.toLowerCase())
    const matchDesc = !activeFilter.description || (item.description && item.description.toLowerCase().includes(activeFilter.description.toLowerCase()))
    return matchName && matchCode && matchDesc
  })
})

async function fetchData() {
  loading.value = true
  try {
    const res: any = await getRoleList()
    roleList.value = res.data || []
  } catch (err: any) {
    // 统一拦截器捕获
  } finally {
    loading.value = false
  }
}

function handleSearch() {
  activeFilter.name = queryForm.name.trim()
  activeFilter.code = queryForm.code.trim()
  activeFilter.description = queryForm.description.trim()
}

function handleReset() {
  queryForm.name = ''
  queryForm.code = ''
  queryForm.description = ''
  handleSearch()
}

function handleCreate() {
  formDialogRef.value?.open()
}

function handleEdit(row: RoleItem) {
  formDialogRef.value?.open(row)
}

function handlePermission(row: RoleItem) {
  permissionDrawerRef.value?.open(row)
}

async function handleToggleStatus(row: RoleItem) {
  if (row.code === 'ADMIN') {
    ElMessage.warning('系统超级管理员核心角色受系统保护，不可停用！')
    return
  }
  const nextStatus = row.is_active === false ? true : false
  const actionText = nextStatus ? '启用' : '停用'
  try {
    await ElMessageBox.confirm(
      `确定要${actionText}角色【${row.name}】（编码：${row.code}）吗？${!nextStatus ? '停用后该角色将无法赋予新人员，且关联业务流转将受到限制。' : '启用后该角色恢复正常使用。'}`,
      '提示',
      {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: nextStatus ? 'info' : 'warning'
      }
    )
    await updateRole(row.id, { is_active: nextStatus })
    ElMessage.success(`角色已成功${actionText}`)
    fetchData()
  } catch (e: any) {
    // 取消
  }
}

async function handleDelete(row: RoleItem) {
  if (row.code === 'ADMIN') {
    ElMessage.warning('系统超级管理员核心角色受保护，不可删除！')
    return
  }
  try {
    await ElMessageBox.confirm(
      `确定要删除角色【${row.name}】（编码：${row.code}）吗？删除后该角色下的人员将失去该身份。`,
      '危险操作确认',
      {
        confirmButtonText: '确定删除',
        cancelButtonText: '取消',
        type: 'warning',
        confirmButtonClass: 'el-button--danger'
      }
    )
    await deleteRole(row.id)
    ElMessage.success('角色已成功删除')
    fetchData()
  } catch (e: any) {
    // 取消删除
  }
}

function formatTime(val?: string) {
  if (!val) return '-'
  return val.replace('T', ' ').substring(0, 19)
}

onMounted(() => {
  fetchData()
})
</script>

<style scoped>
.role-page {
  background: #fff;
  padding: 12px 16px;
  min-height: calc(100vh - 90px);
}
.search-panel {
  position: relative;
  background: #fafafa;
  border: 1px solid #ebeef5;
  padding: 10px 12px 2px 12px;
  margin-bottom: 12px;
}
.panel-toggle {
  position: absolute;
  top: 4px;
  left: 8px;
  color: #009688;
  font-size: 14px;
}
.search-form {
  display: flex;
  flex-wrap: wrap;
  align-items: center;
  padding-left: 14px;
}
:deep(.el-form-item) {
  margin-bottom: 8px;
  margin-right: 12px;
}
.actions {
  margin-left: 8px;
}
.btn-search {
  background-color: #009688 !important;
  border-color: #009688 !important;
  color: #fff !important;
}
.btn-reset {
  background-color: #fff;
  border: 1px solid #dcdfe6;
}
.toolbar {
  display: flex;
  gap: 8px;
  margin-bottom: 12px;
}
.tool-btn {
  background-color: #009688 !important;
  border-color: #009688 !important;
  color: #fff !important;
  border-radius: 3px;
  font-size: 13px;
  padding: 6px 14px;
}
.tool-btn:hover {
  opacity: 0.9;
}
.tool-btn-plain {
  background-color: #fff;
  border: 1px solid #dcdfe6;
  border-radius: 3px;
  font-size: 13px;
  padding: 6px 14px;
}
.table-container {
  margin-top: 4px;
}
.role-name {
  font-weight: 500;
  color: #303133;
}
.code-badge {
  font-family: monospace;
  color: #555;
  background: #f0f2f5;
  padding: 2px 6px;
  border-radius: 2px;
  font-size: 12px;
}
.op-group {
  display: inline-flex;
  align-items: center;
  justify-content: center;
  flex-wrap: nowrap;
  gap: 4px;
  white-space: nowrap;
}
.op-btn {
  padding: 2px 6px;
  font-size: 12px;
  border: 1px solid #dcdfe6;
  border-radius: 3px;
  margin: 0 !important;
  white-space: nowrap;
}
.op-view {
  color: #009688;
  border-color: #009688;
}
.op-auth {
  color: #e6a23c;
  border-color: #e6a23c;
}
.op-status-enable {
  color: #009688;
  border-color: #009688;
}
.op-status-disable {
  color: #e6a23c;
  border-color: #e6a23c;
}
.op-status-disable.is-disabled {
  color: #c0c4cc !important;
  border-color: #e4e7ed !important;
  cursor: not-allowed;
}
.op-del {
  color: #f56c6c;
  border-color: #f56c6c;
}
.op-del.is-disabled {
  color: #c0c4cc !important;
  border-color: #e4e7ed !important;
  cursor: not-allowed;
}
</style>
