import Vue from 'vue'
import VueRouter from 'vue-router'
import VueCookies from 'vue-cookies';
const cookies = VueCookies;

Vue.use(VueRouter)

const routes = [
  {
    path: '/',
    name: 'login',
    component: () => import('@/views/LoginPage.vue'),
    beforeEnter: (to, from, next) => {
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
    path: '/account',
    component: () => import('@/views/GroupAccount.vue'),
    children: [
      {
        path: '/createAccount',
        name: 'createAccount',
        component: () => import('@/views/CreateAccount.vue')
      },
      {
        path: '/forgottenPassword',
        name: 'forgottenPassword',
        component: () => import('@/views/ForgottenPassword.vue'),
        beforeEnter: (to, from, next) => {
          if (!cookies.isKey("mail")) {
            next(from.path);
          } else {
            next();
          }
        }
      },
    ],beforeEnter: (to, from, next) => {
        if((cookies.isKey("token") && cookies.isKey("role")))
        {
          next(from.path);
        }
        else{
            next();
        }
      }
  },
  {
    path: '/client',
    component: () => import('@/views/GroupClient.vue'),
    children: [
      {
        path: '/Home',
        name: 'HomeClient',
        component: () => import('@/views/HomeClient.vue')
      },
      {
        path: '/walletFull',
        name: 'WalletFull',
        component: () => import('@/views/WalletFull.vue')
      },
      {
        path: '/wallets',
        name: 'Wallets',
        component: () => import('@/views/WalletsPage.vue')
      },
      {
        path: '/addWallet',
        name: 'addWallet',
        component: () => import('@/views/AddWallet.vue')
      },
      {
        path: '/newcontracts',
        name: 'NewContracts',
        component: () => import('@/views/NewContractsPage.vue')
      },
      {
        path: '/consumptions',
        name: 'Consumptions',
        component: () => import('@/views/ConsumptionPage.vue')
      }
    ],beforeEnter: (to, from, next) => {
        if(cookies.isKey("token") && cookies.isKey("role"))
        {
          if(cookies.get("role") === 'client'){
            next();
          }
          else{
            next(from.path);
          }
        }
    }
  },
  {
    path: '/supplier',
    component: () => import('@/views/GroupSupplier.vue'),
    children: [
      {
        path: '/Home',
        name: 'HomeSupplier',
        component: () => import('@/views/HomeSupplier.vue')
      },
      {
        path: '/clients',
        name: 'Clients',
        component: () => import('@/views/ClientsPage.vue')
      },
      {
        path: '/addClient',
        name: 'addClient',
        component: () => import('@/views/AddClient.vue')
      },
      {
        path: '/clientFull',
        name: 'ClientFull',
        component: () => import('@/views/ClientFull.vue')
      },
      {
        path: '/createContract',
        name: 'CreateContract',
        component: () => import('@/views/CreateContract.vue')
      },
      {
        path: '/contracts',
        name: 'ContractsSupplier',
        component: () => import('@/views/SupplierContractPage.vue')
      }
    ],beforeEnter: (to, from, next) => {
        if(cookies.isKey("token") && cookies.isKey("role"))
        {
          if(cookies.get("role") === 'supplier'){
            next();
          }
          else{
            next(from.path);
          }
        }
    }
  },
  //Redirections valables pour les 2
  {
    path: '/notifications',
    name: 'Notifications',
    component: () => import('@/views/NotificationsPage.vue'),
    beforeEnter: (to, from, next) => {
      if(cookies.isKey("token") && cookies.isKey("role"))
      {
        next();
      }
      else {
        next(from.path);
      }
    }
  },
  {
    path: '/settings',
    name: 'Settings',
    component: () => import('@/views/SettingsPage.vue'),
    beforeEnter: (to, from, next) => {
      if(cookies.isKey("token") && cookies.isKey("role"))
      {
        next();
      }
      else {
        next(from.path);
      }
    }
  },
  {
    path: '/contractFull',
    name: 'ContractFull',
    component: () => import('@/views/ContractFull.vue'),
    beforeEnter: (to, from, next) => {
      if(cookies.isKey("token") && cookies.isKey("role"))
      {
        next();
      }
      else {
        next(from.path);
      }
    }
  }
]

const router = new VueRouter({
  mode: 'history',
  base: process.env.BASE_URL,
  routes
})

export default router
