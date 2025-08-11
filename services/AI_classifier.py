from pathlib import Path

from keras.models import load_model
from PIL import Image, ImageOps
import numpy as np
import base64
from io import BytesIO

SCRIPT_DIR = Path(__file__).parent.absolute()
UTIL_DIR = SCRIPT_DIR.parent / "util"

def classify_style(image_base64):
    model_path = UTIL_DIR / "style_class.h5"
    labels_path = UTIL_DIR / "style_labels.txt"
    np.set_printoptions(suppress=True)
    model = load_model(model_path, compile=False)
    class_names = open(labels_path, "r").readlines()

    data = np.ndarray(shape=(1, 224, 224, 3), dtype=np.float32)

    image_data = base64.b64decode(image_base64)
    image = Image.open(BytesIO(image_data)).convert("RGB")

    size = (224, 224)
    image = ImageOps.fit(image, size, Image.Resampling.LANCZOS)
    image_array = np.asarray(image)
    normalized_image_array = (image_array.astype(np.float32) / 127.5) - 1
    data[0] = normalized_image_array

    prediction = model.predict(data)
    index = np.argmax(prediction)
    class_name = class_names[index]
    confidence_score = prediction[0][index]

    return class_name, float(confidence_score)

def classify_masterCategory(image_base64):
    model_path = UTIL_DIR / "masterCategory_class.h5"
    labels_path = UTIL_DIR / "masterCategory_labels.txt"
    np.set_printoptions(suppress=True)
    model = load_model(model_path, compile=False)
    class_names = open(labels_path, "r").readlines()

    data = np.ndarray(shape=(1, 224, 224, 3), dtype=np.float32)

    image_data = base64.b64decode(image_base64)
    image = Image.open(BytesIO(image_data)).convert("RGB")

    size = (224, 224)
    image = ImageOps.fit(image, size, Image.Resampling.LANCZOS)
    image_array = np.asarray(image)
    normalized_image_array = (image_array.astype(np.float32) / 127.5) - 1
    data[0] = normalized_image_array

    prediction = model.predict(data)
    index = np.argmax(prediction)
    class_name = class_names[index]
    confidence_score = prediction[0][index]

    return class_name, float(confidence_score)

def classify_subCategory(masterCategory, image_base64):
    topwear_model_path = UTIL_DIR / "topwear_class.h5"
    topwear_labels_path = UTIL_DIR / "topwear_labels.txt"
    footwear_model_path = UTIL_DIR / "footwear_class.h5"
    footwear_labels_path = UTIL_DIR / "footwear_labels.txt"
    bottomwear_model_path = UTIL_DIR / "bottomwear_class.h5"
    bottomwear_labels_path = UTIL_DIR / "bottomwear_labels.txt"
    accessories_model_path = UTIL_DIR / "accessories_class.h5"
    accessories_labels_path = UTIL_DIR / "accessories_labels.txt"
    np.set_printoptions(suppress=True)

    if masterCategory == "Topwear":
        model = load_model(topwear_model_path, compile=False)
        class_names = open(topwear_labels_path, "r").readlines()
    elif masterCategory == "Footwear":
        model = load_model(footwear_model_path, compile=False)
        class_names = open(footwear_labels_path, "r").readlines()
    elif masterCategory == "Bottomwear":
        model = load_model(bottomwear_model_path, compile=False)
        class_names = open(bottomwear_labels_path, "r").readlines()
    elif masterCategory == "Accessories":
        model = load_model(accessories_model_path, compile=False)
        class_names = open(accessories_labels_path, "r").readlines()

    data = np.ndarray(shape=(1, 224, 224, 3), dtype=np.float32)

    image_data = base64.b64decode(image_base64)
    image = Image.open(BytesIO(image_data)).convert("RGB")

    size = (224, 224)
    image = ImageOps.fit(image, size, Image.Resampling.LANCZOS)
    image_array = np.asarray(image)
    normalized_image_array = (image_array.astype(np.float32) / 127.5) - 1
    data[0] = normalized_image_array

    prediction = model.predict(data)
    index = np.argmax(prediction)
    class_name = class_names[index]
    confidence_score = prediction[0][index]

    return class_name, float(confidence_score)