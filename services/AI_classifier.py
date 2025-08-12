from pathlib import Path
import numpy as np
from keras.utils import img_to_array
from keras.models import load_model
from keras.applications.imagenet_utils import preprocess_input
from rembg import remove
import base64
import io
from PIL import Image

SCRIPT_DIR = Path(__file__).parent.absolute()
UTIL_DIR = SCRIPT_DIR.parent / "util"


def process_image(img_base64, img_size=(224, 224)):
    try:
        if isinstance(img_base64, str) and 'base64,' in img_base64:
            img_base64 = img_base64.split('base64,')[1]

        img_data = base64.b64decode(img_base64)

        output_data = remove(img_data)

        img_no_bg = Image.open(io.BytesIO(output_data)).convert("RGBA")
        white_bg = Image.new("RGBA", img_no_bg.size, (255, 255, 255, 255))
        final_img = Image.alpha_composite(white_bg, img_no_bg).convert("RGB")

        img = final_img.resize(img_size)
        img_array = img_to_array(img)
        img_array = np.expand_dims(img_array, axis=0)
        img_array = preprocess_input(img_array)

        return img_array, None

    except Exception as e:
        return None, f"Image processing error: {str(e)}"


def load_and_predict(model_path, class_names, img_array):
    try:
        model = load_model(model_path)
        predictions = model.predict(img_array)
        predicted_class = class_names[np.argmax(predictions[0])]
        confidence = float(np.max(predictions[0]) * 100)
        return predicted_class, confidence, None
    except Exception as e:
        return None, 0.0, f"Prediction error: {str(e)}"


def predict_style(img_base64, img_size=(224, 224)):
    class_names = [
        "Casual", "Ethnic", "Formal", "Party",
        "Smart Casual", "Sports", "Travel"
    ]

    img_array, error = process_image(img_base64, img_size)
    if error:
        return {'success': False, 'error': error, 'predicted_class': None, 'confidence': 0.0}

    predicted_class, confidence, error = load_and_predict(
        UTIL_DIR / "style_model.keras",
        class_names,
        img_array
    )

    if error:
        return {'success': False, 'error': error, 'predicted_class': None, 'confidence': 0.0}

    return {
        'success': True,
        'predicted_class': predicted_class,
        'confidence': confidence
    }


def predict_masterCategory(img_base64, img_size=(224, 224)):
    class_names = ["Accessories", "Bottomwear", "Footwear", "Topwear"]

    img_array, error = process_image(img_base64, img_size)
    if error:
        return {'success': False, 'error': error, 'predicted_class': None, 'confidence': 0.0}

    predicted_class, confidence, error = load_and_predict(
        UTIL_DIR / "masterCategory_model.keras",
        class_names,
        img_array
    )

    if error:
        return {'success': False, 'error': error, 'predicted_class': None, 'confidence': 0.0}

    return {
        'success': True,
        'predicted_class': predicted_class,
        'confidence': confidence
    }


def predict_subCategory(img_base64, masterCategory, img_size=(224, 224)):
    category_config = {
        "Accessories": {
            "model": "accessories_model.keras",
            "classes": [
                "Backpacks", "Bag", "Belts", "Bracelet", "Caps", "Earrings",
                "Gloves", "Handbags", "Hat", "Headband", "Jewellery Set", "Ring",
                "Scarves", "Socks", "Sunglasses", "Ties", "Umbrellas", "Wallets", "Watches"
            ]
        },
        "Bottomwear": {
            "model": "bottomwear_model.keras",
            "classes": [
                "Capris", "Churidar", "Jeans", "Jeggings", "Leggings",
                "Lounge Pants", "Shorts", "Skirts", "Tights", "Track Pants",
                "Tracksuits", "Trousers"
            ]
        },
        "Topwear": {
            "model": "topwear_model.keras",
            "classes": [
                "Blazers", "Dresses", "Jackets", "Shirts", "Shrug",
                "Sweaters", "Sweatshirts", "Tops", "Tshirts", "Tunics", "Waistcoat"
            ]
        },
        "Footwear": {
            "model": "footwear_model.keras",
            "classes": [
                "Casual Shoes", "Flats", "Flip Flops", "Formal Shoes",
                "Heels", "Sandals", "Sports Sandals", "Sports Shoes"
            ]
        }
    }

    if masterCategory not in category_config:
        return {
            'success': False,
            'error': f"Unknown masterCategory: {masterCategory}",
            'predicted_class': None,
            'confidence': 0.0
        }

    img_array, error = process_image(img_base64, img_size)
    if error:
        return {'success': False, 'error': error, 'predicted_class': None, 'confidence': 0.0}

    config = category_config[masterCategory]
    predicted_class, confidence, error = load_and_predict(
        UTIL_DIR / config["model"],
        config["classes"],
        img_array
    )

    if error:
        return {'success': False, 'error': error, 'predicted_class': None, 'confidence': 0.0}

    return {
        'success': True,
        'predicted_class': predicted_class,
        'confidence': confidence
    }