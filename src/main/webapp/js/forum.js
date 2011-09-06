/*globals Mustache*/
$(document).ready(function(){
	
	var appendPosts = function(data) {
		// add all forum entries
		var template = $('#forumPostsTemplate').html();
		var html = Mustache.to_html(template, data);
		$('#posts').append(html);
		
		var progressVal = $('#progress').attr('value');
		$('#progress').attr('value', progressVal + 1);
	};
	
	var fetchForumEntries = function(link) {
		$.getJSON('/forum', {url: link}, function(data){
			appendPosts(data);
		});
	};
	
	var fetchForumPosts = function(link){
		$('#posts').html('');
		$('#progress').attr('value', 0);
		
		$.getJSON('/forum', {url: link}, function(data){
			appendPosts(data);
			
			var paginationLinks = data.paginationLinks;
			if (paginationLinks) {
				var x = 0;
				var count = paginationLinks.count;
				
				$('#progress').attr('max', count);
				
				for(x in paginationLinks) {
					var pageLink = paginationLinks[x];
					fetchForumEntries(pageLink);
				}
			}
		});
	};
	
	$('#forumUrl').keypress(function(evt){
		if (evt.which === 13) {
			var url = $(this).val();
			fetchForumPosts(url);
		}
	});
	
});