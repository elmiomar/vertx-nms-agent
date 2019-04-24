angular.module('home').component('home', {
    templateUrl: 'home/home.template.html',
    controller: ['$scope', function HomeController($scope) {
        $scope.navbarItems = [{
            label: 'FACES',
            link: 'faces',
            icon: 'fas fa-image'
        }, {
            label: 'FIB',
            link: 'fib',
            icon: 'fas fa-image'
        }, {
            label: 'EVENTBUS',
            link: 'monitor',
            icon: 'fas fa-image'
        }];

        $scope.selectedIndex = 0; /* first one set active by default */
        $scope.select = function (i) {
            $scope.selectedIndex = i;
        };

    }]
});