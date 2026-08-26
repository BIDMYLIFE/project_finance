Vue.createApp({ data: () => ({ form: { organizationName: '', email: '', password: '' }, loading: false, message: '' }), methods: {
  MSG: function (key) { return window.MSG(key); },
  async submit() { this.loading = true; this.message = ''; try { await axios.post('/api/v1/auth/bootstrap', this.form); this.message = this.MSG('bootstrap.success'); await Swal.fire({ icon: 'success', title: this.MSG('bootstrap.success_title'), text: this.MSG('bootstrap.success_text') }); this.form = { organizationName: '', email: '', password: '' }; } catch (error) { this.message = error.response?.data?.message || this.MSG('bootstrap.error'); await Swal.fire({ icon: 'error', title: this.MSG('bootstrap.error'), text: this.message }); } finally { this.loading = false; }
  }
} }).mount('#bootstrap-app');
