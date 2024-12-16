<template>
  <div class="app-container">
    <!-- 第一个容器：商品框，货号，创建时间，搜索和重置 -->
    <div class="search-container">
      <el-form
        :model="queryParams"
        ref="queryForm"
        size="small"
        :inline="true"
        v-show="showSearch"
        label-width="68px"
        class="search-form"
      >
        <div class="search-items">
          <!-- 商品输入框 -->
          <el-form-item label="商品" prop="productName">
            <el-input
              v-model="queryParams.productName"
              placeholder="请输入商品"
              clearable
              @keyup.enter.native="handleQuery"
              style="width: 170px"
            />
          </el-form-item>

          <!-- 查询货号 -->
          <el-form-item label="货号" prop="productCode">
            <el-input
              id="productCode"
              v-model="queryParams.productCode"
              placeholder="请输入货号"
              clearable
              @keyup.enter.native="handleQuery"
            />
          </el-form-item>

          <!-- 查询创建时间 -->
          <el-form-item label="创建时间" prop="createTime">
            <el-date-picker
              clearable
              v-model="queryParams.createTime"
              type="date"
              value-format="yyyy-MM-dd"
              placeholder="请选择创建时间"
              @change="handleQuery"
              style="width: 170px"
            />
          </el-form-item>

          <!-- 搜索重置 -->
          <el-form-item class="form-actions">
            <el-button
              type="primary"
              icon="el-icon-search"
              size="mini"
              @click="handleQuery"
            >搜索</el-button>
            <el-button icon="el-icon-refresh" size="mini" @click="resetQuery">
              重置
            </el-button>
          </el-form-item>
        </div>
      </el-form>
    </div>
  

    <!-- 第二个容器：时间范围，计算总利润，显示总利润 -->
    <div class="profit-container">
      <el-form :model="queryParams"  class="profit-form">
        <!-- 查询一段范围的利润 -->
        <el-form-item label="利润" label-width="53px" class="profit-range">
          <el-row gutter="Number(4)">
            <el-col :span="10">
              <el-date-picker
                clearable
                v-model="queryParams.startDate"
                type="date"
                value-format="yyyy-MM-dd"
                placeholder="请选择开始时间"
                @change="handleQuery"
                style="width: 87%;"
              />
            </el-col>
            <el-col :span="10">
              <el-date-picker
                clearable
                v-model="queryParams.endDate"
                type="date"
                value-format="yyyy-MM-dd"
                placeholder="请选择结束时间"
                @change="handleQuery"
                style="width: 100%;"
              />
            </el-col>
          </el-row>
        </el-form-item>
      </el-form>

      <!-- 利润计算 -->
      <div class="profit-actions">
        <el-button type="primary" @click="calculateTotalProfit">
          计算总利润
        </el-button>
        <span class="total-profit">总利润: {{ totalProfit }}</span>
      </div>
    </div>


    <div>
      <!-- ECharts 容器 -->
      <div id="demo" style="width: 100%; height: 400px;"></div>
          <!-- 第三个容器：导出和分类筛选 -->
    <div class="export-category">
      <el-button
        type="warning"
        plain
        icon="el-icon-download"
        size="mini"
        @click="handleExport"
        v-hasPermi="['merchant:good:export']"
      >导出</el-button>

      <!-- 分类按钮 -->
      <div class="category-buttons">
        <el-button
          type="primary"
          plain
          @click="filterByCategory('shoes')"
          size="mini"
        >
          鞋子
        </el-button>
        <el-button
          type="primary"
          plain
          @click="filterByCategory('clothing')"
          size="mini"
        >
          服饰
        </el-button>
        <el-button
          type="primary"
          plain
          @click="filterByCategory('other')"
          size="mini"
        >
          其他
        </el-button>
      </div>
    </div>
  </div>

    <!-- 表格显示 -->
    <el-table
      v-loading="loading"
      :data="filteredGoods"
      @selection-change="handleSelectionChange"
    >
      <el-table-column label="序号" align="center">
        <template slot-scope="scope">
          {{
            (queryParams.pageNum - 1) * queryParams.pageSize + scope.$index + 1
          }}
        </template>
      </el-table-column>
      <el-table-column label="商品" align="center" prop="productName" />
      <el-table-column label="货号" align="center" prop="productCode" />
      <el-table-column label="码数" align="center" prop="sizeCode">
        <template slot-scope="scope">
          <dict-tag
            :options="dict.type.tb_good_size_code"
            :value="scope.row.sizeCode"
          />
        </template>
      </el-table-column>
      <el-table-column label="尺寸" align="center" prop="dimensions">
        <template slot-scope="scope">
          <dict-tag
            :options="dict.type.tb_good_size"
            :value="scope.row.dimensions"
          />
        </template>
      </el-table-column>
      <el-table-column label="成本" align="center" prop="cost" />
      <el-table-column label="售价" align="center" prop="salePrice" />
      <el-table-column label="利润" align="center" prop="profit">
        <template slot-scope="scope">
          <span>{{ scope.row.profit === 0 ? '未售' : scope.row.profit }}</span>
        </template>
      </el-table-column>
      <el-table-column
        label="操作时间"
        align="center"
        prop="dateTime"
        width="180"
      >
        <template slot-scope="scope">
          <span>{{ parseTime(scope.row.createTime, "{y}-{m}-{d}") }}</span>
        </template>
        <template slot-scope="scope">
          <el-button
            size="mini"
            type="text"
            icon="el-icon-edit"
            @click="handleUpdate(scope.row)"
            v-hasPermi="['merchant:good:edit']"
          >修改</el-button>
          <el-button
            size="mini"
            type="text"
            icon="el-icon-delete"
            @click="handleDelete(scope.row)"
            v-hasPermi="['merchant:good:remove']"
          >删除</el-button>
        </template>
      </el-table-column>
    </el-table>

    <!-- 分页组件 -->
    <pagination
      v-show="total > 0"
      :total="total"
      :page.sync="queryParams.pageNum"
      :limit.sync="queryParams.pageSize"
      @pagination="getList"
    />
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
import { nextTick } from 'vue';
export default {
  name: "Good",
  dicts: ["tb_good_size_code", "tb_good_status", "tb_good_size"],
  data() {
    return {
      loading: true,
      ids: [],
      single: true,
      multiple: true,
      showSearch: true,
      total: 0,
      goodList: [],
      title: "",
      open: false,
      queryParams: {
        pageNum: 1,
        pageSize: 50,
        productName: null,
        createTime: null,
        startDate: null,
        endDate: null,
      },
      totalProfit: 0, //总利润
      filterCategory: null, //新增筛选类型状态
      rawData: [], // 原始商品数据
      chartData: [], // 按月统计的利润数据
      form: {},
      rules: {
        productName: [
          { required: true, message: "商品不能为空", trigger: "blur" },
        ],
        productCode: [
          { required: true, message: "货号不能为空", trigger: "blur" },
        ],
        cost: [{ required: true, message: "成本不能为空", trigger: "blur" }],
      },
    };
  },
  created() {},
  computed: {
    // 过滤符合时间范围的商品
    filteredGoods() {
      let goods = this.goodList;
      if (this.filterCategory === 'shoes') {
        //筛选出有码数或尺寸的商品
        goods = goods.filter(
          (good) => good.sizeCode || good.dimensions
        );
      } else if (this.filterCategory === 'clothing') {
        //筛选出没有码数和尺寸的商品
        goods = goods.filter(
          (good) => !good.sizeCode && good.dimensions
        );
      } else if(this.filterCategory === 'other') {
        //筛选出没有码数和尺寸的商品
        goods = goods.filter(
          (good) => !good.sizeCode && !good.dimensions
         );
      }
      if (this.queryParams.startDate && this.queryParams.endDate) {
        const startDate = this.queryParams.startDate;
        const endDate = this.queryParams.endDate;
        goods = goods.filter((good) => {
          const createTime = good.createTime.split(" ")[0];
          console.log(createTime, startDate, endDate);

          return createTime >= startDate && createTime <= endDate;
        });
      }
      return goods;
    },
  },
  created() {
    this.getList();
  },
  mounted() {
    this.fetchData();
  },
  methods: {
    //按照分类过滤商品
    filterByCategory(category) {
      this.filterCategory = category; //设置当前筛选类别
      this.getList(); //刷新商品列表
    },
    async getList() {
      console.log("分页参数：", this.queryParams);  // 打印查询参数
      this.loading = true;
      try {
        const response = await listGood(this.queryParams);  // 请求数据
        this.goodList = response.rows;  // 当前页数据
        this.total = response.total;   // 总数据量
      } catch (error) {
        console.error('获取数据失败:', error);
      } finally {
        this.loading = false;
      }
      // 在获取列表之前检查是否有时间范围的选择
      let filteredParams = { ...this.queryParams };

      // if (this.queryParams.startDate && this.queryParams.endDate) {
      //   // 将日期范围加入过滤参数
      //   filteredParams.startDate = this.queryParams.startDate;
      //   filteredParams.endDate = this.queryParams.endDate;
      // }

      // listGood(filteredParams).then((response) => {
      //   this.goodList = response.rows.map((item) => ({
      //     ...item,
      //     isSold: item.isSold || false, // 添加 isSold 字段的默认值
      //   }));
      //   this.total = response.total;
      //   this.loading = false;
      // });
    },
    // 取消按钮
    cancel() {
      this.open = false;
      this.reset();
    },
    // 表单重置
    reset() {
      this.form = {
        id: null,
        productName: null,
        productCode: null,
        sizeCode: null,
        dimensions: null,
        cost: null,
        status: null,
        salePrice: null,
        totalValue: null,
        createTime: null,
        dateTime: null,
        profit: null,
      };
      this.resetForm("form");
    },
    handleQuery() {
      this.queryParams.pageNum = 1;
      this.getList();
    },
    resetQuery() {
      this.resetForm("queryForm");
      this.handleQuery();
    },
    // 多选框选中数据
    handleSelectionChange(selection) {
      this.ids = selection.map((item) => item.id);
      this.single = selection.length !== 1;
      this.multiple = !selection.length;
    },
    //添加仓库
    handleAdd() {
      this.reset();
      this.open = true;
      this.title = "添加仓库";
    },
    // 修改按钮操作
    handleUpdate(row) {
      this.reset();
      const id = row.id || this.ids;
      getGood(id).then((response) => {
        this.form = response.data;
        this.open = true;
        this.title = "修改仓库";
      });
    },
    submitForm() {
      this.$refs["form"].validate((valid) => {
        if (valid) {
          if (this.form.id != null) {
            updateGood(this.form).then((response) => {
              this.$modal.msgSuccess("修改成功");
              this.open = false;
              this.getList();
            });
          } else {
            addGood(this.form).then((response) => {
              this.$modal.msgSuccess("新增成功");
              this.open = false;
              this.getList();
            });
          }
        }
      });
    },
    // 删除按钮操作
    handleDelete(row) {
      const ids = row.id || this.ids;
      this.$modal
        .confirm('是否确认删除仓库编号为"' + ids + '"的数据项？')
        .then(() => delGood(ids))
        .then(() => {
          this.getList();
          this.$modal.msgSuccess("删除成功");
        })
        .catch(() => {});
    },
    // 售出商品
    async handleSell(good) {
      try {
        // 1. 让用户输入售价
        const sale = prompt("请输入售价：");
        // 检查是否输入了有效的售价
        if (!sale || isNaN(sale)) {
          alert("请输入有效的数字作为售价！");
          return;
        }
        // 2. 更新商品的状态和售价
        const updatedGood = {
          ...good,
          status: "sold", // 更新商品状态
          salePrice: sale, // 设置售价
        };
        // 3. 调用接口更新商品信息
        await updateGood(updatedGood);
        // 成功后刷新列表或做出其他响应
        alert("商品已成功售出！");
        this.reset(); // 假设你有个方法用于刷新商品列表
      } catch (error) {
        console.error("售出商品时发生错误：", error);
        alert("售出商品时发生错误，请稍后重试！");
      }
    },
    //处理导出
    handleExport() {
      this.download(
        "merchant/good/export",
        { ...this.queryParams },
        `good_${new Date().getTime()}.xlsx`
      );
    },
    //获取所有商品数据
    async fetchAllGoods(){
      try{
        const response = await listGood({pageNum: 1, pageSize: 99999})
        return response.rows;
      }catch(error){
        console.error("获取商品列表时发生错误：", error);
        return [];
      }
    },
    // 计算总利润
    async calculateTotalProfit() {
  try {
    // 获取所有商品
    const allGoods = await this.fetchAllGoods();

    // 如果有选择时间范围，进行过滤
    let filteredGoods = allGoods;

    if (this.queryParams.startDate && this.queryParams.endDate) {
      const startDate = this.queryParams.startDate;
      const endDate = this.queryParams.endDate;

      // 过滤商品，确保它们的 `createTime` 在选择的时间范围内
      filteredGoods = filteredGoods.filter(good => {
        const createTime = good.createTime.split(" ")[0]; // 提取日期部分
        return createTime >= startDate && createTime <= endDate;
      });
    }

    // 计算符合条件商品的总利润
    this.totalProfit= filteredGoods.reduce((total, good) => {
      return total + (good.profit || 0);
    }, 0).toFixed(2);

  } catch (error) {
    console.error("计算利润时发生错误：", error);
  }
},
    // 初始化页面时，计算总利润
    async mounted() {
    await this.calculateTotalProfit();
    this.getList();
  },

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
}

},
};


</script>
<style scoped>
.app-container {
  display: flex;
  flex-direction: column;
  gap: 0px;
  padding: 0px;
  background-color: #f5f7fa;
}

.search-container {
  display: flex;
  flex-direction: column;
  gap: 0px;
  padding: 0px;
  border: none;
  border-radius: 4px;
  background-color: #fff;
}

.search-form {
  display: flex;
  flex-wrap: wrap;
  gap: 20px;
  justify-content: flex-start;
}

.search-items {
  display: flex;
  flex-wrap: wrap;
  gap: 20px;
  align-items: center; /* Align the items vertically */
}

.form-actions {
  display: flex;
  gap: 10px;
  align-items: center; /* Align buttons in the same row */
  margin-top: 0; /* Remove top margin to keep buttons aligned with inputs */
}

.el-form-item {
  margin-bottom: 0; /* Remove bottom margin to keep items aligned */
}

/* 第二个容器：时间范围，计算总利润 */
.profit-container {
  display: flex;
  align-items: center;
  justify-content: flex-start;
  gap: 0px;
  padding: 0px;
  border: none;
  border-radius: 4px;
  background-color: #fff
}
.profit-form {
  display: flex;
  padding: 15px;
  align-items: center;  /* 使时间范围选择框和按钮水平排列 */
}
.profit-range {
  margin-right: -58px;
}
.profit-actions {
  display: flex;
  align-items: center;
  gap: 10px;
}

.total-profit {
  font-weight: bold;
  color: #040404;
}

/* 第三个容器：导出和分类筛选 */
.export-category {
  display: flex;
  justify-content: flex-start; /* 将元素放在左侧 */
  align-items: center; /* 垂直居中 */
  gap: 15px; /* 按钮之间的间距 */
  padding: 5px;
  border: none; 
  border-radius: 5px;
  background-color: #fff;
}

.category-buttons {
  display: flex;
  gap: 10px;
}
.canvas {
  max-width: 600px;
  margin: 20px auto;
}
</style>
