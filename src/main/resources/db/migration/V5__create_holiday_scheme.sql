----------------------------------------------------------
--  Holiday_Calendar
----------------------------------------------------------

CREATE TABLE holiday_calendar (
    holiday_id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    holiday_name VARCHAR(255) NOT NULL,
    holiday_date DATE NOT NULL,
    holiday_type VARCHAR(50) NOT NULL CHECK (holiday_type IN ('PUBLIC', 'REGIONAL', 'COMPANY_SPECIFIC', 'RELIGIOUS')),
    location_region VARCHAR(255),
    description TEXT,
    recurrence_rule VARCHAR(20) DEFAULT 'ONE_TIME' CHECK (recurrence_rule IN ('ANNUAL', 'ONE_TIME')),
    country_code VARCHAR(10),
    active_status BOOLEAN DEFAULT TRUE,
    created_by UUID,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (created_by) REFERENCES admin(admin_id)
);

----------------------------------------------------------
--  Holiday_Scheme
----------------------------------------------------------

CREATE TABLE holiday_scheme (
    scheme_id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    scheme_name VARCHAR(255) NOT NULL,
    description TEXT,
    country_code VARCHAR(10),
    state VARCHAR(100),
    city VARCHAR(100),
    active_status BOOLEAN DEFAULT TRUE,
    created_by UUID,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (created_by) REFERENCES admin(admin_id)
);


----------------------------------------------------------
--  Holiday_Scheme_Mapping
----------------------------------------------------------

CREATE TABLE holiday_scheme_mapping (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    scheme_id UUID NOT NULL,
    holiday_id UUID NOT NULL,
    FOREIGN KEY (scheme_id) REFERENCES holiday_scheme(scheme_id) ON DELETE CASCADE,
    FOREIGN KEY (holiday_id) REFERENCES holiday_calendar(holiday_id) ON DELETE CASCADE,
    UNIQUE (scheme_id, holiday_id)
);

----------------------------------------------------------
--  Holiday_Assignment
----------------------------------------------------------

CREATE TABLE holiday_assignment (
    assignment_id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    scheme_id UUID NOT NULL,
    employee_id UUID,
    assigned_by UUID,
    inheritance_flag BOOLEAN DEFAULT FALSE,  -- true if inherited via hierarchy
    assigned_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (scheme_id) REFERENCES holiday_scheme(scheme_id) ON DELETE CASCADE,
    FOREIGN KEY (employee_id) REFERENCES employee(employee_id) ON DELETE CASCADE,
    FOREIGN KEY (assigned_by) REFERENCES admin(admin_id)
);