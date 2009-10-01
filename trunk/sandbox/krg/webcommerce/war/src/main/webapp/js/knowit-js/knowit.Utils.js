/*
 * File        : knowit.Utils.js
 * Version     : 1.0
 * Author      : leif.olsen@communityr.com
 * Copyright   : 
 * Description : Helper utilities implemented as a singleton pattern, 
 *               Also known as the module pattern, referring to the fact that it modularizes 
 *               and namespaces a set of related  methods and attributes. 
 *               see: http://yuiblog.com/blog/2007/06/12/module-pattern/
 * Notes       : 
 * Limitations : 
 * Dependencies: knowit.String
 * Created     : 20080516
 * History     : 20080520 - Modified for knowit
 *               20081020 - Added truncateHtml
 */
 
window.status = 'Loading [knowit.Utils.js]';

// Define the namespace 
if(knowit == undefined) {
  var knowit = {};
}

if( !knowit.Utils ) {
knowit.Utils = ( function() {

  // Private attributes and methods
  var VERSION = '1.1';

return ({ //Public attributes and methods


  /** 
   * Additionally truncates the input string (based on a user-specified number of characters), 
   * and in the process only counts text outside of HTML tags towards the length, avoids ending 
   * the string in the middle of a tag or word, and avoids adding closing tags for singleton 
   * elements like <br> or <img>. 
   * For additional info, see: http://blog.stevenlevithan.com/archives/get-html-summary
   *
   * @method truncateHtml
   * @static
   * @param input {String} the HTML to truncate
   * @param maxChars {int} maximum number of characters in the truncated string
   * @param addDots {boolean} adds '...' to the end of the truncated string if the length of
   *        the truncated string exceeds the maxChars value.
   * @return {String} the truncated HTML
   */
  truncateHtml: function( input, maxChars, addDots ) {
  	// token matches a word, tag, or special character
    var token = new RegExp('\\w+|[^\\w<]|<(\\/)?(\\w+)[^>]*(\\/)?>|<', 'g'),
        //token         = /\w+|[^\w<]|<(\/)?(\w+)[^>]*(\/)?>|</g,
        selfClosingTag= /^(?:[hb]r|img)$/i,
        output        = "",
        charCount     = 0,
        openTags      = [],
        match, i;
        
    input = input.normalize(1),
    // Set the default for the max number of characters
    // (only counts characters outside of HTML tags)
    maxChars = maxChars || 250;
    addDots = addDots || false;

    while ((charCount < maxChars) && (match = token.exec(input))) {
      // If this is an HTML tag
      if (match[2]) {
        output += match[0];
        
        // If this is not a self-closing tag
        if (!(match[3] || selfClosingTag.test(match[2]))) {
          // If this is a closing tag
          if (match[1]) 
            openTags.pop();
          else 
            openTags.push(match[2]);
        }
      }
      else {
        charCount += match[0].length;
        if (charCount <= maxChars) 
          output += match[0];
      }
      
      if( addDots && charCount >= maxChars )
        output += '...';
      
    }
    // Close any tags which were left open
    i = openTags.length;
    while (i--) 
      output += "</" + openTags[i] + ">";

    return output;
  },


  /**
   * Extracts a documents URL search data into an hash array.<br/>
   * Search data is a series of name/value pairs. An equal symbol (=)
   * separates a name and its value. Multiple name/value pairs have ampersands (&) 
   * between them. The unescape() function is used to convert the data from an 
   * URL-friendly format.<br/>
   *
   * Example: http://localhost/my/index.htm?a=1&b=2&c=true --> a['a']=1, a['b']=2, a['c']='true'
   *
   * @method getLocationSearchData
   * @static
   * @return {Array} Hash array with name value pairs where name is the index used 
   *         to get the corresponding value. 
   */
  getLocationSearchData: function() {
    if (location.search) {
      var searchString = unescape(location.search.substring(1, location.search.length));
      return searchString.splitAttributes('&','=');
    }
    return null;
  },
  
  toString: function() { 
    return ( 'knowit.Utils, version ' + VERSION ); 
  }
  
}); //~return

} ) (); //~anonymous function immediately invoked with ()
};

window.status = '';

  
  
  

/* see: http://www.julienlecomte.net/blog/2007/12/38/

YAHOO.util.Dom.setInnerHTML = function (el, html) {
    el = YAHOO.util.Dom.get(el);
    if (!el || typeof html !== 'string') {
        return null;
    }

    // Break circular references.
    (function (o) {

        var a = o.attributes, i, l, n, c;
        if (a) {
            l = a.length;
            for (i = 0; i < l; i += 1) {
                n = a[i].name;
                if (typeof o[n] === 'function') {
                    o[n] = null;
                }
            }
        }

        a = o.childNodes;

        if (a) {
            l = a.length;
            for (i = 0; i < l; i += 1) {
                c = o.childNodes[i];

                // Purge child nodes.
                arguments.callee(c);

                // Removes all listeners attached to the element via YUI's addListener.
                YAHOO.util.Event.purgeElement(c);
            }
        }

    })(el);

    // Remove scripts from HTML string, and set innerHTML property
    el.innerHTML = html.replace(/<script[^>]*>[\S\s]*?<\/script[^>]*>/ig, "");

    // Return a reference to the first child
    return el.firstChild;
};

*/


/*
  getText = function(extElement) {
    return extElement 
      ? (/textarea|input/i).test(extElement.dom.tagName) 
        ? extElement.dom.value 
        : extElement.dom.innerHTML
      : "";
  };
  

  hasHtmlBr: function(element) {
    //TODO: TBD!
    element = Ext.get(element);
    return false;
  },
  
  hasNewLine: function(element) {
    element = Ext.get(element);
    return (/textarea|input/i).test(element.dom.tagName)
      ? (/\n/g).test(element.dom.value) 
      : (/\n/g).test(element.dom.innerHTML);
  },
  
  measureTextWidth: function(element) {
    var result = 0;
    
    //element = Ext.get(element);
    //var span = document.createElement('span');
    //var s = span.style;
    //s.visibility = 'hidden';
    //s.position = 'absolute';
    //s.zIndex = '1';
    //s.fontFamily = element.getStyle('fontFamily');
    //s.fontSize = element.getStyle('fontSize');
    //s.fontStyle = element.getStyle('fontStyle');
    //s.fontVariant = element.getStyle('fontVariant');    
    //span.innerHTML = element.dom.innerHTML;
    //document.body.appendChild(span);
    //var len = span.offsetWidth;
    //document.body.removeChild(span);

    
    var t;
    if( (element = Ext.get(element)) && (t = getText(element)) ) {
      var body = Ext.get(document.body);
      var span = body.createChild({tag:'span'}); 
      try {
        span.setStyle({
          visibility : 'hidden', 
          position   : 'absolute', 
          zIndex     : '1',
          margin     : '0',
          padding    : '0',
          fontFamily : element.getStyle('fontFamily'),
          fontSize   : element.getStyle('fontSize'),
          fontStyle  : element.getStyle('fontStyle'),
          fontVariant: element.getStyle('fontVariant')
        });
        var n = t.indexOf('\n');     
        span.update(n > 0 ? t.substr(0, n-1) : t);
        result = span.getComputedWidth();
      }
      finally {
        span.remove();
      }
    } //~if(element)
    
    return result;
  },

  isTextOverflow: function(element) {
    if( (element = Ext.get(element)) ) {
      return this.hasNewLine(element) 
        ? true 
        : this.measureTextWidth(element) > element.getWidth(true);
    }
    return false;
  },
  


*/