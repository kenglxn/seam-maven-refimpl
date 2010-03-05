/* 
 * File        : site.js
 * Version     : 
 * Author      : Leif Olsen, leif.olsen@knowit.no
 * Copyright   : none
 * Description : 
 * Notes       :
 * Dependencies: 
 * Created     : 20100305
 * History     : 
 *               
 */
var iframeDelta = (document.all && !window.opera) ? 2 : 0; // Correct some pixelstuff in m$ie 6/7/8

function getElement( element ) {
  return element ? (typeof element === "string" ? document.getElementById( element ) : element) : null;
}

function toEms( px ) {
  return (px / 10) + 'em';
}

function toggleElement( target ) {
  target = getElement( target );
  if( target ) {
    target.style.display = target.style.display.match(/none/i) ? 'block' : 'none';
  }
  return target;
}

function getIframeDocumentHeight( target ) {
  if( target ) {
    return target.contentDocument && target.contentDocument.body.scrollHeight // W3C DOM document syntax
         ? target.contentDocument.body.scrollHeight 
         : target.Document && target.Document.body.scrollHeight               // IE DOM syntax
         ? target.Document.body.scrollHeight 
         : 0;
  }
  return 0;
}

function toggleIframe( target ) {

  toggle = function( element ) {
    var iframeHeight = (parseFloat(element.style.height) || 0) * 10,
        docHeight    = getIframeDocumentHeight( element );
        
    element.style.height = toEms( iframeHeight < docHeight ? docHeight + iframeDelta : 0 );
  }

  var elements = target.parentNode.getElementsByTagName( 'iframe' )
  for( var i=0; i<elements.length; i++) {
    toggle( elements[i] );
  }
  return elements;
}