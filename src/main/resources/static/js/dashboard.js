Vue.createApp({
  data: function () {
    return {
      identity: null,
      ui: window.erpState.create(),
      loggingOut: false,
      capabilities: window.erpCapabilities.list(),
      reports: { loading: false, error: '', data: null }
    };
  },
  mounted: function () { this.loadIdentity(); this.loadReports(); },
  methods: {
    MSG: function (key) { return window.MSG(key); },
    loadIdentity: function () {
      var self = this;
      window.erpState.setLoading(self.ui);
      window.erpApi.currentIdentity()
        .then(function (response) { window.erpState.setSuccess(self.ui, response.data); self.identity = response.data; })
        .catch(function (error) {
          if (error.response && error.response.status === 401) return;
          window.erpState.setError(self.ui, error);
        });
    },
    loadReports: function () {
      var self = this; self.reports.loading = true; self.reports.error = '';
      var to = new Date().toISOString().slice(0, 10), from = new Date(Date.now() - 30 * 86400000).toISOString().slice(0, 10);
      window.reportsApi.summary({ from: from, to: to }).then(function (response) { self.reports.data = response.data; }).catch(function () { self.reports.error = self.MSG('dashboard.reports.error'); }).finally(function () { self.reports.loading = false; });
    },
    logout: function () {
      var self = this;
      self.loggingOut = true;
      window.erpApi.logout()
        .then(function () {
          Swal.fire({ icon: 'success', title: MSG('common.logout.title'), timer: 900, showConfirmButton: false });
        })
        .catch(function () {})
        .finally(function () { window.location.assign('/auth/login'); });
    },
    capabilityClass: function (cap) {
      return cap.available ? 'nav-link' : 'nav-link disabled';
    }
  }
}).mount('#dashboard-app');
