# Mutual Information calculation requires 'entropy'

mutualInformation <- function(data, EPS=0.001){

    require('entropy')

	mi_mat = matrix(0, nrow = ncol(data), ncol = ncol(data));

	for (i in 1:ncol(data)) {

		col_i = data[,i];

		for (j in 1: ncol(data)) {

			col_j = data[,j];

			freqs = rbind(col_i, col_j);

			mut_info = mi.plugin(freqs);

        if (is.na(mut_info)) {
      
            print('NaN Value for Mutual Information.');  
            mut_info = 0;

        }
			
			mi_mat[i,j] = mut_info;
		}
		
	}
	mi_mat
}

