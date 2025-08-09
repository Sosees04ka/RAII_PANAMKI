import base64

import cv2
import numpy as np
from sklearn.cluster import KMeans


def get_dominant_color(image, k=3, mask=None):
    image = cv2.cvtColor(image, cv2.COLOR_BGR2RGB)

    if mask is None:
        pixels = image.reshape(-1, 3)
    else:
        masked_image = cv2.bitwise_and(image, image, mask=mask)
        pixels = masked_image[mask > 0].reshape(-1, 3)

    kmeans = KMeans(n_clusters=k, n_init=10)
    kmeans.fit(pixels)

    colors = kmeans.cluster_centers_
    counts = np.bincount(kmeans.labels_)
    percentages = counts / len(pixels)

    dominant_idx = np.argmax(counts)
    dominant_color = colors[dominant_idx].astype(int)

    return dominant_color, percentages[dominant_idx], colors, percentages


def segment_clothing_improved(image):
    hsv = cv2.cvtColor(image, cv2.COLOR_BGR2HSV)

    gray = cv2.cvtColor(image, cv2.COLOR_BGR2GRAY)
    _, mask_brightness = cv2.threshold(gray, 0, 255, cv2.THRESH_BINARY_INV + cv2.THRESH_OTSU)

    lower_skin = np.array([0, 20, 70], dtype=np.uint8)
    upper_skin = np.array([20, 255, 255], dtype=np.uint8)
    mask_skin = cv2.inRange(hsv, lower_skin, upper_skin)
    mask_color = 255 - mask_skin

    combined_mask = cv2.bitwise_and(mask_brightness, mask_color)

    kernel = cv2.getStructuringElement(cv2.MORPH_ELLIPSE, (7, 7))
    combined_mask = cv2.morphologyEx(combined_mask, cv2.MORPH_CLOSE, kernel)
    combined_mask = cv2.morphologyEx(combined_mask, cv2.MORPH_OPEN, kernel)

    return combined_mask


def get_color(image_base64):
    try:
        if ',' in image_base64:
            image_base64 = image_base64.split(',')[1]

        image_data = base64.b64decode(image_base64)
        nparr = np.frombuffer(image_data, np.uint8)
        image = cv2.imdecode(nparr, cv2.IMREAD_COLOR)

        if image is None:
            raise ValueError("Не удалось декодировать изображение")
    except Exception as e:
        raise ValueError(f"Ошибка обработки base64 изображения: {str(e)}")

    mask = segment_clothing_improved(image)
    dominant_color, _, _, _ = get_dominant_color(image, mask=mask)
    return dominant_color.tolist()