import $axios from "@/js/requset";

export function getOrderAll(params) {
    return $axios({
        url: '/order/page',
        method: 'get',
        params
    })
}
export function getOrderById(params) {
    return $axios({
        url: '/order/orderDetails',
        method: 'get',
        params
    })
}
export function saveOrder(data) {
    return $axios({
        url: '/order',
        method: 'post',
        data
    })
}
export function updateOrderStatus(data) {
    return $axios({
        url: '/order',
        method: 'put',
        data
    })
}