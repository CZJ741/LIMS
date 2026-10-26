<template>
  <div class="login-wrapper">
    <div class="login-box">
      <div class="login-header">
        <h2 class="title">环境检测 LIMS 系统</h2>
        <p class="subtitle">CMA / CNAS 全生命周期合规管理平台</p>
      </div>

      <el-form
        ref="formRef"
        :model="form"
        :rules="rules"
        class="login-form"
        size="large"
        @keyup.enter="handleLogin"
      >
        <el-form-item prop="username">
          <el-input
            v-model="form.username"
            placeholder="请输入用户名 (管理员: admin)"
            :prefix-icon="User"
            clearable
          />
        </el-form-item>

        <el-form-item prop="password">
          <el-input
            v-model="form.password"
            type="password"
            placeholder="请输入密码 (默认: 123456)"
            :prefix-icon="Lock"
            show-password
            clearable
          />
        </el-form-item>

        <el-button
          type="primary"
          class="login-btn"
          :loading="loading"
          @click="handleLogin"
        >
          登 录
        </el-button>
      </el-form>

      <div class="login-footer">
        <span>检验检测机构合规技术保障体系</span>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage, type FormInstance, type FormRules } from 'element-plus'
import { User, Lock } from '@element-plus/icons-vue'
import { useUserStore } from '@/stores/user'

const router = useRouter()
const userStore = useUserStore()

const formRef = ref<FormInstance>()
const loading = ref(false)

const form = reactive({
  username: '',
  password: ''
})

const rules: FormRules = {
  username: [{ required: true, message: '请输入用户名', trigger: 'blur' }],
  password: [{ required: true, message: '请输入登录密码', trigger: 'blur' }]
}

const handleLogin = async () => {
  if (!formRef.value) return
  await formRef.value.validate((valid) => {
    if (!valid) return
    loading.value = true

    // 默认超级管理员身份认证校验
    setTimeout(() => {
      loading.value = false
      if (form.username === 'admin' && form.password === '123456') {
        userStore.login({
          username: 'admin',
          realName: '系统超级管理员',
          role: 'ADMIN',
          token: 'mock-admin-token-' + Date.now()
        })
        ElMessage.success('登录成功，欢迎进入 LIMS 工作台')
        router.push('/dashboard')
      } else {
        ElMessage.error('用户名或密码错误，请核对后重试')
      }
    }, 400)
  })
}
</script>

<style scoped>
.login-wrapper {
  height: 100vh;
  display: flex;
  align-items: center;
  justify-content: center;
  background: linear-gradient(135deg, #1f2d3d 0%, #304156 100%);
}

.login-box {
  width: 400px;
  padding: 36px 32px;
  background: #ffffff;
  border-radius: 8px;
  box-shadow: 0 8px 24px rgba(0, 0, 0, 0.2);
}

.login-header {
  text-align: center;
  margin-bottom: 28px;
}

.login-header .title {
  margin: 0;
  font-size: 22px;
  color: #303133;
  font-weight: 600;
}

.login-header .subtitle {
  margin: 8px 0 0 0;
  font-size: 13px;
  color: #909399;
}

.login-form {
  margin-top: 20px;
}

.login-btn {
  width: 100%;
  margin-top: 10px;
  letter-spacing: 2px;
}

.login-footer {
  margin-top: 24px;
  text-align: center;
  font-size: 12px;
  color: #a8abb2;
}
</style>
