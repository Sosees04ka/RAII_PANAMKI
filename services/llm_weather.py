import requests
import json
import base64
import uuid

client_id = "6637e006-e8cc-44a2-88c8-43da68732cbd"
secret = "1e302e7a-95c2-4f07-8285-b5621782620e"
auth = "NjYzN2UwMDYtZThjYy00NGEyLTg4YzgtNDNkYTY4NzMyY2JkOjRmMmM5ZmQ0LWZhMDMtNDU5ZC1iZDE4LTUxYzdhOTFkYWM5YQ=="
credentials = f"{client_id}:{secret}"
encoded_credentials = base64.b64encode(credentials.encode('utf-8')).decode('utf-8')

if encoded_credentials != auth:
    raise Exception("Credentials do not match")

def get_token(auth_token, scope='GIGACHAT_API_PERS'):
    rq_uid = str(uuid.uuid4())
    url = "https://ngw.devices.sberbank.ru:9443/api/v2/oauth"
    headers = {
        'Content-Type': 'application/x-www-form-urlencoded',
        'Accept': 'application/json',
        'RqUID': rq_uid,
        'Authorization': f'Basic {auth_token}'
    }
    payload = {'scope': scope}
    try:
        response = requests.post(url, headers=headers, data=payload, verify=False)
        response.raise_for_status()
        return response.json()['access_token']
    except requests.RequestException as e:
        print(f"Ошибка: {str(e)}")
        return None

giga_token = get_token(auth)

def get_chat_completion(auth_token, user_message):
    url = "https://gigachat.devices.sberbank.ru/api/v1/chat/completions"
    payload = json.dumps({
        "model": "GigaChat",
        "messages": [
            {"role": "user", "content": user_message}
        ],
        "temperature": 1,
        "top_p": 0.1,
        "n": 1,
        "stream": False,
        "max_tokens": 512,
        "repetition_penalty": 1,
        "update_interval": 0
    })
    headers = {
        'Content-Type': 'application/json',
        'Accept': 'application/json',
        'Authorization': f'Bearer {auth_token}'
    }
    try:
        response = requests.post(url, headers=headers, data=payload, verify=False)
        response.raise_for_status()
        return response.json()['choices'][0]['message']['content']
    except requests.RequestException as e:
        print(f"Произошла ошибка: {str(e)}")
        return "Ошибка в запросе к нейронной сети"