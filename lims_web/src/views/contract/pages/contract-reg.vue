<template>
  <div class="contract-reg-container">
    <!-- 顶部紧凑检索面板 (对齐 user-org.vue 规范) -->
    <div class="search-panel">
      <div class="panel-toggle" @click="isSearchCollapsed = !isSearchCollapsed">
        <el-icon :class="{ 'is-rotate': isSearchCollapsed }"><ArrowUp /></el-icon>
      </div>

      <el-collapse-transition>
        <div v-show="!isSearchCollapsed" class="search-body">
          <el-form :inline="true" :model="queryParams" class="search-form" size="small">
            <el-form-item label="合同名称">
              <el-input
                v-model="queryParams.contract_name"
                placeholder="合同名称"
                clearable
                style="width: 130px"
              />
            </el-form-item>

            <el-form-item label="报价编号">
              <el-input
                v-model="queryParams.quotation_no"
                placeholder="报价编号"
                clearable
                style="width: 120px"
              />
            </el-form-item>

            <el-form-item label="归属业务员">
              <el-input
                v-model="queryParams.salesperson"
                placeholder="业务员"
                clearable
                style="width: 110px"
              >
                <template #suffix>
                  <el-icon class="search-input-icon"><Search /></el-icon>
                </template>
              </el-input>
            </el-form-item>

            <el-form-item label="委托企业">
              <el-input
                v-model="queryParams.client_company"
                placeholder="委托企业"
                clearable
                style="width: 140px"
              >
                <template #suffix>
                  <el-icon class="search-input-icon"><Search /></el-icon>
                </template>
              </el-input>
            </el-form-item>

            <el-form-item label="受测企业">
              <el-input
                v-model="queryParams.tested_company"
                placeholder="受测企业"
                clearable
                style="width: 140px"
              >
                <template #suffix>
                  <el-icon class="search-input-icon"><Search /></el-icon>
                </template>
              </el-input>
            </el-form-item>

            <el-form-item label="检测类型">
              <el-select
                v-model="queryParams.detect_type"
                placeholder="全部"
                clearable
                style="width: 105px"
              >
                <el-option label="委托检测" value="ENTRUST" />
                <el-option label="送样检测" value="SEND" />
                <el-option label="比对监测" value="COMPARE" />
                <el-option label="现场自测" value="SELF" />
              </el-select>
            </el-form-item>

            <el-form-item label="状态">
              <el-select
                v-model="queryParams.status"
                placeholder="全部"
                clearable
                style="width: 100px"
              >
                <el-option label="草稿" value="DRAFT" />
                <el-option label="待审批" value="PENDING" />
                <el-option label="合同确认" value="CONFIRMED" />
                <el-option label="已生效" value="ACTIVE" />
                <el-option label="已终止" value="TERMINATED" />
              </el-select>
            </el-form-item>

            <el-form-item label="签订日期">
              <el-date-picker
                v-model="queryParams.sign_date_range"
                type="daterange"
                range-separator="-"
                start-placeholder="开始"
                end-placeholder="结束"
                value-format="YYYY-MM-DD"
                style="width: 200px"
              />
            </el-form-item>

            <el-form-item class="actions">
              <el-button type="primary" class="btn-search" @click="handleSearch">
                查询
              </el-button>
              <el-button class="btn-reset" @click="handleReset">
                重置
              </el-button>
            </el-form-item>
          </el-form>
        </div>
      </el-collapse-transition>
    </div>

    <!-- 表格上方操作条 -->
    <div class="table-toolbar">
      <div class="left-actions">
        <el-button class="tool-btn" type="primary" @click="handleCreate">
          + 新增合同
        </el-button>
        <el-button class="op-btn" :disabled="selectedRows.length === 0" @click="handleBatchExport">
          批量导出
        </el-button>
      </div>
      <div class="right-stats">
        共找到 <span class="stat-count">{{ total }}</span> 份合同记录
      </div>
    </div>

    <!-- 数据表格 -->
    <div class="table-wrapper">
      <el-table
        v-loading="loading"
        :data="tableData"
        border
        stripe
        size="small"
        header-cell-class-name="custom-table-header"
        row-key="id"
        @selection-change="handleSelectionChange"
      >
        <!-- 复选框 -->
        <el-table-column type="selection" width="46" align="center" fixed="left" />

        <!-- 委托企业 -->
        <el-table-column
          prop="client_company"
          label="委托企业"
          min-width="160"
          show-overflow-tooltip
        >
          <template #default="{ row }">
            <span class="company-link" @click="handleViewDetail(row)">{{ row.client_company }}</span>
          </template>
        </el-table-column>

        <!-- 合同名称 -->
        <el-table-column prop="contract_name" label="合同名称" min-width="130" show-overflow-tooltip />

        <!-- 报价编号 -->
        <el-table-column prop="quotation_no" label="报价编号" width="120">
          <template #default="{ row }">
            <span class="quote-link">{{ row.quotation_no }}</span>
          </template>
        </el-table-column>

        <!-- 状态 -->
        <el-table-column prop="status_display" label="状态" width="95" align="center">
          <template #default="{ row }">
            <span class="status-tag" :class="`status-${row.status.toLowerCase()}`">
              {{ row.status_display }}
            </span>
          </template>
        </el-table-column>

        <!-- 签订日期 -->
        <el-table-column prop="sign_date" label="签订日期" width="105" align="center" />

        <!-- 服务类型 -->
        <el-table-column prop="service_type" label="服务类型" width="135" align="center">
          <template #default="{ row }">
            <span class="chip-tag service-tag">{{ row.service_type }}</span>
          </template>
        </el-table-column>

        <!-- 检测类型 -->
        <el-table-column prop="detect_type" label="检测类型" width="95" align="center">
          <template #default="{ row }">
            <span class="chip-tag detect-tag">{{ row.detect_type }}</span>
          </template>
        </el-table-column>

        <!-- 归属业务员 -->
        <el-table-column prop="salesperson_name" label="归属业务员" width="105" align="center" />

        <!-- 总价 (不含税) -->
        <el-table-column prop="total_amount_excl_tax" label="总价 (不含税)" width="115" align="right">
          <template #default="{ row }">
            <span class="amount-text">{{ formatMoney(row.total_amount_excl_tax) }}</span>
          </template>
        </el-table-column>

        <!-- 预设审批人 -->
        <el-table-column prop="preset_approver_name" label="预设审批人" width="105" align="center">
          <template #default="{ row }">
            <span>{{ row.preset_approver_name || '-' }}</span>
          </template>
        </el-table-column>

        <!-- 开始日期 -->
        <el-table-column prop="start_date" label="开始日期" width="105" align="center" />

        <!-- 结束日期 -->
        <el-table-column prop="end_date" label="结束日期" width="105" align="center" />

        <!-- 受测企业 -->
        <el-table-column
          prop="tested_company"
          label="受测企业"
          min-width="180"
          show-overflow-tooltip
        />

        <!-- 下单客服 -->
        <el-table-column prop="order_cs_name" label="下单客服" width="105" align="center" />

        <!-- 操作 -->
        <el-table-column label="操作" width="160" align="center" fixed="right">
          <template #default="{ row }">
            <div class="op-group">
              <button class="op-btn op-view" @click="handleViewDetail(row)">查看</button>
              <button class="op-btn op-auth" @click="handleEdit(row)">编辑</button>
              <button class="op-btn op-del" @click="handleDelete(row)">作废</button>
            </div>
          </template>
        </el-table-column>
      </el-table>

      <!-- 分页组件 -->
      <div class="pagination-bar">
        <el-pagination
          v-model:current-page="pagination.page"
          v-model:page-size="pagination.pageSize"
          :page-sizes="[10, 20, 50, 100]"
          layout="total, sizes, prev, pager, next, jumper"
          :total="total"
          @size-change="handleSearch"
          @current-change="handleSearch"
        />
      </div>
    </div>

    <!-- 新增合同表单全屏弹窗 -->
    <ContractFormDialog ref="formDialogRef" @success="handleSearch" />
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted } from 'vue'
import { ArrowUp, Search } from '@element-plus/icons-vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import ContractFormDialog from './components/ContractFormDialog.vue'

const formDialogRef = ref<InstanceType<typeof ContractFormDialog>>()

interface ContractItem {
  id: string
  client_company: string
  contract_name: string
  quotation_no: string
  status: string
  status_display: string
  sign_date: string
  service_type: string
  detect_type: string
  salesperson_name: string
  total_amount_excl_tax: number
  preset_approver_name: string
  start_date: string
  end_date: string
  tested_company: string
  order_cs_name: string
}

// 展开/折叠搜索栏
const isSearchCollapsed = ref(false)
const loading = ref(false)
const selectedRows = ref<ContractItem[]>([])
const total = ref(0)

const queryParams = reactive({
  contract_name: '',
  quotation_no: '',
  salesperson: '',
  sign_date_range: [] as string[],
  client_company: '',
  tested_company: '',
  detect_type: '',
  status: ''
})

const pagination = reactive({
  page: 1,
  pageSize: 20
})

// 真实合同数据列表（默认初始为空，通过新增或查询接口载入）
const tableData = ref<ContractItem[]>([])

function formatMoney(val: number) {
  if (val === null || val === undefined) return '0.00'
  return val.toFixed(2)
}

function handleSearch() {
  loading.value = true
  setTimeout(() => {
    loading.value = false
    ElMessage.success('数据已更新')
  }, 200)
}

function handleReset() {
  queryParams.contract_name = ''
  queryParams.quotation_no = ''
  queryParams.salesperson = ''
  queryParams.sign_date_range = []
  queryParams.client_company = ''
  queryParams.tested_company = ''
  queryParams.detect_type = ''
  queryParams.status = ''
  handleSearch()
}

function handleSelectionChange(rows: ContractItem[]) {
  selectedRows.value = rows
}

function handleCreate() {
  formDialogRef.value?.open()
}

function handleBatchExport() {
  ElMessage.success(`已导出所选的 ${selectedRows.value.length} 条合同数据`)
}

function handleViewDetail(row: ContractItem) {
  ElMessage.info(`查看合同详情：${row.contract_name}`)
}

function handleEdit(row: ContractItem) {
  ElMessage.info(`编辑合同：${row.contract_name}`)
}

function handleDelete(row: ContractItem) {
  ElMessageBox.confirm(
    `按照 CMA/CNAS 合规要求，合同记录不可物理删除。确定将合同【${row.contract_name}】置为作废/终止状态吗？`,
    '合规作废提示',
    {
      confirmButtonText: '确定作废',
      cancelButtonText: '取消',
      type: 'warning'
    }
  ).then(() => {
    ElMessage.success('合同已安全归档作废')
  })
}

onMounted(() => {
  total.value = tableData.value.length
})
</script>

<style scoped>
.contract-reg-container {
  background: #fff;
  padding: 12px 16px;
  min-height: calc(100vh - 90px);
  box-sizing: border-box;
}

/* 顶部紧凑检索面板 (严格对齐 user-org.vue) */
.search-panel {
  position: relative;
  background: #fafafa;
  border: 1px solid #ebeef5;
  padding: 8px 12px 2px 12px;
  margin-bottom: 12px;
}

.panel-toggle {
  position: absolute;
  top: 6px;
  left: 6px;
  color: #009688;
  font-size: 13px;
  cursor: pointer;
  transition: transform 0.2s ease;
  z-index: 1;
}

.panel-toggle .is-rotate {
  transform: rotate(180deg);
}

.search-body {
  padding-left: 12px;
}

.search-form {
  display: flex;
  flex-wrap: wrap;
  align-items: center;
}

:deep(.search-form .el-form-item) {
  margin-bottom: 6px;
  margin-right: 10px;
}

:deep(.search-form .el-form-item__label) {
  font-size: 12px;
  color: #333;
  padding-right: 6px;
  font-weight: 500;
  line-height: 28px;
}

:deep(.search-form .el-input__wrapper),
:deep(.search-form .el-select__wrapper) {
  border-radius: 3px;
  box-shadow: 0 0 0 1px #dcdfe6 inset;
}

.search-input-icon {
  color: #909399;
  font-size: 12px;
}

.actions {
  margin-left: 4px;
}

.btn-search {
  background-color: #009688 !important;
  border-color: #009688 !important;
  color: #fff !important;
  border-radius: 3px;
  padding: 5px 14px;
  font-size: 12px;
}

.btn-reset {
  background-color: #fff;
  border: 1px solid #dcdfe6;
  border-radius: 3px;
  padding: 5px 14px;
  font-size: 12px;
}

/* 操作条 */
.table-toolbar {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 8px;
}

.left-actions {
  display: flex;
  gap: 8px;
}

.tool-btn {
  background-color: #009688 !important;
  border-color: #009688 !important;
  color: #fff;
  border-radius: 3px;
  font-size: 12px;
  padding: 5px 12px;
}

.right-stats {
  font-size: 12px;
  color: #606266;
}

.stat-count {
  color: #009688;
  font-weight: 600;
}

/* 表格定制 */
.table-wrapper {
  background: #fff;
}

:deep(.custom-table-header th) {
  background-color: #f2f5f8 !important;
  color: #333333;
  font-weight: 600;
  font-size: 12px;
  height: 36px;
  border-color: #e4e7ed;
}

.company-link {
  color: #333;
  cursor: pointer;
  text-decoration: underline;
  text-decoration-color: #909399;
  text-underline-offset: 3px;
}

.company-link:hover {
  color: #009688;
  text-decoration-color: #009688;
}

.quote-link {
  color: #009688;
  font-weight: 500;
  cursor: pointer;
}

.status-tag {
  display: inline-block;
  padding: 1px 8px;
  border-radius: 3px;
  font-size: 11px;
  line-height: 18px;
}

.status-confirmed {
  background-color: #e8f8f5;
  color: #009688;
  border: 1px solid #b2dfdb;
}

.status-pending {
  background-color: #fdf6ec;
  color: #e6a23c;
  border: 1px solid #f5dab1;
}

.chip-tag {
  display: inline-block;
  padding: 1px 6px;
  border-radius: 3px;
  font-size: 11px;
}

.service-tag {
  background: #f0f9eb;
  color: #67c23a;
  border: 1px solid #c2e7b0;
}

.detect-tag {
  background: #f4f4f5;
  color: #606266;
  border: 1px solid #dcdfe6;
}

.amount-text {
  font-family: Consolas, monospace;
  font-weight: 600;
  color: #303133;
}

/* 行内操作按钮组 */
.op-group {
  display: inline-flex;
  align-items: center;
  flex-wrap: nowrap;
  gap: 4px;
  white-space: nowrap;
}

.op-btn {
  padding: 2px 6px;
  font-size: 12px;
  border: 1px solid #dcdfe6;
  border-radius: 3px;
  background-color: #fff;
  cursor: pointer;
  line-height: 1.2;
}

.op-view {
  color: #009688;
  border-color: #009688;
}

.op-auth {
  color: #409eff;
  border-color: #409eff;
}

.op-del {
  color: #f56c6c;
  border-color: #f56c6c;
}

.pagination-bar {
  display: flex;
  justify-content: flex-end;
  margin-top: 10px;
}
</style>
