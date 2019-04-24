angular.module('fibList').component('fibList', {
    templateUrl: 'fib-list/fib-list.template.html',
    controller: ['Fib', '$scope', '$http', '$mdDialog', '$mdToast', function FibController(Fib, $scope, $http, $mdDialog, $mdToast) {
        
        $scope.readFib = function () {
            Fib.getFibEntries().then(function (response) {
                $scope.fib = response.data.fib;
                console.log($scope.fib);
            },
            function (response) {
                if (response.data === null) {
                    $scope.showToast("Connection failed! Check if the server is up.");
                }
            });
        };

        // call function readFib() to fill list
        $scope.readFib();

        //cancel function
        $scope.cancel = function () {
            $mdDialog.cancel();
        };

        // clear variable / form values
        $scope.clearFibEntryForm = function () {
            $scope.prefix = "";
            $scope.face = "";
            $scope.cost = "";
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

        // methods for dialog box
        function DialogController($scope, $mdDialog) {
            $scope.cancel = function () {
                $mdDialog.cancel();
            };
        }

        $scope.showCreateFibEntryForm = function (event) {
            console.log('called showCreateFibEntryForm()');
            $mdDialog.show({
                controller: DialogController,
                templateUrl: 'fib-list/create-fib-entry.template.html',
                parent: angular.element(document.body),
                clickOutsideToClose: true,
                scope: $scope,
                preserveScope: true,
                fullscreen: true // Only for -xs, -sm breakpoints.
            });
        };

        // create new fib entry
        $scope.createFibEntry = function () {
            Fib.createFibEntry($scope).then(function successCallback(response) {
                // log response
                console.log(response);
                // tell the user new fib entry was created
                $scope.showToast(response.data.message);
                // refresh the libst
                $scope.readFib();
                // close dialog
                $scope.cancel();
                // remove form values
                $scope.clearFibEntryForm();

            }, function errorCallback(response) {
                $scope.showToast("Unable to create fib entry.");
            });
        };


        // retrieve record to fill out the form
        $scope.readOneFibEntry = function (id) {
            console.log('calling readOneFibEntry(' + id + ')');
            // get face to be edited
            Fib.readOneFibEntry(id).then(function successCallback(response) {

                console.log(response.data);
                // put the values in form
                // $scope.entryId = response.data.entry.entryId;
                $scope.name = response.data.entry.prefix;
                $scope.face = response.data.entry.face;
                $scope.cost = response.data.entry.cost;

                $mdDialog.show({
                    controller: DialogController,
                    templateUrl: 'fib-list/read-one-fib-entry.template.html',
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
                        $scope.clearFibEntryForm();
                    }
                );

            }, function errorCallback(response) {
                $scope.showToast("Unable to retrieve fib entry.");
            });

        };

        $scope.showUpdateFibEntryForm = function (id) {
            console.log('called showUpdateFibEntryForm()');

            Fib.readOneFibEntry(id).then(function successCallback(response) {
                    $scope.prefix = response.data.entry.prefix;
                    $scope.face = response.data.entry.face;
                    $scope.cost = response.data.entry.cost;

                    $mdDialog.show({
                        controller: DialogController,
                        templateUrl: 'fib-list/update-fib-entry.template.html',
                        parent: angular.element(document.body),
                        clickOutsideToClose: true,
                        scope: $scope,
                        preserveScope: true,
                        fullscreen: true // Only for -xs, -sm breakpoints.
                    });
                },
                function errorCallback(response) {
                    $scope.showToast("Unable to update fib entry.");
                });
        };

        // // update fib entry record / save changes
        $scope.updateFibEntry = function () {
            console.log('calling service Fib.updateFibEntry()');
            Fib.updateFibEntry($scope).then(function successCallback(response) {
                    // tell the user fib entry record was updated
                    $scope.showToast(response.data.message);
                    // refresh the fib list
                    $scope.readFib();
                    // close dialog
                    $scope.cancel();
                    // clear modal content
                    $scope.clearFibEntryForm();
                },
                function errorCallback(response) {
                    $scope.showToast("Unable to update fib entry.");
                });
        };

        // // delete fib entry record / save changes
        $scope.deleteFibEntry = function (id) {
            console.log('calling service Fib.deleteFibEntry()');
            Fib.deleteFibEntry(id).then(function successCallback(response) {
                    // tell the user face record was deleted
                    $scope.showToast(response.data.message);
                    // refresh the list
                    $scope.readFib();
                },
                function errorCallback(response) {
                    $scope.showToast("Unable to delete fib entry.");
                });
        };

        // cofirm fib entry deletion
        $scope.deleteFibEntryAndConfirm = function (event, id) {
            // set id of record to delete
            $scope.id = id;
            // dialog settings
            var confirm = $mdDialog.confirm()
                .title('Are you sure?')
                .textContent('Fib entry will be deleted.')
                .targetEvent(event)
                .ok('Yes')
                .cancel('No');
            // show dialog
            $mdDialog.show(confirm).then(
                // 'Yes' button
                function () {
                    // if user clicked 'Yes', delete product record
                    $scope.deleteFibEntry(id);
                },
                // 'No' button
                function () {
                    // hide dialog
                }
            );
        };


        $scope.deleteAllFibEntries = function () {
            Fib.deleteAllFibEntries().then(function successCallback(response) {
                    // tell the user face record was deleted
                    $scope.showToast(response.data.message);
                    // refresh the list
                    $scope.readFib();
                },
                function errorCallback(response) {
                    $scope.showToast("Unable to delete all fib entries.");
                });
        };


        $scope.deleteAllFibEntriesAndConfirm = function (event) {
            if ($scope.fib.length > 0) {
                var confirm = $mdDialog.confirm()
                    .title('Are you sure?')
                    .textContent('All fib entries will be deleted.')
                    .targetEvent(event)
                    .ok('Yes')
                    .cancel('No');
                // show dialog
                $mdDialog.show(confirm).then(
                    // 'Yes' button
                    function () {
                        // if user clicked 'Yes', delete product record
                        $scope.deleteAllFibEntries();
                    },
                    // 'No' button
                    function () {
                        // hide dialog
                    }
                );
            } else {
                var alert = $mdDialog.alert()
                    .title('Empty list')
                    .textContent('There are no registred fib entries to delete.')
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