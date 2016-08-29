(function ()
{
    var mod = angular.module("main");

    mod.directive("dashHeader", function ()
    {
        return {
            restrict:     'E',
            templateUrl:  'templates/directives/dash-header.html',
            controller: ['$scope', '$http', function($scope, $http){

                $http.get("http://localhost:8080/config")
                    .then(function(response) {
                        $scope.boardName = response.data.boardName;
                    });
            }]
        };
    });

})();