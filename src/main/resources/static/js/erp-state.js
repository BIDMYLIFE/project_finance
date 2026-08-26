(function (window) {
  window.MSG = function (key) {
    var msgs = window.__ERP_MESSAGES__;
    return (msgs && msgs[key]) || key;
  };

  function createInitialState() {
    return { loading: false, success: false, error: '', validationErrors: {}, networkError: false, empty: false, retryCount: 0 };
  }

  function setLoading(state) {
    state.loading = true;
    state.success = false;
    state.error = '';
    state.validationErrors = {};
    state.networkError = false;
    state.empty = false;
  }

  function setSuccess(state, data) {
    state.loading = false;
    state.success = true;
    state.error = '';
    state.validationErrors = {};
    state.networkError = false;
    state.empty = data === null || data === undefined || (Array.isArray(data) && data.length === 0);
  }

  function setError(state, error) {
    state.loading = false;
    state.success = false;
    state.networkError = !error.response;
    if (error.response && error.response.data) {
      var data = error.response.data;
      state.error = data.message || MSG('common.error.request_failed');
      if (data.fields && typeof data.fields === 'object') {
        state.validationErrors = data.fields;
      }
    } else if (error.code === 'ECONNABORTED') {
      state.error = MSG('common.error.timeout');
    } else {
      state.error = MSG('common.error.network');
    }
  }

  function reset(state) {
    Object.assign(state, createInitialState());
  }

  window.erpState = {
    create: createInitialState,
    setLoading: setLoading,
    setSuccess: setSuccess,
    setError: setError,
    reset: reset
  };
}(window));
