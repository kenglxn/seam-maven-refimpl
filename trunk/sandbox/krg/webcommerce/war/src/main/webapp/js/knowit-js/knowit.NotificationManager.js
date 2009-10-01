/*
 * File        : knowit.NotificationManager.js
 * Version     : @see: knowit.NotificationManager._VERSION 
 * Author      : leif.olsen@knowit.no
 * Copyright   : (c) 2009
 * Description : see the constructor
 * Notes       : This is a bare bone implementation of the Observer pattern.
 *               Use the Tibco PageBus, (http://www.tibco.com/devnet/pagebus/default.jsp) ,
 *               if you need more advanced stuff like a pub/sub broker.
 * Limitations : 
 * Dependencies: knowit.js, knowit.Array.js, knowit.String
 * Created     : 20090115
 * History     : 
 */
 
window.status = 'Loading [knowit.NotificationManager.js]';
 
// Define the namespace 
window.knowit = window.knowit || {};

if( !knowit.NotificationManager ) {
/** 
 * This is a simplified implementation of the Observer pattern where the notification
 * manager manages a set of notifications. An observer kan register it's interest
 * in a notification by subscribing to the notification of interest.
 * Use the Tibco PageBus, (http://www.tibco.com/devnet/pagebus/default.jsp), 
 * if you need more advanced stuff like a pub/sub broker.
 * @class NotificationManager
 * @constructor
 * @param arguments[0],...,arguments[n] {String} 
 *   The notifications you want to publish. If notification names are given as arguments
 *   to the constructor then the publishing of notifications is "strict", meaning that a
 *   notification an observer want to subscribe to must exist. On the other hand, if the 
 *   constructor argument list is empty then the notification is "loose", meaning that a
 *   notification an observer subscribes to will be defined in the notifications hash if
 *   it not already exists, @see: knowit.NotificationManager.subscribe code.
 * @see http://en.wikipedia.org/wiki/Observer_pattern
 * @see http://peter.michaux.ca/articles/anonymous-notifications-extremely-loose-coupling
 * @see http://www.dustindiaz.com/javascript-observer-class/
 * @see http://www.openajax.org/index.php
 * @see http://www.tibco.com/devnet/pagebus/default.jsp
 */
knowit.NotificationManager = function( /* arguments[1],...,arguments[n] {String} */) {

  /**
   * Indicates whether notification names must exist before you can subscribe to them 
   * @property loose
   * @type {Boolean} 
   * @private
   */
  this.loose = true;
  
  /**
   * Notifications that an subscriber can subscribe to
   * This is an hash object where the notification name is the hash. Each hash keeps 
   * an array with the observers that subscribes to the notification.
   * @property notifications
   * @type {Object} 
   * @private
   */
  this.notifications = {};

  // Strict or Loose depends on arguments array
  for ( var i = 0, n = arguments.length; i < n; i++ ) {
    if( typeof arguments[i] == "string" && arguments[i] ) {
      this.notifications[ arguments[i] ] = [];
      this.loose = false;
    }
  }
  return this;
};
 
 
knowit.NotificationManager.prototype = {

  /**
   * Gives observer objects the ability to subscribe to a notification
   * @method subscribe
   * @param notificationName {String} 
   *   the notification to subscribe to
   * @param observer {Object} 
   *   The observer object registers it's interest in a notification by subscribing to 
   *   a notification. The observer is the scope (or the "this" object) of the 
   *   "notificationCallback" argument. If observer is null or undefined, the "observer" 
   *   will be the global object. Otherwise, "observer" will be equal to Object(observer) 
   *   (which is "observer" if "observer" is already an object, or a String, Boolean, or 
   *   Number if "object" is a primitive value of the corresponding type). Therefore, it is
   *   always true that typeof observer == "object" when the notification callback executes.
   * @param notificationCallback {Function} 
   *   the notification callback to fire when the observer notifies its observer.
   * @param isAsync {Boolean} 
   *   specifies whether callbacks to observers will be done via a setTimeout() or by a 
   *   direct function call. Synchronous notification is useful when subscriber must 
   *   be notified of each distinct change using the observer's current state. 
   *   Asynchronous notifications are useful when only the notification is important 
   *   and the exact state of the observable at the time is not required. 
   * @return {NotificationManager} 
   *   the notification manager instance
   * @throws knowit.NotificationManager.InvalidNotificationName 
   *   if the "notificationName" parameter is not a valid string.
   * @throws knowit.NotificationManager.InvalidNotificationCallback 
   *   if the "notificationCallback" parameter is not a function.
   */
  subscribe: function( notificationName, observer, notificationCallback, isAsynch ) {
    if( !(typeof notificationName == "string" && notificationName) ) {
      throw new TypeError( knowit.NotificationManager.InvalidNotificationName );
    }
    if( typeof notificationCallback != "function" ) {
      throw new TypeError( knowit.NotificationManager.InvalidNotificationCallback );
    }
    if( !this.notifications[notificationName] ) {
      if( this.loose ) {
        this.notifications[notificationName] = [];
      }
      else {
        throw new TypeError( 
          knowit.NotificationManager.NotificationDoesNotExist
          .toString().format( notificationName ) );
      }
    }
    observer = observer || null;
    var exists = this.notifications[notificationName].some( 
      function( subscription ) {
        return ( observer === subscription.observer );
      } 
    );
    if( exists ) {
      this.unsubscribe( notificationName, observer );
    }
    this.notifications[notificationName].push({
      observer    : observer, 
      notification: notificationCallback,
      isAsynch    : isAsynch || false
    });
    return this;
  },
  
  /**
   * Unsubscribe to a notification
   * @method unsubscribe
   * @param notificationName {String} 
   *   the notification to unsubscribe to
   * @param observer {Object} 
   *   the scope of the notificationCallback
   * @return {NotificationManager} 
   *   the notification manager instance
   */
  unsubscribe: function( notificationName, observer ) {
    if ( !notificationName || !this.notifications[notificationName] ) {
      return this;
    }
    observer = observer || null;
    this.notifications[notificationName] = this.notifications[notificationName].filter(
      function( subscription ) {
        return ( observer !== subscription.observer );
      }
    );
    return this;
  },
  
  /**
   * Fire a notification to subscribers.
   * Let a notification callback return a boolean value of false to indicate to the 
   * notification manager that the the rest of the callbacks shold not be fired. Let
   * the callback retur true or undefined (no return value) to indicate that the next
   * callbak for this notification should fire.
   * @method fire
   * @param notificationName {String} 
   *   the notification to fire
   * @param arguments {Array} 
   *   the built in arguments array where arguments[0] is the "notificationName" argument. 
   *   The rest of the arguments array (arguments[1]...arguments[n]) is reserved for the 
   *   arguments to apply to the notification function callback.
   * @return {Boolean}
   *   The return value is true if all callbacks for the given notification has fired.
   *   The return value is false if a callback has returned false.
   */
  fire: function( notificationName /* arguments[1],...,arguments[n] */ ) {
    if ( !this.notifications[notificationName] ) {
      return false;
    }
    var proceed = true, subscription, i, n;
    for ( i = 0, n = this.notifications[notificationName].length; i < n; i++ ) {
      subscription = this.notifications[notificationName][i];
      if( subscription.isAsync ) {
        //var args = Array.prototype.slice.call(arguments, 0); 
        var args = Array.slice(arguments, 0);
        window.setTimeout( 
          function() { 
            subscription.notification.apply( subscription.observer, args ) 
          }, 0 );
      }
      else {
        proceed = subscription.notification.apply( subscription.observer, arguments );
      }
      if ( !(proceed == undefined || proceed == true) ) break;
    }
    return ( i > n); // i < n => the callbacks has not completed
  },

  toString: function() {
    return 'knowit.NotificationManager, version ' + knowit.NotificationManager._VERSION;
  }
};

//
// Static methods and attributes
//

/**
 * Exception to throw when the notificationCallback argument is not a function
 * @property InvalidNotificationCallback
 * @type {Error}
 * @private
 * @static
 */
knowit.NotificationManager.InvalidNotificationCallback = new Error(
  'The "notificationCallback" argument is not a function.');

/**
 * Exception to throw when the notificationName argument is not a valid string
 * @property InvalidNotificationName
 * @type {Error}
 * @private
 * @static
 */
knowit.NotificationManager.InvalidNotificationName = new Error(
  'The "notificationName" argument is not a valid string.');

/**
 * Exception to throw when the notificationName argument does not exist 
 * @property NotificationDoesNotExist
 * @type {Error}
 * @private
 * @static
 */
knowit.NotificationManager.NotificationDoesNotExist = new Error(
  'The notificationName: "{0}" does not exist in this notification manager.');

/**
 * The version
 * @property _VERSION
 * @type {String}
 * @private
 * @static
 */
knowit.NotificationManager._VERSION = '1.0';

};
window.status = '';
