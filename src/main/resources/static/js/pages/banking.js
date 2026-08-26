(function (window) {
  var app = Vue.createApp({
    data: function () { return { accounts: [], query: { keyword: '', active: 'true' }, page: 0, totalPages: 0, loading: false, saving: false, loadError: '', formError: '', fieldErrors: {}, editing: false, form: this.emptyForm ? this.emptyForm() : { id: null, accountName: '', currencyCode: 'TWD', openingBalance: '' }, loggingOut: false }; },
    computed: { busy: function () { return this.loading || this.saving; }, pageNumbers: function () { var start = Math.max(1, this.page + 1 - 2); var end = Math.min(this.totalPages, start + 4); start = Math.max(1, end - 4); var result = []; for (var n = start; n <= end; n += 1) result.push(n); return result; } },
    mounted: function () { this.loadAccounts(); },
    methods: {
      MSG: function (key) { return window.MSG(key); },
      emptyForm: function () { return { id: null, accountName: '', currencyCode: 'TWD', openingBalance: '' }; },
      loadAccounts: function () { var self = this; self.loading = true; self.loadError = ''; return window.bankingApi.list(self.query, self.page).then(function (response) { var data = response.data; self.accounts = Array.isArray(data.items) ? data.items : []; self.page = data.page || 0; self.totalPages = data.totalPages || 0; }).catch(function (error) { if (!(error.response && error.response.status === 401)) self.loadError = self.safeError(error, 'banking.error.load'); }).finally(function () { self.loading = false; }); },
      search: function () { this.page = 0; this.loadAccounts(); },
      clearSearch: function () { this.query.keyword = ''; this.query.active = 'true'; this.search(); },
      goTo: function (page) { if (page < 0 || page >= this.totalPages || page === this.page || this.busy) return; this.page = page; this.loadAccounts(); },
      openCreate: function () { this.editing = false; this.form = this.emptyForm(); this.clearFormErrors(); this.showModal(); },
      openEdit: function (account) { this.editing = true; this.form = { id: account.id, accountName: account.accountName || '', currencyCode: account.currencyCode || 'TWD', openingBalance: account.openingBalance }; this.clearFormErrors(); this.showModal(); },
      showModal: function () { bootstrap.Modal.getOrCreateInstance(document.getElementById('banking-modal')).show(); },
      clearFormErrors: function () { this.formError = ''; this.fieldErrors = {}; },
      fieldClass: function (field) { return this.fieldErrors[field] ? 'is-invalid' : ''; },
      validate: function () { var f = this.form, errors = {}; if (!f.accountName) errors.accountName = this.MSG('banking.form.required'); else if (f.accountName.length > 200) errors.accountName = this.MSG('banking.form.invalid_name'); if (!/^[A-Z]{3}$/.test(f.currencyCode)) errors.currencyCode = this.MSG('banking.form.invalid_currency'); if (f.openingBalance === '' || f.openingBalance === null || Number.isNaN(Number(f.openingBalance)) || !/^\d{1,15}(\.\d{1,4})?$/.test(String(f.openingBalance))) errors.openingBalance = this.MSG('banking.form.invalid_balance'); this.fieldErrors = errors; return Object.keys(errors).length === 0; },
      payload: function () { return { accountName: this.form.accountName, currencyCode: this.form.currencyCode, openingBalance: Number(this.form.openingBalance) }; },
      save: function () { var self = this; self.clearFormErrors(); if (!self.validate() || self.saving) return; self.saving = true; var editing = self.editing; var request = editing ? window.bankingApi.update(self.form.id, self.payload()) : window.bankingApi.create(self.payload()); request.then(function () { bootstrap.Modal.getOrCreateInstance(document.getElementById('banking-modal')).hide(); Swal.fire({ icon: 'success', title: self.MSG(editing ? 'banking.success.updated' : 'banking.success.created'), timer: 1200, showConfirmButton: false }); self.loadAccounts(); }).catch(function (error) { if (!(error.response && error.response.status === 401)) self.formError = self.safeError(error, 'banking.error.save'); }).finally(function () { self.saving = false; }); },
      deactivate: function (account) { var self = this; if (self.busy) return; Swal.fire({ icon: 'warning', title: self.MSG('banking.confirm.title'), text: self.MSG('banking.confirm.text'), showCancelButton: true, confirmButtonText: self.MSG('banking.confirm.confirm'), cancelButtonText: self.MSG('banking.confirm.cancel'), reverseButtons: true }).then(function (result) { if (!result.isConfirmed) return; self.loading = true; window.bankingApi.deactivate(account.id).then(function () { Swal.fire({ icon: 'success', title: self.MSG('banking.success.deactivated'), timer: 1200, showConfirmButton: false }); self.loadAccounts(); }).catch(function (error) { self.loadError = self.safeError(error, 'banking.error.deactivate'); self.loading = false; }); }); },
      safeError: function (error, fallback) { if (!error.response) return error.code === 'ECONNABORTED' ? this.MSG('common.error.timeout') : this.MSG('common.error.network'); return this.MSG(fallback); },
      logout: function () { var self = this; self.loggingOut = true; window.erpApi.logout().finally(function () { window.location.assign('/auth/login'); }); }
    }
  });
  app.mount('#banking-app');
}(window));
