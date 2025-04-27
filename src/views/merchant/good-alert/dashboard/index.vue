<template>
    <div class="good-alert-dashboard">
      <!-- 统计卡片 -->
      <el-row :gutter="20">
        <el-col :span="6">
          <el-card shadow="hover">
            <div slot="header" class="card-header">
              <span>待处理预警</span>
              <el-tag type="danger">{{ stats.pendingCount }}</el-tag>
            </div>
            <div class="card-content">
              <el-progress
                type="dashboard"
                :percentage="stats.pendingPercentage"
                :color="colors.danger">
              </el-progress>
            </div>
          </el-card>
        </el-col>
        <el-col :span="6" v-for="type in alertTypes" :key="type.value">
          <el-card shadow="hover">
            <div slot="header" class="card-header">
              <span>{{ type.label }}预警</span>
              <el-tag :type="type.tagType">{{ stats[type.value + 'Count'] }}</el-tag>
            </div>
            <div class="card-content">
              <el-progress
                type="dashboard"
                :percentage="stats[type.value + 'Percentage']"
                :color="colors[type.tagType]">
              </el-progress>
            </div>
          </el-card>
        </el-col>
      </el-row>
  
      <!-- 趋势图 -->
      <el-row :gutter="20" class="chart-row">
        <el-col :span="12">
          <el-card shadow="hover">
            <div slot="header" class="card-header">
              <span>预警趋势</span>
              <el-radio-group v-model="trendTimeRange" size="small">
                <el-radio-button label="week">本周</el-radio-button>
                <el-radio-button label="month">本月</el-radio-button>
              </el-radio-group>
            </div>
            <div class="chart-container">
              <v-chart :option="trendChartOption" autoresize></v-chart>
            </div>
          </el-card>
        </el-col>
        <el-col :span="12">
          <el-card shadow="hover">
            <div slot="header" class="card-header">
              <span>预警类型分布</span>
            </div>
            <div class="chart-container">
              <v-chart :option="typeChartOption" autoresize></v-chart>
            </div>
          </el-card>
        </el-col>
      </el-row>
  
      <!-- 最新预警列表 -->
      <el-card shadow="hover" class="latest-alerts">
        <div slot="header" class="card-header">
          <span>最新预警</span>
          <el-button type="primary" @click="goToRecordPage">查看全部</el-button>
        </div>
        <el-table :data="latestAlerts" border stripe>
          <el-table-column label="商品名称" prop="goodName"></el-table-column>
          <el-table-column label="预警类型">
            <template slot-scope="scope">
              <el-tag :type="getAlertTypeTag(scope.row.alertType)">
                {{ getAlertTypeLabel(scope.row.alertType) }}
              </el-tag>
            </template>
          </el-table-column>
          <el-table-column label="预警内容" prop="alertContent"></el-table-column>
          <el-table-column label="预警时间" prop="alertTime" width="160"></el-table-column>
          <el-table-column label="操作" width="100">
            <template slot-scope="scope">
              <el-button
                v-if="scope.row.status === '0'"
                type="primary"
                size="mini"
                @click="handleProcess(scope.row)">
                处理
              </el-button>
            </template>
          </el-table-column>
        </el-table>
      </el-card>
  
      <!-- 手动检查按钮 -->
      <div class="check-button">
        <el-button type="primary" @click="handleCheck">手动检查预警</el-button>
      </div>
    </div>
  </template>
  
  <script>
  export default {
    name: 'GoodAlertDashboard',
    data() {
      return {
        // 统计数据
        stats: {
          pendingCount: 0,
          pendingPercentage: 0,
          priceCount: 0,
          pricePercentage: 0,
          profitCount: 0,
          profitPercentage: 0,
          salesCount: 0,
          salesPercentage: 0
        },
        // 预警类型
        alertTypes: [
          { value: 'price', label: '价格波动', tagType: 'warning' },
          { value: 'profit', label: '利润率', tagType: 'danger' },
          { value: 'sales', label: '销售异常', tagType: 'info' }
        ],
        // 时间范围
        trendTimeRange: 'week',
        // 颜色配置
        colors: {
          warning: '#E6A23C',
          danger: '#F56C6C',
          info: '#909399',
          success: '#67C23A'
        },
        // 图表配置
        trendChartOption: {},
        typeChartOption: {},
        // 最新预警列表
        latestAlerts: []
      }
    },
    created() {
      this.getStats()
    },
    methods: {
      // 获取统计数据
      async getStats() {
        const res = await this.$api.good.alert.getRecordList({
          pageSize: 1000
        })
        
        const records = res.rows
        const total = records.length
        
        // 计算各类预警数量
        this.stats.pendingCount = records.filter(r => r.status === '0').length
        this.stats.priceCount = records.filter(r => r.alertType === 'PRICE').length
        this.stats.profitCount = records.filter(r => r.alertType === 'PROFIT').length
        this.stats.salesCount = records.filter(r => r.alertType === 'SALES').length
        
        // 计算百分比
        this.stats.pendingPercentage = total > 0 ? (this.stats.pendingCount / total * 100) : 0
        this.stats.pricePercentage = total > 0 ? (this.stats.priceCount / total * 100) : 0
        this.stats.profitPercentage = total > 0 ? (this.stats.profitCount / total * 100) : 0
        this.stats.salesPercentage = total > 0 ? (this.stats.salesCount / total * 100) : 0
  
        // 获取最新预警
        this.latestAlerts = records.slice(0, 5)
      },
      // 手动触发预警检查
      async handleCheck() {
        await this.$api.good.alert.checkAlerts()
        this.$message.success('预警检查完成')
        this.getStats()
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
      // 跳转到预警记录页面
      goToRecordPage() {
        this.$router.push('/good/alert/record')
      }
    }
  }
  </script>
  
  <style scoped>
  .good-alert-dashboard {
    padding: 20px;
  }
  
  .card-header {
    display: flex;
    justify-content: space-between;
    align-items: center;
  }
  
  .card-content {
    display: flex;
    justify-content: center;
    align-items: center;
    height: 200px;
  }
  
  .chart-row {
    margin-top: 20px;
  }
  
  .chart-container {
    height: 300px;
  }
  
  .latest-alerts {
    margin-top: 20px;
  }
  
  .check-button {
    margin-top: 20px;
    text-align: center;
  }
  </style>