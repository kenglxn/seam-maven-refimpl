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
if (!dojo._hasResource["demos.cropper.src"]) {
    dojo._hasResource["demos.cropper.src"] = true;
    dojo.provide("demos.cropper.src");
    (function(d, $) {
        d.addOnLoad(function() {
            var _34a = d.byId("loader"),hide = d.fadeOut({node:_34a}),show = d.fadeIn({node:_34a});
            var _34d = new image.Preview({imageReady:d.hitch(hide, "play"),hoverable:true}, "me");
        });
    })(dojo, dojo.query);
}

