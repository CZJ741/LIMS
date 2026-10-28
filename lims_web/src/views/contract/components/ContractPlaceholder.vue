<template>
  <div class="contract-page-container">
    <div class="search-panel">
      <el-form :inline="true" :model="queryForm">
        <el-form-item label="单号/编号">
          <el-input v-model="queryForm.code" placeholder="请输入编号" clearable />
        </el-form-item>
        <el-form-item label="委托单位">
          <el-input v-model="queryForm.client" placeholder="请输入单位名称" clearable />
        </el-form-item>
        <el-form-item>
          <el-button type="primary" class="btn-search">查询</el-button>
          <el-button>重置</el-button>
        </el-form-item>
      </el-form>
    </div>

    <div class="toolbar">
      <el-button class="tool-btn" type="primary">
        + 新增{{ pageTitle }}
      </el-button>
    </div>

    <div class="placeholder-content">
      <el-empty :description="`${pageTitle} 模块已就位，等待业务接入`" />
    </div>
  </div>
</template>

<script setup lang="ts">
import { reactive } from 'vue'

defineProps<{
  pageTitle: string
}>()

const queryForm = reactive({
  code: '',
  client: ''
})
</script>

<style scoped>
.contract-page-container {
  background: #fff;
  padding: 12px 16px;
  min-height: calc(100vh - 90px);
}

.search-panel {
  background: #fafafa;
  border: 1px solid #ebeef5;
  padding: 10px 12px 2px 12px;
  margin-bottom: 12px;
}

.btn-search {
  background-color: #009688;
  border-color: #009688;
}

.toolbar {
  margin-bottom: 12px;
}

.tool-btn {
  background-color: #009688;
  border-color: #009688;
  color: #fff;
  border-radius: 3px;
  font-size: 13px;
  padding: 6px 14px;
}

.placeholder-content {
  margin-top: 40px;
}
</style>
