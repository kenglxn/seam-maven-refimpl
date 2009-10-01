/*
 * File        : knowit.RomanConverter.js
 * Version     : 1.0
 * Author      : Steven Levithan <stevenlevithan.com> & al
 * Copyright   : 
 * Description : Convert to and from Roman numerals.
 * Notes       : see: http://blog.stevenlevithan.com/archives/javascript-roman-numeral-converter
 * Limitations : 
 * Dependencies: 
 * Created     : 20081020
 * History     :  
 */
 
window.status = 'Loading [knowit.RomanConverter.js]';

// Define the knowit namespace 
if(knowit == undefined) {
  var knowit = {};
}

if( !knowit.RomanConverter ) {
knowit.RomanConverter = ( function() {

  // Private attributes and methods
  var VERSION = '1.0';

return ({ //Public attributes and methods

  /**
   * Convert a Arabic numeral to a Roman numreal.
   * @param num {Integer} the (Arabic) numeral to convert
   * @return {String} the Roman numreal in UPPERCASE, or '' if the number could not be converted.
   */
  toRoman: function( num ) {
    var lookup = {M:1000,CM:900,D:500,CD:400,C:100,XC:90,L:50,XL:40,X:10,IX:9,V:5,IV:4,I:1},
        roman  = '',
        i;

    if (!+num) {
      return '';
    }
        
    for ( i in lookup ) {
      while ( num >= lookup[i] ) {
        roman += i;
        num -= lookup[i];
      }
    }
    return roman;
  
  },
  
  /**
   * Convert a Roman numeral to an Arabic numeral. 
   * @param roman {String} a roman numeral
   * @return {Number} the converted number or NaN
   */
  fromRoman: function( roman ) {
  
    if( !/^M*(?:D?C{0,3}|C[MD])(?:L?X{0,3}|X[CL])(?:V?I{0,3}|I[XV])$/i.test(roman) ) {
      return NaN;
    }

    var roman = roman.toUpperCase().split(''),
        lookup= {I:1,V:5,X:10,L:50,C:100,D:500,M:1000},
        num   = 0, 
        val   = 0;

    while (roman.length) {
      val = lookup[roman.shift()];
      num += val * (val < lookup[roman[0]] ? -1 : 1);
    }
    return num;  
  },
  
  toString: function() { 
    return ( 'knowit.RomanConverter, version ' + VERSION ); 
  }
  
}); //~return

} ) (); //~anonymous function immediately invoked with ()
};

window.status = '';
