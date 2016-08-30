var branchNetwork_Service_Data = angular.module('branchNetwork.Service.Data', 
        ['restangular']
    ).config(function(RestangularProvider) {
        RestangularProvider.setBaseUrl('/json/admin');
    });

branchNetwork_Service_Data.factory('branchNetworkService', function($http,Restangular) {
    var currentbranchNetwork;
    return {
        getCurrent : function() {
            return currentbranchNetwork;
        },
        setCurrent : function(data) {
            currentbranchNetwork = data;
        },
        getBranchList:function(callback){
            var branchNetworks =Restangular.all('/branchNetwork/list');
            return  branchNetworks.get("").then(function (data) {
                return data;
            });
        },
        getAll:function(callback){
            var branchNetworks =Restangular.all('/branchNetwork/list');

            return branchNetworks.getList().$object;
        },
        remove:function(id,callback){
            var message = Restangular.one('/branchNetwork/edit',id);
            message.remove().then(function(data){
                 callback(data);
            });
        },
        getOne:function(id,callback){
            var bra = Restangular.one('/branchNetwork/edit',id);
            return bra.get().$object;
        },
        save:function(obj,callback){
            var bra =  Restangular.all('/branchNetwork/edit');
            bra.post(obj).then(function(data){
                  callback(data);

            })
        }
    };
});

branchNetwork_Service_Data.factory('MultiBranchNetworkLoader', ['branchNetworkService', '$q',
    function(branchNetworkService, $q) {
        return function() {
            var delay = $q.defer();
                delay.resolve(branchNetworkService.getAll());
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
