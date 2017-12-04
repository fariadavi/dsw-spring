package br.unirio.dsw.selecaoppgi.service.dao;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;

import com.itextpdf.text.Anchor;
import com.itextpdf.text.BaseColor;
import com.itextpdf.text.Chapter;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Element;
import com.itextpdf.text.Font;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.Section;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;

import br.unirio.dsw.selecaoppgi.model.edital.Edital;
import br.unirio.dsw.selecaoppgi.model.inscricao.AvaliacaoProvaEscrita;
import br.unirio.dsw.selecaoppgi.model.inscricao.InscricaoEdital;
import br.unirio.dsw.selecaoppgi.model.inscricao.InscricaoProjetoPesquisa;

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

		InscricaoEdital inscricao = new InscricaoEdital(edital);
		inscricao.setId(rs.getInt("id"));
		
		inscricao.setIdCandidato(idCandidato);
		if(rs.getString("nomeCandidato") != null)
			inscricao.setNomeCandidato(rs.getString("nomeCandidato"));
		else
			inscricao.setNomeCandidato(userDAO.carregaUsuarioId(idCandidato).getNome());
		
		inscricao.setCotaNegros(rs.getInt("cotaNegros") != 0);
		inscricao.setCotaDeficientes(rs.getInt("cotaDeficientes") != 0);
		
		if(!(rs.getInt("homologadoInicial") == 0 && rs.getString("justificativaHomologacaoInicial") == null))
			inscricao.setHomologadoOriginal(rs.getInt("homologadoInicial") != 0);
		if(!(rs.getInt("homologadoRecurso") == 0 && rs.getString("justificativaHomologacaoRecurso") == null))
			inscricao.setHomologadoRecurso(rs.getInt("homologadoRecurso") != 0);
		
		if(!(rs.getInt("dispensadoProvaInicial") == 0 && rs.getString("justificativaDispensaInicial") == null))
			inscricao.setDispensadoProvaOriginal(rs.getInt("dispensadoProvaInicial") != 0);
		if(!(rs.getInt("dispensadoProvaRecurso") == 0 && rs.getString("justificativaDispensaRecurso") == null))
			inscricao.setDispensadoProvaRecurso(rs.getInt("dispensadoProvaRecurso") != 0);

		inscricao.setJustificativaHomologacaoOriginal(rs.getString("justificativaHomologacaoInicial"));
		inscricao.setJustificativaHomologacaoRecurso(rs.getString("justificativaHomologacaoRecurso"));
		inscricao.setJustificativaDispensaOriginal(rs.getString("justificativaDispensaInicial"));
		inscricao.setJustificativaDispensaRecurso(rs.getString("justificativaDispensaRecurso"));
		
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
	public List<InscricaoEdital> carregaAvaliacaoHomologacao(int idEdital, int pagina, int tamanhoPagina, String filtroNome, String filtroStatus)
	{
		String SQL = "SELECT i.*, u.nome as nomeCandidato " + 
					 "FROM Inscricao i INNER JOIN USUARIO u ON i.idCandidato = u.id " + 
					 "WHERE i.idEdital = ? AND homologadoRecurso = 0 AND justificativaHomologacaoRecurso IS NULL AND u.nome LIKE ? ";
		
		String SQLStatus = "";
		
		switch(filtroStatus) {
			case "Homologados":
				SQLStatus = "AND i.homologadoInicial = 1 ";
				break;
			case "Não-homologados":
				SQLStatus = "AND i.homologadoInicial = 0 AND justificativaHomologacaoInicial IS NOT NULL ";
				break;
			case "Aguardando homologação":
				SQLStatus = "AND i.homologadoInicial = 0 AND justificativaHomologacaoInicial IS NULL ";
				break;
		}
		
		String SQLPaging = "ORDER BY u.nome ASC LIMIT ? OFFSET ?";
		
		Connection c = getConnection();
		
		if (c == null)
			return null;
		
		try
		{
			List<InscricaoEdital> listItems = new ArrayList<InscricaoEdital>();
			
			PreparedStatement ps = c.prepareStatement(SQL + SQLStatus + SQLPaging);
			ps.setLong(1, idEdital);
			ps.setString(2, "%" + filtroNome + "%");
			ps.setInt(3, tamanhoPagina);
			ps.setInt(4, pagina * tamanhoPagina);
			
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
	 * Carrega a lista de inscrições não homologadas de um determinado edital que podem ser homologadas
	 */
	public List<InscricaoEdital> carregaAvaliacaoHomologacaoRecurso(int idEdital, int pagina, int tamanhoPagina, String filtroNome, String filtroStatus)
	{
		String SQL = "SELECT i.*, u.nome as nomeCandidato " + 
					 "FROM Inscricao i INNER JOIN USUARIO u ON i.idCandidato = u.id " + 
					 "WHERE i.idEdital = ? AND homologadoInicial = 0 AND justificativaHomologacaoInicial IS NOT NULL AND u.nome LIKE ? ";
		
		String SQLStatus = "";
			
		switch(filtroStatus) {
			case "Homologados":
				SQLStatus = "AND i.homologadoRecurso = 1 "; 
				break;
			case "Não-homologados":
				SQLStatus = "AND i.homologadoRecurso = 0 AND justificativaHomologacaoRecurso IS NOT NULL ";
				break;
			case "Aguardando homologação":
				SQLStatus = "AND i.homologadoRecurso = 0 AND justificativaHomologacaoRecurso IS NULL ";
				break;
		}
		
		String SQLPaging = "ORDER BY u.nome ASC LIMIT ? OFFSET ?";
		
		Connection c = getConnection();
		
		if (c == null)
			return null;
		
		try
		{
			List<InscricaoEdital> listItems = new ArrayList<InscricaoEdital>();
			
			PreparedStatement ps = c.prepareStatement(SQL + SQLStatus + SQLPaging);
			ps.setLong(1, idEdital);
			ps.setString(2, "%" + filtroNome + "%");
			ps.setInt(3, tamanhoPagina);
			ps.setInt(4, pagina * tamanhoPagina);
			
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
	 * Conta o numero de inscricoes em um edital que atendem aos filtros usados
	 */
	public int conta(int idEdital, String filtroNome, String filtroStatus, String comissao)
	{
		String SQL = "SELECT i.*, u.nome as nomeCandidato " + 
				 "FROM Inscricao i INNER JOIN USUARIO u ON i.idCandidato = u.id " + 
				 "WHERE i.idEdital = ? AND u.nome LIKE ? ";
	
		String SQLStatus = "";
		
		switch(filtroStatus) {
			case "Homologados":
				SQLStatus = "AND i.homologado" + comissao + " = 1";
				break;
			case "Não-homologados":
				SQLStatus = "AND i.homologado" + comissao + " = 0 AND justificativaHomologacao" + comissao + " IS NOT NULL ";
				break;
			case "Aguardando homologação":
				SQLStatus = "AND i.homologado" + comissao + " = 0 AND justificativaHomologacao" + comissao + " IS NULL ";
				break;
			case "Dispensados":
				SQLStatus = "AND i.dispensadoProva" + comissao + " = 1";
				break;
			case "Não-dispensados":
				SQLStatus = "AND i.dispensadoProva" + comissao + " = 0 AND justificativaDispensa" + comissao + " IS NOT NULL ";
				break;
			case "Aguardando dispensa":
				SQLStatus = "AND i.dispensadoProva" + comissao + " = 0 AND justificativaDispensa" + comissao + " IS NULL ";
				break;
		}
		
		Connection c = getConnection();
		
		if (c == null)
			return 0;
		
		try
		{
			PreparedStatement ps = c.prepareStatement(SQL + SQLStatus);
			ps.setLong(1, idEdital);
			ps.setString(2, "%" + filtroNome + "%");

			ResultSet rs = ps.executeQuery();
			int count = rs.next() ? rs.getInt(1) : 0;

			c.close();
			return count;

		} catch (SQLException e)
		{
			log("InscricaoDAO.conta: " + e.getMessage());
			return 0;
		}
	}
	
	/**
	 * Registra a homologação de uma inscrição na avaliação inicial
	 */
	public boolean homologacaoInicial(int idInscricao)
	{
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
	 * Monta o relatório inicial e retorna
	 * @throws DocumentException 
	 */
	public void mostraRelatorioHomologacaoInicial(Edital edital,Document document,List<InscricaoEdital> inscricoes) throws DocumentException
	{
		Font catFont = new Font(Font.FontFamily.TIMES_ROMAN, 18,
	            Font.BOLD);
	    Font redFont = new Font(Font.FontFamily.TIMES_ROMAN, 12,
	            Font.NORMAL, BaseColor.RED);
	    Font subFont = new Font(Font.FontFamily.TIMES_ROMAN, 16,
	            Font.BOLD);
	    Font smallBold = new Font(Font.FontFamily.TIMES_ROMAN, 12,
	            Font.BOLD);
		
		document.addTitle("Relatório Homologação");
        document.addSubject("Homologação Inicial");
        document.addKeywords("Unirio, PDF, Homologação Inicial");
        document.addAuthor("Unirio");
        document.addCreator("Unirio");
        
        Paragraph preface = new Paragraph();        
        preface.add(new Paragraph(" "));        
        preface.add(new Paragraph("Homologação Inicial", catFont));
        
        Anchor anchor = new Anchor("Homologação Inicial", catFont);
        //anchor.setName("Homologação Inicial");        
        Chapter catPart = new Chapter(new Paragraph(anchor), 1);
        Paragraph subPara = new Paragraph(edital.getNome(), subFont);
        Section subCatPart = catPart.addSection(subPara);
        
        Paragraph paragraph = new Paragraph();
        for (int i = 0; i < 2; i++) {
            paragraph.add(new Paragraph(" "));
        }
        subCatPart.add(paragraph);
        
        PdfPTable table = new PdfPTable(1);        
        PdfPCell c1 = new PdfPCell(new Phrase("Homologados"));
        c1.setHorizontalAlignment(Element.ALIGN_CENTER);
        table.addCell(c1);
        
        table.setHeaderRows(1);  
       // table.addCell("Aluno 1");
       // table.addCell("Aluno 2");
        for (InscricaoEdital inscricao : inscricoes) {
       	 table.addCell(inscricao.getNomeCandidato());             
       }
        
        subCatPart.add(table);
        
        document.add(catPart);
	}
	
	/**
	 * Monta o relatório de recurso e retorna
	 * @throws DocumentException 
	 */
	public void mostraRelatorioHomologacaoRecurso(Edital edital,Document document,List<InscricaoEdital> inscricoes) throws DocumentException
	{
		Font catFont = new Font(Font.FontFamily.TIMES_ROMAN, 18,
	            Font.BOLD);
	    Font redFont = new Font(Font.FontFamily.TIMES_ROMAN, 12,
	            Font.NORMAL, BaseColor.RED);
	    Font subFont = new Font(Font.FontFamily.TIMES_ROMAN, 16,
	            Font.BOLD);
	    Font smallBold = new Font(Font.FontFamily.TIMES_ROMAN, 12,
	            Font.BOLD);
		
		document.addTitle("Relatório Homologação");
        document.addSubject("Homologação de Recurso");
        document.addKeywords("Unirio, PDF, Homologação de Recurso");
        document.addAuthor("Unirio");
        document.addCreator("Unirio");
        
        Paragraph preface = new Paragraph();        
        preface.add(new Paragraph(" "));        
        preface.add(new Paragraph("Homologação de Recurso", catFont));
        
        Anchor anchor = new Anchor("Homologação de Recurso", catFont);
        anchor.setName("Homologação de Recurso");        
        Chapter catPart = new Chapter(new Paragraph(anchor), 1);
        Paragraph subPara = new Paragraph(edital.getNome(), subFont);
        Section subCatPart = catPart.addSection(subPara);
        
        Paragraph paragraph = new Paragraph();
        for (int i = 0; i < 2; i++) {
            paragraph.add(new Paragraph(" "));
        }
        subCatPart.add(paragraph);
        
        PdfPTable table = new PdfPTable(1);        
        PdfPCell c1 = new PdfPCell(new Phrase("Homologados Recurso"));
        c1.setHorizontalAlignment(Element.ALIGN_CENTER);
        table.addCell(c1);
        
        table.setHeaderRows(1);        
       // table.addCell("Aluno 1");
       // table.addCell("Aluno 2");
        for (InscricaoEdital inscricao : inscricoes) {
       	 table.addCell(inscricao.getNomeCandidato());             
       }
       
        
        subCatPart.add(table);
        
        document.add(catPart);
	}

	
	/**
	 * Carrega a lista de inscrições de um determinado edital que podem ser dispensados de prova
	 */
	
	public List<InscricaoEdital> carregaAvaliacaoDispensaProvaInicial(int idEdital, int pagina, int tamanhoPagina, String filtroNome, String filtroStatus)
	{
		String SQL = "SELECT i.*, u.nome as nomeCandidato " + 
					 "FROM Inscricao i INNER JOIN USUARIO u ON i.idCandidato = u.id " + 
					 "WHERE i.idEdital = ? AND dispensadoProvaRecurso = 0 AND justificativaDispensaRecurso IS NULL AND u.nome LIKE ? ";
		
		String SQLStatus = "";
		
		switch(filtroStatus) {
			case "Dispensados":
				SQLStatus = "AND i.dispensadoProvaInicial = 1 ";
				break;
			case "Não-dispensados":
				SQLStatus = "AND i.dispensadoProvaInicial = 0 AND justificativaDispensaInicial IS NOT NULL ";
				break;
			case "Aguardando dispensa":
				SQLStatus = "AND i.dispensadoProvaInicial = 0 AND justificativaDispensaInicial IS NULL ";
				break;
		}
		
		String SQLPaging = "ORDER BY u.nome ASC LIMIT ? OFFSET ?";
		
		Connection c = getConnection();
		
		if (c == null)
			return null;
		
		try
		{
			List<InscricaoEdital> listItems = new ArrayList<InscricaoEdital>();
			
			PreparedStatement ps = c.prepareStatement(SQL + SQLStatus + SQLPaging);
			ps.setLong(1, idEdital);
			ps.setString(2, "%" + filtroNome + "%");
			ps.setInt(3, tamanhoPagina);
			ps.setInt(4, pagina * tamanhoPagina);
			
			ResultSet rs = ps.executeQuery();
			
			while(rs.next())
				listItems.add(carrega(rs));
			
			c.close();
			return listItems;

		} catch (SQLException e)
		{
			log("InscricaoDAO.carregaAvaliacaoDispensaProva: " + e.getMessage());
			return null;
		}
	}
	
	/**
	 * Carrega a lista de inscrições de provas não dispensadas de um determinado edital que podem ser dispensadas
	 */
	public List<InscricaoEdital> carregaAvaliacaoDispensaProvaRecurso(int idEdital, int pagina, int tamanhoPagina, String filtroNome, String filtroStatus)
	{		
		String SQL = "SELECT i.*, u.nome as nomeCandidato " + 
					 "FROM Inscricao i INNER JOIN USUARIO u ON i.idCandidato = u.id " + 
					 "WHERE i.idEdital = ? AND dispensadoProvaInicial = 0 AND justificativaDispensaInicial IS NOT NULL AND u.nome LIKE ? ";
		
		String SQLStatus = "";
		
		switch(filtroStatus) {
			case "Dispensados":
				SQLStatus = "AND i.dispensadoProvaRecurso = 1 "; 
				break;
			case "Não-dispensados":
				SQLStatus = "AND i.dispensadoProvaRecurso = 0 AND justificativaDispensaRecurso IS NOT NULL ";
				break;
			case "Aguardando dispensa":
				SQLStatus = "AND i.dispensadoProvaRecurso = 0 AND justificativaDispensaRecurso IS NULL ";
				break;
		}
		
		String SQLPaging = "ORDER BY u.nome ASC LIMIT ? OFFSET ?";
		
		Connection c = getConnection();
		
		if (c == null)
			return null;
		
		try
		{
			List<InscricaoEdital> listItems = new ArrayList<InscricaoEdital>();
			
			PreparedStatement ps = c.prepareStatement(SQL + SQLStatus + SQLPaging);
			ps.setLong(1, idEdital);
			ps.setString(2, "%" + filtroNome + "%");
			ps.setInt(3, tamanhoPagina);
			ps.setInt(4, pagina * tamanhoPagina);
			
			ResultSet rs = ps.executeQuery();
			
			while(rs.next())
				listItems.add(carrega(rs));
			
			c.close();
			return listItems;

		} catch (SQLException e)
		{
			log("InscricaoDAO.carregaAvaliacaoDispensaProvaRecurso: " + e.getMessage());
			return null;
		}
	}
	

	/**
	 * Registra a dispensa de provas de uma inscrição na avaliação inicial
	 */
	public boolean dispensaProvaInicial(int idInscricao)
	{
		Connection c = getConnection();
		
		if (c == null)
			return false;
		
		try
		{
			CallableStatement cs = c.prepareCall("{call InscricaoDispensaProvaInicial(?)}");
			cs.setInt(1, idInscricao);			
			cs.execute();
			c.close();
			return true;

		} catch (SQLException e)
		{
			log("InscricaoDAO.DispensaProvaInicial: " + e.getMessage());
			return false;
		}				
	}
	
	/**
	 * Registra a recusa de dispensa de provas de uma inscrição na avaliação inicial
	 */
	public boolean recusaDispensaProvaInicial(int idInscricao, String justificativa)
	{
		Connection c = getConnection();
		
		if (c == null)
			return false;
		
		try
		{
			CallableStatement cs = c.prepareCall("{call InscricaoRecusaDispensaProvaInicial(?,?)}");
			cs.setInt(1, idInscricao);		
			cs.setString(2, justificativa);
			cs.execute();
			c.close();
			return true;

		} catch (SQLException e)
		{
			log("InscricaoDAO.RecusaDispensaProvaInicial: " + e.getMessage());
			return false;
		}				
	}
	
	/**
	 * Registra a dispensa de provas de uma inscrição no recurso
	 */
	public boolean dispensaProvaRecurso(int idInscricao)
	{
		Connection c = getConnection();
		
		if (c == null)
			return false;
		
		try
		{
			CallableStatement cs = c.prepareCall("{call InscricaoDispensaProvaRecurso(?)}");
			cs.setInt(1, idInscricao);			
			cs.execute();
			c.close();
			return true;

		} catch (SQLException e)
		{
			log("InscricaoDAO.DispensaProvaRecurso: " + e.getMessage());
			return false;
		}	
	}
	
	/**
	 * Registra a recusa de dispensa de provas de uma inscrição no recurso
	 */
	public boolean recusaDispensaProvaRecurso(int idInscricao, String justificativa)
	{
		Connection c = getConnection();
		
		if (c == null)
			return false;
		
		try
		{
			CallableStatement cs = c.prepareCall("{call InscricaoRecusaDispensaProvaRecurso(?,?)}");
			cs.setInt(1, idInscricao);		
			cs.setString(2, justificativa);
			cs.execute();
			c.close();
			return true;

		} catch (SQLException e)
		{
			log("InscricaoDAO.RecusaDispensaProvaRecurso: " + e.getMessage());
			return false;
		}	
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