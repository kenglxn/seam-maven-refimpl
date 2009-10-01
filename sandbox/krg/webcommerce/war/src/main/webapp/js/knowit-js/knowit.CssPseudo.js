/*
 * File        : knowit.CssPseudo.js
 * Version     : 1.2
 * Author      : Leif Olsen, leif.olsen@start.no
 * Copyright   : (c) 2005 Leif Olsen, all rights reserved.
 * Description : Mimic of the :hover, :active, :focus and :target 
 *               CSS pseudo-classes that ie up to version 6 does not support
 *               Inspired by/adapted from htmldog.com Suckerfish
 * Issues      : IE6 bug: 
 *               If a label is attached to a selectbox ( <label for='id-of-selectbox'> )
 *               and that particular label is clicked, then the value of the selectbox is 
 *               reset to the value of the first item in the select box
 * Dependencies: prototype.js (>=1.6)
 * Created     : 20051107
 * History     : 20060606 - Rewritten to singleton. 
 *                          Started to use prototype.js and cssQuery.js libraries
 *               20061101 - Tested ie7 and found that ie7 does not support
 *                          :active, :focus and :target. Only supports :hover
 *               20061127 - Renamed from dom-csspseudo.js to CSSPseudo.js     
 *             : 20080928 - Modified for knowit
 *               20081021 - Replaced cssQuery with $$
 */
window.status = "Loading knowit.CSSPseudo.js";

if( typeof Prototype != 'object' ) {
  throw new Error( 'knowit.Csi.js requires prototype.js version 1.6 or greater' );
}

// Define the knowit namespace 
if(knowit == undefined) {
  var knowit = {};
}

knowit.CssPseudo = ( function() {
  // Private variables
  var VERSION     = '1.3',
      activeClass = 'active',
      focusClass  = 'focus',
      hoverClass  = 'hover',
      targetClass = 'target',
      lastTarget  = null,
      fixSelect   = null;  // See Issues;

  // Private methods

  /*
   * Mimicking the :active pseudo-class.
   * ie7 does NOT support :active
   *
   * CSS usage->  p:active, p.active { color: red; }
   */
  _active = function(tags) {
    tags.each( function(el) { 
      Event.observe(el, 'mousedown', function(ev) {
          el.addClassName(activeClass);
      } ); //~Event.oberve
        
      Event.observe(el, 'mouseup',   function(ev) {
          el.removeClassName(activeClass);
      } ); //~Event.oberve
    } ); //~each
  }; //~active
  
  /* 
   * Mimicking the :focus pseudo-class.
   * The :focus pseudo-class can be used to apply styles to a page element that receives focus. 
   * ie7 does NOT support :focus (at least not on input)
   * 
   * CSS usage-> input:focus, input.focus { background: orange; }
   */
  _focus = function(tags) {
    tags.each( function(el) {
      el = $(el); // Needed by m$ie 
 
      el.observe('focus', function(ev) {
        this.addClassName(focusClass);
      }.bindAsEventListener(el) ); //~el.oberve
                                   // .bindAsEventListener(el) is not necessary since 
                                   // closure is applied, left
                                   // here for me to remember how to use :) 

      el.observe('blur',  function(ev) { 
        el.removeClassName(focusClass);  // See notes above!
      } ); //~el.observe
      
    } ); //~each
  } //~focus


  /*
   * Mimicking the :hover pseudo-class.
   * To change the style of an element that is hovered over with the cursor 
   * with the :hover pseudo-class.
   * Internet Explorer will only recognise:hover when it comes to links however, 
   * but by applying this JavaScript method you can acheive the hover effect on
   * other elemnts as well.
   *
   * CSS usage-> li:hover ul, li.hover ul { display: block } 
   */
  _hover = function(tags) {
    // ie7 supports :hover
    // document.body.style.maxHeight is not defined for ie6
    if (typeof document.body.style.maxHeight == "undefined") {
      tags.each( function(el) {
        el = $(el); // Needed by m$ie

        el.observe('mouseenter', function(ev) {
          el.addClassName(hoverClass);
        } ); //~el.observe
          
        el.observe('mouseleave',  function(ev) {
          el.removeClassName(hoverClass);
        } ); //~el.observe
      } ); //~each
    }; //~if (typeof...
  }; //~hover


  /*
   * Mimicking the CSS3 :target pseudo-class.
   * The CSS3 :target pseudo class allows you to style a targeted page anchor. 
   * ie7 does NOT support :target
   *
   * CSS usage-> h2:target, h2.target { color: white; background: #f60; }
   */
  _target = function(tags) {
    var aTags = $A(document.getElementsByTagName('A'));

    for (var i=0; i<tags.length; i++) {
      if(tags[i].id) {
        if (location.hash==("#" + tags[i].id)) {
          $(tags[i]).addClassName(activeClass);
          lastTarget=tags[i];
        }
        aTags.each( function(el) {
          if(el.hash == ("#" + tags[i].id)) {
            el.targetElement = $(tags[i]);

            Event.observe(el, 'click', function(ev) {
              if(lastTarget) {
                lastTarget.removeClassName(targetClass);
              }
              if(el.targetElement) {
                el.targetElement.addClassName(targetClass);
              }
              lastTarget = (el.targetElement) ? el.targetElement : null;
            } ); //~Event.oberve

          } //~if
        } ); //~each
      } //~if(tags[i].id)
    } //~for i
  }; //~target)

return ( {
  // Public attributes and methods

  // Handle pseudo classes
  handleClass: function(type, selector, from) {
  
    if (document.all && !window.opera) {
      // IE6+7 Only. Real browsers use pseudo classes
      from = $(from) || document;
      
      // IMPORTANT!!!! No var before 'tags' variable (obfuscator must not obfuscate 'tags')
      tags = $A( selector ? selector.constructor === Array
                          ? selector : Selector.findChildElements(from, $A(selector.split(',') )) : [] );
      if(tags && tags.length>0) {
        if (/focus/i.test(type) && (typeof document.body.style.maxHeight == "undefined")) {
          // ie6 only
          if(!fixSelect) {
            // Create a function and run only once
            fixSelect = function() {
              $$('select').each( function(el) { 
                if(el.id) {
                  // Find attached label
                  var labels = Selector.findChildElements(el.form, 'label[for="' + el.id + '"]');
                  if(labels && labels.length > 0) {
                    labels[0].setAttribute('htmlFor', ''); // remove the labels for attribute
                    // Use event handler to focus 
                    Event.observe(labels[0], 'click', function(ev) { el.focus(); } );
                  } //~if labels ...
                } //~if(el.id)
              } ); //~tags.each
            }; //~fixSelect
            
            // Execute fix
            fixSelect();
          } //~if(!fixSelect)
        } //~if(/focus/i.test...

        // Execute function corresponding to type
        var evalStr = '_' + type +'(tags)';
        eval(evalStr);
      } //~if 
    } //~if (document.all && ...
  }, // ~handleClass

  toString: function() { 
    return 'knowit.CssPseudo, version ' + VERSION;
  }
  
}); //~return
	
} ) (); //~anonymous function immediately invoked with ()


window.status = "";
