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
        component: () => import('@/views/ForgottenPassword.vue')
      },
    ]/*,
      beforeRouteEnter: (to, from, next) => {
        if(cookies.isKey("token") && cookies.isKey("role"))
        {
          next(false);
        }
        else{
            next();
          }
      }*/
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
        path: '/newcontracts',
        name: 'NewContracts',
        component: () => import('@/views/NewContractsPage.vue')
      },
      {
        path: '/contracts',
        name: 'Contracts',
        component: () => import('@/views/ContractPage.vue')
      },
      {
        path: '/consumptions',
        name: 'Consumptions',
        component: () => import('@/views/ConsumptionPage.vue')
      },
    ]/*,
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
    path: '/supplier',
    component: () => import('@/views/GroupSupplier.vue'),
    children: [
      {
        path: '/Home',
        name: 'HomeSupplier',
        component: () => import('@/views/HomeSupplier.vue')
      }
    ]/*,
      beforeRouteEnter: (to, from, next) => {
        if(cookies.isKey("token") && cookies.isKey("role"))
        {
          if(cookies.get("role") === 'supplier'){
            next();
          }
          else{
            next(false);
          }
        }
      }*/
  },
  //Redirections valables pour les 2
  {
    path: '/notifications',
    name: 'Notifications',
    component: () => import('@/views/NotificationsPage.vue'),
    beforeRouteEnter: (to, from, next) => {
      if(cookies.isKey("token") && cookies.isKey("role"))
      {
        next();
      }
      else {
        next(false);
      }
    }
  },
  {
    path: '/settings',
    name: 'Settings',
    component: () => import('@/views/SettingsPage.vue'),
    beforeRouteEnter: (to, from, next) => {
      if(cookies.isKey("token") && cookies.isKey("role"))
      {
        next();
      }
      else {
        next(false);
      }
    }
  }
]

const router = new VueRouter({
  mode: 'history',
  base: process.env.BASE_URL,
  routes
})

router.beforeEach((to, from, next) => {
  next();
});

export default router
