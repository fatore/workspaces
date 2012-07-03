all:
	pdflatex -output-directory=bin/ -halt-on-error src/paper.tex > temp/log.txt


create:
	mkdir bin/
	mkdir temp/

clean:
	rmdir bin/
	rmdir temp/

run: all
	okular bin/paper.pdf &
	
