package br.unirio.dsw.selecaoppgi.service.dao;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.joda.time.DateTime;
import org.springframework.stereotype.Service;

import br.unirio.dsw.selecaoppgi.model.edital.Edital;
import br.unirio.dsw.selecaoppgi.model.inscricao.AvaliacaoProvaEscrita;
import br.unirio.dsw.selecaoppgi.model.inscricao.InscricaoEdital;
import br.unirio.dsw.selecaoppgi.model.inscricao.InscricaoProjetoPesquisa;
import br.unirio.dsw.selecaoppgi.model.usuario.Usuario;

/**
 * Classe responsavel pela persistencia de inscrições em edital de seleção
 * 
 * @author Marcio Barros
 */
@Service("inscricaoDAO")
public class InscricaoDAO extends AbstractDAO
{
	/**
	 * Carrega os dados de uma inscricao a partir do resultado de uma consulta
	 */
	private InscricaoEdital carrega(ResultSet rs) throws SQLException
	{
		int idCandidato = rs.getInt("idCandidato");
		UsuarioDAO userDAO = new UsuarioDAO();
		Edital edital = new EditalDAO().carregaEditalId(rs.getInt("idEdital"), userDAO);
		Usuario candidato = userDAO.carregaUsuarioId(idCandidato);

		InscricaoEdital inscricao = new InscricaoEdital(edital);
		inscricao.setId(rs.getInt("id"));
		inscricao.setIdCandidato(idCandidato);
		inscricao.setNomeCandidato(candidato.getNome());
		inscricao.setCotaNegros(rs.getInt("cotaNegros") != 0);
		inscricao.setCotaDeficientes(rs.getInt("cotaDeficientes") != 0);
		inscricao.setHomologadoOriginal(rs.getInt("homologadoInicial") != 0);
		inscricao.setJustificativaHomologacaoOriginal(rs.getString("justificativaHomologacaoInicial"));
		inscricao.setHomologadoRecurso(rs.getInt("homologadoRecurso") != 0);
		inscricao.setJustificativaHomologacaoRecurso(rs.getString("justificativaHomologacaoRecurso"));
		inscricao.setDispensadoProvaOriginal(rs.getInt("dispensadoProvaInicial") != 0);
		inscricao.setJustificativaDispensaOriginal(rs.getString("justificativaDispensaInicial"));
		inscricao.setDispensadoProvaRecurso(rs.getInt("dispensadoProvaRecurso") != 0);
		inscricao.setJustificativaDispensaRecurso(rs.getString("justificativaDispensaRecurso"));
//		this.projetosPesquisa = new ArrayList<InscricaoProjetoPesquisa>();
//		this.provasEscritas = new ArrayList<AvaliacaoProvaEscrita>();
		
		return inscricao;
	}
	
	/**
	 * Registra uma nova inscrição de um candidato, incluindo os projetos de pesquisa, provas e critérios de alinhamento
	 */
	public boolean registraInscricao(InscricaoEdital inscricao)
	{
//		Cria um registro preenchendo os seguintes campos na tabela de Inscricao:
//
//		* dataRegistro
//		* dataAtualizacao
//		* idEdital
//		* idCandidato
//		* cotaNegros
//		* cotaDeficientes
//		* jsonProjetos: vetor de codigo do projeto de pesquisa e texto descrevendo interesses de pesquisa
//
//		Cria os registros de provas escritas referentes à inscrição (notas e presencao nulas, inicialmente)
//
//		Cria os registros de critérios de alinhamento referentes à inscrição

		return false;
	}
	
	/**
	 * Carrega a lista de inscrições de um determinado edital que podem ser homologadas
	 */
	public List<InscricaoEdital> carregaAvaliacaoHomologacao(int idEdital)
	{
		// TODO Grupo 4: implementar este método em função do caso de uso #6

		Connection c = getConnection();
		
		if (c == null)
			return null;
		
		try
		{
			List<InscricaoEdital> listItems = new ArrayList<InscricaoEdital>();
			
			PreparedStatement ps = c.prepareStatement("SELECT * FROM Inscricao WHERE idEdital = ?");
			ps.setLong(1, idEdital);
			
			ResultSet rs = ps.executeQuery();
			while(rs.next())
				listItems.add(carrega(rs));
			
			c.close();
			return listItems;

		} catch (SQLException e)
		{
			log("InscricaoDAO.getAvaliacaoHomologacao: " + e.getMessage());
			return null;
		}
	}
	
	/**
	 * Registra a homologação de uma inscrição na avaliação inicial
	 */
	public boolean homologacaoInicial(int idInscricao)
	{
		// Muda o campo homologadoInicial para TRUE e limpa o campo justificativaHomologacaoInicial
		// Muda a data de atualização do registro de inscrição para a data de hoje
		// TODO Grupo 4: implementar este método em função do caso de uso #6
		
		Connection c = getConnection();
		
		if (c == null)
			return false;
		
		try
		{
			CallableStatement cs = c.prepareCall("{call InscricaoAceitaHomolagacaoInicial(?)}");
			cs.setInt(1, idInscricao);
			cs.execute();
			c.close();
			return true;

		} catch (SQLException e)
		{
			log("InscricaoDAO.homologacaoInicial: " + e.getMessage());
			return false;
		}
	}
	
	/**
	 * Registra a recusa de homologação de uma inscrição na avaliação inicial
	 */
	public boolean recusaHomologacaoInicial(int idInscricao, String justificativa)
	{
		// Muda o campo homologadoInicial para FALSE e preenche o campo justificativaHomologacaoInicial
		// Muda a data de atualização do registro de inscrição para a data de hoje
		// TODO Grupo 4: implementar este método em função do caso de uso #6
		
		Connection c = getConnection();
		
		if (c == null)
			return false;
		
		try
		{
			CallableStatement cs = c.prepareCall("{call InscricaoRecusaHomolagacaoInicial(?, ?)}");
			cs.setInt(1, idInscricao);
			cs.setString(2, justificativa);
			cs.execute();
			c.close();
			return true;

		} catch (SQLException e)
		{
			log("InscricaoDAO.recusaHomologacaoInicial: " + e.getMessage());
			return false;
		}
	}
	
	/**
	 * Registra a homologação de uma inscrição no recurso
	 */
	public boolean homologacaoRecurso(int idInscricao)
	{
		// Muda o campo homologadoRecurso para TRUE e limpa o campo justificativaHomologacaoRecurso
		// Muda a data de atualização do registro de inscrição para a data de hoje
		// Somente se o campo homologadoInicial estiver FALSE
		// TODO Grupo 4: implementar este método em função do caso de uso #6
		
		Connection c = getConnection();
		
		if (c == null)
			return false;
		
		try
		{
			CallableStatement cs = c.prepareCall("{call InscricaoAceitaHomolagacaoRecurso(?)}");
			cs.setInt(1, idInscricao);
			cs.execute();
			c.close();
			return true;

		} catch (SQLException e)
		{
			log("InscricaoDAO.homologacaoRecurso: " + e.getMessage());
			return false;
		}
	}
	
	/**
	 * Registra a recusa de homologação de uma inscrição no recurso
	 */
	public boolean recusaHomologacaoRecurso(int idInscricao, String justificativa)
	{
		// Muda o campo homologadoRecurso para FALSE e preenche o campo justificativaHomologacaoRecurso
		// Muda a data de atualização do registro de inscrição para a data de hoje
		// Somente se o campo homologadoInicial estiver FALSE
		// TODO Grupo 4: implementar este método em função do caso de uso #6
		
		Connection c = getConnection();
		
		if (c == null)
			return false;
		
		try
		{
			CallableStatement cs = c.prepareCall("{call InscricaoRecusaHomolagacaoRecurso(?, ?)}");
			cs.setInt(1, idInscricao);
			cs.setString(2, justificativa);
			cs.execute();
			c.close();
			return true;

		} catch (SQLException e)
		{
			log("InscricaoDAO.recusaHomologacaoRecurso: " + e.getMessage());
			return false;
		}
	}
	
	/**
	 * Carrega a lista de inscrições de um determinado edital que podem ser dispensados de prova
	 */
	public List<InscricaoEdital> carregaAvaliacaoDispensaProva(int idEdital)
	{
		// As inscrições devem estar homologadas na avaliação inicial ou no recurso
		// TODO Grupo 4: implementar este método em função do caso de uso #6
		Connection c = getConnection();
		
		if (c == null)
			return null;
		
		try
		{
			List<InscricaoEdital> listItems = new ArrayList<InscricaoEdital>();
			
			PreparedStatement ps = c.prepareStatement("SELECT * FROM Inscricao WHERE idEdital = ? AND (homologadoInicial != 0 OR homologadoRecurso != 0)");
			ps.setLong(1, idEdital);
			
			ResultSet rs = ps.executeQuery();
			while(rs.next())
				listItems.add(carrega(rs));
			
			c.close();
			return listItems;

		} catch (SQLException e)
		{
			log("InscricaoDAO.getAvaliacaoDispensa: " + e.getMessage());
			return null;
		}
	}

	/**
	 * Registra a dispensa de provas de uma inscrição na avaliação inicial
	 */
	public boolean dispensaProvaInicial(int idInscricao)
	{
		// Muda o campo dispensadoProvaInicial para TRUE e limpa o campo justificativaDispensaInicial
		// Muda a data de atualização do registro de inscrição para a data de hoje
		// Somente se o campo homologadoInicial estiver TRUE ou o campo homologadoRecurso estiver TRUE
		// TODO Grupo 4: implementar este método em função do caso de uso #7
		return false;
	}
	
	/**
	 * Registra a recusa de dispensa de provas de uma inscrição na avaliação inicial
	 */
	public boolean recusaDispensaProvaInicial(int idInscricao, String justificativa)
	{
		// Muda o campo dispensadoProvaInicial para FALSE e preenche o campo justificativaDispensaInicial
		// Muda a data de atualização do registro de inscrição para a data de hoje
		// Somente se o campo homologadoInicial estiver TRUE ou o campo homologadoRecurso estiver TRUE
		// TODO Grupo 4: implementar este método em função do caso de uso #7
		return false;
	}
	
	/**
	 * Registra a dispensa de provas de uma inscrição no recurso
	 */
	public boolean dispensaProvaRecurso(int idInscricao)
	{
		// Muda o campo dispensadoProvaRecurso para TRUE e limpa o campo justificativaDispensaRecurso
		// Muda a data de atualização do registro de inscrição para a data de hoje
		// Somente se o campo homologadoInicial estiver TRUE ou o campo homologadoRecurso estiver TRUE
		// Somente se o campo dispensadoProvaInicial estiver FALSE
		// TODO Grupo 4: implementar este método em função do caso de uso #7
		return false;
	}
	
	/**
	 * Registra a recusa de dispensa de provas de uma inscrição no recurso
	 */
	public boolean recusaDispensaProvaRecurso(int idInscricao, String justificativa)
	{
		// Muda o campo dispensadoProvaRecurso para FALSE e preenche o campo justificativaDispensaRecurso
		// Muda a data de atualização do registro de inscrição para a data de hoje
		// Somente se o campo homologadoInicial estiver TRUE ou o campo homologadoRecurso estiver TRUE
		// Somente se o campo dispensadoProvaInicial estiver FALSE
		// TODO Grupo 4: implementar este método em função do caso de uso #7
		return false;
	}
	
	/**
	 * Marca uma inscrição como homologada, como parte do encerramento das homologações
	 */
	public boolean marcaHomologado(int idInscricao)
	{
		// Muda o campo homologado da inscricao para TRUE
		// TODO Grupo 3: implementar este método em função do caso de uso #8
		return false;
	}
	
	/**
	 * Marca uma inscrição como não homologada, como parte do encerramento das homologações
	 */
	public boolean marcaNaoHomologado(int idInscricao)
	{
		// Muda o campo homologado da inscricao para FALSE
		// TODO Grupo 3: implementar este método em função do caso de uso #8
		return false;
	}
	
	/**
	 * Marca uma inscrição como dispensada da prova, como parte do encerramento das homologações
	 */
	public boolean marcaDispensadoProva(int idInscricao)
	{
		// Muda o campo dispensado da inscricao para TRUE
		// TODO Grupo 3: implementar este método em função do caso de uso #8
		return false;
	}
	
	/**
	 * Marca uma inscrição como não dispensada da prova, como parte do encerramento das homologações
	 */
	public boolean marcaNaoDispensadoProva(int idInscricao)
	{
		// Muda o campo dispensado da inscricao para FALSE
		// TODO Grupo 3: implementar este método em função do caso de uso #8
		return false;
	}
	
	/**
	 * Carrega a lista de inscrições de um determinado edital que podem fazer uma prova
	 */
	public List<InscricaoEdital> carregaPresencaProvaEscrita(int idEdital, String codigoProva)
	{
		// TODO Grupo 1: implementar este método em função do caso de uso #9
		return null;
	}

	/**
	 * Indica que um candidato esteve presente em uma prova
	 */
	public boolean indicaPresencaProvaEscrita(int idInscricao, String codigoProva)
	{
		// Muda o campo presente para TRUE no registro da prova escrita associada à inscrição
		// Somente se o campo homologadoInicial estiver TRUE ou o campo homologadoRecurso estiver TRUE
		// Somente se o campo dispensadoProvaInicial estiver FALSE ou dispensadoProvaRecurso estiver FALSE
		// TODO Grupo 1: implementar este método em função do caso de uso #9
		return false;
	}
	
	/**
	 * Indica que um candidato faltou a uma prova
	 */
	public boolean indicaAusenciaProvaEscrita(int idInscricao, String codigoProva)
	{
		// Muda o campo presente para FALSE no registro da prova escrita associada à inscrição
		// Somente se o campo homologadoInicial estiver TRUE ou o campo homologadoRecurso estiver TRUE
		// Somente se o campo dispensadoProvaInicial estiver FALSE ou dispensadoProvaRecurso estiver FALSE
		// TODO Grupo 1: implementar este método em função do caso de uso #9
		return false;
	}
	
	/**
	 * Carrega a lista de inscrições de um determinado edital que fizeram uma prova
	 */
	public List<InscricaoEdital> carregaAvaliacaoProvaEscrita(int idEdital, String codigoProva)
	{
		// TODO Grupo 2: implementar este método em função do caso de uso #10
		return null;
	}

	/**
	 * Atualiza os notas da avaliação inicial de uma inscrição
	 */
	public boolean indicaNotasProvaEscritaInicial(int idInscricao, AvaliacaoProvaEscrita avaliacao)
	{
		// Muda os campos jsonQuestoes e notaFinal de acordo com os parâmetros
		// Somente se o campo homologadoInicial estiver TRUE ou o campo homologadoRecurso estiver TRUE
		// Somente se o campo dispensadoProvaInicial estiver FALSE ou dispensadoProvaRecurso estiver FALSE
		// Somente se a presença na prova estiver TRUE
		// TODO Grupo 2: implementar este método em função do caso de uso #10
		return false;
	}
	
	/**
	 * Atualiza os notas do recurso de uma inscrição
	 */
	public boolean indicaNotasProvaEscritaRecurso(int idInscricao, AvaliacaoProvaEscrita avaliacao)
	{
		// Muda os campos jsonQuestoes e notaFinal de acordo com os parâmetros
		// Somente se o campo homologadoInicial estiver TRUE ou o campo homologadoRecurso estiver TRUE
		// Somente se o campo dispensadoProvaInicial estiver FALSE ou dispensadoProvaRecurso estiver FALSE
		// Somente se a presença na prova estiver TRUE
		// TODO Grupo 2: implementar este método em função do caso de uso #11
		return false;
	}
	
	/**
	 * Indica que um candidato foi aprovado nas provas escritas
	 */
	public boolean marcaAprovadoProvasEscritas(int idInscricao)
	{
		// Muda o campo aprovadoProvas de uma inscrição para TRUE
		// TODO Grupo 1: implementar este método em função do caso de uso #12
		return false;
	}
	
	/**
	 * Indica que um candidato foi reprovado nas provas escritas
	 */
	public boolean marcaReprovadoProvasEscritas(int idInscricao)
	{
		// Muda o campo aprovadoProvas de uma inscrição para FALSE
		// TODO Grupo 1: implementar este método em função do caso de uso #12
		return false;
	}
	
	/**
	 * Carrega a lista de inscrições de um determinado edital que podem fazer uma prova oral
	 */
	public List<InscricaoEdital> carregaPresencaProvaOral(int idEdital, String codigoProjetoPesquisa)
	{
		// TODO Grupo 1: implementar este método em função do caso de uso #13
		return null;
	}

	/**
	 * Indica que um candidato esteve presente na prova oral de um projeto
	 */
	public boolean indicaPresencaProvaOral(int idInscricao, String codigoProjetoPesquisa)
	{
		// Muda o campo presenteProvaOral para TRUE no registro da prova de alinhamento associada à inscrição e projeto de pesquisa
		// Somente se o campo homologadoInicial estiver TRUE ou o campo homologadoRecurso estiver TRUE
		// Somente se o campo dispensadoProvaInicial estiver FALSE ou dispensadoProvaRecurso estiver FALSE
		// Somente se a nota final de todas as provas escritas for maior do que a nota mínima para aprovação
		// Somente se o projeto exigir prova oral
		// TODO Grupo 1: implementar este método em função do caso de uso #13
		return false;
	}
	
	/**
	 * Indica que um candidato esteve ausente na prova oral de um projeto
	 */
	public boolean indicaAusenciaProvaOral(int idInscricao, String codigoProjetoPesquisa)
	{
		// Muda o campo presenteProvaOral para FALSE no registro da prova de alinhamento associada à inscrição e projeto de pesquisa
		// Somente se o campo homologadoInicial estiver TRUE ou o campo homologadoRecurso estiver TRUE
		// Somente se o campo dispensadoProvaInicial estiver FALSE ou dispensadoProvaRecurso estiver FALSE
		// Somente se a nota final de todas as provas escritas for maior do que a nota mínima para aprovação
		// Somente se o projeto exigir prova oral
		// TODO Grupo 1: implementar este método em função do caso de uso #13
		return false;
	}
	
	/**
	 * Carrega a lista de inscrições de um determinado edital que podem fazer uma prova oral
	 */
	public List<InscricaoEdital> carregaAvaliacaoAlinhamento(int idEdital, String codigoProjetoPesquisa)
	{
		// TODO Grupo 7: implementar este método em função dos casos de uso #14 e #15
		return null;
	}

	/**
	 * Indica as notas de alinhamento de um projeto de pesquisa na avaliação inicial
	 */
	public boolean indicaNotasAlinhamentoInicial(int idInscricao, InscricaoProjetoPesquisa avaliacao)
	{
		// Muda o campo justificativaNotasInicial do alinhamento da inscrição e projeto de pesquisa de acordo com o parâmetro
		// Muda o campo jsonSubcriteriosInicial com as avaliações dos critérios recebidas como parâmetro
		// Muda o campo notaFinal de acordo com o parâmetro
		// Somente se o campo homologadoInicial estiver TRUE ou o campo homologadoRecurso estiver TRUE
		// Somente se o campo dispensadoProvaInicial estiver FALSE ou dispensadoProvaRecurso estiver FALSE
		// Somente se a nota final de todas as provas escritas for maior do que a nota mínima para aprovação
		// Somente se o projeto de pesquisa não exigir prova oral ou estiver presente na prova oral
		// TODO Grupo 7: implementar este método em função do caso de uso #14
		return false;
	}
	
	/**
	 * Indica as notas de alinhamento de um projeto de pesquisa no recurso
	 */
	public boolean indicaNotasAlinhamentoRecurso(int idInscricao, InscricaoProjetoPesquisa avaliacao)
	{
		// Muda o campo justificativaNotasRecurso do alinhamento da inscrição e projeto de pesquisa de acordo com o parâmetro
		// Muda o campo jsonSubcriteriosRecurso com as avaliações dos critérios recebidas como parâmetro
		// Muda o campo notaFinal de acordo com o parâmetro
		// Somente se o campo homologadoInicial estiver TRUE ou o campo homologadoRecurso estiver TRUE
		// Somente se o campo dispensadoProvaInicial estiver FALSE ou dispensadoProvaRecurso estiver FALSE
		// Somente se a nota final de todas as provas escritas for maior do que a nota mínima para aprovação
		// Somente se o projeto de pesquisa não exigir prova oral ou estiver presente na prova oral
		// TODO Grupo 7: implementar este método em função do caso de uso #15
		return false;
	}
	
	// TODO criar script para povoar as inscrições para os nossos editais
}