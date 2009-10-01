/*
 * File        : knowit.DomPrinter.js
 * Version     : 1.0
 * Author      : leif.olsen@knowit.no
 * Copyright   : 
 * Description : DOM "Pretty" print
 * Notes       : 
 * Limitations : 
 * Dependencies: knowit.js, knowit.String.js
 * Created     : 20081009
 * History     : 
 */
 
window.status = 'Loading [knowit.DomPrinter.js]';

if ( typeof $ != 'function' ) {
// The $ function from prototype
$ = function(element) {
 if (arguments.length > 1) {
   for (var i = 0, elements = [], length = arguments.length; i < length; i++)
     elements.push($(arguments[i]));
   return elements;
 }
 return element.nodeName ? element : typeof element === "string" ? document.getElementById(element) : null;
}
}

// Define the namespace 
if(knowit == undefined) {
  var knowit = {};
}

if( !knowit.DomPrinter ) {

knowit.DomPrinter = ( function() {

  // Private attributes and methods
  var VERSION = '1.0',
  
  lt    = '&lt;',
  gt    = '&gt;',
  sp    = '&nbsp;&nbsp;&nbsp;',
  br    = '<br />';
  slash = '&oslash;';
  
  makeMap = function (str) {
    var obj = {}, items = str.split(","), i, n;
    for ( i = 0, n = items.length; i < n; i++ )
      obj[ items[i] ] = true;
    return obj;
  };
	var empty = makeMap('area,base,basefont,br,col,frame,hr,img,input,isindex,link,meta,param,embed');

  
  indent = function( level ) {
    var result = '', i;
    for ( i = 0; i < level; i++ ) {
      result += sp;
    }
    return result;
  };
  
  traverse = function( node, level ) {
    level = level || 0;
    var unary = false, result = '',
        tagName;
    
    if (node) {  
      // Print opening tag
      
      if( node.tagName ) {
        tagName = node.tagName.toLowerCase();
        unary = empty[ tagName ];

        var id        = node.id, 
            className = node.className, 
            href      = node.href,
            src       = node.src;
            
        result += indent( level ) + lt + tagName
               + (id        ? ' id="'+id+'"' : '') 
               + (className ? ' class="'+className+'"' : '' ) 
               + (href      ? ' href="'+href+'"' : '' ) 
               + (src       ? ' src="'+src+'"' : '' ) 
               + (unary ? ' /' : '') + gt + br;
      }
      else if ( node.nodeName == "#text" ) {
        if( node.data.trim() ) {
          result += indent( level ) + node.data + br;
        }
      }
      
      // Traverse
      if(node.childNodes.length) {
        var n = 0, childNode;  
        while ( (childNode = node.childNodes[ n++ ] ) ) { 
          result += traverse( childNode, level+1 );
        }      
      }
      
      // Print closing tag
      if( node.tagName && !unary ) {
        result += indent( level ) + lt + '/' + tagName + gt + br;
      }
      
    }
    return result;
  };
  
return ({ //Public attributes and methods

  escapeOutput: function( flag ) {
    if(flag) {
      lt = '&lt;';
      gt = '&gt;';
      sp = '&nbsp;&nbsp;&nbsp;';
      br = '<br />';
    }
    else {
      lt = '<';
      gt = '>';
      sp = '   ';
      br = '\n';
    }
  },

  print: function( startNode, outputNode ) {
    var result = '';
    startNode = $(startNode);
    
    if( startNode ) {
      result = traverse( startNode, 0);
      outputNode = $(outputNode);
      
      if( outputNode ) {
        outputNode.innerHTML = result;
      }
    }
    return result;
  },

  toString: function() { 
    return ( 'knowit.DomPrinter, version ' + VERSION ); 
  }
  
}); //~return

} ) (); //~anonymous function immediately invoked with ()
};

window.status = '';

