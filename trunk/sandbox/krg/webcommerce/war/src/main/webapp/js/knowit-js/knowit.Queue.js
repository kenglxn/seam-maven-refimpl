/*
 * File        : knowit.Queue.js
 * Version     : 1.0
 * Author      : leif.olsen@knowit.no
 * Copyright   : (c) 2008
 * Description : A Queue is a first-in-first-out (FIFO) data structure.
 *               Inspired by the YUI.queue.js, the dojo.Queue.js and Queue.js from Safalra 
 *               (Stephen Morley), see: http://www.safalra.com/web-design/javascript/
 * Notes       : 
 * Limitations : 
 * Dependencies: 
 * Created     : 20081026
 * History     :     
 */
window.status = 'Loading [knowit.Queue.js]';

// Define the knowit namespace 
if(knowit == undefined) {
  var knowit = {};
}

if( !knowit.Queue ) {
knowit.Queue = function() {

  /**
   * The queue
   * @property q
   * @type {Array}
   * @protected
   */
  this.q = [];
  this.enqueue.apply( this, arguments );
  return this;
};


knowit.Queue.prototype = {

  VERSION: 1.0,

  /**
   * The amount of space at the front of the queue, initialised to zero
   * @property queueSpace
   * @type {number}
   * @protected
   */
  queueSpace = 0,
  
  /**
   *
   */
  add: function( element ) {
    return this.enqueue( element );
  },
  
  /**
   *
   */
  clear: function() {
    this.q = [];
  },

  /**
   * Dequeues an element from this Queue. The oldest element in this Queue is
   * removed and returned. If this Queue is empty then undefined is returned.
   * @method dequeue
   * @return {Object}
   */
  dequeue: function() {

    var element = this.peek();  // == q.shift
    if ( element ) {
    
      // update the amount of space and check whether a shift should occur
      if (++this.queueSpace * 2 >= this.q.length){

        // set the queue equal to the non-empty portion of the queue
        this.q = this.q.slice(this.queueSpace);

        // reset the amount of space at the front of the queue
        this.queueSpace = 0;
      }
    }
    return element;
  },

  /**
   * Enqueues the specified element in this Queue.
   * @method enqueue
   * @param element {Boolean|Number|String|Array|Object|Function} the element to enqueue
   * @return {Integer} the size of the queue
   */
  enqueue: function( element ) {
  
    if (arguments.length > 1)  {
      for (var i = 0, n = arguments.length; i < n; i++) 
        //if( arguments[i] ) 
        this.q.push( arguments[i] );
    }      
    else if( element ) {
        this.q.push( element );
    }
    return this.getSize();
  },

  /**
   * Returns the size of this Queue. The size of a Queue is equal to the number
   * of elements that have been enqueued minus the number of elements that have
   * been dequeued.
   * @method getSize
   * @return {Integer} the size of the queue
   */
  getSize: function(){
    return this.q.length - this.queueSpace;
  },

  /**
   * Returns true if this Queue is empty, and false otherwise. A Queue is empty
   * if the number of elements that have been enqueued equals the number of
   * elements that have been dequeued.
   * @method isEmpty
   * @return {Boolean} true if this Queue is empty, and false otherwise
   */
  isEmpty: function(){
    //return (this.q.length == 0);
    return (this.getSize() == 0);
  },

  /**
   * Get the next element in the queue without altering the queue. 
   * If this Queue is empty then null is returned. 
   * @method peek
   * @return {Boolean|Number|String|Array|Object|Function} the element in the queue
   */
  peek: function() {
    return this.q.length ? this.q[this.queueSpace] : null;
  },
  
  /* 
   * return an array based on the internal array of the queue.
   */
  toArray: function() {
    // @TODO: Check this against dequeue
    return [].concat(q);
  },
  
  //@TODO: Make static
  toString: function() {
    return 'knowit.Queue, version ' + this.VERSION;
  }
}
};

window.status = '';