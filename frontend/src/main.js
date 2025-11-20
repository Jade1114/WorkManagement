import { createApp } from 'vue'
import App from './App.vue'
import { createPinia } from 'pinia'

import 'element-plus/dist/index.css'
import './style.css'
import axios from "axios";
import router from './router'

const app = createApp(App)
const pinia = createPinia()

axios.defaults.baseURL = 'http://localhost:8080'

app.use(router)
app.use(pinia)
app.mount('#app')
