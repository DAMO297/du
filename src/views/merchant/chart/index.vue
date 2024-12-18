<template>
    <div class="dashboard">
    <!-- 顶部统计卡片 -->
      <div class="stats-container">
        <div class="stats-card" v-for="(card, index) in statsCards" :key="index">
          <div class="icon-placeholder">{{card.icon}}</div>
          <div class="stats-number">{{card.number}}</div>
          <div 
          class="stats-change"
          :style="{ color: card.change.startsWith('-') ? 'red' : 'green' }"
          >
          {{card.change}}</div>
        </div>
      </div>

    <!-- ECharts 容器 -->
       <div id="demo" style="width: 100%; height: 400px;"></div> 
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
import * as echarts from "echarts";
export default {
name: "ChartComponent",
data() {
      return {
        statsCards: [
        { icon: "📈", number: 120, change: "+12%" },
        { icon: "📊", number: 85, change: "-8%" },
        { icon: "💰", number: 300, change: "+25%" },
        { icon: "🛒", number: 150, change: "+5%" }
      ],
        chartData: [], // 按月统计的利润数据
      };
    },
mounted() {
   this.fetchData();
  },
methods: {
// 调用接口获取商品数据
async fetchData() {
  try {
    const response = await listGood({ pageNum: 1, pageSize: 99999 });
    console.log("接口返回数据：", response);
    if (response && response.code === 200 && Array.isArray(response.rows)) {
      this.rawData = response.rows; // 确保数据是数组格式
      console.log("已保存 rawData：", this.rawData); // 确认 rawData
      this.processData(); // 处理数据
      this.drawChart(); // 调用改写后的 drawChart 方法
    } else {
      console.error("接口返回的数据格式错误，rows 不为数组或 code 不为 200");
    }
  } catch (error) {
    console.error("获取商品数据失败：", error);
  }
},
// 处理数据：按月统计利润
processData() {
  this.monthlyData = {}; // 存储按月统计的数据

  if (!this.rawData || !Array.isArray(this.rawData) || this.rawData.length === 0) {
    console.error("rawData 数据无效或格式错误", this.rawData);
    return;
  }

  this.rawData.forEach(item => {
    if (item.dateTime && typeof item.dateTime === 'string') {
      const month = item.dateTime.substring(0, 7); // 获取年月，如 "2024-10"
      if (!this.monthlyData[month]) {
        this.monthlyData[month] = 0;
      }

      if (typeof item.profit === 'number') {
        this.monthlyData[month] += item.profit;
      } else {
        console.warn(`无效的 profit 数据：`, item.profit);
      }
    } else {
      console.warn(`无效的 dateTime 数据：`, item.dateTime);
    }
  });

  console.log("按月统计的利润：", this.monthlyData);
},
// 初始化 ECharts 图表
drawChart() {
  if (!this.monthlyData || typeof this.monthlyData !== 'object' || Object.keys(this.monthlyData).length === 0) {
    console.error("monthlyData 无效或为空", this.monthlyData);
    return;
  }

  // 获取月份数据
  const months = Object.keys(this.monthlyData);

  if (months.length === 0) {
    console.warn("没有按月统计的数据！");
    return;
  }

  // 图表数据
  const chartData = months.map(month => this.monthlyData[month].toFixed(2));

  // 获取 DOM 元素
  let chartDom = document.getElementById('demo');
  if (!chartDom) {
    console.error("图表容器未找到");
    return;
  }

  // 初始化图表实例
  let myChart = echarts.init(chartDom);

  // 配置项
  let option = {
    title: {
      text: '每月利润统计',
    },
    xAxis: {
      type: 'category',
      data: months, // 使用处理后的月份数据
    },
    yAxis: {
      type: 'value',
    },
    series: [
      {
        data: chartData,
        type: 'line',
        smooth: true,
        label: {
          show: true, // 开启数据标签
          position: 'top', // 数据标签显示位置
          formatter: '{c}', // 使用默认格式，显示数字
          color: '#000', // 字体颜色
          fontSize: 12, // 字体大小
      },
    },
    ],
  };

  // 设置图表配置项
  myChart.setOption(option);
},
}
};
  </script>
  
  <style scoped>
  .dashboard {
    width: 100%;
    padding: 20px;
    box-sizing: border-box;
    font-family: Arial, sans-serif;
  }
  
  /* 顶部统计卡片容器 */
  .stats-container {
    display: flex;
    justify-content: space-between;
    gap: 20px;
    margin-bottom: 30px;
  }
  
  .stats-card {
    flex: 1;
    background-color: #f8f9fa;
    border-radius: 8px;
    box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
    height: 120px;
    display: flex;
    flex-direction: column;
    justify-content: space-around;
    align-items: center;
  }
  
  /* 图标占位样式 */
  .icon-placeholder {
    width: 40px;
    height: 40px;
    background-color: #dfe3e6;
    border-radius: 50%;
  }
  
  /* 数据区域占位 */
  .stats-number {
    width: 60%;
    height: 20px;
    background-color: #e0e0e0;
    border-radius: 4px;
  }
  
  .stats-change {
    width: 40%;
    height: 12px;
    background-color: #e0e0e0;
    border-radius: 4px;
  }
  
  </style>
  

  