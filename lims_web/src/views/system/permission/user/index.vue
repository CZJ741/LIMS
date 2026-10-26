<template>
  <el-card shadow="never" class="page-container">
    <template #header>
      <div class="card-header">
        <span class="title">用户管理 (系统账号与安全控制)</span>
      </div>
    </template>
    <div class="empty-wrap">
      <el-empty description="暂无普通用户账号" :image-size="100">
        <el-button type="primary" size="default">创建系统用户</el-button>
      </el-empty>
    </div>
  </el-card>
</template>

<style scoped>
.page-container {
  min-height: calc(100vh - 100px);
}
.card-header {
  font-weight: 600;
  font-size: 15px;
  color: #303133;
}
.empty-wrap {
  padding: 60px 0;
}
</style>
