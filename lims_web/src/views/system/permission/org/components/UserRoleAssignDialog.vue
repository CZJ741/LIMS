<template>
  <el-dialog
    v-model="visible"
    :title="`人员职务配置 - ${currentUser?.real_name || currentUser?.username || ''}`"
    width="520px"
    destroy-on-close
    :close-on-click-modal="false"
  >
    <div class="assign-role-tip">
      请选择为该人员授予的职位/岗位角色（支持多选，数据来源于角色管理系统）：
    </div>

    <el-form label-width="80px" class="assign-form">
      <el-form-item label="人员姓名">
        <span class="user-text">{{ currentUser?.real_name || '-' }} ({{ currentUser?.username }})</span>
      </el-form-item>
      <el-form-item label="分配职务">
        <el-select
          v-model="selectedRoleIds"
          multiple
          placeholder="请选择所任职务角色"
          style="width: 100%"
          collapse-tags
          collapse-tags-tooltip
        >
          <el-option
            v-for="role in availableRoles"
            :key="role.id"
            :label="`${role.name} (${role.code})`"
            :value="role.id"
            :disabled="role.is_active === false"
          >
            <div class="role-option">
              <span>{{ role.name }}</span>
              <span class="role-code">{{ role.code }}</span>
            </div>
          </el-option>
        </el-select>
      </el-form-item>
    </el-form>

    <template #footer>
      <div class="dialog-footer">
        <el-button @click="visible = false">取消</el-button>
        <el-button type="primary" class="btn-theme" :loading="submitting" @click="handleSubmit">
          保存配置
        </el-button>
      </div>
    </template>
  </el-dialog>
</template>

<script setup lang="ts">
import { ref } from 'vue'
import { ElMessage } from 'element-plus'
import { assignUserRoles, type UserItem } from '@/api/user'
import { getRoleList, type RoleItem } from '@/api/role'

const emit = defineEmits<{
  (e: 'success'): void
}>()

const visible = ref(false)
const submitting = ref(false)
const currentUser = ref<UserItem | null>(null)
const availableRoles = ref<RoleItem[]>([])
const selectedRoleIds = ref<string[]>([])

const open = async (user: UserItem) => {
  currentUser.value = user
  visible.value = true
  // 初始化选中的角色 ID
  if (user.roles && Array.isArray(user.roles)) {
    selectedRoleIds.value = [...user.roles]
  } else if (user.roles_info && Array.isArray(user.roles_info)) {
    selectedRoleIds.value = user.roles_info.map((r) => r.id)
  } else {
    selectedRoleIds.value = []
  }

  // 加载系统有效角色列表
  try {
    const res: any = await getRoleList()
    const list = Array.isArray(res.data) ? res.data : (res.data?.results || [])
    availableRoles.value = list.filter((r: RoleItem) => r.is_active !== false)
  } catch (err) {}
}

const handleSubmit = async () => {
  if (!currentUser.value) return
  submitting.value = true
  try {
    await assignUserRoles(currentUser.value.id, selectedRoleIds.value)
    ElMessage.success('职务角色配置成功')
    visible.value = false
    emit('success')
  } catch (err) {
  } finally {
    submitting.value = false
  }
}

defineExpose({ open })
</script>

<style scoped>
.assign-role-tip {
  margin-bottom: 16px;
  color: #606266;
  font-size: 13px;
  background: #f4f4f5;
  padding: 8px 12px;
  border-radius: 4px;
}
.user-text {
  font-weight: 500;
  color: #303133;
}
.role-option {
  display: flex;
  justify-content: space-between;
  align-items: center;
  width: 100%;
}
.role-code {
  font-size: 12px;
  color: #909399;
  font-family: monospace;
}
.btn-theme {
  background-color: #009688 !important;
  border-color: #009688 !important;
  color: #fff !important;
}
.dialog-footer {
  text-align: right;
}
</style>
