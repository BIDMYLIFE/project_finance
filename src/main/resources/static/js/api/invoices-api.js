(function (window) {
  var base = '/invoices';
  window.invoicesApi = {
    list: function (query, page) { return window.erpApi.client.get(base, { params: { keyword: query.keyword || undefined, status: query.status || undefined, fromDate: query.fromDate || undefined, toDate: query.toDate || undefined, page: page, size: 20, sort: 'createdAt', direction: 'DESC' } }); },
    openForPayment: function (status) { return window.erpApi.client.get(base, { params: { status: status, page: 0, size: 100, sort: 'createdAt', direction: 'ASC' } }); },
    detail: function (id) { return window.erpApi.client.get(base + '/' + encodeURIComponent(id)); },
    create: function (payload) { return window.erpApi.client.post(base, payload); },
    update: function (id, payload) { return window.erpApi.client.put(base + '/' + encodeURIComponent(id), payload); },
    issue: function (id) { return window.erpApi.client.post(base + '/' + encodeURIComponent(id) + '/issue'); },
    cancel: function (id) { return window.erpApi.client.post(base + '/' + encodeURIComponent(id) + '/cancel'); }
  };
}(window));
