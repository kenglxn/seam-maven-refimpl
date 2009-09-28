/*
 Copyright (c) 2004-2009, The Dojo Foundation All Rights Reserved.
 Available via Academic Free License >= 2.1 OR the modified BSD license.
 see: http://dojotoolkit.org/license for details
 */

/*
 This is a compiled version of Dojo, built for deployment and not for
 development. To get an editable version, please visit:

 http://dojotoolkit.org

 for documentation and information on getting the source.
 */

if (!dojo._hasResource["dijit._base.focus"]) {
    dojo._hasResource["dijit._base.focus"] = true;
    dojo.provide("dijit._base.focus");
    dojo.mixin(dijit, {_curFocus:null,_prevFocus:null,isCollapsed:function() {
        var _1 = dojo.doc;
        if (_1.selection) {
            var s = _1.selection;
            if (s.type == "Text") {
                return !s.createRange().htmlText.length;
            } else {
                return !s.createRange().length;
            }
        } else {
            var _3 = dojo.global;
            var _4 = _3.getSelection();
            if (dojo.isString(_4)) {
                return !_4;
            } else {
                return !_4 || _4.isCollapsed || !_4.toString();
            }
        }
    },getBookmark:function() {
        var _5,_6 = dojo.doc.selection;
        if (_6) {
            var _7 = _6.createRange();
            if (_6.type.toUpperCase() == "CONTROL") {
                if (_7.length) {
                    _5 = [];
                    var i = 0,_9 = _7.length;
                    while (i < _9) {
                        _5.push(_7.item(i++));
                    }
                } else {
                    _5 = null;
                }
            } else {
                _5 = _7.getBookmark();
            }
        } else {
            if (window.getSelection) {
                _6 = dojo.global.getSelection();
                if (_6) {
                    _7 = _6.getRangeAt(0);
                    _5 = _7.cloneRange();
                }
            } else {
                console.warn("No idea how to store the current selection for this browser!");
            }
        }
        return _5;
    },moveToBookmark:function(_a) {
        var _b = dojo.doc;
        if (_b.selection) {
            var _c;
            if (dojo.isArray(_a)) {
                _c = _b.body.createControlRange();
                dojo.forEach(_a, function(n) {
                    _c.addElement(n);
                });
            } else {
                _c = _b.selection.createRange();
                _c.moveToBookmark(_a);
            }
            _c.select();
        } else {
            var _e = dojo.global.getSelection && dojo.global.getSelection();
            if (_e && _e.removeAllRanges) {
                _e.removeAllRanges();
                _e.addRange(_a);
            } else {
                console.warn("No idea how to restore selection for this browser!");
            }
        }
    },getFocus:function(_f, _10) {
        return {node:_f && dojo.isDescendant(dijit._curFocus, _f.domNode) ? dijit._prevFocus : dijit._curFocus,bookmark:!dojo.withGlobal(_10 || dojo.global, dijit.isCollapsed) ? dojo.withGlobal(_10 || dojo.global, dijit.getBookmark) : null,openedForWindow:_10};
    },focus:function(_11) {
        if (!_11) {
            return;
        }
        var _12 = "node" in _11 ? _11.node : _11,_13 = _11.bookmark,_14 = _11.openedForWindow;
        if (_12) {
            var _15 = (_12.tagName.toLowerCase() == "iframe") ? _12.contentWindow : _12;
            if (_15 && _15.focus) {
                try {
                    _15.focus();
                } catch(e) {
                }
            }
            dijit._onFocusNode(_12);
        }
        if (_13 && dojo.withGlobal(_14 || dojo.global, dijit.isCollapsed)) {
            if (_14) {
                _14.focus();
            }
            try {
                dojo.withGlobal(_14 || dojo.global, dijit.moveToBookmark, null, [_13]);
            } catch(e) {
            }
        }
    },_activeStack:[],registerIframe:function(_16) {
        dijit.registerWin(_16.contentWindow, _16);
    },registerWin:function(_17, _18) {
        dojo.connect(_17.document, "onmousedown", function(evt) {
            dijit._justMouseDowned = true;
            setTimeout(function() {
                dijit._justMouseDowned = false;
            }, 0);
            dijit._onTouchNode(_18 || evt.target || evt.srcElement);
        });
        var doc = _17.document;
        if (doc) {
            if (dojo.isIE) {
                doc.attachEvent("onactivate", function(evt) {
                    if (evt.srcElement.tagName.toLowerCase() != "#document") {
                        dijit._onFocusNode(_18 || evt.srcElement);
                    }
                });
                doc.attachEvent("ondeactivate", function(evt) {
                    dijit._onBlurNode(_18 || evt.srcElement);
                });
            } else {
                doc.addEventListener("focus", function(evt) {
                    dijit._onFocusNode(_18 || evt.target);
                }, true);
                doc.addEventListener("blur", function(evt) {
                    dijit._onBlurNode(_18 || evt.target);
                }, true);
            }
        }
        doc = null;
    },_onBlurNode:function(_1f) {
        dijit._prevFocus = dijit._curFocus;
        dijit._curFocus = null;
        if (dijit._justMouseDowned) {
            return;
        }
        if (dijit._clearActiveWidgetsTimer) {
            clearTimeout(dijit._clearActiveWidgetsTimer);
        }
        dijit._clearActiveWidgetsTimer = setTimeout(function() {
            delete dijit._clearActiveWidgetsTimer;
            dijit._setStack([]);
            dijit._prevFocus = null;
        }, 100);
    },_onTouchNode:function(_20) {
        if (dijit._clearActiveWidgetsTimer) {
            clearTimeout(dijit._clearActiveWidgetsTimer);
            delete dijit._clearActiveWidgetsTimer;
        }
        var _21 = [];
        try {
            while (_20) {
                if (_20.dijitPopupParent) {
                    _20 = dijit.byId(_20.dijitPopupParent).domNode;
                } else {
                    if (_20.tagName && _20.tagName.toLowerCase() == "body") {
                        if (_20 === dojo.body()) {
                            break;
                        }
                        _20 = dijit.getDocumentWindow(_20.ownerDocument).frameElement;
                    } else {
                        var id = _20.getAttribute && _20.getAttribute("widgetId");
                        if (id) {
                            _21.unshift(id);
                        }
                        _20 = _20.parentNode;
                    }
                }
            }
        } catch(e) {
        }
        dijit._setStack(_21);
    },_onFocusNode:function(_23) {
        if (!_23) {
            return;
        }
        if (_23.nodeType == 9) {
            return;
        }
        dijit._onTouchNode(_23);
        if (_23 == dijit._curFocus) {
            return;
        }
        if (dijit._curFocus) {
            dijit._prevFocus = dijit._curFocus;
        }
        dijit._curFocus = _23;
        dojo.publish("focusNode", [_23]);
    },_setStack:function(_24) {
        var _25 = dijit._activeStack;
        dijit._activeStack = _24;
        for (var _26 = 0; _26 < Math.min(_25.length, _24.length); _26++) {
            if (_25[_26] != _24[_26]) {
                break;
            }
        }
        for (var i = _25.length - 1; i >= _26; i--) {
            var _28 = dijit.byId(_25[i]);
            if (_28) {
                _28._focused = false;
                _28._hasBeenBlurred = true;
                if (_28._onBlur) {
                    _28._onBlur();
                }
                if (_28._setStateClass) {
                    _28._setStateClass();
                }
                dojo.publish("widgetBlur", [_28]);
            }
        }
        for (i = _26; i < _24.length; i++) {
            _28 = dijit.byId(_24[i]);
            if (_28) {
                _28._focused = true;
                if (_28._onFocus) {
                    _28._onFocus();
                }
                if (_28._setStateClass) {
                    _28._setStateClass();
                }
                dojo.publish("widgetFocus", [_28]);
            }
        }
    }});
    dojo.addOnLoad(function() {
        dijit.registerWin(window);
    });
}
if (!dojo._hasResource["dijit._base.manager"]) {
    dojo._hasResource["dijit._base.manager"] = true;
    dojo.provide("dijit._base.manager");
    dojo.declare("dijit.WidgetSet", null, {constructor:function() {
        this._hash = {};
    },add:function(_29) {
        if (this._hash[_29.id]) {
            throw new Error("Tried to register widget with id==" + _29.id + " but that id is already registered");
        }
        this._hash[_29.id] = _29;
    },remove:function(id) {
        delete this._hash[id];
    },forEach:function(_2b) {
        for (var id in this._hash) {
            _2b(this._hash[id]);
        }
    },filter:function(_2d) {
        var res = new dijit.WidgetSet();
        this.forEach(function(_2f) {
            if (_2d(_2f)) {
                res.add(_2f);
            }
        });
        return res;
    },byId:function(id) {
        return this._hash[id];
    },byClass:function(cls) {
        return this.filter(function(_32) {
            return _32.declaredClass == cls;
        });
    }});
    dijit.registry = new dijit.WidgetSet();
    dijit._widgetTypeCtr = {};
    dijit.getUniqueId = function(_33) {
        var id;
        do{
            id = _33 + "_" + (_33 in dijit._widgetTypeCtr ? ++dijit._widgetTypeCtr[_33] : dijit._widgetTypeCtr[_33] = 0);
        } while (dijit.byId(id));
        return id;
    };
    dijit.findWidgets = function(_35) {
        var _36 = [];

        function _37(_38) {
            var _39 = dojo.isIE ? _38.children : _38.childNodes,i = 0,_3b;
            while (_3b = _39[i++]) {
                if (_3b.nodeType != 1) {
                    continue;
                }
                var _3c = _3b.getAttribute("widgetId");
                if (_3c) {
                    var _3d = dijit.byId(_3c);
                    _36.push(_3d);
                } else {
                    _37(_3b);
                }
            }
        }

        ;
        _37(_35);
        return _36;
    };
    if (dojo.isIE) {
        dojo.addOnWindowUnload(function() {
            dojo.forEach(dijit.findWidgets(dojo.body()), function(_3e) {
                if (_3e.destroyRecursive) {
                    _3e.destroyRecursive();
                } else {
                    if (_3e.destroy) {
                        _3e.destroy();
                    }
                }
            });
        });
    }
    dijit.byId = function(id) {
        return (dojo.isString(id)) ? dijit.registry.byId(id) : id;
    };
    dijit.byNode = function(_40) {
        return dijit.registry.byId(_40.getAttribute("widgetId"));
    };
    dijit.getEnclosingWidget = function(_41) {
        while (_41) {
            if (_41.getAttribute && _41.getAttribute("widgetId")) {
                return dijit.registry.byId(_41.getAttribute("widgetId"));
            }
            _41 = _41.parentNode;
        }
        return null;
    };
    dijit._tabElements = {area:true,button:true,input:true,object:true,select:true,textarea:true};
    dijit._isElementShown = function(_42) {
        var _43 = dojo.style(_42);
        return (_43.visibility != "hidden") && (_43.visibility != "collapsed") && (_43.display != "none") && (dojo.attr(_42, "type") != "hidden");
    };
    dijit.isTabNavigable = function(_44) {
        if (dojo.hasAttr(_44, "disabled")) {
            return false;
        }
        var _45 = dojo.hasAttr(_44, "tabindex");
        var _46 = dojo.attr(_44, "tabindex");
        if (_45 && _46 >= 0) {
            return true;
        }
        var _47 = _44.nodeName.toLowerCase();
        if (((_47 == "a" && dojo.hasAttr(_44, "href")) || dijit._tabElements[_47]) && (!_45 || _46 >= 0)) {
            return true;
        }
        return false;
    };
    dijit._getTabNavigable = function(_48) {
        var _49,_4a,_4b,_4c,_4d,_4e;
        var _4f = function(_50) {
            dojo.query("> *", _50).forEach(function(_51) {
                var _52 = dijit._isElementShown(_51);
                if (_52 && dijit.isTabNavigable(_51)) {
                    var _53 = dojo.attr(_51, "tabindex");
                    if (!dojo.hasAttr(_51, "tabindex") || _53 == 0) {
                        if (!_49) {
                            _49 = _51;
                        }
                        _4a = _51;
                    } else {
                        if (_53 > 0) {
                            if (!_4b || _53 < _4c) {
                                _4c = _53;
                                _4b = _51;
                            }
                            if (!_4d || _53 >= _4e) {
                                _4e = _53;
                                _4d = _51;
                            }
                        }
                    }
                }
                if (_52 && _51.nodeName.toUpperCase() != "SELECT") {
                    _4f(_51);
                }
            });
        };
        if (dijit._isElementShown(_48)) {
            _4f(_48);
        }
        return {first:_49,last:_4a,lowest:_4b,highest:_4d};
    };
    dijit.getFirstInTabbingOrder = function(_54) {
        var _55 = dijit._getTabNavigable(dojo.byId(_54));
        return _55.lowest ? _55.lowest : _55.first;
    };
    dijit.getLastInTabbingOrder = function(_56) {
        var _57 = dijit._getTabNavigable(dojo.byId(_56));
        return _57.last ? _57.last : _57.highest;
    };
    dijit.defaultDuration = dojo.config["defaultDuration"] || 200;
}
if (!dojo._hasResource["dojo.AdapterRegistry"]) {
    dojo._hasResource["dojo.AdapterRegistry"] = true;
    dojo.provide("dojo.AdapterRegistry");
    dojo.AdapterRegistry = function(_58) {
        this.pairs = [];
        this.returnWrappers = _58 || false;
    };
    dojo.extend(dojo.AdapterRegistry, {register:function(_59, _5a, _5b, _5c, _5d) {
        this.pairs[((_5d) ? "unshift" : "push")]([_59,_5a,_5b,_5c]);
    },match:function() {
        for (var i = 0; i < this.pairs.length; i++) {
            var _5f = this.pairs[i];
            if (_5f[1].apply(this, arguments)) {
                if ((_5f[3]) || (this.returnWrappers)) {
                    return _5f[2];
                } else {
                    return _5f[2].apply(this, arguments);
                }
            }
        }
        throw new Error("No match found");
    },unregister:function(_60) {
        for (var i = 0; i < this.pairs.length; i++) {
            var _62 = this.pairs[i];
            if (_62[0] == _60) {
                this.pairs.splice(i, 1);
                return true;
            }
        }
        return false;
    }});
}
if (!dojo._hasResource["dijit._base.place"]) {
    dojo._hasResource["dijit._base.place"] = true;
    dojo.provide("dijit._base.place");
    dijit.getViewport = function() {
        var _63 = (dojo.doc.compatMode == "BackCompat") ? dojo.body() : dojo.doc.documentElement;
        var _64 = dojo._docScroll();
        return {w:_63.clientWidth,h:_63.clientHeight,l:_64.x,t:_64.y};
    };
    dijit.placeOnScreen = function(_65, pos, _67, _68) {
        var _69 = dojo.map(_67, function(_6a) {
            var c = {corner:_6a,pos:{x:pos.x,y:pos.y}};
            if (_68) {
                c.pos.x += _6a.charAt(1) == "L" ? _68.x : -_68.x;
                c.pos.y += _6a.charAt(0) == "T" ? _68.y : -_68.y;
            }
            return c;
        });
        return dijit._place(_65, _69);
    };
    dijit._place = function(_6c, _6d, _6e) {
        var _6f = dijit.getViewport();
        if (!_6c.parentNode || String(_6c.parentNode.tagName).toLowerCase() != "body") {
            dojo.body().appendChild(_6c);
        }
        var _70 = null;
        dojo.some(_6d, function(_71) {
            var _72 = _71.corner;
            var pos = _71.pos;
            if (_6e) {
                _6e(_6c, _71.aroundCorner, _72);
            }
            var _74 = _6c.style;
            var _75 = _74.display;
            var _76 = _74.visibility;
            _74.visibility = "hidden";
            _74.display = "";
            var mb = dojo.marginBox(_6c);
            _74.display = _75;
            _74.visibility = _76;
            var _78 = (_72.charAt(1) == "L" ? pos.x : Math.max(_6f.l, pos.x - mb.w)),_79 = (_72.charAt(0) == "T" ? pos.y : Math.max(_6f.t, pos.y - mb.h)),_7a = (_72.charAt(1) == "L" ? Math.min(_6f.l + _6f.w, _78 + mb.w) : pos.x),_7b = (_72.charAt(0) == "T" ? Math.min(_6f.t + _6f.h, _79 + mb.h) : pos.y),_7c = _7a - _78,_7d = _7b - _79,_7e = (mb.w - _7c) + (mb.h - _7d);
            if (_70 == null || _7e < _70.overflow) {
                _70 = {corner:_72,aroundCorner:_71.aroundCorner,x:_78,y:_79,w:_7c,h:_7d,overflow:_7e};
            }
            return !_7e;
        });
        _6c.style.left = _70.x + "px";
        _6c.style.top = _70.y + "px";
        if (_70.overflow && _6e) {
            _6e(_6c, _70.aroundCorner, _70.corner);
        }
        return _70;
    };
    dijit.placeOnScreenAroundNode = function(_7f, _80, _81, _82) {
        _80 = dojo.byId(_80);
        var _83 = _80.style.display;
        _80.style.display = "";
        var _84 = _80.offsetWidth;
        var _85 = _80.offsetHeight;
        var _86 = dojo.coords(_80, true);
        _80.style.display = _83;
        return dijit._placeOnScreenAroundRect(_7f, _86.x, _86.y, _84, _85, _81, _82);
    };
    dijit.placeOnScreenAroundRectangle = function(_87, _88, _89, _8a) {
        return dijit._placeOnScreenAroundRect(_87, _88.x, _88.y, _88.width, _88.height, _89, _8a);
    };
    dijit._placeOnScreenAroundRect = function(_8b, x, y, _8e, _8f, _90, _91) {
        var _92 = [];
        for (var _93 in _90) {
            _92.push({aroundCorner:_93,corner:_90[_93],pos:{x:x + (_93.charAt(1) == "L" ? 0 : _8e),y:y + (_93.charAt(0) == "T" ? 0 : _8f)}});
        }
        return dijit._place(_8b, _92, _91);
    };
    dijit.placementRegistry = new dojo.AdapterRegistry();
    dijit.placementRegistry.register("node", function(n, x) {
        return typeof x == "object" && typeof x.offsetWidth != "undefined" && typeof x.offsetHeight != "undefined";
    }, dijit.placeOnScreenAroundNode);
    dijit.placementRegistry.register("rect", function(n, x) {
        return typeof x == "object" && "x" in x && "y" in x && "width" in x && "height" in x;
    }, dijit.placeOnScreenAroundRectangle);
    dijit.placeOnScreenAroundElement = function(_98, _99, _9a, _9b) {
        return dijit.placementRegistry.match.apply(dijit.placementRegistry, arguments);
    };
}
if (!dojo._hasResource["dijit._base.window"]) {
    dojo._hasResource["dijit._base.window"] = true;
    dojo.provide("dijit._base.window");
    dijit.getDocumentWindow = function(doc) {
        if (dojo.isIE && window !== document.parentWindow && !doc._parentWindow) {
            doc.parentWindow.execScript("document._parentWindow = window;", "Javascript");
            var win = doc._parentWindow;
            doc._parentWindow = null;
            return win;
        }
        return doc._parentWindow || doc.parentWindow || doc.defaultView;
    };
}
if (!dojo._hasResource["dijit._base.popup"]) {
    dojo._hasResource["dijit._base.popup"] = true;
    dojo.provide("dijit._base.popup");
    dijit.popup = new function() {
        var _9e = [],_9f = 1000,_a0 = 1;
        this.prepare = function(_a1) {
            var s = _a1.style;
            s.visibility = "hidden";
            s.position = "absolute";
            s.top = "-9999px";
            if (s.display == "none") {
                s.display = "";
            }
            dojo.body().appendChild(_a1);
        };
        this.open = function(_a3) {
            var _a4 = _a3.popup,_a5 = _a3.orient || {"BL":"TL","TL":"BL"},_a6 = _a3.around,id = (_a3.around && _a3.around.id) ? (_a3.around.id + "_dropdown") : ("popup_" + _a0++);
            var _a8 = dojo.create("div", {id:id,"class":"dijitPopup",style:{zIndex:_9f + _9e.length,visibility:"hidden"}}, dojo.body());
            dijit.setWaiRole(_a8, "presentation");
            _a8.style.left = _a8.style.top = "0px";
            if (_a3.parent) {
                _a8.dijitPopupParent = _a3.parent.id;
            }
            var s = _a4.domNode.style;
            s.display = "";
            s.visibility = "";
            s.position = "";
            s.top = "0px";
            _a8.appendChild(_a4.domNode);
            var _aa = new dijit.BackgroundIframe(_a8);
            var _ab = _a6 ? dijit.placeOnScreenAroundElement(_a8, _a6, _a5, _a4.orient ? dojo.hitch(_a4, "orient") : null) : dijit.placeOnScreen(_a8, _a3, _a5 == "R" ? ["TR","BR","TL","BL"] : ["TL","BL","TR","BR"], _a3.padding);
            _a8.style.visibility = "visible";
            var _ac = [];
            var _ad = function() {
                for (var pi = _9e.length - 1; pi > 0 && _9e[pi].parent === _9e[pi - 1].widget; pi--) {
                }
                return _9e[pi];
            };
            _ac.push(dojo.connect(_a8, "onkeypress", this, function(evt) {
                if (evt.charOrCode == dojo.keys.ESCAPE && _a3.onCancel) {
                    dojo.stopEvent(evt);
                    _a3.onCancel();
                } else {
                    if (evt.charOrCode === dojo.keys.TAB) {
                        dojo.stopEvent(evt);
                        var _b0 = _ad();
                        if (_b0 && _b0.onCancel) {
                            _b0.onCancel();
                        }
                    }
                }
            }));
            if (_a4.onCancel) {
                _ac.push(dojo.connect(_a4, "onCancel", null, _a3.onCancel));
            }
            _ac.push(dojo.connect(_a4, _a4.onExecute ? "onExecute" : "onChange", null, function() {
                var _b1 = _ad();
                if (_b1 && _b1.onExecute) {
                    _b1.onExecute();
                }
            }));
            _9e.push({wrapper:_a8,iframe:_aa,widget:_a4,parent:_a3.parent,onExecute:_a3.onExecute,onCancel:_a3.onCancel,onClose:_a3.onClose,handlers:_ac});
            if (_a4.onOpen) {
                _a4.onOpen(_ab);
            }
            return _ab;
        };
        this.close = function(_b2) {
            while (dojo.some(_9e, function(_b3) {
                return _b3.widget == _b2;
            })) {
                var top = _9e.pop(),_b5 = top.wrapper,_b6 = top.iframe,_b7 = top.widget,_b8 = top.onClose;
                if (_b7.onClose) {
                    _b7.onClose();
                }
                dojo.forEach(top.handlers, dojo.disconnect);
                if (!_b7 || !_b7.domNode) {
                    return;
                }
                this.prepare(_b7.domNode);
                _b6.destroy();
                dojo.destroy(_b5);
                if (_b8) {
                    _b8();
                }
            }
        };
    }();
    dijit._frames = new function() {
        var _b9 = [];
        this.pop = function() {
            var _ba;
            if (_b9.length) {
                _ba = _b9.pop();
                _ba.style.display = "";
            } else {
                if (dojo.isIE) {
                    var _bb = dojo.config["dojoBlankHtmlUrl"] || (dojo.moduleUrl("dojo", "resources/blank.html") + "") || "javascript:\"\"";
                    var _bc = "<iframe src='" + _bb + "'" + " style='position: absolute; left: 0px; top: 0px;" + "z-index: -1; filter:Alpha(Opacity=\"0\");'>";
                    _ba = dojo.doc.createElement(_bc);
                } else {
                    _ba = dojo.create("iframe");
                    _ba.src = "javascript:\"\"";
                    _ba.className = "dijitBackgroundIframe";
                }
                _ba.tabIndex = -1;
                dojo.body().appendChild(_ba);
            }
            return _ba;
        };
        this.push = function(_bd) {
            _bd.style.display = "none";
            if (dojo.isIE) {
                _bd.style.removeExpression("width");
                _bd.style.removeExpression("height");
            }
            _b9.push(_bd);
        };
    }();
    dijit.BackgroundIframe = function(_be) {
        if (!_be.id) {
            throw new Error("no id");
        }
        if (dojo.isIE < 7 || (dojo.isFF < 3 && dojo.hasClass(dojo.body(), "dijit_a11y"))) {
            var _bf = dijit._frames.pop();
            _be.appendChild(_bf);
            if (dojo.isIE) {
                _bf.style.setExpression("width", dojo._scopeName + ".doc.getElementById('" + _be.id + "').offsetWidth");
                _bf.style.setExpression("height", dojo._scopeName + ".doc.getElementById('" + _be.id + "').offsetHeight");
            }
            this.iframe = _bf;
        }
    };
    dojo.extend(dijit.BackgroundIframe, {destroy:function() {
        if (this.iframe) {
            dijit._frames.push(this.iframe);
            delete this.iframe;
        }
    }});
}
if (!dojo._hasResource["dijit._base.scroll"]) {
    dojo._hasResource["dijit._base.scroll"] = true;
    dojo.provide("dijit._base.scroll");
    dijit.scrollIntoView = function(_c0) {
        try {
            _c0 = dojo.byId(_c0);
            var doc = dojo.doc;
            var _c2 = dojo.body();
            var _c3 = _c2.parentNode;
            if ((!(dojo.isFF >= 3 || dojo.isIE || dojo.isWebKit) || _c0 == _c2 || _c0 == _c3) && (typeof _c0.scrollIntoView == "function")) {
                _c0.scrollIntoView(false);
                return;
            }
            var ltr = dojo._isBodyLtr();
            var _c5 = dojo.isIE >= 8 && !_c6;
            var rtl = !ltr && !_c5;
            var _c8 = _c2;
            var _c6 = doc.compatMode == "BackCompat";
            if (_c6) {
                _c3._offsetWidth = _c3._clientWidth = _c2._offsetWidth = _c2.clientWidth;
                _c3._offsetHeight = _c3._clientHeight = _c2._offsetHeight = _c2.clientHeight;
            } else {
                if (dojo.isWebKit) {
                    _c2._offsetWidth = _c2._clientWidth = _c3.clientWidth;
                    _c2._offsetHeight = _c2._clientHeight = _c3.clientHeight;
                } else {
                    _c8 = _c3;
                }
                _c3._offsetHeight = _c3.clientHeight;
                _c3._offsetWidth = _c3.clientWidth;
            }
            function _c9(_ca) {
                var ie = dojo.isIE;
                return ((ie <= 6 || (ie >= 7 && _c6)) ? false : (dojo.style(_ca, "position").toLowerCase() == "fixed"));
            }

            ;
            function _cc(_cd) {
                var _ce = _cd.parentNode;
                var _cf = _cd.offsetParent;
                if (_cf == null || _c9(_cd)) {
                    _cf = _c3;
                    _ce = (_cd == _c2) ? _c3 : null;
                }
                _cd._offsetParent = _cf;
                _cd._parent = _ce;
                var bp = dojo._getBorderExtents(_cd);
                _cd._borderStart = {H:(_c5 && !ltr) ? (bp.w - bp.l) : bp.l,V:bp.t};
                _cd._borderSize = {H:bp.w,V:bp.h};
                _cd._scrolledAmount = {H:_cd.scrollLeft,V:_cd.scrollTop};
                _cd._offsetSize = {H:_cd._offsetWidth || _cd.offsetWidth,V:_cd._offsetHeight || _cd.offsetHeight};
                _cd._offsetStart = {H:(_c5 && !ltr) ? _cf.clientWidth - _cd.offsetLeft - _cd._offsetSize.H : _cd.offsetLeft,V:_cd.offsetTop};
                _cd._clientSize = {H:_cd._clientWidth || _cd.clientWidth,V:_cd._clientHeight || _cd.clientHeight};
                if (_cd != _c2 && _cd != _c3 && _cd != _c0) {
                    for (var dir in _cd._offsetSize) {
                        var _d2 = _cd._offsetSize[dir] - _cd._clientSize[dir] - _cd._borderSize[dir];
                        var _d3 = _cd._clientSize[dir] > 0 && _d2 > 0;
                        if (_d3) {
                            _cd._offsetSize[dir] -= _d2;
                            if (dojo.isIE && rtl && dir == "H") {
                                _cd._offsetStart[dir] += _d2;
                            }
                        }
                    }
                }
            }

            ;
            var _d4 = _c0;
            while (_d4 != null) {
                if (_c9(_d4)) {
                    _c0.scrollIntoView(false);
                    return;
                }
                _cc(_d4);
                _d4 = _d4._parent;
            }
            if (dojo.isIE && _c0._parent) {
                var _d5 = _c0._offsetParent;
                _c0._offsetStart.H += _d5._borderStart.H;
                _c0._offsetStart.V += _d5._borderStart.V;
            }
            if (dojo.isIE >= 7 && _c8 == _c3 && rtl && _c2._offsetStart && _c2._offsetStart.H == 0) {
                var _d6 = _c3.scrollWidth - _c3._offsetSize.H;
                if (_d6 > 0) {
                    _c2._offsetStart.H = -_d6;
                }
            }
            if (dojo.isIE <= 6 && !_c6) {
                _c3._offsetSize.H += _c3._borderSize.H;
                _c3._offsetSize.V += _c3._borderSize.V;
            }
            if (rtl && _c2._offsetStart && _c8 == _c3 && _c3._scrolledAmount) {
                var ofs = _c2._offsetStart.H;
                if (ofs < 0) {
                    _c3._scrolledAmount.H += ofs;
                    _c2._offsetStart.H = 0;
                }
            }
            _d4 = _c0;
            while (_d4) {
                var _d8 = _d4._parent;
                if (!_d8) {
                    break;
                }
                if (_d8.tagName == "TD") {
                    var _d9 = _d8._parent._parent._parent;
                    if (_d8 != _d4._offsetParent && _d8._offsetParent != _d4._offsetParent) {
                        _d8 = _d9;
                    }
                }
                var _da = _d4._offsetParent == _d8;
                for (var dir in _d4._offsetStart) {
                    var _dc = dir == "H" ? "V" : "H";
                    if (rtl && dir == "H" && (_d8 != _c3) && (_d8 != _c2) && (dojo.isIE || dojo.isWebKit) && _d8._clientSize.H > 0 && _d8.scrollWidth > _d8._clientSize.H) {
                        var _dd = _d8.scrollWidth - _d8._clientSize.H;
                        if (_dd > 0) {
                            _d8._scrolledAmount.H -= _dd;
                        }
                    }
                    if (_d8._offsetParent.tagName == "TABLE") {
                        if (dojo.isIE) {
                            _d8._offsetStart[dir] -= _d8._offsetParent._borderStart[dir];
                            _d8._borderStart[dir] = _d8._borderSize[dir] = 0;
                        } else {
                            _d8._offsetStart[dir] += _d8._offsetParent._borderStart[dir];
                        }
                    }
                    if (dojo.isIE) {
                        _d8._offsetStart[dir] += _d8._offsetParent._borderStart[dir];
                    }
                    var _de = _d4._offsetStart[dir] - _d8._scrolledAmount[dir] - (_da ? 0 : _d8._offsetStart[dir]) - _d8._borderStart[dir];
                    var _df = _de + _d4._offsetSize[dir] - _d8._offsetSize[dir] + _d8._borderSize[dir];
                    var _e0 = (dir == "H") ? "scrollLeft" : "scrollTop";
                    var _e1 = dir == "H" && rtl;
                    var _e2 = _e1 ? -_df : _de;
                    var _e3 = _e1 ? -_de : _df;
                    var _e4 = (_e2 * _e3 <= 0) ? 0 : Math[(_e2 < 0) ? "max" : "min"](_e2, _e3);
                    if (_e4 != 0) {
                        var _e5 = _d8[_e0];
                        _d8[_e0] += (_e1) ? -_e4 : _e4;
                        var _e6 = _d8[_e0] - _e5;
                    }
                    if (_da) {
                        _d4._offsetStart[dir] += _d8._offsetStart[dir];
                    }
                    _d4._offsetStart[dir] -= _d8[_e0];
                }
                _d4._parent = _d8._parent;
                _d4._offsetParent = _d8._offsetParent;
            }
            _d8 = _c0;
            var _e7;
            while (_d8 && _d8.removeAttribute) {
                _e7 = _d8.parentNode;
                _d8.removeAttribute("_offsetParent");
                _d8.removeAttribute("_parent");
                _d8 = _e7;
            }
        } catch(error) {
            console.error("scrollIntoView: " + error);
            _c0.scrollIntoView(false);
        }
    };
}
if (!dojo._hasResource["dijit._base.sniff"]) {
    dojo._hasResource["dijit._base.sniff"] = true;
    dojo.provide("dijit._base.sniff");
    (function() {
        var d = dojo,_e9 = d.doc.documentElement,ie = d.isIE,_eb = d.isOpera,maj = Math.floor,ff = d.isFF,_ee = d.boxModel.replace(/-/, ""),_ef = {dj_ie:ie,dj_ie6:maj(ie) == 6,dj_ie7:maj(ie) == 7,dj_iequirks:ie && d.isQuirks,dj_opera:_eb,dj_opera8:maj(_eb) == 8,dj_opera9:maj(_eb) == 9,dj_khtml:d.isKhtml,dj_webkit:d.isWebKit,dj_safari:d.isSafari,dj_gecko:d.isMozilla,dj_ff2:maj(ff) == 2,dj_ff3:maj(ff) == 3};
        _ef["dj_" + _ee] = true;
        for (var p in _ef) {
            if (_ef[p]) {
                if (_e9.className) {
                    _e9.className += " " + p;
                } else {
                    _e9.className = p;
                }
            }
        }
        dojo._loaders.unshift(function() {
            if (!dojo._isBodyLtr()) {
                _e9.className += " dijitRtl";
                for (var p in _ef) {
                    if (_ef[p]) {
                        _e9.className += " " + p + "-rtl";
                    }
                }
            }
        });
    })();
}
if (!dojo._hasResource["dijit._base.typematic"]) {
    dojo._hasResource["dijit._base.typematic"] = true;
    dojo.provide("dijit._base.typematic");
    dijit.typematic = {_fireEventAndReload:function() {
        this._timer = null;
        this._callback(++this._count, this._node, this._evt);
        this._currentTimeout = (this._currentTimeout < 0) ? this._initialDelay : ((this._subsequentDelay > 1) ? this._subsequentDelay : Math.round(this._currentTimeout * this._subsequentDelay));
        this._timer = setTimeout(dojo.hitch(this, "_fireEventAndReload"), this._currentTimeout);
    },trigger:function(evt, _f3, _f4, _f5, obj, _f7, _f8) {
        if (obj != this._obj) {
            this.stop();
            this._initialDelay = _f8 || 500;
            this._subsequentDelay = _f7 || 0.9;
            this._obj = obj;
            this._evt = evt;
            this._node = _f4;
            this._currentTimeout = -1;
            this._count = -1;
            this._callback = dojo.hitch(_f3, _f5);
            this._fireEventAndReload();
        }
    },stop:function() {
        if (this._timer) {
            clearTimeout(this._timer);
            this._timer = null;
        }
        if (this._obj) {
            this._callback(-1, this._node, this._evt);
            this._obj = null;
        }
    },addKeyListener:function(_f9, _fa, _fb, _fc, _fd, _fe) {
        if (_fa.keyCode) {
            _fa.charOrCode = _fa.keyCode;
            dojo.deprecated("keyCode attribute parameter for dijit.typematic.addKeyListener is deprecated. Use charOrCode instead.", "", "2.0");
        } else {
            if (_fa.charCode) {
                _fa.charOrCode = String.fromCharCode(_fa.charCode);
                dojo.deprecated("charCode attribute parameter for dijit.typematic.addKeyListener is deprecated. Use charOrCode instead.", "", "2.0");
            }
        }
        return [dojo.connect(_f9, "onkeypress", this, function(evt) {
            if (evt.charOrCode == _fa.charOrCode && (_fa.ctrlKey === undefined || _fa.ctrlKey == evt.ctrlKey) && (_fa.altKey === undefined || _fa.altKey == evt.ctrlKey) && (_fa.shiftKey === undefined || _fa.shiftKey == evt.ctrlKey)) {
                dojo.stopEvent(evt);
                dijit.typematic.trigger(_fa, _fb, _f9, _fc, _fa, _fd, _fe);
            } else {
                if (dijit.typematic._obj == _fa) {
                    dijit.typematic.stop();
                }
            }
        }),dojo.connect(_f9, "onkeyup", this, function(evt) {
            if (dijit.typematic._obj == _fa) {
                dijit.typematic.stop();
            }
        })];
    },addMouseListener:function(node, _102, _103, _104, _105) {
        var dc = dojo.connect;
        return [dc(node, "mousedown", this, function(evt) {
            dojo.stopEvent(evt);
            dijit.typematic.trigger(evt, _102, node, _103, node, _104, _105);
        }),dc(node, "mouseup", this, function(evt) {
            dojo.stopEvent(evt);
            dijit.typematic.stop();
        }),dc(node, "mouseout", this, function(evt) {
            dojo.stopEvent(evt);
            dijit.typematic.stop();
        }),dc(node, "mousemove", this, function(evt) {
            dojo.stopEvent(evt);
        }),dc(node, "dblclick", this, function(evt) {
            dojo.stopEvent(evt);
            if (dojo.isIE) {
                dijit.typematic.trigger(evt, _102, node, _103, node, _104, _105);
                setTimeout(dojo.hitch(this, dijit.typematic.stop), 50);
            }
        })];
    },addListener:function(_10c, _10d, _10e, _10f, _110, _111, _112) {
        return this.addKeyListener(_10d, _10e, _10f, _110, _111, _112).concat(this.addMouseListener(_10c, _10f, _110, _111, _112));
    }};
}
if (!dojo._hasResource["dijit._base.wai"]) {
    dojo._hasResource["dijit._base.wai"] = true;
    dojo.provide("dijit._base.wai");
    dijit.wai = {onload:function() {
        var div = dojo.create("div", {id:"a11yTestNode",style:{cssText:"border: 1px solid;" + "border-color:red green;" + "position: absolute;" + "height: 5px;" + "top: -999px;" + "background-image: url(\"" + (dojo.config.blankGif || dojo.moduleUrl("dojo", "resources/blank.gif")) + "\");"}}, dojo.body());
        var cs = dojo.getComputedStyle(div);
        if (cs) {
            var _115 = cs.backgroundImage;
            var _116 = (cs.borderTopColor == cs.borderRightColor) || (_115 != null && (_115 == "none" || _115 == "url(invalid-url:)"));
            dojo[_116 ? "addClass" : "removeClass"](dojo.body(), "dijit_a11y");
            if (dojo.isIE) {
                div.outerHTML = "";
            } else {
                dojo.body().removeChild(div);
            }
        }
    }};
    if (dojo.isIE || dojo.isMoz) {
        dojo._loaders.unshift(dijit.wai.onload);
    }
    dojo.mixin(dijit, {_XhtmlRoles:/banner|contentinfo|definition|main|navigation|search|note|secondary|seealso/,hasWaiRole:function(elem, role) {
        var _119 = this.getWaiRole(elem);
        return role ? (_119.indexOf(role) > -1) : (_119.length > 0);
    },getWaiRole:function(elem) {
        return dojo.trim((dojo.attr(elem, "role") || "").replace(this._XhtmlRoles, "").replace("wairole:", ""));
    },setWaiRole:function(elem, role) {
        var _11d = dojo.attr(elem, "role") || "";
        if (dojo.isFF < 3 || !this._XhtmlRoles.test(_11d)) {
            dojo.attr(elem, "role", dojo.isFF < 3 ? "wairole:" + role : role);
        } else {
            if ((" " + _11d + " ").indexOf(" " + role + " ") < 0) {
                var _11e = dojo.trim(_11d.replace(this._XhtmlRoles, ""));
                var _11f = dojo.trim(_11d.replace(_11e, ""));
                dojo.attr(elem, "role", _11f + (_11f ? " " : "") + role);
            }
        }
    },removeWaiRole:function(elem, role) {
        var _122 = dojo.attr(elem, "role");
        if (!_122) {
            return;
        }
        if (role) {
            var _123 = dojo.isFF < 3 ? "wairole:" + role : role;
            var t = dojo.trim((" " + _122 + " ").replace(" " + _123 + " ", " "));
            dojo.attr(elem, "role", t);
        } else {
            elem.removeAttribute("role");
        }
    },hasWaiState:function(elem, _126) {
        if (dojo.isFF < 3) {
            return elem.hasAttributeNS("http://www.w3.org/2005/07/aaa", _126);
        }
        return elem.hasAttribute ? elem.hasAttribute("aria-" + _126) : !!elem.getAttribute("aria-" + _126);
    },getWaiState:function(elem, _128) {
        if (dojo.isFF < 3) {
            return elem.getAttributeNS("http://www.w3.org/2005/07/aaa", _128);
        }
        return elem.getAttribute("aria-" + _128) || "";
    },setWaiState:function(elem, _12a, _12b) {
        if (dojo.isFF < 3) {
            elem.setAttributeNS("http://www.w3.org/2005/07/aaa", "aaa:" + _12a, _12b);
        } else {
            elem.setAttribute("aria-" + _12a, _12b);
        }
    },removeWaiState:function(elem, _12d) {
        if (dojo.isFF < 3) {
            elem.removeAttributeNS("http://www.w3.org/2005/07/aaa", _12d);
        } else {
            elem.removeAttribute("aria-" + _12d);
        }
    }});
}
if (!dojo._hasResource["dijit._base"]) {
    dojo._hasResource["dijit._base"] = true;
    dojo.provide("dijit._base");
}
if (!dojo._hasResource["dijit._Widget"]) {
    dojo._hasResource["dijit._Widget"] = true;
    dojo.provide("dijit._Widget");
    dojo.require("dijit._base");
    dojo.connect(dojo, "connect", function(_12e, _12f) {
        if (_12e && dojo.isFunction(_12e._onConnect)) {
            _12e._onConnect(_12f);
        }
    });
    dijit._connectOnUseEventHandler = function(_130) {
    };
    (function() {
        var _131 = {};
        var _132 = function(dc) {
            if (!_131[dc]) {
                var r = [];
                var _135;
                var _136 = dojo.getObject(dc).prototype;
                for (var _137 in _136) {
                    if (dojo.isFunction(_136[_137]) && (_135 = _137.match(/^_set([a-zA-Z]*)Attr$/)) && _135[1]) {
                        r.push(_135[1].charAt(0).toLowerCase() + _135[1].substr(1));
                    }
                }
                _131[dc] = r;
            }
            return _131[dc] || [];
        };
        dojo.declare("dijit._Widget", null, {id:"",lang:"",dir:"","class":"",style:"",title:"",srcNodeRef:null,domNode:null,containerNode:null,attributeMap:{id:"",dir:"",lang:"","class":"",style:"",title:""},_deferredConnects:{onClick:"",onDblClick:"",onKeyDown:"",onKeyPress:"",onKeyUp:"",onMouseMove:"",onMouseDown:"",onMouseOut:"",onMouseOver:"",onMouseLeave:"",onMouseEnter:"",onMouseUp:""},onClick:dijit._connectOnUseEventHandler,onDblClick:dijit._connectOnUseEventHandler,onKeyDown:dijit._connectOnUseEventHandler,onKeyPress:dijit._connectOnUseEventHandler,onKeyUp:dijit._connectOnUseEventHandler,onMouseDown:dijit._connectOnUseEventHandler,onMouseMove:dijit._connectOnUseEventHandler,onMouseOut:dijit._connectOnUseEventHandler,onMouseOver:dijit._connectOnUseEventHandler,onMouseLeave:dijit._connectOnUseEventHandler,onMouseEnter:dijit._connectOnUseEventHandler,onMouseUp:dijit._connectOnUseEventHandler,_blankGif:(dojo.config.blankGif || dojo.moduleUrl("dojo", "resources/blank.gif")),postscript:function(_138, _139) {
            this.create(_138, _139);
        },create:function(_13a, _13b) {
            this.srcNodeRef = dojo.byId(_13b);
            this._connects = [];
            this._deferredConnects = dojo.clone(this._deferredConnects);
            for (var attr in this.attributeMap) {
                delete this._deferredConnects[attr];
            }
            for (attr in this._deferredConnects) {
                if (this[attr] !== dijit._connectOnUseEventHandler) {
                    delete this._deferredConnects[attr];
                }
            }
            if (this.srcNodeRef && (typeof this.srcNodeRef.id == "string")) {
                this.id = this.srcNodeRef.id;
            }
            if (_13a) {
                this.params = _13a;
                dojo.mixin(this, _13a);
            }
            this.postMixInProperties();
            if (!this.id) {
                this.id = dijit.getUniqueId(this.declaredClass.replace(/\./g, "_"));
            }
            dijit.registry.add(this);
            this.buildRendering();
            if (this.domNode) {
                this._applyAttributes();
                var _13d = this.srcNodeRef;
                if (_13d && _13d.parentNode) {
                    _13d.parentNode.replaceChild(this.domNode, _13d);
                }
                for (attr in this.params) {
                    this._onConnect(attr);
                }
            }
            if (this.domNode) {
                this.domNode.setAttribute("widgetId", this.id);
            }
            this.postCreate();
            if (this.srcNodeRef && !this.srcNodeRef.parentNode) {
                delete this.srcNodeRef;
            }
            this._created = true;
        },_applyAttributes:function() {
            var _13e = function(attr, _140) {
                if ((_140.params && attr in _140.params) || _140[attr]) {
                    _140.attr(attr, _140[attr]);
                }
            };
            for (var attr in this.attributeMap) {
                _13e(attr, this);
            }
            dojo.forEach(_132(this.declaredClass), function(a) {
                if (!(a in this.attributeMap)) {
                    _13e(a, this);
                }
            }, this);
        },postMixInProperties:function() {
        },buildRendering:function() {
            this.domNode = this.srcNodeRef || dojo.create("div");
        },postCreate:function() {
        },startup:function() {
            this._started = true;
        },destroyRecursive:function(_143) {
            this.destroyDescendants(_143);
            this.destroy(_143);
        },destroy:function(_144) {
            this.uninitialize();
            dojo.forEach(this._connects, function(_145) {
                dojo.forEach(_145, dojo.disconnect);
            });
            dojo.forEach(this._supportingWidgets || [], function(w) {
                if (w.destroy) {
                    w.destroy();
                }
            });
            this.destroyRendering(_144);
            dijit.registry.remove(this.id);
        },destroyRendering:function(_147) {
            if (this.bgIframe) {
                this.bgIframe.destroy(_147);
                delete this.bgIframe;
            }
            if (this.domNode) {
                if (_147) {
                    dojo.removeAttr(this.domNode, "widgetId");
                } else {
                    dojo.destroy(this.domNode);
                }
                delete this.domNode;
            }
            if (this.srcNodeRef) {
                if (!_147) {
                    dojo.destroy(this.srcNodeRef);
                }
                delete this.srcNodeRef;
            }
        },destroyDescendants:function(_148) {
            dojo.forEach(this.getChildren(), function(_149) {
                if (_149.destroyRecursive) {
                    _149.destroyRecursive(_148);
                }
            });
        },uninitialize:function() {
            return false;
        },onFocus:function() {
        },onBlur:function() {
        },_onFocus:function(e) {
            this.onFocus();
        },_onBlur:function() {
            this.onBlur();
        },_onConnect:function(_14b) {
            if (_14b in this._deferredConnects) {
                var _14c = this[this._deferredConnects[_14b] || "domNode"];
                this.connect(_14c, _14b.toLowerCase(), _14b);
                delete this._deferredConnects[_14b];
            }
        },_setClassAttr:function(_14d) {
            var _14e = this[this.attributeMap["class"] || "domNode"];
            dojo.removeClass(_14e, this["class"]);
            this["class"] = _14d;
            dojo.addClass(_14e, _14d);
        },_setStyleAttr:function(_14f) {
            var _150 = this[this.attributeMap["style"] || "domNode"];
            if (dojo.isObject(_14f)) {
                dojo.style(_150, _14f);
            } else {
                if (_150.style.cssText) {
                    _150.style.cssText += "; " + _14f;
                } else {
                    _150.style.cssText = _14f;
                }
            }
            this["style"] = _14f;
        },setAttribute:function(attr, _152) {
            dojo.deprecated(this.declaredClass + "::setAttribute() is deprecated. Use attr() instead.", "", "2.0");
            this.attr(attr, _152);
        },_attrToDom:function(attr, _154) {
            var _155 = this.attributeMap[attr];
            dojo.forEach(dojo.isArray(_155) ? _155 : [_155], function(_156) {
                var _157 = this[_156.node || _156 || "domNode"];
                var type = _156.type || "attribute";
                switch (type) {case "attribute":if (dojo.isFunction(_154)) {
                    _154 = dojo.hitch(this, _154);
                }if (/^on[A-Z][a-zA-Z]*$/.test(attr)) {
                    attr = attr.toLowerCase();
                }dojo.attr(_157, attr, _154);break;case "innerHTML":_157.innerHTML = _154;break;case "class":dojo.removeClass(_157, this[attr]);dojo.addClass(_157, _154);break;}
            }, this);
            this[attr] = _154;
        },attr:function(name, _15a) {
            var args = arguments.length;
            if (args == 1 && !dojo.isString(name)) {
                for (var x in name) {
                    this.attr(x, name[x]);
                }
                return this;
            }
            var _15d = this._getAttrNames(name);
            if (args == 2) {
                if (this[_15d.s]) {
                    return this[_15d.s](_15a) || this;
                } else {
                    if (name in this.attributeMap) {
                        this._attrToDom(name, _15a);
                    }
                    this[name] = _15a;
                }
                return this;
            } else {
                if (this[_15d.g]) {
                    return this[_15d.g]();
                } else {
                    return this[name];
                }
            }
        },_attrPairNames:{},_getAttrNames:function(name) {
            var apn = this._attrPairNames;
            if (apn[name]) {
                return apn[name];
            }
            var uc = name.charAt(0).toUpperCase() + name.substr(1);
            return apn[name] = {n:name + "Node",s:"_set" + uc + "Attr",g:"_get" + uc + "Attr"};
        },toString:function() {
            return "[Widget " + this.declaredClass + ", " + (this.id || "NO ID") + "]";
        },getDescendants:function() {
            if (this.containerNode) {
                var list = dojo.query("[widgetId]", this.containerNode);
                return list.map(dijit.byNode);
            } else {
                return [];
            }
        },getChildren:function() {
            if (this.containerNode) {
                return dijit.findWidgets(this.containerNode);
            } else {
                return [];
            }
        },nodesWithKeyClick:["input","button"],connect:function(obj, _163, _164) {
            var d = dojo;
            var dc = dojo.connect;
            var _167 = [];
            if (_163 == "ondijitclick") {
                if (!this.nodesWithKeyClick[obj.nodeName]) {
                    var m = d.hitch(this, _164);
                    _167.push(dc(obj, "onkeydown", this, function(e) {
                        if (!d.isFF && e.keyCode == d.keys.ENTER && !e.ctrlKey && !e.shiftKey && !e.altKey && !e.metaKey) {
                            return m(e);
                        } else {
                            if (e.keyCode == d.keys.SPACE) {
                                d.stopEvent(e);
                            }
                        }
                    }), dc(obj, "onkeyup", this, function(e) {
                        if (e.keyCode == d.keys.SPACE && !e.ctrlKey && !e.shiftKey && !e.altKey && !e.metaKey) {
                            return m(e);
                        }
                    }));
                    if (d.isFF) {
                        _167.push(dc(obj, "onkeypress", this, function(e) {
                            if (e.keyCode == d.keys.ENTER && !e.ctrlKey && !e.shiftKey && !e.altKey && !e.metaKey) {
                                return m(e);
                            }
                        }));
                    }
                }
                _163 = "onclick";
            }
            _167.push(dc(obj, _163, this, _164));
            this._connects.push(_167);
            return _167;
        },disconnect:function(_16c) {
            for (var i = 0; i < this._connects.length; i++) {
                if (this._connects[i] == _16c) {
                    dojo.forEach(_16c, dojo.disconnect);
                    this._connects.splice(i, 1);
                    return;
                }
            }
        },isLeftToRight:function() {
            return dojo._isBodyLtr();
        },isFocusable:function() {
            return this.focus && (dojo.style(this.domNode, "display") != "none");
        },placeAt:function(_16e, _16f) {
            if (_16e["declaredClass"] && _16e["addChild"]) {
                _16e.addChild(this, _16f);
            } else {
                dojo.place(this.domNode, _16e, _16f);
            }
            return this;
        }});
    })();
}
if (!dojo._hasResource["dojo.dnd.common"]) {
    dojo._hasResource["dojo.dnd.common"] = true;
    dojo.provide("dojo.dnd.common");
    dojo.dnd._isMac = navigator.appVersion.indexOf("Macintosh") >= 0;
    dojo.dnd._copyKey = dojo.dnd._isMac ? "metaKey" : "ctrlKey";
    dojo.dnd.getCopyKeyState = function(e) {
        return e[dojo.dnd._copyKey];
    };
    dojo.dnd._uniqueId = 0;
    dojo.dnd.getUniqueId = function() {
        var id;
        do{
            id = dojo._scopeName + "Unique" + (++dojo.dnd._uniqueId);
        } while (dojo.byId(id));
        return id;
    };
    dojo.dnd._empty = {};
    dojo.dnd.isFormElement = function(e) {
        var t = e.target;
        if (t.nodeType == 3) {
            t = t.parentNode;
        }
        return " button textarea input select option ".indexOf(" " + t.tagName.toLowerCase() + " ") >= 0;
    };
    dojo.dnd._lmb = dojo.isIE ? 1 : 0;
    dojo.dnd._isLmbPressed = dojo.isIE ? function(e) {
        return e.button & 1;
    } : function(e) {
        return e.button === 0;
    };
}
if (!dojo._hasResource["dojo.dnd.autoscroll"]) {
    dojo._hasResource["dojo.dnd.autoscroll"] = true;
    dojo.provide("dojo.dnd.autoscroll");
    dojo.dnd.getViewport = function() {
        var d = dojo.doc,dd = d.documentElement,w = window,b = dojo.body();
        if (dojo.isMozilla) {
            return {w:dd.clientWidth,h:w.innerHeight};
        } else {
            if (!dojo.isOpera && w.innerWidth) {
                return {w:w.innerWidth,h:w.innerHeight};
            } else {
                if (!dojo.isOpera && dd && dd.clientWidth) {
                    return {w:dd.clientWidth,h:dd.clientHeight};
                } else {
                    if (b.clientWidth) {
                        return {w:b.clientWidth,h:b.clientHeight};
                    }
                }
            }
        }
        return null;
    };
    dojo.dnd.V_TRIGGER_AUTOSCROLL = 32;
    dojo.dnd.H_TRIGGER_AUTOSCROLL = 32;
    dojo.dnd.V_AUTOSCROLL_VALUE = 16;
    dojo.dnd.H_AUTOSCROLL_VALUE = 16;
    dojo.dnd.autoScroll = function(e) {
        var v = dojo.dnd.getViewport(),dx = 0,dy = 0;
        if (e.clientX < dojo.dnd.H_TRIGGER_AUTOSCROLL) {
            dx = -dojo.dnd.H_AUTOSCROLL_VALUE;
        } else {
            if (e.clientX > v.w - dojo.dnd.H_TRIGGER_AUTOSCROLL) {
                dx = dojo.dnd.H_AUTOSCROLL_VALUE;
            }
        }
        if (e.clientY < dojo.dnd.V_TRIGGER_AUTOSCROLL) {
            dy = -dojo.dnd.V_AUTOSCROLL_VALUE;
        } else {
            if (e.clientY > v.h - dojo.dnd.V_TRIGGER_AUTOSCROLL) {
                dy = dojo.dnd.V_AUTOSCROLL_VALUE;
            }
        }
        window.scrollBy(dx, dy);
    };
    dojo.dnd._validNodes = {"div":1,"p":1,"td":1};
    dojo.dnd._validOverflow = {"auto":1,"scroll":1};
    dojo.dnd.autoScrollNodes = function(e) {
        for (var n = e.target; n;) {
            if (n.nodeType == 1 && (n.tagName.toLowerCase() in dojo.dnd._validNodes)) {
                var s = dojo.getComputedStyle(n);
                if (s.overflow.toLowerCase() in dojo.dnd._validOverflow) {
                    var b = dojo._getContentBox(n, s),t = dojo._abs(n, true);
                    var w = Math.min(dojo.dnd.H_TRIGGER_AUTOSCROLL, b.w / 2),h = Math.min(dojo.dnd.V_TRIGGER_AUTOSCROLL, b.h / 2),rx = e.pageX - t.x,ry = e.pageY - t.y,dx = 0,dy = 0;
                    if (dojo.isWebKit || dojo.isOpera) {
                        rx += dojo.body().scrollLeft,ry += dojo.body().scrollTop;
                    }
                    if (rx > 0 && rx < b.w) {
                        if (rx < w) {
                            dx = -w;
                        } else {
                            if (rx > b.w - w) {
                                dx = w;
                            }
                        }
                    }
                    if (ry > 0 && ry < b.h) {
                        if (ry < h) {
                            dy = -h;
                        } else {
                            if (ry > b.h - h) {
                                dy = h;
                            }
                        }
                    }
                    var _189 = n.scrollLeft,_18a = n.scrollTop;
                    n.scrollLeft = n.scrollLeft + dx;
                    n.scrollTop = n.scrollTop + dy;
                    if (_189 != n.scrollLeft || _18a != n.scrollTop) {
                        return;
                    }
                }
            }
            try {
                n = n.parentNode;
            } catch(x) {
                n = null;
            }
        }
        dojo.dnd.autoScroll(e);
    };
}
if (!dojo._hasResource["dojo.dnd.Mover"]) {
    dojo._hasResource["dojo.dnd.Mover"] = true;
    dojo.provide("dojo.dnd.Mover");
    dojo.declare("dojo.dnd.Mover", null, {constructor:function(node, e, host) {
        this.node = dojo.byId(node);
        this.marginBox = {l:e.pageX,t:e.pageY};
        this.mouseButton = e.button;
        var h = this.host = host,d = node.ownerDocument,_190 = dojo.connect(d, "onmousemove", this, "onFirstMove");
        this.events = [dojo.connect(d, "onmousemove", this, "onMouseMove"),dojo.connect(d, "onmouseup", this, "onMouseUp"),dojo.connect(d, "ondragstart", dojo.stopEvent),dojo.connect(d.body, "onselectstart", dojo.stopEvent),_190];
        if (h && h.onMoveStart) {
            h.onMoveStart(this);
        }
    },onMouseMove:function(e) {
        dojo.dnd.autoScroll(e);
        var m = this.marginBox;
        this.host.onMove(this, {l:m.l + e.pageX,t:m.t + e.pageY});
        dojo.stopEvent(e);
    },onMouseUp:function(e) {
        if (dojo.isWebKit && dojo.dnd._isMac && this.mouseButton == 2 ? e.button == 0 : this.mouseButton == e.button) {
            this.destroy();
        }
        dojo.stopEvent(e);
    },onFirstMove:function() {
        var s = this.node.style,l,t,h = this.host;
        switch (s.position) {case "relative":case "absolute":l = Math.round(parseFloat(s.left));t = Math.round(parseFloat(s.top));break;default:s.position = "absolute";var m = dojo.marginBox(this.node);var b = dojo.doc.body;var bs = dojo.getComputedStyle(b);var bm = dojo._getMarginBox(b, bs);var bc = dojo._getContentBox(b, bs);l = m.l - (bc.l - bm.l);t = m.t - (bc.t - bm.t);break;}
        this.marginBox.l = l - this.marginBox.l;
        this.marginBox.t = t - this.marginBox.t;
        if (h && h.onFirstMove) {
            h.onFirstMove(this);
        }
        dojo.disconnect(this.events.pop());
    },destroy:function() {
        dojo.forEach(this.events, dojo.disconnect);
        var h = this.host;
        if (h && h.onMoveStop) {
            h.onMoveStop(this);
        }
        this.events = this.node = this.host = null;
    }});
}
if (!dojo._hasResource["dojo.dnd.Moveable"]) {
    dojo._hasResource["dojo.dnd.Moveable"] = true;
    dojo.provide("dojo.dnd.Moveable");
    dojo.declare("dojo.dnd.Moveable", null, {handle:"",delay:0,skip:false,constructor:function(node, _19f) {
        this.node = dojo.byId(node);
        if (!_19f) {
            _19f = {};
        }
        this.handle = _19f.handle ? dojo.byId(_19f.handle) : null;
        if (!this.handle) {
            this.handle = this.node;
        }
        this.delay = _19f.delay > 0 ? _19f.delay : 0;
        this.skip = _19f.skip;
        this.mover = _19f.mover ? _19f.mover : dojo.dnd.Mover;
        this.events = [dojo.connect(this.handle, "onmousedown", this, "onMouseDown"),dojo.connect(this.handle, "ondragstart", this, "onSelectStart"),dojo.connect(this.handle, "onselectstart", this, "onSelectStart")];
    },markupFactory:function(_1a0, node) {
        return new dojo.dnd.Moveable(node, _1a0);
    },destroy:function() {
        dojo.forEach(this.events, dojo.disconnect);
        this.events = this.node = this.handle = null;
    },onMouseDown:function(e) {
        if (this.skip && dojo.dnd.isFormElement(e)) {
            return;
        }
        if (this.delay) {
            this.events.push(dojo.connect(this.handle, "onmousemove", this, "onMouseMove"), dojo.connect(this.handle, "onmouseup", this, "onMouseUp"));
            this._lastX = e.pageX;
            this._lastY = e.pageY;
        } else {
            this.onDragDetected(e);
        }
        dojo.stopEvent(e);
    },onMouseMove:function(e) {
        if (Math.abs(e.pageX - this._lastX) > this.delay || Math.abs(e.pageY - this._lastY) > this.delay) {
            this.onMouseUp(e);
            this.onDragDetected(e);
        }
        dojo.stopEvent(e);
    },onMouseUp:function(e) {
        for (var i = 0; i < 2; ++i) {
            dojo.disconnect(this.events.pop());
        }
        dojo.stopEvent(e);
    },onSelectStart:function(e) {
        if (!this.skip || !dojo.dnd.isFormElement(e)) {
            dojo.stopEvent(e);
        }
    },onDragDetected:function(e) {
        new this.mover(this.node, e, this);
    },onMoveStart:function(_1a8) {
        dojo.publish("/dnd/move/start", [_1a8]);
        dojo.addClass(dojo.body(), "dojoMove");
        dojo.addClass(this.node, "dojoMoveItem");
    },onMoveStop:function(_1a9) {
        dojo.publish("/dnd/move/stop", [_1a9]);
        dojo.removeClass(dojo.body(), "dojoMove");
        dojo.removeClass(this.node, "dojoMoveItem");
    },onFirstMove:function(_1aa) {
    },onMove:function(_1ab, _1ac) {
        this.onMoving(_1ab, _1ac);
        var s = _1ab.node.style;
        s.left = _1ac.l + "px";
        s.top = _1ac.t + "px";
        this.onMoved(_1ab, _1ac);
    },onMoving:function(_1ae, _1af) {
    },onMoved:function(_1b0, _1b1) {
    }});
}
if (!dojo._hasResource["dojo.dnd.move"]) {
    dojo._hasResource["dojo.dnd.move"] = true;
    dojo.provide("dojo.dnd.move");
    dojo.declare("dojo.dnd.move.constrainedMoveable", dojo.dnd.Moveable, {constraints:function() {
    },within:false,markupFactory:function(_1b2, node) {
        return new dojo.dnd.move.constrainedMoveable(node, _1b2);
    },constructor:function(node, _1b5) {
        if (!_1b5) {
            _1b5 = {};
        }
        this.constraints = _1b5.constraints;
        this.within = _1b5.within;
    },onFirstMove:function(_1b6) {
        var c = this.constraintBox = this.constraints.call(this, _1b6);
        c.r = c.l + c.w;
        c.b = c.t + c.h;
        if (this.within) {
            var mb = dojo.marginBox(_1b6.node);
            c.r -= mb.w;
            c.b -= mb.h;
        }
    },onMove:function(_1b9, _1ba) {
        var c = this.constraintBox,s = _1b9.node.style;
        s.left = (_1ba.l < c.l ? c.l : c.r < _1ba.l ? c.r : _1ba.l) + "px";
        s.top = (_1ba.t < c.t ? c.t : c.b < _1ba.t ? c.b : _1ba.t) + "px";
    }});
    dojo.declare("dojo.dnd.move.boxConstrainedMoveable", dojo.dnd.move.constrainedMoveable, {box:{},markupFactory:function(_1bd, node) {
        return new dojo.dnd.move.boxConstrainedMoveable(node, _1bd);
    },constructor:function(node, _1c0) {
        var box = _1c0 && _1c0.box;
        this.constraints = function() {
            return box;
        };
    }});
    dojo.declare("dojo.dnd.move.parentConstrainedMoveable", dojo.dnd.move.constrainedMoveable, {area:"content",markupFactory:function(_1c2, node) {
        return new dojo.dnd.move.parentConstrainedMoveable(node, _1c2);
    },constructor:function(node, _1c5) {
        var area = _1c5 && _1c5.area;
        this.constraints = function() {
            var n = this.node.parentNode,s = dojo.getComputedStyle(n),mb = dojo._getMarginBox(n, s);
            if (area == "margin") {
                return mb;
            }
            var t = dojo._getMarginExtents(n, s);
            mb.l += t.l,mb.t += t.t,mb.w -= t.w,mb.h -= t.h;
            if (area == "border") {
                return mb;
            }
            t = dojo._getBorderExtents(n, s);
            mb.l += t.l,mb.t += t.t,mb.w -= t.w,mb.h -= t.h;
            if (area == "padding") {
                return mb;
            }
            t = dojo._getPadExtents(n, s);
            mb.l += t.l,mb.t += t.t,mb.w -= t.w,mb.h -= t.h;
            return mb;
        };
    }});
    dojo.dnd.move.constrainedMover = function(fun, _1cc) {
        dojo.deprecated("dojo.dnd.move.constrainedMover, use dojo.dnd.move.constrainedMoveable instead");
        var _1cd = function(node, e, _1d0) {
            dojo.dnd.Mover.call(this, node, e, _1d0);
        };
        dojo.extend(_1cd, dojo.dnd.Mover.prototype);
        dojo.extend(_1cd, {onMouseMove:function(e) {
            dojo.dnd.autoScroll(e);
            var m = this.marginBox,c = this.constraintBox,l = m.l + e.pageX,t = m.t + e.pageY;
            l = l < c.l ? c.l : c.r < l ? c.r : l;
            t = t < c.t ? c.t : c.b < t ? c.b : t;
            this.host.onMove(this, {l:l,t:t});
        },onFirstMove:function() {
            dojo.dnd.Mover.prototype.onFirstMove.call(this);
            var c = this.constraintBox = fun.call(this);
            c.r = c.l + c.w;
            c.b = c.t + c.h;
            if (_1cc) {
                var mb = dojo.marginBox(this.node);
                c.r -= mb.w;
                c.b -= mb.h;
            }
        }});
        return _1cd;
    };
    dojo.dnd.move.boxConstrainedMover = function(box, _1d9) {
        dojo.deprecated("dojo.dnd.move.boxConstrainedMover, use dojo.dnd.move.boxConstrainedMoveable instead");
        return dojo.dnd.move.constrainedMover(function() {
            return box;
        }, _1d9);
    };
    dojo.dnd.move.parentConstrainedMover = function(area, _1db) {
        dojo.deprecated("dojo.dnd.move.parentConstrainedMover, use dojo.dnd.move.parentConstrainedMoveable instead");
        var fun = function() {
            var n = this.node.parentNode,s = dojo.getComputedStyle(n),mb = dojo._getMarginBox(n, s);
            if (area == "margin") {
                return mb;
            }
            var t = dojo._getMarginExtents(n, s);
            mb.l += t.l,mb.t += t.t,mb.w -= t.w,mb.h -= t.h;
            if (area == "border") {
                return mb;
            }
            t = dojo._getBorderExtents(n, s);
            mb.l += t.l,mb.t += t.t,mb.w -= t.w,mb.h -= t.h;
            if (area == "padding") {
                return mb;
            }
            t = dojo._getPadExtents(n, s);
            mb.l += t.l,mb.t += t.t,mb.w -= t.w,mb.h -= t.h;
            return mb;
        };
        return dojo.dnd.move.constrainedMover(fun, _1db);
    };
    dojo.dnd.constrainedMover = dojo.dnd.move.constrainedMover;
    dojo.dnd.boxConstrainedMover = dojo.dnd.move.boxConstrainedMover;
    dojo.dnd.parentConstrainedMover = dojo.dnd.move.parentConstrainedMover;
}
if (!dojo._hasResource["dojo.string"]) {
    dojo._hasResource["dojo.string"] = true;
    dojo.provide("dojo.string");
    dojo.string.rep = function(str, num) {
        if (num <= 0 || !str) {
            return "";
        }
        var buf = [];
        for (; ;) {
            if (num & 1) {
                buf.push(str);
            }
            if (!(num >>= 1)) {
                break;
            }
            str += str;
        }
        return buf.join("");
    };
    dojo.string.pad = function(text, size, ch, end) {
        if (!ch) {
            ch = "0";
        }
        var out = String(text),pad = dojo.string.rep(ch, Math.ceil((size - out.length) / ch.length));
        return end ? out + pad : pad + out;
    };
    dojo.string.substitute = function(_1ea, map, _1ec, _1ed) {
        _1ed = _1ed || dojo.global;
        _1ec = (!_1ec) ? function(v) {
            return v;
        } : dojo.hitch(_1ed, _1ec);
        return _1ea.replace(/\$\{([^\s\:\}]+)(?:\:([^\s\:\}]+))?\}/g, function(_1ef, key, _1f1) {
            var _1f2 = dojo.getObject(key, false, map);
            if (_1f1) {
                _1f2 = dojo.getObject(_1f1, false, _1ed).call(_1ed, _1f2, key);
            }
            return _1ec(_1f2, key).toString();
        });
    };
    dojo.string.trim = String.prototype.trim ? dojo.trim : function(str) {
        str = str.replace(/^\s+/, "");
        for (var i = str.length - 1; i >= 0; i--) {
            if (/\S/.test(str.charAt(i))) {
                str = str.substring(0, i + 1);
                break;
            }
        }
        return str;
    };
}
if (!dojo._hasResource["dojo.date.stamp"]) {
    dojo._hasResource["dojo.date.stamp"] = true;
    dojo.provide("dojo.date.stamp");
    dojo.date.stamp.fromISOString = function(_1f5, _1f6) {
        if (!dojo.date.stamp._isoRegExp) {
            dojo.date.stamp._isoRegExp = /^(?:(\d{4})(?:-(\d{2})(?:-(\d{2}))?)?)?(?:T(\d{2}):(\d{2})(?::(\d{2})(.\d+)?)?((?:[+-](\d{2}):(\d{2}))|Z)?)?$/;
        }
        var _1f7 = dojo.date.stamp._isoRegExp.exec(_1f5);
        var _1f8 = null;
        if (_1f7) {
            _1f7.shift();
            if (_1f7[1]) {
                _1f7[1]--;
            }
            if (_1f7[6]) {
                _1f7[6] *= 1000;
            }
            if (_1f6) {
                _1f6 = new Date(_1f6);
                dojo.map(["FullYear","Month","Date","Hours","Minutes","Seconds","Milliseconds"], function(prop) {
                    return _1f6["get" + prop]();
                }).forEach(function(_1fa, _1fb) {
                    if (_1f7[_1fb] === undefined) {
                        _1f7[_1fb] = _1fa;
                    }
                });
            }
            _1f8 = new Date(_1f7[0] || 1970, _1f7[1] || 0, _1f7[2] || 1, _1f7[3] || 0, _1f7[4] || 0, _1f7[5] || 0, _1f7[6] || 0);
            var _1fc = 0;
            var _1fd = _1f7[7] && _1f7[7].charAt(0);
            if (_1fd != "Z") {
                _1fc = ((_1f7[8] || 0) * 60) + (Number(_1f7[9]) || 0);
                if (_1fd != "-") {
                    _1fc *= -1;
                }
            }
            if (_1fd) {
                _1fc -= _1f8.getTimezoneOffset();
            }
            if (_1fc) {
                _1f8.setTime(_1f8.getTime() + _1fc * 60000);
            }
        }
        return _1f8;
    };
    dojo.date.stamp.toISOString = function(_1fe, _1ff) {
        var _ = function(n) {
            return (n < 10) ? "0" + n : n;
        };
        _1ff = _1ff || {};
        var _202 = [];
        var _203 = _1ff.zulu ? "getUTC" : "get";
        var date = "";
        if (_1ff.selector != "time") {
            var year = _1fe[_203 + "FullYear"]();
            date = ["0000".substr((year + "").length) + year,_(_1fe[_203 + "Month"]() + 1),_(_1fe[_203 + "Date"]())].join("-");
        }
        _202.push(date);
        if (_1ff.selector != "date") {
            var time = [_(_1fe[_203 + "Hours"]()),_(_1fe[_203 + "Minutes"]()),_(_1fe[_203 + "Seconds"]())].join(":");
            var _207 = _1fe[_203 + "Milliseconds"]();
            if (_1ff.milliseconds) {
                time += "." + (_207 < 100 ? "0" : "") + _(_207);
            }
            if (_1ff.zulu) {
                time += "Z";
            } else {
                if (_1ff.selector != "time") {
                    var _208 = _1fe.getTimezoneOffset();
                    var _209 = Math.abs(_208);
                    time += (_208 > 0 ? "-" : "+") + _(Math.floor(_209 / 60)) + ":" + _(_209 % 60);
                }
            }
            _202.push(time);
        }
        return _202.join("T");
    };
}
if (!dojo._hasResource["dojo.parser"]) {
    dojo._hasResource["dojo.parser"] = true;
    dojo.provide("dojo.parser");
    dojo.parser = new function() {
        var d = dojo;
        var _20b = d._scopeName + "Type";
        var qry = "[" + _20b + "]";
        var _20d = 0,_20e = {};
        var _20f = function(_210, _211) {
            var nso = _211 || _20e;
            if (dojo.isIE) {
                var cn = _210["__dojoNameCache"];
                if (cn && nso[cn] === _210) {
                    return cn;
                }
            }
            var name;
            do{
                name = "__" + _20d++;
            } while (name in nso);
            nso[name] = _210;
            return name;
        };

        function _215(_216) {
            if (d.isString(_216)) {
                return "string";
            }
            if (typeof _216 == "number") {
                return "number";
            }
            if (typeof _216 == "boolean") {
                return "boolean";
            }
            if (d.isFunction(_216)) {
                return "function";
            }
            if (d.isArray(_216)) {
                return "array";
            }
            if (_216 instanceof Date) {
                return "date";
            }
            if (_216 instanceof d._Url) {
                return "url";
            }
            return "object";
        }

        ;
        function _217(_218, type) {
            switch (type) {case "string":return _218;case "number":return _218.length ? Number(_218) : NaN;case "boolean":return typeof _218 == "boolean" ? _218 : !(_218.toLowerCase() == "false");case "function":if (d.isFunction(_218)) {
                _218 = _218.toString();
                _218 = d.trim(_218.substring(_218.indexOf("{") + 1, _218.length - 1));
            }try {
                if (_218.search(/[^\w\.]+/i) != -1) {
                    _218 = _20f(new Function(_218), this);
                }
                return d.getObject(_218, false);
            } catch(e) {
                return new Function();
            }case "array":return _218 ? _218.split(/\s*,\s*/) : [];case "date":switch (_218) {case "":return new Date("");case "now":return new Date();default:return d.date.stamp.fromISOString(_218);}case "url":return d.baseUrl + _218;default:return d.fromJson(_218);}
        }

        ;
        var _21a = {};

        function _21b(_21c) {
            if (!_21a[_21c]) {
                var cls = d.getObject(_21c);
                if (!d.isFunction(cls)) {
                    throw new Error("Could not load class '" + _21c + "'. Did you spell the name correctly and use a full path, like 'dijit.form.Button'?");
                }
                var _21e = cls.prototype;
                var _21f = {},_220 = {};
                for (var name in _21e) {
                    if (name.charAt(0) == "_") {
                        continue;
                    }
                    if (name in _220) {
                        continue;
                    }
                    var _222 = _21e[name];
                    _21f[name] = _215(_222);
                }
                _21a[_21c] = {cls:cls,params:_21f};
            }
            return _21a[_21c];
        }

        ;
        this._functionFromScript = function(_223) {
            var _224 = "";
            var _225 = "";
            var _226 = _223.getAttribute("args");
            if (_226) {
                d.forEach(_226.split(/\s*,\s*/), function(part, idx) {
                    _224 += "var " + part + " = arguments[" + idx + "]; ";
                });
            }
            var _229 = _223.getAttribute("with");
            if (_229 && _229.length) {
                d.forEach(_229.split(/\s*,\s*/), function(part) {
                    _224 += "with(" + part + "){";
                    _225 += "}";
                });
            }
            return new Function(_224 + _223.innerHTML + _225);
        };
        this.instantiate = function(_22b, _22c) {
            var _22d = [];
            _22c = _22c || {};
            d.forEach(_22b, function(node) {
                if (!node) {
                    return;
                }
                var type = _20b in _22c ? _22c[_20b] : node.getAttribute(_20b);
                if (!type || !type.length) {
                    return;
                }
                var _230 = _21b(type),_231 = _230.cls,ps = _231._noScript || _231.prototype._noScript;
                var _233 = {},_234 = node.attributes;
                for (var name in _230.params) {
                    var item = name in _22c ? {value:_22c[name],specified:true} : _234.getNamedItem(name);
                    if (!item || (!item.specified && (!dojo.isIE || name.toLowerCase() != "value"))) {
                        continue;
                    }
                    var _237 = item.value;
                    switch (name) {case "class":_237 = "className" in _22c ? _22c.className : node.className;break;case "style":_237 = "style" in _22c ? _22c.style : (node.style && node.style.cssText);}
                    var _238 = _230.params[name];
                    if (typeof _237 == "string") {
                        _233[name] = _217(_237, _238);
                    } else {
                        _233[name] = _237;
                    }
                }
                if (!ps) {
                    var _239 = [],_23a = [];
                    d.query("> script[type^='dojo/']", node).orphan().forEach(function(_23b) {
                        var _23c = _23b.getAttribute("event"),type = _23b.getAttribute("type"),nf = d.parser._functionFromScript(_23b);
                        if (_23c) {
                            if (type == "dojo/connect") {
                                _239.push({event:_23c,func:nf});
                            } else {
                                _233[_23c] = nf;
                            }
                        } else {
                            _23a.push(nf);
                        }
                    });
                }
                var _23e = _231["markupFactory"];
                if (!_23e && _231["prototype"]) {
                    _23e = _231.prototype["markupFactory"];
                }
                var _23f = _23e ? _23e(_233, node, _231) : new _231(_233, node);
                _22d.push(_23f);
                var _240 = node.getAttribute("jsId");
                if (_240) {
                    d.setObject(_240, _23f);
                }
                if (!ps) {
                    d.forEach(_239, function(_241) {
                        d.connect(_23f, _241.event, null, _241.func);
                    });
                    d.forEach(_23a, function(func) {
                        func.call(_23f);
                    });
                }
            });
            d.forEach(_22d, function(_243) {
                if (_243 && _243.startup && !_243._started && (!_243.getParent || !_243.getParent())) {
                    _243.startup();
                }
            });
            return _22d;
        };
        this.parse = function(_244) {
            var list = d.query(qry, _244);
            var _246 = this.instantiate(list);
            return _246;
        };
    }();
    (function() {
        var _247 = function() {
            if (dojo.config["parseOnLoad"] == true) {
                dojo.parser.parse();
            }
        };
        if (dojo.exists("dijit.wai.onload") && (dijit.wai.onload === dojo._loaders[0])) {
            dojo._loaders.splice(1, 0, _247);
        } else {
            dojo._loaders.unshift(_247);
        }
    })();
}
if (!dojo._hasResource["dijit._Templated"]) {
    dojo._hasResource["dijit._Templated"] = true;
    dojo.provide("dijit._Templated");
    dojo.declare("dijit._Templated", null, {templateString:null,templatePath:null,widgetsInTemplate:false,_skipNodeCache:false,_stringRepl:function(tmpl) {
        var _249 = this.declaredClass,_24a = this;
        return dojo.string.substitute(tmpl, this, function(_24b, key) {
            if (key.charAt(0) == "!") {
                _24b = dojo.getObject(key.substr(1), false, _24a);
            }
            if (typeof _24b == "undefined") {
                throw new Error(_249 + " template:" + key);
            }
            if (_24b == null) {
                return "";
            }
            return key.charAt(0) == "!" ? _24b : _24b.toString().replace(/"/g, "&quot;");
        }, this);
    },buildRendering:function() {
        var _24d = dijit._Templated.getCachedTemplate(this.templatePath, this.templateString, this._skipNodeCache);
        var node;
        if (dojo.isString(_24d)) {
            node = dojo._toDom(this._stringRepl(_24d));
        } else {
            node = _24d.cloneNode(true);
        }
        this.domNode = node;
        this._attachTemplateNodes(node);
        if (this.widgetsInTemplate) {
            var cw = (this._supportingWidgets = dojo.parser.parse(node));
            this._attachTemplateNodes(cw, function(n, p) {
                return n[p];
            });
        }
        this._fillContent(this.srcNodeRef);
    },_fillContent:function(_252) {
        var dest = this.containerNode;
        if (_252 && dest) {
            while (_252.hasChildNodes()) {
                dest.appendChild(_252.firstChild);
            }
        }
    },_attachTemplateNodes:function(_254, _255) {
        _255 = _255 || function(n, p) {
            return n.getAttribute(p);
        };
        var _258 = dojo.isArray(_254) ? _254 : (_254.all || _254.getElementsByTagName("*"));
        var x = dojo.isArray(_254) ? 0 : -1;
        for (; x < _258.length; x++) {
            var _25a = (x == -1) ? _254 : _258[x];
            if (this.widgetsInTemplate && _255(_25a, "dojoType")) {
                continue;
            }
            var _25b = _255(_25a, "dojoAttachPoint");
            if (_25b) {
                var _25c,_25d = _25b.split(/\s*,\s*/);
                while ((_25c = _25d.shift())) {
                    if (dojo.isArray(this[_25c])) {
                        this[_25c].push(_25a);
                    } else {
                        this[_25c] = _25a;
                    }
                }
            }
            var _25e = _255(_25a, "dojoAttachEvent");
            if (_25e) {
                var _25f,_260 = _25e.split(/\s*,\s*/);
                var trim = dojo.trim;
                while ((_25f = _260.shift())) {
                    if (_25f) {
                        var _262 = null;
                        if (_25f.indexOf(":") != -1) {
                            var _263 = _25f.split(":");
                            _25f = trim(_263[0]);
                            _262 = trim(_263[1]);
                        } else {
                            _25f = trim(_25f);
                        }
                        if (!_262) {
                            _262 = _25f;
                        }
                        this.connect(_25a, _25f, _262);
                    }
                }
            }
            var role = _255(_25a, "waiRole");
            if (role) {
                dijit.setWaiRole(_25a, role);
            }
            var _265 = _255(_25a, "waiState");
            if (_265) {
                dojo.forEach(_265.split(/\s*,\s*/), function(_266) {
                    if (_266.indexOf("-") != -1) {
                        var pair = _266.split("-");
                        dijit.setWaiState(_25a, pair[0], pair[1]);
                    }
                });
            }
        }
    }});
    dijit._Templated._templateCache = {};
    dijit._Templated.getCachedTemplate = function(_268, _269, _26a) {
        var _26b = dijit._Templated._templateCache;
        var key = _269 || _268;
        var _26d = _26b[key];
        if (_26d) {
            if (!_26d.ownerDocument || _26d.ownerDocument == dojo.doc) {
                return _26d;
            }
            dojo.destroy(_26d);
        }
        if (!_269) {
            _269 = dijit._Templated._sanitizeTemplateString(dojo.trim(dojo._getText(_268)));
        }
        _269 = dojo.string.trim(_269);
        if (_26a || _269.match(/\$\{([^\}]+)\}/g)) {
            return (_26b[key] = _269);
        } else {
            return (_26b[key] = dojo._toDom(_269));
        }
    };
    dijit._Templated._sanitizeTemplateString = function(_26e) {
        if (_26e) {
            _26e = _26e.replace(/^\s*<\?xml(\s)+version=[\'\"](\d)*.(\d)*[\'\"](\s)*\?>/im, "");
            var _26f = _26e.match(/<body[^>]*>\s*([\s\S]+)\s*<\/body>/im);
            if (_26f) {
                _26e = _26f[1];
            }
        } else {
            _26e = "";
        }
        return _26e;
    };
    if (dojo.isIE) {
        dojo.addOnWindowUnload(function() {
            var _270 = dijit._Templated._templateCache;
            for (var key in _270) {
                var _272 = _270[key];
                if (!isNaN(_272.nodeType)) {
                    dojo.destroy(_272);
                }
                delete _270[key];
            }
        });
    }
    dojo.extend(dijit._Widget, {dojoAttachEvent:"",dojoAttachPoint:"",waiRole:"",waiState:""});
}
if (!dojo._hasResource["dojo.fx.Toggler"]) {
    dojo._hasResource["dojo.fx.Toggler"] = true;
    dojo.provide("dojo.fx.Toggler");
    dojo.declare("dojo.fx.Toggler", null, {constructor:function(args) {
        var _t = this;
        dojo.mixin(_t, args);
        _t.node = args.node;
        _t._showArgs = dojo.mixin({}, args);
        _t._showArgs.node = _t.node;
        _t._showArgs.duration = _t.showDuration;
        _t.showAnim = _t.showFunc(_t._showArgs);
        _t._hideArgs = dojo.mixin({}, args);
        _t._hideArgs.node = _t.node;
        _t._hideArgs.duration = _t.hideDuration;
        _t.hideAnim = _t.hideFunc(_t._hideArgs);
        dojo.connect(_t.showAnim, "beforeBegin", dojo.hitch(_t.hideAnim, "stop", true));
        dojo.connect(_t.hideAnim, "beforeBegin", dojo.hitch(_t.showAnim, "stop", true));
    },node:null,showFunc:dojo.fadeIn,hideFunc:dojo.fadeOut,showDuration:200,hideDuration:200,show:function(_275) {
        return this.showAnim.play(_275 || 0);
    },hide:function(_276) {
        return this.hideAnim.play(_276 || 0);
    }});
}
if (!dojo._hasResource["dojo.fx"]) {
    dojo._hasResource["dojo.fx"] = true;
    dojo.provide("dojo.fx");
    (function() {
        var d = dojo,_278 = {_fire:function(evt, args) {
            if (this[evt]) {
                this[evt].apply(this, args || []);
            }
            return this;
        }};
        var _27b = function(_27c) {
            this._index = -1;
            this._animations = _27c || [];
            this._current = this._onAnimateCtx = this._onEndCtx = null;
            this.duration = 0;
            d.forEach(this._animations, function(a) {
                this.duration += a.duration;
                if (a.delay) {
                    this.duration += a.delay;
                }
            }, this);
        };
        d.extend(_27b, {_onAnimate:function() {
            this._fire("onAnimate", arguments);
        },_onEnd:function() {
            d.disconnect(this._onAnimateCtx);
            d.disconnect(this._onEndCtx);
            this._onAnimateCtx = this._onEndCtx = null;
            if (this._index + 1 == this._animations.length) {
                this._fire("onEnd");
            } else {
                this._current = this._animations[++this._index];
                this._onAnimateCtx = d.connect(this._current, "onAnimate", this, "_onAnimate");
                this._onEndCtx = d.connect(this._current, "onEnd", this, "_onEnd");
                this._current.play(0, true);
            }
        },play:function(_27e, _27f) {
            if (!this._current) {
                this._current = this._animations[this._index = 0];
            }
            if (!_27f && this._current.status() == "playing") {
                return this;
            }
            var _280 = d.connect(this._current, "beforeBegin", this, function() {
                this._fire("beforeBegin");
            }),_281 = d.connect(this._current, "onBegin", this, function(arg) {
                this._fire("onBegin", arguments);
            }),_283 = d.connect(this._current, "onPlay", this, function(arg) {
                this._fire("onPlay", arguments);
                d.disconnect(_280);
                d.disconnect(_281);
                d.disconnect(_283);
            });
            if (this._onAnimateCtx) {
                d.disconnect(this._onAnimateCtx);
            }
            this._onAnimateCtx = d.connect(this._current, "onAnimate", this, "_onAnimate");
            if (this._onEndCtx) {
                d.disconnect(this._onEndCtx);
            }
            this._onEndCtx = d.connect(this._current, "onEnd", this, "_onEnd");
            this._current.play.apply(this._current, arguments);
            return this;
        },pause:function() {
            if (this._current) {
                var e = d.connect(this._current, "onPause", this, function(arg) {
                    this._fire("onPause", arguments);
                    d.disconnect(e);
                });
                this._current.pause();
            }
            return this;
        },gotoPercent:function(_287, _288) {
            this.pause();
            var _289 = this.duration * _287;
            this._current = null;
            d.some(this._animations, function(a) {
                if (a.duration <= _289) {
                    this._current = a;
                    return true;
                }
                _289 -= a.duration;
                return false;
            });
            if (this._current) {
                this._current.gotoPercent(_289 / this._current.duration, _288);
            }
            return this;
        },stop:function(_28b) {
            if (this._current) {
                if (_28b) {
                    for (; this._index + 1 < this._animations.length; ++this._index) {
                        this._animations[this._index].stop(true);
                    }
                    this._current = this._animations[this._index];
                }
                var e = d.connect(this._current, "onStop", this, function(arg) {
                    this._fire("onStop", arguments);
                    d.disconnect(e);
                });
                this._current.stop();
            }
            return this;
        },status:function() {
            return this._current ? this._current.status() : "stopped";
        },destroy:function() {
            if (this._onAnimateCtx) {
                d.disconnect(this._onAnimateCtx);
            }
            if (this._onEndCtx) {
                d.disconnect(this._onEndCtx);
            }
        }});
        d.extend(_27b, _278);
        dojo.fx.chain = function(_28e) {
            return new _27b(_28e);
        };
        var _28f = function(_290) {
            this._animations = _290 || [];
            this._connects = [];
            this._finished = 0;
            this.duration = 0;
            d.forEach(_290, function(a) {
                var _292 = a.duration;
                if (a.delay) {
                    _292 += a.delay;
                }
                if (this.duration < _292) {
                    this.duration = _292;
                }
                this._connects.push(d.connect(a, "onEnd", this, "_onEnd"));
            }, this);
            this._pseudoAnimation = new d._Animation({curve:[0,1],duration:this.duration});
            var self = this;
            d.forEach(["beforeBegin","onBegin","onPlay","onAnimate","onPause","onStop"], function(evt) {
                self._connects.push(d.connect(self._pseudoAnimation, evt, function() {
                    self._fire(evt, arguments);
                }));
            });
        };
        d.extend(_28f, {_doAction:function(_295, args) {
            d.forEach(this._animations, function(a) {
                a[_295].apply(a, args);
            });
            return this;
        },_onEnd:function() {
            if (++this._finished == this._animations.length) {
                this._fire("onEnd");
            }
        },_call:function(_298, args) {
            var t = this._pseudoAnimation;
            t[_298].apply(t, args);
        },play:function(_29b, _29c) {
            this._finished = 0;
            this._doAction("play", arguments);
            this._call("play", arguments);
            return this;
        },pause:function() {
            this._doAction("pause", arguments);
            this._call("pause", arguments);
            return this;
        },gotoPercent:function(_29d, _29e) {
            var ms = this.duration * _29d;
            d.forEach(this._animations, function(a) {
                a.gotoPercent(a.duration < ms ? 1 : (ms / a.duration), _29e);
            });
            this._call("gotoPercent", arguments);
            return this;
        },stop:function(_2a1) {
            this._doAction("stop", arguments);
            this._call("stop", arguments);
            return this;
        },status:function() {
            return this._pseudoAnimation.status();
        },destroy:function() {
            d.forEach(this._connects, dojo.disconnect);
        }});
        d.extend(_28f, _278);
        dojo.fx.combine = function(_2a2) {
            return new _28f(_2a2);
        };
        dojo.fx.wipeIn = function(args) {
            args.node = d.byId(args.node);
            var node = args.node,s = node.style,o;
            var anim = d.animateProperty(d.mixin({properties:{height:{start:function() {
                o = s.overflow;
                s.overflow = "hidden";
                if (s.visibility == "hidden" || s.display == "none") {
                    s.height = "1px";
                    s.display = "";
                    s.visibility = "";
                    return 1;
                } else {
                    var _2a8 = d.style(node, "height");
                    return Math.max(_2a8, 1);
                }
            },end:function() {
                return node.scrollHeight;
            }}}}, args));
            d.connect(anim, "onEnd", function() {
                s.height = "auto";
                s.overflow = o;
            });
            return anim;
        };
        dojo.fx.wipeOut = function(args) {
            var node = args.node = d.byId(args.node),s = node.style,o;
            var anim = d.animateProperty(d.mixin({properties:{height:{end:1}}}, args));
            d.connect(anim, "beforeBegin", function() {
                o = s.overflow;
                s.overflow = "hidden";
                s.display = "";
            });
            d.connect(anim, "onEnd", function() {
                s.overflow = o;
                s.height = "auto";
                s.display = "none";
            });
            return anim;
        };
        dojo.fx.slideTo = function(args) {
            var node = args.node = d.byId(args.node),top = null,left = null;
            var init = (function(n) {
                return function() {
                    var cs = d.getComputedStyle(n);
                    var pos = cs.position;
                    top = (pos == "absolute" ? n.offsetTop : parseInt(cs.top) || 0);
                    left = (pos == "absolute" ? n.offsetLeft : parseInt(cs.left) || 0);
                    if (pos != "absolute" && pos != "relative") {
                        var ret = d.coords(n, true);
                        top = ret.y;
                        left = ret.x;
                        n.style.position = "absolute";
                        n.style.top = top + "px";
                        n.style.left = left + "px";
                    }
                };
            })(node);
            init();
            var anim = d.animateProperty(d.mixin({properties:{top:args.top || 0,left:args.left || 0}}, args));
            d.connect(anim, "beforeBegin", anim, init);
            return anim;
        };
    })();
}
if (!dojo._hasResource["dojox.layout.ResizeHandle"]) {
    dojo._hasResource["dojox.layout.ResizeHandle"] = true;
    dojo.provide("dojox.layout.ResizeHandle");
    dojo.experimental("dojox.layout.ResizeHandle");
    dojo.declare("dojox.layout.ResizeHandle", [dijit._Widget,dijit._Templated], {targetId:"",targetContainer:null,resizeAxis:"xy",activeResize:false,activeResizeClass:"dojoxResizeHandleClone",animateSizing:true,animateMethod:"chain",animateDuration:225,minHeight:100,minWidth:100,constrainMax:false,maxHeight:0,maxWidth:0,fixedAspect:false,intermediateChanges:false,templateString:"<div dojoAttachPoint=\"resizeHandle\" class=\"dojoxResizeHandle\"><div></div></div>",postCreate:function() {
        this.connect(this.resizeHandle, "onmousedown", "_beginSizing");
        if (!this.activeResize) {
            this._resizeHelper = dijit.byId("dojoxGlobalResizeHelper");
            if (!this._resizeHelper) {
                this._resizeHelper = new dojox.layout._ResizeHelper({id:"dojoxGlobalResizeHelper"}).placeAt(dojo.body());
                dojo.addClass(this._resizeHelper.domNode, this.activeResizeClass);
            }
        } else {
            this.animateSizing = false;
        }
        if (!this.minSize) {
            this.minSize = {w:this.minWidth,h:this.minHeight};
        }
        if (this.constrainMax) {
            this.maxSize = {w:this.maxWidth,h:this.maxHeight};
        }
        this._resizeX = this._resizeY = false;
        var _2b8 = dojo.partial(dojo.addClass, this.resizeHandle);
        switch (this.resizeAxis.toLowerCase()) {case "xy":this._resizeX = this._resizeY = true;_2b8("dojoxResizeNW");break;case "x":this._resizeX = true;_2b8("dojoxResizeW");break;case "y":this._resizeY = true;_2b8("dojoxResizeN");break;}
    },_beginSizing:function(e) {
        if (this._isSizing) {
            return false;
        }
        this.targetWidget = dijit.byId(this.targetId);
        this.targetDomNode = this.targetWidget ? this.targetWidget.domNode : dojo.byId(this.targetId);
        if (this.targetContainer) {
            this.targetDomNode = this.targetContainer;
        }
        if (!this.targetDomNode) {
            return false;
        }
        if (!this.activeResize) {
            var c = dojo.coords(this.targetDomNode, true);
            this._resizeHelper.resize({l:c.x,t:c.y,w:c.w,h:c.h});
            this._resizeHelper.show();
        }
        this._isSizing = true;
        this.startPoint = {x:e.clientX,y:e.clientY};
        var mb = this.targetWidget ? dojo.marginBox(this.targetDomNode) : dojo.contentBox(this.targetDomNode);
        this.startSize = {w:mb.w,h:mb.h};
        if (this.fixedAspect) {
            var max,val;
            if (mb.w > mb.h) {
                max = "w";
                val = mb.w / mb.h;
            } else {
                max = "h";
                val = mb.h / mb.w;
            }
            this._aspect = {prop:max};
            this._aspect[max] = val;
        }
        this._pconnects = [];
        this._pconnects.push(dojo.connect(dojo.doc, "onmousemove", this, "_updateSizing"));
        this._pconnects.push(dojo.connect(dojo.doc, "onmouseup", this, "_endSizing"));
        dojo.stopEvent(e);
    },_updateSizing:function(e) {
        if (this.activeResize) {
            this._changeSizing(e);
        } else {
            var tmp = this._getNewCoords(e);
            if (tmp === false) {
                return;
            }
            this._resizeHelper.resize(tmp);
        }
        e.preventDefault();
    },_getNewCoords:function(e) {
        try {
            if (!e.clientX || !e.clientY) {
                return false;
            }
        } catch(e) {
            return false;
        }
        this._activeResizeLastEvent = e;
        var dx = this.startPoint.x - e.clientX,dy = this.startPoint.y - e.clientY,newW = this.startSize.w - (this._resizeX ? dx : 0),newH = this.startSize.h - (this._resizeY ? dy : 0);
        return this._checkConstraints(newW, newH);
    },_checkConstraints:function(newW, newH) {
        if (this.minSize) {
            var tm = this.minSize;
            if (newW < tm.w) {
                newW = tm.w;
            }
            if (newH < tm.h) {
                newH = tm.h;
            }
        }
        if (this.constrainMax && this.maxSize) {
            var ms = this.maxSize;
            if (newW > ms.w) {
                newW = ms.w;
            }
            if (newH > ms.h) {
                newH = ms.h;
            }
        }
        if (this.fixedAspect) {
            var ta = this._aspect[this._aspect.prop];
            if (newW < newH) {
                newH = newW * ta;
            } else {
                if (newH < newW) {
                    newW = newH * ta;
                }
            }
        }
        return {w:newW,h:newH};
    },_changeSizing:function(e) {
        var tmp = this._getNewCoords(e);
        if (tmp === false) {
            return;
        }
        if (this.targetWidget && dojo.isFunction(this.targetWidget.resize)) {
            this.targetWidget.resize(tmp);
        } else {
            if (this.animateSizing) {
                var anim = dojo.fx[this.animateMethod]([dojo.animateProperty({node:this.targetDomNode,properties:{width:{start:this.startSize.w,end:tmp.w,unit:"px"}},duration:this.animateDuration}),dojo.animateProperty({node:this.targetDomNode,properties:{height:{start:this.startSize.h,end:tmp.h,unit:"px"}},duration:this.animateDuration})]);
                anim.play();
            } else {
                dojo.style(this.targetDomNode, {width:tmp.w + "px",height:tmp.h + "px"});
            }
        }
        if (this.intermediateChanges) {
            this.onResize(e);
        }
    },_endSizing:function(e) {
        dojo.forEach(this._pconnects, dojo.disconnect);
        if (!this.activeResize) {
            this._resizeHelper.hide();
            this._changeSizing(e);
        }
        this._isSizing = false;
        this.onResize(e);
    },onResize:function(e) {
    }});
    dojo.declare("dojox.layout._ResizeHelper", dijit._Widget, {show:function() {
        dojo.fadeIn({node:this.domNode,duration:120,beforeBegin:dojo.partial(dojo.style, this.domNode, "display", "")}).play();
    },hide:function() {
        dojo.fadeOut({node:this.domNode,duration:250,onEnd:dojo.partial(dojo.style, this.domNode, "display", "none")}).play();
    },resize:function(dim) {
        dojo.marginBox(this.domNode, dim);
    }});
}
if (!dojo._hasResource["demos.cropper.src.Preview"]) {
    dojo._hasResource["demos.cropper.src.Preview"] = true;
    dojo.provide("demos.cropper.src.Preview");
    (function(d, $) {
        var wrap = function(node, _2d4) {
            var n = d.create(_2d4);
            d.place(n, node, "before");
            d.place(node, n, "first");
            return n;
        },abs = "absolute",pos = "position",_2d8 = "px",_2d9 = Math.floor;
        d.declare("image.Preview", dijit._Widget, {glassSize:150,scale:2,withMouseMove:false,withDrag:true,moveInterval:50,hoverable:false,resizeable:true,opacity:0.35,postCreate:function() {
            var gs = this.glassSize,s = this.scale;
            var mb = d.marginBox(this.domNode);
            this.currentSize = mb;
            this.container = wrap(this.domNode, "div");
            d.marginBox(this.container, mb);
            this.picker = d.create("div", {"class":"imageDragger",style:{opacity:this.hoverable ? 0 : this.opacity,width:gs + _2d8,height:gs + _2d8}}, this.domNode, "before");
            this.preview = d.create("div", {style:{position:abs,overflow:"hidden",width:_2d9(gs * s) + _2d8,height:_2d9(gs * s) + _2d8}}, d.body());
            var n = wrap(d.create("img", {style:{position:abs},src:this.altSrc || this.domNode.src}, this.preview), "div");
            d.style(n, pos, "relative");
            d.style(this.domNode, pos, abs);
            this.image = d.query("img", this.preview).onload(d.hitch(this, "_adjustImage"))[0];
            this._positionPicker();
            this.mover = new d.dnd.move.parentConstrainedMoveable(this.picker, {area:"content",within:true});
            if (this.resizeable) {
                this._handle = new dojox.layout.ResizeHandle({targetContainer:this.picker,fixedAspect:true,intermediateChanges:true,activeResize:true,onResize:d.hitch(this, function(e, f) {
                    this._adjustImage(e, f);
                    this._whileMoving();
                }),constrainMax:true,maxWidth:mb.w,maxHeight:mb.h,minWidth:40,minHeight:40}).placeAt(this.picker);
            }
            d.subscribe("/dnd/move/start", this, "_startDnd");
            d.subscribe("/dnd/move/stop", this, "_stopDnd");
            d.isIE && (this.image.src = this.image.src);
            this.connect(d.global, "onresize", "_positionPicker");
            if (this.hoverable) {
                this.connect(this.container, "onmouseenter", "_enter");
                this.connect(this.container, "onmouseleave", "_leave");
            }
            setTimeout(dojo.hitch(this, "_positionPicker"), 125);
        },_adjustImage:function(e) {
            var tc = this.coords,s = this.scale;
            if (e && e.type && (e.type == "mouseup" || e.type == "mousemove")) {
                var xy = d.coords(this.picker);
                this.scale = d.coords(this.preview).w / xy.w;
            } else {
                if (e && e.type && e.type == "load" && this.imageReady(e)) {
                }
            }
            d.style(this.image, {height:_2d9(tc.h * s) + _2d8,width:_2d9(tc.w * s) + _2d8});
        },_positionPicker:function(e) {
            var tc = this.coords = d.coords(this.container, true);
            d.style(this.preview, {left:tc.x + tc.w + 10 + _2d8,top:tc.y + _2d8});
        },_startDnd:function(n) {
            if (!this._interval && n && n.node == this.picker) {
                this._interval = this.connect(d.doc, "onmousemove", "_whileMoving");
            }
        },_stopDnd:function() {
            if (this._interval) {
                this.disconnect(this._interval);
                delete this._interval;
                if (this.resizeable && this._lastXY) {
                    var tc = this.coords;
                    this._handle.maxSize = {h:_2d9(tc.h - (this._lastXY.t - tc.t)),w:_2d9(tc.w - (this._lastXY.l - tc.l))};
                }
            }
        },_whileMoving:function() {
            var xy = this._lastXY = d.coords(this.picker),tc = this.coords,r = this.image.width / tc.w,x = _2d9((xy.l - tc.l) * r),y = _2d9((xy.t - tc.t) * r);
            d.style(this.image, {top:"-" + y + _2d8,left:"-" + x + _2d8});
        },destroy:function() {
            d.place(this.domNode, this.container, "before");
            d.forEach(["preview","picker","container","image"], function(n) {
                d.destroy(this[n]);
                delete this[n];
            }, this);
            this.inherited(arguments);
        },imageReady:function() {
        },_enter:function(e) {
            this._anim && this._anim.stop();
            this._anim = d.anim(this.picker, {opacity:this.opacity});
        },_leave:function(e) {
            if (!this._interval && !this._handle._isSizing) {
                this._anim && this._anim.stop();
                this._anim = d.anim(this.picker, {opacity:0});
            }
        }});
        d.extend(d.NodeList, {preview:function(args) {
            return this.instantiate(image.Preview, args);
        }});
    })(dojo, dojo.query);
}
if (!dojo._hasResource["dojox.analytics.Urchin"]) {
    dojo._hasResource["dojox.analytics.Urchin"] = true;
    dojo.provide("dojox.analytics.Urchin");
    dojo.declare("dojox.analytics.Urchin", null, {acct:dojo.config.urchin,loadInterval:42,decay:0.5,timeout:4200,constructor:function(args) {
        this.tracker = null;
        dojo.mixin(this, args);
        this._loadGA();
    },_loadGA:function() {
        var _2f2 = ("https:" == document.location.protocol) ? "https://ssl." : "http://www.";
        dojo.create("script", {src:_2f2 + "google-analytics.com/ga.js"}, dojo.doc.getElementsByTagName("head")[0]);
        setTimeout(dojo.hitch(this, "_checkGA"), this.loadInterval);
    },_checkGA:function() {
        if (this.loadInterval > this.timeout) {
            return;
        }
        setTimeout(dojo.hitch(this, !window["_gat"] ? "_checkGA" : "_gotGA"), this.loadInterval);
        this.loadInterval *= (this.decay + 1);
    },_gotGA:function() {
        this.tracker = _gat._getTracker(this.acct);
        this.tracker._initData();
        this.GAonLoad.apply(this, arguments);
    },GAonLoad:function() {
        this.trackPageView();
    },trackPageView:function(url) {
        this.tracker._trackPageview.apply(this, arguments);
    }});
}
if (!dojo._hasResource["dojo.fx.easing"]) {
    dojo._hasResource["dojo.fx.easing"] = true;
    dojo.provide("dojo.fx.easing");
    dojo.fx.easing = {linear:function(n) {
        return n;
    },quadIn:function(n) {
        return Math.pow(n, 2);
    },quadOut:function(n) {
        return n * (n - 2) * -1;
    },quadInOut:function(n) {
        n = n * 2;
        if (n < 1) {
            return Math.pow(n, 2) / 2;
        }
        return -1 * ((--n) * (n - 2) - 1) / 2;
    },cubicIn:function(n) {
        return Math.pow(n, 3);
    },cubicOut:function(n) {
        return Math.pow(n - 1, 3) + 1;
    },cubicInOut:function(n) {
        n = n * 2;
        if (n < 1) {
            return Math.pow(n, 3) / 2;
        }
        n -= 2;
        return (Math.pow(n, 3) + 2) / 2;
    },quartIn:function(n) {
        return Math.pow(n, 4);
    },quartOut:function(n) {
        return -1 * (Math.pow(n - 1, 4) - 1);
    },quartInOut:function(n) {
        n = n * 2;
        if (n < 1) {
            return Math.pow(n, 4) / 2;
        }
        n -= 2;
        return -1 / 2 * (Math.pow(n, 4) - 2);
    },quintIn:function(n) {
        return Math.pow(n, 5);
    },quintOut:function(n) {
        return Math.pow(n - 1, 5) + 1;
    },quintInOut:function(n) {
        n = n * 2;
        if (n < 1) {
            return Math.pow(n, 5) / 2;
        }
        n -= 2;
        return (Math.pow(n, 5) + 2) / 2;
    },sineIn:function(n) {
        return -1 * Math.cos(n * (Math.PI / 2)) + 1;
    },sineOut:function(n) {
        return Math.sin(n * (Math.PI / 2));
    },sineInOut:function(n) {
        return -1 * (Math.cos(Math.PI * n) - 1) / 2;
    },expoIn:function(n) {
        return (n == 0) ? 0 : Math.pow(2, 10 * (n - 1));
    },expoOut:function(n) {
        return (n == 1) ? 1 : (-1 * Math.pow(2, -10 * n) + 1);
    },expoInOut:function(n) {
        if (n == 0) {
            return 0;
        }
        if (n == 1) {
            return 1;
        }
        n = n * 2;
        if (n < 1) {
            return Math.pow(2, 10 * (n - 1)) / 2;
        }
        --n;
        return (-1 * Math.pow(2, -10 * n) + 2) / 2;
    },circIn:function(n) {
        return -1 * (Math.sqrt(1 - Math.pow(n, 2)) - 1);
    },circOut:function(n) {
        n = n - 1;
        return Math.sqrt(1 - Math.pow(n, 2));
    },circInOut:function(n) {
        n = n * 2;
        if (n < 1) {
            return -1 / 2 * (Math.sqrt(1 - Math.pow(n, 2)) - 1);
        }
        n -= 2;
        return 1 / 2 * (Math.sqrt(1 - Math.pow(n, 2)) + 1);
    },backIn:function(n) {
        var s = 1.70158;
        return Math.pow(n, 2) * ((s + 1) * n - s);
    },backOut:function(n) {
        n = n - 1;
        var s = 1.70158;
        return Math.pow(n, 2) * ((s + 1) * n + s) + 1;
    },backInOut:function(n) {
        var s = 1.70158 * 1.525;
        n = n * 2;
        if (n < 1) {
            return (Math.pow(n, 2) * ((s + 1) * n - s)) / 2;
        }
        n -= 2;
        return (Math.pow(n, 2) * ((s + 1) * n + s) + 2) / 2;
    },elasticIn:function(n) {
        if (n == 0 || n == 1) {
            return n;
        }
        var p = 0.3;
        var s = p / 4;
        n = n - 1;
        return -1 * Math.pow(2, 10 * n) * Math.sin((n - s) * (2 * Math.PI) / p);
    },elasticOut:function(n) {
        if (n == 0 || n == 1) {
            return n;
        }
        var p = 0.3;
        var s = p / 4;
        return Math.pow(2, -10 * n) * Math.sin((n - s) * (2 * Math.PI) / p) + 1;
    },elasticInOut:function(n) {
        if (n == 0) {
            return 0;
        }
        n = n * 2;
        if (n == 2) {
            return 1;
        }
        var p = 0.3 * 1.5;
        var s = p / 4;
        if (n < 1) {
            n -= 1;
            return -0.5 * (Math.pow(2, 10 * n) * Math.sin((n - s) * (2 * Math.PI) / p));
        }
        n -= 1;
        return 0.5 * (Math.pow(2, -10 * n) * Math.sin((n - s) * (2 * Math.PI) / p)) + 1;
    },bounceIn:function(n) {
        return (1 - dojo.fx.easing.bounceOut(1 - n));
    },bounceOut:function(n) {
        var s = 7.5625;
        var p = 2.75;
        var l;
        if (n < (1 / p)) {
            l = s * Math.pow(n, 2);
        } else {
            if (n < (2 / p)) {
                n -= (1.5 / p);
                l = s * Math.pow(n, 2) + 0.75;
            } else {
                if (n < (2.5 / p)) {
                    n -= (2.25 / p);
                    l = s * Math.pow(n, 2) + 0.9375;
                } else {
                    n -= (2.625 / p);
                    l = s * Math.pow(n, 2) + 0.984375;
                }
            }
        }
        return l;
    },bounceInOut:function(n) {
        if (n < 0.5) {
            return dojo.fx.easing.bounceIn(n * 2) / 2;
        }
        return (dojo.fx.easing.bounceOut(n * 2 - 1) / 2) + 0.5;
    }};
}
if (!dojo._hasResource["dojox.image._base"]) {
    dojo._hasResource["dojox.image._base"] = true;
    dojo.provide("dojox.image._base");
    (function(d) {
        var _320;
        dojox.image.preload = function(urls) {
            if (!_320) {
                _320 = d.create("div", {style:{position:"absolute",top:"-9999px",display:"none"}}, d.body());
            }
            d.forEach(urls, function(url) {
                d.create("img", {src:url}, _320);
            });
        };
        if (d.config.preloadImages) {
            d.addOnLoad(function() {
                dojox.image.preload(d.config.preloadImages);
            });
        }
    })(dojo);
}
if (!dojo._hasResource["demos.cropper.src.nav"]) {
    dojo._hasResource["demos.cropper.src.nav"] = true;
    dojo.provide("demos.cropper.src.nav");
    (function(d, $) {
        var _325 = "mouse" + (d.isIE ? "enter" : "over");
        d.extend(d.NodeList, {hover:function(func, _327) {
            return this.onmouseenter(func).onmouseleave(_327 || func);
        },hoverClass:function(_328) {
            return this.hover(function(e) {
                d[(e.type == _325 ? "addClass" : "removeClass")](e.target, _328);
            });
        }});
        d.addOnLoad(function() {
            var _32a = [],_32b = 70,_32c = [];
            var _32d = $("> li", "picker").forEach(function(n, i) {
                d.style(n, "position", "relative");
                _32a.push(d.animateProperty({node:n,duration:375,delay:_32b * i,properties:{top:45},easing:d.fx.easing.backIn}));
                _32a.push(d.fadeOut({node:n,delay:_32b * i,duration:375}));
                _32c.push(d.animateProperty({node:n,delay:_32b * i,properties:{opacity:1,top:0}}));
            });
            var _in = d.fx.combine(_32a),_out = d.fx.combine(_32c);
            var _332 = function(arr) {
                var c = d.connect(_in, "onEnd", function() {
                    _32d.query("a").forEach(function(n, i) {
                        d.attr(n, {href:arr[i].replace(/\/thumb/, "").replace(/t\./, ".")});
                    });
                    _32d.query("img").forEach(function(n, i) {
                        d.attr(n, "src", arr[i]);
                    });
                    d.disconnect(c);
                    _out.play();
                });
                _in.play();
            };
            d.xhrGet({url:"images.json",handleAs:"json",load:function(resp) {
                var _33a = resp.images,_33b = [],_33c = (_33a.length / 6);
                var _33d = d.map(_33a, function(item) {
                    var _33f = item.src.replace(/\./, "t.");
                    return "images/thumb/" + _33f;
                });
                dojox.image.preload(_33d);
                var _340 = [];
                _340.push(_32d.query("img").attr("src"));
                var _341 = d.filter(_33d, function(url) {
                    return d.indexOf(_340[0], url) < 0;
                });
                _340.push(_341.slice(0, 6));
                _340.push(_341.slice(6));
                var _343 = d.create("ul", {id:"pager",style:{opacity:0}}, "navi");
                d.forEach(_340, function(p, i) {
                    var n = d.create("li", {innerHTML:(1 + i) + ""}, _343);
                    $(n).hoverClass("over").onclick(function(e) {
                        if (d.hasClass(n, "selected")) {
                            return;
                        }
                        $("> li", _343).removeClass("selected");
                        d.addClass(n, "selected");
                        _332(p);
                    });
                });
                d.fadeIn({node:_343}).play();
            }});
        });
    })(dojo, dojo.query);
}
if (!dojo._hasResource["demos.cropper.src"]) {
    dojo._hasResource["demos.cropper.src"] = true;
    dojo.provide("demos.cropper.src");
    (function(d, $) {
        d.addOnLoad(function() {
            var _34a = d.byId("loader"),hide = d.fadeOut({node:_34a}),show = d.fadeIn({node:_34a});
            var _34d = new image.Preview({imageReady:d.hitch(hide, "play"),hoverable:true}, "me");
            $("#picker").onclick(function(e) {
                e.preventDefault();
                var et = e.target,src = et.parentNode.href || et.href;
                if (src && _34d.image.src != src) {
                    show.play();
                    _34d.domNode.src = _34d.image.src = src;
                }
            });
            $("#navjs").onclick(function(e) {
                d["require"]("demos.cropper.src.nav");
                e.preventDefault();
            });
            new dojox.analytics.Urchin({acct:"UA-3572741-1",GAonLoad:function() {
                this.trackPageView("/demos/cropper");
            }});
        });
    })(dojo, dojo.query);
}

