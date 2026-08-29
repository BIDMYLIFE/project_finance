(function (window) {
  var base = '/expenses';
  window.expensesApi = {
    list: function (query, page) { return window.erpApi.client.get(base, { params: Object.assign({}, query, { page: page || 0, size: 20 }) }); },
    create: function (payload) { return window.erpApi.client.post(base, payload); },
    update: function (id, payload) { return window.erpApi.client.put(base + '/' + id, payload); },
    confirm: function (id, bankAccountId) { return window.erpApi.client.post(base + '/' + id + '/confirm', null, { params: { bankAccountId: bankAccountId } }); },
    voidExpense: function (id) { return window.erpApi.client.delete(base + '/' + id); }
  };
}(window));
