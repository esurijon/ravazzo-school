function CashierController($scope, cashierService) {

	$scope.tables = {};
	$scope.waiters = {};


	$scope.$on('TableMessage', function(evt, data) {
		$scope.tables[data.table].messages.push(data);
	});

	$scope.$on('WsOpen', function() {
		cashierService.getRestoInfo();
	});
	
	$scope.$on('RestoInfo', function(evt, data) {
		$scope.tables = data.tables;
		for ( var tableId in $scope.tables) {
			$scope.tables[tableId].messages = [];
		}
		$scope.waiters = data.waiters;
	});

	$scope.$on('UserStatus', function(evt, data) {
		var id = data.user.id;
		$scope.waiters[id] = data;
	});
	
	$scope.$on('TableStatus', function(evt, data) {
		var id = data.table.number;
		data.messages = data.isActive ? [] : $scope.tables[id].messages;
		$scope.tables[id] = data;
	});
	
	$scope.send = function(tableNumber) {
		cashierService.sendMessage(tableNumber*1, $scope.tables[tableNumber].message);
		$scope.tables[tableNumber].message = null;
	}

	$scope.toggleTableServiceSession = function(id) {
		if($scope.tables[id].isActive) {
			cashierService.openTable(id);
		} else {
			cashierService.closeTable(id);
		}
	}
	
}
