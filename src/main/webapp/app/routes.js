(function ()
{
    angular.module("myApp").config(["$routeProvider", "$locationProvider", function ($routeProvider, $locationProvider)
    {

        $routeProvider
            .when("/users", {
                templateUrl:  "app/user/user-view.html",
                controller:   "UserController",
                controllerAs: "userCtrl"
            })
            .when("/repositories", {
                templateUrl:  "app/repository/repository-view.html",
                controller:   "RepositoryController",
                controllerAs: "repoCtrl"
            })
            .when("/stats", {
                templateUrl:  "app/stats/stats-view.html",
                controller:   "StatsController",
                controllerAs: "statsCtrl"
            })
            .when("/", {
                redirectTo: '/repositories'
            })
            .otherwise({
               redirectTo: "/"
           });

        $locationProvider.html5Mode(true);
    }]);

})();