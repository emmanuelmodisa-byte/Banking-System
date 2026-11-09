# CSE202 Banking System - Part B

Layers (each layer only talks to the one below it):

    View (JavaFX)  ->  Controller  ->  BankingService  ->  Model  /  DAO (H2 database)

## Packages
    bank.model        Customer (abstract) -> IndividualCustomer / OrganisationCustomer
                      Account (abstract)  -> SavingsAccount / InvestmentAccount / ChequeAccount
                      InterestBearing (interface), Transaction, TransactionType, User, Role (TELLER, MANAGER)
    bank.exception    BankException
    bank.service      BankingService: the banking rules; saves and loads through the DAOs
    bank.dao          Database (connection + creates the 4 tables), UserDAO, CustomerDAO, AccountDAO, TransactionDAO
    bank.controller   LoginController, CustomerController, AccountController: check input, call the service, catch BankException
    bank.view         MainApp, LoginView, CustomerView, AccountView, TransactionView, ViewUtil (JavaFX, no business logic)
    bank           Launcher (start the GUI), BankDemo (console demo), SampleData (adds the sample records)
    src/test          BankFlowTest (integration tests)

## How to run in IntelliJ
1. File > Open > choose the `pom.xml` > Open as Project. Wait for Maven to finish downloading (bottom right).
2. File > Project Structure > Project: set the SDK to JDK 17 or 21.
3. **GUI:** right-click `src/main/java/bank/Launcher.java` > Run 'Launcher.main()'.
   Starter logins: `teller / teller123` and `manager / manager123`.
   (Run `Launcher`, not `MainApp`: running a class that extends Application directly gives "JavaFX runtime components are missing".)
4. **Console demo (Step 4):** run `BankDemo`. It uses a temporary database, so you can run it as often as you like.
5. **Sample data (Step 7):** close the GUI, then run `SampleData` ONCE. It adds 5 customers, 6 accounts and the transactions.
6. **Tests (Step 8):** right-click `src/test/java/bank/BankFlowTest.java` > Run, or in the Maven panel run `test`.

The GUI saves to a file called `bankdb.mv.db` in the project folder. Delete that file to start with an empty database.
Only one program can use that file at a time, so close the GUI before running SampleData.

## Rules built into the model
- Savings: 0.05% monthly interest, no withdrawals.
- Investment: 5% monthly interest, opens with at least BWP500, deposits and withdrawals allowed.
- Cheque: no interest, needs employer details, individuals only.
- Only a manager can pay monthly interest. Only staff (Teller, Manager) log in.

## Push to GitHub
    git init
    git add .
    git commit -m "Part B: MVC with DAO layer"
    git branch -M main
    git remote add origin https://github.com/<your-username>/bank-system.git
    git push -u origin main
