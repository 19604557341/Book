<template>
  <div>
    <div class="input">
      <el-input class="categoryNameInput" v-model="categoryName"></el-input>
      <el-button type="primary" @click="getCategory" plain>搜索</el-button>
      <el-button type="primary" @click="addDialog('add')" icon="Plus" :disabled="disabled">添加分类</el-button>
    </div>

    <div class="data-table">
      <el-table :data="categoryList" key="tableKey" v-loading="loading">
        <el-table-column prop="categoryName" label="分类名称"></el-table-column>
        <el-table-column prop="createTime" label="创建时间"></el-table-column>
        <el-table-column prop="updateTime" label="修改时间"></el-table-column>
        <el-table-column prop="createUserName" label="创建人"></el-table-column>
        <el-table-column prop="updateUserName" label="修改人"></el-table-column>
        <el-table-column label="操作">
          <template #default="scope">
            <el-button
              @click="addDialog(scope.row.categoryId)"
              size="small"
              type="primary"
              :disabled="disabled"
            >编辑</el-button>
            <el-button
              @click="remove(scope.row.categoryId)"
              size="small"
              type="danger"
              :disabled="disabled"
            >删除</el-button>
          </template>
        </el-table-column>
      </el-table>
    </div>

    <div class="data-pagination">
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
      <el-form
        :model="category"
        label-position="left"
        label-width="auto"
        :rules="rules"
        ref="formRef"
      >
        <el-form-item label="名称" prop="categoryName">
          <el-input v-model="category.categoryName"></el-input>
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
import {
  getCategoryAll,
  getCategoryId,
  saveCategory,
  updateCategory,
  removeCategory
} from "@/api/category";
import { ElMessage } from "element-plus";

const page = ref(1);
const pageSize = ref(10);
const total = ref(0);
const dialog = ref(false);
const loading = ref(null);
const disabled = ref(null);
const formRef = ref(null);

const categoryName = ref("");
const dialogTitle = ref("");

const category = ref({});
const categoryList = ref([]);
const rules = {
  categoryName: { required: true, message: "请输入名称", trigger: "blur" }
};

onMounted(() => {
  getCategory();
  disabled.value = localStorage.getItem("identity") == 1;
});

const getCategory = () => {
  loading.value = true;
  const params = {
    page: page.value,
    pageSize: pageSize.value,
    categoryName: categoryName.value
  };
  getCategoryAll(params)
    .then(response => {
      if (response.data.code === 200) {
        categoryList.value = response.data.data.records;
      } else {
        ElMessage.error(response.data.msg);
      }
    })
    .catch(error => [ElMessage.error(error)])
    .finally(() => {
      loading.value = false;
    });
};

const addDialog = type => {
  if (type === "add") {
    dialogTitle.value = "新增分类";
    dialog.value = true;
  } else {
    dialogTitle.value = "修改分类";
    dialog.value = true;
    getCategoryId(type)
      .then(response => {
        if (response.data.code === 200) {
          category.value = response.data.data;
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
  if (dialogTitle.value === "新增分类") {
    if (type === "save") {
      saveCategory(category.value)
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
      saveCategory(category.value)
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
          category.value = {};
        });
    }
  } else {
    updateCategory(category.value)
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

const remove = categoryId => {
  removeCategory(categoryId)
    .then(response => {
      if (response.data.code === 200) {
        ElMessage.success(response.data.code);
      } else {
        ElMessage.error(response.data.msg);
      }
    })
    .catch(error => {
      ElMessage.error(error);
    })
    .finally(() => {
      getCategory();
    });
};

const close = () => {
  dialog.value = false;
  category.value = {};
  getCategory();
};

const handleCurrentChange = newPage => {
  page.value = newPage;
  getCategory();
};

const handleSizeChange = newPageSize => {
  pageSize.value = newPageSize;
  getCategory();
};
</script>

<style scoped>
.input {
  width: 1290px;
  margin: 50px auto;
}

.categoryNameInput {
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