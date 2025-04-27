<template>
    <div class="app-container">
      <el-row :gutter="20">
        <el-col :span="6">
          <el-card class="box-card">
            <div slot="header" class="clearfix">
              <span>消息管理</span>
            </div>
            <el-menu
              :default-active="activeMenu"
              class="el-menu-vertical"
              @select="handleSelect"
            >
              <el-menu-item index="log">
                <i class="el-icon-document"></i>
                <span slot="title">消息日志</span>
              </el-menu-item>
              <el-menu-item index="template">
                <i class="el-icon-edit"></i>
                <span slot="title">消息模板</span>
              </el-menu-item>
            </el-menu>
          </el-card>
        </el-col>
        <el-col :span="18">
          <router-view></router-view>
        </el-col>
      </el-row>
    </div>
  </template>
  
  <script>
  export default {
    name: 'Message',
    data() {
      return {
        activeMenu: 'log'
      }
    },
    created() {
      // 设置默认激活的菜单
      const path = this.$route.path
      if (path.includes('/template')) {
        this.activeMenu = 'template'
      } else {
        this.activeMenu = 'log'
      }
    },
    methods: {
      handleSelect(key) {
        this.$router.push(`/system/message/${key}`)
      }
    }
  }
  </script>
  
  <style scoped>
  .el-menu-vertical {
    border-right: none;
  }
  .box-card {
    margin-bottom: 20px;
  }
  </style>