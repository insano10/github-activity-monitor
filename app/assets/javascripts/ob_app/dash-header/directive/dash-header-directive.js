(function ()
{
    var mod = angular.module("main");

    mod.directive("dashHeader", function ()
    {
        return {
            restrict:     'E',
            templateUrl:  'app/dash-header/directive/dash-header.html',
            controller: ['$scope', '$http', 'hostName', function($scope, $http, hostName){

                $http.get("http://" + hostName + ":8080/api/config")
                    .then(function(response) {
                        $scope.boardName = response.data.boardName;
                    });
            }]
        };
    });

})();