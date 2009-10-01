/* 
 * File        : knowit.js
 * Version     : 1.0
 * Author      : Leif Olsen, leif.olsen@start.no / leif.olsen@knowit.no
 * Copyright   : (c) 2003-2008
 *               Do not use (or abuse) without permission
 * Description : 
 * Notes       :
 * Dependencies: 
 * Created     : 20031201
 * History     : 20070531 - Modified for community-r
 *             : 20080520 - Modified for knowit
 *             : 20080925 - Moved string and math functions to separate modules: 
 *                          knowit.String.js and knowit.Math.js
 *               
 */
window.status = 'Loading [knowit.js]';

// Define the knowit namespace
//if(knowit == undefined) {
//  var knowit = {
//    VERSION: '1.0'
//  };
//}
window.knowit = window.knowit || { VERSION: '1.0' };

// Modern browser check
if(!document.getElementById) {
  alert('DEBUG ERROR: This browser does not support [document.getElementById]');
}
if(!document.getElementsByTagName) {
  alert('DEBUG ERROR: This browser does not support [document.getElementsByTagName]');
}
if(!document.createElement) {
  alert('DEBUG ERROR: This browser does not support [document.createElement]');
}
if(!document.createTextNode) {
  alert('DEBUG ERROR: This browser does not support [document.createTextNode]');
}

// Global consts
var IS_XHTML  = (typeof document.createElementNS != 'undefined');
var IS_STRICT = (document.compatMode && document.compatMode.indexOf("CSS1Compat") >= 0);

// document.compatMode: 
//   'BackCompat' Standards-compliant mode is not switched on. (quirks) --> document.body
//   'CSS1Compat' Standards-compliant mode is switched on.     (strict) --> document.documentElement
var IS_IE6_STRICT = (!window.innerHeight && document.compatMode && document.compatMode.indexOf("CSS1Compat") >= 0);


// IE<7 does not support the node-type constants defined by the Node interface so we define them here
if (!window.Node) {
  var Node = {                      // Code copyed from Javascript: The Definitive Guide 4th ed
    ELEMENT_NODE               : 1, // Contains the name of the (XML) tag,
                                    // with any namespace prefix included if present.
    ATTRIBUTE_NODE             : 2, // Contains the name of the attribute.
    TEXT_NODE                  : 3, // Contains the literal string "#text".
    CDATA_SECTION_NODE         : 4, // Contains the literal string "#cdata-section".
    ENTITY_REFERENCE_NODE      : 5, // Contains the name of the entity referenced.
                                    // Note that the name does not include the leading
                                    // ampersand or the trailing semicolon.
                                    // The name includes the namespace if one is present.
    ENTITY_NODE                : 6, // Contains the name of the entity.
    PROCESSING_INSTRUCTION_NODE: 7, // Contains the target;
                                    // the first token following the <? characters.
    COMMENT_NODE               : 8, // Contains the literal string "#comment".
    DOCUMENT_NODE              : 9, // Contains the literal string "#document".
    DOCUMENT_TYPE_NODE         :10, // Contains the name of the document type;
                                    // for example, xxx in <!DOCTYPE xxx ...>.
    DOCUMENT_FRAGMENT_NODE     :11, // Contains the literal string "#document-fragment".
    NOTATION_NODE              :12  // Contains the name of the notation.
  };
}

// apply prototype - modified from http://youngpup.net/projects/dhtml/listener/listener.js
if( !Function.prototype.apply ) {
Function.prototype.apply = function( thisObj, params ) {
  if( thisObj == null || thisObj == undefined ) { thisObj = window; }
  if( !params ) { params = []; }
  var args = [];
  for( var i = 0; i < params.length; i++ ) {
    args[ args.length ] = "params[" + i + "]";
  }
  thisObj.__method__ = this;
  var returnValue = eval( "thisObj.__method__(" + args.join( "," ) + ");" );
  thisObj.__method__ = null;
  return returnValue;
};
}

/**
 * document.createElement:
 * when dealing with XML documents Gecko needs you to use document.createElementNS 
 * in place of document.createElement when manipulating the DOM. 
 * See: http://simon.incutio.com/archive/2003/06/15/javascriptWithXML
 * Note: The element to be created must be named in lowercase
*/

if(document.createElementNS && 
 ((document.compatMode && document.compatMode.indexOf("CSS1Compat") >= 0)) ) {
  document.createElement = function(tag) {
    tag = tag ? tag.toLowerCase() : 'div';
    return document.createElementNS('http://www.w3.org/1999/xhtml', tag);   
  };
}

window.status = '';


/*
function $() {
 	if (arguments.length == 0) {
 		return undefined;
 	} else if (arguments.length == 1) {
 		if (arguments[0].constructor == String)
 			return document.getElementById(arguments[0]);
 		else
 			return arguments[0];
 	} else {
 		var results = new Array(arguments.length);
 		for (var i = 0; i < arguments.length; i++) {
 			if (arguments[i].constructor == String) {
 				results[i] =
 					document.getElementById(arguments[i]);
 			} else {
 				results[i] = arguments[i];
 			}
 		}
 		return results;
 	}
 }
*/
