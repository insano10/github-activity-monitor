/**
 * User package module.
 * Manages all sub-modules so other RequireJS modules only have to import the package.
 */
define(['angular', './history-routes', './history-services'], function(angular) {
  'use strict';

  return angular.module('observation-deck.history', ['history.routes', 'history.services']);
});
