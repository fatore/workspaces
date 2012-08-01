all:
	pdflatex -halt-on-error src/document.tex > log.txt
	bibtex document.aux > log.txt
	pdflatex -halt-on-error src/document.tex > log.txt
	
	rm *.bbl *blx.bib *.out *.run.xml *.aux *.blg *.log 
		
run: all
	okular document.pdf &
	
