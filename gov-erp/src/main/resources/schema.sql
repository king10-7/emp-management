-- Create messages table if not exists
CREATE TABLE IF NOT EXISTS messages (
    id BIGSERIAL PRIMARY KEY,
    employee_id BIGINT NOT NULL,
    content VARCHAR(1000) NOT NULL,
    month INTEGER NOT NULL,
    year INTEGER NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (employee_id) REFERENCES employees(id)
);

-- Create a function to generate message
CREATE OR REPLACE FUNCTION generate_payslip_message()
RETURNS TRIGGER AS $$
DECLARE
    v_employee_first_name VARCHAR(255);
    v_employee_id_str VARCHAR(255);
    v_institution VARCHAR(255);
    v_month_str VARCHAR(2);
    v_message TEXT;
BEGIN
    -- Get employee details
    SELECT 
        e.first_name,
        emp.employee_id_string,
        emp.institution
    INTO 
        v_employee_first_name,
        v_employee_id_str,
        v_institution
    FROM 
        employees e
    JOIN 
        employments emp ON e.id = emp.employee_id
    WHERE 
        e.id = NEW.employee_id;

    -- Format month as two digits
    v_month_str := LPAD(NEW.month::TEXT, 2, '0');

    -- Create message
    v_message := 'Dear ' || v_employee_first_name || 
                 ' Your salary of ' || v_month_str || '/' || NEW.year || 
                 ' from ' || v_institution || 
                 ' ' || NEW.net_salary || ' has been credited to your ' || 
                 v_employee_id_str || ' account Successfully.';

    -- Insert into messages
    INSERT INTO messages (employee_id, content, month, year, created_at)
    VALUES (NEW.employee_id, v_message, NEW.month, NEW.year, CURRENT_TIMESTAMP);

    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

-- Create trigger to fire when payslip status changes to 'Paid'
CREATE OR REPLACE TRIGGER trigger_payslip_approved
AFTER UPDATE OF status ON payslips
FOR EACH ROW
WHEN (OLD.status = 'Pending' AND NEW.status = 'Paid')
EXECUTE FUNCTION generate_payslip_message();
