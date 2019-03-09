/*
   G.E.T.
*/

window.Coi = Backbone.Model.extend({
	
		defaults: {
        id: null,
        name: ' ',        
    },

    // initialize:function () {}

});

window.CoiCollection = Backbone.Collection.extend({

	
    model:Coi,
    
    fetch:function () {
        var self = this;
        //console.log("fetching switch list")
        $.ajax({
               url:hackBase + "/authorization/authorized/json",
               dataType:"json",
               success:function (data) {
               //console.log("fetched  switch list: " + data.length);
               //console.log(data);
               var old_ids = self.pluck('id');
               //console.log("old_ids" + old_ids);
               
               _.each(data, function(c) {
                      old_ids = _.without(old_ids, c['id']);
                      self.add({id: c['id'], name: c['name']})
                      });
               
               // old_ids now holds switches that no longer exist; remove them
               //console.log("old_ids" + old_ids);
               _.each(old_ids, function(c) {
                      console.log("removing switch " + c);
                      self.remove({id:c});
                      });
               },
               });
        },        
});
