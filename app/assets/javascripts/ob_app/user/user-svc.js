(function() {

    var mod = angular.module("main");

    mod.factory("User", ["$resource", "hostName", function UserFactory($resource, hostName){

        return $resource("http://" + hostName + ":8080/api/user", {}, {});

    }]);

})();