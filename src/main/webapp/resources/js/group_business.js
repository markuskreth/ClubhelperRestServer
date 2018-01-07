/**
 * 
 */
var allGroups = function(targetFunction) {

	var groups = groupStore.get();
	
	if(groups !== null) {
		targetFunction(groups);
	} else {
		repo(baseUrl + "group/", function(response) {
			groupStore.set(response);
			targetFunction(response);
		});
		
	}
};
