<template>
  <el-dialog
    v-model="visible"
    :title="isEdit ? '编辑角色' : '新增角色'"
    width="540px"
    destroy-on-close
    :close-on-click-modal="false"
  >
    <el-form
      ref="formRef"
      :model="form"
      :rules="rules"
      label-width="90px"
      label-position="right"
      :show-message="false"
      scroll-to-error
    >
      <el-form-item label="角色名称" prop="name">
        <el-input
          v-model="form.name"
          placeholder="例如：采样组长、分析检验员"
          clearable
          maxlength="64"
        />
      </el-form-item>

      <el-form-item label="角色编码" prop="code">
        <el-input
          v-model="form.code"
          placeholder="选填，留空自动生成（如 10001、10002）"
          clearable
          maxlength="32"
          :disabled="isEdit && form.code === 'ADMIN'"
        />
      </el-form-item>

      <el-form-item label="备注说明" prop="description">
        <el-input
          v-model="form.description"
          type="textarea"
          :rows="4"
          placeholder="请输入角色的职责范围或说明信息"
          maxlength="256"
          show-word-limit
        />
      </el-form-item>
    </el-form>

    <template #footer>
      <span class="dialog-footer">
        <el-button @click="visible = false">取消</el-button>
        <el-button type="primary" :loading="submitting" @click="handleSubmit">
          确定
        </el-button>
      </span>
    </template>
  </el-dialog>
</template>

<script setup lang="ts">
import { ref, reactive } from 'vue'
import type { FormInstance, FormRules } from 'element-plus'
import { ElMessage } from 'element-plus'
import { createRole, updateRole, type RoleItem } from '@/api/role'

const emit = defineEmits<{
  (e: 'success'): void
}>()

const visible = ref(false)
const isEdit = ref(false)
const submitting = ref(false)
const currentId = ref<string>('')
const formRef = ref<FormInstance>()

const form = reactive({
  name: '',
  code: '',
  description: ''
})

const rules = reactive<FormRules>({
  name: [{ required: true, message: '请输入角色名称', trigger: 'blur' }]
})

function open(row?: RoleItem) {
  visible.value = true
  if (row) {
    isEdit.value = true
    currentId.value = row.id
    form.name = row.name
    form.code = row.code
    form.description = row.description || ''
  } else {
    isEdit.value = false
    currentId.value = ''
    form.name = ''
    form.code = ''
    form.description = ''
  }
}

async function handleSubmit() {
  if (!formRef.value) return
  await formRef.value.validate(async (valid, fields) => {
    if (!valid) {
      const errorFieldNames: Record<string, string> = {
        name: '角色名称',
        code: '角色编码'
      }
      const missingList: string[] = []
      if (fields) {
        Object.keys(fields).forEach((key) => {
          missingList.push(errorFieldNames[key] || key)
        })
      }
      const tipText = missingList.length > 0 ? `：【${missingList.join('、')}】未通过校验` : ''
      ElMessage.warning(`请完善必填或格式错误信息${tipText}，已为您自动定位至首个待办项`)
      return
    }
    submitting.value = true
    try {
      const payload: Partial<RoleItem> = {
        name: form.name.trim(),
        description: form.description ? form.description.trim() : ''
      }
      if (form.code && form.code.trim()) {
        payload.code = form.code.trim().toUpperCase()
      }

      if (isEdit.value) {
        await updateRole(currentId.value, payload)
        ElMessage.success('角色信息已成功更新')
      } else {
        await createRole(payload)
        ElMessage.success('角色创建成功')
      }
      visible.value = false
      emit('success')
    } catch (err: any) {
      // 错误已由 request 拦截器统一提示或抛出
    } finally {
      submitting.value = false
    }
  })
}

defineExpose({
  open
})
</script>

<style scoped>
:deep(.el-form-item__error) {
  display: none !important;
}
.dialog-footer {
  display: flex;
  justify-content: flex-end;
  gap: 10px;
}
</style>
