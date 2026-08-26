(function (window) {
  var base = '/bank-accounts';
  window.bankingApi = {
    list: function (query, page) { return window.erpApi.client.get(base, { params: { keyword: query.keyword || undefined, active: query.active === 'true', page: page, size: 20, sort: 'createdAt', direction: 'DESC' } }); },
    create: function (payload) { return window.erpApi.client.post(base, payload); },
    update: function (id, payload) { return window.erpApi.client.put(base + '/' + id, payload); },
    deactivate: function (id) { return window.erpApi.client.delete(base + '/' + id); }
  };
}(window));
