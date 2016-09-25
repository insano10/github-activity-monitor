(function ()
{
    var mod = angular.module("main");

    mod.directive("dashHeader", function ()
    {
        return {
            restrict:     'E',
            templateUrl:  'app/dash-header/directive/dash-header.html',
            controller: ['$scope', '$http', function($scope, $http){

                $http.get("http://localhost:8080/api/config")
                    .then(function(response) {
                        $scope.boardName = response.data.boardName;
                    });
            }]
        };
    });

})();