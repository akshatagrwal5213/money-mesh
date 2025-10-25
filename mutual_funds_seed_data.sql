-- Seed data for Mutual Funds
-- This will create sample mutual funds for testing Module 6 Investment features

-- Clear existing data (if any)
DELETE FROM mutual_funds;

-- Insert 15 sample mutual funds with variety of categories and risk levels
INSERT INTO mutual_funds (
    fund_code, fund_name, amc, category, risk_level, 
    current_nav, previous_nav, aum, expense_ratio,
    returns1year, returns3year, returns5year,
    min_investment, min_sip_amount,
    sip_available, lumpsum_available,
    exit_load_days, exit_load_percentage,
    is_active, created_at, updated_at
) VALUES

-- EQUITY FUNDS (High Risk)
('HDFC001', 'HDFC Top 100 Fund', 'HDFC Mutual Fund', 'EQUITY', 'VERY_HIGH',
 542.34, 538.12, 25000000000.00, 1.85,
 15.5, 18.2, 16.8,
 5000.00, 500.00,
 1, 1, 365, 1.0,
 1, NOW(), NOW()),

('ICICI001', 'ICICI Bluechip Fund', 'ICICI Prudential', 'EQUITY', 'HIGH',
 89.45, 88.21, 18000000000.00, 1.75,
 14.2, 16.5, 15.3,
 5000.00, 500.00,
 1, 1, 365, 1.0,
 1, NOW(), NOW()),

('SBI001', 'SBI Large Cap Fund', 'SBI Mutual Fund', 'EQUITY', 'HIGH',
 152.67, 150.34, 12000000000.00, 1.95,
 13.8, 17.1, 14.9,
 5000.00, 500.00,
 1, 1, 365, 1.0,
 1, NOW(), NOW()),

-- DEBT FUNDS (Low Risk)
('HDFC002', 'HDFC Corporate Bond Fund', 'HDFC Mutual Fund', 'DEBT', 'LOW',
 23.45, 23.42, 8000000000.00, 0.85,
 6.5, 7.2, 7.5,
 5000.00, 1000.00,
 1, 1, 0, 0.0,
 1, NOW(), NOW()),

('ICICI002', 'ICICI Liquid Fund', 'ICICI Prudential', 'LIQUID', 'VERY_LOW',
 301.23, 301.15, 15000000000.00, 0.25,
 4.2, 4.5, 4.8,
 1000.00, 500.00,
 1, 1, 0, 0.0,
 1, NOW(), NOW()),

-- HYBRID FUNDS (Moderate Risk)
('HDFC003', 'HDFC Balanced Advantage Fund', 'HDFC Mutual Fund', 'HYBRID', 'MODERATE',
 67.89, 67.12, 10000000000.00, 1.45,
 10.5, 12.3, 11.8,
 5000.00, 500.00,
 1, 1, 365, 1.0,
 1, NOW(), NOW()),

('SBI002', 'SBI Equity Hybrid Fund', 'SBI Mutual Fund', 'HYBRID', 'MODERATE',
 142.56, 141.23, 9000000000.00, 1.55,
 11.2, 13.1, 12.5,
 5000.00, 500.00,
 1, 1, 365, 1.0,
 1, NOW(), NOW()),

-- ELSS (Tax Saving Funds - Moderate to High Risk)
('AXIS001', 'Axis Long Term Equity Fund', 'Axis Mutual Fund', 'ELSS', 'HIGH',
 95.34, 94.12, 11000000000.00, 1.75,
 16.5, 19.2, 17.5,
 500.00, 500.00,
 1, 1, 1095, 0.0,
 1, NOW(), NOW()),

('MIRAE001', 'Mirae Asset Tax Saver Fund', 'Mirae Asset', 'ELSS', 'HIGH',
 34.67, 34.23, 7000000000.00, 1.65,
 17.2, 20.1, 18.3,
 500.00, 500.00,
 1, 1, 1095, 0.0,
 1, NOW(), NOW()),

-- INDEX FUNDS (Low to Moderate Risk)
('UTI001', 'UTI Nifty Index Fund', 'UTI Mutual Fund', 'INDEX', 'MODERATE',
 178.45, 177.23, 6000000000.00, 0.35,
 12.5, 14.8, 13.9,
 5000.00, 500.00,
 1, 1, 0, 0.0,
 1, NOW(), NOW()),

('HDFC004', 'HDFC Sensex Index Fund', 'HDFC Mutual Fund', 'INDEX', 'MODERATE',
 245.67, 243.89, 5500000000.00, 0.40,
 11.8, 14.2, 13.5,
 5000.00, 500.00,
 1, 1, 0, 0.0,
 1, NOW(), NOW()),

-- GOLD FUND (Moderate Risk)
('SBI003', 'SBI Gold Fund', 'SBI Mutual Fund', 'GOLD', 'MODERATE',
 18.34, 18.12, 3000000000.00, 0.95,
 8.5, 9.2, 10.1,
 1000.00, 500.00,
 1, 1, 0, 0.0,
 1, NOW(), NOW()),

-- INTERNATIONAL FUNDS (High Risk)
('FRANKLIN001', 'Franklin US Opportunities Fund', 'Franklin Templeton', 'INTERNATIONAL', 'VERY_HIGH',
 52.67, 51.89, 2500000000.00, 2.15,
 18.5, 22.3, 20.1,
 5000.00, 1000.00,
 1, 1, 365, 1.0,
 1, NOW(), NOW()),

('MOTILAL001', 'Motilal Oswal Nasdaq 100 FoF', 'Motilal Oswal', 'INTERNATIONAL', 'VERY_HIGH',
 34.89, 34.23, 1800000000.00, 2.25,
 20.5, 25.8, 23.5,
 5000.00, 1000.00,
 1, 1, 365, 1.0,
 1, NOW(), NOW()),

-- DEBT FUND (Additional)
('SBI004', 'SBI Short Term Debt Fund', 'SBI Mutual Fund', 'DEBT', 'LOW',
 28.67, 28.64, 4000000000.00, 0.75,
 5.8, 6.5, 6.8,
 5000.00, 1000.00,
 1, 1, 0, 0.0,
 1, NOW(), NOW());

SELECT 'Mutual Funds seed data inserted successfully!' as Status, COUNT(*) as TotalFunds FROM mutual_funds;
