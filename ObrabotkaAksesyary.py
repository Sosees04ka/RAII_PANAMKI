import os
import pandas as pd
import shutil
import csv

def safe_read_csv(file_path):
    """Безопасное чтение CSV с пропуском проблемных строк"""
    data = []
    with open(file_path, 'r', encoding='utf-8') as f:
        reader = csv.reader(f)
        header = next(reader) 
        for i, row in enumerate(reader, 2):  
            try:
                if len(row) != len(header):

                    print(f"Пропускаем строку {i}: неверное количество полей ({len(row)} вместо {len(header)})")
                    continue
                data.append(row)
            except Exception as e:
                print(f"Пропускаем строку {i} из-за ошибки: {e}")
                continue
    return pd.DataFrame(data, columns=header)


source_folder = 'Accessories'
csv_file = 'styles.csv'


if not os.path.exists(source_folder):
    print(f"Папка {source_folder} не существует!")
    exit()


try:
    print("Чтение CSV файла...")
    df = safe_read_csv(csv_file)
    print(f"Успешно прочитано {len(df)} строк")
except Exception as e:
    print(f"Фатальная ошибка при чтении CSV: {e}")
    exit()


success_count = 0
skip_count = 0

for index, row in df.iterrows():
    try:
        photo_id = str(row['id']).strip()
        target_folder = str(row['articleType']).strip()
        
        source_path = os.path.join(source_folder, f"{photo_id}.png")
        target_dir = os.path.join(source_folder, target_folder)
        target_path = os.path.join(target_dir, f"{photo_id}.png")

        if not os.path.exists(source_path):
            print(f"Файл {photo_id}.png не найден, пропускаем...")
            skip_count += 1
            continue

        os.makedirs(target_dir, exist_ok=True)

        shutil.move(source_path, target_path)
        print(f"Успешно: {photo_id}.png -> {target_folder}/")
        success_count += 1
        
    except Exception as e:
        print(f"Ошибка при обработке строки {index+2}: {e}")
        skip_count += 1
        continue

print("\nРезультаты:")
print(f"Успешно обработано: {success_count}")
print(f"Пропущено: {skip_count}")
print(f"Всего строк в CSV: {len(df)}")