import json
import pymysql

def expense(request):

    # Extract JSON payload from request
    payload = request.get_json()

    # Extract required values from payload
    username = payload['sessionInfo']['session'].split("/")[-1]
    expense_category = payload["intentInfo"]["parameters"]["expensecategory"]["resolvedValue"]
    expense_amount = payload["intentInfo"]["parameters"]["unit-currency"]["resolvedValue"]["amount"]
    expense_earning = "expense"

    expense_category = expense_category if expense_category in ["food","transport","housing","shopping","others"] else "others"
    
    connection = pymysql.connect(
        host="34.145.40.220",
        user="root",
        password="toor",
        database="budgetpal"
    )

    # Update the user's transaction history
    with connection.cursor() as cursor:
        sql = f"INSERT INTO {username}_trans (expense_earning, amount, category) VALUES (%s, %s, %s)"
        cursor.execute(sql, (expense_earning, expense_amount, expense_category))
        connection.commit()

    # Update the user's budget
    if expense_earning == "expense":
        with connection.cursor() as cursor:
            sql = f"SELECT {expense_category}_balance_budget FROM {username}_budget WHERE id = 1"
            cursor.execute(sql)
            result = cursor.fetchone()
            balance = float(result[0]) - expense_amount
            if balance<0 :
                connection.close()
                return json.dumps({"fulfillment_response": {"messages": [{"text": {"text": [f"Your expense had exceeded budget under {expense_category} Category"],"allow_playback_interruption": False,}}]}})    
            sql = f"UPDATE {username}_budget SET {expense_category}_balance_budget = {expense_category}_balance_budget - %s WHERE id = 1"
            cursor.execute(sql, (expense_amount,))
            connection.commit()

    # Close database connection
    connection.close()

    # Return response
    return json.dumps(
        {
            "fulfillment_response": {
                "messages": [
                    {
                        "text": {
                            "text": [f"Your Entry of Rs.{expense_amount} under {expense_category} Category is succesfull. Balance budget is Rs.{balance}"],
                            "allow_playback_interruption": False,
                        }
                    }
                ]
            }
        }
    )

def earning(request):

    # Extract JSON payload from request
    payload = request.get_json()

    # Extract required values from payload
    username = payload['sessionInfo']['session'].split("/")[-1]
    expense_category = "income"
    expense_amount = payload["intentInfo"]["parameters"]["unit-currency"]["resolvedValue"]["amount"]
    expense_earning = "earning"

    # Connect to MySQL database
    connection = pymysql.connect(
        host="34.145.40.220",
        user="root",
        password="toor",
        database="budgetpal"
    )

    # Update the user's transaction history
    with connection.cursor() as cursor:
        sql = f"INSERT INTO {username}_trans (expense_earning, amount, category) VALUES (%s, %s, %s)"
        cursor.execute(sql, (expense_earning, expense_amount, expense_category))
        connection.commit()

    # Close database connection
    connection.close()

    # Return response
    return json.dumps(
        {
            "fulfillment_response": {
                "messages": [
                    {
                        "text": {
                            "text": [f"Your income of Rs.{expense_amount} is succesfully Updated."],
                            "allow_playback_interruption": False,
                        }
                    }
                ]
            }
        }
    )


def webhook_fcn(request):
    request_dict = request.get_json()
    tag = request_dict["fulfillmentInfo"]["tag"]
    if tag == "expense":
        return expense(request)
    if tag == "earning":
        return earning(request)
    raise RuntimeError(f"Unrecognized tag: {tag}")
