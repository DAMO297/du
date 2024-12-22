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
      <div class="search-card">
        <div class="search-content">
          <div class="search-group">
            <!-- 左侧：单号搜索和按钮 -->
            <div class="left-section">
              <div class="search-item">
                <el-form-item label="单号后四" prop="lastFourDigits">
                  <div class="input-with-button">
                    <el-input
                      v-model="queryParams.lastFourDigits"
                      placeholder="请输入单号后四"
                      prefix-icon="el-icon-search"
                      clearable
                      @keyup.enter.native="handleQuery"
                    />
                    <el-button 
                      type="primary" 
                      icon="el-icon-search"
                      size="small"
                      @click="handleQuery"
                    >
                      搜索
                    </el-button>
                  </div>
                </el-form-item>
              </div>
            </div>

            <!-- 右侧：日期选择和总利润 -->
            <div class="right-section">
              <div class="date-profit-row">
                <div class="date-range">
                  <el-form-item label="开始时间" prop="startDate">
                    <el-date-picker
                      v-model="queryParams.startDate"
                      type="date"
                      placeholder="选择开始日期"
                      clearable
                      @change="handleQuery"
                    />
                  </el-form-item>
                  <span class="date-separator"></span>
                  <el-form-item label="结束时间" prop="endDate">
                    <el-date-picker
                      v-model="queryParams.endDate"
                      type="date"
                      placeholder="选择结束日期"
                      clearable
                      @change="handleQuery"
                    />
                  </el-form-item>
                  <div class="profit-section">
                    <div class="profit-info">
                      <span class="profit-amount">¥ {{ totalCost.toFixed(2) }}</span>
                      <span class="profit-label">总开销</span>
                    </div>
                    <el-button 
                      type="primary"
                      icon="el-icon-refresh"
                      circle
                      class="refresh-btn"
                      @click="calculateTotalCost"
                    ></el-button>
                  </div>
                </div>
              </div>
            </div>
          </div>
        </div>
      </div>
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
          >新增</el-button
        >
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
          >修改</el-button
        >
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
          >删除</el-button
        >
      </el-col>
      <el-col :span="1.5">
        <el-button
          type="warning"
          plain
          icon="el-icon-download"
          size="mini"
          @click="handleExport"
          v-hasPermi="['merchant:cost:export']"
          >导出</el-button
        >
      </el-col>
      <el-col :span="1.5">
        <el-button
          type="info"
          plain
          icon="el-icon-refresh"
          size="mini"
          @click="resetQuery"
        >重置</el-button>
      </el-col>
      <right-toolbar
        :showSearch.sync="showSearch"
        @queryTable="getList"
      ></right-toolbar>
    </el-row>
    <!-- 表格 -->
    <el-table
      v-loading="loading"
      :data="costList"
      @selection-change="handleSelectionChange"
    >
      <el-table-column label="序号" align="center">
        <template slot-scope="scope">
          {{
            (queryParams.pageNum - 1) * queryParams.pageSize + scope.$index + 1
          }}
        </template>
      </el-table-column>
      <el-table-column label="物流公司" align="center" prop="logisticsCompany">
        <template slot-scope="scope">
          <dict-tag
            :options="dict.type.tb_name"
            :value="scope.row.logisticsCompany"
          />
        </template>
      </el-table-column>
      <el-table-column label="单号后四" align="center" prop="lastFourDigits" />
      <el-table-column label="开销" align="center" prop="costAmount" />
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
        <template slot-scope="scope">
          <el-button
            size="mini"
            type="text"
            icon="el-icon-edit"
            @click="handleUpdate(scope.row)"
            v-hasPermi="['merchant:cost:edit']"
            >修改</el-button
          >
          <el-button
            size="mini"
            type="text"
            icon="el-icon-delete"
            @click="handleDelete(scope.row)"
            v-hasPermi="['merchant:cost:remove']"
            >删除</el-button
          >
        </template>
      </el-table-column>
    </el-table>
    <!-- 分页 -->
    <pagination
      v-show="total > 0"
      :total="total"
      :page.sync="queryParams.pageNum"
      :limit.sync="queryParams.pageSize"
      @pagination="getList"
    />

    <!-- 添加或修改邮费对话框 -->
    <el-dialog :title="title" :visible.sync="open" width="500px" append-to-body>
      <el-form ref="form" :model="form" :rules="rules" label-width="80px">
        <el-form-item label="物流公司" prop="logisticsCompany">
          <el-select
            v-model="form.logisticsCompany"
            placeholder="请选择物流公司"
          >
            <el-option
              v-for="dict in dict.type.tb_name"
              :key="dict.value"
              :label="dict.label"
              :value="dict.value"
            ></el-option>
          </el-select>
        </el-form-item>
        <el-form-item label="单号后四" prop="lastFourDigits">
          <el-input
            v-model="form.lastFourDigits"
            placeholder="请输入单号后四"
          />
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
import {
  listCost,
  getCost,
  delCost,
  addCost,
  updateCost,
} from "@/api/merchant/cost";

export default {
  name: "Cost",
  dicts: ["tb_name"],
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
        startDate: null,
        endDate: null,
      },
      totalCost: 0,
      // 表单参数
      form: {},
      // 表单校验
      rules: {
        logisticsCompany: [
          { required: true, message: "物流公司不能为空", trigger: "blur" },
        ],
        lastFourDigits: [
          { required: true, message: "单号后四不能为空", trigger: "blur" },
        ],
        costAmount: [
          { required: true, message: "开销不能为空", trigger: "blur" },
        ],
      },
    };
  },
  created() {
    this.getList();
  },
  methods: {
    /** 查询邮费列表 */
    getList() {
      this.loading = true;
      listCost(this.queryParams).then((response) => {
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
        dateTime: null,
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
      this.ids = selection.map((item) => item.id);
      this.single = selection.length !== 1;
      this.multiple = !selection.length;
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
      const id = row.id || this.ids;
      getCost(id).then((response) => {
        this.form = response.data;
        this.open = true;
        this.title = "修改邮费";
      });
    },
    /** 提交按钮 */
    submitForm() {
      this.$refs["form"].validate((valid) => {
        if (valid) {
          if (this.form.id != null) {
            updateCost(this.form).then((response) => {
              this.$modal.msgSuccess("修改成功");
              this.open = false;
              this.getList();
            });
          } else {
            addCost(this.form).then((response) => {
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
      this.$modal
        .confirm('是否确认删除邮费编号为"' + ids + '"的数据项？')
        .then(function () {
          return delCost(ids);
        })
        .then(() => {
          this.getList();
          this.$modal.msgSuccess("删除成功");
        })
        .catch(() => {});
    },
    /** 导出按钮操作 */
    handleExport() {
      this.download(
        "merchant/cost/export",
        {
          ...this.queryParams,
        },
        `cost_${new Date().getTime()}.xlsx`
      );
    },
    //获取所有列表开销
    async fetchAllCosts() {
      try {
        const response = await listCost({ pageNum: 1, pageSize: 10 });
        return response.rows;
      } catch (error) {
        console.error("获取开销列表时发生错误: ", error);
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
          filteredCosts = filteredCosts.filter((cost) => {
            const createTime = cost.createTime.split(" ")[0]; // 提取日期部分
            return createTime >= startDate && createTime <= endDate;
          });
        }

        // 计算符合条件商品的总利润
        this.totalCost = filteredCosts.reduce((total, cost) => {
          return total + (parseFloat(cost.costAmount) || 0);
        }, 0);
        this.$message.success("总开销计算成功");
      } catch (error) {
        console.error("计算开销时发生错误：", error);
        this.$message.error("计算总开销失败,请重试! ");
      }
    },
  },
};
</script>

<style lang="scss" scoped>
.app-container {
  .search-card {
    background: #fff;
    border-radius: 16px;
    padding: 24px;
    margin-bottom: 24px;
    box-shadow: 0 4px 20px rgba(0, 0, 0, 0.08);

    .search-content {
      .search-group {
        display: flex;
        gap: 32px;
        
        @media (max-width: 768px) {
          flex-direction: column;
          gap: 24px;
        }

        // 左侧部分样式
        .left-section {
          flex: 1;
          display: flex;
          flex-direction: column;
          gap: 16px;
          padding-right: 24px; // 添加右侧内边距

          .search-item {
            .el-form-item {
              margin-bottom: 0;
              width: 100%;
              min-width: 160px; // 添加最小宽度
              max-width: 180px; // 添加最大宽度，与日期选择器保持一致

              .input-with-button {
                display: flex;
                align-items: center;
                gap: 8px;

                .el-input {
                  flex: 1;
                }

                .el-button {
                  position: absolute; // 将按钮绝对定位
                  right: -90px;    // 调整按钮位置
                  margin-left: 8px;
                  padding: 8px 16px;
                  border-radius: 4px;
                  font-size: 13px;
                }
              }
            }
          }
        }

        // 右侧部分样式
        .right-section {
          flex: 2;
          padding-left: 24px;
          border-left: 1px solid #e5e7eb;
          position: relative;

          @media (max-width: 768px) {
            padding-left: 0;
            border-left: none;
            padding-top: 16px;
            border-top: 1px solid #e5e7eb;
          }

          .date-range {
            display: flex;
            align-items: center;
            gap: 12px;
            width: 100%;
            padding-right: 190px;
            
            @media (max-width: 768px) {
              flex-wrap: wrap;
            }

            .el-form-item {
              margin-bottom: 0;
              flex: 1;
              min-width: 160px;
              max-width: 180px;
            }

            .date-separator {
              padding: 0 31.5px; // 减小间距
            }

            .profit-section {
              display: flex;
              align-items: center;
              background: linear-gradient(135deg, #3b82f6, #2563eb);
              padding: 8px 12px;
              border-radius: 8px;
              color: white;
              margin-left: 35px;
              width: 170px;
              height: 72px;
              position: absolute;
              right: 24px;
              top: 50%;
              transform: translateY(-50%);

              .profit-info {
                display: flex;
                flex-direction: row;
                align-items: center;
                gap: 8px;

                .profit-amount {
                  font-size: 16px;
                  font-weight: 600;
                }

                .profit-label {
                  font-size: 15px;
                }
              }

              .refresh-btn {
                padding: 4px;
                font-size: 12px;
                margin-left: 8px;
                height: 24px;
                width: 24px;
              }
            }
          }
        }
      }
    }
  }

  // 暗色主题支持
  @media (prefers-color-scheme: dark) {
    .search-card {
      background: #1f2937;

      .search-group {
        .right-section {
          border-left-color: #374151;

          @media (max-width: 768px) {
            border-top-color: #374151;
          }
        }
      }
    }

    ::v-deep .el-input__inner,
    ::v-deep .el-date-editor .el-input__inner {
      background: #374151;
      border-color: #4b5563;
      color: #e5e7eb;

      &::placeholder {
        color: #9ca3af;
      }
    }

    .date-separator {
      color: #9ca3af !important;
    }
  }

  // 修改操作按钮组样式
  .mb8 {
    display: flex;
    align-items: center;
    flex-wrap: wrap;
    gap: 8px;
    margin-bottom: 16px;

    .el-button {
      margin: 0; // 移除默认边距
      padding: 8px 16px;
      height: 32px;
      line-height: 1;
      
      &.is-plain {
        &:hover {
          color: var(--el-color-primary);
          border-color: var(--el-color-primary);
          background-color: var(--el-color-primary-light-9);
        }
      }
    }

    .el-col {
      padding: 0 5px;
      margin-bottom: 8px;
    }
  }
}
</style>
