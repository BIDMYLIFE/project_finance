Vue.createApp({ data: () => ({ form: { email: '', password: '' }, loading: false, message: '' }), methods: {
  MSG: function (key) { return window.MSG(key); },
  async submit() { this.loading = true; this.message = ''; try { await axios.post('/api/v1/auth/login', this.form, { withCredentials: true }); window.location.assign(this.safeReturnPath()); } catch (error) { this.message = error.response?.data?.message || this.MSG('login.error'); await Swal.fire({ icon: 'error', title: this.MSG('login.error'), text: this.message }); } finally { this.loading = false; }
  },
  safeReturnPath() { const value = new URLSearchParams(window.location.search).get('return') || '/'; return value.startsWith('/') && !value.startsWith('//') && !value.includes('://') ? value : '/'; }
} }).mount('#login-app');
