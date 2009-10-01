/*
 * File        : knowit.ComponentController.js
 * Version     : 0.1a
 * Author      : leif.olsen@knowit.no / ken.gullaksen@knowit.no
 * Copyright   : 
 * Description : 
 * Notes       : 
 * Limitations : 
 * Dependencies: prototype.js(for Ajax), knowit.NotiticationManager.js, knowit.Cookie.js, 
 *               knowit.Csi.js, knowit.CommandQueue.js, knowit.UriParser.js, knowit.Array.js, 
 *               knowit.String.js
 * Created     : 20081126
 * History     : 
 */
 
window.status = 'Loading [knowit.ComponentController.js]';

// Define the namespace 
window.knowit = window.knowit || {};

if( !knowit.ComponentController ) {
knowit.ComponentController = ( function() {

  // Private attributes and methods
  var VERSION = '0.4', 
  
  /**
   * The coolkie servlet name
   */
  cookieJarName = 'cookiejar',
  
  /**
   * Flag that tells if cookie is being fetched
   */
  fetchingCookie = false,
      
  /**
   * The url to the SSO servlet
   */
  ssoUrl = '/cc/sso/sso.php',
      
  /**
   * SSO queues need SSO to function and must wait for SSO to fire. There is one sso 
   * queue per application url. This is an hash object where the application url is 
   * the hash. Each hash keeps a command queue with the pending requests for a particular 
   * app.
   * @property ssoQueues
   * @type {Object} 
   * @static
   * @private
   */
  ssoQueues = {},
  
  /**
   * Application urls in a stateless queue does not need SSO to function and can execute ASAP.
   * @property statelessQueue
   * @type {knowit.CommandQueue} 
   * @static
   * @private
   */
  statelessQueue, 
  
  /**
   *
   */
  error = function( message ) {
    throw new Error( knowit.ComponentController.toString() + ': ' + message );
  },

  /**
   *
   */
  getAppUrl = function( url ) {
    var parsedUrl = knowit.UriParser.parse( url ),
        dirParts  = parsedUrl['directory'].split('/');
    return ( '/' + dirParts[1] );
  },
  
  getAppPostfix = function( url ) {
	var parsedUrl = knowit.UriParser.parse( url ),
        fileParts  = parsedUrl['file'].split('.');
    return ( '.' + fileParts[1] );
  },

  /**
   *
   */
  triggerApplicationCookie = function( url, onCookieReady ) {
  
    var cookieJarUrl = getAppUrl( url ) + '/' + cookieJarName + getAppPostfix( url ),
       sessionCookie = knowit.Cookie.read( 'JSESSIONID' ),
       i, appQueue;
    
    if( !sessionCookie ) {
      // pause all q's
      for ( i in ssoQueues ) {
        appQueue = ssoQueues[i];
        appQueue.running = appQueue.queue.isRunning();
        appQueue.queue.pause();
      }
      // get cookie
      if (!fetchingCookie) {
        //log.debug("fetching...");
        fetchingCookie = true;
        knowit.Csi.getDocument(cookieJarUrl, {
          onSuccess: function(transport) {
            var cookie = knowit.Cookie.read('JSESSIONID');
            fetchingCookie = false;
            if (cookie) {
              // resume all previously running q's
              for (i in ssoQueues) {
                appQueue = ssoQueues[i];
                if (appQueue.running) {
                  appQueue.queue.resume();
                }
              }
              // Signal back that we are ready to run
              onCookieReady();
            }
            else {
              error('Could not get JSESSIONID cookie');
            }
          }
        });
      }
      else {
        // cookie is being fetched, try again with timeout
        //log.debug("waiting...");
        window.setTimeout(
          function() {
             triggerApplicationCookie(url, onCookieReady)
          }, 100 );
      }
    }
    else {
      // Signal back that we are ready to run
      onCookieReady();
    }
  },
  
  /**
   *
   */
  getAppQueue = function( url, options, onSsoReady) {
    var parsedUrl = knowit.UriParser.parse( url ),
        appUrl    = parsedUrl['directory'].split('/')[1],
        appQueue  = ssoQueues[ appUrl ];

    // @TODO: Check for race conditions
    //log.debug('getAappQueue: begin');
    
    if( !appQueue ) {
      appQueue = {
        ssoFired: false,
        running : false,
        queue   : new knowit.CommandQueue( { running: false, parallel: false } )
      };
      ssoQueues[ appUrl ] = appQueue;
    }
    
    // check for sso
    if( !appQueue.ssoFired ) {
      // fire sso
      appQueue.queue.pause();
      var jsessionid = knowit.Cookie.read( 'JSESSIONID' ).getValue();
      knowit.Csi.getDocument( ssoUrl, {
        parameters: {app : appUrl, jsessionid : jsessionid},
        onSuccess: function( transport ) {
          //  @TODO:  Check sso status ok
          log.debug("sso onsuccess \n"+transport.responseText);
          appQueue.queue.run();
          onSsoReady( appQueue.queue );
        },
        onCreate: options.onCreate,
        onFinish: options.onFinish
      } );
    }
    else {
      onSsoReady( appQueue.queue );
    }
  };
  
return ({ //Public attributes and methods

  /**
   * Loads a HTML document ...
   *
   * @method includeComponent
   * @static
   * @param url {String} the document URL to load
   * @param targetNode {String||Object} target node where HTML document fragment is included
   * @param options {Object} @see knowit.Csi.getDocument
   *   onSuccess(transport): Called after the document has been loaded successfully
   *     @param transport
   *
   *   onParseHtml(doc, documentParts)  : Called after the document has been parsed.
   *     @param doc {String} the unparsed document
   *     @param docParts {Object} @see knowit.Csi._parseHtml for documentation of the object structure
   *
   *   onResolveDocument(doc, documentParts): Called after post parsing of the document has finished.
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
   *   requireSso {Boolean} indicates whether SSO is required or not, default false.
   *
   * @return {void}
   * @throws Error if url is empty
   *
   */
  includeComponent: function( url, targetNode, options ) {
    if( !knowit.Cookie.isEnabled() ) {
      error( 'Cookies not enabled!' );
      return;
    }
    
    options = options || {};
    var cmd = {
      thisArg: knowit.Csi,                 // The scope of the func to execute
      func   : knowit.Csi.includeHtml,     // The function (or command) to execute
      args   : [url, targetNode, options]  // Arguments passed to func when it executes
    };
    
    if( options.requireSso ) {
      // Get session cookie
      triggerApplicationCookie( url, function() { // onCookieReady
        // get queue
        getAppQueue( url, options, function( q ) { // onSsoReady
          if( q ) {
            q.add( cmd );
          }
        })
      });
    }
    else {
      if(!statelessQueue) {
        statelessQueue = new knowit.CommandQueue( { running: false, parallel: false } );
        statelessQueue.run();
      }
      statelessQueue.add( cmd );
    }
  },

  /**
   * Set name of servlet to ask for session cookie
   */
  setCookieJarName: function( name ) {
    cookieJarName = name;
  },
    
  /**
   * Set sso url
   */
  setSsoUrl: function( url ) {
    ssoUrl = url;
  },
    
  toString: function() { 
    return ( 'knowit.ComponentController, version ' + VERSION ); 
  }
  
}); //~return

} ) (); //~anonymous function immediately invoked with ()
};

window.status = '';
