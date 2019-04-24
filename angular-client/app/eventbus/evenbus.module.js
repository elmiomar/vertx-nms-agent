angular.module('eventBusServices', []).factory('EventBusService', function () {
    return new EventBus("http://localhost:8080/eventbus");
});