<template>
    <div class="app-container">
      <el-form :model="queryParams" ref="queryForm" :inline="true" v-show="showSearch" label-width="68px">
        <el-form-item label="模板编码" prop="templateCode">
          <el-input
            v-model="queryParams.templateCode"
            placeholder="请输入模板编码"
            clearable
            size="small"
            @keyup.enter.native="handleQuery"
          />
        </el-form-item>
        <el-form-item label="模板名称" prop="templateName">
          <el-input
            v-model="queryParams.templateName"
            placeholder="请输入模板名称"
            clearable
            size="small"
            @keyup.enter.native="handleQuery"
          />
        </el-form-item>
        <el-form-item label="模板类型" prop="templateType">
          <el-select v-model="queryParams.templateType" placeholder="请选择模板类型" clearable size="small">
            <el-option
              v-for="dict in templateTypeOptions"
              :key="dict.dictValue"
              :label="dict.dictLabel"
              :value="dict.dictValue"
            />
          </el-select>
        </el-form-item>
        <el-form-item label="状态" prop="status">
          <el-select v-model="queryParams.status" placeholder="请选择状态" clearable size="small">
            <el-option
              v-for="dict in statusOptions"
              :key="dict.dictValue"
              :label="dict.dictLabel"
              :value="dict.dictValue"
            />
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" icon="el-icon-search" size="mini" @click="handleQuery">搜索</el-button>
          <el-button icon="el-icon-refresh" size="mini" @click="resetQuery">重置</el-button>
        </el-form-item>
      </el-form>
  
      <el-row :gutter="10" class="mb8">
        <el-col :span="1.5">
          <el-button
            type="primary"
            plain
            icon="el-icon-plus"
            size="mini"
            @click="handleAdd"
            v-hasPermi="['system:message:add']"
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
            v-hasPermi="['system:message:edit']"
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
            v-hasPermi="['system:message:remove']"
          >删除</el-button>
        </el-col>
        <el-col :span="1.5">
          <el-button
            type="warning"
            plain
            icon="el-icon-download"
            size="mini"
            @click="handleExport"
            v-hasPermi="['system:message:export']"
          >导出</el-button>
        </el-col>
        <right-toolbar :showSearch.sync="showSearch" @queryTable="getList"></right-toolbar>
      </el-row>
  
      <el-table v-loading="loading" :data="templateList" @selection-change="handleSelectionChange">
        <el-table-column type="selection" width="55" align="center" />
        <el-table-column label="模板ID" align="center" prop="templateId" />
        <el-table-column label="模板编码" align="center" prop="templateCode" />
        <el-table-column label="模板名称" align="center" prop="templateName" />
        <el-table-column label="模板类型" align="center" prop="templateType">
          <template slot-scope="scope">
            <dict-tag :options="templateTypeOptions" :value="scope.row.templateType"/>
          </template>
        </el-table-column>
        <el-table-column label="状态" align="center" prop="status">
          <template slot-scope="scope">
            <dict-tag :options="statusOptions" :value="scope.row.status"/>
          </template>
        </el-table-column>
        <el-table-column label="创建时间" align="center" prop="createTime" width="180">
          <template slot-scope="scope">
            <span>{{ parseTime(scope.row.createTime) }}</span>
          </template>
        </el-table-column>
        <el-table-column label="操作" align="center" class-name="small-padding fixed-width">
          <template slot-scope="scope">
            <el-button
              size="mini"
              type="text"
              icon="el-icon-edit"
              @click="handleUpdate(scope.row)"
              v-hasPermi="['system:message:edit']"
            >修改</el-button>
            <el-button
              size="mini"
              type="text"
              icon="el-icon-delete"
              @click="handleDelete(scope.row)"
              v-hasPermi="['system:message:remove']"
            >删除</el-button>
          </template>
        </el-table-column>
      </el-table>
      
      <pagination
        v-show="total>0"
        :total="total"
        :page.sync="queryParams.pageNum"
        :limit.sync="queryParams.pageSize"
        @pagination="getList"
      />
  
      <!-- 添加或修改消息模板对话框 -->
      <el-dialog :title="title" :visible.sync="open" width="500px" append-to-body>
        <el-form ref="form" :model="form" :rules="rules" label-width="80px">
          <el-form-item label="模板编码" prop="templateCode">
            <el-input v-model="form.templateCode" placeholder="请输入模板编码" />
          </el-form-item>
          <el-form-item label="模板名称" prop="templateName">
            <el-input v-model="form.templateName" placeholder="请输入模板名称" />
          </el-form-item>
          <el-form-item label="模板类型" prop="templateType">
            <el-select v-model="form.templateType" placeholder="请选择模板类型">
              <el-option
                v-for="dict in templateTypeOptions"
                :key="dict.dictValue"
                :label="dict.dictLabel"
                :value="dict.dictValue"
              ></el-option>
            </el-select>
          </el-form-item>
          <el-form-item label="模板内容" prop="templateContent">
            <el-input type="textarea" v-model="form.templateContent" placeholder="请输入模板内容" />
          </el-form-item>
          <el-form-item label="状态" prop="status">
            <el-radio-group v-model="form.status">
              <el-radio
                v-for="dict in statusOptions"
                :key="dict.dictValue"
                :label="dict.dictValue"
              >{{dict.dictLabel}}</el-radio>
            </el-radio-group>
          </el-form-item>
          <el-form-item label="备注" prop="remark">
            <el-input type="textarea" v-model="form.remark" placeholder="请输入备注" />
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
  import { listTemplate, getTemplate, delTemplate, addTemplate, updateTemplate } from "@/api/system/message";
  
  export default {
    name: "MessageTemplate",
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
        // 消息模板表格数据
        templateList: [],
        // 弹出层标题
        title: "",
        // 是否显示弹出层
        open: false,
        // 模板类型选项
        templateTypeOptions: [],
        // 状态选项
        statusOptions: [],
        // 查询参数
        queryParams: {
          pageNum: 1,
          pageSize: 10,
          templateCode: null,
          templateName: null,
          templateType: null,
          status: null
        },
        // 表单参数
        form: {},
        // 表单校验
        rules: {
          templateCode: [
            { required: true, message: "模板编码不能为空", trigger: "blur" }
          ],
          templateName: [
            { required: true, message: "模板名称不能为空", trigger: "blur" }
          ],
          templateType: [
            { required: true, message: "模板类型不能为空", trigger: "change" }
          ],
          templateContent: [
            { required: true, message: "模板内容不能为空", trigger: "blur" }
          ]
        }
      };
    },
    created() {
      this.getList();
      this.getDicts("sys_message_type").then(response => {
        this.templateTypeOptions = response.data;
      });
      this.getDicts("sys_normal_disable").then(response => {
        this.statusOptions = response.data;
      });
    },
    methods: {
      /** 查询消息模板列表 */
      getList() {
        this.loading = true;
        listTemplate(this.queryParams).then(response => {
          this.templateList = response.rows;
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
          templateId: null,
          templateCode: null,
          templateName: null,
          templateType: null,
          templateContent: null,
          status: "0",
          remark: null
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
        this.ids = selection.map(item => item.templateId)
        this.single = selection.length!==1
        this.multiple = !selection.length
      },
      /** 新增按钮操作 */
      handleAdd() {
        this.reset();
        this.open = true;
        this.title = "添加消息模板";
      },
      /** 修改按钮操作 */
      handleUpdate(row) {
        this.reset();
        const templateId = row.templateId || this.ids
        getTemplate(templateId).then(response => {
          this.form = response.data;
          this.open = true;
          this.title = "修改消息模板";
        });
      },
      /** 提交按钮 */
      submitForm() {
        this.$refs["form"].validate(valid => {
          if (valid) {
            if (this.form.templateId != null) {
              updateTemplate(this.form).then(response => {
                this.$modal.msgSuccess("修改成功");
                this.open = false;
                this.getList();
              });
            } else {
              addTemplate(this.form).then(response => {
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
        const templateIds = row.templateId || this.ids;
        this.$modal.confirm('是否确认删除消息模板编号为"' + templateIds + '"的数据项？').then(function() {
          return delTemplate(templateIds);
        }).then(() => {
          this.getList();
          this.$modal.msgSuccess("删除成功");
        }).catch(() => {});
      },
      /** 导出按钮操作 */
      handleExport() {
        this.download('system/message/template/export', {
          ...this.queryParams
        }, `message_template_${new Date().getTime()}.xlsx`)
      }
    }
  };
  </script>