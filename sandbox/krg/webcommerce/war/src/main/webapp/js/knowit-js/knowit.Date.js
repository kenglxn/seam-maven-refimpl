/* 
 * File        : knowit.Date.js
 * Version     : 1.0
 * Author      : Leif Olsen, leif.olsen@knowit.no
 * Copyright   : (c) 2003-2008
 *
 * Description : Extensions to standard javascript Date object
 * Notes       : dateFormat, see: http://blog.stevenlevithan.com/archives/date-time-format
 *               prettyDate, see: http://ejohn.org/projects/javascript-pretty-date/
 * Dependencies: knowit.String.js
 * Created     : 20081021 - Created from Date Format version 1.2.2
 * History     : 20081117 - Added dateUtil.parse and Date.parseEx
 *               20081125 - Renamed from dateUtil to dateUtils
 */
window.status = 'Loading [knowit.Date.js]';


/**
 * Date Format 1.2.2
 * (c) 2007-2008 Steven Levithan <stevenlevithan.com>
 * MIT license
 * Includes enhancements by Scott Trenda <scott.trenda.net> and Kris Kowal <cixar.com/~kris.kowal/>
 *
 * A simple way to format dates and times according to a user-specified mask.
 * Accepts a date, a mask, or a date and a mask.
 * Returns a formatted version of the given date.
 * The date defaults to the current date/time.
 * The mask defaults to dateUtil.masks.default.
 *
 * For documentation and usage see:  http://blog.stevenlevithan.com/archives/date-time-format
 *
 */
var dateUtils =  ( function () {
  var VERSION = '1.3.0',
      token = /d{1,4}|m{1,4}|yy(?:yy)?|([HhMsTt])\1?|[LloSZ]|"[^"]*"|'[^']*'/g,
      timezone = /\b(?:[PMCEA][SDP]T|(?:Pacific|Mountain|Central|Eastern|Atlantic) (?:Standard|Daylight|Prevailing) Time|(?:GMT|UTC)(?:[-+]\d{4})?)\b/g,
      timezoneClip = /[^-+\dA-Z]/g,
      
      pad = function (val, len) {
        val = String(val);
        len = len || 2;
        while (val.length < len) val = "0" + val;
        return val;
      };
      
  // Regexes and supporting functions are cached through closure
  return ( {
    format: function ( date, mask, utc ) {
      var dU = dateUtils;

      // You can't provide utc if you skip other args (use the "UTC:" mask prefix)
      if (arguments.length == 1 && (typeof date == "string" || date instanceof String) && !/\d/.test(date)) {
        mask = date;
        date = undefined;
      }

      // Passing date through Date applies Date.parse, if necessary
      date = date ? new Date(date) : new Date();
      if (isNaN(date)) throw new SyntaxError( 'invalid date' + this.toString() + ", method format(" + date + ", " + mask + ", " + utc + ")" );

      mask = String(dU.masks[mask] || mask || dU.masks["default"]);

      // Allow setting the utc argument via the mask
      if (mask.slice(0, 4) == "UTC:") {
        mask = mask.slice(4);
        utc = true;
      }

      var	_ = utc ? "getUTC" : "get",
        d = date[_ + "Date"](),
        D = date[_ + "Day"](),
        m = date[_ + "Month"](),
        y = date[_ + "FullYear"](),
        H = date[_ + "Hours"](),
        M = date[_ + "Minutes"](),
        s = date[_ + "Seconds"](),
        L = date[_ + "Milliseconds"](),
        o = utc ? 0 : date.getTimezoneOffset(),
        flags = {
          d:    d,
          dd:   pad(d),
          ddd:  dU.i18n.dayNames[D],
          dddd: dU.i18n.dayNames[D + 7],
          m:    m + 1,
          mm:   pad(m + 1),
          mmm:  dU.i18n.monthNames[m],
          mmmm: dU.i18n.monthNames[m + 12],
          yy:   String(y).slice(2),
          yyyy: y,
          h:    H % 12 || 12,
          hh:   pad(H % 12 || 12),
          H:    H,
          HH:   pad(H),
          M:    M,
          MM:   pad(M),
          s:    s,
          ss:   pad(s),
          l:    pad(L, 3),
          L:    pad(L > 99 ? Math.round(L / 10) : L),
          t:    H < 12 ? "a"  : "p",
          tt:   H < 12 ? "am" : "pm",
          T:    H < 12 ? "A"  : "P",
          TT:   H < 12 ? "AM" : "PM",
          Z:    utc ? "UTC" : (String(date).match(timezone) || [""]).pop().replace(timezoneClip, ""),
          o:    (o > 0 ? "-" : "+") + pad(Math.floor(Math.abs(o) / 60) * 100 + Math.abs(o) % 60, 4),
          S:    ["th", "st", "nd", "rd"][d % 10 > 3 ? 0 : (d % 100 - d % 10 != 10) * d % 10]
        };

      return mask.replace(token, function ($0) {
        return $0 in flags ? flags[$0] : $0.slice(1, $0.length - 1);
      });
    },
    
    
    parse: function( dateString, mask, utc ) {
      dateString = (dateString || '').trim();
    
      if( !dateString ) { 
        return new Date();
      }
      
      var result,
          dateStringIndex = 0, 
          lenDateString = dateString.length;
          
      takeDigitsFromDateString = function( howMany, signed ) {
        var digits = '', 
            i      = dateStringIndex, 
            actual = 0,
            re     = signed ? /\+|-|[0-9]/ : /[0-9]/;
        
        // Find first digit in string
        while( i < lenDateString && !dateString.charAt(i).match( re ) ) i++;
        dateStringIndex = i;
        
        if( dateStringIndex < lenDateString ) {
          // Extract sign
          if( dateString.charAt(i).match( /\+|\-/) ) i++;
          
          // Extract up to 'howMany' digits
          while( i < lenDateString && dateString.charAt(i).match( re ) ) {
            i++;
            if ( ++actual == howMany ) break;
          }
          digits = dateString.substring( dateStringIndex, i );
          dateStringIndex = i;
        }
        return digits;
      };
      
      calculateOffsetInMilliseconds = function() {
        var result, h, m;
        h = takeDigitsFromDateString( 2, true );
        if( h ) {
          m =  takeDigitsFromDateString( 2 );
          result = 60000 * ( parseInt( h ) * 60 + ( m ? parseInt( m ) : 0 ) );
        }
        return result;
      };
      
      
      // begin parse
      var dU = dateUtils, yr=0, mo=0, da=0, hr=0, mn=0, sc=0, ms=0, offset, m, d;
      mask = String(dU.masks[mask] || mask || dU.masks["default"]);
      
      // Allow setting the utc argument via the mask
      if (mask.slice(0, 4) == "UTC:") {
        mask = mask.slice(4);
        utc = true;
      }

      while ( m = token.exec( mask ) ) {
        switch (m[0]) {
          case 'yyyy':
            d = takeDigitsFromDateString( 4 );
            if( d ) yr = parseInt( d );
            break;
            
          case   'yy': 
            d = takeDigitsFromDateString( 2 );
            if( d ) { 
              yr = parseInt( d );
              yr += yr < 50 ? 2000 : 1900;
            }
            break;
          
          case   'mm':
          case    'm':
            d = takeDigitsFromDateString( 2 );
            if( d ) mo = parseInt( d );
            break;
          
          case   'dd':
          case    'd':
            d = takeDigitsFromDateString( 2 );
            if( d ) da = parseInt( d );
            break;
          
          case   'hh':
          case    'h':
            d = takeDigitsFromDateString( 2 );
            if( d ) {
              hr = parseInt( d );
              if( mask.match( /tt|t/i ) && dateString.match( /pm|p/i ) && hr < 12 ) hr += 12;
            }
            break;
          
          case   'HH':
          case    'H':
            d = takeDigitsFromDateString( 2 );
            if( d ) {
              hr = parseInt( d );
            }
            break;
          
          case   'MM':
          case    'M':
            d = takeDigitsFromDateString( 2 );
            if( d ) mn = parseInt( d );
            break;

          case   'ss':
          case    's': 
            d = takeDigitsFromDateString( 2 );
            if( d ) sc = parseInt( d );
            break;
            
          case    'l':
            d = takeDigitsFromDateString( 3 );
            if( d ) ms = parseInt( d );
            break;
          case    'L':
            d = takeDigitsFromDateString( 2 );
            if( d ) ms = parseInt( d ) * 10;
            break;
          
          case    'o':
          case    'Z':
            // next digits are time zone offset
            offset = calculateOffsetInMilliseconds();
            break;
          
          case 'mmmm':
          case  'mmm': 
            throw new SyntaxError( 'Invalid mask. Can not parse month names ("mmm" or "mmmm"). ' + 
              this.toString() + ", method parse(" + dateString + ", " + mask + ", " + utc + ")" );
            break;
          
          case 'dddd':
          case  'ddd': 
            throw new SyntaxError( 'Invalid mask. Can not parse day names ("ddd" or "dddd"). ' + 
              this.toString() + ", method parse(" + dateString + ", " + mask + ", " + utc + ")" );
            break;
            
          default:
            //alert('Unknown token: ' + m[0]);
            
        } // ~switch
      } //~while
      
      if( offset == undefined && dateString.match( /GMT|UTC/i ) ) {
        offset = calculateOffsetInMilliseconds();
        
        // offset == undefined || offset == 0 -> Date is UTC
        
      }
      
      if( yr == 0 || mo == 0 || da == 0 ) {
        // Use current date
        result = new Date();
        result.setHours( hr );
        result.setMinutes( mn );
        result.setSeconds( sc );
        result.setMilliseconds ( ms );
      }
      else if ( yr > 0 && mo > 0 && da > 0 ) {
        if( utc ) {
          result = new Date( Date.UTC( yr, mo-1, da, hr, mn, sc, ms ) );
        }
        else {
          result = new Date( yr, mo-1, da, hr, mn, sc, ms );
        }
      }
      else {
        throw new SyntaxError( 'Invalid date string. ' + 
          this.toString() + ", method parse(" + dateString + ", " + mask + ", " + utc + ")" );
      }
      
      return result;
    },
    
    toString: function() {
      return 'dateUtils version: ' + VERSION;
    }
    
  } ); //~return
} ) (); //~anonymous function immediately invoked with ()

// Some common format strings
dateUtils.masks = {
	"default":      "ddd mmm dd yyyy HH:MM:ss",
	shortDate:      "m/d/yy",
	mediumDate:     "mmm d, yyyy",
	longDate:       "mmmm d, yyyy",
	fullDate:       "dddd, mmmm d, yyyy",
	shortTime:      "h:MM TT",
	mediumTime:     "h:MM:ss TT",
	longTime:       "h:MM:ss TT Z",
	isoDate:        "yyyy-mm-dd",
	isoTime:        "HH:MM:ss",
	isoDateTime:    "yyyy-mm-dd'T'HH:MM:ss",
	isoUtcDateTime: "UTC:yyyy-mm-dd'T'HH:MM:ss'Z'"
};

// Internationalization strings
dateUtils.i18n = {
	dayNames: [
		"Sun", "Mon", "Tue", "Wed", "Thu", "Fri", "Sat",
		"Sunday", "Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday"
	],
	monthNames: [
		"Jan", "Feb", "Mar", "Apr", "May", "Jun", "Jul", "Aug", "Sep", "Oct", "Nov", "Dec",
		"January", "February", "March", "April", "May", "June", "July", "August", "September", "October", "November", "December"
	],
  prettyNames: [
    "just now", "1 minute ago", " minutes ago", "1 hour ago", " hours ago",  "Yesterday", " days ago", "1 week ago", " weeks ago"
  ]
};
//
// Externalize for spesific language and include after this file, eg:
// knowit.Date-no.js (remember to save file with UTF-8):
//
//dateUtils.i18n = {
//	dayNames: [
//		"Søn", "Man", "Tir", "Ons", "Tor", "Fre", "Lør",
//		"Søndag", "Mandag", "Tirsdag", "Onsdag", "Torsdag", "Fredag", "Lørdag"
//	],
//	monthNames: [
//		"Jan", "Feb", "Mar", "Apr", "Mai", "Jun", "Jul", "Aug", "Sep", "Okt", "Nov", "Des",
//		"Januar", "Februar", "Mars", "April", "Mai", "Juni", "Juli", "August", "September", "Oktober", "November", "Desember"
//	],
// prettyNames: [
//    "mindre enn ett minutt siden", "ett minutt siden", " minutter siden", "1 time siden", " timer siden",  "i går", " dager siden", "1 uke siden", " uker siden"
//  ]
//

// For convenience...
if(!Date.prototype.format) {
Date.prototype.format = function (mask, utc) {
	return dateUtils.format(this, mask, utc);
};
}

if(!Date.parseEx) {
Date.parseEx = function (dateString, mask, utc) {
  return dateUtils.parse(dateString, mask, utc);
};
}

window.status = '';
