define([], function () {
    'use strict';

    var HistoryCtrl = function (ReleaseHistory) {

        var ctrl = this;

        ReleaseHistory.getHistory()
            .then(
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
    };
    HistoryCtrl.$inject = ['ReleaseHistory'];

    return {
        HistoryCtrl: HistoryCtrl
    };

});