<template>
  <el-dialog
    v-model="visible"
    :title="dialogTitle"
    width="1050px"
    destroy-on-close
    :close-on-click-modal="false"
  >
    <el-form
      ref="formRef"
      :model="form"
      :rules="rules"
      label-width="120px"
      label-position="right"
      size="default"
      :disabled="currentMode === 'view'"
      :show-message="false"
      scroll-to-error
    >
      <!-- 第一行 -->
      <el-row :gutter="20">
        <el-col :span="8">
          <el-form-item label="人员编码" prop="user_code" required>
            <el-input
              v-model="form.user_code"
              placeholder="请输入人员编码"
              :disabled="currentMode === 'edit'"
              clearable
            />
          </el-form-item>
        </el-col>
        <el-col :span="8">
          <el-form-item label="人员姓名" prop="real_name" required>
            <el-input v-model="form.real_name" placeholder="请输入人员姓名" clearable />
          </el-form-item>
        </el-col>
        <el-col :span="8">
          <el-form-item label="所属部门" prop="department" required>
            <el-select v-model="form.department" placeholder="请选择所属部门" style="width: 100%">
              <el-option
                v-for="dept in departments"
                :key="dept.id"
                :label="dept.name"
                :value="dept.id"
              />
            </el-select>
          </el-form-item>
        </el-col>
      </el-row>

      <!-- 第二行 -->
      <el-row :gutter="20">
        <el-col :span="8">
          <el-form-item label="所属单位" prop="company">
            <el-input v-model="form.company" placeholder="请输入所属单位" clearable />
          </el-form-item>
        </el-col>
        <el-col :span="8">
          <el-form-item label="性别" prop="gender">
            <el-select v-model="form.gender" placeholder="请选择" style="width: 100%">
              <el-option label="男" value="male" />
              <el-option label="女" value="female" />
              <el-option label="其他" value="other" />
            </el-select>
          </el-form-item>
        </el-col>
        <el-col :span="8">
          <el-form-item label="身份证号码" prop="id_card">
            <el-input v-model="form.id_card" placeholder="请输入身份证号码" clearable />
          </el-form-item>
        </el-col>
      </el-row>

      <!-- 第三行 -->
      <el-row :gutter="20">
        <el-col :span="8">
          <el-form-item label="电话号码" prop="phone">
            <el-input v-model="form.phone" placeholder="请输入电话号码" clearable />
          </el-form-item>
        </el-col>
        <el-col :span="8">
          <el-form-item label="邮件" prop="email">
            <el-input v-model="form.email" placeholder="请输入邮件地址" clearable />
          </el-form-item>
        </el-col>
        <el-col :span="8">
          <el-form-item label="工号" prop="employee_no">
            <el-input v-model="form.employee_no" placeholder="请输入工号" clearable />
          </el-form-item>
        </el-col>
      </el-row>

      <!-- 第四行 -->
      <el-row :gutter="20">
        <el-col :span="8">
          <el-form-item label="出生年月" prop="birth_date">
            <el-date-picker
              v-model="form.birth_date"
              type="date"
              placeholder="选择日期"
              value-format="YYYY-MM-DD"
              style="width: 100%"
            />
          </el-form-item>
        </el-col>
        <el-col :span="8">
          <el-form-item label="职务/职称" prop="job_title">
            <el-input v-model="form.job_title" placeholder="请输入职务或职称" clearable />
          </el-form-item>
        </el-col>
        <el-col :span="8">
          <el-form-item label="学历学位" prop="education">
            <el-input v-model="form.education" placeholder="请输入学历学位" clearable />
          </el-form-item>
        </el-col>
      </el-row>

      <!-- 第五行 -->
      <el-row :gutter="20">
        <el-col :span="8">
          <el-form-item label="毕业院校" prop="graduated_school">
            <el-input v-model="form.graduated_school" placeholder="请输入毕业院校" clearable />
          </el-form-item>
        </el-col>
        <el-col :span="8">
          <el-form-item label="所学专业" prop="major">
            <el-input v-model="form.major" placeholder="请输入所学专业" clearable />
          </el-form-item>
        </el-col>
        <el-col :span="8">
          <el-form-item label="参加工作时间" prop="work_start_date">
            <el-date-picker
              v-model="form.work_start_date"
              type="date"
              placeholder="选择日期"
              value-format="YYYY-MM-DD"
              style="width: 100%"
            />
          </el-form-item>
        </el-col>
      </el-row>

      <!-- 第六行 -->
      <el-row :gutter="20">
        <el-col :span="8">
          <el-form-item label="从事本岗位年限" prop="years_in_position">
            <el-input-number
              v-model="form.years_in_position"
              :min="0"
              :max="60"
              :precision="1"
              :step="0.5"
              placeholder="年限"
              style="width: 100%"
            />
          </el-form-item>
        </el-col>
      </el-row>

      <!-- 第七行：培训经历 -->
      <el-row :gutter="20">
        <el-col :span="24">
          <el-form-item label="培训经历" prop="training_experience">
            <el-input
              v-model="form.training_experience"
              type="textarea"
              :rows="3"
              placeholder="请输入培训经历"
            />
          </el-form-item>
        </el-col>
      </el-row>

      <!-- 第八行：工作经历 -->
      <el-row :gutter="20">
        <el-col :span="24">
          <el-form-item label="工作经历" prop="work_experience">
            <el-input
              v-model="form.work_experience"
              type="textarea"
              :rows="3"
              placeholder="请输入工作经历"
            />
          </el-form-item>
        </el-col>
      </el-row>

      <!-- 第九行：常住地 -->
      <el-row :gutter="20">
        <el-col :span="12">
          <el-form-item label="常住地" prop="residence_area">
            <el-input v-model="form.residence_area" placeholder="🔍 选择区域 / 输入常住地址" clearable />
          </el-form-item>
        </el-col>
      </el-row>
    </el-form>

    <template #footer>
      <div class="dialog-footer">
        <!-- 查看模式：提供【编辑】与【关闭】按钮 -->
        <template v-if="currentMode === 'view'">
          <el-button @click="visible = false">关闭</el-button>
          <el-button type="primary" class="btn-theme" @click="handleSwitchToEdit">
            编辑信息
          </el-button>
        </template>

        <!-- 编辑模式：提供【取消】与【保存修改】按钮 -->
        <template v-else-if="currentMode === 'edit'">
          <el-button @click="handleCancelEdit">取消</el-button>
          <el-button type="primary" class="btn-theme" :loading="submitting" @click="handleSubmit">
            保存修改
          </el-button>
        </template>

        <!-- 新增模式：提供【取消】与【保存】按钮 -->
        <template v-else>
          <el-button @click="visible = false">取消</el-button>
          <el-button type="primary" class="btn-theme" :loading="submitting" @click="handleSubmit">
            保存
          </el-button>
        </template>
      </div>
    </template>
  </el-dialog>
</template>

<script setup lang="ts">
import { ref, reactive, computed } from 'vue'
import { ElMessage, type FormInstance, type FormRules } from 'element-plus'
import { createUser, updateUser, type DepartmentItem, type UserItem } from '@/api/user'

defineProps<{
  departments: DepartmentItem[]
}>()

const emit = defineEmits<{
  (e: 'success'): void
}>()

const visible = ref(false)
const currentMode = ref<'create' | 'view' | 'edit'>('create')
const submitting = ref(false)
const formRef = ref<FormInstance>()
let originalUserData: UserItem | null = null

const dialogTitle = computed(() => {
  if (currentMode.value === 'view') return '人员档案详情'
  if (currentMode.value === 'edit') return '编辑人员档案'
  return '新增人员'
})

const initialForm = () => ({
  id: undefined as number | undefined,
  user_code: '',
  real_name: '',
  department: null as string | null,
  company: '',
  gender: 'male',
  id_card: '',
  phone: '',
  email: '',
  employee_no: '',
  birth_date: null as string | null,
  job_title: '',
  education: '',
  graduated_school: '',
  major: '',
  work_start_date: null as string | null,
  years_in_position: null as number | null,
  training_experience: '',
  work_experience: '',
  residence_area: ''
})

const form = reactive(initialForm())

const rules: FormRules = {
  user_code: [{ required: true, message: '请输入人员编码', trigger: 'blur' }],
  real_name: [{ required: true, message: '请输入人员姓名', trigger: 'blur' }],
  department: [{ required: true, message: '请选择所属部门', trigger: 'change' }]
}

const open = (row?: UserItem, viewOnly = false) => {
  visible.value = true
  if (row) {
    currentMode.value = viewOnly ? 'view' : 'edit'
    originalUserData = { ...row }
    Object.assign(form, row)
  } else {
    currentMode.value = 'create'
    originalUserData = null
    Object.assign(form, initialForm())
  }
}

const handleSwitchToEdit = () => {
  currentMode.value = 'edit'
}

const handleCancelEdit = () => {
  if (originalUserData) {
    Object.assign(form, originalUserData)
    currentMode.value = 'view'
  } else {
    visible.value = false
  }
}

const handleSubmit = async () => {
  if (!formRef.value) return
  await formRef.value.validate(async (valid, fields) => {
    if (!valid) {
      const errorFieldNames: Record<string, string> = {
        user_code: '人员编码',
        real_name: '人员姓名',
        department: '所属部门',
        email: '电子邮箱',
        phone: '联系电话',
        id_card: '身份证号'
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
      if (currentMode.value === 'edit' && form.id) {
        await updateUser(form.id, form)
        ElMessage.success('人员信息修改成功')
      } else {
        await createUser(form)
        ElMessage.success('人员档案保存成功')
      }
      visible.value = false
      emit('success')
    } catch (err: any) {
      // 错误信息已由 Axios 拦截器或后端异常拦截提示
    } finally {
      submitting.value = false
    }
  })
}

defineExpose({ open })
</script>

<style scoped>
:deep(.el-form-item__label) {
  font-size: 13px;
  color: #606266;
}
:deep(.el-form-item__error) {
  display: none !important;
}
.btn-theme {
  background-color: #009688 !important;
  border-color: #009688 !important;
  color: #fff !important;
}
.btn-theme:hover {
  opacity: 0.9;
}
.dialog-footer {
  text-align: right;
}
</style>
