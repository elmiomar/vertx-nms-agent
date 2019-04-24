// Define the `agentApp` module
angular.module('agentApp', [
    // depends on the `facesList` module
    'ngRoute',
    'ngMaterial',
    'md.data.table',
    'ngMessages',
    'core',
    'facesList',
    'fibList',
    'monitor',
    'home',
    'eventBusServices',
    'angular-uuid'
]);