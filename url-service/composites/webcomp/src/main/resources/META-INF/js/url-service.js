$(function() {
	$(".keywords").each(function(i){
		var readonly = $(this).hasClass("readonly")
		
		var keywords = $(this).val().split(" ")
		$(this).val("")
		var input = $(this)
		
		var container = $("<span></span>")
		input.after(container)
		$(keywords).each(function() {
			if($.trim(this.toString()) != "") {
				afterElm = addKeyword(this.toString(), input, container, readonly)
			}
		})
		
		var newKeywordSpan = $("<input value=''></input>")
		newKeywordSpan.keydown(function(e) {
			if(e.keyCode == 13) {
				var val = " " + input.val() + " "
				var name = $(this).val()
				// check if keyword already exists
				if(val.indexOf(" " + name + " ") == -1) {
					afterElm = addKeyword(name, input, container, readonly)
				}
				$(this).val("")
                e.preventDefault();
                return false;
			}
		})
		
		container.after(newKeywordSpan)

		// convert the input to a hidden field
		var marker = $('<span />').insertBefore(input)
		input.detach().attr('type', 'hidden').insertAfter(marker)
		marker.remove()
	})
});

function addKeyword(name, input, container, readonly) {
	var span = $("<span class='keyword'>" + name + "</span>")
	span.data("value", name)

	if(readonly) {
		span.addClass("readonly")
	} else {
		var removeKeywordSpan = $("<span class='remove-keyword'>X</span>")
		removeKeywordSpan.click(function() {
			var val = " " + input.val() + " ";
			input.val($.trim(val.replace(" " + name + " ", " ")))
			span.remove()
		})
		
		span.append(removeKeywordSpan)
	}
	input.val($.trim(input.val().toString() + " " + name))
	
	container.append(span)
}