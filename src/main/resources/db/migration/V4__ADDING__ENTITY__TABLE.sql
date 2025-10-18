CREATE TABLE entity_address (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    address_id UUID REFERENCES address(address_id),
    address_type VARCHAR(50),
    entity_type VARCHAR(50),
    entity_id UUID
);

-- Drop employee_address table
DROP TABLE IF EXISTS employee_address CASCADE;

-- Drop client_address table
DROP TABLE IF EXISTS client_address CASCADE;
