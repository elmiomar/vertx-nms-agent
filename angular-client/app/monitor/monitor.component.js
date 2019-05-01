angular.module('monitor').component('monitor', {
    templateUrl: 'monitor/monitor.template.html',
    controller: ['$scope', '$timeout', '$http', '$mdDialog', '$mdToast', 'Log', function MonitorController($scope, $timeout, $http, $mdDialog, $mdToast, Log) {
        $scope.logs = [];

        $scope.getLogs = function () {
            Log.getLogs().then(function (response) {
                    $scope.logs = response.data.logs.reverse();
                },
                function (response) {
                    if (response.data === null) {
                        $scope.showToast("Connection failed! Check if the server is up.");
                    }
                });
        };

        $scope.getLogs();

        var eb = new EventBus("http://localhost:8080/eventbus");

        eb.onopen = function () {
            console.log('eb.onopen() call');
            eb.registerHandler("nms.web.monitor", function (error, message) {
                console.log('eb.registerHandler() call');
                var log = message.body;
                console.log(log);
                $timeout(function () {
                    $scope.logs.unshift(log);
                    $scope.createLog(log);
                });

                // $scope.getLogs();
            });
        };





        $scope.createLog = function (log) {
            Log.createLog(log).then(function successCallback(response) {
                console.log(response);
            }, function errorCallback(response) {
                console.log(response);
            });
        };

        // show toast message
        $scope.showToast = function (message) {
            $mdToast.show(
                $mdToast.simple()
                .textContent(message)
                .hideDelay(3000)
                .position("bottom center")
            );
        };

        $scope.deleteAllLogs = function () {
            Log.deleteAllLogs().then(function successCallback(response) {
                    // tell the user face record was deleted
                    $scope.showToast("All logs were deleted successfully!");
                    // refresh the list
                    $scope.getLogs();
                },
                function errorCallback(response) {
                    $scope.showToast("Unable to delete all logs entries.");
                });
        };


        $scope.deleteAllLogsAndConfirm = function (event) {
            if ($scope.logs.length > 0) {
                var confirm = $mdDialog.confirm()
                    .title('Are you sure?')
                    .textContent('All log entries will be deleted.')
                    .targetEvent(event)
                    .ok('Yes')
                    .cancel('No');
                // show dialog
                $mdDialog.show(confirm).then(
                    // 'Yes' button
                    function () {
                        $scope.deleteAllLogs();
                    },
                    // 'No' button
                    function () {
                        // hide dialog
                    }
                );
            } else {
                var alert = $mdDialog.alert()
                    .title('Empty list')
                    .textContent('There are no registred log entries to delete.')
                    .targetEvent(event)
                    .ok('Close');
                // show dialog
                $mdDialog.show(alert).finally(function () {
                    alert = undefined;
                });
            }
        };

    }]
});