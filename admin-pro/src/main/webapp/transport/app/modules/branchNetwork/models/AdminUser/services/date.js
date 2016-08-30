var adminUser_Service_Data = angular.module('adminUser.Service.Data',
        ['restangular']
    )
adminUser_Service_Data.factory('adminUserService', function($http,Restangular) {
    var currentbranchNetwork;
    return {
        getCurrent : function() {
            return currentbranchNetwork;
        },
        setCurrent : function(data) {
            currentbranchNetwork = data;
        },
        getAll:function(callback){
            var adminUsers =Restangular.all('/admin/user/list');
            //返回带有翻页参数的对象
            return adminUsers.getList().$object;
        },
        remove:function(id,callback){
            var message = Restangular.one('message',id);
            message.remove().then(function(data){
                callback(data);
            });
        }
    };
});

adminUser_Service_Data.factory('MultiAdminUserLoader', ['adminUserService', '$q',
    function(adminUserService, $q) {
        return function() {
            var delay = $q.defer();
                delay.resolve(adminUserService.getAll());
            return delay.promise;
        };
    }]);
//路由调用的时候 用来 加载  branchNetwork 列表 的服务
/*branchNetwork_Service_Data.factory('MultiBranchNetworkLoader',
    ['BranchNetwork', '$q',
        function(BranchNetwork, $q) {
            return function() {
                var delay = $q.defer();
                Recipe.query(function(recipes) {
                    delay.resolve(recipes);
                }, function() {
                    delay.reject('Unable to fetch recipes');
                });
                return delay.promise;
            };
        }]);*/

//路由调用的时候 用来 根据url的id参数加载  branchNetwork 数据 的服务
/*branchNetwork_Service_Data.factory('RecipeLoader', ['Recipe', '$route', '$q',
    function(Recipe, $route, $q) {
        return function() {
            var delay = $q.defer();
            Recipe.get({id: $route.current.params.recipeId}, function(recipe) {
                delay.resolve(recipe);
            }, function() {
                delay.reject('Unable to fetch recipe '  + $route.current.params.recipeId);
            });
            return delay.promise;
        };
    }]);*/
