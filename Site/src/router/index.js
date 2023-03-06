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
  },
  {
    path: '/notifications',
    name: 'Notifications',
    component: () => import('@/views/NotificationsPage.vue')
  },
  {
    path: '/newcontracts',
    name: 'NewContracts',
    component: () => import('@/views/NewContractsPage.vue')
  },
  {
    path: '/settings',
    name: 'Settings',
    component: () => import('@/views/SettingsPage.vue')
  },
  {
    path: '/wallets/walletfull/consumptions',
    name: 'Consumptions',
    component: () => import('@/views/ConsumptionPage.vue')
  },
  {
    path: '/createAccount',
    name: 'createAccount',
    component: () => import('@/views/CreateAccount.vue')
  },
  {
    path: '/login',
    name: 'login',
    component: () => import('@/views/Login.vue')
  },
  {
    path: '/forgottenPassword',
    name: 'forgottenPassword',
    component: () => import('@/views/ForgottenPassword.vue')
  }
]

const router = new VueRouter({
  mode: 'history',
  base: process.env.BASE_URL,
  routes
})

export default router
