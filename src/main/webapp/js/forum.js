/*globals Mustache*/
$(document).ready(function(){
	
	var appendPosts = function(data) {
		// add all forum entries
		var template = $('#forumPostsTemplate').html();
		var html = Mustache.to_html(template, data);
		$('#posts').append(html);
	};
	
	var fetchForumEntries = function(link) {
		$.getJSON('/forum', {url: link}, function(data){
			appendPosts(data);
		});
	};
	
	var fetchForumPosts = function(link){
		$('#posts').html('');
		$.getJSON('/forum', {url: link}, function(data){
			appendPosts(data);
			
			var paginationLinks = data.paginationLinks;
			if (paginationLinks) {
				var x = 0;
				for(x in data.paginationLinks) {
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