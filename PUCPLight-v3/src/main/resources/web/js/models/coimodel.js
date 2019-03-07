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
    
    // instead of the collection loading its children, the switch will load them
    // initialize:function () {}
    
    /*
    fetch:function () {
        var self = this;
        //console.log("fetching coi list")
        $.ajax({
            
        });

    },
    */
});

