/*
 * File        : knowit.Rater.js
 * Version     : 1.0
 * Author      : Leif Olsen, leif.olsen@start.no
 * Copyright   : (c) 2006, all rights reserved.
 * Description : Ajax ready Rater component
 * Dependencies: cr-core.js, prototype.js (>=1.5), cssQuery-p.js
 * Created     : 20060929
 * History     : ; 
 */
window.status = "Loading Rater.js";

if( typeof Prototype != 'object' ) {
  throw new Error( 'knowit.Rater.js requires prototype.js version 1.6 or greater' );
}

// Define the knowit namespace 
if(knowit == undefined) {
  var knowit = {};
}

if(!knowit.Rater) {
knowit.Rater = ( function() {

  // Private attributes and methods
  var VERSION             = '1.0';
  var ATTRIBUTE_SEPARATOR = ';';
  var NAMEVALUE_SEPARATOR = ':';
  var RATER_CLASS         = 'rater';
  var RATING_CLASS        = 'rating';
  var RATE_CLASS          = 'rate';
  var SELECTED_CLASS      = 'selected';
  var WORKING_CLASS       = 'working';
  
  var clickCallback = null;
  var processing = false;
  var working = null;
  var elementsIsUniform = true;
  var uniformW = 0;
  var uniformH = 0;

  // Collect tags 
  collectTags = function(selector, from) {
    /*
    if(!selector) {
      selector = '.' + RATER_CLASS; 
    }
    return( $A( (typeof selector == 'string') ? cssQuery(selector, $(from)) :
              (selector.constructor == Array)? selector : [selector] ) );
    */
    return $$( selector || '.' + RATER_CLASS );
    
  };
  
  // Collect rater tags
  collectRaterTags = function(selector, from) {
    uniformW = 0;
    uniformH = 0;    

    var a, s, v, w, ul, li;
    var tags = collectTags(selector, from);
    if(tags && tags.length>0) {
    for (var i=0; i<tags.length; i++) {

      if (!Element.hasClassName(tags[i], 'rater')) { 
        // Get real rater tag
        //tags[i] = Element.getElementsByClassName(tags[i], 'rater')[0];			
        tags[i] = tags[i].select('.rater')[0];			
	    }
      // Only proceed if we have an rater tag
      if(tags[i]) {
        // Calculate width of each raters <ul> tag
        if(!elementsIsUniform || uniformW < 1) {
          // For some reason it consumes a lot of cpu time  
          // to call offsetWidth inside a loop (ff and ie).
          // Try to reduce calls to offsetWidth/offsetHeight
          li = tags[i].getElementsByTagName('li');
          uniformW = 0;
          for (var j=0; j<li.length; j++) {
            w = li[j].offsetWidth;
            if(w > uniformW) {
              uniformW = w;
            }
          } //~for j
          
        } //~if(!elementsIsUniform ...)

        // Set width of <ul> tag
        ul = $(tags[i].getElementsByTagName('ul')[0] || tags[i]);
        ul.setStyle( { width: uniformW+'px'} ); 
        
        if(!elementsIsUniform) {
          uniformH = ul.offsetHeight;
        }
        else if (uniformH < 1) {
          uniformH = ul.offsetHeight;
        }
          
        // Keep width and height so we don't have to access offsetWidt/offsetHeight 
        // next time we need dimensions for this <ul> tag
        ul._width = uniformW;
        ul._height = uniformH;

        // Collect <a> tags inside <ul>
        a = $A(ul.getElementsByTagName('a'));
        if(a && a.length>0) {
          // Show/Hide rater on mouse over
/* LOO-20070223          
          ul.observe('mouseover',  function(ev) {
            hideRatingTag(this);
          }.bindAsEventListener(ul) ); //~ul.oberve mouseover

          ul.observe('mouseout',  function(ev) {
            showRatingTag(this);
          }.bindAsEventListener(ul) ); //~ul.oberve mouseout
*/
          // Handle <a> click event
          a.each( function(tag) {
            tag = $(tag); // Needed by stupid m$ie 
            tag.observe('click',  function(ev) {
              Event.stop(ev);
  	    
              // Get the rater tag
              var rater = getRaterTag(this);
              if(rater && !processing) {
                // Remove working image
                hideWorking();
  	      
                // Set selection
                var li = Event.findElement(ev, 'li');
                toggleSelection(rater, li);
  
                // Fire callback	    
                if(clickCallback && !rater._rateTag) {
                  processing = true;
                  // Set working image
                  showWorking(rater);
                  clickCallback(this, rater, this.hash.replace('#', '') ); 
                } //~if
              } //~if(rater && !processing)
            }.bindAsEventListener(tag) ); //~tag.oberve click
  	  
          } ); //~a.each   
          
        } //~if(a

        
        // Collect .rate tag inside .rater tags
        // The rate tag is used to submit a rater selection
        // Ther can be 0,1 or n rate tags per rater
        //var rateTags = Element.getElementsByClassName(tags[i], 'rate');
        var rateTags = tags[i].select('.rate');
        
        knowit.Rater.bindRateTag(tags[i], rateTags); 

        // Parse options attribute
        // Options attribute can be located on '.rater' tag or containing <ul> tag or both
        var s = tags[i].getAttribute('options');
        if(tags[i] != ul && ul.getAttribute('options')) {
          s = ul.getAttribute('options') + (s ? ATTRIBUTE_SEPARATOR+s : '');
        }
        if(s) {
          a = s.splitAttributes(ATTRIBUTE_SEPARATOR, NAMEVALUE_SEPARATOR);

          v = a['key'];
          if(v) { tags[i]._key = v}

          v = a['rating'];
          if(v) { knowit.Rater.setRating(tags[i], parseFloat(v||0));}

          v = a['rate-tag'];
          if(v) { knowit.Rater.bindRateTag(tags[i], $(v));}
        } //~if(s)

      } //~if(tags[i])
      } //~for i

      // Remove rejected rater tags     
      tags = tags.compact();
    }

    return(tags);
  }; //~collectRaterTags
  
  getRaterTag = function(tag) {
    // Get the rater tag that contains the tag
    var rater = tag;
    while (rater = rater.parentNode) {
      if (Element.hasClassName(rater, 'rater') ) { break; }
    } //~while
    return(rater ? $(rater) : null);
  };

  getSelectedTag = function(rater) {
    var li = $A(rater.getElementsByTagName('li'));
    return( li.detect( function(t) { return t.className.hasWord('selected') } ) );
  };
  
  hideRatingTag = function(rater) {
    //var li = $(Element.getElementsByClassName(rater, 'rating')[0]);
    var li = rater.select('.rating')[0];
    if(li) { li.hide() };
  };
    
  hideWorking = function() {
    if(working) {
      working.setStyle( {visibility:'hidden', width:0} );
    }
  };
  
  onRateClick = function(ev, rater) {
    Event.stop(ev);

    // Get the rater tag. 'this' points to the rate tag
    if(rater && !processing) {
      // Set working image
      if(clickCallback) {
        // Get selection
        var ul = $(rater.getElementsByTagName('ul')[0] || rater);
        var li = $A(ul.getElementsByTagName('li'));
        var s = li.detect( function(t) { return t.className.hasWord('selected') } );
        var a = s ? s.getElementsByTagName('a')[0] : null;
            s = a && a.hash ? a.hash.replace('#', '') : '';

        // Execute callback	    
        if(s) { 
          processing = true;
          hideWorking();
          showWorking(rater);
          clickCallback(this, rater, s ); 
        }
      } //~if
    } //~if(rater && !processing)
  };
  
  showRatingTag = function(rater) {
    // Only show if no selection
    if(!getSelectedTag(rater)) {
      //li = $(Element.getElementsByClassName(rater, 'rating')[0]);
      li = rater.select('.rating')[0];
      if(li) { li.show() };
    }
  };
    
  showWorking = function(rater) {
    //working = $(cssQuery('.working', rater)[0]);
    //working = Element.getElementsByClassName(rater, 'working')[0];
    working = rater.select('.working')[0];
    
    if(!working) {
      var ul = rater.getElementsByTagName('ul')[0] || rater;
      working = $(document.createElement('li'));
      working.className = 'working';
      working.innerHTML = '<span></span>';
      ul.appendChild(working);
    }
    working.setStyle( {
      visibility:'visible', 
      width:'100%', 
      height:$(working.parentNode).getHeight()+'px'} ); // height:100% does not work in ie6
  };
  
  toggleSelection = function(rater, newSelection) {
    // Remove old selection
    var s = getSelectedTag(rater);
    if(s) { s.className = s.className.replace(/selected/gi, ''); }
      
    if(newSelection) {
      // Hide rating
      hideRatingTag(rater);
      
      // Set new selection
      newSelection.className += (newSelection.className ? ' ' : '') + 'selected';
      
      // For stupid stupid m$ie that does not perform
      $(newSelection).setStyle( {
        height:$(newSelection.parentNode).getHeight()+'px'} ); // height:100% does not work in ie6
    }
  };
  
return ( {
  // Public attributes and methods
  init: function(callback, selector, from, isUniform) {
    if(callback) {
      clickCallback = callback;
    }
    return(this.append(selector, from, isUniform));
  }, //~init
  
  append: function(selector, from, isUniform) {
    elementsIsUniform = (typeof isUniform != 'undefined') ? isUniform : true;
    // Collect rater tags
    return(collectRaterTags(selector, from).reduce());
  }, //~append

  bindRateTag: function(rater, rateTags) {
    // If a rate tag is attached, callback is blocked when a rater tag element is clicked.
    // Callback will only fire when rate tag is clicked
    var tags = collectTags(rateTags, rater);
    rater = $(rater);
    
    if(tags.length>0 && rater) {
      rater._rateTag = tags; // Only a flag to inform rater that a rate tag is used to submit selection
      $A(tags).each( function(tag) {
        tag.observe('click', onRateClick.bindAsEventListener(tag, rater));
      } ); //~each   
    }
  },
  
  // Get key used to store rater value in DB etc., etc.
  getKey: function(rater) {
    return(rater._key || null);
  },
  
  isProcessing: function() {
    return(processing);
  },
  
  processAborted: function(rater) {
    hideWorking();
    processing = false;
  }, //~processAborted
  
  processComplete: function(rater, rating) {
    rater = $(rater);
    toggleSelection(rater);
    this.setRating(rater, rating);
    hideWorking();
    processing = false;
  }, //~processComplete
  
  // Set key used to store rater value in DB etc., etc.
  setKey: function(rater, key) {
    if( rater ) {
      rater._key = key || null;
    }
  }, //~setKey
  
  setRating: function(rater, rating) {
    if(rater) {
      //var li = $(cssQuery('.rating', rater)[0]);
      //var li = $(Element.getElementsByClassName(rater, 'rating')[0]);
      var li = rater.select('.rating')[0];
      var ul = $(rater.getElementsByTagName('ul')[0] || rater);
      if(!li) {
        li = $(document.createElement('li'));
        li.className = 'rating';
        ul.appendChild(li);
      }
      
      //var d = ul.getDimensions();
      
      var p = parseFloat(rating || 0) > 1.0 ? 1.0 : parseFloat(rating);
      var w = Math.round(ul._width * p) + 'px'; //parseInt(d.width * p) + 'px';
      
      li.setStyle( {width:w, height:ul._height+'px', display:'block'} );
    } //~if(rater)
    
  }, //~setRating
  
  toString: function() { 
    return 'Rater, version ' + VERSION;
  }
  
}); //~return

} ) (); //~anonymous function immediately invoked with ()

} //~if( !knowit.Rater )

window.status = "";
