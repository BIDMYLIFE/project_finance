Vue.createApp({
  data: function () {
    return {
      identity: null,
      ui: window.erpState.create(),
      loggingOut: false,
      capabilities: window.erpCapabilities.list()
    };
  },
  mounted: function () { this.loadIdentity(); },
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
