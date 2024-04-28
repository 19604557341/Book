<template>
  <div class="login">
    <div class="loginBox" :style="{
      boxShadow: 'var(--el-box-shadow-dark)'
    }">
      <div class="loginBoxLeft"></div>

      <div class="loginBoxRight">
        <img src="../assets/背景.png" width="200px" />

        <el-input class="userName" placeholder="请输入账号" v-model="account" prefix-icon="User"></el-input>

        <el-input
          class="password"
          type="password"
          id="password"
          placeholder="请输入密码"
          :show-password="true"
          v-model="password"
          prefix-icon="Lock"
        ></el-input>

        <el-button class="submin" type="warning" @click="login">登录</el-button>
      </div>
    </div>
  </div>
</template>

<script>
import { login } from "@/api/user";

export default {
  data() {
    return {
      account: "admin",
      password: "123456",
      token: ""
    };
  },
  methods: {
    login() {
      const loginData = {
        account: this.account,
        password: this.password
      };
      const data = JSON.stringify(loginData);
      login(data)
        .then(res => {
          if (res.data.code === 200) {
            this.$message.success("登录成功");
            this.token = res.data.msg;
            localStorage.setItem("token", this.token);
            this.loginSuccess();
          } else {
            this.$message.error(res.data.msg);
          }
        })
        .catch(error => {
          this.$message.error(error);
        });
    },
    loginSuccess() {
      // 登录成功后的逻辑，例如跳转到登录后的页面
      const token = localStorage.getItem("token");
      const nameIndex = token.lastIndexOf("-");
      const identityIndex = token.lastIndexOf(".");
      const loginName = token.slice(nameIndex + 1);
      const identity = token.slice(identityIndex + 1, nameIndex);
      if (identity == "admin") {
        localStorage.setItem("identity", 0);
      } else {
        localStorage.setItem("identity", 1);
      }
      localStorage.setItem("loginName", loginName);
      this.$router.push("/user"); // 跳转到用户页面
    }
  }
};
</script>

<style scoped>
.login {
  width: 100vw;
  height: 100vh;
  background-color: #333;
}

.loginBox {
  width: 1000px;
  height: 500px;
  background-color: #fff;
  /* 与 .loginBox 的背景色相同 */
  border-radius: 15px;
  /* 应用圆角 */
  position: absolute;
  left: 0;
  top: 0;
  right: 0;
  bottom: 0;
  margin: auto;
  display: flex;
}

.loginBoxLeft {
  width: 600px;
  background-image: url(../assets/背景.png);
  background-size: cover;
  border-radius: 12px 0 0 12px;
}

.loginBoxRight {
  width: 400px;
  display: flex;
  flex-direction: column;
  text-align: center;
  align-items: center;
  justify-content: center;
}

.userName {
  width: 200px;
  margin-top: 20px;
  border: none;
}

.password {
  width: 200px;
  margin-top: 20px;
}

.submin {
  margin-top: 20px;
}
</style>