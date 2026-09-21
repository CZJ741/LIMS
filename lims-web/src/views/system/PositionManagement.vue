<template>
  <div class="position-management-container">
    <el-card shadow="never">
      <template #header>
        <div class="card-header">
          <span>实验室 19 类专业职位身份体系（对齐 ISO/IEC 17025 准则）</span>
        </div>
      </template>
      <el-table :data="positionList" border stripe style="width: 100%">
        <el-table-column prop="id" label="序号" width="70" align="center" />
        <el-table-column prop="posName" label="职位名称" width="180" />
        <el-table-column prop="posCode" label="职位编码" width="220">
          <template #default="{ row }">
            <code>{{ row.posCode }}</code>
          </template>
        </el-table-column>
        <el-table-column prop="remark" label="职位核心职责与资质要求" min-width="260" />
        <el-table-column prop="status" label="状态" width="90" align="center">
          <template #default="{ row }">
            <el-tag :type="row.status === 1 ? 'success' : 'info'">{{ row.status === 1 ? '启用' : '停用' }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column label="默认权限范围" width="180" align="center">
          <template #default="{ row }">
            <el-button link type="primary" size="small" @click="previewDefaultPerms(row)">权限基线预览</el-button>
          </template>
        </el-table-column>
      </el-table>
    </el-card>

    <el-dialog v-model="previewDialogVisible" :title="`[${currentPos?.posName}] 默认权限基线预览`" width="500px">
      <el-descriptions border :column="1">
        <el-descriptions-item label="职位编码">{{ currentPos?.posCode }}</el-descriptions-item>
        <el-descriptions-item label="推荐角色">{{ currentPos?.posName }}基础角色包</el-descriptions-item>
        <el-descriptions-item label="默认数据范围">
          <el-tag type="warning">仅本部门 / 指定项目组</el-tag>
        </el-descriptions-item>
        <el-descriptions-item label="标准合规要求">
          满足 GB/T 27025 第 6.2 条人员能力确认要求
        </el-descriptions-item>
      </el-descriptions>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import request from '@/utils/request'

const positionList = ref<any[]>([])
const previewDialogVisible = ref(false)
const currentPos = ref<any>(null)

async function fetchPositions() {
  try {
    const res: any = await request.get('/system/position/list')
    positionList.value = res || []
  } catch (e) {
    positionList.value = [
      { id: 1, posCode: 'POS_TOP_MANAGEMENT', posName: '管理层', remark: '最高管理层、总经理、分管实验室副总经理', status: 1 },
      { id: 2, posCode: 'POS_QUALITY_MANAGER', posName: '质量负责人', remark: '负责体系运行、内部审核、管理评审、质量监督', status: 1 },
      { id: 3, posCode: 'POS_TECHNICAL_DIRECTOR', posName: '技术负责人', remark: '负责全面技术运作、方法确认、仪器配置与技术攻关', status: 1 },
      { id: 4, posCode: 'POS_MARKET_SPECIALIST', posName: '市场销售专员', remark: '负责客户对接、业务洽谈、合同起草登记', status: 1 },
      { id: 5, posCode: 'POS_FINANCE_OFFICER', posName: '财务人员', remark: '负责合同款项审核、检测收费结算、开票与放行', status: 1 },
      { id: 6, posCode: 'POS_ENTRUST_CLERK', posName: '下单/接样专员', remark: '负责委托受理、客户样品接收、任务派发交接', status: 1 },
      { id: 7, posCode: 'POS_SAMPLE_RECEIVER', posName: '样品库管员', remark: '负责样品入库、贮存、领用、留样期管理与销毁处置', status: 1 },
      { id: 8, posCode: 'POS_SAMPLING_LEADER', posName: '采样组长', remark: '负责制定现场采样排程、人员指派、技术方案交底', status: 1 },
      { id: 9, posCode: 'POS_SAMPLER', posName: '采样员', remark: '负责现场踏勘、采样作业、现场测定与样品封样运输', status: 1 },
      { id: 10, posCode: 'POS_SAMPLING_REVIEWER', posName: '采样质控审核员', remark: '负责采样原始记录核验、点位质控样审查', status: 1 },
      { id: 11, posCode: 'POS_LAB_HEAD', posName: '检测室主任', remark: '负责分析实验任务派工、检测进度监管、室间协调', status: 1 },
      { id: 12, posCode: 'POS_ANALYST', posName: '检测实验员', remark: '负责实验前处理、仪器上机测定、原始数据计算录入', status: 1 },
      { id: 13, posCode: 'POS_LAB_SUPERVISOR', posName: '检测质控员', remark: '负责实验室内质控（空白、平行、加标回收）检查', status: 1 },
      { id: 14, posCode: 'POS_DATA_REVIEWER', posName: '实验数据复审员', remark: '负责原始记录、图谱基线、标准曲线拟合度的二级复核', status: 1 },
      { id: 15, posCode: 'POS_REPORT_WRITER', posName: '报告编制员', remark: '负责汇总检测数据、选择模板生成初版检测报告', status: 1 },
      { id: 16, posCode: 'POS_REPORT_AUDITOR', posName: '报告审核员', remark: '负责报告二级技术审核、标准一致性与数据逻辑校核', status: 1 },
      { id: 17, posCode: 'POS_REPORT_SIGNER', posName: '报告授权签字人', remark: '资质认定/认可批准的签字人，履行报告最终签发批准', status: 1 },
      { id: 18, posCode: 'POS_DEVICE_ADMIN', posName: '设备管理员', remark: '负责仪器设备台账建立、检定校准、日常维护与直连对接', status: 1 },
      { id: 19, posCode: 'POS_STD_ADMIN', posName: '标物/耗材管理员', remark: '负责标准物质购买、验收、期间核查、危化品台账管控', status: 1 }
    ]
  }
}

function previewDefaultPerms(row: any) {
  currentPos.value = row
  previewDialogVisible.value = true
}

onMounted(() => {
  fetchPositions()
})
</script>

<style scoped>
.card-header {
  font-weight: bold;
}
</style>
