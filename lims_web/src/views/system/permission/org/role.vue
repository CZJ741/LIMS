<template>
  <el-card shadow="never" class="page-container">
    <template #header>
      <div class="card-header">
        <span class="title">角色管理 (岗位角色与功能授权)</span>
      </div>
    </template>
    <div class="empty-wrap">
      <el-empty description="暂无自定义角色" :image-size="100">
        <el-button type="primary" size="default">新建角色</el-button>
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
