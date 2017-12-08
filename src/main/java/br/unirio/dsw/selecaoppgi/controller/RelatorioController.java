package br.unirio.dsw.selecaoppgi.controller;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.itextpdf.text.Document;
import com.itextpdf.text.pdf.PdfWriter;

import br.unirio.dsw.selecaoppgi.model.edital.Edital;
import br.unirio.dsw.selecaoppgi.model.inscricao.InscricaoEdital;
import br.unirio.dsw.selecaoppgi.model.usuario.Usuario;
import br.unirio.dsw.selecaoppgi.service.dao.EditalDAO;
import br.unirio.dsw.selecaoppgi.service.dao.InscricaoDAO;
import br.unirio.dsw.selecaoppgi.service.dao.UsuarioDAO;
import br.unirio.dsw.selecaoppgi.service.relatorio.HomologacaoService;

@Controller
public class RelatorioController
{
	@Autowired
	private EditalDAO editalDAO;
	@Autowired
	private UsuarioDAO userDAO;
	@Autowired
	private InscricaoDAO inscricaoDAO;
	
	HomologacaoService homologacaoService = new HomologacaoService();
	/**
	 * Ação AJAX que mostra relatório de homologação original
	 * @throws IOException 
	 */
	@ResponseBody
	@RequestMapping(value = "/relatorio/homologacao/homologacao/original", method = RequestMethod.GET, produces = "application/pdf")
	public ResponseEntity<byte[]> mostraRelatorioOriginal(HttpServletRequest request) throws IOException {
		Usuario usuario = (Usuario) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
			
		Edital edital = (Edital) request.getSession().getAttribute("edital");
//		String filePath = new File("C:/Users/Daniel/Desktop/dsw-spring-master/dsw-spring/src/main/java/br/unirio/dsw/selecaoppgi/PdfOriginal.pdf").getAbsolutePath();	 
//		File file = new File(filePath);
//		Path path = Paths.get(filePath);

		if ((edital == null || edital.getId() != usuario.getIdEdital()) && usuario.getIdEdital() > 0) {
			edital = editalDAO.carregaEditalId(usuario.getIdEdital(), userDAO);
			request.getSession().setAttribute("edital", edital);
		}
		
		List<InscricaoEdital> inscricoesHomologados = inscricaoDAO.carregaAvaliacaoHomologacao(edital.getId(), 0, 10,
				"", "Homologados");
		List<InscricaoEdital> inscricoesNaoHomologados = inscricaoDAO.carregaAvaliacaoHomologacao(edital.getId(), 0, 10,
				"", "Não-homologados");
		
		ByteArrayOutputStream output = new ByteArrayOutputStream();
		
		Document document = new Document();
		try {            
			PdfWriter.getInstance(document, output);
            document.open();
            //if (edital.getStatus() == StatusEdital.Homologacao) {
    			//for (Usuario u : edital.getComissaoSelecao()) {
    				//if (u.getId() == usuario.getId())
    					homologacaoService.mostraRelatorioHomologacaoInicial(edital,document,inscricoesHomologados,inscricoesNaoHomologados);			
    			//}
    		//}
            document.close();
        } catch (Exception e) {
            e.printStackTrace();
        }		
				
		byte[] contents = output.toByteArray();
		
		HttpHeaders headers = new HttpHeaders();
	    headers.setContentType(MediaType.parseMediaType("application/pdf"));
	    String filename = "PDFHomologadoOriginal.pdf";
	    headers.setContentDispositionFormData(filename, filename);
	    headers.setCacheControl("must-revalidate, post-check=0, pre-check=0");
	    ResponseEntity<byte[]> response = new ResponseEntity<byte[]>(contents, headers, HttpStatus.OK);
	    return response;
	
		//JsonObject root = new JsonObject();
		//root.addProperty("Result", "OK");		
	}
	
	/**
	 * Ação AJAX que mostra relatório de homologação de recurso
	 * @throws IOException 
	 */
	@ResponseBody
	@RequestMapping(value = "/relatorio/homologacao/homologacao/recurso", method = RequestMethod.GET, produces = "application/pdf")
	public ResponseEntity<byte[]> mostraRelatorioRecurso(HttpServletRequest request) throws IOException {
		Usuario usuario = (Usuario) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
			
		Edital edital = (Edital) request.getSession().getAttribute("edital");
	    //String filePath = new File("C:/Users/Daniel/Desktop/dsw-spring-master/dsw-spring/src/main/java/br/unirio/dsw/selecaoppgi/PDFRecurso.pdf").getAbsolutePath();	    			     
	    //File file = new File(filePath);
	    //Path path = Paths.get(filePath);
	    
		if ((edital == null || edital.getId() != usuario.getIdEdital()) && usuario.getIdEdital() > 0) {
			edital = editalDAO.carregaEditalId(usuario.getIdEdital(), userDAO);
			request.getSession().setAttribute("edital", edital);
		}
		
		List<InscricaoEdital> inscricoesHomologados = inscricaoDAO.carregaAvaliacaoHomologacaoRecurso(edital.getId(), 0, 10,
				"", "Homologados");
		List<InscricaoEdital> inscricoesNaoHomologados = inscricaoDAO.carregaAvaliacaoHomologacaoRecurso(edital.getId(), 0, 10,
				"", "Não-homologados");

		ByteArrayOutputStream output = new ByteArrayOutputStream();
		
		Document document = new Document();
		try {            
			PdfWriter.getInstance(document, output);
            document.open();
          //  if (edital.getStatus() == StatusEdital.Homologacao) {    			
    			//for (Usuario u : edital.getComissaoRecursos()) {
    				//if (u.getId() == usuario.getId())
				homologacaoService.mostraRelatorioHomologacaoRecurso(edital,document,inscricoesHomologados,inscricoesNaoHomologados);
    			//}
    		//}
            document.close();
        } catch (Exception e) {
            e.printStackTrace();
        }	
		
		byte[] contents = output.toByteArray();
		
		HttpHeaders headers = new HttpHeaders();
	    headers.setContentType(MediaType.parseMediaType("application/pdf"));
	    String filename = "PDFHomologadoRecurso.pdf";
	    headers.setContentDispositionFormData(filename, filename);
	    headers.setCacheControl("must-revalidate, post-check=0, pre-check=0");
	    ResponseEntity<byte[]> response = new ResponseEntity<byte[]>(contents, headers, HttpStatus.OK);
	    return response;
	}	
	
//	/relatorio/homologacao/dispensa/original
//	/relatorio/homologacao/dispensa/recurso
//	/relatorio/escritas/presenca
//	/relatorio/escritas/notas/original
//	/relatorio/escritas/notas/recurso
//	/relatorio/escritas/pendencias
//	/relatorio/alinhamento/presenca
//	/relatorio/alinhamento/notas/original
//	/relatorio/alinhamento/notas/recurso
//	/relatorio/alinhamento/pendencias
//	/relatorio/aprovacao
}