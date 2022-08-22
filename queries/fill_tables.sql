
INSERT INTO Users(account_id,username)
VALUES("1","Kostantinos");

INSERT INTO Users(account_id,username)
VALUES("2","Dimitris");

INSERT INTO Users(account_id,username)
VALUES("3","England");

INSERT INTO Users(account_id,username)
VALUES("4","Xirosima");

INSERT INTO Users(account_id,username)
VALUES("5","Nagasaki");

INSERT INTO Users(account_id,username)
VALUES("6","Filotas");

INSERT INTO Users(account_id,username)
VALUES("7","Ioannis");

INSERT INTO Users(account_id,username)
VALUES("8","Epaminondas");

INSERT INTO Users(account_id,username)
VALUES("9","Pelopidas");

INSERT INTO Users(account_id,username)
VALUES("10","Mixail");

INSERT INTO Users(account_id,username)
VALUES("11","Takhs");

INSERT INTO Users(account_id,username)
VALUES("12","Mhtsos");

INSERT INTO Users(account_id,username)
VALUES("13","Kapitalas");

INSERT INTO Employees(account_id,employer_id)
VALUES("1","3")

INSERT INTO Employees(account_id,employer_id)
VALUES("2","4")

INSERT INTO Employees(account_id,employer_id)
VALUES("11","5")

INSERT INTO Employees(account_id,employer_id)
VALUES("12","3")

INSERT INTO Employees(account_id,employer_id)
VALUES("13","4")

INSERT INTO Clients (name,account_id,account_expiration,credit_limit,debt,credit_balance,is_company)
VALUES("Kostantinos","1", "2022-02-22", "150", "8000", "14000","0");

INSERT INTO Clients (name,account_id,account_expiration,credit_limit,debt,credit_balance,is_company)
VALUES("Dimitris","2", "2022-02-26", "500", "1500", "3000","0");

INSERT INTO Clients (name,account_id,account_expiration,credit_limit,debt,credit_balance,is_company)
VALUES("Takhs","11","2022-02-22", "150", "0", "14000","0");

INSERT INTO Clients (name,account_id,account_expiration,credit_limit,debt,credit_balance,is_company)
VALUES("Mhtsos","12", "2022-02-26", "500", "1500", "3000","0");

INSERT INTO Clients (name,account_id,account_expiration,credit_limit,debt,credit_balance,is_company)
VALUES("Kapitalas","13", "2022-08-02", "20000", "0", "83000","0");

INSERT INTO Clients (name,account_id,account_expiration,credit_limit,debt,credit_balance,is_company)
VALUES("England","3", "2022-08-02", "20000", "45000", "83000","1");

INSERT INTO Clients (name,account_id,account_expiration,credit_limit,debt,credit_balance,is_company)
VALUES("Xirosima","4", "2022-08-06", "40000 ", "126000", "226000","1");

INSERT INTO Clients (name,account_id,account_expiration,credit_limit,debt,credit_balance,is_company)
VALUES("Nagasaki","5", "2022-08-09", "9000", "0", "226000","1");

INSERT INTO Merchants (name,account_id,commission,customer_profits,debt)
VALUES("Filotas","6", "5", "1500", "1000");

INSERT INTO Merchants (name,account_id,commission,customer_profits,debt)
VALUES("Ioannis","7","5", "2000", "1500");

INSERT INTO Merchants (name,account_id,commission,customer_profits,debt)
VALUES("Epaminondas","8", "5", "3000", "1800");

INSERT INTO Merchants (name,account_id,commission,customer_profits,debt)
VALUES("Pelopidas","9", "5", "4500", "3000");

INSERT INTO Merchants (name,account_id,commission,customer_profits,debt)
VALUES("Mixail","10", "5", "5500", "3400");

INSERT INTO Transactions (client_id,merchant_id,transaction_date,transaction_amount,transaction_type,as_employee)
VALUES("1","9", "2022-11-10", "5000", "purchase","1");

INSERT INTO Transactions (client_id,merchant_id,transaction_date,transaction_amount,transaction_type,as_employee)
VALUES("2","8", "2022-11-12", "6000", "purchase","1");

INSERT INTO Transactions (client_id,merchant_id,transaction_date,transaction_amount,transaction_type,as_employee)
VALUES("11","7", "2022-11-13", "7000", "purchase","0");

INSERT INTO Transactions (client_id,merchant_id,transaction_date,transaction_amount,transaction_type,as_employee)
VALUES("12","10", "2022-11-14", "8000", "purchase","0");

INSERT INTO Transactions (client_id,merchant_id,transaction_date,transaction_amount,transaction_type,as_employee)
VALUES("13","7", "2022-11-20", "9000", "purchase","0");
