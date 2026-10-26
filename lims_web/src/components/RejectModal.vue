<template>
  <el-dialog
    v-model="visible"
    title="审批驳回操作"
    width="520px"
    destroy-on-close
    :close-on-click-modal="false"
  >
    <el-alert
      title="CMA/CNAS 准则要求：驳回动作必须明确填写驳回理由与整改要求，此操作将全程留痕审计。"
      type="warning"
      :closable="false"
      show-icon
      class="mb-4"
    />
    <el-form ref="formRef" :model="formData" :rules="rules" label-position="top">
      <el-form-item label="快捷驳回常用短语">
        <el-select
          v-model="quickReason"
          placeholder="快速选择常见驳回原因"
          style="width: 100%"
          @change="handleQuickSelect"
        >
          <el-option
            v-for="item in quickOptions"
            :key="item"
            :label="item"
            :value="item"
          />
        </el-select>
      </el-form-item>
      <el-form-item label="驳回详细原因 (必填)" prop="reason">
        <el-input
          v-model="formData.reason"
          type="textarea"
          :rows="4"
          placeholder="请详细说明检测项目不全、点位不符或样品信息缺失等驳回原因..."
          maxlength="500"
          show-word-limit
        />
      </el-form-item>
    </el-form>
    <template #footer>
      <span class="dialog-footer">
        <el-button @click="visible = false">取消</el-button>
        <el-button type="danger" :loading="loading" @click="handleConfirm">确认驳回</el-button>
      </span>
    </template>
  </el-dialog>
</template>

<script setup lang="ts">
import { ref, reactive } from 'vue'
import type { FormInstance, FormRules } from 'element-plus'

const emit = defineEmits<{
  (e: 'confirm', reason: string): void
}>()

const visible = ref(false)
const loading = ref(false)
const quickReason = ref('')
const formRef = ref<FormInstance>()

const quickOptions = [
  '委托单位或监测点位信息不完整',
  '检测因子与执行环境标准不匹配',
  '样品保存剂或保存条件不符合合规规范',
  '实验原始记录公式计算有偏差，请复核',
  '检测报告信息与原始记录数据不一致'
]

const formData = reactive({
  reason: ''
})

const rules: FormRules = {
  reason: [{ required: true, message: '必须填写驳回原因', trigger: 'blur' }]
}

const open = () => {
  formData.reason = ''
  quickReason.value = ''
  visible.value = true
}

const handleQuickSelect = (val: string) => {
  formData.reason = val
}

const handleConfirm = async () => {
  if (!formRef.value) return
  await formRef.value.validate((valid) => {
    if (valid) {
      emit('confirm', formData.reason)
    }
  })
}

const close = () => {
  visible.value = false
  loading.value = false
}

defineExpose({ open, close })
</script>

<style scoped>
.mb-4 {
  margin-bottom: 16px;
}
</style>
