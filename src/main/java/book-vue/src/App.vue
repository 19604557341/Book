<template>
  <div class="d">
    <el-container>
      <el-header v-if="showSidebar">
        <div class="user">
          <span>{{ loginName }}</span>
          <el-button @click="logout" class="logout">退出</el-button>
        </div>
      </el-header>
      <el-container>
        <el-aside width="200px" v-if="showSidebar">
          <el-col>
            <h5>图书管理系统</h5>
            <el-menu
              router
              :default-active="$route.path"
              background-color="#66b1ff"
              active-text-color="#fff"
            >
              <div v-for="item in menuList" :key="item.id">
                <el-menu-item :key="item.id" :index="item.url">
                  <el-icon>
                    <component :is="item.icon" />
                  </el-icon>
                  <span>{{ item.name }}</span>
                </el-menu-item>
              </div>
            </el-menu>
          </el-col>
        </el-aside>
        <el-container>
          <el-main style="padding: 0%;">
            <router-view></router-view>
          </el-main>
        </el-container>
      </el-container>
    </el-container>
  </div>
</template>

<script setup>
import { onMounted, ref, watch } from "vue";
import { useRoute, useRouter } from "vue-router";

const loginName = ref("");
const showSidebar = ref("");
const route = useRoute();
const router = useRouter();
const path = ref(route.path);

const menuList = [
  {
    id: 1,
    name: "用户管理",
    url: "/user",
    icon: "UserFilled"
  },
  {
    id: 2,
    name: "图书管理",
    url: "/book",
    icon: "Reading"
  },
  {
    id: 3,
    name: "分类管理",
    url: "/category",
    icon: "Management"
  },
  {
    id: 4,
    name: "订单管理",
    url: "/order",
    icon: "List"
  }
];

onMounted(() => {
  loginName.value = localStorage.getItem("loginName");
});

watch(path, (newPath, oldPath) => {
  console.log(newPath, oldPath);
  myMethod(newPath);
});

watch(
  () => route.path,
  newPath => {
    path.value = newPath;
  }
);

const myMethod = newPath => {
  loginName.value = localStorage.getItem("loginName");
  showSidebar.value = newPath !== "/login";
};

const logout = () => {
  localStorage.setItem("token", "");
  router.push('/login');
};
</script>


<style>
body {
  margin: 0;
  height: 100vh;
}

.d {
  height: 100%;
}

#app {
  height: 100%;
}

.el-container {
  height: 100%;
}

.el-header {
  background: #2a598a;
  color: var(--el-text-color-primary);
}

.el-aside {
  text-align: center;
  color: var(--el-text-color-primary);
  background: #66b1ff;
}

.user {
  float: right;
  margin: 10px;
  border: 0;
  color: #fff;
  line-height: 2;
}

.logout {
  margin-left: 20px;
}
</style>
