<template>
  <div class="user-management-container">
    <el-card shadow="never">
      <!-- 搜索栏 -->
      <el-form :inline="true" :model="queryForm" class="demo-form-inline">
        <el-form-item label="用户名">
          <el-input v-model="queryForm.username" placeholder="请输入用户名" clearable />
        </el-form-item>
        <el-form-item label="真实姓名">
          <el-input v-model="queryForm.realName" placeholder="请输入真实姓名" clearable />
        </el-form-item>
        <el-form-item>
          <el-button type="primary" icon="Search" @click="fetchUserList">查询</el-button>
          <el-button icon="Refresh" @click="resetQuery">重置</el-button>
          <el-button type="success" icon="Plus" @click="openCreateDialog">新增用户</el-button>
        </el-form-item>
      </el-form>

      <!-- 数据表格 -->
      <el-table :data="userList" v-loading="loading" border style="width: 100%">
        <el-table-column prop="id" label="ID" width="80" align="center" />
        <el-table-column prop="username" label="登录账号" min-width="120" />
        <el-table-column prop="realName" label="真实姓名" min-width="120" />
        <el-table-column prop="phone" label="联系电话" min-width="130" />
        <el-table-column prop="email" label="电子邮箱" min-width="160" />
        <el-table-column prop="status" label="状态" width="90" align="center">
          <template #default="{ row }">
            <el-tag :type="row.status === 1 ? 'success' : 'danger'">
              {{ row.status === 1 ? '启用' : '禁用' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="createTime" label="创建时间" min-width="170" />
        <el-table-column label="三维度授权与操作" width="380" fixed="right" align="center">
          <template #default="{ row }">
            <el-button link type="primary" size="small" @click="openRoleDialog(row)">角色</el-button>
            <el-button link type="primary" size="small" @click="openPositionDialog(row)">岗位</el-button>
            <el-button link type="primary" size="small" @click="openGroupDialog(row)">项目组</el-button>
            <el-button link type="warning" size="small" @click="openEditDialog(row)">编辑</el-button>
            <el-button link type="danger" size="small" @click="handleDelete(row)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>

      <!-- 分页组件 -->
      <div class="pagination-box">
        <el-pagination
          v-model:current-page="pageReq.current"
          v-model:page-size="pageReq.size"
          :total="total"
          :page-sizes="[10, 20, 50]"
          layout="total, sizes, prev, pager, next, jumper"
          @size-change="fetchUserList"
          @current-change="fetchUserList"
        />
      </div>
    </el-card>

    <!-- 新增/编辑用户对话框 -->
    <el-dialog v-model="userDialogVisible" :title="isEdit ? '编辑用户' : '新增用户'" width="500px">
      <el-form :model="userForm" label-width="90px">
        <el-form-item label="登录账号" required>
          <el-input v-model="userForm.username" :disabled="isEdit" placeholder="请输入用户名" />
        </el-form-item>
        <el-form-item label="真实姓名" required>
          <el-input v-model="userForm.realName" placeholder="请输入真实姓名" />
        </el-form-item>
        <el-form-item label="初始密码" v-if="!isEdit">
          <el-input v-model="userForm.password" placeholder="默认密码为 123456" show-password />
        </el-form-item>
        <el-form-item label="手机号码">
          <el-input v-model="userForm.phone" placeholder="请输入手机号码" />
        </el-form-item>
        <el-form-item label="电子邮箱">
          <el-input v-model="userForm.email" placeholder="请输入电子邮箱" />
        </el-form-item>
        <el-form-item label="所属机构ID">
          <el-input-number v-model="userForm.orgId" :min="1" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="userDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="submitUserForm">确定</el-button>
      </template>
    </el-dialog>

    <!-- 分配角色对话框 -->
    <el-dialog v-model="roleDialogVisible" title="分配用户角色 (RBAC角色维度)" width="480px">
      <el-select v-model="selectedRoleIds" multiple placeholder="请选择角色" style="width: 100%">
        <el-option v-for="r in roleOptions" :key="r.id" :label="r.roleName" :value="r.id" />
      </el-select>
      <template #footer>
        <el-button @click="roleDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="saveUserRoles">保存</el-button>
      </template>
    </el-dialog>

    <!-- 分配岗位对话框 -->
    <el-dialog v-model="positionDialogVisible" title="分配专业职位身份 (岗位维度)" width="520px">
      <el-select v-model="selectedPositionIds" multiple placeholder="请选择19种预置职位身份" style="width: 100%">
        <el-option v-for="p in positionOptions" :key="p.id" :label="p.posName" :value="p.id" />
      </el-select>
      <template #footer>
        <el-button @click="positionDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="saveUserPositions">保存</el-button>
      </template>
    </el-dialog>

    <!-- 分配项目组对话框 -->
    <el-dialog v-model="groupDialogVisible" title="分配项目班组 (项目组维度)" width="480px">
      <el-select v-model="selectedGroupIds" multiple placeholder="请选择项目组" style="width: 100%">
        <el-option v-for="g in groupOptions" :key="g.id" :label="g.groupName" :value="g.id" />
      </el-select>
      <template #footer>
        <el-button @click="groupDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="saveUserGroups">保存</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import request from '@/utils/request'

const loading = ref(false)
const userList = ref<any[]>([])
const total = ref(0)

const pageReq = reactive({
  current: 1,
  size: 10
})

const queryForm = reactive({
  username: '',
  realName: ''
})

const userDialogVisible = ref(false)
const isEdit = ref(false)
const userForm = reactive({
  id: undefined as number | undefined,
  username: '',
  realName: '',
  password: '',
  phone: '',
  email: '',
  orgId: 1
})

// 三维度分配状态
const currentUserId = ref<number | null>(null)
const roleDialogVisible = ref(false)
const selectedRoleIds = ref<number[]>([])
const roleOptions = ref<any[]>([
  { id: 1, roleName: '超级管理员' },
  { id: 2, roleName: '质量主管角色' },
  { id: 3, roleName: '检测实验人员角色' },
  { id: 4, roleName: '报告签字人角色' }
])

const positionDialogVisible = ref(false)
const selectedPositionIds = ref<number[]>([])
const positionOptions = ref<any[]>([])

const groupDialogVisible = ref(false)
const selectedGroupIds = ref<number[]>([])
const groupOptions = ref<any[]>([
  { id: 1, groupName: '地表水监测攻坚专班' }
])

async function fetchUserList() {
  loading.value = true
  try {
    const res: any = await request.get('/system/user/page', {
      params: {
        current: pageReq.current,
        size: pageReq.size,
        username: queryForm.username,
        realName: queryForm.realName
      }
    })
    userList.value = res.records || []
    total.value = res.total || 0
  } catch (e) {
    // 降级使用演示数据
    userList.value = [
      { id: 1, username: 'admin', realName: '系统超级管理员', phone: '13800000000', email: 'admin@lims.local', status: 1, createTime: '2024-05-20 10:00:00' }
    ]
    total.value = 1
  } finally {
    loading.value = false
  }
}

async function fetchPositions() {
  try {
    const res: any = await request.get('/system/position/list')
    positionOptions.value = res || []
  } catch (e) {
    positionOptions.value = [
      { id: 1, posName: '管理层' },
      { id: 2, posName: '质量负责人' },
      { id: 8, posName: '采样组长' },
      { id: 9, posName: '采样员' },
      { id: 12, posName: '检测实验员' }
    ]
  }
}

function resetQuery() {
  queryForm.username = ''
  queryForm.realName = ''
  pageReq.current = 1
  fetchUserList()
}

function openCreateDialog() {
  isEdit.value = false
  userForm.id = undefined
  userForm.username = ''
  userForm.realName = ''
  userForm.password = ''
  userForm.phone = ''
  userForm.email = ''
  userForm.orgId = 1
  userDialogVisible.value = true
}

function openEditDialog(row: any) {
  isEdit.value = true
  userForm.id = row.id
  userForm.username = row.username
  userForm.realName = row.realName
  userForm.phone = row.phone
  userForm.email = row.email
  userForm.orgId = row.orgId || 1
  userDialogVisible.value = true
}

async function submitUserForm() {
  if (isEdit.value) {
    await request.put(`/system/user/${userForm.id}`, userForm)
    ElMessage.success('用户更新成功')
  } else {
    await request.post('/system/user', userForm)
    ElMessage.success('用户创建成功')
  }
  userDialogVisible.value = false
  fetchUserList()
}

function handleDelete(row: any) {
  ElMessageBox.confirm(`确定要删除用户 [${row.username}] 吗？`, '提示', { type: 'warning' })
    .then(async () => {
      await request.delete(`/system/user/${row.id}`)
      ElMessage.success('删除成功')
      fetchUserList()
    })
    .catch(() => {})
}

function openRoleDialog(row: any) {
  currentUserId.value = row.id
  selectedRoleIds.value = [1]
  roleDialogVisible.value = true
}

async function saveUserRoles() {
  if (!currentUserId.value) return
  await request.post(`/system/user/${currentUserId.value}/assign-roles`, selectedRoleIds.value)
  ElMessage.success('角色分配已更新')
  roleDialogVisible.value = false
}

function openPositionDialog(row: any) {
  currentUserId.value = row.id
  selectedPositionIds.value = [1]
  positionDialogVisible.value = true
}

async function saveUserPositions() {
  if (!currentUserId.value) return
  await request.post(`/system/user/${currentUserId.value}/assign-positions`, selectedPositionIds.value)
  ElMessage.success('岗位身份分配已更新')
  positionDialogVisible.value = false
}

function openGroupDialog(row: any) {
  currentUserId.value = row.id
  selectedGroupIds.value = [1]
  groupDialogVisible.value = true
}

async function saveUserGroups() {
  if (!currentUserId.value) return
  await request.post(`/system/user/${currentUserId.value}/assign-project-groups`, selectedGroupIds.value)
  ElMessage.success('项目组分配已更新')
  groupDialogVisible.value = false
}

onMounted(() => {
  fetchUserList()
  fetchPositions()
})
</script>

<style scoped>
.user-management-container {
  display: flex;
  flex-direction: column;
  gap: 15px;
}
.pagination-box {
  margin-top: 15px;
  display: flex;
  justify-content: flex-end;
}
</style>
