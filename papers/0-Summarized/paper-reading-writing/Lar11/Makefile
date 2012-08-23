all:
	pdflatex -halt-on-error resumo.tex > log.txt
	bibtex resumo.aux > log.txt
	pdflatex -halt-on-error resumo.tex > log.txt
	
	rm *.bbl *blx.bib *.out *.run.xml *.aux *.blg *.log 
		
run: all
	okular resumo.pdf &
	
