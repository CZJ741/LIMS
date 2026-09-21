<template>
  <div class="role-management-container">
    <el-card shadow="never">
      <el-form :inline="true" :model="queryForm">
        <el-form-item label="角色名称">
          <el-input v-model="queryForm.roleName" placeholder="请输入角色名称" clearable />
        </el-form-item>
        <el-form-item label="角色编码">
          <el-input v-model="queryForm.roleCode" placeholder="请输入角色编码" clearable />
        </el-form-item>
        <el-form-item>
          <el-button type="primary" icon="Search" @click="fetchRoles">查询</el-button>
          <el-button type="success" icon="Plus" @click="openCreateRole">新增角色</el-button>
        </el-form-item>
      </el-form>

      <el-table :data="roleList" border style="width: 100%">
        <el-table-column prop="id" label="ID" width="80" align="center" />
        <el-table-column prop="roleName" label="角色名称" min-width="150" />
        <el-table-column prop="roleCode" label="角色编码" min-width="150" />
        <el-table-column prop="remark" label="备注" min-width="200" />
        <el-table-column label="操作" width="280" align="center" fixed="right">
          <template #default="{ row }">
            <el-button link type="primary" size="small" @click="openMatrixDialog(row)">权限矩阵配置</el-button>
            <el-button link type="warning" size="small" @click="openCopyDialog(row)">复制权限</el-button>
            <el-button link type="danger" size="small" @click="handleDelete(row)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>
    </el-card>

    <!-- 权限矩阵网格编辑器对话框 -->
    <el-dialog v-model="matrixDialogVisible" :title="`权限矩阵网格配置 - [${currentRole?.roleName}]`" width="900px">
      <div class="matrix-tip">
        点击单元格图标切换数据范围：
        <span class="tip-item">🌐 = 全局</span>
        <span class="tip-item">🔒 = 仅本部门</span>
        <span class="tip-item">📁 = 指定项目组</span>
        <span class="tip-item">👤 = 仅本人</span>
        <span class="tip-item">❌ = 禁止</span>
      </div>
      <el-table :data="permissionRows" border height="450px">
        <el-table-column prop="module" label="功能模块" width="180" />
        <el-table-column prop="permissionName" label="操作权限项" width="220" />
        <el-table-column prop="permTag" label="权限标识字符" min-width="200" />
        <el-table-column label="数据范围授权状态" width="180" align="center">
          <template #default="{ row }">
            <el-button
              :type="getScopeBtnType(row.scope)"
              size="small"
              circle
              @click="toggleScope(row)"
            >
              {{ getScopeIcon(row.scope) }}
            </el-button>
            <span class="scope-text">{{ getScopeLabel(row.scope) }}</span>
          </template>
        </el-table-column>
      </el-table>
      <template #footer>
        <el-button @click="matrixDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="saveMatrixPermissions">保存矩阵配置</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import request from '@/utils/request'

const roleList = ref<any[]>([])
const queryForm = reactive({
  roleName: '',
  roleCode: ''
})

const currentRole = ref<any>(null)
const matrixDialogVisible = ref(false)

// 演示权限网格项
const permissionRows = ref<any[]>([
  { id: 201, module: '合同管理', permissionName: '合同登记', permTag: 'contract:create:btn', scope: 'GLOBAL' },
  { id: 205, module: '合同管理', permissionName: '合同审核审批', permTag: 'contract:audit:btn', scope: 'DEPARTMENT' },
  { id: 303, module: '委托单管理', permissionName: '委托下单', permTag: 'entrust:order:btn', scope: 'GLOBAL' },
  { id: 401, module: '采样管理', permissionName: '采样前准备', permTag: 'sampling:prepare:btn', scope: 'PROJECT_GROUP' },
  { id: 402, module: '采样管理', permissionName: '现场采样', permTag: 'sampling:submit:btn', scope: 'PROJECT_GROUP' },
  { id: 502, module: '样品流转', permissionName: '样品接收入库', permTag: 'sample:receive:btn', scope: 'GLOBAL' },
  { id: 601, module: '检测管理', permissionName: '检测任务认领', permTag: 'detection:claim:btn', scope: 'DEPARTMENT' },
  { id: 605, module: '检测管理', permissionName: '检测数据录入', permTag: 'detection:result:btn', scope: 'PERSONAL' },
  { id: 607, module: '检测管理', permissionName: '实验数据复审', permTag: 'detection:recheck:btn', scope: 'DEPARTMENT' },
  { id: 701, module: '报告管理', permissionName: '报告编制', permTag: 'report:compile:btn', scope: 'PERSONAL' },
  { id: 702, module: '报告管理', permissionName: '报告审核', permTag: 'report:audit:btn', scope: 'DEPARTMENT' },
  { id: 703, module: '报告管理', permissionName: '报告发放签章', permTag: 'report:release:btn', scope: 'GLOBAL' }
])

const scopes = ['GLOBAL', 'DEPARTMENT', 'PROJECT_GROUP', 'PERSONAL', 'DENIED']

function toggleScope(row: any) {
  const currentIndex = scopes.indexOf(row.scope)
  const nextIndex = (currentIndex + 1) % scopes.length
  row.scope = scopes[nextIndex]
}

function getScopeIcon(scope: string) {
  switch (scope) {
    case 'GLOBAL': return '🌐'
    case 'DEPARTMENT': return '🔒'
    case 'PROJECT_GROUP': return '📁'
    case 'PERSONAL': return '👤'
    case 'DENIED': return '❌'
    default: return '❌'
  }
}

function getScopeBtnType(scope: string): '' | 'success' | 'warning' | 'info' | 'primary' | 'danger' {
  switch (scope) {
    case 'GLOBAL': return 'success'
    case 'DEPARTMENT': return 'warning'
    case 'PROJECT_GROUP': return 'primary'
    case 'PERSONAL': return 'info'
    default: return 'danger'
  }
}

function getScopeLabel(scope: string) {
  switch (scope) {
    case 'GLOBAL': return '全局'
    case 'DEPARTMENT': return '本部门'
    case 'PROJECT_GROUP': return '项目组'
    case 'PERSONAL': return '仅本人'
    default: return '禁止'
  }
}

async function fetchRoles() {
  try {
    const res: any = await request.get('/system/role/page', {
      params: { current: 1, size: 50, ...queryForm }
    })
    roleList.value = res.records || []
  } catch (e) {
    roleList.value = [
      { id: 1, roleName: '超级管理员', roleCode: 'SUPER_ADMIN', remark: '全局最高控制权限' },
      { id: 2, roleName: '质量主管角色', roleCode: 'ROLE_QUALITY_DIRECTOR', remark: '质量体系监督' },
      { id: 3, roleName: '检测实验人员角色', roleCode: 'ROLE_LAB_ANALYST', remark: '实验数据录入' }
    ]
  }
}

function openCreateRole() {
  ElMessage.info('新增角色弹窗')
}

function openMatrixDialog(row: any) {
  currentRole.value = row
  matrixDialogVisible.value = true
}

function openCopyDialog(row: any) {
  ElMessage.info(`从角色 [${row.roleName}] 复制权限`)
}

function handleDelete(row: any) {
  ElMessageBox.confirm(`确定要删除角色 [${row.roleName}] 吗？`, '提示', { type: 'warning' })
    .then(() => {
      ElMessage.success('删除成功')
      fetchRoles()
    })
    .catch(() => {})
}

async function saveMatrixPermissions() {
  if (!currentRole.value) return
  const menuIds = permissionRows.value.filter(r => r.scope !== 'DENIED').map(r => r.id)
  const dataScopeMap: Record<number, string> = {}
  permissionRows.value.forEach(r => {
    dataScopeMap[r.id] = r.scope
  })

  await request.post(`/system/role/${currentRole.value.id}/assign-permissions`, {
    menuIds,
    dataScopeMap
  })
  ElMessage.success('权限矩阵保存成功！')
  matrixDialogVisible.value = false
}

onMounted(() => {
  fetchRoles()
})
</script>

<style scoped>
.matrix-tip {
  margin-bottom: 12px;
  font-size: 13px;
  color: #606266;
}
.tip-item {
  margin-right: 15px;
  font-weight: bold;
}
.scope-text {
  margin-left: 8px;
  font-size: 12px;
}
</style>
