var contextMenu = require("sdk/context-menu");
var Request = require("sdk/request").Request;
var UUID = require('sdk/util/uuid');
var System = require("sdk/system");
var Base64 = require("sdk/base64");

var BASIC_AUTH_MATCHER = new RegExp(/^https?:\/\/(([^:]*:[^@]*)@)?.*/);

var sendToServer = function(content) {
	var len, shortContent, url, uuid;
	len = content.length;

	uuid = UUID.uuid().toString().substring(1, 36);
	url = System.staticArgs.COUCH_URL + "/" + uuid;

	headers = {};

	var basicAuth = BASIC_AUTH_MATCHER.exec(System.staticArgs.COUCH_URL);
	if(basicAuth && typeof basicAuth[1] !== 'undefined') {
		headers.Authorization = "Basic " +
				Base64.encode(basicAuth[2]);
	}

	var request = Request({
		url: url,
		contentType: 'application/json',
		content: JSON.stringify({ type: 'article', content: content,
				date_added: Date.now() }),
		onComplete: function(response) {
			var json = JSON.stringify(response.json);
			console.log(json);
		},
		anonymous: true,
		headers: headers
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
