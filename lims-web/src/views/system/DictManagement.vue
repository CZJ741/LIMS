<template>
  <div class="dict-management-container">
    <el-row :gutter="20">
      <!-- 左侧字典分类 -->
      <el-col :span="8">
        <el-card shadow="never">
          <template #header>
            <div class="header-box">
              <span>字典分类</span>
              <el-button type="primary" size="small" icon="Plus" @click="openAddCategory">新增分类</el-button>
            </div>
          </template>
          <el-table :data="categoryList" highlight-current-row @current-change="handleCategorySelect" border>
            <el-table-column prop="categoryName" label="分类名称" />
            <el-table-column prop="categoryCode" label="编码" />
          </el-table>
        </el-card>
      </el-col>

      <!-- 右侧字典项明细 -->
      <el-col :span="16">
        <el-card shadow="never">
          <template #header>
            <div class="header-box">
              <span>字典项列表 - [{{ currentCategory?.categoryName || '未选择' }}]</span>
              <el-button type="success" size="small" icon="Plus" :disabled="!currentCategory" @click="openAddItem">
                新增字典项
              </el-button>
            </div>
          </template>
          <el-table :data="itemList" border stripe>
            <el-table-column prop="id" label="ID" width="70" align="center" />
            <el-table-column prop="itemLabel" label="显示标签" min-width="150" />
            <el-table-column prop="itemValue" label="字典键值" min-width="160" />
            <el-table-column prop="sortOrder" label="排序" width="80" align="center" />
            <el-table-column prop="remark" label="说明" min-width="180" />
          </el-table>
        </el-card>
      </el-col>
    </el-row>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import request from '@/utils/request'

const categoryList = ref<any[]>([])
const currentCategory = ref<any>(null)
const itemList = ref<any[]>([])

async function fetchCategories() {
  try {
    const res: any = await request.get('/system/dict/categories')
    categoryList.value = res || []
    if (categoryList.value.length > 0) {
      handleCategorySelect(categoryList.value[0])
    }
  } catch (e) {
    categoryList.value = [
      { id: 1, categoryName: '检测类别', categoryCode: 'detection_category' },
      { id: 2, categoryName: '检测项目', categoryCode: 'detection_item' },
      { id: 3, categoryName: '检测标准', categoryCode: 'detection_standard' }
    ]
    handleCategorySelect(categoryList.value[0])
  }
}

async function handleCategorySelect(cat: any) {
  if (!cat) return
  currentCategory.value = cat
  try {
    const res: any = await request.get(`/system/dict/item/by-category/${cat.categoryCode}`)
    itemList.value = res || []
  } catch (e) {
    itemList.value = [
      { id: 101, itemLabel: '水质与废水检测', itemValue: 'CAT_WATER', sortOrder: 1, remark: '生活饮用水、地表水' }
    ]
  }
}

function openAddCategory() {
  ElMessage.info('新增字典分类')
}

function openAddItem() {
  ElMessage.info('新增字典明细项')
}

onMounted(() => {
  fetchCategories()
})
</script>

<style scoped>
.header-box {
  display: flex;
  justify-content: space-between;
  align-items: center;
}
</style>
