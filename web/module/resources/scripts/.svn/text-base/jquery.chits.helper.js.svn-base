(function($){
	$.fn.loadAndCenter = function(data, opts) {
		var dlgOpts = {position:'center',height:'auto',modal:true}
		$.extend(dlgOpts, opts)
		this.empty().html(data).dialog(dlgOpts).dialog('open').scrollTop(0)
		return this
	}
})(jQuery)