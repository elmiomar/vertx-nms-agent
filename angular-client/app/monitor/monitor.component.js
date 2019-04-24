angular.module('monitor').component('monitor', {
    templateUrl: 'monitor/monitor.template.html',
    controller: ['$scope', '$timeout', '$http', '$mdToast', 'Log', function MonitorController($scope, $timeout, $http, $mdToast, Log) {
        $scope.logs = [];

        var eb = new EventBus("http://localhost:8080/eventbus");

        $scope.getLogs = function () {
            Log.getLogs().then(function (response) {
                    $scope.logs = response.data.logs;
                },
                function (response) {
                    if (response.data === null) {
                        $scope.showToast("Connection failed! Check if the server is up.");
                    }
                });
        };

        $scope.getLogs();

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

        eb.onopen = function () {

        };

        eb.registerHandler("nms.web.monitor", function (error, message) {
            console.log('register handler');
            var log = message.body;
            // console.log(obj);
            // $timeout(function () {
            //     $scope.logs.unshift(obj);
            // });
            $scope.createLog(log);
            $scope.getLogs();
        });
    }]
});