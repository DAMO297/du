<template>
  <div class="cost-report-container">
    <!-- 顶部操作按钮 -->
    <el-row :gutter="20" class="mb-20">
      <el-col :span="24">
        <el-button type="primary" @click="handleGenerateReport">
          <i class="el-icon-plus"></i> 生成报告
        </el-button>
        <el-button type="success" @click="handleExport">
          <i class="el-icon-download"></i> 导出
        </el-button>
      </el-col>
    </el-row>

    <!-- 搜索区域 -->
    <el-card class="search-card">
      <el-form :model="queryParams" ref="queryForm" :inline="true">
        <el-form-item label="报告类型" prop="reportType">
          <el-select
            v-model="queryParams.reportType"
            placeholder="请选择报告类型"
            clearable
          >
            <el-option label="周报" value="1" />
            <el-option label="月报" value="2" />
            <el-option label="季报" value="3" />
            <el-option label="年报" value="4" />
            <el-option label="自定义" value="5" />
          </el-select>
        </el-form-item>
        <el-form-item label="报告状态" prop="reportStatus">
          <el-select
            v-model="queryParams.reportStatus"
            placeholder="请选择报告状态"
            clearable
          >
            <el-option label="待生成" value="0" />
            <el-option label="已生成" value="1" />
            <el-option label="已导出" value="2" />
          </el-select>
        </el-form-item>
        <el-form-item label="生成时间">
          <el-date-picker
            v-model="dateRange"
            type="daterange"
            range-separator="至"
            start-placeholder="开始日期"
            end-placeholder="结束日期"
            value-format="yyyy-MM-dd"
          ></el-date-picker>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" icon="el-icon-search" @click="handleQuery"
            >搜索</el-button
          >
          <el-button icon="el-icon-refresh" @click="resetQuery">重置</el-button>
        </el-form-item>
      </el-form>
    </el-card>

    <!-- 数据表格 -->
    <el-card class="table-card">
      <el-table v-loading="loading" :data="costReportList">
        <el-table-column label="报告编号" align="center" prop="id" />
        <el-table-column label="报告名称" align="center" prop="reportName" />
        <el-table-column label="报告类型" align="center" prop="reportType">
          <template slot-scope="scope">
            <el-tag :type="getReportTypeTag(scope.row.reportType)">
              {{ getReportTypeLabel(scope.row.reportType) }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="报告周期" align="center" prop="reportPeriod" />
        <el-table-column
          label="开始日期"
          align="center"
          prop="startDate"
          width="180"
        >
          <template slot-scope="scope">
            <span>{{ parseTime(scope.row.startDate) }}</span>
          </template>
        </el-table-column>
        <el-table-column
          label="结束日期"
          align="center"
          prop="endDate"
          width="180"
        >
          <template slot-scope="scope">
            <span>{{ parseTime(scope.row.endDate) }}</span>
          </template>
        </el-table-column>
        <el-table-column label="总费用" align="center" prop="totalCost">
          <template slot-scope="scope">
            <span class="cost-amount">¥{{ scope.row.totalCost }}</span>
          </template>
        </el-table-column>
        <el-table-column label="订单数" align="center" prop="itemCount" />
        <el-table-column label="平均费用" align="center" prop="averageCost">
          <template slot-scope="scope">
            <span>¥{{ scope.row.averageCost }}</span>
          </template>
        </el-table-column>
        <el-table-column label="异常订单" align="center" prop="abnormalCount" />
        <el-table-column label="状态" align="center" prop="reportStatus">
          <template slot-scope="scope">
            <el-tag :type="getStatusType(scope.row.reportStatus)">
              {{ getStatusLabel(scope.row.reportStatus) }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column
          label="操作"
          align="center"
          class-name="small-padding fixed-width"
        >
          <template slot-scope="scope">
            <el-button
              size="mini"
              type="text"
              icon="el-icon-view"
              @click="handleView(scope.row)"
              >查看</el-button
            >
            <el-button
              size="mini"
              type="text"
              icon="el-icon-edit"
              @click="handleUpdate(scope.row)"
              >修改</el-button
            >
            <el-button
              size="mini"
              type="text"
              icon="el-icon-delete"
              @click="handleDelete(scope.row)"
              >删除</el-button
            >
            <el-button
              size="mini"
              type="text"
              icon="el-icon-download"
              @click="handleExportSingle(scope.row)"
              >导出</el-button
            >
          </template>
        </el-table-column>
      </el-table>

      <pagination
        v-show="total > 0"
        :total="total"
        :page.sync="queryParams.pageNum"
        :limit.sync="queryParams.pageSize"
        @pagination="getList"
      />
    </el-card>

    <!-- 生成报告对话框 -->
    <el-dialog
      :title="dialogTitle"
      :visible.sync="dialogVisible"
      width="500px"
      append-to-body
    >
      <el-form ref="form" :model="form" :rules="rules" label-width="80px">
        <el-form-item label="报告类型" prop="reportType">
          <el-radio-group v-model="form.reportType">
            <el-radio label="1">周报</el-radio>
            <el-radio label="2">月报</el-radio>
            <el-radio label="3">季报</el-radio>
            <el-radio label="4">年报</el-radio>
            <el-radio label="5">自定义</el-radio>
          </el-radio-group>
        </el-form-item>
        <el-form-item
          label="时间范围"
          prop="dateRange"
          v-if="form.reportType === '5'"
        >
          <el-date-picker
            v-model="form.dateRange"
            type="daterange"
            range-separator="至"
            start-placeholder="开始日期"
            end-placeholder="结束日期"
            value-format="yyyy-MM-dd"
          ></el-date-picker>
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
  listCostReport,
  getCostReport,
  addCostReport,
  updateCostReport,
  delCostReport,
  generatePeriodicReport,
  generateCustomReport,
  getOptimizationSuggestions,
  exportReport,
  exportCostReport,
} from "@/api/merchant/cost-report";

export default {
  name: "CostReport",
  data() {
    // 日期范围验证
    const validateDateRange = (rule, value, callback) => {
      if (this.form.reportType === "5" && (!value || value.length !== 2)) {
        callback(new Error("请选择时间范围"));
      } else {
        callback();
      }
    };

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
      // 邮费报告表格数据
      costReportList: [],
      // 弹出层标题
      dialogTitle: "",
      // 是否显示弹出层
      dialogVisible: false,
      // 日期范围
      dateRange: [],
      // 查询参数
      queryParams: {
        pageNum: 1,
        pageSize: 10,
        reportType: null,
        reportStatus: null,
        startDate: null,
        endDate: null,
      },
      // 表单参数
      form: {
        reportType: "1",
        dateRange: [],
      },
      // 表单校验
      rules: {
        reportType: [
          { required: true, message: "请选择报告类型", trigger: "change" },
        ],
        dateRange: [{ validator: validateDateRange, trigger: "change" }],
      },
    };
  },
  created() {
    this.getList();
  },
  methods: {
    /** 查询邮费报告列表 */
    getList() {
      this.loading = true;
      listCostReport(this.addDateRange(this.queryParams, this.dateRange)).then(
        (response) => {
          this.costReportList = response.rows;
          this.total = response.total;
          this.loading = false;
        }
      );
    },
    /** 生成报告按钮操作 */
    handleGenerateReport() {
      this.dialogTitle = "生成邮费报告";
      this.dialogVisible = true;
    },
    /** 导出按钮操作 */
    handleExport() {
      this.$confirm("是否确认导出所有邮费报告数据?", "警告", {
        confirmButtonText: "确定",
        cancelButtonText: "取消",
        type: "warning",
      })
        .then(() => {
          exportCostReport(
            this.addDateRange(this.queryParams, this.dateRange)
          ).then((response) => {
            this.$message.success("导出成功");
          });
        })
        .catch(() => {});
    },
    /** 搜索按钮操作 */
    handleQuery() {
      this.queryParams.pageNum = 1;
      this.getList();
    },
    /** 重置按钮操作 */
    resetQuery() {
      this.dateRange = [];
      this.resetForm("queryForm");
      this.handleQuery();
    },
    /** 查看按钮操作 */
    handleView(row) {
      getCostReport(row.id)
        .then((response) => {
          if (response.code === 200) {
            this.$router.push({
              path: "/merchant/cost-report/detail",
              query: { id: row.id },
            });
          } else {
            this.$message.error(response.msg || "获取报告详情失败");
          }
        })
        .catch((err) => {
          this.$message.error("获取报告详情失败");
          console.error(err);
        });
    },
    /** 修改按钮操作 */
    handleUpdate(row) {
      this.dialogTitle = "修改邮费报告";
      this.dialogVisible = true;
      this.$nextTick(() => {
        this.form = row;
      });
    },
    /** 删除按钮操作 */
    handleDelete(row) {
      const ids = row.id || this.ids;
      this.$confirm(
        '是否确认删除邮费报告编号为"' + ids + '"的数据项?',
        "警告",
        {
          confirmButtonText: "确定",
          cancelButtonText: "取消",
          type: "warning",
        }
      )
        .then(() => {
          return delCostReport(ids);
        })
        .then(() => {
          this.getList();
          this.$message.success("删除成功");
        });
    },
    /** 表单提交 */
    submitForm() {
      this.$refs["form"].validate((valid) => {
        if (valid) {
          this.loading = true;
          try {
            if (this.form.reportType === "5") {
              // 自定义时间段报告
              if (!this.form.dateRange || this.form.dateRange.length !== 2) {
                this.$message.error("请选择自定义报告的时间范围");
                this.loading = false;
                return;
              }

              // 发送日期字符串，API会将其转换为时间戳
              const startDate = this.form.dateRange[0];
              const endDate = this.form.dateRange[1];

              generateCustomReport(startDate, endDate)
                .then((response) => {
                  if (response.code === 200) {
                    this.$message.success("生成成功");
                    this.dialogVisible = false;
                    this.getList();
                  } else {
                    this.$message.error(response.msg || "生成失败");
                  }
                  this.loading = false;
                })
                .catch((error) => {
                  console.error("生成报告失败", error);
                  this.$message.error(
                    "生成报告失败: " + (error.message || "请稍后重试")
                  );
                  this.loading = false;
                });
            } else {
              // 周期性报告
              const reportType = parseInt(this.form.reportType);

              generatePeriodicReport(reportType)
                .then((response) => {
                  if (response.code === 200) {
                    this.$message.success("生成成功");
                    this.dialogVisible = false;
                    this.getList();
                  } else {
                    this.$message.error(response.msg || "生成失败");
                  }
                  this.loading = false;
                })
                .catch((error) => {
                  console.error("生成报告失败", error);
                  this.$message.error(
                    "生成报告失败: " + (error.message || "请稍后重试")
                  );
                  this.loading = false;
                });
            }
          } catch (error) {
            console.error("表单提交错误", error);
            this.$message.error(
              "表单提交错误: " + (error.message || "请检查输入数据")
            );
            this.loading = false;
          }
        }
      });
    },
    /** 取消按钮 */
    cancel() {
      this.dialogVisible = false;
      this.reset();
    },
    /** 表单重置 */
    reset() {
      this.form = {
        reportType: "1",
        dateRange: [],
      };
      this.resetForm("form");
    },
    /** 获取报告类型标签 */
    getReportTypeLabel(type) {
      const typeMap = {
        1: "周报",
        2: "月报",
        3: "季报",
        4: "年报",
        5: "自定义",
      };
      return typeMap[type] || "未知";
    },
    /** 获取报告类型标签样式 */
    getReportTypeTag(type) {
      const typeMap = {
        1: "info",
        2: "success",
        3: "warning",
        4: "danger",
        5: "",
      };
      return typeMap[type] || "";
    },
    /** 获取状态标签 */
    getStatusLabel(status) {
      const statusMap = {
        0: "待生成",
        1: "已生成",
        2: "已导出",
      };
      return statusMap[status] || "未知";
    },
    /** 获取状态标签样式 */
    getStatusType(status) {
      const typeMap = {
        0: "info",
        1: "success",
        2: "warning",
      };
      return typeMap[status] || "";
    },
    /** 导出单个报告 */
    handleExportSingle(row) {
      this.$confirm("是否确认导出该邮费报告?", "警告", {
        confirmButtonText: "确定",
        cancelButtonText: "取消",
        type: "warning",
      })
        .then(() => {
          exportReport(row.id).then((response) => {
            this.$message.success("导出成功");
          });
        })
        .catch(() => {});
    },
  },
};
</script>

<style scoped>
.cost-report-container {
  padding: 20px;
  background-color: #f5f7fa;
  min-height: 100vh;
}

.mb-20 {
  margin-bottom: 20px;
}

.search-card {
  margin-bottom: 20px;
}

.table-card {
  margin-bottom: 20px;
}

.cost-amount {
  color: #f56c6c;
  font-weight: bold;
}

.el-tag {
  margin-right: 5px;
}
</style>
