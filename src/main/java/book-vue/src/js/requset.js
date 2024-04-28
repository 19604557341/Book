import axios from "axios";
import $router from "../router/index"

const $axios = axios.create({
    headers:{
        'Content-Type': 'application/json; charset=utf-8',
    },
    baseURL: "http://localhost:8080/", // 设置基本的请求路径
    timeout: 5000
});

$axios.interceptors.request.use(
    function (config) {
        const token_name = localStorage.getItem('token');
        if (token_name) {
            const index = token_name.lastIndexOf("-")
            const token = token_name.substring(0, index)
            config.headers.Authorization = `Bearer ${token}`; // 假设你使用的是Bearer token  
        }
        return config;  
    }
)

$axios.interceptors.response.use(  
    function (response) {
        if (response.data.code === 500 && response.data.msg === "NOT_LOGIN") {
            $router.push("/login");
            return Promise.reject(response.data.msg);
        } else {
            return response;  
        }
        
    },  
    function(error) {  
        console.log(error)
        let message;  
        if (error.message === "Network Error" && error.code === 'ERR_NETWORK') {
            message = "后端接口连接异常"  
        } else if (axios.isAxiosError(error) && error.response) {  
            message = `系统接口${error.response.status}异常`;  
        } else {
            message = "未知错误";  
        }

        return Promise.reject(message);
    }  
);  

export default $axios;