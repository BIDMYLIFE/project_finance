Vue.createApp({
  data: function () { var today = new Date().toISOString().slice(0, 10); var start = new Date(Date.now() - 30 * 86400000).toISOString().slice(0, 10); return { types: ['expenses','pending-deposits','payment-categories','bank-balance','invoice-status','receivable-aging','tax','cash-flow'], type: 'expenses', query: { from: start, to: today, currencyCode: '', page: 0, size: 20, sort: 'date', direction: 'DESC' }, report: null, loading: false, error: '', exporting: false, loggingOut: false }; },
  methods: {
    MSG: function (key) { return window.MSG(key); },
    load: function () { var self = this; self.loading = true; self.error = ''; window.reportsApi.list(self.type, self.query).then(function (r) { self.report = r.data; }).catch(function () { self.error = self.MSG('reporting.error.load'); }).finally(function () { self.loading = false; }); },
    exportFile: function (format) { var self = this; self.exporting = true; window.reportsApi.export(self.type, format, self.query).then(function (r) { var blob = new Blob([r.data]); var link = document.createElement('a'); link.href = URL.createObjectURL(blob); link.download = 'report-' + self.type + '.' + format; link.click(); URL.revokeObjectURL(link.href); }).catch(function () { self.error = self.MSG('reporting.error.export'); }).finally(function () { self.exporting = false; }); },
    logout: function () { var self = this; self.loggingOut = true; window.erpApi.logout().finally(function () { window.location.assign('/auth/login'); }); }
  }, mounted: function () { var value = new URLSearchParams(window.location.search).get('type'); if (value && this.types.indexOf(value) >= 0) this.type = value; this.load(); }
}).mount('#reporting-app');
