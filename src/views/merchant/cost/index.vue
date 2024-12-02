<template>
  <div class="app-container">
    <!-- 单号查询 -->
    <el-form :model="queryParams" ref="queryForm" size="small" :inline="true" v-show="showSearch" label-width="68px">
      <el-form-item label="单号后四" prop="lastFourDigits">
        <el-input
          v-model="queryParams.lastFourDigits"
          placeholder="请输入单号后四"
          clearable
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item>
        <el-button type="primary" icon="el-icon-search" size="mini" @click="handleQuery">搜索</el-button>
        <el-button icon="el-icon-refresh" size="mini" @click="resetQuery">重置</el-button>
      </el-form-item>
      <!-- 开始时间 -->
      <el-form-item label="开始时间" prop="startDate" class="cost-range">
        <el-date-picker
        v-model="queryParams.startDate"
        placeholder="请选择结束时间"
        clearable
        @change="handleQuery"/>
      </el-form-item>
      <!-- 结束时间 -->
      <el-form-item label="结束时间" prop="endDate"  class="cost-range">
        <el-date-picker
        v-model="queryParams.endDate"
        placeholder="请选择结束时间"
        clearable
        @change="handleQuery"/>
      </el-form-item>  
      <!-- 计算总利润 -->
      <el-row class="cost-row">
      <el-col :span="6" class="cost-col">
        <el-button type="primary" @click="calculateTotalCost">
          计算总利润
        </el-button>
      </el-col>
      </el-row>
      <!-- 总利润显示 -->
      <el-row class="cost-row">
      <el-col :span="6" class="cost-col">
        <span>总利润: {{ totalCost }}</span>
      </el-col>
      </el-row>

    </el-form>
    <!-- 新增修改删除导出 -->
    <el-row :gutter="10" class="mb8">
      <el-col :span="1.5">
        <el-button
          type="primary"
          plain
          icon="el-icon-plus"
          size="mini"
          @click="handleAdd"
          v-hasPermi="['merchant:cost:add']"
        >新增</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button
          type="success"
          plain
          icon="el-icon-edit"
          size="mini"
          :disabled="single"
          @click="handleUpdate"
          v-hasPermi="['merchant:cost:edit']"
        >修改</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button
          type="danger"
          plain
          icon="el-icon-delete"
          size="mini"
          :disabled="multiple"
          @click="handleDelete"
          v-hasPermi="['merchant:cost:remove']"
        >删除</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button
          type="warning"
          plain
          icon="el-icon-download"
          size="mini"
          @click="handleExport"
          v-hasPermi="['merchant:cost:export']"
        >导出</el-button>
      </el-col>
      <right-toolbar :showSearch.sync="showSearch" @queryTable="getList"></right-toolbar>
    </el-row>
    <!-- 表格 -->
    <el-table v-loading="loading" :data="costList" @selection-change="handleSelectionChange">
      <el-table-column label="序号" align="center">
        <template slot-scope="scope">
          {{
            (queryParams.pageNum - 1) * queryParams.pageSize + scope.$index + 1
          }}
        </template>
      </el-table-column>
      <el-table-column label="物流公司" align="center" prop="logisticsCompany">
        <template slot-scope="scope">
          <dict-tag :options="dict.type.tb_name" :value="scope.row.logisticsCompany"/>
        </template>
      </el-table-column>
      <el-table-column label="单号后四" align="center" prop="lastFourDigits" />
      <el-table-column label="开销" align="center" prop="costAmount" />
      <el-table-column label="创建时间" align="center" prop="createTime" width="180">
        <template slot-scope="scope">
          <span>{{ parseTime(scope.row.createTime, '{y}-{m}-{d}') }}</span>
        </template>
      </el-table-column>
      <el-table-column label="操作" align="center" class-name="small-padding fixed-width">
        <template slot-scope="scope">
          <el-button
            size="mini"
            type="text"
            icon="el-icon-edit"
            @click="handleUpdate(scope.row)"
            v-hasPermi="['merchant:cost:edit']"
          >修改</el-button>
          <el-button
            size="mini"
            type="text"
            icon="el-icon-delete"
            @click="handleDelete(scope.row)"
            v-hasPermi="['merchant:cost:remove']"
          >删除</el-button>
        </template>
      </el-table-column>
    </el-table>
    <!-- 分页 -->
    <pagination
      v-show="total>0"
      :total="total"
      :page.sync="queryParams.pageNum"
      :limit.sync="queryParams.pageSize"
      @pagination="getList"
    />

    <!-- 添加或修改邮费对话框 -->
    <el-dialog :title="title" :visible.sync="open" width="500px" append-to-body>
      <el-form ref="form" :model="form" :rules="rules" label-width="80px">
        <el-form-item label="物流公司" prop="logisticsCompany">
          <el-select v-model="form.logisticsCompany" placeholder="请选择物流公司">
            <el-option
              v-for="dict in dict.type.tb_name"
              :key="dict.value"
              :label="dict.label"
              :value="dict.value"
            ></el-option>
          </el-select>
        </el-form-item>
        <el-form-item label="单号后四" prop="lastFourDigits">
          <el-input v-model="form.lastFourDigits" placeholder="请输入单号后四" />
        </el-form-item>
        <el-form-item label="开销" prop="costAmount">
          <el-input v-model="form.costAmount" placeholder="请输入开销" />
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
import { listCost, getCost, delCost, addCost, updateCost } from "@/api/merchant/cost";

export default {
  name: "Cost",
  dicts: ['tb_name'],
  data() {
    return {
      // 遮罩层
      loading: true,
      // 选中数组
      ids: [],
      // 非单个禁用
      single: true,
      // 非多个禁用
      multiple: true,
      // 显示搜索条件
      showSearch: true,
      // 总条数
      total: 0,
      // 邮费表格数据
      costList: [],
      // 弹出层标题
      title: "",
      // 是否显示弹出层
      open: false,
      // 查询参数
      queryParams: {
        pageNum: 1,
        pageSize: 10,
        lastFourDigits: null,
        startDate:null,
        endDate: null,
      },
      totalCost: 0,
      // 表单参数
      form: {},
      // 表单校验
      rules: {
        logisticsCompany: [
          { required: true, message: "物流公司不能为空", trigger: "blur" }
        ],
        lastFourDigits: [
          { required: true, message: "单号后四不能为空", trigger: "blur" }
        ],
        costAmount: [
          { required: true, message: "开销不能为空", trigger: "blur" }
        ],
      }
    };
  },
  created() {
    this.getList();
  },
  methods: {
    /** 查询邮费列表 */
    getList() {
      this.loading = true;
      listCost(this.queryParams).then(response => {
        this.costList = response.rows;
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
        logisticsCompany: null,
        lastFourDigits: null,
        costAmount: null,
        createTime: null,
        dateTime: null
      };
      this.resetForm("form");
    },
    /** 搜索按钮操作 */
    handleQuery() {
      this.queryParams.pageNum = 1;
      this.getList();
    },
    /** 重置按钮操作 */
    resetQuery() {
      this.resetForm("queryForm");
      this.handleQuery();
    },
    // 多选框选中数据
    handleSelectionChange(selection) {
      this.ids = selection.map(item => item.id)
      this.single = selection.length!==1
      this.multiple = !selection.length
    },
    /** 新增按钮操作 */
    handleAdd() {
      this.reset();
      this.open = true;
      this.title = "添加邮费";
    },
    /** 修改按钮操作 */
    handleUpdate(row) {
      this.reset();
      const id = row.id || this.ids
      getCost(id).then(response => {
        this.form = response.data;
        this.open = true;
        this.title = "修改邮费";
      });
    },
    /** 提交按钮 */
    submitForm() {
      this.$refs["form"].validate(valid => {
        if (valid) {
          if (this.form.id != null) {
            updateCost(this.form).then(response => {
              this.$modal.msgSuccess("修改成功");
              this.open = false;
              this.getList();
            });
          } else {
            addCost(this.form).then(response => {
              this.$modal.msgSuccess("新增成功");
              this.open = false;
              this.getList();
            });
          }
        }
      });
    },
    /** 删除按钮操作 */
    handleDelete(row) {
      const ids = row.id || this.ids;
      this.$modal.confirm('是否确认删除邮费编号为"' + ids + '"的数据项？').then(function() {
        return delCost(ids);
      }).then(() => {
        this.getList();
        this.$modal.msgSuccess("删除成功");
      }).catch(() => {});
    },
    /** 导出按钮操作 */
    handleExport() {
      this.download('merchant/cost/export', {
        ...this.queryParams
      }, `cost_${new Date().getTime()}.xlsx`)
    },
    //获取所有列表开销
    async fetchAllCosts() { 
      try{
          const response = await listCost({pageNum:1,pageSize: 10})
          return response.rows;
      }catch(error){
          console.error("获取开销列表时发生错误: ",error);
          return [];
      }
    },
    // 计算总开销
    async calculateTotalCost() {
  try {
    // 获取所有商品
    const allCost = await this.fetchAllCosts();

    // 如果有选择时间范围，进行过滤
    let filteredCosts = allCost;

    if (this.queryParams.startDate && this.queryParams.endDate) {
      const startDate = this.queryParams.startDate;
      const endDate = this.queryParams.endDate;

      // 过滤商品，确保它们的 `createTime` 在选择的时间范围内
      filteredCosts = filteredCosts.filter(cost => {
        const createTime = cost.createTime.split(" ")[0]; // 提取日期部分
        return createTime >= startDate && createTime <= endDate;
      });
    }

    // 计算符合条件商品的总利润
    this.totalCost = filteredCosts.reduce((total, cost) => {
      return total + (parseFloat(cost.costAmount) || 0);
    }, 0);
    this.$message.success("总开销计算成功")
  } catch (error) {
    console.error("计算开销时发生错误：", error);
    this.$message.error("计算总开销失败,请重试! ");
  }
},

  }
};
</script>
