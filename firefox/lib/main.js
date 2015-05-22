var contextMenu = require("sdk/context-menu");
var sendToServer = function(content) {
	len = content.length;
	console.log(len < 100 ? content : content.substring(0, 99) + "…");
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
