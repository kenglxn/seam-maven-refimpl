/*
 * File        : knowit.UrlResolver.js
 * Version     : 1.0
 * Author      : Leif Olsen <leif.olsen@knowit.no>
 * Copyright   : (c) 2008 
 * Description : 
 * Notes       : 
 * Limitations : 
 * Dependencies: knowit.js, knowit.String.js, knowit.UriParser.js
 * Created     : 20080929
 * History     : 
 */
 
window.status = 'Loading [knowit.UrlResolver.js]';

// Define the knowit namespace 
if(knowit == undefined) {
  var knowit = {};
}

if( !knowit.UrlResolver ) {
knowit.UrlResolver = ( function() {

  // Private attributes and methods
  var VERSION = '1.0',
  
  removeEmptySlotsFromTopAndBottomOfStringArray = function( theArray ) {
  
    var result = [], i, j;
    for ( i = 0; i < theArray.length; i++ ) {
      if( theArray[i].trim() ) {
        break;
      }
    }
    if( i < theArray.length ) {
      j = theArray.length - 1;
      do {
        if( theArray[j].trim() ) {
          result = theArray.slice(i, j+1);
          break;
        }
      } while ( --j >= i )
    }
    return result;
  };
  
  stripDotSlash = function( url ) {
    return url.indexOf('./') == 0 ? url.substring(2) : url;
  };
  
  joinUris = function( baseUri, relativeUri ) {
  
    var result = '', b, f, r, s;
    
    if( relativeUri['protocol'] || relativeUri['authority'] ) {
      // relativeUri is actually an absolute address
      result = relativeUri['source'];
    }
    else if ( !baseUri['directory'] ) {
      // baseUri and relativeUri has same root
      result = relativeUri['source'];
    }
    else {
      // protocol://host:port always from baseUri
      f = baseUri['protocol']  ? baseUri['protocol'] + '://' : '';
      f+= baseUri['authority'] ? baseUri['authority']        : '';
      
      if ( relativeUri['relative'].indexOf('/') == 0 ) {
        // relativeUri is actually an absolute address
        result = f + relativeUri['relative'];
      }
      else {
        // Join uris
        if( relativeUri['relative'].indexOf('../') == 0 ) {
        
          r = removeEmptySlotsFromTopAndBottomOfStringArray( relativeUri['directory'].split('/') );
          b = removeEmptySlotsFromTopAndBottomOfStringArray( baseUri['directory'].split('/') );
          
          // 'peel' off directory elements
          while( r.length ) {
            if( r[0] !== '..' ) {
              break;
            }
            if( b.length && b[b.length-1] !== '..' ) {
              r.shift();
              b.pop();
            }
            else {
              break;
            }
          }
          
          // Add remaining relative directory to base directory
          while( r.length ) {
            b.push( r.shift() );
          }

          // Add file, query and anchor
          result = (f ? f + '/' : '') 
                 + (b ? b.join('/') : '') + '/' + relativeUri['file']             
                 + (relativeUri['query']  ? '?' + relativeUri['query']  : '') 
                 + (relativeUri['anchor'] ? '#' + relativeUri['anchor'] : '');
        }
        else {
          result = f + baseUri['directory'] + relativeUri['relative'];
        }
      }
    }
    return result
  };  
  

return ({ //Public attributes and methods

  /**
   * Creates a relative URL based on baseUrl and relativeUrl
   * @method joinUrls
   * @param baseUrl {String}
   * @param relativeUrl {String} 
   * @return {String} the joined URL
   */
  joinUrls: function( baseUrl, relativeUrl ) {
    return( baseUrl && relativeUrl && relativeUrl !== baseUrl 
           ? joinUris( knowit.UriParser.parse(stripDotSlash(baseUrl)), 
                       knowit.UriParser.parse(stripDotSlash(relativeUrl)) )
           : baseUrl || relativeUrl || '' );
  },
  
  
  /** 
   * Creates an URL based on baseUrl and relativeUrl.
   * If the user agent is Internat Explorer then a relative url is created, oterwise an absolute url is created.
   * @method resolveUrl
   * @param baseUrl {String}
   * @param relativeUrl {String} 
   * @return {String} the resolved URL
   */
  resolveUrl: function( baseUrl, relativeUrl ) {
    var result = this.joinUrls( baseUrl, relativeUrl );
    if( result && !knowit.UserAgent.ie ) {
      // w3c browsers makes any relative URL in <script>, <link> and <style> tags 
      // absolute by adding the current document's absolute path to the URL
      result = this.joinUrls( document.URL, result );
    }
    return result; //alert(baseUrl + '\n' + relativeUrl + '\n' + result); 
  },
  
  /** 
   * Creates an absolute URL based on baseUrl and relativeUrl
   * @method resolveAbsuluteUrl
   * @param baseUrl {String} 
   * @param relativeUrl {String} 
   * @return {String} the resolved URL
   */
  resolveAbsuluteUrl: function( baseUrl, relativeUrl ) {
    result = this.joinUrls( document.URL, this.joinUrls(baseUrl, relativeUrl) );
    return result;
  },

  toString: function() { 
    return ( 'knowit.UrlResolver, version ' + VERSION );
  }
  
}); //~return

} ) (); //~anonymous function immediately invoked with ()
};

window.status = '';
