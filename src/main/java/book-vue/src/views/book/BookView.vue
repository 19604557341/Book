<template>
  <div>
    <div class="input">
      <el-input class="bookNameInput" v-model="bookName"></el-input>
      <el-select
        v-model="categoryId"
        style="width: 200px; margin-right:20px"
        clearable
        placeholder="全部"
      >
        <el-option
          v-for="item in categorys"
          :key="item.categoryId"
          :value="item.categoryId"
          :label="item.categoryName"
        />
      </el-select>
      <el-button type="primary" @click="getBook" plain>搜索</el-button>
      <el-button type="primary" @click="addDialog('add')" icon="Plus" :disabled="disabled">添加图书</el-button>
    </div>

    <div>
      <el-table :data="bookList" class="data-table" v-loading="loading">
        <el-table-column fixed prop="bookName" label="图书名称" width="200"></el-table-column>
        <el-table-column prop="categoryName" label="分类" width="100px"></el-table-column>
        <el-table-column prop="price" label="单价" width="60px"></el-table-column>
        <el-table-column prop="inventory" label="库存" width="100px"></el-table-column>
        <el-table-column prop="createTime" label="创建时间" width="190"></el-table-column>
        <el-table-column prop="updateTime" label="最后修改时间" width="190"></el-table-column>
        <el-table-column prop="createUserName" label="创建人" width="100"></el-table-column>
        <el-table-column prop="updateUserName" label="最后修改人" width="100"></el-table-column>
        <el-table-column prop="status" label="售卖状态" width="100">
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
        <el-table-column label="操作" width="150">
          <template #default="scope">
            <el-button
              @click="addDialog(scope.row.bookId)"
              size="small"
              type="primary"
              :disabled="disabled"
            >编辑</el-button>
            <el-button
              @click="remove(scope.row.bookId)"
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
      width="500"
      :before-close="close"
      align-center
    >
      <el-form :model="book" label-position="left" label-width="auto" :rules="rules" ref="formRef">
        <el-form-item label="名称：" prop="bookName">
          <el-input v-model="book.bookName" placeholder="请输入图书名称"></el-input>
        </el-form-item>
        <el-form-item label="分类：" prop="categoryId">
          <el-select v-model="book.categoryId" style="width: 200px; margin-right:20px" clearable>
            <el-option
              v-for="item in categorys"
              :key="item.categoryId"
              :value="item.categoryId"
              :label="item.categoryName"
            />
          </el-select>
        </el-form-item>
        <el-form-item label="单价：" prop="price">
          <el-input-number v-model="book.price" placeholder="单价" />
        </el-form-item>
        <el-form-item label="库存：" prop="inventory">
          <el-input-number v-model="book.inventory" placeholder="库存" />
        </el-form-item>
        <el-form-item style="float: right;">
          <el-button type="primary" @click="close()" plain>关闭</el-button>
          <el-button type="primary" @click="doSave('save')">保存</el-button>
          <el-button type="primary" @click="doSave()" v-if="dialogTitle==='新增图书'">保存并继续添加</el-button>
        </el-form-item>
      </el-form>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, onMounted } from "vue";
import {
  getBookAll,
  updateBook,
  getBookId,
  saveBook,
  removeBook
} from "@/api/book";
import { Check, Close } from "@element-plus/icons-vue";
import { ElMessage } from "element-plus";
import { getCategoryAll } from "@/api/category";

const page = ref(1);
const pageSize = ref(10);
const total = ref(0);
const dialog = ref(false);
const loading = ref(null);
const disabled = ref(null);
const categoryId = ref(null);
const formRef = ref(null);

const bookName = ref("");
const dialogTitle = ref("");

const book = ref({});
const bookList = ref([]);
const categorys = ref([]);
const rules = {
  bookName: { required: true, message: "请输入图书名称", trigger: "change" },
  categoryId: { required: true, message: "请选择分类", trigger: "change" },
  price: [{ required: true, message: "请输入单价", trigger: "change" }],
  inventory: { required: true, message: "请输入库存数量", trigger: "change" }
};

onMounted(() => {
  getBook();
  getCategory();
  disabled.value = localStorage.getItem("identity") == 1;
});

const getBook = () => {
  loading.value = true;
  const params = {
    page: page.value,
    pageSize: pageSize.value,
    bookName: bookName.value,
    categoryId: categoryId.value
  };
  getBookAll(params)
    .then(response => {
      if (response.data.code === 200) {
        bookList.value = response.data.data.records;
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

const getCategory = () => {
  const params = {
    page: 1,
    pageSize: 2000,
    categoryName: ""
  };
  getCategoryAll(params)
    .then(response => {
      if (response.data.code === 200) {
        categorys.value = response.data.data.records;
      } else {
        ElMessage.error(response.data.msg);
      }
    })
    .catch(error => {
      console.error(error);
    });
};

const updateStatus = data => {
  updateBook(data)
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
      getBook();
    });
};

const addDialog = type => {
  dialog.value = true;
  if (type === "add") {
    dialogTitle.value = "新增图书";
  } else {
    dialogTitle.value = "修改图书";
    getBookId(type)
      .then(response => {
        if (response.data.code === 200) {
          book.value = response.data.data;
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
  if (dialogTitle.value === "新增图书") {
    if (type === "save") {
      saveBook(book.value)
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
      saveBook(book.value)
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
          book.value = {};
        });
    }
  } else {
    updateBook(book.value)
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

const remove = bookId => {
  removeBook(bookId)
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
      getBook();
    });
};

const close = () => {
  dialog.value = false;
  getBook();
  book.value = {};
};

const handleCurrentChange = newpage => {
  page.value = newpage;
  getBook();
};

const handleSizeChange = newpageSize => {
  pageSize.value = newpageSize;
  getBook();
};
</script>

<style scoped>
.input {
  width: 1290px;
  margin: 50px auto;
}

.bookNameInput {
  width: 200px;
  margin-right: 20px;
}

.el-table {
  width: 1290px;
  height: 527px;
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