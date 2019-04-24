angular.
module('agentApp').
config(['$routeProvider',
    function config($routeProvider) {
        $routeProvider.
        when('/home', {
            templateUrl : "home/home.template.html"
        }).
        when('/faces', {
            template: '<faces-list></faces-list>'
        }).
        when('/fib', {
            template: '<fib-list></fib-list>'
        }).
        when('/monitor', {
            template: '<monitor></monitor>'
        }).
        when('/faces/:faceId', {
            template: ''
        }).
        otherwise('/faces');
    }
]);