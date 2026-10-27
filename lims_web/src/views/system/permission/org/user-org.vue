<template>
  <div class="user-org-page">
    <!-- 顶部检索区 -->
    <div class="search-panel">
      <div class="panel-toggle">
        <el-icon><ArrowUp /></el-icon>
      </div>
      <el-form :inline="true" :model="queryParams" class="search-form">
        <el-form-item label="人员编码">
          <el-input v-model="queryParams.user_code" placeholder="" clearable style="width: 130px" />
        </el-form-item>
        <el-form-item label="人员姓名">
          <el-input v-model="queryParams.real_name" placeholder="" clearable style="width: 130px" />
        </el-form-item>
        <el-form-item label="电话号码">
          <el-input v-model="queryParams.phone" placeholder="" clearable style="width: 130px" />
        </el-form-item>
        <el-form-item label="邮件">
          <el-input v-model="queryParams.email" placeholder="" clearable style="width: 130px" />
        </el-form-item>
        <el-form-item label="所属部门">
          <el-input v-model="queryParams.department" placeholder="" clearable style="width: 130px" />
        </el-form-item>
        <el-form-item label="所属单位">
          <el-input v-model="queryParams.company" placeholder="" clearable style="width: 130px" />
        </el-form-item>
        <el-form-item class="actions">
          <el-button class="btn-search" type="primary" @click="handleSearch">查询</el-button>
          <el-button class="btn-reset" @click="handleReset">重置</el-button>
        </el-form-item>
      </el-form>
    </div>

    <!-- 中间操作工具条 -->
    <div class="toolbar">
      <el-button class="tool-btn" type="primary" @click="handleAdd">
        <el-icon><Plus /></el-icon> 新增
      </el-button>
      <el-button class="tool-btn" type="primary">
        <el-icon><User /></el-icon> 设置用户
      </el-button>
      <el-button class="tool-btn" type="primary" :disabled="!selectedUser" @click="handleOpenAssignRole">
        <el-icon><User /></el-icon> 人员定职
      </el-button>
      <el-button class="tool-btn" type="primary">
        <el-icon><Upload /></el-icon> 导入
      </el-button>
      <el-button class="tool-btn" type="primary">
        <el-icon><Download /></el-icon> 导出数据
      </el-button>
      <el-button class="tool-btn" type="primary">
        <el-icon><Upload /></el-icon> 人员定职导入
      </el-button>
    </div>

    <!-- 表格展示区 -->
    <div class="table-container">
      <el-table
        ref="tableRef"
        v-loading="loading"
        :data="tableData"
        border
        stripe
        highlight-current-row
        style="width: 100%"
        :header-cell-style="{ background: '#f5f7fa', color: '#333', fontWeight: 'bold' }"
        @current-change="handleRowSelect"
      >
        <el-table-column type="index" label="序号" width="55" align="center" />
        <el-table-column prop="user_code" label="人员编码" min-width="110" />
        <el-table-column prop="username" label="登录账号" min-width="120">
          <template #default="{ row }">
            <span class="account-badge">{{ row.username }}</span>
          </template>
        </el-table-column>
        <el-table-column prop="real_name" label="人员姓名" min-width="110" />
        <el-table-column prop="roles_info" label="所任职务" min-width="150" show-overflow-tooltip>
          <template #default="{ row }">
            <template v-if="row.roles_info && row.roles_info.length > 0">
              <el-tag
                v-for="r in row.roles_info"
                :key="r.id"
                size="small"
                effect="plain"
                class="role-tag"
              >
                {{ r.name }}
              </el-tag>
            </template>
            <span v-else class="text-muted">暂未配置</span>
          </template>
        </el-table-column>
        <el-table-column prop="phone" label="电话号码" min-width="120" />
        <el-table-column prop="last_login" label="最近登录" min-width="155" align="center">
          <template #default="{ row }">
            <span v-if="row.last_login" class="login-time-text">
              {{ formatDateTime(row.last_login) }}
            </span>
            <span v-else class="text-muted">从未登录</span>
          </template>
        </el-table-column>
        <el-table-column prop="department_name" label="所属部门" min-width="120" />
        <el-table-column prop="company" label="所属单位" min-width="180" show-overflow-tooltip />
        <el-table-column prop="is_active" label="状态" width="75" align="center">
          <template #default="{ row }">
            <el-tag :type="row.is_active ? 'success' : 'info'" size="small">
              {{ row.is_active ? '启用' : '停用' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="280" fixed="right" align="center">
          <template #default="{ row }">
            <div class="op-group">
              <el-button link class="op-btn op-view" size="small" @click="handleView(row)">查看</el-button>

              <!-- 非超级管理员才展示 重置密码、定职、停用/启用 与 删除 按钮 -->
              <template v-if="row.username !== 'admin'">
                <el-button
                  link
                  class="op-btn op-reset"
                  size="small"
                  @click="handleResetPassword(row)"
                >
                  重置密码
                </el-button>

                <el-button
                  link
                  class="op-btn op-auth"
                  size="small"
                  @click="handleSingleAssignRole(row)"
                >
                  定职
                </el-button>

                <el-button
                  link
                  :class="['op-btn', row.is_active ? 'op-status-disable' : 'op-status-enable']"
                  size="small"
                  @click="handleToggleStatus(row)"
                >
                  {{ row.is_active ? '停用' : '启用' }}
                </el-button>

                <el-button
                  link
                  class="op-btn op-del"
                  size="small"
                  @click="handleDelete(row)"
                >
                  删除
                </el-button>
              </template>
            </div>
          </template>
        </el-table-column>
      </el-table>

      <!-- 分页栏 -->
      <div class="pagination-wrap">
        <el-pagination
          v-model:current-page="queryParams.page"
          v-model:page-size="queryParams.page_size"
          :total="total"
          :page-sizes="[10, 20, 50, 100]"
          layout="total, sizes, prev, pager, next, jumper"
          @size-change="fetchData"
          @current-change="fetchData"
        />
      </div>
    </div>

    <!-- 新增 / 查看表单弹窗 -->
    <UserFormDialog
      ref="formDialogRef"
      :departments="departmentList"
      @success="fetchData"
    />

    <!-- 人员定职弹窗 -->
    <UserRoleAssignDialog
      ref="assignRoleDialogRef"
      @success="fetchData"
    />
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Plus, User, Upload, Download, ArrowUp } from '@element-plus/icons-vue'
import {
  getUserList,
  updateUser,
  deleteUser,
  resetUserPassword,
  getDepartmentList,
  type UserItem,
  type DepartmentItem,
  type UserQueryParams
} from '@/api/user'
import UserFormDialog from './components/UserFormDialog.vue'
import UserRoleAssignDialog from './components/UserRoleAssignDialog.vue'

const loading = ref(false)
const tableData = ref<UserItem[]>([])
const total = ref(0)
const selectedUser = ref<UserItem | null>(null)
const departmentList = ref<DepartmentItem[]>([])
const formDialogRef = ref<InstanceType<typeof UserFormDialog>>()
const assignRoleDialogRef = ref<InstanceType<typeof UserRoleAssignDialog>>()

const queryParams = reactive<UserQueryParams>({
  user_code: '',
  real_name: '',
  phone: '',
  email: '',
  department: '',
  company: '',
  page: 1,
  page_size: 10
})

const fetchDepartments = async () => {
  try {
    const res: any = await getDepartmentList()
    departmentList.value = res.data || []
  } catch (err) {
    // 忽略
  }
}

const fetchData = async () => {
  loading.value = true
  try {
    const res: any = await getUserList(queryParams)
    if (res.data && res.data.results) {
      tableData.value = res.data.results
      total.value = res.data.count
    } else if (Array.isArray(res.data)) {
      tableData.value = res.data
      total.value = res.data.length
    }
  } catch (err) {
    //
  } finally {
    loading.value = false
  }
}

const handleSearch = () => {
  queryParams.page = 1
  fetchData()
}

const handleReset = () => {
  queryParams.user_code = ''
  queryParams.real_name = ''
  queryParams.phone = ''
  queryParams.email = ''
  queryParams.department = ''
  queryParams.company = ''
  queryParams.page = 1
  fetchData()
}

const handleAdd = () => {
  formDialogRef.value?.open()
}

const handleView = (row: UserItem) => {
  formDialogRef.value?.open(row, true)
}

const handleRowSelect = (row: UserItem | null) => {
  selectedUser.value = row
}

const handleOpenAssignRole = () => {
  if (!selectedUser.value) {
    ElMessage.warning('请先在表格中选择要定职的人员')
    return
  }
  assignRoleDialogRef.value?.open(selectedUser.value)
}

const handleSingleAssignRole = (row: UserItem) => {
  selectedUser.value = row
  assignRoleDialogRef.value?.open(row)
}

const handleResetPassword = (row: UserItem) => {
  if (row.username === 'admin') {
    ElMessage.warning('系统超级管理员密码受系统安全保护，不可随意重置')
    return
  }
  ElMessageBox.prompt(
    `请输入人员【${row.real_name || row.username}】的新密码（默认不输入则重置为 123456）：`,
    '重置密码确认',
    {
      confirmButtonText: '确定重置',
      cancelButtonText: '取消',
      inputPlaceholder: '留空默认重置为 123456',
      inputType: 'password'
    }
  ).then(async ({ value }) => {
    try {
      const pwd = value && value.trim() ? value.trim() : undefined
      const res: any = await resetUserPassword(row.id, pwd)
      const usedPwd = res.data?.default_password || pwd || '123456'
      ElMessageBox.alert(
        `人员【${row.real_name || row.username}】密码已成功重置为：<strong style="color: #009688;">${usedPwd}</strong>，请通知该人员妥善保管并及时登录修改。`,
        '重置成功',
        {
          dangerouslyUseHTMLString: true,
          type: 'success'
        }
      )
    } catch (err) {}
  }).catch(() => {})
}

const handleToggleStatus = (row: UserItem) => {
  if (row.username === 'admin') {
    ElMessage.warning('系统超级管理员受核心安全保护，严禁停用！')
    return
  }
  const nextStatus = !row.is_active
  const actionText = nextStatus ? '启用' : '停用'
  ElMessageBox.confirm(
    `确定要${actionText}人员【${row.real_name || row.username}】吗？${!nextStatus ? '停用后该人员将无法登录系统。' : '启用后该人员将恢复登录和业务办理权限。'}`,
    '提示',
    {
      type: nextStatus ? 'info' : 'warning',
      confirmButtonText: '确定',
      cancelButtonText: '取消'
    }
  ).then(async () => {
    try {
      await updateUser(row.id, { is_active: nextStatus })
      ElMessage.success(`人员已${actionText}`)
      fetchData()
    } catch (err) {}
  })
}

const handleDelete = (row: UserItem) => {
  if (row.username === 'admin') {
    ElMessage.warning('系统超级管理员账号受核心安全保护，严禁删除！')
    return
  }
  ElMessageBox.confirm(`确认删除人员【${row.real_name || row.username}】的档案信息吗？`, '提示', {
    type: 'warning',
    confirmButtonText: '确定',
    cancelButtonText: '取消'
  }).then(async () => {
    try {
      await deleteUser(row.id)
      ElMessage.success('人员已删除')
      fetchData()
    } catch (err) {}
  })
}

const formatDateTime = (val?: string | null) => {
  if (!val) return '-'
  return val.replace('T', ' ').substring(0, 19)
}

onMounted(() => {
  fetchDepartments()
  fetchData()
})
</script>

<style scoped>
.user-org-page {
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
.table-container {
  margin-top: 4px;
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
.account-badge {
  font-family: monospace;
  font-weight: 500;
  color: #303133;
  background: #f0f2f5;
  padding: 2px 6px;
  border-radius: 3px;
  font-size: 12px;
}
.login-time-text {
  font-size: 12px;
  color: #555;
  font-family: monospace;
}
.role-tag {
  margin-right: 4px;
  margin-bottom: 2px;
}
.text-muted {
  color: #909399;
  font-size: 12px;
}
.op-view {
  color: #009688;
  border-color: #009688;
}
.op-auth {
  color: #009688;
  border-color: #009688;
}
.op-reset {
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
.pagination-wrap {
  margin-top: 14px;
  display: flex;
  justify-content: flex-end;
}
</style>
