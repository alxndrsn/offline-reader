var ArticleHelper = (function() {
  'use strict';

  return {
    emit: function(doc) {
      var cleanContent = Sanitizer.compressWhitespace(
          Sanitizer.removeTags(doc.content));
      var title = cleanContent.length > 100 ?
        cleanContent.substring(0, 99) + "…" :
        cleanContent;
      emit(doc.date_added, title);
    }
  };
}());
