angular.module('app').config(
    ['$routeProvider',
        function($routeProvider) {
            $routeProvider.
              when('/branchNetwork/adminUser/list', {
                controller: 'adminUserController',
                resolve: {
                    adminUsers: ["MultiAdminUserLoader", function(MultiAdminUserLoader) {
                        return MultiAdminUserLoader();
                    }]
                },
                templateUrl:'views/branchNetwork/adminUser/webUserManager.html'
              });
        }]);
