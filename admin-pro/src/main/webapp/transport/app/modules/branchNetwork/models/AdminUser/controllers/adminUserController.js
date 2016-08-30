angular.module('adminUser').controller("adminUserController",
    function($scope,$http,adminUserService,adminUsers) {
            $scope.webUserManagers = [{}];
            $scope.webUserManagers = adminUsers;

    });
