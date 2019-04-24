angular.
module('core.logs').
factory('Log', ['$http', function ($http) {

    var service = {};
    var urlBase = 'http://localhost:8080/api/logs';


    // get all logs
    service.getLogs= function () {
        return $http.get(urlBase);
    };

    // get one log
    service.readOneLog = function (id) {
        return $http.get(urlBase + '/' + id);
    };

    // create a new log
    service.createLog = function (log) {
        console.log('calling Log.createLog');
        return $http({
            method: 'POST',
            data: {
                'timestamp': log.timestamp,
                'verticle': log.verticle,
                'level': log.level,
                'message': log.message
            },
            url: urlBase
        });
    };

    // delete log
    service.deleteLog = function (id) {
        return $http({
            method: 'DELETE',
            url: urlBase + '/' + id
        });
    };

    // delete all logs
    service.deleteAllLogs = function () {
        return $http({
            method: 'DELETE',
            url: urlBase
        });
    };

    return service;
}]);