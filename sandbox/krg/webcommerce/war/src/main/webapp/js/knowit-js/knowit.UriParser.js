/*
 * File        : knowit.UriParser.js
 * Version     : 1.2.1
 * Author      : Steven Levithan <stevenlevithan.com>
 * Copyright   : (c) 2007 Steven Levithan <stevenlevithan.com>
 *                see: http://blog.stevenlevithan.com/archives/parseuri
 *                MIT License
 * Description : 
 * Notes       : 
 * Limitations : 
 * Dependencies: 
 * Created     : 
 * History     : 20080929 - Modified for knowit by Leif Olsen
 */
 
window.status = 'Loading [knowit.UriParser.js]';

// Define the knowit namespace 
if(knowit == undefined) {
  var knowit = {};
}

if( !knowit.UriParser ) {
knowit.UriParser = ( function() {

  // Private attributes and methods
  var VERSION = '1.2.1';

return ({ //Public attributes and methods

  /**
   * @method parse
   * @param uriToParse {String} the uri to parse
   * @return {Array} Hash array with name value pairs with the following names:
   *                   anchor, query, file, directory, path, relative, port, host, password, user, 
   *                   userInfo, authority, protocol, source, queryKey, object
   *                   See: see: http://blog.stevenlevithan.com/archives/parseuri for usage.
   */
  parse: function( uriToParse ) {
    var	o   = this.options,
        m   = o.parser[o.strictMode ? "strict" : "loose"].exec(uriToParse),
        uri = {},
        i   = 14;

    while (i--) {
      uri[o.key[i]] = m[i] || "";
    }
    uri[o.q.name] = {};
    uri[o.key[12]].replace(o.q.parser, function ($0, $1, $2) {
      if ($1) uri[o.q.name][$1] = $2;
    });

    return uri;
  },
  
  options: {
    strictMode: true,
    key: ["source","protocol","authority","userInfo","user","password","host","port","relative","path","directory","file","query","anchor"],
    q:   {
      name:   "queryKey",
      parser: /(?:^|&)([^&=]*)=?([^&]*)/g
    },

    parser: {
      strict: /^(?:([^:\/?#]+):)?(?:\/\/((?:(([^:@]*):?([^:@]*))?@)?([^:\/?#]*)(?::(\d*))?))?((((?:[^?#\/]*\/)*)([^?#]*))(?:\?([^#]*))?(?:#(.*))?)/,
      loose:  /^(?:(?![^:@]+:[^:@\/]*@)([^:\/?#.]+):)?(?:\/\/)?((?:(([^:@]*):?([^:@]*))?@)?([^:\/?#]*)(?::(\d*))?)(((\/(?:[^?#](?![^?#\/]*\.[^?#\/.]+(?:[?#]|$)))*\/?)?([^?#\/]*))(?:\?([^#]*))?(?:#(.*))?)/
    }
  },

  toString: function() { 
    return ( 'knowit.UriParser, version ' + VERSION 
            +'\n(c) 2007 Steven Levithan <stevenlevithan.com>\nMIT License.' );
  }
  
}); //~return

} ) (); //~anonymous function immediately invoked with ()
};

window.status = '';
