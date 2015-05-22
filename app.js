var couchapp = require('couchapp'),
    path = require('path');

d = {
  _id: '_design/app',
  views: {},
  lists: {},
  shows: {}
}

module.exports = d;

d.views.articles = {
  map: function(doc) {
    // From: https://stackoverflow.com/questions/295566/sanitize-rewrite-html-on-the-client-side/430240#430240
    var sanitizer = (function() {
      var tagBody = '(?:[^"\'>]|"[^"]*"|\'[^\']*\')*';

      var tagOrComment = new RegExp(
          '<(?:'
          // Comment body.
          + '!--(?:(?:-*[^->])*--+|-?)'
          // Special "raw text" elements whose content should be elided.
          + '|script\\b' + tagBody + '>[\\s\\S]*?</script\\s*'
          + '|style\\b' + tagBody + '>[\\s\\S]*?</style\\s*'
          // Regular name
          + '|/?[a-z]'
          + tagBody
          + ')>',
          'gi');
      function removeTags(html) {
        var oldHtml;
        do {
          oldHtml = html;
          html = html.replace(tagOrComment, '');
        } while (html !== oldHtml);
        return html.replace(/</g, '&lt;');
      }

      return { sanitize:removeTags };
    }());
    if(doc.type === 'article') {
      var cleanContent = sanitizer.sanitize(doc.content);
      var title = cleanContent.length > 100 ?
        cleanContent.substring(0, 99) + "…" :
        cleanContent;
      emit(doc._id, title);
    }
  }
}
