/*
 * File        : knowit.AppFetcher.js
 * Version     : 0.1a
 * Author      : ken.gullaksen@knowit.no
 * Copyright   : 
 * Description : Wrapper component for ComponentController,
 * Notes       : 
 * Limitations : 
 * Dependencies: knowit.ComponentController.js
 * Created     : 20081126
 * History     : 
 */

window.status = 'Loading [knowit.AppFetcher.js]';

// Define the namespace 
window.knowit = window.knowit || {};

if (!knowit.AppFetcher) {
  knowit.AppFetcher = ( function() {

    var version = "1.0",
        pageApps = {},
        addPageApp = function(id, url, useParams, callbackHandler) {
          pageApps[id] = {
            id : id,
            url: url,
            useParams: useParams,
            callbackHandler: callbackHandler
          };
        };
    return ({
      getPageApp: function(id) {
        return pageApps[id];
      },
      fetchApp: function(id, url, useParams, callbackHandler) {
        callbackHandler = callbackHandler || {};
        var onFinish = callbackHandler.onFinish,
            onCreate = callbackHandler.onCreate;

        if (useParams) { //this is ugly, //todo fix
          if (url.indexOf("?") < 0) url = url.concat("?");
          else url = url.concat("&");
          url = url.concat(window.location.search.substring(1));
        }
        knowit.ComponentController.includeComponent(url, id, {
          requireSso : true,
          onCreate: function(doc, documentParts) {
            // Do something when finished, e.g. run some JS
            if (log && log.isDebugEnabled()) {
              log.debug('onCreate: ' + url);
            }

            //run hook callbackhandler
            if (callbackHandler.onCreate) {
              callbackHandler.onCreate(id);
            }
          },
          onFinish: function(doc, documentParts) {
            addPageApp(id, url, useParams, callbackHandler);
            if (log && log.isDebugEnabled()) {
              log.debug('onFinish: ' + url);
            }
            //run hook callbackhandler
            if (callbackHandler.onFinish) {
              callbackHandler.onFinish(id);
            }
          },
          onFailure: function(transport) {
            if (log && log.isDebugEnabled()) {
              log.debug('onFailure: ' + url);
            }
          },
          on403: function(transport) {
            if (log && log.isDebugEnabled()) {
              log.debug('on403: ' + url);
            }
          },
          on404: function(transport) {
            if (log && log.isDebugEnabled()) {
              log.debug('on404: ' + url);
            }
          },
          onException: function(request, exception) {
            if (log && log.isDebugEnabled()) {
              log.debug('onException: ' + url);
              log.debug(exception);
            }
          }
        });
      },
      toString: function() {
        return "AppFetcher, version:" + version;
      }
    });
  } )(); //~anonymous function immediately invoked with ()
}
;

window.status = '';
