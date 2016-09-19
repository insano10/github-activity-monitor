(function ()
{
    angular.module("myApp").config(["$routeProvider", "$locationProvider", function ($routeProvider, $locationProvider)
    {

        $routeProvider
            .when("/users", {
                templateUrl:  "templates/pages/users/index.html",
                controller:   "UserController",
                controllerAs: "userCtrl"
            })
            .when("/repositories", {
                templateUrl:  "templates/pages/repositories/index.html",
                controller:   "RepositoryController",
                controllerAs: "repoCtrl"
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