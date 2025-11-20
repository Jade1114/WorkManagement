<template>
    <el-aside class="sidebar">
        <!-- 登录成功后显示的菜单将在这里放置 -->
    </el-aside>
</template>

<script setup>
// 暂时空组件，后续功能需要时再添加
</script>

<style scoped>
.sidebar {
    background-color: #f5f7fa;
    border-right: 1px solid #dcdfe6;
    width: 200px !important;
    height: calc(100% - 60px);
    overflow-y: auto;
}

.el-menu {
    border-right: none;
}

/* 隐藏菜单滚动条 */
.sidebar::-webkit-scrollbar {
    width: 6px;
}

.sidebar::-webkit-scrollbar-track {
    background: #f5f7fa;
}

.sidebar::-webkit-scrollbar-thumb {
    background: #bfcdc9;
    border-radius: 3px;
}
</style>