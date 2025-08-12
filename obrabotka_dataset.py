import os
from PIL import Image, ImageOps
from pathlib import Path

input_path = Path("C:/myntradataset_test/MainClassDataset/Topwear")

output_path = Path("C:/myntradataset_test/MainClassDataset/Topwear_1")
output_path.mkdir(parents=True, exist_ok=True)


target_size = (60, 80)

def process_image(image_path, save_path):

    img = Image.open(image_path).convert("RGB")

    img = ImageOps.autocontrast(img)

    img.thumbnail(target_size, Image.Resampling.LANCZOS)

    new_img = Image.new("RGB", target_size, (128, 128, 128))

    x_offset = (target_size[0] - img.width) // 2
    y_offset = (target_size[1] - img.height) // 2
    new_img.paste(img, (x_offset, y_offset))

    new_img.save(save_path, format="PNG")

all_images = [
    Path(root) / file
    for root, _, files in os.walk(input_path)
    for file in files
    if file.lower().endswith((".png", ".jpg", ".jpeg"))
]
total_images = len(all_images)

for idx, src in enumerate(all_images, start=1):
    relative_path = src.relative_to(input_path)
    save_dir = output_path / relative_path.parent
    save_dir.mkdir(parents=True, exist_ok=True)

    dst = save_dir / (src.stem + ".png")
    process_image(src, dst)

    print(f"[{idx}/{total_images}] Обработано: {src.name}")

print("✅ Обработка завершена.")
