import pymysql.cursors

# Database connection configuration
db_config = {
    'user': 'root',
    'password': 'toor',
    'host': '34.145.40.220',
    'db': 'budgetpal',
    'charset': 'utf8mb4',
    'cursorclass': pymysql.cursors.DictCursor
}


def signup(user, pwd):
    # Parse the request body for the username and password
    request_json = request.get_json()
    username = request_json.get('username')
    password = request_json.get('password')

    # Connect to the database
    connection = pymysql.connect(**db_config)

    try:
        # Check if the username already exists in the database
        with connection.cursor() as cursor:
            sql = "SELECT COUNT(*) FROM user_login WHERE username = %s"
            cursor.execute(sql, username)
            result = cursor.fetchone()
            if result['COUNT(*)'] > 0:
                return "Username already exists"

        # Add the new user to the database
        with connection.cursor() as cursor:
            sql = "INSERT INTO user_login (username, password_hash) VALUES (%s, SHA2(%s, 256))"
            cursor.execute(sql, (username, password))
            connection.commit()

        # Create the new tables for the user
        with connection.cursor() as cursor:
            # Create the username_trans table
            sql = f"CREATE TABLE {username}_trans (id INT NOT NULL AUTO_INCREMENT, expense_earning ENUM('expense', 'earning') NOT NULL, amount DECIMAL(10, 2) NOT NULL, category VARCHAR(50) NOT NULL, PRIMARY KEY (id))"
            cursor.execute(sql)
            connection.commit()

            # Create the username_budget table
            categories = ["food", "transport", "housing",
                          "shopping", "others"]
            budget_columns = ", ".join(
                [f"{category}_set_budget DECIMAL(10, 2) DEFAULT 0.00, {category}_balance_budget DECIMAL(10, 2) DEFAULT 0.00" for category in categories])
            sql = f"CREATE TABLE {username}_budget (id INT NOT NULL AUTO_INCREMENT, {budget_columns}, PRIMARY KEY (id))"
            cursor.execute(sql)
            connection.commit()

            # Insert a single row into the username_budget table with default budget values
            budget_values = [0.00] * (2 * len(categories))
            budget_columns = ", ".join(
                [f"{category}_set_budget, {category}_balance_budget" for category in categories])
            budget_placeholders = ", ".join(["%s"] * (2 * len(categories)))
            sql = f"INSERT INTO {username}_budget ({budget_columns}) VALUES ({budget_placeholders})"
            cursor.execute(sql, budget_values)
            connection.commit()

    finally:
        # Close the database connection
        connection.close()

    # Return a success message
    return f"User {username} signed up successfully"

