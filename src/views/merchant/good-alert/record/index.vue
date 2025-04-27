<template>
  <div class="good-alert-record">
    <!-- 搜索栏 -->
    <el-card class="search-card">
      <el-form :inline="true" :model="queryParams">
        <el-form-item label="商品名称">
          <el-input v-model="queryParams.goodName" placeholder="请输入商品名称" clearable></el-input>
        </el-form-item>
        <el-form-item label="预警类型">
          <el-select v-model="queryParams.alertType" placeholder="请选择预警类型" clearable>
            <el-option label="价格波动" value="PRICE"></el-option>
            <el-option label="利润率" value="PROFIT"></el-option>
            <el-option label="销售异常" value="SALES"></el-option>
          </el-select>
        </el-form-item>
        <el-form-item label="状态">
          <el-select v-model="queryParams.status" placeholder="请选择状态" clearable>
            <el-option label="未处理" value="0"></el-option>
            <el-option label="已处理" value="1"></el-option>
          </el-select>
        </el-form-item>
        <el-form-item label="预警时间">
          <el-date-picker
            v-model="queryParams.timeRange"
            type="daterange"
            range-separator="至"
            start-placeholder="开始日期"
            end-placeholder="结束日期"
            value-format="yyyy-MM-dd">
          </el-date-picker>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="handleQuery">搜索</el-button>
          <el-button @click="resetQuery">重置</el-button>
        </el-form-item>
      </el-form>
    </el-card>

    <!-- 操作按钮 -->
    <el-card class="toolbar-card">
      <el-button type="success" @click="handleExport">导出</el-button>
    </el-card>

    <!-- 预警记录表格 -->
    <el-card>
      <el-table :data="recordList" border stripe>
        <el-table-column label="商品名称" prop="goodName"></el-table-column>
        <el-table-column label="预警类型">
          <template slot-scope="scope">
            <el-tag :type="getAlertTypeTag(scope.row.alertType)">
              {{ getAlertTypeLabel(scope.row.alertType) }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="预警内容" prop="alertContent"></el-table-column>
        <el-table-column label="预警值">
          <template slot-scope="scope">
            {{ formatAlertValue(scope.row) }}
          </template>
        </el-table-column>
        <el-table-column label="阈值">
          <template slot-scope="scope">
            {{ formatThreshold(scope.row) }}
          </template>
        </el-table-column>
        <el-table-column label="预警时间" prop="alertTime" width="160"></el-table-column>
        <el-table-column label="状态">
          <template slot-scope="scope">
            <el-tag :type="scope.row.status === '0' ? 'danger' : 'success'">
              {{ scope.row.status === '0' ? '未处理' : '已处理' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="200" fixed="right">
          <template slot-scope="scope">
            <el-button
              v-if="scope.row.status === '0'"
              type="primary"
              size="mini"
              @click="handleProcess(scope.row)">
              处理
            </el-button>
            <el-button type="info" size="mini" @click="handleDetail(scope.row)">
              详情
            </el-button>
          </template>
        </el-table-column>
      </el-table>

      <!-- 分页 -->
      <el-pagination
        @size-change="handleSizeChange"
        @current-change="handleCurrentChange"
        :current-page="queryParams.pageNum"
        :page-sizes="[10, 20, 50, 100]"
        :page-size="queryParams.pageSize"
        :total="total"
        layout="total, sizes, prev, pager, next, jumper">
      </el-pagination>
    </el-card>

    <!-- 处理弹窗 -->
    <el-dialog
      title="处理预警"
      :visible.sync="processDialogVisible"
      width="500px">
      <el-form :model="processForm" label-width="100px">
        <el-form-item label="处理备注">
          <el-input
            v-model="processForm.handleRemark"
            type="textarea"
            rows="3"
            placeholder="请输入处理备注">
          </el-input>
        </el-form-item>
      </el-form>
      <div slot="footer" class="dialog-footer">
        <el-button @click="processDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="handleSubmitProcess">确定</el-button>
      </div>
    </el-dialog>
  </div>
</template>

<script>
export default {
  name: 'GoodAlertRecord',
  data() {
    return {
      // 查询参数
      queryParams: {
        goodName: undefined,
        alertType: undefined,
        status: undefined,
        timeRange: [],
        pageNum: 1,
        pageSize: 10
      },
      // 预警记录列表
      recordList: [],
      total: 0,
      // 处理预警
      processDialogVisible: false,
      processForm: {
        id: undefined,
        handleRemark: undefined
      }
    }
  },
  created() {
    this.getList()
  },
  methods: {
    // 获取预警记录列表
    async getList() {
      const res = await this.$api.good.alert.getRecordList(this.queryParams)
      this.recordList = res.rows
      this.total = res.total
    },
    // 搜索
    handleQuery() {
      this.queryParams.pageNum = 1
      this.getList()
    },
    // 重置
    resetQuery() {
      this.queryParams.goodName = undefined
      this.queryParams.alertType = undefined
      this.queryParams.status = undefined
      this.queryParams.timeRange = []
      this.handleQuery()
    },
    // 处理按钮
    handleProcess(row) {
      this.processDialogVisible = true
      this.processForm.id = row.id
      this.processForm.handleRemark = undefined
    },
    // 提交处理
    async handleSubmitProcess() {
      await this.$api.good.alert.handleRecord(this.processForm.id, 'admin', this.processForm.handleRemark)
      this.$message.success('处理成功')
      this.processDialogVisible = false
      this.getList()
    },
    // 预警类型标签
    getAlertTypeTag(type) {
      const tags = {
        PRICE: 'warning',
        PROFIT: 'danger',
        SALES: 'info'
      }
      return tags[type] || 'info'
    },
    // 预警类型标签文本
    getAlertTypeLabel(type) {
      const labels = {
        PRICE: '价格波动',
        PROFIT: '利润率',
        SALES: '销售异常'
      }
      return labels[type] || type
    },
    // 格式化预警值
    formatAlertValue(row) {
      if (row.alertType === 'PRICE' || row.alertType === 'PROFIT') {
        return `${row.alertValue}%`
      } else if (row.alertType === 'SALES') {
        return `${row.alertValue}天`
      }
      return row.alertValue
    },
    // 格式化阈值
    formatThreshold(row) {
      if (row.alertType === 'PRICE' || row.alertType === 'PROFIT') {
        return `${row.threshold}%`
      } else if (row.alertType === 'SALES') {
        return `${row.threshold}天`
      }
      return row.threshold
    },
    // 分页大小改变
    handleSizeChange(val) {
      this.queryParams.pageSize = val
      this.getList()
    },
    // 当前页改变
    handleCurrentChange(val) {
      this.queryParams.pageNum = val
      this.getList()
    }
  }
}
</script>

<style scoped>
.good-alert-record {
  padding: 20px;
}
.search-card {
  margin-bottom: 20px;
}
.toolbar-card {
  margin-bottom: 20px;
}
</style>