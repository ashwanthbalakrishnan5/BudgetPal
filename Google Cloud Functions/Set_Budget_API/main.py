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

def set_category_budget(request):
    # Parse the request body for the username, category name, and the new budget value
    request_json = request.get_json()
    username = request_json.get('username')
    category_name = request_json.get('category_name')
    budget_value = request_json.get('budget_value')

    # Connect to the database
    connection = pymysql.connect(**db_config)

    try:
        # Update the budget value in the username_budget table for the given category
        with connection.cursor() as cursor:
            sql = f"UPDATE {username}_budget SET {category_name}_set_budget = %s, {category_name}_balance_budget = %s"
            cursor.execute(sql, (budget_value, budget_value))
            connection.commit()

    finally:
        # Close the database connection
        connection.close()

    # Return a success message
    return f"Budget value updated successfully for {category_name} category for user {username}"
