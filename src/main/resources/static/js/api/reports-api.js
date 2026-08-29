(function (window) {
  function normalizedQuery(query) {
    var params = Object.assign({}, query || {});
    if (typeof params.currencyCode === 'string') {
      params.currencyCode = params.currencyCode.trim().toUpperCase();
      if (!params.currencyCode) delete params.currencyCode;
    }
    return params;
  }

  window.reportsApi = {
    list: function (type, query) { return window.erpApi.client.get('/reports/' + encodeURIComponent(type), { params: normalizedQuery(query) }); },
    export: function (type, format, query) { return window.erpApi.client.get('/reports/' + encodeURIComponent(type) + '/export/' + format, { params: normalizedQuery(query), responseType: 'blob' }); },
    summary: function (query) { return window.erpApi.client.get('/reports/summary', { params: normalizedQuery(query) }); }
  };
}(window));
