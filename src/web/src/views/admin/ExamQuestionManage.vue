<template>
  <div>
    <div class="pb10px">
      <el-form ref="searchForm" :model="searchForm" inline>
        <el-form-item label="题目" prop="name">
          <el-input v-model="searchForm.name" clearable placeholder="请输入题目"></el-input>
        </el-form-item>
        <el-form-item label="">
          <el-button type="primary" icon="el-icon-search" @click="handleSearch">搜 索</el-button>
          <el-button icon="el-icon-refresh-left" @click="resetForm">重 置</el-button>
        </el-form-item>
      </el-form>
    </div>
    <el-button type="primary" icon="el-icon-plus" @click="handleAdd">新增</el-button>
    <el-button type="danger" icon="el-icon-remove-outline" @click="batchDelete" :disabled="ids.length<=0">批量删除</el-button>
    <el-table ref="multipleTable" :data="listData" tooltip-effect="dark" style="width: 100%">
      <el-table-column type="selection" width="55"></el-table-column>
      <el-table-column prop="id" label="ID" width="50"></el-table-column>
      <el-table-column prop="name" label="题目"></el-table-column>
      <el-table-column prop="type" label="题型"></el-table-column>
      <el-table-column prop="options" label="选择题选项"></el-table-column>
      <el-table-column prop="answer" label="正确答案"></el-table-column>
      <el-table-column prop="score" label="分值"></el-table-column>
      <el-table-column prop="user" label="用户名"></el-table-column>
      <el-table-column label="操作" width="200" fixed="right">
        <template slot-scope="scope">
          <el-button icon="el-icon-edit-outline" @click="handleEdit(scope.$index, scope.row)">编辑</el-button>
          <el-button type="danger" icon="el-icon-remove-outline"
            @click="handleDelete(scope.$index, scope.row)">删除</el-button>
        </template>
      </el-table-column>
    </el-table>

    <div style="margin-top: 20px">
      <el-pagination @current-change="currentChange" @size-change="sizeChange" :page-size="pageInfo.pageSize"
        :current-page="pageInfo.currentPage" background layout="sizes, prev, pager, next" :total="total">
      </el-pagination>
    </div>
    <el-drawer :title="formData.id?'编辑':'新增'" :visible.sync="isDrawerDialog" size="400px" direction="rtl"
      :wrapper-closable="false">
      <div class="demo-drawer_content" style="padding-right: 40px;box-sizing: border-box">
        <el-form :model="formData" label-width="100px" class="demo-ruleForm">
          <slot name="content">
            <el-form-item label="题目">
              <el-input v-model="formData.name"></el-input>
            </el-form-item>
            <el-form-item label="题型">
              <el-select clearable v-model="formData.type" placeholder="请选择" style="width: 100%"
                :disabled="formData.id">
                <el-option v-for="item in questionType" :key="item" :label="item" :value="item"></el-option>
              </el-select>
            </el-form-item>
            <el-form-item label="选择题选项" v-if="formData.type==='单选题'||formData.type==='多选题'">
              <el-input v-model="selectList[i]" v-for="(item,i) in selectList" :key="item"></el-input>
            </el-form-item>
            <el-form-item label="正确答案">
              <el-input type="textarea" :rows="7" v-model="formData.answer"></el-input>
            </el-form-item>
            <el-form-item label="分值">
              <el-input type="number" v-model="formData.score"></el-input>
            </el-form-item>
          </slot>
        </el-form>
      </div>
      <div class="demo-drawer_footer" style="display: flex; justify-content: space-around;">
        <el-button @click="cancel" icon="el-icon-close">取 消</el-button>
        <el-button type="primary" @click="submit">提交</el-button>
      </div>
    </el-drawer>
  </div>
</template>

<script>
import utils from "@/utils/utils";

export default {
  data() {
    return {
      //抽屉控制
      isDrawerDialog: false,
      //列表数据
      listData: [],
      //总条数
      total: 0,
      //分页详情
      pageInfo: {
        //当前页
        pageNum: 1,
        //分页大小
        pageSize: 10
      },
      //搜索
      searchForm: {
        name: ""
      },
      formData: {},
      //删除数据的id
      ids: [],
      userInfo:utils.getUserInfo(),
        questionType:[
            "单选题",
            "多选题",
            "判断题",
            "填空题",
            "主观题"
        ],
        selectList:[
            "A：",
            "B：",
            "C：",
            "D：",
        ],
        defaultList:[
            "A：",
            "B：",
            "C：",
            "D：",
        ],
    }
  },

  created() {
    this.getPageList()
  },
  methods: {
    //获取分页数据
    getPageList() {
      let data = Object.assign(this.searchForm, this.pageInfo)
      this.$axios.get("/examQuestion/page", {
        params: data
      }).then(res => {
        this.listData = res.data.list
        this.total = res.data.total
      })
    },
    resetForm() {
      this.$refs['searchForm'].resetFields();
    },
    //选择分页
    currentChange(e) {
      this.pageInfo.pageNum = e
      this.getPageList()
    },
    //改变分页数量
    sizeChange(e) {
      this.pageInfo.pageSize = e
      this.getPageList()
    },
    //搜索
    handleSearch() {
      this.pageInfo.pageNum = 1
      this.getPageList()
    },
    cancel() {
      this.isDrawerDialog = false
      this.selectList = this.defaultList
    },
    //提交数据
    submit() {
      //新增
      if (!this.formData.id) {
          //单选题和多选题才能有进行配置
          if(this.formData.type==='单选题'||this.formData.type==='多选题'){
              this.formData.options=JSON.stringify(this.selectList)
          }
        this.$axios.post("/examQuestion/add", this.formData).then(res => {
          if (res.code === 200) {
            this.isDrawerDialog = false
            this.$message({
              message: res.msg,
              type: 'success'
            });
            this.getPageList()
          }
        })
      } else {
        //更新
        this.$axios.put("/examQuestion/update", this.formData).then(res => {
          if (res.code === 200) {
            this.isDrawerDialog = false
            this.$message({
              message: res.msg,
              type: 'success'
            });
            this.getPageList()
          }
        })
      }
      this.selectList = this.defaultList
    },
    //添加数据
    handleAdd() {
      this.formData = {}
      this.isDrawerDialog = true
    },
    //编辑
    handleEdit(index, row) {
      this.formData = Object.assign({}, row)
        if(this.formData.type==='单选题'||this.formData.type==='多选题'){
            this.selectList=JSON.parse(this.formData.options)
        }
      this.isDrawerDialog = true
    },
    //单个删除
    handleDelete(index, row) {
      this.batchDelete([row])
    },
    //批量删除
    batchDelete(rows) {
      if (!rows) {
        rows = this.$refs.multipleTable.selection;
      }
      let ids = rows.map(item => item.id);
      this.$confirm(`此操作将永久删除ID为[${ids}]的数据, 是否继续?`, '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning',
        center: true
      }).then(() => {
        //批量删除数据
        this.$axios.delete("/examQuestion/delBatch", {data: ids}).then(res => {
          if (res.code === 200) {
            this.$message({
              message: res.msg,
              type: 'success'
            });
            this.getPageList()
          }
        })
      }).catch(() => {
        this.$message({
          type: 'info',
          message: '已取消删除'
        });
        this.$refs.multipleTable.clearSelection();
      });
    },

  }
};
</script>


