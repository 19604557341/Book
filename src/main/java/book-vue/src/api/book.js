import $axios from "@/js/requset";

export function getBookAll(params) {
    return $axios({
        url: '/book/page',
        method: 'get',
        params
    });
}
export function saveBook(data) {
    return $axios({
        url: '/book',
        method: 'post',
        data
    })
}
export function getBookId(bookId) {
    return $axios({
        url: '/book/' + bookId,
        method: 'get'
    });
}
export function updateBook(data) {
    return $axios({
        url: '/book',
        method: 'put',
        data
    })
}
export function removeBook(bookId) {
    return $axios({
        url: '/book/' + bookId,
        method: 'delete',
    })
}