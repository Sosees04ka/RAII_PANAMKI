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

def _prepare_image(img_base64, img_size):
    if ',' in img_base64:
        img_base64 = img_base64.split(',')[-1]
    input_data = base64.b64decode(img_base64)
    output_data = remove(input_data)
    img_no_bg = Image.open(io.BytesIO(output_data)).convert("RGBA")
    white_bg = Image.new("RGBA", img_no_bg.size, (255, 255, 255, 255))
    final_img = Image.alpha_composite(white_bg, img_no_bg).convert("RGB")
    img = final_img.resize(img_size)
    img_array = img_to_array(img)
    img_array = np.expand_dims(img_array, axis=0)
    img_array = preprocess_input(img_array)
    return img_array

def _predict(img_array, model_path, class_names):
    model = load_model(model_path)
    predictions = model.predict(img_array)
    idx = np.argmax(predictions[0])
    return class_names[idx], float(np.max(predictions[0]) * 100)

def predict_style(img_base64, img_size=(224, 224)):
    class_names = ["Casual", "Ethnic", "Formal", "Party", "Smart Casual", "Sports", "Travel"]
    try:
        img_array = _prepare_image(img_base64, img_size)
        predicted_class, confidence = _predict(img_array, UTIL_DIR / "style_model.keras", class_names)
        return predicted_class
    except Exception as e:
        return {"success": False, "error": str(e), "predicted_class": None, "confidence": 0.0}

def predict_masterCategory(img_base64, img_size=(224, 224)):
    class_names = ["Accessories", "Bottomwear", "Footwear", "Topwear"]
    try:
        img_array = _prepare_image(img_base64, img_size)
        predicted_class, confidence = _predict(img_array, UTIL_DIR / "masterCategory_model.keras", class_names)
        return predicted_class
    except Exception as e:
        return {"success": False, "error": str(e), "predicted_class": None, "confidence": 0.0}

def predict_subCategory(img_base64, masterCategory, img_size=(224, 224)):
    category_map = {
        "Accessories": {
            "model": "accessories_model.keras",
            "classes": [
                "Backpacks", "Bag", "Belts", "Bracelet", "Caps", "Earrings", "Gloves", "Handbags",
                "Hat", "Headband", "Jewellery Set", "Ring", "Scarves", "Socks", "Sunglasses", "Ties",
                "Umbrellas", "Wallets", "Watches"
            ]
        },
        "Bottomwear": {
            "model": "bottomwear_model.keras",
            "classes": [
                "Capris", "Churidar", "Jeans", "Jeggings", "Leggings", "Lounge Pants", "Shorts",
                "Skirts", "Tights", "Track Pants", "Tracksuits", "Trousers"
            ]
        },
        "Topwear": {
            "model": "topwear_model.keras",
            "classes": [
                "Blazers", "Dresses", "Jackets", "Shirts", "Shrug", "Sweaters", "Sweatshirts",
                "Tops", "Tshirts", "Tunics", "Waistcoat"
            ]
        },
        "Footwear": {
            "model": "footwear_model.keras",
            "classes": [
                "Casual Shoes", "Flats", "Flip Flops", "Formal Shoes", "Heels", "Sandals",
                "Sports Sandals", "Sports Shoes"
            ]
        }
    }

    try:
        masterCategory = masterCategory.strip().title()
        if masterCategory not in category_map:
            raise ValueError(f"Неизвестная masterCategory: {masterCategory}")

        img_array = _prepare_image(img_base64, img_size)
        predicted_class, confidence = _predict(
            img_array,
            UTIL_DIR / category_map[masterCategory]["model"],
            category_map[masterCategory]["classes"]
        )

        return predicted_class
    except Exception as e:
        return {"success": False, "error": str(e), "predicted_class": None, "confidence": 0.0}
