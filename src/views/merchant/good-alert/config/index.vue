<template>
    <div class="good-alert-config">
      <!-- 搜索栏 -->
      <el-card class="search-card">
        <el-form :inline="true" :model="queryParams">
          <el-form-item label="商品名称">
            <el-input v-model="queryParams.goodName" placeholder="请输入商品名称" clearable></el-input>
          </el-form-item>
          <el-form-item label="状态">
            <el-select v-model="queryParams.enabled" placeholder="请选择状态" clearable>
              <el-option label="启用" value="1"></el-option>
              <el-option label="禁用" value="0"></el-option>
            </el-select>
          </el-form-item>
          <el-form-item>
            <el-button type="primary" @click="handleQuery">搜索</el-button>
            <el-button @click="resetQuery">重置</el-button>
          </el-form-item>
        </el-form>
      </el-card>
  
      <!-- 操作按钮 -->
      <el-card class="toolbar-card">
        <el-button type="primary" @click="handleAdd">新增预警</el-button>
        <el-button type="success" @click="handleExport">导出</el-button>
      </el-card>
  
      <!-- 预警配置表格 -->
      <el-card>
        <el-table :data="configList" border stripe>
          <el-table-column label="商品名称" prop="goodName"></el-table-column>
          <el-table-column label="价格波动阈值">
            <template slot-scope="scope">
              {{ scope.row.priceChangeThreshold }}%
            </template>
          </el-table-column>
          <el-table-column label="利润率阈值">
            <template slot-scope="scope">
              {{ scope.row.profitThreshold }}%
            </template>
          </el-table-column>
          <el-table-column label="销售异常阈值">
            <template slot-scope="scope">
              {{ scope.row.salesAbnormalThreshold }}天
            </template>
          </el-table-column>
          <el-table-column label="状态">
            <template slot-scope="scope">
              <el-tag :type="scope.row.enabled === '1' ? 'success' : 'info'">
                {{ scope.row.enabled === '1' ? '启用' : '禁用' }}
              </el-tag>
            </template>
          </el-table-column>
          <el-table-column label="操作" width="200" fixed="right">
            <template slot-scope="scope">
              <el-button type="primary" size="mini" @click="handleEdit(scope.row)">编辑</el-button>
              <el-button type="danger" size="mini" @click="handleDelete(scope.row)">删除</el-button>
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
  
      <!-- 新增/编辑弹窗 -->
      <el-dialog
        :title="form.id ? '编辑预警' : '新增预警'"
        :visible.sync="dialogVisible"
        width="500px">
        <el-form :model="form" label-width="100px">
          <el-form-item label="商品" required>
            <el-select
              v-model="form.goodId"
              filterable
              remote
              :remote-method="handleGoodSearch"
              placeholder="请选择商品"
              style="width: 100%">
              <el-option
                v-for="item in goodOptions"
                :key="item.id"
                :label="item.name"
                :value="item.id">
              </el-option>
            </el-select>
          </el-form-item>
          <el-form-item label="价格波动阈值">
            <el-input-number
              v-model="form.priceChangeThreshold"
              :min="0"
              :max="100"
              :precision="2"
              style="width: 100%">
              <template slot="suffix">%</template>
            </el-input-number>
          </el-form-item>
          <el-form-item label="利润率阈值">
            <el-input-number
              v-model="form.profitThreshold"
              :min="0"
              :max="100"
              :precision="2"
              style="width: 100%">
              <template slot="suffix">%</template>
            </el-input-number>
          </el-form-item>
          <el-form-item label="销售异常阈值">
            <el-input-number
              v-model="form.salesAbnormalThreshold"
              :min="1"
              :precision="0"
              style="width: 100%">
              <template slot="suffix">天</template>
            </el-input-number>
          </el-form-item>
          <el-form-item label="状态">
            <el-switch
              v-model="form.enabled"
              :active-value="'1'"
              :inactive-value="'0'">
            </el-switch>
          </el-form-item>
        </el-form>
        <div slot="footer" class="dialog-footer">
          <el-button @click="dialogVisible = false">取消</el-button>
          <el-button type="primary" @click="handleSubmit">确定</el-button>
        </div>
      </el-dialog>
    </div>
  </template>
  
  <script>
  export default {
    name: 'GoodAlertConfig',
    data() {
      return {
        // 查询参数
        queryParams: {
          goodName: undefined,
          enabled: undefined,
          pageNum: 1,
          pageSize: 10
        },
        // 预警配置列表
        configList: [],
        total: 0,
        // 新增/编辑表单
        dialogVisible: false,
        form: {
          id: undefined,
          goodId: undefined,
          priceChangeThreshold: undefined,
          profitThreshold: undefined,
          salesAbnormalThreshold: undefined,
          enabled: '1'
        },
        // 商品选项
        goodOptions: []
      }
    },
    created() {
      this.getList()
    },
    methods: {
      // 获取预警配置列表
      async getList() {
        const res = await this.$api.good.alert.getConfigList(this.queryParams)
        this.configList = res.rows
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
        this.queryParams.enabled = undefined
        this.handleQuery()
      },
      // 新增按钮
      handleAdd() {
        this.dialogVisible = true
        this.form = {
          id: undefined,
          goodId: undefined,
          priceChangeThreshold: undefined,
          profitThreshold: undefined,
          salesAbnormalThreshold: undefined,
          enabled: '1'
        }
      },
      // 编辑按钮
      handleEdit(row) {
        this.dialogVisible = true
        this.form = Object.assign({}, row)
      },
      // 删除按钮
      handleDelete(row) {
        this.$confirm('确认要删除该预警配置吗？', '警告', {
          type: 'warning'
        }).then(async () => {
          await this.$api.good.alert.deleteConfig([row.id])
          this.$message.success('删除成功')
          this.getList()
        })
      },
      // 提交表单
      async handleSubmit() {
        if (this.form.id) {
          await this.$api.good.alert.updateConfig(this.form)
          this.$message.success('修改成功')
        } else {
          await this.$api.good.alert.addConfig(this.form)
          this.$message.success('新增成功')
        }
        this.dialogVisible = false
        this.getList()
      },
      // 商品搜索
      async handleGoodSearch(query) {
        if (query) {
          const res = await this.$api.good.search({ name: query })
          this.goodOptions = res.data
        }
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
  .good-alert-config {
    padding: 20px;
  }
  .search-card {
    margin-bottom: 20px;
  }
  .toolbar-card {
    margin-bottom: 20px;
  }
  </style>