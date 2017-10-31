DROP PROCEDURE IF EXISTS `InscricaoAceitaHomolagacaoInicial`;
DELIMITER $$
CREATE PROCEDURE `InscricaoAceitaHomolagacaoInicial`(vId INT)
BEGIN
	UPDATE Inscricao
	SET homologadoInicial = 1,
    justificativaHomologacaoInicial = null,
    dataAtualizacao = NOW()	
	WHERE id = vId;
END$$
DELIMITER ;

DROP PROCEDURE IF EXISTS `InscricaoRecusaHomolagacaoInicial`;
DELIMITER $$
CREATE PROCEDURE `InscricaoRecusaHomolagacaoInicial`(vId INT, vJustificativa VARCHAR(4096))
BEGIN
	UPDATE Inscricao
	SET homologadoInicial = 0,
    justificativaHomologacaoInicial = vJustificativa,
    dataAtualizacao = NOW()	
	WHERE id = vId;
END$$
DELIMITER ;

DROP PROCEDURE IF EXISTS `InscricaoAceitaHomolagacaoRecurso`;
DELIMITER $$
CREATE PROCEDURE `InscricaoAceitaHomolagacaoRecurso`(vId INT)
BEGIN
	DECLARE CONTINUE HANDLER FOR SQLEXCEPTION ROLLBACK;
	START TRANSACTION;

	IF homolagadoInicial = 0 THEN
		UPDATE Inscricao
		SET homologadoRecurso = 1,
		justificativaHomologacaoRecurso = null,
		dataAtualizacao = NOW()	
		WHERE id = vId;
	END IF;
	
	COMMIT;
END$$
DELIMITER ;

DROP PROCEDURE IF EXISTS `InscricaoRecusaHomolagacaoRecurso`;
DELIMITER $$
CREATE PROCEDURE `InscricaoRecusaHomolagacaoRecurso`(vId INT, vJustificativa VARCHAR(4096))
BEGIN
	UPDATE Inscricao
	SET homologadoRecurso = 0,
    justificativaHomologacaoRecurso = vJustificativa,
    dataAtualizacao = NOW()	
	WHERE id = vId;
END$$
DELIMITER ;


DROP PROCEDURE IF EXISTS `InscricaoDispensaProvaInicial`;
DELIMITER $$
CREATE PROCEDURE `InscricaoDispensaProvaInicial`(vId INT)
BEGIN
	UPDATE Inscricao
	SET dispensadoProvaInicial = 1,
    justificativaDispensaInicial = null,
    dataAtualizacao = NOW()	
	WHERE id = vId;
END$$
DELIMITER ;


DROP PROCEDURE IF EXISTS `InscricaoRecusaDispensaProvaInicial`;
DELIMITER $$
CREATE PROCEDURE `InscricaoRecusaDispensaProvaInicial`(vId INT, vJustificativa VARCHAR(4096))
BEGIN
	UPDATE Inscricao
	SET dispensadoProvaInicial = 0,
    justificativaDispensaInicial = vJustificativa,
    dataAtualizacao = NOW()	
	WHERE id = vId;
END$$
DELIMITER ;


DROP PROCEDURE IF EXISTS `InscricaoDispensaProvaRecurso`;
DELIMITER $$
CREATE PROCEDURE `InscricaoDispensaProvaRecurso`(vId INT)
BEGIN
	UPDATE Inscricao
	SET dispensadoProvaRecurso = 1,
    justificativaDispensaRecurso = null,
    dataAtualizacao = NOW()	
	WHERE id = vId;
END$$
DELIMITER ;


DROP PROCEDURE IF EXISTS `InscricaoRecusaDispensaProvaRecurso`;
DELIMITER $$
CREATE PROCEDURE `InscricaoRecusaDispensaProvaRecurso`(vId INT, vJustificativa VARCHAR(4096))
BEGIN
	UPDATE Inscricao
	SET dispensadoProvaRecurso = 0,
    justificativaDispensaRecurso = vJustificativa,
    dataAtualizacao = NOW()	
	WHERE id = vId;
END$$
DELIMITER ;