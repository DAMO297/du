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
      <!-- 输入商品 -->
      <el-form-item label="商品" prop="productName">
        <el-input
          v-model="queryParams.productName"
          placeholder="请输入商品"
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
          @change="handleDateChange"
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
    </el-form>
       <!-- 新增 导出 -->
    <el-row :gutter="10" class="mb8">
      <el-col :span="1.5">
        <el-button
          type="primary"
          plain
          icon="el-icon-plus"
          size="mini"
          @click="handleAdd"
          v-hasPermi="['merchant:good:add']"
          >新增
        </el-button
        >
      </el-col>
      
      <el-col :span="1.5">
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
      <right-toolbar
        :showSearch.sync="showSearch"
        @queryTable="getList"
      ></right-toolbar>
    </el-row>
      <!-- 表单 -->
    <el-table
      v-loading="loading"
      :data="filteredGoods"
      @selection-change="handleSelectionChange"
    >
      <el-table-column type="selection" width="55" align="center" />
      <el-table-column label="序号" align="center">
        <template slot-scope="scope">
          {{ (queryParams.pageNum - 1) * queryParams.pageSize + scope.$index + 1 }}
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
      <el-table-column
        label="创建时间"
        align="center"
        prop="createTime"
        width="180"
      >
        <template slot-scope="scope">
          <span>{{ parseTime(scope.row.createTime, "{y}-{m}-{d}") }}</span>
        </template>
      </el-table-column>
      <el-table-column
        label="操作"
        align="center"
        class-name="small-padding fixed-width"
      >
        <!-- 表格右边的几个 -->
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
          <el-button
            data="filteredGoods"
            size="mini"
            type="text"
            icon="el-icon-sell"
            @click="handleSell(scope.row)"
            v-hasPermi="['merchant:good:sell']"
            >售出
          </el-button>
        </template>
      </el-table-column>
    </el-table>

    <!-- 自定义分页组件 -->
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
    // api接口
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
        displayId: null,
        pageNum: 1,
        pageSize: 30,
        productName: null,
        createTime: null,
        selectedDate: null,
        profitDate: null,
      },
      productCount: 0, // 用于存储商品条数
      totalProfit: 0, // 用于存储总利润
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
    filteredGoods() {
      // 过滤已售出的商品
      return this.goodList.filter((good) => good.status !== "sold");
    },
  },
  created() {
    this.getList();
  },
  methods: {
    getList() {
      this.loading = true;
      listGood(this.queryParams).then((response) => {
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
        displayId: null,
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
    //根据日期改变
    handleDateChange() {
      this.queryParams.pageNum = 1;
      this.getList();
    },
    resetQuery() {
      this.resetForm("queryForm");
      this.handleQuery();
    },
    handleSelectionChange(selection) {
      this.ids = selection.map((item) => item.id);
      this.single = selection.length !== 1;
      //this.status = returned;
      this.multiple = !selection.length;
    },
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

    handleExport() {
      this.download(
        "merchant/good/export",
        { ...this.queryParams },
        `good_${new Date().getTime()}.xlsx`
      );
    },
    // 商品售出
    async handleSell(good) {
      try {
        //1.让用户输入售价
        const sale = prompt("请输入售价:");
        //检查是否输入了有效售价
        if (!sale || isNaN(sale)) {
          alert("请输入有效的数字!");
          return;
        }
        //2.更新商品的状态和售价
        const updatedGood = {
          ...good,
          status: "sold", //更新商品状态
          salePrice: sale, //设置售价
        };
        //调用接口更新商品信息
        await updateGood(updatedGood);
        alert("商品已成功售出!");
        this.reset(); //
      } catch (error) {
        console.error("售出商品时发生错误:", error);
        alert("售出商品时发生错误，请稍后重试!");
      }
    },
    handleDateChange() {
      this.queryParams.createTime = this.queryParams.selectedDate;
      this.queryParams.profitDate = this.queryParams.selectedDate;

      //执行查询
      this.getProductCount();
      this, getTotalProfit();
    },
    getProductCount() {
      fetchTotalProfit(this.queryParams.selectedDate).then((profit) => {
        this.totalProfit = profit; //更新总利润
      });
    },
  },
};
</script>
