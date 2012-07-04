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

prepareForMLP <- function(data, orth=c(1:7), disc=c(8:11)) {

    mlp_data = NULL 

    #   Orthogonalize values in 1:6

    cat(sprintf("Orthogonalizing columns...\n"))

    for (i in orth) {

        cat(sprintf("\t...column %s\n", colnames(data)[i]))

        ortho_columns = orthogonalize(data[,i])
        mlp_data = cbind(mlp_data, ortho_columns)
    }

    cat(sprintf("Creating bins for continuous data...\n"))

    #   Discretize values in 7:9

    for (i in disc) {

        cat(sprintf("\t...column %s\n", colnames(data)[i]))

        disc_column = continuousToDiscrete(data[,i])
        mlp_data = cbind(mlp_data, disc_column)
    }
    #   Set label values as 0 for all different than 1

    res_col = data[,ncol(data)]
    
    res_col[res_col != 1] = 0

    mlp_data = cbind(mlp_data, res_col)

    mlp_data 
}

orthogonalize <- function(vec) {

    len = length(unique(vec))

    vec = sort(vec)

    last_value = vec[1]

    cur_pos = 1

    result = NULL; 

    for (i in 1:length(vec)) {

        ortho_value = rep(0,len)

        cur_value = vec[i]

        if (cur_value != last_value) {

            cur_pos = cur_pos + 1
        }

        ortho_value[cur_pos] = 1

        last_value = cur_value

        result = rbind(result, ortho_value)
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

    result[result[,1] > 0.5] = 1
    result[result[,1] <= 0.5] = 0

    sucess = length(which(result[,1] == result[,2]))
set ts=4
    total = nrow(result)

    prob = (total / sucess) * 100

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
