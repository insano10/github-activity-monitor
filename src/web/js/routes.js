(function(){

    angular.module("myApp").config(["$routeProvider", function($routeProvider){

        $routeProvider
            .when("/users", {
                templateUrl: "templates/pages/users/index.html",
                controller: "UserController",
                controllerAs: "userCtrl"
        })
            .when("/", {
                redirectTo: '/users'
        })
            .otherwise({
                redirectTo: "/"
       })
    }]);

})();