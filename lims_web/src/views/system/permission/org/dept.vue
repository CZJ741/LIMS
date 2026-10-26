<template>
  <el-card shadow="never" class="page-container">
    <template #header>
      <div class="card-header">
        <span class="title">组织维护 (部门/实验室架构)</span>
      </div>
    </template>
    <div class="empty-wrap">
      <el-empty description="暂未配置组织部门架构" :image-size="100">
        <el-button type="primary" size="default">新增组织部门</el-button>
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
