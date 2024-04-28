import $axios from "@/js/requset";

export function getCategoryAll(params) {
    return $axios({
        url: '/category/page',
        method: 'get',
        params
    })
}
export function getCategoryId(categoryId) {
    return $axios({
        url: '/category/' + categoryId,
        method: 'get',
    })
}
export function updateCategory(data) {
    return $axios({
        url: '/category',
        method: 'put',
        data
    });
}
export function saveCategory(data) {
    return $axios({
        url: '/category',
        method: 'post',
        data
    })
}
export function removeCategory(categoryId) {
    return $axios({
        url: '/category/' + categoryId,
        method: 'delete',
    });
}