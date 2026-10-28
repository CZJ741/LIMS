<template>
  <el-dialog
    v-model="visible"
    :title="isEdit ? '修改客户信息' : '新增客户信息'"
    width="820px"
    destroy-on-close
    :close-on-click-modal="false"
    class="customer-dialog"
  >
    <el-form
      ref="formRef"
      :model="formData"
      :rules="formRules"
      label-width="95px"
      label-position="right"
      :show-message="false"
      scroll-to-error
      class="customer-form"
    >
      <!-- 基本信息分组 -->
      <div class="form-section">
        <div class="section-header">
          <el-icon class="section-icon"><User /></el-icon>
          <span class="section-title">基本信息</span>
        </div>
        <div class="section-body">
          <el-row :gutter="20">
            <el-col :span="12">
              <el-form-item label="客户名称" prop="name">
                <el-input v-model="formData.name" placeholder="请输入客户名称" clearable />
              </el-form-item>
            </el-col>
            <el-col :span="12">
              <el-form-item label="法人代表" prop="legal_person">
                <el-input v-model="formData.legal_person" placeholder="请输入法人代表" clearable />
              </el-form-item>
            </el-col>
          </el-row>

          <el-row :gutter="20">
            <el-col :span="24">
              <el-form-item label="通讯地址" prop="address">
                <el-input v-model="formData.address" placeholder="请输入通讯地址" clearable />
              </el-form-item>
            </el-col>
          </el-row>

          <el-row :gutter="20">
            <el-col :span="12">
              <el-form-item label="联系人" prop="contact_person">
                <el-input v-model="formData.contact_person" placeholder="请输入联系人" clearable />
              </el-form-item>
            </el-col>
            <el-col :span="12">
              <el-form-item label="手机" prop="mobile">
                <el-input v-model="formData.mobile" placeholder="请输入手机号" clearable />
              </el-form-item>
            </el-col>
          </el-row>

          <el-row :gutter="20">
            <el-col :span="12">
              <el-form-item label="邮箱" prop="email">
                <el-input v-model="formData.email" placeholder="请输入邮箱" clearable />
              </el-form-item>
            </el-col>
            <el-col :span="12">
              <el-form-item label="固定电话" prop="phone">
                <el-input v-model="formData.phone" placeholder="请输入固定电话" clearable />
              </el-form-item>
            </el-col>
          </el-row>

          <el-row :gutter="20">
            <el-col :span="12">
              <el-form-item label="传真" prop="fax">
                <el-input v-model="formData.fax" placeholder="请输入传真" clearable />
              </el-form-item>
            </el-col>
          </el-row>
        </div>
      </div>

      <!-- 开票信息分组 -->
      <div class="form-section">
        <div class="section-header">
          <el-icon class="section-icon"><Document /></el-icon>
          <span class="section-title">开票信息</span>
        </div>
        <div class="section-body">
          <el-row :gutter="20">
            <el-col :span="12">
              <el-form-item label="发票抬头" prop="tax_title">
                <el-input v-model="formData.tax_title" placeholder="请输入发票抬头" clearable />
              </el-form-item>
            </el-col>
            <el-col :span="12">
              <el-form-item label="纳税人识别号" prop="tax_number">
                <el-input v-model="formData.tax_number" placeholder="请输入纳税人识别号" clearable />
              </el-form-item>
            </el-col>
          </el-row>

          <el-row :gutter="20">
            <el-col :span="24">
              <el-form-item label="地址" prop="invoice_address">
                <el-input v-model="formData.invoice_address" placeholder="请输入开票地址" clearable />
              </el-form-item>
            </el-col>
          </el-row>

          <el-row :gutter="20">
            <el-col :span="12">
              <el-form-item label="电话" prop="invoice_phone">
                <el-input v-model="formData.invoice_phone" placeholder="请输入开票电话" clearable />
              </el-form-item>
            </el-col>
            <el-col :span="12">
              <el-form-item label="开户行" prop="bank_name">
                <el-input v-model="formData.bank_name" placeholder="请输入开户行" clearable />
              </el-form-item>
            </el-col>
          </el-row>

          <el-row :gutter="20">
            <el-col :span="12">
              <el-form-item label="银行账号" prop="bank_account">
                <el-input v-model="formData.bank_account" placeholder="请输入银行账号" clearable />
              </el-form-item>
            </el-col>
          </el-row>
        </div>
      </div>
    </el-form>

    <template #footer>
      <div class="dialog-footer">
        <el-button type="primary" :loading="submitting" @click="handleSubmit" class="btn-save">
          保存
        </el-button>
        <el-button @click="visible = false" class="btn-cancel">取消</el-button>
      </div>
    </template>
  </el-dialog>
</template>

<script setup lang="ts">
import { ref, reactive } from 'vue'
import type { FormInstance, FormRules } from 'element-plus'
import { ElMessage } from 'element-plus'
import { User, Document } from '@element-plus/icons-vue'
import { createCustomer, updateCustomer, type CustomerItem } from '@/api/customer'

const emit = defineEmits<{
  (e: 'success'): void
}>()

const visible = ref(false)
const isEdit = ref(false)
const submitting = ref(false)
const formRef = ref<FormInstance>()
const currentId = ref<string>('')

const fieldLabels: Record<string, string> = {
  name: '客户名称',
  legal_person: '法人代表',
  address: '通讯地址',
  contact_person: '联系人',
  mobile: '手机',
  phone: '固定电话',
  email: '邮箱',
  fax: '传真',
  tax_title: '发票抬头',
  tax_number: '纳税人识别号',
  invoice_address: '开票地址',
  invoice_phone: '开票电话',
  bank_name: '开户行',
  bank_account: '银行账号'
}

const formData = reactive<CustomerItem>({
  name: '',
  legal_person: '',
  address: '',
  contact_person: '',
  mobile: '',
  phone: '',
  email: '',
  fax: '',
  tax_title: '',
  tax_number: '',
  invoice_address: '',
  invoice_phone: '',
  bank_name: '',
  bank_account: ''
})

const formRules: FormRules = {
  name: [{ required: true, message: '请输入客户名称', trigger: 'blur' }]
}

function open(row?: CustomerItem) {
  visible.value = true
  if (row && row.id) {
    isEdit.value = true
    currentId.value = row.id
    Object.assign(formData, {
      name: row.name || '',
      legal_person: row.legal_person || '',
      address: row.address || '',
      contact_person: row.contact_person || '',
      mobile: row.mobile || '',
      phone: row.phone || '',
      email: row.email || '',
      fax: row.fax || '',
      tax_title: row.tax_title || '',
      tax_number: row.tax_number || '',
      invoice_address: row.invoice_address || '',
      invoice_phone: row.invoice_phone || '',
      bank_name: row.bank_name || '',
      bank_account: row.bank_account || ''
    })
  } else {
    isEdit.value = false
    currentId.value = ''
    Object.assign(formData, {
      name: '',
      legal_person: '',
      address: '',
      contact_person: '',
      mobile: '',
      phone: '',
      email: '',
      fax: '',
      tax_title: '',
      tax_number: '',
      invoice_address: '',
      invoice_phone: '',
      bank_name: '',
      bank_account: ''
    })
  }
}

async function handleSubmit() {
  if (!formRef.value) return

  await formRef.value.validate(async (valid, fields) => {
    if (!valid && fields) {
      const errorKeys = Object.keys(fields)
      const errorNames = errorKeys.map((k) => fieldLabels[k] || k).join('、')
      ElMessage.warning(`请完善必填或格式错误信息：【${errorNames}】未通过校验，已自动定位至首个待办项`)
      return
    }

    submitting.value = true
    try {
      if (isEdit.value) {
        await updateCustomer(currentId.value, formData)
        ElMessage.success('客户信息更新成功')
      } else {
        await createCustomer(formData)
        ElMessage.success('客户信息创建成功')
      }
      visible.value = false
      emit('success')
    } catch (err) {
      // 错误由全局拦截器提示
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
:deep(.el-dialog__header) {
  border-bottom: 1px solid #ebeef5;
  margin-right: 0;
  padding: 14px 20px;
}

:deep(.el-dialog__title) {
  font-size: 16px;
  font-weight: 600;
  color: #009688;
}

:deep(.el-dialog__body) {
  padding: 16px 20px;
  background-color: #f7f9fa;
}

.customer-form {
  display: flex;
  flex-direction: column;
  gap: 14px;
}

.form-section {
  background: #fff;
  border: 1px solid #e4e7ed;
  border-radius: 4px;
  padding: 16px 20px 4px 20px;
}

.section-header {
  display: flex;
  align-items: center;
  gap: 6px;
  margin-bottom: 14px;
  padding-bottom: 8px;
  border-bottom: 1px solid #f2f3f5;
}

.section-icon {
  font-size: 16px;
  color: #009688;
}

.section-title {
  font-size: 14px;
  font-weight: 600;
  color: #009688;
}

:deep(.el-form-item) {
  margin-bottom: 14px;
}

:deep(.el-form-item__label) {
  font-size: 13px;
  color: #606266;
  font-weight: 400;
}

/* 消除绝对定位浮动红字 */
:deep(.el-form-item__error) {
  display: none !important;
}

.dialog-footer {
  display: flex;
  justify-content: flex-end;
  gap: 10px;
  padding: 10px 0 4px 0;
}

.btn-save {
  background-color: #009688;
  border-color: #009688;
  color: #fff;
  padding: 8px 22px;
  font-size: 13px;
}

.btn-save:hover {
  background-color: #00796b;
  border-color: #00796b;
}

.btn-cancel {
  padding: 8px 22px;
  font-size: 13px;
}
</style>
