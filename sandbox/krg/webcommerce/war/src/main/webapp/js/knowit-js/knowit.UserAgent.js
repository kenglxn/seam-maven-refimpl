/*
 * File        : knowit.UserAgent.js
 * Version     : 1.0
 * Author      : Leif Olsen <leif.olsen@knowit.no>
 * Copyright   : (c) 2008 knowit.no
 * Description : 
 * Notes       : 
 * Limitations : 
 * Dependencies: 
 * Created     : 20080929
 * History     : 
 */
 
window.status = 'Loading [knowit.UserAgent.js]';

// Define the namespace 
if(knowit == undefined) {
  var knowit = {};
}

if(!knowit.UserAgent) {
knowit.UserAgent = ( function() {

  // Private attributes and methods
  var ua;
  
  init = function() {
    if(!ua) {
      var nua = navigator.userAgent, m;
      ua = {
        gecko : 0,
        ie    : 0,
        webkit: 0,
        opera : 0
      };
      m = nua.match(/AppleWebKit\/(\S*)/);
      if (m && m[1]) {
        ua.webkit = parseFloat(m[1]);
      } 
      else {
        m = nua.match(/MSIE\s([^;]*)/);
        if (m && m[1]) {
          ua.ie = parseFloat(m[1]);
        }
        else {
          m = nua.match(/Opera\/(\S*)/);
          if (m && m[1]) {
            ua.opera = parseFloat(m[1]);
          } 
          else if ((/Gecko\/(\S*)/).test(nua)) {
            ua.gecko = 1;
            m = nua.match(/rv:([^\s\)]*)/);

            if (m && m[1]) {
              ua.gecko = parseFloat(m[1]);
            }
          }
        }
      }
    }
    return ua;
  };

return ({
  // Public attributes and methods
  gecko : ua ? ua.gecko  : init().gecko,
  ie    : ua ? ua.ie     : init().ie,
  webkit: ua ? ua.webkit : init().webkit,
  opera : ua ? ua.opera  : init().opera,
  
  toString: function() {
    if( !ua ) {
      init();
    }
    return ( 'gecko: ' + ua.gecko + ', ie: ' + ua.ie + ', webkit: ' + ua.webkit + ', opera: ' + ua.opera );
  }
}); //~return

} ) (); //~anonymous function immediately invoked with ()

// Shortcut
knowit.ua = knowit.UserAgent;
};


window.status = '';
