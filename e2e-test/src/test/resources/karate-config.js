function fn() {
  var gatewayUrl = java.lang.System.getProperty('gateway.url');
  var config = {
    baseUrl: gatewayUrl || 'http://localhost:18080'
  };

  karate.configure('connectTimeout', 30000);
  karate.configure('readTimeout', 30000);

  return config;
}
