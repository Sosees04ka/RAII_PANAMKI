import os
import pandas as pd
import shutil

def organize_photos(csv_path, photos_folder, output_folder):

    print("\n=== ПРОВЕРКА ПУТЕЙ ===")
    print(f"CSV файл: {csv_path} - {'существует' if os.path.exists(csv_path) else 'НЕ НАЙДЕН!'}")
    print(f"Папка с фото: {photos_folder} - {'существует' if os.path.exists(photos_folder) else 'НЕ НАЙДЕНА!'}")
    
    if os.path.exists(photos_folder):
        num_files = len([f for f in os.listdir(photos_folder) if os.path.isfile(os.path.join(photos_folder, f))])
        print(f"Найдено {num_files} файлов в папке с фото")

        sample_files = os.listdir(photos_folder)[:5]
        print(f"Примеры файлов: {sample_files}")

    try:
        print("\n=== ЧТЕНИЕ CSV ===")
        df = pd.read_csv(csv_path, on_bad_lines='warn')
        print(f"Прочитано {len(df)} строк из CSV")
        print("Пример данных:")
        print(df.head(3))
    except Exception as e:
        print(f"Ошибка при чтении CSV файла: {e}")
        return

    if not os.path.exists(output_folder):
        os.makedirs(output_folder)
        print(f"\nСоздана выходная папка: {output_folder}")
    else:
        print(f"\nВыходная папка уже существует: {output_folder}")

    total = 0
    copied = 0
    missing = 0
    errors = 0
    
    print("\n=== ОБРАБОТКА ФОТО ===")

    for _, row in df.iterrows():
        total += 1
        try:
            photo_id = str(row['id']).strip()
            gender = str(row['articleType']).strip()

            source_photo = os.path.join(photos_folder, f"{photo_id}.jpg") 
            target_folder = os.path.join(output_folder, gender)

            if not os.path.exists(target_folder):
                os.makedirs(target_folder)

            if os.path.exists(source_photo):
                shutil.copy2(source_photo, target_folder)
                copied += 1
                if copied % 100 == 0: 
                    print(f"Обработано {copied} фото...")
            else:
                missing += 1

                if missing <= 5:
                    print(f"Фото {photo_id} не найдено. Искали по пути: {source_photo}")

                    alt_files = [f for f in os.listdir(photos_folder) if f.startswith(photo_id)]
                    if alt_files:
                        print(f"Найдены альтернативные файлы: {alt_files}")
        except Exception as e:
            errors += 1
            if errors <= 5: 
                print(f"Ошибка при обработке строки {total}: {e}")
            continue
    
    print("\n=== УДАЛЕНИЕ ПУСТЫХ ПАПОК ===")
    empty_folders_removed = 0
    for root, dirs, files in os.walk(output_folder, topdown=False):
        for dir_name in dirs:
            folder_path = os.path.join(root, dir_name)
            try:
                if not os.listdir(folder_path):
                    os.rmdir(folder_path)
                    empty_folders_removed += 1
                    print(f"Удалена пустая папка: {folder_path}")
            except Exception as e:
                print(f"Ошибка при удалении папки {folder_path}: {e}")
    
    print("\n=== ИТОГИ ===")
    print(f"Всего строк обработано: {total}")
    print(f"Успешно скопировано фото: {copied}")
    print(f"Отсутствующих фото: {missing}")
    print(f"Ошибок обработки: {errors}")
    print(f"Удалено пустых папок: {empty_folders_removed}")
    if os.path.exists(output_folder):
        num_output_files = sum([len(files) for _, _, files in os.walk(output_folder)])
        print(f"Файлов в выходной папке: {num_output_files}")

if __name__ == "__main__":
    csv_path = "C:/myntradataset_test/styles.csv" 
    photos_folder = "C:/myntradataset_test/MainClassDataset/Topwear" 
    output_folder = "C:/myntradataset_test/Topwear_class"  
    
    organize_photos(csv_path, photos_folder, output_folder)