(function() {

    var mod = angular.module("main");

    mod.factory("User", ["$resource", function UserFactory($resource){

        return $resource("http://localhost:8080/api/user", {}, {});

    }]);

})();