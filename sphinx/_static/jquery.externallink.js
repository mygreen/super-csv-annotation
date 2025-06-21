/**
 * jQuery Plugin
 *  外部リンクに、taget="_blank" を付与し、かつアイコン画像も付与する。
 */
(function( $ ) {
	
	$.fn.externalLink = function( options ) {
	
		var settings = $.extend( {
			cssClass : 'external',
			iconUrl : 'images/externalink.png',
			startUrl : ''
		}, options);
		
		$(this).find('a').each(function() {
			var href = $(this).attr('href');
			if(href != undefined && href != null && href != "") {
				
				if(href.indexOf('http://') == 0 || href.indexOf('https://') == 0
						|| (settings.startUrl.length > 0 && href.indexOf(settings.startUrl) == 0) ) {
					
					$(this).attr("target", "_blank");
					$(this).addClass(settings.cssClass);
					
					if(settings.iconUrl.length > 0) {
						var img = $('<img>').addClass('icon_externallink').attr('src', settings.iconUrl);
						$(this).append(img);
					}
					
				}
				
			}
		});
		
		return this;
	
	};
})( jQuery );


