var contextMenu = require("sdk/context-menu");
var Request = require("sdk/request").Request;
var UUID = require('sdk/util/uuid');
var System = require("sdk/system");

var sendToServer = function(content) {
	var len, uuid, url;
	len = content.length;
	console.log(len < 100 ? content : content.substring(0, 99) + "…");

	uuid = UUID.uuid().toString().substring(1, 36);
	url = System.staticArgs.COUCH_URL + "/" + uuid;
	console.log("Pushing content to url: " + url);
	var request = Request({
		url: url,
		contentType: 'application/json',
		content: JSON.stringify({ type: 'article', content: content,
				date_added: new Date().toString() }),
		onComplete: function(response) {
			var json = JSON.stringify(response.json);
			console.log(json);
		},
		anonymous: true
	});
	request.put();
}
var menuItem = contextMenu.Item({
	label: "Read offline",
	context: contextMenu.SelectorContext('article'),
	contentScript: 'self.on("click", function (node, data) {' +
			'  self.postMessage(node.innerHTML)' +
			'});',
	onMessage: function(content) {
		sendToServer(content);
	}
});
