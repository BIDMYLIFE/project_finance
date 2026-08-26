(function (window) {
  var base = '/payments';
  window.paymentsApi = {
    list: function (query, page) { return window.erpApi.client.get(base, { params: Object.assign({}, query, { page: page || 0, size: 20 }) }); },
    detail: function (id) { return window.erpApi.client.get(base + '/' + id); },
    create: function (payload) { return window.erpApi.client.post(base, payload); },
    createFromInvoices: function (payload) { return window.erpApi.client.post(base + '/from-invoices', payload); },
    post: function (id, bankAccountId) { return window.erpApi.client.post(base + '/' + id + '/post', null, { params: { bankAccountId: bankAccountId } }); },
    voidPayment: function (id) { return window.erpApi.client.post(base + '/' + id + '/void'); },
    print: function (id) { return window.erpApi.client.post(base + '/' + id + '/print'); },
    categories: function () { return window.erpApi.client.get(base + '/categories', { params: { page: 0, size: 100 } }); },
    createCategory: function (name) { return window.erpApi.client.post(base + '/categories', { name: name }); }
  };
}(window));
