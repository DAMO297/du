<template>
  <div class="app-container">
    <el-form
      :model="queryParams"
      ref="queryForm"
      size="small"
      :inline="true"
      v-show="showSearch"
      label-width="68px"
    >
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
        >
        </el-date-picker>
      </el-form-item>
      <!-- 搜索重置 -->
      <el-form-item>
        <el-button
          type="primary"
          icon="el-icon-search"
          size="mini"
          @click="handleQuery"
          >搜索</el-button
        >
        <el-button icon="el-icon-refresh" size="mini" @click="resetQuery"
          >重置</el-button
        >
      </el-form-item>

      <!-- 查询一段范围的利润 -->
      <el-form-item label="开始时间" prop="startDate" class="profit-range">
        <el-date-picker
          clearable
          v-model="queryParams.startDate"
          type="date"
          value-format="yyyy-MM-dd"
          placeholder="请选择开始时间"
          @change="handleQuery"
        />
      </el-form-item>
      <el-form-item label="结束时间" prop="endDate" class="profit-range">
        <el-date-picker
          clearable
          v-model="queryParams.endDate"
          type="date"
          value-format="yyyy-MM-dd"
          placeholder="请选择开始时间"
          @change="handleQuery"
        />
      </el-form-item>
    </el-form>

    <!-- 计算总利润 -->
    <el-row class="profit-row">
      <el-col :span="6" class="profit-col">
        <el-button type="primary" @click="calculateTotalProfit">
          计算总利润
        </el-button>
      </el-col>
    </el-row>

    <!-- 总利润显示 -->
    <el-row class="profit-row">
      <el-col :span="6" class="profitotal-col">
        <span>总利润: {{ totalProfit }}</span>
      </el-col>
    </el-row>
    <!-- 导出 -->
    <el-col :span="1.5" class="excel">
      <el-button
        type="warning"
        plain
        icon="el-icon-download"
        size="mini"
        @click="handleExport"
        v-hasPermi="['merchant:good:export']"
        >导出</el-button
      >
    </el-col>
    <!-- 隐藏搜索,刷新 -->
    <right-toolbar
      class="toolbar"
      :showSearch.sync="showSearch"
      @queryTable="getList"
    ></right-toolbar>
 
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
      <el-table-column label="利润" align="center" prop="profit" />
      <el-table-column
        label="操作时间"
        align="center"
        prop="dateTime"
        width="180"
      >
        <template slot-scope="scope">
          <span>{{ parseTime(scope.row.createTime, "{y}-{m}-{d}") }}</span>
        </template>
    <!-- 修改删除 -->
        <template slot-scope="scope">
          <el-button
            size="mini"
            type="text"
            icon="el-icon-edit"
            @click="handleUpdate(scope.row)"
            v-hasPermi="['merchant:good:edit']"
            >修改</el-button
          >
          <el-button
            size="mini"
            type="text"
            icon="el-icon-delete"
            @click="handleDelete(scope.row)"
            v-hasPermi="['merchant:good:remove']"
            >删除</el-button
          >
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

    <!-- 添加或修改仓库对话框 -->
    <el-dialog :title="title" :visible.sync="open" width="500px" append-to-body>
      <el-form ref="form" :model="form" :rules="rules" label-width="80px">
        <el-form-item label="商品" prop="productName">
          <el-input v-model="form.productName" placeholder="请输入商品" />
        </el-form-item>
        <el-form-item label="货号" prop="productCode">
          <el-input v-model="form.productCode" placeholder="请输入货号" />
        </el-form-item>
        <el-form-item label="码数" prop="sizeCode">
          <el-select v-model="form.sizeCode" placeholder="请选择码数">
            <el-option
              v-for="dict in dict.type.tb_good_size_code"
              :key="dict.value"
              :label="dict.label"
              :value="dict.value"
            ></el-option>
          </el-select>
        </el-form-item>
        <el-form-item label="尺寸" prop="dimensions">
          <el-select v-model="form.dimensions" placeholder="请选择尺寸">
            <el-option
              v-for="dict in dict.type.tb_good_size"
              :key="dict.value"
              :label="dict.label"
              :value="dict.value"
            ></el-option>
          </el-select>
        </el-form-item>
        <el-form-item label="成本" prop="cost">
          <el-input v-model="form.cost" placeholder="请输入成本" />
        </el-form-item >
        <el-form-item label="售价" prop="salePrice">
          <el-input v-model="form.salePrice" placeholder="请输入售价" />
        </el-form-item>
        <el-form-item label="利润" prop="profit">
          <el-input v-model="form.profit" placeholder="请输入利润" />
        </el-form-item>
      </el-form>
      <div slot="footer" class="dialog-footer">
        <el-button type="primary" @click="submitForm">确 定</el-button>
        <el-button @click="cancel">取 消</el-button>
      </div>
    </el-dialog>
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
  methods: {
    getList() {
      this.loading = true;
      // 在获取列表之前检查是否有时间范围的选择
      let filteredParams = { ...this.queryParams };

      if (this.queryParams.startDate && this.queryParams.endDate) {
        // 将日期范围加入过滤参数
        filteredParams.startDate = this.queryParams.startDate;
        filteredParams.endDate = this.queryParams.endDate;
      }

      listGood(filteredParams).then((response) => {
        this.goodList = response.rows.map((item) => ({
          ...item,
          isSold: item.isSold || false, // 添加 isSold 字段的默认值
        }));
        this.total = response.total;
        this.loading = false;
      });
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
    this.totalProfit = filteredGoods.reduce((total, good) => {
      return total + (good.profit || 0);
    }, 0);

  } catch (error) {
    console.error("计算利润时发生错误：", error);
  }
},
    // 初始化页面时，计算总利润
  async mounted() {
    await this.calculateTotalProfit();
  },
 

  },
};

</script>
<style scoped>
/* 容器样式 */
.app-container {
  padding: 20px;
  position: relative;
}

/* 查询表单样式 */
.el-form {
  margin-bottom: 100px;
  display: flex;
  flex-wrap: wrap;
}

/* 每个表单项的样式 */
.el-form-item {
  margin-right: 20px;
  margin-bottom: 10px;
}

/* 搜索框和时间选择器宽度 */
.el-input,
.el-date-picker {
  width: 170px;
}

/* 导出按钮样式 */
.el-col {
  margin-bottom: 20px;
}

/* 利润显示区 */
.profit-row {
  margin-top: -95.5px;
  display: flex;
  justify-content: flex-end; /* 右对齐 */
  align-items: center;
  position: relative;
  left: 235px;
}

.profit-col {
  margin-right: 10px; /* 保持间距 */
}

.profitotal-col {
  font-size: 16px;
  font-weight: bold;
}

/* 表格样式 */
.el-table {
  margin-top: 20px;
}

/* 分页组件样式 */
.pagination {
  margin-top: 20px;
  display: flex;
  justify-content: flex-end;
}
/*利润时间范围*/
.profit-range {
  margin-right: 15px;
  margin-bottom: -55px;
  position: relative;
  left: 0px;
}
/*工具栏*/
.toolbar {
  position: absolute;
  top: 75px; /* 与顶部的间距 */
  left: 25px; /* 与左侧的间距 */
}
/*导出*/
.excel {
  position: relative;
  top: 9px; /* 与顶部的间距 */
  left: 80px; /* 与左侧的间距 */
}
</style>
