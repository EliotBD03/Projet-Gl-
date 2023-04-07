import Vue from 'vue'
import VueRouter from 'vue-router'
import VueCookies from 'vue-cookies';
const cookies = VueCookies;

Vue.use(VueRouter)

const routes = [
  /*Redirections valables pour les comptes*/
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
    component: {
      render(c) {
        return c('div', [c('router-view')]) //c = createElement
      }
    },
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
    /*Redirections valables pour le client*/
    path: '/client',
    component: {
      render(c) {
        return c('div', [c('router-view')]) //c = createElement
      }
    },
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
        path: "/contractInformation",
        name: "contractInformation",
        component: () => import('@/views/ContractInformation')
      },
      {
        path: '/clientContracts',
        name: 'Contracts',
        component: () => import('@/views/ContractPage.vue')
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
  /*Redirections valables pour le fournisseur*/
  {
    path: '/supplier',
    component: {
      render(c) {
        return c('div', [c('router-view')]) //c = createElement
      }
    },
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
        path: '/providerContracts',
        name: 'ContractsSupplier',
        component: () => import('@/views/SupplierContractPage.vue')
      },
      {
        path: '/proposalfull',
        name: 'ProposalFull',
        component: () => import('@/views/ProposalFull.vue')
      },
      {
        path: '/changeProposal',
        name: 'ModifyProposal',
        component: () => import('@/views/SupplierModifyContract.vue')
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
  /*Redirections valables pour les 2*/
  {
    path: '/common',
    component: {
      render(c) {
        return c('div', [c('router-view')]) //c = createElement
      }
    },
    children: [
      {
        path: '/notifications',
        name: 'Notifications',
        component: () => import('@/views/NotificationsPage.vue')
      },
      {
        path: '/settings',
        name: 'Settings',
        component: () => import('@/views/SettingsPage.vue')
      },
      {
        path: '/contractFull',
        name: 'ContractFull',
        component: () => import('@/views/ContractFull.vue')
      },
      {
        path: '/consumptions',
        name: 'Consumptions',
        component: () => import('@/views/ConsumptionPage.vue')
      },
    ],beforeEnter: (to, from, next) => {
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
    path: '/:catchAll(.*)',
    name: 'NotFound',
    component: () => import('@/views/NotFound.vue')
  }
]

const router = new VueRouter({
  mode: 'history',
  fallback: true,
  base: process.env.BASE_URL,
  routes
})

export default router
