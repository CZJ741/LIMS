<template>
  <el-dialog
    v-model="visible"
    :title="isEdit ? '修改单位/部门信息' : (isSub ? '新增下级单位/部门' : '新增单位/部门')"
    width="680px"
    destroy-on-close
    :close-on-click-modal="false"
  >
    <el-form
      ref="formRef"
      :model="formData"
      :rules="formRules"
      label-width="110px"
      label-position="right"
      :show-message="false"
      scroll-to-error
    >
      <el-row :gutter="16">
        <el-col :span="12">
          <el-form-item label="上级单位" prop="parent">
            <el-tree-select
              v-model="formData.parent"
              :data="treeSelectOptions"
              check-strictly
              :render-after-expand="false"
              placeholder="顶级机构（无上级）"
              clearable
              style="width: 100%"
              node-key="id"
              :props="{ label: 'name', value: 'id', children: 'children' }"
              :disabled="isSub"
            />
          </el-form-item>
        </el-col>
        <el-col :span="12">
          <el-form-item label="单位编码" prop="code">
            <el-input v-model="formData.code" placeholder="如 200, LAB-01" clearable />
          </el-form-item>
        </el-col>
      </el-row>

      <el-row :gutter="16">
        <el-col :span="24">
          <el-form-item label="单位名称" prop="name">
            <el-input v-model="formData.name" placeholder="请输入单位/部门/实验室全称" clearable />
          </el-form-item>
        </el-col>
      </el-row>

      <el-row :gutter="16">
        <el-col :span="12">
          <el-form-item label="单位类型" prop="org_type">
            <el-select v-model="formData.org_type" placeholder="请选择单位类型" style="width: 100%">
              <el-option label="单位" value="COMPANY" />
              <el-option label="分支机构" value="BRANCH" />
              <el-option label="部门" value="DEPT" />
              <el-option label="检测实验室" value="LAB" />
              <el-option label="采样/业务组" value="TEAM" />
            </el-select>
          </el-form-item>
        </el-col>
        <el-col :span="12">
          <el-form-item label="业务类型" prop="business_type">
            <el-select v-model="formData.business_type" placeholder="请选择业务类型" style="width: 100%">
              <el-option label="环境检测" value="ENV" />
              <el-option label="职业卫生" value="OCCUPATIONAL" />
              <el-option label="公共卫生" value="PUBLIC_HEALTH" />
              <el-option label="综合业务" value="COMPREHENSIVE" />
              <el-option label="其他" value="OTHER" />
            </el-select>
          </el-form-item>
        </el-col>
      </el-row>

      <el-row :gutter="16">
        <el-col :span="12">
          <el-form-item label="独立法人" prop="is_legal_person">
            <el-radio-group v-model="formData.is_legal_person">
              <el-radio :label="true">是</el-radio>
              <el-radio :label="false">否</el-radio>
            </el-radio-group>
          </el-form-item>
        </el-col>
        <el-col :span="12">
          <el-form-item label="状态" prop="is_active">
            <el-radio-group v-model="formData.is_active">
              <el-radio :label="true">有效</el-radio>
              <el-radio :label="false">停用</el-radio>
            </el-radio-group>
          </el-form-item>
        </el-col>
      </el-row>

      <el-row :gutter="16">
        <el-col :span="12">
          <el-form-item label="所属单位" prop="company_name">
            <el-input v-model="formData.company_name" placeholder="留空则自动继承母公司" clearable />
          </el-form-item>
        </el-col>
        <el-col :span="12">
          <el-form-item label="排序号" prop="sort_order">
            <el-input-number v-model="formData.sort_order" :min="0" :max="9999" style="width: 100%" />
          </el-form-item>
        </el-col>
      </el-row>

      <el-form-item label="职责描述" prop="description">
        <el-input
          v-model="formData.description"
          type="textarea"
          :rows="3"
          placeholder="请输入该机构或实验室的合规授权范围、测试业务职责或管理要求"
        />
      </el-form-item>
    </el-form>

    <template #footer>
      <div class="dialog-footer">
        <el-button @click="visible = false">取 消</el-button>
        <el-button type="primary" class="btn-save" :loading="submitting" @click="handleSubmit">
          保 存
        </el-button>
      </div>
    </template>
  </el-dialog>
</template>

<script setup lang="ts">
import { ref, reactive } from 'vue'
import type { FormInstance, FormRules } from 'element-plus'
import { ElMessage } from 'element-plus'
import { createDepartment, updateDepartment, type DepartmentItem } from '@/api/department'

const emit = defineEmits(['success'])

const visible = ref(false)
const submitting = ref(false)
const isEdit = ref(false)
const isSub = ref(false)
const editId = ref('')
const formRef = ref<FormInstance>()
const treeSelectOptions = ref<DepartmentItem[]>([])

const initialFormData = () => ({
  parent: null as string | null,
  code: '',
  name: '',
  org_type: 'COMPANY',
  business_type: 'ENV',
  is_legal_person: false,
  company_name: '',
  sort_order: 10,
  is_active: true,
  description: ''
})

const formData = reactive(initialFormData())

const fieldNameMap: Record<string, string> = {
  code: '单位编码',
  name: '单位名称',
  org_type: '单位类型',
  business_type: '业务类型'
}

const formRules: FormRules = {
  code: [{ required: true, trigger: 'blur' }],
  name: [{ required: true, trigger: 'blur' }],
  org_type: [{ required: true, trigger: 'change' }],
  business_type: [{ required: true, trigger: 'change' }]
}

function open(treeData: DepartmentItem[], row?: DepartmentItem, subParentRow?: DepartmentItem) {
  treeSelectOptions.value = treeData
  submitting.value = false
  visible.value = true

  if (row) {
    // 编辑模式
    isEdit.value = true
    isSub.value = false
    editId.value = row.id
    Object.assign(formData, {
      parent: row.parent || null,
      code: row.code,
      name: row.name,
      org_type: row.org_type,
      business_type: row.business_type,
      is_legal_person: row.is_legal_person,
      company_name: row.company_name || '',
      sort_order: row.sort_order ?? 10,
      is_active: row.is_active,
      description: row.description || ''
    })
  } else if (subParentRow) {
    // 新增下级模式
    isEdit.value = false
    isSub.value = true
    editId.value = ''
    Object.assign(formData, initialFormData())
    formData.parent = subParentRow.id
    formData.company_name = subParentRow.company_name || subParentRow.name
    formData.org_type = 'DEPT'
  } else {
    // 顶级新增模式
    isEdit.value = false
    isSub.value = false
    editId.value = ''
    Object.assign(formData, initialFormData())
  }
}

async function handleSubmit() {
  if (!formRef.value) return
  await formRef.value.validate(async (valid, fields) => {
    if (!valid && fields) {
      const errLabels = Object.keys(fields)
        .map(k => fieldNameMap[k] || k)
        .join('、')
      ElMessage.warning(`请完善必填或格式错误信息：【${errLabels}】未通过校验，已自动定位至待办项`)
      return
    }

    submitting.value = true
    try {
      if (isEdit.value) {
        await updateDepartment(editId.value, formData)
        ElMessage.success(`单位【${formData.name}】更新成功`)
      } else {
        await createDepartment(formData)
        ElMessage.success(`单位【${formData.name}】创建成功`)
      }
      visible.value = false
      emit('success')
    } catch (err) {
      // 错误由全局拦截器自动直观显示
    } finally {
      submitting.value = false
    }
  })
}

defineExpose({ open })
</script>

<style scoped>
:deep(.el-form-item__error) {
  display: none !important;
}

.btn-save {
  background-color: #009688;
  border-color: #009688;
}

.dialog-footer {
  text-align: right;
}
</style>
