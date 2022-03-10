/* Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
angular.module('flowableModeler')
  .controller('ProcessCtrl', ['$rootScope', '$scope', '$translate', '$http', '$location', '$routeParams','$modal', '$popover', '$timeout', 'appResourceRoot', 'ResourceService',
                              function ($rootScope, $scope, $translate, $http, $location, $routeParams, $modal, $popover, $timeout, appResourceRoot, ResourceService) {

    // Main page (needed for visual indicator of current page)
    $rootScope.setMainPageById('processes');

    // Initialize model
    $scope.model = {
        // Store the main model id, this points to the current version of a model,
        // even when we're showing history
        latestModelId: $routeParams.modelId
    };
    
    $scope.loadProcess = function() {
    	
      $http({method: 'GET', url: FLOWABLE.APP_URL.getFhadminCallbackJsonUrl()}).
        success(function(data, status, headers, config) {
          $scope.model.process = data;
          
          $timeout(function() {
        	var fhadminID = $routeParams.modelId;
        	if(fhadminID.substring(0, 8) == "fhadmind"){		//流程管理里面查看流程图
        	    fhadminID = fhadminID.replace('fhadmind','');
        	    jQuery("#bpmnModel").attr('data-model-type', 'fhadmin-deployment');
        	}else if(fhadminID.substring(0, 8) == "fhadminp"){
        	    fhadminID = fhadminID.replace('fhadminp','');	//办理任务和查看流程信息里面流程跟踪显示的流程图
        	    jQuery("#bpmnModel").attr('data-model-type', 'fhadmin-definition');
        	}else if(fhadminID.substring(0, 8) == "fhadminj"){
        	    fhadminID = fhadminID.replace('fhadminj','');	//流程自由跳转显示的流程图
        	    jQuery("#bpmnModel").attr('data-model-type', 'fhadmin-jump');
        	}
            jQuery("#bpmnModel").attr('data-model-id', fhadminID);
            var viewerUrl = appResourceRoot + "display/displayfhmodel.html?version=" + Date.now();
            var amdDefine = window.define;
            window.define = undefined;
            ResourceService.loadFromHtml(viewerUrl, function(){
                window.define = amdDefine;
            });
          });

        }).error(function(data, status, headers, config) {
        });
    };
    
    $scope.useAsNewVersion = function() {
    };
    
    $scope.loadVersions = function() {
    };
    
    $scope.showVersion = function(version) {
    };
    
    $scope.returnToList = function() {
    };
    
    $scope.editProcess = function() {
    };

    $scope.duplicateProcess = function() {
    };

    $scope.deleteProcess = function() {
    };
    
    $scope.deploymentProcess = function() {
	};
    
    $scope.openEditor = function() {
    };
      
    $scope.toggleHistory = function($event) {
    };
    
    $scope.loadProcess();
}]);
