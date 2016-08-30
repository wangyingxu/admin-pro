angular.module('branchNetwork').controller("BranchNetworkController",
    function($scope,$http,$location,Restangular,branchNetworkService,branchNetworks) {
        $scope.webManagers = [{}];
        $scope.webManagers = branchNetworks;
       /* $scope.posts = [];
        $scope.syncPosts = function () {
            var request = $http.get('http:/localhost:3000/posts.json');
            request.success(function (response) {
                $scope.posts = angular.forEach(angular.fromJson(response), function (post) {
                    post.trustedBody = $sce.trustAsHtml(post.html_body);
                });
            });
        };
        $scope.syncPosts();*/

        $scope.addBra = function(){
            $scope.branchNetwork ={};
        }

        $scope.delete = function(id,index){

              branchNetworkService.remove(id,function(data){
                  //alert("条路径");
                 // $location.path("/branchNetwork");
                $scope.webManagers.splice(index,1);
            });

        }
        $scope.selectOne = function(id){
            var b =  branchNetworkService.getOne(id);
            $scope.branchNetwork= b;
        }

        $scope.save = function(){
            var bran = $scope.branchNetwork;
            branchNetworkService.save(bran,function(data){
                //alert("4565656");
                window.location.reload(true);
                //$location.path("/branchNetwork");
                //alert(data);
                //$scope.webManagers.push(data);
            });
        }

    });
