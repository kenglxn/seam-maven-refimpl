/*
 * File        : Lock.js
 * Version     : 
 * Author      : Jakob Heuser (jakob@felocity.org)
 * Copyright   : Copyright (c) 2007 Jakob Heuser (jakob@felocity.org). All rights reserved.
 *                see: http://www.felocity.org/blog/article/javascripts_strange_threaded_nature
 * Description : Lock: A Unified Locking Library
 *              Thanks to the magic of the event stack in Firefox / IE, it is possible to
 *              have your data be changed behind your back when using browser window
 *              events. A basic lock will help stop that. An object is returned to
 *              the requesting application which will say if a lock was obtained or not.
 *             
 *              This class is licensed under the New BSD License:
 *              http://www.opensource.org/licenses/bsd-license.html
 *
 * Notes       : 
 * Limitations : 
 * Dependencies: 
 * Created     : 
 * History     : 20080929 - Modified for knowit by Leif Olsen
 */
 
window.status = 'Loading [Lock.js]';

if (!Lock) {
var Lock = function() {
  var locks = {};
  
  var normalize_namespace = function(name) {
      return ("c" + name).replace(/[^a-z0-9\-\_]/gi, "");
  };

  return {
    declare: function() {
      for (var i = 0; i < arguments.length; i++) {
        if (!locks[normalize_namespace(arguments[i])]) {
          locks[normalize_namespace(arguments[i])] = new Array();
        }
      }
    },
    obtain: function(space) {
      // atomic assignment, no 2 objects are same
      var lock = new Object();
      
      // no namespace? problem
      space = normalize_namespace(space);
      if (!locks[space]) {
        throw "Namespaces must be declared before getting into locks.";
      }

      // atomic op for as long as JS is single threaded
      // whenever JS multi-threads, this one call is synchronized
      locks[space].push(lock);

      // safely clean lock_owner
      if (locks[space][0] === lock) {
        locks[space] = [locks[space][0]];
      }

      var lock_obj = {
        isOwner: function() {
          return (locks[space][0] === lock);
        },
        release: function() {
          if (locks[space][0] === lock) {
              locks[space] = new Array();
          }
        }
      };
      return lock_obj;
    }
  };
}();
};
window.status = '';
