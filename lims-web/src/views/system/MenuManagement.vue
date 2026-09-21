<template>
  <div class="menu-management-container">
    <el-card shadow="never">
      <template #header>
        <div class="header-box">
          <span>系统菜单与功能树形架构</span>
          <el-button type="success" size="small" icon="Plus" @click="openAddRootMenu">添加顶级菜单</el-button>
        </div>
      </template>

      <el-table
        :data="menuTree"
        row-key="id"
        border
        default-expand-all
        :tree-props="{ children: 'children', hasChildren: 'hasChildren' }"
      >
        <el-table-column prop="menuName" label="菜单名称" min-width="220" />
        <el-table-column prop="icon" label="图标" width="80" align="center">
          <template #default="{ row }">
            <el-icon v-if="row.icon"><component :is="row.icon" /></el-icon>
          </template>
        </el-table-column>
        <el-table-column prop="routePath" label="路由地址" min-width="180" />
        <el-table-column prop="componentPath" label="组件路径" min-width="180" />
        <el-table-column prop="perms" label="权限标识" min-width="180">
          <template #default="{ row }">
            <el-tag v-if="row.perms" size="small">{{ row.perms }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="sortOrder" label="排序" width="80" align="center" />
        <el-table-column label="操作" width="180" align="center">
          <template #default="{ row }">
            <el-button link type="primary" size="small" @click="openAddChildMenu(row)">添加子项</el-button>
            <el-button link type="danger" size="small" @click="handleDelete(row)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>
    </el-card>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import request from '@/utils/request'

const menuTree = ref<any[]>([])

async function fetchMenuTree() {
  try {
    const res: any = await request.get('/system/menu/tree')
    menuTree.value = res || []
  } catch (e) {
    menuTree.value = [
      {
        id: 100, menuName: '任务列表', routePath: '/task', icon: 'Checked', sortOrder: 1,
        children: [
          { id: 101, menuName: '我的申请', routePath: '/task/my-apply', perms: 'task:myApply:view', sortOrder: 1 },
          { id: 102, menuName: '我的审批任务', routePath: '/task/my-audit', perms: 'task:myAudit:view', sortOrder: 2 }
        ]
      },
      {
        id: 200, menuName: '合同/委托协议管理', routePath: '/contract', icon: 'Document', sortOrder: 2,
        children: [
          { id: 201, menuName: '合同登记', routePath: '/contract/create', perms: 'contract:create:view', sortOrder: 1 },
          { id: 205, menuName: '合同/委托协议审批', routePath: '/contract/audit', perms: 'contract:audit:view', sortOrder: 5 }
        ]
      }
    ]
  }
}

function openAddRootMenu() {
  ElMessage.info('新增顶级菜单')
}

function openAddChildMenu(row: any) {
  ElMessage.info(`为 [${row.menuName}] 添加子菜单`)
}

function handleDelete(row: any) {
  ElMessage.warning(`删除菜单 [${row.menuName}]`)
}

onMounted(() => {
  fetchMenuTree()
})
</script>

<style scoped>
.header-box {
  display: flex;
  justify-content: space-between;
  align-items: center;
}
</style>
