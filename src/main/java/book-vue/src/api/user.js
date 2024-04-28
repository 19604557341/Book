import $axios from "@/js/requset";
export function login(data) {
    return $axios({
        url: '/user/login',
        method: 'Post',
        data
    })
}
export function getUserAll(params) {
    return $axios({
        url: '/user/page',
        method: 'get',
        params
    });
}
export function getUserId(userId) {
    return $axios ({
        url: '/user/'+ userId,
        method: 'get',
    });
}
export function updateUser(data) {
    return $axios({
        url: '/user',
        method: 'put',
        data
    });
}
export function saveUser(data) {
    return $axios({
        url: '/user',
        method: 'post',
        data
    })
}
export function removeUser(userId) {
    return $axios({
        url: '/user/'+ userId,
        method: 'delete',
    })
}