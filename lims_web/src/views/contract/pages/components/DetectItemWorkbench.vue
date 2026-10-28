<template>
  <div class="detect-item-workbench">
    <!-- 顶部高效检索工具栏 -->
    <div class="workbench-header">
      <div class="header-left">
        <span class="workbench-title">
          <el-icon class="title-icon"><DataAnalysis /></el-icon>
          配置检测因子与分析方法（行内直选）
        </span>
        <div class="quick-types">
          <span
            v-for="cat in categoryList"
            :key="cat"
            class="cat-chip"
            :class="{ 'is-active': selectedCategory === cat }"
            @click="selectedCategory = cat"
          >
            {{ cat }}
          </span>
        </div>
      </div>
      <div class="header-right">
        <el-input
          v-model="searchKeyword"
          placeholder="搜索因子名称、标准编号、方法..."
          clearable
          size="small"
          class="quick-search-input"
        >
          <template #prefix>
            <el-icon class="search-icon"><Search /></el-icon>
          </template>
        </el-input>
      </div>
    </div>

    <!-- 主体双栏工作区 -->
    <div class="workbench-body">
      <!-- 左侧：待选因子库 (70%) -->
      <div class="library-col">
        <el-table
          :data="filteredItems"
          size="small"
          stripe
          border
          height="340px"
          row-key="id"
          header-cell-class-name="workbench-table-header"
          :row-class-name="getRowClassName"
          @row-click="handleRowClick"
        >
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

          <el-table-column prop="category" label="所属分类" width="120" show-overflow-tooltip>
            <template #default="{ row }">
              <span class="cat-tag">{{ row.category }}</span>
            </template>
          </el-table-column>

          <el-table-column prop="standard_no" label="标准编号" width="140" show-overflow-tooltip>
            <template #default="{ row }">
              <span class="standard-code">{{ row.standard_no }}</span>
            </template>
          </el-table-column>

          <el-table-column prop="method_name" label="检测分析方法" min-width="180" show-overflow-tooltip />

          <el-table-column prop="price" label="基准单价" width="95" align="right">
            <template #default="{ row }">
              <span class="price-text">¥ {{ Number(row.price || 0).toFixed(2) }}</span>
            </template>
          </el-table-column>

          <el-table-column prop="scene_type" label="场景" width="80" align="center">
            <template #default="{ row }">
              <el-tag size="small" type="info" effect="plain">{{ row.scene_type || '实验室' }}</el-tag>
            </template>
          </el-table-column>
        </el-table>

        <div class="library-bottom-bar">
          <span class="tip-text">💡 单击表格任意行即可选中/移出右侧清单</span>
          <div class="batch-btns">
            <el-button size="small" @click="handleSelectAllCurrent">全选当前分类</el-button>
            <el-button size="small" @click="handleDeselectAllCurrent">取消全选</el-button>
          </div>
        </div>
      </div>

      <!-- 右侧：当前方案已选因子卡片流 (30%) -->
      <div class="selected-col">
        <div class="selected-header">
          <div class="selected-title">
            <span>本方案已选因子</span>
            <span class="badge-count">{{ selectedList.length }}</span>
          </div>
          <el-button
            v-if="selectedList.length > 0"
            type="danger"
            link
            size="small"
            @click="selectedList = []"
          >
            清空
          </el-button>
        </div>

        <div v-if="selectedList.length === 0" class="empty-holder">
          <el-empty :image-size="45" description="暂未选择任何检测因子" />
        </div>

        <div v-else class="selected-cards">
          <div
            v-for="(item, idx) in selectedList"
            :key="item.id"
            class="factor-card"
          >
            <span class="card-seq">{{ idx + 1 }}</span>
            <div class="card-info">
              <div class="card-name-row">
                <span class="card-title">{{ item.name }}</span>
                <span class="card-amount">¥{{ Number(item.price || 0).toFixed(2) }}</span>
              </div>
              <div class="card-sub-info">{{ item.standard_no }} · {{ item.method_name }}</div>
            </div>
            <span class="card-del-btn" @click.stop="removeItem(item.id)">
              <el-icon><Close /></el-icon>
            </span>
          </div>
        </div>

        <!-- 底部汇总统计与完成折叠按钮 -->
        <div class="selected-footer">
          <div class="fee-summary">
            <span>因子基准单价合计：</span>
            <span class="total-fee">¥ {{ totalPrice.toFixed(2) }}</span>
          </div>
          <el-button type="primary" size="small" class="btn-done" @click="handleDone">
            保存并收起面板
          </el-button>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, computed, watch } from 'vue'
import { DataAnalysis, Search, Close } from '@element-plus/icons-vue'
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

const props = defineProps<{
  initialFactors?: DetectFactorItem[]
  defaultCategory?: string
}>()

const emit = defineEmits<{
  (e: 'update', factors: DetectFactorItem[]): void
  (e: 'close'): void
}>()

const searchKeyword = ref('')
const selectedCategory = ref(props.defaultCategory || '全部')
const selectedList = ref<DetectFactorItem[]>([])

watch(
  () => props.initialFactors,
  (val) => {
    selectedList.value = val ? [...val] : []
  },
  { immediate: true, deep: true }
)

watch(
  () => props.defaultCategory,
  (val) => {
    if (val) selectedCategory.value = val
  }
)

const categoryList = ref([
  '全部',
  '水和废水',
  '环境空气与废气',
  '土壤与沉积物',
  '噪声与振动',
  '固体废物'
])

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
    name: '土壤重金属总量',
    category: '土壤与沉积物',
    standard_no: 'GB 15618-2018',
    method_name: 'ICP-MS 电感耦合等离子体质谱法',
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

const totalPrice = computed(() => {
  return selectedList.value.reduce((sum, item) => sum + (item.price || 0), 0)
})

function isSelected(id: string) {
  return selectedList.value.some((i) => i.id === id)
}

function getRowClassName({ row }: { row: DetectFactorItem }) {
  return isSelected(row.id) ? 'workbench-row-selected' : ''
}

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
  emit('update', [...selectedList.value])
}

function removeItem(id: string) {
  const index = selectedList.value.findIndex((i) => i.id === id)
  if (index > -1) {
    selectedList.value.splice(index, 1)
    emit('update', [...selectedList.value])
  }
}

function handleSelectAllCurrent() {
  filteredItems.value.forEach((row) => {
    if (!isSelected(row.id)) {
      selectedList.value.push({ ...row })
    }
  })
  emit('update', [...selectedList.value])
}

function handleDeselectAllCurrent() {
  const currentIds = new Set(filteredItems.value.map((i) => i.id))
  selectedList.value = selectedList.value.filter((i) => !currentIds.has(i.id))
  emit('update', [...selectedList.value])
}

function handleDone() {
  emit('update', [...selectedList.value])
  emit('close')
  ElMessage.success(`方案检测因子已更新（共 ${selectedList.value.length} 项）`)
}
</script>

<style scoped>
.detect-item-workbench {
  background-color: #f8fafb;
  border: 1px solid #dcdfe6;
  border-radius: 4px;
  padding: 10px 12px;
  margin: 6px 0;
  box-shadow: inset 0 1px 3px rgba(0, 0, 0, 0.04);
}

.workbench-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 8px;
  padding-bottom: 6px;
  border-bottom: 1px solid #ebeef5;
}

.header-left {
  display: flex;
  align-items: center;
  gap: 12px;
}

.workbench-title {
  font-size: 13px;
  font-weight: 600;
  color: #009688;
  display: flex;
  align-items: center;
  gap: 4px;
}

.title-icon {
  font-size: 15px;
}

.quick-types {
  display: flex;
  gap: 4px;
}

.cat-chip {
  padding: 2px 8px;
  font-size: 11px;
  border-radius: 10px;
  background-color: #eaeff2;
  color: #606266;
  cursor: pointer;
  transition: all 0.2s;
}

.cat-chip:hover {
  background-color: #e0f2f1;
  color: #009688;
}

.cat-chip.is-active {
  background-color: #009688;
  color: #fff;
  font-weight: 500;
}

.quick-search-input {
  width: 260px;
}

/* 主体双栏 */
.workbench-body {
  display: flex;
  gap: 12px;
}

.library-col {
  flex: 7;
  background: #fff;
  border: 1px solid #ebeef5;
  border-radius: 3px;
  padding: 6px;
}

:deep(.workbench-table-header th) {
  background-color: #f2f5f8 !important;
  color: #333333;
  font-weight: 600;
  font-size: 11px;
  height: 32px;
}

:deep(.el-table__row) {
  cursor: pointer;
}

:deep(.workbench-row-selected) {
  background-color: #e8f8f5 !important;
}

.item-name {
  font-size: 12px;
  color: #333;
}

.item-name.is-selected {
  color: #009688;
  font-weight: 600;
}

.cat-tag {
  font-size: 11px;
  color: #909399;
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
  font-size: 12px;
}

.library-bottom-bar {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 6px 4px 2px 4px;
}

.tip-text {
  font-size: 11px;
  color: #909399;
}

/* 右侧已选卡片流 */
.selected-col {
  flex: 3;
  background: #fff;
  border: 1px solid #ebeef5;
  border-radius: 3px;
  padding: 8px 10px;
  display: flex;
  flex-direction: column;
}

.selected-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding-bottom: 6px;
  border-bottom: 1px solid #ebeef5;
}

.selected-title {
  font-size: 12px;
  font-weight: 600;
  color: #303133;
  display: flex;
  align-items: center;
  gap: 6px;
}

.badge-count {
  display: inline-block;
  padding: 0 5px;
  height: 16px;
  line-height: 16px;
  border-radius: 8px;
  background-color: #009688;
  color: #fff;
  font-size: 10px;
}

.empty-holder {
  flex: 1;
  display: flex;
  align-items: center;
  justify-content: center;
}

.selected-cards {
  flex: 1;
  max-height: 280px;
  overflow-y: auto;
  padding: 6px 0;
}

.factor-card {
  display: flex;
  align-items: center;
  padding: 6px 8px;
  margin-bottom: 5px;
  background: #fcfdfe;
  border: 1px solid #eef2f5;
  border-left: 3px solid #009688;
  border-radius: 3px;
}

.card-seq {
  font-size: 11px;
  color: #909399;
  width: 18px;
}

.card-info {
  flex: 1;
  min-width: 0;
}

.card-name-row {
  display: flex;
  justify-content: space-between;
}

.card-title {
  font-size: 11px;
  font-weight: 600;
  color: #333;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.card-amount {
  font-size: 11px;
  font-weight: 600;
  color: #ff9800;
  font-family: Consolas, monospace;
}

.card-sub-info {
  font-size: 10px;
  color: #909399;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.card-del-btn {
  cursor: pointer;
  color: #c0c4cc;
  padding: 2px 4px;
}

.card-del-btn:hover {
  color: #f56c6c;
}

.selected-footer {
  padding-top: 8px;
  border-top: 1px dashed #ebeef5;
}

.fee-summary {
  display: flex;
  justify-content: space-between;
  font-size: 12px;
  margin-bottom: 6px;
}

.total-fee {
  font-family: Consolas, monospace;
  font-weight: 600;
  color: #009688;
}

.btn-done {
  width: 100%;
  background-color: #009688 !important;
  border-color: #009688 !important;
}
</style>
