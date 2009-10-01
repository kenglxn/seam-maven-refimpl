/* 
 * File        : knowit.Array.js
 * Version     : 1.0
 * Author      : Leif Olsen, leif.olsen@knowit.no
 * Copyright   : (c) 2003-2008
 *
 * Description : Extensions to the javascript Array object introduced in JavaScript 1.6
 *               and JavaScript 1.8. 
 *               @see https://developer.mozilla.org/en/New_in_JavaScript_1.6#Array_extras
 *               @see http://www.coryhudson.com/lab/javascript/cross-browser/array.extras.js
 *               @see http://www.webreference.com/programming/javascript/ncz/column4/index.html
 *               @see http://www.dustindiaz.com/basement/sugar-arrays.html
 * Notes       : 
 * Dependencies: 
 * Created     : 20081205
 * History     : 
 */
 
 /**
  * Extracts a section of an array and returns a new array.
  * Copied from: http://osteele.com/sources/javascript/functional/functional.js
  * @param begin {integer} Zero-based index at which to begin extraction.   
  * @param end  {integer} Zero-based index at which to end extraction:
  *             * slice extracts up to but not including end. slice(1,4) extracts the second element 
  *               through the fourth element (elements indexed 1, 2, and 3).
  *             * As a negative index, end indicates an offset from the end of the sequence. 
  *               slice(2,-1) extracts the third element through the second to last element in the 
  *               sequence.
  *             * If end is omitted, slice extracts to the end of the sequence.
  */
if (!Array.slice) { // mozilla already supports this
  Array.slice = (function(slice) {
    return function(object) {
      return slice.apply(object, slice.call(arguments, 1));
    };
  })(Array.prototype.slice);
}


/*
 * http://www.coryhudson.com/lab/javascript/cross-browser/array.extras.js
 */
 
(function () {
  var methods = {
    indexOf: function (obj, fromIndex) {
      var len = this.length, 
          from= (Number(fromIndex) || 0) < 0 ? Math.ceil(from) : Math.floor(from);
          
      if (from < 0) {
        from += len;
      }
      for (var i = from; i < len; i++) {
        if (this[i] === obj)
          return i;
      }
      return -1;
    },
    
    lastIndexOf: function (obj, fromIndex) {
      var len = this.length, 
          from = isNaN(fromIndex) ? len -1 : ( fromIndex < 0 
               ? Math.ceil(fromIndex) : Math.floor(fromIndex) );
               
      if ( from < 0 ) {
        from += len;
      }
      if (from >= len ) {
        from = len - 1;
      }
      for ( var i = from; i >= 0; i-- ) {
        if ( this[i] === obj ) {
          return i;
        }
      }
      return -1;
    },
    
    forEach: function (f, thisArg) {
      //var l = this.length;	// must be fixed during loop... see docs
      
      //thisArg: If thisArg is null or undefined, this will be the global object. 
      // Otherwise, this will be equal to Object(thisArg) (which is thisArg if 
      // thisArg is already an object, or a String, Boolean, or Number if thisArg 
      // is a primitive value of the corresponding type).
      if (typeof f != "function")
        throw new TypeError();
      
      for (var i = 0, l = this.length; i < l; i++) {
        f.call(thisArg, this[i], i, this);
      }
    },
    
    filter: function (f, thisArg) {
      //var l = this.length;	// must be fixed during loop... see docs
      if (typeof f != "function")
        throw new TypeError();
        
      var res = [], val;
      for (var i = 0, l = this.length; i < l; i++) {
        val = this[i]; // in case fun mutates this
        if (f.call(thisArg, val, i, this)) {
          res.push(this[i]);
        }
      }
      return res;
    },
    
    map: function (f, thisArg) {
      //var l = this.length;	// must be fixed during loop... see docs
      if (typeof f != "function")
        throw new TypeError();
        
      var res = [];
      for (var i = 0, l = this.length; i < l; i++) {
        res.push(f.call(thisArg, this[i], i, this));
      }
      return res;
    },
    
    some: function (f, obj) {
      //var l = this.length;	// must be fixed during loop... see docs
      if (typeof f != "function")
        throw new TypeError();
        
      for (var i = 0, l = this.length; i < l; i++) {
        if (f.call(obj, this[i], i, this)) {
          return true;
        }
      }
      return false;
    },
    
    every: function (f, obj) {
      //var l = this.length;	// must be fixed during loop... see docs
      if (typeof f != "function")
        throw new TypeError();
        
      for (var i = 0, l = this.length; i < l; i++) {
        if (!f.call(obj, this[i], i, this)) {
          return false;
        }
      }
      return true;
    },
    
    // Introduced in JavaScript 1.8
    reduce: function(fun /*, initial*/) {
      var len = this.length, i = 0, rv;
      
      if (typeof fun != "function")
        throw new TypeError();

      // no value to return if no initial value and an empty array
      if (len == 0 && arguments.length == 1)
        throw new TypeError();

      if (arguments.length >= 2) {
        rv = arguments[1];
      }
      else {
        do {
          if (i in this) {
            rv = this[i++];
            break;
          }

          // if array contains no values, no initial value to return
          if (++i >= len)
            throw new TypeError();
            
        } while (true);
      }
      for (; i < len; i++) {
        if (i in this)
          rv = fun.call(null, rv, this[i], i, this);
      }
      return rv;
    },
    
    // Introduced in JavaScript 1.8
    reduceRight: function(fun /*, initial*/) {
      var len = this.length, i = len - 1, rv;
      
      if (typeof fun != "function")
        throw new TypeError();

      // no value to return if no initial value, empty array
      if (len == 0 && arguments.length == 1)
        throw new TypeError();

      if (arguments.length >= 2) {
        rv = arguments[1];
      }
      else {
        do {
          if (i in this) {
            rv = this[i--];
            break;
          }

          // if array contains no values, no initial value to return
          if (--i < 0)
            throw new TypeError();
            
        } while (true);
      }
      for (; i >= 0; i--) {
        if (i in this)
          rv = fun.call(null, rv, this[i], i, this);
      }
      return rv;
    }
  };

  for (var i in methods) {
    if (!Array.prototype[i]) Array.prototype[i] = methods[i];
  }
})();

// Mozilla's Array Generics
['concat',      'every', 'filter', 'forEach',     'indexOf', 'join',  
 'lastIndexOf', 'map',   'reduce', 'reduceRight', 'some',    'slice'].forEach(function(func) {
    if (!Array[func]) Array[func] = function (object) {
      return this.prototype[func].apply(object, Array.prototype.slice.call(arguments, 1));
	}
});





// @TODO: 

/*
append to end of array, optionally checking for duplicates
if(!Array.prototype.append) {
Array.prototype.append=function(obj,nodup){
  if (!(nodup && this.contains(obj))){
    this[this.length]=obj;
  }
}
}
*/

/*
return index of element in the array
if(!Array.prototype.indexOf){
Array.prototype.indexOf=function(obj){
  var result=-1;
  for (var i=0;i<this.length;i++){
    if (this[i]==obj){
      result=i;
      break;
    }
  }
  return result;
}
}
*/

/*
return true if element is in the array
if(!Array.prototype.contains){
Array.prototype.contains=function(obj){
  return (this.indexOf(obj)>=0);
}
}
*/

/*
empty the array
if(!Array.prototype.clear){
Array.prototype.clear=function(){
  this.length=0;
}
}
*/

/*
insert element at given position in the array, bumping all
subsequent members up one index
if(!Array.prototype.insertAt){
Array.prototype.insertAt=function(index,obj){
  this.splice(index,0,obj);
}
}
*/

/*
remove element at given index
if(!Array.prototype.removeAt){
Array.prototype.removeAt=function(index){
  this.splice(index,1);
}
}
*/

/*
return index of element in the array
if(!Array.prototype.remove){
Array.prototype.remove=function(obj){
  var index=this.indexOf(obj);
  if (index>=0){
    this.removeAt(index);
  }
}
}
*/
