/*
 * JQZoom Evolution 1.0.1 - Javascript Image magnifier
 *
 * Copyright (c) Engineer Renzi Marco(www.mind-projects.it)
 *
 * $Date: 12-12-2008
 *
 *	ChangeLog:
 *  
 * $License : GPL,so any change to the code you should copy and paste this section,and would be nice to report this to me(renzi.mrc@gmail.com).
 */
(function($)
{
    jQuery.fn.jqzoom = function(options)
    {
        var settings = {
            zoomType: 'standard', //standard/reverse/innerzoom
            zoomWidth: 200,		//zoomed width default width
            zoomHeight: 200,		//zoomed div default width
            xOffset: 10,		//zoomed div default offset
            yOffset: 0,
            position: "right" ,//zoomed div default position,offset position is to the right of the image
            lens:true, //zooming lens over the image,by default is 1;
			lensReset : false,
			imageOpacity: 0.2,
			title : true,
			alwaysOn: false,
			showEffect: 'show',
			hideEffect: 'hide',
			fadeinSpeed: 'fast',
			fadeoutSpeed: 'slow',
			preloadImages :true,
			showPreload: true,
			preloadText : 'Loading zoom',
			preloadPosition : 'center'   //bycss
        };

			//extending options
			options = options || {};
        	jQuery.extend(settings, options);


		return this.each(function()
		{
			var a = jQuery(this);
			var aTitle = a.attr('title'); //variabile per memorizzare il titolo href
			jQuery(a).removeAttr('title');
			jQuery(a).css('outline-style','none');


			var img = jQuery("img.small", this);
			var imageTitle = img.attr('title');
			img.removeAttr('title');	//variabile per memorizzare il titolo immagine


			var smallimage = new Smallimage( img );
			var smallimagedata = {};
			//imageborder
			var btop = 0;
			var bleft = 0;

			var loader = null;     //variabile per memorizzare oggetto loader
			loader = new Loader();

			var ZoomTitle = (trim(aTitle).length > 0) ? aTitle :
			(trim(imageTitle).length > 0) ? imageTitle : null;  //setting zoomtitle
			var ZoomTitleObj = new zoomTitle();
            var img = jQuery("img.large", this);

			var largeimage = new Largeimage( img );

			var lens = new Lens();
			var lensdata = {};
			//lensborder



			var largeimageloaded = false;
			var scale = {}; //rapporto tra immagine grande e piccola scale.x/scale.y
			var stage = null; // quadrato che mostra l'immagine ingrandita
			var running = false; // running = true quando si verifica l'evento che mostra lo zoom(adesso mouseover).
			var mousepos = {};
			var firstime = 0;
			var preloadshow = false;
			var isMouseDown = false;
			var dragstatus = false
			//loading smallimagedata
			smallimage.loadimage();

			//ritorna false al click dell href
			jQuery(this).click(function(){return false;});

			//se settato alwaysOn attivo lo Zoom e lo mostro.

			//attivo al mouseover
			jQuery(this).hover(function(e)
			{
				mousepos.x = e.pageX;
				mousepos.y	= e.pageY;
				activate();
			},function()
			{
				deactivate();
			});


			//ALWAYS ON
			if(settings.alwaysOn)
			{
				setTimeout(function(){activate();},150);
			}


			function activate()
			{

				if ( !running ) {

					//finding border
					smallimage.findborder();

					running = true;

					//rimuovo il titolo al mouseover
					imageTitle = img.attr('title');
					img.removeAttr('title');
					aTitle = a.attr('title');
					jQuery(a).removeAttr('title');

					//se non c� creo l'oggetto largeimage
					if (!largeimage || jQuery.browser.safari) {
						largeimage = new Largeimage( a[0].href );
					}

					//se l'immagine grande non � stata caricata la carico
					if(!largeimageloaded || jQuery.browser.safari)
					{
						largeimage.loadimage();
					}else
					{
					//after preload
						if(settings.zoomType != 'innerzoom')
						{
							stage = new Stage();
							stage.activate();
						}
						lens = new Lens;
						lens.activate();
					}

					//hack per MAC
				/*	if(jQuery.browser.safari)
					{
						if(settings.zoomType != 'innerzoom') //se innerzoom non mostro la finestra dello zoom
						{
							stage = new Stage();
							stage.activate();
						}
						if(jQuery('div.jqZoomPup').length <= 0)
						{
						lens = new Lens();
						}
						//if(settings.zoomType == 'innerzoom'){lens = new Lens()};
						lens.activate();
						(settings.alwaysOn) ? lens.center() : lens.setposition(null);
					}
					*/
					a[0].blur();
					//alert(jQuery('div.jqZoomPup').length);
					return false;
				}




			}

			function deactivate()
			{
				if(settings.zoomType == 'reverse' &&  !settings.alwaysOn)
				{
					img.css({'opacity' : 1});
				}

				if(!settings.alwaysOn)
				{
					//resetting parameters
					running = false;
					largeimageloaded = false;
					jQuery(lens.node).unbind('mousemove');
					lens.remove();
					if(jQuery('div.jqZoomWindow').length >0)
					{
						stage.remove();
					}
					if(jQuery('div.jqZoomTitle').length > 0)
					{
						ZoomTitleObj.remove();
					}
					//resetting title
					img.attr('title',imageTitle);
					a.attr('title',aTitle);
					jQuery().unbind();

					a.unbind('mousemove');
					//resetto il parametro che mi dice che � la prima volta che mostor lo zoom
					firstime = 0;
					//remove ieiframe
					if(jQuery('.zoom_ieframe').length > 0)
					{
						jQuery('.zoom_ieframe').remove();
					}
				}else
				{
					if(settings.lensReset)
					{
						switch(settings.zoomType)
						{
							case 'innerzoom':
							largeimage.setcenter();
							break;
							default:
							lens.center();
							break;
						}
					}
				}

				//non so se serve da provare
				if(settings.alwaysOn)
				{
					activate();
				}
			};





		//smallimage
		function Smallimage( image )
		{
			this.node = image[0];

			this.loadimage = function() {
				this.node.src = image[0].src;
			};
			this.findborder = function()
			{
				var bordertop = '';
				bordertop = jQuery(img).css('border-top-width');
				btop = '';
				var borderleft = '';
				borderleft = jQuery(img).css('border-left-width');
				bleft = '';
				/*if(jQuery.browser.msie)
				{
					var temp = bordertop.split(' ');

					bordertop = temp[1];
					var temp = borderleft.split(' ');
					borderleft = temp[1];
				}*/

				if(bordertop)
				{
					for(i=0;i<3;i++)
					{
						var x = [];
						x = bordertop.substr(i,1);

						if(isNaN(x) == false)
						{
							btop = btop +''+ bordertop.substr(i,1);
						}else
						{
							break;
						}
					}
				}

				if(borderleft)
				{
					for(i=0;i<3;i++)
					{
						if(!isNaN(borderleft.substr(i,1)))
						{
							bleft = bleft + borderleft.substr(i,1)
						}else
						{
							break;
						}
					}
				}
				btop = (btop.length > 0) ? eval(btop) : 0;
				bleft = (bleft.length > 0) ? eval(bleft) : 0;


			}
			this.node.onload = function()
			{
				//setto il cursor e la posizione dell'href


				a.css({'cursor':'crosshair','display':'block'});

				if(a.css('position')!= 'absolute' && a.parent().css('position'))
				{
					a.css({'cursor':'crosshair','position':'relative','display':'block'});
				}
				if(a.parent().css('position') != 'absolute')
				{
					a.parent().css('position','relative');
					//a.css('position','relative');
				}
				else{
				//a.css('position','relative');
				}
				if(jQuery.browser.safari || jQuery.browser.opera)
				{
					jQuery(img).css({position:'absolute',top:'0px',left:'0px'});
				}
				/*if(a.css('position')!= 'absolute' && a.parent().css('position'))
				{
					a.css({'cursor':'crosshair','position':'relative','display':'block'});
				}
				if(a.parent().css('position') != 'absolute')
				{
					alert('in');
					a.parent().css('position','relative');
					//a.css('position','relative');
				}
				else{
				//a.css('position','relative');
				}*/



				/*
				if(a.parent().css('position') != 'relative' && a.css('position') != 'absolute')
				{
				a.css({'cursor':'crosshair','position':'relative','display':'block'});
				}*/

				//al docuemnt ready viene caricato l'src quindi viene azionato l'onload e carico tutti i dati
				smallimagedata.w = jQuery( this ).width();
				smallimagedata.h = jQuery( this ).height();


				//non viene fatta assegnazione alla variabile globale
				smallimagedata.h = jQuery( this ).height();
				smallimagedata.pos = jQuery( this ).offset();
				smallimagedata.pos.l = jQuery( this ).offset().left;
				smallimagedata.pos.t = jQuery( this ).offset().top;
				smallimagedata.pos.r = smallimagedata.w + smallimagedata.pos.l;
				smallimagedata.pos.b = smallimagedata.h + smallimagedata.pos.t;

				//per sicurezza setto l'altezza e la width dell'href
				a.height(smallimagedata.h);
				a.width(smallimagedata.w);


				//PRELOAD IMAGES
				if(settings.preloadImages)
				{
					largeimage.loadimage();
				}



			};



			return this;
		};



		//Lens
		function Lens()
		{


			//creating element and adding class
			this.node = document.createElement("div");
			jQuery(this.node).addClass('jqZoomPup');

			this.node.onerror = function() {
				jQuery( lens.node ).remove();
				lens = new Lens();
				lens.activate() ;
			};




			//funzione privata per il caricamento dello zoom
			this.loadlens = function()
			{


				switch(settings.zoomType)
				{
					case 'reverse':
						this.image = new Image();
						this.image.src = smallimage.node.src; // fires off async
						this.node.appendChild( this.image );
						jQuery( this.node ).css({'opacity' : 1});
					break;
					case 'innerzoom':

						this.image = new Image();
						this.image.src = largeimage.node.src; // fires off async
						this.node.appendChild( this.image );
						jQuery( this.node ).css({'opacity' : 1});
					break
					default:
					break;
				}



				switch(settings.zoomType)
				{
					case 'innerzoom':
						lensdata.w = smallimagedata.w;
						lensdata.h = smallimagedata.h;
					break;
					default:
						lensdata.w = (settings.zoomWidth)/scale.x;
						lensdata.h = (settings.zoomHeight)/scale.y;
					break;
				}

			jQuery( this.node ).css({
					width: lensdata.w + 'px',
					height: lensdata.h + 'px',
					position: 'absolute',
					/*cursor: 'crosshair',*/
					display: 'none',
					//border: '1px solid blue'
					borderWidth: 1+'px'
				});
			a.append(this.node);
			}
			return this;
		};

		Lens.prototype.activate = function()
		{
			//carico la lente
			this.loadlens();

			switch(settings.zoomType)
			{
				case 'reverse':
					img.css({'opacity' : settings.imageOpacity});

					(settings.alwaysOn) ? lens.center() : lens.setposition(null);
					//lens.center();
					//bindo ad a il mousemove della lente
					a.bind( 'mousemove', function(e)
					{
						mousepos.x = e.pageX;
						mousepos.y = e.pageY;
						lens.setposition( e );
					});
				break;
				case 'innerzoom':

					//	lens = new Lens();
					//	lens.activate();

					jQuery( this.node ).css({top : 0 ,left: 0});
				   	if(settings.title)
					{
						ZoomTitleObj.loadtitle();
					}

					largeimage.setcenter();

				   	a.bind( 'mousemove', function(e)
				   	{
						mousepos.x = e.pageX;
						mousepos.y = e.pageY;
						largeimage.setinner( e );

					/*if(settings.zoomType == 'innerzoom' && running)
					{
						jQuery(a).mousemove(function(){
							if(jQuery('div.jqZoomPup').length <= 0)
							{
								lens = new Lens();
								lens.activate();
							}
						});
					}*/

						/*if(jQuery('div.jqZoomPup').length <= 0)
							{
								lens = new Lens();
								lens.activate();
							}*/

					});
				break;
				default:
					/*jQuery(document).mousemove(function(e){
					if(isMouseDown && dragstatus != false){
					lens.setposition( e );
					}
					});
					lens.center()


					dragstatus = 'on'
					jQuery(document).mouseup(function(e){
					if(isMouseDown && dragstatus != false){
						isMouseDown = false;
						dragstatus = false;

					}
					});

					jQuery(this.node).mousedown(function(e){
					jQuery('div.jqZoomPup').css("cursor", "move");
					jQuery(this.node).css("position", "absolute");

				// set z-index
					jQuery(this.node).css("z-index", parseInt( new Date().getTime()/1000 ));
					if(jQuery.browser.safari)
					{
						jQuery(a).css("cursor", "move");
					}
					isMouseDown    = true;
					dragstatus = 'on';
					lens.setposition( e );
					});
					*/


					(settings.alwaysOn) ? lens.center() : lens.setposition(null);

					//bindo ad a il mousemove della lente
					jQuery(a).bind( 'mousemove', function(e)
					{

						mousepos.x = e.pageX;
						mousepos.y = e.pageY;
						lens.setposition( e );
					});

				break;
			}


			return this;
		};

		Lens.prototype.setposition = function( e)
		{


			if(e)
			{
				mousepos.x = e.pageX;
				mousepos.y	= e.pageY;
			}

			if(firstime == 0)
			{
			 	var lensleft = (smallimagedata.w)/2 - (lensdata.w)/2 ;
			 	var lenstop = (smallimagedata.h)/2 - (lensdata.h)/2 ;
				//ADDED

				jQuery('div.jqZoomPup').show()
				if(settings.lens)
				{
					this.node.style.visibility = 'visible';
				}
				else
				{
					this.node.style.visibility = 'hidden';
					jQuery('div.jqZoomPup').hide();
				}
				//ADDED
				firstime = 1;

			}else
			{
				var lensleft = mousepos.x - smallimagedata.pos.l - (lensdata.w)/2 ;
				var lenstop = mousepos.y - smallimagedata.pos.t -(lensdata.h)/2 ;
			}


				//a sinistra
				if(overleft())
				{
					lensleft = 0  + bleft;
				}else
				//a destra
				if(overright())
				{
					if(jQuery.browser.msie)
					{
					lensleft = smallimagedata.w - lensdata.w  + bleft + 1  ;
					}else
					{
					lensleft = smallimagedata.w - lensdata.w  + bleft - 1  ;
					}


				}

				//in alto
				if(overtop())
				{
					lenstop = 0 + btop ;
				}else
				//sotto
				if(overbottom())
				{

					if(jQuery.browser.msie)
					{
					lenstop = smallimagedata.h - lensdata.h  + btop + 1 ;
					}else
					{
					lenstop = smallimagedata.h - lensdata.h - 1 + btop  ;
					}

				}
				lensleft = parseInt(lensleft);
				lenstop = parseInt(lenstop);

				//setto lo zoom ed un eventuale immagine al centro
				jQuery('div.jqZoomPup',a).css({top: lenstop,left: lensleft });

				if(settings.zoomType == 'reverse')
				{
					jQuery('div.jqZoomPup img',a).css({'position': 'absolute','top': -( lenstop - btop +1) ,'left': -(lensleft - bleft +1)  });
				}

				this.node.style.left = lensleft + 'px';
				this.node.style.top = lenstop + 'px';

				//setto l'immagine grande
				largeimage.setposition();

				function overleft() {
					return mousepos.x - (lensdata.w +2*1)/2  - bleft < smallimagedata.pos.l;
				}

				function overright() {

					return mousepos.x + (lensdata.w + 2* 1)/2  > smallimagedata.pos.r + bleft ;
				}

				function overtop() {
					return mousepos.y - (lensdata.h + 2* 1)/2  - btop < smallimagedata.pos.t;
				}

				function overbottom() {
					return mousepos.y + (lensdata.h + 2* 1)/2    > smallimagedata.pos.b + btop;
				}

			return this;
		};


		//mostra la lente al centro dell'immagine
		Lens.prototype.center = function()
		{
			jQuery('div.jqZoomPup',a).css('display','none');
			var lensleft = (smallimagedata.w)/2 - (lensdata.w)/2 ;
			var lenstop = (smallimagedata.h)/2 - (lensdata.h)/2;
			this.node.style.left = lensleft + 'px';
			this.node.style.top = lenstop + 'px';
			jQuery('div.jqZoomPup',a).css({top: lenstop,left: lensleft });

			if(settings.zoomType == 'reverse')
			{
				/*if(jQuery.browser.safari){
					alert('safari');
					alert(2*bleft);
					jQuery('div.jqZoomPup img',a).css({'position': 'absolute','top': -( lenstop - btop +1) ,'left': -(lensleft - 2*bleft)  });
				}else
				{*/
					jQuery('div.jqZoomPup img',a).css({'position': 'absolute','top': -(lenstop - btop + 1) ,'left': -( lensleft  - bleft +1)   });
				//}
			}

			largeimage.setposition();
			if(jQuery.browser.msie)
			{
				jQuery('div.jqZoomPup',a).show();
			}else
			{
				setTimeout(function(){jQuery('div.jqZoomPup').fadeIn('fast');},10);
			}
		};


		//ritorna l'offset
		Lens.prototype.getoffset = function() {
			var o = {};
			o.left = parseInt(this.node.style.left) ;
			o.top =  parseInt(this.node.style.top) ;
			return o;
		};

		//rimuove la lente
		Lens.prototype.remove = function()
		{

			if(settings.zoomType == 'innerzoom')
			{
				jQuery('div.jqZoomPup',a).fadeOut('fast',function(){/*jQuery('div.jqZoomPup img').remove();*/jQuery(this).remove();});
			}else
			{
				//jQuery('div.jqZoomPup img').remove();
				jQuery('div.jqZoomPup',a).remove();
			}
		};

		Lens.prototype.findborder = function()
		{
			var bordertop = '';
			bordertop = jQuery('div.jqZoomPup').css('borderTop');
			//alert(bordertop);
			lensbtop = '';
			var borderleft = '';
			borderleft = jQuery('div.jqZoomPup').css('borderLeft');
			lensbleft = '';
			if(jQuery.browser.msie)
			{
				var temp = bordertop.split(' ');

				bordertop = temp[1];
				var temp = borderleft.split(' ');
				borderleft = temp[1];
			}

			if(bordertop)
			{
				for(i=0;i<3;i++)
				{
					var x = [];
					x = bordertop.substr(i,1);

					if(isNaN(x) == false)
					{
						lensbtop = lensbtop +''+ bordertop.substr(i,1);
					}else
					{
						break;
					}
				}
			}

			if(borderleft)
			{
				for(i=0;i<3;i++)
				{
					if(!isNaN(borderleft.substr(i,1)))
					{
						lensbleft = lensbleft + borderleft.substr(i,1)
					}else
					{
						break;
					}
				}
			}


			lensbtop = (lensbtop.length > 0) ? eval(lensbtop) : 0;
			lensbleft = (lensbleft.length > 0) ? eval(lensbleft) : 0;
		}

		//LARGEIMAGE
		function Largeimage( img )
		{
            this.node = img[0];
			/*if(settings.preloadImages)
			{
			 	preload.push(new Image());
				preload.slice(-1).src = url ;
			}*/

			this.loadimage = function()
			{                
                this.node.src = img[0].src;
                if(!this.node)
				this.node = new Image();

				this.node.style.position = 'absolute';
				this.node.style.display = 'none';
				this.node.style.left = '-5000px';
				this.node.style.top = '10px';
				loader = new Loader();

				if(settings.showPreload && !preloadshow)
				{
					loader.show();
					preloadshow = true;
				}

				document.body.appendChild( this.node );
			}

			this.node.onload = function()
			{
				this.style.display = 'block';
				var w = Math.round(jQuery(this).width());
				var	h = Math.round(jQuery(this).height());

				this.style.display = 'none';

				//setting scale
				scale.x = (w / smallimagedata.w);
				scale.y = (h / smallimagedata.h);





				if(jQuery('div.preload').length > 0)
				{
					jQuery('div.preload').remove();
				}

				largeimageloaded = true;

				if(settings.zoomType != 'innerzoom' && running){
					stage = new Stage();
					stage.activate();
				}

				if(running)
				{
				//alert('in');
				lens = new Lens();

				lens.activate() ;

				}
				//la attivo

				if(jQuery('div.preload').length > 0)
				{
					jQuery('div.preload').remove();
				}
			}
			return this;
		}


		Largeimage.prototype.setposition = function()
		{
          	this.node.style.left = Math.ceil( - scale.x * parseInt(lens.getoffset().left) + bleft) + 'px';
			this.node.style.top = Math.ceil( - scale.y * parseInt(lens.getoffset().top) +btop) + 'px';
		};

		//setto la posizione dell'immagine grande nel caso di innerzoom
		Largeimage.prototype.setinner = function(e) {
          	this.node.style.left = Math.ceil( - scale.x * Math.abs(e.pageX - smallimagedata.pos.l)) + 'px';
			this.node.style.top = Math.ceil( - scale.y * Math.abs(e.pageY - smallimagedata.pos.t)) + 'px';
			jQuery('div.jqZoomPup img',a).css({'position': 'absolute','top': this.node.style.top,'left': this.node.style.left  });
		};


		Largeimage.prototype.setcenter = function() {
          	this.node.style.left = Math.ceil(- scale.x * Math.abs((smallimagedata.w)/2)) + 'px';
			this.node.style.top = Math.ceil( - scale.y * Math.abs((smallimagedata.h)/2)) + 'px';


			jQuery('div.jqZoomPup img',a).css({'position': 'absolute','top': this.node.style.top,'left': this.node.style.left  });
		};


		//STAGE
		function Stage()
		{

			var leftpos = smallimagedata.pos.l;
			var toppos = smallimagedata.pos.t;
			//creating element and class
			this.node = document.createElement("div");
			jQuery(this.node).addClass('jqZoomWindow');

			jQuery( this.node )
				.css({
					position: 'absolute',
					width: Math.round(settings.zoomWidth) + 'px',
					height: Math.round(settings.zoomHeight) + 'px',
					display: 'none',
					zIndex: 10000,
					overflow: 'hidden'
				});

			//fa il positionamento
		    switch(settings.position)
		    {
		    	case "right":

				leftpos = (smallimagedata.pos.r + Math.abs(settings.xOffset) + settings.zoomWidth < screen.width)
				? (smallimagedata.pos.l + smallimagedata.w + Math.abs(settings.xOffset))
				: (smallimagedata.pos.l - settings.zoomWidth - Math.abs(settings.xOffset));

				topwindow = smallimagedata.pos.t + settings.yOffset + settings.zoomHeight;
				toppos = (topwindow < screen.height && topwindow > 0)
				?  smallimagedata.pos.t + settings.yOffset
				:  smallimagedata.pos.t;

		    	break;
		    	case "left":

				leftpos = (smallimagedata.pos.l - Math.abs(settings.xOffset) - settings.zoomWidth > 0)
				? (smallimagedata.pos.l - Math.abs(settings.xOffset) - settings.zoomWidth)
				: (smallimagedata.pos.l + smallimagedata.w + Math.abs(settings.xOffset));

				topwindow = smallimagedata.pos.t + settings.yOffset + settings.zoomHeight;
				toppos = (topwindow < screen.height && topwindow > 0)
				?  smallimagedata.pos.t + settings.yOffset
				:  smallimagedata.pos.t;

		    	break;
		    	case "top":

				toppos = (smallimagedata.pos.t - Math.abs(settings.yOffset) - settings.zoomHeight > 0)
				? (smallimagedata.pos.t - Math.abs(settings.yOffset) - settings.zoomHeight)
				: (smallimagedata.pos.t + smallimagedata.h + Math.abs(settings.yOffset));


				leftwindow = smallimagedata.pos.l + settings.xOffset + settings.zoomWidth;
				leftpos = (leftwindow < screen.width && leftwindow > 0)
				? smallimagedata.pos.l + settings.xOffset
				: smallimagedata.pos.l;

		    	break;
		    	case "bottom":


				toppos = (smallimagedata.pos.b + Math.abs(settings.yOffset) + settings.zoomHeight < jQuery('body').height())
				? (smallimagedata.pos.b + Math.abs(settings.yOffset))
				: (smallimagedata.pos.t - settings.zoomHeight - Math.abs(settings.yOffset));


				leftwindow = smallimagedata.pos.l + settings.xOffset + settings.zoomWidth;
				leftpos = (leftwindow < screen.width && leftwindow > 0)
				? smallimagedata.pos.l + settings.xOffset
				: smallimagedata.pos.l;

		    	break;
		    	default:

				leftpos = (smallimagedata.pos.l + smallimagedata.w + settings.xOffset + settings.zoomWidth < screen.width)
				? (smallimagedata.pos.l + smallimagedata.w + Math.abs(settings.xOffset))
				: (smallimagedata.pos.l - settings.zoomWidth - Math.abs(settings.xOffset));

				toppos = (smallimagedata.pos.b + Math.abs(settings.yOffset) + settings.zoomHeight < screen.height)
				? (smallimagedata.pos.b + Math.abs(settings.yOffset))
				: (smallimagedata.pos.t - settings.zoomHeight - Math.abs(settings.yOffset));

		    	break;
		    }

			this.node.style.left = leftpos + 'px';
			this.node.style.top = toppos + 'px';
			return this;
		}


		Stage.prototype.activate = function()
		{

			if ( !this.node.firstChild )
					this.node.appendChild( largeimage.node );


			if(settings.title)
			{
				ZoomTitleObj.loadtitle();
			}



			document.body.appendChild( this.node );


			switch(settings.showEffect)
			{
				case 'show':
					jQuery(this.node).show();
				break;
				case 'fadein':
					jQuery(this.node).fadeIn(settings.fadeinSpeed);
				break;
				default:
					jQuery(this.node).show();
				break;
			}

			jQuery(this.node).show();

            if (jQuery.browser.msie && jQuery.browser.version < 7) {
	        this.ieframe = jQuery('<iframe class="zoom_ieframe" frameborder="0" src="#"></iframe>')
	          .css({ position: "absolute", left:this.node.style.left,top:this.node.style.top,zIndex: 99,width:settings.zoomWidth,height:settings.zoomHeight })
	          .insertBefore(this.node);
	     	 };


			largeimage.node.style.display = 'block';
		}

		Stage.prototype.remove = function() {
			switch(settings.hideEffect)
			{
				case 'hide':
					jQuery('.jqZoomWindow').remove();
				break;
				case 'fadeout':
					jQuery('.jqZoomWindow').fadeOut(settings.fadeoutSpeed);
				break;
				default:
					jQuery('.jqZoomWindow').remove();
				break;
			}
		}

		function zoomTitle()
		{

			this.node =  jQuery('<div />')
				.addClass('jqZoomTitle')
				.html('' + ZoomTitle +'');

			this.loadtitle = function()
			{
				if(settings.zoomType == 'innerzoom')
				{
					jQuery(this.node)
					.css({position: 'absolute',
						  top: smallimagedata.pos.b +3,
						  left: (smallimagedata.pos.l+1),
						  width:smallimagedata.w
						  })
					.appendTo('body');
				}else
				{
					jQuery(this.node).appendTo(stage.node);
				}
			};
		}

		zoomTitle.prototype.remove = function() {
			jQuery('.jqZoomTitle').remove();
		}


		function Loader()
		{

			this.node = document.createElement("div");
			jQuery(this.node).addClass('preload');
			jQuery(this.node).html(settings.preloadText);//appendo il testo

			jQuery(this.node )
				.appendTo("body")
				.css('visibility','hidden');



			this.show = function()
			{
				switch(settings.preloadPosition)
				{
					case 'center':
						loadertop =  smallimagedata.pos.t + (smallimagedata.h - jQuery(this.node ).height())/2;
						loaderleft = smallimagedata.pos.l + (smallimagedata.w - jQuery(this.node ).width())/2;
					break;
					default:
					var loaderoffset = this.getoffset();
					loadertop = !isNaN(loaderoffset.top) ? smallimagedata.pos.t + loaderoffset.top : smallimagedata.pos.t + 0;
					loaderleft = !isNaN(loaderoffset.left) ? smallimagedata.pos.l + loaderoffset.left : smallimagedata.pos.l + 0;
					break;
				}

				//setting position
				jQuery(this.node).css({
							top: loadertop  ,
							left: loaderleft ,
							position: 'absolute',
							visibility:'visible'
					    	});
			}
			return this;
		}

		Loader.prototype.getoffset = function()
		{
			var o = null;
			o = jQuery('div.preload').offset();
			return o;
		}

		});
	}
})(jQuery);

	function trim(stringa)
	{
	    while (stringa.substring(0,1) == ' '){
	        stringa = stringa.substring(1, stringa.length);
	    }
	    while (stringa.substring(stringa.length-1, stringa.length) == ' '){
	        stringa = stringa.substring(0,stringa.length-1);
	    }
	    return stringa;
	}