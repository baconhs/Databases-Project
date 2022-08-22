package com.project.main;
import org.springframework.boot.SpringApplication;

import static org.hamcrest.CoreMatchers.containsStringIgnoringCase;

import java.io.*;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;
import java.util.logging.*;

import javax.print.DocFlavor.BYTE_ARRAY;

import org.json.*;
import org.json.JSONObject;

import com.project.main.DBConnect;
import java.sql.*;
import java.time.LocalDate;

import org.json.simple.*;

import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@SpringBootApplication
public class MainApplication {
	 public static void main(String[] args) {
		 try {
			MainAPI.init();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		 SpringApplication.run(MainApplication.class, args);
	 }
}

@RestController
class MainAPI {

	static Connection conn;
	static Statement statement;

	public static void init() throws ClassNotFoundException, SQLException {
		conn = DBConnect.getConnection();
		statement = conn.createStatement();
	}
	
	@PostMapping("/api/control")
	public String CreateTables(@RequestParam(value="operation", defaultValue="create") String operation) {
		String queries = "";
		String response = "";
		
		switch (operation) {
			case "create":
				System.out.println("INFO: Creating Tables...");
				try {
					queries = new String(Files.readString(Paths.get("queries/create_tables.sql").toAbsolutePath()));
					response = "Creating Tables...";
				} catch (IOException e) {
					e.printStackTrace();
				}
				break;
			case "drop":
				System.out.println("INFO: Dropping Tables...");
				try {
					queries = new String(Files.readString(Paths.get("queries/drop_tables.sql").toAbsolutePath()));
					response = "Dropping Tables...";
				} catch (IOException e) {
					e.printStackTrace();
				}
				break;
			case "fill":
				System.out.println("INFO: Filling Tables...");
				try {
					queries = new String(Files.readString(Paths.get("queries/fill_tables.sql").toAbsolutePath()));
					response = "Filling Tables...";
				} catch (IOException e) {
					e.printStackTrace();
				}
				break;
		}

		String batch[] = queries.split("\\n\\n");

		try {
			for(String str : batch) {
				statement.addBatch(str);
			}
			statement.executeBatch();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return response;
	}
	
	 @GetMapping("/api/new-account")
	 public String NewAccount(@RequestParam Map<String, String> params ) {
		 System.out.println();

		 // ---------- Generate User Account ----------
		 Random random = new Random();
		 String account_id = Integer.toString(random.nextInt(2000000000));
		 String username = params.get("username");
		 String usersQuery = String.format("insert into Users values(\"%s\", \"%s\");",
				 account_id,
				 username);
		 System.out.println(usersQuery);
		 try {
			 statement.executeUpdate(usersQuery);
		 } catch (SQLException e) {
			 e.printStackTrace();
		 }

		 // ---------- Generate Client fields ----------
		 String name = params.get("name");
		 String account_expiration = params.get("account-expiration-date");
		 String credit_limit = params.get("credit-limit");
		 String credit_balance = params.get("credit-balance");
		 String commission = params.get("commission");
		 String is_company = "0";

		 switch(params.get("account-type")) {
		 	case("Company"):
		 		is_company = "1";
		 	case("Individual"):

		 		// ---------- Insert client into Clients table ----------
		 		String clientQuery = String.format("insert into Clients values(\"%s\", \"%s\", \"%s\", \"%s\", \"%s\", \"%s\", \"%s\");",
		 				account_id,
		 				name,
		 				account_expiration,
		 				credit_limit,
		 				"0",
		 				credit_balance,
		 				is_company);
		 		System.out.println(clientQuery);
				try {
					statement.executeUpdate(clientQuery);
				} catch (SQLException e) {
					e.printStackTrace();
				}

		 		String works_for = params.get("works-for");
		 		if(!works_for.isBlank()) {

		 			// ---------- Insert client into shared accounts table ---------- 
		 			ResultSet ids = null;
		 			String company_id = "";
		 			try {
		 				String fetch_company_query = "select account_id from Clients where name=\"" + works_for + "\";";
						System.out.println(fetch_company_query);
						ids = statement.executeQuery(fetch_company_query);
						if(ids.next()) {
							company_id = ids.getString(1);
						}
						System.out.println("Company id: " + company_id);
					} catch (SQLException e) {
						e.printStackTrace();
					}

					String sharedAccountsQuery = String.format("insert into Employees values(\"%s\", \"%s\");",
							 account_id,
							 company_id);
					try {
						System.out.println(sharedAccountsQuery);
						statement.executeUpdate(sharedAccountsQuery);
					} catch (SQLException e) {
						e.printStackTrace();
					}
		 		}
		 		break;
		 	case("Merchant"):
		 		// ---------- Insert client into merchants table ----------
		 		String merchantQuery = String.format("insert into Merchants values(\"%s\", \"%s\", \"%s\", \"%s\", \"%s\");",
		 				account_id,
		 				name,
		 				commission,
		 				"0",
		 				"0");
		 		System.out.println(merchantQuery);
				try {
					statement.executeUpdate(merchantQuery);
				} catch (SQLException e) {
					e.printStackTrace();
				}
		 		break;
		 }
		 return "Account created successfully!";
	 }
	 
	 @GetMapping("/api/close-account")
	 public String CloseAccount(@RequestParam Map<String, String> params ) throws SQLException {
		 // ---------- Check if client exists ----------
		 System.out.println();
		 ResultSet result;
		 String getIDQuery = "select account_id from Users where username=\"" + params.get("username") + "\";";
		 System.out.println(getIDQuery);
		 result = statement.executeQuery(getIDQuery);

		 int account_id;
		 if(result.next()) {
			 account_id = result.getInt(1);
			 System.out.println("User account id: " + account_id);
		 } else {
			 return "User not found!";
		 }
		 // ---------- Get Client/Merchant debt, if positive, don't delete -----------
		 String getDebtQuery = "select debt from Clients where account_id=\"" + account_id + "\";";
		 System.out.println(getDebtQuery);
		 result = statement.executeQuery(getDebtQuery);

		 int debt = 0;
		 if(result.next()) {
			 debt = result.getInt(1);
			 System.out.println("Client debt: " + debt);
		 } else {
			 String getMerchantDebtQuery = "select debt from Merchants where account_id=\"" + account_id + "\";";
			 System.out.println(getMerchantDebtQuery);
			 result = statement.executeQuery(getMerchantDebtQuery);
			 if(result.next()) {
				 debt = result.getInt(1);
				 System.out.println("Merchant debt: " + debt);
				 if(debt > 0) {
					 return "Cant't delete Merchant account, Merchant is in debt.";
				 } else {
					 String deleteMerchantQuery = "delete from Merchants where account_id=\"" + account_id + "\";";
					 System.out.println(deleteMerchantQuery);
					 statement.executeUpdate(deleteMerchantQuery);
				 }
			 }
		 }
		 if(debt > 0) {
			 return "Can't delete Client account, Client is in debt.";
		 } else {
			 String deleteClientQuery = "delete from Clients where account_id=\"" + account_id + "\";";
			 System.out.println(deleteClientQuery);
			 statement.executeUpdate(deleteClientQuery);

		 }

		 String deleteUserQuery = "delete from Users where account_id=\"" + account_id + "\"";
		 System.out.println(deleteUserQuery);
		 statement.executeUpdate(deleteUserQuery);

		 String deleteCorporateIfExistsQuery = "delete from Employees where account_id=\"" + account_id + "\"";
		 System.out.println(deleteCorporateIfExistsQuery);
		 try {
			 statement.executeUpdate(deleteCorporateIfExistsQuery);
		 } catch(Exception e) {
			 
		 }

		 return "Account deleted successfully!";
	 }
	 
	 @SuppressWarnings("resource")
	 @GetMapping("/api/buy")
	 public String Buy(@RequestParam Map<String, String> params ) throws SQLException {
		 String account_id = "";
		 String merchant_id = "";
		 String username = params.get("username");
		 String merchant= params.get("merchant");
		 String asEmployee = params.get("asEmployee");
		 String keep_account_id = "";
		 int debt = 0;
		 int credit_limit = 0;
		 int amount = Integer.parseInt(params.get("amount"));
		 
		 // ---------- Get client id ----------
		 String getIDQuery = "select account_id from Users where username=\"" + username + "\";";
		 System.out.println(getIDQuery);
		 ResultSet res = statement.executeQuery(getIDQuery);
		 if(res.next()) {
			 account_id = res.getString("account_id");
		 }
		 
		 // ---------- Charge Client. If Employee, charge company. ----------
		 if(!(asEmployee == null)) {
			 // ---------- Get Employer id -----------
			 String getEmployerIDQuery = "select employer_id from Employees where account_id=\"" + account_id + "\";";
			 System.out.println(getEmployerIDQuery);
			 res = statement.executeQuery(getEmployerIDQuery);
			 if(res.next()) {
				 keep_account_id = account_id;
				 System.out.println("nai re egine" + keep_account_id);
				 account_id = res.getString(1);
				 System.out.println("nai re egine" + keep_account_id);
			 } else {
				 return "User is not an employee!";
			 }
			 // ---------- Get Debt and Credit Limit ----------
			 String getClientDebtQuery = "select debt,credit_limit from Clients where account_id=\"" + account_id +  "\";";
			 System.out.println(getClientDebtQuery);
			 res = statement.executeQuery(getClientDebtQuery);
			 if(res.next()) {
				debt = res.getInt(1);
				credit_limit=res.getInt(2);
			 }

			 if(amount > credit_limit) {
				 return "Transaction can't be completed, Employee can only spend " + credit_limit;
			 }

			 // ---------- Charge Employer ----------
			 String chargeEmployerQuery = "update Clients set debt=\"" + (debt + amount) + "\", credit_limit=credit_limit-\"" + amount + "\" where account_id=\"" + account_id + "\";";
			 System.out.println(chargeEmployerQuery);
			 statement.executeUpdate(chargeEmployerQuery);
			 
		 } else {
			 // ---------- Get Client Debt ----------
			 String getClientDebtQuery = "select debt,credit_limit from Clients where account_id=\"" + account_id +  "\";";
			 System.out.println(getClientDebtQuery);
			 ResultSet res1  = statement.executeQuery(getClientDebtQuery);
			 if(res1.next()) {
				debt = res1.getInt(1);
				credit_limit = res1.getInt(2);
			 }
			 
			 if(amount > credit_limit) {
				 return "Transaction can't be completed, Client can only spend " + credit_limit;
			 }

			 // ---------- Charge Client and reduce limit ----------
			 String chargeClientQuery = "update Clients set debt=\"" + (debt + amount) + "\", credit_limit=credit_limit - \"" + amount + "\" where account_id=\"" + account_id + "\";";
			 System.out.println(chargeClientQuery);
			 statement.executeUpdate(chargeClientQuery);

		 }

		 // ---------- Add transaction ----------
		 String getMerchantIDQuery = "select account_id from Users where username=\"" + merchant + "\";";
		 System.out.println(getMerchantIDQuery);
		 res = statement.executeQuery(getMerchantIDQuery);
		 if(res.next()) {
			 merchant_id = res.getString(1);
		 }

		 // ---------- Add amount to merchant profits ----------
		 String updateMerchantProfitsQuery = "update Merchants set customer_profits=customer_profits + (" + amount + " - ((commission/100)*\"" + amount + "\")) where account_id=\""+ merchant_id +"\";";
		 System.out.println(updateMerchantProfitsQuery);
		 statement.executeUpdate(updateMerchantProfitsQuery);
		 
		 if(!(asEmployee == null)) {
			 String addTransactionQueryString = String.format("INSERT INTO Transactions (client_id,merchant_id,transaction_date,transaction_amount,transaction_type,as_employee) values (\"%s\", \"%s\", \"%s\", \"%s\", \"%s\", \"%s\");",
					 keep_account_id,
					 merchant_id,
					 LocalDate.now().toString(),
					 amount,
					 "purchase",
					 "1"
					 );
			 statement.executeUpdate(addTransactionQueryString);
		 }
		 else {
			 String addTransactionQueryString = String.format("INSERT INTO Transactions (client_id,merchant_id,transaction_date,transaction_amount,transaction_type,as_employee) values (\"%s\", \"%s\", \"%s\", \"%s\", \"%s\", \"%s\");",
					 account_id,
					 merchant_id,
					 LocalDate.now().toString(),
					 amount,
					 "purchase",
					 "0"
					 );
			 statement.executeUpdate(addTransactionQueryString);
		 }
		 

		 return "Purchase succeeded!";
	 }
	 
	 @SuppressWarnings("resource")
	 @GetMapping("/api/return")
	 public String Return(@RequestParam Map<String, String> params ) throws SQLException {
		 ResultSet result;
		 int amount = Integer.parseInt(params.get("amount"));
		 String account_id = "";
		 String merchant_id = "";
		 String username = params.get("username");
		 String merchant= params.get("merchant");
		 String type = params.get("account-type-3");
		 String getUserQuery = "";
		 String employer_id = "";
		 String keep_account_id = "";
	 
		 // ---------- Get client id ----------
		 String getIDQuery = "select account_id from Users where username=\"" + username + "\";";
		 System.out.println(getIDQuery);
		 result = statement.executeQuery(getIDQuery);
		 if(result.next()) {
			 account_id = result.getString(1);
		 }
		 if (type.equals("Employee")) {
			 keep_account_id = account_id;
			 String getEmployerIdQuery="select employer_id from Employees where account_id=\"" + account_id + "\";";
			 result = statement.executeQuery(getEmployerIdQuery);
			 if (result.next()) {
				 account_id = result.getString("employer_id");
				 System.out.println(account_id);
			}
		 }
		 
		 // ---------- Get merchant id ----------
		 String getMerchantIDQuery = "select account_id from Users where username=\"" + merchant + "\";";
		 System.out.println(getMerchantIDQuery);
		 ResultSet res = statement.executeQuery(getMerchantIDQuery);
		 if(res.next()) {
			 merchant_id = res.getString("account_id");
		 }
		 String searchTransactionQuery="";
		 String alreadyReturnedQuery="";
		 if(type.equals("Employee")) {
			  searchTransactionQuery="select * from Transactions where client_id=\"" + keep_account_id + "\" and merchant_id=\"" + merchant_id + "\"and as_employee=\"" +1+ "\" and transaction_amount=\"" + amount + "\" and transaction_type=\"purchase\";" ;
			  alreadyReturnedQuery="select * from Transactions where client_id=\"" + keep_account_id + "\" and merchant_id=\"" + merchant_id + "\"and as_employee=\"" +1+ "\" and transaction_amount=\"" + amount + "\" and transaction_type=\"return\";" ;
		 }
		 else {
			  searchTransactionQuery="select * from Transactions where client_id=\"" + account_id + "\" and merchant_id=\"" + merchant_id + "\"and as_employee=\"" +0+ "\" and transaction_amount=\"" + amount + "\" and transaction_type=\"purchase\";" ;
			  alreadyReturnedQuery="select * from Transactions where client_id=\"" + account_id + "\" and merchant_id=\"" + merchant_id + "\"and as_employee=\"" +0+ "\" and transaction_amount=\"" + amount + "\" and transaction_type=\"return\";" ;
		 }
		 
		 res = statement.executeQuery(searchTransactionQuery);
		 if (!res.next()) {
			 return "no purchase has been made";
		 }
		 res = statement.executeQuery(alreadyReturnedQuery);
		 if (res.next()) {
			 return "the return has already been made";
		 }
		
		 // ---------- Return the product and adjust values ----------
		 String updateClientQuery ="update Clients set credit_balance=credit_balance +\"" + amount + "\" where account_id=\"" + account_id + "\";";
		 statement.executeUpdate(updateClientQuery);
		 String updateMerchantQuery ="update Merchants set debt=debt + \"" + amount + "\" where account_id=\"" + merchant_id + "\";";
		 statement.executeUpdate(updateMerchantQuery);		 		 
		// ---------- Add transaction ----------
		 if (type.equals("Employee")) {
			 String addTransactionQueryString = String.format("INSERT INTO Transactions (client_id,merchant_id,transaction_date,transaction_amount,transaction_type,as_employee) values (\"%s\", \"%s\", \"%s\", \"%s\", \"%s\", \"%s\");",
					 keep_account_id,
					 merchant_id,
					 LocalDate.now().toString(),
					 amount,
					 "return",
					 "1"
					 );
			 statement.executeUpdate(addTransactionQueryString);
		 }
		 else {
			 String addTransactionQueryString = String.format("INSERT INTO Transactions (client_id,merchant_id,transaction_date,transaction_amount,transaction_type,as_employee) values (\"%s\", \"%s\", \"%s\", \"%s\", \"%s\", \"%s\");",
					 account_id,
					 merchant_id,
					 LocalDate.now().toString(),
					 amount,
					 "return",
					 "0"
					 );
			 statement.executeUpdate(addTransactionQueryString);
		 }
		 return "Product returned successfully";	 
	 }
	 
	 
	 @GetMapping("/api/pay")
	 public String Pay(@RequestParam Map<String, String> params ) throws SQLException {
		 ResultSet result;
		 int debt = 0;
		 int credit_balance = 0;
		 int amount = Integer.parseInt(params.get("amount"));
		 String account_id="";
		 String employer_id = "";
		 String username = params.get("username");
		 String type = params.get("account-type-2");
		 
		 // ---------- Get User id ----------
		 String getUserQuery = "";
		 String getIdQuery = "select account_id from Users where username=\"" + username + "\";";
		 result = statement.executeQuery(getIdQuery);
		 if (result.next()) {
			 account_id = result.getString("account_id");
		 }
		 System.out.println(account_id);
		 
		 // ---------- Get user's debt, credit_balance/costumer_profits ----------
		 if(type.equals("Individual")) {
			 getUserQuery = "select * from Clients where account_id=\"" + account_id + "\";";
			 System.out.println(getUserQuery);
			 result = statement.executeQuery(getUserQuery);
			 if (result.next()) {
				debt = result.getInt("debt");
				credit_balance = result.getInt("credit_balance");
			 }
		 }
		 else if(type.equals("Merchant")) {
			 getUserQuery= "select * from Merchants where account_id=\"" + account_id + "\";";
			 System.out.println(getUserQuery);
			 result = statement.executeQuery(getUserQuery);
			 if (result.next()) {
				debt = result.getInt("debt");
				credit_balance = result.getInt("customer_profits");
			 }
		 }
		 else {
			 String getEmployerIdQuery="select employer_id from Employees where account_id=\"" + account_id + "\";";
			 result = statement.executeQuery(getEmployerIdQuery);
			 if (result.next()) {
				 employer_id = result.getString("employer_id");
			 }
			 getUserQuery= "select * from Clients where account_id=\"" + employer_id + "\";";
			 System.out.println(getUserQuery);
			 result = statement.executeQuery(getUserQuery);
			 if (result.next()) {
				debt = result.getInt("debt");
				credit_balance = result.getInt("credit_balance");
			 }
		 }
		 
		 // ---------- Finalize payment if it can be done ----------
		 if( amount == 0 ) {
			 return "you gotta give us something";
		 }
		 else if (credit_balance < amount) {
			 return "Not enough money to pay the amount";
		 }
		 else if (amount > debt) {
			 return "you're giving us too much money!!";
		 }
		 else {
			 if (type.equals("Individual")) {
				 String updateQuery ="update Clients set debt= debt - \"" + amount +"\",credit_balance= credit_balance -\"" + amount + "\" where account_id=\"" + account_id + "\";";
				 statement.executeUpdate(updateQuery);
			 }
			 else if (type.equals("Employee")){
				 String updateQuery ="update Clients set debt= debt - \"" + amount +"\",credit_balance= credit_balance -\"" + amount + "\" where account_id=\"" + employer_id + "\";";
				 statement.executeUpdate(updateQuery);
			 }
			 else {
				 String updateQuery ="update Merchants set debt=debt- \"" + amount +"\",customer_profits=customer_profits -\"" + amount + "\" where account_id=\"" + account_id + "\";";
				 statement.executeUpdate(updateQuery);
			 }
		 }
		 return "Debt amount paid successfully";
	 }
	
	 @GetMapping("/api/question-1")
	 public String Question1(@RequestParam Map<String, String> params ) throws SQLException {
		 String date1 = params.get("date1");
		 String date2 = params.get("date2");
		 String getTransactionsQuery = "select * from Transactions where transaction_date > \"" + date1 + "\" and transaction_date < \"" + date2 + "\";";
		 System.out.println(getTransactionsQuery);
		 ResultSet result = statement.executeQuery(getTransactionsQuery);
		 if (!result.next()) {
			 return "There are no transactions made between these dates";
		 }
		 else {
			 StringBuilder response = new StringBuilder();
			 response.append("<table class=\"table\">\n"
				 		+ "  <thead>\n"
				 		+ "    <tr>\n"
				 		+ "      <th>id</th>\n"
				 		+ "      <th>client_id</th>\n"
				 		+ "      <th>merchant_id</th>\n"
				 		+ "      <th>transaction_date</th>\n"
				 		+ "      <th>transaction_amount</th>\n"
				 		+ "      <th>transaction_type</th>\n"
				 		+ "    </tr>\n"
				 		+ "  </thead>"
				 		+ "  <tbody>");
				do {
					 response.append("<tr>");
					 for(int i = 1; i <= 6; i ++) {
						 response.append("<td>" + result.getString(i) + "</td>");
					 }
					 response.append("</tr>");
				 }while(result.next()); 
				 response.append("</tbody></table>");
				 return response.toString(); 
		 }
	 }
	 
	 @GetMapping("/api/question-3")
	 public String Question3(@RequestParam Map<String, String> params ) throws SQLException {
		 Statement statement2;
		 ResultSet result;
		 String account_id="";
		 String username = params.get("username");
		 
		 // ---------- Get User id ----------
		 String getIdQuery = "select account_id from Users where username=\"" + username + "\";";
		 result = statement.executeQuery(getIdQuery);
		 if (result.next()) {
			 account_id = result.getString("account_id");
		 }
		 else {
			 return "This company does not exist in the database";
		 }
		
			 System.out.println(account_id);
			 String getTransactionsQuery = "select * from Transactions inner join Employees ON Employees.account_id = Transactions.client_id where Employees.employer_id=\"" + account_id + "\"and Transactions.as_employee=\"" +1+ "\";";
			 result = statement.executeQuery(getTransactionsQuery);
			 StringBuilder response = new StringBuilder();
			 if (!result.next()) {
				 return "There are no transactions made from this employee";
			 }
			 else {
				 response.append("<table class=\"table\">\n"
					 		+ "  <thead>\n"
					 		+ "    <tr>\n"
					 		+ "      <th>id</th>\n"
					 		+ "      <th>client_id</th>\n"
					 		+ "      <th>merchant_id</th>\n"
					 		+ "      <th>transaction_date</th>\n"
					 		+ "      <th>transaction_amount</th>\n"
					 		+ "      <th>transaction_type</th>\n"
					 		+ "    </tr>\n"
					 		+ "  </thead>"
					 		+ "  <tbody>");
					do {
						 response.append("<tr>");
						 for(int i = 1; i <= 6; i ++) {
							 response.append("<td>" + result.getString(i) + "</td>");
						 }
						 response.append("</tr>");
					 }while(result.next()); 
					 response.append("</tbody></table>");
					 return response.toString(); 
			 }
		 }
	 
	 @GetMapping("/api/question-2")
	 public String Question2(@RequestParam Map<String, String> params ) throws SQLException {
		 ResultSet result;
		 String company = params.get("username");
		 String employee = params.get("employee");
		 String employee_id ="";
		 String employer_id = "";
		 String company_id = "";
		 
		 // ---------- Get Employee id ----------
		 String getIdQuery = "select * from Users where username=\"" + employee + "\";";
		 result = statement.executeQuery(getIdQuery);
		 if (result.next()) {
			 employee_id = result.getString("account_id");
			 System.out.println(employee_id);
		 }
		 else {
			 return "There is no such employee in the database";
		 }
		
		 // ---------- Get Company id ----------
		 getIdQuery = "select * from Users where username=\"" +company+ "\";";
		 System.out.println(getIdQuery);
		 result = statement.executeQuery(getIdQuery);
		 if (result.next()) {
			 company_id = result.getString("account_id");
			 System.out.println(company_id);
		 }
		 else {
			 return "This company is not listed in the database";
		 }
		 String verifyCompanyQuery ="select is_company from Clients where account_id = \"" + company_id +"\" and is_company=\"" +1+ "\";";
		 result = statement.executeQuery(verifyCompanyQuery);
		 if (!result.next()) {
			 return "This client is not a company";
		 }
		
		 // ---------- Match Employee to Company ----------
		 String getEmployerQuery = "select * from Employees where account_id= \"" + employee_id + "\";";
		 result = statement.executeQuery(getEmployerQuery);
		 if (result.next()) {
			 employer_id = result.getString("employer_id");
			 if(!employer_id.equals(company_id)) return "This employee does not work for that company";
		 }
		 else {
			 return "the employee doesn't work for this company";
		 }
		 
		 // ---------- Build the Transactions Table ----------
		 String getTransactionsQuery = "select * from Transactions where client_id=\"" +employee_id+ "\" and as_employee=\"" +1+ "\";";
		 result = statement.executeQuery(getTransactionsQuery);
		 StringBuilder response = new StringBuilder();
		 if (!result.next()) {
			 return "There are no transactions made from this employee";
		 }
		 else {
			 response.append("<table class=\"table\">\n"
				 		+ "  <thead>\n"
				 		+ "    <tr>\n"
				 		+ "      <th>id</th>\n"
				 		+ "      <th>client_id</th>\n"
				 		+ "      <th>merchant_id</th>\n"
				 		+ "      <th>transaction_date</th>\n"
				 		+ "      <th>transaction_amount</th>\n"
				 		+ "      <th>transaction_type</th>\n"
				 		+ "    </tr>\n"
				 		+ "  </thead>"
				 		+ "  <tbody>");
				do {
					 response.append("<tr>");
					 for(int i = 1; i <= 6; i ++) {
						 response.append("<td>" + result.getString(i) + "</td>");
					 }
					 response.append("</tr>");
				 }while(result.next()); 
				 response.append("</tbody></table>");
				 return response.toString(); 
		 }
		 
	 }
	 
	 @GetMapping("/api/good-users")
	 public String GoodUsers(@RequestParam Map<String, String> params ) throws SQLException {
		 StringBuilder response = new StringBuilder();
		 String getGoodUsersQuery = "select * from Clients where debt=0";
		 System.out.println(getGoodUsersQuery);
		 System.out.println(getGoodUsersQuery);
		 ResultSet result = statement.executeQuery(getGoodUsersQuery);
		 while(result.next()) {
			 response.append("<div class=\"box my-1\">" + result.getString("name") + "</div>");
		 }
		 return response.toString();
	 }

	 @GetMapping("/api/bad-users")
	 public String BadUsers(@RequestParam Map<String, String> params ) throws SQLException {
		 StringBuilder response = new StringBuilder();
		 String getBadUsersQuery = "select * from Clients where debt>0 order by debt";
		 System.out.println(getBadUsersQuery);
		 ResultSet result = statement.executeQuery(getBadUsersQuery);
		 while(result.next()) {
			 response.append("<div class=\"box my-1\">" + result.getString("name") + "</div>");
		 }
		 return response.toString();
	 }
	 
	 @GetMapping("/api/merchant-of-the-month")
	 public String MerchantOfTheMonth(@RequestParam Map<String, String> params ) throws SQLException {
		 int merchant_id = 0;
		 String response = "Error?";
		 String getMoMQuery = "SELECT merchant_id, COUNT(merchant_id) AS merchant_id_occur\n"
		 		+ "FROM Transactions\n"
		 		+ "GROUP BY merchant_id LIMIT 1;";
		 System.out.println(getMoMQuery);
		 ResultSet result = statement.executeQuery(getMoMQuery);
		 if(result.next()) {
			 merchant_id = result.getInt(1);
		 } else {
			 return "Couldn't find merchant of the month!";
		 }

		 String getMerchantUsernameQuery = "select username from Users where account_id=\"" + merchant_id + "\";";
		 System.out.println(getMerchantUsernameQuery);
		 result = statement.executeQuery(getMerchantUsernameQuery);
		 if(result.next()) {
			 response =  "<div class=\"box my-1\">" + result.getString(1) + "</div>";
		 }
		 
		 String reduceMerchantDebtQuery = "update Merchants set debt=debt-debt*(5/100) where account_id=\"" + merchant_id + "\";";
		 System.out.println(getMerchantUsernameQuery);
		 statement.executeUpdate(reduceMerchantDebtQuery);
		 
		 return response;
	 }
}