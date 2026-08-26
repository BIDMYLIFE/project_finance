(function (window) {
  var registry = [
    { id: 'dashboard', labelKey: 'capability.dashboard', route: '/', available: true, owner: 'dashboard-post-auth-routing' },
    { id: 'customers', labelKey: 'capability.customers', route: '/customers', available: true, owner: 'financial-erp-core' },
    { id: 'products', labelKey: 'capability.products', route: null, available: false, owner: 'financial-erp-core' },
    { id: 'quotes', labelKey: 'capability.quotes', route: null, available: false, owner: 'financial-erp-core' },
    { id: 'invoices', labelKey: 'capability.invoices', route: null, available: false, owner: 'financial-erp-core' },
    { id: 'payments', labelKey: 'capability.payments', route: null, available: false, owner: 'financial-erp-core' },
    { id: 'banking', labelKey: 'capability.banking', route: null, available: false, owner: 'financial-erp-core' },
    { id: 'reporting', labelKey: 'capability.reporting', route: null, available: false, owner: 'erp-reporting' }
  ];

  function resolveLabel(item) {
    var MSG = window.MSG || function (k) { return k; };
    return MSG(item.labelKey);
  }

  window.erpCapabilities = {
    list: function () { return registry.map(function (c) { return { id: c.id, label: resolveLabel(c), route: c.route, available: c.available, owner: c.owner }; }); },
    available: function () { return registry.filter(function (c) { return c.available; }).map(function (c) { return { id: c.id, label: resolveLabel(c), route: c.route, available: c.available, owner: c.owner }; }); },
    unavailable: function () { return registry.filter(function (c) { return !c.available; }).map(function (c) { return { id: c.id, label: resolveLabel(c), route: c.route, available: c.available, owner: c.owner }; }); },
    find: function (id) { var c = registry.find(function (c) { return c.id === id; }); return c ? { id: c.id, label: resolveLabel(c), route: c.route, available: c.available, owner: c.owner } : null; }
  };
}(window));
