(function (window) {
  window.productsApi = {
    list: function (query, page) {
      return window.erpApi.client.get('/products', { params: { keyword: query.keyword || undefined, active: query.active === 'true', page: page, size: 20, sort: 'createdAt', direction: 'DESC' } });
    },
    create: function (product) { return window.erpApi.client.post('/products', product); },
    update: function (id, product) { return window.erpApi.client.put('/products/' + encodeURIComponent(id), product); },
    deactivate: function (id) { return window.erpApi.client.delete('/products/' + encodeURIComponent(id)); }
  };
}(window));
