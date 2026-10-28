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
import request from '@/utils/request'

const router = useRouter()
const userStore = useUserStore()

const formRef = ref<FormInstance>()
const loading = ref(false)

const form = reactive({
  username: '',
  password: '123456'
})

const rules: FormRules = {
  username: [{ required: true, message: '请输入用户名', trigger: 'blur' }],
  password: [{ required: true, message: '请输入登录密码', trigger: 'blur' }]
}

const handleLogin = async () => {
  if (!formRef.value) return
  await formRef.value.validate(async (valid) => {
    if (!valid) return
    loading.value = true
    try {
      const res: any = await request({
        url: '/auth/login/',
        method: 'post',
        data: {
          username: form.username,
          password: form.password
        }
      })
      const access = res.data?.access
      const user = res.data?.user
      if (access) {
        // 解析角色列表并设定初始默认激活的角色
        const rawRoles = user?.roles || []
        const userRoles = rawRoles.map((r: any) => ({
          id: r.id,
          name: r.name,
          code: r.code,
          menu_permissions: r.menu_permissions || []
        }))

        // 若用户是系统超级管理员或拥有 ADMIN 角色，优先激活 ADMIN，否则激活第一个可用角色
        let defaultRole = userRoles.find((r: any) => r.code === 'ADMIN')
        if (!defaultRole && userRoles.length > 0) {
          defaultRole = userRoles[0]
        }

        let initialPermissions: string[] = []
        if (user?.username === 'admin' || defaultRole?.code === 'ADMIN') {
          initialPermissions = [
            '/dashboard',
            '/system',
            '/system/permission',
            '/system/permission/org',
            '/system/permission/org/dept',
            '/system/permission/org/user-org',
            '/system/permission/org/role',
            '/system/permission/org/position',
            '/system/permission/user'
          ]
        } else if (defaultRole) {
          initialPermissions = defaultRole.menu_permissions || ['/dashboard']
        } else {
          initialPermissions = ['/dashboard']
        }

        userStore.login({
          username: user?.username || form.username,
          realName: user?.real_name || '系统用户',
          token: access,
          roles: userRoles,
          activeRoleId: defaultRole?.id,
          activeRoleName: defaultRole?.name || '普通人员',
          activeRoleCode: defaultRole?.code || 'USER',
          permissions: initialPermissions
        })
        ElMessage.success('登录成功，欢迎进入 LIMS 工作台')
        router.push('/dashboard')
      }
    } catch (err: any) {
      // 错误已由拦截器处理
    } finally {
      loading.value = false
    }
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
