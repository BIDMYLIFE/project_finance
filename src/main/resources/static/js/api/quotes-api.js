(function (window) {
  window.quotesApi = {
    list: function (query, page) { return window.erpApi.client.get('/quotes', { params: { keyword: query.keyword || undefined, status: query.status || undefined, page: page, size: 20, sort: 'createdAt', direction: 'DESC' } }); },
    detail: function (id) { return window.erpApi.client.get('/quotes/' + encodeURIComponent(id)); },
    create: function (quote) { return window.erpApi.client.post('/quotes', quote); },
    update: function (id, quote) { return window.erpApi.client.put('/quotes/' + encodeURIComponent(id), quote); },
    transition: function (id, action) { return window.erpApi.client.post('/quotes/' + encodeURIComponent(id) + '/' + action); }
  };
}(window));
