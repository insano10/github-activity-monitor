(function () {

    var mod = angular.module("main");

    mod.factory("ReleaseHistory", ["$http", "hostName", function ReleaseHistoryFactory($http, hostName) {

        function getHistory() {
            return $http({
                             method: 'GET',
                             url:    "http://" + hostName + ":8080/api/history/release"
                         });
        }

        return {
            'getHistory': getHistory
        };

    }]);

})();