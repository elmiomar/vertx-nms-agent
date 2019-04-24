angular.
module('core.faces').
factory('Face', ['$http', function ($http) {

    var service = {};
    var urlBase = 'http://localhost:8080/api/faces';


    // get all faces
    service.getFaces = function () {
        return $http.get(urlBase);
    };

    // get one face
    service.readOneFace = function (id) {
        return $http.get(urlBase + '/' + id);
    };

    // create a new face
    service.createFace = function ($scope) {
        console.log('calling Face.createFace( ' + $scope.id + ',' + $scope.remoteUri + ',' + $scope.localUri + ')');
        return $http({
            method: 'POST',
            data: {
                'faceId': $scope.id,
                'remoteUri': $scope.remoteUri,
                'localUri': $scope.localUri
            },
            url: urlBase
        });
    };

    // update a face
    service.updateFace = function ($scope) {
        console.log('calling Face.updateFace( ' + $scope.id + ',' + $scope.remoteUri + ',' + $scope.localUri + ')');
        return $http({
            method: 'PUT',
            data: {
                'faceId': $scope.id,
                'remoteUri': $scope.remoteUri,
                'localUri': $scope.localUri
            },
            url: urlBase + '/' + $scope.id
        });
    };

    // delete face
    service.deleteFace = function (id) {
        return $http({
            method: 'DELETE',
            url: urlBase + '/' + id
        });
    };

    // delete all faces
    service.deleteAllFaces = function () {
        return $http({
            method: 'DELETE',
            url: urlBase
        });
    };

    return service;
}]);