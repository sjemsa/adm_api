create or replace package decision_maker.api
as
	invalid_amount_input				    exception;
	invalid_period_input				    exception;
	customer_has_debt						exception;
	cannot_calculate_new_period	            exception;
	customer_check_impossible	            exception;

	procedure get_decision(i_personal_code 	    in 				varchar2,
						   io_loan_amount 		in	out 		number,
						   io_loan_period 		in	out 		number);
end api;
/
create or replace package body decision_maker.api
is
------------------------------------------------------------------------------------------------------------------------
  ---Helpers
------------------------------------------------------------------------------------------------------------------------
	function get_minimum_amount 		return number 	is begin return 2000; 		end;
	function get_maximum_amount 		return number 	is begin return 10000;		end;
	function get_minimum_period 		return number 	is begin return 12;			end;
	function get_maximum_period 		return number 	is begin return 60;			end;
------------------------------------------------------------------------------------------------------------------------
	function get_score(i_credit_modifier 	    in 	number,
					   i_loan_amount 			in 	number,
					   i_loan_period 			in 	number)
		return number
		is
	begin
		return (i_credit_modifier / i_loan_amount) * i_loan_period;
	end get_score;
------------------------------------------------------------------------------------------------------------------------
	function is_scoring_valid(i_credit_modifier		    in	number,
							  i_loan_amount 			in	number,
							  i_loan_period 			in	number)
		return boolean
		is
		x_credit_score number;
	begin
		x_credit_score := get_score(i_credit_modifier, i_loan_amount, i_loan_period);
		return x_credit_score >= 1;
	end is_scoring_valid;
------------------------------------------------------------------------------------------------------------------------
	function get_customer_segment(i_personal_code in	varchar2)
		return number
		is
		x_segment_id customer_segment.segment_id%type;
	begin
		select segment_id
		  into x_segment_id
		  from customer_segment
		 where personal_code = i_personal_code;
		return x_segment_id;
	exception
	    when no_data_found then raise customer_check_impossible;
	end get_customer_segment;
------------------------------------------------------------------------------------------------------------------------
	procedure get_credit_modifier_by_segment(i_segment_id 		in		segment.id%type,
											 o_credit_modifier	out 	segment.credit_modifier%type)
		is
		x_credit_modifier segment.credit_modifier%type;
	begin
		select credit_modifier
		  into x_credit_modifier
		  from segment
		 where id = i_segment_id;
	if x_credit_modifier <> 0 then o_credit_modifier := x_credit_modifier;
	 else raise customer_has_debt;
	end if;
	end get_credit_modifier_by_segment;
------------------------------------------------------------------------------------------------------------------------
	procedure calculate_amount(i_credit_modifier 	in			number,
							   io_loan_amount 		in	out		number,
							   i_loan_period 		in			number)
		is
		x_loan_amount number;
	begin
		x_loan_amount := round((i_credit_modifier * i_loan_period),0);
		if x_loan_amount between get_minimum_amount and io_loan_amount
            then io_loan_amount := greatest(x_loan_amount,get_minimum_amount);
        elsif ((x_loan_amount between io_loan_amount and get_maximum_amount) and x_loan_amount < get_maximum_amount)
            then io_loan_amount := least(x_loan_amount,get_maximum_amount);
        elsif x_loan_amount > get_maximum_amount
            then io_loan_amount := get_maximum_amount;
        end if;
	end calculate_amount;
------------------------------------------------------------------------------------------------------------------------
	procedure calculate_new_period(i_credit_modifier 	in			number,
								   i_loan_amount 		in			number,
								   io_loan_period 		in	out		number)
		is
		x_loan_period number;
	begin
	  if i_credit_modifier <> 0 then
			x_loan_period := ceil((i_loan_amount / i_credit_modifier));
			if x_loan_period < get_maximum_period then io_loan_period := x_loan_period;
			else
				raise cannot_calculate_new_period;
			end if;
		end if;
	end calculate_new_period;
------------------------------------------------------------------------------------------------------------------------
	procedure validate_amount(i_loan_amount 		in 		number)
		is
	begin
		if i_loan_amount < get_minimum_amount
			or i_loan_amount > get_maximum_amount
		then
			raise invalid_amount_input;
		end if;
	end validate_amount;
------------------------------------------------------------------------------------------------------------------------
	procedure validate_period(i_loan_period 		in 		number)
		is
	begin
		if i_loan_period < get_minimum_period
		or i_loan_period > get_maximum_period
		then
			raise invalid_period_input;
		end if;
	end validate_period;
------------------------------------------------------------------------------------------------------------------------
	procedure validate_input(i_loan_amount		in 		number,
	  						 i_loan_period 		in 		number)

		is
	begin
		validate_amount(i_loan_amount);
		validate_period(i_loan_period);
	end validate_input;
------------------------------------------------------------------------------------------------------------------------
	---API
------------------------------------------------------------------------------------------------------------------------
	procedure get_decision(i_personal_code 	in 				varchar2,
						   io_loan_amount 	in	out 		number,
						   io_loan_period 	in	out 		number)
		is
		x_credit_modifier   segment.credit_modifier%type;
	begin
		validate_input(io_loan_amount, io_loan_period);
		get_credit_modifier_by_segment(get_customer_segment(i_personal_code), x_credit_modifier);
        calculate_amount(x_credit_modifier, io_loan_amount, io_loan_period);
		if not is_scoring_valid(x_credit_modifier, io_loan_amount, io_loan_period) then
			calculate_new_period(x_credit_modifier,io_loan_amount,io_loan_period);
		end if;
--for testing purposes
    dbms_output.put_line('Loan decission is POSITIVE in amount '||IO_LOAN_AMOUNT||' euros for period '||IO_LOAN_PERIOD||' months!');
	exception
		when invalid_amount_input then
			raise_application_error(-20003, 'Invalid amount input. Minimum possible amount is '||get_minimum_amount||' and maximum amount is '||get_maximum_amount||'!',true);
		when invalid_period_input then
			raise_application_error(-20004, 'Invalid period input. Minimum possible period is '||get_minimum_period||' months and maximum period is '||get_maximum_period||' months!',true);
		when customer_check_impossible then
			raise_application_error(-20005, 'Customer check is impossible! Please try again later!',true);
		when customer_has_debt then
			raise_application_error(-20006, 'Customer has debt! Decision NEGATIVE!',true);
		when cannot_calculate_new_period then
			raise_application_error(-20007, 'Amount is too big! Impossible to calculate new period! Decision NEGATIVE!',true);
		when others then
			raise_application_error(-20008, 'Decision making failed! Error: ' || SQLCODE || ':' || SQLERRM, true);
	end get_decision;
end api;
/