<template>
  <div class="login-container">
    <div class="login-card">
      <div class="login-header">
        <h2>LIMS 实验室管理系统</h2>
        <p class="subtitle">GB/T 27025 / ISO/IEC 17025 规范数字化平台</p>
      </div>
      <el-form :model="loginForm" :rules="loginRules" ref="formRef" size="large">
        <el-form-item prop="username">
          <el-input
            v-model="loginForm.username"
            placeholder="账号 (默认: admin)"
            prefix-icon="User"
          />
        </el-form-item>
        <el-form-item prop="password">
          <el-input
            v-model="loginForm.password"
            type="password"
            placeholder="密码 (默认: 123456)"
            prefix-icon="Lock"
            show-password
            @keyup.enter="handleLogin"
          />
        </el-form-item>
        <el-form-item>
          <el-button
            type="primary"
            style="width: 100%"
            :loading="loading"
            @click="handleLogin"
          >
            登 录
          </el-button>
        </el-form-item>
      </el-form>
    </div>
  </div>
</template>

<script setup lang="ts">
import { reactive, ref } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage, type FormInstance, type FormRules } from 'element-plus'
import { useUserStore } from '@/stores/user'

const router = useRouter()
const userStore = useUserStore()

const formRef = ref<FormInstance>()
const loading = ref(false)

const loginForm = reactive({
  username: 'admin',
  password: 'password123'
})

const loginRules: FormRules = {
  username: [{ required: true, message: '请输入登录账号', trigger: 'blur' }],
  password: [{ required: true, message: '请输入登录密码', trigger: 'blur' }]
}

function handleLogin() {
  if (!formRef.value) return
  formRef.value.validate((valid) => {
    if (valid) {
      loading.value = true
      setTimeout(() => {
        userStore.setToken('mock_jwt_token_admin_super_user')
        userStore.setUserInfo({
          userId: 1,
          username: loginForm.username,
          realName: '系统管理员',
          roles: ['SUPER_ADMIN'],
          permissions: ['*:*:*']
        })
        loading.value = false
        ElMessage.success('登录成功')
        router.push('/home')
      }, 500)
    }
  })
}
</script>

<style scoped>
.login-container {
  height: 100vh;
  display: flex;
  justify-content: center;
  align-items: center;
  background: linear-gradient(135deg, #1f4037 0%, #99f2c8 100%);
}

.login-card {
  width: 420px;
  padding: 40px;
  background: #ffffff;
  border-radius: 8px;
  box-shadow: 0 10px 25px rgba(0, 0, 0, 0.15);
}

.login-header {
  text-align: center;
  margin-bottom: 30px;
}

.login-header h2 {
  margin: 0 0 8px 0;
  color: #303133;
}

.subtitle {
  margin: 0;
  font-size: 13px;
  color: #909399;
}
</style>
