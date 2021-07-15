(function (_) {
  'use strict';
  function main(args) {
    var fireFunctions = require('firebase-functions');
    exports.myTestFun = fireFunctions.https.onRequest(_no_name_provided_$factory());
  }
  function _no_name_provided_() {
  }
  _no_name_provided_.prototype.invoke_4bbw1s_k$ = function (request, response) {
    return response.send('Hi from kotlin!');
  };
  _no_name_provided_.$metadata$ = {
    kind: 'class',
    interfaces: []
  };
  function _no_name_provided_$factory() {
    var i = new _no_name_provided_();
    return function (p1, p2) {
      return i.invoke_4bbw1s_k$(p1, p2);
    };
  }
  main([]);
  return _;
}(module.exports));
