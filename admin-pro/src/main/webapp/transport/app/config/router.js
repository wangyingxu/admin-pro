angular.module('app').config(['$routeProvider', function($routeProvider) {
    $routeProvider.
    when('/', {
        controller: 'IndexController',
        templateUrl:'views/index/index.html'
    }).otherwise({
        redirectTo:'/'
    });
}]);




/*
transport.config(['$routeProvider', function($routeProvider) {
    $routeProvider.
    when('/', {
        controller: 'IndexController',
        templateUrl:'view/index/index.html'
    }).when('/webUserManager', {
        controller: 'webUserManagerController',
        resolve: {
            webUserManagers: ["MultiwebUserManagers", function(MultiwebUserManagers) {
                return MultiwebUserManagers();
            }]
        },
        templateUrl:'view/web/webUserManager.html'
    }).when('/resourceManager', {
        controller: 'resourceManagerController',
        resolve: {
            resourceManagers: ["MultiresourceManagers", function(MultiresourceManagers) {
                return MultiresourceManagers();
            }]
        },
        templateUrl:'view/web/resourceManager.html'
    }).when('/webManager', {
        controller: 'webManagerController',
        resolve: {
            webManagers: ["MultiWebManagers", function(MultiWebManagers) {
                return MultiWebManagers();
            }]
        },
        templateUrl:'view/web/webManager.html'
    }).when('/webRole', {
        controller: 'webRoleController',
        templateUrl:'view/web/webRole.html'
    }).otherwise({
        redirectTo:'/'
    });
}]);*/
