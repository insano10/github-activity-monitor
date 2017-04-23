(function () {
    var mod = angular.module("main");

    mod.controller("HistoryController", ['ReleaseHistory', function (ReleaseHistory) {

        var ctrl = this;

        ReleaseHistory.getHistory().then(
            function successCallback(response) {

                ctrl.releaseHistory = response.data;

                var keys = Object.keys(ctrl.releaseHistory);
                ctrl.orderedReleaseKeys = keys.sort(function (a, b) {
                    return b - a;
                });

                console.log(ctrl.orderedReleaseKeys);

            },
            function errorCallback(response) {
                console.log("Failed to get history: " + JSON.stringify(response));
            });
    }]);

})();