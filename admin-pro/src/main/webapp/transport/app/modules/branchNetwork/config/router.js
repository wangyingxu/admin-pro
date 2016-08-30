angular.module('branchNetwork').config(['$routeProvider', function($routeProvider) {
    $routeProvider.
    when('/branchNetwork', {
        controller: 'BranchNetworkController',
        resolve: {
            branchNetworks: ["MultiBranchNetworkLoader", function(MultiBranchNetworkLoader) {
                return MultiBranchNetworkLoader();
            }]
        },
        templateUrl:'views/branchNetwork/webManager.html'
    });
}]);




/*
angular.module('branchNetwork').config(['$stateProvider', '$urlRouterProvider',
    function($stateProvider, $urlRouterProvider) {
        // 错误的路由重定向
        $urlRouterProvider
            .when('/branchNetwork/!**', '/branchNetwork/list');
        状态配置
        $stateProvider.
            state('contents.branchNetwork', {
                url: '/branchNetwork/list',

                templateUrl: 'views/branchNetwork/webManager.html',

                resolve: {
                    branchNetworks: ["MultiBranchNetworkLoader", function(MultiBranchNetworkLoader) {
                        return MultiBranchNetworkLoader();
                    }]
                },

                controller: 'BranchNetworkController'

            });
    }
]);

*/
