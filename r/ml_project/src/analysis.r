loadData <- function(filename) {

    require('lattice')

    #   Load data

    data = as.matrix(read.table(filename, sep="|", header=TRUE))

    cat(sprintf("Total elements loaded %d.\n", nrow(data))) 

    #   Remove NaN values

    data  = removeNans(data)

    cat(sprintf("Valid elements %d.\n", nrow(data))) 

    data
}

analyseMI <- function(data) {

    data = cleanLabel(data)

    #   Normalize values between [0..1]

    norm_data = normalizeMatrix(data)

    result = list() 

    #   Calculate mutual information for all attributes

    cat(sprintf('Calculating mutual information...\n'))

    mi_matrix = mutualInformation(norm_data)

    result$mi_matrix = mi_matrix

    result$output = mi_matrix[nrow(mi_matrix),]

    result
}

prepareForMLP <- function(data, orth=c(2,4,5,6,7), disc=c(10:11)) {

    mlp_data = NULL 

    #   Orthogonalize values in 1:7

    cat(sprintf("Orthogonalizing columns...\n"))

    for (i in orth) {

        cat(sprintf("\t...column %s ", colnames(data)[i]))

        ortho_columns = orthogonalize(data[,i])

        cat(sprintf("%d\n", ncol(as.matrix(ortho_columns))))

        mlp_data = cbind(mlp_data, ortho_columns)
    }

    cat(sprintf("Creating bins for continuous data...\n"))

    #   Discretize values in 8:11

    for (i in disc) {

        cat(sprintf("\t...column %s ", colnames(data)[i]))

        disc_column = continuousToDiscrete(data[,i])

        cat(sprintf("%d\n", ncol(as.matrix(disc_column))))

        mlp_data = cbind(mlp_data, disc_column)
    }
    #   Set label values as 0 for all different than 1

    res_col = data[,ncol(data)]
    
    res_col[res_col != 1] = 0

    mlp_data = cbind(mlp_data, res_col)

    mlp_data 
}

orthogonalize <- function(vec) {

        temp = sort(unique(vec))

        len = length(temp)

        result = NULL

        if (len < 3) {

                result = vec

        } else {

                for (i in 1:length(vec)) {

                        orth_vec = rep(0, len)

                        ind = which(temp == vec[i])

                        orth_vec[ind] = 1

                        result = rbind(result, orth_vec)
                }
        }      
        result
}

cleanLabel <- function(data) {

        label_col = data[,ncol(data)]

        label_col[label_col != 1] = 0

    data[,ncol(data)] = label_col

    data
    
}

removeNans <- function(data) {

    ind <- colSums(is.na(data)) != nrow(data)

    noInvalids = length(which(ind == FALSE))

    if (noInvalids > 0) {

        for (i in 1 : noInvalids) {

            cat(sprintf("Column %s was removed.\n", 
                        colnames(data)[which(ind == FALSE)[i]]))
        }
        data = data[,ind]
    }

    fixed_data = data[complete.cases(data),];

    fixed_data
}

continuousToDiscrete <- function(cont) {

    h = hist(cont, plot = FALSE, breaks = 16)
    states = c()

    for (i in 1 : length(cont)) {

        state = which(h$breaks >= cont[i])[1] - 1
        states = c(states, state)
    }

    states
}

normalizeMatrix <- function(mat, col=0) {

    res = mat;

    if (col == 0) {

        for (i in 1:ncol(mat)) {

            if (max(mat[,i]) != 0) {

                res[,i] =
                (mat[,i] - min(mat[,i])) /
                (max(mat[,i])- min(mat[,i]))
            }
        }
    }
    else {

        if (max(mat[,i]) != 0) {

            res[,i] =
            (mat[,i] - min(mat[,i])) /
            (max(mat[,i])- min(mat[,i]))
        }
    }
    res
}

getProbs <- function(result, thres=0.5) {

        result = as.matrix(result)

        values = result[,1]
        
        values[values > 0.5] = 1
        values[values <= 0.5] = 0

        result[,1] = values

        sucess = length(which(result[,1] == result[,2]))
        total = nrow(result)

        prob = (sucess / total) * 100

        prob
}

probWomanTotal <- function(data) {

        wins = which(data[,ncol(data)] == 1);
        
        w_wins = which(data[wins,4] == 1)
        m_wins = which(data[wins,4] == 0)

        total = length(wins)
        
        w_count = length(w_wins)

        prob = (w_count / total) * 100

        prob
}

probOnesAndZeros<- function(data, col) {

        wins = which(data[,ncol(data)] == 1);
        
        one_wins = which(data[wins,col] == 1)
        zero_wins = which(data[wins,col] == 0)

        total = length(wins)
        
        one_count = length(one_wins)

        prob = (one_count / total) * 100

        prob
}

plotBarMI <- function(mi) {

        result = mi$output

        result = result[1:length(result)-1]

        barplot(as.matrix(result), beside=TRUE, col=rainbow(length(result)),
                names.arg = c(1:length(result)));
}

catOcup <- function(data, ocups=c(10, 180, 163, 143, 167, 100, 179, 4, 18), col=6) {

        ocupations = data[,col]
        ocupations[ocupations %in% ocups] = -1
        ocupations[ocupations > 0] = 0
        ocupations[ocupations < 0] = 1
       
        data[,col] = ocupations;

        data
}


catState <- function(data, state=4, col=2) {

        states = data[,col]

        states[states == state] = -1
        states[states > 0] = 0
        states[states < 0] = 1
        
        data[,col] = states

        data
}

getTrainMLP <- function(mlp_data) {


}
