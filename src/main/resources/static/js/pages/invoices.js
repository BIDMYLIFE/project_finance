(function (window) {
  window.MSG = window.MSG || function (key) {
    var messages = window.__ERP_MESSAGES__;
    return (messages && messages[key]) || key;
  };

  var app = Vue.createApp({
    data: function () { return { invoices: [], customers: [], products: [], selectedInvoice: null, query: { keyword: '', status: '', fromDate: '', toDate: '' }, page: 0, totalPages: 0, loading: false, saving: false, formError: '', loadError: '', fieldErrors: {}, editing: false, form: { id: null, customerId: '', currencyCode: 'TWD', invoiceDate: new Date().toISOString().slice(0, 10), dueDate: new Date(Date.now() + 30 * 86400000).toISOString().slice(0, 10), lines: [{ productId: '', quantity: 1, discount: 0 }] }, loggingOut: false }; },
    computed: { busy: function () { return this.loading || this.saving; }, pageNumbers: function () { var start = Math.max(1, this.page + 1 - 2), end = Math.min(this.totalPages, start + 4), result = []; start = Math.max(1, end - 4); for (var n = start; n <= end; n += 1) result.push(n); return result; }, formSubtotal: function () { var self = this; return this.form.lines.reduce(function (sum, line) { var p = self.product(line.productId); return sum + (p ? Math.max(0, Number(line.quantity) * Number(p.unitPrice) - Number(line.discount || 0)) : 0); }, 0).toFixed(4); }, formTax: function () { var self = this; return this.form.lines.reduce(function (sum, line) { var p = self.product(line.productId); var base = p ? Math.max(0, Number(line.quantity) * Number(p.unitPrice) - Number(line.discount || 0)) : 0; return sum + base * (p ? Number(p.taxRate) / 100 : 0); }, 0).toFixed(4); }, formGrandTotal: function () { return (Number(this.formSubtotal) + Number(this.formTax)).toFixed(4); } },
    mounted: function () {
      var self = this;
      this.loadLookups().then(function () {
        return self.loadInvoices();
      }).catch(function (error) {
        if (!(error.response && error.response.status === 401)) self.loadError = self.safeError(error, 'invoices.error.load');
      });
    },
    methods: {
      MSG: function (key) { return window.MSG(key); },
      emptyForm: function () { return { id: null, customerId: '', currencyCode: 'TWD', invoiceDate: new Date().toISOString().slice(0, 10), dueDate: new Date(Date.now() + 30 * 86400000).toISOString().slice(0, 10), lines: [{ productId: '', quantity: 1, discount: 0 }] }; },
      product: function (id) { return this.products.find(function (p) { return p.id === id; }); },
      lineTotal: function (line) { var p = this.product(line.productId); return p ? Math.max(0, Number(line.quantity) * Number(p.unitPrice) - Number(line.discount || 0)).toFixed(4) : '0.0000'; },
      loadLookups: function () { var self = this; return Promise.all([window.customersApi.list({ keyword: '', active: 'true' }, 0), window.productsApi.list({ keyword: '', active: 'true' }, 0)]).then(function (responses) { self.customers = responses[0].data.items || []; self.products = responses[1].data.items || []; }); },
      loadInvoices: function () { var self = this; self.loading = true; self.loadError = ''; return window.invoicesApi.list(self.query, self.page).then(function (response) { var data = response.data; self.invoices = data.items || []; self.page = data.page || 0; self.totalPages = data.totalPages || 0; }).catch(function (error) { if (!(error.response && error.response.status === 401)) self.loadError = self.safeError(error, 'invoices.error.load'); }).finally(function () { self.loading = false; }); },
      search: function () { this.page = 0; this.loadInvoices(); },
      clearSearch: function () { this.query = { keyword: '', status: '', fromDate: '', toDate: '' }; this.search(); },
      goTo: function (page) { if (page < 0 || page >= this.totalPages || page === this.page || this.busy) return; this.page = page; this.loadInvoices(); },
      openCreate: function () { this.editing = false; this.form = this.emptyForm(); this.clearErrors(); this.showModal(); },
      openEdit: function (invoice) { if (invoice.status !== 'DRAFT') return; var self = this; window.invoicesApi.detail(invoice.id).then(function (response) { var value = response.data; self.editing = true; self.form = { id: value.id, customerId: value.customerId, currencyCode: value.currencyCode, invoiceDate: value.invoiceDate, dueDate: value.dueDate, lines: (value.lines || []).map(function (line) { return { productId: line.productId, quantity: line.quantity, discount: line.discount }; }) }; self.clearErrors(); self.showModal(); }); },
      openDetail: function (invoice) { var self = this; window.invoicesApi.detail(invoice.id).then(function (response) { self.selectedInvoice = response.data; bootstrap.Modal.getOrCreateInstance(document.getElementById('invoice-detail-modal')).show(); }); },
      printReceipt: function (invoice) { if (invoice.status !== 'ISSUED' || this.busy) return; window.open('/invoices/receipt/' + encodeURIComponent(invoice.id), '_blank', 'noopener'); },
      showModal: function () { bootstrap.Modal.getOrCreateInstance(document.getElementById('invoice-modal')).show(); },
      clearErrors: function () { this.formError = ''; this.fieldErrors = {}; },
      fieldClass: function (field) { return this.fieldErrors[field] ? 'is-invalid' : ''; },
      addLine: function () { this.form.lines.push({ productId: '', quantity: 1, discount: 0 }); },
      removeLine: function (index) { if (this.form.lines.length > 1) this.form.lines.splice(index, 1); },
      validate: function () { var f = this.form, errors = {}; if (!f.customerId) errors.customerId = this.MSG('invoices.form.required'); if (!f.invoiceDate || !f.dueDate || f.dueDate < f.invoiceDate) errors.dates = this.MSG('invoices.form.invalid'); if (!f.lines.length || f.lines.some(function (line) { return !line.productId || Number(line.quantity) <= 0 || Number(line.discount) < 0; })) errors.lines = this.MSG('invoices.form.invalid'); this.fieldErrors = errors; return Object.keys(errors).length === 0; },
      payload: function () { return { customerId: this.form.customerId, currencyCode: this.form.currencyCode, invoiceDate: this.form.invoiceDate, dueDate: this.form.dueDate, lines: this.form.lines.map(function (line) { return { productId: line.productId, quantity: Number(line.quantity), discount: Number(line.discount || 0) }; }) }; },
      save: function () { var self = this; self.clearErrors(); if (!self.validate() || self.saving) return; self.saving = true; var request = self.editing ? window.invoicesApi.update(self.form.id, self.payload()) : window.invoicesApi.create(self.payload()); request.then(function () { bootstrap.Modal.getOrCreateInstance(document.getElementById('invoice-modal')).hide(); Swal.fire({ icon: 'success', title: self.MSG(self.editing ? 'invoices.success.updated' : 'invoices.success.created'), timer: 1200, showConfirmButton: false }); self.loadInvoices(); }).catch(function (error) { if (!(error.response && error.response.status === 401)) self.formError = self.safeError(error, 'invoices.error.save'); }).finally(function () { self.saving = false; }); },
      lifecycle: function (invoice, action) { var self = this; if (self.busy) return; Swal.fire({ icon: 'warning', title: self.MSG(action === 'issue' ? 'invoices.confirm.issue_title' : 'invoices.confirm.cancel_title'), text: self.MSG('invoices.confirm.text'), showCancelButton: true, confirmButtonText: self.MSG('invoices.confirm.confirm'), cancelButtonText: self.MSG('invoices.confirm.cancel'), reverseButtons: true }).then(function (result) { if (!result.isConfirmed) return; self.loading = true; var request = action === 'issue' ? window.invoicesApi.issue(invoice.id) : window.invoicesApi.cancel(invoice.id); request.then(function () { Swal.fire({ icon: 'success', title: self.MSG(action === 'issue' ? 'invoices.success.issued' : 'invoices.success.cancelled'), timer: 1200, showConfirmButton: false }); self.loadInvoices(); }).catch(function (error) { self.loadError = self.safeError(error, 'invoices.error.lifecycle'); self.loading = false; }); }); },
      statusLabel: function (status) { return this.MSG('invoices.status.' + status.toLowerCase()); },
      safeError: function (error, fallback) { if (!error.response) return error.code === 'ECONNABORTED' ? this.MSG('common.error.timeout') : this.MSG('common.error.network'); return this.MSG(fallback); },
      logout: function () { var self = this; self.loggingOut = true; window.erpApi.logout().finally(function () { window.location.assign('/auth/login'); }); }
    }
  }); app.mount('#invoices-app');
}(window));
