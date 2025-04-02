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
      <el-form-item label="物流公司名称" prop="logisticsCompany">
        <el-select
          v-model="queryParams.logisticsCompany"
          placeholder="请选择物流公司名称"
          clearable
        >
          <el-option
            v-for="dict in dict.type.tb_name"
            :key="dict.value"
            :label="dict.label"
            :value="dict.value"
          />
        </el-select>
      </el-form-item>
      <el-form-item label="完整运单号" prop="fullTrackingNumber">
        <el-input
          v-model="queryParams.fullTrackingNumber"
          placeholder="请输入完整运单号"
          clearable
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item label="花费金额" prop="costAmount">
        <el-input
          v-model="queryParams.costAmount"
          placeholder="请输入花费金额"
          clearable
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item label="修改时间" prop="dateTime">
        <el-date-picker
          clearable
          v-model="queryParams.dateTime"
          type="date"
          value-format="yyyy-MM-dd"
          placeholder="请选择修改时间"
        >
        </el-date-picker>
      </el-form-item>
      <el-form-item label="发货日期" prop="shippingDate">
        <el-date-picker
          clearable
          v-model="queryParams.shippingDate"
          type="date"
          value-format="yyyy-MM-dd"
          placeholder="请选择发货日期"
        >
        </el-date-picker>
      </el-form-item>
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
      <right-toolbar
        :showSearch.sync="showSearch"
        @queryTable="getList"
      ></right-toolbar>
    </el-row>
    <el-table
      v-loading="loading"
      :data="costList"
      @selection-change="handleSelectionChange"
    >
      <el-table-column type="selection" width="55" align="center" />
      <el-table-column label="邮费主键ID" align="center" prop="id" />
      <el-table-column
        label="物流公司名称"
        align="center"
        prop="logisticsCompany"
      >
        <template slot-scope="scope">
          <dict-tag
            :options="dict.type.tb_name"
            :value="scope.row.logisticsCompany"
          />
        </template>
      </el-table-column>
      <el-table-column
        label="完整运单号"
        align="center"
        prop="fullTrackingNumber"
      />
      <el-table-column label="物流状态" align="center" prop="logisticsStatus" />
      <el-table-column
        label="物流轨迹"
        align="center"
        prop="logisticsTraces"
        :show-overflow-tooltip="true"
      />
      <el-table-column label="花费金额" align="center" prop="costAmount" />
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
        label="修改时间"
        align="center"
        prop="dateTime"
        width="180"
      >
        <template slot-scope="scope">
          <span>{{ parseTime(scope.row.dateTime, "{y}-{m}-{d}") }}</span>
        </template>
      </el-table-column>
      <el-table-column
        label="发货日期"
        align="center"
        prop="shippingDate"
        width="180"
      >
        <template slot-scope="scope">
          <span>{{ parseTime(scope.row.shippingDate, "{y}-{m}-{d}") }}</span>
        </template>
      </el-table-column>
      <el-table-column
        label="收件详细地址"
        align="center"
        prop="recipientAddress"
      />
      <el-table-column label="包裹重量(kg)" align="center" prop="weight" />
      <el-table-column
        label="运单图片"
        align="center"
        prop="receiptImageUrl"
        width="100"
      >
        <template slot-scope="scope">
          <image-preview
            :src="scope.row.receiptImageUrl"
            :width="50"
            :height="50"
          />
        </template>
      </el-table-column>
      <el-table-column
        label="图片上传时间"
        align="center"
        prop="imageUploadTime"
        width="180"
      >
        <template slot-scope="scope">
          <span>{{
            parseTime(scope.row.imageUploadTime, "{y}-{m}-{d} {h}:{i}:{s}")
          }}</span>
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
        <el-form-item label="运单图片" prop="receiptImageUrl">
          <image-upload
            v-model="form.receiptImageUrl"
            @image-uploaded="ImageUploaded"
            @change="handleImageChange"
          />
        </el-form-item>
        <el-form-item label="完整运单号" prop="fullTrackingNumber">
          <el-input
            v-model="form.fullTrackingNumber"
            placeholder="请输入完整运单号"
          >
            <el-button slot="append" @click="handleQueryLogistics"
              >查询物流</el-button
            >
          </el-input>
        </el-form-item>
        <el-form-item label="顺丰尾号" prop="lastFourDigits" v-if="isSFExpress">
          <el-input
            v-model="form.lastFourDigits"
            placeholder="请输入收件人手机号后四位"
            maxlength="4"
            show-word-limit
          />
        </el-form-item>
        <el-form-item label="物流状态" prop="logisticsStatus">
          <el-input
            v-model="form.logisticsStatus"
            placeholder="物流状态"
            readonly
          />
        </el-form-item>
        <el-form-item label="物流轨迹" prop="logisticsTraces">
          <el-input
            type="textarea"
            v-model="form.logisticsTraces"
            placeholder="物流轨迹"
            readonly
            :rows="3"
          />
        </el-form-item>
        <el-form-item label="快递公司" prop="expName">
          <el-input v-model="form.expName" placeholder="快递公司" readonly />
        </el-form-item>
        <el-form-item label="包裹重量(kg)" prop="weight">
          <el-input v-model="form.weight" placeholder="请输入包裹重量(kg)" />
        </el-form-item>
        <el-form-item label="花费金额" prop="costAmount">
          <el-input v-model="form.costAmount" placeholder="请输入花费金额" />
        </el-form-item>
        <el-form-item label="发货日期" prop="shippingDate">
          <el-date-picker
            clearable
            v-model="form.shippingDate"
            type="date"
            value-format="yyyy-MM-dd"
            placeholder="请选择发货日期"
          >
          </el-date-picker>
        </el-form-item>
        <el-form-item label="收件详细地址" prop="recipientAddress">
          <el-input
            v-model="form.recipientAddress"
            type="textarea"
            placeholder="请输入内容"
          />
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
  queryLogisticsInfo,
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
        logisticsCompany: null,
        fullTrackingNumber: null,
        costAmount: null,
        dateTime: null,
        shippingDate: null,
        recipientAddress: null,
      },
      // 表单参数
      form: {
        id: null,
        userId: null,
        deptId: null,
        logisticsCompany: null,
        lastFourDigits: null,
        fullTrackingNumber: null,
        costAmount: null,
        createTime: null,
        shippingDate: null,
        recipientProvince: null,
        recipientCity: null,
        recipientDistrict: null,
        recipientAddress: null,
        weight: null,
        receiptImageUrl: null,
        imageUploadTime: null,
        logisticsStatus: null,
        logisticsTraces: null,
        expName: null,
      },
      // 表单校验
      rules: {
        fullTrackingNumber: [
          { required: true, message: "完整运单号不能为空", trigger: "blur" },
        ],
        costAmount: [
          { required: true, message: "花费金额不能为空", trigger: "blur" },
        ],
        createTime: [
          { required: true, message: "创建时间不能为空", trigger: "blur" },
        ],
      },
      // 是否为顺丰快递
      isSFExpress: false,
      isOcrProcessing: false,
    };
  },
  watch: {
    "form.logisticsCompany": {
      handler(newVal) {
        // 检查是否为顺丰快递
        this.isSFExpress = newVal === "SF";
      },
      immediate: true,
    },
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
        userId: null,
        deptId: null,
        logisticsCompany: null,
        lastFourDigits: null,
        fullTrackingNumber: null,
        costAmount: null,
        createTime: null,
        shippingDate: null,
        recipientProvince: null,
        recipientCity: null,
        recipientDistrict: null,
        recipientAddress: null,
        weight: null,
        receiptImageUrl: null,
        imageUploadTime: null,
        logisticsStatus: null,
        logisticsTraces: null,
        expName: null,
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
    /** 查询物流信息 */
    handleQueryLogistics() {
      if (!this.form.fullTrackingNumber) {
        this.$modal.msgError("请输入运单号");
        return;
      }

      // 处理顺丰快递的特殊情况
      let trackingNumber = this.form.fullTrackingNumber;
      if (this.isSFExpress) {
        if (!this.form.lastFourDigits) {
          this.$modal.msgError("顺丰快递请输入收件人手机号后四位");
          return;
        }
        if (!/^\d{4}$/.test(this.form.lastFourDigits)) {
          this.$modal.msgError("请输入正确的手机号后四位");
          return;
        }
        trackingNumber = `${trackingNumber}:${this.form.lastFourDigits}`;
      }

      this.loading = true;
      queryLogisticsInfo(trackingNumber)
        .then((response) => {
          if (response.status === "0") {
            const result = response.result;

            // 更新物流状态
            let statusText = "";
            switch (result.deliverystatus) {
              case "0":
                statusText = "运输中";
                break;
              case "1":
                statusText = "已揽收";
                break;
              case "2":
                statusText = "疑难";
                break;
              case "3":
                statusText = "已签收";
                break;
              case "4":
                statusText = "已退签";
                break;
              case "5":
                statusText = "派送中";
                break;
              case "6":
                statusText = "退回中";
                break;
              default:
                statusText = "未知状态";
            }
            this.form.logisticsStatus = statusText;

            // 格式化物流轨迹
            if (result.list && result.list.length > 0) {
              const traces = result.list.map((item) => {
                return `${item.time}\n${item.status}`;
              });
              this.form.logisticsTraces = traces.join("\n\n");
            }

            // 更新快递公司信息
            this.form.expName = result.expName;
            // 根据快递公司类型自动设置物流公司
            this.form.logisticsCompany = result.type;

            this.$modal.msgSuccess("物流信息查询成功");
          } else {
            this.$modal.msgError(response.msg || "物流信息查询失败");
          }
        })
        .catch((error) => {
          console.error("查询物流信息失败：", error);
          this.$modal.msgError("物流信息查询失败");
        })
        .finally(() => {
          this.loading = false;
        });
    },
    /** 处理图片变化事件 */
    handleImageChange(file) {
      if (!file) return;

      console.log("开始处理图片上传，文件名：", file.name);
      console.log("文件类型：", file.type);
      console.log("文件大小：", file.size);

      // 创建FormData对象
      const formData = new FormData();
      formData.append("file", file);

      console.log("准备发送请求到后端...");

      // 调用通用上传接口
      this.$http
        .post("/common/upload", formData, {
          headers: {
            "Content-Type": "multipart/form-data",
          },
        })
        .then((response) => {
          console.log("收到后端响应：", response);
          if (response.code === 200) {
            // 保存图片URL
            this.form.receiptImageUrl = response.data.url;
            this.form.imageUploadTime = new Date();
            this.$modal.msgSuccess("图片上传成功");
          } else {
            this.$modal.msgError(response.msg || "图片上传失败");
          }
        })
        .catch((error) => {
          console.error("处理图片失败：", error);
          console.error("错误详情：", error.response || error);
          this.$modal.msgError("图片上传失败");
        });
    },
    /** 处理图片上传完成事件 */
    handleImageUploaded(imageUrl) {
      if (!imageUrl) return;
      this.form.receiptImageUrl = imageUrl; // 保存图片URL
      this.form.imageUploadTime = new Date(); // 修改为当前时间
    },
  },
};
</script>
