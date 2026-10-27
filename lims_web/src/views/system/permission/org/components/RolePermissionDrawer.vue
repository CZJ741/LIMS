<template>
  <el-drawer
    v-model="visible"
    :title="`菜单权限授权 - 【${roleName}】`"
    size="420px"
    destroy-on-close
  >
    <div class="permission-container">
      <div class="permission-header">
        <span class="sub-text">为该角色勾选允许访问的业务模块与系统功能菜单</span>
        <div class="action-bar">
          <el-button link type="primary" size="small" @click="handleSelectAll">全选</el-button>
          <el-button link type="info" size="small" @click="handleClearAll">清空</el-button>
          <el-button link type="primary" size="small" @click="handleToggleExpand">
            {{ isExpanded ? '折叠全部' : '展开全部' }}
          </el-button>
        </div>
      </div>

      <el-divider style="margin: 12px 0;" />

      <div class="tree-box">
        <el-tree
          ref="treeRef"
          :data="menuTreeData"
          show-checkbox
          node-key="id"
          :default-expand-all="isExpanded"
          :props="{ children: 'children', label: 'label' }"
          highlight-current
        >
          <template #default="{ node, data }">
            <span class="custom-tree-node">
              <span>{{ node.label }}</span>
              <el-tag v-if="data.tag" size="small" type="info" effect="plain" class="node-tag">
                {{ data.tag }}
              </el-tag>
            </span>
          </template>
        </el-tree>
      </div>
    </div>

    <template #footer>
      <div class="drawer-footer">
        <el-button @click="visible = false">取消</el-button>
        <el-button type="primary" :loading="saving" @click="handleSave">
          保存权限配置
        </el-button>
      </div>
    </template>
  </el-drawer>
</template>

<script setup lang="ts">
import { ref, nextTick } from 'vue'
import { ElMessage, type ElTree } from 'element-plus'
import { updateRole, type RoleItem } from '@/api/role'

const emit = defineEmits<{
  (e: 'success'): void
}>()

interface MenuNode {
  id: string
  label: string
  tag?: string
  children?: MenuNode[]
}

const visible = ref(false)
const saving = ref(false)
const roleName = ref('')
const currentRoleId = ref('')
const currentRoleCode = ref('')
const isExpanded = ref(true)
const treeRef = ref<InstanceType<typeof ElTree>>()

// 真实与系统实际侧边栏导航 1:1 对应的菜单权限树
const menuTreeData: MenuNode[] = [
  {
    id: '/dashboard',
    label: '工作台首页',
    tag: '基础'
  },
  {
    id: '/system',
    label: '系统管理',
    children: [
      {
        id: '/system/permission',
        label: '权限管理',
        children: [
          {
            id: '/system/permission/org',
            label: '组织管理',
            children: [
              { id: '/system/permission/org/dept', label: '组织维护' },
              { id: '/system/permission/org/user-org', label: '人员维护' },
              { id: '/system/permission/org/role', label: '角色管理' },
              { id: '/system/permission/org/position', label: '职位维护' }
            ]
          },
          { id: '/system/permission/user', label: '用户管理' }
        ]
      }
    ]
  }
]

function open(row: RoleItem) {
  visible.value = true
  roleName.value = row.name
  currentRoleId.value = row.id
  currentRoleCode.value = row.code
  nextTick(() => {
    if (row.code === 'ADMIN') {
      handleSelectAll()
    } else {
      const perms = row.menu_permissions || ['/dashboard']
      treeRef.value?.setCheckedKeys(perms)
    }
  })
}

function handleSelectAll() {
  const allKeys = getAllKeys(menuTreeData)
  treeRef.value?.setCheckedKeys(allKeys)
}

function handleClearAll() {
  treeRef.value?.setCheckedKeys([])
}

function getAllKeys(nodes: MenuNode[]): string[] {
  let keys: string[] = []
  nodes.forEach(node => {
    keys.push(node.id)
    if (node.children) {
      keys = keys.concat(getAllKeys(node.children))
    }
  })
  return keys
}

function handleToggleExpand() {
  isExpanded.value = !isExpanded.value
  const nodes = treeRef.value?.store.nodesMap
  if (nodes) {
    for (const key in nodes) {
      nodes[key].expanded = isExpanded.value
    }
  }
}

async function handleSave() {
  saving.value = true
  try {
    const checkedKeys = treeRef.value?.getCheckedKeys() || []
    const halfCheckedKeys = treeRef.value?.getHalfCheckedKeys() || []
    const totalSelected = [...checkedKeys, ...halfCheckedKeys] as string[]

    // 真正调用后端 API 持久化更新
    await updateRole(currentRoleId.value, {
      menu_permissions: totalSelected
    })

    ElMessage.success(`角色【${roleName.value}】菜单权限配置已成功保存`)
    visible.value = false
    emit('success')
  } catch (err: any) {
    // 错误由拦截器统一提示
  } finally {
    saving.value = false
  }
}

defineExpose({
  open
})
</script>

<style scoped>
.permission-container {
  display: flex;
  flex-direction: column;
  height: 100%;
}
.permission-header {
  display: flex;
  flex-direction: column;
  gap: 8px;
}
.sub-text {
  font-size: 13px;
  color: #909399;
}
.action-bar {
  display: flex;
  gap: 8px;
}
.tree-box {
  flex: 1;
  overflow-y: auto;
  padding: 4px;
}
.custom-tree-node {
  flex: 1;
  display: flex;
  align-items: center;
  justify-content: space-between;
  font-size: 14px;
  padding-right: 8px;
}
.node-tag {
  font-size: 11px;
}
.drawer-footer {
  display: flex;
  justify-content: flex-end;
  gap: 12px;
}
</style>
