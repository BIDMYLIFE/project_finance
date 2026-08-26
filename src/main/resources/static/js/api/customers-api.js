(function (window) {
  window.customersApi = {
    list: function (query, page) { return window.erpApi.client.get('/customers', { params: { keyword: query.keyword || undefined, active: query.active === 'true', page: page, size: 20, sort: 'createdAt', direction: 'DESC' } }); },
    create: function (customer) { return window.erpApi.client.post('/customers', customer); },
    update: function (id, customer) { return window.erpApi.client.put('/customers/' + encodeURIComponent(id), customer); },
    deactivate: function (id) { return window.erpApi.client.delete('/customers/' + encodeURIComponent(id)); }
  };
}(window));