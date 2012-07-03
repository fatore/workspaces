SELECT 	

	c.birthtown_id as cidade_natal,
	c.birthstate_id as uf_natal,
	c.citizenship_id as cidadania,
	c.sex_id as sexo,
	
	s.job_id as ocupacao,
	s.maritalstatus_id as estado_civil,
	
	sch.tseid as escolaridade,
	s.age as idade,
	cr.max_expenses as despesas_maximas,
	
	cr.result_id as resultado
	
	from (
		((candidate c join status s on c.id = s.candidate_id) join 
			candidature cr on c.id = cr.candidate_id) join
			election e on cr.election_id = e.id
		) join schooling sch on s.schooling_id = sch.id
		
	where e.uf = 'SP' ;