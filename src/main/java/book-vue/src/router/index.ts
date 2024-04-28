import { createRouter, createWebHistory, RouteRecordRaw } from 'vue-router'
import LoginView from '@/views/LoginView.vue'
import UserView from '@/views/user/UserView.vue'
import BookView from '@/views/book/BookView.vue'
import CategoryView from '@/views/category/CategoryView.vue'
import Order from '@/views/order/OrderView.vue'

const routes: Array<RouteRecordRaw> = [
  {
    path: '/',
    redirect: '/login'
  },
  {
    path: '/login',
    name: 'Login',
    component: LoginView,
  },
  {
    path: '/user',
    name: 'User',
    component: UserView,
  },
  {
    path: '/book',
    name: 'Book',
    component: BookView,
  },
  {
    path: "/category",
    name: "Category",
    component: CategoryView,
  },
  {
    path: "/order",
    name: "Order",
    component: Order,
  },
]

const router = createRouter({
  history: createWebHistory(process.env.BASE_URL),
  routes
})

export default router
