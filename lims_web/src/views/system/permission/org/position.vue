<template>
  <el-card shadow="never" class="page-container">
    <template #header>
      <div class="card-header">
        <span class="title">职位维护 (岗位职能体系)</span>
      </div>
    </template>
    <div class="empty-wrap">
      <el-empty description="暂无职位字典记录" :image-size="100">
        <el-button type="primary" size="default">新增职位</el-button>
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
