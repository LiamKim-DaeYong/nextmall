function fn() {
  var config = {
    baseUrl: java.lang.System.getProperty('gateway.url')
  };
  
  karate.configure('connectTimeout', 30000);
  karate.configure('readTimeout', 30000);
  
  return config;
}
