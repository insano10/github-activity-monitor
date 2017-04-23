/**
 * User package module.
 * Manages all sub-modules so other RequireJS modules only have to import the package.
 */
define(['angular', './user-routes', './user-services', './directive/user-directive'], function(angular) {
  'use strict';

  return angular.module('observation-deck.user', ['user.routes', 'user.services', 'user.directives']);
});
