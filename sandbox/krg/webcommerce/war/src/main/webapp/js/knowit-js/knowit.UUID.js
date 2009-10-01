/*
 * File        : knowit.UUID.js
 * Version     : 1.4
 * Author      : robert@broofa.com / leif.olsen@knowit.no
 * Copyright   : see below
 * Description : Random UUID generator
 * Notes       : Change History:
 *               v1.0 - first release
 *                 v1.1 - less code and 2x performance boost (by minimizing calls to Math.random())
 *                 v1.2 - Add support for generating non-standard uuids of arbitrary length
 *                 v1.3 - Fixed IE7 bug (can't use []'s to access string chars.  Thanks, Brian R.)
 *                 v1.4 - Added support for generating UUIDs in a spesified character range (leif.olsen@knowit.no).
 *               
 *               Latest version:   http://www.broofa.com/Tools/randomUUID.js
 *               Information:      http://www.broofa.com/blog/?p=151
 *               Contact:          robert@broofa.com
 *               ----
 *               Copyright (c) 2008, Robert Kieffer
 *               All rights reserved.
 *               
 *               Redistribution and use in source and binary forms, with or without modification, are 
 *               permitted provided that the following conditions are met:
 *                 * Redistributions of source code must retain the above copyright notice, this list 
 *                   of conditions and the following disclaimer.
 *                 * Redistributions in binary form must reproduce the above copyright notice, this list 
 *                   of conditions and the following disclaimer in the documentation and/or other 
 *                   materials provided with the distribution.
 *                 * Neither the name of Robert Kieffer nor the names of its contributors may be used to 
 *                   endorse or promote products derived from this software without specific prior 
 *                   written permission.
 *               
 *               THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND ANY 
 *               EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES 
 *               OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT 
 *               SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, 
 *               INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, 
 *               PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS 
 *               INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT 
 *               LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE 
 *               OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 *
 * Limitations : 
 * Dependencies: knowit.String
 * Created     : 20081017
 * History     : 
 */
 
window.status = 'Loading [knowit.UUID.js]';

// Define the namespace 
window.knowit = window.knowit || {};

if( !knowit.UUID ) {
knowit.UUID = ( function() {

  // Private attributes and methods
  var VERSION = '1.4',
      c = '0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz'.split(''); 

return ({ //Public attributes and methods

  /**
   * Generate a random uuid.  If 'len' is unspecified or zero then an RFC 4122
   * (version 4) UUID is created, otherwise the the uuid will consist of 'len'
   * characters, each of which is randomly choosen from the set of [0-9A-Za-z].
   *
   * For example:
   *   >>> randomUUID()
   *   "92329D39-6F5C-4520-ABFC-AAB64544E172"
   *   >>> randomUUID(10)
   *   "KI4Ed4ctHq"
   *   >>> randomUUID(10, 26)
   *   "NJYLJQWUOJ"
   *   >>> randomUUID(10, 36)
   *   "NH1T24VC46"
   *
   * @param len {int} the length of the generated UUID
   * @param type {int} type of UUID, default = 62, must be one of the following values:
   *             2 generates a random uuid from the set of [0-1]
   *            10 generates a random uuid from the set of [0-9]
   *            16 generates a random uuid from the set of [0-9A-F]
   *            26 generates a random uuid from the set of [A-Z]
   *            36 generates a random uuid from the set of [0-9A-Z]
   *            52 generates a random uuid from the set of [A-Za-z]
   *            62 generates a random uuid from the set of [0-9A-Za-z]
   *        The type parameter is only meaningful in combination with the len parameter.
   * @return {String} a random UID
   */
  getRandomUUID: function ( len, type ) {
    var uuid = [], i; 
    if ( len ) {
      // Compact uuid form
      type = type || 62;
      var range  = type ==  2 || type == 10 || type == 16 || type == 26 || type == 36 || type == 52 ? type : 62,
          offset = type == 26 || type == 52 ? 10 : 0;
          
      for ( i = 0; i < len; i++ ) 
        uuid[i] = c[ 0 | Math.random() * range + offset ];
    } 
    else {
      var ri = 0, r;

      uuid[ 8] = uuid[13] = uuid[18] = uuid[23] = '-';
      uuid[14] = '4';

      for ( i = 0; i < 36; i++ ) {
        if (uuid[i]) continue;
        r = 0 | Math.random()*16;
        // i==19: set the high bits of clock sequence as per rfc4122, sec. 4.1.5
        uuid[i] = c[(i == 19) ? (r & 0x3) | 0x8 : r & 0xf];
      }
    }
    return uuid.join('');
  },
 
  toString: function() { 
    return ( 'knowit.UUID, version ' + VERSION ); 
  }
  
}); //~return

} ) (); //~anonymous function immediately invoked with ()
};

window.status = '';
