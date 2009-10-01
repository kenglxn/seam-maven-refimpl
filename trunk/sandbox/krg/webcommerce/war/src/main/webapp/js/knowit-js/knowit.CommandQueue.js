/*
 * File        : knowit.CommandQueue.js
 * Version     : @see: knowit.CommandQueue._VERSION 
 * Author      : leif.olsen@knowit.no
 * Copyright   : (c) 2008 knowit.no
 * Description : see the constructor
 * Notes       : 
 * Limitations : Parallel Queue is not working yet
 * Dependencies: knowit.js, knowit.Array.js, knowit.NotificationManager.js
 * Created     : 20081027
 * History     : 20090113 - Added delegate for command.func
 *                        - Documentation.
 *               20090115 - Added observers 
 */

window.status = 'Loading [knowit.CommandQueue.js]';

// Define the knowit namespace 
window.knowit = window.knowit || {};

if( !knowit.CommandQueue ) {

/**
 * A Command Queue encapsulate the invocation of methods and it provides the ability to
 * parameterize and pass around method calls, which can then be executed whenever you need
 * it to be. It also allows you to decouple the object invoking the action from the object 
 * implementing it, thus providing a huge degree of flexibility in swapping out concrete 
 * classes. The methods are invoked anynchronusly via the window.setTimeout function - this 
 * ensures that the user can interact with other parts of the system without waiting for
 * the command method to finish. The command queue can be instantiated as a sequential queue 
 * or as a parallel queue. 
 *
 * Sequential queue:
 *   Allows you to chain commands. The commands in the queue will execute in sequence so 
 *   that only one command in the queue executes at a time (the next command in the queue 
 *   waits for the pending command to finish). 
 *
 *   Special considerations need to be taken if the command itself is asynchrounus 
 *   (runs in a timer or executes Ajax calls):
 *     The command method (command.func) must define handlers for one or more of the 
 *     following callbacks:
 *       onCmdStart()          - Fire when the timer starts execution of the command
 *       onCmdProgress(result) - Fire when the command want to inform of an intermediate result
 *       onCmdComplete(result) - Fire when the command completes successfully. This callback 
 *                               handler is mandatory when the command is asynchrounus b.c. 
 *                               it signals to the queue that the next command in the queue is 
 *                               allowed to execute.
 *       onCmdFailure(error)   - Fire if the command fails
 *       
 *     One of the command parameters must be an hash object containing the following structure:
 *     { 
 *       cmdStart:    {Boolean}, // The command method has its own onCmdStart,    default false
 *       cmdProgress: {Boolean}, // The command method has its own onCmdProgress, default false
 *       cmdComplete: {Boolean}, // The command method has its own onCmdComplete, default false.
 *                               // This property MUST be set to "true" when the command is 
 *                               // asynchrounus
 *       cmdFailure:  {Boolean}  // The command method has its own onCmdFailure,  default false
 *     }
 *     The hash objects properties must be set to "true" for each of the commands callback 
 *     handlers, e.g. if the command has a onCmdComplete handler then cmdComplete must be
 *     set to "true".
 *
 *   The queue will attach callbacks to the command method corresponding to the structure.
 *   See usage examples for more.
 *
 * Parallel queue:
 *   A parallel queue executes the commands as soon as possible an dwill not wait for any of
 *   the pending commands to finish. To avoid race conditions it is important that no more than 
 *   one instance of a particular command resides in the queue a the same time. If you need to 
 *   execute the same command in parallel then create a class for the command and make different 
 *   instances of it. If the instances need interoperability use some kind of event mechanism 
 *   (e.g. knowit.Observer or Tibcos PageBus) to exchange data between the instances.
 *   Add callbacks as described in the paragraph above if you want to monitor the execution
 *   of the commands.
 *   See usage examples for more.
 *
 * The biggest deterrent for running CPU intensive computations in a web browser is the fact 
 * that the entire browser user interface is frozen while a JavaScript thread is running. 
 * This means that under no circumstance should a script ever take more than 300 msec (at most) 
 * to complete. Breaking this rule inevitably leads to bad user experience. Since all JavaScript 
 * in a browser executes on a single thread asynchronous methods will only execute when there 
 * is an "opening" in the thread. When the "opening" is found the  method blocks the thread 
 * while executing. You need to take this into account when you construct your command functions. 
 *   For more information about this topic see:
 *     http://www.julienlecomte.net/blog/2007/10/28/
 *     http://www.nczonline.net/blog/2009/01/05/what-determines-that-a-script-is-long-running/
 *     http://www.nczonline.net/blog/2009/01/13/speed-up-your-javascript-part-1/ 
 *
 * @class CommandQueue
 * @constructor
 * @param options {Object} as a hash with the following structure:
 *   autorun {Boolean} default false
 *   parallel {Boolean} default false
 */
knowit.CommandQueue = function( options ) {
  options = options || {};
  this.running  = options.autorun  || false;
  this.parallel = options.parallel || false;
  return this;
};

knowit.CommandQueue.prototype = {

  /**
   * The Command queue
   * @property q
   * @type {Array}
   * @protected
   */
  q: [],
  
  
  /**
   * Indicates wether a command is currently in progress.
   * If the command queue is sequential then the next command in the 
   * queue must wait until the pending command finishes.
   * @property pending
   * @type {Object}
   * @protected
   */
  pending: null,
  
  
  /**
   * Indicates wether a command queue is running or not.
   * @property running
   * @type {Boolean}
   * @protected
   */
  running: false,

  /**
   * Indicates wether a command queue is parallel or not.
   * @property parallel
   * @type {Boolean}
   * @protected
   */
  parallel: false,

  /**
   * The notification manager will fire the folloing notifications:
   *   ('onCreate', command) 
   *     Fires when a command is created (before the given command.delay)
   *   ('onStart', command)
   *     Fires when a command starts (after the given command.delay)
   *   ('onProgress', command, intermediateResult)
   *     Fires when a command.func want to inform of an intermediate result
   *   ('onComplete', command, result)
   *     Fires when a command.func completes successfully
   *   ('onFailure', command, failure)
   *     Fires if a command.func fails
   * @property notificationManager
   * @type {NotificationManager}
   * @protected
   */
  notificationManager: new knowit.NotificationManager(
    'onCreate', 'onStart', 'onProgress', 'onComplete', 'onFailure' ),

  /**
   * Adds a command to this Command Queue
   * 
   * @method add
   * @param command {Object} as a hash with the following structure:
   *   func {Function} 
   *     the command to execute
   *
   *   thisArg {Object} 
   *     determines the value of "this" when command.func executes. 
   *     If thisArg is null or undefined, this will be the global object. Otherwise, 
   *     "this" will be equal to Object(thisArg) (which is thisArg if thisArg is already 
   *     an object, or a String, Boolean, or Number if thisArg is a primitive value of 
   *     the corresponding type). Therefore, it is always true that typeof this == "object" 
   *     when the function executes.
   *
   *   delay {Number} 
   *     milliseconds delay before executing the command.func function, default 0ms.
   *
   *   args {Array} 
   *     specifying the arguments with which command.func should be called.
   *     Alternatively the arguments can be passed via the add methods arguments parameter.
   *      If the command.func manages one or more of the callbacks itself then one of the 
   *      args array elements must be an hash object with the following structure:
   *      { 
   *        cmdStart:    {Boolean}, // The command method has its own onCmdStart,    default false
   *        cmdProgress: {Boolean}, // The command method has its own onCmdProgress, default false
   *        cmdComplete: {Boolean}, // The command method has its own onCmdComplete, default false
   *        cmdFailure:  {Boolean}  // The command method has its own onCmdFailure,  default false
   *      }
   *
   *     The queue will attach callbacks to the command method corresponding to the structure.
   *     NOTE: You can not spesify both command.args and function arguments, 
   *           function arguments takes precedence over command.args.
   *
   * @param arguments {Array} 
   *  the built in arguments array where arguments[0] is the "command" parameter. The rest of the 
   *  arguments array (arguments[1]...arguments[n]) is reserved for the parameters to apply to 
   *  the command.func. Alternatively the arguments can be passed via the command.args 
   *  array. 
   *  If the command.func manages one or more of the callbacks itself then one of the arguments 
   *  must be an hash object with the following structure:
   *  { 
   *    cmdStart:    {Boolean}, // The command method has its own onCmdStart,    default false
   *    cmdProgress: {Boolean}, // The command method has its own onCmdProgress, default false
   *    cmdComplete: {Boolean}, // The command method has its own onCmdComplete, default false
   *    cmdFailure:  {Boolean}  // The command method has its own onCmdFailure,  default false
   *  }
   *  The queue will attach callbacks to the command method corresponding to the structure.
   *  NOTE: You can not spesify both command.args and arguments[1]...arguments[n], 
   *        arguments[1]...arguments[n] takes precedence over command.args.
   *
   * @return {CommandQueue} the Command Queue instance
   * @throws knowit.CommandQueue.InvalidType if the command.func parameter is not a function
   */
  add: function( command /* arguments[1],...,arguments[n] */ ) {
    
    if( command ) {
      var command = command || {}, me = this;

      if (typeof command.func != "function") {
        throw new TypeError( knowit.CommandQueue.InvalidType );
      }
      
      // the ID of the timeout, which can be used with window.clearTimeout.
      command.timeoutId = 0; 
      command.delay = command.delay || 0;

      // Callbacks for command.func
      // NOTE: In a callback the "this" object is unpredictable. If command.func 
      //       manages it's own callbacks then the "this" object points to 
      //       command.thisArg, otherwise the "this" object points to command.
      //       But, we're in a closure, so we can reference the command parameter 
      //       (or alternatively the CommandQueue.pending property). 
      
      // this callback fires when a command.func starts
      // and notifies subscribers about the event
      command.onCmdStart = function() {
        me.notificationManager.fire( 'onStart', command );
      };
      
      // this callback fires when a command.func want to inform of an intermediate result
      // and notifies subscribers about the event
      command.onCmdProgress = function( result ) {
        me.notificationManager.fire( 'onProgress', command, result );
      };
      
      // this callback fires when a command.func completes successfully
      // and notifies subscribers about the event
      command.onCmdComplete = function( result ) {
        window.clearTimeout( me.pending.timeoutId );
        me.pending.timeoutId = 0;
        
        me.notificationManager.fire( 'onComplete', command, result );
        
        me.pending = null;
        me._nextCommandInQueue();
      };
      
      // this callback fires if a command.func fails
      // and notifies subscribers about the event
      command.onCmdFailure = function( error ) {
        me.notificationManager.fire('onFailure', command, error );
      };
      
      // Delegate for command.func. The delegate will fire the 
      // command.onCmdStart, command.onCmdComplete and command.onCmdFailure
      // callbacks if the callbacks are not managed by the command.func itself
      // The timer executes the delegate, not the command.func
      command.delegate = function() {
      
        // NOTE: This method is not a callback so we can be shure 
        //       that the "this" object points to the command object
        
        if( !this.cmdStart ) {
          // The command does not have it's own onStart callback
          this.onCmdStart(); 
        }
        
        var result = this.func.apply( this.thisArg, this.args );
        
        if( !this.cmdComplete ) {
          // The command does not have it's own onComplete callback
          this.onCmdComplete( result ); 
        }
      }; //~command.delegate
      
      // Determine arguments for command.func
      command.args = arguments.length > 1 
                   ? Array.slice( arguments, 1 ) 
                   : command.args || []; 
      
      // Find out if command.func manages any callbacks
      var a;
      for ( var i = 0, n = command.args.length; i < n; ++i ) {
        a = command.args[i];
        if( typeof a == 'object' && 
          ( a.cmdStart || a.cmdProgress || a.cmdComplete || a.cmdFailure ) ) {
          
          command.cmdStart = a.cmdStart;
          a.onCmdStart     = command.onCmdStart;
          
          command.cmdProgress = a.cmdProgress;
          a.onCmdProgress = command.onCmdProgress;
          
          command.cmdComplete = a.cmdComplete;
          a.onCmdComplete = command.onCmdComplete;
          
          command.cmdFailure = a.cmdFailure;
          a.onCmdFailure  = command.onCmdFailure;
          
          command.args[i] = a;
          break;
        }
      }
      this.q.push( command );    
    }
    
    this._nextCommandInQueue();
    return this;
  },
  
  /**
   * Executes commands in the Command Queue sequentially
   * @protected
   */
  _nextCommandInQueue: function() {
  
    // If a command is currently in progress, we'll wait our
    // turn. Otherwise, grab the first command off the top of the queue.
    if ( this.running && ( !( this.pending || !( this.pending = this.q.shift() ) ) ) ) {
    
      this.notificationManager.fire( 'onCreate', this.pending );
      
      // NOTE: You can not do this: 
      //   window.setTimeout( this.pending.delegate(), this.pending.delay );
      //   MUST do this:
      var me = this;
      this.pending.timeoutId = window.setTimeout( 
          function() { 
            me.pending.delegate() 
          }, 
          this.pending.delay 
        );
    }
  },
  
  /**
   * Executes commands in the Command Queue sequentially
   * @protected
   */
  _nextCommandInQueueSequential: function() {
  },
  
  
  /**
   * Executes commands in the Command Queue in parallel
   * @protected
   */
  _nextCommandInQueueParallel: function() {
  },
  
  
  /**
   * Pause the execution of the Command Queue after the execution of the current
   * callback(s) completes. A paused Queue can be restarted with q.resume()
   * @method pause
   * @return {CommandQueue} the Command Queue instance
   */
  pause: function() {
    this.running = false;
    return this;
  },
    
  /**
   * Execute the queue callbacks (also resumes paused Queue).
   * @method run
   * @return {CommandQueue} the Command Queue instance
   */
  run: function() {
    if( !this.running ) {
      this.running = true;
      this._nextCommandInQueue();
    }
    return this;
  },

  /**
   * Checks wether queue is running
   * @method isRunning
   * @return {Boolean} true if the queue is running
   */
  isRunning: function() {
    return this.running;
  },
  
  /**
   * Stop and clear the Queue's queue after execution of the current callback completes.
   * @method stop
   * @return {CommandQueue} the Command Queue instance
   */
  stop: function() { 
    this.pause();
    this.q = [];
    return this;
  },
  
  /**
   * Subscribe to interesting moments of the command queue
   * @method subscribe
   * @param notificationName {String} 
   *   the notification to subscribe to, see constructor for details
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
   * @return {CommandQueue} 
   *   the Command Queue instance
   */
  subscribe: function( notificationName, observer, notificationCallback ) {
    this.notificationManager.subscribe( notificationName, observer, notificationCallback );
    return this;
  },
  
  /**
   * Unsubscribe to a command queue notification
   * @method unsubscribe
   * @param notificationName {String} 
   *   the notification to unsubscribe to
   * @param observer {Object} 
   *   the scope of the notificationCallback
   * @return {CommandQueue} 
   *   the Command Queue instance
   */
  unsubscribe: function( notificationName, observer ) {
    this.notificationManager.unsubscribe( notificationName, observer );
    return this;
  },
  
  /**
   * @method toString
   * @return version information 
   */
  toString: function() {
    return 'knowit.CommandQueue, version ' + knowit.CommandQueue._VERSION;
  }
}

//
// Static methods and attributes
//

/**
 * Exception to throw when the command.func parameter is not a function
 * @property InvalidCommand
 * @type {Error}
 * @private
 * @static
 */
knowit.CommandQueue.InvalidType = 
  new Error('Missing a valid command function. "command.func" is not a function.');


/**
 * The version
 * @property _VERSION
 * @type {String}
 * @private
 * @static
 */
knowit.CommandQueue._VERSION = '0.5';

};

window.status = '';
