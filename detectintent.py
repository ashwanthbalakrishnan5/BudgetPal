import os
from google.auth import credentials
from google.oauth2 import service_account
from google.cloud import dialogflowcx_v3beta1 as dialogflow


# Replace with your project and agent details
project_id = "solvingforindia"
location_id = "global"
agent_id = "4cc57f30-97a5-4d11-8c5d-ad86453ee615"
session_id = "username"
language_code = "en-us"


# Replace with your service account key path
key_path = "key.json"


# Set up authentication using your service account key
client_options = {"api_endpoint": f"{location_id}-dialogflow.googleapis.com"}
credentials = service_account.Credentials.from_service_account_file(key_path)
client = dialogflow.SessionsClient(
    credentials=credentials, client_options=client_options)

# Set up session path
session_path = client.session_path(
    project_id, location_id, agent_id, session_id)

# Set up query text
query_text = ""

# Set up query parameters
query_params = dialogflow.QueryParameters(
    time_zone="America/Los_Angeles"
)

# Set up query input
query_input = dialogflow.QueryInput(
    text=dialogflow.TextInput(text=query_text), language_code="en-us")


# Call detect_intent method to send a query to your agent and receive a response
response = client.detect_intent(
    request={"session": session_path,
             "query_input": query_input, "query_params": query_params}
)

# Print the response to the console
print(
    f"Response text: {response.query_result.response_messages[0].text.text[0]}")
