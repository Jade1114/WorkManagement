import { createApp } from 'vue'
import { createPinia } from 'pinia'
import ElementPlus from 'element-plus'
import 'element-plus/dist/index.css'
import App from './App.vue'
import router from './router'
import './style.css'

const app = createApp(App)

// 注册 Pinia
app.use(createPinia())

// 注册 Router
app.use(router)

// 注册 Element Plus
app.use(ElementPlus)

app.mount('#app')