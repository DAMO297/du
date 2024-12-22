<template>
  <div class="dashboard">
    <!-- 顶部统计卡片 -->
    <div class="stats-container">
      <div 
        v-for="(card, index) in statsCards" 
        :key="index"
        class="stats-card"
        :class="{ 'loading': card.loading }"
      >
        <div class="card-header">
          <div class="icon">
            <i :class="card.icon"></i>
          </div>
          <h3 class="title">{{ card.title }}</h3>
        </div>
        <div class="card-body">
          <div class="number">
            <span class="currency">¥</span>
            {{ card.number }}
          </div>
          <div 
            class="growth-rate"
            :class="{ 
              'increase': !card.change.startsWith('-'),
              'decrease': card.change.startsWith('-')
            }"
          >
            <i class="arrow-icon"></i>
            <span>{{ card.change }}</span>
            <span class="label">较上月</span>
          </div>
        </div>
      </div>
    </div>

    <!-- 图表容器 -->
    <div class="charts-grid">
      <div class="chart-container">
        <div id="costChart" class="chart"></div>
      </div>
      <div class="chart-container">
        <div id="demo" class="chart"></div>
      </div>
      <div class="chart-container">
        <div id="netProfitChart" class="chart"></div>
      </div>
    </div>
  </div>
</template>
  
<script>
import {
  listGood,
  getGood,
  delGood,
  addGood,
  updateGood,
} from "@/api/merchant/good";
import { listCost } from "@/api/merchant/cost";
import * as echarts from "echarts";

export default {
  name: "ChartComponent",
  data() {
    return {
      statsCards: [
        { 
          icon: "📬", 
          title: "当月邮费",
          number: 0, 
          change: "0%",
          loading: true
        },
        { 
          icon: "💰", 
          title: "当月售卖",
          number: 0, 
          change: "0%",
          loading: true
        },
        { 
          icon: "📈", 
          title: "当月利润",
          number: 0, 
          change: "0%",
          loading: true
        }
      ],
      currentMonthCosts: 0,
      currentMonthSales: 0,
      monthlyData: {}, // 用于图表的月度数据
      rawData: [], // 用于存储原始数据
      monthlyCosts: {}, // 存储按月统计的邮费数据
      monthlyNetProfits: {}, // 存储按月统计的净利润数据
    };
  },
  methods: {
    // 获取当月日期范围
    getCurrentMonthRange() {
      const now = new Date();
      const year = now.getFullYear();
      const month = now.getMonth() + 1; // JavaScript 月份从 0 开始，需要 +1
      
      // 格式化月份，确保是两位数
      const monthStr = month < 10 ? `0${month}` : month;
      
      return {
        start: `${year}-${monthStr}-01`,
        end: `${year}-${monthStr}-31`
      };
    },

    // 获取上月日期范围
    getLastMonthRange() {
      const now = new Date();
      const year = now.getFullYear();
      let month = now.getMonth(); // 上个月
      
      // 处理跨年的情况
      if (month === 0) {
        year = year - 1;
        month = 12;
      }
      
      // 格式化月份，确保是两位数
      const monthStr = month < 10 ? `0${month}` : month;
      
      return {
        start: `${year}-${monthStr}-01`,
        end: `${year}-${monthStr}-31`
      };
    },

    // 计算环比增长率
    calculateGrowthRate(current, previous) {
      if (!previous || previous === 0) return '+100%';
      // 计算环比变化率：(本期数 - 上期数) / 上期数 × 100%
      const rate = ((current - previous) / previous) * 100;
      // 保留一位小数
      const formattedRate = Math.abs(rate).toFixed(1);
      // 如果是减少，返回负数
      return rate >= 0 ? `+${formattedRate}%` : `-${formattedRate}%`;
    },

    // 获取当月邮费
    async fetchCurrentMonthCosts() {
      try {
        const dateRange = this.getCurrentMonthRange();
        const response = await listCost({
          pageNum: 1,
          pageSize: 99999,
          params: {
            startDate: dateRange.start,
            endDate: dateRange.end
          }
        });

        if (response && response.code === 200 && Array.isArray(response.rows)) {
          const currentMonthData = response.rows.filter(item => {
            const itemDate = item.dateTime?.substring(0, 7);
            const currentMonth = dateRange.start.substring(0, 7);
            return itemDate === currentMonth;
          });

          this.currentMonthCosts = currentMonthData.reduce((sum, item) => {
            return sum + (Number(item.costAmount) || 0);
          }, 0);
          
          // 先只更新数值，不更新增长率
          this.updateStatsCard(0, this.currentMonthCosts);
          
          // 获取上月数据并计算增长率
          await this.fetchLastMonthCosts();
        }
      } catch (error) {
        console.error("获取邮费数据失败：", error);
      }
    },

    // 获取上月邮费
    async fetchLastMonthCosts() {
      try {
        const dateRange = this.getLastMonthRange();
        const response = await listCost({
          pageNum: 1,
          pageSize: 99999,
          params: {
            startDate: dateRange.start,
            endDate: dateRange.end
          }
        });

        if (response && response.code === 200 && Array.isArray(response.rows)) {
          const lastMonthData = response.rows.filter(item => {
            const itemDate = item.dateTime?.substring(0, 7);
            const lastMonth = dateRange.start.substring(0, 7);
            return itemDate === lastMonth;
          });

          const lastMonthCosts = lastMonthData.reduce((sum, item) => {
            return sum + (Number(item.costAmount) || 0);
          }, 0);

          // 计算增长率并更新卡片
          const growthRate = this.calculateGrowthRate(this.currentMonthCosts, lastMonthCosts);
          this.updateStatsCardGrowth(0, growthRate);
        }
      } catch (error) {
        console.error("获取上月邮费数据失败：", error);
      }
    },

    // 获取当月销售数据
    async fetchCurrentMonthSales() {
      try {
        const dateRange = this.getCurrentMonthRange();
        const response = await listGood({
          pageNum: 1,
          pageSize: 99999,
          params: {
            startDate: dateRange.start,
            endDate: dateRange.end
          }
        });

        if (response && response.code === 200 && Array.isArray(response.rows)) {
          const currentMonthData = response.rows.filter(item => {
            const itemDate = item.dateTime?.substring(0, 7);
            const currentMonth = dateRange.start.substring(0, 7);
            return itemDate === currentMonth;
          });

          this.currentMonthSales = currentMonthData.reduce((sum, item) => {
            return sum + (Number(item.profit) || 0);
          }, 0);
          
          // 先只更新数值，不更新增长率
          this.updateStatsCard(1, this.currentMonthSales);
          
          // 计算到手利润
          const netProfit = this.currentMonthSales - this.currentMonthCosts;
          this.updateStatsCard(2, netProfit);
          
          // 获取上月数据并计算增长率
          await this.fetchLastMonthSales();
        }
      } catch (error) {
        console.error("获取销售数据失败：", error);
      }
    },

    // 获取上月销售数据
    async fetchLastMonthSales() {
      try {
        const dateRange = this.getLastMonthRange();
        const response = await listGood({
          pageNum: 1,
          pageSize: 99999,
          params: {
            startDate: dateRange.start,
            endDate: dateRange.end
          }
        });

        if (response && response.code === 200 && Array.isArray(response.rows)) {
          const lastMonthData = response.rows.filter(item => {
            const itemDate = item.dateTime?.substring(0, 7);
            const lastMonth = dateRange.start.substring(0, 7);
            return itemDate === lastMonth;
          });

          const lastMonthSales = lastMonthData.reduce((sum, item) => {
            return sum + (Number(item.profit) || 0);
          }, 0);

          // 计算销售额增长率
          const salesGrowthRate = this.calculateGrowthRate(this.currentMonthSales, lastMonthSales);
          this.updateStatsCardGrowth(1, salesGrowthRate);

          // 计算净利润增长率
          const lastMonthProfit = lastMonthSales - this.lastMonthCosts;
          const currentMonthProfit = this.currentMonthSales - this.currentMonthCosts;
          const profitGrowthRate = this.calculateGrowthRate(currentMonthProfit, lastMonthProfit);
          this.updateStatsCardGrowth(2, profitGrowthRate);
        }
      } catch (error) {
        console.error("获取上月销售数据失败：", error);
      }
    },

    // 更新统计卡片数据
    updateStatsCard(index, value) {
      this.statsCards[index].number = value.toFixed(2);
      this.statsCards[index].loading = false;
    },

    // 更新统计卡片的增长率
    updateStatsCardGrowth(index, growthRate) {
      this.statsCards[index].change = growthRate;
    },

    // 原有的图表相关法
    async fetchData() {
      try {
        const response = await listGood({ pageNum: 1, pageSize: 99999 });
        if (response && response.code === 200 && Array.isArray(response.rows)) {
          this.rawData = response.rows;
          this.processData();
          this.drawChart();
          // 获取邮费数据并处理净利润
          await this.processCostData();
          this.processNetProfitData();
        }
      } catch (error) {
        console.error("获取商品数据失败：", error);
      }
    },

    processData() {
      // 保持原有的数据处理逻辑
      this.monthlyData = {};
      if (!this.rawData || !Array.isArray(this.rawData)) return;

      this.rawData.forEach(item => {
        if (item.dateTime && typeof item.dateTime === 'string') {
          const month = item.dateTime.substring(0, 7);
          if (!this.monthlyData[month]) {
            this.monthlyData[month] = 0;
          }
          if (typeof item.profit === 'number') {
            this.monthlyData[month] += item.profit;
          }
        }
      });
    },

    // 创建个通用的图表配置生成函数
    createChartOption(title, data, months, color) {
      return {
        title: {
          text: title,
          textStyle: {
            fontSize: 18,
            fontWeight: 600,
            color: '#1d1d1f'
          },
          top: 10,
          left: 'center'
        },
        tooltip: {
          trigger: 'axis',
          backgroundColor: 'rgba(255, 255, 255, 0.9)',
          borderColor: 'rgba(0, 0, 0, 0.1)',
          borderWidth: 1,
          textStyle: {
            color: '#1d1d1f',
            fontSize: 12
          },
          padding: [8, 12]
        },
        grid: {
          top: '15%',
          left: '3%',
          right: '4%',
          bottom: '3%',
          containLabel: true
        },
        xAxis: {
          type: 'category',
          data: months,
          axisLine: {
            lineStyle: {
              color: '#e0e0e0'
            }
          },
          axisLabel: {
            color: '#666666',
            fontSize: 12
          }
        },
        yAxis: {
          type: 'value',
          axisLine: {
            show: false
          },
          splitLine: {
            lineStyle: {
              color: '#f0f0f0'
            }
          },
          axisLabel: {
            color: '#666666',
            fontSize: 12
          }
        },
        series: [{
          data: data,
          type: 'line',
          smooth: true,
          symbolSize: 8,
          lineStyle: {
            width: 3,
            color: color
          },
          label: {
            show: true,
            position: 'top',
            formatter: '{c}',
            color: '#1d1d1f',
            fontSize: 12,
            fontWeight: 500,
            backgroundColor: 'rgba(255, 255, 255, 0.8)',
            padding: [6, 10],
            borderRadius: 4
          },
          itemStyle: {
            color: color,
            borderWidth: 2,
            borderColor: '#ffffff'
          },
          areaStyle: {
            color: {
              type: 'linear',
              x: 0,
              y: 0,
              x2: 0,
              y2: 1,
              colorStops: [{
                offset: 0,
                color: `${color}33` // 添加透明度
              }, {
                offset: 1,
                color: `${color}05` // 更淡的透明度
              }]
            }
          }
        }]
      };
    },

    // 修改邮费图表绘制方法
    drawCostChart() {
      const chartDom = document.getElementById('costChart');
      if (!chartDom) return;

      const months = Object.keys(this.monthlyCosts).sort();
      const costData = months.map(month => this.monthlyCosts[month].toFixed(2));

      const myChart = echarts.init(chartDom);
      const option = this.createChartOption('每月邮费统计', costData, months, '#FF3B30');
      myChart.setOption(option);
    },

    // 修改利润图表绘制方法
    drawChart() {
      if (!this.monthlyData || Object.keys(this.monthlyData).length === 0) return;

      const months = Object.keys(this.monthlyData);
      const chartData = months.map(month => this.monthlyData[month].toFixed(2));

      let chartDom = document.getElementById('demo');
      if (!chartDom) return;

      let myChart = echarts.init(chartDom);
      const option = this.createChartOption('每月利润统计', chartData, months, '#34C759');
      myChart.setOption(option);
    },

    // 修改净利润图表绘制方法
    drawNetProfitChart() {
      const chartDom = document.getElementById('netProfitChart');
      if (!chartDom) return;

      const months = Object.keys(this.monthlyNetProfits).sort();
      const profitData = months.map(month => this.monthlyNetProfits[month].toFixed(2));

      const myChart = echarts.init(chartDom);
      const option = this.createChartOption('每月到手利润统计', profitData, months, '#007AFF');
      myChart.setOption(option);
    },

    // 处理邮费数据
    async processCostData() {
      try {
        const response = await listCost({ pageNum: 1, pageSize: 99999 });
        if (response && response.code === 200 && Array.isArray(response.rows)) {
          this.monthlyCosts = {};
          response.rows.forEach(item => {
            if (item.dateTime && typeof item.dateTime === 'string') {
              const month = item.dateTime.substring(0, 7);
              if (!this.monthlyCosts[month]) {
                this.monthlyCosts[month] = 0;
              }
              this.monthlyCosts[month] += Number(item.costAmount) || 0;
            }
          });
          this.drawCostChart();
        }
      } catch (error) {
        console.error("获取邮费数据失败：", error);
      }
    },

    // 处理净利润数据
    processNetProfitData() {
      this.monthlyNetProfits = {};
      Object.keys(this.monthlyData).forEach(month => {
        const profit = this.monthlyData[month];
        const cost = this.monthlyCosts[month] || 0;
        this.monthlyNetProfits[month] = profit - cost;
      });
      this.drawNetProfitChart();
    },
  },
  mounted() {
    // 按顺序执行数据获取
    this.fetchCurrentMonthCosts();  // 这个方法会自动调用 fetchLastMonthCosts
    this.fetchCurrentMonthSales();  // 这个方法会自动调用 fetchLastMonthSales
    this.fetchData();  // 图表数据
  }
};
</script>
  
  <style lang="scss" scoped>
  :root {
    /* 亮色主题变量 */
    --bg-primary: #ffffff;
    --bg-secondary: #f5f7fa;
    --text-primary: #1d1d1f;
    --text-secondary: #86868b;
    --accent-blue: #0071e3;
    --accent-green: #34c759;
    --accent-red: #ff3b30;
    --card-shadow: 0 8px 30px rgba(0, 0, 0, 0.08);
    --card-shadow-hover: 0 12px 40px rgba(0, 0, 0, 0.12);
    --card-bg: #ffffff;
    --border-color: rgba(0, 0, 0, 0.1);
  }

  .dark-mode {
    /* 暗色主题变量 */
    --bg-primary: #000000;
    --bg-secondary: #1c1c1e;
    --text-primary: #ffffff;
    --text-secondary: #98989d;
    --card-shadow: 0 8px 30px rgba(0, 0, 0, 0.3);
    --card-shadow-hover: 0 12px 40px rgba(0, 0, 0, 0.4);
    --card-bg: #1c1c1e;
    --border-color: rgba(255, 255, 255, 0.1);
  }

  .dashboard {
    width: 100%;
    min-height: 100vh;
    padding: 32px;
    background-color: var(--bg-primary);
    transition: all 0.3s ease;
  }

  .stats-container {
    display: grid;
    grid-template-columns: repeat(3, 1fr);
    gap: 24px;
    margin-bottom: 40px;
  }

  .stats-card {
    background: var(--card-bg);
    border-radius: 24px;
    padding: 32px;
    box-shadow: var(--card-shadow);
    transition: all 0.4s cubic-bezier(0.4, 0, 0.2, 1);
    position: relative;
    overflow: hidden;
    display: flex;
    flex-direction: column;
    gap: 20px;
    border: 1px solid var(--border-color);
  }

  .card-header {
    display: flex;
    align-items: center;
    gap: 16px;
    margin-bottom: 0;
  }

  .icon {
    width: 52px;
    height: 52px;
    border-radius: 16px;
    background: linear-gradient(135deg, var(--accent-blue), #40a9ff);
    display: flex;
    align-items: center;
    justify-content: center;
    font-size: 24px;
    color: white;
    transition: all 0.3s ease;
    box-shadow: 0 4px 12px rgba(0, 113, 227, 0.2);
  }

  .title {
    font-size: 18px;
    font-weight: 600;
    color: var(--text-primary);
    margin: 0;
    line-height: 1.4;
  }

  .card-body {
    padding-left: 0;
    display: flex;
    flex-direction: column;
    gap: 12px;
  }

  .number {
    font-size: 42px;
    font-weight: 700;
    color: var(--text-primary);
    margin-bottom: 0;
    letter-spacing: -0.03em;
    line-height: 1;
  }

  .currency {
    font-size: 28px;
    margin-right: 8px;
    font-weight: 600;
    color: var(--text-secondary);
  }

  .growth-rate {
    display: inline-flex;
    align-items: center;
    gap: 6px;
    font-size: 15px;
    font-weight: 600;
    padding: 8px 16px;
    border-radius: 12px;
    width: fit-content;
  }

  .growth-rate.increase {
    color: #ff3b30;
    background: rgba(255, 59, 48, 0.1);
  }

  .growth-rate.decrease {
    color: #34c759;
    background: rgba(52, 199, 89, 0.1);
  }

  .arrow-icon {
    width: 0;
    height: 0;
    border-left: 6px solid transparent;
    border-right: 6px solid transparent;
  }

  .increase .arrow-icon {
    border-bottom: 8px solid currentColor;
  }

  .decrease .arrow-icon {
    border-top: 8px solid currentColor;
  }

  .label {
    font-size: 14px;
    font-weight: 500;
    color: var(--text-secondary);
    margin-left: 4px;
  }

  .stats-card:nth-child(1) .icon {
    background: linear-gradient(135deg, #ff3b30, #ff9500);
    box-shadow: 0 4px 12px rgba(255, 59, 48, 0.2);
  }

  .stats-card:nth-child(2) .icon {
    background: linear-gradient(135deg, #34c759, #30b0c7);
    box-shadow: 0 4px 12px rgba(52, 199, 89, 0.2);
  }

  .stats-card:nth-child(3) .icon {
    background: linear-gradient(135deg, #007aff, #5856d6);
    box-shadow: 0 4px 12px rgba(0, 122, 255, 0.2);
  }

  .stats-card:hover {
    transform: translateY(-4px);
    box-shadow: var(--card-shadow-hover);
  }

  .stats-card:hover .icon {
    transform: scale(1.05);
  }

  .stats-card::before {
    content: '';
    position: absolute;
    top: 0;
    left: 0;
    right: 0;
    height: 4px;
    opacity: 0.8;
    transition: opacity 0.3s ease;
  }

  .stats-card:nth-child(1)::before {
    background: linear-gradient(90deg, #ff3b30, #ff9500);
  }

  .stats-card:nth-child(2)::before {
    background: linear-gradient(90deg, #34c759, #30b0c7);
  }

  .stats-card:nth-child(3)::before {
    background: linear-gradient(90deg, #007aff, #5856d6);
  }

  /* 图表容器样式优化 */
  .charts-grid {
    display: grid;
    grid-template-columns: 1fr;
    gap: 32px;
    margin-top: 32px;
  }

  .chart-container {
    background: var(--card-bg);
    border-radius: 24px;
    padding: 32px;
    box-shadow: var(--card-shadow);
    border: 1px solid var(--border-color);
    transition: all 0.4s cubic-bezier(0.4, 0, 0.2, 1);
    position: relative;
    overflow: hidden;
  }

  .chart-container::before {
    content: '';
    position: absolute;
    top: 0;
    left: 0;
    right: 0;
    height: 4px;
    opacity: 0.8;
    transition: opacity 0.3s ease;
  }

  /* 为每个图表设置不同的顶部渐变条 */
  .chart-container:nth-child(1)::before {
    background: linear-gradient(90deg, #ff3b30, #ff9500);
  }

  .chart-container:nth-child(2)::before {
    background: linear-gradient(90deg, #34c759, #30b0c7);
  }

  .chart-container:nth-child(3)::before {
    background: linear-gradient(90deg, #007aff, #5856d6);
  }

  .chart-container:hover {
    transform: translateY(-4px);
    box-shadow: var(--card-shadow-hover);
  }

  .chart {
    width: 100%;
    height: 400px;
    border-radius: 16px;
  }

  /* 美化图表标题 */
  .chart-container .echarts-title {
    font-size: 20px;
    font-weight: 600;
    color: var(--text-primary);
    margin-bottom: 35px;
    letter-spacing: -0.02em;
  }

  /* 应式设计优化 */
  @media (max-width: 1200px) {
    .stats-container {
      grid-template-columns: repeat(2, 1fr);
    }
  }

  @media (max-width: 768px) {
    .dashboard {
      padding: 20px;
    }
    
    .stats-container {
      grid-template-columns: 1fr;
      gap: 20px;
    }
    
    .stats-card {
      padding: 24px;
    }
    
    .number {
      font-size: 28px;
    }
    
    .currency {
      font-size: 20px;
    }
    
    .icon {
      width: 40px;
      height: 40px;
      font-size: 20px;
    }
  }

  /* 加载动画优化 */
  .loading::after {
    content: "";
    position: absolute;
    top: 0;
    left: 0;
    width: 100%;
    height: 100%;
    background: linear-gradient(
      90deg,
      transparent,
      rgba(255, 255, 255, 0.1),
      transparent
    );
    animation: shimmer 1.5s infinite;
  }

  @keyframes shimmer {
    0% {
      transform: translateX(-100%);
    }
    100% {
      transform: translateX(100%);
    }
  }

  .chart-container {
    padding: 2rem;
    background: var(--bg-color);
    border-radius: 16px;
    box-shadow: 0 8px 24px rgba(0,0,0,0.05);
    
    :deep(.echarts) {
      border-radius: 12px;
      padding: 1rem;
      transition: all 0.3s ease;
      
      &:hover {
        transform: scale(1.01);
        box-shadow: 0 12px 36px rgba(0,0,0,0.1);
      }
    }
  }

  .chart-controls {
    display: flex;
    gap: 1rem;
    margin-bottom: 2rem;
    flex-wrap: wrap;
    
    .el-select {
      min-width: 200px;
      
      :deep(.el-input__inner) {
        border-radius: 8px;
      }
    }
  }

  // 自适应主题色变量
  :root {
    --bg-color: #ffffff;
    --text-color: #333333;
    --border-color: #e4e4e4;
  }

  .dark-theme {
    --bg-color: #1a1a1a;
    --text-color: #ffffff;
    --border-color: #333333;
  }
  </style>
  

  