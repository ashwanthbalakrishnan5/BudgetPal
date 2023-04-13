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


def login(request):
    # Parse the request body for the username and password
    request_json = request.get_json()
    username = request_json.get('username')
    password = request_json.get('password')

    # Connect to the database
    connection = pymysql.connect(**db_config)

    try:
        # Check if the username and password match a row in the database
        with connection.cursor() as cursor:
            sql = "SELECT COUNT(*) FROM user_login WHERE username = %s AND password_hash = SHA2(%s, 256)"
            cursor.execute(sql, (username, password))
            result = cursor.fetchone()
            if result['COUNT(*)'] == 0:
                return "Invalid username or password"

    finally:
        # Close the database connection
        connection.close()

    # Return a success message
    return f"User {username} logged in successfully"

