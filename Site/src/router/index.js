import Vue from 'vue'
import VueRouter from 'vue-router'

Vue.use(VueRouter)

const routes = [
  {
    path: '/',
    name: 'Home',
    component: () => import('@/views/HomePage.vue')
  },
  {
    path: '/wallets',
    name: 'Wallets',
    component: () => import('@/views/WalletsPage.vue')
  },
  {
    path: '/wallets/walletfull',
    name: 'WalletFull',
    component: () => import('@/views/WalletFull.vue')
  },
  {
    path: '/contracts',
    name: 'Contracts',
    component: () => import('@/views/ContractPage.vue')
  }
]

const router = new VueRouter({
  mode: 'history',
  base: process.env.BASE_URL,
  routes
})

export default router
