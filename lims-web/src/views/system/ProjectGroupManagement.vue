<template>
  <div class="project-group-container">
    <el-card shadow="never">
      <el-form :inline="true" :model="queryForm">
        <el-form-item label="项目组名称">
          <el-input v-model="queryForm.groupName" placeholder="请输入项目组名称" clearable />
        </el-form-item>
        <el-form-item>
          <el-button type="primary" icon="Search" @click="fetchGroups">查询</el-button>
          <el-button type="success" icon="Plus" @click="openCreateGroup">新建项目组</el-button>
        </el-form-item>
      </el-form>

      <el-table :data="groupList" border style="width: 100%">
        <el-table-column prop="id" label="ID" width="80" align="center" />
        <el-table-column prop="groupName" label="项目组/班组名称" min-width="180" />
        <el-table-column prop="groupCode" label="项目组编码" min-width="160" />
        <el-table-column prop="remark" label="专班业务说明" min-width="220" />
        <el-table-column prop="createTime" label="创建时间" min-width="170" />
        <el-table-column label="成员与权限操作" width="220" align="center" fixed="right">
          <template #default="{ row }">
            <el-button link type="primary" size="small" @click="openMemberDialog(row)">成员管理</el-button>
            <el-button link type="danger" size="small" @click="handleDelete(row)">解散</el-button>
          </template>
        </el-table-column>
      </el-table>
    </el-card>

    <!-- 成员管理对话框 -->
    <el-dialog v-model="memberDialogVisible" :title="`[${currentGroup?.groupName}] 班组成员维护`" width="600px">
      <div class="add-member-bar">
        <el-input-number v-model="newMemberUserId" placeholder="输入用户ID" :min="1" />
        <el-button type="primary" @click="handleAddMember">添加成员</el-button>
      </div>
      <el-table :data="memberList" border style="width: 100%">
        <el-table-column prop="id" label="用户ID" width="90" align="center" />
        <el-table-column prop="username" label="用户名" min-width="120" />
        <el-table-column prop="realName" label="姓名" min-width="120" />
        <el-table-column label="操作" width="100" align="center">
          <template #default="{ row }">
            <el-button link type="danger" size="small" @click="handleRemoveMember(row)">移除</el-button>
          </template>
        </el-table-column>
      </el-table>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import request from '@/utils/request'

const groupList = ref<any[]>([])
const queryForm = reactive({
  groupName: ''
})

const memberDialogVisible = ref(false)
const currentGroup = ref<any>(null)
const memberList = ref<any[]>([])
const newMemberUserId = ref<number>(1)

async function fetchGroups() {
  try {
    const res: any = await request.get('/system/project-group/page', {
      params: { current: 1, size: 20, ...queryForm }
    })
    groupList.value = res.records || []
  } catch (e) {
    groupList.value = [
      { id: 1, groupName: '地表水监测攻坚专班', groupCode: 'GRP_ENV_WATER', remark: '重点流域地表水专项分析', createTime: '2024-05-20 12:00:00' }
    ]
  }
}

function openCreateGroup() {
  ElMessage.info('新建项目组')
}

async function openMemberDialog(row: any) {
  currentGroup.value = row
  memberDialogVisible.value = true
  try {
    const res: any = await request.get(`/system/project-group/${row.id}/members`)
    memberList.value = res || []
  } catch (e) {
    memberList.value = [
      { id: 1, username: 'admin', realName: '系统超级管理员' }
    ]
  }
}

async function handleAddMember() {
  if (!currentGroup.value || !newMemberUserId.value) return
  await request.post(`/system/project-group/${currentGroup.value.id}/members/${newMemberUserId.value}`)
  ElMessage.success('成员添加成功')
  openMemberDialog(currentGroup.value)
}

async function handleRemoveMember(row: any) {
  if (!currentGroup.value) return
  await request.delete(`/system/project-group/${currentGroup.value.id}/members/${row.id}`)
  ElMessage.success('成员已移除')
  openMemberDialog(currentGroup.value)
}

function handleDelete(row: any) {
  ElMessage.warning(`解散项目组 [${row.groupName}]`)
}

onMounted(() => {
  fetchGroups()
})
</script>

<style scoped>
.add-member-bar {
  display: flex;
  gap: 10px;
  margin-bottom: 15px;
}
</style>
