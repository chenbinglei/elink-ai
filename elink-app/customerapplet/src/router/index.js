import {createRouter, createWebHashHistory} from 'vue-router'

const routes = [
    {
        path: '/',
        name: 'index',
        component: () => import('@/views/homePage/index.vue')
    },
    {
        path: '/applet',
        name: 'applet',
        component: () => import('@/views/externalPages/applet.vue')
    },
    {
        path: '/reportView',
        name: 'reportView',
        component: () => import('@/views/ies/reportView.vue')
    },
    {
        path: '/404',
        name: '404',
        component: () => import('@/views/404.vue')
    }, {
        path: "/:catchAll(.*)",
        redirect: "/404",
    }
]

const router = createRouter({
    history: createWebHashHistory(process.env.BASE_URL),
    routes
})
export default router
