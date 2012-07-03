# Correlation Plot requires 'lattice'

rgb.palette <- colorRampPalette(c("blue", "yellow"), space = "rgb")
levelplot(mi_mat, xlab="", ylab="", col.regions=rgb.palette(120))
levelplot(mi_mat, xlab="", ylab="")


result_mis = mi_mat[10,]
result_mis = result_mis[1:length(result_mis)-1]

plot(result_mis, type="o")

barplot(as.matrix(result_mis), ylab= "Mutual Information",
        main="Mutual Information for Election Result", 
        beside=TRUE, col=rainbow(length(result_mis)));


barplot(as.matrix(result_mis), beside=TRUE, col=rainbow(9));


# Write to file

# direct output to a file 
sink("myfile", append=FALSE, split=FALSE)

# return output to the terminal 
sink()
