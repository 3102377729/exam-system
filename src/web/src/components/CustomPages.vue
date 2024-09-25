<template>
    <div>
        <el-pagination
              @current-change="currentChange"
              @size-change="sizeChange"
              :page-sizes= "pageSizes"
              :page-size="pageSize"
              :current-page="currentPage"
              background
              layout="sizes, prev, pager, next"
              :total="total">
        </el-pagination>
    </div>
</template>

<script>
export default {
  name: "CustomPages",
  props:{
    total:{
      type:[String,Number],
      default: 0
    },
    pageSizes: {
      type: Array,
      default: function (){
        return [10, 20, 30]
      }
    },
    pageSize:{
      type: [String,Number],
      default: 10
    },
    currentPage:{
      type: [Number],
      default: 1
    }
  },
  methods:{
    currentChange(e){
      //自定义事件
      this.$emit("current-change",e)
    },
    sizeChange(e){
      this.$emit("size-change",e)
    }
  }
}
</script>

<style scoped>

</style>
