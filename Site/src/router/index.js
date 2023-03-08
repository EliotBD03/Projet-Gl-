import Vue from 'vue'
import VueRouter from 'vue-router'
import VueCookies from 'vue-cookies';
const cookies = VueCookies;

Vue.use(VueRouter)

const routes = [
  {
    path: '/',
    name: 'login',
    component: () => import('@/views/Login.vue'),
    beforeRouteEnter: (to, from, next) => {
      if(cookies.isKey("token") && cookies.isKey("role"))
      {
        if(cookies.get("role") === 'client'){
          next({ name: 'HomeClient' });
        }
        else{
          next({ name: 'HomeSupplier' });
        }
      }
      else {
        next();
      }
    }
  },
  {
    path: '/Home',
    name: 'HomeClient',
    component: () => import('@/views/HomeClient.vue')
    /*,
    beforeRouteEnter: (to, from, next) => {
      if(cookies.isKey("token") && cookies.isKey("role"))
      {
        if(cookies.get("role") === 'client'){
          next();
        }
        else{
          next(false);
        }
      }
    }*/
  },
  {
    path: '/Home',
    name: 'HomeSupplier',
    component: () => import('@/views/HomeSupplier.vue')
  },
  {
    path: '/walletsFull',
    name: 'WalletsFull',
    component: () => import('@/views/WalletFull.vue')
  },
  {
    path: '/wallets',
    name: 'Wallets',
    component: () => import('@/views/WalletsPage.vue')
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
    path: '/wallets/consumptions',
    name: 'Consumptions',
    component: () => import('@/views/ConsumptionPage.vue')
  },
  {
    path: '/createAccount',
    name: 'createAccount',
    component: () => import('@/views/CreateAccount.vue')
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
