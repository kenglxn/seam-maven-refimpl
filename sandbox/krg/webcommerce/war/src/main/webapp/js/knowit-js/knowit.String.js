/* 
 * File        : knowit.String.js
 * Version     : 1.0
 * Author      : Leif Olsen, leif.olsen@start.no / leif.olsen@knowit.no
 * Copyright   : (c) 2003-2008
 *               Do not use (or abuse) without permission
 * Description : Extensions to standard javascript String object
 * Notes       : 
 * Dependencies: 
 * Created     : 20031201
 * History     : 20070531 - Modified for community-r
 *             : 20080520 - Modified for knowit
 *             : 20080925 - Moved to separate module: knowit.String.js
 *             : 20090119 - Added String.prototype.format function
 */
window.status = 'Loading [knowit.String.js]';

/**
 * Checks if a string contains a newline character
 * @return {boolean} true if a newline character ocurs in the string
 */
if(!String.prototype.hasNewLine) {
String.prototype.hasNewLine = function() {
  return (/\n/).test(this);
};
}

/**
 * Checks if a string is empty
 * @return {boolean} true if empty
 */
if(!String.prototype.isEmpty) {
String.prototype.isEmpty = function() {
  return this == '';
};
}

/**
 * Checks if a string only contains whitespaces
 * @return {boolean} true if blank
 */
if(!String.prototype.isBlank) {
String.prototype.isEmpty = function() {
  return /^\s*$/.test(this);
};
}

// hasToken:
// Returns true if 'token' ocurs in 's'
// flag     : gim, see JavaScript RegExp documentation
//                 g - Not applicable, no meaning here
//                 i - Ignore case
//                 m - Multiline
// Example  : this   : 'a String'
//            token  : 'str'
//            flag   : i
//            returns: true
// Issues   : 
// To-do    : 
//
if(!String.prototype.hasToken) {
String.prototype.hasToken = function(token, flag) {
  
  return( (token) ? new RegExp("(^|\\s*\\b)"+token+"\\b", flag||'').test(this) : false );
  
  //return( (token) ? new RegExp("\\s*\\b"+token+"\\b", flag||'').test(this) : false );
  //return( (token) ? new RegExp("(^|\\s)"+token+"($|\\s)", flag||'').test(this) : false );
};
}

// hasWord:
// Returns true if 'word' ocurs in 's'
// A word is defined as a sequence of characters separeted by one or more 
// whitespaces and/or punctation marks
// flag     : gim, see JavaScript RegExp documentation
//                 g - Not applicable, no meaning here
//                 i - Ignore case
//                 m - Multiline
// Example  : this   : 'this is my cat' or 'this is my cat.'
//            word   : 'cat'
//            flag   : ''
//            returns: true
//          : this   : 'this is my .cat' or 'this is my-cat'
//            word   : 'cat'
//            flag   : ''
//            returns: false
// Issues   : 
//            this   : 'this is my [cat]' or 'this is my (cat)' or 'this is my {cat}'
//            word   : 'cat'
//            flag   : ''
//            returns: false but should return true since cat is a word surrounded by parens
// To-do    : Extend punctation rules
//            word surrounded by () or {} or [] should match
//
if(!String.prototype.hasWord) {
String.prototype.hasWord = function(word, flag) {
  return( (word) ? new RegExp("(^|\\s)"+word+"($|(?=[\\s.,;:!\\?]))", flag||'').test(this) : false );
  
  //return( (word) ? new RegExp("(^|\\s)"+word+"($|\\s)", flag||'').test(this) : false );
};
}

// normalize:
// String.normalize prototype, removes leading and trailing (white)spaces and
// replaces interior consecutive spaces with a single space. 	If the string contains
// line breaks then the the line break will be kept in advantage of the space if preserveLineBreaks flag is true.
// This method does not change the String object it is called on. It simply returns a new string.

// Example: '  hat    cat  rat   ' -> 'hat cat rat'

if(!String.prototype.normalize) {
String.prototype.normalize = function( preserveFlag ) {
  var preserveNone  = 0, // 00b
      preserveSpace = 1, // 10b
      preserveLbr   = 2; // 01b
      
  preserveFlag = preserveFlag || preserveNone;
  for (var i = 0, lines = this.split("\n"), length = lines.length; i < length; i++) {
    lines[i] = lines[i].trim().replace(/ +/g, ' ');
  }
  return ( preserveFlag & preserveSpace 
    ? lines.join(' ') : preserveFlag & preserveLbr 
    ? lines.join('\n') : lines.join('') ).trim();
};
}

// replaceWord:
// String.replaceWord prototype, replaces occurences of oldWord with newWord
// A word is defined as a sequence of characters separeted by one or more whitespaces
// This method does not change the String object it is called on. It simply returns a new string.
// flag     : gim, see JavaScript RegExp documentation
//                 g - Replace all occurences
//                 i - Ignore case
//                 m - Multiline
// normalize: removes leading and trailing spaces and
//            replaces interior consecutive spaces with a single space
// Example:   Replace the word 'cat' with '' in '   cat hat cat     sat bat c at end    '
//            this     : '   cat hat cat     sat bat c at end    '
//            oldWord  : 'cat'
//            newWord  : ''
//            flag     : 'g'
//           _normalize: true 
//            returns  : 'hat sat bat c at end'
if(!String.prototype.replaceWord) {
String.prototype.replaceWord = function(oldWord, newWord, flag, _normalize) {
  newWord = newWord ? (' ' + newWord.trim() + ' ') : '';
  return( oldWord ? _normalize ? 
   this.normalize(1).replace(new RegExp('(^|\\s*\\b[^-])'+oldWord+'($|\\b(?=[^-]))', flag||''), newWord) : 
   this.replace(new RegExp('(^|\\s*\\b[^-])'+oldWord+'($|\\b(?=[^-]))', flag||''), newWord) : this );
};
}

/**
 * Splits name value pairs into an hash array.
 * example: "a:1;b:2;c:3" -> result['a']=1, result['b']=2, result['c']=3
 *    
 * @param {Char} attributeSeparator, default value is ';'
 * @param {Char} nameValueSeparator, default value is ':'
 * 
 * @return {Array} Hash array with name value pairs where name is the index used to get the
 * corresponding value. 
 */
if(!String.prototype.splitAttributes) {
String.prototype.splitAttributes = function(attributeSeparator, nameValueSeparator) {
  var result=[], j;
  if( this.length > 0 ) {
    // the '\\s*'+(separator)+'\\s*' ensures that whitespaces are removed from
    // the split. No need to trim name value pairs.
    var attrs = this.split(new RegExp('\\s*'+(attributeSeparator || ';')+'\\s*'));
    var nv = new RegExp('\\s*'+(nameValueSeparator || ':')+'\\s*');
    for(j=0; j<attrs.length; j++) {
      var pair = attrs[j].split(nv);
      if(pair && pair.length>1 && pair[1]) {
        result[pair[0]] = pair[1]; 
      } 
    }
  }
  return(result);    
};
}

/**
 * Converts a hyphenated string to a camelcase string.
 * Example: "this-is-hypen" --> "thisIsHypen"
 * @return {String} The camelized string
 */
if(!String.prototype.toCamelCase) {
String.prototype.toCamelCase = function() {
  var s = this;
  for(var exp = /-([a-z])/; exp.test(s); s = s.replace(exp, RegExp.$1.toUpperCase()) );
  return s;
};
}

/**
 * Converts a cameCased string to a hyphenated string.
 * Example: "thisIsCamel" --> "this-is-camel"
 * @return {String} The hypenated string
 */
if(!String.prototype.toHyphen) {
String.prototype.toHyphen = function() {
  return this.replace(/([A-Z])/g, "-$1" ).toLowerCase();
};
}

// trim:
// String.trim prototype, removes leading and trailing spaces
// This method does not change the String object it is called on. It simply returns a new string.
// Example: '  hat    cat  ' returns 'hat    cat'
//
//var TRIM = /^\s+|\s+$/g;
//String.prototype.trim = function() {
//  return( this.replace(TRIM, '') );
//};
//
//String.prototype.trim = function() {
//  return this.replace(/^\s\s*/, '').replace(/\s\s*$/, '');
//};



if(!String.prototype.trim) {
// See: http://blog.stevenlevithan.com/archives/faster-trim-javascript
String.prototype.trim = function() {
	var	s = this.replace(/^\s\s*/, ''),
		  i = s.length,
		  ws= /\s/;
  if( i ) {
  	while (ws.test(s.charAt(--i)));
  	return s.slice(0, i + 1);
  }
  return '';
};
}

// trimLeft:
// Removes leading spaces
// This method does not change the String object it is called on. It simply returns a new string.
// Example: '  hat    cat  ' returns 'hat    cat  '
if(!String.prototype.trimLeft) {
String.prototype.trimLeft = function() {
  return( this.replace(/^\s\s*/, '') );
};
}

// trimRight:
// Removes trailing spaces
// This method does not change the String object it is called on. It simply returns a new string.
// Example: '  hat    cat  ' returns '  hat    cat'
if(!String.prototype.trimRight) {
String.prototype.trimRight = function() {
  return( this.replace(/\s\s*$/, '') );
};
}

// You supply a template string, in which you add place-holders for values using {0} to {9}, 
// and then supply up to 9 other arguments which represent the strings to insert. For example:
// "And the {0} want to know whose {1} you {2}".format("papers", "shirt", "wear"); ==>
// And the papers want to know whose shirt you wear
// see: http://code.google.com/p/base2/
// see: http://frogsbrain.wordpress.com/2007/04/28/javascript-stringformat-method/
if(!String.prototype.format) {
String.prototype.format = function( /* arguments */ ) {
  var pattern = /\{\d+\}/g;
  var args = arguments;
  return this.replace( pattern, function(capture){ 
    return args[capture.match(/\d+/)]; 
  });
};
}


window.status = '';
