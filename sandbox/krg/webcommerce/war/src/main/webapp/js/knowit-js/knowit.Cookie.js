/* 
 * File        : knowit.Cookie.js
 * Version     : @see knowit.Cookie.VERSION
 * Author      : Leif Olsen, leif.olsen@knowit.no
 * Copyright   : (c) 2008
 * Description : Cookie wrapper/factory for document.cookie
 *
 *               Description is copy/paste from quirksmode.org.
 *
 *               A cookie contains the following elements:
 *                 1. A name-value pair containing the actual data
 *                 2. An expiry date after which it is no longer valid
 *                 3. The domain and path of the server it should be sent to
 *
 *               name-value: Each cookie has a name-value pair that contains the actual information. 
 *                 The name of the cookie is for your benefit, you will search for this name when reading 
 *                 out the cookie information. If you want to read out the cookie you search for the 
 *                 name and see what value is attached to it. Read out this value. Of course you yourself 
 *                 have to decide which value(s) the cookie can have and to write the scripts to deal 
 *                 with these value(s).
 *
 *               Expiry date: Each cookie has an expiry date after which it is trashed. If you don't specify 
 *                 the expiry date the cookie is trashed when you close the browser. This expiry 
 *                 date should be in UTC (Greenwich) time in the format created by the 
 *                 Date.toGMTString() method
 *
 *               Domain and path : The domain tells the browser to which domain the cookie should be sent. 
 *                 If you don't specify it, it becomes the domain of the page that sets the cookie, 
 *                 e.g. www.quirksmode.org. Please note that the purpose of the domain is to allow cookies 
 *                 to cross sub-domains. My cookie will not be read by search.quirksmode.org because 
 *                 its domain is www.quirksmode.org . When I set the domain to quirksmode.org, 
 *                 the search sub-domain may also read the cookie. I cannot set the cookie domain to a domain 
 *                 I'm not in, I cannot make the domain www.microsoft.com . 
 *                 Only quirksmode.org is allowed, in this case.
 *                 The path gives you the chance to specify a directory where the cookie is active. 
 *                 So if you want the cookie to be only sent to pages in the directory cgi-bin, 
 *                 set the path to /cgi-bin. Usually the path is set to /, which means the cookie 
 *                 is valid throughout the entire domain. This script does so, so the cookies you can set on 
 *                 this page will be sent to any page in the www.quirksmode.org domain (though only this page 
 *                 has a script that searches for the cookies and does something with them).
 *
 * Notes       : 
 * Limitations : Google Chrome doesn't accept cookies from web pages on a local file system
 *               (paths like file:///C:/websites/foo.html)
 *               see: http://code.google.com/p/chromium/issues/detail?id=535
 * Dependencies: knowit.String.js
 * Created     : 20081111
 * History     : 
 */

 
window.status = 'Loading [knowit.Cookie.js]';
 
// Define the knowit namespace 
if(knowit == undefined) {
  var knowit = {};
}

if( !knowit.Cookie ) {

/**
 * Creates a knowit.Cookie object
 * @class Cookie
 * @constructor
 * @param cookieString {String} The cookie string with the following format:
 *        cookieString = "cookiename=cookievalue"
 *        Note: 
 *           1: The cookieString must at least contain the name of the cookie, e.g. 
 *              cookieString="mycookie".
 *           2: The cookievalue part of the cookieString can optionally 
 *              consist of name/value pairs where name/value is separated by ':' and each 
 *              name/value pair is separated by ',', e.g. cookieString = "mycookie=a:1,b:2".
 * @param options {Object} having the following properties:
 *   @property expires {Date} Expiration date of the cookie (default: end of current session)
 *   @property maxAge {int} Lifetime in seconds. Default value is 0
 *   @property domain {String} Domain where the cookie is valid (default: domain of calling document)
 *   @property path {String} Path where the cookie is valid (default: path of calling document)
 *   @property secure {boolean} value indicating if the cookie transmission requires a secure 
 *             transmission (default: false)
 * @throws knowit.Cookie.RequiredParameterException if the cookieString parameter is empty
 */
knowit.Cookie = function( cookieString, options ) {
  cookieString = (cookieString || '').trim();
  if( cookieString.length ) {
    this._value = '';
    var re= /\s?(.*?)=(.*?);/,  // Matches "cookiename=cookievalue"
        s = cookieString + ';',
        m = re.exec( s ),
        p, values;

    if( m ) {
      // m[0] is the matched string
      // m[1] is the cookie name
      // m[2] is the value
      this._name = m[1].trim();
      if( m.length > 2 ) {
        this._value = m[2];
        // Split value into name/vaue pairs
        values = m[2].splitAttributes( ',', ':' );
        for ( p in values ) {
          this[p] = values[p];
        }
      }
    }
    else {
      this._name = cookieString;
    }
    this.setOptions( options );
  }
  else {
    throw(knowit.Cookie.RequiredParameterException);        
  }
};

/**
 * Set options
 * @method setOptions
 * @param options {Object} @see knowit.Cookie = function( name, options )
 */
knowit.Cookie.prototype.setOptions = function( options ) {
  options = options || {};
  
  /**
   * @property _expires {Date}
   */
  this._expires = options.expires;

  /**
   * @property _expires {int}
   */
  this._maxAge = options.maxAge;
  
  /**
   * @property _domain {String}
   */
  this._domain  = options.domain;
  
  /**
   * @property _path {String}
   */
  this._path    = options.path;
  
  /**
   * @property _secure {boolean}
   */
  this._secure  = options.secure;
}

/**
 * Creates a cookie string that can be assigned into document.cookie.
 * @method _createCookieString
 * @private
 * @param encodeCookieProps {boolean} indicates whether cookie property values should be encoded, default false
 */
knowit.Cookie.prototype._createCookieString = function( encodeCookieProps ) {
  var s = '', expires, maxAge;
  
  // Extract cookie properties
  for( var p in this ) {
    if ( (p.charAt(0) == '_') || ((typeof this[p]) == 'function') ) {
      continue; // Ignore private attributes and methods
    }
    s += (s ? ',' : '')  + p + ':' + this[p];
  }
  if( !s ) {
    s = this._value;
  }
  if( s && encodeCookieProps ) {
    s = knowit.Cookie.encode(s);
  }
  // Calculate time to live
  if( this._expires ) {
    expires = this._expires;
    maxAge  = this._maxAge != undefined ? this._maxAge : (expires - new Date()) / 1000;
  }
  else if( this._maxAge != undefined ) {
    maxAge = this._maxAge;
    expires = this._expires ? this._expires : new Date( new Date().getTime() + maxAge * 1000 );
  }
  return this._name + "=" + s 
    + (expires             ? "; expires=" + expires.toGMTString() : "") 
    + (maxAge != undefined ? "; max-age=" + maxAge : "") 
    + (this._path          ? "; path="    + this._path : "") 
    + (this._domain        ? "; domain="  + this._domain : "") 
    + (this._secure        ? "; secure" : "");
};

/**
 * Get cookie attributes as a hash array
 * @method getAttributesHashArray
 * @return {Array} the cookie attributes as a hash array where attribute name is the index 
 * used to get the corresponding value.
 */
knowit.Cookie.prototype.getAttributesHashArray = function() {
  var result = [];
  for( var p in this ) {
    if ( p.charAt(0) == '_' || typeof this[p] == 'function' ) {
      continue; // Ignore private properties and methods
    }
    result[p] = this[p];
  }
  return result;
}

/**
 * Get cookie attribute
 * @method getAttribute
 * @param name {String} The name of the attribute to get.
 * @return {Variant} the attribute value for the given name or undefined if the given name does not match.
 */
knowit.Cookie.prototype.getAttribute = function( name ) {
  var result;
  name = (name || '').trim();
  if( name && name.charAt(0) != '_' ) {
    if( this[name] && typeof this[name] != 'function' ) {
      result = this[name];
    }
  }
  return result;
}

/**
 * Set cookie attribute
 * @method setAttribute
 * @param name {String} The name of the attribute to set.
 * @param {Variant} value The value of the attribute with the given name.
 */
knowit.Cookie.prototype.setAttribute = function( name, value ) {
  name = (name || '').trim();
  if( name && name.charAt(0) != '_' && value && typeof value != 'function' ) {
    this[name] = value;
  }
}

/**
 * Get cookie name
 * @method getName
 * @return {String} the cookie name
 */
knowit.Cookie.prototype.getName = function() {
  return this._name;
}

/**
 * Get cookie value string
 * @method getValue
 * @return {String} the value string
 */
knowit.Cookie.prototype.getValue = function() {
  return this._value;
}

/**
 * Store cookie in document.cookie
 * @method write
 * @param options {Object} @see knowit.Cookie.setOptions
 */
knowit.Cookie.prototype.write = function( options ) {
  if( options ) {
    this.setOptions( options );
  }
  document.cookie = this._createCookieString( true );
};

/**
 * Deletes the attributes of the cookie object and removes the cookie from the browser's 
 * local store. The arguments to this function are all optional, but to remove a cookie
 * you must pass the same values you passed to the knowit.Cookie.write method.
 *
 * @method remove
 * @param options {Object}
 *   @property domain {String} Domain where the cookie is valid (default: domain of calling document)
 *   @property path {String} Path where the cookie is valid (default: path of calling document)
 *   @property secure {boolean} value indicating if the cookie transmission requires a secure transmission (default: false)
 */
knowit.Cookie.prototype.remove = function( options ) {

  for( var p in this ) {
    if ( p.charAt(0) != '_' && typeof this[p] != 'function' ) {
      delete this[p]; // Ignore private properties and methods
    }
  }
  if( options ) {
    this.setOptions( options );
  }
  this._expires = new Date( 1000 ); // == 01-Jan-70 00:00:01 GMT
  this._maxAge = 0;
  this.write();
};

/**
 * @method toString
 * @return the cookie string
 */
knowit.Cookie.prototype.toString = function() {
  return this._createCookieString( false );
};


//
// Static methods and attributes
//

/**
 * Exception to throw when the command.func parameter is not a function
 * @property InvalidCommand
 * @type {Error}
 * @private
 * @static
 */
knowit.Cookie.RequiredParameterException = 
  new Error('The "cookieString" parameter is required to create a cookie object.');
  
/**
 * The version
 * @property _VERSION
 * @type {String}
 * @private
 * @static
 */
knowit.Cookie._VERSION = '1.0';

/** 
 * Encoding function. Default is encodeURIComponent 
 * @method encode
 * @static
 */
knowit.Cookie.encode = encodeURIComponent;

/** 
 * Decoding function. Default is decodeURIComponent 
 * @method decode
 * @static
 */
knowit.Cookie.decode = decodeURIComponent;


/**
 * Get stored cookie by a given name
 * @method read
 * @static
 * @param name {String} The name of the cookie
 * @return {knowit.Cookie||null} the cookie or null if cookie does not exist
 */
knowit.Cookie.read = function( name ) {

  var result = null;
  name = (name || '').trim();
  if( name ) {
    var re= new RegExp( '(' + name + ')=(.*?);' ), 
        c = document.cookie + ';',
        m = re.exec(c),
        s;
        
    if( m ) {
      // m[0] is the matched string
      // m[1] is the cookie name
      // m[2] is the value
      s = m.length > 2 ? m[1] + '=' + knowit.Cookie.decode( m[2].trim() ) : m[1];
      result = new knowit.Cookie( s );
    }
  }
  return result;
};


/**
 * This method attempts to determine whether cookies are enabled.
 * It returns true if they appear to be enabled and false otherwise.
 * A return value of true does not guarantee that cookies actually persist.
 * Nonpersistent session cookies may still work even if this method
 * returns false.
 * @method isEnabled
 * @static
 * @return {boolean} true if cookie enabled, else false
 */
knowit.Cookie.isEnabled = function() {
  
	if(typeof navigator.cookieEnabled != "boolean") {
    document.cookie = "knowit.cookieAllowed=yes; max-age=10000";  // Create cookie
    if ( document.cookie.indexOf("knowit.cookieAllowed=test") < 0 ) {
      // Cookie not created
      navigator.cookieEnabled = false;
    }
    else {
      // Delete cookie
      document.cookie = "knowit.cookieAllowed=yes; max-age=0;expires=Thu, 01 Jan 1970 00:00:01 GMT";  
      navigator.cookieEnabled = true;
    }
  }
  return navigator.cookieEnabled;
};

} //~if( !knowit.Cookie )
window.status = '';