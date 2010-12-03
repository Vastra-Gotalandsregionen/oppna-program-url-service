jQuery.fn.reverse = function() {
    return this.pushStack(this.get().reverse(), arguments);
};

$(function() {
	$(".keywords option").reverse().each(function(i){
		var select = $(this).parent();
		var readonly = select.hasClass("readonly")
		
		var span = $("<span class='keyword'>" + $(this).text() + "</span>")
		span.data("value", $(this).val())
		span.data("select", select)
		if(readonly) span.addClass("readonly")
		
		if($(this).attr("selected")) {
			span.toggleClass("selected")
		}
		
		if(!readonly) {
			span.click(function() {
				$(this).toggleClass("selected")
			
				$("option[value='" + $(this).data("value") + "']").each(function() {
					if($(this).attr("selected")) {
						$(this).attr("selected", "")
					} else {
						$(this).attr("selected", "selected")
					} 
				})
			});
		}
		select.after(span)
		
	
	})
	
	$(".keywords").each(function(i) {
		$(this).hide()
	})
});