/**
 * User package module.
 * Manages all sub-modules so other RequireJS modules only have to import the package.
 */
define(['angular', './component-controllers', './loading-thingy/loading-thingy-directive'], function (angular, controllers) {
    'use strict';

    var mod = angular.module('observation-deck.components', ['components.directives']);
    mod.controller('DashHeaderCtrl', controllers.DashHeaderCtrl);

    return mod;
});
