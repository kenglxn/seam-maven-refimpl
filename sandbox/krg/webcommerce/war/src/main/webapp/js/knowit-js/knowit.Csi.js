/*
 * File        : knowit.Csi.js
 * Version     : 1.0
 * Author      : leif.olsen@knowit.no
 * Copyright   : (c) 2008 knowit.no
 *               Class LazyLoad
 *                  Author:
 *                    Ryan Grove (ryan@wonko.com)
 *                    Leif Olsen (leif.olsen@knowit.no) (modifications to original code)
 *                  Copyright:
 *                   Copyright (c) 2008 Ryan Grove (ryan@wonko.com). All rights reserved.
 *                  License:
 *                    BSD License (http://www.opensource.org/licenses/bsd-license.html)
 *                  URL:
 *                    http://wonko.com/post/painless_javascript_lazy_loading_with_lazyload
 * 
 *                 Just In Time (JIT) Loader
 *                   JIT segments Copyright (c) 2007 Jakob Heuser (jakob@felocity.org). All rights reserved.
 *                   For additional details, please check out the following usage guides:
 *                   LazyLoad: http://wonko.com/article/527
 *                   JITLoad:  http://www.felocity.org/blog/article/just_in_time_loader_for_javascript/

 *
 * Description : Client Side Include library
 *
 * Notes       : 
 * Limitations : 
 * Dependencies: prototype-1.6.x.js (for ajax), htmlparser.js, 
 *               knowit.js, knowit.String.js, knowit.UriParser.js, 
 *               knowit.UrlResolver.js, knowit.UserAgent.js
 * Created     : 20080908
 * History     :     
 */
window.status = 'Loading [knowit.Csi.js] ';

if( typeof Prototype != 'object' ) {
  throw new Error( 'knowit.Csi.js requires prototype.js version 1.6 or greater' );
}

if( typeof HTMLParser != 'function' ) {
  throw new Error( 'knowit.Csi.js requires htmlparser.js' );
}

// Define the knowit namespace 
if(knowit == undefined) {
  var knowit = {};
}

if(!knowit.Csi) {

/*
 * CSI Utility 
 */
knowit.CsiUtils = ( function() {

  // Private attributes and methods
  VERSION = '1.0';
  

return ({ //Public attributes and methods

  /**
   * @method createNode
   * @param name {String} 
   * @param attrs {Array} 
   * @return {HTMLElement} the created node
   * @private
   **/
  createNode: function( name, attrs ) {
    var node = (name ? document.createElement(name) : null), 
        i;

    if( node ) {
      for( i = 0; i < attrs.length; i++ ) {
        if ( attrs[i].name ) {
          node.setAttribute("" + attrs[i].name, "" + attrs[i].value||'');
        }
      }
    }
    return node;
  },
  
  escape: function( value ) {
    return value.replace(/(^|[^\\])"/g, '$1\\\"'); //"  
  },
  
  /**
   * Get an attribute from the attrs array
   * @param attrs {Array} array of attributes
   * @param  name {String} attribute name
   * @return {Object} the attribute with the given name or <code>null</code> if attribute not found.
   *         attribute has the following structure : {name, value, escaped}
   */
  getAttributeByName: function( attrs, name ) {
    var result = null, re = new RegExp(name, 'i'), i;

    if( attrs && name ) {  
      for (i = 0; i < attrs.length; i++) {
        if ( re.test(attrs[i].name) ) { //if(attrs[i].name === name) {
          result = attrs[i];
          break;
        }
      }
    }
    return result;
  },

  /**
   * Set an attribute in the attrs array with the following structure : {name, value, escaped}
   * @param attrs {Array} array of attributes
   * @param name {String} attribute name
   * @param value {String} attribute value
   * @return void
   */
  setAttributeByName: function( attrs, name, value ) {

    _escape = function() {  
      return value.replace(/(^|[^\\])"/g, '$1\\\"'); //"
    }

    if( attrs && name ) {
      var re = new RegExp(name, 'i'),  i;
      for ( i = 0; i < attrs.length; i++ ) {
        if( re.test(attrs[i].name) ) {  //if( attrs[i].name === name) {
          attrs[i].value   = value;
          attrs[i].escaped = _escape();
          return;
        }
      }
      attrs.push({
        name   : name,
        value  : value,
        escaped: _escape()
      });
    }
  },
  /**
   * Cast arg to an Array
   * @method toArray
   * @param arg {anything} the arg to cast
   * @return {Array} arg as an array
   **/
  toArray: function(arg) {
    return arg ? (arg.constructor === Array ? arg : [arg]) : [];
  },

  toString: function() { 
    return 'knowit.CsiUtils, version ' + VERSION;
  }
  
}); //~return

} ) (); //~anonymous function immediately invoked with ()



/*
Class: LazyLoad

LazyLoad makes it easy and painless to lazily load one or more JavaScript
files on demand after a web page has been rendered.

Supported browsers include Firefox 2.x, Firefox 3.x, Internet Explorer 6.x,
Internet Explorer 7.x, Safari 3.x (including iPhone), and Opera 9.x. Other
browsers may or may not work and are not officially supported.

Author:
  Ryan Grove (ryan@wonko.com)
  Leif Olsen (leif.olsen@knowit.no) (modifications to original code)

Copyright:
  Copyright (c) 2008 Ryan Grove (ryan@wonko.com). All rights reserved.

License:
  BSD License (http://www.opensource.org/licenses/bsd-license.html)

URL:
  http://wonko.com/post/painless_javascript_lazy_loading_with_lazyload
  
Class: Chain
  Just In Time (JIT) Loader
    JIT segments Copyright (c) 2007 Jakob Heuser (jakob@felocity.org). All rights reserved.
    For additional details, please check out the following usage guides:
    LazyLoad: http://wonko.com/article/527
    JITLoad:  http://www.felocity.org/blog/article/just_in_time_loader_for_javascript/
  
Version:
  1.0.4 (2008-07-24)
  1.1.0 (2008-08-22)
  1.2.0 (2008-09-22) Modifications to original code
*/

var LazyLoad = function () {

  // -- Group: Private Variables -----------------------------------------------

  var VERSION = '1.2.0',

  /*
  Object: d
  Shorthand reference to the browser's *document* object.
  */
  d = document,
  
  /*
  Object: pending
  Pending request object, or null if no request is in progress.
  */
  pending = null,

  /*
  Array: queue
  Array of queued load requests.
  */
  queue = [];

  
  /**
   * @method evalJavaScript
   * @param code {String} 
   * @param errorMessage {String} 
   * @return eval(code)
   * @private
   **/
  evalJavaScript = function( code, errorMessage ) {
    try {
      return( eval(code) );
    }
    catch(e) {
      throw new Error( (errorMessage || 'LazyLoad.evalJavaScript: Could not eval code:') + '\n[' + code + ']');
    }
  };
    
  
  /**
   * Do the include
   * @param includes {Array} files to include.
   *        @see knowit.Csi.parseHtml for documentation of the includes structure
   **/
  function doInclude( includes, callback, obj, scope ) {

    var head = d.getElementsByTagName('head')[0], i;
    if( includes ) {
      // Create a request object for each URL. If multiple URLs are specified,
      // the callback will only be executed after the last URL is loaded.
      for (i = 0; i < includes.length; i++) {
        queue.push({
          'include' : includes[i],
          'callback': i === includes.length - 1 ? callback : null,
          'obj'     : obj,
          'scope'   : scope
        });
      }
    }

    // If a previous load request is currently in progress, we'll wait our
    // turn. Otherwise, grab the first request object off the top of the queue.
    if ( !( pending || !(pending = queue.shift()) ) ) {
      if ( /script/i.test(pending.include.tag) ) {
        var src = knowit.CsiUtils.getAttributeByName(pending.include.attrs, 'src');
        if( src && src.value ) {
          doIncludeScript(pending);
        }
        else if ( pending.include.script ) {
          doIncludeInlineScript(pending);
        }
        else {
          LazyLoad.__requestComplete();      
        }
      }
      else if ( /link|style/i.test(pending.include.tag) ) {
        doIncludeCss(pending);
      }
      else if ( /body/i.test(pending.include.tag) ) {
        doIncludeHtmlBody(pending);
      }
      else {
        // Unhandled tag
        LazyLoad.__requestComplete();      
      }
    }
  };
  
  function doIncludeScript( pending ) {

    var head = d.getElementsByTagName('head')[0],
        node = knowit.CsiUtils.createNode('script', pending.include.attrs);
        
    node.type = 'text/javascript'; // Override required attributes
    
    if( pending.callback ) {
      if ( knowit.UserAgent.ie ) {
        // If this is IE, watch the last script's ready state.
        node.onreadystatechange = function () {
          if (!this.loaded && /^(loaded|complete)$/.test(this.readyState)) {
            if(!this.loaded) {
              LazyLoad.__requestComplete();
            }
            this.loaded = true;
          }
        };
      }
      else if ( knowit.UserAgent.webkit >= 420 ) {
        // Safari 3.x supports the load event for script nodes (DOM2)
          node.addEventListener( 'load', LazyLoad.__requestComplete );
      }
      else if( knowit.UserAgent.gecko || knowit.UserAgent.opera > 8 ) {
        // FireFox and Opera (9.0+?) support onload (but not DOM2 in FF) handlers for
        // script nodes.  
        node.onload  = LazyLoad.__requestComplete;
        
        //@TODO: Add requestError callback
        node.onerror = LazyLoad.__requestComplete;
      }
    }

    head.appendChild(node);

    if( pending.callback ) {
      if (!knowit.ua.ie && !knowit.ua.gecko && 
          !(knowit.ua.webkit >= 420) && !(knowit.ua.opera >= 9) ) {
        // Try to use script node blocking to figure out when things have
        // loaded. This works well in Opera, but may or may not be reliable in
        // other browsers. It definitely doesn't work in Safari 2.x.
        node = d.createElement('script');
        node.type = 'text/javascript';
        node.appendChild(d.createTextNode('LazyLoad.__requestComplete();'));
        head.appendChild(node);
      }  
    }
    else {
      LazyLoad.__requestComplete();      
    }
  };
  
  function doIncludeInlineScript( pending ) {
  
    // Get the start and end position of a quoted string (surrounded by ' or ")
    // taking into account that the string kan have escaped quotes (\' or \") 
    getQuotedStringPos = function( code, index ) {
      var beginIndex = -1, 
          endIndex   = -1,
          esc        = false, 
          quote, 
          c, i, n;
      
      for ( i = index, n = code.length; i < n; i++ ) {
      
        c = code.charAt(i);
        if( beginIndex < 0 ) {
          if( c == '"' || c == "'" ) {
            beginIndex = i;
            quote = c;
            esc = false;
          }
        }
        else {
          if( c == quote && !esc ) {
            endIndex = i + 1;
            break;
          }
          esc = (c == '\\' && !esc) ? true : false;
        }
      }
      return beginIndex > -1 ? { begin: beginIndex, end: (endIndex > -1 ? endIndex : n) } : null;
    };
    
    removeCommentsFromCode = function( code ) {

      // Can not use this:  code.replace(slashSlashRegex, '').replace(slashStarRegex, '');  
      // bc it will also remove comment characters from within strings, e.g:
      // var s = "hell/*o*/"; --> s = 'hell';
      // var s = 'hell//o' --> s = 'hell
      // var slashSlashRegex = /\s*\/\/.*/g,
      //     slashStarRegex  = /((?:\/\*(?:[^*]|(?:\*+[^*\/]))*\*+\/)|(?:\/\/.*))/gm;
      //     commentRegex_gm = /\s*\/\/.*|(?:\/\*(?:[^*]|(?:\*+[^*\/]))*\*+\/)|(?:\/\/.*)/gm,
      //     quoteRegex      = /[\'"]([^\'"]*)[\'"]/m,
      // return code.replace(slashSlashRegex, '').replace(slashStarRegex, '');
      
      
      var codeLength = code.length;
      
      scanToEndQuote = function(index) {
        var quote = code.charAt(index), 
            esc   = false, 
            c, i;
            
        for ( i = index+1; i < codeLength; i++ ) {
          c = code.charAt(i);
          if( c == quote && !esc ) {
            return i;
          }
          esc = (c == '\\' && !esc) ? true : false;
        }
        return codeLength-1;
      };
      
      scanToEndComment = function( index ) {
        var c, i, n;
        i = index + 1;
        if ( i < codeLength-1 ) {
          c = code.charAt( i );
          if( c == '/' ) {
            n = code.substring(i+1).indexOf('\n');
            i = n > -1 ? i+n+1 : codeLength;
          }
          else if ( c == '*' ) {
            n = code.substring(i+1).indexOf('*/')
            i = n > -1 ? i+n+2 : codeLength;
          }
        }
        return i;
      };
      
      // begin function removeCommentsFromCode
      var result = '', 
          i = 0, 
          c, n;
      while ( i < codeLength ) {
        c = code.charAt(i);
        if( c == '"' || c == "'" ) {
          n = scanToEndQuote( i );
          result += code.substring(i, n+1);
          i = n;
        }
        else if ( c == '/' ) {
          n = scanToEndComment( i );
          if( i+1 == n) {
            // '/' was not a comment line or block
            result += c;
          }
          else {
            i = n;
          }
        }
        else {
          result += c;
        }
        i++;
      }
      return result;
    };
    
    parseFunctionBody = function( code, index ) {
      index = index || 0;
      var beginIndex = index + code.substring(index).indexOf('{'), 
          endIndex   = 0, 
          i, c;
      
      for ( i = beginIndex; i < code.length; i++ ) {
        c = code.substr(i, 1);
        if( c === '{' ) {
          ++endIndex;
        }
        else if( c === '}' ) {
          --endIndex;
        }
        if( endIndex == 0 ) {
          endIndex = i;
          break;
        }
      }
      return endIndex > beginIndex+1 ? { begin: beginIndex+1, end: endIndex } : null ;
    };
    
    parseFunctionAssign = function( code, index ) {
      index = index || 0;
      var beginIndex = index + code.substring(index).indexOf('='), 
          endIndex   = index + code.substring(index).indexOf(';');
          
      return endIndex > beginIndex+1 ? { begin: beginIndex+1, end: endIndex } : null ;
    };
    
    // NOTE: This is for prototype.js only
    executeDocumentReady = function ( code, codeIndex ) {
      var result = -1,
          documentObserveDomLoadedRegex       = /document.observe\s*\(\s*('|")\s*dom:loaded('|")\s*,/m,
          documentObserveDomLoadedAnonymRegex = /document.observe\s*\(\s*('|")\s*dom:loaded('|")\s*,\s*function\s*\(/m,
          functionIndex                       = null,
          matchDocumentObserveDomLoaded, 
          matchDocumentObserveDomLoadedAnonym;
          
      if( matchDocumentObserveDomLoaded = documentObserveDomLoadedRegex.exec(code.substring(codeIndex)) ) {
      
        matchDocumentObserveDomLoadedAnonym = documentObserveDomLoadedAnonymRegex.exec(code.substring(codeIndex));
        
        if( matchDocumentObserveDomLoadedAnonym && 
            matchDocumentObserveDomLoaded.index == matchDocumentObserveDomLoadedAnonym.index ) {
          // Execute anonymous function
          functionIndex = parseFunctionBody( code, matchDocumentObserveDomLoadedAnonym.index + codeIndex );
          if( functionIndex ) {
            evalJavaScript( code.substring(functionIndex.begin, functionIndex.end) );
          }
        }
        else {
          // Execute function reference
          var x = matchDocumentObserveDomLoaded.index + matchDocumentObserveDomLoaded[0].length + codeIndex,
              y = code.substring(x).indexOf(','),
              z = code.substring(x).indexOf(')'),
              n = y > -1 && z > -1 ? (y < z ? y : z) : (y > -1 ? y : z),
              f = n > -1 ? code.substr(x, n).trim() : ''; 
          
          if( f ) {
            evalJavaScript( f+'()' );
            result = x + n + 1;
          }
        }
      }
      return (result == -1 && functionIndex) ? functionIndex.end + 1 : result;
    };
    
    executeDocumentReadyCalls = function( code ) {
      var codeIndex = 0;
      do {
        codeIndex = executeDocumentReady(code, codeIndex);
      } while (codeIndex != -1);
    };
    
    executeOnLoad = function( code, codeIndex ) {
      codeIndex = codeIndex || 0;
      var result                 = -1,
          reWindowOnLoad         = new RegExp('window.onload\\s*=', 'im'), 
          reWindowOnLoadAnonym   = new RegExp('window.onload\\s*=\\s*function\\s*\\(.*\\)', 'im'), 
          observeLoadRegex       = /Event.observe\s*\(\s*window\s*,\s*('|")\s*load('|")\s*,/m,
          observeLoadAnonymRegex = /Event.observe\s*\(\s*window\s*,\s*('|")\s*load('|")\s*,\s*function\s*\(/m,
          functionIndex          = null,
          matchWindowOnLoad, 
          matchWindowOnLoadAnonym, 
          matchObserveLoad, 
          matchObserveLoadAnonym;

      if ( matchWindowOnLoad = reWindowOnLoad.exec( code.substring(codeIndex) ) ) {
      
        matchWindowOnLoadAnonym = reWindowOnLoadAnonym.exec( code.substring(codeIndex) );
        
        if( matchWindowOnLoadAnonym && matchWindowOnLoad.index == matchWindowOnLoadAnonym.index ) {
          // Execute anonymous function
          functionIndex = parseFunctionBody( code, matchWindowOnLoadAnonym.index + codeIndex );
          if( functionIndex ) {
            evalJavaScript( code.substring(functionIndex.begin, functionIndex.end) );
          }
        }
        else {
          // Execute function
          functionIndex = parseFunctionAssign( code, matchWindowOnLoad.index + codeIndex );
          if( functionIndex ) {
            evalJavaScript( code.substring(functionIndex.begin, functionIndex.end) + '()' );
          }
        }
      }
      else if( matchObserveLoad = observeLoadRegex.exec(code.substring(codeIndex)) ) {
      
        matchObserveLoadAnonym = observeLoadAnonymRegex.exec(code.substring(codeIndex));
        
        if( matchObserveLoadAnonym && matchObserveLoad.index == matchObserveLoadAnonym.index ) {
          // Execute anonymous function
          functionIndex = parseFunctionBody( code, matchObserveLoadAnonym.index + codeIndex );
          if( functionIndex ) {
            evalJavaScript( code.substring(functionIndex.begin, functionIndex.end) );
          }
        }
        else {
          // Execute function reference
          var x = matchObserveLoad.index + matchObserveLoad[0].length + codeIndex,
              y = code.substring(x).indexOf(','),
              z = code.substring(x).indexOf(')'),
              n = y > -1 && z > -1 ? (y < z ? y : z) : (y > -1 ? y : z),
              f = n > -1 ? code.substr(x, n).trim() : ''; 
          
          if( f ) {
            evalJavaScript( f+'()' );
            result = x + n + 1;
          }
        }
      }
      return (result == -1 && functionIndex) ? functionIndex.end + 1 : result;
    };
    
    executeOnLoadCalls = function( code ) {
      var codeIndex = 0;
      do {
        codeIndex = executeOnLoad(code, codeIndex);
      } while (codeIndex != -1);
    };

  
    // begin function doIncludeInlineScript
    if ( knowit.CsiUtils.getAttributeByName(pending.include.attrs, 'id') ) {
      // Create a script node if the inline script has an id attribute
      // Later on the includeScriptOnce method can use this id to check  
      // if the inline script is already loaded.
      var head = d.getElementsByTagName('head')[0], 
          node = knowit.CsiUtils.createNode('script', pending.include.attrs);
          
      node.type = 'text/javascript'; // Override required attributes
      head.appendChild(node);
    }
    
    // Compile the inline script
    var code = removeCommentsFromCode( pending.include.script );
    evalJavaScript( code );
    
    // Execute document ready and window onload calls
    executeDocumentReadyCalls( code );
    executeOnLoadCalls( code );
    
    LazyLoad.__requestComplete();      
  };  
  
  function doIncludeCss( pending ) {
  
    var head = d.getElementsByTagName('head')[0],
        node = knowit.CsiUtils.createNode(pending.include.tag, pending.include.attrs);

    // Override common attributes for link and style tags (that must be present)
    node.type = 'text/css';
    node.rel  = 'stylesheet';

    if ( /link/i.test(pending.include.tag) ) {
        
      head.appendChild(node);

      if(pending.callback) {
        // in MSIE, we will need to listen to the onreadystatechange
        // if the file is cached, we may not even see "loaded" as an option
        // and may instead see "complete". Because of this, we need to scan
        // for both. Script loading is linear, so we only need to watch
        // the last script we were inserting
        if (knowit.UserAgent.ie) {
          // If this is IE, watch the last script's ready state.
          node.onreadystatechange = function () {
            if (!this.loaded && /^(loaded|complete)$/.test(this.readyState)) {
              if(!this.loaded) {
                LazyLoad.__requestComplete();
              }
              this.loaded = true;
            }
          };
        }
        else {
          // this is a non MSIE browser. We can use a safer method of
          // detecting when a script is done. We insert a small scriptlet
          // at the end of all our script objects which executes the
          // requestComplete() code.
          // NOTE: Opera, but not FF, supports the onload event for link
          //       nodes, but we do'nt take that into consideration here.
          node = d.createElement('script');
          node.type = 'text/javascript';
          node.appendChild(d.createTextNode('LazyLoad.__requestComplete();'));
          head.appendChild(node);
        }
      } 
      else {
        LazyLoad.__requestComplete();      
      }
    
    }
    else {
      if(node.styleSheet) { 
        // IE
        node.styleSheet.cssText = pending.include.css;
      } 
      else { 
        // w3c
        node.appendChild( document.createTextNode(pending.include.css) );
      }
      head.appendChild(node);
      LazyLoad.__requestComplete();      
    }
    
  };
  
  function doIncludeHtmlBody( pending ) {
  
    var targetNode = $(pending.include.target);
    if( targetNode ) {
      targetNode.innerHTML = pending.include.content;
    }
    LazyLoad.__requestComplete();      
  };  
  
  /**
   * Executes a callback without affecting the queue
   */
  function emptyCallback(callback, obj, scope) {
  
    if (obj) {
      if (scope) {
        callback.call(obj);
      } else {
        callback.call(window, obj);
      }
    } else {
      callback.call();
    }
  };
  
  
  /**
   * Returns a batch object for chain processing (the Chain of Command pattern).
   * Adopted from: http://www.felocity.org/blog/article/just_in_time_loader_for_javascript/
   * The functionality of the chain is similar to LazyLoad. However,
   * for load, includeScript, includeScriptOnce, includeCss and includeHtmlBody, there is no 
   * callbacks involved. Instead, the system uses its run function as a callback method to 
   * unfurl the stack created.
   * @return 
   * @see 
   **/
  var Chain = function() {
    var stack        = [],
        run_callback = null,
        run_object   = null,
        run_scope    = null;

    // return object
    return {
      includeScript: function( scripts ) {
        if( scripts ) {
          stack.push( {type: "script", once: false, scripts: scripts} );
        }
        return this;
      },
      
      includeScriptOnce: function( scripts ) {
        if( scripts ) {
          stack.push( {type: "script", once: true,  scripts: scripts } );
        }
        return this;
      },
      
      includeCss: function( styles ) {
        if( styles ) {
          stack.push( {type: "css", once: true,  styles: styles} );
        }
        return this;
      },
      
      includeHtmlBody: function( bodies ) {
        if( bodies ) {
          stack.push( {type: "body", once: true,  bodies: bodies } );
        }
        return this;
      },
      
      /**
       * Executes the stack of objects, using a basic form of recursion
       * @param callback {function} the callback function to run
       * @param obj {Object} the object to include in the callback
       * @param scope {boolean} if true, the callback will be ran in the object's scope
       **/
      onComplete: function( callback, obj, scope ) {
        var self = this;
    
        // store the run callback the first time we enter the onComplete
        if ( !run_callback ) {
          run_callback = callback || function(){};
          run_object   = obj;
          run_scope    = scope;
        }
    
        // no stack, we are done, run the callback
        if (stack.length == 0) {
          if (obj) {
            if (scope) {
              run_callback.call(obj);
            }
            else {
              run_callback.call(window, obj);
            }
          }
          else {
            run_callback.call();
          }
          return;
        }

        // start unstacking
        var next_call = stack.shift();
      
        // call a run op for this
        if (next_call.type == "css") {
          LazyLoad.includeCss(next_call.styles, self.onComplete, self, true);
        }
        else if (next_call.type == "body") {
          LazyLoad.includeHtmlBody(next_call.bodies, self.onComplete, self, true);
        }
        else if (next_call.type == "script") {
          if (next_call.once) {
            LazyLoad.includeScriptOnce(next_call.scripts, self.onComplete, self, true);
          }
          else {
            LazyLoad.includeScript(next_call.scripts, self.onComplete, self, true);
          }
        }
      }
    }; 
  }; //~Chain


  return {
    // -- Group: Public Methods ------------------------------------------------

    /*
    Method: includeScript
    Loads the specified script(s) and runs the specified callback function
    when all scripts have been completely loaded.

    Parameters:
      scripts  - Script object or array of script objects
                 @see knowit.Csi.parseHtml for documentation of the scripts structure
      callback - function to call when loading is complete
      obj      - (optional) object to pass to the callback function
      scope    - (optional) if true, *callback* will be executed in the scope
                 of *obj* instead of receiving *obj* as an argument.
    */
    includeScript: function ( scripts, callback, obj, scope ) {
      scripts = knowit.CsiUtils.toArray(scripts);
      callback = callback || function() {};
      if(scripts.length > 0) {
        doInclude(scripts, callback, obj, scope)
      }
      else {
        emptyCallback(callback, obj, scope);
      }
    },

    /*
    Method: includeScriptOnce
    Loads the specified script(s) only if they haven't already been loaded
    and runs the specified callback function when loading is complete. 
    
    Parameters:
      scripts  - Script object or array of script objects.
                 @see knowit.Csi.parseHtml for documentation of the script structure
      callback - function to call when loading is complete
      obj      - (optional) object to pass to the callback function
      scope    - (optional) if true, *callback* will be executed in the scope
                 of *obj* instead of receiving *obj* as an argument
    */
    includeScriptOnce: function ( scripts, callback, obj, scope ) {
    
      var loadedScripts = d.getElementsByTagName('script'),
          newScripts = [], 
          i;

      isScriptLoaded = function(script) {
        var result = false, 
            src    = knowit.CsiUtils.getAttributeByName(script.attrs, 'src'),
            id;
        
        if( src && src.value) {
          for ( var i = 0; i < loadedScripts.length; i++ ) {
            if ( src.value === loadedScripts[i].src ) {
              result = true;
              break;
            }
          }
        }
        else {
          id = knowit.CsiUtils.getAttributeByName(script.attrs, 'id');
          if ( id && id.value && document.getElementById(id.value) ) {
            result = true;
          }
        }
        return result;
      }; //~isScriptLoaded
    
      scripts = knowit.CsiUtils.toArray(scripts);
      
      for ( i = 0; i < scripts.length; i++ ) {
        if( !isScriptLoaded(scripts[i]) ) {
          newScripts.push( scripts[i] );
        }
      }
      
      callback = callback || function(){};
      
      if (newScripts.length > 0) {
        doInclude(newScripts, callback, obj, scope);
      } 
      else {
        emptyCallback(callback, obj, scope);
      }
    },
    
    /*
    Method: includeCss
    Loads the specified css(s) only if they haven't already been loaded
    and runs the specified callback function when loading is complete.
    
    Parameters:
      styles   - object or array of style objects.
                 @see knowit.Csi.parseHtml for documentation of the styles structure
      callback - function to call when loading is complete
      obj      - (optional) object to pass to the callback function
      scope    - (optional) if true, *callback* will be executed in the scope
                 of *obj* instead of receiving *obj* as an argument
    */
    includeCss: function( styles, callback, obj, scope ) {
    
      var loadedStylesheetsHref = [],
          newStyles = [],
          i, j, stylesheet, rules;
          
      for ( i = 0; i < document.styleSheets.length; i++ ) {
        stylesheet = document.styleSheets[i];
        if( stylesheet.href ) {
          loadedStylesheetsHref.push( stylesheet.href );
        }
        else { 
          rules = stylesheet.cssRules || stylesheet.imports;  // w3c || ie
          if(rules) {
            for ( j = 0; j < rules.length; j++ ) {
              if( rules[j].href ) {
                // w3c browsers makes any relative URL absolute by 
                // adding the current document's absolute path to the URL
                // -- except for urls in cssRules (@import rule), 
                //    we have to make the URL absolute ourselves
                loadedStylesheetsHref.push( 
                  (knowit.UserAgent.ie ? rules[j].href : knowit.UrlResolver.resolveUrl( rules[j].href )) 
                );
              }
            }
          }
        }
      } //~for
      

      isStylesheetLoaded = function( style ) {
        var href   = knowit.CsiUtils.getAttributeByName(style.attrs, 'href'), 
            result = false;
            
        if( href && href.value ) {
          for ( var i = 0; i < loadedStylesheetsHref.length; i++ ) {
            if ( href.value === loadedStylesheetsHref[i] ) {
              result = true;
              break;
            }
          }
        }
        return result;
      }

      styles = knowit.CsiUtils.toArray(styles);
      for ( i = 0; i < styles.length; i++ ) {
        if( !isStylesheetLoaded(styles[i]) || (/style/i.test(styles[i].tag) && styles[i].css) ) {
          newStyles.push( styles[i] );
        }
      }
          
      callback = callback || function() {};
      
      if(newStyles.length > 0) {
        doInclude(newStyles, callback, obj, scope)
      }
      else {
        emptyCallback(callback, obj, scope);
      }
    },
    
    /*
    Method: includeHtmlBody
    Inserts the specified html fragment(s) 
    and runs the specified callback function when loading is complete.
    
    Parameters:
      bodies   - object or array of body objects.
                 @see knowit.Csi.parseHtml for documentation of the body structure
      callback - function to call when loading is complete
      obj      - (optional) object to pass to the callback function
      scope    - (optional) if true, *callback* will be executed in the scope
                 of *obj* instead of receiving *obj* as an argument
    */
    includeHtmlBody: function( bodies, callback, obj, scope ) {
      bodies = knowit.CsiUtils.toArray(bodies);
      doInclude( bodies, callback, obj, scope )
    },

    /**
     * Chains includeXxx functions via Chain object
     */
    chain: function() {
      return Chain();
    },
    
    
    /*
     * Method: __requestComplete
     * Handles callback execution and cleanup after a request is completed. 
     * This method is for internal use only and should not be called manually. 
     */
    __requestComplete: function() {
      // Execute the callback.
      if (pending.callback) {
        if (pending.obj) {
          if (pending.scope) {
            pending.callback.call(pending.obj);
          } 
          else {
            pending.callback.call(window, pending.obj);
          }
        } 
        else {
          pending.callback.call();
        }
      }
      pending = null;

      // Execute the next load request on the queue (if any).
      if ( queue.length ) {
        doInclude();
      }
    },
    
    toString: function() {
      return 'LazyLoad, version: ' + VERSION;
    }
  };
}();



/**
 * Client Side Include
 */
knowit.Csi = ( function() {

  // Private attributes and methods
  VERSION = '1.2',
  
  /**
   * Parses a HTML document.
   * @method _parseHtml
   * @private
   */
  _parseHtml = function( url, content ) {
  
    if( !content ) {
      return null;
    }
    
    var result = { 
          url     : url,
          target  : null,
          xmlDecl : '',
          docType : '',
          html    : {}, 
          head    : {}, 
          title   : {},
          metaTags: [],
          scripts : [], 
          styles  : [],
          links   : [],
          body    : { tag: 'body' } 
        },
        bodyStarted         = true,
        bodyContent         = [],
        preStarted          = false,
        preContent          = [],
        titleStarted        = false,
        titleContent        = [],
        inlineScriptStarted = false,
        inlineScriptContent = [],
        inlineStyleStarted  = false,
        inlineStyleContent  = [];
    
    // Regex to match the pattern: @import url(someurl.css);
    var importRegex = /@import\s*url\s*\(\s*["|']*(.*?)["|']*\s*\)\s*;/i;
    
    HTMLParser(content, {
      start: function( tag, attrs, unary ) {

        //if( window.log !== undefined && log.isDebugEnabled() ) 
        //    log.debug('url: ' + url + '<br/>&nbsp;tag: ' + tag);
      
        if( tag == "html" ) {
          bodyStarted = false;
          result.html = {
            tag    : tag,
            attrs  : attrs,
            content: ''
          };
        }
        else if( tag == "head" ) {
          bodyStarted = false;
          result.head = {
            tag    : tag,
            attrs  : attrs,
            content: ''
          };
        }
        else if( tag == "title" ) {
          titleStarted = true;
          result.title = {
            tag    : tag,
            attrs  : attrs,
            content: ''
          };
        }
        else if( tag == "body" ) {
          bodyStarted = true;
          result.body = {
            tag    : tag,
            attrs  : attrs, 
            content: ''
          };
        }
        else if( tag == "script" ) {
          var attr = knowit.CsiUtils.getAttributeByName(attrs, 'src');
          if( attr ) {
            result.scripts.push({
              tag   : tag,
              attrs : attrs,
              script: ''
            });
          }
          else {
            // inline script
            inlineScriptStarted = true;
            inlineScriptContent = [];
            result.scripts.push({
              tag   : tag,
              attrs : attrs,
              script: ''
            });
          }
        }
        else if( tag == "link" ) {
          var rel  = knowit.CsiUtils.getAttributeByName(attrs, 'rel') || '', href;
          if( /stylesheet/i.test(rel) ) {
            href = knowit.CsiUtils.getAttributeByName(attrs, 'href');
            if( href ) {
              result.styles.push({
                tag     : tag,
                attrs   : attrs,
                css     : '',
                imported: false
              });
            }
          }
          else {
            result.links.push({
              tag     : tag,
              attrs   : attrs
            });
          }
        }
        else if( tag == "style" ) {
          // inline style node
          inlineStyleStarted = true;
          inlineStyleContent = [];
          result.styles.push({
            tag     : tag,
            attrs   : attrs,
            css     : '',
            imported: false
          });
        }
        else if( tag == "meta" ) {
          result.metaTags.push({
            tag    : tag,
            attrs  : attrs
          });
        }
        else if( tag == "<?xml" ) {
          result.xmlDecl = attrs[0].value;
        }
        else if( tag == "<!DOCTYPE" ) {
          result.docType = attrs[0].value;
        }
        else {
          if( bodyStarted ) {
            var t;
            if( attrs.length ) {
              var attributes = [];
              for ( var i = 0; i < attrs.length; i++ ) {
                attributes.push(attrs[i].name + '="' + attrs[i].value + '"');
              }
              t = "<" + tag + " " + attributes.join(" ") + (unary ? "/" : "") + ">";
            }
            else {
              t = "<" + tag + (unary ? "/" : "") + ">";
            }
            if( tag == "pre" ) 
              preStarted = true;
              
            if( preStarted ) {
              preContent.push(t);
            }
            else {
              bodyContent.push(t);
            }
          }
        }
      },
      
      end: function( tag ) {
      
        //if( window.log !== undefined && log.isDebugEnabled() ) log.debug('url: ' + url + '<br/>&nbsp;tag: /' + tag);
        
        if( tag == "script" ) { 
          if( inlineScriptStarted ) {
            result.scripts[result.scripts.length-1].script = inlineScriptContent.join("\n");
            inlineScriptStarted = false;
          }
        }
        else if( tag == "style" ) {
        
          var style = result.styles.pop(),
              attrs = style.attrs,
              css   = inlineStyleContent.join("\n"),
              newAttrs,
              rel,
              m;

          inlineStyleStarted = false;
          
          // Check for "@import url(someurl.css);" pattern inside <style> tag
          while ( m = importRegex.exec(css) ) {
            newAttrs = [];
    				for ( var i = 0; i < attrs.length; i++ ) {
              newAttrs.push({
                name:    attrs[i].name,
                value:   attrs[i].value,
                escaped: attrs[i].escaped
              });
            } //~for

            // set/override the href attribute (href = m[1])
            knowit.CsiUtils.setAttributeByName( newAttrs, 'href', m[1] );

            // Add to styles
            result.styles.push({
              tag     : 'link',
              attrs   : newAttrs,
              css     : '',
              imported: true
            });
            
            // Remove match from css string
            css = css.replace(importRegex, '');  // alert("["+css+"]");
            
          } //~while

          // Keep any css left after removing @import statements
          style.css = css.trim();
          if( style.css.length > 0 ) {
            result.styles.push( style );
          }
        }
        else if( tag == "link" ) {
          // link ended -- do something
        }
        else if( tag == "body" ) {
          // Body ended -- do something
        }
        else if( tag == "title" ) {
          titleStarted = false;
          result.title.content = titleContent.join(' ').normalize(1);
        }
        else if( tag == "head" ) {
          // Head ended -- do something
        }
        else if( tag == "html" ) {
          // HTML ended -- do something
        }
        else {
          if( bodyStarted ) {
            if( tag == 'pre' ) {
              preStarted = false;
              preContent.push( "</" + tag + ">" );
              bodyContent.push( preContent.join("") );
              preContent = [];
            }
            else if( preStarted ) {
              preContent.push( "</" + tag + ">" );
            }
            else {
              bodyContent.push( "</" + tag + ">" );
            }
          }
        }
      },
      
      chars: function( text ) {
        
        //if( window.log !== undefined && log.isDebugEnabled() ) 
        //  log.debug('text: [' + text + ']' + '<br/><pre>[' + text + ']</pre>' + 
        //    ( text.trim() 
        //    ? '<br/><pre>[' + (text.match(/^\s/) ? ' ' : '') + text.normalize(1) + (text.match(/\s$/) ? ' ' : '') + ']</pre>'
        //    : '[]' ) );
        
        if(text) {
          // Note: order of ifs are significant!
          if( inlineScriptStarted ) 
            inlineScriptContent.push(text);
          else if( inlineStyleStarted ) 
            inlineStyleContent.push(text);
          else if( bodyStarted ) {
            if( preStarted )
              preContent.push( text );
            else if( text.trim() )
              // Leave one leading and trailing space if present
              bodyContent.push( (text.match(/^\s/) ? ' ' : '') + text.normalize(1) + (text.match(/\s$/) ? ' ' : '') );
          }
          else if( titleStarted ) 
            titleContent.push(text);
        }
      },
      
      comment: function( text ) {
        // Comments are stripped
      }
    });
    
    result.body.content = bodyContent.join(''); //("\n"); // Use bodyContent.join("\n") if you need "pretty" formatting
    return result;          
  },
  


  /**
   * Get a document from a spesified URL
   * @method _getDocument
   * @private
   * @return {Object} The Ajax object - only valid if the AJAX request is synchronous 
   * @throws Error if url is empty
   */
  _getDocument = function( url, options ) {
    url = (url || '').trim();
    options = options || {};
    var result;
    
    if( url ) {
      result = new Ajax.Request( url, {
        method      : options.method || 'get',
        asynchronous: options.asynchronous || true,
        parameters  : options.parameters || {},
        
        onCreate: function() {
          if (options.onCreate) {
            options.onCreate();
          }
        }
        ,onComplete:function(request) {
          if (options.onComplete) {
            options.onComplete(request);
          }
        }
        ,onSuccess: function(transport) {
          if (options.onSuccess) {
            options.onSuccess(transport);
          }
        }
        ,onFailure: function(transport){ 
          if (options.onFailure) {
            options.onFailure(transport);
          }
          else {
            alert( knowit.Csi.toString() + ': An unhandled error occured when accessing url: [' 
              + transport.request.url + ']\n (' 
              + transport.status + ': ' + transport.statusText + ')' );
          }
          return true
        }
        ,on403: function(transport) {
          if (options.on403) {
            options.on403(transport);
          }
          else {
            alert( knowit.Csi.toString() + ': 403, Your session appears to have expired. (' + 
              transport.status + ': ' + transport.statusText + ')' );
            //window.location.reload()
          }
          return true
        }
        ,on404: function(transport) {
          if (options.on404) {
            options.on404(transport);
          }
          else {
            alert( knowit.Csi.toString() + ': 404, Page not found: [' 
              + transport.request.url + ']\n (' 
              + transport.status + ': ' + transport.statusText + ')' )
          }
          return true
        }
        ,onException: function(request, exception) {
          if (options.onException) {
            options.onException(request, exception);
          }
          else {
            var e = knowit.Csi.toString() 
              + ":\nAJAX EXCEPTION thrown when accessing url: ["  + request.url + "]"
              + "\n--------------------------------------------"
              + "\nexception.name                : " + exception.name
              + "\nexception.number              : " + exception.number
              + "\nexception.description         : " + exception.description
              + "\nrequest.transport.status      : " + ( request && request.transport ? request.transport.status : "UNDEFINED" )
              + "\nrequest.transport.statusText  : " + ( request && request.transport ? request.transport.statusText : "UNDEFINED" )
              + "\nrequest.transport.responseText: " + ( request && request.transport ? request.transport.responseText : "UNDEFINED" );
            alert(e);
            throw new Error(e);
          }
          return true;
        } 
        
        // onLoaded(): Called when the response has been received but not evaluated. The same as XHR
        // ready state 2. Not recommended for use due to cross-browser differences.
        
        // onInteractive(): Called when the response has been received and parsed; some information
        // is available. The same as XHR ready state 3. Not recommended for use due to cross-browser differences.
        
      }); //~new Ajax.Request
    }
    else {
      throw new Error( knowit.Csi.toString() + '->_getDocument: Url can not be empty!' );
    }
    return result;
  };

  /**
   * Url resolving etc. after document is parsed
   */
  resolveDocument = function( documentParts ) {

    var urlRegex   = /(src|href)\s*=\s*[\'"]([^\'"]*)[\'"]/gim,
        content    = documentParts.body.content,
        onLoadAttr,
        bodyIdAttr, 
        scriptAttrs,
        style, script, attr, url, i, m, n;
    
    // Resolve urls
    for ( i = 0, n = documentParts.styles.length; i < n; i++ ) {
      style = documentParts.styles[i];
      attr = knowit.CsiUtils.getAttributeByName(style.attrs, 'href');
      if(attr && attr.value) {
        knowit.CsiUtils.setAttributeByName( style.attrs, 'href', 
          knowit.UrlResolver.resolveUrl(documentParts.url, attr.value) );
      }
    }
    
    for ( i = 0, n = documentParts.scripts.length; i < n; i++ ) {
      script = documentParts.scripts[i];
      attr = knowit.CsiUtils.getAttributeByName(script.attrs, 'src');
      if(attr && attr.value) {
        knowit.CsiUtils.setAttributeByName( script.attrs, 'src', 
          knowit.UrlResolver.resolveUrl(documentParts.url, attr.value) );
      }
    }
    
    // Add onload script from body tag
    onLoadAttr = knowit.CsiUtils.getAttributeByName( documentParts.body.attrs, 'onload' );
    if( onLoadAttr && onLoadAttr.value ) {
      // Check if script already in dom
      bodyIdAttr = knowit.CsiUtils.getAttributeByName( documentParts.body.attrs, 'id' );

      scriptAttrs = [];
      knowit.CsiUtils.setAttributeByName( scriptAttrs, 'type', 'text/javascript' );
      if( bodyIdAttr && bodyIdAttr.value ) {
        knowit.CsiUtils.setAttributeByName( scriptAttrs, 'id', 'BODY_' + bodyIdAttr.value );
      }
      documentParts.scripts.push({
        tag   : 'script',
        attrs : scriptAttrs,
        script: onLoadAttr.value
      });
    }
    
    // Resolve "href" and "src" urls in body content
    // Consider the following string: <a href="folder/some.html?a=1">xxxxxx</a>
    // m[0] == the match, ==>. href="folder/some.html?a=1"
    // m[1] == url|src,   ==> href
    // m[2] == the url,   ==> folder/some.html?a=1
    n = 0;
    while( m = urlRegex.exec(content) ) {
      if( m[2] && m[2].charAt(0) !== '#' && m[2].charAt(0) !== '?') {
        // Empty urls ('', '?' and '#') are not resolved
        url = knowit.UrlResolver.joinUrls(documentParts.url,  m[2]);
        content = content.replace(m[0], m[1] + '="' + url + '"');
        n++;
      }
    }
    if( n > 0 ) {
      documentParts.body.content = content;
    }
    return documentParts;
  };


return ({ //Public attributes and methods

  /**
   * Get a document from a spesified URL
   * @method getDocument
   * @param url {String} the document URL.
   * @param options {Object} The following options are defined:
   *
   *   method:
   *     The method to be used; default method is GET
   *
   *   parameters:
   *     You can pass the parameters for the request as the parameters property in options:
   *     e.g. options.parameters: {company: 'example', limit: 12}
   *     Parameters are passed in as a hash (preferred) or a string of key-value pairs separated by 
   *     ampersands (like company=example&limit=12). You can use parameters with both GET and POST 
   *     requests. Keep in mind, however, that GET requests to your application should never cause 
   *     data to be changed. Also, browsers are less likely to cache a response to a POST request, 
   *     but more likely to do so with GET.
   *
   *   asynchronous:
   *     Ajax requests are by default asynchronous, which means you must have callbacks that will 
   *     handle the data from a response. Callback methods are passed in the options hash when 
   *     making a request:
   *
   *   onCreate():
   *     Triggered when the Ajax.Request object is initialized. 
   *     This is after the parameters and the URL have been processed, 
   *     but before first using the methods of the XHR object.
   *     Can for example be used to show an activity indicator
   *  
   *   onComplete(request)
   *     Called after the AJAX request has finished and the response has been 
   *     completely received and parsed, irrespective of the result. 
   *     The same as XHR ready state 4.
   *     Can for example be used to hide an activity indicator
   *  
   *   onSuccess(transport)
   *     Function to call when the response has been successfully received. 
   *     It fires when the status of a response is between 200 and 300.
   *  
   *   onFailure(transport)
   *     onFailure will be executed when the response has failed, it fires when the server
   *     responds to the AJAX request with anything *but* a 200 code.
   *   
   *   on403(transport)
   *     Session expired
   *  
   *   on404(transport)
   *     Page not found
   *  
   *   onException(request, exception)
   *     Called when an error occurred in the JavaScript code while trying to execute
   *     the request or during a callback function call
   *
   * @return {Object} The Ajax object. 
   *         The object is only valid if the AJAX request is synchronous - for asynchronous
   *         Ajax use callbacks to handle data from respponse. 
   * @throws Error if url is empty
   */
  getDocument: function( url, options ) {
    return _getDocument(  url, options );
  },

  /**
   * Parses a HTML document.
   * @method parseHtml
   * @param url {String} the origin of the content
   * @param url {String} the origin of the HTML to parse. If the url is present the following post 
   *        parsing will take effect:
   *          1. Resolve urls for css and script
   *          2. Add onload script from body tag
   *          3. Resolve "href" and "src" urls in body content
   * @param content {String} the HTML to parse
   * @return {Object} as a hash with the following structure:
   *
        url     : {String} the origin of the content
        
        target  : {Object} the target the paresed content should be inserted
        
        xmlDecl : {String} Contains the xml declaration, <?xml version="1.0" ....?>, string if present
        
        docType : {String} Contains the DOCTYPE declaration, <!DOCTYPE html ...., string if present
        
        html    : {Object} <html> object with the following struct:
            tag    : {String} -> 'html'
            attrs  : {array} of name/value pairs for the tags attributes -> attr{name, value, escaped}
            content: {String} -> ''
        
        head    : {Object} <head> object as a hash with the following structure:
            tag    : {String} -> 'body'         
            attrs  : {array} of name/value pairs for the tags attributes -> attr{name, value, escaped}
            content: {String} the HTML contained in the head tag
            target : {String||Object} target node for the content
            
        title   : {Object} <title> object
            tag    : {String} -> 'title'
            attrs  : {array} of name/value pairs for the tags attributes -> attr{name, value, escaped}
            content: {String} the content of the title tag

        metaTags: {Array} of <meta> tags with the following struct:
            tag    : {String} -> 'meta'
            attrs  : {array} of name/value pairs for the tags attributes -> attr{name, value, escaped}
          
        scripts : {array} of <script> objects as a hash with the following struct:
            tag    : {String} -> 'script'
            attrs  : {array} of name/value pairs for the tags attributes -> attr{name, value, escaped}
            script : {String} the javascript code if this is an inline script or <code>null</code>
                            if the attrs array contains the src attribute.
        
        styles  : {array} of <link rel="Stylesheet"> and <style> objects
            tag     : {String} -> 'link' || 'style'
            attrs   : {array} of name/value pairs for the tags attributes -> attr{name, value, escaped}
            css     : {String} the CSS if this is an inline CSS or <code>null</code> if
                              if the attrs array contains the href attribute.
            imported: {boolean} <code>true</code> if the href comes from an 
                      @import statement oterwise <code>false</code>
                      
        links   : {array} array of <link> objects, except <link rel="Stylesheet">.
            tag     : {String} -> 'link'
            attrs   : {array} of name/value pairs for the tags attributes -> attr{name, value, escaped}
                      
        body    : {Object} <body> object
            tag    : {String} -> 'body'
            attrs  : {array} of name/value pairs for the tags attributes -> attr{name, value, escaped}
            content: {String} the parsed HTML content of the body tag (excluding inline css and javascript)
   */
  parseHtml: function( url, content ) {
    url = ( url || '' ).trim();
    var result = _parseHtml( url, content );
    if( url ) {
      result = resolveDocument( result );
    }
    return result;
  },
  
  /**
   * Loads a HTML document and parses the document 
   * @method loadHtml
   * @param url {String} the document URL to load
   * @param options {Object} @see knowit.Csi.getDocument
   *   onFinish(doc, documentParts): Called after the document has been parsed.
   *     @param doc {String} the unparsed document
   *     @param documentParts {Object} @see knowit.Csi._parseHtml for documentation of the object structure
   * @return {void} 
   * @throws Error if url is empty
   */
  loadHtml: function( url, options ) {
    options = options || {};
    var onSuccess = options.onSuccess;
    
    options.onSuccess = function( transport ) {
    
      // Document loaded successfully
      if ( typeof onSuccess == "function" ) {
        onSuccess( transport );
      }
      
      // Parse the document
      var doc = transport.responseText || '',
          documentParts = _parseHtml( url, doc );
          
      if ( typeof options.onFinish == "function" ) {
        options.onFinish( doc, documentParts );
      }
    }
    _getDocument( url, options ); 
  },
  
  /**
   * Loads a HTML document, parses it and includes the documents CSS- and JavaScript files
   * into the  including document. The HTML body (of the included document) is 'injected' 
   * into the including documents' target node.
   *
   * @method includeHtml
   * @param url {String} the document URL to load
   * @param targetNode {String||Object} target node where HTML document fragment is included
   * @param options {Object} @see knowit.Csi.getDocument
   *   onSuccess(transport): Called after the document has been loaded successfully
   *     @param transport
   *
   *   onParseHtml(doc, documentParts): Called after the document has been parsed.
   *     @param doc {String} the unparsed document
   *     @param docParts {Object} @see knowit.Csi._parseHtml for documentation of the object structure
   *
   *   onResolveDocument(doc, documentParts): Called after post parsing of the document is finished.
   *     The post parser does the following tasks:
   *       1. Resolve urls
   *       2. Add onload script from body tag
   *       3. Resolve "href" and "src" urls in body content
   *     @param doc {String} the unparsed document
   *     @param docParts {Object} @see knowit.Csi._parseHtml for documentation of the object structure
   *
   *   onFinish(doc, documentParts): Called after the documents CSS, JavaScript and HTML body has been included.
   *     @param doc {String} the unparsed document
   *     @param docParts {Object} @see knowit.Csi._parseHtml for documentation of the object structure
   *
   * @return {void}
   * @throws Error if url is empty
   *
   */
  includeHtml: function( url, targetNode, options ) {
    options = options || {};
    var onSuccess         = options.onSuccess,
        onParseHtml       = options.onParseHtml,
        onResolveDocument = options.onResolveDocument;
        
    options.onSuccess = function( transport ) {
    
      // Document loaded successfully
      if ( typeof onSuccess == "function" ) {
        onSuccess( transport );
      }
      
      // Parse the document
      var doc           = transport.responseText || '',
          documentParts = _parseHtml( url, doc );
          
      if ( typeof onParseHtml == "function" ) {
        onParseHtml( doc, documentParts );
      }
      
      // Resolve (post parse) document
      //resolveDocumentUrls( documentParts );
      documentParts = resolveDocument( documentParts );
      if ( typeof onResolveDocument == "function" ) {
        onResolveDocument( doc, documentParts );
      }
      
      // Set the target for the HTML
      documentParts.body.target = targetNode;
      
      // Include everything 
      LazyLoad.chain()
              .includeCss       ( documentParts.styles )
              .includeHtmlBody  ( documentParts.body )
              .includeScriptOnce( documentParts.scripts )
              .onComplete       ( function() {
                if( typeof options.onFinish == 'function' ) {
                  options.onFinish( doc, documentParts );
                }
              });
    }
    
    // Get document and run the callbacks (see code above).
    _getDocument( url, options ); 
  },
  
  /** 
   * include required *.js and *.css files if they do not already exist in DOM
   * @method require
   * @param url {string|string[]} the url(s) to include
   * @param callback {function()} function to execute when loading is finished
   */
  require: function( url, callback ) {
    if( url ) {
      var urls = knowit.CsiUtils.toArray( url ), n = urls.length,
          styles = [], scripts = [], u, i;
          
      for( i = 0; i < n; i++ ) {
        if( urls[i].trim() ) {
          // @TODO: use knowit.UriParser to detect type of file
          
          u = knowit.UrlResolver.resolveUrl( urls[i] );
          if( u.indexOf('.js') > 0 ) {
            scripts.push({
              tag   : 'script', 
              attrs : [ { name: 'type', value: 'text/javascript', escaped: 'text/javascript' }, 
                        { name: 'src',  value: u,                 escaped: knowit.CsiUtils.escape(u) } ], 
              script: '' });
          }
          else if ( u.indexOf('.css') > 0 ) {
            styles.push({
              tag   : 'link', 
              attrs : [ { name: 'type', value: 'text/css',   escaped: 'text/css' }, 
                        { name: 'rel',  value: 'stylesheet', escaped: 'stylesheet' }, 
                        { name: 'href', value: u,            escaped: knowit.CsiUtils.escape(u) } ], 
              script: '' });
          }
        }
      } //~for
      
      LazyLoad.chain()
        .includeCss( styles )
        .includeScriptOnce( scripts )
        .onComplete( function() {
          if( typeof callback == 'function' ) {
            callback();
          }
        });
    } //~if( url )
  },
  
  purge: function( url ) {
    throw new Error( 'knowit.Csi.purge Method NOT YET IMPLEMENTED!' );
  },
  
  toString: function() { 
    return 'knowit.Csi, version ' + VERSION;
  }
  
}); //~return

} ) (); //~anonymous function immediately invoked with ()

}; //~if(!knowit.Csi)

window.status = '';











  /** 
   * Generates an HTML element, this is not appended to a document
   * @method _node
   * @param type {string} the type of element
   * @param attr {string} the attributes
   * @param win {Window} optional window to create the element in
   * @return {HTMLElement} the generated node
   * @private
   */
  /*
  var _node = function(type, attr, win) {
      var w = win || Y.config.win, d=w.document, n=d.createElement(type);

      for (var i in attr) {
          if (attr[i] && Y.Object.owns(attr, i)) {
              n.setAttribute(i, attr[i]);
          }
      }

      return n;
  };
  */

  /**
   * Generates a link node
   * @method _linkNode
   * @param url {string} the url for the css file
   * @param win {Window} optional window to create the node in
   * @return {HTMLElement} the generated node
   * @private
   */
  /*
  var _linkNode = function(url, win, charset) {
      var c = charset || "utf-8";
      return _node("link", {
              "id":      "yui__dyn_" + (nidx++),
              "type":    "text/css",
              "charset": c,
              "rel":     "stylesheet",
              "href":    url
          }, win);
  };
  */
  
  /**
   * Generates a script node
   * @method _scriptNode
   * @param url {string} the url for the script file
   * @param win {Window} optional window to create the node in
   * @return {HTMLElement} the generated node
   * @private
   */
  /*
  var _scriptNode = function(url, win, charset) {
      var c = charset || "utf-8";
      return _node("script", {
              "id":      "yui__dyn_" + (nidx++),
              "type":    "text/javascript",
              "charset": c,
              "src":     url
          }, win);
  };
  */
