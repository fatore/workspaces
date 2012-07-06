all:
	pdflatex -halt-on-error src/paper.tex > log.txt
	bibtex paper.aux > log.txt
	pdflatex -halt-on-error src/paper.tex > log.txt
	
	rm *.bbl *blx.bib *.out *.run.xml *.aux *.blg *.log 
		
clean:
	rmdir bin/
	rmdir temp/

run: all
	okular bin/paper.pdf &
	
