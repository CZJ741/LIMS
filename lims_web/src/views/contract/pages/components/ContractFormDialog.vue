<template>
  <el-dialog
    v-model="visible"
    title="新增合同信息"
    width="92%"
    top="3vh"
    :close-on-click-modal="true"
    :before-close="handleBeforeClose"
    class="contract-form-dialog"
    destroy-on-close
    append-to-body
  >
    <div class="form-container">
      <el-form
        ref="formRef"
        :model="formData"
        :rules="formRules"
        label-position="right"
        label-width="100px"
        size="small"
        scroll-to-error
        :show-message="false"
      >
        <!-- 1. 委托单位信息 -->
        <div class="form-section">
          <div class="section-header">
            <el-icon class="section-icon"><DocumentCopy /></el-icon>
            <span class="section-title">委托单位信息</span>
          </div>
          <div class="section-body">
            <el-row :gutter="16">
              <el-col :span="8">
                <el-form-item label="委托单位" prop="client_company">
                  <el-input
                    v-model="formData.client_company"
                    placeholder="请输入或选择委托单位"
                    clearable
                  >
                    <template #suffix>
                      <el-icon class="pointer-icon"><Search /></el-icon>
                    </template>
                  </el-input>
                </el-form-item>
              </el-col>
              <el-col :span="8">
                <el-form-item label="联系人">
                  <el-input v-model="formData.client_contact" placeholder="请输入联系人" clearable />
                </el-form-item>
              </el-col>
              <el-col :span="8">
                <el-form-item label="联系电话">
                  <el-input v-model="formData.client_phone" placeholder="请输入联系电话" clearable />
                </el-form-item>
              </el-col>
            </el-row>

            <el-row :gutter="16">
              <el-col :span="8">
                <el-form-item label="地址">
                  <el-input v-model="formData.client_address" placeholder="请输入地址" clearable />
                </el-form-item>
              </el-col>
              <el-col :span="8">
                <el-form-item label="邮箱">
                  <el-input v-model="formData.client_email" placeholder="请输入邮箱" clearable />
                </el-form-item>
              </el-col>
              <el-col :span="8">
                <el-form-item label="固定电话">
                  <el-input v-model="formData.client_tel" placeholder="请输入固定电话" clearable />
                </el-form-item>
              </el-col>
            </el-row>

            <el-row :gutter="16">
              <el-col :span="8">
                <el-form-item label="传真">
                  <el-input v-model="formData.client_fax" placeholder="请输入传真" clearable />
                </el-form-item>
              </el-col>
            </el-row>
          </div>
        </div>

        <!-- 2. 受检单位信息 -->
        <div class="form-section">
          <div class="section-header">
            <el-icon class="section-icon"><DocumentCopy /></el-icon>
            <span class="section-title">受检单位信息</span>
            <el-button type="primary" link size="small" class="copy-client-btn" @click="handleCopyClient">
              同委托单位
            </el-button>
          </div>
          <div class="section-body">
            <el-row :gutter="16">
              <el-col :span="8">
                <el-form-item label="受检单位" prop="tested_company">
                  <el-input
                    v-model="formData.tested_company"
                    placeholder="请输入或选择受检单位"
                    clearable
                  >
                    <template #suffix>
                      <el-icon class="pointer-icon"><Search /></el-icon>
                    </template>
                  </el-input>
                </el-form-item>
              </el-col>
              <el-col :span="8">
                <el-form-item label="联系人">
                  <el-input v-model="formData.tested_contact" placeholder="请输入联系人" clearable />
                </el-form-item>
              </el-col>
              <el-col :span="8">
                <el-form-item label="联系电话">
                  <el-input v-model="formData.tested_phone" placeholder="请输入联系电话" clearable />
                </el-form-item>
              </el-col>
            </el-row>

            <el-row :gutter="16">
              <el-col :span="8">
                <el-form-item label="地址">
                  <el-input v-model="formData.tested_address" placeholder="请输入地址" clearable />
                </el-form-item>
              </el-col>
              <el-col :span="8">
                <el-form-item label="邮箱">
                  <el-input v-model="formData.tested_email" placeholder="请输入邮箱" clearable />
                </el-form-item>
              </el-col>
              <el-col :span="8">
                <el-form-item label="固定电话">
                  <el-input v-model="formData.tested_tel" placeholder="请输入固定电话" clearable />
                </el-form-item>
              </el-col>
            </el-row>

            <el-row :gutter="16">
              <el-col :span="8">
                <el-form-item label="传真">
                  <el-input v-model="formData.tested_fax" placeholder="请输入传真" clearable />
                </el-form-item>
              </el-col>
            </el-row>
          </div>
        </div>

        <!-- 3. 合同信息 -->
        <div class="form-section">
          <div class="section-header">
            <el-icon class="section-icon"><Tickets /></el-icon>
            <span class="section-title">合同信息</span>
          </div>
          <div class="section-body">
            <el-row :gutter="16">
              <el-col :span="8">
                <el-form-item label="合同编号">
                  <el-input
                    v-model="formData.contract_no"
                    placeholder="不填系统自动生成"
                    clearable
                  />
                </el-form-item>
              </el-col>
              <el-col :span="8">
                <el-form-item label="合同名称" prop="contract_name">
                  <el-input v-model="formData.contract_name" placeholder="请输入合同名称" clearable />
                </el-form-item>
              </el-col>
              <el-col :span="8">
                <el-form-item label="业务类型" prop="business_type">
                  <el-select v-model="formData.business_type" placeholder="请选择业务类型" class="full-width">
                    <el-option label="环境污染 (HW)" value="HW" />
                    <el-option label="生态环保 (EH)" value="EH" />
                    <el-option label="工业排污 (IP)" value="IP" />
                    <el-option label="土壤勘查 (SE)" value="SE" />
                  </el-select>
                </el-form-item>
              </el-col>
            </el-row>

            <el-row :gutter="16">
              <el-col :span="8">
                <el-form-item label="检测类型">
                  <el-select v-model="formData.detect_type" placeholder="请选择检测类型" class="full-width">
                    <el-option label="委托检测" value="ENTRUST" />
                    <el-option label="送样检测" value="SEND" />
                    <el-option label="比对监测" value="COMPARE" />
                    <el-option label="现场自测" value="SELF" />
                  </el-select>
                </el-form-item>
              </el-col>
              <el-col :span="8">
                <el-form-item label="用章类型">
                  <el-select
                    v-model="formData.seal_types"
                    multiple
                    collapse-tags
                    collapse-tags-tooltip
                    placeholder="请选择用章"
                    class="full-width"
                  >
                    <el-option label="CMA" value="CMA" />
                    <el-option label="CNAS" value="CNAS" />
                    <el-option label="检测专用章" value="TEST_SEAL" />
                    <el-option label="无章" value="NONE" />
                  </el-select>
                </el-form-item>
              </el-col>
              <el-col :span="8">
                <el-form-item label="服务类型" prop="service_type">
                  <el-select v-model="formData.service_type" placeholder="请选择服务类型" class="full-width">
                    <el-option label="标准服务7个工作日" value="标准服务7个工作日" />
                    <el-option label="加急服务3个工作日" value="加急服务3个工作日" />
                    <el-option label="特急服务24小时" value="特急服务24小时" />
                  </el-select>
                </el-form-item>
              </el-col>
            </el-row>

            <el-row :gutter="16">
              <el-col :span="8">
                <el-form-item label="签订日期">
                  <el-date-picker
                    v-model="formData.sign_date"
                    type="date"
                    placeholder="选择签订日期"
                    value-format="YYYY-MM-DD"
                    class="full-width"
                  />
                </el-form-item>
              </el-col>
              <el-col :span="8">
                <el-form-item label="开始日期" prop="start_date">
                  <el-date-picker
                    v-model="formData.start_date"
                    type="date"
                    placeholder="选择开始日期"
                    value-format="YYYY-MM-DD"
                    class="full-width"
                  />
                </el-form-item>
              </el-col>
              <el-col :span="8">
                <el-form-item label="结束日期" prop="end_date">
                  <el-date-picker
                    v-model="formData.end_date"
                    type="date"
                    placeholder="选择结束日期"
                    value-format="YYYY-MM-DD"
                    class="full-width"
                  />
                </el-form-item>
              </el-col>
            </el-row>

            <el-row :gutter="16">
              <el-col :span="8">
                <el-form-item label="来样方式" prop="sample_delivery_method">
                  <el-select v-model="formData.sample_delivery_method" placeholder="请选择" class="full-width">
                    <el-option label="委托采样" value="SAMPLING" />
                    <el-option label="客户送样" value="DELIVERY" />
                    <el-option label="快递送达" value="EXPRESS" />
                  </el-select>
                </el-form-item>
              </el-col>
              <el-col :span="8">
                <el-form-item label="接受分包">
                  <el-select v-model="formData.accept_subcontract" placeholder="请选择" class="full-width">
                    <el-option label="是" :value="true" />
                    <el-option label="否" :value="false" />
                  </el-select>
                </el-form-item>
              </el-col>
              <el-col :span="8">
                <el-form-item label="是否判定">
                  <el-select v-model="formData.is_evaluated" placeholder="请选择" class="full-width">
                    <el-option label="是" :value="true" />
                    <el-option label="否" :value="false" />
                  </el-select>
                </el-form-item>
              </el-col>
            </el-row>

            <el-row :gutter="16">
              <el-col :span="8">
                <el-form-item label="使用非标方法">
                  <el-select v-model="formData.use_non_standard_method" placeholder="请选择" class="full-width">
                    <el-option label="否" :value="false" />
                    <el-option label="是" :value="true" />
                  </el-select>
                </el-form-item>
              </el-col>
              <el-col :span="8">
                <el-form-item label="业务员" prop="salesperson_name">
                  <el-input
                    v-model="formData.salesperson_name"
                    placeholder="请选择或指定业务员"
                    clearable
                  >
                    <template #suffix>
                      <el-icon class="pointer-icon"><Search /></el-icon>
                    </template>
                  </el-input>
                </el-form-item>
              </el-col>
              <el-col :span="8">
                <el-form-item label="余样处置" prop="sample_disposal">
                  <el-select v-model="formData.sample_disposal" placeholder="请选择" class="full-width">
                    <el-option label="检耗" value="CONSUME" />
                    <el-option label="退还客户" value="RETURN" />
                    <el-option label="留样保存期满作废" value="EXPIRE_DISCARD" />
                  </el-select>
                </el-form-item>
              </el-col>
            </el-row>

            <el-row :gutter="16">
              <el-col :span="8">
                <el-form-item label="折扣">
                  <el-input
                    v-model="formData.discount"
                    placeholder="请输入1-10的数字；例如9.85、7.6、8折"
                    clearable
                  />
                </el-form-item>
              </el-col>
              <el-col :span="8">
                <el-form-item label="合同金额">
                  <el-input
                    v-model="formData.contract_amount"
                    placeholder="自动计算"
                    disabled
                  >
                    <template #append>元</template>
                  </el-input>
                </el-form-item>
              </el-col>
            </el-row>

            <el-row :gutter="16">
              <el-col :span="24">
                <el-form-item label="备注">
                  <el-input
                    v-model="formData.remark"
                    type="textarea"
                    :rows="2"
                    placeholder="请输入备注"
                  />
                </el-form-item>
              </el-col>
            </el-row>

            <el-row :gutter="16">
              <el-col :span="24">
                <el-form-item label="附件">
                  <el-upload
                    drag
                    action="#"
                    :auto-upload="false"
                    class="custom-uploader"
                  >
                    <el-icon class="el-icon--upload"><UploadFilled /></el-icon>
                    <div class="el-upload__text">
                      将文件拖到此处，或<em>点击上传</em>
                    </div>
                  </el-upload>
                </el-form-item>
              </el-col>
            </el-row>
          </div>
        </div>

        <!-- 4. 报告要求 -->
        <div class="form-section">
          <div class="section-header">
            <el-icon class="section-icon"><Document /></el-icon>
            <span class="section-title">报告要求</span>
          </div>
          <div class="section-body">
            <el-row :gutter="16">
              <el-col :span="8">
                <el-form-item label="一单一库">
                  <el-select v-model="formData.single_order_db" placeholder="请选择" class="full-width">
                    <el-option label="是" :value="true" />
                    <el-option label="否" :value="false" />
                  </el-select>
                </el-form-item>
              </el-col>
              <el-col :span="8">
                <el-form-item label="报告类型">
                  <el-select v-model="formData.report_type" placeholder="请选择报告类型" class="full-width">
                    <el-option label="常规检测报告" value="NORMAL" />
                    <el-option label="对比监测报告" value="COMPARE" />
                    <el-option label="验收监测报告" value="ACCEPTANCE" />
                  </el-select>
                </el-form-item>
              </el-col>
              <el-col :span="8">
                <el-form-item label="报告要求">
                  <el-input v-model="formData.report_requirement" placeholder="请输入报告要求" clearable />
                </el-form-item>
              </el-col>
            </el-row>

            <el-row :gutter="16">
              <el-col :span="8">
                <el-form-item label="报告数量">
                  <el-input-number
                    v-model="formData.report_copies"
                    :min="1"
                    :max="20"
                    controls-position="right"
                    class="full-width"
                  />
                </el-form-item>
              </el-col>
              <el-col :span="8">
                <el-form-item label="取报告方式">
                  <el-select v-model="formData.report_delivery_method" placeholder="请选择取报告方式" class="full-width">
                    <el-option label="快递" value="EXPRESS" />
                    <el-option label="客户自取" value="SELF_PICK" />
                    <el-option label="电子邮件" value="EMAIL" />
                  </el-select>
                </el-form-item>
              </el-col>
              <el-col :span="8">
                <el-form-item label="取报告单位">
                  <el-input v-model="formData.report_receive_company" placeholder="请输入取报告单位" clearable />
                </el-form-item>
              </el-col>
            </el-row>

            <el-row :gutter="16">
              <el-col :span="8">
                <el-form-item label="取报告地址">
                  <el-input v-model="formData.report_receive_address" placeholder="请输入取报告地址" clearable />
                </el-form-item>
              </el-col>
              <el-col :span="8">
                <el-form-item label="收件人">
                  <el-input v-model="formData.report_receiver" placeholder="请输入收件人" clearable />
                </el-form-item>
              </el-col>
              <el-col :span="8">
                <el-form-item label="收件人电话">
                  <el-input v-model="formData.report_receiver_phone" placeholder="请输入收件人电话" clearable />
                </el-form-item>
              </el-col>
            </el-row>
          </div>
        </div>

        <!-- 5. 付款信息 -->
        <div class="form-section">
          <div class="section-header">
            <el-icon class="section-icon"><Money /></el-icon>
            <span class="section-title">付款信息</span>
          </div>
          <div class="section-body">
            <el-row :gutter="16">
              <el-col :span="8">
                <el-form-item label="发票类型">
                  <el-select v-model="formData.invoice_type" placeholder="请选择发票类型" class="full-width">
                    <el-option label="增值税专用发票" value="SPECIAL" />
                    <el-option label="增值税普通发票" value="NORMAL" />
                    <el-option label="不开票" value="NONE" />
                  </el-select>
                </el-form-item>
              </el-col>
              <el-col :span="8">
                <el-form-item label="发票抬头">
                  <el-input v-model="formData.invoice_title" placeholder="请输入发票抬头" clearable />
                </el-form-item>
              </el-col>
              <el-col :span="8">
                <el-form-item label="纳税人识别号">
                  <el-input v-model="formData.taxpayer_id" placeholder="请输入纳税人识别号" clearable />
                </el-form-item>
              </el-col>
            </el-row>

            <el-row :gutter="16">
              <el-col :span="8">
                <el-form-item label="发票地址">
                  <el-input v-model="formData.invoice_address" placeholder="请输入发票地址" clearable />
                </el-form-item>
              </el-col>
              <el-col :span="8">
                <el-form-item label="发票电话">
                  <el-input v-model="formData.invoice_phone" placeholder="请输入发票电话" clearable />
                </el-form-item>
              </el-col>
              <el-col :span="8">
                <el-form-item label="银行名称">
                  <el-input v-model="formData.bank_name" placeholder="请输入银行名称" clearable />
                </el-form-item>
              </el-col>
            </el-row>

            <el-row :gutter="16">
              <el-col :span="8">
                <el-form-item label="银行账号">
                  <el-input v-model="formData.bank_account" placeholder="请输入银行账号" clearable />
                </el-form-item>
              </el-col>
            </el-row>
          </div>
        </div>

        <!-- 6. 监测方案 (动态表格与项目配置) -->
        <div class="form-section">
          <div class="section-header space-between">
            <div class="header-left">
              <el-icon class="section-icon"><DataAnalysis /></el-icon>
              <span class="section-title">监测方案与检测项目</span>
            </div>
            <div class="header-right">
              <el-button class="tool-btn-small" type="primary" @click="handleAddScheme">
                + 新增监测方案
              </el-button>
              <el-button class="op-btn" @click="handleCopyScheme">
                复制监测方案
              </el-button>
            </div>
          </div>
          <div class="section-body">
            <el-table
              ref="schemeTableRef"
              :data="schemeList"
              border
              size="small"
              header-cell-class-name="custom-table-header"
              class="scheme-table"
              row-key="id"
            >
              <!-- 行内展开插槽：因子配置工作台 (无须弹窗，零闪退) -->
              <el-table-column type="expand" width="1">
                <template #default="{ row }">
                  <div class="expand-workbench-wrap">
                    <DetectItemWorkbench
                      :initial-factors="row.factors"
                      :default-category="getCategoryByMonitorType(row.monitor_type)"
                      @update="(factors) => handleWorkbenchUpdate(row, factors)"
                      @close="() => toggleRowExpand(row)"
                    />
                  </div>
                </template>
              </el-table-column>

              <el-table-column type="index" label="序号" width="50" align="center" />

              <el-table-column label="检测周期" width="105">
                <template #default="{ row }">
                  <el-select v-model="row.detect_period" size="small" class="full-width">
                    <el-option label="单次" value="单次" />
                    <el-option label="第1周期" value="第1周期" />
                    <el-option label="第2周期" value="第2周期" />
                    <el-option label="定期巡测" value="定期巡测" />
                  </el-select>
                </template>
              </el-table-column>

              <el-table-column label="监测类型" width="150">
                <template #default="{ row }">
                  <el-select v-model="row.monitor_type" size="small" class="full-width">
                    <el-option label="水 (含地表水、地下水) 和废水" value="水 (含地表水、地下水) 和废水" />
                    <el-option label="环境空气与废气" value="环境空气与废气" />
                    <el-option label="土壤与沉积物" value="土壤与沉积物" />
                    <el-option label="噪声与振动" value="噪声与振动" />
                  </el-select>
                </template>
              </el-table-column>

              <el-table-column label="点位名称 (分号分隔)" min-width="160">
                <template #default="{ row }">
                  <el-input
                    v-model="row.point_name"
                    size="small"
                    placeholder="输入点位名称，分号分隔"
                    @input="() => handlePointInput(row)"
                  />
                </template>
              </el-table-column>

              <el-table-column label="点位数量" width="75" align="center">
                <template #default="{ row }">
                  <span class="count-tag">{{ row.point_count }}</span>
                </template>
              </el-table-column>

              <el-table-column label="周期(天)" width="80" align="center">
                <template #default="{ row }">
                  <el-input-number
                    v-model="row.period_days"
                    :min="1"
                    :max="365"
                    size="small"
                    controls-position="right"
                    style="width: 65px"
                    @change="() => updateSchemeSubtotal(row)"
                  />
                </template>
              </el-table-column>

              <el-table-column label="频次(次/天)" width="90" align="center">
                <template #default="{ row }">
                  <el-input-number
                    v-model="row.frequency_num"
                    :min="1"
                    :max="24"
                    size="small"
                    controls-position="right"
                    style="width: 75px"
                    @change="() => updateSchemeSubtotal(row)"
                  />
                </template>
              </el-table-column>

              <el-table-column label="已配检测因子" min-width="180">
                <template #default="{ row }">
                  <div class="factor-tags-container">
                    <template v-if="row.factors && row.factors.length > 0">
                      <el-tag
                        v-for="f in row.factors.slice(0, 2)"
                        :key="f.id"
                        size="small"
                        type="success"
                        effect="plain"
                        class="factor-chip"
                      >
                        {{ f.name }}
                      </el-tag>
                      <el-tooltip
                        v-if="row.factors.length > 2"
                        placement="top"
                        effect="dark"
                      >
                        <template #content>
                          <div style="max-width: 260px; line-height: 1.6;">
                            <b>全部已选因子：</b><br />
                            {{ row.factors.map((i: any) => i.name).join('、') }}
                          </div>
                        </template>
                        <el-tag size="small" type="info" class="factor-more-tag">
                          +{{ row.factors.length - 2 }}
                        </el-tag>
                      </el-tooltip>
                    </template>
                    <span v-else class="text-placeholder">未配置检测因子</span>
                  </div>
                </template>
              </el-table-column>

              <el-table-column label="小计(元)" width="105" align="right">
                <template #default="{ row }">
                  <span class="subtotal-text">¥ {{ Number(row.subtotal || 0).toFixed(2) }}</span>
                </template>
              </el-table-column>

              <el-table-column label="操作" width="130" align="center" fixed="right">
                <template #default="{ row, $index }">
                  <div class="op-group" @click.stop>
                    <button class="op-btn op-auth" @click.stop="handleOpenConfigItems(row, $index)">
                      配置项目
                    </button>
                    <button class="op-btn op-del" @click.stop="handleDeleteScheme($index)">
                      删除
                    </button>
                  </div>
                </template>
              </el-table-column>

              <template #empty>
                <div class="empty-tip">
                  <el-icon><InfoFilled /></el-icon>
                  <span>暂无监测方案，请点击「+ 新增监测方案」添加</span>
                </div>
              </template>
            </el-table>
          </div>
        </div>
      </el-form>
    </div>

    <template #footer>
      <div class="dialog-footer">
        <el-button type="primary" class="btn-submit" :loading="submitting" @click="handleSubmit">
          提交
        </el-button>
        <el-button type="primary" class="btn-save" :loading="saving" @click="handleSave">
          保存
        </el-button>
        <el-button @click="visible = false">取消</el-button>
      </div>
    </template>
  </el-dialog>

  <!-- 将子弹窗移出父 el-dialog 外部，保持兄弟层级，彻底避免嵌套遮罩冲突与闪退 -->
  <SelectDetectItemModal
    ref="selectDetectModalRef"
    @confirm="handleConfirmDetectItems"
    @close="isChildModalActive = false"
  />
</template>

<script setup lang="ts">
import { ref, reactive } from 'vue'
import {
  DocumentCopy,
  Tickets,
  Document,
  Money,
  DataAnalysis,
  Search,
  UploadFilled,
  InfoFilled
} from '@element-plus/icons-vue'
import { ElMessage, ElMessageBox, type FormInstance, type FormRules, type TableInstance } from 'element-plus'
import DetectItemWorkbench, { type DetectFactorItem } from './DetectItemWorkbench.vue'

const schemeTableRef = ref<TableInstance>()
const emit = defineEmits(['success'])

const visible = ref(false)
const submitting = ref(false)
const saving = ref(false)
const formRef = ref<FormInstance>()

// 表单字段
const formData = reactive({
  // 1. 委托单位
  client_company: '',
  client_contact: '',
  client_phone: '',
  client_address: '',
  client_email: '',
  client_tel: '',
  client_fax: '',

  // 2. 受检单位
  tested_company: '',
  tested_contact: '',
  tested_phone: '',
  tested_address: '',
  tested_email: '',
  tested_tel: '',
  tested_fax: '',

  // 3. 合同信息
  contract_no: '',
  contract_name: '',
  business_type: 'HW',
  detect_type: 'ENTRUST',
  seal_types: ['CMA'],
  service_type: '标准服务7个工作日',
  sign_date: new Date().toISOString().substring(0, 10),
  start_date: new Date().toISOString().substring(0, 10),
  end_date: new Date(Date.now() + 7 * 86400000).toISOString().substring(0, 10),
  sample_delivery_method: 'SAMPLING',
  accept_subcontract: true,
  is_evaluated: false,
  use_non_standard_method: false,
  salesperson_name: '超级管理员',
  sample_disposal: 'CONSUME',
  discount: '',
  contract_amount: '0.00',
  remark: '',

  // 4. 报告要求
  single_order_db: true,
  report_type: 'NORMAL',
  report_requirement: '',
  report_copies: 1,
  report_delivery_method: 'EXPRESS',
  report_receive_company: '',
  report_receive_address: '',
  report_receiver: '',
  report_receiver_phone: '',

  // 5. 付款信息
  invoice_type: 'NORMAL',
  invoice_title: '',
  taxpayer_id: '',
  invoice_address: '',
  invoice_phone: '',
  bank_name: '',
  bank_account: ''
})

// 字段名称中文映射，用于校验失败时顶部全局汇总警告
const fieldNameMap: Record<string, string> = {
  client_company: '委托单位',
  tested_company: '受检单位',
  contract_name: '合同名称',
  business_type: '业务类型',
  service_type: '服务类型',
  start_date: '开始日期',
  end_date: '结束日期',
  sample_delivery_method: '来样方式',
  salesperson_name: '业务员',
  sample_disposal: '余样处置'
}

const formRules = reactive<FormRules>({
  client_company: [{ required: true, message: '请选择或输入委托单位', trigger: 'blur' }],
  tested_company: [{ required: true, message: '请选择或输入受检单位', trigger: 'blur' }],
  contract_name: [{ required: true, message: '请输入合同名称', trigger: 'blur' }],
  business_type: [{ required: true, message: '请选择业务类型', trigger: 'change' }],
  service_type: [{ required: true, message: '请选择服务类型', trigger: 'change' }],
  start_date: [{ required: true, message: '请选择开始日期', trigger: 'change' }],
  end_date: [{ required: true, message: '请选择结束日期', trigger: 'change' }],
  sample_delivery_method: [{ required: true, message: '请选择来样方式', trigger: 'change' }],
  salesperson_name: [{ required: true, message: '请指定业务员', trigger: 'blur' }],
  sample_disposal: [{ required: true, message: '请选择余样处置方式', trigger: 'change' }]
})

// 监测方案明细
interface SchemeItem {
  detect_period: string
  monitor_type: string
  scheme_name: string
  point_name: string
  point_count: number
  period_days: number
  frequency_num: number
  factors: DetectFactorItem[]
  subtotal: number
}
const schemeList = ref<SchemeItem[]>([])

// 跟踪当前子弹窗是否处于打开状态
const isChildModalActive = ref(false)

function open() {
  visible.value = true
  isChildModalActive.value = false
  // 清空上一次的方案列表与总额，确保表单纯净
  schemeList.value = []
  calculateTotal()
}

function handleCopyClient() {
  formData.tested_company = formData.client_company
  formData.tested_contact = formData.client_contact
  formData.tested_phone = formData.client_phone
  formData.tested_address = formData.client_address
  formData.tested_email = formData.client_email
  formData.tested_tel = formData.client_tel
  formData.tested_fax = formData.client_fax
  ElMessage.success('已复制委托单位信息至受检单位')
}

// 点位分词解析与数量自动更新
function handlePointInput(row: SchemeItem) {
  if (!row.point_name || !row.point_name.trim()) {
    row.point_count = 0
  } else {
    const list = row.point_name
      .split(/[;；,，\n]+/)
      .map((s) => s.trim())
      .filter(Boolean)
    row.point_count = list.length || 1
  }
  updateSchemeSubtotal(row)
}

function updateSchemeSubtotal(row: SchemeItem) {
  const baseFactorSum = (row.factors || []).reduce((sum, f) => sum + (f.price || 0), 0)
  const pts = row.point_count || 1
  const days = row.period_days || 1
  const freq = row.frequency_num || 1
  row.subtotal = baseFactorSum * pts * days * freq
  calculateTotal()
}

function handleAddScheme() {
  const newRow: SchemeItem = {
    detect_period: '单次',
    monitor_type: '水 (含地表水、地下水) 和废水',
    scheme_name: `监测方案-${schemeList.value.length + 1}`,
    point_name: '1#排口',
    point_count: 1,
    period_days: 1,
    frequency_num: 1,
    factors: [],
    subtotal: 0.0
  }
  schemeList.value.push(newRow)
  calculateTotal()
  ElMessage.success('已添加监测方案')
}

function handleCopyScheme() {
  if (schemeList.value.length === 0) {
    ElMessage.warning('暂无方案可复制')
    return
  }
  const last = schemeList.value[schemeList.value.length - 1]
  schemeList.value.push({
    ...last,
    detect_period: `第${schemeList.value.length + 1}周期`,
    factors: [...(last.factors || [])]
  })
  calculateTotal()
  ElMessage.success('已复制监测方案')
}

function handleDeleteScheme(idx: number) {
  schemeList.value.splice(idx, 1)
  calculateTotal()
}

function handleOpenConfigItems(row: SchemeItem, index: number) {
  currentSchemeIndex.value = index
  isChildModalActive.value = true
  let defaultCategory = '水和废水'
  if (row.monitor_type.includes('水')) defaultCategory = '水和废水'
  else if (row.monitor_type.includes('气')) defaultCategory = '环境空气与废气'
  else if (row.monitor_type.includes('土')) defaultCategory = '土壤与沉积物'
  else if (row.monitor_type.includes('声')) defaultCategory = '噪声与振动'

  selectDetectModalRef.value?.open(defaultCategory, row.factors || [])
}

function handleConfirmDetectItems(items: DetectFactorItem[]) {
  isChildModalActive.value = false
  if (currentSchemeIndex.value >= 0 && currentSchemeIndex.value < schemeList.value.length) {
    const row = schemeList.value[currentSchemeIndex.value]
    row.factors = items
    updateSchemeSubtotal(row)
  }
}

function calculateTotal() {
  const sum = schemeList.value.reduce((acc, cur) => acc + (cur.subtotal || 0), 0)
  formData.contract_amount = sum.toFixed(2)
}

function validateAndAlert(onValid: () => void) {
  if (!formRef.value) return
  formRef.value.validate((valid, fields) => {
    if (valid) {
      onValid()
    } else {
      const errFields = fields
        ? Object.keys(fields)
            .map((k) => fieldNameMap[k] || k)
            .filter(Boolean)
        : []
      ElMessage.warning(
        `请完善必填或格式错误信息：【${errFields.join('、')}】未通过校验，已自动定位至首个待办项`
      )
    }
  })
}

function handleSubmit() {
  validateAndAlert(() => {
    submitting.value = true
    setTimeout(() => {
      submitting.value = false
      visible.value = false
      ElMessage.success('合同已登记并成功提交审批流转')
      emit('success')
    }, 600)
  })
}

function handleSave() {
  saving.value = true
  setTimeout(() => {
    saving.value = false
    visible.value = false
    ElMessage.success('合同草稿已保存')
    emit('success')
  }, 400)
}

function handleBeforeClose(done: () => void) {
  // 如果当前正在操作配置项目的子弹窗，严禁关闭主表单！
  if (isChildModalActive.value) {
    return
  }
  ElMessageBox.confirm('当前表单数据尚未保存，确定退出填写吗？', '退出确认', {
    type: 'warning',
    confirmButtonText: '确定退出',
    cancelButtonText: '继续填写'
  }).then(() => {
    done()
  }).catch(() => {})
}

defineExpose({
  open
})
</script>

<style scoped>
:deep(.contract-form-dialog) {
  padding: 0;
  display: flex;
  flex-direction: column;
  max-height: 94vh;
  margin-bottom: 3vh;
  border-radius: 6px;
  overflow: hidden;
}

:deep(.contract-form-dialog .el-dialog__header) {
  margin-right: 0;
  padding: 12px 20px;
  border-bottom: 1px solid #ebeef5;
  color: #009688;
}

:deep(.contract-form-dialog .el-dialog__title) {
  color: #009688;
  font-weight: 600;
  font-size: 15px;
}

:deep(.contract-form-dialog .el-dialog__body) {
  flex: 1;
  overflow-y: auto;
  padding: 16px 24px;
  background-color: #f7f9fa;
}

/* 表单主体区块 */
.form-container {
  max-width: 1300px;
  margin: 0 auto;
}

.form-section {
  background: #fff;
  border: 1px solid #ebeef5;
  border-radius: 4px;
  margin-bottom: 16px;
  box-shadow: 0 1px 3px rgba(0, 0, 0, 0.02);
}

.section-header {
  display: flex;
  align-items: center;
  padding: 10px 14px;
  background-color: #fcfcfc;
  border-bottom: 1px solid #f0f2f5;
  border-left: 3px solid #009688;
  position: relative;
}

.section-header.space-between {
  justify-content: space-between;
}

.header-left {
  display: flex;
  align-items: center;
}

.section-icon {
  color: #009688;
  font-size: 15px;
  margin-right: 6px;
}

.section-title {
  font-size: 13px;
  font-weight: 600;
  color: #009688;
}

.copy-client-btn {
  margin-left: 12px;
  font-size: 12px;
  color: #009688 !important;
}

.section-body {
  padding: 14px 16px 6px 16px;
}

/* 输入控件样式 */
.full-width {
  width: 100% !important;
}

.pointer-icon {
  cursor: pointer;
  color: #909399;
}

.pointer-icon:hover {
  color: #009688;
}

:deep(.el-form-item) {
  margin-bottom: 12px;
}

:deep(.el-form-item__label) {
  font-size: 12px;
  color: #606266;
}

/* 隐藏红字并保留红框高亮 (CLAUDE.md规范) */
:deep(.el-form-item__error) {
  display: none !important;
}

/* 上传控件 */
.custom-uploader {
  width: 100%;
}

:deep(.custom-uploader .el-upload-dragger) {
  padding: 12px;
  border-radius: 4px;
}

:deep(.custom-uploader .el-icon--upload) {
  font-size: 28px;
  margin-bottom: 4px;
  color: #909399;
}

:deep(.custom-uploader .el-upload__text) {
  font-size: 12px;
  color: #606266;
}

:deep(.custom-uploader .el-upload__text em) {
  color: #009688;
}

/* 方案表格 */
.scheme-table {
  width: 100%;
}

:deep(.custom-table-header th) {
  background-color: #f2f5f8 !important;
  color: #333333;
  font-weight: 600;
  font-size: 12px;
  height: 36px;
}

/* 方案表格内部样式 */
.count-tag {
  display: inline-block;
  padding: 1px 6px;
  background-color: #f0f9eb;
  color: #67c23a;
  border: 1px solid #c2e7b0;
  border-radius: 10px;
  font-weight: 600;
  font-size: 11px;
}

.factor-tags-container {
  display: flex;
  align-items: center;
  flex-wrap: nowrap;
  gap: 4px;
}

.factor-chip {
  max-width: 120px;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.factor-more-tag {
  cursor: pointer;
  background-color: #e8f8f5;
  color: #009688;
  border-color: #b2dfdb;
  font-weight: 600;
}

.text-placeholder {
  color: #c0c4cc;
  font-size: 11px;
}

.subtotal-text {
  font-family: Consolas, monospace;
  font-weight: 600;
  color: #ff9800;
  font-size: 12px;
}

.op-group {
  display: inline-flex;
  align-items: center;
  gap: 4px;
  white-space: nowrap;
}

.op-auth {
  color: #009688;
  border-color: #009688;
}

.op-del {
  color: #f56c6c;
  border-color: #f56c6c;
}

.empty-tip {
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 6px;
  padding: 18px 0;
  color: #909399;
  font-size: 12px;
}

/* 按钮组 */
.tool-btn-small {
  background-color: #009688 !important;
  border-color: #009688 !important;
  color: #fff !important;
  border-radius: 3px;
  font-size: 12px;
  padding: 4px 10px;
}

.op-btn {
  padding: 3px 8px;
  font-size: 12px;
  border: 1px solid #dcdfe6;
  border-radius: 3px;
  background-color: #fff;
  cursor: pointer;
}

.dialog-footer {
  display: flex;
  justify-content: center;
  gap: 12px;
  padding: 10px 0;
  border-top: 1px solid #ebeef5;
  background: #fff;
}

.btn-submit {
  background-color: #009688 !important;
  border-color: #009688 !important;
  border-radius: 3px;
  padding: 6px 20px;
}

.btn-save {
  background-color: #13ce66 !important;
  border-color: #13ce66 !important;
  border-radius: 3px;
  padding: 6px 20px;
}
</style>
