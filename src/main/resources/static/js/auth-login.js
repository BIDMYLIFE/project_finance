Vue.createApp({ data: () => ({ form: { email: '', password: '' }, loading: false, message: '' }), methods: {
  async submit() { this.loading = true; this.message = ''; try { await axios.post('/api/v1/auth/login', this.form); this.message = '登入成功。'; await Swal.fire({ icon: 'success', title: '登入成功' }); } catch (error) { this.message = error.response?.data?.message || '登入失敗'; await Swal.fire({ icon: 'error', title: '登入失敗', text: this.message }); } finally { this.loading = false; }
  }
} }).mount('#login-app');