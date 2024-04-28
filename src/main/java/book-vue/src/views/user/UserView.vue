<template>
  <div>
    <div class="input">
      <el-input class="userNameInput" v-model="userName"></el-input>
      <el-button type="primary" @click="getUser" plain>搜索</el-button>
      <el-button type="primary" @click="addDialog('add')" icon="Plus" :disabled="disabled">添加员工</el-button>
    </div>

    <div class="data-table">
      <el-table :data="userList" v-loading="loading" stripe>
        <el-table-column prop="userName" label="员工姓名"></el-table-column>
        <el-table-column prop="account" label="员工账号"></el-table-column>
        <el-table-column prop="sex" label="员工性别">
          <template #default="scope">{{ scope.row.sex === 1 ? '男' : '女' }}</template>
        </el-table-column>
        <el-table-column prop="number" label="手机号"></el-table-column>
        <el-table-column prop="identity" label="员工身份">
          <template #default="scope">{{ scope.row.identity === 'admin' ? '管理员' : '用户' }}</template>
        </el-table-column>
        <el-table-column prop="status" label="账号状态">
          <template #default="scope">
            <el-switch
              v-model="scope.row.status"
              :active-value="1"
              :inactive-value="0"
              inline-prompt
              :disabled="disabled"
              :active-icon="Check"
              :inactive-icon="Close"
              @change="updateStatus(scope.row)"
            />
          </template>
        </el-table-column>
        <el-table-column label="操作">
          <template #default="scope">
            <el-button
              @click="addDialog(scope.row.userId)"
              size="small"
              type="primary"
              :disabled="disabled"
            >编辑</el-button>
          </template>
        </el-table-column>
      </el-table>
    </div>

    <div>
      <el-pagination
        @size-change="handleSizeChange"
        @current-change="handleCurrentChange"
        :current-page="page"
        :page-sizes="[10, 20, 30, 40]"
        :page-size="pageSize"
        layout="total, sizes, prev, pager, next, jumper"
        :total="total"
      ></el-pagination>
    </div>

    <el-dialog
      v-model="dialog"
      :title="dialogTitle"
      width="500px"
      :before-close="close"
      align-center
    >
      <el-form :model="user" label-position="left" label-width="auto" :rules="rules" ref="formRef">
        <el-form-item label="员工姓名：" prop="userName">
          <el-input v-model="user.userName" placeholder="请输入员工姓名"></el-input>
        </el-form-item>
        <el-form-item label="员工账号：" prop="account">
          <el-input v-model="user.account" placeholder="请输入员工账号"></el-input>
        </el-form-item>
        <el-form-item label="员工手机号：" prop="number">
          <el-input v-model="user.number" placeholder="请输入员工手机号"></el-input>
        </el-form-item>
        <el-form-item label="员工性别：" prop="sex">
          <el-radio-group v-model="user.sex">
            <el-radio value="1">男</el-radio>
            <el-radio value="2">女</el-radio>
          </el-radio-group>
        </el-form-item>
        <el-form-item label="员工身份：" prop="identity">
          <el-radio-group v-model="user.identity">
            <el-radio value="admin">管理员</el-radio>
            <el-radio value="user">用户</el-radio>
          </el-radio-group>
        </el-form-item>
        <el-form-item style="float: right;">
          <el-button type="primary" @click="close()" plain>关闭</el-button>
          <el-button type="primary" @click="doSave('save')">保存</el-button>
          <el-button type="primary" @click="doSave()">保存并继续添加</el-button>
        </el-form-item>
      </el-form>
    </el-dialog>
  </div>
</template>

<script setup>
import { onMounted, ref } from "vue";
import { Check, Close } from "@element-plus/icons-vue";
import {
  getUserAll,
  updateUser,
  getUserId,
  saveUser,
  removeUser
} from "@/api/user";
import { ElMessage } from "element-plus";

const page = ref(1);
const pageSize = ref(10);
const total = ref(0);
const dialog = ref(false);
const loading = ref(null);
const disabled = ref(null);
const formRef = ref(null);

const userName = ref("");
const dialogTitle = ref("");

const user = ref({});
const userList = ref([]);
const rules = {
  userName: { required: true, message: "请输入名称", trigger: "blur" },
  account: [
    { required: true, message: "请输入账号", trigger: "blur" },
    { pattern: /^[^\u4e00-\u9fa5]*$/, message: "账号不可为中文" },
    { pattern: /^.{6,10}$/, message: "账号需要6-10个字符" }
  ],
  number: [
    { required: true, message: "请输入手机号", trigger: "blur" },
    { pattern: /^1[3-9]\d{9}$/, message: "请输入正确的手机号" }
  ],
  sex: { required: true, message: "请选择性别", trigger: "change" },
  identity: { required: true, message: "请选择身份", trigger: "change" }
};

onMounted(() => {
  getUser();
  disabled.value = localStorage.getItem("identity") == 1;
});

const getUser = () => {
  loading.value = true;
  const params = {
    page: page.value,
    pageSize: pageSize.value,
    userName: userName.value
  };
  getUserAll(params)
    .then(response => {
      if (response.data.code === 200) {
        userList.value = response.data.data.records;
        total.value = response.data.data.total;
      } else {
        ElMessage.error(response.data.msg);
      }
    })
    .catch(error => {
      ElMessage.error(error);
    })
    .finally(() => {
      loading.value = false;
    });
};

const updateStatus = data => {
  updateUser(data)
    .then(response => {
      if (response.data.code === 200) {
        ElMessage.success(response.data.msg);
      } else {
        ElMessage.error(response.data.msg);
      }
    })
    .catch(error => {
      ElMessage.error(error);
    })
    .finally(() => {
      getUser();
    });
};

const addDialog = type => {
  if (type === "add") {
    dialogTitle.value = "新增员工";
    dialog.value = true;
  } else {
    dialogTitle.value = "修改员工";
    dialog.value = true;
    getUserId(type)
      .then(response => {
        if (response.data.code === 200) {
          user.value = response.data.data;
        } else {
          ElMessage.error(response.data.msg);
        }
      })
      .catch(error => {
        ElMessage.error(error);
      });
  }
};

const doSave = type => {
  formRef.value.validate(valid => {
    if (valid) {
      save(type);
    }
  });
};

const save = type => {
  if (dialogTitle.value === "新增员工") {
    if (type === "save") {
      saveUser(user.value)
        .then(response => {
          if (response.data.code === 200) {
            ElMessage.success(response.data.msg);
          } else {
            ElMessage.error(response.data.msg);
          }
        })
        .catch(error => {
          ElMessage.error(error);
        })
        .finally(() => {
          close();
        });
    } else {
      saveUser(user.value)
        .then(response => {
          if (response.data.code === 200) {
            ElMessage.success(response.data.msg);
          } else {
            ElMessage.error(response.data.msg);
          }
        })
        .catch(error => {
          ElMessage.error(error);
        })
        .finally(() => {
          user.value = {};
        });
    }
  } else {
    updateUser(user.value)
      .then(response => {
        if (response.data.code === 200) {
          ElMessage.success(response.data.msg);
        } else {
          ElMessage.error(response.data.msg);
        }
      })
      .catch(error => {
        ElMessage.error(error);
      })
      .finally(() => {
        close();
      });
  }
};

const close = () => {
  dialog.value = false;
  user.value = {};
  getUser();
};

const handleCurrentChange = newPage => {
  page.value = newPage;
  getUser();
};

const handleSizeChange = newPageSize => {
  pageSize.value = newPageSize;
  getUser();
};
</script>

<style scoped>
.input {
  width: 1290px;
  margin: 50px auto;
}

.userNameInput {
  width: 200px;
  margin-right: 20px;
}

.el-table {
  width: 1290px;
  height: 100%;
}

.data-table {
  margin: 30px auto;
  width: 1290px;
  height: 70vh;
}

.el-pagination {
  margin-top: 20px;
  justify-content: center;
}
</style>