<template>
  <el-dialog
    v-model="visible"
    title="配置检测因子与分析方法"
    width="86%"
    top="4vh"
    class="select-detect-modal"
    append-to-body
    destroy-on-close
    :close-on-click-modal="false"
    @closed="emit('close')"
  >
    <div class="modal-workspace">
      <!-- 左侧：因子库宽视野检索区 (70%) -->
      <div class="library-panel">
        <!-- 顶部高效检索工具栏 -->
        <div class="filter-bar">
          <div class="quick-types">
            <span
              v-for="cat in categoryList"
              :key="cat"
              class="cat-chip"
              :class="{ 'is-active': selectedCategory === cat }"
              @click="handleCategoryChange(cat)"
            >
              {{ cat }}
            </span>
          </div>
          <div class="search-input-box">
            <el-input
              v-model="searchKeyword"
              placeholder="快速搜索因子名称、标准编号、分析方法..."
              clearable
              size="default"
              class="quick-search-input"
            >
              <template #prefix>
                <el-icon class="search-icon"><Search /></el-icon>
              </template>
            </el-input>
          </div>
        </div>

        <!-- 因子标准表格 (点击行直接切换选择状态) -->
        <div class="table-container">
          <el-table
            ref="libraryTableRef"
            :data="filteredItems"
            size="small"
            stripe
            border
            height="460px"
            row-key="id"
            header-cell-class-name="custom-table-header"
            :row-class-name="getRowClassName"
            @row-click="handleRowClick"
          >
            <!-- 勾选指示器 -->
            <el-table-column width="46" align="center">
              <template #default="{ row }">
                <el-checkbox
                  :model-value="isSelected(row.id)"
                  @click.stop
                  @change="() => toggleItem(row)"
                />
              </template>
            </el-table-column>

            <el-table-column prop="name" label="检测项目" width="130" show-overflow-tooltip>
              <template #default="{ row }">
                <span class="item-name" :class="{ 'is-selected': isSelected(row.id) }">
                  {{ row.name }}
                </span>
              </template>
            </el-table-column>

            <el-table-column prop="category" label="所属分类" width="130" show-overflow-tooltip>
              <template #default="{ row }">
                <span class="cat-tag">{{ row.category }}</span>
              </template>
            </el-table-column>

            <el-table-column prop="standard_no" label="标准编号" width="150" show-overflow-tooltip>
              <template #default="{ row }">
                <span class="standard-code">{{ row.standard_no }}</span>
              </template>
            </el-table-column>

            <el-table-column prop="method_name" label="检测分析方法" min-width="200" show-overflow-tooltip />

            <el-table-column prop="price" label="基准单价" width="100" align="right">
              <template #default="{ row }">
                <span class="price-text">¥ {{ Number(row.price || 0).toFixed(2) }}</span>
              </template>
            </el-table-column>

            <el-table-column prop="scene_type" label="检测场景" width="90" align="center">
              <template #default="{ row }">
                <el-tag size="small" type="info" effect="plain">{{ row.scene_type || '实验室' }}</el-tag>
              </template>
            </el-table-column>
          </el-table>
        </div>

        <!-- 底部快捷批量条 -->
        <div class="library-footer">
          <div class="batch-tips">
            <span>💡 提示：单击表格任意行可<b>直接勾选/移出</b>右侧清单，无须重复点击移动</span>
          </div>
          <div class="batch-buttons">
            <el-button size="small" @click="handleSelectAllCurrentPage">全选当前页</el-button>
            <el-button size="small" @click="handleDeselectAllCurrentPage">反选/清空当前页</el-button>
          </div>
        </div>
      </div>

      <!-- 右侧：即时响应的清单与核算面板 (30%) -->
      <div class="selected-panel">
        <!-- 标题与状态栏 -->
        <div class="panel-header">
          <div class="header-title">
            <span>已选检测因子</span>
            <span class="badge-count">{{ selectedList.length }}</span>
          </div>
          <el-button
            v-if="selectedList.length > 0"
            type="danger"
            link
            size="small"
            @click="handleClearAll"
          >
            一键清空
          </el-button>
        </div>

        <!-- 空态指示 -->
        <div v-if="selectedList.length === 0" class="empty-bucket">
          <el-empty :image-size="60" description="请从左侧点击勾选检测因子" />
        </div>

        <!-- 已选因子卡片流 -->
        <div v-else class="bucket-list">
          <transition-group name="list-fade">
            <div
              v-for="(item, idx) in selectedList"
              :key="item.id"
              class="bucket-card"
            >
              <div class="card-index">{{ idx + 1 }}</div>
              <div class="card-content">
                <div class="card-top">
                  <span class="card-name">{{ item.name }}</span>
                  <span class="card-price">¥{{ Number(item.price || 0).toFixed(2) }}</span>
                </div>
                <div class="card-sub">{{ item.standard_no }} · {{ item.method_name }}</div>
              </div>
              <div class="card-action" @click="removeItem(item.id)">
                <el-icon class="remove-icon"><Close /></el-icon>
              </div>
            </div>
          </transition-group>
        </div>

        <!-- 右侧底部实时统计核算 -->
        <div class="panel-summary">
          <div class="summary-row">
            <span>项目总数：</span>
            <span class="summary-val font-bold">{{ selectedList.length }} 项</span>
          </div>
          <div class="summary-row">
            <span>基准单价合计：</span>
            <span class="summary-val text-green font-bold">¥ {{ totalPrice.toFixed(2) }}</span>
          </div>
        </div>
      </div>
    </div>

    <!-- 弹窗底部操作条 -->
    <template #footer>
      <div class="modal-footer">
        <div class="footer-left-info">
          <span>当前方案已配置 <b>{{ selectedList.length }}</b> 个检测因子，基准总额 <b>¥ {{ totalPrice.toFixed(2) }}</b></span>
        </div>
        <div class="footer-right-buttons">
          <el-button @click="visible = false">取消</el-button>
          <el-button type="primary" class="btn-confirm" @click="handleConfirm">
            确认应用项目 ({{ selectedList.length }})
          </el-button>
        </div>
      </div>
    </template>
  </el-dialog>
</template>

<script setup lang="ts">
import { ref, computed } from 'vue'
import { Search, Close } from '@element-plus/icons-vue'
import { ElMessage } from 'element-plus'

export interface DetectFactorItem {
  id: string
  name: string
  category: string
  standard_no: string
  method_name: string
  price: number
  scene_type?: string
}

const emit = defineEmits<{
  (e: 'confirm', items: DetectFactorItem[]): void
  (e: 'close'): void
}>()

const visible = ref(false)
const searchKeyword = ref('')
const selectedCategory = ref('全部')

// 分类快捷标签
const categoryList = ref([
  '全部',
  '水和废水',
  '环境空气与废气',
  '土壤与沉积物',
  '噪声与振动',
  '固体废物'
])

// 基础演示因子库数据 (后续支持表格一键导入)
const rawFactorLibrary: DetectFactorItem[] = [
  {
    id: 'f-1',
    name: '化学需氧量 (COD)',
    category: '水和废水',
    standard_no: 'HJ 828-2017',
    method_name: '重铬酸盐法',
    price: 80.0,
    scene_type: '实验室'
  },
  {
    id: 'f-2',
    name: '氨氮 (NH3-N)',
    category: '水和废水',
    standard_no: 'HJ 535-2009',
    method_name: '纳氏试剂分光光度法',
    price: 70.0,
    scene_type: '实验室'
  },
  {
    id: 'f-3',
    name: '总磷 (TP)',
    category: '水和废水',
    standard_no: 'GB 11893-89',
    method_name: '钼酸铵分光光度法',
    price: 80.0,
    scene_type: '实验室'
  },
  {
    id: 'f-4',
    name: '总氮 (TN)',
    category: '水和废水',
    standard_no: 'HJ 636-2012',
    method_name: '碱性过硫酸钾消解紫外分光光度法',
    price: 90.0,
    scene_type: '实验室'
  },
  {
    id: 'f-5',
    name: 'pH值',
    category: '水和废水',
    standard_no: 'GB/T 6920-86',
    method_name: '玻璃电极法',
    price: 30.0,
    scene_type: '现场+实验室'
  },
  {
    id: 'f-6',
    name: '悬浮物 (SS)',
    category: '水和废水',
    standard_no: 'GB 11901-89',
    method_name: '重量法',
    price: 50.0,
    scene_type: '实验室'
  },
  {
    id: 'f-7',
    name: '五日生化需氧量 (BOD5)',
    category: '水和废水',
    standard_no: 'HJ 505-2009',
    method_name: '稀释与接种法',
    price: 120.0,
    scene_type: '实验室'
  },
  {
    id: 'f-8',
    name: '石油类',
    category: '水和废水',
    standard_no: 'HJ 970-2018',
    method_name: '紫外分光光度法',
    price: 100.0,
    scene_type: '实验室'
  },
  {
    id: 'f-9',
    name: '颗粒物 (PM)',
    category: '环境空气与废气',
    standard_no: 'GB/T 16157-1996',
    method_name: '重量法',
    price: 150.0,
    scene_type: '现场采样'
  },
  {
    id: 'f-10',
    name: '二氧化硫 (SO2)',
    category: '环境空气与废气',
    standard_no: 'HJ 57-2017',
    method_name: '定电位电解法',
    price: 80.0,
    scene_type: '现场直读'
  },
  {
    id: 'f-11',
    name: '氮氧化物 (NOx)',
    category: '环境空气与废气',
    standard_no: 'HJ 692-2014',
    method_name: '非分散红外吸收法',
    price: 90.0,
    scene_type: '现场直读'
  },
  {
    id: 'f-12',
    name: '非甲烷总烃 (NMHC)',
    category: '环境空气与废气',
    standard_no: 'HJ 38-2017',
    method_name: '气相色谱法',
    price: 260.0,
    scene_type: '实验室'
  },
  {
    id: 'f-13',
    name: '土壤pH值',
    category: '土壤与沉积物',
    standard_no: 'HJ 962-2018',
    method_name: '电位法',
    price: 40.0,
    scene_type: '实验室'
  },
  {
    id: 'f-14',
    name: '土壤铅、镉、铬、铜、镍',
    category: '土壤与沉积物',
    standard_no: 'GB 15618-2018',
    method_name: '电感耦合等离子体质谱法',
    price: 360.0,
    scene_type: '实验室'
  },
  {
    id: 'f-15',
    name: '厂界环境噪声',
    category: '噪声与振动',
    standard_no: 'GB 12348-2008',
    method_name: '积分声级计法',
    price: 100.0,
    scene_type: '现场直读'
  }
]

// 已选择清单
const selectedList = ref<DetectFactorItem[]>([])

// 智能过滤列表
const filteredItems = computed(() => {
  let list = rawFactorLibrary
  if (selectedCategory.value !== '全部') {
    list = list.filter((i) => i.category === selectedCategory.value)
  }
  if (searchKeyword.value.trim()) {
    const kw = searchKeyword.value.trim().toLowerCase()
    list = list.filter(
      (i) =>
        i.name.toLowerCase().includes(kw) ||
        i.standard_no.toLowerCase().includes(kw) ||
        i.method_name.toLowerCase().includes(kw) ||
        i.category.toLowerCase().includes(kw)
    )
  }
  return list
})

// 总金额
const totalPrice = computed(() => {
  return selectedList.value.reduce((sum, item) => sum + (item.price || 0), 0)
})

function isSelected(id: string) {
  return selectedList.value.some((i) => i.id === id)
}

function getRowClassName({ row }: { row: DetectFactorItem }) {
  return isSelected(row.id) ? 'row-is-selected' : ''
}

// 单击行即切换
function handleRowClick(row: DetectFactorItem) {
  toggleItem(row)
}

function toggleItem(row: DetectFactorItem) {
  const index = selectedList.value.findIndex((i) => i.id === row.id)
  if (index > -1) {
    selectedList.value.splice(index, 1)
  } else {
    selectedList.value.push({ ...row })
  }
}

function removeItem(id: string) {
  const index = selectedList.value.findIndex((i) => i.id === id)
  if (index > -1) {
    selectedList.value.splice(index, 1)
  }
}

function handleCategoryChange(cat: string) {
  selectedCategory.value = cat
}

function handleClearAll() {
  selectedList.value = []
}

function handleSelectAllCurrentPage() {
  filteredItems.value.forEach((row) => {
    if (!isSelected(row.id)) {
      selectedList.value.push({ ...row })
    }
  })
}

function handleDeselectAllCurrentPage() {
  const currentIds = new Set(filteredItems.value.map((i) => i.id))
  selectedList.value = selectedList.value.filter((i) => !currentIds.has(i.id))
}

function open(defaultCategory?: string, initialSelected?: DetectFactorItem[]) {
  visible.value = true
  searchKeyword.value = ''
  if (defaultCategory && categoryList.value.includes(defaultCategory)) {
    selectedCategory.value = defaultCategory
  } else {
    selectedCategory.value = '全部'
  }
  selectedList.value = initialSelected ? [...initialSelected] : []
}

function handleConfirm() {
  emit('confirm', [...selectedList.value])
  visible.value = false
  ElMessage.success(`已成功为方案配置 ${selectedList.value.length} 个检测因子`)
}

defineExpose({
  open
})
</script>

<style scoped>
:deep(.select-detect-modal .el-dialog__header) {
  padding: 12px 20px;
  margin-right: 0;
  border-bottom: 1px solid #ebeef5;
  background-color: #fcfcfc;
}

:deep(.select-detect-modal .el-dialog__title) {
  color: #009688;
  font-weight: 600;
  font-size: 15px;
}

:deep(.select-detect-modal .el-dialog__body) {
  padding: 12px 16px;
  background-color: #f7f9fa;
}

/* 整体工作区双栏 */
.modal-workspace {
  display: flex;
  gap: 14px;
  height: 540px;
}

/* 左侧：因子标准库 */
.library-panel {
  flex: 7;
  display: flex;
  flex-direction: column;
  background: #fff;
  border: 1px solid #ebeef5;
  border-radius: 4px;
  padding: 12px;
  box-sizing: border-box;
}

.filter-bar {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 10px;
  gap: 12px;
}

.quick-types {
  display: flex;
  flex-wrap: wrap;
  gap: 6px;
}

.cat-chip {
  padding: 3px 10px;
  font-size: 12px;
  border-radius: 12px;
  background-color: #f4f4f5;
  color: #606266;
  cursor: pointer;
  transition: all 0.2s ease;
  border: 1px solid transparent;
}

.cat-chip:hover {
  color: #009688;
  background-color: #e8f8f5;
}

.cat-chip.is-active {
  background-color: #009688;
  color: #fff;
  font-weight: 500;
}

.search-input-box {
  width: 280px;
}

.quick-search-input :deep(.el-input__wrapper) {
  border-radius: 16px;
}

.search-icon {
  color: #909399;
}

.table-container {
  flex: 1;
  overflow: hidden;
}

:deep(.custom-table-header th) {
  background-color: #f2f5f8 !important;
  color: #333333;
  font-weight: 600;
  font-size: 12px;
  height: 36px;
}

/* 行高亮选中与交互动效 */
:deep(.el-table__row) {
  cursor: pointer;
}

:deep(.el-table__row.row-is-selected) {
  background-color: #e8f8f5 !important;
}

:deep(.el-table__row.row-is-selected:hover > td.el-table__cell) {
  background-color: #d2f2ec !important;
}

.item-name {
  font-weight: 500;
  color: #303133;
}

.item-name.is-selected {
  color: #009688;
  font-weight: 600;
}

.cat-tag {
  color: #606266;
  font-size: 11px;
}

.standard-code {
  font-family: Consolas, monospace;
  font-size: 11px;
  color: #409eff;
}

.price-text {
  font-family: Consolas, monospace;
  font-weight: 600;
  color: #ff9800;
}

.library-footer {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-top: 8px;
  padding-top: 8px;
  border-top: 1px dashed #ebeef5;
}

.batch-tips {
  font-size: 12px;
  color: #909399;
}

.batch-tips b {
  color: #009688;
}

/* 右侧：即时清单面板 */
.selected-panel {
  flex: 3;
  display: flex;
  flex-direction: column;
  background: #fff;
  border: 1px solid #ebeef5;
  border-radius: 4px;
  padding: 12px;
  box-sizing: border-box;
}

.panel-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding-bottom: 8px;
  border-bottom: 1px solid #ebeef5;
}

.header-title {
  display: flex;
  align-items: center;
  gap: 8px;
  font-size: 13px;
  font-weight: 600;
  color: #303133;
}

.badge-count {
  display: inline-block;
  padding: 0 6px;
  height: 18px;
  line-height: 18px;
  border-radius: 9px;
  background-color: #009688;
  color: #fff;
  font-size: 11px;
}

.empty-bucket {
  flex: 1;
  display: flex;
  align-items: center;
  justify-content: center;
}

.bucket-list {
  flex: 1;
  overflow-y: auto;
  padding: 8px 4px 8px 0;
}

.bucket-card {
  display: flex;
  align-items: center;
  padding: 8px 10px;
  margin-bottom: 6px;
  background-color: #fafbfc;
  border: 1px solid #ebeef5;
  border-left: 3px solid #009688;
  border-radius: 3px;
  transition: all 0.2s ease;
}

.bucket-card:hover {
  box-shadow: 0 2px 6px rgba(0, 150, 136, 0.12);
  border-color: #b2dfdb;
}

.card-index {
  width: 20px;
  font-size: 11px;
  color: #909399;
  font-weight: 500;
}

.card-content {
  flex: 1;
  min-width: 0;
}

.card-top {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.card-name {
  font-size: 12px;
  font-weight: 600;
  color: #333;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.card-price {
  font-size: 12px;
  font-weight: 600;
  color: #ff9800;
  font-family: Consolas, monospace;
}

.card-sub {
  font-size: 11px;
  color: #909399;
  margin-top: 2px;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.card-action {
  cursor: pointer;
  padding: 2px 4px;
  border-radius: 2px;
  margin-left: 6px;
  color: #c0c4cc;
}

.card-action:hover {
  color: #f56c6c;
  background-color: #fef0f0;
}

.remove-icon {
  font-size: 13px;
}

/* 动效 */
.list-fade-enter-active,
.list-fade-leave-active {
  transition: all 0.25s ease;
}
.list-fade-enter-from {
  opacity: 0;
  transform: translateX(15px);
}
.list-fade-leave-to {
  opacity: 0;
  transform: translateX(-15px);
}

.panel-summary {
  padding-top: 10px;
  border-top: 1px dashed #ebeef5;
}

.summary-row {
  display: flex;
  justify-content: space-between;
  font-size: 12px;
  margin-bottom: 4px;
  color: #606266;
}

.summary-val {
  color: #303133;
}

.font-bold {
  font-weight: 600;
}

.text-green {
  color: #009688;
}

/* 弹窗底部操作条 */
.modal-footer {
  display: flex;
  justify-content: space-between;
  align-items: center;
  width: 100%;
}

.footer-left-info {
  font-size: 12px;
  color: #606266;
}

.footer-left-info b {
  color: #009688;
}

.btn-confirm {
  background-color: #009688 !important;
  border-color: #009688 !important;
}
</style>
