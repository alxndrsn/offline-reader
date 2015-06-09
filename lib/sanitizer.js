var Sanitizer = (function() {
  'use strict';

  // From: https://stackoverflow.com/questions/295566/sanitize-rewrite-html-on-the-client-side/430240#430240
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
  var multipleSpaces = new RegExp('\\s+', 'gi');

  function removeTags(html) {
    var oldHtml;
    do {
      oldHtml = html;
      html = html.replace(tagOrComment, '');
    } while (html !== oldHtml);
    return html.replace(/</g, '&lt;');
  }

  function compressWhitespace(html) {
    var oldHtml;
    do {
      oldHtml = html;
      html = html.replace(multipleSpaces, ' ');
    } while (html !== oldHtml);
    return html;
  }

  function getTitle(doc) {
    var cleanContent = compressWhitespace(
        removeTags(doc.content));
    return cleanContent.length > 100 ?
      cleanContent.substring(0, 99) + "…" :
      cleanContent;
  }

  return { removeTags:removeTags,
      compressWhitespace:compressWhitespace,
      getTitle:getTitle };
}());
