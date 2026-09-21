<template>
  <div class="error-page">
    <el-result
      icon="error"
      title="401"
      sub-title="抱歉，您暂无权限访问该资源或登录态已失效"
    >
      <template #extra>
        <el-button type="primary" @click="$router.push('/login')">返回登录</el-button>
      </template>
    </el-result>
  </div>
</template>

<script setup lang="ts">
</script>

<style scoped>
.error-page {
  height: 100%;
  display: flex;
  align-items: center;
  justify-content: center;
}
</style>
