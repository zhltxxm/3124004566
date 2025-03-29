<template>
  <div>
    <div>
      <button @click="isLogin = true">登录</button>
      <button @click="isLogin = false">注册</button>
    </div>

    <div v-if="isLogin">
      <div>
        <input type="email" v-model="login.email" placeholder="邮箱">
        <div>{{ loginErrors.email }}</div>
      </div>
      <div>
        <input type="password" v-model="login.password" placeholder="密码">
        <div>{{ loginErrors.password }}</div>
      </div>
      <button @click="handleSubmit">登录</button>
    </div>

    <div v-else>
      <div>
        <input type="email" v-model="register.email" placeholder="邮箱">
        <div>{{ registerErrors.email }}</div>
      </div>
      <div>
        <input type="tel" v-model="register.phone" placeholder="手机号">
        <div>{{ registerErrors.phone }}</div>
      </div>
      <div>
        <input type="password" v-model="register.password" placeholder="密码">
        <div>{{ registerErrors.password }}</div>
      </div>
      <div>
        <input type="password" v-model="register.confirmPassword" placeholder="确认密码">
        <div>{{ registerErrors.confirmPassword }}</div>
      </div>
      <button @click="handleSubmit">注册</button>
    </div>
  </div>
</template>

<script>
export default {
  data() {
    return {
      isLogin: true,
      login: { email: '', password: '' },
      loginErrors: { email: '', password: '' },
      register: { email: '', phone: '', password: '', confirmPassword: '' },
      registerErrors: { email: '', phone: '', password: '', confirmPassword: '' }
    }
  },
  methods: {
    validateEmail(email) {
      return /^\w+@[a-z0-9]+\.[a-z]{2,4}$/i.test(email)
    },
    validatePhone(phone) {
      return /^1[3-9]\d{9}$/.test(phone)
    },
    handleSubmit() {
      let isValid = true
      
      if (this.isLogin) {
        // 登录验证
        if (!this.login.email) {
          this.loginErrors.email = '邮箱必填'
          isValid = false
        } else if (!this.validateEmail(this.login.email)) {
          this.loginErrors.email = '邮箱格式错误'
          isValid = false
        }

        if (!this.login.password) {
          this.loginErrors.password = '密码必填'
          isValid = false
        } else if (this.login.password.length < 6) {
          this.loginErrors.password = '密码至少6位'
          isValid = false
        }
      } else {
        // 注册验证
        if (!this.register.email) {
          this.registerErrors.email = '邮箱必填'
          isValid = false
        } else if (!this.validateEmail(this.register.email)) {
          this.registerErrors.email = '邮箱格式错误'
          isValid = false
        }

        if (!this.register.phone) {
          this.registerErrors.phone = '手机号必填'
          isValid = false
        } else if (!this.validatePhone(this.register.phone)) {
          this.registerErrors.phone = '手机号格式错误'
          isValid = false
        }

        if (!this.register.password) {
          this.registerErrors.password = '密码必填'
          isValid = false
        } else if (this.register.password.length < 6) {
          this.registerErrors.password = '密码至少6位'
          isValid = false
        }

        if (!this.register.confirmPassword) {
          this.registerErrors.confirmPassword = '确认密码必填'
          isValid = false
        } else if (this.register.password !== this.register.confirmPassword) {
          this.registerErrors.confirmPassword = '密码不一致'
          isValid = false
        }
      }

      if (isValid) {
        alert(this.isLogin ? '登录成功' : '注册成功')
      }
    }
  }
}
</script>