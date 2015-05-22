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
    if(doc.type === 'article') {
      var title = doc.content.length > 100 ?
        doc.content.substring(0, 99) + "…" :
        doc.content;
      emit(doc._id, title);
    }
  }
}
