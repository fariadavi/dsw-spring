App.controller("dispensaController", function($scope, dispensaDataService, NgTableParams) {
	var self = this;

	$scope.tipoStatus = [ "Aguardando dispensa", "Dispensados",
			"Não-Dispensados", "Todos" ];

	$scope.disabled= true;
	/**
	 * Filtros
	 */
	$scope.filtros = {
		nome : "",
		statusDispensa : "Todos"
	}

	/*
	 * Altera os filtros de consulta
	 */
	self.atualizaFiltro = function() {
		atualizaLista();
	}

	/*
	 * Atualiza a lista de inscrições
	 */
	var atualizaLista = function() {
		$scope.tableParams.reload();
	}

	/*
	 *	Libera o botão de Enviar 
	 */
	self.liberarSend = function(itemId){
		if ($scope.disabled == true)
			angular.element(document.getElementById(itemId)).scope().disabled = false
	}
	
	/*
	 * Atualiza o status de dispensa inicial de prova de uma inscrição
	 */
	self.dispensarInicial = function(id, dispensado, justificativa) {
		return dispensaDataService.dispensarInicial({
			id : id,
			dispensado : dispensado,
			justificativa : justificativa
		}).then(function successCallback(response) {
			showError('Sucesso');
		}, function errorCallback(response) {
			showError('Erro');
		});
	}

	/*
	 * Atualiza o status de recurso de dispensa de prova de uma inscrição
	 */
	self.dispensarRecurso = function(id, dispensado, justificativa) {		
		return dispensaDataService.dispensarRecurso({
			id : id,
			dispensado : dispensado,
			justificativa : justificativa
		}).then(function successCallback(response) {
			showError('Sucesso');
		}, function errorCallback(response) {
			showError('Erro');
		});
	}

	/*
	 * Prepara a tabela
	 */
	$scope.tableParams = new NgTableParams({}, {
		getData : function(params) {
			return dispensaDataService.listaDispensaProva({
				id : idEdital,
				page : params.page() - 1,
				size : params.count(),
				nome : $scope.filtros.nome,
				statusDispensa : $scope.filtros.statusDispensa,
				comissao : comissao
			}).then(function(data) {
				if (data.data.TotalRecordCount == 0) {
					self.noSite = true;
				} else {
					params.total(data.data.TotalRecordCount);
					self.noSite = false;
					return data = data.data.Records;
				}
			});
		}
	});
});