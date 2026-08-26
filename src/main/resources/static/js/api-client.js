(function (window) {
  var client = axios.create({ baseURL: '/api/v1', timeout: 10000, withCredentials: true });
  var redirecting = false;
  var refreshRequest = null;

  function isAuthEndpoint(url) { return /\/auth\/(login|refresh|logout)(?:$|\?)/.test(url || ''); }

  function redirectToLogin() {
    if (redirecting || window.location.pathname === '/auth/login') return;
    redirecting = true;
    var path = window.location.pathname + window.location.search;
    var returnPath = path.startsWith('/') && !path.startsWith('//') ? encodeURIComponent(path) : '';
    window.location.assign('/auth/login' + (returnPath ? '?return=' + returnPath : ''));
  }

  function parseErrorDto(error) {
    var MSG = window.MSG || function (k) { return k; };
    if (!error.response) {
      if (error.code === 'ECONNABORTED') return { code: 'TIMEOUT', message: MSG('common.error.timeout') };
      return { code: 'NETWORK_ERROR', message: MSG('common.error.network') };
    }
    var data = error.response.data;
    if (data && data.code) return { code: data.code, message: data.message || MSG('common.error.request_failed'), fields: data.fields || {} };
    return { code: 'UNKNOWN', message: MSG('common.error.request_failed') };
  }

  client.interceptors.response.use(function (response) { return response; }, async function (error) {
    var request = error.config || {};
    if (error.response && error.response.status === 401 && !request._retry && !isAuthEndpoint(request.url)) {
      request._retry = true;
      refreshRequest = refreshRequest || axios.post('/api/v1/auth/refresh', null, { withCredentials: true, timeout: 10000 });
      try { await refreshRequest; refreshRequest = null; return client(request); }
      catch (refreshError) { refreshRequest = null; redirectToLogin(); return Promise.reject(refreshError); }
    }
    if (error.response && error.response.status === 401 && !isAuthEndpoint(request.url)) redirectToLogin();
    return Promise.reject(error);
  });

  window.erpApi = {
    client: client,
    parseError: parseErrorDto,
    currentIdentity: function () { return client.get('/auth/me'); },
    logout: function () { return axios.post('/api/v1/auth/logout', null, { withCredentials: true, timeout: 10000 }); }
  };
}(window));
