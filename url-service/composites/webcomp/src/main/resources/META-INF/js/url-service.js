        $(function() {
            $("input.tags").each(function() {
                var input = $(this)
                var div = $("<div class='tags' />")
                div.width(input.width())
                
                if($.trim(input.val()) != "") {
                    $(input.val().split(" ")).each(function() {
                		div.append(createTag(this.toString(), input))
                    })
                }
                
                var tagInput = $("<input />")
                tagInput.keydown(function(e) {
                    var name = $(this).val()
        			if(e.keyCode == 13 && $.trim(name) != "") {
        				var val = " " + input.val() + " "
        				
        				// check if keyword already exists
        				if(val.indexOf(" " + name + " ") == -1) {
        				    input.val(input.val() + " " + name)
        					$(this).before(createTag(name, input))
        				}
        				$(this).val("")
        				$(this).autocomplete( "close" )
                        e.preventDefault();
                        return false;
        			}
        		})

        		tagInput.autocomplete({ 
        			minLength: 3,
        			source: function(request, response) {
        				$.getJSON('../keywords?prefix=' + request.term, function(data) {
	        				var names = []
                      		$(data).each(function() {
                      			names[names.length] = this.name
                      		})
                  			response(names)
                    	});

        				
        			},
        			select: function(event) { event.preventDefault(); return false }	
        		});
        		
                div.append(tagInput)
                
                input.after(div)
                input.hide()
            })
        })
        
        function createTag(name, input) {
            var span = $("<span>" + name + "</span>")
            
            var removeTag = $("<span class='x'>x</span>")
            removeTag.click(function() {
    			var val = " " + input.val() + " ";
    			input.val($.trim(val.replace(" " + name + " ", " ")))
    			span.remove()
    		})
    		span.append(removeTag)
            return span
        }
