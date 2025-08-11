import base64
import numpy as np
import tensorflow as tf
from PIL import Image
from io import BytesIO

def load_labels(label_path):
    with open(label_path, 'r', encoding='utf-8') as f:
        labels = [line.strip() for line in f.readlines()]
    return labels

def preprocess_image(base64_str, target_size=(60, 80)):
    try:
        if base64_str.startswith("data:image"):
            base64_str = base64_str.split(",", 1)[1]

        image_data = base64.b64decode(base64_str)
    except Exception as e:
        raise ValueError(f"Ошибка при декодировании base64: {e}")

    print("Декодированный размер:", len(image_data), "байт")

    try:
        img = Image.open(BytesIO(image_data)).convert('RGB')
    except Exception as e:
        raise ValueError(f"Ошибка при открытии изображения: {e}")

    img = img.resize(target_size)
    img_array = np.array(img) / 255.0
    img_array = np.expand_dims(img_array, axis=0)
    return img_array.astype(np.float32)


def predict(model_path, labels_path, base64_image):
    labels = load_labels(labels_path)
    model = tf.saved_model.load(model_path)
    infer = model.signatures["serving_default"]

    img = preprocess_image(base64_image)

    input_key = list(infer.structured_input_signature[1].keys())[0]

    outputs = infer(tf.constant(img))
    output_key = list(outputs.keys())[0]
    preds = outputs[output_key].numpy()[0]

    pred_idx = np.argmax(preds)
    pred_label = labels[pred_idx]
    pred_score = float(preds[pred_idx])

    return pred_label