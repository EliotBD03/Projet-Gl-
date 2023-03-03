import Vue from 'vue'
import App from './App.vue'
import router from './router'
import VueCookies from 'vue-cookies'
import infiniteScroll from 'vue-infinite-scroll'
import VueSweetalert2 from 'vue-sweetalert2';
import 'sweetalert2/dist/sweetalert2.min.css';

Vue.config.productionTip = false

Vue.use(VueCookies)
Vue. use(infiniteScroll)
Vue.use(VueSweetalert2);

new Vue({
  router,
  render: h => h(App)
}).$mount('#app')