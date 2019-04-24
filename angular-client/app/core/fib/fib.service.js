angular.
module('core.fib').
factory('Fib', function ($http) {

    var service = {};
    var urlBase = "http://localhost:8080/api/fib";

    // get all fib entries
    service.getFibEntries = function () {
        return $http.get(urlBase);
    };


    // get one fib entry
    service.readOneFibEntry = function (id) {
        return $http.get(urlBase + '/' + id);
    };


    // create a fib entry
    service.createFibEntry = function ($scope) {
        console.log('calling Fib.createFibEntry( ' + $scope.prefix + ',' + $scope.face + ',' + $scope.cost + ')');
        return $http({
            method: 'POST',
            data: {
                'prefix': $scope.prefix,
                'faceId': $scope.face,
                'cost': $scope.cost
            },
            url: urlBase
        });
    };

    // update a fib entry
    service.updateFibEntry = function ($scope) {
        console.log('calling Fib.updateFibEntry( ' + $scope.id + ',' + $scope.prefix + ',' + $scope.face + $scope.cost + ')');
        return $http({
            method: 'PUT',
            data: {
                'prefix': $scope.prefix,
                'face': $scope.face,
                'cost': $scope.cost
            },
            url: urlBase + '/' + $scope.id
        });
    };

    // delete fib entry
    service.deleteFibEntry = function (id) {
        console.log('calling Fib.deleteFibEntry( ' + id + ')');
        return $http({
            method: 'DELETE',
            url: urlBase + '/' + id
        });
    };

    // delete all fib entries
    service.deleteAllFaces = function () {
        console.log('calling Fib.deleteAllFaces()');
        return $http({
            method: 'DELETE',
            url: urlBase
        });
    };

    return service;
});