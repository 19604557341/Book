<template>
  <div>
    <div class="input">
      <el-select
        v-model="createUser"
        style="width: 200px; margin-right:20px"
        clearable
        placeholder="全部"
      >
        <el-option
          v-for="item in user"
          :key="item.userId"
          :value="item.userId"
          :label="item.userName"
        />
      </el-select>
      <el-button type="primary" @click="getOrder" plain>搜索</el-button>
      <el-button type="primary" @click="addDialog()" icon="Plus" :disabled="disabled">添加订单</el-button>
    </div>

    <div class="data-table">
      <el-table :data="orderList" key="tableKey" class="father-table" v-loading="loading">
        <el-table-column type="expand">
          <template #default="scope">
            <div class="chlid-table">
              <el-text tag="h4" size="large">订单ID {{ scope.row.orderId }}</el-text>
              <el-table :data="scope.row.orderDetails">
                <el-table-column prop="bookName" label="图书名称"></el-table-column>
                <el-table-column prop="quantity" label="购买数量"></el-table-column>
                <el-table-column prop="price" label="图书单价"></el-table-column>
              </el-table>
              <el-text size="large" class="span">付款时间 {{ scope.row.paymentTime }}</el-text>
              <el-text size="large" class="span" style="float: right;">付款总金额 {{ scope.row.amount }}</el-text>
            </div>
          </template>
        </el-table-column>
        <el-table-column prop="orderId" label="订单ID"></el-table-column>
        <el-table-column prop="createTime" label="创建时间"></el-table-column>
        <el-table-column prop="createUserName" label="创建人"></el-table-column>
        <el-table-column prop="paymentTime" label="付款时间"></el-table-column>
        <el-table-column prop="status" label="状态">
          <template #default="scope">
            <span v-if="scope.row.status==0">未付款</span>
            <span v-if="scope.row.status==1">已付款</span>
            <span v-if="scope.row.status==2">已退款</span>
          </template>
        </el-table-column>
        <el-table-column prop="amount" label="总金额"></el-table-column>
        <el-table-column label="操作">
          <template #default="scope">
            <el-button
              size="small"
              type="primary"
              @click="orderStatus(scope.row)"
              :disabled="disabled || scope.row.status!=0"
            >付款</el-button>
            <el-button
              size="small"
              type="danger"
              @click="orderStatus(scope.row)"
              :disabled="disabled || scope.row.status!=1"
            >退款</el-button>
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

    <el-dialog v-model="dialog" ref="books" title="选择图书" :before-close="close" align-center>
      <el-table :data="bookList" style="width: 100%;" @selection-change="handleSelectionChange">
        <el-table-column type="selection" width="55" :selectable="selectable" />
        <el-table-column prop="bookName" label="书名"></el-table-column>
        <el-table-column prop="inventory" label="库存"></el-table-column>
        <el-table-column prop="price" label="单价"></el-table-column>
        <el-table-column prop="bookName" label="书名"></el-table-column>
      </el-table>
      <el-button type="primary" @click="close('f')" plain>关闭</el-button>
      <el-button type="primary" @click="getSelectedRows">下一步</el-button>
    </el-dialog>

    <el-dialog v-model="dialogBody" title="选择数量" append-to-body width="650px" align-center>
      <el-form v-model="selectBooks">
        <el-form-item v-for="item in selectBooks" :key="item.id">
          <el-text tag="b">书名：{{ item.bookName }}</el-text>
          <el-text tag="b">单价：{{ item.price }}</el-text>
          <el-input-number v-model="item.quantity" @change="updateAllPrice"></el-input-number>
        </el-form-item>
        <el-form-item label="订单状态：">
          <el-radio-group v-model="status">
            <el-radio value="0">未付款</el-radio>
            <el-radio value="1">已付款</el-radio>
          </el-radio-group>
        </el-form-item>
        <el-form-item>
          <el-text tag="b">总价格：{{ allPrice }}</el-text>
        </el-form-item>
      </el-form>
      <el-button type="primary" @click="close('body')" plain>关闭</el-button>
      <el-button type="primary" @click="save()">保存</el-button>
    </el-dialog>
  </div>
</template>

<script setup>
import { onMounted, ref } from "vue";
import { ElMessage } from "element-plus";
import { getUserAll } from "@/api/user";
import { getOrderAll, saveOrder, updateOrderStatus } from "@/api/order";
import { getBookAll } from "@/api/book";

const page = ref(1);
const pageSize = ref(10);
const total = ref(0);
const dialog = ref(false);
const dialogBody = ref(false);
const loading = ref(null);
const disabled = ref(null);
// const formRef = ref(null);
const createUser = ref(null);
const allPrice = ref(null);
const status = ref(null);

const user = ref([]);
const orderList = ref([]);
const bookList = ref([]);
const books = ref([]);
const selectBooks = ref([]);

onMounted(() => {
  getUser();
  getOrder();
  disabled.value = localStorage.getItem("identity") == 1;
});

const getSelectedRows = () => {
  selectBooks.value = books.value;
  dialogBody.value = true;
  allPrice.value = selectBooks.value.reduce((total, item) => {
    return total + item.price * item.quantity;
  }, 0);
  console.log(selectBooks.value);
};

const updateAllPrice = () => {
  allPrice.value = selectBooks.value.reduce((total, item) => {
    return total + item.price * item.quantity;
  }, 0);
};

const handleSelectionChange = val => {
  books.value = val.map(item => ({
    bookName: item.bookName,
    quantity: 1,
    price: item.price
  }));
};

const getOrder = () => {
  loading.value = true;
  const params = {
    page: page.value,
    pageSize: pageSize.value,
    createUser: createUser.value
  };
  getOrderAll(params)
    .then(response => {
      if (response.data.code === 200) {
        orderList.value = response.data.data.records;
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

const getUser = () => {
  const params = {
    page: 1,
    pageSize: 10000
  };
  getUserAll(params)
    .then(response => {
      if (response.data.code === 200) {
        user.value = response.data.data.records;
        total.value = response.data.data.total;
      } else {
        ElMessage.error(response.data.msg);
      }
    })
    .catch(error => {
      console.error(error);
    });
};

const getBook = () => {
  const params = {
    page: 1,
    pageSize: 10000
  };
  getBookAll(params)
    .then(response => {
      if (response.data.code === 200) {
        bookList.value = response.data.data.records;
      } else {
        ElMessage.error(response.data.msg);
      }
    })
    .catch(error => {
      ElMessage.error(error);
    });
};

const addDialog = () => {
  dialog.value = true;
  getBook();
};

const close = val => {
  if (val === "f") {
    dialog.value = false;
  } else {
    dialogBody.value = false;
  }
};

const selectable = row => {
  return row.status == 1;
};

const orderStatus = row => {
  if (row.status == 0) {
    row.status = 1;
  } else {
    row.status = 2;
  }
  console.log(row);
  updateOrderStatus(row)
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
      getOrder();
    });
};

const save = () => {
  const data = {
    status: status.value,
    amount: allPrice.value,
    orderDetails: selectBooks.value
  };
  console.log(data);
  saveOrder(data)
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
      dialogBody.value = false;
      dialog.value = false;
      getOrder();
    });
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

.categoryNameInput {
  width: 200px;
  margin-right: 20px;
}

.father-table {
  width: 1290px;
  height: 527px;
}

.chlid-table {
  width: 1000px;
  line-height: 4;
  margin: auto;
}

.data-table {
  margin: 30px auto;
  width: 1290px;
  height: 575px;
}

.el-pagination {
  margin-top: 20px;
  justify-content: center;
}

.el-form .el-text {
  width: 200px;
  margin-right: 20px;
}
</style>