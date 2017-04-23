/**
 * User package module.
 * Manages all sub-modules so other RequireJS modules only have to import the package.
 */
define(['angular', './dash-header/dash-header-directive', './loading-thingy/loading-thingy-directive'], function(angular) {
  'use strict';

  return angular.module('observation-deck.components', ['components.directives']);
});
