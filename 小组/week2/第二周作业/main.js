import Vue from 'vue'
//import App from './App.vue'
import e1 from './views/ExampleOne.vue'
import router from './router'
import store from './store'
//import { apply } from 'core-js/library/es6/reflect'

Vue.config.productionTip = false

new Vue({
  router,
  store,
  render: h => h(e1)
}).$mount('#app')
