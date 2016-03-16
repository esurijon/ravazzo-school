function WaiterController($scope, waiterService) {

	$scope.messages = [];

	$scope.$on('TableMessage', function(evt, data) {
		$scope.messages.push(data);
		$scope.lastMessageTableId = data.table;
	});

	$scope.send = function() {
		waiterService.sendMessage($scope.lastMessageTableId, $scope.message);
		$scope.message = null;
	}

}
