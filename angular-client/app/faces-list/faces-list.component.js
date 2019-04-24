// Register the `facesList` component on the `phoneList` module,
angular.
module('facesList').
component('facesList', {
    // Note: The URL is relative to our `index.html` file
    templateUrl: 'faces-list/faces-list.template.1.html',
    controller: ['Face', '$scope', '$mdDialog', '$mdToast', '$http', function FacesListController(Face, $scope, $mdDialog, $mdToast, $http) {
        var self = this;
        self.orderProp = 'id';

        self.logs = [];

        $scope.readFaces = function () {
            $http.get("http://localhost:8080/api/faces").then(function (response) {
                    $scope.faces = response.data.faces;
                },
                function (response) {
                    if (response.data === null) {
                        $scope.showToast("Connection failed! Check if the server is up.");
                    }
                });
        };

        $scope.readFaces();

        // methods for dialog box
        function DialogController($scope, $mdDialog) {
            $scope.cancel = function () {
                $mdDialog.cancel();
            };
        }

        $scope.showCreateFaceForm = function (event) {
            console.log('called showCreateFaceForm()');
            $mdDialog.show({
                controller: DialogController,
                templateUrl: 'faces-list/create-face.template.html',
                parent: angular.element(document.body),
                clickOutsideToClose: true,
                scope: $scope,
                preserveScope: true,
                fullscreen: true // Only for -xs, -sm breakpoints.
            });
        };

        // clear form values
        $scope.clearFaceForm = function () {
            $scope.id = "";
            $scope.remoteUri = "";
            $scope.localUri = "";
        };

        // show toast message
        $scope.showToast = function (message) {
            $mdToast.show(
                $mdToast.simple()
                .textContent(message)
                .hideDelay(2000)
                .position("bottom center")
            );
        };


        // create new face
        $scope.createFace = function () {
            Face.createFace($scope).then(function successCallback(response) {
                // log response
                console.log(response);
                // tell the user new face was created
                $scope.showToast(response.data.message);
                // refresh the list
                $scope.readFaces();
                // close dialog
                $scope.cancel();
                // remove form values
                $scope.clearFaceForm();

            }, function errorCallback(response) {
                $scope.showToast("Unable to create face.");
            });
        };


        // retrieve record to fill out the form
        $scope.readOneFace = function (id) {
            console.log('calling readOneFace(' + id + ')');
            // get face to be edited
            Face.readOneFace(id).then(function successCallback(response) {

                console.log(response.data);
                // put the values in form
                $scope.id = response.data.face.id;
                $scope.remoteUri = response.data.face.remoteUri;
                $scope.localUri = response.data.face.localUri;

                $mdDialog.show({
                    controller: DialogController,
                    templateUrl: 'faces-list/read-one-face.template.html',
                    parent: angular.element(document.body),
                    clickOutsideToClose: true,
                    scope: $scope,
                    preserveScope: true,
                    fullscreen: true
                }).then(
                    function () {},

                    // user clicked 'Cancel'
                    function () {
                        // clear modal content
                        $scope.clearFaceForm();
                    }
                );

            }, function errorCallback(response) {
                $scope.showToast("Unable to retrieve record.");
            });

        };

        $scope.showUpdateFaceForm = function (id) {
            console.log('called showUpdateFaceForm()');

            Face.readOneFace(id).then(function successCallback(response) {
                    $scope.id = response.data.face.id;
                    $scope.remoteUri = response.data.face.remoteUri;
                    $scope.localUri = response.data.face.localUri;

                    $mdDialog.show({
                        controller: DialogController,
                        templateUrl: 'faces-list/update-face.template.html',
                        parent: angular.element(document.body),
                        clickOutsideToClose: true,
                        scope: $scope,
                        preserveScope: true,
                        fullscreen: true // Only for -xs, -sm breakpoints.
                    });
                },
                function errorCallback(response) {
                    $scope.showToast("Unable to update face.");
                });
        };

        // // update face record / save changes
        $scope.updateFace = function () {
            console.log('calling service Face.updateFace()');
            Face.updateFace($scope).then(function successCallback(response) {
                    // tell the user face record was updated
                    $scope.showToast(response.data.message);
                    // refresh the face list
                    $scope.readFaces();
                    // close dialog
                    $scope.cancel();
                    // clear modal content
                    $scope.clearFaceForm();
                },
                function errorCallback(response) {
                    $scope.showToast("Unable to update face.");
                });
        };

        // // delete face record / save changes
        $scope.deleteFace = function (id) {
            console.log('calling service Face.deleteFace()');
            Face.deleteFace(id).then(function successCallback(response) {
                    // tell the user face record was deleted
                    $scope.showToast(response.data.message);
                    // refresh the list
                    $scope.readFaces();
                },
                function errorCallback(response) {
                    $scope.showToast("Unable to delete face.");
                });
        };

        // cofirm face deletion
        $scope.deleteFaceAndConfirm = function (event, id) {
            // set id of record to delete
            $scope.id = id;
            // dialog settings
            var confirm = $mdDialog.confirm()
                .title('Are you sure?')
                .textContent('Face will be deleted.')
                .targetEvent(event)
                .ok('Yes')
                .cancel('No');
            // show dialog
            $mdDialog.show(confirm).then(
                // 'Yes' button
                function () {
                    // if user clicked 'Yes', delete product record
                    $scope.deleteFace(id);
                },
                // 'No' button
                function () {
                    // hide dialog
                }
            );
        };


        $scope.deleteAllFaces = function () {
            Face.deleteAllFaces().then(function successCallback(response) {
                    // tell the user face record was deleted
                    $scope.showToast(response.data.message);
                    // refresh the list
                    $scope.readFaces();
                },
                function errorCallback(response) {
                    $scope.showToast("Unable to delete all faces.");
                });
        };


        $scope.deleteAllFacesAndConfirm = function (event) {
            if ($scope.faces.length > 0) {
                var confirm = $mdDialog.confirm()
                    .title('Are you sure?')
                    .textContent('All faces will be deleted.')
                    .targetEvent(event)
                    .ok('Yes')
                    .cancel('No');
                // show dialog
                $mdDialog.show(confirm).then(
                    // 'Yes' button
                    function () {
                        // if user clicked 'Yes', delete product record
                        $scope.deleteAllFaces();
                    },
                    // 'No' button
                    function () {
                        // hide dialog
                    }
                );
            } else {
                var alert = $mdDialog.alert()
                    .title('Empty list')
                    .textContent('There are no faces to delete. Please create some faces first.')
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