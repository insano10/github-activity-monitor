(function() {

    var mod = angular.module("main");

    mod.factory("Repository", ["$resource", "hostName", function RepositoryFactory($resource, hostName){

        return $resource("http://" + hostName + ":8080/api/repository", {}, {});

    }]);

})();