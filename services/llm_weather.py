import requests
import json
import re
import base64
import uuid

client_id = "6637e006-e8cc-44a2-88c8-43da68732cbd"
secret = "1e302e7a-95c2-4f07-8285-b5621782620e"
auth = "NjYzN2UwMDYtZThjYy00NGEyLTg4YzgtNDNkYTY4NzMyY2JkOjRmMmM5ZmQ0LWZhMDMtNDU5ZC1iZDE4LTUxYzdhOTFkYWM5YQ=="
credentials = f"{client_id}:{secret}"
encoded_credentials = base64.b64encode(credentials.encode('utf-8')).decode('utf-8')


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

def get_giga_token():
    return get_token(auth)

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
        "max_tokens": 16672,
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
    
    
def ask_gigachat(weather_desc: str, wardrobe_items: list):
    auth_token = get_giga_token()
    if not auth_token:
        return json.dumps({"error": "Failed to get auth token"})

    wardrobe_text = "\n".join(
        f"{i}. id: {item.get('id') if item.get('id') is not None else 'null'}, "
        f"masterCategory: \"{item['masterCategory']}\", "
        f"subCategory: \"{item['subCategory']}\", "
        f"color: {item['color']}, "
        f"usage: \"{item['usage']}\""
        for i, item in enumerate(wardrobe_items, 1)
    )

    user_message = (
        f"Погода: {weather_desc}.\n"
        f"В гардеробе следующие вещи (ВСЕХ, что есть, с их уникальными id):\n{wardrobe_text}\n\n"
        "Сгенерируй 3 комбинации одежды по следующим правилам:\n"
        "1. Каждая комбинация должна содержать ровно 4 предмета: верх, низ, обувь и аксессуар.\n"
        "2. БЕРИ id ТОЛЬКО из списка выше. Категорически запрещено придумывать новые id.\n"
        "3. Для каждого предмета в комбинации копируй ВСЕ поля (id, masterCategory, subCategory, color, usage, imageBase64) ИЗ исходного списка.\n"
        "4. Не менять порядок ключей в объектах.\n"
        "5. Верни ответ ТОЛЬКО в формате JSON, без комментариев и текста вокруг.\n"
        "6. Начни ответ сразу с открывающей фигурной скобки {\n"
        "7. Формат ответа:\n"
        "{\n"
        "  \"generated_outfits\": [\n"
        "    {\n"
        "      \"score\": 0.85,\n"
        "      \"items\": [\n"
        "        {\"id\": 27, \"masterCategory\": \"topwear\", \"subCategory\": \"Shirt\", \"color\": [255,0,0], \"usage\": \"Formal\", \"imageBase64\": null},\n"
        "        {\"id\": 54, \"masterCategory\": \"bottomwear\", \"subCategory\": \"Pants\", \"color\": [0,0,0], \"usage\": \"Formal\", \"imageBase64\": null},\n"
        "        {\"id\": 78, \"masterCategory\": \"footwear\", \"subCategory\": \"Shoes\", \"color\": [255,255,255], \"usage\": \"Casual\", \"imageBase64\": null},\n"
        "        {\"id\": 91, \"masterCategory\": \"accessories\", \"subCategory\": \"Watch\", \"color\": [50,50,50], \"usage\": \"Formal\", \"imageBase64\": null}\n"
        "      ]\n"
        "    }\n"
        "  ]\n"
        "}\n"
        "8. Используй JSON стандарт: отсутствующие значения — null, а не None.\n"
    )


    response = get_chat_completion(auth_token, user_message)

    # Очистка маркдауна, если есть
    response = response.strip()
    if response.startswith('```json'):
        response = response[7:].strip().rstrip('```').strip()
    elif response.startswith('```'):
        response = response[3:].strip().rstrip('```').strip()

    return response


def extract_json_from_text(text: str) -> dict:
    start = text.find('{')
    end = text.rfind('}')
    if start == -1 or end == -1:
        raise ValueError("JSON not found in text")

    json_str = text[start:end+1]
    return json.loads(json_str)



