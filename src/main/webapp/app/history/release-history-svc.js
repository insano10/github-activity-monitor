(function() {

    var mod = angular.module("main");

    mod.factory("ReleaseHistory", ["$resource", "hostName", function ReleaseHistoryFactory($resource, hostName){

        return $resource("http://" + hostName + ":8080/api/history/release", {}, {});

    }]);

})();