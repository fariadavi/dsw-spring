package br.unirio.dsw.selecaoppgi.service.relatorio;

import java.util.List;

import com.itextpdf.text.Anchor;
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
import br.unirio.dsw.selecaoppgi.model.inscricao.InscricaoEdital;

public class HomologacaoService {

	/**
	 * Monta o relatório dos alunos homologados inicialmente e retorna
	 * @throws DocumentException 
	 */
	public void mostraRelatorioHomologacaoInicial(Edital edital,Document document,List<InscricaoEdital> inscricoesHomologadas,List<InscricaoEdital> inscricoesNaoHomologadas) throws DocumentException
	{
		Font chapterFont = new Font(Font.FontFamily.TIMES_ROMAN, 18,
	            Font.BOLD);
	    Font subChapterFont = new Font(Font.FontFamily.TIMES_ROMAN, 16,
	            Font.BOLD);
	 	
		document.addTitle("Homologação de Inscrições");
        document.addSubject("Homologação de Inscrições");
        document.addKeywords("Unirio, PDF, Homologação de Inscrições");
        document.addAuthor("Unirio");
        document.addCreator("Unirio");
        
        Paragraph preface = new Paragraph();        
        preface.add(new Paragraph(" "));                
        
        Anchor anchor = new Anchor("Homologação de Inscrições", chapterFont);        
        
		Chapter chapter = new Chapter(new Paragraph(anchor), 1);
        Paragraph subParagraph = new Paragraph(edital.getNome(), subChapterFont);        
        
        Section section = chapter.addSection(subParagraph);		
		
		FillDocumentOfHomologacao("Inscrições Homologadas","Inicial",inscricoesHomologadas,document,edital,section);
		FillDocumentOfHomologacao("Inscrições Não-homologadas","Inicial",inscricoesNaoHomologadas,document,edital,section);
		
		document.add(chapter);		
	}	
	
	/**
	 * Monta o relatório de alunos homologados em recurso e retorna
	 * @throws DocumentException 
	 */
	public void mostraRelatorioHomologacaoRecurso(Edital edital,Document document,List<InscricaoEdital> inscricoesHomologadas,List<InscricaoEdital> inscricoesNaoHomologadas) throws DocumentException
	{
		Font chapterFont = new Font(Font.FontFamily.TIMES_ROMAN, 18,
	            Font.BOLD);
	    Font subChapterFont = new Font(Font.FontFamily.TIMES_ROMAN, 16,
	            Font.BOLD);
	  
		document.addTitle("Homologação de Inscrições em Recurso");
        document.addSubject("Homologação de Inscrições em Recurso");
        document.addKeywords("Unirio, PDF, Homologação de Inscrições em Recurso");
        document.addAuthor("Unirio");
        document.addCreator("Unirio");
        
        Paragraph preface = new Paragraph();        
        preface.add(new Paragraph(" "));                
        
        Anchor anchor = new Anchor("Homologação de Inscrições em Recurso", chapterFont);
                
        Chapter chapter = new Chapter(new Paragraph(anchor), 1);
        Paragraph subParagraph = new Paragraph(edital.getNome(), subChapterFont);
        Section section = chapter.addSection(subParagraph);
        
        FillDocumentOfHomologacao("Inscrições Homologadas em Recurso","Recurso",inscricoesHomologadas,document,edital,section);
		FillDocumentOfHomologacao("Inscrições Não-homologadas em Recurso","Recurso",inscricoesNaoHomologadas,document,edital,section);
		
		document.add(chapter);		
	}
	
	public void FillDocumentOfHomologacao(String tipoHomologacao,String faseHomologacao,List<InscricaoEdital> inscricoes,Document document,Edital edital,Section section) throws DocumentException {
			       
        Paragraph paragraph = new Paragraph();
        for (int i = 0; i < 2; i++) {
            paragraph.add(new Paragraph(" "));
        }
        section.add(paragraph);
        
        Paragraph tituloParte = new Paragraph(tipoHomologacao);
        for (int i = 0; i < 2; i++) {
            tituloParte.add(new Paragraph(" "));
        }        
        
        section.add(tituloParte);
        
        
        PdfPTable table = null;
        if (tipoHomologacao == "Inscrições Não-homologadas" || tipoHomologacao == "Inscrições Não-homologadas em Recurso") {
        	table = new PdfPTable(2);
        }else {
        	table = new PdfPTable(1);
        }
        
        PdfPCell c1 = new PdfPCell(new Phrase("Inscrição"));
        c1.setHorizontalAlignment(Element.ALIGN_CENTER);
        table.addCell(c1);
        
        if (tipoHomologacao == "Inscrições Não-homologadas" || tipoHomologacao == "Inscrições Não-homologadas em Recurso") {
        	c1 = new PdfPCell(new Phrase("Justificativa"));
        	c1.setHorizontalAlignment(Element.ALIGN_CENTER);
        	table.addCell(c1);        	  
        } 
       
        table.setHeaderRows(1);
        
        for (InscricaoEdital inscricao : inscricoes) {
       	 table.addCell(inscricao.getNomeCandidato());  
       	 if (faseHomologacao == "Inicial" && tipoHomologacao == "Inscrições Não-homologadas" || tipoHomologacao == "Inscrições Não-homologadas em Recurso") {
       		table.addCell(inscricao.getJustificativaHomologacaoOriginal());
       	 }else if (faseHomologacao == "Recurso" && tipoHomologacao == "Inscrições Não-homologadas" || tipoHomologacao == "Inscrições Não-homologadas em Recurso") {
       		table.addCell(inscricao.getJustificativaHomologacaoRecurso());
       	 }
       }
        
        section.add(table);                		
	}
			
	
}
